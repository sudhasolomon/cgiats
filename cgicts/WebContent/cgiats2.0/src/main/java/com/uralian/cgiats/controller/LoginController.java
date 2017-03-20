/**
 * 
 */
package com.uralian.cgiats.controller;

import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uralian.cgiats.config.HttpSessionCollector;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.LoginAttempts;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.service.CommService;
import com.uralian.cgiats.service.CommService.AttachmentInfo;
import com.uralian.cgiats.service.IndiaUserService;
import com.uralian.cgiats.service.LoginDetailsService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.UserService;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.StatusMessage;
import com.uralian.cgiats.util.TransformDtoToEntity;
import com.uralian.cgiats.util.TransformEntityToDto;
import com.uralian.cgiats.util.Utils;

/**
 * @author skurapati
 *
 */
@RestController
@RequestMapping("/LoginController")
public class LoginController {
	private static final Logger LOG = Logger.getLogger(LoginController.class);
	private static PasswordEncoder encoder = new Md5PasswordEncoder();
	@Autowired
	private UserService userService;
	@Autowired
	private IndiaUserService indiaUserService;
	@Autowired
	private CommService commService;
	
	@Autowired
	private LoginDetailsService loginDetailService;

	@RequestMapping(value = "/loginAction", method = RequestMethod.POST)
	public ResponseEntity<?> loginAction(@RequestBody UserDto dto, HttpServletRequest httpServletRequest) {
		UserDto userDto = null;
		try {
			LOG.info("loginAction() started");
			String encodedString = encoder.encodePassword(dto.getPassword(), Constants.SALT);
			if (dto.getIsDirectCall() != null && !dto.getIsDirectCall()) {
				encodedString = dto.getPassword();
			}
			userDto = userService.getUserByCredentials(dto.getUserId(), encodedString,true);
			if(userDto == null || userDto.getUserRole().name().toUpperCase().startsWith(Constants.IN_PREFIX)){
				userDto = indiaUserService.getUserByCredentials(dto.getUserId(), encodedString,true);
			}
			if (userDto != null) {

				removeDuplicateUserLogin(userDto);
				LoginAttempts loginAttempt = saveLoginAttempts(userDto);
				if(!Utils.isNull(loginAttempt)){
					userDto.setLoginAttemptId(loginAttempt.getId().toString());
				}
				HttpSession session = httpServletRequest.getSession();
				session.setAttribute(Constants.LOGIN_USER, userDto);
				 
				LOG.info("loginAction() ended");
				return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
			} else {
				return new ResponseEntity<List<?>>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
 			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			return new ResponseEntity<List<?>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	

	private LoginAttempts saveLoginAttempts(UserDto userDto) {
		LoginAttempts attempts = null;
		if(!Utils.isNull(userDto)){
			try {
			attempts = new LoginAttempts();
			//attempts.setTotal(loginDetailService.getLoginAttemptByDate(new Date(), userDto.getUserId()).get(0));
			TransformDtoToEntity.getuserloginAttempt(userDto, attempts);
			attempts = loginDetailService.saveLoginDetails(attempts);
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return attempts;
	}



	private void removeDuplicateUserLogin(UserDto userDto) {
		// To clear the existing session
		Map<String, HttpSession> sessions = HttpSessionCollector.getSessions();

		HttpSession existingSession = null;
		if (!sessions.isEmpty()) {
			for (Map.Entry<String, HttpSession> mapEntry : sessions.entrySet()) {
				UserDto existedLoggedInUsers = (UserDto) mapEntry.getValue().getAttribute(Constants.LOGIN_USER);
				if (existedLoggedInUsers != null) {
					if (existedLoggedInUsers.getUserId().equals(userDto.getUserId())) {
						existingSession = mapEntry.getValue();
					}
				}
			}
		}
		if (existingSession != null) {
			LOG.info("User is existed so invalidate()");
			existingSession.invalidate();
		}
	}

	@RequestMapping(value = "/getLoggedInPersonInfo", method = RequestMethod.GET)
	public UserDto getLoggedInPersonInfo(HttpServletRequest httpServletRequest, @RequestParam(value="currentLoginUserId",required=false) String currentLoginUserId) {
		UserDto dto = null;
		try {
			if (currentLoginUserId != null && !currentLoginUserId.equals("null")) {
				User user = userService.loadUser(currentLoginUserId);
				if (user.getAccessToken() != null) {
					user.setAccessToken(null);
					userService.updateUser(user);
					dto = TransformEntityToDto.getUserDto(user);
					removeDuplicateUserLogin(dto);
					HttpSession session = httpServletRequest.getSession();
					session.setAttribute(Constants.LOGIN_USER, dto);
				} else {
					return dto;
				}
			} else {
				HttpSession session = httpServletRequest.getSession();
				dto = (UserDto) session.getAttribute(Constants.LOGIN_USER);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
		}
		return dto;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ResponseEntity<?> logout(HttpServletRequest httpServletRequest) {
		try {
			UserDto userDto = null;
			HttpSession session = httpServletRequest.getSession();
			userDto = (UserDto) session.getAttribute(Constants.LOGIN_USER);
			if (userDto != null) {
				updateLoginAttempts(httpServletRequest);
				session.removeAttribute(Constants.LOGIN_USER);
				session.invalidate();
				return new ResponseEntity<UserDto>(userDto, HttpStatus.OK);
			} else {
				return new ResponseEntity<List<?>>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			return new ResponseEntity<List<?>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	

	@RequestMapping(value = "/passwordRecovery", method = RequestMethod.POST)
	public ResponseEntity<?> getRecoveryPassword(@RequestBody UserDto dto, HttpServletRequest httpServletRequest) {

		User user = null;
		StatusMessage status = new StatusMessage();
		if (dto.getUserId() != null){
			user = userService.loadUser(dto.getUserId());
			if(user == null){
				user = indiaUserService.loadUser(dto.getUserId());
				
			}else{
				if(user.getUserRole().name().toUpperCase().startsWith(Constants.IN_PREFIX)){
					user = indiaUserService.loadUser(dto.getUserId());
				}
			}
		}
		else if (dto.getEmail() != null){
			user = userService.loadUserByEmail(dto.getEmail());
			if(user == null){
				user = indiaUserService.loadUserByEmail(dto.getUserId());
			}else{
				if(user.getUserRole().name().toUpperCase().startsWith(Constants.IN_PREFIX)){
					user = indiaUserService.loadUserByEmail(dto.getUserId());
				}
			}
		}
		if (user != null) {
			if(user.getStatus().equals(Constants.ACTIVE)){
			if (user.getEmail() != null && user.getEmail().trim().length() > 0) {
				StringBuffer randomPassword = new StringBuffer();
				for (int i = 0; i < Constants.RANDOM_PASS_LENGTH; i++) {
					int number = getRandomNumber();
					char passChars = Constants.PASS_CHAR_LIST.charAt(number);
					randomPassword.append(passChars);
				}
				try {
					user.setPassword(randomPassword.toString());
					if(user.getUserRole()!=null && user.getUserRole().name().toUpperCase().startsWith(Constants.IN_PREFIX)){
						indiaUserService.updateUser(user);	
					}else{
					userService.updateUser(user);
					}
					StringBuffer msgbody = new StringBuffer("Hello CGI ATS User, ");
					msgbody.append("<br><br>");
					msgbody.append("Your Temporary password is :" + randomPassword);
					msgbody.append("<br>");
					msgbody.append("Please change your password after first login");
					msgbody.append("<br><br>");
					msgbody.append("<b>*** This is an automatically generated email, please do not reply ***</b>");
					String msgSub = "[CGI ATS] Auto generated password";

					commService.sendEmail(null, user.getEmail(), msgSub, msgbody.toString(), new AttachmentInfo[0]);
				} catch (Exception e) {
					e.printStackTrace();
					LOG.error(e.getMessage(), e);
				}

				status.setStatusCode(String.valueOf(200));
				status.setStatusMessage("Password successfully sent to your mail id");
				return new ResponseEntity<>(status, HttpStatus.OK);
			} else {
				status.setStatusCode(String.valueOf(500));
				status.setStatusMessage("Email does not exist!.. Please contact your administrator ");
				return new ResponseEntity<>(status, HttpStatus.OK);
			}
			}else{
				status.setStatusCode(String.valueOf(500));
				status.setStatusMessage("User is already Inactive.");
				return new ResponseEntity<>(status, HttpStatus.OK);
			}
		} else {
			status.setStatusCode(String.valueOf(500));
			status.setStatusMessage("Unable to get details please provide valid details");
			return new ResponseEntity<>(status, HttpStatus.OK);
		}

	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public ResponseEntity<?> getupdatedPassword(@RequestBody UserDto newdto, HttpServletRequest httpServletRequest) {
		StatusMessage status = new StatusMessage();
		UserDto user = Utils.getLoginUser(httpServletRequest);
		User entityUser = null;
		if (user.getPassword().equals(encoder.encodePassword(newdto.getPassword(), Constants.SALT))) {
			if(user.getUserRole()!=null && user.getUserRole().name().toUpperCase().startsWith(Constants.IN_PREFIX)){
				entityUser = indiaUserService.loadUser(user.getUserId());
			}else{
				entityUser = userService.loadUser(user.getUserId());
			}
			entityUser.setPassword(newdto.getNewPassword());
			try {
				if(user.getUserRole()!=null && user.getUserRole().name().toUpperCase().startsWith(Constants.IN_PREFIX)){
					indiaUserService.updateUser(entityUser);
				}else{
					userService.updateUser(entityUser);
				}
				
			} catch (ServiceException e) {
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
			}
			status.setStatusCode(String.valueOf(200));
			status.setStatusMessage("password successfully changed");
			return new ResponseEntity<>(status, HttpStatus.OK);
		} else {
			status.setStatusCode(String.valueOf(500));
			status.setStatusMessage("your current password dose not match");
			return new ResponseEntity<>(status, HttpStatus.OK);
		}
	}

	private int getRandomNumber() {
		int randomInt = 0;
		Random randomGenerator = new Random();
		randomInt = randomGenerator.nextInt(Constants.PASS_CHAR_LIST.length());
		if (randomInt - 1 == -1) {
			return randomInt;
		} else {
			return randomInt - 1;
		}
	}
	
	private void updateLoginAttempts(HttpServletRequest httpServletRequest) {
		
		try {
			LoginAttempts attempts = null;
	
		UserDto dto = null;
		HttpSession session = httpServletRequest.getSession();
		dto = (UserDto) session.getAttribute(Constants.LOGIN_USER);
		 if(dto.getLoginAttemptId() != null){
			 attempts = loginDetailService.loadLoginAttempts(Integer.parseInt(dto.getLoginAttemptId()));
				TransformDtoToEntity.getuserloginAttempt(dto, attempts);
				loginDetailService.updateLoginDetails(attempts);
		 }
			
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 
	}
}

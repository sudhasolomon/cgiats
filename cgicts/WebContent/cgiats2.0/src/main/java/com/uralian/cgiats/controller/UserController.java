/**
 * 
 */
package com.uralian.cgiats.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.service.IndiaUserService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.UserService;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.StatusMessage;
import com.uralian.cgiats.util.TransformDtoToEntity;
import com.uralian.cgiats.util.Utils;

/**
 * 
 * @author skurapati
 *
 */
@RestController
@RequestMapping("userController")
public class UserController {
	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private UserService userService;

	@Autowired
	private IndiaUserService indiaUserService;

	/**
	 * This method is to perform save or update user table
	 * 
	 * @param userDto
	 * @param request
	 * @return userdto
	 */
	@RequestMapping(value = "/saveOrUpdateUser", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> saveOrUpdateUser(@RequestBody UserDto userDto, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				StatusMessage status = new StatusMessage();
				if (userDto != null) {
					UserDto loggedInUserDto = Utils.getLoginUser(request);
					userDto.setCreatedBy(loggedInUserDto.getUserId());
					userDto.setUpdatedBy(loggedInUserDto.getUserId());
					User user = null;
					if (userDto.getUserRole() != null && userDto.getUserRole().name().toUpperCase().startsWith(Constants.IN_PREFIX)) {
						user = indiaUserService.loadUser(userDto.getUserId());
						if(user!=null){
							user.setStatus(userDto.getStatus());
						}
						User usUser = userService.loadUser(userDto.getUserId());
						if (usUser != null && (user==null || (user !=null && user.getStatus().equalsIgnoreCase(Constants.ACTIVE)))) {
							usUser.setStatus(Constants.INACTIVE);
							usUser.setRelievingDate(usUser.getRelievingDate() != null ? usUser.getRelievingDate() : new Date());
							// userService.permanentDeleteUser(userDto.getUserId(),
							// null);
							userService.updateUser(usUser);
						}
					} else {
						// user = userService.loadUser(userDto.getUserId());

						user = userService.loadUser(userDto.getUserId());
						if(user!=null){
							user.setStatus(userDto.getStatus());
						}
						User indUser = indiaUserService.loadUser(userDto.getUserId());
						if (indUser != null && (user==null || (user !=null && user.getStatus().equalsIgnoreCase(Constants.ACTIVE)))) {
							indUser.setStatus(Constants.INACTIVE);
							indUser.setRelievingDate(indUser.getRelievingDate() != null ? indUser.getRelievingDate() : new Date());
							// userService.permanentDeleteUser(userDto.getUserId(),
							// null);
							indiaUserService.updateUser(indUser);
						}
					}
					if (user != null) {
						if (userDto.getUserRole() != null && userDto.getUserRole().name().toUpperCase().startsWith(Constants.IN_PREFIX)) {
							indiaUserService.updateUser(userDto);
						} else {
							userService.updateUser(userDto);
						}
						if (loggedInUserDto.getUserId().equals(userDto.getUserId())) {
							UserDto userdto = null;
							if (userDto.getUserRole() != null && userDto.getUserRole().name().toUpperCase().startsWith(Constants.IN_PREFIX)) {
								userdto = indiaUserService.getUserByCredentials(userDto.getUserId(), userDto.getPassword(), null);
							} else {
								userdto = userService.getUserByCredentials(userDto.getUserId(), userDto.getPassword(), null);
							}

							loggedInUserDto.setFirstName(userdto.getFirstName());
							loggedInUserDto.setLastName(userdto.getLastName());
							loggedInUserDto.setEmail(userdto.getEmail());
							loggedInUserDto.setPhone(userdto.getPhone());
							loggedInUserDto.setFullName(userdto.getFullName());
							loggedInUserDto.setCity(userdto.getCity());
							loggedInUserDto.setImageExt(userdto.getImageExt());
							loggedInUserDto.setBase64Image(userdto.getBase64Image());
							return new ResponseEntity<Object>(loggedInUserDto, HttpStatus.OK);
						} else {
							status.setStatusCode(String.valueOf(200));
							status.setStatusMessage("User has updated successfully");
							return new ResponseEntity<StatusMessage>(status, HttpStatus.OK);
						}
					} else {
						User newUser = new User();
						if (userDto.getPassword() == null) {
							userDto.setPassword(Constants.DEFAULT_PWD);
						}
						TransformDtoToEntity.getUser(userDto, newUser, null);
						newUser.setStatus(Constants.ACTIVE);
						if (userDto.getUserRole() != null && userDto.getUserRole().name().toUpperCase().startsWith(Constants.IN_PREFIX)) {
							indiaUserService.saveUser(newUser);
						} else {
							userService.saveUser(newUser);
						}

						status.setStatusCode(String.valueOf(200));
						status.setStatusMessage("User has Created successfully");
						return new ResponseEntity<StatusMessage>(status, HttpStatus.OK);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
				return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
			}

			return new ResponseEntity<Object>(HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/createdUserByDate", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> createdUserByDate(@RequestBody Object obj, HttpServletRequest request)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String loginUser = Utils.getLoginUserId(request);
		if (loginUser != null) {
			List<UserDto> users, indiaUsers;
			List<UserDto> userDtoList = new ArrayList<UserDto>();

			String month = (String) PropertyUtils.getProperty(obj, "month");
			String year = (String) PropertyUtils.getProperty(obj, "year");

			if (month != null && year != null) {
				Calendar cal = Calendar.getInstance();
				cal.set(Integer.parseInt(year), Integer.parseInt(month), cal.getActualMaximum(Calendar.DAY_OF_MONTH), 0, 0);
				Date date2 = cal.getTime();

				cal.set(Integer.parseInt(year), Integer.parseInt(month), 1, 0, 0);
				Date date1 = cal.getTime();

				users = userService.getUsersByCreatedDate(loginUser, date1, date2);
				indiaUsers = indiaUserService.getUsersByCreatedDate(loginUser, date1, date2);

			} else {
				users = userService.getUsersByCreatedDate(loginUser, null, null);
				indiaUsers = indiaUserService.getUsersByCreatedDate(loginUser, null, null);
			}
			if (indiaUsers != null && indiaUsers.size() > 0) {
				if (users != null) {
					users.addAll(indiaUsers);
				} else {
					users = indiaUsers;
				}
			}
			for (UserDto user : users) {
				UserDto dto = new UserDto();
				dto.setUserId(user.getUserId() != null ? user.getUserId() : "");
				dto.setUserRole(user.getUserRole());
				dto.setFullName(user.getFullName() != null ? user.getFullName() : "");
				dto.setAssignedBdm(user.getAssignedBdm() != null ? user.getAssignedBdm() : "");
				dto.setOfficeLocation(user.getOfficeLocation() != null ? user.getOfficeLocation() : "");
				dto.setCreatedon(user.getCreatedon() != null ? user.getCreatedon() : "");
				dto.setStrCreatedOn(user.getStrCreatedOn());
				dto.setStatus(user.getStatus() != null ? user.getStatus() : "");
				dto.setEmail(user.getEmail() != null ? user.getEmail() : "");
				userDtoList.add(dto);
			}

			return new ResponseEntity<Object>(userDtoList, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}

	@RequestMapping(value = "/activateUser", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<?> activateUser(@RequestParam(value = "userId") String userId, @RequestParam(value = "joiningDate") String joiningDate,
			@RequestParam(value = "userRole", required = false) String userRole, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				Date joinDate = Utils.convertStringToDate(joiningDate);
				User user = null;
				if (userRole != null && userRole.toUpperCase().startsWith(Constants.IN_PREFIX)) {
					user = indiaUserService.loadUser(userId);

					User usUser = userService.loadUser(userId);
					if (usUser != null && usUser.getStatus().equalsIgnoreCase(Constants.ACTIVE)) {
						usUser.setStatus(Constants.INACTIVE);
						usUser.setRelievingDate(usUser.getRelievingDate() != null ? usUser.getRelievingDate() : new Date());
						userService.updateUser(usUser);
					}
					if (user == null && usUser != null) {
						user = usUser;
						user.setStatus(Constants.ACTIVE);
						user.setJoinDate(joinDate);
						user.setRelievingDate(null);
						indiaUserService.saveUser(user);
					}
				} else {
					user = userService.loadUser(userId);
					User indUser = indiaUserService.loadUser(userId);
					if (indUser != null && indUser.getStatus().equalsIgnoreCase(Constants.ACTIVE)) {
						indUser.setStatus(Constants.INACTIVE);
						indUser.setRelievingDate(indUser.getRelievingDate() != null ? indUser.getRelievingDate() : new Date());
						indiaUserService.updateUser(indUser);
					}

					if (user == null && indUser != null) {
						user = indUser;
						user.setStatus(Constants.ACTIVE);
						user.setJoinDate(joinDate);
						user.setRelievingDate(null);
						userService.saveUser(user);
					}
				}

				if (user != null) {
					// user.setPassword(null);
					user.setStatus("ACTIVE");
					user.setJoinDate(joinDate);
					user.setRelievingDate(null);
					if (user.getUserRole().name().toUpperCase().startsWith(Constants.IN_PREFIX)) {
						indiaUserService.updateUser(user);
					} else {
						userService.updateUser(user);
					}
				}
				return new ResponseEntity<>(HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}

	@RequestMapping(value = "/deleteUser", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<?> deleteUser(@RequestParam(value = "userId") String userId, @RequestParam(value = "relievDate") String relievDate,
			@RequestParam(value = "userRole", required = false) String userRole, HttpServletRequest request) throws ServiceException {
		if (Utils.getLoginUserId(request) != null) {
			User user = null;
			if (userRole != null && userRole.toUpperCase().startsWith(Constants.IN_PREFIX)) {
				user = indiaUserService.loadUser(userId);

				User usUser = userService.loadUser(userId);
				if (usUser != null && usUser.getStatus().equalsIgnoreCase(Constants.ACTIVE)) {
					usUser.setStatus(Constants.INACTIVE);
					usUser.setRelievingDate(usUser.getRelievingDate() != null ? usUser.getRelievingDate() : new Date());
					userService.updateUser(usUser);
				}
				if (user != null) {
					user.setStatus(Constants.INACTIVE);
					user.setRelievingDate(Utils.convertStringToDate(relievDate));
					indiaUserService.updateUser(user);
				}
			} else {
				user = userService.loadUser(userId);
				User indUser = indiaUserService.loadUser(userId);
				if (indUser != null && indUser.getStatus().equalsIgnoreCase(Constants.ACTIVE)) {
					indUser.setStatus(Constants.INACTIVE);
					indUser.setRelievingDate(indUser.getRelievingDate() != null ? indUser.getRelievingDate() : new Date());
					indiaUserService.updateUser(indUser);
				}

				if (user != null) {
					user.setStatus(Constants.INACTIVE);
					user.setRelievingDate(Utils.convertStringToDate(relievDate));
					userService.updateUser(user);
				}
			}

			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}

	@RequestMapping(value = "/getUserDetais", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<?> getUserDetais(@RequestParam(value = "userId") String userId, @RequestParam(value = "userRole", required = false) String userRole,
			HttpServletRequest request) throws ServiceException {
		if (Utils.getLoginUserId(request) != null) {
			List<UserDto> usersList = new ArrayList<>();
//			User user = userService.loadUser(userId);
			if (userRole!=null && userRole.toUpperCase().startsWith(Constants.IN_PREFIX)) {
				usersList = indiaUserService.getUsersInfoByIds(Arrays.asList(userId));
				if (usersList == null || usersList.size()==0) {
					usersList = userService.getUsersInfoByIds(Arrays.asList(userId));
				}
			} else {
				usersList = userService.getUsersInfoByIds(Arrays.asList(userId));
			}
			return new ResponseEntity<>(usersList, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}

}

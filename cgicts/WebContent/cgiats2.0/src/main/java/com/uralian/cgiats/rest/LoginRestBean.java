package com.uralian.cgiats.rest;

import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.uralian.cgiats.dao.CareerAccountDao;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CarrerAccount;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.CareerAccountService;
import com.uralian.cgiats.service.CommService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.UserService;
import com.uralian.cgiats.util.Utils;

@Component
@Path("/restLogin")
public class LoginRestBean extends SpringBeanAutowiringSupport {

	private static final PasswordEncoder passwordEncoder = new Md5PasswordEncoder();
	private static final Logger log = Logger.getLogger(LoginRestBean.class);
	private static final String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	private static final int RANDOM_STRING_LENGTH = 10;
	@Autowired
	public CareerAccountDao careerAccountDao;

	@Autowired
	public CareerAccountService careerAccount;

	@Autowired
	public UserService userService;

	@Autowired
	private CommService commService;

	@Autowired
	private CandidateService candidateService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String authUser(
			@QueryParam("user") String userId,
			// @QueryParam("careerId") Integer careerId,
			@QueryParam("careerPwd") String careerPwd,
			@QueryParam("regId") String registerId,
			@QueryParam("deviceType") String deviceType,
			@QueryParam("email") String email,
			@QueryParam("executiveFlag") String executiveFlag,
			@Context HttpServletRequest request) throws JSONException {
		log.info("From Login RestBean (Method... authUser) Start");
		List<CarrerAccount> carrerAccountList = null;
		User user = null;

		JSONObject jsonObj = new JSONObject();

		System.out.println(email + "     " + registerId + "        "
				+ deviceType + "               " + careerPwd);

		if (email == null || careerPwd == null) {
			jsonObj.put("statusMessage", "All fields are mandatory");
			jsonObj.put("statusCode", 21);
			return jsonObj.toString();
		}
		if (registerId == null) {
			jsonObj.put("statusMessage", "Installation Problem");
			jsonObj.put("statusCode", 21);
			return jsonObj.toString();
		}

		if (executiveFlag.equalsIgnoreCase("Y")) {
			try {
				user = userService.loadUser(email);
			} catch (Exception ex) {
				jsonObj.put("statusMessage",
						"You are not authorized person to login as executive, Please contact Admin.");
				jsonObj.put("statusCode", 21);
			}
			String encryptPwd = passwordEncoder
					.encodePassword(careerPwd, "CGI");
			if (user != null && user.getPassword().equals(encryptPwd)
					&& user.getExecutiveDASHBOARD().equalsIgnoreCase("Y")) {
				jsonObj.put("statusMessage", "Succesfully logged in");
				jsonObj.put("statusCode", 1);
			} else {
				jsonObj.put("statusMessage",
						"You are not authorized person to login as executive, Please contact Admin.");
				jsonObj.put("statusCode", 21);
			}

		} else {
			String encryptPwd = passwordEncoder
					.encodePassword(careerPwd, "CGI");
			carrerAccountList = careerAccount.getCareerAccountFromEmail(email,
					encryptPwd);
			System.out.println(carrerAccountList);
			System.out.println(user);
			if (carrerAccountList == null || carrerAccountList.size() == 0) {
				jsonObj.put("statusMessage", "Please enter valid credentials");
				jsonObj.put("statusCode", 21);
				return jsonObj.toString();
			} else {
				jsonObj.put("statusMessage", "Succesfully logged in");
				jsonObj.put("statusCode", 1);
			}
		}

		HttpSession session = request.getSession();
		session.setAttribute(registerId, registerId);
		log.info("From Login RestBean(Method.. authUser) End:...");
		// session.setMaxInactiveInterval(120);
		return jsonObj.toString();
	}

	@GET
	@Path("/forgetPassword")
	@Produces(MediaType.APPLICATION_JSON)
	public String getForgetPassword(@QueryParam("email") String email,
			@QueryParam("careerPwd") String careerPwd) throws JSONException {
		JSONObject jsonObj = new JSONObject();
		String temporaryPwd = null;
		
		log.info("From RestLoginBean (ForgotPassword Method) Start...");
		try {
			// List<CarrerAccount> carrerAccountList = null;
			// CarrerAccount carrerAccount = null;
			if (Utils.isBlank(email)) {
				jsonObj.put("statusMessage", "Please enter valid Email ID");
				jsonObj.put("statusCode", 11);
				return jsonObj.toString();
			}

			CarrerAccount carrerAccount = careerAccount
					.getEmailFromCareerAcct(email);
			temporaryPwd = generateRandomString();

			log.info("password" + temporaryPwd);
			if (carrerAccount != null) {

				carrerAccount.setCareerPwd(temporaryPwd);
				// carrerAccount=carrerAccountList.get(0);

				prepareMailContent(carrerAccount);
				String encryptPwd = passwordEncoder.encodePassword(
						temporaryPwd, "CGI");
				carrerAccount.setCareerPwd(encryptPwd);
				careerAccount.updateCarrerAccount(carrerAccount);
				log.info("password" + temporaryPwd + "Encrypt" + encryptPwd);
				/*
				 * jsonObj.put("password", carrerAccount.getCareerPwd());
				 * prepareMailContent(carrerAccount);
				 * commService.sendEmail("rgovvala@charterglobal.com", new
				 * String[]{email.trim()},null,"Password Resend",
				 * ""+" Your Password is" + carrerAccount.getCareerPwd() +"",
				 * null); //commService.sendEmail(null, email.trim(),
				 * "Password Resend", ""+" Your Password is" +
				 * carrerAccount.getCareerPwd() +"", null);
				 */
				jsonObj.put("statusMessage",
						"Password sent to registered Email successfully");
				jsonObj.put("statusCode", 1);
			} else {
				jsonObj.put("statusMessage", "Email ID does not exist"
						+ temporaryPwd);
				jsonObj.put("statusCode", 11);

			}
			log.info("From RestLoginBean (ForgotPassword Method) End...");
			return jsonObj.toString();
		}

		/*
		 * if (!Utils.isBlank(email)) { carrerAccountList =
		 * careerAccount.getCareerFromEmail(email);
		 * carrerAccountList.get(0).getCareerPwd();
		 * System.out.println(carrerAccountList); if (carrerAccountList != null)
		 * { carrerAccount = carrerAccountList.get(0);
		 * System.out.println(carrerAccount);
		 * 
		 * } }
		 */

		/*
		 * if (carrerAccount != null) { String subject =
		 * prepareMailContent(carrerAccount); try {
		 * commService.sendEmail("rgovvala@charterglobal.com",
		 * email.trim(),subject, " Change Password ", null, null); } catch
		 * (ServiceException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } jsonObj.put("statusMessage",
		 * "Password Sent To Registered Email Successfully");
		 * jsonObj.put("password", carrerAccountList.get(0).getCareerPwd());
		 * jsonObj.put("statusCode", 200); } else { jsonObj.put("statusMessage",
		 * "Email Not Found"); jsonObj.put("statusCode", 404); }
		 * 
		 * }
		 */catch (Exception e) {
			// TODO Auto-generated catch block
			jsonObj.put("statusMessage",
					"Internal error, please try after some time.");
			jsonObj.put("statusCode", 21);
			e.printStackTrace();
			return jsonObj.toString();
		}
	}

	@GET
	@Path("/changePassword")
	@Produces(MediaType.APPLICATION_JSON)
	public String changePassword(@QueryParam("email") String email,
			@QueryParam("careerPwd") String careerPwd,
			@QueryParam("newPwd") String newPwd) throws JSONException {
		JSONObject jsonObj = new JSONObject();
		String encryptPwd = passwordEncoder.encodePassword(careerPwd, "CGI");
		log.info("From RestLoginBean ChangePassword Method... Start");
		CarrerAccount carrerAccount = careerAccount.getCareerFromEmail(email,
				encryptPwd);
		if (carrerAccount != null) {
			encryptPwd = passwordEncoder.encodePassword(newPwd, "CGI");
			carrerAccount.setCareerPwd(encryptPwd);
			try {
				careerAccount.updateCarrerAccount(carrerAccount);
				jsonObj.put("statusMessage", "Password changed successfully");
				jsonObj.put("statusCode", 1);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		} else {
			jsonObj.put("statusMessage", "Authentication error");
			jsonObj.put("statusCode", 21);
		}
		log.info("From RestLoginBean ChangePassword Method... End");

		return jsonObj.toString();
	}

	private String prepareMailContent(CarrerAccount carrerAccount) {
		// TODO Auto-generated method stub
		String email = carrerAccount.getEmail();
		log.info("From LoginRestBean PrepareMailContentMethod...Start");
		Candidate candidate = candidateService.getCandidateFromEmail(email,
				false, false);
		StringBuilder sb = new StringBuilder();
		sb.append("Hi &nbsp;" + candidate.getFirstName() + ",");
		sb.append("<br><br/>You can access Carrer Success application with below credentials.");
		sb.append("<br><br/> User Id: " + carrerAccount.getEmail());
		sb.append(" <br><br/> Password: ");
		sb.append(carrerAccount.getCareerPwd());
		sb.append(" <br><br/>You can change password after login to application.");
		sb.append("<br><br/>All The Best! ");
		sb.append("<br/><br/><br/><b>*** This is an automatically generated email, please do not reply ***</b>");
		try {
			commService.sendEmail(null, email.trim(),
					"Career Success App-Forget Password", sb.toString());
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("From LoginRestBean PrepareMailContentMethod...End");
		return sb.toString();
	}

	public String generateRandomString() {

		StringBuffer randStr = new StringBuffer();
		for (int i = 0; i < RANDOM_STRING_LENGTH; i++) {
			int number = getRandomNumber();
			char ch = CHAR_LIST.charAt(number);
			randStr.append(ch);
		}
		return randStr.toString();
	}

	private int getRandomNumber() {
		int randomInt = 0;
		Random randomGenerator = new Random();
		randomInt = randomGenerator.nextInt(CHAR_LIST.length());
		if (randomInt - 1 == -1) {
			return randomInt;
		} else {
			return randomInt - 1;
		}
	}
}

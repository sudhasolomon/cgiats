package com.uralian.cgiats.rest;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.uralian.cgiats.model.Address;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CarrerAccount;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.CareerAccountService;
import com.uralian.cgiats.service.CommService;
import com.uralian.cgiats.service.CommService.AttachmentInfo;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.UserService;
import com.uralian.cgiats.util.Utils;

@Component
@Path("/restRegister")
public class RegisterRestBean extends SpringBeanAutowiringSupport {

	private static final PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

	@Autowired
	public UserService userService;

	@Autowired
	public CandidateService candidateService;

	@Autowired
	public CareerAccountService careerAccount;

	@Autowired
	private CommService commService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String candidateReg(@QueryParam("candidate") Integer candidateId,
			@QueryParam("password") String password,
			@QueryParam("regId") String registerId,
			@QueryParam("firstName") String firstName,
			@QueryParam("lastName") String lastName,
			@QueryParam("email") String email,
			@QueryParam("phone") String phone,
			@QueryParam("candidate_mobile") String createdUser,
			@QueryParam("currentLocation") String currentLocation,
			@Context HttpServletRequest request) throws JSONException {
		String encryptPwd = passwordEncoder.encodePassword(password, "CGI");
		Candidate candidate = null;
		Candidate result = null;
		JSONObject jsonObj = new JSONObject();

		int errorCount = 0;

		if (Utils.isBlank(password)) {
			errorCount = errorCount + 1;
		}
		if (Utils.isBlank(email)) {
			errorCount = errorCount + 1;
		}
		if (Utils.isBlank(firstName)) {
			errorCount = errorCount + 1;
		}
		if (Utils.isBlank(lastName)) {
			errorCount = errorCount + 1;
		}
		if (Utils.isBlank(phone)) {
			errorCount = errorCount + 1;
		}
		if (Utils.isBlank(currentLocation)) {
			errorCount = errorCount + 1;
		}

		if (errorCount != 0) {
			jsonObj.put("statusMessage", "All fields are mandatory");
			jsonObj.put("statusCode", 11);
			return jsonObj.toString();
		}
		candidate = candidateService.getCandidateFromEmail(email, false, false);
		if (candidate != null) {

			jsonObj.put("statusMessage", "Candidate already exists");
			jsonObj.put("statusCode", 11);
			return jsonObj.toString();
		}
		candidate = new Candidate();
		candidate.setFirstName(firstName);
		candidate.setLastName(lastName);
		candidate.setEmail(email);
		candidate.setPhone(phone);
		candidate.setDeleteFlag(0);
		candidate.setCreatedUser(createdUser);
		Address a = new Address();
		a.setCity(currentLocation);
		candidate.setAddress(a);

		try {
			result = candidateService.saveCandidateFromMobile(candidate);
			System.out.println(result);
		} catch (ServiceException e) {
			e.printStackTrace();
			jsonObj.put("statusMessage", "Internal error, try after some time");
			jsonObj.put("statusCode", 21);
			return jsonObj.toString();
		}

		CarrerAccount careerAct = new CarrerAccount();
		careerAct.setPhone(phone);
		careerAct.setEmail(email);
		careerAct.setCareerPwd(encryptPwd);
//		careerAct.setCareerPwd(password);
		careerAct.setCandidateId(result.getId());
		try {
			careerAccount.saveCareerAccount(careerAct);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jsonObj.put("statusMessage", "Internal error, try after some time");
			jsonObj.put("statusCode", 21);
			return jsonObj.toString();
		}
		prepareMailContent(careerAct);
		jsonObj.put("statusCode", 1);
		jsonObj.put("statusMessage",
				"Thank you, You have successfully registered.");

		return jsonObj.toString();

	}

	private String prepareMailContent(CarrerAccount carrerAccount) {
		// TODO Auto-generated method stub
		String email = carrerAccount.getEmail();
		Candidate candidate = candidateService.getCandidateFromEmail(
				carrerAccount.getEmail(), false, false);
		/*
		 * StringBuilder sb = new StringBuilder(); sb.append("Hi "); sb.append(
		 * "<table>Thank you for registering with us - we shall be delighted to serve you! "
		 * ); sb.append("<br><br/><b> Login Information</b>  ");
		 * sb.append("<br><br/><b> Email</b>"+carrerAccount.getEmail());
		 * sb.append("<br><br/><b> Password</b>"+carrerAccount.getCareerPwd());
		 * sb.append("<br><br/>");
		 * sb.append("<br><br/><b> Contact Information</b>");
		 * sb.append("<br><br/><b> First Name</b>"+candidate.getFirstName());
		 * sb.append("<br><br/><b> Last Name</b>"+candidate.getLastName());
		 * sb.append("<br><br/>"); sb.append("<br><br/><b> Address</b>");
		 * sb.append("<br><br/><b> City</b>"+candidate.getAddress());
		 * //sb.append("<br><br/><b> State</b>");
		 * //sb.append("<br><br/><b> Zipcode</b>");
		 * sb.append("<br><br/><b> Mobile</b>"+candidate.getPhone()); sb.append(
		 * "<br/><br/><br/><b>*** Please login through your mobile and check your application status and new jobs ***</b>"
		 * ); sb.append(
		 * "<br/><br/><br/><b>*** This is an automatically generated email, please do not reply ***</b>"
		 * );
		 */
		AttachmentInfo ai = null;
		String subject = "Welcome to CGI : Career Success App";
		String mailMsg = "Hi ";
		mailMsg = mailMsg + candidate.getFirstName() + ",";
		mailMsg = mailMsg
				+ "<br/><br/>"
				+ " <div style='text-align:center'><b>Thank you for registering with us - we shall be delighted to serve you! </b></div><br><br>"
				+ "<table style='width:500px'>"
				+ "<tr><td><h2><u><i>Contact Information</i></u></h2></td></tr>"
				+ "<tr><td><b>First Name </b></td><td>"
				+ "<b>:</b>"
				+ "&nbsp;&nbsp;"
				+ candidate.getFirstName()
				+ "</td></tr>"
				+ "<tr><td><b>Last Name </b></td><td>"
				+ "<b>:</b>"
				+ "&nbsp;&nbsp;"
				+ candidate.getLastName()
				+ "</td></tr>"
				+ "<tr><td><b>Email </b></td><td>"
				+ "<b>:</b>"
				+ "&nbsp;&nbsp;"
				+ carrerAccount.getEmail()
				+ "</td></tr>"
				+ "<tr><td><b>City </b></td><td>"
				+ "<b>:</b>"
				+ "&nbsp;&nbsp;"
				+ candidate.getAddress()
				+ "</td></tr>"
				+ "<tr><td><b>Mobile </b></td><td>"
				+ "<b>:</b>"
				+ "&nbsp;&nbsp;"
				+ candidate.getPhone()
				+ "</td></tr></table><br/>"
				+ "</td></tr></table><br/>"
				+ " <table><tr><td><b>*** Please login through your mobile and check your application status and new jobs ***</b></td></tr>"
				+ " <tr><td><b>*** This is an automatically generated email, please do not reply ***</b></td></tr></table>";
		try {
			// commService.sendEmail(null, email.trim(),
			// "Welcome to CGI : Career Successes App", sb.toString());
			// commService.sendEmail(null, email.trim(), subject, mailMsg);
			commService.sendEmail(null, email.trim(), subject, mailMsg);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}

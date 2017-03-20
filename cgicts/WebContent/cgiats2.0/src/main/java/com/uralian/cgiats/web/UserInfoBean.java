///*
// * UserInfoBean.java May 16, 2012
// *
// * Copyright 2012 Uralian, LLC. All rights reserved.
// */
//package com.uralian.cgiats.web;
//
//import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
//
//import java.io.Serializable;
//import java.security.Principal;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.List;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import javax.faces.context.FacesContext;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Component;
//
//import com.uralian.cgiats.model.CandidateSearchDto;
//import com.uralian.cgiats.model.LoginAttempts;
//import com.uralian.cgiats.model.User;
//import com.uralian.cgiats.service.LoginDetailsService;
//import com.uralian.cgiats.service.ServiceException;
//import com.uralian.cgiats.service.UserService;
//import com.uralian.cgiats.util.Utils;
//
///**
// * @author Vlad Orzhekhovskiy
// */
//@Component
//@Scope("session")
//public class UserInfoBean extends UIBean implements Serializable {
//	private static final long serialVersionUID = -1697461607437391447L;
//
//	@Autowired
//	private UserService userService;
//
//	private User user;
//	private String currentUserPassword;
//
//	private static CandidateSearchDto lastCriteria;
//
//	private Date loginTime;
//	private Date logoutTime;
//	private String userId;
//	private String assignedBdm;
//	private static String portalId;
//	private static String currentUserName;
//	private static String userIdOnline;
//	private boolean renderReturn = false;
//	private LoginAttempts loginAttempts = new LoginAttempts();
//
//	@Autowired
//	private LoginDetailsService loginDetailsService;
//
//	/**
//	 */
//	public UserInfoBean() {
//		log.debug("Bean created: " + hashCode());
//	}
//
//	/**
//	 */
//	@PostConstruct
//	public void init() {
//		log.debug("Bean initialized: " + hashCode());
//
//	}
//
//	/**
//	 * @return the assignedBdm
//	 */
//	public String getAssignedBdm() {
//		return assignedBdm;
//	}
//
//	/**
//	 * @param assignedBdm
//	 *            the assignedBdm to set
//	 */
//	public void setAssignedBdm(String assignedBdm) {
//		this.assignedBdm = assignedBdm;
//	}
//
//	/**
//	 */
//	@PreDestroy
//	public void destroy() {
//		try {
//			User u = userService.loadUser(user.getId());
//			u.setLastLogin(loginTime);
//			userService.updateUser(u);
//
//			List<Long> list = null;
//			if (loginTime != null && userId != null) {
//				list = loginDetailsService.getLoginAttemptByDate(loginTime,
//						userId);
//			}
//			logoutTime = new Date();
//			if (list.size() != 0 && list.get(0) != null) {
//				Long duration = (logoutTime.getTime() - loginTime.getTime())
//						+ list.get(0);
//				LoginAttempts loginAtt = loginDetailsService
//						.loadLoginAttempts(loginAttempts.getId());
//				loginAtt.setLogoutDate(logoutTime);
//				loginAtt.setDurationTime(duration);
//				log.info("logout time" + loginAtt.getLogoutDate());
//				loginDetailsService.updateLoginDetails(loginAtt);
//
//			} else {
//				Long duration = (logoutTime.getTime() - loginTime.getTime());
//				LoginAttempts loginAtt = loginDetailsService
//						.loadLoginAttempts(loginAttempts.getId());
//				loginAtt.setLogoutDate(logoutTime);
//				loginAtt.setDurationTime(duration);
//				log.info("logout time" + loginAtt.getLogoutDate());
//				loginDetailsService.updateLoginDetails(loginAtt);
//			}
//
//		} catch (ServiceException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * @return
//	 */
//	public User getCurrentUser() {
//		if (user == null) {
//			Principal principal = getFacesContext().getExternalContext()
//					.getUserPrincipal();
//			if (principal == null)
//				return null;
//
//			userId = principal.getName();
//			user = userService.loadUser(userId);
//			assignedBdm = user.getAssignedBdm();
//			if (assignedBdm == null) {
//				setAssignedBdm(userId);
//			}
//
//			try {
//				loginTime = new Date();
//				loginAttempts.setLoginDate(loginTime);
//				loginAttempts = loginDetailsService
//						.saveLoginDetails(loginAttempts);
//			} catch (ServiceException e) {
//				e.printStackTrace();
//			}
//		}
//		return user;
//	}
//
//	/**
//	 * @return the currentUserPassword.
//	 */
//	public String getCurrentUserPassword() {
//		return currentUserPassword;
//	}
//
//	/**
//	 * @param currentUserPassword
//	 *            the currentUserPassword to set.
//	 */
//	public void setCurrentUserPassword(String currentUserPassword) {
//		this.currentUserPassword = currentUserPassword;
//	}
//
//	/**
//	 * @return
//	 */
//	public List<User> getSubordinates() {
//		List<User> users = userService.listUsers();
//
//		List<User> subordinates = new ArrayList<User>();
//		for (User user : users) {
//			// if (user.getUserRole() == UserRole.Recruiter) //as per ken
//			// request (ken wants to show all users in assigned to field in
//			// joborders) condition is commented out
//			subordinates.add(user);
//		}
//		Collections.sort(subordinates, new Comparator<User>() {
//			@Override
//			public int compare(User u1, User u2) {
//				String name1 = u1.getFullName();
//				String name2 = u2.getFullName();
//				if (!Utils.isBlank(name1) && !Utils.isBlank(name2)){
//					if(name1.equalsIgnoreCase(name2))
//					return -1;
//					else return 1;
//				}
//				else{
//					return u1.getId().compareToIgnoreCase(u2.getId());
//				}
//				
//			}
//		});
//
//		return subordinates;
//	}
//
//	/**
//	 * @return
//	 */
//	public List<User> getAllUsers() {
//		List<User> users = userService.listUsers();
//
//		ArrayList<User> allUsers = new ArrayList<User>();
//		for (User user : users) {
//			allUsers.add(user);
//		}
//		return allUsers;
//	}
//
//	/**
//	 * @return
//	 */
//	public List<User> getAllBdms() {
//		List<User> users = userService.listBdms();
//		ArrayList<User> allBdms = new ArrayList<User>();
//		for (User user : users) {
//			allBdms.add(user);
//		}
//		return allBdms;
//	}
//
//	/**
//	 * @return the lastCriteria.
//	 */
//	public static CandidateSearchDto getLastCriteria() {
//		return lastCriteria;
//	}
//
//	/**
//	 * @param lastCriteria
//	 *            the lastCriteria to set.
//	 */
//	public static void setLastCriteria(CandidateSearchDto lastCriteria) {
//		UserInfoBean.lastCriteria = lastCriteria;
//	}
//
//	/**
//	 * @return
//	 */
//	public String saveUpdateUser() {
//		log.debug("Saving/updating user: " + user);
//
//		if (!Utils.isBlank(currentUserPassword))
//			user.setPassword(currentUserPassword);
//
//		try {
//			userService.updateUser(user);
//			log.debug("User updated: " + user);
//			addInfoMessage("Success", "User successfully updated");
//
//			user = null;
//			FacesContext context = FacesContext.getCurrentInstance();
//			context.getExternalContext().getFlash().setKeepMessages(true);
//			return "home?faces-redirect=true&amp;includeViewParams=true";
//		} catch (ServiceException e) {
//			e.printStackTrace();
//			addErrorMessage("userForm", SEVERITY_ERROR, e);
//			return null;
//		}
//	}
//
//	public static String getUserIdOnline() {
//		return userIdOnline;
//	}
//
//	public static void setUserIdOnline(String userIdOnline) {
//		UserInfoBean.userIdOnline = userIdOnline;
//	}
//
//	public static String getCurrentUserName() {
//		return currentUserName;
//	}
//
//	public static void setCurrentUserName(String currentUserName) {
//		UserInfoBean.currentUserName = currentUserName;
//	}
//
//	public boolean isRenderReturn() {
//		return renderReturn;
//	}
//
//	public void setRenderReturn(boolean renderReturn) {
//		this.renderReturn = renderReturn;
//	}
//
//	public static String getPortalId() {
//		return portalId;
//	}
//
//	public static void setPortalId(String portalId) {
//		UserInfoBean.portalId = portalId;
//	}
//
//}
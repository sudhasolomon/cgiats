/*
 * User.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import com.uralian.cgiats.util.Utils;

/**
 * @author Vlad Orzhekhovskiy
 */
@Entity
@Table(name = "USER_ACCT")
public class User extends AuditableEntity<String> {
	private static final long serialVersionUID = -6943667123062154813L;

	private static final PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

	@Id
	@Column(name = "USER_ID")
	private String userId;

	@Enumerated(EnumType.STRING)
	@Column(name = "USER_ROLE", nullable = false)
	private UserRole userRole;

	@Column(name = "USER_PWD", nullable = false)
	private String password;

	@Column(name = "FIRST_NAME")
	private String firstName;

	@Column(name = "LAST_NAME")
	private String lastName;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "PHONE")
	private String phone;

	@Column(name = "CITY")
	private String city;

	@Column(name = "ASSIGNED_BDM")
	private String assignedBdm;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_LOGIN")
	private Date lastLogin;

	@Column(name = "OFFICE_LOCATION")
	private String officeLocation;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "EXECUTIVE_DASHBOARD")
	private String executiveDASHBOARD;

	@Lob
	@Column(name = "profileImage")
	private byte[] profileImage;
	
	@Column(name = "user_profileImage")
	private byte[] userProfileImage;

	@Column(name = "profileImage_ext")
	private String profileImageExt;

	@Column(name = "accessToken")
	private String accessToken;
	
	@Column(name = "employee_id")
	private String employeeId;
	
	@Column(name = "join_date")
	private Date joinDate;

	@Column(name = "level")
	private Integer level;

	@Column(name = "relieving_date")
	private Date relievingDate;

	@Column(name = "noofstartspermonth")
	private Float noOfStartsPerMonth;

	@Column(name = "designation")
	private String designation;
	@ManyToOne
	@JoinColumn(name = "level", insertable = false, updatable = false)
	private Designation designationObj;

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Date getRelievingDate() {
		return relievingDate;
	}

	public void setRelievingDate(Date relievingDate) {
		this.relievingDate = relievingDate;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public Float getNoOfStartsPerMonth() {
		return noOfStartsPerMonth;
	}

	public void setNoOfStartsPerMonth(Float noOfStartsPerMonth) {
		this.noOfStartsPerMonth = noOfStartsPerMonth;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public byte[] getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(byte[] profileImage) {
		this.profileImage = profileImage;
	}

	public String getProfileImageExt() {
		return profileImageExt;
	}

	public void setProfileImageExt(String profileImageExt) {
		this.profileImageExt = profileImageExt;
	}

	public byte[] getUserProfileImage() {
		return userProfileImage;
	}

	public void setUserProfileImage(byte[] userProfileImage) {
		this.userProfileImage = userProfileImage;
	}

	/**
	*/
	public User() {
	}

	/**
	 * @param userId
	 * @param userRole
	 */
	public User(String userId, UserRole userRole) {
		this.userId = userId;
		this.userRole = userRole;
	}

	/**
	 * @param userId
	 * @param updatedBy
	 */
	public User(String userId, String updatedBy) {
		this.userId = userId;
		this.setUpdatedBy(updatedBy);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.model.AbstractEntity#getId()
	 */
	@Override
	public String getId() {
		return userId;
	}

	/**
	 * @param userId
	 */
	public void setId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the userRole.
	 */
	public UserRole getUserRole() {
		return userRole;
	}

	/**
	 * @param userRole
	 *            the userRole to set.
	 */
	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	/**
	 * @return
	 */
	public boolean isAdmin() {
		return userRole == UserRole.Administrator;
	}

	/**
	 * @return
	 */
	public boolean isRecruiter() {
		return userRole == UserRole.Recruiter;
	}

	/**
	 * @return
	 */
	public boolean isEM() {
		return userRole == UserRole.EM;
	}

	/**
	 * @return
	 */
	public boolean isDM() {
		return userRole == UserRole.DM;
	}

	/**
	 * @return
	 */
	public boolean isADM() {
		return userRole == UserRole.ADM;
	}

	public boolean isIn_Recruiter() {
		return userRole == UserRole.IN_Recruiter;
	}

	public boolean isIn_DM() {
		return userRole == UserRole.IN_DM;
	}
	
	public boolean isIn_TL() {
		return userRole == UserRole.IN_TL;
	}

	/**
	 * @return the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set.
	 */
	public void setPassword(String password) {
		this.password = passwordEncoder.encodePassword(password, "CGI");
	}

	/**
	 * @return the firstName.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the phone.
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set.
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the officeLocation
	 */
	public String getOfficeLocation() {
		return officeLocation;
	}

	/**
	 * @param officeLocation
	 *            the officeLocation to set
	 */
	public void setOfficeLocation(String officeLocation) {
		this.officeLocation = officeLocation;
	}

	/**
	 * @return
	 */
	public String getFullName() {
		StringBuffer sb = new StringBuffer();
		if (!Utils.isBlank(firstName))
			sb.append(firstName).append(" ");
		if (!Utils.isBlank(lastName))
			sb.append(lastName);
		return sb.toString().trim();
	}

	/**
	 * @return
	 */
	public String getLastFirst() {
		StringBuffer sb = new StringBuffer();
		if (!Utils.isBlank(lastName))
			sb.append(lastName);
		if (!Utils.isBlank(lastName) && !Utils.isBlank(firstName))
			sb.append(", ");
		if (!Utils.isBlank(firstName))
			sb.append(firstName);
		return sb.toString().trim();
	}

	/**
	 * @return the .
	 */
	public Date getLastLogin() {
		return lastLogin;
	}

	/**
	 * @param lastLogin
	 *            the lastLogin to set.
	 */
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.model.AbstractEntity#getBusinessKey()
	 */
	@Override
	protected Object getBusinessKey() {
		return userId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [userId=" + userId + ", userRole=" + userRole + "]";
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the assignedBdm
	 */
	public String getAssignedBdm() {
		return assignedBdm;
	}

	/**
	 * @param assignedBdm
	 *            the assignedBdm to set
	 */

	public void setAssignedBdm(String assignedBdm) {
		/*
		 * if (assignedBdm != null) { StringTokenizer st = new
		 * StringTokenizer(assignedBdm, ","); String stBdm = st.nextToken();
		 * stBdm = stBdm.substring(13, stBdm.length()); assignedBdm = stBdm; }
		 */
		this.assignedBdm = assignedBdm;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public String getExecutiveDASHBOARD() {
		return executiveDASHBOARD;
	}

	public void setExecutiveDASHBOARD(String executiveDASHBOARD) {
		this.executiveDASHBOARD = executiveDASHBOARD;
	}

	/**
	 * @return the designationObj
	 */
	public Designation getDesignationObj() {
		return designationObj;
	}

	/**
	 * @param designationObj
	 *            the designationObj to set
	 */
	public void setDesignationObj(Designation designationObj) {
		this.designationObj = designationObj;
	}

}
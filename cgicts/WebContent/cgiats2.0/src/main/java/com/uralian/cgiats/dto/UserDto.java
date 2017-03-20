/*
 * User.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.dto;

import java.util.Date;

import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.util.Utils;

public class UserDto {

	private String userId;

	private String password;

	private String firstName;

	private String lastName;

	private String email;

	private String phone;

	private String city;

	private String assignedBdm;

	private Date lastLogin;

	private String officeLocation;

	private String status;

	private String executiveDASHBOARD;

	private String fullName;

	private String recFullName;

	private UserRole userRole;
	private String loginAttemptId;

	private Integer minStartCount;
	private Integer maxStartCount;
	private Integer avgStartCount;

	public Integer getAvgStartCount() {
		return avgStartCount;
	}

	public void setAvgStartCount(Integer avgStartCount) {
		this.avgStartCount = avgStartCount;
	}

	private String strJoinDate;

	private Date joinDate;

	private String strServedDate;

	private Date servedDate;

	private String recDesignation;

	private String designation;

	private Integer noOfStarts;
	
	private Integer noOfBackOuts;

	private Integer noOfActiveStarts;
	private Integer noOfInActiveStarts;

	private String level;

	private String joiningDate;

	private String relievingDate;

	private String employeeId;

	private Double avgHires;

	private Integer statusValue;

	private Integer noOfMonthsWorked;

	public void setNoOfBackOuts(Integer noOfBackOuts) {
		this.noOfBackOuts = noOfBackOuts;
	}
	
	public Integer getNoOfBackOuts() {
		return noOfBackOuts;
	}
	
	public Integer getNoOfActiveStarts() {
		return noOfActiveStarts;
	}

	public void setNoOfActiveStarts(Integer noOfActiveStarts) {
		this.noOfActiveStarts = noOfActiveStarts;
	}

	public Integer getNoOfInActiveStarts() {
		return noOfInActiveStarts;
	}

	public void setNoOfInActiveStarts(Integer noOfInActiveStarts) {
		this.noOfInActiveStarts = noOfInActiveStarts;
	}

	public String getStrJoinDate() {
		return strJoinDate;
	}

	public void setStrJoinDate(String strJoinDate) {
		this.strJoinDate = strJoinDate;
	}

	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	public String getStrServedDate() {
		return strServedDate;
	}

	public void setStrServedDate(String strServedDate) {
		this.strServedDate = strServedDate;
	}

	public Date getServedDate() {
		return servedDate;
	}

	public void setServedDate(Date servedDate) {
		this.servedDate = servedDate;
	}

	public Integer getMinStartCount() {
		return minStartCount;
	}

	public void setMinStartCount(Integer minStartCount) {
		this.minStartCount = minStartCount;
	}

	public Integer getMaxStartCount() {
		return maxStartCount;
	}

	public void setMaxStartCount(Integer maxStartCount) {
		this.maxStartCount = maxStartCount;
	}

	private String createdBy;
	private String updatedBy;
	private String createdon;
	private String strCreatedOn;
	private String updatedOn;

	private String newPassword;
	private String strLastLogin;
	private String base64Image;
	private String imageExt;
	private Boolean isFromProfile;

	private Boolean isDirectCall = true;

	/*
	 * public String getFullName() { StringBuffer sb = new StringBuffer(); if
	 * (!Utils.isBlank(firstName)) sb.append(firstName).append(" "); if
	 * (!Utils.isBlank(lastName)) sb.append(lastName); return
	 * sb.toString().trim(); }
	 */

	public void setLoginAttemptId(String loginAttemptId) {
		this.loginAttemptId = loginAttemptId;
	}

	public String getLoginAttemptId() {
		return loginAttemptId;
	}

	public String getFullName() {
		return fullName;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAssignedBdm() {
		return assignedBdm;
	}

	public void setAssignedBdm(String assignedBdm) {
		this.assignedBdm = assignedBdm;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getOfficeLocation() {
		return officeLocation;
	}

	public void setOfficeLocation(String officeLocation) {
		this.officeLocation = officeLocation;
	}

	public String getStatus() {
		return status;
	}

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
	 * @return the userRole
	 */
	public UserRole getUserRole() {
		return userRole;
	}

	/**
	 * @param userRole
	 *            the userRole to set
	 */
	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	/**
	 * @return the strLastLogin
	 */
	public String getStrLastLogin() {
		return strLastLogin;
	}

	/**
	 * @param strLastLogin
	 *            the strLastLogin to set
	 */
	public void setStrLastLogin(String strLastLogin) {
		this.strLastLogin = strLastLogin;
	}

	/**
	 * @return the base64Image
	 */
	public String getBase64Image() {
		return base64Image;
	}

	/**
	 * @param base64Image
	 *            the base64Image to set
	 */
	public void setBase64Image(String base64Image) {
		this.base64Image = base64Image;
	}

	/**
	 * @return the imageExt
	 */
	public String getImageExt() {
		return imageExt;
	}

	/**
	 * @param imageExt
	 *            the imageExt to set
	 */
	public void setImageExt(String imageExt) {
		this.imageExt = imageExt;
	}

	/**
	 * @return the isDirectCall
	 */
	public Boolean getIsDirectCall() {
		return isDirectCall;
	}

	/**
	 * @param isDirectCall
	 *            the isDirectCall to set
	 */
	public void setIsDirectCall(Boolean isDirectCall) {
		this.isDirectCall = isDirectCall;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy
	 *            the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return the isFromProfile
	 */
	public Boolean getIsFromProfile() {
		return isFromProfile != null ? isFromProfile : true;
	}

	/**
	 * @param isFromProfile
	 *            the isFromProfile to set
	 */
	public void setIsFromProfile(Boolean isFromProfile) {
		this.isFromProfile = isFromProfile;
	}

	public String getCreatedon() {
		return createdon;
	}

	public void setCreatedon(String createdon) {
		this.createdon = createdon;
	}

	public String getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getStrCreatedOn() {
		return strCreatedOn;
	}

	public void setStrCreatedOn(String strCreatedOn) {
		this.strCreatedOn = strCreatedOn;
	}

	public String getRecDesignation() {
		return recDesignation;
	}

	public void setRecDesignation(String recDesignation) {
		this.recDesignation = recDesignation;
	}

	/**
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * @param designation
	 *            the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * @return the noOfStarts
	 */
	public Integer getNoOfStarts() {
		return noOfStarts;
	}

	/**
	 * @param noOfStarts
	 *            the noOfStarts to set
	 */
	public void setNoOfStarts(Integer noOfStarts) {
		this.noOfStarts = noOfStarts;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getJoiningDate() {
		return joiningDate;
	}

	public void setJoiningDate(String joiningDate) {
		this.joiningDate = joiningDate;
	}

	public String getRelievingDate() {
		return relievingDate;
	}

	public void setRelievingDate(String relievingDate) {
		this.relievingDate = relievingDate;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	/**
	 * @return the avgHires
	 */
	public Double getAvgHires() {
		return avgHires;
	}

	/**
	 * @param avgHires
	 *            the avgHires to set
	 */
	public void setAvgHires(Double avgHires) {
		this.avgHires = avgHires;
	}

	/**
	 * @return the noOfMonthsWorked
	 */
	public Integer getNoOfMonthsWorked() {
		return noOfMonthsWorked;
	}

	/**
	 * @param noOfMonthsWorked
	 *            the noOfMonthsWorked to set
	 */
	public void setNoOfMonthsWorked(Integer noOfMonthsWorked) {
		this.noOfMonthsWorked = noOfMonthsWorked;
	}

	/**
	 * @return the statusValue
	 */
	public Integer getStatusValue() {
		return statusValue;
	}

	/**
	 * @param statusValue
	 *            the statusValue to set
	 */
	public void setStatusValue(Integer statusValue) {
		this.statusValue = statusValue;
	}

	/**
	 * @return the recFullName
	 */
	public String getRecFullName() {
		return recFullName;
	}

	/**
	 * @param recFullName
	 *            the recFullName to set
	 */
	public void setRecFullName(String recFullName) {
		this.recFullName = recFullName;
	}

}

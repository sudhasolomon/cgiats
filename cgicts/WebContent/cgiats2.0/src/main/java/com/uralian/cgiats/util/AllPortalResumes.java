package com.uralian.cgiats.util;

import java.util.Date;

public class AllPortalResumes {

	private String orderId;
	private String candidateId;
	private String fullName;
	private String email;
	private String status;
	private String canCreatedBy;
	private String subCreatedBy; 
	private Date subCreatedOn;

	public AllPortalResumes(){

	}

	public String getCandidateId() {
		return candidateId;
	}
	public String getFullName() {
		return fullName;
	}
	public String getEmail() {
		return email;
	}
	public String getStatus() {
		return status;
	}
	public String getCanCreatedBy() {
		return canCreatedBy;
	}
	public String getSubCreatedBy() {
		return subCreatedBy;
	}
	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setCanCreatedBy(String canCreatedBy) {
		this.canCreatedBy = canCreatedBy;
	}
	public void setSubCreatedBy(String subCreatedBy) {
		this.subCreatedBy = subCreatedBy;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Date getSubCreatedOn() {
		return subCreatedOn;
	}
	public void setSubCreatedOn(Date subCreatedOn) {
		this.subCreatedOn = subCreatedOn;
	}
}

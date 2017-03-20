package com.uralian.cgiats.model;

import java.util.Date;

public class SubmitalStats {

	private int submittedCount;
	private int dmrejCount;
	private int acceptedCount;
	private int interviewingCount;
	private int confirmedCount;
	private int rejectedCount;
	private int startedCount;
	private int backOutCount;
	private int outOfProjCount;
	private String month;
	private String year;
	private int openCount;
	private int closedCount;
	private String submitalDate;
	private String officeLocation; 
	private String createdBy;
	private String recruiterName;
	private String orderCreatedDm;
	private String assignedDm;
	private String userRole;
	private String dm;
	
	public String getDm() {
		return dm;
	}
	public void setDm(String dm) {
		this.dm = dm;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public int getSubmittedCount() {
		return submittedCount;
	}
	public void setSubmittedCount(int submittedCount) {
		this.submittedCount = submittedCount;
	}
	public int getDmrejCount() {
		return dmrejCount;
	}
	public void setDmrejCount(int dmrejCount) {
		this.dmrejCount = dmrejCount;
	}
	public int getAcceptedCount() {
		return acceptedCount;
	}
	public void setAcceptedCount(int acceptedCount) {
		this.acceptedCount = acceptedCount;
	}
	public int getInterviewingCount() {
		return interviewingCount;
	}
	public void setInterviewingCount(int interviewingCount) {
		this.interviewingCount = interviewingCount;
	}
	public int getConfirmedCount() {
		return confirmedCount;
	}
	public void setConfirmedCount(int confirmedCount) {
		this.confirmedCount = confirmedCount;
	}
	public int getRejectedCount() {
		return rejectedCount;
	}
	public void setRejectedCount(int rejectedCount) {
		this.rejectedCount = rejectedCount;
	}
	public int getStartedCount() {
		return startedCount;
	}
	public void setStartedCount(int startedCount) {
		this.startedCount = startedCount;
	}
	public int getBackOutCount() {
		return backOutCount;
	}
	public void setBackOutCount(int backOutCount) {
		this.backOutCount = backOutCount;
	}
	public int getOutOfProjCount() {
		return outOfProjCount;
	}
	public void setOutOfProjCount(int outOfProjCount) {
		this.outOfProjCount = outOfProjCount;
	}
	
	public int getOpenCount() {
		return openCount;
	}
	public void setOpenCount(int openCount) {
		this.openCount = openCount;
	}
	public int getClosedCount() {
		return closedCount;
	}
	public void setClosedCount(int closedCount) {
		this.closedCount = closedCount;
	}
	public String getSubmitalDate() {
		return submitalDate;
	}
	public void setSubmitalDate(String submitalDate) {
		this.submitalDate = submitalDate;
	}
	public String getOfficeLocation() {
		return officeLocation;
	}
	public void setOfficeLocation(String officeLocation) {
		this.officeLocation = officeLocation;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getRecruiterName() {
		return recruiterName;
	}
	public void setRecruiterName(String recruiterName) {
		this.recruiterName = recruiterName;
	}
	
	public String getOrderCreatedDm() {
		return orderCreatedDm;
	}
	public void setOrderCreatedDm(String orderCreatedDm) {
		this.orderCreatedDm = orderCreatedDm;
	}
	public String getAssignedDm() {
		return assignedDm;
	}
	public void setAssignedDm(String assignedDm) {
		this.assignedDm = assignedDm;
	}
	
	
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	@Override
	public String toString() {
		return "SubmitalStats [submittedCount=" + submittedCount
				+ ", dmrejCount=" + dmrejCount + ", acceptedCount="
				+ acceptedCount + ", interviewingCount=" + interviewingCount
				+ ", confirmedCount=" + confirmedCount + ", rejectedCount="
				+ rejectedCount + ", startedCount=" + startedCount
				+ ", backOutCount=" + backOutCount + ", outOfProjCount="
				+ outOfProjCount + ", month=" + month + ", year=" + year
				+ ", openCount=" + openCount + ", closedCount=" + closedCount
				+ ", submitalDate=" + submitalDate + ", officeLocation="
				+ officeLocation + ", createdBy=" + createdBy
				+ ", recruiterName=" + recruiterName + ", orderCreatedDm="
				+ orderCreatedDm + ", assignedDm=" + assignedDm + ", userRole="
				+ userRole + "]";
	}

	
}

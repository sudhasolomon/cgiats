package com.uralian.cgiats.dto;

import java.util.List;
import java.util.Map;

import com.uralian.cgiats.model.SubmittalStatus;

public class SubmittalStatsDto {

	private Integer SUBMITTED;
	private Integer DMREJ;
	private Integer ACCEPTED;
	private Integer INTERVIEWING;
	private Integer CONFIRMED;
	private Integer REJECTED;
	private Integer STARTED;
	private Integer activeStarted;
	private Integer inactiveStarted;
	private Integer BACKOUT;
	private Integer OUTOFPROJ;
	private Integer NotUpdated;
	private Integer Total;
	private String Name;
	private String userId;
	private String DM;
	private String Location;
	private Integer openJobOrders;
	private Integer closedJobOrders;
	private String clientName;
	private String jobTitle;
	private String createdDate;
	private String assignedBdm;
	private String candidateFullName;
	private String createdOrUpdatedBy;
	private String createdOrUpdatedOn;
	private Integer rank;
	private String avgDays;
	private String avgTime;
	private String noOfStarts;
	private Integer year;
	private String createdBy;
	private String ExpectedStatsPerMonth;
	private String designation;
	private String count1;
	private String color;
	private String source;
	private List<String> DMs;
	
	
	public Integer getActiveStarted() {
		return activeStarted;
	}
	public void setActiveStarted(Integer activeStarted) {
		this.activeStarted = activeStarted;
	}
	public Integer getInactiveStarted() {
		return inactiveStarted;
	}
	public void setInactiveStarted(Integer inactiveStarted) {
		this.inactiveStarted = inactiveStarted;
	}
	public void setDMs(List<String> dMs) {
		DMs = dMs;
	}
	public List<String> getDMs() {
		return DMs;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSource() {
		return source;
	}
	public void setColor(String color) {
		this.color = color;
	}public String getColor() {
		return color;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getExpectedStatsPerMonth() {
		return ExpectedStatsPerMonth;
	}
	public void setExpectedStatsPerMonth(String expectedStatsPerMonth) {
		ExpectedStatsPerMonth = expectedStatsPerMonth;
	}
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	public String getCount1() {
		return count1;
	}
	public void setCount(String count1) {
		this.count1 = count1;
	}

	private Integer count;
	private String status;
	private Integer orderId;
	
	private Map<SubmittalStatus, Integer> submittalTotalsByStatus;
	
	public void setNoOfStarts(String noOfStarts) {
		this.noOfStarts = noOfStarts;
	}
	public String getNoOfStarts() {
		return noOfStarts;
	}
	
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public void setAvgDays(String avgDays) {
		this.avgDays = avgDays;
	}
	
	public String getAvgDays() {
		return avgDays;
	}
	public void setAvgTime(String avgTime) {
		this.avgTime = avgTime;
	}
	public String getAvgTime() {
		return avgTime;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getAssignedBdm() {
		return assignedBdm;
	}

	public void setAssignedBdm(String assignedBdm) {
		this.assignedBdm = assignedBdm;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getCandidateFullName() {
		return candidateFullName;
	}

	public void setCandidateFullName(String candidateFullName) {
		this.candidateFullName = candidateFullName;
	}

	public Integer getOpenJobOrders() {
		return openJobOrders;
	}

	public void setOpenJobOrders(Integer openJobOrders) {
		this.openJobOrders = openJobOrders;
	}

	public Integer getClosedJobOrders() {
		return closedJobOrders;
	}

	public void setClosedJobOrders(Integer closedJobOrders) {
		this.closedJobOrders = closedJobOrders;
	}

	public Integer getSUBMITTED() {
		return SUBMITTED;
	}

	public void setSUBMITTED(Integer sUBMITTED) {
		SUBMITTED = sUBMITTED;
	}

	public Integer getDMREJ() {
		return DMREJ;
	}

	public void setDMREJ(Integer dMREJ) {
		DMREJ = dMREJ;
	}

	public Integer getACCEPTED() {
		return ACCEPTED;
	}

	public void setACCEPTED(Integer aCCEPTED) {
		ACCEPTED = aCCEPTED;
	}

	public Integer getINTERVIEWING() {
		return INTERVIEWING;
	}

	public void setINTERVIEWING(Integer iNTERVIEWING) {
		INTERVIEWING = iNTERVIEWING;
	}

	public Integer getCONFIRMED() {
		return CONFIRMED;
	}

	public void setCONFIRMED(Integer cONFIRMED) {
		CONFIRMED = cONFIRMED;
	}

	public Integer getREJECTED() {
		return REJECTED;
	}

	public void setREJECTED(Integer rEJECTED) {
		REJECTED = rEJECTED;
	}

	public Integer getSTARTED() {
		return STARTED;
	}

	public void setSTARTED(Integer sTARTED) {
		STARTED = sTARTED;
	}

	public Integer getBACKOUT() {
		return BACKOUT;
	}

	public void setBACKOUT(Integer bACKOUT) {
		BACKOUT = bACKOUT;
	}

	public Integer getOUTOFPROJ() {
		return OUTOFPROJ;
	}

	public void setOUTOFPROJ(Integer oUTOFPROJ) {
		OUTOFPROJ = oUTOFPROJ;
	}

	public Integer getTotal() {
		return Total;
	}

	public void setTotal(Integer total) {
		Total = total;
	}

	public Integer getNotUpdated() {
		return NotUpdated;
	}

	public void setNotUpdated(Integer notUpdated) {
		NotUpdated = notUpdated;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public Map<SubmittalStatus, Integer> getSubmittalTotalsByStatus() {
		return submittalTotalsByStatus;
	}

	public void setSubmittalTotalsByStatus(Map<SubmittalStatus, Integer> submittalTotalsByStatus) {
		this.submittalTotalsByStatus = submittalTotalsByStatus;
	}

	public String getDM() {
		return DM;
	}

	public void setDM(String dM) {
		DM = dM;
	}

	public String getLocation() {
		return Location;
	}

	public void setLocation(String location) {
		Location = location;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the createdOrUpdatedBy
	 */
	public String getCreatedOrUpdatedBy() {
		return createdOrUpdatedBy;
	}

	/**
	 * @param createdOrUpdatedBy
	 *            the createdOrUpdatedBy to set
	 */
	public void setCreatedOrUpdatedBy(String createdOrUpdatedBy) {
		this.createdOrUpdatedBy = createdOrUpdatedBy;
	}

	/**
	 * @return the rank
	 */
	public Integer getRank() {
		return rank;
	}

	/**
	 * @param rank
	 *            the rank to set
	 */
	public void setRank(Integer rank) {
		this.rank = rank;
	}

	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the orderId
	 */
	public Integer getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the createdOrUpdatedOn
	 */
	public String getCreatedOrUpdatedOn() {
		return createdOrUpdatedOn;
	}

	/**
	 * @param createdOrUpdatedOn the createdOrUpdatedOn to set
	 */
	public void setCreatedOrUpdatedOn(String createdOrUpdatedOn) {
		this.createdOrUpdatedOn = createdOrUpdatedOn;
	}
}

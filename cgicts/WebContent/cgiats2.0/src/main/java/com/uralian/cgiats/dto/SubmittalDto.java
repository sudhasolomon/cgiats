/**
 * 
 */
package com.uralian.cgiats.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Sreenath
 *
 */
public class SubmittalDto {

	private String updatedBy;
	private String createdBy;
	private String createdOn;
	private String updatedOn;
	private String status;
	private String candidateName;
	private String submittalId;
	private String jobOrderId;
	private Integer candidateId;
	private String dmName;
	private String recName;
	private String comments;
	private String orderId;
	private UserDto userDto;
	private String document_status;

	private String jobOrderStatus;
	private String level;
	private String userId;
	private String noOfJobOrders;
	private String noOfPositions;
	private String submittedCount;
	private String interviewingCount;
	private String confirmedCount;
	private String startedCount;

	private String activeStartedCount;
	private String inActiveStartedCount;

	private String avgHires;
	private String rank;
	private String netPositions;
	private String assignedTo;
	private String noOfResumesRequired;
	private String turnAroundTime;

	private String color;
	private String performanceStatus;
	private Integer noOfRecs;
	private String jobTitle;
	private String jobClient;
	private Integer minStartCount;
	private Integer maxStartCount;
	private Integer avgStartCount;
	private String location;
	private String userRole;
	private Date joinDate;
	private Date servedDate;

	public String getActiveStartedCount() {
		return activeStartedCount;
	}

	public void setActiveStartedCount(String activeStartedCount) {
		this.activeStartedCount = activeStartedCount;
	}

	public String getInActiveStartedCount() {
		return inActiveStartedCount;
	}

	public void setInActiveStartedCount(String inActiveStartedCount) {
		this.inActiveStartedCount = inActiveStartedCount;
	}

	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	public Date getServedDate() {
		return servedDate;
	}

	public void setServedDate(Date servedDate) {
		this.servedDate = servedDate;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	private Integer activeCount;
	private Integer inActiveCount;

	private Map<String, Integer> activeWithLevelMap;
	private Map<String, Integer> inActiveWithLevelMap;

	private List<SubmittalEventDto> submittalEventHistoryDtoList;
	private CandidateDto candidateDto;
	private IndiaCandidateDto indiacandidateDto;
	private AddEditJobOrderDto jobOrderDto;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getAvgStartCount() {
		return avgStartCount;
	}

	public void setAvgStartCount(Integer avgStartCount) {
		this.avgStartCount = avgStartCount;
	}

	public void setTurnAroundTime(String turnAroundTime) {
		this.turnAroundTime = turnAroundTime;
	}

	public String getTurnAroundTime() {
		return turnAroundTime;
	}

	public void setNoOfResumesRequired(String noOfResumesRequired) {
		this.noOfResumesRequired = noOfResumesRequired;
	}

	public String getNoOfResumesRequired() {
		return noOfResumesRequired;
	}

	public Map<String, Integer> getActiveWithLevelMap() {
		return activeWithLevelMap;
	}

	public void setActiveWithLevelMap(Map<String, Integer> activeWithLevelMap) {
		this.activeWithLevelMap = activeWithLevelMap;
	}

	public Map<String, Integer> getInActiveWithLevelMap() {
		return inActiveWithLevelMap;
	}

	public void setInActiveWithLevelMap(Map<String, Integer> inActiveWithLevelMap) {
		this.inActiveWithLevelMap = inActiveWithLevelMap;
	}

	public Integer getActiveCount() {
		return activeCount;
	}

	public void setActiveCount(Integer activeCount) {
		this.activeCount = activeCount;
	}

	public Integer getInActiveCount() {
		return inActiveCount;
	}

	public void setInActiveCount(Integer inActiveCount) {
		this.inActiveCount = inActiveCount;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setNetPositions(String netPositions) {
		this.netPositions = netPositions;
	}

	public String getNetPositions() {
		return netPositions;
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

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getRank() {
		return rank;
	}

	public void setAvgHires(String avgHires) {
		this.avgHires = avgHires;
	}

	public String getAvgHires() {
		return avgHires;
	}

	public String getSubmittedCount() {
		return submittedCount;
	}

	public void setSubmittedCount(String submittedCount) {
		this.submittedCount = submittedCount;
	}

	public String getConfirmedCount() {
		return confirmedCount;
	}

	public void setConfirmedCount(String confirmedCount) {
		this.confirmedCount = confirmedCount;
	}

	public String getStartedCount() {
		return startedCount;
	}

	public void setStartedCount(String startedCount) {
		this.startedCount = startedCount;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNoOfJobOrders() {
		return noOfJobOrders;
	}

	public void setNoOfJobOrders(String noOfJobOrders) {
		this.noOfJobOrders = noOfJobOrders;
	}

	public String getNoOfPositions() {
		return noOfPositions;
	}

	public void setNoOfPositions(String noOfPositions) {
		this.noOfPositions = noOfPositions;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getJobClient() {
		return jobClient;
	}

	public void setJobClient(String jobClient) {
		this.jobClient = jobClient;
	}

	public CandidateDto getCandidateDto() {
		return candidateDto;
	}

	public void setCandidateDto(CandidateDto candidateDto) {
		this.candidateDto = candidateDto;
	}

	public AddEditJobOrderDto getJobOrderDto() {
		return jobOrderDto;
	}

	public void setJobOrderDto(AddEditJobOrderDto jobOrderDto) {
		this.jobOrderDto = jobOrderDto;
	}

	public Integer getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(Integer candidateId) {
		this.candidateId = candidateId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCandidateName() {
		return candidateName;
	}

	public void setCandidateName(String candidateName) {
		this.candidateName = candidateName;
	}

	public String getSubmittalId() {
		return submittalId;
	}

	public void setSubmittalId(String submittalId) {
		this.submittalId = submittalId;
	}

	public String getJobOrderId() {
		return jobOrderId;
	}

	public void setJobOrderId(String jobOrderId) {
		this.jobOrderId = jobOrderId;
	}

	public String getDmName() {
		return dmName;
	}

	public void setDmName(String dmName) {
		this.dmName = dmName;
	}

	/**
	 * @return the submittalEventHistoryDtoList
	 */
	public List<SubmittalEventDto> getSubmittalEventHistoryDtoList() {
		return submittalEventHistoryDtoList;
	}

	/**
	 * @param submittalEventHistoryDtoList
	 *            the submittalEventHistoryDtoList to set
	 */
	public void setSubmittalEventHistoryDtoList(List<SubmittalEventDto> submittalEventHistoryDtoList) {
		this.submittalEventHistoryDtoList = submittalEventHistoryDtoList;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @return the userDto
	 */
	public UserDto getUserDto() {
		return userDto;
	}

	/**
	 * @param userDto
	 *            the userDto to set
	 */
	public void setUserDto(UserDto userDto) {
		this.userDto = userDto;
	}

	public IndiaCandidateDto getIndiacandidateDto() {
		return indiacandidateDto;
	}

	public void setIndiacandidateDto(IndiaCandidateDto indiacandidateDto) {
		this.indiacandidateDto = indiacandidateDto;
	}

	/**
	 * @return the noOfRecs
	 */
	public Integer getNoOfRecs() {
		return noOfRecs;
	}

	/**
	 * @param noOfRecs
	 *            the noOfRecs to set
	 */
	public void setNoOfRecs(Integer noOfRecs) {
		this.noOfRecs = noOfRecs;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return the performanceStatus
	 */
	public String getPerformanceStatus() {
		return performanceStatus;
	}

	/**
	 * @param performanceStatus
	 *            the performanceStatus to set
	 */
	public void setPerformanceStatus(String performanceStatus) {
		this.performanceStatus = performanceStatus;
	}

	public String getRecName() {
		return recName;
	}

	public void setRecName(String recName) {
		this.recName = recName;
	}

	public String getInterviewingCount() {
		return interviewingCount;
	}

	public void setInterviewingCount(String interviewingCount) {
		this.interviewingCount = interviewingCount;
	}

	/**
	 * @return the jobOrderStatus
	 */
	public String getJobOrderStatus() {
		return jobOrderStatus;
	}

	/**
	 * @param jobOrderStatus
	 *            the jobOrderStatus to set
	 */
	public void setJobOrderStatus(String jobOrderStatus) {
		this.jobOrderStatus = jobOrderStatus;
	}

	public String getDocument_status() {
		return document_status;
	}

	public void setDocument_status(String document_status) {
		this.document_status = document_status;
	}

}

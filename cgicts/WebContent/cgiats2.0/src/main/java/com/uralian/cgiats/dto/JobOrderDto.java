/**
 * 
 */
package com.uralian.cgiats.dto;

import java.util.List;

/**
 * @author Sreenath
 *
 */
public class JobOrderDto {

	private int jobOrderId;
	private String priority;
	private String status;
	// private String[] jobTypes;
	private String type;
	private String client;
	private String location;
	private String excell_Location;
	private String dm;
	private String assignedTo;
	private Integer noOfPositions;
	private Integer openJobOrders;
	private String strUpdatedOn;
	private String updatedDate;
	private String sbm;
	private String activeDays;
	private String title;
	private String keySkills;
	private Boolean hot;
	private String reason;
	private String dmName;
	private String noOfResumesRequired;
	private String jobExpireIn;
	private Integer count;
	private String createdBy;

	public String getNoOfResumesRequired() {
		return noOfResumesRequired;
	}

	public void setNoOfResumesRequired(String noOfResumesRequired) {
		this.noOfResumesRequired = noOfResumesRequired;
	}

	public String getJobExpireIn() {
		return jobExpireIn;
	}

	public void setJobExpireIn(String jobExpireIn) {
		this.jobExpireIn = jobExpireIn;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public boolean isHot() {
		return hot != null ? hot.booleanValue() : false;
	}

	public void setHot(boolean hot) {
		this.hot = hot;
	}

	public String getKeySkills() {
		return keySkills;
	}

	public void setKeySkills(String keySkills) {
		this.keySkills = keySkills;
	}

	// To display properties
	private List<JobOrderFieldDto> jobOrderFieldDtoList;

	public int getJobOrderId() {
		return jobOrderId;
	}

	public void setJobOrderId(int jobOrderId) {
		this.jobOrderId = jobOrderId;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDm() {
		return dm;
	}

	public void setDm(String dm) {
		this.dm = dm;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getSbm() {
		return sbm;
	}

	public void setSbm(String sbm) {
		this.sbm = sbm;
	}

	public String getActiveDays() {
		return activeDays;
	}

	public void setActiveDays(String activeDays) {
		this.activeDays = activeDays;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the jobOrderFieldDtoList
	 */
	public List<JobOrderFieldDto> getJobOrderFieldDtoList() {
		return jobOrderFieldDtoList;
	}

	/**
	 * @param jobOrderFieldDtoList
	 *            the jobOrderFieldDtoList to set
	 */
	public void setJobOrderFieldDtoList(List<JobOrderFieldDto> jobOrderFieldDtoList) {
		this.jobOrderFieldDtoList = jobOrderFieldDtoList;
	}

	/**
	 * @return the excell_Location
	 */
	public String getExcell_Location() {
		return excell_Location;
	}

	/**
	 * @param excell_Location
	 *            the excell_Location to set
	 */
	public void setExcell_Location(String excell_Location) {
		this.excell_Location = excell_Location;
	}

	/**
	 * @return the strUpdatedOn
	 */
	public String getStrUpdatedOn() {
		return strUpdatedOn;
	}

	/**
	 * @param strUpdatedOn
	 *            the strUpdatedOn to set
	 */
	public void setStrUpdatedOn(String strUpdatedOn) {
		this.strUpdatedOn = strUpdatedOn;
	}

	/**
	 * @return the dmName
	 */
	public String getDmName() {
		return dmName;
	}

	/**
	 * @param dmName
	 *            the dmName to set
	 */
	public void setDmName(String dmName) {
		this.dmName = dmName;
	}

	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param count
	 *            the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the noOfPositions
	 */
	public Integer getNoOfPositions() {
		return noOfPositions;
	}

	/**
	 * @param noOfPositions the noOfPositions to set
	 */
	public void setNoOfPositions(Integer noOfPositions) {
		this.noOfPositions = noOfPositions;
	}

	/**
	 * @return the openJobOrders
	 */
	public Integer getOpenJobOrders() {
		return openJobOrders;
	}

	/**
	 * @param openJobOrders the openJobOrders to set
	 */
	public void setOpenJobOrders(Integer openJobOrders) {
		this.openJobOrders = openJobOrders;
	}

}

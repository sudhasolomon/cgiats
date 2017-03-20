package com.uralian.cgiats.dto;

import java.util.List;

import javax.persistence.Column;

/**
 * @author Christian Rebollar
 */
public class AddEditJobOrderDto {
	private Integer id;
	private String customer;
	private Boolean customerHidden;
	private String strCustomerHidden;
	private String city;
	private String state;
	private Integer numOfPos;
	private String jobType;
	private Integer salary;
	private Integer permFee;
	private Integer payrate;
	private Boolean acceptW2;
	private Integer hourlyRateW2;
	private Integer hourlyRateW2max;
	private Integer annualRateW2;
	private Boolean accept1099;
	private Integer hourlyRate1099;
	private Boolean acceptC2c;
	private Integer hourlyRateC2c;
	private Integer hourlyRateC2cmax;
	private String startDate;
	private String endDate;
	private String assignedTo;
	private String priority;
	private String status;
	private String minSal;
	private Integer maxSal;
	private String minExp;
	private String maxExp;
	private String education;

	private Integer noOfResumesRequired;
	private String jobExpireIn;
	
	
	// private Set<Submittal> submittals = new HashSet<Submittal>();
	// private Map<String, JobOrderField> fields = new HashMap<String,
	// JobOrderField>();
	private String category;
	private String keySkills;
	private String title;
	private String description;
	// private byte[] attachment;
	private byte[] attachmentByte;
	private String attachmentFileName;
	private String strAttachment;
	private Integer hoursToOpen;
	private Integer deleteFlag;
	private Long days;
	private String onlineFlag;
	// companyFlag = jobBelongsTo
	private String companyFlag;
	private String emName;
	private String note;
	private String location;
	private String createdBy;
	private String updatedBy;
	private String postedDate;
	private String strPostedDate;
	private String dmName;
	private List<JobOrderFieldDto> jobOrderFieldList;

	public Integer getNoOfResumesRequired() {
		return noOfResumesRequired;
	}

	public void setNoOfResumesRequired(Integer noOfResumesRequired) {
		this.noOfResumesRequired = noOfResumesRequired;
	}


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public Boolean getCustomerHidden() {
		return customerHidden;
	}

	public void setCustomerHidden(Boolean customerHidden) {
		this.customerHidden = customerHidden;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Integer getNumOfPos() {
		return numOfPos;
	}

	public void setNumOfPos(Integer numOfPos) {
		this.numOfPos = numOfPos;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public Integer getSalary() {
		return salary;
	}

	public void setSalary(Integer salary) {
		this.salary = salary;
	}

	public Integer getPermFee() {
		return permFee;
	}

	public void setPermFee(Integer permFee) {
		this.permFee = permFee;
	}

	public Integer getPayrate() {
		return payrate;
	}

	public void setPayrate(Integer payrate) {
		this.payrate = payrate;
	}

	public Boolean getAcceptW2() {
		return acceptW2;
	}

	public void setAcceptW2(Boolean acceptW2) {
		this.acceptW2 = acceptW2;
	}

	public Integer getHourlyRateW2() {
		return hourlyRateW2;
	}

	public void setHourlyRateW2(Integer hourlyRateW2) {
		this.hourlyRateW2 = hourlyRateW2;
	}

	public Integer getAnnualRateW2() {
		return annualRateW2;
	}

	public void setAnnualRateW2(Integer annualRateW2) {
		this.annualRateW2 = annualRateW2;
	}

	public Boolean getAccept1099() {
		return accept1099;
	}

	public void setAccept1099(Boolean accept1099) {
		this.accept1099 = accept1099;
	}

	public Integer getHourlyRate1099() {
		return hourlyRate1099;
	}

	public void setHourlyRate1099(Integer hourlyRate1099) {
		this.hourlyRate1099 = hourlyRate1099;
	}

	public Boolean getAcceptC2c() {
		return acceptC2c;
	}

	public void setAcceptC2c(Boolean acceptC2c) {
		this.acceptC2c = acceptC2c;
	}

	public Integer getHourlyRateC2c() {
		return hourlyRateC2c;
	}

	public void setHourlyRateC2c(Integer hourlyRateC2c) {
		this.hourlyRateC2c = hourlyRateC2c;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getKeySkills() {
		return keySkills;
	}

	public void setKeySkills(String keySkills) {
		this.keySkills = keySkills;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAttachmentFileName() {
		return attachmentFileName;
	}

	public void setAttachmentFileName(String attachmentFileName) {
		this.attachmentFileName = attachmentFileName;
	}

	public Integer getHoursToOpen() {
		return hoursToOpen;
	}

	public void setHoursToOpen(Integer hoursToOpen) {
		this.hoursToOpen = hoursToOpen;
	}

	public Integer getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public Long getDays() {
		return days;
	}

	public void setDays(Long days) {
		this.days = days;
	}

	public String getOnlineFlag() {
		return onlineFlag;
	}

	public void setOnlineFlag(String onlineFlag) {
		this.onlineFlag = onlineFlag;
	}

	public String getCompanyFlag() {
		return companyFlag;
	}

	public void setCompanyFlag(String companyFlag) {
		this.companyFlag = companyFlag;
	}

	public String getEmName() {
		return emName;
	}

	public void setEmName(String emName) {
		this.emName = emName;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the jobOrderFieldList
	 */
	public List<JobOrderFieldDto> getJobOrderFieldList() {
		return jobOrderFieldList;
	}

	/**
	 * @param jobOrderFieldList
	 *            the jobOrderFieldList to set
	 */
	public void setJobOrderFieldList(List<JobOrderFieldDto> jobOrderFieldList) {
		this.jobOrderFieldList = jobOrderFieldList;
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
	 * @return the strAttachment
	 */
	public String getStrAttachment() {
		return strAttachment;
	}

	/**
	 * @param strAttachment
	 *            the strAttachment to set
	 */
	public void setStrAttachment(String strAttachment) {
		this.strAttachment = strAttachment;
	}

	/**
	 * @return the strCustomerHidden
	 */
	public String getStrCustomerHidden() {
		return strCustomerHidden;
	}

	/**
	 * @param strCustomerHidden
	 *            the strCustomerHidden to set
	 */
	public void setStrCustomerHidden(String strCustomerHidden) {
		this.strCustomerHidden = strCustomerHidden;
	}

	/**
	 * @return the strPostedDate
	 */
	public String getStrPostedDate() {
		return strPostedDate;
	}

	/**
	 * @param strPostedDate
	 *            the strPostedDate to set
	 */
	public void setStrPostedDate(String strPostedDate) {
		this.strPostedDate = strPostedDate;
	}

	/**
	 * @return the attachmentByte
	 */
	public byte[] getAttachmentByte() {
		return attachmentByte;
	}

	/**
	 * @param attachmentByte the attachmentByte to set
	 */
	public void setAttachmentByte(byte[] attachmentByte) {
		this.attachmentByte = attachmentByte;
	}

	/**
	 * @return the hourlyRateW2max
	 */
	public Integer getHourlyRateW2max() {
		return hourlyRateW2max;
	}

	/**
	 * @param hourlyRateW2max the hourlyRateW2max to set
	 */
	public void setHourlyRateW2max(Integer hourlyRateW2max) {
		this.hourlyRateW2max = hourlyRateW2max;
	}

	/**
	 * @return the hourlyRateC2cmax
	 */
	public Integer getHourlyRateC2cmax() {
		return hourlyRateC2cmax;
	}

	/**
	 * @param hourlyRateC2cmax the hourlyRateC2cmax to set
	 */
	public void setHourlyRateC2cmax(Integer hourlyRateC2cmax) {
		this.hourlyRateC2cmax = hourlyRateC2cmax;
	}

	/**
	 * @return the dmName
	 */
	public String getDmName() {
		return dmName;
	}

	/**
	 * @param dmName the dmName to set
	 */
	public void setDmName(String dmName) {
		this.dmName = dmName;
	}

	public String getMinSal() {
		return minSal;
	}

	public void setMinSal(String minSal) {
		this.minSal = minSal;
	}

	public void setMaxSal(Integer maxSal) {
		this.maxSal = maxSal;
	}
	
	public Integer getMaxSal() {
		return maxSal;
	}

	public String getMinExp() {
		return minExp;
	}

	public void setMinExp(String minExp) {
		this.minExp = minExp;
	}

	public String getMaxExp() {
		return maxExp;
	}

	public void setMaxExp(String maxExp) {
		this.maxExp = maxExp;
	}

	public void setEducation(String education) {
		this.education = education;
	}
	
	public String getEducation() {
		return education;
	}

	/**
	 * @return the postedDate
	 */
	public String getPostedDate() {
		return postedDate;
	}

	/**
	 * @param postedDate the postedDate to set
	 */
	public void setPostedDate(String postedDate) {
		this.postedDate = postedDate;
	}

	/**
	 * @return the jobExpireIn
	 */
	public String getJobExpireIn() {
		return jobExpireIn;
	}

	/**
	 * @param jobExpireIn the jobExpireIn to set
	 */
	public void setJobExpireIn(String jobExpireIn) {
		this.jobExpireIn = jobExpireIn;
	}
	

}
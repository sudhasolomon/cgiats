/**
 * 
 */
package com.uralian.cgiats.dto;

import java.util.List;

import com.uralian.cgiats.model.CandidateStatuses;

/**
 * @author Sreenath
 *
 */
/**
 * candidateVo Class which contains candidate information
 *
 */
public class CandidateVo {

	private String candidatdeId;
	private String firstname;
	private String lastname;
	private String status;
	private String title;
	private String email;
	private String keySkills;
	private String statusReason;
	private String skills;
	private String jobType;
	private String phoneWork;
	private String phoneCell;
	private String qualification;
	private String totalExperience;
	private String relevantExperience;
	private String lastCompany;
	private String lastPosition;
	private String employmentStatus;
	private String minSalaryRequirement;
	private String presentRate;
	private String expectedRate;
	private String city;
	private String state;
	private String zip;
	private String address;
	private String visaType;
	private String visaExpiryDate;
	private String resumeContent;
	private String address1;
	private String address2;

	// Resume Details
	private boolean block;
	private boolean hot;
	private String addToHotList;
	private String addToBlockList;
	private String uploadedBy;
	private String portalEmail;
	private String atsUserId;
	private String originalResumeUpdatedMsg;
	private String rtrDocumentUpdatedMsg;
	private String cgiDocumentUpdatedMsg;
	private String updatedBy;

	private byte[] originalResume;
	private byte[] cgiResume;
	private byte[] rtrDocument;

	private String rtrContent;
	private String cgiContent;
	// Reference

	private String referenceName;
	private String referenceEmail;
	private String referencePhone;
	private String referenceCompanyName;
	private String referenceDesignation;

	//
	private List<String> userIds;
	private List<String> portalEmails;
	private List<String> uploaded;
	private List<CandidateStatusesDto> statusHistory;
	private boolean securityClearance;
	
	private String pageName;
	
	// private List<>

	// Delete Candidate Details
	private String reason;
	private String candidateId;
	private String otherResumeSource;
	
	
	
	

	public String getOtherResumeSource() {
		return otherResumeSource;
	}

	public void setOtherResumeSource(String otherResumeSource) {
		this.otherResumeSource = otherResumeSource;
	}

	public boolean isSecurityClearance() {
		return securityClearance;
	}

	public void setSecurityClearance(boolean securityClearance) {
		this.securityClearance = securityClearance;
	}

	public List<CandidateStatusesDto> getStatusHistory() {
		return statusHistory;
	}

	public void setStatusHistory(List<CandidateStatusesDto> statusHistory) {
		this.statusHistory = statusHistory;
	}

	public boolean isBlock() {
		return block;
	}

	public void setBlock(boolean block) {
		this.block = block;
	}

	public boolean isHot() {
		return hot;
	}

	public void setHot(boolean hot) {
		this.hot = hot;
	}

	public List<String> getUploaded() {
		return uploaded;
	}

	public void setUploaded(List<String> uploaded) {
		this.uploaded = uploaded;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

	public List<String> getPortalEmails() {
		return portalEmails;
	}

	public void setPortalEmails(List<String> portalEmails) {
		this.portalEmails = portalEmails;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getResumeContent() {
		return resumeContent;
	}

	public void setResumeContent(String resumeContent) {
		this.resumeContent = resumeContent;
	}

	public String getLastname() {
		return lastname;
	}

	public String getRtrContent() {
		return rtrContent;
	}

	public void setRtrContent(String rtrContent) {
		this.rtrContent = rtrContent;
	}

	public String getCgiContent() {
		return cgiContent;
	}

	public void setCgiContent(String cgiContent) {
		this.cgiContent = cgiContent;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getKeySkills() {
		return keySkills;
	}

	public void setKeySkills(String keySkills) {
		this.keySkills = keySkills;
	}

	public String getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getPhoneWork() {
		return phoneWork;
	}

	public void setPhoneWork(String phoneWork) {
		this.phoneWork = phoneWork;
	}

	public String getPhoneCell() {
		return phoneCell;
	}

	public void setPhoneCell(String phoneCell) {
		this.phoneCell = phoneCell;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getTotalExperience() {
		return totalExperience;
	}

	public void setTotalExperience(String totalExperience) {
		this.totalExperience = totalExperience;
	}

	public String getLastCompany() {
		return lastCompany;
	}

	public void setLastCompany(String lastCompany) {
		this.lastCompany = lastCompany;
	}

	public String getLastPosition() {
		return lastPosition;
	}

	public void setLastPosition(String lastPosition) {
		this.lastPosition = lastPosition;
	}

	public String getEmploymentStatus() {
		return employmentStatus;
	}

	public void setEmploymentStatus(String employmentStatus) {
		this.employmentStatus = employmentStatus;
	}

	public String getMinSalaryRequirement() {
		return minSalaryRequirement;
	}

	public void setMinSalaryRequirement(String minSalaryRequirement) {
		this.minSalaryRequirement = minSalaryRequirement;
	}

	public String getPresentRate() {
		return presentRate;
	}

	public void setPresentRate(String presentRate) {
		this.presentRate = presentRate;
	}

	public String getExpectedRate() {
		return expectedRate;
	}

	public void setExpectedRate(String expectedRate) {
		this.expectedRate = expectedRate;
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

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getVisaType() {
		return visaType;
	}

	public void setVisaType(String visaType) {
		this.visaType = visaType;
	}

	public String getVisaExpiryDate() {
		return visaExpiryDate;
	}

	public void setVisaExpiryDate(String visaExpiryDate) {
		this.visaExpiryDate = visaExpiryDate;
	}

	public String getAddToHotList() {
		return addToHotList;
	}

	public void setAddToHotList(String addToHotList) {
		this.addToHotList = addToHotList;
	}

	public String getAddToBlockList() {
		return addToBlockList;
	}

	public void setAddToBlockList(String addToBlockList) {
		this.addToBlockList = addToBlockList;
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public String getPortalEmail() {
		return portalEmail;
	}

	public void setPortalEmail(String portalEmail) {
		this.portalEmail = portalEmail;
	}

	public String getAtsUserId() {
		return atsUserId;
	}

	public void setAtsUserId(String atsUserId) {
		this.atsUserId = atsUserId;
	}

	public byte[] getOriginalResume() {
		return originalResume;
	}

	public void setOriginalResume(byte[] originalResume) {
		this.originalResume = originalResume;
	}

	public byte[] getCgiResume() {
		return cgiResume;
	}

	public void setCgiResume(byte[] cgiResume) {
		this.cgiResume = cgiResume;
	}

	public byte[] getRtrDocument() {
		return rtrDocument;
	}

	public void setRtrDocument(byte[] rtrDocument) {
		this.rtrDocument = rtrDocument;
	}

	public String getReferenceName() {
		return referenceName;
	}

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public String getReferenceEmail() {
		return referenceEmail;
	}

	public void setReferenceEmail(String referenceEmail) {
		this.referenceEmail = referenceEmail;
	}

	public String getReferencePhone() {
		return referencePhone;
	}

	public void setReferencePhone(String referencePhone) {
		this.referencePhone = referencePhone;
	}

	public String getReferenceCompanyName() {
		return referenceCompanyName;
	}

	public void setReferenceCompanyName(String referenceCompanyName) {
		this.referenceCompanyName = referenceCompanyName;
	}

	public String getReferenceDesignation() {
		return referenceDesignation;
	}

	public void setReferenceDesignation(String referenceDesignation) {
		this.referenceDesignation = referenceDesignation;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}

	public String getCandidatdeId() {
		return candidatdeId;
	}

	public void setCandidatdeId(String candidatdeId) {
		this.candidatdeId = candidatdeId;
	}

	public String getOriginalResumeUpdatedMsg() {
		return originalResumeUpdatedMsg;
	}

	public void setOriginalResumeUpdatedMsg(String originalResumeUpdatedMsg) {
		this.originalResumeUpdatedMsg = originalResumeUpdatedMsg;
	}

	public String getRtrDocumentUpdatedMsg() {
		return rtrDocumentUpdatedMsg;
	}

	public void setRtrDocumentUpdatedMsg(String rtrDocumentUpdatedMsg) {
		this.rtrDocumentUpdatedMsg = rtrDocumentUpdatedMsg;
	}

	public String getCgiDocumentUpdatedMsg() {
		return cgiDocumentUpdatedMsg;
	}

	public void setCgiDocumentUpdatedMsg(String cgiDocumentUpdatedMsg) {
		this.cgiDocumentUpdatedMsg = cgiDocumentUpdatedMsg;
	}

	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return the pageName
	 */
	public String getPageName() {
		return pageName;
	}

	/**
	 * @param pageName the pageName to set
	 */
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public String getRelevantExperience() {
		return relevantExperience;
	}

	public void setRelevantExperience(String relevantExperience) {
		this.relevantExperience = relevantExperience;
	}

}

/**
 * 
 */
package com.uralian.cgiats.dto;

import java.util.List;

/**
 * @author Sreenath
 *
 */
public class CandidateDto {

	private String id;
	private String firstName;
	private String lastName;
	private String fullName;
	private String createdUser;
	private String expectedRate;
	private String presentRate;
	private String jobOrderId;
	private String title;
	private String location;
	private String email;
	private String createdOn;
	private String keySkill;
	private String updatedOn;
	private String status;
	private String visaType;
	private String phoneNumber;
	private String resumeContent;
	private boolean hot;
	private Boolean isAuthFlag = false;
	private boolean block;
	private byte[] document;
	private String portalInfo;
	private String jobTitle;
	private String dmName;
	private String onlineCandidateId;
	private String mobileCandidateId;
	private String skills;
	private String uploadedBy;
	private String altPhoneNumber;
	private String hotList;
	private String blockList;
	private String referenceName1;
	private String referenceEmail1;
	private String referencePhone1;
	private String referenceCompanyName1;
	private String referenceDesignation1;
	private String referenceName2;
	private String referenceEmail2;
	private String referencePhone2;
	private String referenceCompanyName2;
	private String referenceDesignation2;
	private String totalRecords;
	private String queryId;
	private String reason;

	/* Added */
	private String street1;
	private String street2;
	private String street3;
	private String street4;
	private String city;
	private String state;
	private String zipcode;
	private String country;
	private List<CandidateStatusesDto> statusHistory;
	private boolean securityClearance;
	private String strSecurityClearance;
	private String visaExpiryDate;
	private String documentType;
	
	
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setVisaExpiryDate(String visaExpiryDate) {
		this.visaExpiryDate = visaExpiryDate;
	}
	public String getVisaExpiryDate() {
		return visaExpiryDate;
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

	public String getStreet1() {
		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	public String getStreet3() {
		return street3;
	}

	public void setStreet3(String street3) {
		this.street3 = street3;
	}

	public String getStreet4() {
		return street4;
	}

	public void setStreet4(String street4) {
		this.street4 = street4;
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

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public String getHotList() {
		return hotList;
	}

	public void setHotList(String hotList) {
		this.hotList = hotList;
	}

	public String getBlockList() {
		return blockList;
	}

	public void setBlockList(String blockList) {
		this.blockList = blockList;
	}

	public String getReferenceName1() {
		return referenceName1;
	}

	public void setReferenceName1(String referenceName1) {
		this.referenceName1 = referenceName1;
	}

	public String getReferenceEmail1() {
		return referenceEmail1;
	}

	public void setReferenceEmail1(String referenceEmail1) {
		this.referenceEmail1 = referenceEmail1;
	}

	public String getReferencePhone1() {
		return referencePhone1;
	}

	public void setReferencePhone1(String referencePhone1) {
		this.referencePhone1 = referencePhone1;
	}

	public String getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(String totalRecords) {
		this.totalRecords = totalRecords;
	}

	public String getReferenceCompanyName1() {
		return referenceCompanyName1;
	}

	public void setReferenceCompanyName1(String referenceCompanyName1) {
		this.referenceCompanyName1 = referenceCompanyName1;
	}

	public String getReferenceDesignation1() {
		return referenceDesignation1;
	}

	public void setReferenceDesignation1(String referenceDesignation1) {
		this.referenceDesignation1 = referenceDesignation1;
	}

	public String getReferenceName2() {
		return referenceName2;
	}

	public void setReferenceName2(String referenceName2) {
		this.referenceName2 = referenceName2;
	}

	public String getReferenceEmail2() {
		return referenceEmail2;
	}

	public void setReferenceEmail2(String referenceEmail2) {
		this.referenceEmail2 = referenceEmail2;
	}

	public String getReferencePhone2() {
		return referencePhone2;
	}

	public void setReferencePhone2(String referencePhone2) {
		this.referencePhone2 = referencePhone2;
	}

	public String getReferenceCompanyName2() {
		return referenceCompanyName2;
	}

	public void setReferenceCompanyName2(String referenceCompanyName2) {
		this.referenceCompanyName2 = referenceCompanyName2;
	}

	public String getReferenceDesignation2() {
		return referenceDesignation2;
	}

	public void setReferenceDesignation2(String referenceDesignation2) {
		this.referenceDesignation2 = referenceDesignation2;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKeySkill() {
		return keySkill;
	}

	public void setKeySkill(String keySkill) {
		this.keySkill = keySkill;
	}

	public String getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(String updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getAltPhoneNumber() {
		return altPhoneNumber;
	}

	public void setAltPhoneNumber(String altPhoneNumber) {
		this.altPhoneNumber = altPhoneNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVisaType() {
		return visaType;
	}

	public void setVisaType(String visaType) {
		this.visaType = visaType;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getResumeContent() {
		return resumeContent;
	}

	public void setResumeContent(String resumeContent) {
		this.resumeContent = resumeContent;
	}

	public boolean isHot() {
		return hot;
	}

	public void setHot(boolean hot) {
		this.hot = hot;
	}

	public boolean isBlock() {
		return block;
	}

	public void setBlock(boolean block) {
		this.block = block;
	}

	public byte[] getDocument() {
		return document;
	}

	public void setDocument(byte[] document) {
		this.document = document;
	}

	public String getJobOrderId() {
		return jobOrderId;
	}

	public void setJobOrderId(String jobOrderId) {
		this.jobOrderId = jobOrderId;
	}

	public String getPortalInfo() {
		return portalInfo;
	}

	public void setPortalInfo(String portalInfo) {
		this.portalInfo = portalInfo;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getDmName() {
		return dmName;
	}

	public void setDmName(String dmName) {
		this.dmName = dmName;
	}

	public String getOnlineCandidateId() {
		return onlineCandidateId;
	}

	public void setOnlineCandidateId(String onlineCandidateId) {
		this.onlineCandidateId = onlineCandidateId;
	}

	public String getMobileCandidateId() {
		return mobileCandidateId;
	}

	public void setMobileCandidateId(String mobileCandidateId) {
		this.mobileCandidateId = mobileCandidateId;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public String getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(String uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the expectedRate
	 */
	public String getExpectedRate() {
		return expectedRate;
	}

	/**
	 * @param expectedRate
	 *            the expectedRate to set
	 */
	public void setExpectedRate(String expectedRate) {
		this.expectedRate = expectedRate;
	}

	/**
	 * @return the presentRate
	 */
	public String getPresentRate() {
		return presentRate;
	}

	/**
	 * @param presentRate
	 *            the presentRate to set
	 */
	public void setPresentRate(String presentRate) {
		this.presentRate = presentRate;
	}

	/**
	 * @return the createdUser
	 */
	public String getCreatedUser() {
		return createdUser;
	}

	/**
	 * @param createdUser
	 *            the createdUser to set
	 */
	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	/**
	 * @return the isAuthFlag
	 */
	public Boolean getIsAuthFlag() {
		return isAuthFlag;
	}

	/**
	 * @param isAuthFlag
	 *            the isAuthFlag to set
	 */
	public void setIsAuthFlag(Boolean isAuthFlag) {
		this.isAuthFlag = isAuthFlag;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the strSecurityClearance
	 */
	public String getStrSecurityClearance() {
		return strSecurityClearance;
	}

	/**
	 * @param strSecurityClearance the strSecurityClearance to set
	 */
	public void setStrSecurityClearance(String strSecurityClearance) {
		this.strSecurityClearance = strSecurityClearance;
	}


}

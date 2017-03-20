package com.uralian.cgiats.dto;

import java.util.List;

import com.uralian.cgiats.model.CandidateStatus;

public class IndiaCandidateDto {
	
	private String id;
	private CandidateStatus status;
	private String firstName;
	private String lastName;
	private String fullName;
	private String phone;
	private String phoneAlt;
	private String email;
	private String category;
	private String title;
	private String indiaResumeContent;
	private byte[] documentType;
	private byte[] proccessDocumentType;
	private byte[] rtrDocumentType;
	private boolean hot;
	private boolean block;
	private String visaType;
	private String createdOn;
	private String updatedOn;
	private String createdUser;
	private String updatedBy;
	private String portalResumeId;
	private String portalResumeLastUpd;
	private String portalResumeExperience;
	private String portalResumeLastComp;
	private String portalResumeLastPosition;
	private String portalResumeQual;
	private String totalRecords;
	private String location;
	private List<CandidateStatusesDto> statusHistory;
	private String keySkill;
	
	private List<String> userIds;
	private List<String> portalEmails;
	private List<String> uploaded;
	
	
	
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
	public List<String> getUploaded() {
		return uploaded;
	}
	public void setUploaded(List<String> uploaded) {
		this.uploaded = uploaded;
	}
	public String getIndiaResumeContent() {
		return indiaResumeContent;
	}
	public void setIndiaResumeContent(String indiaResumeContent) {
		this.indiaResumeContent = indiaResumeContent;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(String totalRecords) {
		this.totalRecords = totalRecords;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public CandidateStatus getStatus() {
		return status;
	}
	public void setStatus(CandidateStatus status) {
		this.status = status;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPhoneAlt() {
		return phoneAlt;
	}
	public void setPhoneAlt(String phoneAlt) {
		this.phoneAlt = phoneAlt;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public byte[] getDocumentType() {
		return documentType;
	}
	public void setDocumentType(byte[] documentType) {
		this.documentType = documentType;
	}
	public byte[] getProccessDocumentType() {
		return proccessDocumentType;
	}
	public void setProccessDocumentType(byte[] proccessDocumentType) {
		this.proccessDocumentType = proccessDocumentType;
	}
	public byte[] getRtrDocumentType() {
		return rtrDocumentType;
	}
	public void setRtrDocumentType(byte[] rtrDocumentType) {
		this.rtrDocumentType = rtrDocumentType;
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
	public String getVisaType() {
		return visaType;
	}
	public void setVisaType(String visaType) {
		this.visaType = visaType;
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
	public String getCreatedUser() {
		return createdUser;
	}
	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public String getPortalResumeId() {
		return portalResumeId;
	}
	public void setPortalResumeId(String portalResumeId) {
		this.portalResumeId = portalResumeId;
	}
	public String getPortalResumeLastUpd() {
		return portalResumeLastUpd;
	}
	public void setPortalResumeLastUpd(String portalResumeLastUpd) {
		this.portalResumeLastUpd = portalResumeLastUpd;
	}
	public String getPortalResumeExperience() {
		return portalResumeExperience;
	}
	public void setPortalResumeExperience(String portalResumeExperience) {
		this.portalResumeExperience = portalResumeExperience;
	}
	public String getPortalResumeLastComp() {
		return portalResumeLastComp;
	}
	public void setPortalResumeLastComp(String portalResumeLastComp) {
		this.portalResumeLastComp = portalResumeLastComp;
	}
	public String getPortalResumeLastPosition() {
		return portalResumeLastPosition;
	}
	public void setPortalResumeLastPosition(String portalResumeLastPosition) {
		this.portalResumeLastPosition = portalResumeLastPosition;
	}
	public String getPortalResumeQual() {
		return portalResumeQual;
	}
	public void setPortalResumeQual(String portalResumeQual) {
		this.portalResumeQual = portalResumeQual;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public List<CandidateStatusesDto> getStatusHistory() {
		return statusHistory;
	}
	public void setStatusHistory(List<CandidateStatusesDto> statusHistory) {
		this.statusHistory = statusHistory;
	}
	public String getKeySkill() {
		return keySkill;
	}
	public void setKeySkill(String keySkill) {
		this.keySkill = keySkill;
	}

	
	
	
	
	
	
	

}

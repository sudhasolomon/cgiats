package com.uralian.cgiats.dto;

import java.util.Date;
import java.util.HashMap;

import com.uralian.cgiats.model.SubmittalStatus;

public class IndiaSubmittalStatsDto {

	private Integer SUBMITTED;
	private Integer DMREJ;
	private Integer ACCEPTED;
	private Integer INTERVIEWING;
	private Integer CONFIRMED;
	private Integer REJECTED;
	private Integer STARTED;
	private Integer BACKOUT;
	private Integer OUTOFPROJ;
	private Integer NotUpdated;
	private Integer Total;
	private String Name;
	private String DM;
	private String Location;
	private String fullName;
	private Integer openCount;
	private Integer closedCount;
	private String clientName;
	private String jobTitle;
	private String candidateFullName;
	private String recruiterName;
	private String createdOn;
	private String status;
	private HashMap<SubmittalStatus, Integer> submittalTotalsByStatus;
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
	public HashMap<SubmittalStatus, Integer> getSubmittalTotalsByStatus() {
		return submittalTotalsByStatus;
	}
	public void setSubmittalTotalsByStatus(HashMap<SubmittalStatus, Integer> submittalTotalsByStatus) {
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
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public Integer getOpenCount() {
		return openCount;
	}
	public void setOpenCount(Integer openCount) {
		this.openCount = openCount;
	}
	public Integer getClosedCount() {
		return closedCount;
	}
	public void setClosedCount(Integer closedCount) {
		this.closedCount = closedCount;
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
	public String getRecruiterName() {
		return recruiterName;
	}
	public void setRecruiterName(String recruiterName) {
		this.recruiterName = recruiterName;
	}
	public String getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}

package com.uralian.cgiats.dto;

import java.util.List;
import java.util.Map;

public class SearchResumeDto {

	private String keyskills;
	private String title;
	private String resumeupdated;
	private String createdbetween1;
	private String createdbetween2;
	private List<String> visatype;
	private String state;
	private String firstname;
	private String lastname;
	private String email;
	private String city;
	private String resumesearch;

	private String candidateid;
	private String education;
	private String experience;
	private String compensation;
	private String phone;
	private Map<String, String> lastUpdated;
	private Map<String, String> created;

	public Map<String, String> getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Map<String, String> lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Map<String, String> getCreated() {
		return created;
	}

	public void setCreated(Map<String, String> created) {
		this.created = created;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCandidateid() {
		return candidateid;
	}

	public void setCandidateid(String candidateid) {
		this.candidateid = candidateid;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public String getCompensation() {
		return compensation;
	}

	public void setCompensation(String compensation) {
		this.compensation = compensation;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<String> getVisatype() {
		return visatype;
	}

	public void setVisatype(List<String> visatype) {
		this.visatype = visatype;
	}

	public String getKeyskills() {
		return keyskills;
	}

	public void setKeyskills(String keyskills) {
		this.keyskills = keyskills;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getResumeupdated() {
		return resumeupdated;
	}

	public void setResumeupdated(String resumeupdated) {
		this.resumeupdated = resumeupdated;
	}

	public String getCreatedbetween1() {
		return createdbetween1;
	}

	public void setCreatedbetween1(String createdbetween1) {
		this.createdbetween1 = createdbetween1;
	}

	public String getCreatedbetween2() {
		return createdbetween2;
	}

	public void setCreatedbetween2(String createdbetween2) {
		this.createdbetween2 = createdbetween2;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getResumesearch() {
		return resumesearch;
	}

	public void setResumesearch(String resumesearch) {
		this.resumesearch = resumesearch;
	}

}

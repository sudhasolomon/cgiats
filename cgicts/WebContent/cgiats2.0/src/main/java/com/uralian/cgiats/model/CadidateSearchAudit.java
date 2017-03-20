/**
 * 
 */
package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author Sreenath
 * 
 */
@Entity
@Table(name = "search_candidate_audit")
@SequenceGenerator(sequenceName = "search_candidate_audit_seq", name = "searchSeq", initialValue = 0)
public class CadidateSearchAudit extends AuditableEntity<Serializable> implements Serializable {

	/**
	 * longCadidateSearchAudit.java
	 * 
	 */
	private static final long serialVersionUID = -7863436311620602235L;
	@Id
	@Column(name = "candidate_search_id")
	@GeneratedValue(generator = "searchSeq", strategy = GenerationType.SEQUENCE)
	private int candidateSearchId;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "email")
	private String email;
	@Column(name = "visa_type")
	private String visaType;
	@Column(name = "title")
	private String title;
	@Column(name = "city")
	private String city;
	@Column(name = "state")
	private String state;
	@Column(name = "key_skills")
	private String keySkills;
	@Column(name = "resume_update")
	private String resumeUpdate;
	@Lob
	@Column(name = "resume_search")
	private byte[] resumeSearch;
	@Column(name = "search_flg")
	private String searchFlag;
	@Transient
	private String resumeText;
	@Column(name = "from_date")
	private Date fromDate;
	@Column(name = "to_date")
	private Date toDate;
	@Column(name = "query_Name")
	private String queryName;

	@Column(name = "phone")
	private String phone;

	@Column(name = "education")
	private String education;

	/**
	 * @return the candidateSearchId
	 */
	public int getCandidateSearchId() {
		return candidateSearchId;
	}

	/**
	 * @param candidateSearchId
	 *            the candidateSearchId to set
	 */
	public void setCandidateSearchId(int candidateSearchId) {
		this.candidateSearchId = candidateSearchId;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
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
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the visaType
	 */
	public String getVisaType() {
		return visaType;
	}

	/**
	 * @param visaType
	 *            the visaType to set
	 */
	public void setVisaType(String visaType) {
		this.visaType = visaType;
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
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the keySkills
	 */
	public String getKeySkills() {
		return keySkills;
	}

	/**
	 * @param keySkills
	 *            the keySkills to set
	 */
	public void setKeySkills(String keySkills) {
		this.keySkills = keySkills;
	}

	/**
	 * @return the resumeUpdate
	 */
	public String getResumeUpdate() {
		return resumeUpdate;
	}

	/**
	 * @param resumeUpdate
	 *            the resumeUpdate to set
	 */
	public void setResumeUpdate(String resumeUpdate) {
		this.resumeUpdate = resumeUpdate;
	}

	/**
	 * @return the resumeSearch
	 */
	public byte[] getResumeSearch() {

		return resumeSearch;
	}

	/**
	 * @param resumeSearch
	 *            the resumeSearch to set
	 */
	public void setResumeSearch(byte[] resumeSearch) {
		this.resumeSearch = resumeSearch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.model.AbstractEntity#getId()
	 */
	@Override
	public Serializable getId() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.model.AbstractEntity#getBusinessKey()
	 */
	@Override
	protected Object getBusinessKey() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the searchFlag
	 */
	public String getSearchFlag() {
		return searchFlag;
	}

	/**
	 * @param searchFlag
	 *            the searchFlag to set
	 */
	public void setSearchFlag(String searchFlag) {
		this.searchFlag = searchFlag;
	}

	/**
	 * @return the resumeText
	 */
	public String getResumeText() {
		String resume = new String(getResumeSearch());

		this.setResumeText(resume);
		return resumeText;
	}

	/**
	 * @param resumeText
	 *            the resumeText to set
	 */
	public void setResumeText(String resumeText) {

		this.resumeText = resumeText;
	}

	/**
	 * @return the fromDate
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * @param fromDate
	 *            the fromDate to set
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return the toDate
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * @param toDate
	 *            the toDate to set
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

}

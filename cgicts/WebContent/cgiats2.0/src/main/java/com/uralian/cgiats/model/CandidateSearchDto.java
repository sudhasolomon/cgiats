package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.util.Utils;

/**
 * This class encapsulates search criteria for candidates/resumes.
 * 
 * @author Vlad Orzhekhovskiy
 */
/**
 * @author Sreenath
 *
 */

public class CandidateSearchDto implements Serializable {
	private static final long serialVersionUID = -5001348085915854175L;

	// private String loginId;
	private String name;
	private String firstName;
	private String lastName;
	private String title;
	private List<String> states;
	private String city;
	private String email;
	private List<String> visaStats;
	private String resumeStats;
	private Date startEntryDate;
	private Date endEntryDate;
	private String startDate;
	private String endDate;
	private String resumeTextQuery;
	private String createdBy;
	private Map<String, String> properties;
	private String phoneNumber;
	private String candidateId;
	private String compensation;
	private String workExperince;
	private List<String> education;
	private Map<String, String> lastUpdated;
	private Map<String, String> created;
	private String status;
	private Boolean isCallFromPagination = false;
	private Boolean isCancelBtnClicked = false;
	//
	private String queryName;
	private String queryId;
	private String candidateSearchId;
	private String fieldName="id";
	private String sortName;

	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}
	
	private String pageNumber;
	private String pageSize;
	// order and pagination
	private OrderByColumn orderByColumn = OrderByColumn.ID;
	private OrderByType orderByType = OrderByType.DESC;
	private int startPosition = 0;
	private int maxResults = -1;
	private String keySkill;

	
	
	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}
	public String getPageNumber() {
		return pageNumber;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public String getPageSize() {
		return pageSize;
	}
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */

	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 */

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return
	 */

	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 */

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getCandidateSearchId() {
		return candidateSearchId;
	}

	public void setCandidateSearchId(String candidateSearchId) {
		this.candidateSearchId = candidateSearchId;
	}

	/**
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the visaStats
	 */
	public String getVisa() {

		StringBuffer str = new StringBuffer();
		if (!Utils.isEmpty(visaStats)) {
			for (String visa : visaStats) {
				str.append(visa + ",");

			}

		}
		if (str != null && str.length() > 0) {
			str.deleteCharAt(str.length() - 1);
		}
		return str.toString();

	}

	public String getEducations() {
		StringBuffer str = new StringBuffer();
		if (!Utils.isEmpty(education)) {
			for (String edut : education) {
				str.append(edut + ",");

			}

		}
		if (str != null && str.length() > 0) {
			str.deleteCharAt(str.length() - 1);
		}
		return str.toString();

	}

	/**
	 * @param visaStats
	 */
	public void setVisa(String visa) {
		this.visaStats = new ArrayList<String>(Arrays.asList(visa));
	}

	/**
	 * @return
	 */
	public List<String> getVisaStats() {
		return visaStats;
	}

	/**
	 * @param visaStats
	 */
	public void setVisaStats(List<String> visaStats) {

		this.visaStats = visaStats;
	}

	public String getResumeStats() {
		return resumeStats;
	}

	public void setResumeStats(String resumeStats) {
		this.resumeStats = resumeStats;
	}

	/**
	 * @return
	 */
	public String getState() {

		StringBuffer str = new StringBuffer();
		if (!Utils.isEmpty(states)) {
			for (String state : states) {
				str.append(state + ",");

			}

		}
		if (str != null && str.length() > 0) {
			str.deleteCharAt(str.length() - 1);
		}

		return str.toString();
	}

	/**
	 * @param state
	 */
	public void setState(String state) {
		this.states = new ArrayList<String>(Arrays.asList(state));
	}

	/**
	 * @return
	 */
	public List<String> getStates() {
		return states;
	}

	/**
	 * @param state
	 */
	public void setStates(List<String> states) {
		this.states = states;
	}

	/**
	 * @return
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return
	 */
	public Date getStartEntryDate() {
		return startEntryDate;
	}

	/**
	 * @param startEntryDate
	 */
	public void setStartEntryDate(Date startEntryDate) {
		this.startEntryDate = startEntryDate;
	}

	/**
	 * @return
	 */
	public Date getEndEntryDate() {
		return endEntryDate;
	}

	/**
	 * @param endEntryDate
	 */
	public void setEndEntryDate(Date endEntryDate) {
		this.endEntryDate = endEntryDate;
	}

	/**
	 * @return the resumeTextQuery.
	 */
	public String getResumeTextQuery() {
		return resumeTextQuery;
	}

	/**
	 * @param resumeTextQuery
	 *            the resumeTextQuery to set.
	 */
	public void setResumeTextQuery(String resumeTextQuery) {
		this.resumeTextQuery = resumeTextQuery;
	}

	public OrderByColumn getOrderByColumn() {
		return orderByColumn;
	}

	public void setOrderByColumn(OrderByColumn orderByColumn) {
		this.orderByColumn = orderByColumn;
	}

	public OrderByType getOrderByType() {
		return orderByType;
	}

	public void setOrderByType(OrderByType orderByType) {
		this.orderByType = orderByType;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResulsts) {
		this.maxResults = maxResulsts;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString() loginId="+loginId+",
	 */

	public String getCreatedBy() {
		return createdBy;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}

	public String getCompensation() {
		return compensation;
	}

	public void setCompensation(String compensation) {
		this.compensation = compensation;
	}

	public String getWorkExperince() {
		return workExperince;
	}

	public void setWorkExperince(String workExperince) {
		this.workExperince = workExperince;
	}

	public List<String> getEducation() {
		return education;
	}

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

	public void setEducation(List<String> education) {
		this.education = education;
	}

	@Override
	public String toString() {
		return "CandidateSearchDto [name=" + name + ", firstName=" + firstName + ", lastName=" + lastName + ", title=" + title + ", states=" + states
				+ ", city=" + city + ", email=" + email + ", visaStats=" + visaStats + ", resumeStats=" + resumeStats + ", startEntryDate=" + startEntryDate
				+ ", endEntryDate=" + endEntryDate + ", startDate=" + startDate + ", endDate=" + endDate + ", resumeTextQuery=" + resumeTextQuery
				+ ", createdBy=" + createdBy + ", properties=" + properties + ", orderByColumn=" + orderByColumn + ", orderByType=" + orderByType
				+ ", startPosition=" + startPosition + ", maxResults=" + maxResults + "]";
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the keySkill
	 */
	public String getKeySkill() {
		return keySkill;
	}

	/**
	 * @param keySkill
	 *            the keySkill to set
	 */
	public void setKeySkill(String keySkill) {
		this.keySkill = keySkill;
	}

	public String getQueryName() {
		return queryName;
	}

	public void setQueryName(String queryName) {
		this.queryName = queryName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the isCallFromPagination
	 */
	public Boolean getIsCallFromPagination() {
		return isCallFromPagination;
	}

	/**
	 * @param isCallFromPagination
	 *            the isCallFromPagination to set
	 */
	public void setIsCallFromPagination(Boolean isCallFromPagination) {
		this.isCallFromPagination = isCallFromPagination;
	}

	/**
	 * @return the isCancelBtnClicked
	 */
	public Boolean getIsCancelBtnClicked() {
		return isCancelBtnClicked;
	}

	/**
	 * @param isCancelBtnClicked
	 *            the isCancelBtnClicked to set
	 */
	public void setIsCancelBtnClicked(Boolean isCancelBtnClicked) {
		this.isCancelBtnClicked = isCancelBtnClicked;
	}

	/*
	 * public String getLoginId() { return loginId; }
	 * 
	 * public void setLoginId(String loginId) { this.loginId = loginId; }
	 */

}
package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Rebollar
 */
public class JobOrderSearchDto implements Serializable {
	private static final long serialVersionUID = 1285186525102713802L;

	private List<JobOrderPriority> priorities;

	private List<JobOrderStatus> statuses;

	private List<JobType> jobTypes;

	private String strPriorities;
	private String strStatuses;
	private String strJobTypes;

	private String assignedTo;

	private Map<String, String> fields;

	private Date startEntryDate;

	private Date endEntryDate;

	private int startPosition = -1;

	private int maxResults = -1;

	private String bdm;

	private String awd;

	private String submittalBdms;

	private String title;

	private boolean emJobOrders;

	private boolean deleteFlag;

	private String admName;

	private boolean dmJobOrders;

	private Boolean hot;

	private List<String> jobBelongsTo;
	private String strJobBelongsTo;

	private Map<String, String> jobOrderTimeIntervalMap;

	public boolean isHot() {
		return hot != null? hot.booleanValue() :false;
	}

	public void setHot(boolean hot) {
		this.hot = hot;
	}

	private Integer jobOrderId;

	/**
	 * @return the awd
	 */
	public String getAwd() {
		return awd;
	}

	/**
	 * @param awd
	 *            the awd to set
	 */
	public void setAwd(String awd) {
		this.awd = awd;
	}

	/**
	 * @return the submittalBdms
	 */
	public String getSubmittalBdms() {
		return submittalBdms;
	}

	/**
	 * @param submittalBdms
	 *            the submittalBdms to set
	 */
	public void setSubmittalBdms(String submittalBdms) {
		this.submittalBdms = submittalBdms;
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
	 * @return the priorities.
	 */
	public List<JobOrderPriority> getPriorities() {
		return priorities;
	}

	/**
	 * @param priorities
	 *            the priorities to set.
	 */
	public void setPriorities(List<JobOrderPriority> priorities) {
		this.priorities = priorities;
	}

	/**
	 * @return the statuses.
	 */
	public List<JobOrderStatus> getStatuses() {
		return statuses;
	}

	/**
	 * @param statuses
	 *            the statuses to set.
	 */
	public void setStatuses(List<JobOrderStatus> statuses) {
		this.statuses = statuses;
	}

	/**
	 * @return the jobTypes.
	 */
	public List<JobType> getJobTypes() {
		return jobTypes;
	}

	/**
	 * @param jobTypes
	 *            the jobTypes to set.
	 */
	public void setJobTypes(List<JobType> jobTypes) {
		this.jobTypes = jobTypes;
	}

	public String getStrPriorities() {
		return strPriorities;
	}

	public void setStrPriorities(String strPriorities) {
		this.strPriorities = strPriorities;
	}

	public String getStrStatuses() {
		return strStatuses;
	}

	public void setStrStatuses(String strStatuses) {
		this.strStatuses = strStatuses;
	}

	public String getStrJobTypes() {
		return strJobTypes;
	}

	public void setStrJobTypes(String strJobTypes) {
		this.strJobTypes = strJobTypes;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	/**
	 * @return
	 */
	public Map<String, String> getFields() {
		return fields;
	}

	/**
	 * @param fields
	 */
	public void setFields(Map<String, String> fields) {
		this.fields = fields;
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
	 * @return
	 */
	public int getStartPosition() {
		return startPosition;
	}

	/**
	 * @param startPosition
	 */
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	/**
	 * @return
	 */
	public int getMaxResults() {
		return maxResults;
	}

	/**
	 * @param maxResults
	 */
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public String getBdm() {
		return bdm;
	}

	public void setBdm(String bdm) {
		this.bdm = bdm;
	}

	public boolean isEmJobOrders() {
		return emJobOrders;
	}

	public void setEmJobOrders(boolean emJobOrders) {
		this.emJobOrders = emJobOrders;
	}

	public boolean isDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}

	public boolean isDmJobOrders() {
		return dmJobOrders;
	}

	public void setDmJobOrders(boolean dmJobOrders) {
		this.dmJobOrders = dmJobOrders;
	}

	public List<String> getJobBelongsTo() {
		return jobBelongsTo;
	}

	public void setJobBelongsTo(List<String> jobBelongsTo) {
		this.jobBelongsTo = jobBelongsTo;
	}

	/**
	 * @return the jobOrderTimeIntervalMap
	 */
	public Map<String, String> getJobOrderTimeIntervalMap() {
		return jobOrderTimeIntervalMap;
	}

	/**
	 * @param jobOrderTimeIntervalMap
	 *            the jobOrderTimeIntervalMap to set
	 */
	public void setJobOrderTimeIntervalMap(Map<String, String> jobOrderTimeIntervalMap) {
		this.jobOrderTimeIntervalMap = jobOrderTimeIntervalMap;
	}

	/**
	 * @return the jobOrderId
	 */
	public Integer getJobOrderId() {
		return jobOrderId;
	}

	/**
	 * @param jobOrderId
	 *            the jobOrderId to set
	 */
	public void setJobOrderId(Integer jobOrderId) {
		this.jobOrderId = jobOrderId;
	}

	/**
	 * @return the strJobBelongsTo
	 */
	public String getStrJobBelongsTo() {
		return strJobBelongsTo;
	}

	/**
	 * @param strJobBelongsTo
	 *            the strJobBelongsTo to set
	 */
	public void setStrJobBelongsTo(String strJobBelongsTo) {
		this.strJobBelongsTo = strJobBelongsTo;
	}

	/**
	 * @return the admName
	 */
	public String getAdmName() {
		return admName;
	}

	/**
	 * @param admName
	 *            the admName to set
	 */
	public void setAdmName(String admName) {
		this.admName = admName;
	}

}
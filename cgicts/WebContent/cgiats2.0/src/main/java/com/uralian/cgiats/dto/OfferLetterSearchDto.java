package com.uralian.cgiats.dto;

import java.util.Date;
import java.util.Map;

/**
 * 
 * @author skurapati
 *
 */
public class OfferLetterSearchDto {

	private boolean deleteFlag;
	private Map<String, String> timeIntervalMap;
	private Integer jobOrderId;
	private String userId;
	private Date startEntryDate;
	private Date endEntryDate;

	public Date getStartEntryDate() {
		return startEntryDate;
	}

	public void setStartEntryDate(Date startEntryDate) {
		this.startEntryDate = startEntryDate;
	}

	public Date getEndEntryDate() {
		return endEntryDate;
	}

	public void setEndEntryDate(Date endEntryDate) {
		this.endEntryDate = endEntryDate;
	}

	public boolean getDeleteFlag() {
		return deleteFlag;
	}

	public void setDeleteFlag(boolean deleteFlag) {
		this.deleteFlag = deleteFlag;
	}


	public Integer getJobOrderId() {
		return jobOrderId;
	}

	public void setJobOrderId(Integer jobOrderId) {
		this.jobOrderId = jobOrderId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the timeIntervalMap
	 */
	public Map<String, String> getTimeIntervalMap() {
		return timeIntervalMap;
	}

	/**
	 * @param timeIntervalMap the timeIntervalMap to set
	 */
	public void setTimeIntervalMap(Map<String, String> timeIntervalMap) {
		this.timeIntervalMap = timeIntervalMap;
	}

}
package com.uralian.cgiats.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Christian Rebollar
 */
public class DashboardSearchDto implements Serializable {
	private static final long serialVersionUID = 1285186525102713802L;

	private List<String> officeLocations;
	private List<String> dmOrAdms;

	private String strOfficeLocations;
	private String strDMOrAdms;

	private Date dtStartDate;
	private Date dtEndDate;

	private String startDate;
	private String status;
	private String userStatus;
	private String endDate;
	private String userId;
	private Boolean isDm;
	private Boolean isCitySelected;
	private Boolean isAuthRequired;

	public Boolean getIsAuthRequired() {
		return isAuthRequired;
	}

	public void setIsAuthRequired(Boolean isAuthRequired) {
		this.isAuthRequired = isAuthRequired;
	}

	public String getStrOfficeLocations() {
		return strOfficeLocations;
	}

	public void setStrOfficeLocations(String strOfficeLocations) {
		this.strOfficeLocations = strOfficeLocations;
	}

	public String getStrDMOrAdms() {
		return strDMOrAdms;
	}

	public void setStrDMOrAdms(String strDMOrAdms) {
		this.strDMOrAdms = strDMOrAdms;
	}

	public Date getDtStartDate() {
		return dtStartDate;
	}

	public void setDtStartDate(Date dtStartDate) {
		this.dtStartDate = dtStartDate;
	}

	public Date getDtEndDate() {
		return dtEndDate;
	}

	public void setDtEndDate(Date dtEndDate) {
		this.dtEndDate = dtEndDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Boolean getIsDm() {
		return isDm;
	}

	public void setIsDm(Boolean isDm) {
		this.isDm = isDm;
	}

	public List<String> getOfficeLocations() {
		return officeLocations;
	}

	public void setOfficeLocations(List<String> officeLocations) {
		this.officeLocations = officeLocations;
	}

	public List<String> getDmOrAdms() {
		return dmOrAdms;
	}

	public void setDmOrAdms(List<String> dmOrAdms) {
		this.dmOrAdms = dmOrAdms;
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

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the isCitySelected
	 */
	public Boolean getIsCitySelected() {
		return isCitySelected != null ? isCitySelected : false;
	}

	/**
	 * @param isCitySelected
	 *            the isCitySelected to set
	 */
	public void setIsCitySelected(Boolean isCitySelected) {
		this.isCitySelected = isCitySelected;
	}

	/**
	 * @return the userStatus
	 */
	public String getUserStatus() {
		return userStatus;
	}

	/**
	 * @param userStatus the userStatus to set
	 */
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

}
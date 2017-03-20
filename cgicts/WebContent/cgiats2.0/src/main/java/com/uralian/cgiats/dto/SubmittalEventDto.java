/*
 * SubmittalEvent.java Jun 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.dto;

public class SubmittalEventDto {
	private Integer id;
	private String status;
	private String notes;
	private String createdBy;
	private String updatedBy;
	private String strCreatedOn;
	private String oldStrCreatedOn;
	private boolean isEditMode;
	private boolean jsEditMode;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return the strCreatedOn
	 */
	public String getStrCreatedOn() {
		return strCreatedOn;
	}

	/**
	 * @param strCreatedOn
	 *            the strCreatedOn to set
	 */
	public void setStrCreatedOn(String strCreatedOn) {
		this.strCreatedOn = strCreatedOn;
	}

	/**
	 * @return the isEditMode
	 */
	public boolean isEditMode() {
		return isEditMode;
	}

	/**
	 * @param isEditMode the isEditMode to set
	 */
	public void setEditMode(boolean isEditMode) {
		this.isEditMode = isEditMode;
	}

	/**
	 * @return the jsEditMode
	 */
	public boolean isJsEditMode() {
		return jsEditMode;
	}

	/**
	 * @param jsEditMode the jsEditMode to set
	 */
	public void setJsEditMode(boolean jsEditMode) {
		this.jsEditMode = jsEditMode;
	}

	/**
	 * @return the oldStrCreatedOn
	 */
	public String getOldStrCreatedOn() {
		return oldStrCreatedOn;
	}

	/**
	 * @param oldStrCreatedOn the oldStrCreatedOn to set
	 */
	public void setOldStrCreatedOn(String oldStrCreatedOn) {
		this.oldStrCreatedOn = oldStrCreatedOn;
	}

}
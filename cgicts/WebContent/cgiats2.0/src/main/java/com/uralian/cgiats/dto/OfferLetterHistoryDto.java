package com.uralian.cgiats.dto;

public class OfferLetterHistoryDto {
	private Integer offerLetterHistoryId;
	private OfferLetterDto offerletter;
	private Integer offerLetterId;
	private String status;
	private String notes;
	private String createdBy;
	private String strStatusCreatedOn;
	private String strCreatedDate;
	private String updatedBy;
	private String strUpdatedOn;

	public Integer getOfferLetterHistoryId() {
		return offerLetterHistoryId;
	}

	public void setOfferLetterHistoryId(Integer offerLetterHistoryId) {
		this.offerLetterHistoryId = offerLetterHistoryId;
	}

	public OfferLetterDto getOfferletter() {
		return offerletter;
	}

	public void setOfferletter(OfferLetterDto offerletter) {
		this.offerletter = offerletter;
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

	public String getStrStatusCreatedOn() {
		return strStatusCreatedOn;
	}

	public void setStrStatusCreatedOn(String strStatusCreatedOn) {
		this.strStatusCreatedOn = strStatusCreatedOn;
	}

	public String getStrCreatedDate() {
		return strCreatedDate;
	}

	public void setStrCreatedDate(String strCreatedDate) {
		this.strCreatedDate = strCreatedDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getStrUpdatedOn() {
		return strUpdatedOn;
	}

	public void setStrUpdatedOn(String strUpdatedOn) {
		this.strUpdatedOn = strUpdatedOn;
	}

	/**
	 * @return the offerLetterId
	 */
	public Integer getOfferLetterId() {
		return offerLetterId;
	}

	/**
	 * @param offerLetterId
	 *            the offerLetterId to set
	 */
	public void setOfferLetterId(Integer offerLetterId) {
		this.offerLetterId = offerLetterId;
	}

}

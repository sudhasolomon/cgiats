package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "offer_letter_history")
public class OfferLetterHistory implements Serializable{

	private static final long serialVersionUID = 3160318050765342153L;
	
	@Id
	@GeneratedValue
	@Column(name = "event_id")
	private Integer id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "offerletter_id")
	@ForeignKey(name = "fk_event_offerletter")
	private OfferLetter offerletter;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private OfferLetterStatus status;

	@Column(name = "notes")
	private String notes;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "status_created_on")
	private Date statusCreatedOn;


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on")
	private Date createdDate;

	@Column(name = "updated_by")
	private String updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_on")
	private Date updatedOn;

	@Version
	@Column(name = "version")
	private long version;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}


	public OfferLetter getOfferletter() {
		return offerletter;
	}

	public void setOfferletter(OfferLetter offerletter) {
		this.offerletter = offerletter;
	}

	/**
	 * @return the status
	 */
	

	public OfferLetterStatus getStatus() {
		return status;
	}

	public void setStatus(OfferLetterStatus status) {
		this.status = status;
	}

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdOn
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdOn the createdOn to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return the updatedOn
	 */
	public Date getUpdatedOn() {
		return updatedOn;
	}

	/**
	 * @param updatedOn the updatedOn to set
	 */
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	/**
	 * @return the version
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(long version) {
		this.version = version;
	}

	public Date getStatusCreatedOn() {
		return statusCreatedOn;
	}

	public void setStatusCreatedOn(Date statusCreatedOn) {
		this.statusCreatedOn = statusCreatedOn;
	}

}

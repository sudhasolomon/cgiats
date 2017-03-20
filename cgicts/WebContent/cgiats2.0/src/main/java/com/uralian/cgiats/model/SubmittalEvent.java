/*
 * SubmittalEvent.java Jun 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.ForeignKey;

/**
 * @author Vlad Orzhekhovskiy
 */
@Entity
@Table(name = "submittal_history")
public class SubmittalEvent implements Serializable
{
	private static final long serialVersionUID = 6971334821979180879L;

	@Id
	@GeneratedValue
	@Column(name = "event_id")
	private Integer id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "submittal_id")
	@ForeignKey(name = "fk_event_submittal")
	private Submittal submittal;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private SubmittalStatus status;

	@Column(name = "notes")
	private String notes;
	
	@Column(name = "created_by")
	private String createdBy;

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

	/**
	 * @return the submittal
	 */
	public Submittal getSubmittal() {
		return submittal;
	}

	/**
	 * @param submittal the submittal to set
	 */
	public void setSubmittal(Submittal submittal) {
		this.submittal = submittal;
	}

	/**
	 * @return the status
	 */
	public SubmittalStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(SubmittalStatus status) {
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
	
	
}
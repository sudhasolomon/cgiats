/*
 * Submittal.java Jun 15, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.ForeignKey;

/**
 * @author Vlad Orzhekhovskiy
 */
@Entity
@Table(name = "submittal")
public class Submittal implements Serializable
{
	private static final long serialVersionUID = -8708795589893005083L;

	@Id
	@GeneratedValue
	@Column(name = "submittal_id")
	private Integer id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "candidate_id")
	@ForeignKey(name = "fk_submittal_candidate")
	private Candidate candidate;

	@ManyToOne(optional = false)
	@JoinColumn(name = "order_id")
	@ForeignKey(name = "fk_submittal_order")
	private JobOrder jobOrder;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private SubmittalStatus status;

	@OneToMany(mappedBy = "submittal", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("createdDate")
	private List<SubmittalEvent> history = new ArrayList<SubmittalEvent>();
	
	@Column(name = "created_by")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on")
	private Date createdOn;

	@Column(name = "updated_by")
	private String updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_on")
	private Date createdDate;

	@Version
	@Column(name = "version")
	private long version;

	@Column(name = "delete_flag")
	private Integer deleteFlag;
	
	@Column(name = "comments")
	private String comments;
	
	@Transient
	private boolean flag=true;
	/**
	 */
	public Submittal()
	{
		setStatus(SubmittalStatus.SUBMITTED);
	}

	/**
	 * @param candidate
	 */
	public Submittal(Candidate candidate)
	{
		setStatus(SubmittalStatus.SUBMITTED);
		setCandidate(candidate);
	}
	

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
	 * @return the createdDate
	 */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
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
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
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

	/**
	 * @return the candidate.
	 */
	public Candidate getCandidate()
	{
		return candidate;
	}

	/**
	 * @param candidate the candidate to set.
	 */
	public void setCandidate(Candidate candidate)
	{
		this.candidate = candidate;
	}

	/**
	 * @return the order.
	 */
	public JobOrder getJobOrder()
	{
		return jobOrder;
	}

	/**
	 * @param order the order to set.
	 */
	public void setJobOrder(JobOrder order)
	{
		this.jobOrder = order;
	}

	/**
	 * @return the status.
	 */
	public SubmittalStatus getStatus()
	{
		return status;
	}

	/**
	 * @param status the status to set.
	 */
	public void setStatus(SubmittalStatus status)
	{
		this.status = status;
	}

	/**
	 * @return the history.
	 */
	public List<SubmittalEvent> getHistory()
	{
		return history;
	}

	/**
	 * @param history the history to set.
	 */
	public void setHistory(List<SubmittalEvent> history)
	{
		this.history = history;
	}
	
	
	/**
	 * @return the deleteFlag.
	 */

	public Integer getDeleteFlag() {
		return deleteFlag;
	}
	
	/**
	 * @param deleteFlag the deleteFlag to set.
	 */
	
	public void setDeleteFlag(Integer deleteFlag) {
		this.deleteFlag = deleteFlag;
	}
	
	

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * @param event
	 */
	public void addEvent(SubmittalEvent event)
	{		
		this.history.add(event);
		event.setSubmittal(this);
	}

	/**
	 * @return
	 */
	public List<SubmittalEvent> getKeyEvents()
	{
		SubmittalStatus lastStatus = null;
		List<SubmittalEvent> list = new ArrayList<SubmittalEvent>();
		for (SubmittalEvent event : history)
		{
			if (event.getStatus() != lastStatus)
			{
				list.add(event);
				lastStatus = event.getStatus();
			}
		}
		return list;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	
}
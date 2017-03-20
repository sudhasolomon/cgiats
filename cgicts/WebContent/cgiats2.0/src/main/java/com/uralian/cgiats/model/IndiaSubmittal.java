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


@Entity
@Table(name = "india_submittal")
public class IndiaSubmittal implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3710124870867224209L;

	@Id
	@GeneratedValue
	@Column(name = "submittal_id")
	private Integer id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "candidate_id")
	@ForeignKey(name = "fk_submittal_candidate")
	private IndiaCandidate candidate;

	@ManyToOne(optional = false)
	@JoinColumn(name = "order_id")
	@ForeignKey(name = "fk_submittal_order")
	private IndiaJobOrder jobOrder;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private SubmittalStatus status;

	@OneToMany(mappedBy = "submittal", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("createdDate")
	private List<IndiaSubmittalEvent> history = new ArrayList<IndiaSubmittalEvent>();
	
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
	public IndiaSubmittal()
	{
		setStatus(SubmittalStatus.SUBMITTED);
	}

	/**
	 * @param candidate
	 */
	public IndiaSubmittal(IndiaCandidate candidate)
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
	public IndiaCandidate getCandidate()
	{
		return candidate;
	}

	/**
	 * @param candidate the candidate to set.
	 */
	public void setCandidate(IndiaCandidate candidate)
	{
		this.candidate = candidate;
	}

	/**
	 * @return the order.
	 */
	public IndiaJobOrder getJobOrder()
	{
		return jobOrder;
	}

	/**
	 * @param order the order to set.
	 */
	public void setJobOrder(IndiaJobOrder order)
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
	public List<IndiaSubmittalEvent> getHistory()
	{
		return history;
	}

	/**
	 * @param history the history to set.
	 */
	public void setHistory(List<IndiaSubmittalEvent> history)
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

	/**
	 * @param event
	 */
	public void addEvent(IndiaSubmittalEvent event)
	{		
		this.history.add(event);
		event.setSubmittal(this);
	}

	/**
	 * @return
	 */
	public List<IndiaSubmittalEvent> getKeyEvents()
	{
		SubmittalStatus lastStatus = null;
		List<IndiaSubmittalEvent> list = new ArrayList<IndiaSubmittalEvent>();
		for (IndiaSubmittalEvent event : history)
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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}


	


}

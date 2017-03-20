package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;


@Entity
@Table(name = "india_online_cgi_candidates")
public class IndiaOnlineCgiCandidates extends AuditableEntity<Integer> implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9066689408440645779L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	protected Integer id;

	@Column(name = "candidate_id")
	protected Integer candidateId;

	@Column(name = "order_id")
	private Integer orderId;


	@Column(name = "resume_status")
	protected String resumeStatus;

	/**
	 */
	public IndiaOnlineCgiCandidates()
	{
	}


	/**
	 * @param candidateId
	 * @param orderId
	 * @param resumeStatus
	 * @param createdUser
	 * @param createdOn
	 * @param updatedBy
	 * @param updatedOn
	 */
	public IndiaOnlineCgiCandidates(Integer id,Integer candidateId,Integer orderId,String resumeStatus)
	{
		this.id=id;
		this.candidateId = candidateId;
		this.orderId = orderId;
		this.resumeStatus = resumeStatus;
	}

	@Override
	protected Object getBusinessKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getCandidateId() {
		return candidateId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public String getResumeStatus() {
		return resumeStatus;
	}

	public void setCandidateId(Integer candidateId) {
		this.candidateId = candidateId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public void setResumeStatus(String resumeStatus) {
		this.resumeStatus = resumeStatus;
	}


	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * @return
	 */
	public Date getLastModified()
	{
		return getUpdatedOn() != null ? getUpdatedOn() : getCreatedOn();
	}


}

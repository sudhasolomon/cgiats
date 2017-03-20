package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

@Entity
@Table(name = "mobile_cgi_candidates")
public class MobileCgiCandidates extends AuditableEntity<Integer> implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6586606949518551336L;

	/**
	 * 
	 */

	@Id
	@GeneratedValue
	@Column(name = "id")
	protected Integer id;

	@ManyToOne
	@JoinColumn(name = "candidate_id")
	@ForeignKey(name = "fk_mobilecandidate_user")
	protected Candidate candidateId;
	@ManyToOne
	@JoinColumn(name = "order_id")
	@ForeignKey(name = "fk_mobilejoborder_user")
	private JobOrder orderId;

	@Column(name = "resume_status")
	protected String resumeStatus;

	public MobileCgiCandidates() {
		// TODO Auto-generated constructor stub
	}

	public MobileCgiCandidates(Integer id, Candidate candidateId,
			JobOrder orderId, String resumeStatus) {
		this.id = id;
		this.candidateId = candidateId;
		this.orderId = orderId;
		this.resumeStatus = resumeStatus;
	}

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object getBusinessKey() {
		// TODO Auto-generated method stub
		return null;
	}

	public Candidate getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(Candidate candidateId) {
		this.candidateId = candidateId;
	}

	public JobOrder getOrderId() {
		return orderId;
	}

	public void setOrderId(JobOrder orderId) {
		this.orderId = orderId;
	}

	public String getResumeStatus() {
		return resumeStatus;
	}

	public void setResumeStatus(String resumeStatus) {
		this.resumeStatus = resumeStatus;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getLastModified() {
		return getUpdatedOn() != null ? getUpdatedOn() : getCreatedOn();
	}

}

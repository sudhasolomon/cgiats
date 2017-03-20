package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

/**
 * @author Chaitanya
 * 
 */
@Entity
@Table(name = "online_cgi_candidates")
public class OnlineCgiCandidates extends AuditableEntity<Integer> implements
		Serializable {
	private static final long serialVersionUID = -2162621836299612953L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	protected Integer id;

	@ManyToOne
	@JoinColumn(name = "candidate_id")
	@ForeignKey(name = "fk_candidate_user")
	protected Candidate candidateId;

	@ManyToOne
	@JoinColumn(name = "order_id")
	@ForeignKey(name = "fk_joborder_user")
	private JobOrder orderId;

	@Column(name = "resume_status")
	protected String resumeStatus;
	@Transient
	private String firstName;
	@Transient
	private String lastName;
	@Transient
	private String email;

	@Transient
	private String mobileNo;

	@Transient
	private String jobTitle;

	/**
	 */
	public OnlineCgiCandidates() {
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
	public OnlineCgiCandidates(Integer id, Candidate candidateId,
			JobOrder orderId, String resumeStatus) {
		this.id = id;
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

	public String getResumeStatus() {
		return resumeStatus;
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

	public void setResumeStatus(String resumeStatus) {
		this.resumeStatus = resumeStatus;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return
	 */
	public Date getLastModified() {
		return getUpdatedOn() != null ? getUpdatedOn() : getCreatedOn();
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

}
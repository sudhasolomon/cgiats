package com.uralian.cgiats.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;


@Entity
@Table(name = "india_resume_audit_history")
public class IndiaResumeHistory extends AuditableEntity<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3834808742301800174L;

	@Id
	@SequenceGenerator(name = "generator", sequenceName = "resume_history_seq")
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator = "generator")
	@Column(name = "history_id")
	private Integer id;

	@Column(name = "status")
	private String status;
	

	@JoinColumn(name = "candidate_id")
	@ForeignKey(name = "fk_resume_audit_history_candidate")
	private Integer candidate;
	
	@Column(name = "document_status")
	private String docStatus;

	
	public IndiaResumeHistory(){
		
	}
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the candidate
	 */
	public Integer getCandidate() {
		return candidate;
	}

	/**
	 * @param candidate the candidate to set
	 */
	public void setCandidate(Integer candidate) {
		this.candidate = candidate;
	}

	/**
	 * @return the docStatus
	 */
	public String getDocStatus() {
		return docStatus;
	}

	/**
	 * @param docStatus the docStatus to set
	 */
	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	protected Object getBusinessKey() {
		// TODO Auto-generated method stub
		return id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ResumeHistory [id=" + id + ", status=" + status
				+ ", candidate=" + candidate + ", docStatus=" + docStatus + "]";
	}
	
	

}

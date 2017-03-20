package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="india_candidate_statuses")
@SequenceGenerator(sequenceName = "SEQ_CANDIDATESTATUSES", name = "SEQ_CANDIDATESTATUSES")
public class IndiaCandidateStatuses implements Serializable {

	

	private static final long serialVersionUID = -6106367504861585125L;

	@Id
	@GeneratedValue(generator = "SEQ_CANDIDATESTATUSES", strategy = GenerationType.AUTO)
	private int id;

	@Column(name = "status")
	@Enumerated(EnumType.ORDINAL)
	private CandidateStatus status;

	@Column(name = "createdDate")
	private Date createdDate;
	
	@Column(name = "createdBy")
	private String createdBy;
/*
	@ManyToOne
	@JoinColumn
    private Candidate candidate;*/
	
	@ManyToOne
	@JoinColumn
    private IndiaCandidate indiacandidate;
	
	
	@Column(name = "reason")
	private byte[] reason;
	


	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CandidateStatus getStatus() {
		return status;
	}

	public void setStatus(CandidateStatus status) {
		this.status = status;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/*public Candidate getCandidate() {
		return candidate;
	}

	public void setCandidate(Candidate candidate) {
		this.candidate = candidate;
	}
*/
	public byte[] getReason() {
		return reason;
	}

	public void setReason(byte[] reason) {
		this.reason = reason;
	}
	
	public String getReason1(){
		
		if(getReason()!=null&&getReason().length>0)
		return new String(getReason());
		else
			return null;
	}

	public IndiaCandidate getIndiacandidate() {
		return indiacandidate;
	}

	public void setIndiacandidate(IndiaCandidate indiacandidate) {
		this.indiacandidate = indiacandidate;
	}


}

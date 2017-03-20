package com.uralian.cgiats.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;


@Entity
@Table(name = "india_candidate_history")
public class IndiaCandidateHistory extends AuditableEntity<Integer> implements Serializable{
	
	
	private static final long serialVersionUID = 225375305786372878L;

	@Id
	@SequenceGenerator(name = "generator", sequenceName = "india_candidate_history_seq")
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator = "generator")
	@Column(name = "candidate_history_id")
	private Integer id;

	@Column(name = "status")
	private String status;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "candidate_id")
	@ForeignKey(name = "fk_candidate_history_candidate")
	private IndiaCandidate candidate;
	
	@Column(name = "reason")
	private String reason;

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

	/**
	 */
 public IndiaCandidateHistory() {
		// TODO Auto-generated constructor stub
	 setCandidate(candidate);	
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public IndiaCandidate getCandidate() {
		return candidate;
	}

	public void setCandidate(IndiaCandidate candidate) {
		this.candidate = candidate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	/**
	 * @param candidate
	 */
	public IndiaCandidateHistory(IndiaCandidate candidate)
	{
		setCandidate(candidate);
	}
	@Override
	public String toString() {
		return "IndiaCandidateHistory [id=" + id + ", status=" + status
				+ ", candidate=" + candidate + ", reason=" + reason + "]";
	}


}

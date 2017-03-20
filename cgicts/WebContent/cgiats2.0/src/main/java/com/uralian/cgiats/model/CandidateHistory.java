package com.uralian.cgiats.model;
import org.hibernate.annotations.ForeignKey;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Radhika Bharatha
 *
 */

@Entity
@Table(name = "candidate_history")
public class CandidateHistory extends AuditableEntity<Integer> implements Serializable{
	private static final long serialVersionUID = 1537337264767793979L;
	
	@Id
	@SequenceGenerator(name = "generator", sequenceName = "candidate_history_seq")
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator = "generator")
	@Column(name = "candidate_history_id")
	private Integer id;

	@Column(name = "status")
	private String status;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "candidate_id")
	@ForeignKey(name = "fk_candidate_history_candidate")
	private Candidate candidate;
	
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
 public CandidateHistory() {
		// TODO Auto-generated constructor stub
	 setCandidate(candidate);	
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Candidate getCandidate() {
		return candidate;
	}

	public void setCandidate(Candidate candidate) {
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
	public CandidateHistory(Candidate candidate)
	{
		setCandidate(candidate);
	}
	@Override
	public String toString() {
		return "CandidateHistory [id=" + id + ", status=" + status
				+ ", candidate=" + candidate + ", reason=" + reason + "]";
	}

}

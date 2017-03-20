package com.uralian.cgiats.model;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

import org.hibernate.annotations.ForeignKey;

import com.uralian.cgiats.util.Utils;

/**
 * @author Christian Rebollar
 */
@Entity
@Table(name = "duplicate_resume_track")
public class DuplicateResumeTrack extends AuditableEntity<Integer> implements Serializable

{
	private static final long serialVersionUID = 6086775399753573849L;

	@Id
	@GeneratedValue
	@Column(name = "id")
	private Integer id;

	@Column(name = "portal_userid")
	private String portalUserId;

	@Column(name = "portal_name")  
	private String portalName;
	
	@Column(name = "ats_userid")
	private String atsUserId;

	@Column(name = "candidate_name")
	private String candidateName;

	@Column(name = "candidate_email")  
	private String candidateEmail;

	/**
	 */
	public DuplicateResumeTrack()
	{
	}

	public Integer getId() {
		return id;
	}

	public String getCandidateName() {
		return candidateName;
	}

	public String getCandidateEmail() {
		return candidateEmail;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setPortalUserId(String portalUserId) {
		this.portalUserId = portalUserId;
	}

	
	public void setCandidateName(String candidateName) {
		this.candidateName = candidateName;
	}

	public void setCandidateEmail(String candidateEmail) {
		this.candidateEmail = candidateEmail;
	}

	@Override
	protected Object getBusinessKey() {
		// TODO Auto-generated method stub
		return id;
	}

	public void setAtsUserId(String atsUserId) {
		this.atsUserId = atsUserId;
	}

	public String getPortalUserId() {
		return portalUserId;
	}

	public String getAtsUserId() {
		return atsUserId;
	}

	public String getPortalName() {
		return portalName;
	}

	public void setPortalName(String portalName) {
		this.portalName = portalName;
	}

	
}
/**
 * 
 */
package com.uralian.cgiats.dto;

import java.io.Serializable;

/**
 * @author Sreenath
 *
 */
public class CandidateBlockHotVo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7556594350282134342L;
	private String candidateId;
	private String reason;

	public String getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(String candidateId) {
		this.candidateId = candidateId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}

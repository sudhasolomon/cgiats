package com.uralian.cgiats.dao;

import java.util.List;
import java.util.Map;

import com.uralian.cgiats.model.CarrerAccount;
import com.uralian.cgiats.model.MobileCgiCandidates;

/**
 * This is the Candidate DAO interface.
 * 
 * @author Christian Rebollar
 */
public interface CareerAccountDao extends GenericDao<CarrerAccount, Integer>
{

	/**
	 * Retrieves the candidate by id.
	 * 
	 * @param candidateId
	 * @param fetchDocument
	 * @param fetchProperties
	 * @return
	 */
	
	/*public List<Map<String,String>> findCandidateJobList(Integer candidateId);*/
/*	public List<CarrerAccount> getCareerAccountFromEmail(String emailId);*/
}
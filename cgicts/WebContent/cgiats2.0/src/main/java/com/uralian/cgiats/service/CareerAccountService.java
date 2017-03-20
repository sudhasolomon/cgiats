package com.uralian.cgiats.service;

import java.util.List;

import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CarrerAccount;
import com.uralian.cgiats.model.User;

/**
 * This interfaces exposes Candidate operations.
 * 
 * 
 */
public interface CareerAccountService
{
	
	/**
	 * @param candidate
	 * @throws ServiceException
	 */
	public Candidate saveCareerAccount(CarrerAccount carrerAccount) throws ServiceException;
	
	public List<CarrerAccount> getCareerAccountFromEmail(String emailId, String careerPwd);
	
	public CarrerAccount getCareerFromEmail(String emailId,String password);
	
	public CarrerAccount getEmailFromCareerAcct(String emailId);
	
	public void updateCarrerAccount(CarrerAccount carrerAccount) throws ServiceException;
	
	public User getExecutiveUser(String userId,String password);
	
	/*public MobileCgiCandidates savemobileCandidate(MobileCgiCandidates mobileCgiCandidates);*/
	
	/*public List<MobileCgiCandidates> getCandidate(Integer orderId,Integer candidateId);*/
	
	/*public List<Map<String,String>> findCandidateJobList(Integer candidateId);*/


}
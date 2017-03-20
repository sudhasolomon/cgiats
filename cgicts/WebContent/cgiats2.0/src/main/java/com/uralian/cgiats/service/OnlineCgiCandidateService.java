package com.uralian.cgiats.service;

import java.util.Date;
import java.util.List;

import com.uralian.cgiats.model.OnlineCgiCandidates;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.util.OnlineResumeStats;


/**
 * @author Chaitanya
 *
 */
public interface OnlineCgiCandidateService {

	/**
	 * @param candidate
	 * @throws ServiceException
	 */
	public void saveCandidate(OnlineCgiCandidates onlineCgiCandidates) throws ServiceException;

	/**
	 * @param status
	 * @param userId 
	 * @return
	 */
	public List<OnlineCgiCandidates> getOnlineCgiCandidates(String status, String userId);

	
	/**
	 * @param candidateId
	 * @param jobOrder 
	 * @return
	 */
	public OnlineCgiCandidates getOnlineCgiCandidate(int candidateId, int jobOrder);

	/**
	 * @param onlineCgiCanidates
	 * @throws ServiceException
	 */
	public void updateCandidate(OnlineCgiCandidates onlineCgiCanidates) throws ServiceException;

	
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public List<OnlineResumeStats> getOnlineResumeStats(Date dateStart, Date dateEnd);

	public void deleteCandidate(int candidateId) throws ServiceException;
	
	public List<OnlineCgiCandidates> getCandidate(Integer orderId,Integer candidateId);
	
	public Boolean isCandidateAlreadyApplied(Integer orderId,Integer candidateId);
}

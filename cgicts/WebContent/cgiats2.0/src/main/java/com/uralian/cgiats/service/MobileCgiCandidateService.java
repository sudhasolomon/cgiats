package com.uralian.cgiats.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.model.MobileCgiCandidates;
import com.uralian.cgiats.model.OnlineCgiCandidates;
import com.uralian.cgiats.util.OnlineResumeStats;



public interface MobileCgiCandidateService {

	/**
	 * @param candidate
	 * @throws ServiceException
	 */
	
	public void saveMobileCandidate(MobileCgiCandidates mobileCgiCandidates) throws ServiceException;

	/**
	 * @param status
	 * @param string 
	 * @return
	 */
	public List<MobileCgiCandidates> getMobileCgiCandidates(String status, String string);

	
	/**
	 * @param candidateId
	 * @return
	 */
	public MobileCgiCandidates getMobileCgiCandidate(int candidateId,int JobId);

	/**
	 * @param onlineCgiCanidates
	 * @throws ServiceException
	 */
	public void updateCandidate(MobileCgiCandidates mobileCgiCanidates) throws ServiceException;

	
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	/*public List<OnlineResumeStats> getOnlineResumeStats(Date dateStart, Date dateEnd);*/

	public void deleteCandidate(int candidateId) throws ServiceException;
	
	public List<MobileCgiCandidates> getCandidate(Integer orderId,Integer candidateId);
	
	public List<Map<String,String>> findCandidateJobList(Integer candidateId);
	
	
}

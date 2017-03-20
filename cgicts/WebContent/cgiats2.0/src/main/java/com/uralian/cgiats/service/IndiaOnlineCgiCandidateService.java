package com.uralian.cgiats.service;

import java.util.Date;
import java.util.List;
import com.uralian.cgiats.model.IndiaOnlineCgiCandidates;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.OnlineResumeStats;


public interface IndiaOnlineCgiCandidateService {
	/**
	 * @param candidate
	 * @throws ServiceException
	 */
	public void saveCandidate(IndiaOnlineCgiCandidates onlineCgiCandidates) throws ServiceException;

	/**
	 * @param status
	 * @return
	 */
	public List<IndiaOnlineCgiCandidates> getOnlineCgiCandidates(String status);

	
	/**
	 * @param candidateId
	 * @return
	 */
	public IndiaOnlineCgiCandidates getOnlineCgiCandidate(int candidateId);

	/**
	 * @param onlineCgiCanidates
	 * @throws ServiceException
	 */
	public void updateCandidate(IndiaOnlineCgiCandidates onlineCgiCanidates) throws ServiceException;

	
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public List<OnlineResumeStats> getOnlineResumeStats(Date dateStart, Date dateEnd);

	public void deleteCandidate(int candidateId) throws ServiceException;
}

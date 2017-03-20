package com.uralian.cgiats.service;
/*
 * Radhika
 * Created Date:8/1/2013
 * Comment: Created to store candidate history for the tickets ATS-231 and ATS-235
 */
import java.util.List;

import com.uralian.cgiats.model.CandidateHistory;

public interface CandidateHistoryService {
	/**
	 * @param candidateHistory
	 * @throws ServiceException
	 */
	public void saveCandidateHistory(CandidateHistory candidateHistory) throws ServiceException;

	
	/**
	 * @param candidateId
	 * @return
	 * 
	 */
	
	public List<CandidateHistory> getHotStatusDetails(Integer candidateId);
	
	/**
	 *@param candidateId
	 * @return
	 */
	public List<CandidateHistory> getBlackStatusDetails(Integer candidateId);
	
	/**
	 * @param candidateId
	 * @return
	 */
	public List<CandidateHistory> getDeletedStatusDetails(Integer candidateId);
	
	/**
	 * 
	 * Method to send a mail when candidate is moved to hot list
	 * 
	 * @param candidateHistory
	 * @param city 
	 */
	public void sendHotMail(CandidateHistory candidateHistory, String officeLocation);
	
	/**@author Raghavender
	 * @param candidateHistory
	 * @param officeLoc
	 */
	public void sendBlackListMail(CandidateHistory candidateHistory,String officeLoc);
}

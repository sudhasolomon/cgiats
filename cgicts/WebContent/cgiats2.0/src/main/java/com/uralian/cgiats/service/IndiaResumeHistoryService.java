package com.uralian.cgiats.service;

import java.util.Date;
import java.util.List;

import com.uralian.cgiats.model.IndiaResumeHistory;

public interface IndiaResumeHistoryService {
	
	/**
	 * @return
	 */
	/*public List<String> findAllUsers();*/
	
	/**
	 * @param resumeHistory
	 * @throws ServiceException
	 */
	public void saveResumeHistory(IndiaResumeHistory resumeHistory) throws ServiceException;
	
	
	/**
	 * @param resumeHistory
	 * @return
	 */
	public List<IndiaResumeHistory> getResumeAuditLogs(Date startDate,Date endDate,String userId,Integer candidateId);

}

/**
 * 
 */
package com.uralian.cgiats.service;

import java.util.Date;
import java.util.List;

import com.uralian.cgiats.model.ResumeHistory;

/**
 * @author Parameshwar
 *
 */
public interface ResumeHistoryService {
	
	/**
	 * @return
	 */
	public List<String> findAllUsers();
	
	/**
	 * @param resumeHistory
	 * @throws ServiceException
	 */
	public void saveResumeHistory(ResumeHistory resumeHistory) throws ServiceException;
	
	
	/**
	 * @param resumeHistory
	 * @return
	 */
	public List<ResumeHistory> getResumeAuditLogs(Date startDate,Date endDate,String userId,Integer candidateId);
	
}

package com.uralian.cgiats.dao;

import java.util.Date;
import java.util.List;

import com.uralian.cgiats.model.OnlineCgiCandidates;
import com.uralian.cgiats.util.OnlineResumeStats;

/**
 * @author Chaitanya
 *
 */
public interface OnlineCgiCanidateDao extends GenericDao<OnlineCgiCandidates, Integer> {

	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public List<OnlineResumeStats> getOnlineResumeStats(Date dateStart, Date dateEnd);
	
	public List<OnlineCgiCandidates> getCandidate(Integer orderId,Integer candidateId);
	
	public Boolean isCandidateAlreadyApplied(Integer orderId,Integer candidateId);


}

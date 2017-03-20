package com.uralian.cgiats.dao;

import java.util.Date;
import java.util.List;

import com.uralian.cgiats.model.IndiaOnlineCgiCandidates;
import com.uralian.cgiats.util.OnlineResumeStats;

public interface IndiaOnlineCgiCandidateDao extends IndiaGenericDao<IndiaOnlineCgiCandidates, Integer>{
	
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public List<OnlineResumeStats> getOnlineResumeStats(Date dateStart, Date dateEnd);
	

}

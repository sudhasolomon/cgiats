package com.uralian.cgiats.dao;

import java.util.Date;
import java.util.Map;

import com.uralian.cgiats.model.DuplicateResumeTrack;

public interface DupResumeTrackDao  extends GenericDao<DuplicateResumeTrack, Integer>{
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String, Integer> getDuplicateResumes(Date dateStart, Date dateEnd);

	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String,Map<String, Integer>> getDiceDuplicateCount(Date dateStart, Date dateEnd);
}

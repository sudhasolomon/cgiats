package com.uralian.cgiats.service;

import java.util.Date;
import java.util.Map;

import com.uralian.cgiats.model.DuplicateResumeTrack;

public interface DupResumeTrackService {
	/**
	 * @param DuplicateResumeTrack
	 * @throws Exception
	 */
	public void saveDuplicateResume(DuplicateResumeTrack duplicateResumeTrack) throws Exception;
	
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

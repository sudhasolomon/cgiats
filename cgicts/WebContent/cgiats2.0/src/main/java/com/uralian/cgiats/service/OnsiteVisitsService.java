package com.uralian.cgiats.service;

import java.util.Date;
import java.util.List;

import com.uralian.cgiats.model.OnsiteVisits;

public interface OnsiteVisitsService {
	/**
	 * @param DuplicateResumeTrack
	 * @throws Exception
	 */
	public void saveOnsiteVisits(OnsiteVisits onsiteVisits) throws Exception;

	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public List<OnsiteVisits> getOnsiteVisits(Date dateStart, Date dateEnd);

}

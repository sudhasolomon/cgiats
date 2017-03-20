package com.uralian.cgiats.dao;

import java.util.Date;
import java.util.List;

import com.uralian.cgiats.model.OnsiteVisits;

/*
 * Radhika
 * Created Date:8/5/2013
 * Comment: Created to store candidate info for the tickets ATS-
 */

public interface OnsiteVisitsDao extends GenericDao<OnsiteVisits,Integer>{

	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public  List<OnsiteVisits> getOnsiteVisits(
			Date dateStart, Date dateEnd);
}

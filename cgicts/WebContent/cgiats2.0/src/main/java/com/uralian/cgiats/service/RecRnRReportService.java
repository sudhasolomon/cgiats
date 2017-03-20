/**
 * 
 */
package com.uralian.cgiats.service;

import java.util.Date;
import java.util.List;

import com.uralian.cgiats.model.SubmitalStats;

/**
 * @author Sreenath
 *
 */
public interface RecRnRReportService {

	/**
	 * @param fromDate
	 * @param toDate
	 * @param selectedLocation
	 * @returnList<SubmitalStats>
	 */
	List<SubmitalStats> getRecRnRReport(Date fromDate, Date toDate,
			String selectedLocation);
	
	List<SubmitalStats> getDmRnRReport(Date fromDate, Date toDate);

	/**
	 * @param fromDate
	 * @param toDate
	 * @param selectedLocation
	 * @param recName
	 * @returnList<SubmitalStats>
	 */
	List<SubmitalStats> getRecRnRReportDetails(Date fromDate, Date toDate,
			String selectedLocation, String recName);

}

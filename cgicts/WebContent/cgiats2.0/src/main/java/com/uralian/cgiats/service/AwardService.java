/**
 * 
 */
package com.uralian.cgiats.service;


import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.model.Awards;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.JobOrderSearchDto;
import com.uralian.cgiats.model.User;


/**
 * @author Parameshwar
 *
 */
public interface AwardService {
	
	/**
	* @return
	*/
	public List<Awards> listAwards();
	
	/**
	 * @param awardId
	 * @return
	 */
	public Awards loadAward(Integer awardId);

	
	 /**
	 * @param awards
	 * @return
	 * @throws ServiceException
	 */
	public void saveAwards(Awards awards) throws ServiceException;	
	
	/**
	 * @param awards
	 * @return
	 * @throws ServiceException
	 */
	public void updateAwards(Awards awards) throws ServiceException;
	
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public List<String>  getCandidatesByUser(Date dateStart, Date dateEnd);
	
	/**
	 * @param criteria
	 * @return
	 */
	public List<Awards> findAwards(JobOrderSearchDto criteria);

}

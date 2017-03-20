/**
 * 
 */
package com.uralian.cgiats.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.model.Awards;


/**
 * @author Parameshwar
 *
 */
public interface AwardDao extends GenericDao<Awards, Integer> {
	
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public List<String>  getCandidatesByUser(Date dateStart, Date dateEnd);

}

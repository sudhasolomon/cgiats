package com.uralian.cgiats.dao;
import java.util.Date;
import java.util.List;

import com.uralian.cgiats.model.LoginAttempts;
import com.uralian.cgiats.model.LoginInfoDto;
import com.uralian.cgiats.model.User;

/**
 * 
 */

/**
 * @author Parameshwar
 *
 */
public interface LoginDetailsDao extends GenericDao<LoginAttempts, Integer> {
	
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public List<LoginInfoDto> getLoginAttemptsDetails(Date dateStart,Date dateEnd);
	
	/**
	 * @param loginDate
	 * @param userId
	 * @return
	 */
	public List<Long> getLoginAttemptByDate(Date loginDate,String userId);

}

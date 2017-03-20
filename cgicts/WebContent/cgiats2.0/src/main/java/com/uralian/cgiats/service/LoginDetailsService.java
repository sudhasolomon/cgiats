/**
 * 
 */
package com.uralian.cgiats.service;
import java.util.Date;
import java.util.List;

import com.uralian.cgiats.model.LoginAttempts;
import com.uralian.cgiats.model.LoginInfoDto;



/**
 * @author Parameshwar
 *
 */
/**
 * @author Parameshwar
 *
 */
public interface LoginDetailsService {
	
	
	/**
	 * @param id
	 * @return	 
	 */
	public LoginAttempts loadLoginAttempts(Integer  id);
	
	/**
	 * @return
	 */
	public List<LoginAttempts> listLoginAttempts();
	
	/**
	 * @param loginDate
	 * @param userId
	 * @return
	 */
	public List<Long> getLoginAttemptByDate(Date loginDate,String userId);

	/**
	 * @param loginAttempts
	 * @return
	 * @throws ServiceException
	 */
	public LoginAttempts saveLoginDetails(LoginAttempts loginAttempts) throws ServiceException;
	
	/**
	 * @param user
	 * @throws ServiceException
	 */
	public void updateLoginDetails(LoginAttempts loginAttempts) throws ServiceException;
	
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public List<LoginInfoDto>  getLoginAttemptsDetails(Date dateStart, Date dateEnd);
	
	

}

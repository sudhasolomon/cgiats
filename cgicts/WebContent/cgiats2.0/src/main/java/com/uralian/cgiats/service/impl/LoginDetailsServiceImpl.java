/**
 * 
 */
package com.uralian.cgiats.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.LoginDetailsDao;
import com.uralian.cgiats.model.LoginAttempts;
import com.uralian.cgiats.model.LoginInfoDto;
import com.uralian.cgiats.service.LoginDetailsService;
import com.uralian.cgiats.service.ServiceException;

/**
 * 6
 * 
 * @author Parameshwar
 *
 */

@Service
@Transactional(rollbackFor = ServiceException.class)
public class LoginDetailsServiceImpl implements LoginDetailsService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private LoginDetailsDao loginDetailsDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.LoginDetailsService#loadLoginAttempts(java.
	 * lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public LoginAttempts loadLoginAttempts(Integer id) {
		try {
			LoginAttempts loginAttempts = loginDetailsDao.findById(id);
			return loginAttempts;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.LoginDetailsService#listLoginAttempts()
	 */
	@Override
	public List<LoginAttempts> listLoginAttempts() {
		try {
			List<LoginAttempts> loginAttempts = loginDetailsDao.findAll();
			return loginAttempts;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.LoginDetailsService#saveLoginDeatals(com.
	 * uralian.cgiats.model .LoginAttempts)
	 */
	@Override
	public LoginAttempts saveLoginDetails(LoginAttempts loginAttempts) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + loginAttempts);
			/* loginAttempts.setUserId("sriniB"); */
			return loginDetailsDao.save(loginAttempts);
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.LoginDetailsService#updateLoginDeatals(com.
	 * uralian.cgiats.model .LoginAttempts)
	 */
	@Override
	public void updateLoginDetails(LoginAttempts loginAttempts) throws ServiceException {
		try {
			loginDetailsDao.update(loginAttempts);
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	@Override
	public List<LoginInfoDto> getLoginAttemptsDetails(Date dateStart, Date dateEnd) {
		try {
			return loginDetailsDao.getLoginAttemptsDetails(dateStart, dateEnd);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.LoginDetailsService#getLoginAttemptByDate(java
	 * .util.Date, java.lang.String)
	 */
	@Override
	public List<Long> getLoginAttemptByDate(Date loginDate, String userId) {
		try {
			return loginDetailsDao.getLoginAttemptByDate(loginDate, userId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

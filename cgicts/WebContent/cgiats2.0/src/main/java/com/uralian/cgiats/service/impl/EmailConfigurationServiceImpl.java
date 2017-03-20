/**
 * 
 */
package com.uralian.cgiats.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.EmailConfigurationDao;
import com.uralian.cgiats.model.EmailConfiguration;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.service.EmailConfigurationService;

/**
 * @author Sreenath
 * 
 */
@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class EmailConfigurationServiceImpl implements EmailConfigurationService {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private EmailConfigurationDao emailConfigurationDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.EmailConfigurationService#saveEmailConfig(
	 * java.util.List)
	 */
	@Override
	public EmailConfiguration saveEmailConfig(EmailConfiguration emailConfiguration) {
		try {
			return emailConfigurationDao.saveOrUpdate(emailConfiguration);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.EmailConfigurationService#getEmails()
	 */
	@Override
	public EmailConfiguration[] getEmails() {
		try {
			List<EmailConfiguration> users = emailConfigurationDao.findAll();
			return users.toArray(new EmailConfiguration[users.size()]);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.EmailConfigurationService#deleteMail(com.
	 * uralian.cgiats.model.EmailConfiguration)
	 */
	@Override
	public void deleteMail(EmailConfiguration emailCfg) {
		try {
			emailConfigurationDao.delete(emailCfg);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

}

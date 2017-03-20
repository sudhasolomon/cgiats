/*
 * UserServiceImpl.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.PortalCredentialsDao;
import com.uralian.cgiats.model.PortalCredentials;
import com.uralian.cgiats.service.PortalCredentialsService;
import com.uralian.cgiats.service.ServiceException;

/**
 * @author Vlad Orzhekhovskiy
 */
@Service
@Transactional(rollbackFor = ServiceException.class)
public class PortalCredentialsServiceImpl implements PortalCredentialsService {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private PortalCredentialsDao portalCredentialsDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.UserService#loadUser(java.lang.String)
	 */
	@Override
	@Transactional(readOnly = true)
	public PortalCredentials loadUser(String userId) {
		try {
			PortalCredentials portalCredentials = portalCredentialsDao.findById(userId);
			return portalCredentials;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @param userId
	 * @throws ServiceException
	 */
	public List<PortalCredentials> getUsers(String userId, String portalName) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from PortalCredentials u");
			hql.append(" where portalUserId =:userId and u.portalName = :portalName ");
			params.put("userId", userId);
			params.put("portalName", portalName);
			return portalCredentialsDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public void updateCbPwd(PortalCredentials portalCredentials) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Updating " + portalCredentials);

			portalCredentialsDao.update(portalCredentials);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to update candidate", exception);
		}
	}

}
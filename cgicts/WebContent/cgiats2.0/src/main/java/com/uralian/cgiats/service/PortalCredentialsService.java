/*
 * UserService.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.service;

import java.util.List;

import com.uralian.cgiats.model.PortalCredentials;

/**
 * @author Vlad Orzhekhovskiy
 */
public interface PortalCredentialsService
{

	/**
	 * @param userId
	 * @return
	 */
	public PortalCredentials loadUser(String userId);
	
	public List<PortalCredentials> getUsers(String userId,String portalName);
	public void updateCbPwd(PortalCredentials portalCredentials) throws ServiceException;

}

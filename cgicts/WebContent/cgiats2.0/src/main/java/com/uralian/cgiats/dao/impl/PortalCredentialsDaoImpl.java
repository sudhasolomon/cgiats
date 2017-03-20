/*
 * UserDaoImpl.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.dao.impl;

import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.PortalCredentialsDao;
import com.uralian.cgiats.model.PortalCredentials;

/**
 * @author Vlad Orzhekhovskiy
 */
@Repository
@SuppressWarnings("unchecked")
public class PortalCredentialsDaoImpl extends GenericDaoImpl<PortalCredentials, String> implements
PortalCredentialsDao
{
	/**
	 */
	public PortalCredentialsDaoImpl()
	{
		super(PortalCredentials.class);
	}
}

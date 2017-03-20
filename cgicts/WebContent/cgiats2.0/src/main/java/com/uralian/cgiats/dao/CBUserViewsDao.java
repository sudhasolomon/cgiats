/*
 * UserDao.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.model.CBUserViews;

/**
 * @author Vlad Orzhekhovskiy
 */
public interface CBUserViewsDao extends GenericDao<CBUserViews, String>
{
	public List<CBUserViews> getCBUserViews(String portalUserId,Date dateStart, Date dateEnd,String portalName);
	public List<CBUserViews> getMonsterUserViews(String portalUserId,String portalName, Date monsterStartDate, Date monsterEndDate);
	public Map<String, Integer> getResumeCountFromProxy(Date dateStart, Date dateEnd,String portalName);
}

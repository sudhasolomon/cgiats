/*
 * UserServiceImpl.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.CBUserViewsDao;
import com.uralian.cgiats.model.CBUserViews;
import com.uralian.cgiats.service.CBUserViewsService;
import com.uralian.cgiats.service.ServiceException;

/**
 * @author Vlad Orzhekhovskiy
 */
@Service
@Transactional(rollbackFor = ServiceException.class)
public class CBUserViewsServiceImpl implements CBUserViewsService {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private CBUserViewsDao cbUserViewsDao;

	/**
	 * @param userId
	 * @throws ServiceException
	 */
	public List<CBUserViews> getUsers(String userId, String portalName) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			try {
				hql.append("from CBUserViews cb");
				hql.append(" where 1=1 and cb.portalUserId =:userId and cb.portalName=:portalName");
				params.put("userId", userId);
				params.put("portalName", portalName);

				if (portalName != null && !portalName.trim().equals("") && portalName.trim().equalsIgnoreCase("Careerbuilder")) {
					hql.append(" and to_date(to_char(cb.viewDate, 'MM-dd-yyyy'), 'MM-dd-yyyy') = :todayDate");
					Calendar c = Calendar.getInstance();
					DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
					Date yesterday = dateFormat.parse(dateFormat.format(c.getTime()));
					params.put("todayDate", yesterday);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return cbUserViewsDao.findByQuery(hql.toString(), params);
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
	public List<CBUserViews> getDiceUsers(String userId, String portalName) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			try {
				hql.append("from CBUserViews cb");
				hql.append(" where 1=1 and cb.portalUserId =:userId and cb.portalName=:portalName");
				params.put("userId", userId);
				params.put("portalName", portalName);

				if (portalName != null && !portalName.trim().equals("") && portalName.trim().equalsIgnoreCase("Dice")) {
					Calendar c = Calendar.getInstance();
					c.add(Calendar.MONTH, 0);
					c.set(Calendar.DAY_OF_MONTH, 1);
					DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
					Date fromDate = dateFormat.parse(dateFormat.format(c.getTime()));
					Calendar c1 = Calendar.getInstance();
					c1.set(c1.DATE, c.getActualMaximum(c1.DATE));
					Date toDate = dateFormat.parse(dateFormat.format(c1.getTime()));
					hql.append(" and to_date(to_char(cb.viewDate, 'MM-dd-yyyy'), 'MM-dd-yyyy')  between :fromDate and :toDate");
					params.put("fromDate", fromDate);
					params.put("toDate", toDate);

					log.info("fromDate>>" + fromDate);
					log.info("toDate>>" + toDate);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return cbUserViewsDao.findByQuery(hql.toString(), params);
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
	public List<CBUserViews> getMonsterUsers(String userId, String portalName) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			try {
				hql.append("from CBUserViews cb");
				hql.append(" where 1=1 and cb.portalUserId =:userId and cb.portalName=:portalName");
				Calendar c = Calendar.getInstance();
				params.put("userId", userId);
				params.put("portalName", portalName);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return cbUserViewsDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void saveCBUserViews(CBUserViews cbUserViews) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + cbUserViews);
			log.info("CBUserViews save>>" + cbUserViews);
			cbUserViewsDao.save(cbUserViews);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist candidate", exception);
		}
	}

	@Override
	public void updateCBUserViews(CBUserViews cbUserViews) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Updating " + cbUserViews);

			cbUserViewsDao.update(cbUserViews);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to update CBUserViews", exception);
		}
	}

	public List<CBUserViews> getCBUserViews(String portalUserId, Date dateStart, Date dateEnd, String portalName) {
		try {
			List<CBUserViews> cbUserViewsLst = cbUserViewsDao.getCBUserViews(portalUserId, dateStart, dateEnd, portalName);
			return cbUserViewsLst;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<CBUserViews> getMonsterUserViews(String portalUserId, String portalName, Date monsterStartDate, Date monsterEndDate) {
		try {
			List<CBUserViews> cbUserViewsLst = cbUserViewsDao.getMonsterUserViews(portalUserId, portalName, monsterStartDate, monsterEndDate);
			return cbUserViewsLst;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Integer> getResumeCountFromProxy(Date dateStart, Date dateEnd, String portalName) {
		try {
			Map<String, Integer> resumeCount = cbUserViewsDao.getResumeCountFromProxy(dateStart, dateEnd, portalName);
			return resumeCount;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public CBUserViews loadUserViewsOnId(Integer selectedUserViewsId) {
		try {
			StringBuffer hql = new StringBuffer();

			hql.append("from CBUserViews cb  where 1=1 and cb.id=:selectedUserViewsId");

			List<CBUserViews> views = cbUserViewsDao.findByQuery(hql.toString(), "selectedUserViewsId", selectedUserViewsId);
			if (views != null && views.size() != 0) {
				return views.get(0);
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void updateUserViews(CBUserViews selectedUserViews) {
		try {
			cbUserViewsDao.update(selectedUserViews);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

	}

}
/*
 * UserDaoImpl.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.CBUserViewsDao;
import com.uralian.cgiats.model.CBUserViews;

/**
 * @author Vlad Orzhekhovskiy
 */
@Repository
@SuppressWarnings("unchecked")
public class CBUserViewsDaoImpl extends GenericDaoImpl<CBUserViews, String> implements CBUserViewsDao {
	/**
	 */
	public CBUserViewsDaoImpl() {
		super(CBUserViews.class);
	}

	public List<CBUserViews> getCBUserViews(String portalUserId, Date dateStart, Date dateEnd, String portalName) {
		try {
			// if flag true then show DMs else other than DMs
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			StringBuilder hql = new StringBuilder();
			hql.append("from CBUserViews cb");
			hql.append(" where 1=1 and portalName=:portalName");
			paramNames.add("portalName");
			paramValues.add(portalName);
			if (portalUserId != null && !portalUserId.trim().equals("")) {
				hql.append(" and cb.portalUserId= :portalUserId");
				paramNames.add("portalUserId");
				paramValues.add(portalUserId);
			}
			if (dateStart != null && dateEnd != null) {
				hql.append(" and cb.viewDate between :startDate and :endDate");
				paramNames.add("startDate");
				paramValues.add(dateStart);
				paramNames.add("endDate");
				paramValues.add(DateUtils.addDays(dateEnd, 1));
			}
			hql.append(" order by cb.viewDate DESC");

			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			return (List<CBUserViews>) result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<CBUserViews> getMonsterUserViews(String portalUserId, String portalName, Date monsterStartDate, Date monsterEndDate) {
		try {
			// if flag true then show DMs else other than DMs
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			StringBuilder hql = new StringBuilder();
			hql.append("from CBUserViews cb");
			hql.append(" where 1=1 and portalName=:portalName");
			paramNames.add("portalName");
			paramValues.add(portalName);
			if (portalUserId != null) {
				hql.append(" and cb.portalUserId= :portalUserId");
				paramNames.add("portalUserId");
				paramValues.add(portalUserId);
			}

			if (monsterStartDate != null) {
				hql.append(" and date(cb.createdOn)>=date(:monsterStartDate)");
				paramNames.add("monsterStartDate");
				paramValues.add(monsterStartDate);
			}
			if (monsterEndDate != null) {
				hql.append(" and date(cb.createdOn)<=date(:monsterEndDate)");
				paramNames.add("monsterEndDate");
				paramValues.add(monsterEndDate);
			}

			hql.append(" order by cb.portalUserId");

			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			return (List<CBUserViews>) result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Integer> getResumeCountFromProxy(Date dateStart, Date dateEnd, String portalName) {
		Map<String, Integer> userMap = new TreeMap<String, Integer>();
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			StringBuilder hql = new StringBuilder();
			if (portalName != null && !portalName.trim().equals("") && portalName.trim().equalsIgnoreCase("Careerbuilder")) {
				hql.append("select c.portalEmail,count(c.portalEmail) from Candidate c ");
				hql.append(
						"where 1=1 and c.createdUser = :portalName and c.portalEmail NOT IN ('jleonard@charterglobal.com','cjenkins@charterglobal.com','tbailey@charterglobal.com','dshah@charterglobal.com','Mreddy@charterglobal.com','Sharsh@charterglobal.com','msava@charterglobal.com','vmula@charterglobal.com','bvincent@charterglobal.com','nkashyap@charterglobal.com','rtelleysh@charterglobal.com','santosh@charterglobal.com','mark@charterglobal.com')");
				paramNames.add("portalName");
				paramValues.add(portalName);
				if (dateStart != null) {
					hql.append(" and c.createdOn >= :startDate");
					paramNames.add("startDate");
					paramValues.add(dateStart);
				}
				if (dateEnd != null) {
					hql.append(" and c.createdOn <= :endDate");
					paramNames.add("endDate");
					paramValues.add(DateUtils.addDays(dateEnd, 1));
				}
				hql.append(" group by c.portalEmail");
			}
			if (portalName != null && !portalName.trim().equals("") && portalName.trim().equalsIgnoreCase("Monster")) {
				hql.append("select c.portalEmail,count(c.portalEmail) from Candidate c ");
				hql.append(
						"where 1=1 and c.createdUser = :portalName and c.portalEmail NOT IN ('xcharterpx42','xcharterpx031','xcharterpx44','xcharterpx45','xcharterpx46','xcharterpx47','xcharterpx48','xcharterpx49','xcharterpx50','xcharterpx51','xcharterpx52','xcharterpx53','xcharterpx54','xcharterpx55','xcharterpx56')");
				paramNames.add("portalName");
				paramValues.add(portalName);
				if (dateStart != null) {
					hql.append(" and c.createdOn >= :startDate");
					paramNames.add("startDate");
					paramValues.add(dateStart);
				}
				if (dateEnd != null) {
					hql.append(" and c.createdOn <= :endDate");
					paramNames.add("endDate");
					paramValues.add(DateUtils.addDays(dateEnd, 1));
				}
				hql.append(" group by c.portalEmail");
			}

			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				Number count = (Number) tuple[1];
				log.info("the counts for the user:" + username + "=" + count);

				userMap.put(username != null ? username.trim() : "", count != null ? count.intValue() : 0);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return userMap;
	}
}

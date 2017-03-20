package com.uralian.cgiats.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.DupResumeTrackDao;
import com.uralian.cgiats.model.DuplicateResumeTrack;

/**
 * @author Chaitanya
 *
 */
@Repository
@SuppressWarnings("unchecked")
public class DupResumeTrackDaoImpl extends GenericDaoImpl<DuplicateResumeTrack, Integer> implements DupResumeTrackDao {
	/**
	 */
	public DupResumeTrackDaoImpl() {
		super(DuplicateResumeTrack.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.dao.CandidateDao#getCandidatesByUser(java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public Map<String, Integer> getDuplicateResumes(Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append(
					"select CASE WHEN trim(both ' ' from portalUserId)='' THEN 'NA' WHEN trim(both ' ' from portalUserId) is null THEN 'NA'  ELSE trim(both ' ' from portalUserId) END ,count(*) from DuplicateResumeTrack ");
			hql.append(" where 1=1 and portalName = 'Dice' ");
			if (dateStart != null) {
				hql.append(" and createdOn >= :startDate");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and createdOn <= :endDate"); // as per ken modified
															// (Should it always
															// use the created
															// date when we
															// select by date?)
				paramNames.add("endDate");
				paramValues.add(DateUtils.addDays(dateEnd, 1));
			}
			hql.append(" group by trim(both ' ' from portalUserId)");

			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			Map<String, Integer> userMap = new TreeMap<String, Integer>();
			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				Number count = (Number) tuple[1];
				userMap.put(username != null ? username.trim() : "", count != null ? count.intValue() : 0);
			}

			return userMap;
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
	 * com.uralian.cgiats.dao.DupResumeTrackDao#getDiceDuplicateCount(java.util.
	 * Date, java.util.Date)
	 */
	@Override
	public Map<String, Map<String, Integer>> getDiceDuplicateCount(Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			StringBuilder hql = new StringBuilder();
			hql.append(
					"select CASE WHEN trim(both ' ' from d.atsUserId) ='' THEN 'NA' WHEN trim(both ' ' from d.atsUserId) is null THEN 'NA'  ELSE trim(both ' ' from d.atsUserId) END, ");
			hql.append(
					"CASE WHEN trim(both ' ' from d.portalUserId)='' THEN 'NA' WHEN trim(both ' ' from d.portalUserId) is null THEN 'NA'  ELSE trim(both ' ' from d.portalUserId) END, ");
			hql.append("count(*) from DuplicateResumeTrack d  ");
			hql.append(" where 1=1 and d.portalName='Dice' ");
			if (dateStart != null) {
				hql.append(" and d.createdOn >= :startDate");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and d.createdOn <= :endDate");
				paramNames.add("endDate");
				paramValues.add(DateUtils.addDays(dateEnd, 1));
			}
			hql.append(" group by trim(both ' ' from d.atsUserId),trim(both ' ' from d.portalUserId) order by trim(both ' ' from d.atsUserId)");

			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			Map<String, Map<String, Integer>> userMap = new TreeMap<String, Map<String, Integer>>();
			for (Object record : result) {
				Object[] tuple = (Object[]) record;

				String atsUserId = (String) tuple[0];
				String portalemail = (String) tuple[1];
				Number count = (Number) tuple[2];
				Map<String, Integer> statusMap = userMap.get(atsUserId);
				if (statusMap == null) {
					statusMap = new HashMap<String, Integer>();
					userMap.put(atsUserId, statusMap);
				}
				statusMap.put(portalemail, count != null ? count.intValue() : 0);
			}

			return userMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}
}

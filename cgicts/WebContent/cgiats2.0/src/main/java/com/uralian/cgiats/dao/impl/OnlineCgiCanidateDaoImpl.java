package com.uralian.cgiats.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.OnlineCgiCanidateDao;
import com.uralian.cgiats.dao.SubmittalDao;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.OnlineCgiCandidates;
import com.uralian.cgiats.util.OnlineResumeStats;

/**
 * @author Chaitanya
 *
 */
@Repository
@SuppressWarnings("unchecked")
public class OnlineCgiCanidateDaoImpl extends GenericDaoImpl<OnlineCgiCandidates, Integer> implements OnlineCgiCanidateDao {
	/**
	 */
	public OnlineCgiCanidateDaoImpl() {
		super(OnlineCgiCandidates.class);
	}

	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public List<OnlineResumeStats> getOnlineResumeStats(Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			StringBuilder hql = new StringBuilder();
			hql.append("select o.createdBy, o.resumeStatus,o.updatedBy, count(*) from OnlineCgiCandidates o");
			hql.append(" where 1=1 ");
			if (dateStart != null) {
				hql.append(" and COALESCE(cast(o.updatedOn as date),cast(o.createdOn as date)) >=  cast(:startDate as date)");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and COALESCE(cast(o.updatedOn as date),cast(o.createdOn as date)) <= cast(:endDate as date)"); // as
																															// per
																															// ken
																															// modified
																															// (Should
																															// it
																															// always
																															// use
																															// the
																															// created
																															// date
																															// when
																															// we
																															// select
																															// by
																															// date?)
				paramNames.add("endDate");
				paramValues.add(dateEnd);
			}
			hql.append(" group by  o.createdBy, o.resumeStatus,o.updatedBy");
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
			return (List<OnlineResumeStats>) result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<OnlineCgiCandidates> getCandidate(Integer orderId, Integer candidateId) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();

			hql.append("from OnlineCgiCandidates o");
			hql.append(" where o.orderId.id = :orderId and o.candidateId.id= :candidateId ");
			params.put("orderId", orderId);
			params.put("candidateId", candidateId);
			System.out.println("hql-----" + hql.toString());
			System.out.println("orderId-----" + orderId);
			System.out.println("candidateId-----" + candidateId);
			List<?> result = findByQuery(hql.toString(), params);
			return (List<OnlineCgiCandidates>) result;
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
	 * com.uralian.cgiats.dao.OnlineCgiCanidateDao#isCandidateAlreadyApplied(
	 * java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Boolean isCandidateAlreadyApplied(Integer orderId, Integer candidateId) {
		try {
			Boolean isCandidateApplied = false;

			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();

			hql.append("select o.id from OnlineCgiCandidates o");
			hql.append(" where o.orderId.id = :orderId and o.candidateId.id= :candidateId ");
			params.put("orderId", orderId);
			params.put("candidateId", candidateId);
			System.out.println("hql-----" + hql.toString());
			System.out.println("orderId-----" + orderId);
			System.out.println("candidateId-----" + candidateId);
			List<?> result = findByQuery(hql.toString(), params);
			if (result != null && result.size() > 0) {
				isCandidateApplied = true;
			}
			return isCandidateApplied;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

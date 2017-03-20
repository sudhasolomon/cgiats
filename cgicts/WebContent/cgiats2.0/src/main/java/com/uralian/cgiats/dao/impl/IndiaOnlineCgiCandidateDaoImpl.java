package com.uralian.cgiats.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.IndiaOnlineCgiCandidateDao;
import com.uralian.cgiats.model.IndiaOnlineCgiCandidates;
import com.uralian.cgiats.util.OnlineResumeStats;

@SuppressWarnings("unchecked")
@Repository
public class IndiaOnlineCgiCandidateDaoImpl extends IndiaGenericDaoImpl<IndiaOnlineCgiCandidates, Integer> implements IndiaOnlineCgiCandidateDao {

	public IndiaOnlineCgiCandidateDaoImpl() {
		super(IndiaOnlineCgiCandidates.class);
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
				// as per ken modified (Should it always use the created date
				// when we select by date?)
				hql.append(" and COALESCE(cast(o.updatedOn as date),cast(o.createdOn as date)) <= cast(:endDate as date)");
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

}

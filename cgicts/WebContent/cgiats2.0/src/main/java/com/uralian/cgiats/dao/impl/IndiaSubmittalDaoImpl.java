package com.uralian.cgiats.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.IndiaSubmittalDao;
import com.uralian.cgiats.model.IndiaSubmittal;
import com.uralian.cgiats.model.SubmittalStatus;

@SuppressWarnings("unchecked")
@Repository
public class IndiaSubmittalDaoImpl extends IndiaGenericDaoImpl<IndiaSubmittal, Integer> implements IndiaSubmittalDao {

	protected IndiaSubmittalDaoImpl() {
		super(IndiaSubmittal.class);
	}

	@Override
	public List<IndiaSubmittal> getCandidateJobrderStatus(Integer candidateId) {
		try {
			StringBuffer hql = new StringBuffer();

			hql.append("from IndiaSubmittal s  where ");
			hql.append(" s.status not in ('OUTOFPROJ','REJECTED','DMREJ','BACKOUT') and s.deleteFlag=0 and s.candidate.id=:candidateId");

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("candidateId", candidateId);
			return findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<SubmittalStatus, Integer> getAllSubmittalStatusCounts(Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select s.status, count(*) from IndiaSubmittal s");
			hql.append(" where 1=1 and s.deleteFlag=0");
			if (dateStart != null) {
				hql.append(" and COALESCE(cast(s.createdDate as date),cast(s.createdOn as date)) >=  cast(:startDate as date)");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and COALESCE(cast(s.createdDate as date),cast(s.createdOn as date)) <= cast(:endDate as date)"); 
				
				paramNames.add("endDate");
				paramValues.add(dateEnd);
			}
			hql.append(" group by s.status order by s.status Desc");
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			Map<SubmittalStatus, Integer> userMap = new TreeMap<SubmittalStatus, Integer>();
			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				SubmittalStatus status = (SubmittalStatus) tuple[0];
				Number count = (Number) tuple[1];
				userMap.put(status, count.intValue());
			}

			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				SubmittalStatus status = (SubmittalStatus) tuple[0];
				Number count = (Number) tuple[1];
				userMap.put(status, count.intValue());
			}

			return userMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

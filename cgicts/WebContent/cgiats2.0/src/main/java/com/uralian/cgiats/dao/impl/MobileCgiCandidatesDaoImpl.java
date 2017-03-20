package com.uralian.cgiats.dao.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.MobileCgiCandidatesDao;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.MobileCgiCandidates;
import com.uralian.cgiats.model.Submittal;

@Repository
@SuppressWarnings("unchecked")
public class MobileCgiCandidatesDaoImpl extends GenericDaoImpl<MobileCgiCandidates, Integer> implements MobileCgiCandidatesDao {

	protected MobileCgiCandidatesDaoImpl() {
		super(MobileCgiCandidates.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	@Transactional(readOnly = true)
	public List<?> findCandidateJobList(Integer candidateId) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			StringBuilder hql = new StringBuilder();
			hql.append("select " + " mc, jo from MobileCgiCandidates  mc, JobOrder jo");
			hql.append(" where 1=1 and   mc.orderId.id = jo.id and mc.candidateId.id = :candidateId");
			hql.append(
					" order by case when mc.resumeStatus = 'PENDING' then coalesce(mc.updatedOn,mc.createdOn) else coalesce(jo.updatedOn  ,jo.createdOn) end desc ");
			// hql.append(" order by coalesce(jo.updatedOn,jo.createdOn) desc");
			paramNames.add("candidateId");
			paramValues.add(candidateId);
			// System.out.println("hql------->"+hql);
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
			System.out.println("result------>" + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

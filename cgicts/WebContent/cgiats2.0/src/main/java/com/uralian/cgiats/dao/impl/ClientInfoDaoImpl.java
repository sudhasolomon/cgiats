package com.uralian.cgiats.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jfree.util.Log;
import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.ClientInfoDao;
import com.uralian.cgiats.model.ClientInfo;

/*
 * Radhika
 * Created Date:8/5/2013
 * Comment: Created to store client info for the tickets ATS-
 */
@Repository
@SuppressWarnings("unchecked")
public class ClientInfoDaoImpl extends GenericDaoImpl<ClientInfo, Integer> implements ClientInfoDao {
	/**
	 */
	public ClientInfoDaoImpl() {
		super(ClientInfo.class);
	}

	/**
	 * author Radhika ATS-254 ticket
	 */
	@Override
	public List<ClientInfo> getCandidateContractPeriod(Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("from ClientInfo ci");
			hql.append(" where 1=1 and ");
			hql.append("ci.candidateinfo.candidate.id in (select c.id from Candidate c where c.deleteFlag=0) ");
			if (dateStart != null) {
				hql.append(" and ci.candidateinfo.endDate >= :startDate");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and ci.candidateinfo.endDate <= :endDate");
				paramNames.add("endDate");
				paramValues.add(DateUtils.addDays(dateEnd, 0));
			}
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
			return (List<ClientInfo>) result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

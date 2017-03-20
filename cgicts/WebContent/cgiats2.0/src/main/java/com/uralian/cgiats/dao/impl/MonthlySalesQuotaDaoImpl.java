package com.uralian.cgiats.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.MonthlySalesQuotaDao;
import com.uralian.cgiats.model.MonthlySalesQuotas;
import com.uralian.cgiats.model.UserRole;

/**
 * @author Chaitanya
 * 
 */
@Repository
@SuppressWarnings("unchecked")
public class MonthlySalesQuotaDaoImpl extends
		GenericDaoImpl<MonthlySalesQuotas, Integer> implements
		MonthlySalesQuotaDao {

	/**
	 */
	public MonthlySalesQuotaDaoImpl() {
		super(MonthlySalesQuotas.class);
	}

	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	@Override
	public Map<String, String> getDmMontlyQuotas(String dateStart,
			String dateEnd) {
try {
		List<String> paramNames = new ArrayList<String>();
		List<Object> paramValues = new ArrayList<Object>();

		StringBuilder hql = new StringBuilder();
		hql.append("select m.dmName,m.salesQuota from MonthlySalesQuotas m");
		hql.append(" where 1=1");
		if (dateStart != null) {
			hql.append(" and m.month = :startDate");
			paramNames.add("startDate");
			paramValues.add(dateStart);
		}
		if (dateEnd != null) {
			hql.append(" and m.year = :endDate");
			paramNames.add("endDate");
			paramValues.add(dateEnd);
			// paramValues.add(DateUtils.addDays(dateEnd, 1));
		}
		System.out.println("DM Quota" + hql);
		List<?> result = runQuery(hql.toString(),
				paramNames.toArray(new String[0]), paramValues.toArray());
		System.out.println("DM Result-->" + result);
		Map<String, String> dmMap = new TreeMap<String, String>();
		for (Object record : result) {
			Object[] tuple = (Object[]) record;
			String dmName = (String) tuple[0];
			String sales = (String) tuple[1];
			if (dmName != null && sales != null) {
				dmMap.put(dmName, sales);
			}

		}

		return dmMap;} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MonthlySalesQuotas> getRecSalesQuotaList(String userId,
			String month, String year) {
		List<MonthlySalesQuotas> result = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			StringBuilder sql = new StringBuilder();
			sql.append("From MonthlySalesQuotas as msq  where  1=1 ");
			sql.append(" and   createdBy='" + userId + "'");
			// params.put("userId", userId);
			sql.append(" and   month='" + month + "'");
			// params.put("month", month);
			sql.append(" and   year='" + year + "'");
			// params.put("year", year);
			sql.append(" and   userRole='" + UserRole.Recruiter + "'");
			System.out.println("sql" + sql);

			// result = findBySQLQuery(sql.toString(),params);

			result = findByQuery(sql.toString(), null);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.dao.MonthlySalesQuotaDao#getAdmSalesQuota(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<MonthlySalesQuotas> getAdmSalesQuota(String userId,
			String month, String year) {
		List<MonthlySalesQuotas> result = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			StringBuilder sql = new StringBuilder();
			sql.append("From MonthlySalesQuotas as msq  where  1=1 ");
			sql.append(" and   dmName='" + userId + "'");
			// params.put("userId", userId);
			sql.append(" and   month='" + month + "'");
			// params.put("month", month);
			sql.append(" and   year='" + year + "'");
			// params.put("year", year);
			System.out.println("sql" + sql);

			// result = findBySQLQuery(sql.toString(),params);

			result = findByQuery(sql.toString(), null);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return result;
	}
}

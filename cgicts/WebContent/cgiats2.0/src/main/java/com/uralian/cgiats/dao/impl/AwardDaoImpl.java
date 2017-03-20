/**
 * 
 */
package com.uralian.cgiats.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.AwardDao;
import com.uralian.cgiats.model.Awards;

/**
 * @author Parameshwar
 *
 */
@Repository
@SuppressWarnings("unchecked")
public class AwardDaoImpl extends GenericDaoImpl<Awards, Integer> implements AwardDao {

	/**
	 */
	public AwardDaoImpl() {
		super(Awards.class);
	}

	@Override
	public List<String> getCandidatesByUser(Date dateStart, Date dateEnd) {
		try {

			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("from Awards");
			hql.append(" where 1=1");
			if (dateStart != null) {
				hql.append(" and createdOn >= :startDate");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and createdOn <= :endDate");
				paramNames.add("endDate");
				paramValues.add(DateUtils.addDays(dateEnd, 1));
			}
			hql.append(" order by awardDate DESC");
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			System.out.println("Result::::" + result);

			return (List<String>) result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

}

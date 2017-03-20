package com.uralian.cgiats.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.OnsiteVisitsDao;
import com.uralian.cgiats.model.OnsiteVisits;

/**
 * @author Chaitanya
 *
 */
@Repository
@SuppressWarnings("unchecked")
public class OnsiteVisitsDaoImpl extends GenericDaoImpl<OnsiteVisits, Integer> implements OnsiteVisitsDao {

	/**
	 */
	public OnsiteVisitsDaoImpl() {
		super(OnsiteVisits.class);
	}

	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	@Override
	public List<OnsiteVisits> getOnsiteVisits(Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("from OnsiteVisits o");
			hql.append(" where 1=1");
			if (dateStart != null) {
				hql.append(" and o.visitDate >= :startDate");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and o.visitDate <= :endDate");
				paramNames.add("endDate");
				paramValues.add(dateEnd);
				// paramValues.add(DateUtils.addDays(dateEnd, 1));
			}

			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			/*
			 * Map<String,String> dmMap = new TreeMap<String, String>(); for
			 * (Object record : result) { Object[] tuple = (Object[]) record;
			 * String dmName = (String) tuple[0]; String sales = (String)
			 * tuple[1]; dmMap.put(dmName,sales); }
			 */
			return (List<OnsiteVisits>) result;
			// return dmMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}
}

package com.uralian.cgiats.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.IndiaJobOrdersDao;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.IndiaJobOrder;
import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;

@SuppressWarnings("unchecked")
@Repository
public class IndiaJobOrdersDaoImpl extends IndiaGenericDaoImpl<IndiaJobOrder, Integer> implements IndiaJobOrdersDao {

	public IndiaJobOrdersDaoImpl() {
		super(IndiaJobOrder.class);
	}
	
	public IndiaJobOrder findById(Integer orderId) {
		return (IndiaJobOrder) getHibernateTemplate().getSessionFactory().getCurrentSession().get(IndiaJobOrder.class, orderId);

	}

	@Override
	public List<String> listExistingTitles() {
		try {
			String hql = "select distinct o.title from IndiaJobOrder o where o.title is not null order by o.title";
			List<String> list = (List<String>) runQuery(hql.toString());
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<String> listExistingCustomers() {
		try {

			String hql = "select distinct o.customer from IndiaJobOrder o where o.customer is not null order by o.customer";
			List<String> list = (List<String>) runQuery(hql.toString());
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<String> listExistingCities() {
		try {
			String hql = "select distinct o.city from IndiaJobOrder o where o.city is not null order by o.city";
			List<String> list = (List<String>) runQuery(hql.toString());
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}
	@Override
	public IndiaJobOrder saveOrder(IndiaJobOrder jobOrder) {
		final Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		Integer jobOrderId = (Integer) session.save(jobOrder);
		return findById(jobOrderId);
	}

	@Override
	public List findSubmittalsDetails(Map<String, Object> params, String string) {
		final Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
		final Query sqlQuery = session.createSQLQuery(string);

		if (params != null) {
			for (final Map.Entry<String, Object> entry : params.entrySet()) {
				if (entry.getValue() instanceof Collection)
					sqlQuery.setParameterList(entry.getKey(), (Collection<?>) entry.getValue());
				else if (entry.getValue() instanceof Object[])
					sqlQuery.setParameterList(entry.getKey(), (Object[]) entry.getValue());
				else
					sqlQuery.setParameter(entry.getKey(), entry.getValue());

			}
		}

		List list = sqlQuery.list();
		return list;
	}

	@Override
	public Map<String, Map<JobOrderStatus, Integer>> getStatsByUser(User user, Date dateStart, Date dateEnd,
			boolean flag) {
		long startTime = System.currentTimeMillis();

		List<String> paramNames = new ArrayList<String>();
		List<Object> paramValues = new ArrayList<Object>();

		StringBuilder hql = new StringBuilder();

		hql.append("select jo.createdBy, jo.status, count(*),u.assignedBdm,u.userRole from IndiaJobOrder jo,User u");

		hql.append(" where 1=1 and u.userId=jo.createdBy  and u.status = 'ACTIVE' and jo.deleteFlag=0 ");
		if (flag == true) {
			hql.append(" and u.userRole IN('IN_DM') ");
		} else {
			hql.append(" and u.userRole NOT IN('IN_DM') ");
		}
		if (user.getUserRole().toString().equals(UserRole.Manager.toString())) {
			hql.append(" and u.officeLocation='" + user.getOfficeLocation() + "'");
		}

		if (dateStart != null) {
			hql.append(" and jo.createdOn >= :startDate");
			paramNames.add("startDate");
			paramValues.add(dateStart);
		}
		if (dateEnd != null) {
			hql.append(" and jo.createdOn <= :endDate");
			paramNames.add("endDate");
			paramValues.add(DateUtils.addDays(dateEnd, 1));
		}
		hql.append(" group by jo.createdBy, jo.status,u.assignedBdm,u.userRole");

		List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

		Map<String, Map<JobOrderStatus, Integer>> userMap = new TreeMap<String, Map<JobOrderStatus, Integer>>();

		if (user.getUserRole().equals(UserRole.IN_DM)) {
			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				String assignedBdm = (String) tuple[3];

				JobOrderStatus status = (JobOrderStatus) tuple[1];
				Number count = (Number) tuple[2];
				if (user.getUserId().equals(username) || user.getUserId().equals(assignedBdm)) {
					Map<JobOrderStatus, Integer> statusMap = userMap.get(username);
					if (statusMap == null) {
						statusMap = new HashMap<JobOrderStatus, Integer>();
					}
					userMap.put(username, statusMap);
					statusMap.put(status, count != null ? count.intValue() : 0);
				}
			}
			/*} else if (user.getUserRole().equals(UserRole.ADM)) {
			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				String assignedBdm = (String) tuple[3];

				JobOrderStatus status = (JobOrderStatus) tuple[1];
				Number count = (Number) tuple[2];
				if (user.getUserId().equals(username) || user.getAssignedBdm().equals(username)) {
					Map<JobOrderStatus, Integer> statusMap = userMap.get(username);
					if (statusMap == null) {
						statusMap = new HashMap<JobOrderStatus, Integer>();
					}
					userMap.put(username, statusMap);
					statusMap.put(status, count != null ? count.intValue() : 0);
				}
			}*/
		} else if (user.getUserRole().equals(UserRole.IN_Recruiter)) {
			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				String assignedBdm = (String) tuple[3];

				JobOrderStatus status = (JobOrderStatus) tuple[1];
				Number count = (Number) tuple[2];
				if (user.getAssignedBdm().equals(username) || user.getAssignedBdm().equals(assignedBdm)) {
					Map<JobOrderStatus, Integer> statusMap = userMap.get(username);
					if (statusMap == null) {
						statusMap = new HashMap<JobOrderStatus, Integer>();
					}
					userMap.put(username, statusMap);
					statusMap.put(status, count != null ? count.intValue() : 0);
				}
			}
		} else {
			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				String assignedBdm = (String) tuple[3];

				JobOrderStatus status = (JobOrderStatus) tuple[1];
				Number count = (Number) tuple[2];
				Map<JobOrderStatus, Integer> statusMap = userMap.get(username);
				if (statusMap == null) {
					statusMap = new HashMap<JobOrderStatus, Integer>();
				}
				userMap.put(username, statusMap);
				statusMap.put(status, count != null ? count.intValue() : 0);
			}
		}
		long endTime = System.currentTimeMillis();
		return userMap;
	}

	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getIndiaSubmittalStatusByLocation(UserDto userDto, String officeLoc, Date dateStart, Date dateEnd) {
		long startTime = System.currentTimeMillis();

		List<String> paramNames = new ArrayList<String>();
		List<Object> paramValues = new ArrayList<Object>();

		StringBuilder hql = new StringBuilder();
		hql.append("select s.createdBy, s.status, count(s) from User u,IndiaSubmittal s");
		hql.append(" where 1=1 and s.deleteFlag!=1  and s.createdBy=u.userId and u.userRole='IN_Recruiter'");

		if (officeLoc != null && officeLoc.trim().length() != 0) {
			hql.append(" and u.officeLocation=:loc  ");
			paramNames.add("loc");
			paramValues.add(officeLoc);

			System.out.println("office location is:::::::::::::" + officeLoc);
		}
		if (userDto.getUserRole().toString().equals(UserRole.Manager.toString())) {
			hql.append(" and u.officeLocation='" + userDto.getOfficeLocation() + "'");
		}

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

		hql.append(" group by s. createdBy, s.status");
		System.out.println("hql>>" + hql);
		List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

		System.out.println("List size is:::::::" + result.size());

		Map<String, Map<SubmittalStatus, Integer>> userMap = new TreeMap<String, Map<SubmittalStatus, Integer>>();
		for (Object record : result) {
			Object[] tuple = (Object[]) record;
			String username = (String) tuple[0];
			SubmittalStatus status = (SubmittalStatus) tuple[1];
			Number count = (Number) tuple[2];
			if (username != null) {
				Map<SubmittalStatus, Integer> statusMap = userMap.get(username);
				if (statusMap == null) {
					statusMap = new HashMap<SubmittalStatus, Integer>();
					userMap.put(username, statusMap);
				}
				statusMap.put(status, count != null ? count.intValue() : 0);
			}
		}
		long endTime = System.currentTimeMillis();
		log.info("getSubmittalStatusByUser() in joborderdaoimpl in milliseconds" + String.valueOf(endTime - startTime));
		return userMap;
		// return null;
	}

	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getClientwiseSubmittalStats(Date strDate, Date endDate) {
		long startTime = System.currentTimeMillis();

		List<String> paramNames = new ArrayList<String>();
		List<Object> paramValues = new ArrayList<Object>();

		StringBuilder hql = new StringBuilder();
		hql.append("select s.status,count(s),jo.customer from IndiaSubmittal s,IndiaJobOrder jo where jo.id=s.jobOrder.id and s.createdOn>= :startDate and s.createdOn< :endDate");
		hql.append(" and s.deleteFlag!=1 group by s.status,jo.customer");
		
		paramNames.add("startDate");
		paramValues.add(strDate);
		
		paramNames.add("endDate");
		paramValues.add(endDate);
		System.out.println("hql>>" + hql);
		List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

		System.out.println("List size is:::::::" + result.size());

		Map<String, Map<SubmittalStatus, Integer>> userMap = new TreeMap<String, Map<SubmittalStatus, Integer>>();
		for (Object record : result) {
			Object[] tuple = (Object[]) record;
			String username = (String) tuple[2];
			SubmittalStatus status = (SubmittalStatus) tuple[0];
			Number count = (Number) tuple[1];
			if (username != null) {
				Map<SubmittalStatus, Integer> statusMap = userMap.get(username);
				if (statusMap == null) {
					statusMap = new HashMap<SubmittalStatus, Integer>();
					userMap.put(username, statusMap);
				}
				statusMap.put(status, count != null ? count.intValue() : 0);
			}
		}
		long endTime = System.currentTimeMillis();
		log.info("getSubmittalStatusByUser() in joborderdaoimpl in milliseconds" + String.valueOf(endTime - startTime));
		return userMap;
	}

	@Override
	public Map<JobOrderStatus, Integer> getAllJobOrdersCounts(Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			StringBuilder hql = new StringBuilder();
			hql.append(
					"select CASE when j.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN 'OPEN' ELSE 'CLOSE' END as STATUS,count(*) as Count from IndiaJobOrder j,User u");
			hql.append(" where u.userId=coalesce(j.dmName,j.createdBy)  and j.deleteFlag=0 ");
			if (dateStart != null) {
				hql.append(" and cast(j.createdOn as date) >=  cast(:startDate as date)");
				// hql.append(" and COALESCE(cast(j.updatedOn as
				// date),cast(j.createdOn as date)) >= cast(:startDate as
				// date)");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and cast(j.createdOn as date) <= cast(:endDate as date)");
				// hql.append(" and COALESCE(cast(j.updatedOn as
				// date),cast(j.createdOn as date)) >= cast(:endDate as date)");
				paramNames.add("endDate");
				paramValues.add(dateEnd);
			}
			hql.append(" group by CASE when j.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN 'OPEN' ELSE 'CLOSE' END ");
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
			System.out.println("hql----------->" + hql);
			Map<JobOrderStatus, Integer> userMap = new TreeMap<JobOrderStatus, Integer>();
			for (Object record : result) {
				JobOrderStatus status = null;
				Object[] tuple = (Object[]) record;
				if (tuple[0].toString().equalsIgnoreCase("OPEN"))
					status = JobOrderStatus.OPEN;
				else
					status = JobOrderStatus.CLOSED;
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

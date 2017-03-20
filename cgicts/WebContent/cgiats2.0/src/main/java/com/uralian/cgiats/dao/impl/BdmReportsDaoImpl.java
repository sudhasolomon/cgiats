package com.uralian.cgiats.dao.impl;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.BdmReportsDao;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;

@Repository
@SuppressWarnings("unchecked")
public class BdmReportsDaoImpl extends GenericDaoImpl<Object, Serializable> implements BdmReportsDao {

	public BdmReportsDaoImpl() {
		super(Object.class);
	}

	@Override
	public List<Map<String, Object>> fetchBdmsReports(Date reportStartDate, Date reportEndDate, User user) {
		try {
			StringBuffer jobssql = new StringBuffer();
			StringBuffer sql = new StringBuffer();
			List<Map<String, Object>> jobRecordsList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> submittalRecList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> recordsList = new ArrayList<Map<String, Object>>();

			// Fetching Joborder list
			jobssql.append("select a.created_by,a.open_count,a.closed_count " + " from " + " (select jo.created_by, "
					+ " count( CASE when jo.status in ('OPEN','ASSIGNED','REOPEN') THEN 1 END)as open_count, "
					+ " count(  CASE when jo.status in ('CLOSED','FILLED') THEN 1 END)as closed_count from job_order jo, user_acct u where 1=1 "
					+ " AND u.user_role IN ('DM') and u.user_id = jo.created_by ");

			if (reportStartDate != null && reportEndDate != null) {
				jobssql.append(" and COALESCE(date(jo.created_on))  between :reportStartDate and :reportEndDate");
			}
			if (user.getUserRole().toString().equals(UserRole.Manager.toString())) {
				jobssql.append("  and u.office_location='" + user.getOfficeLocation() + "'");
			}
			if (user != null && user.isDM()) {
				jobssql.append(" and u.user_id =:userId");
			}

			jobssql.append(" and jo.delete_flg = 0 group by jo.created_by) as a ");

			log.info("sql" + jobssql.toString());
			List<?> jobList = findBySQLQuery(jobssql.toString(), reportStartDate, reportEndDate, user);
			Map<String, Map<String, Object>> jobRecordsMap = new HashMap<String, Map<String, Object>>();
			for (Object obj : jobList) {
				Object[] tuple = (Object[]) obj;

				Object dmName = tuple[0];
				Object openJobCount = tuple[1];
				Object closedJobCount = tuple[2];

				Map<String, Object> record = new HashMap<String, Object>();
				record.put("userId", dmName);
				record.put("openJobCount", openJobCount);
				record.put("closedJobCount", closedJobCount);
				jobRecordsMap.put((String) dmName, record);
				jobRecordsList.add(record);
				System.out.println("jobRecordsList--------------->" + jobRecordsList);
			}
			// End of Fetching Joborder list

			// Fetching Submittals list

			sql.append(" select jo.created_by,count(s.submittal_id) as submittal_count," + " count(CASE when  s.status='STARTED' THEN 1 END)as started_count,"
					+ " count(CASE when  s.status = 'INTERVIEWING' THEN 1 END)as inteviewing_count,"
					+ " count(CASE when  s.status='CONFIRMED' THEN 1 END)as confirmed_count from submittal s,job_order jo where 1=1 ");

			if (reportStartDate != null) {
				sql.append(" and COALESCE(cast(s.updated_on as date),cast(s.created_on as date)) >=  cast(:reportStartDate as date)");
			}
			if (reportEndDate != null) {
				sql.append(" and COALESCE(cast(s.updated_on as date),cast(s.created_on as date)) <= cast(:reportEndDate as date)");
			}
			sql.append(
					" and  s.order_id = jo.order_id and s.delete_flag = 0 and jo.delete_flg = 0 and  jo.created_by  in ( select u.user_id from user_acct u where U.user_role IN ('DM') and jo.delete_flg=0 and u.user_id = jo.created_by  ");

			if (user != null && user.isDM()) {
				sql.append(" and u.user_id =:userId");
			}

			if (user.getUserRole().toString().equals(UserRole.Manager.toString())) {
				sql.append("  and u.office_location='" + user.getOfficeLocation() + "'");
			}
			sql.append(" ) group by jo.created_by order by jo.created_by asc");

			log.info("Query" + sql.toString());
			List<?> list = findBySQLQuery(sql.toString(), reportStartDate, reportEndDate, user);

			for (Object obj : list) {
				Object[] tuple = (Object[]) obj;

				Object dmName = tuple[0];
				Object totalSubmittals = tuple[1];
				Object startedSubmittals = tuple[2];
				Object interviewingSubmittals = tuple[3];
				Object confirmedSubmittals = tuple[4];

				Map<String, Object> record = new HashMap<String, Object>();
				record.put("userId", dmName);
				record.put("totalSubmittals", totalSubmittals);
				record.put("startedSubmittals", startedSubmittals);
				record.put("interviewingSubmittals", interviewingSubmittals);
				record.put("confirmedSubmittals", confirmedSubmittals);
				submittalRecList.add(record);

			}

			List<String> dmNames = new ArrayList<String>();
			// jobRecordsMap
			if (submittalRecList != null && submittalRecList.size() > 0) {
				for (Map obj : submittalRecList) {
					String userId = (String) obj.get("userId");
					dmNames.add(userId);
					Map<String, Object> record = new HashMap<String, Object>();
					if (userId != null && jobRecordsMap != null && jobRecordsMap.containsKey(userId)) {
						record = jobRecordsMap.get(userId);
						obj.put("openJobCount", record.get("openJobCount"));
						obj.put("closedJobCount", record.get("closedJobCount"));
						recordsList.add(obj);
					} else {
						obj.put("openJobCount", 0);
						obj.put("closedJobCount", 0);
						recordsList.add(obj);
					}
				}
			}
			if (jobRecordsMap != null && jobRecordsMap.size() > 0) {
				for (String key : jobRecordsMap.keySet()) {
					Map<String, Object> record = new HashMap<String, Object>();
					if (!dmNames.contains(key)) {
						record = jobRecordsMap.get(key);
						record.put("totalSubmittals", 0);
						record.put("startedSubmittals", 0);
						record.put("interviewingSubmittals", 0);
						record.put("confirmedSubmittals", 0);
						recordsList.add(record);
					}
				}
			}

			return recordsList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> fetchAdmsReports(Date startDate, Date endDate, User user) {
		try {
			StringBuffer jobssql = new StringBuffer();
			StringBuffer sql = new StringBuffer();
			List<Map<String, Object>> jobRecordsList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> submittalRecList = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> recordsList = new ArrayList<Map<String, Object>>();
			// Fetching Joborder list
			jobssql.append("select a.created_by,a.open_count,a.closed_count " + " from " + " (select jo.created_by, "
					+ " count( CASE when jo.status in ('OPEN','ASSIGNED','REOPEN') THEN 1 END)as open_count, "
					+ " count(  CASE when jo.status in ('CLOSED','FILLED') THEN 1 END)as closed_count from job_order jo,user_acct u where 1=1 "
					+ " AND u.user_role IN ('ADM') and u.user_id = jo.created_by");

			if (startDate != null && endDate != null) {
				jobssql.append(" and COALESCE(date(jo.created_on))  between :reportStartDate and :reportEndDate");
			}
			if (user != null && user.isADM()) {
				jobssql.append(" and u.user_id=:userId");
			} else if (user != null && user.isDM()) {
				jobssql.append(" and u.assigned_bdm =:userId");

			}
			if (user.getUserRole().toString().equals(UserRole.Manager.toString())) {
				jobssql.append("  and u.office_location='" + user.getOfficeLocation() + "'");
			}
			jobssql.append(" and jo.delete_flg = 0 group by jo.created_by) as a ");

			List<?> jobList = findBySQLQuery(jobssql.toString(), startDate, endDate, user);
			Map<String, Map<String, Object>> jobRecordsMap = new HashMap<String, Map<String, Object>>();
			for (Object obj : jobList) {
				Object[] tuple = (Object[]) obj;

				Object admName = tuple[0];
				Object openJobCount = tuple[1];
				Object closedJobCount = tuple[2];

				Map<String, Object> record = new HashMap<String, Object>();
				record.put("userId", admName);
				record.put("openJobCount", openJobCount);
				record.put("closedJobCount", closedJobCount);
				jobRecordsMap.put((String) admName, record);
				jobRecordsList.add(record);
			}
			// End of Fetching Joborder list

			// Fetching Submittals list

			sql.append(" select jo.created_by,count(s.submittal_id) as submittal_count," + " count(CASE when  s.status='STARTED' THEN 1 END)as started_count,"
					+ " count(CASE when  s.status = 'INTERVIEWING' THEN 1 END)as inteviewing_count,"
					+ " count(CASE when  s.status='CONFIRMED' THEN 1 END)as confirmed_count from submittal s,job_order jo where 1=1 ");

			if (startDate != null) {
				sql.append(" and COALESCE(cast(s.updated_on as date),cast(s.created_on as date)) >=  cast(:reportStartDate as date)");
			}
			if (endDate != null) {
				sql.append(" and COALESCE(cast(s.updated_on as date),cast(s.created_on as date)) <= cast(:reportEndDate as date)");
			}
			sql.append(
					"  and s.order_id = jo.order_id and s.delete_flag = 0 and jo.delete_flg = 0 and  jo.created_by  in ( select u.user_id from user_acct u where U.user_role IN ('ADM') and jo.delete_flg=0 and u.user_id = jo.created_by");

			if (user != null && user.isADM()) {
				sql.append(" and u.user_id=:userId");

			}

			else if (user != null && user.isDM()) {
				sql.append(" and u.assigned_bdm =:userId");
			}

			if (user.getUserRole().toString().equals(UserRole.Manager.toString())) {
				sql.append("  and u.office_location='" + user.getOfficeLocation() + "'");
			}
			sql.append(" ) group by jo.created_by order by jo.created_by asc");

			List<?> list = findBySQLQuery(sql.toString(), startDate, endDate, user);

			for (Object obj : list) {
				Object[] tuple = (Object[]) obj;

				Object admName = tuple[0];
				Object totalSubmittals = tuple[1];
				Object startedSubmittals = tuple[2];
				Object interviewingSubmittals = tuple[3];
				Object confirmedSubmittals = tuple[4];

				Map<String, Object> record = new HashMap<String, Object>();
				record.put("userId", admName);
				record.put("totalSubmittals", totalSubmittals);
				record.put("startedSubmittals", startedSubmittals);
				record.put("interviewingSubmittals", interviewingSubmittals);
				record.put("confirmedSubmittals", confirmedSubmittals);
				submittalRecList.add(record);

			}

			List<String> admNames = new ArrayList<String>();
			// jobRecordsMap
			if (submittalRecList != null && submittalRecList.size() > 0) {
				for (Map obj : submittalRecList) {
					String userId = (String) obj.get("userId");
					admNames.add(userId);
					Map<String, Object> record = new HashMap<String, Object>();
					if (userId != null && jobRecordsMap != null && jobRecordsMap.containsKey(userId)) {
						record = jobRecordsMap.get(userId);
						obj.put("openJobCount", record.get("openJobCount"));
						obj.put("closedJobCount", record.get("closedJobCount"));
						recordsList.add(obj);
					} else {
						obj.put("openJobCount", 0);
						obj.put("closedJobCount", 0);
						recordsList.add(obj);
					}
				}
			}
			if (jobRecordsMap != null && jobRecordsMap.size() > 0) {
				for (String key : jobRecordsMap.keySet()) {
					Map<String, Object> record = new HashMap<String, Object>();
					if (!admNames.contains(key)) {
						record = jobRecordsMap.get(key);
						record.put("totalSubmittals", 0);
						record.put("startedSubmittals", 0);
						record.put("interviewingSubmittals", 0);
						record.put("confirmedSubmittals", 0);
						recordsList.add(record);
					}
				}
			}

			return recordsList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private List<?> findBySQLQuery(final String query, final Date reportStartDate, final Date reportEndDate, final User user) {
		//
		// @SuppressWarnings("rawtypes")
		// List<T> entities = getHibernateTemplate().executeFind(
		// new HibernateCallback() {
		// public Object doInHibernate(Session session)
		// throws HibernateException, SQLException {
		// Query queryObject = session.createSQLQuery(query);
		// if (reportStartDate != null ) {
		// queryObject.setParameter("reportStartDate",
		// reportStartDate);
		// }
		// if (reportEndDate != null) {
		// queryObject.setParameter("reportEndDate",
		// reportEndDate);
		// }
		// if (user != null && (user.isDM() || user.isADM())) {
		// queryObject.setParameter("userId", user.getUserId());
		// }
		//
		//
		//
		// return queryObject.list();
		// }
		// });
		// log.debug(entities.size() + " object(s) retrieved by query");
		try {
			Query qry = getHibernateTemplate().getSessionFactory().getCurrentSession().createSQLQuery(query.toString());
			if (reportStartDate != null) {
				qry.setParameter("reportStartDate", reportStartDate);
			}
			if (reportEndDate != null) {
				qry.setParameter("reportEndDate", reportEndDate);
			}
			if (user != null && (user.isDM() || user.isADM())) {
				qry.setParameter("userId", user.getUserId());
			}

			return qry.list();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

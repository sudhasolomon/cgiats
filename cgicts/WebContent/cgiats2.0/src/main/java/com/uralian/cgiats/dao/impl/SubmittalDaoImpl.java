/*
 * SubmittalDaoImpl.java Jun 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.dao.impl;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.SubmittalDao;
import com.uralian.cgiats.dto.CandidateDto;
import com.uralian.cgiats.model.Address;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.SubmitalStats;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalEvent;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.rest.UserRoleVo;
import com.uralian.cgiats.util.Utils;

/**
 * @author Parameshwar
 * 
 */
@Repository
@SuppressWarnings("unchecked")
public class SubmittalDaoImpl extends GenericDaoImpl<Submittal, Integer> implements SubmittalDao {
	/**
	 */
	public SubmittalDaoImpl() {
		super(Submittal.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.dao.SubmittalDao#getSubmittalStatusByBdm(java.lang
	 * .String, java.util.Date, java.util.Date)
	 */
	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByBdm(String location, User bdmName, Date dateStart, Date dateEnd) {
		long startTime = System.currentTimeMillis();
		Map<String, Map<SubmittalStatus, Integer>> userMap = new TreeMap<String, Map<SubmittalStatus, Integer>>();
		// log.info("DashBoardBean init() start :"+startTime);
		try {
			/*
			 * List<String> paramNames = new ArrayList<String>(); List<Object>
			 * paramValues = new ArrayList<Object>();
			 */

			List<?> result = null;

			StringBuilder hql = new StringBuilder();
			/*
			 * hql.append(
			 * "select s.createdBy, s.status, count(*) from Submittal s,User u"
			 * ); hql.append(
			 * " where 1=1 and s.deleteFlag!=1 and u.userId = s.createdBy and (u.assignedBdm=:bdmName or s.createdBy=:bdmName)"
			 * ); paramNames.add("bdmName");
			 * paramValues.add(bdmName.getUserId()); if (dateStart != null) {
			 * hql.append(
			 * " and COALESCE(cast(s.createdDate as date),cast(s.createdOn as date)) >=  cast(:startDate as date)"
			 * ); paramNames.add("startDate"); paramValues.add(dateStart); } if
			 * (dateEnd != null) { hql.append(
			 * " and COALESCE(cast(s.createdDate as date),cast(s.createdOn as date)) <= cast(:endDate as date)"
			 * ); //as per ken modified (Should it always use the created date
			 * when we select by date?) paramNames.add("endDate");
			 * paramValues.add(dateEnd); } hql.append(
			 * " group by s.createdBy, s.status order by s.createdBy" );
			 * 
			 * List<?> result = runQuery(hql.toString(), paramNames.toArray(new
			 * String[0]), paramValues.toArray());
			 */

			hql.append("select s.created_by, s.status, count(*) from submittal s,user_acct u  where" + "	s.delete_flag!=1 and  s.created_by=u.user_id  and"
					+ "	((s.created_by=? or u.assigned_bdm=? ) or" + "	(s.created_by in (select u1.user_id from user_acct u1 where u1.assigned_bdm in "
					+ "(select u2.user_id from user_acct u2 where u2.assigned_bdm=? ))))" + " ");

			/*
			 * hql.append(
			 * "select s.created_by, s.status, count(*) from submittal s where s.delete_flag=0 AND s.order_id "
			 * +
			 * " in (select order_id from job_order j  where j.created_by= ?  )"
			 * );
			 */

			if (dateStart != null) {
				hql.append(" and COALESCE(cast(s.updated_on as date),cast(s.created_on as date)) >=  cast(? as date)");
			}
			if (dateEnd != null) {
				hql.append(" and COALESCE(cast(s.updated_on as date),cast(s.created_on as date)) <= cast(? as date)"); // as
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
			}
			if (location != null && location.length() > 0) {
				hql.append(" and u.OFFICE_LOCATION='" + location + "'");
			} else {
				log.info("from else");
			}
			hql.append("	group by s.created_by, s.status order by s.created_by");

			if (bdmName != null && bdmName.getUserRole().equals(UserRole.DM)) {
				result = runSQLQueryForDM(hql.toString(), dateStart, dateEnd, bdmName.getUserId());
			} else if (bdmName != null && bdmName.getUserRole().equals(UserRole.ADM))
				result = runSQLQueryForDM(hql.toString(), dateStart, dateEnd, bdmName.getAssignedBdm());

			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				/* SubmittalStatus status = (SubmittalStatus) tuple[1]; */
				SubmittalStatus status = SubmittalStatus.valueOf(tuple[1].toString());
				Number count = (Number) tuple[2];

				Map<SubmittalStatus, Integer> statusMap = userMap.get(username);
				if (statusMap == null) {
					statusMap = new HashMap<SubmittalStatus, Integer>();
					userMap.put(username, statusMap);
				}
				statusMap.put(status, count != null ? count.intValue() : 0);
			}
			long endTime = System.currentTimeMillis();
			// log.info("DashBoardBean init() end :"+endTime);
			log.info("getSubmittalStatusByBdm() in SubmittalDaoImpl in milliseconds" + String.valueOf(endTime - startTime));
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return userMap;
	}

	@Transactional(readOnly = true, propagation = MANDATORY)
	private List<?> runSQLQueryForDM(final String string, final Date dateStart, final Date dateEnd, final String userId) {
		try {
			@SuppressWarnings("rawtypes")
			List<Object> entities = (List<Object>) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					Query queryObject = session.createSQLQuery(string);

					if (userId != null) {
						queryObject.setParameter(0, userId);
						queryObject.setParameter(1, userId);
						queryObject.setParameter(2, userId);
					}
					if (dateStart != null) {
						queryObject.setParameter(3, dateStart);
					}
					if (dateEnd != null) {
						queryObject.setParameter(4, dateEnd);
					}

					return queryObject.list();

				}
			});
			log.debug(entities.size() + " object(s) retrieved by query");
			return entities;
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
	 * com.uralian.cgiats.dao.SubmittalDao#getSubmittalStatusByBdm(java.lang
	 * .String, java.util.Date, java.util.Date)
	 */
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByDm(User bdmName, Date dateStart, Date dateEnd) {
		Map<String, Map<SubmittalStatus, Integer>> userMap = new TreeMap<String, Map<SubmittalStatus, Integer>>();
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("select s.created_by, s.status, count(*) from submittal s  where s.delete_flag=0");
			if (dateStart != null && dateEnd != null) {
				hql.append(" and COALESCE(s.updated_on,s.created_on)>= ? and COALESCE(s.updated_on,s.created_on)<= ?");
			}
			hql.append(
					" and s.created_by in(select us.user_id from user_acct us where us.assigned_bdm= ? or us.user_id= ? UNION select ua.user_id from user_acct ua where ua.assigned_bdm IN (select user_id from user_acct where assigned_bdm=? and user_role='ADM'))");
			hql.append(" group by s.created_by, s.status order by s.created_by");
			// raghavendra
			/*
			 * hql.append(
			 * " and date(s.created_on)>= ? and date(s.created_on)<= ?" ); }
			 * hql.append(
			 * " and s.order_id in(select order_id   from job_order  where created_by in (select us.user_id from user_acct us where us.assigned_bdm= ? or us.user_id= ? UNION select ua.user_id from user_acct ua where ua.assigned_bdm IN (select user_id from user_acct where assigned_bdm=? and user_role='ADM')))"
			 * ); hql.append(
			 * " group by s.created_by, s.status order by s.created_by" );
			 */
			String userId = null;
			if (bdmName.getUserId() != null)
				userId = bdmName.getUserId();
			List<?> result = findBySqlQueryAdm(hql.toString(), dateStart, dateEnd, userId);
			System.out.println("result>>>" + result);

			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				SubmittalStatus status = SubmittalStatus.valueOf(tuple[1].toString());
				Number count = (Number) tuple[2];
				System.out.println("username::" + username);
				System.out.println("count::" + count);

				Map<SubmittalStatus, Integer> statusMap = userMap.get(username);
				if (statusMap == null) {
					statusMap = new HashMap<SubmittalStatus, Integer>();
					userMap.put(username, statusMap);
				}
				statusMap.put(status, count != null ? count.intValue() : 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		return userMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.dao.SubmittalDao#getSubmittalStatusByBdm(java.lang
	 * .String, java.util.Date, java.util.Date)
	 */
	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByRecruiter(User bdmName, Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select s.createdBy, s.status, count(*) from Submittal s,User u");
			hql.append(" where 1=1 and s.deleteFlag!=1 and s.createdBy=:bdmName and u.userId = s.createdBy ");
			paramNames.add("bdmName");
			paramValues.add(bdmName.getUserId());
			log.info("bdmName.getUserId() in dao>>" + bdmName.getUserId());
			if (dateStart != null) {
				hql.append(" and COALESCE(cast(s.createdDate as date),cast(s.createdOn as date)) >=  cast(:startDate as date)");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and COALESCE(cast(s.createdDate as date),cast(s.createdOn as date)) <= cast(:endDate as date)"); // as
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
			hql.append(" group by s.createdBy, s.status");
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			Map<String, Map<SubmittalStatus, Integer>> userMap = new TreeMap<String, Map<SubmittalStatus, Integer>>();
			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				SubmittalStatus status = (SubmittalStatus) tuple[1];
				Number count = (Number) tuple[2];
				Map<SubmittalStatus, Integer> statusMap = userMap.get(username);
				if (statusMap == null) {
					statusMap = new HashMap<SubmittalStatus, Integer>();
					userMap.put(username, statusMap);
				}
				statusMap.put(status, count != null ? count.intValue() : 0);
			}

			return userMap;
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
	 * com.uralian.cgiats.dao.SubmittalDao#getSubmittalStatusByBdm(java.lang
	 * .String, java.util.Date, java.util.Date)
	 */
	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByRecruiterDM(String selectedLocation, User bdmName, Date dateStart, Date dateEnd) {
		try {
			long startTime = System.currentTimeMillis();

			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select s.createdBy, s.status, count(*) from Submittal s,User u");
			hql.append(" where 1=1 and s.deleteFlag!=1 and  u.userId = s.createdBy and (u.assignedBdm=:dmName or s.createdBy=:dmName or s.createdBy=:bdmName)");
			paramNames.add("bdmName");
			paramValues.add(bdmName.getUserId());
			paramNames.add("dmName");
			paramValues.add(bdmName.getAssignedBdm());
			log.info("bdmName.getUserId() in dao>>" + bdmName.getUserId());
			if (dateStart != null) {
				hql.append(" and COALESCE(cast(s.createdDate as date),cast(s.createdOn as date)) >=  cast(:startDate as date)");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and COALESCE(cast(s.createdDate as date),cast(s.createdOn as date)) <= cast(:endDate as date)"); // as
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
			if (selectedLocation != null && selectedLocation.length() > 0) {
				hql.append(" and u.officeLocation='" + selectedLocation + "'");
			}
			hql.append(" group by s.createdBy, s.status");
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			Map<String, Map<SubmittalStatus, Integer>> userMap = new TreeMap<String, Map<SubmittalStatus, Integer>>();
			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				SubmittalStatus status = (SubmittalStatus) tuple[1];
				Number count = (Number) tuple[2];
				Map<SubmittalStatus, Integer> statusMap = userMap.get(username);
				if (statusMap == null) {
					statusMap = new HashMap<SubmittalStatus, Integer>();
					userMap.put(username, statusMap);
				}
				statusMap.put(status, count != null ? count.intValue() : 0);
			}
			long endTime = System.currentTimeMillis();
			log.info("getSubmittalStatusByRecruiterDM() in submittaldaoimpl in milliseconds" + String.valueOf(endTime - startTime));
			return userMap;
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
	 * com.uralian.cgiats.service.SubmittalService#getAllStarterdCandidates()
	 */

	@Override
	@Transactional(readOnly = true)
	public List<Submittal> getSubmittalDetailsByStatus(SubmittalStatus status, Date dateStart, Date dateEnd, String createdUser, String loginUser) {
		try {
			StringBuffer hql = new StringBuffer();
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			// if (!status.equals(SubmittalStatus.SUBMITTED)) {
			hql.append("select s from Submittal s,Candidate c where s.status =:status and s.createdBy =:createdUser ");
			paramNames.add("status");
			paramValues.add(status);
			// } else {
			// hql.append("select s from Submittal s,Candidate c where s.status
			// IN('SUBMITTED', 'DMREJ','ACCEPTED', 'INTERVIEWING', 'CONFIRMED',
			// 'REJECTED', 'STARTED', 'BACKOUT', 'OUTOFPROJ') and s.createdBy
			// =:createdUser");
			// }
			hql.append(" and s.candidate.id=c.id and s.deleteFlag=0 ");
			if (dateStart != null) {
				hql.append(" and COALESCE(cast(s.createdOn as date)) >=  cast(:startDate as date)");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and COALESCE(cast(s.createdOn as date)) <= cast(:endDate as date)");
				paramNames.add("endDate");
				paramValues.add(dateEnd);
			}
			paramNames.add("createdUser");
			paramValues.add(createdUser);

			if (loginUser != null && loginUser.length() > 0)
				hql.append(" and s.jobOrder.createdBy='" + loginUser + "'");

			hql.append(" ORDER By s.candidate");
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
			return (List<Submittal>) result;
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
	 * com.uralian.cgiats.dao.SubmittalDao#getAllStarterdCandidates(java.util
	 * .Date)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Submittal> getAllStarterdCandidates(Date currentDate) {
		try {
			StringBuffer hql = new StringBuffer();
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			hql.append("select s from Submittal s,Candidate c where ");
			hql.append("s.candidate.id=c.id and c.deleteFlag=0 and s.deleteFlag=0 and s.status='STARTED'");
			if (currentDate != null) {
				hql.append(" and s.createdDate <= :currentDate");
				paramNames.add("currentDate");
				paramValues.add(currentDate);
			}
			hql.append(" ORDER By s.candidate");
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
			return (List<Submittal>) result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

	public List<Submittal> getAllOutofProjCandidates(Date fromDate, Date toDate) {
		try {

			StringBuffer hql = new StringBuffer();
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			hql.append("select s from Submittal s,Candidate c where ");
			hql.append("s.candidate.id=c.id and c.deleteFlag=0 and s.status='OUTOFPROJ'");
			/*
			 * hql.append(
			 * "GROUP By s.id,s.candidate.id,s HAVING s.id=(select max(s1.id) from Submittal s1  where s1.candidate.id=s.candidate.id ) "
			 * );
			 */
			/*
			 * hql.append(
			 * "and s.id=(select max(s1.id) from Submittal s1  where s1.candidate.id=s.candidate.id )"
			 * );
			 */
			if (fromDate != null) {
				hql.append(" and COALESCE(cast(s.createdDate as date),cast(s.createdOn as date)) >=:fromDate");
				paramNames.add("fromDate");
				paramValues.add(fromDate);
			}
			if (toDate != null) {
				hql.append(" and COALESCE(cast(s.createdDate as date),cast(s.createdOn as date)) <=:toDate ");
				paramNames.add("toDate");
				paramValues.add(toDate);
			}
			hql.append(" ORDER By s.id DESC");
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
			return (List<Submittal>) result;
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
	 * com.uralian.cgiats.dao.SubmittalDao#editSubmittalEvent(com.uralian.cgiats
	 * .model.SubmittalEvent)
	 */
	@Override
	@Transactional(readOnly = true)
	public Integer editSubmittalEvent(SubmittalEvent sEvent) {
		try {
			Integer result = 0;
			StringBuffer hql = new StringBuffer();
			hql.append("update SubmittalEvent set updatedOn=:updatedOn, updatedBy=:updatedBy where id=:eventId");
			Query query = getHibernateTemplate().getSessionFactory().openSession().createQuery(hql.toString());
			query.setInteger("eventId", sEvent.getId());
			query.setString("updatedBy", sEvent.getUpdatedBy());
			query.setDate("updatedOn", sEvent.getUpdatedOn());
			try {
				result = query.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<SubmittalStatus, Integer> getAllSubmittalStatusCounts(Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select s.status, count(*) from Submittal s");
			hql.append(" where 1=1 and s.deleteFlag=0");
			if (dateStart != null) {
				hql.append(" and COALESCE(cast(s.createdDate as date),cast(s.createdOn as date)) >=  cast(:startDate as date)");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and COALESCE(cast(s.createdDate as date),cast(s.createdOn as date)) <= cast(:endDate as date)"); // as
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

	@Override
	public List<Submittal> getCandidateJobrderStatus(Integer candidateId) {
		try {
			StringBuffer hql = new StringBuffer();

			hql.append("from Submittal s  where ");
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

	public List<Submittal> mobileSubmittalStatus(Integer jobId, Integer candidateId) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select  s from Submittal s, JobOrder jobOrder , Candidate c ");
			sql.append(" where 1=1 and   s.jobOrder = jobOrder.id and s.candidate = c.id and  jobOrder.id =:jobId and c.id = :candidateId ");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("jobId", jobId);
			params.put("candidateId", candidateId);
			List<?> subCandidateStatus = findByQuery(sql.toString(), params);
			return (List<Submittal>) subCandidateStatus;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

	// Mobile Application

	public SubmitalStats executiveTodaySubmittalStatus(UserRoleVo userRoleVo) {
		try {
			StringBuilder sql = new StringBuilder();

			sql.append("select sts.submitted_count,sts.dmrej_count,sts.accepted_count,sts.inteviewing_count,sts.confirmed_count,"
					+ "sts.rejected_count,sts.started_count,sts.backout_count,sts.outofprj_count from submittal_status_day_view sts ");
			if (userRoleVo != null) {
				sql.append(" where sts.dm='" + userRoleVo.getUserName() + "'");
			}
			sql.append(" group BY sts.submitted_count,sts.dmrej_count,sts.accepted_count,sts.inteviewing_count,sts.confirmed_count,"
					+ "sts.rejected_count,sts.started_count,sts.backout_count,sts.outofprj_count ");

			List<?> result = findBySQLQuery(sql.toString(), null);
			SubmitalStats mdo = new SubmitalStats();
			for (Object obj : result) {
				mdo = new SubmitalStats();
				Object[] tuple = (Object[]) obj;

				Object str1 = tuple[0];
				Object str2 = tuple[1];
				Object str3 = tuple[2];
				Object str4 = tuple[3];
				Object str5 = tuple[4];
				Object str6 = tuple[5];
				Object str7 = tuple[6];
				Object str8 = tuple[7];
				Object str9 = tuple[8];
				// Object str10 = tuple[9];

				mdo.setSubmittedCount(Integer.valueOf(String.valueOf(str1)));
				mdo.setDmrejCount(Integer.valueOf(String.valueOf(str2)));
				mdo.setAcceptedCount(Integer.valueOf(String.valueOf(str3)));
				mdo.setInterviewingCount(Integer.valueOf(String.valueOf(str4)));
				mdo.setConfirmedCount(Integer.valueOf(String.valueOf(str5)));
				mdo.setRejectedCount(Integer.valueOf(String.valueOf(str6)));
				mdo.setStartedCount(Integer.valueOf(String.valueOf(str7)));
				mdo.setBackOutCount(Integer.valueOf(String.valueOf(str8)));
				mdo.setOutOfProjCount(Integer.valueOf(String.valueOf(str9)));
				// mdo.setDm((String) str10);
				break;
			}
			System.out.println("resultMdo2 ---->" + mdo);
			return mdo;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<SubmitalStats> getAllSubmittalsStatsForExecutives(UserRoleVo userRoleVo) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(
					"select rws.mon,rws.year,sum(rws.submitted_count) as Submittedcount,sum(rws.dmrej_count) as Dmrejected,sum(rws.accepted_count) as Accepted,sum(rws.inteviewing_count) as Interviewing,sum(rws.confirmed_count) as Confirmed,sum(rws.rejected_count) as Rejected,sum(rws.started_count) as Started,sum(rws.backout_count) as Backout,sum(rws.outofprj_count) as Outofprjt  from submittal_status_monthly_view rws ");
			if (userRoleVo != null) {
				sql.append(" where rws.dm='" + userRoleVo.getUserName() + "'");
			}
			sql.append(" group by rws.mon,rws.year");
			sql.append(" order by rws.mon,rws.year desc");
			List<?> result = findBySQLQuery(sql.toString(), null);
			SubmitalStats mdo = new SubmitalStats();
			List<SubmitalStats> resultMdo = new ArrayList<SubmitalStats>();
			for (Object obj : result) {
				mdo = new SubmitalStats();
				Object[] tuple = (Object[]) obj;

				Object str1 = tuple[0];
				Object str2 = tuple[1];
				// Object str3 = tuple[2];
				Object str4 = tuple[2];
				Object str5 = tuple[3];
				Object str6 = tuple[4];
				Object str7 = tuple[5];
				Object str8 = tuple[6];
				Object str9 = tuple[7];
				Object str10 = tuple[8];
				Object str11 = tuple[9];
				Object str12 = tuple[10];
				// Object str13 = tuple[12];

				mdo.setMonth(String.valueOf(str1));
				mdo.setYear(String.valueOf(str2).substring(0, 4));
				// mdo.setSubmittedCount(Integer.valueOf(String.valueOf(str4).substring(0,String.valueOf(str4).length()-1)));
				mdo.setSubmittedCount(Integer.valueOf(String.valueOf(str4)));
				mdo.setDmrejCount(Integer.valueOf(String.valueOf(str5)));
				mdo.setAcceptedCount(Integer.valueOf(String.valueOf(str6)));
				mdo.setInterviewingCount(Integer.valueOf(String.valueOf(str7)));
				mdo.setConfirmedCount(Integer.valueOf(String.valueOf(str8)));
				mdo.setRejectedCount(Integer.valueOf(String.valueOf(str9)));
				mdo.setStartedCount(Integer.valueOf(String.valueOf(str10)));
				mdo.setBackOutCount(Integer.valueOf(String.valueOf(str11)));
				mdo.setOutOfProjCount(Integer.valueOf(String.valueOf(str12)));
				// mdo.setDm((String) str13);
				resultMdo.add(mdo);
			}
			System.out.println("resultMdo1   " + resultMdo);
			return resultMdo;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<SubmitalStats> getYearWiseSubmittalsForExecutives(String year, UserRoleVo userRoleVo) {
		try {
			StringBuilder sql = new StringBuilder();
			Map<String, Object> params = new HashMap<String, Object>();
			/*
			 * sql.append(
			 * "select * from submittal_status_year_view sys where sys.year ='"
			 * + year + "'");
			 */

			sql.append("select  rws.year,sum(rws.submitted_count) as Submittedcount,sum(rws.dmrej_count) as Dmrejected,"
					+ "sum(rws.accepted_count) as Accepted,sum(rws.inteviewing_count) as Interviewing,sum(rws.confirmed_count) as Confirmed, sum(rws.rejected_count) as Rejected, sum(rws.started_count) as Started,sum(rws.backout_count) as Backout,sum(rws.outofprj_coun) as Outofprjt"
					+ " from submittal_status_year_view rws ");

			if (year != null) {
				sql.append(" where rws.year='" + year + "'");
			}
			if (userRoleVo != null) {
				sql.append(" and rws.dm = '" + userRoleVo.getUserName() + "'");
			}
			sql.append("group by rws.year ");

			List<?> result = findBySQLQuery(sql.toString(), params);
			SubmitalStats mdo = new SubmitalStats();
			List<SubmitalStats> resultMdo = new ArrayList<SubmitalStats>();
			for (Object obj : result) {
				mdo = new SubmitalStats();
				Object[] tuple = (Object[]) obj;
				Object str1 = tuple[0];
				Object str2 = tuple[1];
				Object str3 = tuple[2];
				Object str4 = tuple[3];
				Object str5 = tuple[4];
				Object str6 = tuple[5];
				Object str7 = tuple[6];
				Object str8 = tuple[7];
				Object str9 = tuple[8];
				Object str10 = tuple[9];
				mdo.setYear(String.valueOf(str1).substring(0, 4));
				mdo.setSubmittedCount(Integer.valueOf(String.valueOf(str2)));
				mdo.setDmrejCount(Integer.valueOf(String.valueOf(str3)));
				mdo.setAcceptedCount(Integer.valueOf(String.valueOf(str4)));
				mdo.setInterviewingCount(Integer.valueOf(String.valueOf(str5)));
				mdo.setConfirmedCount(Integer.valueOf(String.valueOf(str6)));
				mdo.setRejectedCount(Integer.valueOf(String.valueOf(str7)));
				mdo.setStartedCount(Integer.valueOf(String.valueOf(str8)));
				mdo.setBackOutCount(Integer.valueOf(String.valueOf(str9)));
				mdo.setOutOfProjCount(Integer.valueOf(String.valueOf(str10)));
				resultMdo.add(mdo);
			}
			System.out.println("resultMdo2 ---->" + resultMdo);
			return resultMdo;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<SubmitalStats> getWeekelyStats(Date fromDate, Date toDate, UserRoleVo userRoleVo) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			StringBuilder sql = new StringBuilder();
			sql.append(" select aws.created_by,aws.user_role, aws.office_location, "
					+ " sum(open_count) as open_cnt, sum(closed_count) as closed_cnt,sum(submitted_count) as submitted_count, "
					+ " sum(dmrej_count) as dmrej_count,sum(accepted_count) as accepted_count,sum(inteviewing_count) as inteviewing_count, "
					+ " sum(confirmed_count) as confirmed_count,sum(rejected_count) as rejected_count,sum(started_count) as started_count,     "
					+ " sum(backout_count) as backout_count,sum(outofprj_count) as outofprj_count  from dm_adm_weekly_status_view aws where ");
			if (fromDate != null) {
				sql.append(" COALESCE(cast(aws.submital_date as date)) >= cast(:startDate as date)");
				params.put("startDate", fromDate);
			}
			if (toDate != null) {
				sql.append(" and COALESCE(cast(aws.submital_date as date)) <= cast(:endDate as date)");
				params.put("endDate", toDate);
			}
			if (userRoleVo != null) {
				sql.append(" and aws.created_by='" + userRoleVo.getUserName() + "'");
			}
			sql.append(" group BY aws.created_by,aws.user_role, aws.office_location");
			System.out.println("WeekelyStats-------->" + sql);
			List<?> result = findBySQLQuery(sql.toString(), params);
			System.out.println("WeekelyStats result--->" + result);
			SubmitalStats mdo = new SubmitalStats();
			List<SubmitalStats> resultMdo = new ArrayList<SubmitalStats>();
			for (Object obj : result) {
				mdo = new SubmitalStats();
				Object[] tuple = (Object[]) obj;
				Object str1 = tuple[0];
				Object str2 = tuple[1];
				Object str3 = tuple[2];
				Object str4 = tuple[3];
				Object str5 = tuple[4];
				Object str6 = tuple[5];
				Object str7 = tuple[6];
				Object str8 = tuple[7];
				Object str9 = tuple[8];
				Object str10 = tuple[9];
				Object str11 = tuple[10];
				Object str12 = tuple[11];
				Object str13 = tuple[12];
				Object str14 = tuple[13];
				// mdo.setSubmitalDate(String.valueOf(str1));
				mdo.setCreatedBy(String.valueOf(str1));
				mdo.setUserRole(String.valueOf(str2));
				mdo.setOfficeLocation(String.valueOf(str3));
				mdo.setOpenCount(Integer.valueOf(String.valueOf(str4)));
				mdo.setClosedCount(Integer.valueOf(String.valueOf(str5)));
				mdo.setSubmittedCount(Integer.valueOf(String.valueOf(str6)));
				mdo.setDmrejCount(Integer.valueOf(String.valueOf(str7)));
				mdo.setAcceptedCount(Integer.valueOf(String.valueOf(str8)));
				mdo.setInterviewingCount(Integer.valueOf(String.valueOf(str9)));
				mdo.setConfirmedCount(Integer.valueOf(String.valueOf(str10)));
				mdo.setRejectedCount(Integer.valueOf(String.valueOf(str11)));
				mdo.setStartedCount(Integer.valueOf(String.valueOf(str12)));
				mdo.setBackOutCount(Integer.valueOf(String.valueOf(str13)));
				mdo.setOutOfProjCount(Integer.valueOf(String.valueOf(str14)));

				resultMdo.add(mdo);
			}
			System.out.println("resultMdoStats ---->" + resultMdo);
			return resultMdo;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<SubmitalStats> getRecruiterWeekelyStats(Date fromDate, Date toDate, String dmName, UserRoleVo userRoleVo) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			StringBuilder sql = new StringBuilder();
			sql.append(" select rws.recut_name,rws.order_created_dm,rws.assigned_dm,rws.office_location,"
					+ "sum(rws.submitted_count) as Submittedcount,sum(rws.dmrej_count) as Dmrejected,"
					+ "sum(rws.accepted_count) as Accepted,sum(rws.inteviewing_count) as Interviewing,"
					+ "sum(rws.confirmed_count) as Confirmed,sum(rws.rejected_count) as Rejected,"
					+ "sum(rws.started_count) as Started,sum(rws.backout_count) as Backout,sum(rws.outofprj_count) as Outofprjt"
					+ " from recruiter_wise_submital_stat_view   rws where ");
			if (fromDate != null) {
				sql.append("  COALESCE(date(rws.submital_date)) >=date(:startDate)");
				/*
				 * Calendar calender=Calendar.getInstance();
				 * calender.setTime(fromDate); calender.add(Calendar.DATE, -1);
				 */
				params.put("startDate", fromDate);
			}
			if (toDate != null) {
				sql.append(" and COALESCE(date(rws.submital_date)) <=date(:endDate)");
				/*
				 * Calendar calender=Calendar.getInstance();
				 * calender.setTime(toDate); calender.add(Calendar.DATE, -1);
				 */
				params.put("endDate", toDate);
			}
			sql.append(" and rws.order_created_dm = '" + dmName + "'");
			// params.put("dm",dmName);
			sql.append(" group by rws.recut_name,rws.order_created_dm,rws.assigned_dm,rws.office_location ");
			System.out.println("sql-------->" + sql);
			List<?> result = findBySQLQuery(sql.toString(), params);
			System.out.println("Recruiter result--->" + result);
			SubmitalStats mdo = new SubmitalStats();
			List<SubmitalStats> resultMdo = new ArrayList<SubmitalStats>();
			for (Object obj : result) {
				mdo = new SubmitalStats();
				Object[] tuple = (Object[]) obj;
				Object recruiterName = tuple[0];
				Object jobDmName = tuple[1];
				Object assignedDm = tuple[2];
				Object officeLocation = tuple[3];
				Object submittedCount = tuple[4];
				Object dmRejCount = tuple[5];
				Object acceptedCount = tuple[6];
				Object interviewingCount = tuple[7];
				Object confirmedCount = tuple[8];
				Object rejectedCount = tuple[9];
				Object startedCount = tuple[10];
				Object backOutCount = tuple[11];
				Object outOfProjCount = tuple[12];
				mdo.setRecruiterName(String.valueOf(recruiterName));
				mdo.setOrderCreatedDm(String.valueOf(jobDmName));
				mdo.setAssignedDm(String.valueOf(assignedDm));
				mdo.setOfficeLocation(String.valueOf(officeLocation));
				mdo.setSubmittedCount(Integer.valueOf(String.valueOf(submittedCount)));
				mdo.setDmrejCount(Integer.valueOf(String.valueOf(dmRejCount)));
				mdo.setAcceptedCount(Integer.valueOf(String.valueOf(acceptedCount)));
				mdo.setInterviewingCount(Integer.valueOf(String.valueOf(interviewingCount)));
				mdo.setConfirmedCount(Integer.valueOf(String.valueOf(confirmedCount)));
				mdo.setRejectedCount(Integer.valueOf(String.valueOf(rejectedCount)));
				mdo.setStartedCount(Integer.valueOf(String.valueOf(startedCount)));
				mdo.setBackOutCount(Integer.valueOf(String.valueOf(backOutCount)));
				mdo.setOutOfProjCount(Integer.valueOf(String.valueOf(outOfProjCount)));
				resultMdo.add(mdo);
			}
			System.out.println("resultMdoStats ---->" + resultMdo);
			return resultMdo;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<SubmitalStats> getRecruiterLocationStats(Date fromDate, Date toDate, String location, UserRoleVo userRoleVo) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			StringBuilder sql = new StringBuilder();
			sql.append(" select rws.recut_name,rws.assigned_dm,rws.office_location,"
					+ "sum(rws.submitted_count) as Submittedcount,sum(rws.dmrej_count) as Dmrejected,"
					+ "sum(rws.accepted_count) as Accepted,sum(rws.inteviewing_count) as Interviewing,"
					+ "sum(rws.confirmed_count) as Confirmed,sum(rws.rejected_count) as Rejected,"
					+ "sum(rws.started_count) as Started,sum(rws.backout_count) as Backout,sum(rws.outofprj_count) as Outofprjt"
					+ " from recruiter_wise_submital_stat_view   rws where ");
			if (fromDate != null) {
				sql.append("  COALESCE(date(rws.submital_date)) >=date(:startDate)");
				/*
				 * Calendar calender=Calendar.getInstance();
				 * calender.setTime(fromDate); calender.add(Calendar.DATE, -1);
				 */
				params.put("startDate", fromDate);
			}
			if (toDate != null) {
				sql.append(" and COALESCE(date(rws.submital_date)) <=date(:endDate)");
				/*
				 * Calendar calender=Calendar.getInstance();
				 * calender.setTime(toDate); calender.add(Calendar.DATE, -1);
				 */
				params.put("endDate", toDate);
			}
			if (userRoleVo != null) {
				sql.append(" and rws.order_created_dm = '" + userRoleVo.getUserName() + "'");

			}
			sql.append(" and rws.office_location = '" + location + "'");
			// params.put("dm",dmName);
			sql.append(" group by rws.recut_name,rws.assigned_dm,rws.office_location ");
			System.out.println("sql-------->" + sql);
			List<?> result = findBySQLQuery(sql.toString(), params);
			System.out.println("Recruiter result--->" + result);
			SubmitalStats mdo = new SubmitalStats();
			List<SubmitalStats> resultMdo = new ArrayList<SubmitalStats>();
			for (Object obj : result) {
				mdo = new SubmitalStats();
				Object[] tuple = (Object[]) obj;
				Object recruiterName = tuple[0];
				/* Object jobDmName = tuple[1]; */
				Object assignedDm = tuple[1];
				Object officeLocation = tuple[2];
				Object submittedCount = tuple[3];
				Object dmRejCount = tuple[4];
				Object acceptedCount = tuple[5];
				Object interviewingCount = tuple[6];
				Object confirmedCount = tuple[7];
				Object rejectedCount = tuple[8];
				Object startedCount = tuple[9];
				Object backOutCount = tuple[10];
				Object outOfProjCount = tuple[11];
				mdo.setRecruiterName(String.valueOf(recruiterName));
				/* mdo.setOrderCreatedDm(String.valueOf(jobDmName)); */
				mdo.setAssignedDm(String.valueOf(assignedDm));
				mdo.setOfficeLocation(String.valueOf(officeLocation));
				mdo.setSubmittedCount(Integer.valueOf(String.valueOf(submittedCount)));
				mdo.setDmrejCount(Integer.valueOf(String.valueOf(dmRejCount)));
				mdo.setAcceptedCount(Integer.valueOf(String.valueOf(acceptedCount)));
				mdo.setInterviewingCount(Integer.valueOf(String.valueOf(interviewingCount)));
				mdo.setConfirmedCount(Integer.valueOf(String.valueOf(confirmedCount)));
				mdo.setRejectedCount(Integer.valueOf(String.valueOf(rejectedCount)));
				mdo.setStartedCount(Integer.valueOf(String.valueOf(startedCount)));
				mdo.setBackOutCount(Integer.valueOf(String.valueOf(backOutCount)));
				mdo.setOutOfProjCount(Integer.valueOf(String.valueOf(outOfProjCount)));

				resultMdo.add(mdo);
			}
			System.out.println("resultMdoStats ---->" + resultMdo);
			return resultMdo;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByDm(Date fromDate, Date toDate) {
		try {
			Map<String, Map<SubmittalStatus, Integer>> userMap = new TreeMap<String, Map<SubmittalStatus, Integer>>();
			try {

				List<String> paramNames = new ArrayList<String>();
				List<Object> paramValues = new ArrayList<Object>();

				StringBuilder hql = new StringBuilder();
				hql.append("select s.createdBy,s.status, count(*)  from Submittal s");
				hql.append(" where 1=1 and s.deleteFlag=0");
				if (fromDate != null) {
					hql.append(" and COALESCE(cast(s.createdOn as date)) >=  cast(:startDate as date)");
					paramNames.add("startDate");
					paramValues.add(fromDate);
				}
				if (toDate != null) {
					hql.append(" and COALESCE(cast(s.createdOn as date)) <= cast(:endDate as date)");
					paramNames.add("endDate");
					paramValues.add(toDate);
				}
				String portalName = "Sapeare";
				hql.append(" and  s.jobOrder.companyFlag='" + portalName + "'");
				hql.append(" group by s.createdBy,s.status order by s.createdBy Desc");
				List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
				for (Object record : result) {
					Object[] tuple = (Object[]) record;
					String username = (String) tuple[0];
					SubmittalStatus status = SubmittalStatus.valueOf(tuple[1].toString());
					Number count = (Number) tuple[2];
					System.out.println("username::" + username);
					System.out.println("count::" + count);

					Map<SubmittalStatus, Integer> statusMap = userMap.get(username);
					if (statusMap == null) {
						statusMap = new HashMap<SubmittalStatus, Integer>();
						userMap.put(username, statusMap);
					}
					statusMap.put(status, count != null ? count.intValue() : 0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return userMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByDmJobOrder(User user, Date fromDate, Date toDate) {
		try {
			Map<String, Map<SubmittalStatus, Integer>> userMap = new TreeMap<String, Map<SubmittalStatus, Integer>>();
			try {
				List<String> paramNames = new ArrayList<String>();
				List<Object> paramValues = new ArrayList<Object>();

				StringBuilder hql = new StringBuilder();
				hql.append("select s.createdBy,s.status, count(*)  from Submittal s");
				hql.append(" where 1=1 and s.deleteFlag=0");
				if (fromDate != null) {
					hql.append(" and COALESCE(cast(s.createdOn as date)) >=  cast(:startDate as date)");
					paramNames.add("startDate");
					paramValues.add(fromDate);
				}
				if (toDate != null) {
					hql.append(" and COALESCE(cast(s.createdOn as date)) <= cast(:endDate as date)");
					paramNames.add("endDate");
					paramValues.add(toDate);
				}
				if (user != null) {
					hql.append(" and s.jobOrder.createdBy='" + user.getUserId() + "'");
				}
				String portalName = "Sapeare";
				hql.append(" and  s.jobOrder.companyFlag='" + portalName + "'");
				hql.append(" group by s.createdBy,s.status order by s.createdBy Desc");
				List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
				System.out.println("result>>>" + result);

				for (Object record : result) {
					Object[] tuple = (Object[]) record;
					String username = (String) tuple[0];
					SubmittalStatus status = SubmittalStatus.valueOf(tuple[1].toString());
					Number count = (Number) tuple[2];
					System.out.println("username::" + username);
					System.out.println("count::" + count);

					Map<SubmittalStatus, Integer> statusMap = userMap.get(username);
					if (statusMap == null) {
						statusMap = new HashMap<SubmittalStatus, Integer>();
						userMap.put(username, statusMap);
					}
					statusMap.put(status, count != null ? count.intValue() : 0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return userMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<Submittal> getSubmittalDetailsbyStatusSapeareReport(SubmittalStatus status, Date dateStart, Date dateEnd, String createdUser, String loginUser,
			String portalName) {
		try {
			StringBuffer hql = new StringBuffer();
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			// if (!status.equals(SubmittalStatus.SUBMITTED)) {
			hql.append("select s from Submittal s,Candidate c where s.status =:status and s.createdBy =:createdUser ");
			paramNames.add("status");
			paramValues.add(status);
			// } else {
			// hql.append("select s from Submittal s,Candidate c where s.status
			// IN('SUBMITTED', 'DMREJ','ACCEPTED', 'INTERVIEWING', 'CONFIRMED',
			// 'REJECTED', 'STARTED', 'BACKOUT', 'OUTOFPROJ') and s.createdBy
			// =:createdUser");
			// }
			hql.append(" and s.candidate.id=c.id and s.deleteFlag=0 ");
			if (dateStart != null) {
				hql.append(" and COALESCE(cast(s.createdOn as date)) >=  cast(:startDate as date)");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and COALESCE(cast(s.createdOn as date)) <= cast(:endDate as date)");
				paramNames.add("endDate");
				paramValues.add(dateEnd);
			}
			paramNames.add("createdUser");
			paramValues.add(createdUser);

			if (loginUser != null && loginUser.length() > 0)
				hql.append(" and s.jobOrder.createdBy='" + loginUser + "'");

			if (portalName.length() > 0)
				hql.append(" and  s.jobOrder.companyFlag='" + portalName + "'");
			hql.append(" ORDER By s.candidate");
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
			return (List<Submittal>) result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	public List<CandidateDto> getOutofProjs(CandidateSearchDto statusDto) {
		try {
			Date fromDate = Utils.convertAngularStrToDate(Utils.replaceNullWithEmpty(statusDto.getStartDate()));
			Date toDate = Utils.convertAngularStrToDate(Utils.replaceNullWithEmpty(statusDto.getEndDate()));
			StringBuffer hql = new StringBuffer();
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			hql.append(
					"select distinct c.id, c.firstName, c.lastName, c.address, c.email, c.phone, c.visaType, c.updatedBy, c.updatedOn,c.title, c.hot, c.block from Candidate c, Submittal s where ");
			hql.append("s.candidate.id=c.id and c.deleteFlag=0 and s.status='OUTOFPROJ'");

			if (fromDate != null) {
				hql.append(" and COALESCE(cast(s.createdDate as date),cast(s.createdOn as date)) >=:fromDate");
				paramNames.add("fromDate");
				paramValues.add(fromDate);
			}
			if (toDate != null) {
				hql.append(" and COALESCE(cast(s.createdDate as date),cast(s.createdOn as date)) <=:toDate ");
				paramNames.add("toDate");

				paramValues.add(toDate);
			}
			hql.append(" ORDER By c.updatedOn DESC");

			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			List<CandidateDto> dtoList = new ArrayList<CandidateDto>();
			if (result != null) {
				Iterator<?> itr = result.iterator();
				while (itr.hasNext()) {
					CandidateDto dto = new CandidateDto();
					Object candiate[] = (Object[]) itr.next();
					dto.setId(Utils.getStringValueOfObj(candiate[0]));
					dto.setFirstName(Utils.concatenateTwoStringsWithSpace(Utils.getStringValueOfObj(candiate[1]), Utils.getStringValueOfObj(candiate[2])));
					Address address = (Address) candiate[3];
					if (address != null) {
						dto.setLocation(Utils.concatenateTwoStringsWithSpace(address.getCity(), address.getCountry()));
					}
					dto.setEmail(Utils.getStringValueOfObj(candiate[4]));
					dto.setPhoneNumber(Utils.getStringValueOfObj(candiate[5]));
					dto.setVisaType(Utils.getStringValueOfObj(candiate[6]));
					dto.setUploadedBy(Utils.getStringValueOfObj(candiate[7]));
					final Date updatedDate = (Date) candiate[8];
					dto.setUpdatedOn(Utils.convertDateToString(updatedDate));
					dto.setTitle(Utils.getStringValueOfObj(candiate[9]));

					dto.setHot((Boolean) (candiate[10] == null ? false : candiate[10]));
					dto.setBlock((Boolean) (candiate[11] == null ? false : candiate[11]));

					dtoList.add(dto);
				}
				List<Integer> candidateIds = new ArrayList<Integer>();
				if (dtoList != null && dtoList.size() > 0) {
					for (CandidateDto dto : dtoList) {
						candidateIds.add(Integer.parseInt(dto.getId()));
					}
					hql = new StringBuffer();
					paramValues = new ArrayList<Object>();
					paramNames = new ArrayList<String>();
					hql.append(
							"select s.status,s.candidate.id from Submittal s where s.candidate.id in ?1 and  s.deleteFlag=0 order by COALESCE(s.createdOn) ASC");
					paramNames.add("1");
					paramValues.add(candidateIds);
					result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
					if (result != null) {
						Map<Integer, SubmittalStatus> candidateIdWithStatusMap = new HashMap<Integer, SubmittalStatus>();
						if (result != null) {
							itr = result.iterator();
							while (itr.hasNext()) {
								Object[] submittal = (Object[]) itr.next();
								candidateIdWithStatusMap.put((Integer) submittal[1], (SubmittalStatus) submittal[0]);
							}
							Iterator<CandidateDto> candidateDtoIterator = dtoList.iterator();
							while (candidateDtoIterator.hasNext()) {
								CandidateDto dto = candidateDtoIterator.next();
								if (candidateIdWithStatusMap.containsKey(Integer.parseInt(dto.getId()))) {
									if (!candidateIdWithStatusMap.get(Integer.parseInt(dto.getId())).equals(SubmittalStatus.OUTOFPROJ)) {
										candidateDtoIterator.remove();
									}
								}
							}

						}
					}
				}

			}
			return dtoList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

package com.uralian.cgiats.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.JobOrderDao;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.ExecutiveResumeView;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.JobViewOrder;
import com.uralian.cgiats.model.MonthlySalesQuotas;
import com.uralian.cgiats.model.SalesQuotaView;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.rest.UserRoleVo;
import com.uralian.cgiats.util.Utils;

/**
 * @author Christian Rebollar
 */
@Repository
@SuppressWarnings("unchecked")
public class JobOrderDaoImpl extends GenericDaoImpl<JobOrder, Integer> implements JobOrderDao {
	/**
	 */
	public JobOrderDaoImpl() {
		super(JobOrder.class);
	}

	public JobOrder findById(Integer orderId) {
		try {
			return (JobOrder) getHibernateTemplate().getSessionFactory().getCurrentSession().get(JobOrder.class, orderId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}
	
	public List<JobOrder> findByIds(List<Integer> orderIds) {
		try {
			Session session=getHibernateTemplate().getSessionFactory().getCurrentSession();
			Query query=session.createQuery("select j from JobOrder j where j.deleteFlag = 0 and j.id in ?1");
			query.setParameterList("1", orderIds);
			return query.list();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.dao.JobOrderDao#findAllTitles()
	 */
	@Override
	public List<String> findAllTitles() {
		try {
			String hql = "select distinct o.title from JobOrder o where o.title is not null order by o.title";
			List<String> list = (List<String>) runQuery(hql.toString());
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.dao.JobOrderDao#findAllCustomers()
	 */
	@Override
	public List<String> findAllCustomers() {
		try {
			String hql = "select distinct o.customer from JobOrder o where o.customer is not null order by o.customer";
			List<String> list = (List<String>) runQuery(hql.toString());
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.dao.JobOrderDao#findAllCities()
	 */
	@Override
	public List<String> findAllCities() {
		try {
			String hql = "select distinct o.city from JobOrder o where o.city is not null order by o.city";
			List<String> list = (List<String>) runQuery(hql.toString());
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.dao.JobOrderDao#findAllStates()
	 */
	@Override
	public List<String> findAllStates() {
		try {
			String hql = "select distinct o.state from JobOrder o where o.state is not null order by o.state";
			List<String> list = (List<String>) runQuery(hql.toString());
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.dao.JobOrderDao#getStatsByUser(java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public Map<String, Map<JobOrderStatus, Integer>> getStatsByUser(User user, Date dateStart, Date dateEnd, boolean flag) {
		try {
			long startTime = System.currentTimeMillis();

			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();

			hql.append("select jo.createdBy, jo.status, count(*),u.assignedBdm,u.userRole from JobOrder jo,User u");

			hql.append(" where 1=1 and u.userId=jo.createdBy  and u.status = 'ACTIVE' and jo.deleteFlag=0 ");
			if (flag == true) {
				hql.append(" and u.userRole IN('DM','ADM') ");
			} else {
				hql.append(" and u.userRole NOT IN('DM','ADM') ");
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

			if (user.getUserRole().equals(UserRole.DM)) {
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
			} else if (user.getUserRole().equals(UserRole.ADM)) {
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
				}
			} else if (user.getUserRole().equals(UserRole.Recruiter)) {
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
	 * com.uralian.cgiats.dao.JobOrderDao#getSubmittalStatusByUser(com.uralian
	 * .cgiats.model.User, java.util.Date, java.util.Date)
	 */
	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByUser(User user, Date dateStart, Date dateEnd) {
		try {
			long startTime = System.currentTimeMillis();

			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select s.createdBy, status, count(*) from Submittal s");
			hql.append(" where 1=1 and s.deleteFlag!=1");

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

			hql.append(" group by createdBy, status");
			System.out.println("hql>>" + hql);
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

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
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByLocation(UserDto userDto, List<String> officeLocs, List<String> dms, Date dateStart,
			Date dateEnd,String userStatus) {
		try {
			long startTime = System.currentTimeMillis();

			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select s.createdBy, s.status, count(s) from User u,Submittal s");
			hql.append(" where  s.deleteFlag!=1  and s.createdBy=u.userId ");
			if(userStatus!=null&&userStatus.trim().length()>0){
				hql.append(" and u.status='"+userStatus+"' ");
			}
			if (officeLocs != null && officeLocs.size() > 0) {
				hql.append(" and u.officeLocation in ?1  ");
				paramNames.add("1");
				paramValues.add(officeLocs);

				System.out.println("office location is:::::::::::::" + officeLocs);
			}
			if (dms != null && dms.size() > 0) {
//				(select u1.userId from User u1 where u1.status = 'ACTIVE' and (u1.assignedBdm in ?2 or u1.userId in ?2)
				List<String> userIds = getSubordinatesWithUserIds(dms);
				hql.append(" and s.createdBy in ?2 ");
				paramNames.add("2");
				userIds.addAll(dms);
				paramValues.add(userIds);

				System.out.println("dms is:::::::::::::" + dms);
			}
//			if (userDto.getUserRole().toString().equals(UserRole.Manager.toString())) {
//				hql.append(" and u.officeLocation='" + userDto.getOfficeLocation() + "'");
//			}

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
	 * com.uralian.cgiats.dao.JobOrderDao#getDmSubmittalStatusByUser(java.lang
	 * .String, java.util.Date, java.util.Date)
	 */
	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getDmSubmittalStatusByUser(String userRole, Date dateStart, Date dateEnd, User user) {
		Map<String, Map<SubmittalStatus, Integer>> userMap = new TreeMap<String, Map<SubmittalStatus, Integer>>();
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			List<?> result = null;
			StringBuilder hql = new StringBuilder();

			if (userRole != null && userRole.equalsIgnoreCase("DM")) {

				hql.append("select a.userName,a.status,sum(a.count) FROM ( ");

				hql.append("select  CASE    WHEN (u.user_role='DM') THEN u.user_id   " + "           WHEN (u.user_role='Recruiter' and 'DM' in " + ""
						+ "                     ( select u1.user_role from user_acct u1 where u1.user_id=u.assigned_bdm)) THEN u.assigned_bdm  END as userName, s.status, count(*) from Submittal s,user_acct u"
						+ " where 1=1 " + " and s.delete_flag !=1 " + " and s.created_by=u.user_id  " + " and s.status IN('STARTED','CONFIRMED','BACKOUT') ");

				if (dateStart != null) {
					hql.append(" and COALESCE(cast(s.updated_on as date),cast(s.created_on as date)) >=  cast(? as date)");

				}

				if (dateEnd != null) {
					hql.append("  and COALESCE(cast(s.updated_on as date),cast(s.created_on as date)) <= cast(? as date)");

				}

				if (user != null) {
					hql.append(" and u.user_id=? and u.user_id=?");
				}

				hql.append(" group by userName,s. status)as a where a.userName!='Null'  group by a.userName,a.status");

				System.out.println("DM Recruiter Hql---->" + hql);

				result = findBySqlQuery1(hql.toString(), dateStart, dateEnd, user);

				for (Object record : result) {
					Object[] tuple = (Object[]) record;
					String username = (String) tuple[0];
					SubmittalStatus status = SubmittalStatus.valueOf(tuple[1].toString());
					Number count = (Number) tuple[2];

					Map<SubmittalStatus, Integer> statusMap = userMap.get(username);
					if (statusMap == null) {
						statusMap = new HashMap<SubmittalStatus, Integer>();
						userMap.put(username, statusMap);
					}
					statusMap.put(status, count != null ? count.intValue() : 0);
				}
			} else {

				hql.append(
						"select s.created_by,s.status, count(s.status) from (select distinct  su.submittal_id, su.status,su.created_by,to_date(to_char(su.created_on, 'MM/DD/YYYY'),'MM/DD/YYYY') created_on from   submittal_history su) s,user_acct u");
				hql.append(" where 1=1 and s.status IN('SUBMITTED','ACCEPTED','DMREJ','CONFIRMED','REJECTED','INTERVIEWING') ");
				hql.append(
						" and s.submittal_id IN(select sub.submittal_id from submittal sub where sub.delete_flag!=1  and s.created_by=u.user_id and u.user_role='Recruiter'");

				if (dateStart != null) {
					hql.append(
							" and  COALESCE(to_date(to_char(sub.updated_on, 'MM/DD/YYYY'),'MM/DD/YYYY') ,to_date(to_char(sub.created_on, 'MM/DD/YYYY'),'MM/DD/YYYY')) >= ?");
				}
				if (dateEnd != null) {
					hql.append(
							" and COALESCE(to_date(to_char(sub.updated_on, 'MM/DD/YYYY'),'MM/DD/YYYY') ,to_date(to_char(sub.created_on, 'MM/DD/YYYY'),'MM/DD/YYYY')) <= ?  ");
				}

				if (user != null && user.getUserRole().equals(UserRole.DM)) {
					hql.append("  and (u.assigned_bdm=? or u.assigned_bdm in (select u1.user_id from user_acct u1 where u1.assigned_bdm=?))");
				} else if (user != null && user.getUserRole().equals(UserRole.ADM)) {
					hql.append(" and u.assigned_bdm=?");
				}

				hql.append(") group by s.submittal_id, s.status,s.created_by order by s.status");
				System.out.println("Recruiter Hql---->" + hql);
				if (user == null)
					result = findBySqlQuery1(hql.toString(), dateStart, dateEnd, null);
				else
					result = findBySqlQuery1(hql.toString(), dateStart, dateEnd, user);
				int finalCount = 0;

				for (Object record : result) {
					Object[] tuple = (Object[]) record;
					String username = (String) tuple[0];
					String status = (String) tuple[1];
					Number count = (Number) tuple[2];

					Map<SubmittalStatus, Integer> statusMap = userMap.get(username);
					if (statusMap == null) {
						statusMap = new HashMap<SubmittalStatus, Integer>();

					}
					userMap.put(username, statusMap);
					if (statusMap.containsKey(SubmittalStatus.valueOf(status)) && count != null) {
						finalCount = statusMap.get(SubmittalStatus.valueOf(status)) + count.intValue();
						statusMap.put(SubmittalStatus.valueOf(status), finalCount);
					} else {
						statusMap.put(SubmittalStatus.valueOf(status), count != null ? count.intValue() : 0);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		log.info("userMap final>>" + userMap);
		return userMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.dao.JobOrderDao#getDmSubmittalStatusByRecruiter(java
	 * .util.Date, java.util.Date)
	 */
	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getDmSubmittalStatusByRecruiter(Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select s.createdBy, s.status, count(*) from Submittal s,User u");
			hql.append(" where 1=1 and s.deleteFlag!=1 and s.createdBy=u.userId and u.userRole='Recruiter' ");

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

	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getAdmMonthlyQuota(String role, Date submittalFrom, Date submittalTo, String userId) {
		try {
			Map<String, Map<SubmittalStatus, Integer>> userMap = new TreeMap<String, Map<SubmittalStatus, Integer>>();
			List<?> result = null;

			Map<String, Object> params = new HashMap<String, Object>();
			StringBuilder sql = new StringBuilder();

			try {

				sql.append("select a.username,a.status,sum(a.count) FROM(");
				sql.append(
						" select (CASE WHEN (u.user_role='Recruiter' and u.user_id=s.created_by and 'ADM' IN(select u2.user_role from user_acct u2 where u2.user_id=u.assigned_bdm)) THEN u.assigned_bdm ");
				sql.append(
						" WHEN (u.user_role='ADM' and u.user_id=s.created_by) THEN u.user_id  END) as username,s.status,count(*) as count from submittal s,user_acct u ");
				sql.append(" where s.status IN('STARTED','CONFIRMED','BACKOUT') and s.delete_flag!=1 ");

				if (submittalFrom != null) {
					sql.append("  and COALESCE(date(s.updated_on),s.created_on)>=date(:submittalFrom)");
					Calendar calender = Calendar.getInstance();
					calender.setTime(submittalFrom);
					calender.add(Calendar.DATE, -1);
					params.put("submittalFrom", submittalFrom);

				}
				if (submittalTo != null) {
					sql.append("  and COALESCE(date(s.updated_on),s.created_on)<=date(:submittalTo)");

					Calendar calender = Calendar.getInstance();
					calender.setTime(submittalTo);
					calender.add(Calendar.DATE, -1);
					params.put("submittalTo", submittalTo);
				}
				sql.append(" and (s.created_by=u.user_id ");
				sql.append(
						" or (s.created_by in (select u1.user_id from user_acct u1 where u1.assigned_bdm=u.user_id and u.user_role in ('Recruiter','ADM'))))");
				sql.append(" group by username,s.status,u.user_id)as a  where a.username!='Null'");

				if (role.equals("DM") && userId != null) {
					sql.append(" and   a.username in (select u3.user_id from user_acct u3 where u3.assigned_bdm=:userId)");
					params.put("userId", userId);
				}

				if (role.equals("ADM") && userId != null) {
					sql.append(" and   a.username=:userId");
					params.put("userId", userId);
				}

				sql.append(" GROUP BY a.username,a.status");

				result = findBySQLQueryNamedParam(sql.toString(), params);

			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}

			/*
			 * if(submittalFrom!=null&&submittalTo!=null) result =
			 * findBySQLQueryNamedParam(sql.toString(),params);
			 */
			int finalCount = 0;

			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				String status = (String) tuple[1];
				Number count = (Number) tuple[2];

				Map<SubmittalStatus, Integer> statusMap = userMap.get(username);
				if (statusMap == null) {
					statusMap = new HashMap<SubmittalStatus, Integer>();

				}
				userMap.put(username, statusMap);
				if (statusMap.containsKey(SubmittalStatus.valueOf(status)) && count != null) {
					finalCount = statusMap.get(SubmittalStatus.valueOf(status)) + count.intValue();
					statusMap.put(SubmittalStatus.valueOf(status), finalCount);
				} else {
					statusMap.put(SubmittalStatus.valueOf(status), count != null ? count.intValue() : 0);
				}
			}
			log.info("userMap final>>" + userMap);
			return userMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<SalesQuotaView> getDmSubmittalsByUserQuota(String month, String year, String userId) {

		List<SalesQuotaView> salesQuotasList = new ArrayList<SalesQuotaView>();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			List<?> result = null;
			StringBuilder sql = new StringBuilder();
			sql.append("select msq.created_by,msq.submitted_count,msq.dmrej_count,msq.accepted_count,msq.inteviewing_count,"
					+ "msq.confirmed_count,msq.rejected_count,msq.sales_quota from monthly_sales_quota msq where 1=1  ");
			sql.append(" and  msq.assigned_bdm='" + userId + "'");
			sql.append(" and   msq.mon ='" + month + "'");
			sql.append(" and   msq.rec_year ='" + year + "'");
			sql.append(" and msq.user_role ='" + UserRole.Recruiter + "'");
			System.out.println("Monthly========>" + sql);
			result = findBySQLQuery(sql.toString(), null);

			// result=findByQuery(sql.toString(), params);

			SalesQuotaView salesQuota = new SalesQuotaView();
			for (Object record : result) {
				salesQuota = new SalesQuotaView();
				Object[] tuple = (Object[]) record;
				Object str1 = tuple[0];
				Object str2 = tuple[1];
				Object str3 = tuple[2];
				Object str4 = tuple[3];
				Object str5 = tuple[4];
				Object str6 = tuple[5];
				Object str7 = tuple[6];
				Object str8 = tuple[7];
				salesQuota.setRecruiterName(String.valueOf(str1));
				salesQuota.setSubmittedCount(Integer.valueOf(String.valueOf(str2)));
				salesQuota.setDmrejCount(Integer.valueOf(String.valueOf(str3)));
				salesQuota.setAcceptedCount(Integer.valueOf(String.valueOf(str4)));
				salesQuota.setInterviewingCount(Integer.valueOf(String.valueOf(str5)));
				salesQuota.setConfirmedCount(Integer.valueOf(String.valueOf(str6)));
				salesQuota.setRejectedCount(Integer.valueOf(String.valueOf(str7)));
				salesQuota.setSalesquota(Integer.valueOf(String.valueOf(str8)));
				salesQuotasList.add(salesQuota);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		log.info("salesquotaList  final>>" + salesQuotasList);
		return salesQuotasList;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MonthlySalesQuotas> getRecSalesQuotaList(String userId, String month, String year) {

		List<MonthlySalesQuotas> salesQuotasList = new ArrayList<MonthlySalesQuotas>();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			List<?> result = null;
			StringBuilder sql = new StringBuilder();
			sql.append("select MonthlySalesQuotas as msq  where  1=1 ");
			sql.append(" and   createdBy='" + userId + "'");
			// params.put("userId", userId);
			sql.append(" and   month='" + month + "'");
			// params.put("month", month);
			sql.append(" and   year='" + year + "'");
			// params.put("year", year);
			System.out.println("sql" + sql);

			result = findBySQLQuery(sql.toString(), params);

			result = findByQuery(sql.toString(), params);

			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				Integer id = (Integer) tuple[0];
				String dmName = (String) tuple[1];
				String salesQuota = (String) tuple[2];
				MonthlySalesQuotas quotas = new MonthlySalesQuotas();
				if (id != null)
					quotas.setId(id);
				quotas.setDmName(dmName);
				quotas.setSalesQuota(salesQuota);
				salesQuotasList.add(quotas);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		log.info("salesquotaList  final>>" + salesQuotasList);
		return salesQuotasList;
	}

	public Map<JobOrderStatus, Integer> getAllJobOrdersCounts(Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			StringBuilder hql = new StringBuilder();
			hql.append(
					"select CASE when j.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN 'OPEN' ELSE 'CLOSE' END as STATUS,count(*) as Count from JobOrder j,User u");
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

	public Map<JobOrderStatus, Integer> getTodayJobOrdersCountsForExecutives(Date dateStart, Date dateEnd) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			StringBuilder sql = new StringBuilder();
			sql.append("select j from job_order_status_view  j");
			if (dateStart != null) {
				sql.append(" and cast(j.createdOn as date) >=  cast(:startDate as date)");
				params.put("startDate", dateStart);
			}
			if (dateEnd != null) {
				sql.append(" and cast(j.createdOn as date) <= cast(:endDate as date)");
				params.put("endDate", dateEnd);
			}
			System.out.println("sql----------->" + sql);
			List<?> result = findByQuery(sql.toString(), null);
			Map<JobOrderStatus, Integer> userMap = new TreeMap<JobOrderStatus, Integer>();
			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				JobOrderStatus status = (JobOrderStatus) tuple[0];
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

	public JobViewOrder getTodayJobOrderStatsForExecutives(UserRoleVo userRoleVo) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select j.open_count,j.closed_count from job_order_status_view  j ");
			if (userRoleVo != null)
				sql.append(" where j.dm='" + userRoleVo.getUserName() + "'");
			sql.append(" group by j.open_count,j.closed_count");
			System.out.println("sql---->" + sql);
			List<?> result = findBySQLQuery(sql.toString(), null);
			JobViewOrder mdo = new JobViewOrder();
			for (Object obj : result) {
				Object[] tuple = (Object[]) obj;
				Object str1 = tuple[0];
				Object str2 = tuple[1];
				mdo.setOpenCount(Integer.valueOf(String.valueOf(str1)));
				mdo.setClosedCount(Integer.valueOf(String.valueOf(str2)));
				break;
			}
			System.out.println("mdo---->" + mdo);
			return mdo;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<JobViewOrder> getAllJobOrderStatsForExecutives(UserRoleVo userRoleVo) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select jsv.mon,jsv.year,sum(jsv.open_count) as open,sum(jsv.closed_count) as close from job_order_status_monthly_view jsv ");
			// sql.append(" group by
			// jsv.mon,jsv.year,jsv.open_count,jsv.closed_count,jsv.order_month");
			// sql.append(" order by jsv.order_month asc");

			if (userRoleVo != null) {
				sql.append(" where jsv.dm='" + userRoleVo.getUserName() + "'");
			}

			sql.append(" group by jsv.mon,jsv.order_month,jsv.year order by jsv.order_month, jsv.year desc ");

			List<?> result = findBySQLQuery(sql.toString(), null);
			JobViewOrder mdo = new JobViewOrder();
			List<JobViewOrder> resultMdo = new ArrayList<JobViewOrder>();
			for (Object obj : result) {
				mdo = new JobViewOrder();
				Object[] tuple = (Object[]) obj;
				Object str1 = tuple[0];
				Object str2 = tuple[1];
				Object str3 = tuple[2];
				Object str4 = tuple[3];
				mdo.setMonth(String.valueOf(str1));
				mdo.setYear(String.valueOf(str2).substring(0, 4));
				mdo.setOpenCount(Integer.valueOf(String.valueOf(str3)));
				mdo.setClosedCount(Integer.valueOf(String.valueOf(str4)));
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

	public List<JobViewOrder> getYearWiseStatusForExecutives(String year, UserRoleVo userRoleVo) {
		try {
			StringBuilder sql = new StringBuilder();
			Map<String, Object> params = new HashMap<String, Object>();
			sql.append("select jys.year,sum(jys.open_count) as open ,sum(jys.closed_count) as close from job_order_status_year_view jys ");
			if (year != null)
				sql.append(" where jys.year='" + year + "'");
			if (userRoleVo != null)
				sql.append(" and jys.dm='" + userRoleVo.getUserName() + "'");
			sql.append("GROUP by jys.year");
			List<?> result = findBySQLQuery(sql.toString(), params);
			JobViewOrder mdo = new JobViewOrder();
			List<JobViewOrder> resultMdo = new ArrayList<JobViewOrder>();
			for (Object obj : result) {
				mdo = new JobViewOrder();
				Object[] tuple = (Object[]) obj;
				Object str1 = tuple[0];
				Object str2 = tuple[1];
				Object str3 = tuple[2];
				mdo.setYear(String.valueOf(str1).substring(0, 4));
				mdo.setOpenCount(Integer.valueOf(String.valueOf(str2)));
				mdo.setClosedCount(Integer.valueOf(String.valueOf(str3)));
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.dao.JobOrderDao#getDmSubmittalStatusByUser(java.lang
	 * .String, java.util.Date, java.util.Date)
	 */

	@Override
	public List<ExecutiveResumeView> getYearWiseResumesStats() {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select rys.resume_year,rys.portal_name,rys.portal_count from resume_year_wise_stat_view  rys");
			sql.append(" group by rys.resume_year,rys.portal_name,rys.portal_count order by rys.resume_year desc");
			System.out.println("sql---->" + sql);
			List<?> result = findBySQLQuery(sql.toString(), null);
			ExecutiveResumeView mdo = new ExecutiveResumeView();
			List<ExecutiveResumeView> resultMdo = new ArrayList<ExecutiveResumeView>();
			for (Object obj : result) {
				mdo = new ExecutiveResumeView();
				Object[] tuple = (Object[]) obj;
				Object str1 = tuple[0];
				Object str2 = tuple[1];
				Object str3 = tuple[2];
				mdo.setYear(String.valueOf(str1).substring(0, 4));
				mdo.setPortalName(String.valueOf(str2));
				mdo.setPortalCount(String.valueOf(str3));
				resultMdo.add(mdo);
			}
			System.out.println("mdo---->" + mdo);
			return resultMdo;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<ExecutiveResumeView> getTodayResumesStats() {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select rds.portal_name,rds.portal_count from resume_day_wise_stat_view  rds");
			sql.append(" group by rds.portal_name,rds.portal_count");
			System.out.println("sql---->" + sql);
			List<?> result = findBySQLQuery(sql.toString(), null);
			System.out.println("result---->" + result);
			ExecutiveResumeView mdo = new ExecutiveResumeView();
			List<ExecutiveResumeView> resultMdo = new ArrayList<ExecutiveResumeView>();
			for (Object obj : result) {
				mdo = new ExecutiveResumeView();
				Object[] tuple = (Object[]) obj;
				Object str1 = tuple[0];
				Object str2 = tuple[1];
				mdo.setPortalName(String.valueOf(str1));
				mdo.setPortalCount(String.valueOf(str2));
				resultMdo.add(mdo);
			}
			System.out.println("mdo---->" + mdo);
			return resultMdo;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public ExecutiveResumeView getAllResumesCounts() {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select rcv.today_count,rcv.total_count from resume_count_view rcv");
			sql.append(" group by rcv.today_count,rcv.total_count");
			System.out.println("sql---->" + sql);
			List<?> result = findBySQLQuery(sql.toString(), null);
			ExecutiveResumeView resumeCounts = new ExecutiveResumeView();
			for (Object obj : result) {
				Object[] tuple = (Object[]) obj;
				Object str1 = tuple[0];
				Object str2 = tuple[1];
				resumeCounts.setToDayResumesCounts(Integer.valueOf(String.valueOf(str1)));
				resumeCounts.setTotalResumeCounts(Integer.valueOf(String.valueOf(str2)));
			}
			System.out.println("resumeCounts---->" + resumeCounts);
			return resumeCounts;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public int findJobOrdersCount(Map<String, Object> params, String sql) {
		try {
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			Query sqlQuery = session.createSQLQuery(sql.toString());

			if (params != null) {
				for (Map.Entry<String, Object> entry : params.entrySet()) {
					if (entry.getValue() instanceof Collection) {
						sqlQuery.setParameterList(entry.getKey(), (Collection<?>) entry.getValue());
					} else if (entry.getValue() instanceof Object[]) {
						sqlQuery.setParameterList(entry.getKey(), (Object[]) entry.getValue());
					} else {
						sqlQuery.setParameter(entry.getKey(), entry.getValue());
					}
				}
			}

			List list = sqlQuery.list();
			Number countValue = (Number) ((!Utils.isEmpty(list)) ? list.get(0) : 0);
			return countValue.intValue();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.dao.JobOrderDao#findSubmittalsDetails(java.util.Map,
	 * java.lang.String)
	 */
	@Override
	public List findSubmittalsDetails(Map<String, Object> params, String string) {
		try {
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
	 * com.uralian.cgiats.dao.JobOrderDao#saveOrder(com.uralian.cgiats.model.
	 * JobOrder)
	 */
	@Override
	public JobOrder saveOrder(JobOrder jobOrder) {
		try {
			final Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			Integer jobOrderId = (Integer) session.save(jobOrder);
			return findById(jobOrderId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.dao.JobOrderDao#getJobOrderDescription(java.lang.
	 * Integer)
	 */
	@Override
	public String getJobOrderDescription(Integer jobOrderId) {
		try {
			String description = null;
			final Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			Criteria criteria = session.createCriteria(JobOrder.class).setProjection(Projections.property("description"));
			criteria.add(Restrictions.eq("id", jobOrderId));
			List<?> list = criteria.list();
			if (list != null && list.size() > 0) {
				description = String.valueOf(list.get(0));
			}
			return description;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}
	private List<String> getSubordinatesWithUserIds(List<String> userIds) {
		StringBuffer hql = new StringBuffer();
		List<String> paramNames = new ArrayList<String>();
		List<Object> paramValues = new ArrayList<Object>();
		hql.append("select u1.userId from User u1 where u1.status = 'ACTIVE' and u1.assignedBdm in ?2");
		paramNames.add("2");
		paramValues.add(userIds);
		List<String> list = (List<String>) runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
		return list;
	}
}

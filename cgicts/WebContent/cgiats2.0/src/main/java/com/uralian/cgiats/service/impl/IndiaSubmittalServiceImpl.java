package com.uralian.cgiats.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.IndiaSubmittalDao;
import com.uralian.cgiats.dao.SubmittalDao;
import com.uralian.cgiats.dto.ReportwiseDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.IndiaSubmittal;
import com.uralian.cgiats.model.JobOrderSearchDto;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalEvent;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.service.IndiaSubmittalService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.MonthEnum;
import com.uralian.cgiats.util.Utils;

@Service
@Transactional(rollbackFor = ServiceException.class)
public class IndiaSubmittalServiceImpl implements IndiaSubmittalService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private IndiaSubmittalDao submittalDao;

	public List<IndiaSubmittal> findSubmittals(JobOrderSearchDto criteria) {
		try {
			// null criteria not allowed
			if (criteria == null)
				throw new IllegalArgumentException("Search criteria not set");

			if (log.isDebugEnabled())
				log.debug("Using following criteria " + criteria);

			StringBuffer hqlSelect = new StringBuffer("from IndiaSubmittal ");
			Map<String, Object> params = new HashMap<String, Object>();

			StringBuffer hqlWhere = new StringBuffer(" where deleteFlag!=1 ");
			buildWhereClause(criteria, hqlWhere, params);

			hqlSelect.append(hqlWhere);
			if (log.isDebugEnabled())
				log.debug("HQL Query " + hqlSelect.toString());

			// return orderDao
			// .findByQuery(hqlSelect.toString(), criteria.getStartPosition(),
			// criteria.getMaxResults(), params.toArray());
			return submittalDao.findByQuery(hqlSelect.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @param criteria
	 * @param hql
	 * @param params
	 */
	private void buildWhereClause(JobOrderSearchDto criteria, StringBuffer hql, Map<String, Object> params) {
		try {
			// add JobOrder fields
			log.info("Inside buildWhereClauseSubmittal :: Selected Submittal : " + criteria.getSubmittalBdms());

			if (!Utils.isEmpty(criteria.getSubmittalBdms())) {
				hql.append(" and createdBy = :createdby ");
				params.put("createdby", criteria.getSubmittalBdms());

				log.info("Inside Condition : " + criteria.getSubmittalBdms());
				log.info("HQL Condition : " + hql.toString());
			}
			if(criteria.getStartEntryDate()!=null){
				hql.append(" and createdOn>= :startDate");
				params.put("startDate", criteria.getStartEntryDate());
			}
			if(criteria.getEndEntryDate()!=null){
				hql.append(" and createdOn< :endDate");
				params.put("endDate", criteria.getEndEntryDate());
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = true) public List<Submittal>
	 * getListStartedCandidates() { StringBuffer hql = new StringBuffer();
	 * hql.append("select s from Submittal s where s.status =:status");
	 * hql.append(
	 * " and s.deleteFlag=0 and s.candidate not in (select c.candidate from CandidateInfo c) ORDER By s.candidate"
	 * ); return
	 * submittalDao.findByQuery(hql.toString(),"status",SubmittalStatus.STARTED)
	 * ; }
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.SubmittalService#getAllStarterdCandidates()
	 * 
	 * 
	 * @Override public List<Submittal> getAllStarterdCandidates(Date
	 * currentDate) { List<Submittal> subDetails =
	 * submittalDao.getAllStarterdCandidates(currentDate); return subDetails; }
	 * 
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.SubmittalService#getSubmittalStatusByBdm(java.
	 * lang.String, java.util.Date, java.util.Date)
	 * 
	 * @Override public Map<String, Map<SubmittalStatus, Integer>>
	 * getSubmittalStatusByBdm( User bdmName, Date dateStart, Date dateEnd) {
	 * Map<String, Map<SubmittalStatus, Integer>> map =
	 * submittalDao.getSubmittalStatusByBdm(bdmName,dateStart, dateEnd); //
	 * replace nulls with 0s for (Map<SubmittalStatus, Integer> stats :
	 * map.values()) { for (SubmittalStatus status : SubmittalStatus.values()) {
	 * if (!stats.containsKey(status)) stats.put(status, 0); } } return map; }
	 * 
	 * 
	 *//**
		 * @param bdmName
		 * @param dateStart
		 * @param dateEnd
		 * @return
		 *//*
		 * public Map<String, Map<SubmittalStatus, Integer>>
		 * getSubmittalStatusByDm( User bdmName, Date dateStart, Date dateEnd) {
		 * Map<String, Map<SubmittalStatus, Integer>> map =
		 * submittalDao.getSubmittalStatusByDm(bdmName,dateStart, dateEnd); //
		 * replace nulls with 0s for (Map<SubmittalStatus, Integer> stats :
		 * map.values()) { for (SubmittalStatus status :
		 * SubmittalStatus.values()) { if (!stats.containsKey(status))
		 * stats.put(status, 0); } } return map; }
		 * 
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.uralian.cgiats.service.SubmittalService#getSubmittalStatusByBdm(
		 * java.lang.String, java.util.Date, java.util.Date)
		 * 
		 * @Override public Map<String, Map<SubmittalStatus, Integer>>
		 * getSubmittalStatusByRecruiter( User user, Date dateStart, Date
		 * dateEnd) { Map<String, Map<SubmittalStatus, Integer>> map =
		 * submittalDao.getSubmittalStatusByRecruiter(user,dateStart, dateEnd);
		 * // replace nulls with 0s for (Map<SubmittalStatus, Integer> stats :
		 * map.values()) { for (SubmittalStatus status :
		 * SubmittalStatus.values()) { if (!stats.containsKey(status))
		 * stats.put(status, 0); } } return map; } (non-Javadoc)
		 * 
		 * @see
		 * com.uralian.cgiats.service.SubmittalService#getSubmittalStatusByBdm(
		 * java.lang.String, java.util.Date, java.util.Date)
		 * 
		 * @Override public Map<String, Map<SubmittalStatus, Integer>>
		 * getSubmittalStatusByRecruiterDM( User user, Date dateStart, Date
		 * dateEnd) { Map<String, Map<SubmittalStatus, Integer>> map =
		 * submittalDao.getSubmittalStatusByRecruiterDM(user,dateStart,
		 * dateEnd); // replace nulls with 0s for (Map<SubmittalStatus, Integer>
		 * stats : map.values()) { for (SubmittalStatus status :
		 * SubmittalStatus.values()) { if (!stats.containsKey(status))
		 * stats.put(status, 0); } } return map; }
		 * 
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.uralian.cgiats.service.SubmittalService#getSubmittalStatusByBdm(
		 * java.lang.String, java.util.Date, java.util.Date)
		 * 
		 * @Override public List<Submittal> getSubmittalDetailsbyStatus(
		 * SubmittalStatus status, Date dateStart, Date dateEnd,String
		 * createdUser) { List<Submittal> map =
		 * submittalDao.getSubmittalDetailsByStatus(status, dateStart, dateEnd,
		 * createdUser); return map; }
		 * 
		 * public List<Submittal> getAllOutofProjCandidates(Date fromDate,Date
		 * toDate){ List<Submittal> map =
		 * submittalDao.getAllOutofProjCandidates(fromDate, toDate); return map;
		 * }
		 * 
		 * public Integer editSubmittalEvent(SubmittalEvent sEvent) {
		 * 
		 * return submittalDao.editSubmittalEvent(sEvent); } // Code added
		 * Raghavendra 19/05/14.
		 * 
		 * @SuppressWarnings("unchecked")
		 * 
		 * @Override public List<Submittal> getDeletedSubmittals() {
		 * StringBuffer hql = new StringBuffer(); hql.append("from Submittal");
		 * hql.append(" where deleteFlag=1"); return (List<Submittal>)
		 * submittalDao.runQuery(hql.toString()); }
		 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.SubmittalService#getAllSubmittalStatusCounts(
	 * java.util.Date, java.util.Date)
	 */
	/*
	 * public Map<SubmittalStatus, Integer> getAllSubmittalStatusCounts(Date
	 * dateStart, Date dateEnd) { Map<SubmittalStatus, Integer> map =
	 * submittalDao.getAllSubmittalStatusCounts(dateStart, dateEnd); return map;
	 * }
	 */

	@Override
	public List<IndiaSubmittal> getCandidateJobrderStatus(Integer candidateId) {
		try {
			List<IndiaSubmittal> submList = submittalDao.getCandidateJobrderStatus(candidateId);

			return submList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IndiaSubmittal> getDeletedSubmittals() {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from IndiaSubmittal");
			hql.append(" where deleteFlag=1");
			return (List<IndiaSubmittal>) submittalDao.runQuery(hql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	
	@Override
	public List<?> getAllIndiaSubmittalYears() {
		List<Integer> list = null;
		try {
			StringBuffer sqlSelect = new StringBuffer(
					"select distinct year(s.createdOn) from IndiaSubmittal s where year(s.createdOn) is not null order by year(s.createdOn) DESC ");

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			List<?> resultlist = submittalDao.findByQuery(sqlSelect.toString(), null);
			if (resultlist != null && resultlist.size() > 0) {
				list = new ArrayList<Integer>();
				for (Object obj : resultlist) {
					list.add(Utils.getIntegerValueOfDoubleObj(obj));
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return list;
	}

	 

	@Override
	public Map<String, Integer> getDMWiseRecPerformanceTotalReport(ReportwiseDto reportwiseDto) {
		Map<String, Integer> userWithSubmittalCountMap = new HashMap<String, Integer>();
		try {
			StringBuffer sqlSelect = new StringBuffer("select count(s),s.status,u.user_id,u.assigned_bdm from india_submittal s,"
					+ "india_job_order j,user_acct u where s.delete_flag=0 and s.order_id=j.order_id and s.created_by=u.user_id "
					+ "  and u.status in ('ACTIVE','INACTIVE')  and u.user_role in ('IN_DM','IN_Recruiter', 'IN_TL')");
			if (reportwiseDto.getYear() != null) {
				sqlSelect.append("  and EXTRACT(YEAR from COALESCE(s.updated_on,s.created_on)) in (" + reportwiseDto.getYear() + ")");
			}
			if (reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() > 0) {
				sqlSelect
						.append(" and to_char(COALESCE(s.updated_on,s.created_on),'Mon') in (" + Utils.getListWithSingleQuote(reportwiseDto.getMonths()) + ")");
			}
			if (reportwiseDto.getDmNames() != null && reportwiseDto.getDmNames().size() > 0) {
				sqlSelect.append(" and (s.created_by in (" + Utils.getListWithSingleQuote(reportwiseDto.getDmNames()) + "))");
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() == 1
					&& reportwiseDto.getMonths().get(0).equalsIgnoreCase(MonthEnum.JAN.getMonth())) {
				sqlSelect.append(" and EXTRACT(week from COALESCE(s.updated_on,s.created_on)) in (" + reportwiseDto.getWeek() + ")");
			} else if (reportwiseDto.getWeek() != null) {
				sqlSelect.append(" and (extract(week from COALESCE(s.updated_on,s.created_on)) - "
						+ "extract(week from date_trunc('month', COALESCE(s.updated_on,s.created_on))) + 1) in (" + reportwiseDto.getWeek() + ")");
			}
			if (reportwiseDto.getStatuses() != null && reportwiseDto.getStatuses().size() > 0) {
				sqlSelect.append(" and s.status in (" + Utils.getListWithSingleQuote(reportwiseDto.getStatuses()) + ")");
			}
			sqlSelect.append(" group by s.status,u.user_id;");

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				log.info("Size of the Records ::: " + resultList.size());
				for (Object object : resultList) {
					Object[] obj = (Object[]) object;
					String userId = Utils.getStringValueOfObj(obj[2]);
					if (userWithSubmittalCountMap.get(userId) == null) {
						userWithSubmittalCountMap.put(userId, Utils.getIntegerValueOfBigDecimalObj(obj[0]));
					} else {
						userWithSubmittalCountMap.put(userId, (userWithSubmittalCountMap.get(userId) + Utils.getIntegerValueOfBigDecimalObj(obj[0])));
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return userWithSubmittalCountMap;
	}

	@Override
	public Map<String, Object> getIndiaDMAverageNoOfDaysForStatus(ReportwiseDto reportwiseDto) {
		Map<String, Object> serviceWiseMap = new HashMap<String, Object>();
		try {
			StringBuffer sqlSelect = new StringBuffer(
					"select round((sum(result.hours)/24)/count(result.status))," + "sum(result.hours),count(result.status) from (");
			// sqlSelect.append(" from (select (EXTRACT(EPOCH FROM
			// s.created_on-j.created_on)/3600) AS hours, ");
			// sqlSelect.append(" from (select (EXTRACT(EPOCH FROM
			// s.created_on-j.created_on)/3600) AS hours, ");

			if (reportwiseDto.getStatuses() != null && reportwiseDto.getStatuses().size() == 1
					&& !reportwiseDto.getStatuses().get(0).equalsIgnoreCase(SubmittalStatus.SUBMITTED.name())) {
				sqlSelect.append("select (EXTRACT(EPOCH FROM s.updated_on-s.created_on)/3600) AS hours, ");
			} else {
				sqlSelect.append("select (EXTRACT(EPOCH FROM s.created_on-j.created_on)/3600) AS hours, ");
			}

			sqlSelect.append("s.created_on AS sub_createdOn,j.created_on AS job_createdOn,s.created_by AS createdBy,u.user_id "
					+ ",(CASE WHEN u.assigned_bdm ISNULL THEN u.user_id ELSE u.assigned_bdm END) AS assignedbdm, "
					+ "j.order_id, s.status  from india_submittal s,india_job_order j,user_acct u " + "where  s.delete_flag=0 and s.created_by=u.user_id "
					+ "and j.order_id=s.order_id and j.delete_flg=0 " + "and u.user_role in ('IN_DM','IN_Recruiter','IN_TL') "
					// + "and s.status='STARTED' "
					+ "and u.status in ('ACTIVE','INACTIVE') ");
			// + "and EXTRACT(YEAR from COALESCE(s.updated_on,s.created_on)) in
			// (2016) "
			// + " and to_char(COALESCE(s.updated_on,s.created_on),'Mon') in
			// ('Jan','Feb','May','Sep') "
			// + "and (extract(week from COALESCE(s.updated_on,s.created_on)) -
			// "
			// + " extract(week from date_trunc('month',
			// COALESCE(s.updated_on,s.created_on))) + 1) "
			// + " in (1,2,3,4,5,6) "
			// + ") AS result "
			// + " group by result.assignedbdm,result.status ");
			if (reportwiseDto.getYear() != null) {
				sqlSelect.append("  and EXTRACT(YEAR from COALESCE(s.updated_on,s.created_on)) in (" + reportwiseDto.getYear() + ")");
			}
			if (reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() > 0) {
				sqlSelect
						.append(" and to_char(COALESCE(s.updated_on,s.created_on),'Mon') in (" + Utils.getListWithSingleQuote(reportwiseDto.getMonths()) + ")");
			}
			if (reportwiseDto.getDmNames() != null && reportwiseDto.getDmNames().size() > 0) {
				sqlSelect.append(" and (s.created_by in (" + Utils.getListWithSingleQuote(reportwiseDto.getDmNames()) + "))");
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() == 1
					&& reportwiseDto.getMonths().get(0).equalsIgnoreCase(MonthEnum.JAN.getMonth())) {
				sqlSelect.append(" and EXTRACT(week from COALESCE(s.updated_on,s.created_on)) in (" + reportwiseDto.getWeek() + ")");
			} else if (reportwiseDto.getWeek() != null) {
				sqlSelect.append(" and (extract(week from COALESCE(s.updated_on,s.created_on)) - "
						+ "extract(week from date_trunc('month', COALESCE(s.updated_on,s.created_on))) + 1) in (" + reportwiseDto.getWeek() + ")");
			}
			if (reportwiseDto.getStatuses() != null && reportwiseDto.getStatuses().size() > 0) {
				sqlSelect.append(" and s.status in (" + Utils.getListWithSingleQuote(reportwiseDto.getStatuses()) + ")");
			}
			sqlSelect.append(" )  AS result group by result.status ");

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				log.info("Size of the Records ::: " + resultList.size());
				for (Object object : resultList) {
					Object[] obj = (Object[]) object;
					serviceWiseMap.put(Constants.AVERAGE_DAYS, Utils.getIntegerValueOfDoubleObj(obj[0]));
					serviceWiseMap.put("count", obj[2]);
					serviceWiseMap.put("sum", obj[1]);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return serviceWiseMap;
	}

	@Override
	public Object indiadmfindHitRatio(ReportwiseDto reportwiseDto) {
		Map<String, Object> hitRatioMap = new HashMap<String, Object>();
		try {
			StringBuffer sqlSelect = new StringBuffer("SELECT " + " sum(Result.submitted_count) AS totalSubmittals, "
					+ "sum(Result.started_count) AS totalStarts, " + " round((sum(Result.started_count)/sum(Result.submitted_count))*100) AS hitRatio "
					+ " from " + " (select count(s) AS submitted_count, " + " count(CASE WHEN s.status='ACCEPTED' THEN 1 END) AS accepted_count, "
					+ " count(CASE WHEN s.status='DMREJ' THEN 1 END) AS dmrej_count, "
					+ " count(CASE WHEN s.status='INTERVIEWING' THEN 1 END) AS inteviewing_count, "
					+ " count(CASE WHEN s.status='CONFIRMED' THEN 1 END) AS confirmed_count, "
					+ " count(CASE WHEN s.status='REJECTED' THEN 1 END) AS rejected_count, "
					+ " count(CASE WHEN s.status='STARTED' THEN 1 END) AS started_count, "
					+ " count(CASE WHEN s.status='BACKOUT' THEN 1 END) AS backout_count, "
					+ " count(CASE WHEN s.status='OUTOFPROJ' THEN 1 END) AS outofproj_count, "
					+ " count(CASE WHEN s.status='SUBMITTED' THEN 1 END) AS nu_count, " + " u.user_id as userId,u.assigned_bdm, "
					+ " ((CASE WHEN u.first_name ISNULL THEN '' ELSE u.first_name END) ||' '|| "
					+ "  (CASE WHEN u.last_name ISNULL THEN '' ELSE u.last_name END)) AS full_name " + "  from india_submittal s,india_job_order j,user_acct u "
					+ " where s.delete_flag=0 and s.order_id=j.order_id " + " and s.created_by=u.user_id " + " and u.status in ('ACTIVE','INACTIVE') ");

			// + " and EXTRACT(YEAR from COALESCE(s.updated_on,s.created_on)) in
			// (2016) "
			// + " and to_char(COALESCE(s.updated_on,s.created_on),'Mon') in
			// ('Jan','Feb','May','Sep') "
			// + " and (extract(week from COALESCE(s.updated_on,s.created_on)) -
			// "
			// + " extract(week from date_trunc('month',
			// COALESCE(s.updated_on,s.created_on))) + 1) "
			// + " in (1,2,3,4,5,6) group by u.user_id)AS RESULT ");

			if (reportwiseDto.getYear() != null) {
				sqlSelect.append("  and EXTRACT(YEAR from COALESCE(s.updated_on,s.created_on)) in (" + reportwiseDto.getYear() + ")");
			}
			if (reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() > 0) {
				sqlSelect
						.append(" and to_char(COALESCE(s.updated_on,s.created_on),'Mon') in (" + Utils.getListWithSingleQuote(reportwiseDto.getMonths()) + ")");
			}
			if (reportwiseDto.getDmNames() != null && reportwiseDto.getDmNames().size() > 0) {
				sqlSelect.append(" and (s.created_by in (" + Utils.getListWithSingleQuote(reportwiseDto.getDmNames()) + "))");
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() == 1
					&& reportwiseDto.getMonths().get(0).equalsIgnoreCase(MonthEnum.JAN.getMonth())) {
				sqlSelect.append(" and EXTRACT(week from COALESCE(s.updated_on,s.created_on)) in (" + reportwiseDto.getWeek() + ")");
			} else if (reportwiseDto.getWeek() != null) {
				sqlSelect.append(" and (extract(week from COALESCE(s.updated_on,s.created_on)) - "
						+ "extract(week from date_trunc('month', COALESCE(s.updated_on,s.created_on))) + 1) in (" + reportwiseDto.getWeek() + ")");
			}
			if (reportwiseDto.getStatuses() != null && reportwiseDto.getStatuses().size() > 0) {
				sqlSelect.append(" and s.status in (" + Utils.getListWithSingleQuote(reportwiseDto.getStatuses()) + ")");
			}
			sqlSelect.append(" group by u.user_id) AS RESULT");

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				log.info("Size of the Records ::: " + resultList.size());
				for (Object object : resultList) {
					Object[] obj = (Object[]) object;
					hitRatioMap.put(Constants.HIT_RATIO, obj[2]);
					hitRatioMap.put(Constants.TOTAL_SUBMITTALS, obj[0]);
					hitRatioMap.put(Constants.TOTAL_STARTS, obj[1]);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return hitRatioMap;
	}

	@Override
	public Object getIndiadmsDetailsReport(Date from, Date to, String dmName, String status) {
		// TODO Auto-generated method stub
		
		List<SubmittalDto> subDtos = new ArrayList<SubmittalDto>();
		
		StringBuffer sql = new StringBuffer("");
		if (dmName != null && dmName.trim().length() > 0) {
			sql.append(" select result.assigned_BDM, count(result) AS submitted_count, "
				+ "count( CASE WHEN result.status ='CONFIRMED' THEN 1 END) AS confirmed_count, "
				+ "count( CASE WHEN result.status ='STARTED' THEN 1 END) AS started_count ,((CASE WHEN uaaa.first_name ISNULL THEN '' ELSE uaaa.first_name END)||' '||"
					+ "(CASE WHEN uaaa.last_name ISNULL THEN '' ELSE uaaa.last_name END)) AS full_name,d.name,d.min_start_count,d.max_start_count, d.avg_start_count from ( select u.user_id  AS assigned_BDM, ");
		} else {
			sql.append(" select newResult.userId,sum(newResult.submitted_count) AS sub_count,sum(newResult.confirmed_count) AS conf_count "
				+ " ,sum(newResult.started_count) AS start_count from"
				+ " (select ( CASE WHEN ua.user_role = 'IN_DM' THEN ua.user_id ELSE ua.assigned_bdm "
				+ " END) AS userId, count(result) AS submitted_count, "
				+ "count( CASE WHEN result.status ='CONFIRMED' THEN 1 END) AS confirmed_count, "
				+ "count( CASE WHEN result.status ='STARTED' THEN 1 END) AS started_count  from ( select (CASE WHEN u.user_role = 'IN_DM' THEN u.user_id ELSE u.assigned_bdm END  ) AS assigned_BDM, ");
		}
		sql.append(" s.status AS status  from india_submittal s,user_acct u where ");

		if (from != null) {
			sql.append(" COALESCE(s.updated_on , s.created_on) >= '" + from + "' AND ");
		}
		if (to != null) {
			sql.append(" COALESCE(s.updated_on , s.created_on) <= '" + to + "' AND ");
		}

		if (dmName != null && dmName.trim().length() > 0) {
			sql.append("  u.user_id in (select ua.user_id from user_acct ua where ua.assigned_bdm in (" + "    '" + dmName
					+ "') or ua.assigned_bdm IN (select uaa.user_id from user_acct uaa where " + "     uaa.assigned_bdm in ('" + dmName + "'))) and ");
		} else {
			sql.append(" u.user_role in('IN_DM','IN_Recruiter','IN_TL') and ");
		}

		sql.append(" s.delete_flag = 0 and s.created_by=u.user_id) AS result");
		if (dmName != null && dmName.trim().length() > 0) {
			sql.append(",user_acct uaaa,designation d " + "  where uaaa.user_id = result.assigned_BDM  and uaaa.level=d.id ");
			if (status != null && status.trim().length() > 0) {
				sql.append(" and uaaa.status='" + status + "'");
			}
			sql.append(" group by result.assigned_BDM,uaaa.user_id,d.name,d.min_start_count,d.max_start_count, d.avg_start_count ");
		} else {
			sql.append(",user_acct ua  where ua.user_id=result.assigned_BDM and ua.status='ACTIVE' group by result.assigned_BDM,ua.user_id) AS newResult "
					+ "group by newResult.userId");
		}

		Integer activeStarts=0,inActiveStarts=0;
		List<?> resultList = submittalDao.findBySQLQuery(sql.toString(), null);
		Map<String, SubmittalDto> submittalsMap = new HashMap<String, SubmittalDto>();
		
		if (resultList != null) {

			Iterator<?> itr = resultList.iterator();
			while (itr.hasNext()) {
				SubmittalDto dto = new SubmittalDto();
				Object[] obj = (Object[]) itr.next();
				String userId = (String) (obj[0] != null ? obj[0] : null);
				dto.setSubmittedCount(obj[1] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[1]).toString() : null);
				dto.setConfirmedCount(obj[2] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[2]).toString() : null);
				dto.setStartedCount(obj[3] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[3]).toString() : null);
				dto.setInActiveStartedCount("0");
				activeStarts+=Integer.parseInt(dto.getStartedCount());
				dto.setUserId(userId);
				if (dmName != null && dmName.trim().length() > 0) {
					dto.setDmName(Utils.getStringValueOfObj(obj[4]));
					dto.setLevel(Utils.getStringValueOfObj(obj[5]));
					dto.setMinStartCount(Utils.getIntegerValueOfBigDecimalObj(obj[6]));
					dto.setMaxStartCount(Utils.getIntegerValueOfBigDecimalObj(obj[7]));
					dto.setAvgStartCount(Utils.getIntegerValueOfBigDecimalObj(obj[8]));
				}

				submittalsMap.put(dto.getUserId(), dto);
			}

		}
		
		
		Map<String, SubmittalDto> inActiveStartedubmittalsMap = dmName != null?getInActiveStartedOfRecruiter(from, to, dmName, status,null,null):getInActiveStartedOfDM(from, to,null);
		if(inActiveStartedubmittalsMap!=null){
			for(String key:inActiveStartedubmittalsMap.keySet()){
				inActiveStarts+=Integer.parseInt(inActiveStartedubmittalsMap.get(key).getStartedCount());
				if(submittalsMap.get(key)!=null){
					submittalsMap.get(key).setStartedCount(String.valueOf(Integer.parseInt(submittalsMap.get(key).getStartedCount())+
							Integer.parseInt(inActiveStartedubmittalsMap.get(key).getStartedCount())));
					
					submittalsMap.get(key).setInActiveStartedCount(
							inActiveStartedubmittalsMap.get(key).getInActiveStartedCount());
					
				}else{
					submittalsMap.put(key, inActiveStartedubmittalsMap.get(key));
				}
			}
		}
		
		
		Map<String, SubmittalDto> jobOrderMap = getDmsStausOfJobOrders(from, to, dmName, status);
		subDtos = getAvgHiresPerDM(submittalsMap,jobOrderMap, from, to, dmName, status);

		Collections.sort(subDtos, new Comparator<SubmittalDto>() {
			@Override
			public int compare(SubmittalDto o1, SubmittalDto o2) {
				// TODO Auto-generated method stub
				return Double.valueOf(o2.getAvgHires()).compareTo(Double.valueOf(o1.getAvgHires()));
			}
		});
		
		
		int i = 1;
		List<String> categories = new ArrayList<String>();
		List<Double> series = new ArrayList<Double>();
		List<Map<String, Object>> seriesData = new ArrayList<Map<String, Object>>();
		Integer noOfJobOrders=0,noOfPositions=0,noOfSub=0,noOfConfirmed=0,noOfStarted=0;
		Integer noOfActiveRecs =0,noOfInActiveRecs=0;
		Map<String,Integer> activeRecLevelWithCountMap =new HashMap<String,Integer>();
		Map<String,Integer> inActiveRecLevelWithCountMap =new HashMap<String,Integer>();
		for (SubmittalDto dto : subDtos) {
			dto.setRank(String.valueOf(i++));

			String color = "",performanceStatus="";
			if (Double.valueOf(dto.getAvgHires()) <= (dmName != null ? (Double.valueOf(dto.getMinStartCount()) / 12f)  : (Double.valueOf(dto.getMinStartCount()) / 12f))) {
				color = Constants.RED;
				performanceStatus = Constants.POOR;
			} else if (Double.valueOf(dto.getAvgHires()) > (dmName != null ? ((Double.valueOf(dto.getMinStartCount()) / 12f) ) : (Double.valueOf(dto.getMinStartCount()) / 12f))
					&& Double.valueOf(dto.getAvgHires()) <= (dmName != null ? ((Double.valueOf(dto.getAvgStartCount()) / 12f)) : (Double.valueOf(dto.getAvgStartCount()) / 12f))) {
				color = Constants.YELLOW;
				performanceStatus = Constants.AVERAGE;
			} else if (Double.valueOf(dto.getAvgHires()) > (dmName != null ? ((Double.valueOf(dto.getAvgStartCount()) / 12f)) : (Double.valueOf(dto.getAvgStartCount()) / 12f))
					&& Double.valueOf(dto.getAvgHires()) <= (dmName != null ? ((Double.valueOf(dto.getMaxStartCount()) / 12f)) : (Double.valueOf(dto.getMaxStartCount()) / 12f))) {
				color = Constants.LIGHT_GREEN;
				performanceStatus = Constants.GOOD;
			} else if (Double.valueOf(dto.getAvgHires()) > (dmName != null ? ((Double.valueOf(dto.getMaxStartCount()) / 12f)) : (Double.valueOf(dto.getMaxStartCount()) / 12f))) {
				color = Constants.GREEN;
				performanceStatus = Constants.EXCELLENT;
			}

			noOfJobOrders+=Integer.parseInt(dto.getNoOfJobOrders());
			noOfPositions+=Integer.parseInt(dto.getNoOfPositions());
			noOfSub+=Integer.parseInt(dto.getSubmittedCount());
			noOfConfirmed+=Integer.parseInt(dto.getConfirmedCount());
			noOfStarted+=Integer.parseInt(dto.getStartedCount());
			
			if(dto.getActiveWithLevelMap()!=null){
				for(String level:dto.getActiveWithLevelMap().keySet()){
					if(activeRecLevelWithCountMap.get(level)!=null){
						activeRecLevelWithCountMap.put(level, dto.getActiveWithLevelMap().get(level)+activeRecLevelWithCountMap.get(level));
					}else{
						activeRecLevelWithCountMap.put(level, dto.getActiveWithLevelMap().get(level));
					}
				}
				for(String level:dto.getInActiveWithLevelMap().keySet()){
					if(inActiveRecLevelWithCountMap.get(level)!=null){
						inActiveRecLevelWithCountMap.put(level, dto.getInActiveWithLevelMap().get(level)+inActiveRecLevelWithCountMap.get(level));
					}else{
						inActiveRecLevelWithCountMap.put(level, dto.getInActiveWithLevelMap().get(level));
					}
				}
			}
			
			dto.setColor(color);
			dto.setPerformanceStatus(performanceStatus);
		}
		
		List<Map<String,Object>> finalActiveRecLevelWithCountList = new ArrayList<Map<String,Object>>(); 
		List<Map<String,Object>> finalInActiveRecLevelWithCountList = new ArrayList<Map<String,Object>>(); 
		if(activeRecLevelWithCountMap!=null){
			for(String level:activeRecLevelWithCountMap.keySet()){
				noOfActiveRecs+=activeRecLevelWithCountMap.get(level);
				Map<String,Object> levelWithCountMap = new HashMap<String,Object>();
				levelWithCountMap.put(Constants.NAME, level);
				levelWithCountMap.put(Constants.VALUE, activeRecLevelWithCountMap.get(level));
				finalActiveRecLevelWithCountList.add(levelWithCountMap);
			}
			
			Collections.sort(finalActiveRecLevelWithCountList,new Comparator<Map>() {

				@Override
				public int compare(Map o1, Map o2) {
					return ((Integer)o2.get(Constants.VALUE)).compareTo((Integer)o1.get(Constants.VALUE));
				}
			});
		}
		if(inActiveRecLevelWithCountMap!=null){
			for(String level:inActiveRecLevelWithCountMap.keySet()){
				noOfInActiveRecs+=inActiveRecLevelWithCountMap.get(level);
				Map<String,Object> levelWithCountMap = new HashMap<String,Object>();
				levelWithCountMap.put(Constants.NAME, level);
				levelWithCountMap.put(Constants.VALUE, inActiveRecLevelWithCountMap.get(level));
				finalInActiveRecLevelWithCountList.add(levelWithCountMap);
			}
		}
		
		//This is to display the vertical bar chart
				for (SubmittalDto dto : subDtos) {
					if((dmName==null && !dto.getNoOfRecs().equals(0)) || dmName!=null){
					series.add(Double.valueOf(dto.getAvgHires()));
					categories.add(dto.getDmName());

					Map<String, Object> map = new LinkedHashMap<String, Object>();

					String color = "";
					if (Double.valueOf(dto.getAvgHires()) <= (dmName != null ? ((Double.valueOf(dto.getMinStartCount()) / 12f)) : ((Double.valueOf(dto.getMinStartCount()) / 12f)))) {
						color = Constants.RED;
					} else if (Double.valueOf(dto.getAvgHires()) > (dmName != null ? ((Double.valueOf(dto.getMinStartCount()) / 12f)) : (Double.valueOf(dto.getMinStartCount()) / 12f))
							&& Double.valueOf(dto.getAvgHires()) <= (dmName != null ? ((Double.valueOf(dto.getAvgStartCount()) / 12f)) : (Double.valueOf(dto.getAvgStartCount()) / 12f))) {
						color = Constants.YELLOW;
					} else if (Double.valueOf(dto.getAvgHires()) > (dmName != null ? ((Double.valueOf(dto.getMinStartCount()) / 12f)) : (Double.valueOf(dto.getMinStartCount()) / 12f))
							&& Double.valueOf(dto.getAvgHires()) <= (dmName != null ? ((Double.valueOf(dto.getMaxStartCount()) / 12f)) : (Double.valueOf(dto.getMaxStartCount()) / 12f))) {
						color = Constants.LIGHT_GREEN;
					} else if (Double.valueOf(dto.getAvgHires()) > (dmName != null ? ((Double.valueOf(dto.getMaxStartCount()) / 12f)) : (Double.valueOf(dto.getMaxStartCount()) / 12f))) {
						color = Constants.GREEN;
					}

					map.put(Constants.Y, Double.valueOf(dto.getAvgHires()));
					map.put(Constants.COLOR, color);
					map.put(Constants.USERID, dto.getUserId());
					map.put(Constants.NO_OF_REC, dto.getNoOfRecs());
					seriesData.add(map);
					}
				}
				Map<String, Object> returnMap = new HashMap<String, Object>();
				returnMap.put(Constants.GRIDDATA, subDtos);
				returnMap.put(Constants.CATEGORIES, categories);
				returnMap.put(Constants.SERIES, series);
				returnMap.put(Constants.SERIES_DATA, seriesData);
				
				returnMap.put(Constants.NO_OF_DMS, subDtos!=null?subDtos.size():0);
				
				if(dmName==null){
				
				returnMap.put(Constants.NO_OF_ACTIVE_REC, noOfActiveRecs);
				returnMap.put(Constants.NO_OF_INACTIVE_REC, noOfInActiveRecs);
				returnMap.put(Constants.NO_OF_REC, noOfActiveRecs+noOfInActiveRecs);
				
			
				
				returnMap.put(Constants.ACTIVE_REC_LEVEL_LIST, finalActiveRecLevelWithCountList);
				returnMap.put(Constants.INACTIVE_REC_LEVEL_LIST, finalInActiveRecLevelWithCountList);
				}
				returnMap.put(Constants.NO_OF_JOB_ORDERS, noOfJobOrders);
				returnMap.put(Constants.NO_OF_POSITIONS, noOfPositions);
				returnMap.put(Constants.NO_OF_SUBMITTALS, noOfSub);
				returnMap.put(Constants.NO_OF_CONFIRMED, noOfConfirmed);
				returnMap.put(Constants.NO_OF_STARTS, noOfStarted);
				
				returnMap.put(Constants.NO_OF_STARTS, noOfStarted);
				
				List<Map<String, Object>> jobOrderOpenClosedCountList = getJobOrderOpenClosedCount(from, to, dmName);
				if(jobOrderOpenClosedCountList!=null && jobOrderOpenClosedCountList.size()>0){
					returnMap.put(Constants.JOB_ORDER_OPEN_CLOSED, jobOrderOpenClosedCountList.get(0));
					returnMap.put(Constants.JOB_ORDER_POSITIONS_OPEN_CLOSED, jobOrderOpenClosedCountList.get(1));
					
					returnMap.put(Constants.JOB_ORDER_SERVICE, jobOrderOpenClosedCountList.get(2));
					returnMap.put(Constants.JOB_ORDER_POSITIONS_SERVICE, jobOrderOpenClosedCountList.get(3));
					
				}else{
					returnMap.put(Constants.JOB_ORDER_OPEN_CLOSED, 0);
					returnMap.put(Constants.JOB_ORDER_POSITIONS_OPEN_CLOSED, 0);
					returnMap.put(Constants.JOB_ORDER_SERVICE, 0);
					returnMap.put(Constants.JOB_ORDER_POSITIONS_SERVICE, 0);
				}
				
				List<Map<String, Object>> submittalsOpenClosedCountList = getSubmittalOpenClosedCount(from, to, dmName,status);
				if(submittalsOpenClosedCountList!=null && submittalsOpenClosedCountList.size()>0){
					returnMap.put(Constants.SUBMITTALS_OPEN_CLOSED, submittalsOpenClosedCountList.get(0));
					returnMap.put(Constants.SUB_CONFIRMED_OPEN_CLOSED, submittalsOpenClosedCountList.get(1));
					returnMap.put(Constants.SUB_STARTED_OPEN_CLOSED, submittalsOpenClosedCountList.get(2));
					
					returnMap.put(Constants.SUB_STARTED_ACTIVE_COUNT, activeStarts);
					returnMap.put(Constants.SUB_STARTED_INACTIVE_COUNT, inActiveStarts);
					
					returnMap.put(Constants.SUBMITTAL_SERVICE, submittalsOpenClosedCountList.get(3));
					returnMap.put(Constants.SUB_CONFIRMED_SERVICE, submittalsOpenClosedCountList.get(4));
					returnMap.put(Constants.SUB_STARTED_SERVICE, submittalsOpenClosedCountList.get(5));
					
				}else{
					returnMap.put(Constants.SUBMITTALS_OPEN_CLOSED, 0);
					returnMap.put(Constants.SUB_CONFIRMED_OPEN_CLOSED, 0);
					returnMap.put(Constants.SUB_STARTED_OPEN_CLOSED, 0);
					returnMap.put(Constants.SUBMITTAL_SERVICE, 0);
					returnMap.put(Constants.SUB_CONFIRMED_SERVICE, 0);
					returnMap.put(Constants.SUB_STARTED_SERVICE, 0);
					
					returnMap.put(Constants.SUB_STARTED_ACTIVE_COUNT, 0);
					returnMap.put(Constants.SUB_STARTED_INACTIVE_COUNT, 0);
				}
				if(dmName!=null && status!=null && status.trim().length()>0){
					Map<String,Integer> activeInactiveUserCount = getActiveInActiveUserCount(from, to, dmName);
					returnMap.put(Constants.ACTIVE+"_"+Constants.INACTIVE, activeInactiveUserCount);
				}
				
				return returnMap;
	}

	private Map<String, SubmittalDto> getInActiveStartedOfDM(Date from, Date to,ReportwiseDto reportwiseDto) {
		Map<String, SubmittalDto> submittalsMap = new HashMap<String, SubmittalDto>();

		StringBuffer sql = new StringBuffer("select obj.userId,"
				+ "sum(obj.count) from"
				+ " (select (CASE WHEN ua.user_role = 'IN_DM' THEN ua.user_id ELSE "
				+ "  ua.assigned_bdm END) AS userId,"
				+ "  count(result.submittal_id) "
				+ "   from(Select result1.submittal_id, "
				+ "       result1.created_by, "
				+ "       result1.status, "
				+ "       (CASE  WHEN u.user_role = 'IN_DM' THEN u.user_id "
				+ "  ELSE u.assigned_bdm END) AS assigned_BDM "
				+ " from (select s.submittal_id, count(h.submittal_id) AS count, s.created_by, "
				+ " s.status from india_submittal s, india_submittal_history h where s.submittal_id =   h.submittal_id"
				+ " AND DATE (COALESCE(h.updated_on, h.created_on)) >= '"+from+"' "
				+ "   AND DATE (COALESCE(h.updated_on, h.created_on)) <= '"+to+"' AND h.status in ('STARTED') "
						+ "AND DATE (COALESCE(s.updated_on, s.created_on)) >= '"+from+"'"
						+ "   AND DATE (COALESCE(s.updated_on, s.created_on)) <= '"+to+"'"
				+ " AND s.delete_flag = 0 AND s.status in ('OUTOFPROJ')  group by s.submittal_id) AS result1,"
				+ " user_acct u where result1.created_by = u.user_id AND "
				+ "      u.user_role in ('IN_DM', 'IN_Recruiter','IN_TL')) AS result, "
				+ " user_acct ua where ua.user_id = result.assigned_BDM and "
				+ " ua.status = 'ACTIVE' group by result.assigned_BDM, ua.user_id)AS obj  group by obj.userId ");


		List<?> resultList = submittalDao.findBySQLQuery(sql.toString(), null);
		if (resultList != null) {
			Iterator<?> itr = resultList.iterator();
			while (itr.hasNext()) {
				SubmittalDto dto = new SubmittalDto();
				Object[] obj = (Object[]) itr.next();
				String userId = (String) (obj[0] != null ? obj[0] : null);
				dto.setStartedCount(obj[1] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[1]).toString() : "0");
				dto.setInActiveStartedCount(obj[1] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[1]).toString() : "0");
				dto.setActiveStartedCount("0");
				dto.setUserId(userId);
				
				submittalsMap.put(dto.getUserId(), dto);
			}

		}
		return submittalsMap;
	}

	public Map<String, SubmittalDto> getInActiveStartedOfRecruiter(Date from, Date to, String dmName, String status,ReportwiseDto reportwiseDto,String location) {
		Map<String, SubmittalDto> submittalsMap = new HashMap<String, SubmittalDto>();

		StringBuffer sql = new StringBuffer("   select result.created_by AS created_by,"
				+ " count(result.status) AS  started_count, "
				+ "   ((CASE   WHEN uaaa.first_name ISNULL THEN '' "
				+ "  ELSE uaaa.first_name END) || ' ' ||(CASE "
				+ "  WHEN uaaa.last_name ISNULL THEN '' "
				+ "  ELSE uaaa.last_name END)) AS full_name, "
				+ "        d.name, "
				+ "        d.min_start_count, "
				+ "        d.max_start_count "
				+ "   from(Select result1.submittal_id, "
				+ "       result1.created_by, "
				+ "       result1.status, "
				+ "       (CASE   WHEN u.user_role = 'IN_DM' THEN u.user_id   ELSE u.assigned_bdm END) AS assigned_BDM "
				+ " from (select s.submittal_id, count(h.submittal_id) AS count, s.created_by, "
				+ " s.status from india_submittal s, india_submittal_history h,user_acct u1 where s.submittal_id =   h.submittal_id"
				+ "  AND u1.user_id = s.created_by ");
		if(reportwiseDto!=null){
		if (reportwiseDto.getYear() != null) {
			sql.append("  and EXTRACT(YEAR from COALESCE(h.updated_on,h.created_on)) in (" + reportwiseDto.getYear() + ")");
		}
		if (reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() > 0) {
			sql
					.append(" and to_char(COALESCE(h.updated_on,h.created_on),'Mon') in (" + Utils.getListWithSingleQuote(reportwiseDto.getMonths()) + ")");
			sql
			.append(" and to_char(COALESCE(s.updated_on,s.created_on),'Mon') in (" + Utils.getListWithSingleQuote(reportwiseDto.getMonths()) + ")");
		}
		if (reportwiseDto.getWeek() != null && reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() == 1
				&& reportwiseDto.getMonths().get(0).equalsIgnoreCase(MonthEnum.JAN.getMonth())) {
			sql.append(" and EXTRACT(week from COALESCE(h.updated_on,h.created_on)) in (" + reportwiseDto.getWeek() + ")");
			sql.append(" and EXTRACT(week from COALESCE(s.updated_on,s.created_on)) in (" + reportwiseDto.getWeek() + ")");
		} else if (reportwiseDto.getWeek() != null) {
			sql.append(" and (extract(week from COALESCE(h.updated_on,h.created_on)) - "
					+ "extract(week from date_trunc('month', COALESCE(h.updated_on,h.created_on))) + 1) in (" + reportwiseDto.getWeek() + ")");
			sql.append(" and (extract(week from COALESCE(s.updated_on,s.created_on)) - "
					+ "extract(week from date_trunc('month', COALESCE(s.updated_on,s.created_on))) + 1) in (" + reportwiseDto.getWeek() + ")");
		}}else{
				
			sql.append( "   AND DATE (COALESCE(h.updated_on, h.created_on)) >= '"+from+"' "
				+ "   AND DATE (COALESCE(h.updated_on, h.created_on)) <= '"+to+"' "
						+ "AND DATE (COALESCE(s.updated_on, s.created_on)) >= '"+from+"'"
						+ "   AND DATE (COALESCE(s.updated_on, s.created_on)) <= '"+to+"'"
				);
		}
		sql.append(" AND h.status " + "    in ('STARTED')     ");
		if(dmName!=null && dmName.trim().length()>0){
		sql.append( " AND u1.user_id in (select ua.user_id from "
				+ "     user_acct ua where ua.assigned_bdm in ('" + dmName + "') or ua.assigned_bdm IN ( "
				+ "    select uaa.user_id from user_acct uaa where uaa.assigned_bdm in ('" + dmName + "'))");
		if (reportwiseDto != null) {
			sql.append(" or ua.user_id ='" + dmName + "'");
		}
		sql.append(") ");
		}
		if (location != null) {
		sql.append(" AND u1.office_location in( " + location + " )  ");
		}
		sql.append( " AND  s.delete_flag = 0 AND s.status in ('OUTOFPROJ') "
				+ "     group by s.submittal_id) AS result1,      user_acct u "
				+ " where result1.created_by = u.user_id AND"
				+ "      u.user_role in ('IN_DM', 'IN_Recruiter', 'IN_TL')) AS"
				+ "           result, user_acct uaaa, "
				+ "      designation d "
				+ "  where uaaa.user_id = result.created_by and "
				+ "       uaaa.level = d.id ");
				if (status != null && status.trim().length() > 0) {
					sql.append(" and uaaa.status='" + status + "'");
				}
		sql.append( " group by result.created_by, uaaa.user_id, d.name, d.min_start_count, d.max_start_count");


		List<?> resultList = submittalDao.findBySQLQuery(sql.toString(), null);
		if (resultList != null) {
			Iterator<?> itr = resultList.iterator();
			while (itr.hasNext()) {
				SubmittalDto dto = new SubmittalDto();
				Object[] obj = (Object[]) itr.next();
				String userId = (String) (obj[0] != null ? obj[0] : null);
				dto.setStartedCount(obj[1] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[1]).toString() : "0");
				dto.setInActiveStartedCount(obj[1] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[1]).toString() : "0");
				dto.setActiveStartedCount("0");
				dto.setUserId(userId);
				if (dmName != null && dmName.trim().length() > 0) {
					dto.setDmName(Utils.getStringValueOfObj(obj[2]));
					dto.setLevel(Utils.getStringValueOfObj(obj[3]));
					dto.setMinStartCount(Utils.getIntegerValueOfBigDecimalObj(obj[4]));
					dto.setMaxStartCount(Utils.getIntegerValueOfBigDecimalObj(obj[5]));
				}

				submittalsMap.put(dto.getUserId(), dto);
			}

		}
		return submittalsMap;
	}

	private Map<String, Integer> getActiveInActiveUserCount(Date from, Date to, String dmName) {

		Map<String,Integer> activeInactiveUserCount = new HashMap<String,Integer>();
		
		StringBuffer sql = new StringBuffer("");
			sql.append("select "
					+ "       result.status AS status, "
					+ "            result.role AS role"
					+ " from (select u.user_id AS user_id, u.join_date AS join_date, u.level AS level,"
					+ " d.min_start_count AS min_count, d.max_start_count AS max_count, ( CASE WHEN "
					+ "  u.relieving_date ISNULL THEN ( CASE WHEN u.status = 'INACTIVE' THEN "
					+ "   u.updated_on ELSE CURRENT_DATE END) ELSE u.relieving_date END) AS SERVEDDATE, "
					+ "    u.status AS status, ( CASE WHEN u.user_role = 'IN_DM' THEN u.user_id ELSE "
					+ "     u.assigned_bdm END) AS assigned_bdm, (( CASE WHEN u.first_name ISNULL THEN "
					+ "      ' ' ELSE u.first_name END) || ' ' ||( CASE WHEN u.last_name IS NULL THEN "
					+ "       ' ' ELSE u.last_name END)) AS rec_full_name, u.user_role AS role from "
					+ "        user_acct u, designation d where (((u.join_date <= "
					+ "         '"+from+"' OR u.join_date <= "
					+ "          '"+to+"') AND ( CASE WHEN u.relieving_date "
					+ "           ISNULL THEN ( CASE WHEN u.status = 'INACTIVE' THEN u.updated_on ELSE "
					+ "            CURRENT_DATE END) ELSE u.relieving_date END) >= "
					+ "             '"+from+"')) AND (u.assigned_bdm in ('"+dmName+"') OR "
					+ "              u.assigned_bdm in (select ua.user_id from user_acct ua where "
					+ "               ua.assigned_bdm in ('"+dmName+"'))) AND u.level = d.id ) AS result, "
					+ "     user_acct ua, "
					+ "     designation d "
					+ " where ua.user_id = result.assigned_bdm and       ua.level = d.id ");


		List<?> resultList = submittalDao.findBySQLQuery(sql.toString(), null);
		
		if (resultList != null) {
			
			activeInactiveUserCount.put(Constants.ACTIVE, 0);
			activeInactiveUserCount.put(Constants.INACTIVE, 0);
			Iterator<?> itr = resultList.iterator();
			while (itr.hasNext()) {
				Object[] obj =(Object[])itr.next();
				String status = Utils.getStringValueOfObj(obj[0]);
				if(status.equalsIgnoreCase(Constants.ACTIVE)){
					activeInactiveUserCount.put(Constants.ACTIVE, activeInactiveUserCount.get(Constants.ACTIVE)+1);
				}else{
					activeInactiveUserCount.put(Constants.INACTIVE, activeInactiveUserCount.get(Constants.INACTIVE)+1);
				}}
			}

		return activeInactiveUserCount;
	}

	private List<Map<String, Object>> getSubmittalOpenClosedCount(Date from, Date to, String dmName, String status) {

		List<Map<String, Object>> submittalOpenClosedCountList = new ArrayList<Map<String, Object>>();
		
		StringBuffer sql = new StringBuffer("");
			sql.append("select "
					+ " count((CASE WHEN result.job_status in ('OPEN','ASSIGNED','REOPEN') THEN 1 END)) AS open_count, "
					+ "count((CASE WHEN result.job_status in ('CLOSED','FILLED','HOLD') THEN 1 END)) AS closed, "
					+ " sum(result.confirmedOpenCount) AS cnf_open_count, "
					+ " sum(result.confirmedClosedCount) AS cnf_closed_count, "
					+ " sum(result.startedOpenCount) AS started_open_count, "
					+ " sum(result.startedClosedCount) AS started_closed_count, "
					+ "  count(CASE   WHEN result.open_days <= 30 THEN 1 END) AS open_less_than_30, "
					+ "   count(CASE   WHEN (result.open_days > 30 and result.open_days <= 90) THEN 1 END) AS open_less_than_90, "
					+ "       count(CASE   WHEN result.open_days > 90 THEN 1 END) AS open_more_than_90, "
					+ " sum(result.open_cf_less_than_30) AS open_cnf_less_than_30, "
					+ " sum(result.open_cf_less_than_90) AS open_cnf_less_than_90, "
					+ " sum(result.open_cf_more_than_90) AS open_cnf_more_than_90, "
					+ " sum(result.open_strd_less_than_30) AS open_started_less_than_30, "
					+ " sum(result.open_strd_less_than_90) AS open_started_less_than_90, "
					+ " sum(result.open_strd_more_than_90) AS open_started_more_than_90 "
					+ " from "
					+ " ( "
					+ " select "
					+ " res.job_status,res.confirmedOpenCount,res.confirmedClosedCount,res.open_pos,res.assigned_BDM,res.open_days, "
					+ " res.startedClosedCount,res.startedOpenCount, "
					+ " (CASE WHEN res.open_days <= 30 THEN res.confirmedOpenCount ELSE 0 END) AS open_cf_less_than_30, "
					+ " (CASE WHEN (res.open_days > 30 and res.open_days <= 90) THEN res.confirmedOpenCount ELSE 0 END) AS open_cf_less_than_90, "
					+ " (CASE WHEN res.open_days > 90 THEN res.confirmedOpenCount ELSE 0 END) AS open_cf_more_than_90, "
					+ " (CASE WHEN res.open_days <= 30 THEN res.startedOpenCount ELSE 0 END) AS open_strd_less_than_30, "
					+ " (CASE WHEN (res.open_days > 30 and res.open_days <= 90) THEN res.startedOpenCount ELSE 0 END) AS open_strd_less_than_90, "
					+ " (CASE WHEN res.open_days > 90 THEN res.startedOpenCount ELSE 0 END) AS open_strd_more_than_90 "
					+ "  from( "
					+ " select j.status AS job_status, "
					+ " (CASE WHEN s.status in ('CONFIRMED') THEN "
					+ " (CASE WHEN j.status in ('OPEN','ASSIGNED','REOPEN') THEN 1 END) ELSE 0  END) AS confirmedOpenCount, "
					+ "  (CASE WHEN s.status in ('CONFIRMED') THEN "
					+ " (CASE WHEN j.status in ('CLOSED','FILLED','HOLD') THEN 1  END) ELSE 0 END) AS confirmedClosedCount, "
					+ "  ( CASE WHEN j.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN "
					+ "  round((EXTRACT(EPOCH FROM date ( '"+to+"') - j.created_on) / 3600) / 24)  END)AS open_days,"
					+ " ( CASE WHEN j.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN   j.num_pos ELSE 0 END) AS open_pos, "
					+ "  ( CASE WHEN u.user_role = 'IN_DM' THEN u.user_id ELSE u.assigned_bdm END) AS assigned_BDM,"
					+ " (CASE WHEN s.status in ('STARTED') THEN "
					+ " (CASE WHEN j.status in ('OPEN','ASSIGNED','REOPEN') THEN 1 END) ELSE 0  END) AS startedOpenCount, "
					+ " (CASE WHEN s.status in ('STARTED') THEN "
					+ " (CASE WHEN j.status in ('CLOSED','FILLED','HOLD') THEN 1  END) ELSE 0  END) AS startedClosedCount "
					+ "  from india_submittal s, user_acct u,india_job_order j where "
					+ "       COALESCE(s.updated_on, s.created_on) >= '"+from+"' "
					+ "       AND j.order_id=s.order_id "
					+ "        AND COALESCE(s.updated_on, s.created_on) <= "
					+ "         '"+to+"' AND u.user_role in ('IN_DM' ,  'IN_Recruiter','IN_TL') "
					+ "  and s.delete_flag = 0 and s.created_by = u.user_id ");
		if (dmName != null && dmName.trim().length() > 0) {
			sql.append(" and u.user_id in (select ua.user_id from user_acct ua where ua.assigned_bdm in (" + "    '" + dmName
					+ "') or ua.assigned_bdm IN (select uaa.user_id from user_acct uaa where " + "     uaa.assigned_bdm in ('" + dmName + "')))  ");
		}
		if (status != null && status.trim().length() > 0) {
			sql.append(" AND u.status='" + status + "'");
		}
					sql.append( "   ) AS res) AS result,user_acct ua"
							+ " where ua.status='ACTIVE' and result.assigned_BDM = ua.user_id and ua.user_role = 'IN_DM' "); 
			


		List<?> resultList = submittalDao.findBySQLQuery(sql.toString(), null);
		
		if (resultList != null) {

			Iterator<?> itr = resultList.iterator();
			while (itr.hasNext()) {
				Object[] obj =(Object[])itr.next();
				Map<String, Object> submittalOpenClosedCountMap = new HashMap<String, Object>();
				submittalOpenClosedCountMap.put(Constants.OPEN, obj[0] != null ? obj[0] : 0);
				submittalOpenClosedCountMap.put(Constants.CLOSED, obj[1] != null ? obj[1] : 0);

				Map<String, Object> subConfirmedOpenClosedCountMap = new HashMap<String, Object>();
				subConfirmedOpenClosedCountMap.put(Constants.OPEN, obj[2] != null ? obj[2] : 0);
				subConfirmedOpenClosedCountMap.put(Constants.CLOSED, obj[3] != null ? obj[3] : 0);

				Map<String, Object> subStartedOpenClosedCountMap = new HashMap<String, Object>();
				subStartedOpenClosedCountMap.put(Constants.OPEN, obj[4] != null ? obj[4] : 0);
				subStartedOpenClosedCountMap.put(Constants.CLOSED, obj[5] != null ? obj[5] : 0);

				Map<String, Object> submittalServiceMap = new HashMap<String, Object>();
				submittalServiceMap.put(Constants.LESS_THAN_30_DAYS, obj[6] != null ? obj[6] : 0);
				submittalServiceMap.put(Constants.LESS_THAN_90_DAYS, obj[7] != null ? obj[7] : 0);
				submittalServiceMap.put(Constants.MORE_THAN_90_DAYS, obj[8] != null ? obj[8] : 0);

				Map<String, Object> submittalCnfServiceMap = new HashMap<String, Object>();
				submittalCnfServiceMap.put(Constants.LESS_THAN_30_DAYS, obj[9] != null ? obj[9] : 0);
				submittalCnfServiceMap.put(Constants.LESS_THAN_90_DAYS, obj[10] != null ? obj[10] : 0);
				submittalCnfServiceMap.put(Constants.MORE_THAN_90_DAYS, obj[11] != null ? obj[11] : 0);

				Map<String, Object> submittalStartedServiceMap = new HashMap<String, Object>();
				submittalStartedServiceMap.put(Constants.LESS_THAN_30_DAYS, obj[12] != null ? obj[12] : 0);
				submittalStartedServiceMap.put(Constants.LESS_THAN_90_DAYS, obj[13] != null ? obj[13] : 0);
				submittalStartedServiceMap.put(Constants.MORE_THAN_90_DAYS, obj[14] != null ? obj[14] : 0);
				
				submittalOpenClosedCountList.add(submittalOpenClosedCountMap);
				submittalOpenClosedCountList.add(subConfirmedOpenClosedCountMap);
				submittalOpenClosedCountList.add(subStartedOpenClosedCountMap);
				submittalOpenClosedCountList.add(submittalServiceMap);
				submittalOpenClosedCountList.add(submittalCnfServiceMap);
				submittalOpenClosedCountList.add(submittalStartedServiceMap);
			}

		}
		return submittalOpenClosedCountList;
	}

	private List<Map<String, Object>> getJobOrderOpenClosedCount(Date from, Date to, String dmName) {

		List<Map<String, Object>> jobOrderOpenClosedCountList = new ArrayList<Map<String, Object>>();
		
		StringBuffer sql = new StringBuffer("");
			sql.append("select count((CASE"
					+ "   WHEN result.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN 1 END)) AS open_count, "
					+ "       count((CASE   WHEN result.status in ('CLOSED', 'FILLED','HOLD') THEN 1 END)) AS closed, "
					+ "       sum(result.open_pos) AS open_positons, "
					+ "       sum(result.closed_pos) AS closed_postions, "
					+ "       count(CASE   WHEN result.open_days <= 30 THEN 1 END) AS open_less_than_30, "
					+ "       count(CASE   WHEN (result.open_days > 30 and result.open_days <= 90) THEN 1 END) AS open_less_than_90, "
					+ "       count(CASE   WHEN result.open_days > 90 THEN 1 END) AS open_more_than_90, "
					+ " sum(result.open_less_than_30) AS open_pos_less_than_30, "
					+ " sum(result.open_less_than_90) AS open_pos_less_than_90, "
					+ " sum(result.open_more_than_90) AS open_pos_more_than_90 "
					+ " from ( "
					+ " select "
					+ " res.assigned_BDM,res.status,res.createdBy,res.open_pos,res.closed_pos,res.open_days, "
					+ " (CASE WHEN res.open_days <= 30 THEN res.open_pos ELSE 0 END) AS open_less_than_30, "
					+ " (CASE WHEN (res.open_days > 30 and res.open_days <= 90) THEN res.open_pos ELSE 0 END) AS open_less_than_90, "
					+ " (CASE WHEN res.open_days > 90 THEN res.open_pos ELSE 0 END) AS open_more_than_90 "
					+ "  from ( "
					+ " select ( CASE WHEN u.user_role = 'IN_DM' THEN u.user_id ELSE u.assigned_bdm "
					+ "END) AS assigned_BDM, j.status AS status, COALESCE(j.dmname, j.created_by) AS "
					+ " createdBy, ( CASE WHEN j.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN "
					+ "  j.num_pos ELSE 0 END) AS open_pos, ( CASE WHEN j.status in ('CLOSED', 'FILLED','HOLD')    THEN j.num_pos ELSE 0 END) AS closed_pos, "
					+ "   ( CASE WHEN j.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN  "
					+ "  round((EXTRACT(EPOCH FROM date ( "
					+ "'"+to+"') - j.created_on) / 3600) / 24)  END)AS open_days "
					+ "    from india_job_order j, user_acct u where "
					+ "    j.delete_flg = 0 and COALESCE(j.dmname, j.created_by) = u.user_id and "
					+ "     u.user_role in ('IN_DM', 'IN_Recruiter','IN_TL') and j.created_on >= "
					+ "      '"+from+"' and j.created_on <= "
					+ "       '"+to+"' "
					+ "       )AS res "
					+ "       ) AS result, "
					+ "     user_acct ua "
					+ " where ua.user_id = result.assigned_BDM and "
					+ "      ua.status = 'ACTIVE' and       ua.user_role = 'IN_DM' ");
			if (dmName != null && dmName.trim().length() > 0) {
					sql.append(" and ua.user_id = '"+dmName+"' ");
			}


		List<?> resultList = submittalDao.findBySQLQuery(sql.toString(), null);
		
		if (resultList != null) {

			Iterator<?> itr = resultList.iterator();
			while (itr.hasNext()) {
				Object[] obj =(Object[])itr.next();
				Map<String, Object> jobOrderOpenClosedCountMap = new HashMap<String, Object>();
				jobOrderOpenClosedCountMap.put(Constants.OPEN, obj[0] != null ? obj[0] : 0);
				jobOrderOpenClosedCountMap.put(Constants.CLOSED, obj[1] != null ? obj[1] : 0);

				Map<String, Object> jobOrderPostionsOpenClosedCountMap = new HashMap<String, Object>();
				jobOrderPostionsOpenClosedCountMap.put(Constants.OPEN, obj[2] != null ? obj[2] : 0);
				jobOrderPostionsOpenClosedCountMap.put(Constants.CLOSED, obj[3] != null ? obj[3] : 0);

				Map<String, Object> jobOrderServiceMap = new HashMap<String, Object>();
				jobOrderServiceMap.put(Constants.LESS_THAN_30_DAYS, obj[4] != null ? obj[4] : 0);
				jobOrderServiceMap.put(Constants.LESS_THAN_90_DAYS, obj[5] != null ? obj[5] : 0);
				jobOrderServiceMap.put(Constants.MORE_THAN_90_DAYS, obj[6] != null ? obj[6] : 0);

				Map<String, Object> jobOrderPositionServiceMap = new HashMap<String, Object>();
				jobOrderPositionServiceMap.put(Constants.LESS_THAN_30_DAYS, obj[7] != null ? obj[7] : 0);
				jobOrderPositionServiceMap.put(Constants.LESS_THAN_90_DAYS, obj[8] != null ? obj[8] : 0);
				jobOrderPositionServiceMap.put(Constants.MORE_THAN_90_DAYS, obj[9] != null ? obj[9] : 0);
				
				jobOrderOpenClosedCountList.add(jobOrderOpenClosedCountMap);
				jobOrderOpenClosedCountList.add(jobOrderPostionsOpenClosedCountMap);
				jobOrderOpenClosedCountList.add(jobOrderServiceMap);
				jobOrderOpenClosedCountList.add(jobOrderPositionServiceMap);
				
			
			}

		}
		return jobOrderOpenClosedCountList;
	}

	private List<SubmittalDto> getAvgHiresPerDM(Map<String, SubmittalDto> submittalMap,Map<String, SubmittalDto> jobOrderMap, Date from, Date to, String dmName, String status) {
		
		
		List<SubmittalDto> subDtos = new ArrayList<SubmittalDto>();
		List<String> listids = new ArrayList<String>();
		if (dmName != null && dmName.trim().length() > 0) {
			listids.add(dmName);
		} 
		if (from != null && to != null) {
			StringBuffer sql = new StringBuffer("select "
					+ " result.user_id,"
					+ " result.join_date,result.level,"
					+ " result.min_count,"
					+ " result.max_count,"
					+ " result.SERVEDDATE,"
					+ " result.status,"
					+ " ( CASE WHEN ua.user_role = 'IN_DM' THEN ua.user_id ELSE ua.assigned_bdm"
					+ " END) AS assigned_bdm,((CASE WHEN ua.first_name ISNULL THEN ' ' ELSE ua.first_name END) || ' '||"
					+ "				(CASE WHEN ua.last_name IS NULL THEN ' ' ELSE ua.last_name END)) AS full_name,"
					+ " result.rec_full_name,d.name,result.role,result.name AS recLevel, result.avg_count "
					+ "  from "
					+ "(select u.user_id AS user_id, "
					+ "       u.join_date AS join_date, "
					+ "       u.level AS level, "
					+ "       d.min_start_count AS min_count,"
					+ "       d.avg_start_count AS avg_count,"
					+ "       d.max_start_count AS max_count, "
					+ "       (CASE   WHEN u.relieving_date ISNULL THEN (CASE "
					+ "  WHEN u.status = 'INACTIVE' THEN u.updated_on "
					+ "   ELSE CURRENT_DATE"
					+ " END)   ELSE u.relieving_date "
					+ " END) AS SERVEDDATE, "
					+ "       u.status AS status, "
					+ "       (CASE "
					+ "  WHEN u.user_role = 'IN_DM' THEN u.user_id "
					+ "  ELSE u.assigned_bdm END) AS assigned_bdm,((CASE"
					+ "  WHEN u.first_name ISNULL THEN ' ' "
					+ "  ELSE u.first_name "
					+ " END) || ' ' ||(CASE "
					+ "  WHEN u.last_name IS NULL THEN ' ' "
					+ "  ELSE u.last_name "
					+ "END)) AS rec_full_name,u.user_role AS role,d.name AS name from user_acct u,designation d " + "where ("
					+ " ((u.join_date <= '" + from + "' OR u.join_date <= '" + to + "') AND (CASE WHEN u.relieving_date ISNULL THEN ( "
					+ "CASE WHEN u.status='INACTIVE' THEN u.updated_on ELSE  CURRENT_DATE " + "  END) ELSE u.relieving_date END) >= '" + from
					+ "'))");
			if(listids!=null && listids.size()>0){
			sql.append( " AND (u.assigned_bdm in (" + Utils.getListWithSingleQuote(listids) + ") OR"
					+ "   u.assigned_bdm in (select ua.user_id from user_acct ua where" + "  ua.assigned_bdm in (" + Utils.getListWithSingleQuote(listids)
					+ ")))");}
			sql.append( " AND u.level = d.id  ");
			if(dmName!=null && status!=null && status.trim().length()>0){
				sql.append(" and u.status='"+status+"'");
			}
					sql.append(") AS result,user_acct ua,designation d "
					+ "      where ua.user_id = result.assigned_bdm and ua.level = d.id  ");
			

			List<?> resultList = submittalDao.findBySQLQuery(sql.toString(), null);
			Map<String, List<UserDto>> usersmap = new HashMap<String, List<UserDto>>();
			Map<String,String> recIdMap=new HashMap<String,String>();
			Map<String,Map<String,Map<String,Integer>>> dmWithActiveInActiveUsers = new HashMap<String,Map<String,Map<String,Integer>>>();
			if (resultList != null) {
				Iterator<?> itr = resultList.iterator();
				List<UserDto> usersList = new ArrayList<UserDto>();
				while (itr.hasNext()) {
					Object[] obj = (Object[]) itr.next();
					UserDto users = new UserDto();
					
					users.setUserId((String) (obj[0] != null ? obj[0] : null));
					users.setJoinDate(obj[1] != null ? (Date) obj[1] : null);
					users.setMinStartCount(obj[3] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[3]) : null);
					users.setMaxStartCount(obj[4] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[4]) : null);
					users.setServedDate(obj[5] != null ? (Date) obj[5] : null);
					users.setStatus((String) (obj[6] != null ? obj[6] : null));
					users.setAssignedBdm((String) (obj[7] != null ? obj[7] : null));
					users.setFullName(Utils.getStringValueOfObj(obj[8]));
					users.setRecFullName(Utils.getStringValueOfObj(obj[9]));
					users.setLevel(Utils.getStringValueOfObj(obj[12]));
					users.setAvgStartCount(obj[13] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[13]) : null);
					users.setUserRole(UserRole.valueOf(Utils.getStringValueOfObj(obj[11])));
					usersList.add(users);
					recIdMap.put(users.getUserId(), users.getUserId());
					if(dmName==null){
					if (usersmap.get(users.getAssignedBdm()) != null) {
						usersmap.get(users.getAssignedBdm()).add(users);
						
						Map<String,Map<String,Integer>> activeInactiveUserCount = dmWithActiveInActiveUsers.get(users.getAssignedBdm());
						if(!users.getUserRole().equals(UserRole.IN_DM)){
						if(users.getStatus().equalsIgnoreCase(Constants.ACTIVE)){
							getCountsByLevel(activeInactiveUserCount, users, Constants.ACTIVE);
						}else{
							getCountsByLevel(activeInactiveUserCount, users, Constants.INACTIVE);
						}}
					} else {
						List<UserDto> dtoList = new ArrayList<UserDto>();
						dtoList.add(users);
						usersmap.put(users.getAssignedBdm(), dtoList);
						
						Map<String,Map<String,Integer>> activeInactiveUserCount = new HashMap<String,Map<String,Integer>>();
						Map<String,Integer> levelWiseMap=new HashMap<String,Integer>();
						activeInactiveUserCount.put(Constants.ACTIVE, levelWiseMap);
						activeInactiveUserCount.put(Constants.INACTIVE, levelWiseMap);
						if(!users.getUserRole().equals(UserRole.IN_DM)){
						if(users.getStatus().equalsIgnoreCase(Constants.ACTIVE)){
							getCountsByLevel(activeInactiveUserCount, users, Constants.ACTIVE);
						}else{
							getCountsByLevel(activeInactiveUserCount, users, Constants.INACTIVE);
						}}
						dmWithActiveInActiveUsers.put(users.getAssignedBdm(), activeInactiveUserCount);
					}}else{
						if (usersmap.get(users.getUserId()) != null) {
							usersmap.get(users.getUserId()).add(users);
						} else {
							List<UserDto> dtoList = new ArrayList<UserDto>();
							dtoList.add(users);
							usersmap.put(users.getUserId(), dtoList);
						}
					}
				}
			}
			
			
			
			
			
			Map<String, Double> servedCounts = new HashMap<String, Double>();

			for (String key : usersmap.keySet()) {
				List<UserDto> dtos = usersmap.get(key);
				long serveddays = 0;
				Iterator<UserDto> itr = dtos.iterator();
				Date endDate = null;
				while (itr.hasNext()) {
					long userServeddays = 0;
					UserDto dto = itr.next();
					if(!dto.getUserRole().equals(UserRole.IN_DM)){
					if (dto.getJoinDate().compareTo(from) > 0) {

						if (dto.getServedDate().compareTo(to) < 0) {
							endDate = dto.getServedDate();
						} else {
							endDate = to;
						}
						serveddays += (endDate.getTime() - dto.getJoinDate().getTime()) / 86400000;
						userServeddays = (endDate.getTime() - dto.getJoinDate().getTime()) / 86400000;
					} else {
						if (dto.getServedDate().compareTo(to) < 0) {
							endDate = dto.getServedDate();
						} else {
							endDate = to;
						}
						serveddays += (endDate.getTime() - from.getTime()) / 86400000;
						userServeddays = (endDate.getTime() - from.getTime()) / 86400000;

					}
					if (dmName != null && dmName.trim().length() > 0) {
						servedCounts.put(dto.getUserId(), Double.valueOf(userServeddays));
					}
					}
				}
				if (dmName == null) {
					servedCounts.put(key, Double.valueOf(serveddays));
				}

			}
			Map<String,SubmittalDto> subCountsmap = new LinkedHashMap<String,SubmittalDto>();
				if(usersmap!=null ){
					for(String key:usersmap.keySet()){
						for(UserDto userDto:usersmap.get(key)){
							SubmittalDto subDto = new SubmittalDto();
							if((userDto.getUserRole().equals(UserRole.IN_DM) && userDto.getStatus().equals(Constants.ACTIVE))|| dmName!=null){
							subDto.setDmName(userDto.getRecFullName());
							subDto.setLevel(userDto.getLevel());
							subDto.setNoOfJobOrders(jobOrderMap.get(key)!=null?jobOrderMap.get(key).getNoOfJobOrders():"0");
							subDto.setNoOfPositions(jobOrderMap.get(key)!=null?jobOrderMap.get(key).getNoOfPositions():"0");
							subDto.setMinStartCount(userDto.getMinStartCount());
							subDto.setMaxStartCount(userDto.getMaxStartCount());
							subDto.setAvgStartCount(userDto.getAvgStartCount());
							subDto.setSubmittedCount(submittalMap.get(key)!=null?submittalMap.get(key).getSubmittedCount():"0");
							subDto.setConfirmedCount(submittalMap.get(key)!=null?submittalMap.get(key).getConfirmedCount():"0");
							subDto.setStartedCount(submittalMap.get(key)!=null?submittalMap.get(key).getStartedCount():"0");
							
							subDto.setActiveStartedCount(submittalMap.get(key)!=null?submittalMap.get(key).getActiveStartedCount():"0");
							subDto.setInActiveStartedCount(submittalMap.get(key)!=null?submittalMap.get(key).getInActiveStartedCount():"0");
							
							subDto.setUserId(userDto.getUserId());
							subDto.setStatus(userDto.getStatus());
							if(dmWithActiveInActiveUsers.get(key)!=null){
							subDto.setActiveWithLevelMap(dmWithActiveInActiveUsers.get(key).get(Constants.ACTIVE));
							subDto.setInActiveWithLevelMap(dmWithActiveInActiveUsers.get(key).get(Constants.INACTIVE));
							}
							subCountsmap.put(userDto.getUserId(), subDto);
							}
						}
					}
				}

			for (String finalKey : subCountsmap.keySet()) {
				SubmittalDto subDto = null;
				if (servedCounts.containsKey(finalKey)) {
					subDto = (SubmittalDto) subCountsmap.get(finalKey);
					if (subDto.getStartedCount() != null) {
						double avgHires = 0d;
						if (dmName != null && dmName.trim().length() > 0) {
							avgHires = Double.valueOf(subDto.getStartedCount());
							if (servedCounts.get(finalKey) != 0) {
								avgHires = avgHires / (servedCounts.get(finalKey) / 30);
							}
						} else {
							avgHires = Double.valueOf(subDto.getStartedCount()) / Double.valueOf(((usersmap.get(finalKey).size())-1)==0?1:(usersmap.get(finalKey).size())-1);
							if (servedCounts.get(finalKey) != 0) {
								avgHires = avgHires / (servedCounts.get(finalKey) / Double.valueOf((usersmap.get(finalKey).size())-1) / 30);
							}
						}

						subDto.setAvgHires(String.valueOf(Utils.getTwoDecimalDoubleFromObj(avgHires)));
					} else {
						subDto.setAvgHires(String.valueOf(0.0f));
					}
				} else {
					subDto = (SubmittalDto) subCountsmap.get(finalKey);
					subDto.setAvgHires(String.valueOf(0.0f));
				}
				subDto.setNoOfRecs(usersmap.get(finalKey) != null ? usersmap.get(finalKey).size()-1 : 0);
				if(subDto.getDmName()==null){
					subDto.setDmName(usersmap.get(finalKey).get(0).getFullName());
				}
				subDtos.add(subDto);
			}
		}

		return subDtos;
	}

	private void getCountsByLevel(Map<String,Map<String,Integer>> activeInactiveUserCount,UserDto users,String userStatus) {
		Map<String,Integer> levelWiseMap = null;
		if(activeInactiveUserCount.get(userStatus).get(users.getLevel())!=null){
			levelWiseMap= activeInactiveUserCount.get(userStatus);
			levelWiseMap.put(users.getLevel(), activeInactiveUserCount.get(userStatus).get(users.getLevel())+1);
			activeInactiveUserCount.put(userStatus, levelWiseMap);
		}else{
			levelWiseMap=new HashMap<String,Integer>();
			if(activeInactiveUserCount.get(userStatus)!=null){
				for(String key:activeInactiveUserCount.get(userStatus).keySet()){
					levelWiseMap.put(key, activeInactiveUserCount.get(userStatus).get(key));
				}
			}			
			levelWiseMap.put(users.getLevel(), 1);
			activeInactiveUserCount.put(userStatus, levelWiseMap);
		}
	}

	private Map<String, SubmittalDto> getDmsStausOfJobOrders( Date from, Date to, String dmName, String status) {
		Map<String, SubmittalDto> jobOrderMap = new HashMap<String, SubmittalDto>();

		StringBuffer sql = new StringBuffer("select ((CASE WHEN ua.first_name ISNULL THEN ' ' ELSE ua.first_name END) || ' '||"
				+ "(CASE WHEN ua.last_name IS NULL THEN ' ' ELSE ua.last_name END)) AS full_name, "
				+ "count(result.no_of_pos),sum(result.no_of_pos),result.assigned_BDM from " + "(select j.num_pos AS no_of_pos,");

		if (dmName != null && dmName.trim().length() > 0) {
			sql.append(" u.user_id  AS assigned_BDM ");
		} else {
			sql.append("  (CASE WHEN u.user_role = 'IN_DM' THEN u.user_id ELSE u.assigned_bdm END  ) AS assigned_BDM ");
		}

		sql.append("from  india_job_order j,user_acct u where j.delete_flg=0");
		if (from != null) {
			sql.append(" AND j.created_on >= '" + from + "' ");
		}
		if (to != null) {
			sql.append(" AND j.created_on <= '" + to + "' ");
		}
		if (dmName != null && dmName.trim().length() > 0) {
			sql.append(" and u.user_id = j.created_by and  j.assigned_to in (select ua.user_id from user_acct ua where ua.assigned_bdm in (" + "    '" + dmName
					+ "') or ua.assigned_bdm IN (select uaa.user_id from user_acct uaa where " + "     uaa.assigned_bdm in ('" + dmName + "')))  ");
		} else {
			sql.append(" and u.user_id = COALESCE(j.dmname,j.created_by) and u.user_role in('IN_DM','IN_Recruiter','IN_TL')  ");
		}


		sql.append(") AS result ,user_acct ua");

		if (dmName != null && dmName.trim().length() > 0) {
			sql.append(" where ua.user_id=result.assigned_BDM");
			if (status != null && status.trim().length() > 0) {
				sql.append(" and ua.status='" + status + "' ");
			}
			sql.append(" group by result.assigned_BDM,ua.user_id");
		} else {
			sql.append(" where ua.user_id=result.assigned_BDM and ua.status='ACTIVE' and ua.user_role='IN_DM' group by result.assigned_BDM,ua.user_id");
		}

		List<?> resultList = submittalDao.findBySQLQuery(sql.toString(), null);
			if (resultList != null) {
				Iterator<?> itr = resultList.iterator();
				
				while (itr.hasNext()) {
					Object[] obj = (Object[]) itr.next();
					SubmittalDto dto = new SubmittalDto();
					Integer noOfpos = obj[1] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[1]) : null;
					Integer noOfjob = obj[2] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[2]) : null;
					dto.setDmName(obj[0] != null ? (String) obj[0] : null);
					dto.setNoOfJobOrders(noOfpos.toString());
					dto.setNoOfPositions(noOfjob.toString());
					dto.setUserId(obj[3] != null ? (String) obj[3] : null);
					jobOrderMap.put(dto.getUserId(), dto);
				}
			}
		return jobOrderMap;
	}

	@Override
	public Map<String, Object> getIndiaClientReportData(Date fromDate, Date endDate) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<SubmittalDto> dtos = new ArrayList<SubmittalDto>();
		StringBuffer sql = null;
		if(fromDate != null && endDate != null){
		  sql = new StringBuffer("select result.customer AS client, count(result.orderCount) AS orderCount, "
				+ "sum(result.submittals) AS submittals, sum(result.noOfPos) AS job_pos, "
				+ "sum(result.startedCount)as startedCount, sum(result.confirmedCount)as confirmedCount, "
				+ " ((sum(result.noOfPos)) - (sum(result.startedCount))) as net_positions from "
				+ "(select lower(j.customer) AS customer, j.order_id as orderCount, j.num_pos as noOfPos, "
				+ " count(s.status) as submittals, "
				+ "count(case when s.status = 'STARTED' THEN 1 END)as startedCount,"
				+ " count(case when s.status = 'CONFIRMED' THEN 1 END)as confirmedCount from india_job_order j"
				+ "  left outer join india_submittal s on s.order_id = j.order_id and s.delete_flag = 0 "
				+ "where date(j.created_on) >= '" + fromDate + "' AND  date(j.created_on) <= '" + endDate + "' and j.delete_flg = 0 "
				+ "GROUP BY j.customer,j.order_id) AS result GROUP BY result.customer order By sum(result.startedCount) desc");
		}
		List<?> result = submittalDao.findBySQLQuery(sql.toString(), null);
		Integer noOfClients = 0,jobOrderCount = 0, submittalsCount = 0, jobPositions=0, noOfStarted=0, noOfConfirmed=0, netPositions=0;
		if(result != null){
			Map<String,SubmittalDto> submittalMap = getInActiveStartedOfClients(fromDate, endDate);
			Iterator<?> itr = result.iterator();
			while(itr.hasNext()){
				Object[] obj = (Object[]) itr.next();
				
				SubmittalDto dto = new SubmittalDto();
				dto.setJobClient(Utils.getStringValueOfObj(obj[0]));
				dto.setNoOfJobOrders(Utils.getIntegerValueOfBigDecimalObj(obj[1]).toString());
				dto.setSubmittedCount(Utils.getIntegerValueOfBigDecimalObj(obj[2]).toString());
				dto.setNoOfPositions(Utils.getIntegerValueOfBigDecimalObj(obj[3]).toString());
				dto.setStartedCount(Utils.getIntegerValueOfBigDecimalObj(obj[4]).toString());
				dto.setActiveStartedCount(Utils.getIntegerValueOfBigDecimalObj(obj[4]).toString());
				dto.setInActiveStartedCount("0");
				dto.setConfirmedCount(Utils.getIntegerValueOfBigDecimalObj(obj[5]).toString());
				if(submittalMap!=null && submittalMap.get(dto.getJobClient())!=null){
					dto.setInActiveStartedCount(submittalMap.get(dto.getJobClient()).getInActiveStartedCount());
					dto.setStartedCount(String.valueOf(Integer.parseInt(dto.getActiveStartedCount())+Integer.parseInt(dto.getInActiveStartedCount())));
				}
				dto.setNetPositions(Utils.getIntegerValueOfBigDecimalObj(obj[6]).toString());
				dtos.add(dto);
				
				noOfClients +=1;
				jobOrderCount += Utils.getIntegerValueOfBigDecimalObj(obj[1]);
				submittalsCount += Utils.getIntegerValueOfBigDecimalObj(obj[2]);
				jobPositions += Utils.getIntegerValueOfBigDecimalObj(obj[3]);
				noOfStarted += Utils.getIntegerValueOfBigDecimalObj(obj[4]);
				noOfConfirmed += Utils.getIntegerValueOfBigDecimalObj(obj[5]);
				netPositions += Utils.getIntegerValueOfBigDecimalObj(obj[6]);
			}
		}
		objMap.put(Constants.GRIDDATA, dtos);
		objMap.put(Constants.NO_OF_CLIENTS, noOfClients);
		objMap.put(Constants.NO_OF_JOB_ORDERS, jobOrderCount);
		objMap.put(Constants.NO_OF_SUBMITTALS, submittalsCount);
		objMap.put(Constants.NO_OF_POSITIONS, jobPositions);
		objMap.put(Constants.NO_OF_STARTS, noOfStarted);
		objMap.put(Constants.NO_OF_CONFIRMED, noOfConfirmed);
		objMap.put(Constants.NET_POSITIONS, netPositions);
		return objMap;
	}

	private Map<String, SubmittalDto> getInActiveStartedOfClients(Date from, Date to) {
		Map<String, SubmittalDto> submittalsMap = new HashMap<String, SubmittalDto>();

		StringBuffer sql = new StringBuffer("select result.customer AS client,"
				+ "       sum(result.startedCount) as startedCount"
				+ "      from (select lower(j.customer) AS customer, j.order_id as orderCount, j.num_pos"
				+ " as noOfPos,  count(history_result.started_count) as startedCount"
				+ "  from india_job_order j left outer join ("
				+ "   select s.submittal_id, count(h.submittal_id) AS started_count, s.created_by,"
				+ " s.status,s.order_id from india_submittal s, india_submittal_history h where s.submittal_id =  h.submittal_id "
				+ " AND DATE (COALESCE(h.updated_on, h.created_on)) >= '"+from+"'"
				+ "   AND DATE (COALESCE(h.updated_on, h.created_on)) <= '"+to+"'"
				+ " AND DATE (COALESCE(s.updated_on, s.created_on)) >= '"+from+"'"
				+ "   AND DATE (COALESCE(s.updated_on, s.created_on)) <= '"+to+"'"
						+ " AND h.status in ('STARTED') "
				+ " AND s.delete_flag = 0 AND s.status in ('OUTOFPROJ')  group by s.submittal_id ) AS history_result on "
				+ "    history_result.order_id = j.order_id  where date (j.created_on) >=      '"+from+"' AND"
				+ " date (j.created_on) <=  '"+to+"'"
				+ " and j.delete_flg = 0 GROUP BY j.customer, "
				+ "       j.order_id) AS result GROUP BY result.customer order By sum(result.startedCount) desc");


		List<?> resultList = submittalDao.findBySQLQuery(sql.toString(), null);
		if (resultList != null) {
			Iterator<?> itr = resultList.iterator();
			while (itr.hasNext()) {
				SubmittalDto dto = new SubmittalDto();
				Object[] obj = (Object[]) itr.next();
				dto.setStartedCount(obj[1] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[1]).toString() : "0");
				dto.setInActiveStartedCount(obj[1] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[1]).toString() : "0");
				dto.setActiveStartedCount("0");
				dto.setJobClient(Utils.getStringValueOfObj(obj[0]));
				submittalsMap.put(dto.getJobClient(), dto);
			}

		}
		return submittalsMap;
	}

	@Override
	public Map<String, Object> getIndiaClientDetialsReportData(Date fromDate, Date endDate, String clientName) {
		Map<String, Object> objMap = new HashMap<String, Object>();
		List<SubmittalDto> dtos = new ArrayList<SubmittalDto>();
		StringBuffer sql = null;
		 
		  sql = new StringBuffer("select j.order_id as orderid, j.num_pos as noOfPos, j.title, count(s.status) as submittals,"
		  		+ " count(case when s.status = 'STARTED' THEN 1 END)as startedCount, "
		  		+ "count(case when s.status = 'CONFIRMED' THEN 1 END)as confirmedCount,"
		  		+ "  (j.num_pos - (count(case when s.status = 'STARTED' THEN 1 END))) as netPos,"
		  		+ " (select u.first_name  ||' '|| u.last_name from user_acct u where u.user_id= COALESCE(j.dmname,j.created_by)) as dm, "
		  		+ " (select u.first_name  ||' '|| u.last_name from user_acct u where u.user_id= j.assigned_to) as rec ,j.status,"
		  		+ " ( CASE WHEN j.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN round((EXTRACT(EPOCH FROM date "
		  		+ "( CURRENT_DATE) - j.created_on) / 3600) / 24) ELSE (round((EXTRACT(EPOCH FROM date "
		  		+ "( j.updated_on) - j.created_on) / 3600) / 24) )  END)AS open_days "
		  		+ " from india_job_order j LEFT OUTER JOIN india_submittal s ON s.order_id = j.order_id AND s.delete_flag = 0  where ");
		  if(clientName != null){
			  sql.append("lower(j.customer) = '" + clientName.replaceAll("'", "''") + "' and ");
		  }
		  if(fromDate != null){
			  sql.append(" date(j.created_on) >= '" + fromDate + "' AND ");
		  }
		  if(endDate != null){
			  sql.append("date(j.created_on) <= '" + endDate + "' and ");
		  }
		  sql.append(" j.delete_flg = 0 group by j.order_id order by  startedCount desc");
		  
		  List<?> result = submittalDao.findBySQLQuery(sql.toString(), null);
			Integer noOfjobOrders = 0,  submittalsCount = 0, jobPositions=0, noOfStarted=0, noOfConfirmed=0, netPositions= 0 ;
			if(result != null){
				Map<String,SubmittalDto> submittalMap = getInActiveStartedOfClientsReportData(fromDate, endDate, clientName);
				Iterator<?> itr = result.iterator();
				while(itr.hasNext()){
					Object[] obj = (Object[]) itr.next();
					SubmittalDto dto = new SubmittalDto();
					dto.setJobOrderId(Utils.getStringValueOfObj(obj[0]));
					dto.setNoOfPositions(Utils.getIntegerValueOfBigDecimalObj(obj[1]).toString());
					dto.setJobTitle(Utils.getStringValueOfObj(obj[2]));
					dto.setSubmittedCount(Utils.getIntegerValueOfBigDecimalObj(obj[3]).toString());
					dto.setInActiveStartedCount("0");
					dto.setActiveStartedCount(Utils.getIntegerValueOfBigDecimalObj(obj[4]).toString());
					dto.setStartedCount(Utils.getIntegerValueOfBigDecimalObj(obj[4]).toString());
					dto.setConfirmedCount(Utils.getIntegerValueOfBigDecimalObj(obj[5]).toString());
					dto.setNetPositions(Utils.getIntegerValueOfBigDecimalObj(obj[6]).toString());
					dto.setDmName(Utils.getStringValueOfObj(obj[7]));
					dto.setRecName(Utils.getStringValueOfObj(obj[8]));
					dto.setStatus(Utils.getStringValueOfObj(obj[9]));
					dto.setActiveCount(Utils.getIntegerValueOfDoubleObj(obj[10]));
					if(submittalMap!=null && submittalMap.get(dto.getJobOrderId())!=null){
						dto.setInActiveStartedCount(submittalMap.get(dto.getJobOrderId()).getInActiveStartedCount());
						dto.setStartedCount(String.valueOf(Integer.parseInt(dto.getActiveStartedCount())+Integer.parseInt(dto.getInActiveStartedCount())));
					}
					dtos.add(dto);
					
					noOfjobOrders +=1;
					jobPositions += Utils.getIntegerValueOfBigDecimalObj(obj[1]);
					submittalsCount += Utils.getIntegerValueOfBigDecimalObj(obj[3]);
					noOfStarted += Utils.getIntegerValueOfBigDecimalObj(obj[4]);
					noOfConfirmed += Utils.getIntegerValueOfBigDecimalObj(obj[5]);
					netPositions += Utils.getIntegerValueOfBigDecimalObj(obj[6]);
				}
			}
			objMap.put("gridData", dtos);
			objMap.put(Constants.NO_OF_JOB_ORDERS, noOfjobOrders);
			objMap.put(Constants.NET_POSITIONS, netPositions);
			objMap.put(Constants.NO_OF_SUBMITTALS, submittalsCount);
			objMap.put(Constants.NO_OF_POSITIONS, jobPositions);
			objMap.put(Constants.NO_OF_STARTS, noOfStarted);
			objMap.put(Constants.NO_OF_CONFIRMED, noOfConfirmed);
		return objMap;
	}

	private Map<String, SubmittalDto> getInActiveStartedOfClientsReportData(Date from, Date to,
			String client) {
		Map<String, SubmittalDto> submittalsMap = new HashMap<String, SubmittalDto>();

		StringBuffer sql = new StringBuffer("select j.order_id as orderid, "
				+ " count(history_result.started_count) as startedCount "
				+ " from india_job_order j"
				+ " LEFT OUTER JOIN"
				+ " ("
				+ " select s.submittal_id, count(h.submittal_id) AS started_count, s.created_by,"
				+ "s.status,s.order_id from india_submittal s, india_submittal_history h where s.submittal_id = h.submittal_id "
				+ " AND DATE (COALESCE(h.updated_on, h.created_on)) >= '"+from+"'"
				+ " AND DATE (COALESCE(h.updated_on, h.created_on)) <= '"+to+"'"
				+ " AND DATE (COALESCE(s.updated_on, s.created_on)) >= '"+from+"'"
				+ " AND DATE (COALESCE(s.updated_on, s.created_on)) <= '"+to+"'"
						+ " AND h.status "
				+ " in ('STARTED') AND s.delete_flag = 0 AND s.status in ('OUTOFPROJ')  group by s.submittal_id ) AS history_result"
				+ " ON history_result.order_id = j.order_id  "
				+ " where lower(j.customer) = '"+client+"' and"
				+ " date (j.created_on) >= '"+from+"' AND"
				+ " date (j.created_on) <= '"+to+"' and "
				+ " j.delete_flg = 0 "
				+ " group by j.order_id "
				+ " order by startedCount desc");


		List<?> resultList = submittalDao.findBySQLQuery(sql.toString(), null);
		if (resultList != null) {
			Iterator<?> itr = resultList.iterator();
			while (itr.hasNext()) {
				SubmittalDto dto = new SubmittalDto();
				Object[] obj = (Object[]) itr.next();
				dto.setStartedCount(obj[1] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[1]).toString() : "0");
				dto.setJobOrderId(obj[0] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[0]).toString() : "0");
				dto.setInActiveStartedCount(obj[1] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[1]).toString() : "0");
				dto.setActiveStartedCount("0");
				dto.setJobClient(Utils.getStringValueOfObj(obj[0]));
				submittalsMap.put(dto.getJobOrderId(), dto);
			}

		}
		return submittalsMap;
	}
	@Override
	public Boolean checkStartedStatusBy_CandidateId_SubmittalId(Integer candidateId, Integer submittalId) {
		Boolean candidateStarted = false;
		try {
			StringBuilder hql = new StringBuilder(
					"select s.id from IndiaSubmittal s where s.candidate.id = ?1 AND s.deleteFlag = 0 AND s.id != ?2 AND s.status = 'STARTED'");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("1", candidateId);
			params.put("2", submittalId);
			List<?> list = submittalDao.findByQuery(hql.toString(), params);
			if (list != null && list.size() > 0) {
				candidateStarted = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return candidateStarted;
	}

	@Override
	public Map<SubmittalStatus, Integer> getAllIndiaSubmittalStatusCounts(Date dateStart, Date dateEnd) {
		try {
			Map<SubmittalStatus, Integer> map = submittalDao.getAllSubmittalStatusCounts(dateStart, dateEnd);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Integer> getIndiaInActiveStartedCounts(Date dateStart, Date dateEnd) {
		Map<String, Integer> inActiveStartedMap = new HashMap<String, Integer>();

		StringBuffer sql = new StringBuffer("select sum(result.started_count) from "
				+ " (select s.submittal_id, count(h.submittal_id) AS started_count, s.created_by, "
				+ " s.status,s.order_id from india_submittal s, india_submittal_history h where s.submittal_id = "
				+ "  h.submittal_id AND DATE (COALESCE(h.updated_on, h.created_on)) >= '"+dateStart+"' "
				+ "   AND DATE (COALESCE(h.updated_on, h.created_on)) <= '"+dateEnd+"'"
						+ " AND DATE (COALESCE(s.updated_on, s.created_on)) >= '"+dateStart+"' "
				+ "   AND DATE (COALESCE(s.updated_on, s.created_on)) <= '"+dateEnd+"'"
						+ " AND h.status "
				+ "    in ('STARTED') AND s.delete_flag = 0 AND s.status in ('OUTOFPROJ')  group by s.submittal_id)     AS result");


		List<?> resultList = submittalDao.findBySQLQuery(sql.toString(), null);
		if (resultList != null) {
			Iterator<?> itr = resultList.iterator();
			while (itr.hasNext()) {
 				BigDecimal obj = (BigDecimal) itr.next();
				if(obj!=null){
				inActiveStartedMap.put(Constants.INACTIVE_STARTS, Integer.parseInt(String.valueOf(obj)));
				}
			}

		}
		return inActiveStartedMap;
	}

}

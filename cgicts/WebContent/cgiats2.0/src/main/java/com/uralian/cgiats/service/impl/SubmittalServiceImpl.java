/**
 * 
 */
package com.uralian.cgiats.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.SubmittalDao;
import com.uralian.cgiats.dto.CandidateDto;
import com.uralian.cgiats.dto.ReportwiseDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.dto.SubmittalStatsDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.JobOrderSearchDto;
import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.SubmitalStats;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalEvent;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.rest.UserRoleVo;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.SubmittalService;
import com.uralian.cgiats.service.UserService;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.MonthEnum;
import com.uralian.cgiats.util.RecRnRReportDto;
import com.uralian.cgiats.util.Utils;

/**
 * @author Parameshwar
 *
 */
/**
 * @author Chaitanya
 * 
 */
@Service
@Transactional(rollbackFor = ServiceException.class)
public class SubmittalServiceImpl implements SubmittalService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private SubmittalDao submittalDao;
	@Autowired
	private UserService userService;

	public List<SubmittalDto> findSubmittals(JobOrderSearchDto criteria) {
		List<SubmittalDto> submittalDtoList = null;
		try {
			// null criteria not allowed
			if (criteria == null)
				throw new IllegalArgumentException("Search criteria not set");

			if (log.isDebugEnabled())
				log.debug("Using following criteria " + criteria);

			StringBuffer hqlSelect = new StringBuffer(
					"select s.id,s.createdOn,s.createdDate,s.createdBy,s.status,(s.candidate.firstName||' '||s.candidate.lastName),s.jobOrder.status from Submittal s ");
			Map<String, Object> params = new HashMap<String, Object>();

			StringBuffer hqlWhere = new StringBuffer(" where s.deleteFlag = 0 ");
			buildWhereClause(criteria, hqlWhere, params);

			hqlSelect.append(hqlWhere);
			if (log.isDebugEnabled())
				log.debug("HQL Query " + hqlSelect.toString());
			List<?> list = submittalDao.findByQuery(hqlSelect.toString(), 0, 0, params);
			if (list != null && list.size() > 0) {
				submittalDtoList = new ArrayList<SubmittalDto>();
				Iterator<?> iterator = list.iterator();
				while (iterator.hasNext()) {
					Object[] object = (Object[]) iterator.next();
					SubmittalDto dto = new SubmittalDto();
					dto.setSubmittalId(Utils.getStringValueOfObj(object[0]));
					dto.setCreatedOn(Utils.convertDateToString_HH_MM_A((Date) object[1]));
					dto.setUpdatedOn(Utils.convertDateToString_HH_MM_A((Date) object[2]));
					dto.setCreatedBy(Utils.getStringValueOfObj(object[3]));
					dto.setStatus(((SubmittalStatus) object[4]).name());
					dto.setCandidateName(Utils.getStringValueOfObj(object[5]));
					dto.setJobOrderStatus(((JobOrderStatus)object[6]).name());
					submittalDtoList.add(dto);
				}
			}
			// return submittalDao.findByQuery(hqlSelect.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return submittalDtoList;
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
			if (criteria.getStartEntryDate() != null && criteria.getEndEntryDate() != null) {
				hql.append(" and COALESCE(cast(createdDate as date),cast(createdOn as date)) >=  cast(:startDate as date)");
				params.put("startDate", criteria.getStartEntryDate());

				hql.append(" and COALESCE(cast(createdDate as date),cast(createdOn as date)) <= cast(:endDate as date)");
				params.put("endDate", criteria.getEndEntryDate());

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<?> getListStartedCandidates() {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select distinct s.candidate.id from Submittal s, Candidate c where s.status =:status and s.deleteFlag = 0");
			hql.append(
					" and c.deleteFlag = 0 and s.candidate.id = c.id and s.candidate.id not in (select ci.candidate.id from CandidateInfo ci) ORDER By s.candidate.id");
			List<?> submittal = submittalDao.findByQuery(hql.toString(), "status", SubmittalStatus.STARTED);

			return submittal;
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
	public List<Submittal> getAllStarterdCandidates(Date currentDate) {
		try {
			List<Submittal> subDetails = submittalDao.getAllStarterdCandidates(currentDate);
			return subDetails;
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
	 * com.uralian.cgiats.service.SubmittalService#getSubmittalStatusByBdm(java
	 * .lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByBdm(String location, User bdmName, Date dateStart, Date dateEnd) {
		try {
			Map<String, Map<SubmittalStatus, Integer>> map = submittalDao.getSubmittalStatusByBdm(location, bdmName, dateStart, dateEnd);
			// replace nulls with 0s
			for (Map<SubmittalStatus, Integer> stats : map.values()) {
				for (SubmittalStatus status : SubmittalStatus.values()) {
					if (!stats.containsKey(status))
						stats.put(status, 0);
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @param bdmName
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByDm(User bdmName, Date dateStart, Date dateEnd) {
		try {
			Map<String, Map<SubmittalStatus, Integer>> map = submittalDao.getSubmittalStatusByDm(bdmName, dateStart, dateEnd);
			// replace nulls with 0s
			for (Map<SubmittalStatus, Integer> stats : map.values()) {
				for (SubmittalStatus status : SubmittalStatus.values()) {
					if (!stats.containsKey(status))
						stats.put(status, 0);
				}
			}
			return map;
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
	 * com.uralian.cgiats.service.SubmittalService#getSubmittalStatusByBdm(java
	 * .lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByRecruiter(User user, Date dateStart, Date dateEnd) {
		try {

			Map<String, Map<SubmittalStatus, Integer>> map = submittalDao.getSubmittalStatusByRecruiter(user, dateStart, dateEnd);
			// replace nulls with 0s
			for (Map<SubmittalStatus, Integer> stats : map.values()) {
				for (SubmittalStatus status : SubmittalStatus.values()) {
					if (!stats.containsKey(status))
						stats.put(status, 0);
				}
			}
			return map;

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
	 * com.uralian.cgiats.service.SubmittalService#getSubmittalStatusByBdm(java
	 * .lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByRecruiterDM(String selectedLocation, User user, Date dateStart, Date dateEnd) {
		try {
			Map<String, Map<SubmittalStatus, Integer>> map = submittalDao.getSubmittalStatusByRecruiterDM(selectedLocation, user, dateStart, dateEnd);
			// replace nulls with 0s
			for (Map<SubmittalStatus, Integer> stats : map.values()) {
				for (SubmittalStatus status : SubmittalStatus.values()) {
					if (!stats.containsKey(status))
						stats.put(status, 0);
				}
			}
			return map;
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
	 * com.uralian.cgiats.service.SubmittalService#getSubmittalStatusByBdm(java
	 * .lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<Submittal> getSubmittalDetailsbyStatus(SubmittalStatus status, Date dateStart, Date dateEnd, String createdUser, String loginUser) {
		try {
			List<Submittal> map = submittalDao.getSubmittalDetailsByStatus(status, dateStart, dateEnd, createdUser, loginUser);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<Submittal> getAllOutofProjCandidates(Date fromDate, Date toDate) {
		try {
			List<Submittal> map = submittalDao.getAllOutofProjCandidates(fromDate, toDate);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Integer editSubmittalEvent(SubmittalEvent sEvent) {
		try {
			return submittalDao.editSubmittalEvent(sEvent);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	// Code added Raghavendra 19/05/14.
	@SuppressWarnings("unchecked")
	@Override
	public List<Submittal> getDeletedSubmittals() {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from Submittal");
			hql.append(" where deleteFlag=1");
			return (List<Submittal>) submittalDao.runQuery(hql.toString());
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
	 * com.uralian.cgiats.service.SubmittalService#getAllSubmittalStatusCounts
	 * (java.util.Date, java.util.Date)
	 */
	public Map<SubmittalStatus, Integer> getAllSubmittalStatusCounts(Date dateStart, Date dateEnd) {
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
	public List<Submittal> getCandidateJobrderStatus(Integer candidateId) {
		try {
			List<Submittal> submList = submittalDao.getCandidateJobrderStatus(candidateId);

			return submList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	// Mobile Application

	public SubmitalStats executiveTodaySubmittalStatus(UserRoleVo userRoleVo) {
		try {
			SubmitalStats exeListStats = submittalDao.executiveTodaySubmittalStatus(userRoleVo);
			return exeListStats;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<SubmitalStats> getAllSubmittalsStatsForExecutives(UserRoleVo userRoleVo) {
		try {
			List<SubmitalStats> allStatList = submittalDao.getAllSubmittalsStatsForExecutives(userRoleVo);
			return allStatList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<SubmitalStats> getYearWiseSubmittalsForExecutives(String year, UserRoleVo userRoleVo) {
		try {
			List<SubmitalStats> yearStatList = submittalDao.getYearWiseSubmittalsForExecutives(year, userRoleVo);
			return yearStatList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<SubmitalStats> getWeekelyStats(Date fromDate, Date toDate, UserRoleVo userRoleVo) {
		try {
			List<SubmitalStats> weekelyStatList = submittalDao.getWeekelyStats(fromDate, toDate, userRoleVo);
			return weekelyStatList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<SubmitalStats> getRecruiterWeekelyStats(Date fromDate, Date toDate, String dmName, UserRoleVo userRoleVo) {
		try {
			List<SubmitalStats> recruiterWeekelyStatList = submittalDao.getRecruiterWeekelyStats(fromDate, toDate, dmName, userRoleVo);
			return recruiterWeekelyStatList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<SubmitalStats> getRecruiterLocationStats(Date fromDate, Date toDate, String location, UserRoleVo userRolevo) {
		try {
			List<SubmitalStats> recruiterLocationStatList = submittalDao.getRecruiterLocationStats(fromDate, toDate, location, userRolevo);
			return recruiterLocationStatList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByDm(Date fromDate, Date toDate) {
		try {
			Map<String, Map<SubmittalStatus, Integer>> map = submittalDao.getSubmittalStatusByDm(fromDate, toDate);

			// replace nulls with 0s
			for (Map<SubmittalStatus, Integer> stats : map.values()) {
				for (SubmittalStatus status : SubmittalStatus.values()) {
					if (!stats.containsKey(status))
						stats.put(status, 0);
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	public List<RecRnRReportDto> getRecRnrReportDetails(Date fromDate, Date maxToDate, String recName) {
		List<RecRnRReportDto> list = new ArrayList<RecRnRReportDto>();
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select s.createdBy,s.status, count(*),s.jobOrder.createdBy  from Submittal s");
			hql.append(" where 1=1 and s.deleteFlag=0");
			if (fromDate != null) {
				hql.append(" and COALESCE(cast(s.createdOn as date)) >=  cast(:startDate as date)");
				paramNames.add("startDate");
				paramValues.add(fromDate);
			}
			if (maxToDate != null) {
				hql.append(" and COALESCE(cast(s.createdOn as date)) <= cast(:endDate as date)");
				paramNames.add("endDate");
				paramValues.add(maxToDate);
			}
			if (recName != null) {
				hql.append(" and s.createdBy='" + recName + "'");
			}
			hql.append(" and s.status IN('SUBMITTED','DMREJ','ACCEPTED','INTERVIEWING','REJECTED','STARTED')");
			String portalName = "Sapeare";
			hql.append(" and  s.jobOrder.companyFlag='" + portalName + "'");

			hql.append(" group by s.createdBy,s.status,s.jobOrder.createdBy order by s.createdBy Desc");
			List<?> result = submittalDao.runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			RecRnRReportDto recRnrDto = null;
			Iterator<?> it = result.iterator();
			while (it.hasNext()) {

				recRnrDto = new RecRnRReportDto();

				Object[] tuple = (Object[]) it.next();

				String reName = (String) tuple[0];

				recRnrDto.setRecName(reName);

				String status = tuple[1].toString();

				if (status.equals(SubmittalStatus.SUBMITTED.toString())) {
					recRnrDto.setStatus(SubmittalStatus.SUBMITTED);
				}
				if (status.equals(SubmittalStatus.ACCEPTED.toString())) {
					recRnrDto.setStatus(SubmittalStatus.ACCEPTED);
				}
				if (status.equals(SubmittalStatus.INTERVIEWING.toString())) {
					recRnrDto.setStatus(SubmittalStatus.INTERVIEWING);
				}
				if (status.equals(SubmittalStatus.DMREJ.toString())) {
					recRnrDto.setStatus(SubmittalStatus.DMREJ);
				}
				if (status.equals(SubmittalStatus.CONFIRMED.toString())) {
					recRnrDto.setStatus(SubmittalStatus.CONFIRMED);
				}
				if (status.equals(SubmittalStatus.BACKOUT.toString())) {
					recRnrDto.setStatus(SubmittalStatus.BACKOUT);
				}
				if (status.equals(SubmittalStatus.OUTOFPROJ.toString())) {
					recRnrDto.setStatus(SubmittalStatus.OUTOFPROJ);
				}
				if (status.equals(SubmittalStatus.STARTED.toString())) {
					recRnrDto.setStatus(SubmittalStatus.STARTED);
				}
				if (status.equals(SubmittalStatus.REJECTED.toString())) {
					recRnrDto.setStatus(SubmittalStatus.REJECTED);
				}
				Number count = (Number) tuple[2];
				System.out.println("Number" + count.intValue());
				recRnrDto.setCount(count.intValue());

				String dmName = (String) tuple[3];
				System.out.println("DmName" + dmName);
				recRnrDto.setDmName(dmName);
				list.add(recRnrDto);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		return list;
	}

	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByDmJobOrder(User user, Date fromDate, Date toDate) {
		Map<String, Map<SubmittalStatus, Integer>> map = submittalDao.getSubmittalStatusByDmJobOrder(user, fromDate, toDate);
		try {
			// replace nulls with 0s
			for (Map<SubmittalStatus, Integer> stats : map.values()) {
				for (SubmittalStatus status : SubmittalStatus.values()) {
					if (!stats.containsKey(status))
						stats.put(status, 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return map;
	}

	@Override
	public List<Submittal> getSubmittalDetailsbyStatusSapeareReport(SubmittalStatus subStatus, Date startDate, Date endDate, String submittalDms,
			String loginUserName, String portalName) {
		try {
			return submittalDao.getSubmittalDetailsbyStatusSapeareReport(subStatus, startDate, endDate, submittalDms, loginUserName, portalName);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<CandidateDto> getOutofProjCandidates(CandidateSearchDto statusDto) {
		try {
			return submittalDao.getOutofProjs(statusDto);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<?> getAllSubmittalYears() {
		List<Integer> list = null;
		try {
			StringBuffer sqlSelect = new StringBuffer(
					"select distinct year(s.createdOn) from Submittal s where year(s.createdOn) is not null order by year(s.createdOn) DESC ");

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

	@SuppressWarnings("unchecked")
	@Override
	public List<?> getAllAssignedBDMs() {
		List<UserDto> assignedBdms = null;
		try {
			StringBuffer sqlSelect = new StringBuffer(" select DISTINCT(s.assigned_dm) from  weekwise_recuriter_report s");

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			List<String> userIds = (List<String>) submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (userIds != null && userIds.size() > 0) {
				assignedBdms = userService.getUsersInfoByIds(userIds);
				Collections.sort(assignedBdms, new Comparator<UserDto>() {

					@Override
					public int compare(UserDto o1, UserDto o2) {
						return o1.getFullName().compareTo(o2.getFullName());
					}
				});
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return assignedBdms;
	}

	@Override
	public List<?> getWeekWiseRecruiterReport(ReportwiseDto reportwiseDto) {
		List<SubmittalStatsDto> list = null;
		try {
			StringBuffer sqlSelect = new StringBuffer("select recut_name,assigned_dm,rank() OVER ( "
					+ "ORDER BY sum(s.started_count) DESC, sum(s.inteviewing_count) DESC,  " + "   sum(s.submitted_count) DESC) AS rank,"
					+ " sum(submitted_count) as \"SUBMITTED\",  sum(started_count) as \"STARTED\",  sum(dmrej_count) as \"DMREJ\","
					+ "  sum(accepted_count) as \"ACCEPTED\",  sum(inteviewing_count) as \"INTERVIEWING\", sum(confirmed_count) as \"CONFIRMED\","
					+ " sum(rejected_count) as \"REJECTED\", sum(backout_count) as \"BACKOUT\", sum(outofprj_count) as \"OUTOFPRJ\","
					+ " sum(nu_count) as \"NU\",full_name from weekwise_recuriter_report s where ");
			if (reportwiseDto.getYear() != null) {
				sqlSelect.append("  s.submital_year in (" + reportwiseDto.getYear() + ")");
			}
			if (reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() > 0) {
				sqlSelect.append(" and s.submital_month in (" + Utils.getListWithSingleQuote(reportwiseDto.getMonths()) + ")");
			}
			if (reportwiseDto.getDmNames() != null && reportwiseDto.getDmNames().size() > 0) {
				sqlSelect.append(" and (s.assigned_dm in (" + Utils.getListWithSingleQuote(reportwiseDto.getDmNames()) + ") or s.recut_name in ("
						+ Utils.getListWithSingleQuote(reportwiseDto.getDmNames()) + "))");
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() == 1
					&& reportwiseDto.getMonths().get(0).equalsIgnoreCase(MonthEnum.JAN.getMonth())) {
				sqlSelect.append(" and s.jan_week_of_month in (" + reportwiseDto.getWeek() + ")");
			} else if (reportwiseDto.getWeek() != null) {
				sqlSelect.append(" and s.week_of_month in (" + reportwiseDto.getWeek() + ")");
			}
			if (reportwiseDto.getStatuses() != null && reportwiseDto.getStatuses().size() > 0) {
				sqlSelect.append(" and s.status in (" + Utils.getListWithSingleQuote(reportwiseDto.getStatuses()) + ")");
			}
			sqlSelect.append(" group by assigned_dm,recut_name,full_name;");

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				log.info("Size of the Records ::: " + resultList.size());
				list = new ArrayList<SubmittalStatsDto>();
				getSubmittalData(resultList, list);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public Object getDMsPerformanceTotalReport(ReportwiseDto reportwiseDto) {
		List<SubmittalStatsDto> list = null;
		Map<String, Integer> userWithSubmittalCountMap = null;
		try {
			StringBuffer sqlSelect = new StringBuffer("select count(s),s.status,u.user_id,u.assigned_bdm from submittal s,"
					+ "job_order j,user_acct u where s.delete_flag=0 and s.order_id=j.order_id and s.created_by=u.user_id "
					+ "and j.delete_flg=0 and u.status in ('ACTIVE','INACTIVE')  and u.user_role in ('DM','ADM','Recruiter')");
			if (reportwiseDto.getYear() != null) {
				sqlSelect.append("  and EXTRACT(YEAR from COALESCE(s.updated_on,s.created_on)) in (" + reportwiseDto.getYear() + ")");
			}
			if (reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() > 0) {
				sqlSelect
						.append(" and to_char(COALESCE(s.updated_on,s.created_on),'Mon') in (" + Utils.getListWithSingleQuote(reportwiseDto.getMonths()) + ")");
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

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				log.info("Size of the Records ::: " + resultList.size());
				userWithSubmittalCountMap = new HashMap<String, Integer>();
				for (Object object : resultList) {
					Object[] obj = (Object[]) object;
					String userId = obj[3] != null ? Utils.getStringValueOfObj(obj[3]) : Utils.getStringValueOfObj(obj[2]);
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
	public Object getDMWiseRecPerformanceTotalReport(ReportwiseDto reportwiseDto) {
		Map<String, Integer> userWithSubmittalCountMap = new HashMap<String, Integer>();
		try {
			StringBuffer sqlSelect = new StringBuffer("select count(s),s.status,u.user_id,u.assigned_bdm from submittal s,"
					+ "job_order j,user_acct u where s.delete_flag=0 and s.order_id=j.order_id and s.created_by=u.user_id "
					+ "  and u.status in ('ACTIVE','INACTIVE')  and u.user_role in ('DM','ADM','Recruiter')");
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

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
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
			/*Map<String, SubmittalDto>  submittalMap=getInActiveStartedOfRecruiter(null, null, reportwiseDto.getDmName(), null, reportwiseDto,null);
			if (submittalMap != null) {
				for (String key : submittalMap.keySet()) {
					if (userWithSubmittalCountMap.get(key) == null) {
						userWithSubmittalCountMap.put(key, Integer.parseInt(submittalMap.get(key).getInActiveStartedCount()));
					} else {
						userWithSubmittalCountMap.put(key, (userWithSubmittalCountMap.get(key) + Integer.parseInt(submittalMap.get(key).getInActiveStartedCount())));
					}
				}
			}*/
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return userWithSubmittalCountMap;
	}

	@Override
	public Object getStartsByDateRange(ReportwiseDto reportwiseDto) {
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		try {
			StringBuffer sqlSelect = new StringBuffer("select " + " ((CASE WHEN u.first_name ISNULL THEN '' ELSE u.first_name END)||' '|| "
					+ " (CASE WHEN u.last_name ISNULL THEN '' ELSE u.last_name END)) as full_name, " + " u.user_id, count(s) " + "  from user_acct u "
					+ "  Left outer join submittal s on (s.created_by = u.user_id and s.delete_flag = 0 " + "    ");
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
			sqlSelect.append(" and s.status='STARTED') where u.user_role = 'Recruiter' group by u.user_id order by full_name;");

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				log.info("Size of the Records ::: " + resultList.size());
				for (Object object : resultList) {
					Object[] obj = (Object[]) object;
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("full_name", Utils.getStringValueOfObj(obj[0]));
					map.put("user_id", Utils.getStringValueOfObj(obj[1]));
					map.put("no_of_starts", Utils.getIntegerValueOfBigDecimalObj(obj[2]));
					mapList.add(map);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return mapList;
	}

	@Override
	public Object getDMWiseRecOverallPerformanceReport(ReportwiseDto reportwiseDto) {
		List<SubmittalStatsDto> list = null;
		// Map<String, Integer> userWithSubmittalCountMap = null;
		try {
			StringBuffer sqlSelect = new StringBuffer("SELECT Result.userId, Result.full_name, Result.submitted_count, Result.accepted_count,"
					+ "Result.dmrej_count,Result.inteviewing_count,Result.confirmed_count,Result.rejected_count,Result.started_count,Result.backout_count,"
					+ "Result.outofproj_count,Result.nu_count,rank() OVER (ORDER BY RESULT.started_count DESC, RESULT.inteviewing_count DESC,"
					+ "    RESULT.submitted_count DESC) AS rank from " + "(select count(s) AS submitted_count, count(CASE WHEN s.status='ACCEPTED' THEN 1 END)"
					+ " AS accepted_count,count(CASE WHEN s.status='DMREJ' THEN 1 END) AS dmrej_count,"
					+ "count(CASE WHEN s.status='INTERVIEWING' THEN 1 END) AS inteviewing_count,count(CASE WHEN s.status='CONFIRMED' THEN 1 END) AS confirmed_count,"
					+ "count(CASE WHEN s.status='REJECTED' THEN 1 END) AS rejected_count,count(CASE WHEN s.status='STARTED' THEN 1 END) AS started_count,"
					+ "count(CASE WHEN s.status='BACKOUT' THEN 1 END) AS backout_count,"
					+ "count(CASE WHEN s.status='OUTOFPROJ' THEN 1 END) AS outofproj_count," + "count(CASE WHEN s.status='SUBMITTED' THEN 1 END) AS nu_count,"
					+ "u.user_id as userId,u.assigned_bdm, " + "((CASE WHEN u.first_name ISNULL THEN '' ELSE u.first_name END) ||' '|| "
					+ " (CASE WHEN u.last_name ISNULL THEN '' ELSE u.last_name END)) AS full_name " + " from submittal s,job_order j,user_acct u "
					+ "where s.delete_flag=0 and s.order_id=j.order_id " + "and s.created_by=u.user_id " + "and u.status in ('ACTIVE','INACTIVE') ");

			// + "and s.created_by in ('kdm','krec','kadm') "

			// + "and EXTRACT(YEAR from COALESCE(s.updated_on,s.created_on)) in
			// (2016) "

			// + "and to_char(COALESCE(s.updated_on,s.created_on),'Mon') in
			// ('Jan','Feb','May','Sep') "
			// + "and (extract(week from COALESCE(s.updated_on,s.created_on)) -
			// "
			// + " extract(week from date_trunc('month',
			// COALESCE(s.updated_on,s.created_on))) + 1) "
			// + " in (1,2,3,4,5,6) group by u.user_id)AS RESULT");
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
			// if (reportwiseDto.getStatuses() != null &&
			// reportwiseDto.getStatuses().size() > 0) {
			// sqlSelect.append(" and s.status in (" +
			// Utils.getListWithSingleQuote(reportwiseDto.getStatuses()) + ")");
			// }
			sqlSelect.append(" group by u.user_id) AS RESULT;");

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);

			/*
			 * Result.userId, Result.full_name, Result.submitted_count,
			 * Result.accepted_count," +
			 * "Result.dmrej_count,Result.inteviewing_count,Result.confirmed_count,Result.rejected_count,Result.started_count,Result.backout_count,"
			 * +
			 * "Result.outofproj_count,Result.nu_count,rank() OVER (ORDER BY RESULT.started_count DESC, RESULT.inteviewing_count DESC,"
			 * + " RESULT.submitted_count DESC) AS rank
			 */

			if (resultList != null && resultList.size() > 0) {
				log.info("Size of the Records ::: " + resultList.size());
				list = new ArrayList<SubmittalStatsDto>();
				for (Object object : resultList) {
					Object[] obj = (Object[]) object;
					SubmittalStatsDto dto = new SubmittalStatsDto();
					dto.setUserId(Utils.getStringValueOfObj(obj[0]));
					dto.setName(Utils.getStringValueOfObj(obj[1]));
					dto.setSUBMITTED(Utils.getIntegerValueOfBigDecimalObj(obj[2]));
					dto.setACCEPTED(Utils.getIntegerValueOfBigDecimalObj(obj[3]));
					dto.setDMREJ(Utils.getIntegerValueOfBigDecimalObj(obj[4]));
					dto.setINTERVIEWING(Utils.getIntegerValueOfBigDecimalObj(obj[5]));
					dto.setCONFIRMED(Utils.getIntegerValueOfBigDecimalObj(obj[6]));
					dto.setREJECTED(Utils.getIntegerValueOfBigDecimalObj(obj[7]));
					dto.setSTARTED(Utils.getIntegerValueOfBigDecimalObj(obj[8]));
					dto.setBACKOUT(Utils.getIntegerValueOfBigDecimalObj(obj[9]));
					dto.setOUTOFPROJ(Utils.getIntegerValueOfBigDecimalObj(obj[10]));
					dto.setNotUpdated(Utils.getIntegerValueOfBigDecimalObj(obj[11]));
					dto.setRank(Utils.getIntegerValueOfBigDecimalObj(obj[12]));
					list.add(dto);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public Object getDMWiseJobOrderServiceReport(ReportwiseDto reportwiseDto) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> serviceWiseMapList = null;
		try {
			StringBuffer sqlSelect = new StringBuffer("select count(CASE WHEN result.hours <=24 THEN 1 END) AS hours_24,"
					+ " count(CASE WHEN (result.hours >24 AND result.hours <=48) THEN 1 END) AS hours_48,"
					+ " count(CASE WHEN (result.hours >48) THEN 1 END) AS hours_48_More,result.assignedbdm " + " from (");
			if (reportwiseDto.getStatuses() != null && reportwiseDto.getStatuses().size() == 0
					&& !reportwiseDto.getStatuses().get(0).equalsIgnoreCase(SubmittalStatus.SUBMITTED.name())) {
				sqlSelect.append("select (EXTRACT(EPOCH FROM COALESCE(s.updated_on,s.created_on)-j.created_on)/3600) AS hours, ");
			} else {
				sqlSelect.append("select (EXTRACT(EPOCH FROM s.created_on-j.created_on)/3600) AS hours, ");
			}
			sqlSelect.append("s.created_on,j.created_on,s.created_by AS createdBy,u.user_id "
					+ ",(CASE WHEN u.user_role = 'DM' THEN u.user_id ELSE u.assigned_bdm END) AS assignedbdm " + " from submittal s,job_order j,user_acct u "
					+ "where  s.delete_flag=0 and s.created_by=u.user_id " + "and j.order_id=s.order_id and j.delete_flg=0 "
					+ "and u.user_role in ('DM','ADM','Recruiter') " + "and u.status in ('ACTIVE','INACTIVE') ");
			// + "and EXTRACT(YEAR from COALESCE(s.updated_on,s.created_on)) in
			// (2016) "
			// + " and to_char(COALESCE(s.updated_on,s.created_on),'Mon') in
			// ('Jan','Feb','May','Sep') "
			// + "and (extract(week from COALESCE(s.updated_on,s.created_on)) -
			// "
			// + " extract(week from date_trunc('month',
			// COALESCE(s.updated_on,s.created_on))) + 1) "
			// + " in (1,2,3,4,5,6) "
			// + ") AS result group by result.assignedbdm");
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
			sqlSelect.append(" )  AS result  group by result.assignedbdm");

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				log.info("Size of the Records ::: " + resultList.size());
				serviceWiseMapList = new ArrayList<Map<String, Object>>();
				List<Object> userIds = new ArrayList<Object>();
				userIds.add(reportwiseDto.getDmName());
				getHorizontalBarChartInputData(serviceWiseMapList, resultList);
				/*
				 * List<String> names = Arrays.asList("Served in 24 Hours",
				 * "Served in 48 Hours", "Served after 48 Hours"); List<Object>
				 * hours24 = new ArrayList<Object>(); List<Object> hours48 = new
				 * ArrayList<Object>(); List<Object> hoursMoreThan48 = new
				 * ArrayList<Object>();
				 * 
				 * 
				 * for (Object object : resultList) { Object[] obj = (Object[])
				 * object; hours24.add(obj[0]); hours48.add(obj[1]);
				 * hoursMoreThan48.add(obj[2]); } for (int i = 0; i <
				 * names.size(); i++) { Map<String, Object> map = new
				 * LinkedHashMap<String, Object>(); map.put("name",
				 * names.get(i)); List<Map<String, Object>> hoursMapList = new
				 * ArrayList<Map<String, Object>>(); if (i == 0) {
				 * map.put(Constants.COLOR, Constants.GREEN); int hours_24=0;
				 * for (Object hour24 : hours24) {
				 * hours_24+=Utils.getIntegerValueOfBigDecimalObj(hour24); }
				 * Map<String, Object> hoursMap = new HashMap<String, Object>();
				 * hoursMap.put(Constants.COLOR, Constants.GREEN);
				 * hoursMap.put(Constants.Y, hours_24);
				 * hoursMapList.add(hoursMap); map.put("data", hoursMapList); }
				 * else if (i == 1) { map.put(Constants.COLOR,
				 * Constants.YELLOW); int hours_48=0; for (Object hour48 :
				 * hours48) {
				 * hours_48+=Utils.getIntegerValueOfBigDecimalObj(hour48); }
				 * Map<String, Object> hoursMap = new HashMap<String, Object>();
				 * hoursMap.put(Constants.COLOR, Constants.YELLOW);
				 * hoursMap.put(Constants.Y, hours_48);
				 * hoursMapList.add(hoursMap); map.put("data", hoursMapList); }
				 * else { map.put(Constants.COLOR, Constants.RED); int
				 * hourMoreThan_48=0; for (Object hourMoreThan48 :
				 * hoursMoreThan48) {
				 * hourMoreThan_48+=Utils.getIntegerValueOfBigDecimalObj(
				 * hourMoreThan48); } Map<String, Object> hoursMap = new
				 * HashMap<String, Object>(); hoursMap.put(Constants.COLOR,
				 * Constants.RED); hoursMap.put(Constants.Y, hourMoreThan_48);
				 * hoursMapList.add(hoursMap); map.put("data", hoursMapList); }
				 * 
				 * serviceWiseMapList.add(map); }
				 */
				resultMap.put("dms", userIds);
				resultMap.put("series", serviceWiseMapList);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return resultMap;
	}

	@Override
	public Object getSubmittalServiceDetailByOrderId_Status_CreatedBy(Integer orderId, String createdBy, String status) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> serviceWiseMapList = null;
		try {
			StringBuffer sqlSelect = new StringBuffer();
			if (status != null && !status.equalsIgnoreCase(SubmittalStatus.SUBMITTED.name())) {
				sqlSelect.append("select count(CASE WHEN result.hours <=168 THEN 1 END) AS one_week,"
						+ " count(CASE WHEN (result.hours >168 AND result.hours <=336) THEN 1 END) AS two_week, "
						+ " count(CASE WHEN (result.hours >336 AND result.hours <=504) THEN 1 END) AS three_week, "
						+ "count(CASE WHEN (result.hours >504) THEN 1 END) AS more_than_three_week");
			} else {
				sqlSelect.append("select count(CASE WHEN result.hours <=24 THEN 1 END) AS hours_24,"
						+ " count(CASE WHEN (result.hours >24 AND result.hours <=48) THEN 1 END) AS hours_48, "
						+ " count(CASE WHEN (result.hours >48) THEN 1 END) AS hours_48_More ");
			}
			sqlSelect.append(" from " + " ( " + " select (EXTRACT(EPOCH FROM " + " CASE WHEN s.status='SUBMITTED' THEN s.created_on-j.created_on "
					+ " ELSE COALESCE(s.updated_on,s.created_on)-s.created_on END " + " )/3600) AS hours,s.status " + " from submittal s,"
					+ "      job_order j, " + "     candidate c " + " where s.delete_flag = 0 and " + "       j.order_id = s.order_id and "
					+ "       c.candidate_id = s.candidate_id and " + "      s.order_id = " + orderId + " and s.created_by = '" + createdBy + "' and "
					+ "      s.status = '" + status + "' " + "      ) AS result ");

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				log.info("Size of the Records ::: " + resultList.size());
				serviceWiseMapList = new ArrayList<Map<String, Object>>();
				List<Object> userIds = new ArrayList<Object>();
				userIds.add(status);
				if (status != null && status.equalsIgnoreCase(SubmittalStatus.SUBMITTED.name())) {
					getHorizontalBarChartInputData(serviceWiseMapList, resultList);
				} else {
					List<String> names = Arrays.asList("Served in One Week", "Served in Two Weeks", "Served in Three Weeks", "Served after Three Weeks");
					List<Object> one_week = new ArrayList<Object>();
					List<Object> two_week = new ArrayList<Object>();
					List<Object> three_week = new ArrayList<Object>();

					List<Object> more_than_three_week = new ArrayList<Object>();

					for (Object object : resultList) {
						Object[] obj = (Object[]) object;
						one_week.add(obj[0]);
						two_week.add(obj[1]);
						three_week.add(obj[2]);
						more_than_three_week.add(obj[3]);
					}
					for (int i = 0; i < names.size(); i++) {
						Map<String, Object> map = new LinkedHashMap<String, Object>();
						map.put("name", names.get(i));
						List<Map<String, Object>> hoursMapList = new ArrayList<Map<String, Object>>();
						if (i == 0) {
							map.put(Constants.COLOR, Constants.GREEN);
							int hours_oneWeek = 0;
							for (Object hour24 : one_week) {
								hours_oneWeek += Utils.getIntegerValueOfBigDecimalObj(hour24);
							}
							Map<String, Object> hoursMap = new HashMap<String, Object>();
							hoursMap.put(Constants.COLOR, Constants.GREEN);
							hoursMap.put(Constants.Y, hours_oneWeek);
							hoursMapList.add(hoursMap);
							map.put("data", hoursMapList);
						} else if (i == 1) {
							map.put(Constants.COLOR, Constants.YELLOW);
							int hours_TwoWeek = 0;
							for (Object hour48 : two_week) {
								hours_TwoWeek += Utils.getIntegerValueOfBigDecimalObj(hour48);
							}
							Map<String, Object> hoursMap = new HashMap<String, Object>();
							hoursMap.put(Constants.COLOR, Constants.YELLOW);
							hoursMap.put(Constants.Y, hours_TwoWeek);
							hoursMapList.add(hoursMap);
							map.put("data", hoursMapList);
						} else if (i == 2) {
							map.put(Constants.COLOR, Constants.ORANGE);
							int hours_ThreeWeek = 0;
							for (Object threeWeek : three_week) {
								hours_ThreeWeek += Utils.getIntegerValueOfBigDecimalObj(threeWeek);
							}
							Map<String, Object> hoursMap = new HashMap<String, Object>();
							hoursMap.put(Constants.COLOR, Constants.ORANGE);
							hoursMap.put(Constants.Y, hours_ThreeWeek);
							hoursMapList.add(hoursMap);
							map.put("data", hoursMapList);
						} else {
							map.put(Constants.COLOR, Constants.RED);
							int hourMoreThan3 = 0;
							for (Object more_than_3_week : more_than_three_week) {
								hourMoreThan3 += Utils.getIntegerValueOfBigDecimalObj(more_than_3_week);
							}
							Map<String, Object> hoursMap = new HashMap<String, Object>();
							hoursMap.put(Constants.COLOR, Constants.RED);
							hoursMap.put(Constants.Y, hourMoreThan3);
							hoursMapList.add(hoursMap);
							map.put("data", hoursMapList);
						}

						serviceWiseMapList.add(map);
					}
				}
				resultMap.put("dms", userIds);
				resultMap.put("series", serviceWiseMapList);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return resultMap;
	}

	@Override
	public Object getSubmittal_Service_Of_All_Job_Orders_BY_DM(ReportwiseDto reportwiseDto) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> serviceWiseMapList = null;
		try {
			StringBuffer sqlSelect = new StringBuffer("" + "select count(CASE WHEN result.hours <=24 THEN 1 END) AS hours_24,"
					+ " count(CASE WHEN (result.hours >24 AND result.hours <=48) THEN 1 END) AS hours_48, "
					+ " count(CASE WHEN (result.hours >48) THEN 1 END) AS hours_48_More " + " ,result.assignedbdm " + "  from "
					+ " (select (EXTRACT(EPOCH FROM COALESCE(s.updated_on,s.created_on)-j.created_on)/3600) AS hours, "
					+ " s.created_on,j.created_on,s.created_by AS createdBy,u.user_id "
					+ " ,(CASE WHEN u.assigned_bdm ISNULL THEN u.user_id ELSE u.assigned_bdm END) AS assignedbdm "
					+ "  from submittal s,job_order j,user_acct u " + " where  s.delete_flag=0 and s.created_by=u.user_id "
					+ " and j.order_id=s.order_id and j.delete_flg=0 ");
					/*
					 * + " and j.status in ('OPEN', 'ASSIGNED', 'REOPEN') " +
					 * " and u.status in ('ACTIVE','INACTIVE') " +
					 * " and EXTRACT(YEAR from COALESCE(s.updated_on,s.created_on)) in (2016) "
					 * + " and " +
					 * " j.created_by in ((select ua.user_id from user_acct ua "
					 * +
					 * "  where ua.user_id ='pvkdm' or ua.assigned_bdm = 'pvkdm')) "
					 * +
					 * " and EXTRACT(MONTH from COALESCE(s.updated_on,s.created_on)) in (10) "
					 * );
					 */

			// + " ) AS result group by result.assignedbdm");

			// + "select count(CASE WHEN result.hours <=24 THEN 1 END) AS
			// hours_24,"
			// + " count(CASE WHEN (result.hours >24 AND result.hours <=48) THEN
			// 1 END) AS hours_48, "
			// + " count(CASE WHEN (result.hours >48) THEN 1 END) AS
			// hours_48_More "
			// + " from "
			// + " ( "
			// + " select (EXTRACT(EPOCH FROM
			// COALESCE(s.updated_on,s.created_on)-s.created_on)/3600) AS
			// hours,s.status "
			// + " from submittal s where s.order_id in ( "
			// + " select j.order_id from job_order j,user_acct u "
			// + " where j.delete_flg =0 and "
			// + " j.created_by=u.user_id ");

			if (reportwiseDto.getYear() != null) {
				sqlSelect.append("  and EXTRACT(YEAR from j.created_on) in (" + reportwiseDto.getYear() + ")");
			}
			if (reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() > 0) {
				sqlSelect.append(" and to_char(j.created_on,'Mon') in (" + Utils.getListWithSingleQuote(reportwiseDto.getMonths()) + ")");
			}
			if (reportwiseDto.getDmNames() != null && reportwiseDto.getDmNames().size() > 0) {
				sqlSelect.append(" and (j.created_by in (" + Utils.getListWithSingleQuote(reportwiseDto.getDmNames()) + "))");
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() == 1
					&& reportwiseDto.getMonths().get(0).equalsIgnoreCase(MonthEnum.JAN.getMonth())) {
				sqlSelect.append(" and EXTRACT(week from j.created_on) in (" + reportwiseDto.getWeek() + ")");
			} else if (reportwiseDto.getWeek() != null) {
				sqlSelect.append(" and (extract(week from j.created_on) - " + "extract(week from date_trunc('month', j.created_on)) + 1) in ("
						+ reportwiseDto.getWeek() + ")");
			}
			if (reportwiseDto.getStatus() != null && reportwiseDto.getStatus().length() > 0) {
				if (reportwiseDto.getStatus().equalsIgnoreCase(Constants.OPEN)) {
					sqlSelect.append(" and j.status in ('OPEN','ASSIGNED','REOPEN') ");
				} else {
					sqlSelect.append(" and j.status in ('CLOSED','FILLED') ");
				}
			}

			sqlSelect.append(" ) AS result     group by result.assignedbdm ");

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				log.info("Size of the Records ::: " + resultList.size());
				serviceWiseMapList = new ArrayList<Map<String, Object>>();
				List<Object> userIds = new ArrayList<Object>();
				userIds.add(reportwiseDto.getDmName());
				getHorizontalBarChartInputData(serviceWiseMapList, resultList);
				/*
				 * List<String> names = Arrays.asList("Served in 24 Hours",
				 * "Served in 48 Hours", "Served after 48 Hours"); List<Object>
				 * hours24 = new ArrayList<Object>(); List<Object> hours48 = new
				 * ArrayList<Object>(); List<Object> hoursMoreThan48 = new
				 * ArrayList<Object>();
				 * 
				 * 
				 * for (Object object : resultList) { Object[] obj = (Object[])
				 * object; hours24.add(obj[0]); hours48.add(obj[1]);
				 * hoursMoreThan48.add(obj[2]); } for (int i = 0; i <
				 * names.size(); i++) { Map<String, Object> map = new
				 * LinkedHashMap<String, Object>(); map.put("name",
				 * names.get(i)); List<Map<String, Object>> hoursMapList = new
				 * ArrayList<Map<String, Object>>(); if (i == 0) {
				 * map.put(Constants.COLOR, Constants.GREEN); int hours_24=0;
				 * for (Object hour24 : hours24) {
				 * hours_24+=Utils.getIntegerValueOfBigDecimalObj(hour24); }
				 * Map<String, Object> hoursMap = new HashMap<String, Object>();
				 * hoursMap.put(Constants.COLOR, Constants.GREEN);
				 * hoursMap.put(Constants.Y, hours_24);
				 * hoursMapList.add(hoursMap); map.put("data", hoursMapList); }
				 * else if (i == 1) { map.put(Constants.COLOR,
				 * Constants.YELLOW); int hours_48=0; for (Object hour48 :
				 * hours48) {
				 * hours_48+=Utils.getIntegerValueOfBigDecimalObj(hour48); }
				 * Map<String, Object> hoursMap = new HashMap<String, Object>();
				 * hoursMap.put(Constants.COLOR, Constants.YELLOW);
				 * hoursMap.put(Constants.Y, hours_48);
				 * hoursMapList.add(hoursMap); map.put("data", hoursMapList); }
				 * else { map.put(Constants.COLOR, Constants.RED); int
				 * hourMoreThan_48=0; for (Object hourMoreThan48 :
				 * hoursMoreThan48) {
				 * hourMoreThan_48+=Utils.getIntegerValueOfBigDecimalObj(
				 * hourMoreThan48); } Map<String, Object> hoursMap = new
				 * HashMap<String, Object>(); hoursMap.put(Constants.COLOR,
				 * Constants.RED); hoursMap.put(Constants.Y, hourMoreThan_48);
				 * hoursMapList.add(hoursMap); map.put("data", hoursMapList); }
				 * 
				 * serviceWiseMapList.add(map); }
				 */
				resultMap.put("dms", userIds);
				resultMap.put("series", serviceWiseMapList);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return resultMap;
	}

	@Override
	public Object getDMAverageNoOfDaysForStatus(ReportwiseDto reportwiseDto) {
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
					+ "j.order_id, s.status  from submittal s,job_order j,user_acct u " + "where  s.delete_flag=0 and s.created_by=u.user_id "
					+ "and j.order_id=s.order_id and j.delete_flg=0 " + "and u.user_role in ('DM','ADM','Recruiter') "
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
	public Object getAvgNoOfStatusesDaysByJobOrderId(Integer orderId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> serviceWiseMapList = null;
		try {
			StringBuffer sqlSelect = new StringBuffer("select count(CASE WHEN result.hours <=24 THEN 1 END) AS hours_24,"
					+ "count(CASE WHEN (result.hours >24 AND result.hours <=48) THEN 1 END) AS hours_48, "
					+ " count(CASE WHEN (result.hours >48) THEN 1 END) AS hours_48_More " + " ,result.assignedbdm " + "  from "
					+ " (select (EXTRACT(EPOCH FROM (CASE WHEN s.status = 'SUBMITTED' THEN s.created_on ELSE s.updated_on END)-j.created_on)/3600) AS hours, "
					+ " s.created_on,j.created_on,s.created_by AS createdBy,u.user_id "
					+ " ,(CASE WHEN u.assigned_bdm ISNULL THEN u.user_id ELSE u.assigned_bdm END) AS assignedbdm "
					+ "  from submittal s,job_order j,user_acct u " + " where  s.delete_flag=0 and s.created_by=u.user_id "
					+ " and j.order_id=s.order_id and j.delete_flg=0 " + " and u.status in ('ACTIVE','INACTIVE') "
					// + " and EXTRACT(YEAR from
					// COALESCE(s.updated_on,s.created_on)) in (2016) "
					+ " and j.order_id= " + orderId + " ) AS result   group by result.assignedbdm");
			/*
			 * 
			 * 
			 * " select count(CASE WHEN result.hours <=24 THEN 1 END) AS hours_24, "
			 * +
			 * " count(CASE WHEN (result.hours >24 AND result.hours <=48) THEN 1 END) AS hours_48, "
			 * +
			 * " count(CASE WHEN (result.hours >48) THEN 1 END) AS hours_48_More,sum(result.hours) AS hours, "
			 * + "result.status AS status from " + " ( " +
			 * " select (EXTRACT(EPOCH FROM COALESCE(s.updated_on,s.created_on)-s.created_on)/3600) AS hours "
			 * +
			 * " ,s.created_by AS createdBy,s.status AS status from submittal s,user_acct u "
			 * + " where s.delete_flag=0 " + " and s.created_by=u.user_id " +
			 * " and s.order_id= "+ orderId + " ) AS result " +
			 * " group by result.status ");
			 */

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				log.info("Size of the Records ::: " + resultList.size());
				serviceWiseMapList = new ArrayList<Map<String, Object>>();
				getHorizontalBarChartInputData(serviceWiseMapList, resultList);
				List<Object> jobOrderIds = new ArrayList<Object>();
				jobOrderIds.add(orderId);
				resultMap.put("orderId", jobOrderIds);
				resultMap.put("series", serviceWiseMapList);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return resultMap;
	}

	private void getHorizontalBarChartInputData(List<Map<String, Object>> serviceWiseMapList, List<?> resultList) {
		List<String> names = Arrays.asList("Served after 48 Hours", "Served in 48 Hours", "Served in 24 Hours");

		List<Object> hours24 = new ArrayList<Object>();
		List<Object> hours48 = new ArrayList<Object>();
		List<Object> hoursMoreThan48 = new ArrayList<Object>();

		for (Object object : resultList) {
			Object[] obj = (Object[]) object;
			hours24.add(obj[0]);
			hours48.add(obj[1]);
			hoursMoreThan48.add(obj[2]);
		}
		for (int i = 0; i < names.size(); i++) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("name", names.get(i));
			List<Map<String, Object>> hoursMapList = new ArrayList<Map<String, Object>>();
			if (i == 0) {
				map.put(Constants.COLOR, Constants.RED);
				int hourMoreThan_48 = 0;
				for (Object hourMoreThan48 : hoursMoreThan48) {
					hourMoreThan_48 += Utils.getIntegerValueOfBigDecimalObj(hourMoreThan48);
				}
				Map<String, Object> hoursMap = new HashMap<String, Object>();
				hoursMap.put(Constants.COLOR, Constants.RED);
				hoursMap.put(Constants.Y, hourMoreThan_48);
				hoursMapList.add(hoursMap);
				map.put("data", hoursMapList);

			} else if (i == 1) {
				map.put(Constants.COLOR, Constants.YELLOW);
				int hours_48 = 0;
				for (Object hour48 : hours48) {
					hours_48 += Utils.getIntegerValueOfBigDecimalObj(hour48);
				}
				Map<String, Object> hoursMap = new HashMap<String, Object>();
				hoursMap.put(Constants.COLOR, Constants.YELLOW);
				hoursMap.put(Constants.Y, hours_48);
				hoursMapList.add(hoursMap);
				map.put("data", hoursMapList);
			} else {
				map.put(Constants.COLOR, Constants.GREEN);
				int hours_24 = 0;
				for (Object hour24 : hours24) {
					hours_24 += Utils.getIntegerValueOfBigDecimalObj(hour24);
				}
				Map<String, Object> hoursMap = new HashMap<String, Object>();
				hoursMap.put(Constants.COLOR, Constants.GREEN);
				hoursMap.put(Constants.Y, hours_24);
				hoursMapList.add(hoursMap);
				map.put("data", hoursMapList);
			}

			serviceWiseMapList.add(map);
		}
	}

	@Override
	public Object getOrderWiseSubmittals(Integer orderId) {
		List<SubmittalStatsDto> submittalStatsDtoList = null;
		try {
			StringBuffer sqlSelect = new StringBuffer("select s.created_by,s.status,count(s.status), "
					+ "((CASE WHEN u.first_name ISNULL THEN '' ELSE u.first_name END) ||' '|| (CASE WHEN u.last_name ISNULL THEN '' ELSE u.last_name END)) "
					+ "AS full_name  from submittal s,user_acct u where " + "s.delete_flag=0 and s.created_by = u.user_id " + "and s.order_id=" + orderId
					+ " group by s.created_by,s.status,u.user_id");

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				submittalStatsDtoList = new ArrayList<SubmittalStatsDto>();
				log.info("Size of the Records ::: " + resultList.size());
				for (Object object : resultList) {
					Object obj[] = (Object[]) object;
					SubmittalStatsDto dto = new SubmittalStatsDto();
					dto.setUserId(Utils.getStringValueOfObj(obj[0]));
					dto.setStatus(Utils.getStringValueOfObj(obj[1]));
					dto.setCount(Utils.getIntegerValueOfBigDecimalObj(obj[2]));
					dto.setName(Utils.getStringValueOfObj(obj[3]));
					submittalStatsDtoList.add(dto);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return submittalStatsDtoList;
	}

	@Override
	public Object getSubmittalDetailByOrderId_Status_CreatedBy(Integer orderId, String createdBy, String status) {
		List<SubmittalStatsDto> submittalStatsDtoList = null;
		try {
			StringBuffer sqlSelect = new StringBuffer("");
			if (status != null && status.trim().length() > 0) {
				sqlSelect.append("select j.order_id,j.title,j.customer ,c.first_name,c.last_name,s.created_by,COALESCE(s.updated_on,s.created_on)"
						+ "    from submittal s,job_order j, candidate c where s.delete_flag=0 "
						+ "    and j.order_id=s.order_id and c.candidate_id=s.candidate_id " + " and s.order_id=" + orderId + "and s.created_by='" + createdBy
						+ "' " + "    and s.status='" + status + "'");
			} else {
				sqlSelect.append("select j.order_id,j.title,j.customer, " + "count(s) AS submitted_count, "
						+ "count(CASE WHEN s.status='INTERVIEWING' THEN 1 END) AS inteviewing_count, "
						+ "count(CASE WHEN s.status='CONFIRMED' THEN 1 END) AS confirmed_count, "
						+ "count(CASE WHEN s.status='STARTED' THEN 1 END) AS started_count" + " from submittal s,job_order j, candidate c "
						+ "where s.delete_flag=0 " + "and j.order_id=s.order_id " + "and c.candidate_id=s.candidate_id " + " and s.order_id=" + orderId
						+ "and s.created_by='" + createdBy + "' " + " group by j.order_id");
			}
			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				submittalStatsDtoList = new ArrayList<SubmittalStatsDto>();
				log.info("Size of the Records ::: " + resultList.size());
				for (Object object : resultList) {
					Object obj[] = (Object[]) object;
					SubmittalStatsDto dto = new SubmittalStatsDto();
					if (status != null && status.trim().length() > 0) {
						dto.setOrderId(Utils.getIntegerValueOfBigDecimalObj(obj[0]));
						dto.setJobTitle(Utils.getStringValueOfObj(obj[1]));
						dto.setClientName(Utils.getStringValueOfObj(obj[2]));
						dto.setCandidateFullName(Utils.concatenateTwoStringsWithSpace(Utils.getStringValueOfObj(obj[3]), Utils.getStringValueOfObj(obj[4])));
						dto.setCreatedOrUpdatedBy(Utils.getStringValueOfObj(obj[5]));
						dto.setCreatedOrUpdatedOn(Utils.convertDateToString((Date) obj[6]));
					} else {
						dto.setOrderId(Utils.getIntegerValueOfBigDecimalObj(obj[0]));
						dto.setJobTitle(Utils.getStringValueOfObj(obj[1]));
						dto.setClientName(Utils.getStringValueOfObj(obj[2]));
						dto.setSUBMITTED(Utils.getIntegerValueOfBigDecimalObj(obj[3]));
						dto.setINTERVIEWING(Utils.getIntegerValueOfBigDecimalObj(obj[4]));
						dto.setCONFIRMED(Utils.getIntegerValueOfBigDecimalObj(obj[5]));
						dto.setSTARTED(Utils.getIntegerValueOfBigDecimalObj(obj[6]));
					}
					submittalStatsDtoList.add(dto);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return submittalStatsDtoList;
	}

	@Override
	public Object getOther_Than_Started_Report(ReportwiseDto reportwiseDto) {
		List<Map<String, Object>> serviceWiseMapList = null;
		try {
			StringBuffer sqlSelect = new StringBuffer(
					"select result.status,sum(result.count) from  " + "(select count(s.status) AS count, s.status AS status,u.user_id,u.assigned_bdm from"
							+ " submittal s,job_order j,user_acct u where s.delete_flag=0 and s.order_id=j.order_id "
							+ "and s.created_by=u.user_id and j.delete_flg=0 " + "and u.status in ('ACTIVE','INACTIVE') ");
			// + " and s.status not in ('STARTED') "
			// + " and EXTRACT(YEAR from COALESCE(s.updated_on,s.created_on)) in
			// (2016) "
			// + "");
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
			sqlSelect.append(" group by s.status,u.user_id) AS result group by result.status ");

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				log.info("Size of the Records ::: " + resultList.size());
				serviceWiseMapList = new ArrayList<Map<String, Object>>();
				for (Object object : resultList) {
					Map<String, Object> serviceWiseMap = new HashMap<String, Object>();
					Object[] obj = (Object[]) object;
					serviceWiseMap.put("userId", obj[2]);
					serviceWiseMap.put("avg_days", Utils.getTwoDecimalDoubleFromObj(obj[0]));
					serviceWiseMap.put("count", obj[3]);
					serviceWiseMap.put("sum", obj[1]);
					serviceWiseMapList.add(serviceWiseMap);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return serviceWiseMapList;
	}

	@Override
	public Object findHitRatio(ReportwiseDto reportwiseDto) {
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
					+ "  (CASE WHEN u.last_name ISNULL THEN '' ELSE u.last_name END)) AS full_name " + "  from submittal s,job_order j,user_acct u "
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

	private void getSubmittalData(List<?> list, List<SubmittalStatsDto> submittalStatsDtoList) {
		for (Object object : list) {
			Object obj[] = (Object[]) object;
			SubmittalStatsDto dto = new SubmittalStatsDto();
			dto.setUserId(Utils.getStringValueOfObj(obj[0]));
			dto.setName(Utils.getStringValueOfObj(obj[13]));
			dto.setAssignedBdm(Utils.getStringValueOfObj(obj[1]));
			dto.setRank(Utils.getIntegerValueOfObj(obj[2]));
			dto.setSUBMITTED(Utils.getIntegerValueOfBigDecimalObj(obj[3]));
			dto.setDMREJ(Utils.getIntegerValueOfBigDecimalObj(obj[5]));
			dto.setACCEPTED(Utils.getIntegerValueOfBigDecimalObj(obj[6]));
			dto.setINTERVIEWING(Utils.getIntegerValueOfBigDecimalObj(obj[7]));
			dto.setCONFIRMED(Utils.getIntegerValueOfBigDecimalObj(obj[8]));
			dto.setREJECTED(Utils.getIntegerValueOfBigDecimalObj(obj[9]));
			dto.setSTARTED(Utils.getIntegerValueOfBigDecimalObj(obj[4]));
			dto.setBACKOUT(Utils.getIntegerValueOfBigDecimalObj(obj[10]));
			dto.setOUTOFPROJ(Utils.getIntegerValueOfBigDecimalObj(obj[11]));
			dto.setNotUpdated(Utils.getIntegerValueOfBigDecimalObj(obj[12]));
			submittalStatsDtoList.add(dto);
		}
	}

	@Override
	public Object getdmsDetailsReport(Date from, Date to, String dmName, String status) {

		List<SubmittalDto> subDtos = new ArrayList<SubmittalDto>();

		StringBuffer sql = new StringBuffer("");
		if (dmName != null && dmName.trim().length() > 0) {
			sql.append(" select result.assigned_BDM, count(result) AS submitted_count, "
				+ "count( CASE WHEN result.status ='CONFIRMED' THEN 1 END) AS confirmed_count, "
				+ "count( CASE WHEN result.status ='STARTED' THEN 1 END) AS started_count ,((CASE WHEN uaaa.first_name ISNULL THEN '' ELSE uaaa.first_name END)||' '||"
					+ "(CASE WHEN uaaa.last_name ISNULL THEN '' ELSE uaaa.last_name END)) AS full_name,d.name,d.min_start_count,d.max_start_count from ( select u.user_id  AS assigned_BDM, ");
		} else {
			sql.append(" select newResult.userId,sum(newResult.submitted_count) AS sub_count,sum(newResult.confirmed_count) AS conf_count "
				+ " ,sum(newResult.started_count) AS start_count from"
				+ " (select ( CASE WHEN ua.user_role = 'DM' THEN ua.user_id ELSE ua.assigned_bdm "
				+ " END) AS userId, count(result) AS submitted_count, "
				+ "count( CASE WHEN result.status ='CONFIRMED' THEN 1 END) AS confirmed_count, "
				+ "count( CASE WHEN result.status ='STARTED' THEN 1 END) AS started_count  from ( select (CASE WHEN u.user_role = 'DM' THEN u.user_id ELSE u.assigned_bdm END  ) AS assigned_BDM, ");
		}
		sql.append(" s.status AS status  from submittal s,user_acct u where ");

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
			sql.append(" u.user_role in('DM','ADM','Recruiter') and ");
		}

		sql.append(" s.delete_flag = 0 and s.created_by=u.user_id) AS result");
		if (dmName != null && dmName.trim().length() > 0) {
			sql.append(",user_acct uaaa,designation d " + "  where uaaa.user_id = result.assigned_BDM  and uaaa.level=d.id ");
			if (status != null && status.trim().length() > 0) {
				sql.append(" and uaaa.status='" + status + "'");
			}
			sql.append(" group by result.assigned_BDM,uaaa.user_id,d.name,d.min_start_count,d.max_start_count ");
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
				dto.setStartedCount(obj[3] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[3]).toString() : "0");
				dto.setActiveStartedCount(obj[3] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[3]).toString() : "0");
				dto.setInActiveStartedCount("0");
				activeStarts+=Integer.parseInt(dto.getStartedCount());
				dto.setUserId(userId);
				if (dmName != null && dmName.trim().length() > 0) {
					dto.setDmName(Utils.getStringValueOfObj(obj[4]));
					dto.setLevel(Utils.getStringValueOfObj(obj[5]));
					dto.setMinStartCount(Utils.getIntegerValueOfBigDecimalObj(obj[6]));
					dto.setMaxStartCount(Utils.getIntegerValueOfBigDecimalObj(obj[7]));
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
			if (Double.valueOf(dto.getAvgHires()) <= (dmName != null ? ((Double.valueOf(dto.getMinStartCount()) / 12f) * 0.75f) : 0.75f)) {
				color = Constants.RED;
				performanceStatus = Constants.POOR;
			} else if (Double.valueOf(dto.getAvgHires()) > (dmName != null ? ((Double.valueOf(dto.getMinStartCount()) / 12f) * 0.75f) : 0.75f)
					&& Double.valueOf(dto.getAvgHires()) <= (dmName != null ? ((Double.valueOf(dto.getMinStartCount()) / 12f)) : 1.0f)) {
				color = Constants.YELLOW;
				performanceStatus = Constants.AVERAGE;
			} else if (Double.valueOf(dto.getAvgHires()) > (dmName != null ? ((Double.valueOf(dto.getMinStartCount()) / 12f)) : 1.0f)
					&& Double.valueOf(dto.getAvgHires()) <= (dmName != null ? ((Double.valueOf(dto.getMinStartCount()) / 12f) * 1.50f) : 1.50f)) {
				color = Constants.LIGHT_GREEN;
				performanceStatus = Constants.GOOD;
			} else if (Double.valueOf(dto.getAvgHires()) > (dmName != null ? ((Double.valueOf(dto.getMinStartCount()) / 12f) * 1.50f) : 1.50f)) {
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
			if (Double.valueOf(dto.getAvgHires()) <= (dmName != null ? ((Double.valueOf(dto.getMinStartCount()) / 12f) * 0.75f) : 0.75f)) {
				color = Constants.RED;
			} else if (Double.valueOf(dto.getAvgHires()) > (dmName != null ? ((Double.valueOf(dto.getMinStartCount()) / 12f) * 0.75f) : 0.75f)
					&& Double.valueOf(dto.getAvgHires()) <= (dmName != null ? ((Double.valueOf(dto.getMinStartCount()) / 12f)) : 1.0f)) {
				color = Constants.YELLOW;
			} else if (Double.valueOf(dto.getAvgHires()) > (dmName != null ? ((Double.valueOf(dto.getMinStartCount()) / 12f)) : 1.0f)
					&& Double.valueOf(dto.getAvgHires()) <= (dmName != null ? ((Double.valueOf(dto.getMinStartCount()) / 12f) * 1.50f) : 1.50f)) {
				color = Constants.LIGHT_GREEN;
			} else if (Double.valueOf(dto.getAvgHires()) > (dmName != null ? ((Double.valueOf(dto.getMinStartCount()) / 12f) * 1.50f) : 1.50f)) {
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
				+ " (select (CASE WHEN ua.user_role = 'DM' THEN ua.user_id ELSE "
				+ "  ua.assigned_bdm END) AS userId,"
				+ "  count(result.submittal_id) "
				+ "   from(Select result1.submittal_id, "
				+ "       result1.created_by, "
				+ "       result1.status, "
				+ "       (CASE  WHEN u.user_role = 'DM' THEN u.user_id "
				+ "  ELSE u.assigned_bdm END) AS assigned_BDM "
				+ " from (select s.submittal_id, count(h.submittal_id) AS count, s.created_by, "
				+ " s.status from submittal s, submittal_history h where s.submittal_id =   h.submittal_id"
				+ " AND DATE (COALESCE(h.updated_on, h.created_on)) >= '"+from+"' "
				+ "   AND DATE (COALESCE(h.updated_on, h.created_on)) <= '"+to+"' AND h.status in ('STARTED') "
						+ "AND DATE (COALESCE(s.updated_on, s.created_on)) >= '"+from+"'"
						+ "   AND DATE (COALESCE(s.updated_on, s.created_on)) <= '"+to+"'"
				+ " AND s.delete_flag = 0 AND s.status in ('OUTOFPROJ')  group by s.submittal_id) AS result1,"
				+ " user_acct u where result1.created_by = u.user_id AND "
				+ "      u.user_role in ('DM', 'ADM', 'Recruiter')) AS result, "
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
	
	@Override
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
				+ "       (CASE   WHEN u.user_role = 'DM' THEN u.user_id   ELSE u.assigned_bdm END) AS assigned_BDM "
				+ " from (select s.submittal_id, count(h.submittal_id) AS count, s.created_by, "
				+ " s.status from submittal s, submittal_history h,user_acct u1 where s.submittal_id =   h.submittal_id"
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
				+ "      u.user_role in ('DM', 'ADM', 'Recruiter')) AS"
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
	
	
	private Map<String, SubmittalStatsDto> getInActiveStartedOfCandidateSource(ReportwiseDto reportwiseDto) {
		Map<String, SubmittalStatsDto> submittalsMap = new HashMap<String, SubmittalStatsDto>();

		StringBuffer sql = new StringBuffer(" select result.source,"
				+ " count(result.inActiveStarted_count) AS started_count"
				+ " from (select history_result.inActiveStarted_count, ( CASE WHEN j.created_on >= c.created_on THEN"
				+ " 'ATS' ELSE CASE WHEN c.created_by = 'Dice' THEN 'Dice' ELSE CASE WHEN "
				+ " c.created_by = 'Monster' THEN 'Monster' ELSE CASE WHEN c.created_by = "
				+ "'Careerbuilder' THEN 'Careerbuilder' ELSE CASE WHEN c.created_by = "
				+ " 'Techfetch' THEN 'Techfetch' ELSE 'Other'END END END END END) AS source from"
				+ "  ( "
				+ " select s.submittal_id, count(h.submittal_id) AS inActiveStarted_count,"
				+ " s.created_by, s.status,s.order_id,s.candidate_id from submittal s, submittal_history h, user_acct u1"
				+ "  where s.submittal_id = h.submittal_id AND u1.user_id = s.created_by "
				);
		
		if(reportwiseDto!=null){
		if (reportwiseDto.getYear() != null) {
			sql.append("  and EXTRACT(YEAR from COALESCE(h.updated_on,h.created_on)) in (" + reportwiseDto.getYear() + ")");
			sql.append("  and EXTRACT(YEAR from COALESCE(s.updated_on,s.created_on)) in (" + reportwiseDto.getYear() + ")");
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
		}}
		sql.append(" AND h.status " + "    in ('STARTED')    ");
		
		if(reportwiseDto.getNames() != null && reportwiseDto.getNames().size() >0){
		sql.append( " AND u1.user_id in (select ua.user_id from "
				+ "     user_acct ua where ua.assigned_bdm in (" + Utils.getListWithSingleQuote(reportwiseDto.getNames()) + ") or ua.assigned_bdm IN ( "
				+ "    select uaa.user_id from user_acct uaa where uaa.assigned_bdm in (" + Utils.getListWithSingleQuote(reportwiseDto.getNames()) + "))");
			sql.append(" or ua.user_id in (" + Utils.getListWithSingleQuote(reportwiseDto.getNames()) + ")");
		sql.append(") ");
		}
		sql.append( " AND  s.delete_flag = 0 AND s.status in ('OUTOFPROJ') "
				+ "     group by s.submittal_id");
		
		sql.append( "  ) history_result, job_order j, candidate c where "
				+ " history_result.candidate_id = c.candidate_id AND j.order_id = history_result.order_id AND "
				+ " j.delete_flg = 0) AS result "
				+ " group by result.source ORDER by result.source");


		List<?> resultList = submittalDao.findBySQLQuery(sql.toString(), null);
		if (resultList != null) {
			Iterator<?> itr = resultList.iterator();
			while (itr.hasNext()) {
				SubmittalStatsDto dto = new SubmittalStatsDto();
				Object[] obj = (Object[]) itr.next();
				String source = (String) (obj[0] != null ? obj[0] : null);
				dto.setInactiveStarted(obj[1] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[1]) : 0);
				dto.setActiveStarted(0);
				dto.setSource(source);

				submittalsMap.put(dto.getSource(), dto);
			}

		}
		return submittalsMap;
	}
	
	
	private Map<String, SubmittalDto> getInActiveStartedOfClients(Date from, Date to) {
		Map<String, SubmittalDto> submittalsMap = new HashMap<String, SubmittalDto>();

		StringBuffer sql = new StringBuffer("select result.customer AS client,"
				+ "       sum(result.startedCount) as startedCount"
				+ "      from (select lower(j.customer) AS customer, j.order_id as orderCount, j.num_pos"
				+ " as noOfPos,  count(history_result.started_count) as startedCount"
				+ "  from job_order j left outer join ("
				+ "   select s.submittal_id, count(h.submittal_id) AS started_count, s.created_by,"
				+ " s.status,s.order_id from submittal s, submittal_history h where s.submittal_id =  h.submittal_id "
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
	
	private Map<String, SubmittalDto> getInActiveStartedOfClientsReportData(Date from, Date to,String client) {
		Map<String, SubmittalDto> submittalsMap = new HashMap<String, SubmittalDto>();

		StringBuffer sql = new StringBuffer("select j.order_id as orderid, "
				+ " count(history_result.started_count) as startedCount "
				+ " from job_order j"
				+ " LEFT OUTER JOIN"
				+ " ("
				+ " select s.submittal_id, count(h.submittal_id) AS started_count, s.created_by,"
				+ "s.status,s.order_id from submittal s, submittal_history h where s.submittal_id = h.submittal_id "
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

	private Map<String, SubmittalDto> getDmsStausOfJobOrders( Date from, Date to, String dmName, String status) {
		Map<String, SubmittalDto> jobOrderMap = new HashMap<String, SubmittalDto>();

		StringBuffer sql = new StringBuffer("select ((CASE WHEN ua.first_name ISNULL THEN ' ' ELSE ua.first_name END) || ' '||"
				+ "(CASE WHEN ua.last_name IS NULL THEN ' ' ELSE ua.last_name END)) AS full_name, "
				+ "count(result.no_of_pos),sum(result.no_of_pos),result.assigned_BDM,ua.user_id from " + "(select j.num_pos AS no_of_pos,");

		if (dmName != null && dmName.trim().length() > 0) {
			sql.append(" u.user_id AS user_id,u.assigned_bdm AS assigned_BDM ");
		} else {
			sql.append("  (CASE WHEN u.user_role = 'DM' THEN u.user_id ELSE u.assigned_bdm END  ) AS assigned_BDM ");
		}

		sql.append("from  job_order j,user_acct u where j.delete_flg=0 AND j.status !='PENDING' ");
		if (from != null) {
			sql.append(" AND j.created_on >= '" + from + "' ");
		}
		if (to != null) {
			sql.append(" AND j.created_on <= '" + to + "' ");
		}
		if (dmName != null && dmName.trim().length() > 0) {
			sql.append(" and u.user_id = j.assigned_to and  j.assigned_to in (select ua.user_id from user_acct ua where ua.assigned_bdm in (" + "    '" + dmName
					+ "') or ua.assigned_bdm IN (select uaa.user_id from user_acct uaa where " + "     uaa.assigned_bdm in ('" + dmName + "')))  ");
		} else {
			sql.append(" and u.user_id = COALESCE(j.dmname,j.created_by) and u.user_role in('DM','ADM','Recruiter')  ");
		}


		sql.append(") AS result ,user_acct ua");

		if (dmName != null && dmName.trim().length() > 0) {
			sql.append(" where ua.user_id=result.user_id ");
			if (status != null && status.trim().length() > 0) {
				sql.append(" and ua.status='" + status + "' ");
			}
			sql.append(" group by result.assigned_BDM,ua.user_id");
		} else {
			sql.append(" where ua.user_id=result.assigned_BDM and ua.status='ACTIVE' and ua.user_role='DM' group by result.assigned_BDM,ua.user_id");
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
					if (dmName != null && dmName.trim().length() > 0) {
						dto.setUserId(obj[4] != null ? (String) obj[4] : null);
					}else{
					dto.setUserId(obj[3] != null ? (String) obj[3] : null);
					}
					jobOrderMap.put(dto.getUserId(), dto);
				}
			}
		return jobOrderMap;
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
					+ " ( CASE WHEN ua.user_role = 'DM' THEN ua.user_id ELSE ua.assigned_bdm"
					+ " END) AS assigned_bdm,((CASE WHEN ua.first_name ISNULL THEN ' ' ELSE ua.first_name END) || ' '||"
					+ "				(CASE WHEN ua.last_name IS NULL THEN ' ' ELSE ua.last_name END)) AS full_name,"
					+ " result.rec_full_name,d.name,result.role,result.name AS recLevel "
					+ "  from "
					+ "(select u.user_id AS user_id, "
					+ "       u.join_date AS join_date, "
					+ "       u.level AS level, "
					+ "       d.min_start_count AS min_count, "
					+ "       d.max_start_count AS max_count, "
					+ "       (CASE   WHEN u.relieving_date ISNULL THEN (CASE "
					+ "  WHEN u.status = 'INACTIVE' THEN u.updated_on "
					+ "   ELSE CURRENT_DATE"
					+ " END)   ELSE u.relieving_date "
					+ " END) AS SERVEDDATE, "
					+ "       u.status AS status, "
					+ "       (CASE "
					+ "  WHEN u.user_role = 'DM' THEN u.user_id "
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
					users.setUserRole(UserRole.valueOf(Utils.getStringValueOfObj(obj[11])));
					usersList.add(users);
					recIdMap.put(users.getUserId(), users.getUserId());
					if(dmName==null){
					if (usersmap.get(users.getAssignedBdm()) != null) {
						usersmap.get(users.getAssignedBdm()).add(users);
						
						Map<String,Map<String,Integer>> activeInactiveUserCount = dmWithActiveInActiveUsers.get(users.getAssignedBdm());
						if(!users.getUserRole().equals(UserRole.DM)){
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
						if(!users.getUserRole().equals(UserRole.DM)){
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
					if(!dto.getUserRole().equals(UserRole.DM)){
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
							if((userDto.getUserRole().equals(UserRole.DM) && userDto.getStatus().equals(Constants.ACTIVE))|| dmName!=null){
							subDto.setDmName(userDto.getRecFullName());
							subDto.setLevel(userDto.getLevel());
							subDto.setNoOfJobOrders(jobOrderMap.get(key)!=null?jobOrderMap.get(key).getNoOfJobOrders():"0");
							subDto.setNoOfPositions(jobOrderMap.get(key)!=null?jobOrderMap.get(key).getNoOfPositions():"0");
							subDto.setMinStartCount(userDto.getMinStartCount());
							subDto.setMaxStartCount(userDto.getMaxStartCount());
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

	private void getCountsByLevel(Map<String,Map<String,Integer>> activeInactiveUserCount,UserDto users,String userStatus){
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
					+ "    u.status AS status, ( CASE WHEN u.user_role = 'DM' THEN u.user_id ELSE "
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
	
	
	
	
	private List<Map<String, Object>> getJobOrderOpenClosedCount(Date from, Date to, String dmName) {

		List<Map<String, Object>> jobOrderOpenClosedCountList = new ArrayList<Map<String, Object>>();
		
		StringBuffer sql = new StringBuffer("");
			sql.append("select count((CASE"
					+ "   WHEN result.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN 1 END)) AS open_count, "
					+ "       count((CASE   WHEN result.status in ('CLOSED', 'FILLED') THEN 1 END)) AS closed, "
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
					+ " select ( CASE WHEN u.user_role = 'DM' THEN u.user_id ELSE u.assigned_bdm "
					+ "END) AS assigned_BDM, j.status AS status, COALESCE(j.dmname, j.created_by) AS "
					+ " createdBy, ( CASE WHEN j.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN "
					+ "  j.num_pos ELSE 0 END) AS open_pos, ( CASE WHEN j.status in ('CLOSED', 'FILLED')    THEN j.num_pos ELSE 0 END) AS closed_pos, "
					+ "   ( CASE WHEN j.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN  "
					+ "  round((EXTRACT(EPOCH FROM date ( "
					+ "'"+to+"') - j.created_on) / 3600) / 24)  END)AS open_days "
					+ "    from job_order j, user_acct u where "
					+ "    j.delete_flg = 0 and COALESCE(j.dmname, j.created_by) = u.user_id and "
					+ "     u.user_role in ('DM', 'ADM', 'Recruiter') and j.created_on >= "
					+ "      '"+from+"' and j.created_on <= "
					+ "       '"+to+"' "
					+ "       )AS res "
					+ "       ) AS result, "
					+ "     user_acct ua "
					+ " where ua.user_id = result.assigned_BDM and "
					+ "      ua.status = 'ACTIVE' and       ua.user_role = 'DM' ");
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
	
	private List<Map<String, Object>> getSubmittalOpenClosedCount(Date from, Date to, String dmName,String status) {

		List<Map<String, Object>> submittalOpenClosedCountList = new ArrayList<Map<String, Object>>();
		
		StringBuffer sql = new StringBuffer("");
			sql.append("select "
					+ " count((CASE WHEN result.job_status in ('OPEN','ASSIGNED','REOPEN') THEN 1 END)) AS open_count, "
					+ "count((CASE WHEN result.job_status in ('CLOSED','FILLED') THEN 1 END)) AS closed, "
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
					+ " (CASE WHEN j.status in ('CLOSED','FILLED') THEN 1  END) ELSE 0 END) AS confirmedClosedCount, "
					+ "  ( CASE WHEN j.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN "
					+ "  round((EXTRACT(EPOCH FROM date ( '"+to+"') - j.created_on) / 3600) / 24)  END)AS open_days,"
					+ " ( CASE WHEN j.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN   j.num_pos ELSE 0 END) AS open_pos, "
					+ "  ( CASE WHEN u.user_role = 'DM' THEN u.user_id ELSE u.assigned_bdm END) AS assigned_BDM,"
					+ " (CASE WHEN s.status in ('STARTED') THEN "
					+ " (CASE WHEN j.status in ('OPEN','ASSIGNED','REOPEN') THEN 1 END) ELSE 0  END) AS startedOpenCount, "
					+ " (CASE WHEN s.status in ('STARTED') THEN "
					+ " (CASE WHEN j.status in ('CLOSED','FILLED') THEN 1  END) ELSE 0  END) AS startedClosedCount "
					+ "  from submittal s, user_acct u,job_order j where "
					+ "       COALESCE(s.updated_on, s.created_on) >= '"+from+"' "
					+ "       AND j.order_id=s.order_id "
					+ "        AND COALESCE(s.updated_on, s.created_on) <= "
					+ "         '"+to+"' AND u.user_role in ('DM', 'ADM',  'Recruiter') "
					+ "  and s.delete_flag = 0 and s.created_by = u.user_id ");
		if (dmName != null && dmName.trim().length() > 0) {
			sql.append(" and u.user_id in (select ua.user_id from user_acct ua where ua.assigned_bdm in (" + "    '" + dmName
					+ "') or ua.assigned_bdm IN (select uaa.user_id from user_acct uaa where " + "     uaa.assigned_bdm in ('" + dmName + "')))  ");
		}
		if (status != null && status.trim().length() > 0) {
			sql.append(" AND u.status='" + status + "'");
		}
					sql.append( "   ) AS res) AS result,user_acct ua"
							+ " where ua.status='ACTIVE' and result.assigned_BDM = ua.user_id and ua.user_role in ('DM','ADM')"); 
			


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
	
	@Override
	public Map<String, Object> getJobOrdersCustomReport(Date from, Date to,
			String string) {
		Map<String, Object> jobOrderesMap = new HashMap<String, Object>();
		List<SubmittalDto> list = new ArrayList<SubmittalDto>();
		StringBuffer sql = null;
		if(from !=null && to !=null){
		
			  sql = new StringBuffer("select j1.order_id,j1.created_by, j1.created_on, j1.customer, j1.title,j1.assigned_to,"
					+ "j1.num_pos, result.started_countfrom,result.subCount, (j1.num_pos -result.started_countfrom) AS netPos,"
					+ " result.confirmed_count from (select  count(CASE  WHEN s.status = 'CONFIRMED' THEN 1 END) AS confirmed_count,"
					+ " count(CASE  WHEN s.status = 'STARTED' THEN 1 END) AS started_countfrom,"
					+ " count(s.order_id) AS subCount, j.order_id  from job_order j "
					+ "	LEFT OUTER JOIN submittal s ON j.order_id = s.order_id and s.delete_flag = 0 WHERE j.delete_flg = 0 "
					+ "  AND j.created_on > '"+ from +"' AND j.created_on <"
					+ "   '"+ to +"'  GROUP BY j.num_pos, j.order_id) AS result,"
					+ "  job_order j1 WHERE j1.order_id = result.order_id");
		}
		
		List<?> result = submittalDao.findBySQLQuery(sql.toString(), null);
		Integer noOfPositions=0,noOfSub=0,noOfConfirmed=0,noOfStarted=0, netPositions=0;
		if(result != null){
			Iterator<?> itr = result.iterator();
			while(itr.hasNext()){
				SubmittalDto dto = new SubmittalDto();
				Object[] obj = (Object[]) itr.next();
				dto.setOrderId(obj[0] != null ? ((Integer)obj[0]).toString() : null);
				dto.setDmName(obj[1] != null ? ((String)obj[1]) : null);
				dto.setCreatedOn(obj[2] != null ? Utils.convertDateToString_HH_MM_A((Date)obj[2]) : null);
				dto.setJobClient(obj[3] != null ? ((String)obj[3]) : null);
				dto.setJobTitle(obj[4] != null ? ((String)obj[4]) : null);
				dto.setAssignedTo(obj[5] != null ? ((String)obj[5]) : null);
				dto.setNoOfPositions(obj[6] != null ? ((Integer)obj[6]).toString() : null);
				dto.setStartedCount(obj[7] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[7]).toString() : null);
				dto.setSubmittedCount(obj[8] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[8]).toString() : null);
				dto.setNetPositions(obj[9] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[9]).toString() : null);
				dto.setConfirmedCount(obj[10] != null ? Utils.getIntegerValueOfBigDecimalObj(obj[10]).toString() : null);
				noOfPositions += Integer.parseInt(dto.getNoOfPositions());
				noOfSub += Integer.parseInt(dto.getSubmittedCount());
				noOfConfirmed += Integer.parseInt(dto.getConfirmedCount());
				noOfStarted += Integer.parseInt(dto.getStartedCount());
				netPositions += Integer.parseInt(dto.getNetPositions());
				list.add(dto);
			}
		}
		jobOrderesMap.put("gridData", list);
		jobOrderesMap.put(Constants.NO_OF_POSITIONS, noOfPositions);
		jobOrderesMap.put(Constants.NO_OF_SUBMITTALS, noOfSub);
		jobOrderesMap.put(Constants.NO_OF_CONFIRMED, noOfConfirmed);
		jobOrderesMap.put(Constants.NO_OF_STARTS, noOfStarted);
		jobOrderesMap.put(Constants.NET_POSITIONS, netPositions);
		
		
		return jobOrderesMap;
	}

	@Override
	public Map<String, Object> getDMClientWiseSubmittals(Date srtDate, Date endDate, String dmName, String clients) {
		Map<String, Object> mapobj = new HashMap<String, Object>();
		List<SubmittalDto> list = new ArrayList<SubmittalDto>();
		StringBuffer sql = new StringBuffer();
		sql.append("select u.user_id,j.customer,count(s) AS submittal_count,count(CASE WHEN s.status='INTERVIEWING' THEN 1  END) AS interviewing, "
				+ "count(CASE WHEN s.status='CONFIRMED' THEN 1  END) AS confirmed, "
				+ "count(CASE WHEN s.status='STARTED' THEN 1  END) AS started, u.assigned_bdm,d.name, ((CASE WHEN u.first_name ISNULL THEN '' ELSE u.first_name END) ||' '|| "
				+ "(CASE WHEN u.last_name ISNULL THEN '' ELSE u.last_name END) )AS full_name from job_order j, submittal s,user_acct u, designation d "
				+ "where COALESCE(s.updated_on,s.created_on) >= '"+srtDate+"' and COALESCE(s.updated_on,s.created_on) <= '"+endDate+"' and "
				+ "j.created_by = '"+dmName+"' and j.customer in ("+clients+") and j.order_id = s.order_id and s.created_by=u.user_id and u.level=d.id "
				+ "GROUP BY u.user_id ,j.customer,d.name ORDER BY started DESC ");
		List<?> result = submittalDao.findBySQLQuery(sql.toString(), null);
		if(result!=null){
			Iterator<?> iterator = result.iterator();
			while(iterator.hasNext()){
			SubmittalDto dto = new SubmittalDto();
			Object[] obj = (Object[]) iterator.next();
			dto.setJobClient(obj[1]!= null ? (obj[1]).toString():null);
			dto.setLevel(obj[7]!=null ? (obj[7]).toString():null);
			dto.setRecName(obj[8]!=null ? (obj[8]).toString() : null);
			dto.setSubmittedCount(obj[2]!=null ? Utils.getIntegerValueOfBigDecimalObj(obj[2]).toString() : null);
			dto.setInterviewingCount(obj[3]!=null ? (obj[3]).toString() : null);
			dto.setConfirmedCount(obj[4]!=null ? Utils.getIntegerValueOfBigDecimalObj(obj[4]).toString() : null);
			dto.setStartedCount(obj[5]!=null ? Utils.getIntegerValueOfBigDecimalObj(obj[5]).toString() : null);
			list.add(dto);
			}
		}
		mapobj.put("gridData",list);
		return mapobj;
	}

	@Override
	public Map<String, Object> getcandidateSourceDetails(ReportwiseDto reportwiseDto) {
		// TODO Auto-generated method stub
		List<SubmittalStatsDto> dtos = new ArrayList<SubmittalStatsDto>();
		Map<String, Object> objMap = new HashMap<String, Object>();
		
		StringBuffer sql1 = new StringBuffer("select result.source, count(result.status) AS submitted_count, "
				+ "count(CASE WHEN result.status='ACCEPTED' THEN 1 END) AS accepted_count, "
				+ " count(CASE WHEN result.status='DMREJ' THEN 1 END) AS dmrej_count, "
				+ " count(CASE WHEN result.status='INTERVIEWING' THEN 1 END) AS inteviewing_count, "
				+ " count(CASE WHEN result.status='CONFIRMED' THEN 1 END) AS confirmed_count, "
				+ " count(CASE WHEN result.status='REJECTED' THEN 1 END) AS rejected_count, "
				+ " count(CASE WHEN result.status='STARTED' THEN 1 END) AS started_count, "
				+ " count(CASE WHEN result.status='BACKOUT' THEN 1 END) AS backout_count, "
				+ " count(CASE WHEN result.status='OUTOFPROJ' THEN 1 END) AS outofproj_count, "
				+ " count(CASE WHEN result.status='SUBMITTED' THEN 1 END) AS nu_count from"
				+ " ( select s.status AS status, (CASE WHEN j.created_on>=c.created_on THEN 'ATS' ELSE"
				+ "  CASE WHEN c.created_by='Dice' THEN 'Dice'  ELSE CASE WHEN c.created_by='Monster' THEN 'Monster'"
				+ "   ELSE CASE WHEN c.created_by='Careerbuilder' THEN 'Careerbuilder'"
				+ " ELSE CASE WHEN c.created_by='Techfetch' THEN 'Techfetch'"
				+ " ELSE 'Other'   END   END  END  END  END)AS source"
				+ "  from submittal s,job_order j, candidate c where s.delete_flag=0 AND"
				+ "     ");
		if(reportwiseDto.getNames() != null && reportwiseDto.getNames().size() >0){
			sql1.append(" s.created_by in (SELECT u.user_id from user_acct u where  u.user_id IN ( " +Utils.getListWithSingleQuote(reportwiseDto.getNames())+ " ) or u.assigned_bdm in "
					+ "(select ua.user_id from user_acct ua where ua.assigned_bdm IN ( " +Utils.getListWithSingleQuote(reportwiseDto.getNames())+ " ) "
							+ "  or ua.user_id in (" +Utils.getListWithSingleQuote(reportwiseDto.getNames())+ ") )) AND ");
		}
			if(reportwiseDto.getYear() != null){
			sql1.append(" EXTRACT(YEAR FROM COALESCE(s.updated_on,s.created_on)) in ("  + reportwiseDto.getYear() + ") and ");
					}
			if(reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() >0){
				sql1.append(" to_char(COALESCE(s.updated_on,s.created_on),'Mon') in ( " +Utils.getListWithSingleQuote(reportwiseDto.getMonths())+ " ) and ");
			}
			sql1.append(" s.candidate_id=c.candidate_id AND j.order_id=s.order_id "
				+ " AND j.delete_flg=0) AS result group by result.source   ORDER by result.source");
				
				List<?> result = submittalDao.findBySQLQuery(sql1.toString(), null);
				Integer noOfSource = 0, submittalsCount = 0, acceptedCount=0, interviewingCount=0, confirmedCount=0, rejectedCount=0,startedCount=0;
				if(result != null){
					Map<String,SubmittalStatsDto> submittalStatsMap = getInActiveStartedOfCandidateSource(reportwiseDto);
					Iterator<?> itr = result.iterator();
					while(itr.hasNext()){
						Object[] obj = (Object[]) itr.next();
						SubmittalStatsDto dto = new SubmittalStatsDto();
							dto.setSource(Utils.getStringValueOfObj(obj[0]));
							dto.setSUBMITTED(Utils.getIntegerValueOfBigDecimalObj(obj[1]));
							dto.setACCEPTED(Utils.getIntegerValueOfBigDecimalObj(obj[2]));
							dto.setDMREJ(Utils.getIntegerValueOfBigDecimalObj(obj[3]));
							dto.setINTERVIEWING(Utils.getIntegerValueOfBigDecimalObj(obj[4]));
							dto.setCONFIRMED(Utils.getIntegerValueOfBigDecimalObj(obj[5]));
							dto.setREJECTED(Utils.getIntegerValueOfBigDecimalObj(obj[6]));
							dto.setSTARTED(Utils.getIntegerValueOfBigDecimalObj(obj[7]));
							dto.setActiveStarted(Utils.getIntegerValueOfBigDecimalObj(obj[7]));
							dto.setInactiveStarted(0);
							dto.setBACKOUT(Utils.getIntegerValueOfBigDecimalObj(obj[8]));
							dto.setOUTOFPROJ(Utils.getIntegerValueOfBigDecimalObj(obj[9]));
							dto.setNotUpdated(Utils.getIntegerValueOfBigDecimalObj(obj[10]));
							if(submittalStatsMap!=null && submittalStatsMap.get(dto.getSource())!=null){
								dto.setInactiveStarted(submittalStatsMap.get(dto.getSource()).getInactiveStarted());
								dto.setSTARTED(dto.getSTARTED()+dto.getInactiveStarted());
							}
							dtos.add(dto);
							
							
							noOfSource +=1;
							submittalsCount += dto.getSUBMITTED();
							acceptedCount+=dto.getACCEPTED();
							interviewingCount+=dto.getINTERVIEWING();
							confirmedCount+=dto.getCONFIRMED();
							rejectedCount+=dto.getREJECTED();
							startedCount+=dto.getSTARTED();
									
						}
				}
				
				objMap.put(Constants.GRIDDATA, dtos);
				objMap.put(Constants.NO_OF_SOURCE, noOfSource);
				objMap.put(Constants.NO_OF_SUBMITTALS, submittalsCount);
				objMap.put(Constants.NO_OF_ACCEPTED, acceptedCount);
				objMap.put(Constants.NO_OF_INTERVIEWING, interviewingCount);
				objMap.put(Constants.NO_OF_CONFIRMED, confirmedCount);
				objMap.put(Constants.NO_OF_REJECTED, rejectedCount);
				objMap.put(Constants.NO_OF_STARTS, startedCount);
				
				return objMap;
	}

	@Override
	public Map<String, Object> getClientReportData(Date fromDate, Date endDate) {
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
				+ " count(case when s.status = 'CONFIRMED' THEN 1 END)as confirmedCount from job_order j"
				+ "  left outer join submittal s on s.order_id = j.order_id and s.delete_flag = 0 "
				+ "where date(j.created_on) >= '" + fromDate + "' AND  date(j.created_on) <= '" + endDate + "' and j.delete_flg = 0 "
						+ " AND j.status !='PENDING'"
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
				dto.setNetPositions(Utils.getIntegerValueOfBigDecimalObj(obj[6]).toString());
				if(submittalMap!=null && submittalMap.get(dto.getJobClient())!=null){
					dto.setInActiveStartedCount(submittalMap.get(dto.getJobClient()).getInActiveStartedCount());
					dto.setStartedCount(String.valueOf(Integer.parseInt(dto.getActiveStartedCount())+Integer.parseInt(dto.getInActiveStartedCount())));
				}
				dto.setNetPositions(String.valueOf(Integer.parseInt(dto.getNoOfPositions())-Integer.parseInt(dto.getStartedCount())));
				dtos.add(dto);
				
				noOfClients +=1;
				jobOrderCount += Utils.getIntegerValueOfBigDecimalObj(obj[1]);
				submittalsCount += Utils.getIntegerValueOfBigDecimalObj(obj[2]);
				jobPositions += Utils.getIntegerValueOfBigDecimalObj(obj[3]);
				noOfStarted += Integer.parseInt(dto.getStartedCount());
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

	@Override
	public Map<String, Object> getClientDetialsReportData(Date fromDate, Date endDate, String clientName) {
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
		  		+ " from job_order j LEFT OUTER JOIN submittal s ON s.order_id = j.order_id AND s.delete_flag = 0  where ");
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
					dto.setNetPositions(String.valueOf(Integer.parseInt(dto.getNoOfPositions())-Integer.parseInt(dto.getStartedCount())));
					dtos.add(dto);
					
					noOfjobOrders +=1;
					jobPositions += Utils.getIntegerValueOfBigDecimalObj(obj[1]);
					submittalsCount += Utils.getIntegerValueOfBigDecimalObj(obj[3]);
					noOfStarted += Integer.parseInt(dto.getStartedCount());
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

 
	@Override
	public List<SubmittalDto> getTurnAroundTimeReport(Date fromDate, Date endDate, String dmName) {
		List<SubmittalDto> dtos = new ArrayList<SubmittalDto>();
		
		
		StringBuffer sql = new StringBuffer("SELECT ((CASE WHEN uas.first_name ISNULL Then '' ELSE uas.first_name END) || ' ' || "
				+ "(CASE WHEN uas.last_name ISNULL Then '' ELSE uas.last_name END)) AS FullName,"
				+ " result.customer AS client, result.title AS title, result.created_by AS dmName, "
				+ "result.no_of_resumes_required AS no_res_req, result.job_createdOn AS job_createdOn, result.sub_count AS sub_count, "
				+ "result.order_id AS order_id, (CASE WHEN result.minutes<0 THEN 1 ELSE result.minutes END ) AS hours from (SELECT "
				+ "rest.customer, rest.title, (CASE WHEN us.user_role = 'DM' THEN us.user_id ELSE "
				+ "CASE WHEN us.assigned_bdm ISNULL THEN  us.user_id ELSE  assigned_bdm END END) AS created_by, rest.no_of_resumes_required, "
				+ "rest.job_createdOn, count(rest.sub_id) AS sub_count, rest.order_id, sum(rest.minutes) AS minutes from (select res.customer,"
				+ "res.title, res.created_by, res.no_of_resumes_required, res.job_createdOn, res.sub_id, res.order_id, (EXTRACT(EPOCH FROM "
				+ "(CASE WHEN  res.rn=res.no_of_resumes_required THEN res.sub_createdOn END) - res.job_createdOn) / 3600)AS minutes from (select "
				+ "row_number() OVER (PARTITION BY j.order_id ORDER BY s.created_on) AS rn, j.customer,j.title, "
				+ "j.created_by,j.created_on AS job_createdOn,s.created_on AS sub_createdOn,"
				+ "j.order_id AS order_id,s.submittal_id AS sub_id, "
				+ "(CASE WHEN j.no_of_resumes_required ISNULL THEN 1 ELSE j.no_of_resumes_required END) AS no_of_resumes_required"
				+ "    from job_order j left OUTER JOIN submittal s on j.order_id= s.order_id  and s.delete_flag=0 where j.delete_flg=0 "
				+ " and j.created_on > '"+fromDate +"' and j.created_on < '"+endDate+"'"
				+ " and s.created_by in  (select u.user_id from user_acct u where u.user_id='"+dmName+"' or u.assigned_bdm='"+dmName+"' "
				+ "  or u.user_id in  (select ua.user_id from user_acct ua where ua.assigned_bdm='"+dmName+"'))  ORDER by s.created_on ASC )"
				+ "  AS res) AS rest,user_acct us  where us.user_id = rest.created_by  group by rest.customer,  us.user_id, rest.title, "
				+ " rest.created_by, rest.no_of_resumes_required, rest.job_createdOn, rest.order_id) AS result, user_acct uas "
				+ "where result.no_of_resumes_required<=result.sub_count and result.created_by = uas.user_id"
				+ " ORDER by hours asc");
		
		 List<?> result = submittalDao.findBySQLQuery(sql.toString(), null);
		 if(result != null){
				Iterator<?> itr = result.iterator();
				while(itr.hasNext()){
					Object[] obj = (Object[]) itr.next();
					SubmittalDto dto = new SubmittalDto();
					Integer orderId= (Integer) obj[7];
					Date createdOn = (Date) obj[5];
					Double hours = Utils.getTwoDecimalDoubleFromObj(obj[8]);
					dto.setDmName(Utils.getStringValueOfObj(obj[0]));
					dto.setJobClient(Utils.getStringValueOfObj(obj[1]));
					dto.setJobTitle(Utils.getStringValueOfObj(obj[2]));
					dto.setNoOfResumesRequired(Utils.getIntegerValueOfBigDecimalObj(obj[4]).toString());
					dto.setCreatedOn(Utils.convertDateToString_HH_MM(createdOn));
					dto.setSubmittedCount(Utils.getIntegerValueOfBigDecimalObj(obj[6]).toString());
					dto.setOrderId(orderId.toString());
					if(hours < 1){
						dto.setTurnAroundTime(String.valueOf(1));
					}else{
						dto.setTurnAroundTime(String.valueOf(Math.round(Utils.getTwoDecimalDoubleFromObj(obj[8]))));
					}
					dtos.add(dto);
				}
		 }
		return dtos;
	}

 
	@Override
	public Map<String, Object> getResumeAuditLogData(Date srtDate, Date endDate, String candidateId, String viewedBy) {
		StringBuffer sql = new StringBuffer();
		List<SubmittalDto> dtos = new ArrayList<SubmittalDto>();
		Map<String, Object> objMap = new HashMap<String, Object>();
		try {
			if(srtDate!=null && endDate!=null){
				sql.append("select r.candidate,r.created_by,r.created_on,r.document_status,r.status from resume_audit_history r "
						+ "where r.created_on>='"+srtDate+"' and r.created_on<='"+endDate+"'and r.status!='UPDATED' ");
			}
			if(candidateId!=null && candidateId.trim().length()>0){
				sql.append(" and r.candidate='"+candidateId+"'");
			}
			if(viewedBy!=null && viewedBy.trim().length()>0){
				sql.append(" and r.created_by='"+viewedBy+"'");
			}
			sql.append(" order by r.created_on DESC");
			List<?> result = submittalDao.findBySQLQuery(sql.toString(), null);
			if(result != null){
				Iterator<?> itr = result.iterator();
				while(itr.hasNext()){
					Object[] obj = (Object[]) itr.next();
					SubmittalDto dto = new SubmittalDto();
					dto.setCandidateId(Integer.parseInt((obj[0]).toString()));
					dto.setCreatedBy((obj[1])!=null? (obj[1]).toString() : "");
					dto.setCreatedOn(obj[2] != null ? Utils.convertDateToString_HH_MM_A((Date)obj[2]) : null);
					//dto.setCreatedOn((obj[2])!=null ? Utils.get(obj[2]).toString() : "");
					dto.setDocument_status((obj[3])!=null ? (obj[3]).toString() : "");
					dto.setStatus((obj[4])!=null ? (obj[4]).toString() : "");
					dtos.add(dto);
				}
			}	
			objMap.put("resumeAuditLogData", dtos);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		return objMap;
	}

	@Override
	public Object getLocationReportData(Date startDate, Date endDate, String location) {
		StringBuffer sql = new StringBuffer();
		
		Map<String, Object> jobOrdersMap = new HashMap<String , Object>();
		Map<String, Object> submittalsMap = new HashMap<String , Object>();
		submittalsMap = getRecruitersSubmittals(startDate, endDate, location);
		jobOrdersMap = getRecruitersJobOrders(startDate, endDate, location);
		
		 
			sql.append("select ((CASE WHEN result.first_name ISNULL THEN ' ' ELSE result.first_name END) || ' ' ||"
					+ " (CASE WHEN result.last_name ISNULL THEN ' ' ELSE result.last_name END)) AS fullName, "
					+ "result.user_id, "
					+ "result.office_location,"
					+ " result.user_role,"
					+ " result.join_date, "
					+ " ((CASE WHEN ua.first_name ISNULL THEN ' ' ELSE ua.first_name END) || ' ' || "
					+ " (CASE WHEN ua.last_name ISNULL THEN ' ' ELSE ua.last_name END)) AS dmName,"
					+ " result.min_start_count, "
					+ " result.max_start_count,"
					+ " result.SERVEDDATE,"
					+ " result.status ,"
					+ " result.name AS level"
					+ " from (select u.first_name,u.last_name, u.user_id, u.office_location, u.user_role, u.created_on, u.assigned_bdm , "
					+ "u.join_date, ( CASE WHEN u.relieving_date ISNULL THEN ( CASE WHEN u.status = 'INACTIVE' THEN"
					+ "   u.updated_on ELSE CURRENT_DATE END) ELSE u.relieving_date END) AS SERVEDDATE,"
					+ "  u.status, "
					+ " d.min_start_count,"
					+ " d.max_start_count,"
					+ " d.name "
					+ " from user_acct u, designation d where  u.user_role IN ('IN_Recruiter', 'Recruiter')");
					if(location !=null){
						sql.append("and u.office_location in( " + location + " ) ");
					}
				sql.append(" AND u.level = d.id )As result, user_acct ua where ua.user_id = result.assigned_bdm ");
		 
		List<?> result = submittalDao.findBySQLQuery(sql.toString(), null);
		Map<String, Object> recruitersDataMap = new HashMap<String , Object>();
		Map<String, Object> activeInActiveRecCount = new HashMap<String , Object>();
		Map<String, Integer> activeUserRolesCount = new HashMap<String, Integer>(); 
		Integer activeCount = 0, inActiveCount = 0;
		
		Integer activeStarts=0,inActiveStarts=0;
		
		if(result != null){
			Map<String, SubmittalDto> inActiveStartedubmittalsMap = getInActiveStartedOfRecruiter(startDate, endDate, null, null,null,location);
			List<SubmittalDto> subDtos =new ArrayList<SubmittalDto>();
			List<String> dms = new ArrayList<String>();
			Iterator<?> itr = result.iterator();
			Date endServed = null;
			while(itr.hasNext()){
				long userServeddays = 0;
				SubmittalDto subDto = new SubmittalDto();
				SubmittalDto subInnerDto = new SubmittalDto();
				Object[] obj = (Object[]) itr.next();
				
				if(Utils.getStringValueOfObj(obj[9]) != null){
				if( Utils.getStringValueOfObj(obj[9]).equals(Constants.ACTIVE)){
					activeCount += 1;
				subDto.setRecName(Utils.getStringValueOfObj(obj[0]));
				subDto.setUserId(Utils.getStringValueOfObj(obj[1]));
				subDto.setLocation(Utils.getStringValueOfObj(obj[2]));
				subDto.setUserRole(Utils.getStringValueOfObj(obj[3]));
				subDto.setJoinDate((Date)obj[4]);
				subDto.setDmName(Utils.getStringValueOfObj(obj[5]));
				subDto.setMaxStartCount(Utils.getIntegerValueOfObj(obj[7]));
				subDto.setMinStartCount(Utils.getIntegerValueOfObj(obj[6]));
				subDto.setServedDate((Date)obj[8]);
				subDto.setStatus(Utils.getStringValueOfObj(obj[9]));
				subDto.setLevel(Utils.getStringValueOfObj(obj[10]));
				subDto.setInActiveStartedCount("0");
				if(jobOrdersMap.containsKey(subDto.getUserId())){
					subInnerDto = (SubmittalDto) jobOrdersMap.get(subDto.getUserId());
					subDto.setNoOfJobOrders(subInnerDto.getNoOfJobOrders());
					subDto.setNoOfPositions(subInnerDto.getNoOfPositions());
				}
				
				if(submittalsMap.containsKey(subDto.getUserId())){
					subInnerDto = (SubmittalDto) submittalsMap.get(subDto.getUserId());
					subDto.setSubmittedCount(subInnerDto.getSubmittedCount());
					subDto.setConfirmedCount(subInnerDto.getConfirmedCount());
					subDto.setStartedCount(subInnerDto.getStartedCount());
					subDto.setActiveStartedCount(subInnerDto.getStartedCount());
					activeStarts+=Integer.parseInt(subDto.getActiveStartedCount());
					if(inActiveStartedubmittalsMap!=null){
						if(inActiveStartedubmittalsMap.get(subDto.getUserId())!=null){
						subDto.setInActiveStartedCount(inActiveStartedubmittalsMap.get(subDto.getUserId()).getInActiveStartedCount());
						subDto.setStartedCount(String.valueOf(Integer.parseInt(subDto.getStartedCount())+Integer.parseInt(subDto.getInActiveStartedCount())));
						inActiveStarts+=Integer.parseInt(subDto.getInActiveStartedCount());
						}
						
					}
				}
				if(subDto.getDmName() != null){
				if(!dms.contains(subDto.getDmName())){
					dms.add(subDto.getDmName());
				}}
				subDto.setNoOfJobOrders(subDto.getNoOfJobOrders() != null ? subDto.getNoOfJobOrders() : String.valueOf(0));
				subDto.setNoOfPositions(subDto.getNoOfPositions() != null ?subDto.getNoOfPositions() : String.valueOf(0));
				subDto.setSubmittedCount(subDto.getSubmittedCount() != null ? subDto.getSubmittedCount() : String.valueOf(0));
				subDto.setConfirmedCount(subDto.getConfirmedCount() != null ? subDto.getConfirmedCount() : String.valueOf(0));
				subDto.setStartedCount(subDto.getStartedCount() != null ? subDto.getStartedCount() : String.valueOf(0));
				
				
				/*To get served days of recruiter*/
				
				if(subDto.getJoinDate() != null || subDto.getServedDate() != null){
					
				if (subDto.getJoinDate().compareTo(startDate) > 0) {

					if (subDto.getServedDate().compareTo(endDate) < 0) {
						endServed = subDto.getServedDate();
					} else {
						endServed = endDate;
					}
					userServeddays = (endServed.getTime() - subDto.getJoinDate().getTime()) / 86400000;
				} else {
					if (subDto.getServedDate().compareTo(endDate) < 0) {
						endServed = subDto.getServedDate();
					} else {
						endServed = endDate;
					}
					userServeddays = (endServed.getTime() - startDate.getTime()) / 86400000;

				}
				
				}
				
				/*To get avg Hires from served days*/
				
				double avgHires = 0d;
				if(subDto.getStartedCount() != null ){
					avgHires = Double.valueOf(subDto.getStartedCount());
					if (userServeddays != 0) {
						avgHires = avgHires / (userServeddays / 30d);
					}
				}
				subDto.setAvgHires(String.valueOf(Utils.getTwoDecimalDoubleFromObj(avgHires)));
				
				
				String color = "",performanceStatus="";
				if (Double.valueOf(subDto.getAvgHires()) <= ((Double.valueOf(subDto.getMinStartCount()) / 12f) * 0.75f)) {
					color = Constants.RED;
					performanceStatus = Constants.POOR;
				} else if (Double.valueOf(subDto.getAvgHires()) > ((Double.valueOf(subDto.getMinStartCount()) / 12f) * 0.75f)
						&& Double.valueOf(subDto.getAvgHires()) <= (((Double.valueOf(subDto.getMinStartCount()) / 12f)))) {
					color = Constants.YELLOW;
					performanceStatus = Constants.AVERAGE;
				} else if (Double.valueOf(subDto.getAvgHires()) > ((Double.valueOf(subDto.getMinStartCount()) / 12f))
						&& Double.valueOf(subDto.getAvgHires()) <= ((Double.valueOf(subDto.getMinStartCount()) / 12f) * 1.50f)) {
					color = Constants.LIGHT_GREEN;
					performanceStatus = Constants.GOOD;
				} else if (Double.valueOf(subDto.getAvgHires()) > ((Double.valueOf(subDto.getMinStartCount()) / 12f) * 1.50f)) {
					color = Constants.GREEN;
					performanceStatus = Constants.EXCELLENT;
				}
				
				subDto.setColor(color);
				subDto.setPerformanceStatus(performanceStatus);
				Integer i=1;
				if(activeUserRolesCount.containsKey(subDto.getLevel())){
					Integer mapCount = activeUserRolesCount.get(subDto.getLevel());
					activeUserRolesCount.put(subDto.getLevel(), mapCount+1);
				}else{
					activeUserRolesCount.put(subDto.getLevel(), i);
				}
				
				subDtos.add(subDto);
				}else{
					inActiveCount +=1;
				}
				}
			}
			
		
	Collections.sort(subDtos, new Comparator<SubmittalDto>() {
		@Override
		public int compare(SubmittalDto o1, SubmittalDto o2) {
			return Double.valueOf(o2.getAvgHires()).compareTo(Double.valueOf(o1.getAvgHires()));
		}
	});
	List<SubmittalDto> rankedGridDto = new ArrayList<SubmittalDto>();
			int i=1;
	for(SubmittalDto dto : subDtos){
		dto.setRank(String.valueOf(i++));
		rankedGridDto.add(dto);
	}
	
		activeInActiveRecCount.put(Constants.ACTIVE,activeCount);
		activeInActiveRecCount.put(Constants.INACTIVE,inActiveCount);
		
			
			recruitersDataMap.put("gridData", rankedGridDto);
			recruitersDataMap.put(Constants.ACTIVE_REC_LEVEL_LIST, activeInActiveRecCount);
			recruitersDataMap.put(Constants.NO_OF_DMS, dms !=null ? dms.size() : 0);
			recruitersDataMap.put(Constants.NO_OF_REC, subDtos !=null ? subDtos.size() : 0);
			recruitersDataMap.put(Constants.NO_OF_JOB_ORDERS, jobOrdersMap.get(Constants.NO_OF_JOB_ORDERS));
			recruitersDataMap.put(Constants.NO_OF_POSITIONS, jobOrdersMap.get(Constants.NO_OF_POSITIONS));
			recruitersDataMap.put(Constants.NO_OF_SUBMITTALS, submittalsMap.get(Constants.TOTAL_SUBMITTALS));
			recruitersDataMap.put(Constants.NO_OF_CONFIRMED, submittalsMap.get(Constants.NO_OF_CONFIRMED));
//			recruitersDataMap.put(Constants.NO_OF_STARTS, submittalsMap.get(Constants.NO_OF_STARTS));
			recruitersDataMap.put(Constants.NO_OF_STARTS, activeStarts+inActiveStarts);
			recruitersDataMap.put(Constants.ACTIVE_USER_ROLES_MAP, activeUserRolesCount);
			
			List<Map<String, Object>> subOpenClosed = getLocationSubOpenClosedCount( startDate,  endDate,  location);
			
			recruitersDataMap.put(Constants.SUBMITTAL_SERVICE, subOpenClosed.get(2));
			recruitersDataMap.put(Constants.SUB_CONFIRMED_SERVICE, subOpenClosed.get(1));
			recruitersDataMap.put(Constants.SUB_STARTED_SERVICE, subOpenClosed.get(0));
			
			recruitersDataMap.put(Constants.SUB_STARTED_ACTIVE_COUNT, activeStarts);
			recruitersDataMap.put(Constants.SUB_STARTED_INACTIVE_COUNT, inActiveStarts);
			
			List<Map<String, Object>> jobOpenClosed = getLocationJobOpenClosedCount( startDate,  endDate,  location);
			
			recruitersDataMap.put(Constants.JOB_ORDER_SERVICE, jobOpenClosed.get(0));
			recruitersDataMap.put(Constants.JOB_ORDER_POSITIONS_SERVICE, jobOpenClosed.get(1));
			
			
	/*		List<String> categories = new ArrayList<String>();
			List<Double> series = new ArrayList<Double>();
			List<Map<String, Object>> seriesData = new ArrayList<Map<String, Object>>();
			
			//This is to display the vertical bar chart
			for (SubmittalDto dto : subDtos) {
				 
				series.add(Double.valueOf(dto.getAvgHires()));
				categories.add(dto.getRecName());

				Map<String, Object> map = new LinkedHashMap<String, Object>();

				String color = "";
				if (Double.valueOf(dto.getAvgHires()) <= ((Double.valueOf(dto.getMinStartCount()) / 12f) * 0.75f)) {
					color = Constants.RED;
				} else if (Double.valueOf(dto.getAvgHires()) > ((Double.valueOf(dto.getMinStartCount()) / 12f) * 0.75f)
						&& Double.valueOf(dto.getAvgHires()) <= ((Double.valueOf(dto.getMinStartCount()) / 12f))) {
					color = Constants.YELLOW;
				} else if (Double.valueOf(dto.getAvgHires()) > ((Double.valueOf(dto.getMinStartCount()) / 12f))
						&& Double.valueOf(dto.getAvgHires()) <= ((Double.valueOf(dto.getMinStartCount()) / 12f) * 1.50f)) {
					color = Constants.LIGHT_GREEN;
				} else if (Double.valueOf(dto.getAvgHires()) > ((Double.valueOf(dto.getMinStartCount()) / 12f) * 1.50f)) {
					color = Constants.GREEN;
				}

				map.put(Constants.Y, Double.valueOf(dto.getAvgHires()));
				map.put(Constants.COLOR, color);
				map.put(Constants.USERID, dto.getUserId());
				map.put(Constants.NO_OF_REC, dto.getNoOfRecs());
				seriesData.add(map);
				 
			}
			
			recruitersDataMap.put(Constants.SERIES_DATA, seriesData);
			recruitersDataMap.put(Constants.CATEGORIES, categories);
			recruitersDataMap.put(Constants.SERIES, series);*/
		}
		
		
		
		return recruitersDataMap;
	}


	private List<Map<String, Object>> getLocationJobOpenClosedCount(Date startDate, Date endDate, String location) {
		
		StringBuffer sql = new StringBuffer();
		
		if(startDate != null && endDate != null && location != null){
			sql.append("select "
					+ "count(CASE WHEN result.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN 1 END) AS open_c,"
					+ " count(CASE WHEN result.status in ('CLOSED', 'FILLED') THEN 1 END) AS closed_c, "
					+ " count(CASE WHEN result.status in ('OPEN', 'ASSIGNED', 'REOPEN') AND result.open_days <= 30 THEN 1 END) AS open_l_30, "
					+ " count(CASE WHEN result.status in ('OPEN', 'ASSIGNED', 'REOPEN') AND result.open_days >= 30 "
					+ " AND result.open_days <= 90 THEN 1 END) AS open_B, "
					+ " "
					+ " count(CASE WHEN result.status in ('OPEN', 'ASSIGNED', 'REOPEN') AND result.open_days >= 90 THEN 1 END) AS open_g_90, "
					+ ""
					+ "sum(CASE WHEN result.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN result.num_pos ELSE 0 END )AS open_num_pos, "
					+ ""
					+ "sum(CASE WHEN result.status in ('CLOSED', 'FILLED') THEN result.num_pos ELSE 0 END )AS cls_num_pos,"
					+ " "
					+ "sum(CASE WHEN result.status in ('OPEN', 'ASSIGNED', 'REOPEN') AND result.open_days <= 30 THEN result.num_pos ELSE 0 END) AS pos_l_30, "
					+ ""
					+ "sum(CASE WHEN result.status in ('OPEN', 'ASSIGNED', 'REOPEN') AND result.open_days >= 30 "
					+ "AND result.open_days <= 90 THEN result.num_pos ELSE 0 END) AS pos_B, "
					+ ""
					+ "sum(CASE WHEN result.status in ('OPEN', 'ASSIGNED', 'REOPEN') AND result.open_days >= 90 THEN result.num_pos ELSE 0 END) AS pos_g_90 "
					+ ""
					+ "from"
					+ " (select j.order_id, j.status,j.num_pos, ( CASE WHEN j.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN round "
					+ "((EXTRACT(EPOCH FROM date (' " + endDate + " ') - COALESCE(j.updated_on, j.created_on)) / 3600) / 24)END) AS open_days "
					+ ""
					+ "from job_order j, user_acct u where  j.created_on >="
					+ " ' " + startDate + " ' AND  j.created_on <="
					
					+ "  ' " + endDate + " ' and j.delete_flg = 0 AND  COALESCE (j.assigned_to , j.dmname, j.created_by) = u.user_id"
					+ "  and  u.user_id in (select ua.user_id from user_acct ua where "
					+ ""
					+ "   ua.office_location in( " + location + " )) GROUP BY u.user_id, j.order_id)  AS result");
		}
		
		
		List<?> result = submittalDao.findBySQLQuery(sql.toString(), null);
		List<Map<String, Object>> jobOpenClsList = new ArrayList<Map<String, Object>>();
		if(result != null ){
			Iterator<?> itr = result.iterator();
			while(itr.hasNext()){
				
				Object[] obj = (Object[]) itr.next();
				
				
				Map<String, Object> jobOpenClosed = new HashMap<String, Object>();
				
				jobOpenClosed.put(Constants.OPEN, obj[0] != null ? obj[0] : 0 );
				jobOpenClosed.put(Constants.CLOSED, obj[1] != null ? obj[1] : 0 );
				jobOpenClosed.put(Constants.LESS_THAN_30_DAYS, obj[2] != null ? obj[2] : 0);
				jobOpenClosed.put(Constants.LESS_THAN_90_DAYS, obj[3] != null ? obj[3] : 0);
				jobOpenClosed.put(Constants.MORE_THAN_90_DAYS, obj[4] != null ? obj[4] : 0);
				
				jobOpenClsList.add(jobOpenClosed);
				
				
				Map<String, Object> jobPosOpenClosed = new HashMap<String, Object>();
				
				jobPosOpenClosed.put(Constants.OPEN, obj[5] != null ? obj[5] : 0 );
				jobPosOpenClosed.put(Constants.CLOSED, obj[6] != null ? obj[6] : 0 );
				jobPosOpenClosed.put(Constants.LESS_THAN_30_DAYS, obj[7] != null ? obj[7] : 0);
				jobPosOpenClosed.put(Constants.LESS_THAN_90_DAYS, obj[8] != null ? obj[8] : 0);
				jobPosOpenClosed.put(Constants.MORE_THAN_90_DAYS, obj[9] != null ? obj[9] : 0);
				
				jobOpenClsList.add(jobPosOpenClosed);
				
				
				
				 
			}
		}
		
		return jobOpenClsList;
	}

	private List<Map<String, Object>> getLocationSubOpenClosedCount(Date startDate, Date endDate, String location) {
		StringBuffer sql = new StringBuffer();
		
		if(startDate != null && endDate != null && location != null){
		sql.append("select count(result.sub_status) AS sub_count, "
				+ "count(CASE WHEN result.sub_status in ('STARTED') AND result.job_status in"
				+ " ('OPEN', 'REOPEN', 'ASSIGNED') AND result.open_days <=30 THEN 1 END ) AS sta_l_30,"
				+ " "
				+ " count(CASE WHEN result.sub_status in ('STARTED') AND result.job_status in"
				+ " ('OPEN', 'REOPEN', 'ASSIGNED') AND result.open_days >=30 AND result.open_days <= 90 THEN 1 END ) AS start_B,"
				+ " "
				+ " count(CASE WHEN result.sub_status in ('STARTED') AND result.job_status in"
				+ " ('OPEN', 'REOPEN', 'ASSIGNED') AND result.open_days >= 90 THEN 1 END ) AS sta_g_90,"
				+ " "
				+ " count(CASE WHEN result.sub_status in ('CONFIRMED') AND result.job_status in "
				+ "('OPEN', 'REOPEN', 'ASSIGNED') AND result.open_days <=30 THEN 1 END ) AS conf_l_30,"
				+ " "
				+ " count(CASE WHEN result.sub_status in ('CONFIRMED') AND result.job_status in"
				+ " ('OPEN', 'REOPEN', 'ASSIGNED') AND result.open_days >=30 AND result.open_days <= 90 THEN 1 END ) AS conf_B,"
				+ " "
				+ " count(CASE WHEN result.sub_status in ('CONFIRMED') AND result.job_status in"
				+ " ('OPEN', 'REOPEN', 'ASSIGNED') AND result.open_days >= 90 THEN 1 END ) AS conf_g_90,"
				+ " "
				+ " count(CASE WHEN result.job_status in ('OPEN', 'REOPEN', 'ASSIGNED')"
				+ " AND result.open_days <=30 THEN 1 END ) AS sub_l_30,"
				+ " "
				+ " count(CASE WHEN  result.job_status in ('OPEN', 'REOPEN', 'ASSIGNED')"
				+ " AND result.open_days >=30 AND result.open_days <= 90 THEN 1 END ) AS sub_B,"
				+ " "
				+ " count(CASE WHEN result.job_status in ('OPEN', 'REOPEN', 'ASSIGNED')"
				+ "  AND result.open_days >= 90 THEN 1 END ) AS sub_g_90,"
				+ ""
				+ "count(CASE WHEN result.sub_status in ('CONFIRMED') AND result.job_status in"
				+ "('CLOSED', 'FILLED') THEN 1 END ) AS closed_confirmed_count, "
				+ " "
				+ "count(CASE WHEN result.sub_status in ('STARTED') AND result.job_status in"
				+ " ('CLOSED', 'FILLED') THEN 1 END ) AS closed_started_count,"
				+ " "
				+ " count(CASE WHEN result.sub_status in ('CONFIRMED') AND result.job_status in "
				+ "('OPEN', 'REOPEN', 'ASSIGNED') THEN 1 END ) AS sub_confirmed_count, "
				+ " "
				+ "count(CASE WHEN result.sub_status in ('STARTED') AND result.job_status in "
				+ "('OPEN', 'REOPEN', 'ASSIGNED') THEN 1 END ) AS sub_started_count,"
				+ ""
				+ " count(CASE WHEN result.job_status in('OPEN', 'REOPEN', 'ASSIGNED') THEN 1 END) AS job_open_count ,"
				+ " "
				+ " count(CASE WHEN result.job_status in('CLOSED','FILLED') THEN 1 END) AS job_closed_count from "
				+ "(SELECT s.order_id AS sub_id, j.order_id AS job_id, j.status AS job_status, s.status AS sub_status, "
				+ "( CASE WHEN j.status in ('OPEN', 'ASSIGNED', 'REOPEN') THEN round "
				+ "((EXTRACT(EPOCH FROM date (' " + endDate + " ') - COALESCE(j.updated_on, j.created_on)) / 3600) / 24)END) AS open_days "
						+ " from submittal s , user_acct u, job_order j where"
						+ " s.created_by = u.user_id and j.order_id = s.order_id and s.delete_flag = 0"
						+ " and COALESCE(s.updated_on, s.created_on) >= ' " + startDate + " ' AND "
								+ "COALESCE(s.updated_on, s.created_on) <=  ' " + endDate + " ' and "
								+ " u.user_id in(select ua.user_id from user_acct ua where ua.user_role IN ('IN_Recruiter', 'Recruiter')"
								+ "  AND ua.status = 'ACTIVE' and ua.office_location in( " + location + " )))AS result");	
		}
		
		List<?> result = submittalDao.findBySQLQuery(sql.toString(), null);
		List<Map<String, Object>> subOpenClsList = new ArrayList<Map<String, Object>>();
		if(result != null ){
			Iterator<?> itr = result.iterator();
			while(itr.hasNext()){
				
				Object[] obj = (Object[]) itr.next();
				
				
				Map<String, Object> subStartedOpenClosed = new HashMap<String, Object>();
				subStartedOpenClosed.put(Constants.OPEN, obj[13] != null ? obj[13] : 0 );
				subStartedOpenClosed.put(Constants.CLOSED, obj[11] != null ? obj[11] : 0 );
				subStartedOpenClosed.put(Constants.LESS_THAN_30_DAYS, obj[1] != null ? obj[1] : 0);
				subStartedOpenClosed.put(Constants.LESS_THAN_90_DAYS, obj[2] != null ? obj[2] : 0);
				subStartedOpenClosed.put(Constants.MORE_THAN_90_DAYS, obj[3] != null ? obj[3] : 0);
				subOpenClsList.add(subStartedOpenClosed);
				
				
				Map<String, Object> subConfirmedOpenClosed = new HashMap<String, Object>();
				subConfirmedOpenClosed.put(Constants.OPEN, obj[12] != null ? obj[12] : 0 );
				subConfirmedOpenClosed.put(Constants.CLOSED, obj[10] != null ? obj[10] : 0 );
				subConfirmedOpenClosed.put(Constants.LESS_THAN_30_DAYS, obj[4] != null ? obj[4] : 0);
				subConfirmedOpenClosed.put(Constants.LESS_THAN_90_DAYS, obj[5] != null ? obj[5] : 0);
				subConfirmedOpenClosed.put(Constants.MORE_THAN_90_DAYS, obj[6] != null ? obj[6] : 0);
				subOpenClsList.add(subConfirmedOpenClosed);
				
				
				
				Map<String, Object> submittalsOpenClosed = new HashMap<String, Object>();
				submittalsOpenClosed.put(Constants.OPEN, obj[14] != null ? obj[14] : 0 );
				submittalsOpenClosed.put(Constants.CLOSED, obj[15] != null ? obj[15] : 0 );
				submittalsOpenClosed.put(Constants.LESS_THAN_30_DAYS, obj[7] != null ? obj[7] : 0);
				submittalsOpenClosed.put(Constants.LESS_THAN_90_DAYS, obj[8] != null ? obj[8] : 0);
				submittalsOpenClosed.put(Constants.MORE_THAN_90_DAYS, obj[9] != null ? obj[9] : 0);
				subOpenClsList.add(submittalsOpenClosed);
			}
		}
		
		
		
		return subOpenClsList;
	}

	private Map<String, Object> getRecruitersJobOrders(Date startDate, Date endDate, String location) {
		StringBuffer sql = new StringBuffer();
		sql.append("select "
				+ "sum(obj.jobOrderCount) AS jobCount, "
				+ "sum(obj.num_pos_count) AS posCount, "
				+ "obj.user_id AS userId "
				+ "from ( ");
		sql.append("  select j.order_id,"
				+ " count(j.order_id) AS jobOrderCount,"
				+ " sum (j.num_pos) As num_pos_count,"
				+ " u.user_id from job_order j, user_acct u where ");
		
		if(startDate != null && endDate != null){
			sql.append(" j.created_on >= ' " + startDate + " '"
					+ "  AND j.created_on <= ' " + endDate + " ' and ");
		}
		sql.append(" j.delete_flg = 0 AND COALESCE (j.assigned_to , j.dmname, j.created_by) = u.user_id and j.status != 'PENDING' AND ");
		
		sql.append(" u.user_id in (select ua.user_id from user_acct ua where ");
		if(location != null){
			sql.append( "  ua.office_location in( " +location+ " ))");
		}else{
			sql.append(")");
		}
		sql.append("GROUP BY u.user_id, j.order_id )AS obj group by obj.user_id");
		
		List<?> result  = submittalDao.findBySQLQuery(sql.toString(), null);
		
		Integer jobOrdersCount = 0, positionsCount = 0;
		
		Map<String, Object> jobOrdersMap = new HashMap<String, Object>();
		if(result != null){
			Iterator<?> itr = result.iterator();
			while(itr.hasNext()){
				SubmittalDto subDto = new SubmittalDto();
				Object[] obj = (Object[]) itr.next();
//				subDto.setJobOrderId(Utils.getStringValueOfObj(obj[0]));
				subDto.setNoOfJobOrders(Utils.getIntegerValueOfBigDecimalObj(obj[0]).toString());
				subDto.setNoOfPositions(Utils.getIntegerValueOfBigDecimalObj(obj[1]).toString());
				subDto.setUserId(Utils.getStringValueOfObj(obj[2]));
				
				jobOrdersCount += Integer.parseInt(subDto.getNoOfJobOrders());
				positionsCount += Integer.parseInt(subDto.getNoOfPositions());
				
				jobOrdersMap.put(subDto.getUserId(), subDto);
			}
		}
		
		jobOrdersMap.put(Constants.NO_OF_JOB_ORDERS, jobOrdersCount);
		jobOrdersMap.put(Constants.NO_OF_POSITIONS, positionsCount);
		
		return jobOrdersMap;
	}

	private Map<String, Object> getRecruitersSubmittals(Date startDate, Date endDate, String location) {
		StringBuffer sql = new StringBuffer();
		
		
			sql.append("select u.user_id, "
					+ "count( u.user_id) AS submitted_count,  "
					+ "count(case when s.status = 'CONFIRMED' then 1 END) As confirmed_count,"
					+ "count(case when s.status = 'STARTED' then 1 END) As started_count "
					+ "from submittal s, user_acct u where ");
					
					if(startDate != null && endDate != null){
						sql.append(" COALESCE(s.updated_on, s.created_on) >= ' "+ startDate + " ' AND "
								+ " COALESCE(s.updated_on, s.created_on) <=' "+ endDate + " ' AND ");
					}
					
					sql.append(" u.user_id in (select ua.user_id from user_acct ua where ua.user_role IN ('IN_Recruiter', 'Recruiter') and ua.status = 'ACTIVE'");
					if(location != null){
						sql.append( " and ua.office_location in( " +location+ " ))");
					}else{
						sql.append(")");
					}
					
					sql.append(" and u.user_id = s.created_by AND s.delete_flag = 0 GROUP BY u.user_id");
					
					List<?> result = submittalDao.findBySQLQuery(sql.toString(), null);
					Integer submittedCount = 0, confirmedCount = 0, startedCount = 0;
					Map<String, Object> subsMap = new HashMap<String, Object>();
					if(result != null){
						Iterator<?> itr = result.iterator();
						while(itr.hasNext()){
							SubmittalDto subDto = new SubmittalDto();
							Object[] obj = (Object[]) itr.next();
							
							subDto.setUserId(Utils.getStringValueOfObj(obj[0]));
							subDto.setSubmittedCount(Utils.getIntegerValueOfBigDecimalObj(obj[1]).toString());
							subDto.setConfirmedCount(Utils.getIntegerValueOfBigDecimalObj(obj[2]).toString());
							subDto.setStartedCount(Utils.getIntegerValueOfBigDecimalObj(obj[3]).toString());
							
							submittedCount += Integer.parseInt(subDto.getSubmittedCount());
							confirmedCount += Integer.parseInt(subDto.getConfirmedCount());
							startedCount  += Integer.parseInt(subDto.getStartedCount());
							
							subsMap.put(subDto.getUserId(), subDto);
						}
					}
			
					subsMap.put(Constants.TOTAL_SUBMITTALS, submittedCount);
					subsMap.put(Constants.NO_OF_CONFIRMED, confirmedCount);
					subsMap.put(Constants.NO_OF_STARTS, startedCount);
		return subsMap;
	}

	@Override
	public Map<String, Integer> getInActiveStartedCounts(Date dateStart, Date dateEnd) {
		Map<String, Integer> inActiveStartedMap = new HashMap<String, Integer>();

		StringBuffer sql = new StringBuffer("select sum(result.started_count) from "
				+ " (select s.submittal_id, count(h.submittal_id) AS started_count, s.created_by, "
				+ " s.status,s.order_id from submittal s, submittal_history h where s.submittal_id = "
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
	
	@Override
	public Boolean checkStartedStatusBy_CandidateId_SubmittalId(Integer candidateId, Integer submittalId) {
		Boolean candidateStarted = false;
		try {
			StringBuilder hql = new StringBuilder(
					"select s.id from Submittal s where s.candidate.id = ?1 AND s.deleteFlag = 0 AND s.id != ?2 AND s.status = 'STARTED'");
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
	
}

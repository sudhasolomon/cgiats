/**
 * 
 */
package com.uralian.cgiats.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.RecRnRReportDao;
import com.uralian.cgiats.model.SubmitalStats;
import com.uralian.cgiats.service.RecRnRReportService;

/**
 * @author Sreenath
 * 
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RecRnRReportServiceImpl implements RecRnRReportService {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private RecRnRReportDao recRnRReportDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.RecRnRReportService#getRecRnRReport(java.util
	 * .Date, java.util.Date, java.lang.String)
	 */
	@Override
	public List<SubmitalStats> getRecRnRReport(Date fromDate, Date toDate, String selectedLocation) {
		List<SubmitalStats> submitalStatsList = null;
		StringBuffer sql = null;
		log.info("From RecRnrReportService");
		try {
			sql = new StringBuffer();
			sql.append("select rws.recut_name,rws.office_location," + "sum(rws.submitted_count) as Submittedcount,sum(rws.dmrej_count) as Dmrejected,"
					+ "sum(rws.accepted_count) as Accepted,sum(rws.inteviewing_count) as Interviewing,"
					+ "sum(rws.confirmed_count) as Confirmed,sum(rws.rejected_count) as Rejected,"
					+ "sum(rws.started_count) as Started,sum(rws.backout_count) as Backout,sum(rws.outofprj_count) as Outofprjt"
					+ " from rnr_rec_wise_submital_stat_view   rws where ");
			Map<String, Object> params = new HashMap<String, Object>();
			if (fromDate != null) {
				sql.append("  COALESCE(date(rws.created_on)) >=date(:startDate)");

				params.put("startDate", fromDate);
			}
			if (toDate != null) {
				sql.append(" and COALESCE(date(rws.created_on)) <=date(:endDate)");

				params.put("endDate", toDate);
			}
			if (selectedLocation != null && selectedLocation.length() > 0)
				sql.append(" and rws.office_location = '" + selectedLocation + "'");
			sql.append(" group by rws.recut_name,rws.office_location ");
			List<?> result = recRnRReportDao.findBySQLQuery(sql.toString(), params);

			SubmitalStats submitals = new SubmitalStats();
			submitalStatsList = new ArrayList<SubmitalStats>();
			for (Object obj : result) {
				submitals = new SubmitalStats();
				Object[] tuple = (Object[]) obj;
				Object recruiterName = tuple[0];
				Object officeLocation = tuple[1];
				Object submittedCount = tuple[2];
				Object dmRejCount = tuple[3];
				Object acceptedCount = tuple[4];
				Object interviewingCount = tuple[5];
				Object confirmedCount = tuple[6];
				Object rejectedCount = tuple[7];
				Object startedCount = tuple[8];
				Object backOutCount = tuple[9];
				Object outOfProjCount = tuple[10];
				submitals.setRecruiterName(String.valueOf(recruiterName));
				submitals.setOfficeLocation(String.valueOf(officeLocation));
				submitals.setSubmittedCount(Integer.valueOf(String.valueOf(submittedCount)));
				submitals.setDmrejCount(Integer.valueOf(String.valueOf(dmRejCount)));
				submitals.setAcceptedCount(Integer.valueOf(String.valueOf(acceptedCount)));
				submitals.setInterviewingCount(Integer.valueOf(String.valueOf(interviewingCount)));
				submitals.setConfirmedCount(Integer.valueOf(String.valueOf(confirmedCount)));
				submitals.setRejectedCount(Integer.valueOf(String.valueOf(rejectedCount)));
				submitals.setStartedCount(Integer.valueOf(String.valueOf(startedCount)));
				submitals.setBackOutCount(Integer.valueOf(String.valueOf(backOutCount)));
				submitals.setOutOfProjCount(Integer.valueOf(String.valueOf(outOfProjCount)));

				submitalStatsList.add(submitals);
			}
		} catch (Exception e) {
			log.error("Error ..............", e.getMessage(), e);
			e.printStackTrace();
		}
		return submitalStatsList;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.RecRnRReportService#getRecRnRReportDetails
	 * (java.util.Date, java.util.Date, java.lang.String, java.lang.String)
	 */
	@Override
	public List<SubmitalStats> getRecRnRReportDetails(Date fromDate, Date toDate, String selectedLocation, String recName) {
		List<SubmitalStats> submitalStatsList = null;
		StringBuffer sql = null;
		log.info("From RecRnrReportService");
		try {
			sql = new StringBuffer();
			sql.append("select rws.recut_name,rws.order_created_dm,rws.office_location,"
					+ "sum(rws.submitted_count) as Submittedcount,sum(rws.dmrej_count) as Dmrejected,"
					+ "sum(rws.accepted_count) as Accepted,sum(rws.inteviewing_count) as Interviewing,"
					+ "sum(rws.confirmed_count) as Confirmed,sum(rws.rejected_count) as Rejected,"
					+ "sum(rws.started_count) as Started,sum(rws.backout_count) as Backout,sum(rws.outofprj_count) as Outofprjt"
					+ " from rnr_rec_wise_submital_stat_view   rws where ");
			Map<String, Object> params = new HashMap<String, Object>();
			if (fromDate != null) {
				sql.append("  COALESCE(date(rws.created_on)) >=date(:startDate)");

				params.put("startDate", fromDate);
			}
			if (toDate != null) {
				sql.append(" and COALESCE(date(rws.created_on)) <=date(:endDate)");

				params.put("endDate", toDate);
			}
			if (selectedLocation != null && selectedLocation.length() > 0) {
				sql.append(" and rws.office_location =:officeLocation");
				params.put("officeLocation", selectedLocation);
			}
			if (recName != null) {
				sql.append(" and rws.recut_name ='" + recName + "'");
				// params.put("recName", recName);
			}
			log.info("sql" + sql);
			sql.append("group by rws.order_created_dm,rws.recut_name,rws.office_location");
			List<?> result = recRnRReportDao.findBySQLQuery(sql.toString(), params);
			log.info("Size" + result.size());
			SubmitalStats submitals = new SubmitalStats();
			submitalStatsList = new ArrayList<SubmitalStats>();
			for (Object obj : result) {
				submitals = new SubmitalStats();
				Object[] tuple = (Object[]) obj;
				Object recruiterName = tuple[0];
				Object jobDmName = tuple[1];
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
				submitals.setRecruiterName(String.valueOf(recruiterName));
				submitals.setOrderCreatedDm(String.valueOf(jobDmName));
				submitals.setOfficeLocation(String.valueOf(officeLocation));
				submitals.setSubmittedCount(Integer.valueOf(String.valueOf(submittedCount)));
				submitals.setDmrejCount(Integer.valueOf(String.valueOf(dmRejCount)));
				submitals.setAcceptedCount(Integer.valueOf(String.valueOf(acceptedCount)));
				submitals.setInterviewingCount(Integer.valueOf(String.valueOf(interviewingCount)));
				submitals.setConfirmedCount(Integer.valueOf(String.valueOf(confirmedCount)));
				submitals.setRejectedCount(Integer.valueOf(String.valueOf(rejectedCount)));
				submitals.setStartedCount(Integer.valueOf(String.valueOf(startedCount)));
				submitals.setBackOutCount(Integer.valueOf(String.valueOf(backOutCount)));
				submitals.setOutOfProjCount(Integer.valueOf(String.valueOf(outOfProjCount)));

				submitalStatsList.add(submitals);
			}
		} catch (Exception e) {
			log.error("Error ..............", e.getMessage(), e);
			e.printStackTrace();
		}
		return submitalStatsList;
	}

	@Override
	public List<SubmitalStats> getDmRnRReport(Date fromDate, Date toDate) {
		List<SubmitalStats> submitalStatsList = null;
		StringBuffer sql = null;
		try {
			sql = new StringBuffer();
			sql.append("select rws.created_by,sum(rws.interview_count) from rnr_dm_wise_inverview_count rws where ");
			Map<String, Object> params = new HashMap<String, Object>();
			if (fromDate != null) {
				sql.append("  COALESCE(date(rws.order_date)) >=date(:startDate)");

				params.put("startDate", fromDate);
			}
			if (toDate != null) {
				sql.append(" and COALESCE(date(rws.order_date)) <=date(:endDate)");

				params.put("endDate", toDate);
			}

			sql.append(" group by rws.created_by ");
			System.out.println("sql-------->" + sql);
			List<?> result = recRnRReportDao.findBySQLQuery(sql.toString(), params);
			System.out.println("Size---->" + result.size());
			SubmitalStats submitals = new SubmitalStats();
			submitalStatsList = new ArrayList<SubmitalStats>();
			for (Object obj : result) {
				submitals = new SubmitalStats();
				Object[] tuple = (Object[]) obj;
				Object dmName = tuple[0];
				Object interviewingCount = tuple[1];
				submitals.setCreatedBy(String.valueOf(dmName));
				submitals.setInterviewingCount(Integer.valueOf(String.valueOf(interviewingCount)));
				submitalStatsList.add(submitals);

			}
			System.out.println("submitalStatsList456---->" + submitalStatsList);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error ..............", e.getMessage(), e);
		}
		return submitalStatsList;
	}
}

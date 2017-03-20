package com.uralian.cgiats.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.uralian.cgiats.model.SubmitalStats;
import com.uralian.cgiats.service.SubmittalService;
import com.uralian.cgiats.util.Utils;

@Controller
@Path("/executiveSubmittals")
public class ExecutiveSubmittalsRestController extends
		SpringBeanAutowiringSupport {

	private static final Logger log = Logger
			.getLogger(ExecutiveSubmittalsRestController.class);
	@Autowired
	private SubmittalService submittalService;
	@Autowired
	private Util checkUserRole;

	@GET
	@Path("/submittalStats")
	@Produces(MediaType.APPLICATION_JSON)
	public String executiveSubmittalStats(@QueryParam("loginUser") String loginUser)
			throws JSONException {
		log.info("from ExecutiveSubmittalsRestController executiveSubmittalStats() Start ....");
		// declaration
		SubmitalStats todaySubmittalStats = null;
		List<SubmitalStats> yearSubmittalStats = null;
		List<SubmitalStats> allSubmittalStats = null;
		JSONArray submittalStatsArray = new JSONArray();
		Map<String, Integer> todaySubmittals = new HashMap<String, Integer>();
		String loginUserName = loginUser;
		UserRoleVo userRoleVo = null;
		if (loginUser != null) {
			userRoleVo = checkUserRole.checkingRoleByUser(loginUserName);
		}
		todaySubmittalStats = submittalService
				.executiveTodaySubmittalStatus(userRoleVo);

		allSubmittalStats = submittalService
				.getAllSubmittalsStatsForExecutives(userRoleVo);

		// preparing json for today Submittal count
		todaySubmittals.put("submitted",
				todaySubmittalStats.getSubmittedCount());
		todaySubmittals.put("dmrej", todaySubmittalStats.getDmrejCount());
		todaySubmittals.put("accepted", todaySubmittalStats.getAcceptedCount());
		todaySubmittals.put("interviewing",
				todaySubmittalStats.getInterviewingCount());
		todaySubmittals.put("confirmed",
				todaySubmittalStats.getConfirmedCount());
		todaySubmittals.put("rejected", todaySubmittalStats.getRejectedCount());
		todaySubmittals.put("started", todaySubmittalStats.getStartedCount());
		todaySubmittals.put("back_out", todaySubmittalStats.getBackOutCount());
		todaySubmittals.put("out_of_proj",
				todaySubmittalStats.getOutOfProjCount());

		// saving today sumbittals into final return array
		submittalStatsArray.put(todaySubmittals);

		// making todaysubmittal to null
		// todaySubmittals = null;

		JSONObject jsonRecord = null;
		JSONArray monthsArray = null;
		JSONObject monthsData = null;
		Map<String, Integer> statusValues = null;
		Collections.sort(allSubmittalStats, new Comparator<SubmitalStats>() {
			@Override
			public int compare(SubmitalStats s1, SubmitalStats s2) {
				// if(s1.getYear() != null && s2.getYear() != null &&
				// s1.getYear().compareTo(s1.getYear()) != 0) {
				// return s1.getYear().compareTo(s2.getYear());
				// }
				if (Integer.valueOf(s1.getYear()) > Integer.valueOf(s2
						.getYear()))
					return -1; // highest value first
				if (s1.getYear().equals(s2.getYear()))
					return 0;
				return 1;
			}
		});
		// preparing Json for year submittal stat counts
		String year = "";
		for (SubmitalStats vo : allSubmittalStats) {
			if (year.equalsIgnoreCase(vo.getYear())) {
				statusValues.put("submitted", vo.getSubmittedCount());
				statusValues.put("dmrej", vo.getDmrejCount());
				statusValues.put("accepted", vo.getAcceptedCount());
				statusValues.put("interviewing", vo.getInterviewingCount());
				statusValues.put("confirmed", vo.getConfirmedCount());
				statusValues.put("rejected", vo.getRejectedCount());
				statusValues.put("started", vo.getStartedCount());
				statusValues.put("back_out", vo.getBackOutCount());
				statusValues.put("out_of_proj", vo.getOutOfProjCount());
				monthsData.put(vo.getMonth(), statusValues);
				// monthsArray.put(monthsData);
			} else {

				if (Utils.isBlank(year)) {
					jsonRecord = new JSONObject();
					year = vo.getYear();
					monthsArray = new JSONArray();
					statusValues = new HashMap<String, Integer>();
					monthsData = new JSONObject();
					statusValues.put("submitted", vo.getSubmittedCount());
					statusValues.put("dmrej", vo.getDmrejCount());
					statusValues.put("accepted", vo.getAcceptedCount());
					statusValues.put("interviewing", vo.getInterviewingCount());
					statusValues.put("confirmed", vo.getConfirmedCount());
					statusValues.put("rejected", vo.getRejectedCount());
					statusValues.put("started", vo.getStartedCount());
					statusValues.put("back_out", vo.getBackOutCount());
					statusValues.put("out_of_proj", vo.getOutOfProjCount());
					monthsData.put(vo.getMonth(), statusValues);
					monthsArray.put(monthsData);
				} else {
					jsonRecord.put("year", year);
					yearSubmittalStats = submittalService
							.getYearWiseSubmittalsForExecutives(year,userRoleVo);
					jsonRecord.put("submitted", yearSubmittalStats.get(0)
							.getSubmittedCount());
					jsonRecord.put("dmrej", yearSubmittalStats.get(0)
							.getDmrejCount());
					jsonRecord.put("accepted", yearSubmittalStats.get(0)
							.getAcceptedCount());
					jsonRecord.put("interviewing", yearSubmittalStats.get(0)
							.getInterviewingCount());
					jsonRecord.put("confirmed", yearSubmittalStats.get(0)
							.getConfirmedCount());
					jsonRecord.put("rejected", yearSubmittalStats.get(0)
							.getRejectedCount());
					jsonRecord.put("started", yearSubmittalStats.get(0)
							.getStartedCount());
					jsonRecord.put("back_out", yearSubmittalStats.get(0)
							.getBackOutCount());
					jsonRecord.put("out_of_proj", yearSubmittalStats.get(0)
							.getOutOfProjCount());

					jsonRecord.put("data", monthsData);
					submittalStatsArray.put(jsonRecord);
					jsonRecord = new JSONObject();
					monthsArray = new JSONArray();
					statusValues = new HashMap<String, Integer>();
					monthsData = new JSONObject();
					year = vo.getYear();
					statusValues.put("submitted", vo.getSubmittedCount());
					statusValues.put("dmrej", vo.getDmrejCount());
					statusValues.put("accepted", vo.getAcceptedCount());
					statusValues.put("interviewing", vo.getInterviewingCount());
					statusValues.put("confirmed", vo.getConfirmedCount());
					statusValues.put("rejected", vo.getRejectedCount());
					statusValues.put("started", vo.getStartedCount());
					statusValues.put("back_out", vo.getBackOutCount());
					statusValues.put("out_of_proj", vo.getOutOfProjCount());
					monthsData.put(vo.getMonth(), statusValues);
					// monthsArray.put(monthsData);
				}
			}
			statusValues = new HashMap<String, Integer>();
		}

		jsonRecord.put("year", year);
		yearSubmittalStats = submittalService
				.getYearWiseSubmittalsForExecutives(year,userRoleVo);
		jsonRecord.put("submitted", yearSubmittalStats.get(0)
				.getSubmittedCount());
		jsonRecord.put("dmrej", yearSubmittalStats.get(0).getDmrejCount());
		jsonRecord
				.put("accepted", yearSubmittalStats.get(0).getAcceptedCount());
		jsonRecord.put("interviewing", yearSubmittalStats.get(0)
				.getInterviewingCount());
		jsonRecord.put("confirmed", yearSubmittalStats.get(0)
				.getConfirmedCount());
		jsonRecord
				.put("rejected", yearSubmittalStats.get(0).getRejectedCount());
		jsonRecord.put("started", yearSubmittalStats.get(0).getStartedCount());
		jsonRecord.put("back_out", yearSubmittalStats.get(0).getBackOutCount());
		jsonRecord.put("out_of_proj", yearSubmittalStats.get(0)
				.getOutOfProjCount());
		jsonRecord.put("data", monthsData);
		submittalStatsArray.put(jsonRecord);
		log.info("from ExecutiveSubmittalsRestController executiveSubmittalStats() End ....");
		return submittalStatsArray.toString();
	}

	@GET
	@Path("/weekelyStats")
	@Produces(MediaType.APPLICATION_JSON)
	public String executiveweekelyStats(
			@QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate,
			@QueryParam("loginUser") String loginUser) throws JSONException,
			ParseException {
		log.info("from ExecutiveSubmittalsRestController executiveweekelyStats() Start ....");
		Map<String, String> weekelyMap = new HashMap<String, String>();
		List<SubmitalStats> weekelyStats = null;
		Date frmDate;
		Date endDate;
		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
		frmDate = format.parse(fromDate);
		endDate = format.parse(toDate);
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();

		String loginUserName = loginUser;
		UserRoleVo userRoleVo = null;
		if (loginUser != null) {
			userRoleVo = checkUserRole.checkingRoleByUser(loginUserName);
		}
		weekelyStats = submittalService.getWeekelyStats(frmDate, endDate,
				userRoleVo);
		JSONObject jsonObject = new JSONObject();
		for (SubmitalStats vo : weekelyStats) {
			weekelyMap = new HashMap<String, String>();
			// weekelyMap.put("submital_date",String.valueOf(vo.getSubmitalDate()));
			// weekelyMap.put("submital_date",String.valueOf((format.parse(vo.getSubmitalDate()))));
			weekelyMap.put("created_by", String.valueOf(vo.getCreatedBy()));
			weekelyMap.put("user_role", vo.getUserRole());
			weekelyMap.put("office_location", vo.getOfficeLocation());
			weekelyMap.put("open_count", String.valueOf(vo.getOpenCount()));
			weekelyMap.put("closed_count", String.valueOf(vo.getClosedCount()));
			weekelyMap.put("submitted", String.valueOf(vo.getSubmittedCount()));
			weekelyMap.put("dmrej", String.valueOf(vo.getDmrejCount()));
			weekelyMap.put("accepted", String.valueOf(vo.getAcceptedCount()));
			weekelyMap.put("interviewing",
					String.valueOf(vo.getInterviewingCount()));
			weekelyMap.put("confirmed", String.valueOf(vo.getConfirmedCount()));
			weekelyMap.put("rejected", String.valueOf(vo.getRejectedCount()));
			weekelyMap.put("started", String.valueOf(vo.getStartedCount()));
			weekelyMap.put("back_out", String.valueOf(vo.getBackOutCount()));
			weekelyMap.put("out_of_proj",
					String.valueOf(vo.getOutOfProjCount()));
			resultList.add(weekelyMap);
		}
		jsonObject.put("data", resultList);
		log.info("from ExecutiveSubmittalsRestController executiveweekelyStats() End ....");
		return jsonObject.toString();

	}

	@GET
	@Path("/recruiterWeekelyStats")
	@Produces(MediaType.APPLICATION_JSON)
	public String recruiterWeekelyStats(
			@QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate, @QueryParam("dm") String dmName)
			throws JSONException, ParseException {
		log.info("from ExecutiveSubmittalsRestController recruiterWeekelyStats() Start ....");
		Map<String, String> recruiterMap = new HashMap<String, String>();
		List<SubmitalStats> weekelyStats = null;
		Date frmDate;
		Date endDate;
		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
		frmDate = format.parse(fromDate);
		endDate = format.parse(toDate);
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		String loginUserName = null;
		Util checkUserRole = new Util();
		UserRoleVo userRoleVo = checkUserRole.checkingRoleByUser(loginUserName);
		weekelyStats = submittalService.getRecruiterWeekelyStats(frmDate,
				endDate, dmName, userRoleVo);
		JSONObject jsonObject = new JSONObject();
		for (SubmitalStats vo : weekelyStats) {
			recruiterMap = new HashMap<String, String>();
			// recruiterMap.put("submital_date",(vo.getSubmitalDate()));
			// recruiterMap.put("submital_date",String.valueOf((format.parse(vo.getSubmitalDate()))));

			recruiterMap.put("recruiter_name",
					String.valueOf(vo.getRecruiterName()));
			recruiterMap.put("order_created_dm",
					String.valueOf(vo.getOrderCreatedDm()));
			recruiterMap.put("assigned_dm", String.valueOf(vo.getAssignedDm()));
			recruiterMap.put("office_location", vo.getOfficeLocation());
			recruiterMap.put("submitted",
					String.valueOf(vo.getSubmittedCount()));
			recruiterMap.put("dmrej", String.valueOf(vo.getDmrejCount()));
			recruiterMap.put("accepted", String.valueOf(vo.getAcceptedCount()));
			recruiterMap.put("interviewing",
					String.valueOf(vo.getInterviewingCount()));
			recruiterMap.put("confirmed",
					String.valueOf(vo.getConfirmedCount()));
			recruiterMap.put("rejected", String.valueOf(vo.getRejectedCount()));
			recruiterMap.put("started", String.valueOf(vo.getStartedCount()));
			recruiterMap.put("back_out", String.valueOf(vo.getBackOutCount()));
			recruiterMap.put("out_of_proj",
					String.valueOf(vo.getOutOfProjCount()));

			resultList.add(recruiterMap);
		}
		jsonObject.put("data", resultList);
		log.info("from ExecutiveSubmittalsRestController recruiterWeekelyStats() End ....");
		return jsonObject.toString();

	}

	@GET
	@Path("/recruiterLocationStats")
	@Produces(MediaType.APPLICATION_JSON)
	public String recruiterLocationStats(
			@QueryParam("fromDate") String fromDate,
			@QueryParam("toDate") String toDate,
			@QueryParam("location") String location,
			@QueryParam("loginUser") String loginUser) throws JSONException,
			ParseException {
		log.info("from ExecutiveSubmittalsRestController recruiterLocationStats() Start ....");
		Map<String, String> recruiterMap = new HashMap<String, String>();
		List<SubmitalStats> locationStats = null;
		Date frmDate;
		Date endDate;
		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
		frmDate = format.parse(fromDate);
		endDate = format.parse(toDate);
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		String loginUserName = loginUser;
		UserRoleVo userRoleVo = null;
		if (loginUser != null) {
			userRoleVo = checkUserRole.checkingRoleByUser(loginUserName);
		}
		locationStats = submittalService.getRecruiterLocationStats(frmDate,
				endDate, location, userRoleVo);
		JSONObject jsonObject = new JSONObject();
		for (SubmitalStats vo : locationStats) {
			recruiterMap = new HashMap<String, String>();
			// recruiterMap.put("submital_date",(vo.getSubmitalDate()));
			// recruiterMap.put("submital_date",String.valueOf((format.parse(vo.getSubmitalDate()))));

			recruiterMap.put("recruiter_name",
					String.valueOf(vo.getRecruiterName()));
			/*
			 * recruiterMap.put("order_created_dm",String.valueOf(vo.
			 * getOrderCreatedDm()));
			 */
			recruiterMap.put("assigned_dm", String.valueOf(vo.getAssignedDm()));
			recruiterMap.put("office_location", vo.getOfficeLocation());
			recruiterMap.put("submitted",
					String.valueOf(vo.getSubmittedCount()));
			recruiterMap.put("dmrej", String.valueOf(vo.getDmrejCount()));
			recruiterMap.put("accepted", String.valueOf(vo.getAcceptedCount()));
			recruiterMap.put("interviewing",
					String.valueOf(vo.getInterviewingCount()));
			recruiterMap.put("confirmed",
					String.valueOf(vo.getConfirmedCount()));
			recruiterMap.put("rejected", String.valueOf(vo.getRejectedCount()));
			recruiterMap.put("started", String.valueOf(vo.getStartedCount()));
			recruiterMap.put("back_out", String.valueOf(vo.getBackOutCount()));
			recruiterMap.put("out_of_proj",
					String.valueOf(vo.getOutOfProjCount()));

			resultList.add(recruiterMap);
		}
		jsonObject.put("data", resultList);
		log.info("from ExecutiveSubmittalsRestController recruiterLocationStats() End ....");
		return jsonObject.toString();

	}

}

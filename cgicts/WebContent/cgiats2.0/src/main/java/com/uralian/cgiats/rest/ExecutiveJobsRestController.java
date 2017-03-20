package com.uralian.cgiats.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.JobViewOrder;
import com.uralian.cgiats.model.SubmitalStats;
import com.uralian.cgiats.service.ExecutiveJobStatService;
import com.uralian.cgiats.service.JobOrderService;
import com.uralian.cgiats.util.Utils;

/**
 * @author Raghavender
 * 
 */
@Controller
@Path("/executiveJobs")
public class ExecutiveJobsRestController extends SpringBeanAutowiringSupport {

	private static final Logger log = Logger
			.getLogger(ExecutiveJobsRestController.class);
	@Autowired
	private ExecutiveJobStatService executiveJobStatService;

	@Autowired
	private JobOrderService jobOrderService;

	@Autowired
	private Util checkUserRole;

	/*
	 * @GET
	 * 
	 * @Path("/jobOrderStats")
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public String executiveJobStats() {
	 * String jsonJobStatus = null; try { jsonJobStatus =
	 * executiveJobStatService.getJobOrdersCount(); } catch (Exception e) {
	 * e.printStackTrace(); jsonJobStatus = "{status:error}";
	 * 
	 * } return jsonJobStatus;
	 * 
	 * }
	 */

	@GET
	@Path("/jobOrderStats")
	@Produces(MediaType.APPLICATION_JSON)
	public String executiveJobStats(@QueryParam("loginUser") String loginUser)
			throws JSONException {
		log.info("from ExecutiveJobsRestController executiveJobStats() Start....");
		JobViewOrder toDayStats = null;
		List<JobViewOrder> allStats = null;
		List<JobViewOrder> yearStats = null;
		UserRoleVo userRoleVo = null;
		if (loginUser != null)
			if (loginUser != null) {
				userRoleVo = checkUserRole.checkingRoleByUser(loginUser);
			}

		Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();
		JSONObject jsonObject = new JSONObject();
		toDayStats = jobOrderService.getTodayJobOrderStatsForExecutives(userRoleVo);
		allStats = (List<JobViewOrder>) jobOrderService
				.getAllJobOrderStatsForExecutives(userRoleVo);
		// yearStats= (List<JobViewOrder>)
		// jobOrderService.getYearWiseStatusForExecutives(year);
		Map<String, String> map = new HashMap<String, String>();
		/*
		 * for (JobViewOrder vo :allStats){ map = new HashMap<String, String>();
		 * if (resultMap.get("year="+vo.getYear()) == null){ if
		 * (map.get(vo.getMonth()) == null){
		 * map.put(vo.getMonth(),"open counts : "+ vo.getOpenCount()
		 * +" | "+"closed counts : "+vo.getClosedCount()); }
		 * resultMap.put("year="+vo.getYear(), map); }else{ if
		 * (resultMap.get("year="+vo.getYear()).get(vo.getMonth()) == null){
		 * resultMap
		 * .get("year="+vo.getYear()).put(vo.getMonth(),"open counts : "+
		 * vo.getOpenCount() +" | "+"closed counts : "+vo.getClosedCount()); } }
		 * }
		 */

		JSONArray finalArray = new JSONArray();

		map.put("ToDay", "OPEN: " + toDayStats.getOpenCount() + " | "
				+ "CLOSED: " + toDayStats.getClosedCount());

		finalArray.put(map);

		String year = "";

		JSONObject jsonRecord = null;
		JSONArray monthsArray = null;
		JSONObject monthsData = null;
		Map<String, Integer> statusValues = null;

		Collections.sort(allStats, new Comparator<JobViewOrder>() {
			@Override
			public int compare(JobViewOrder s1, JobViewOrder s2) {
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

		for (JobViewOrder vo : allStats) {
			if (year.equalsIgnoreCase(vo.getYear())) {
				statusValues.put("open_counts", vo.getOpenCount());
				statusValues.put("closed_counts", vo.getClosedCount());
				monthsData.put(vo.getMonth(), statusValues);
				// monthsArray.put(monthsData);
			} else {
				if (Utils.isBlank(year)) {

					jsonRecord = new JSONObject();
					year = vo.getYear();
					monthsArray = new JSONArray();
					statusValues = new HashMap<String, Integer>();
					monthsData = new JSONObject();
					statusValues.put("open_counts", vo.getOpenCount());
					statusValues.put("closed_counts", vo.getClosedCount());
					monthsData.put(vo.getMonth(), statusValues);
					monthsArray.put(monthsData);
				} else {
					jsonRecord.put("year", year);
					yearStats = (List<JobViewOrder>) jobOrderService
							.getYearWiseStatusForExecutives(year,userRoleVo);
					jsonRecord.put("open_counts", yearStats.get(0)
							.getOpenCount());
					jsonRecord.put("closed_counts", yearStats.get(0)
							.getClosedCount());
					jsonRecord.put("data", monthsData);
					finalArray.put(jsonRecord);
					jsonRecord = new JSONObject();
					monthsArray = new JSONArray();
					statusValues = new HashMap<String, Integer>();
					monthsData = new JSONObject();
					year = vo.getYear();
					statusValues.put("open_counts", vo.getOpenCount());
					statusValues.put("closed_counts", vo.getClosedCount());
					monthsData.put(vo.getMonth(), statusValues);
					// monthsArray.put(monthsData);
				}
			}
			statusValues = new HashMap<String, Integer>();
		}

		jsonRecord.put("year", year);
		yearStats = (List<JobViewOrder>) jobOrderService
				.getYearWiseStatusForExecutives(year,userRoleVo);
		jsonRecord.put("open_counts", yearStats.get(0).getOpenCount());
		jsonRecord.put("closed_counts", yearStats.get(0).getClosedCount());
		jsonRecord.put("data", monthsData);
		finalArray.put(jsonRecord);

		System.out.println(finalArray);

		// resultMap.put("Today", map);
		System.out.println("resultMap  " + resultMap);

		jsonObject.put("resultMap", resultMap);
		log.info("from ExecutiveJobsRestController executiveJobStats() End....");
		return finalArray.toString();
	}

	public ExecutiveJobStatService getExecutiveJobStatService() {
		return executiveJobStatService;
	}

	public void setExecutiveJobStatService(
			ExecutiveJobStatService executiveJobStatService) {
		this.executiveJobStatService = executiveJobStatService;
	}

}

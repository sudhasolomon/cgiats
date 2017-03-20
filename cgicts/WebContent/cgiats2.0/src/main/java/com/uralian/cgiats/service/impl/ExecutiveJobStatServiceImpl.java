package com.uralian.cgiats.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.service.ExecutiveJobStatService;
import com.uralian.cgiats.service.JobOrderService;

@Service
public class ExecutiveJobStatServiceImpl implements ExecutiveJobStatService {

	@Autowired
	private JobOrderService jobOrderService;


	protected final Logger log = LoggerFactory.getLogger(ExecutiveJobStatServiceImpl.class);

	public Map<JobOrderStatus, Integer> fetchJoStatsbyYear(String year) {
		try {
			Date FromDate = null;
			Date ToDate = null;

			if ("TODAY".equalsIgnoreCase(year)) {
				FromDate = fromdates();
				ToDate = todates();
			} else {
				FromDate = getYearStart(Integer.valueOf(year));
				ToDate = getYearEnd(Integer.valueOf(year));
			}

			// Map<JobOrderStatus, Integer> jobOrdersHistoryMap =
			// jobOrderService.getAllJobOrdersCounts(FromDate, ToDate);
			Map<JobOrderStatus, Integer> jobOrdersHistoryMap = jobOrderService.getTodayJobOrdersCountsForExecutives(FromDate, ToDate);
			return jobOrdersHistoryMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public String getJobOrdersCount() throws JSONException {
		try {
			List<Map<String, String>> jsonJobStatList = new ArrayList<Map<String, String>>();
			// String yearArry [] = {"2012","2013","2014","2015","TODAY"};
			String yearArry[] = { "TODAY", "2015", "2014", "2013", "2012" };
			for (String year : yearArry) {
				Map<JobOrderStatus, Integer> JobOrdersMap = fetchJoStatsbyYear(year);
				Map<String, String> map = new HashMap<String, String>();
				for (Entry<JobOrderStatus, Integer> enty : JobOrdersMap.entrySet()) {
					map.put(enty.getKey().name(), String.valueOf(enty.getValue()));
				}
				map.put("year", year);
				if (map.size() > 1) {
					jsonJobStatList.add(map);
				}
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("jsonJobStatList", jsonJobStatList);
			return jsonObject.toString();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private Date getYearStart(int year) {
		try {
			Calendar originalDate = Calendar.getInstance();
			originalDate.set(year, 0, 1, 00, 00, 00);
			Date resumeFrom2 = originalDate.getTime();
			log.info("fromPreviousYearStart--" + resumeFrom2);
			return resumeFrom2;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private Date getYearEnd(int year) {
		try {
			Calendar originalDate = Calendar.getInstance();
			originalDate.set(year, 11, 31, 23, 59, 59);
			Date resumeTo2 = originalDate.getTime();
			log.info("fromPreviousYearEnd--" + resumeTo2);
			return resumeTo2;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private Date fromdates() {
		try {
			Calendar originalDate = Calendar.getInstance();
			// originalDate.add(Calendar.DAY_OF_YEAR, 0);
			originalDate.set(Calendar.HOUR_OF_DAY, 0);
			originalDate.set(Calendar.MINUTE, 0);
			originalDate.set(Calendar.SECOND, 0);
			originalDate.set(Calendar.MILLISECOND, 0);
			// originalDate.set(originalDate.DATE,originalDate.getActualMinimum(originalDate.DATE));
			Date resumeFrom2 = originalDate.getTime();
			log.info("resumefrom--" + resumeFrom2);
			return resumeFrom2;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private Date todates() {
		try {
			Calendar originalDate = Calendar.getInstance();
			// originalDate.add(Calendar.DAY_OF_YEAR, 0);
			originalDate.set(Calendar.HOUR_OF_DAY, 23);
			originalDate.set(Calendar.MINUTE, 59);
			originalDate.set(Calendar.SECOND, 59);
			// originalDate.set(originalDate.DATE,
			// originalDate.getActualMaximum(originalDate.DATE));
			Date resumeTo2 = originalDate.getTime();
			log.info("resumeTo2--" + resumeTo2);
			return resumeTo2;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

package com.uralian.cgiats.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.ConstraintMode;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.IndiaJobOrderService;
import com.uralian.cgiats.service.IndiaSubmittalService;
import com.uralian.cgiats.service.JobOrderService;
import com.uralian.cgiats.service.SubmittalService;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.Utils;

@RestController
@RequestMapping("dashBoardController")
public class DashBoardController {
	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private SubmittalService submittalService;
	
	@Autowired
	private IndiaSubmittalService IndiaSubmittalService;

	@Autowired
	private JobOrderService jobOrderService;
	
	@Autowired
	private IndiaJobOrderService IndiaJobOrderService;

	@Autowired
	private CandidateService candidateService;

	@RequestMapping(value = "/getDashBoardStats", method = RequestMethod.GET)
	public ResponseEntity<?> getDashBoardStats() {
		try {
			// Submittal
			Map<String, Object> yesterdaySubmittalStatsMap = fetchYesterdaySubmittalStats();
			Map<String, Object> previousYearSubmittalStatsMap = fetchPreviousYearSubmittalsStats();
			Map<String, Object> presentYearSubmittalStatsMap = fetchSubmittalStatsPresent();

			// Job Orders
			Map<String, Object> yesterdayJobStatsMap = fetchYesterdayJoStats();
			Map<String, Object> previousYearJobStatsMap = fetchJoStatsHistory();
			Map<String, Object> presentYearJobStatsMap = fetchJoStatsPresent();

			// Resumes
			Map<String, Object> yesterdayResumeStatsMap = fetchYesterdayResumesStats();
			Map<String, Object> previousYearResumeStatsMap = fetchResumesStatsHistory();
			Map<String, Object> presentYearResumeStatsMap = fetchResumesStatsPresent();

			Object totalResumeCount = fetchTotalResumesCounts();
			Long totalPresentYearResumeCount = fetchPresentYearTotalResumesCounts();
			Long totalLastYearResumeCount = fetchLastYearTotalResumesCounts();

			Map<String, Object> dashBoardDataMap = new LinkedHashMap<String, Object>();
			dashBoardDataMap.put("yesterdaySubmittalStatsMap", yesterdaySubmittalStatsMap);
			dashBoardDataMap.put("previousYearSubmittalStatsMap", previousYearSubmittalStatsMap);
			dashBoardDataMap.put("presentYearSubmittalStatsMap", presentYearSubmittalStatsMap);

			dashBoardDataMap.put("yesterdayJobStatsMap", yesterdayJobStatsMap);
			dashBoardDataMap.put("previousYearJobStatsMap", previousYearJobStatsMap);
			dashBoardDataMap.put("presentYearJobStatsMap", presentYearJobStatsMap);

			dashBoardDataMap.put("yesterdayResumeStatsMap", yesterdayResumeStatsMap);
			dashBoardDataMap.put("previousYearResumeStatsMap", previousYearResumeStatsMap);
			dashBoardDataMap.put("presentYearResumeStatsMap", presentYearResumeStatsMap);

			dashBoardDataMap.put("totalResumeCount", totalResumeCount);
			dashBoardDataMap.put("totalPresentYearResumeCount", totalPresentYearResumeCount);
			dashBoardDataMap.put("totalLastYearResumeCount", totalLastYearResumeCount);

			Calendar calendar = Calendar.getInstance();

			dashBoardDataMap.put("currentYear", calendar.get(Calendar.YEAR));
			dashBoardDataMap.put("previousYear", (calendar.get(Calendar.YEAR) - 1));

			return new ResponseEntity<>(dashBoardDataMap, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Date getPreviousYearStart() {
		try {
			Calendar todayDate = Calendar.getInstance();
			todayDate.set((todayDate.get(Calendar.YEAR) - 1), 0, 1, 00, 00, 00);
			return todayDate.getTime();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Date getPreviousYearEnd() {
		try {
			Calendar todayDate = Calendar.getInstance();
			todayDate.set((todayDate.get(Calendar.YEAR) - 1), 11, 31, 23, 59, 59);
			return todayDate.getTime();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Object> fetchPreviousYearSubmittalsStats() {
		try {
			Map<String, Object> previousYearSubmittalsMap = new LinkedHashMap<String, Object>();
			Map<SubmittalStatus, Integer> submittalsHistoryMap = submittalService.getAllSubmittalStatusCounts(getPreviousYearStart(), getPreviousYearEnd());
			Map<String, Integer> inActiveStartsMap = submittalService.getInActiveStartedCounts(getPreviousYearStart(), getPreviousYearEnd());
			List<Integer> submittalsLst = new ArrayList<Integer>(submittalsHistoryMap.values());
			Iterator<Integer> itr = submittalsLst.iterator();
			int values = 0;
			int totalSubmittals;
			totalSubmittals = 0;
			while (itr.hasNext()) {
				values = (Integer) itr.next();
				totalSubmittals += values;
				log.info("values" + values);
			}
			submittalsHistoryMap.put(SubmittalStatus.SUBMITTED, totalSubmittals);
			log.info("fetchSubmittalStatsHistory....." + submittalsHistoryMap);
			log.info("totalSubmittals...." + totalSubmittals);
			
			
			Map<String, Integer> finalRecordsMap = new HashMap<String,Integer>();
			for(SubmittalStatus status:submittalsHistoryMap.keySet()){
				finalRecordsMap.put(status.name(), submittalsHistoryMap.get(status));
			}
			finalRecordsMap.put(Constants.INACTIVE_STARTS, inActiveStartsMap.get(Constants.INACTIVE_STARTS));
			
			previousYearSubmittalsMap.put("totalCount", totalSubmittals);
			previousYearSubmittalsMap.put("records", finalRecordsMap);
			return previousYearSubmittalsMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Date getPresentYearStart() {
		try {
			Calendar todayDate = Calendar.getInstance();
			todayDate.set((todayDate.get(Calendar.YEAR)), 0, 1, 00, 00, 00);
			return todayDate.getTime();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Date getPresentYearEnd() {
		try {
			Calendar todayDate = Calendar.getInstance();
			todayDate.set((todayDate.get(Calendar.YEAR)), 11, 31, 23, 59, 59);
			return todayDate.getTime();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Object> fetchSubmittalStatsPresent() {
		try {
			Map<String, Object> presentYearSubmittalsMap = new LinkedHashMap<String, Object>();
			Date presentYearStart = getPresentYearStart();
			Date presentYearEnd = getPresentYearEnd();
			log.info("presentFromDate>>>" + presentYearStart);
			log.info("presentToDate>>>" + presentYearEnd);
			Map<SubmittalStatus, Integer> submittalsPresentMap = submittalService.getAllSubmittalStatusCounts(presentYearStart, presentYearEnd);
			Map<String, Integer> inActiveStartsMap = submittalService.getInActiveStartedCounts(presentYearStart,presentYearEnd);
			List<Integer> submittalsLst = new ArrayList<Integer>(submittalsPresentMap.values());
			Iterator<Integer> itr = submittalsLst.iterator();
			int values = 0;
			int totalSubmittals;
			totalSubmittals = 0;
			while (itr.hasNext()) {
				values = (Integer) itr.next();
				totalSubmittals += values;
				log.info("values" + values);
			}
			submittalsPresentMap.put(SubmittalStatus.SUBMITTED, totalSubmittals);

			Map<String, Integer> finalRecordsMap = new HashMap<String,Integer>();
			for(SubmittalStatus status:submittalsPresentMap.keySet()){
				finalRecordsMap.put(status.name(), submittalsPresentMap.get(status));
			}
			finalRecordsMap.put(Constants.INACTIVE_STARTS, inActiveStartsMap.get(Constants.INACTIVE_STARTS));
			
			log.info("fetchSubmittalStatsPresent....." + submittalsPresentMap);
			log.info("totalSubmittals...." + totalSubmittals);
			presentYearSubmittalsMap.put("totalCount", totalSubmittals);
			presentYearSubmittalsMap.put("records", finalRecordsMap);
			return presentYearSubmittalsMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}



	public Map<String, Object> fetchYesterdaySubmittalStats() {
		try {
			Map<String, Object> yesterdaySubmittalsMap = new LinkedHashMap<String, Object>();
			Date yesterdayFrom = Utils.getYesterdayFromDate();
			Date yesterdayTo = Utils.getYesterdayToDate();
			log.info("yesterdayFrom>>>" + yesterdayFrom);
			log.info("yesterdayTo>>>" + yesterdayTo);
			Map<SubmittalStatus, Integer> submittalsMap = submittalService.getAllSubmittalStatusCounts(yesterdayFrom, yesterdayTo);
			Map<String, Integer> inActiveStartsMap = submittalService.getInActiveStartedCounts(yesterdayFrom, yesterdayTo);
			List<Integer> submittalsLst = new ArrayList<Integer>(submittalsMap.values());
			Iterator<Integer> itr = submittalsLst.iterator();
			int values = 0;
			int totalSubmittals;
			totalSubmittals = 0;
			while (itr.hasNext()) {
				values = (Integer) itr.next();
				totalSubmittals += values;
				log.info("values" + values);
			}
			submittalsMap.put(SubmittalStatus.SUBMITTED, totalSubmittals);
			
			Map<String, Integer> finalRecordsMap = new HashMap<String,Integer>();
			for(SubmittalStatus status:submittalsMap.keySet()){
				finalRecordsMap.put(status.name(), submittalsMap.get(status));
			}
			finalRecordsMap.put(Constants.INACTIVE_STARTS, inActiveStartsMap.get(Constants.INACTIVE_STARTS));
			
			log.info("fetchTodaySubmittalStats....." + submittalsMap);
			log.info("totalSubmittals...." + totalSubmittals);
			yesterdaySubmittalsMap.put("totalCount", totalSubmittals);
			yesterdaySubmittalsMap.put("records", finalRecordsMap);

			return yesterdaySubmittalsMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Object> fetchYesterdayJoStats() {
		try {
			Map<String, Object> yesterdayJobStatsMap = new LinkedHashMap<String, Object>();
			Date yesterdayFrom = Utils.getYesterdayFromDate();
			Date yesterdayTo = Utils.getYesterdayToDate();
			Map<JobOrderStatus, Integer> jobOrdersMap = jobOrderService.getAllJobOrdersCounts(yesterdayFrom, yesterdayTo);
			List<Integer> josLst = new ArrayList<Integer>(jobOrdersMap.values());
			Iterator<Integer> itr = josLst.iterator();
			int values = 0;
			int totalJos = 0;
			while (itr.hasNext()) {
				values = (Integer) itr.next();
				totalJos += values;
			}
			log.info("jobOrdersMap...." + jobOrdersMap);
			log.info("fetchTodayJoStats...." + totalJos);
			yesterdayJobStatsMap.put("totalCount", totalJos);
			yesterdayJobStatsMap.put("records", jobOrdersMap);
			return yesterdayJobStatsMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Object> fetchJoStatsHistory() {
		try {
			Map<String, Object> previousYearJobStatsMap = new LinkedHashMap<String, Object>();
			Map<JobOrderStatus, Integer> jobOrdersHistoryMap = jobOrderService.getAllJobOrdersCounts(getPreviousYearStart(), getPreviousYearEnd());
			List<Integer> josLst = new ArrayList<Integer>(jobOrdersHistoryMap.values());
			Iterator<Integer> itr = josLst.iterator();
			int values = 0;
			int totalJos = 0;
			while (itr.hasNext()) {
				values = (Integer) itr.next();
				totalJos += values;
			}
			log.info("fetchJoStatsHistory...." + totalJos);
			previousYearJobStatsMap.put("totalCount", totalJos);
			previousYearJobStatsMap.put("records", jobOrdersHistoryMap);
			return previousYearJobStatsMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Object> fetchJoStatsPresent() {
		try {
			Map<String, Object> jobOrdersPresentStatsMap = new LinkedHashMap<String, Object>();
			Map<JobOrderStatus, Integer> jobOrdersPresentMap = jobOrderService.getAllJobOrdersCounts(getPresentYearStart(), getPresentYearEnd());
			List<Integer> josLst = new ArrayList<Integer>(jobOrdersPresentMap.values());
			Iterator<Integer> itr = josLst.iterator();
			int values = 0;
			int totalJos = 0;
			while (itr.hasNext()) {
				values = (Integer) itr.next();
				totalJos += values;
			}
			log.info("fetchJoStatsPresent...." + totalJos);
			jobOrdersPresentStatsMap.put("totalCount", totalJos);
			jobOrdersPresentStatsMap.put("records", jobOrdersPresentMap);

			return jobOrdersPresentStatsMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Object> fetchYesterdayResumesStats() {
		try {
			Map<String, Object> yesterdayResumeStatsMap = new LinkedHashMap<String, Object>();
			Date yesterdayFrom = Utils.getYesterdayFromDate();
			Date yesterdayTo = Utils.getYesterdayToDate();
			log.info("yesterdayFrom>>>" + yesterdayFrom);
			log.info("yesterdayTo>>>" + yesterdayTo);

			Map<String, Integer> resumesMap = candidateService.getAllCandidatesCounts(yesterdayFrom, yesterdayTo);
			// resumeAuthors = new ArrayList<String>(resumesMap.keySet());
			List<Integer> josLst = new ArrayList<Integer>(resumesMap.values());
			log.info("Size" + josLst.size());
			Iterator<Integer> itr = josLst.iterator();
			Integer values = 0;
			int totalResumesToday = 0;
			while (itr.hasNext()) {
				values = (Integer) itr.next();
				if (values == null)
					values = 0;
				totalResumesToday += values;
			}
			log.info("jobOrdersMap...." + resumesMap);
			log.info("fetchTodayResumesStats...." + totalResumesToday);
			yesterdayResumeStatsMap.put("totalCount", totalResumesToday);
			yesterdayResumeStatsMap.put("records", resumesMap);
			return yesterdayResumeStatsMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Object> fetchResumesStatsHistory() {
		try {
			Map<String, Object> previousYearResumeStatsMap = new LinkedHashMap<String, Object>();
			Map<String, Integer> resumesHistoryMap = candidateService.getAllCandidatesCounts(getPreviousYearStart(), getPreviousYearEnd());
			// resumeAuthors = new
			// ArrayList<String>(resumesHistoryMap.keySet());
			List<Integer> josLst = new ArrayList<Integer>(resumesHistoryMap.values());
			Iterator<Integer> itr = josLst.iterator();
			Integer values = 0;
			int totalResumes = 0;
			while (itr.hasNext()) {
				values = (Integer) itr.next();
				if (values == null)
					values = 0;
				totalResumes += values;
			}
			log.info("resumesHistoryMap...." + resumesHistoryMap);
			log.info("fetchResumesStatsHistory...." + totalResumes);
			previousYearResumeStatsMap.put("totalCount", totalResumes);
			previousYearResumeStatsMap.put("records", resumesHistoryMap);
			return previousYearResumeStatsMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Object> fetchResumesStatsPresent() {
		try {
			Map<String, Object> presentYearResumeStatsMap = new LinkedHashMap<String, Object>();
			Map<String, Integer> resumesPresentMap = candidateService.getAllCandidatesCounts(getPresentYearStart(), getPresentYearEnd());
			// resumeAuthors = new
			// ArrayList<String>(resumesPresentMap.keySet());
			List<Integer> josLst = new ArrayList<Integer>(resumesPresentMap.values());
			Iterator<Integer> itr = josLst.iterator();
			Integer values = 0;
			int totalResumes = 0;
			while (itr.hasNext()) {

				values = (Integer) itr.next();
				if (values == null)
					values = 0;
				totalResumes += values;
			}

			log.info("resumesPresentMap...." + resumesPresentMap);
			log.info("fetchResumesStatsPresent...." + totalResumes);
			presentYearResumeStatsMap.put("totalCount", totalResumes);
			presentYearResumeStatsMap.put("records", resumesPresentMap);
			return presentYearResumeStatsMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Object fetchTotalResumesCounts() {
		try {
			List<Integer> resumesPresentList = candidateService.getAllResumesCounts();
			Object totalCounts = 0;
			if (resumesPresentList != null && resumesPresentList.size() > 0) {
				totalCounts = resumesPresentList.get(0);
			}
			log.info("TotalCounts...." + totalCounts);
			return totalCounts;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Long fetchPresentYearTotalResumesCounts() {
		try {
			Long presentYearResumeCount = candidateService.getAllResumesCounts(getPresentYearStart(), getPresentYearEnd());
			log.info("presentYearResumeCount...." + presentYearResumeCount);
			return presentYearResumeCount;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Long fetchLastYearTotalResumesCounts() {
		try {
			Long LastYearResumeCount = candidateService.getAllResumesCounts(getPreviousYearStart(), getPreviousYearEnd());
			log.info("LastYearResumeCount...." + LastYearResumeCount);
			return LastYearResumeCount;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	
	/////India dashboard details//////
	
	@RequestMapping(value = "/getIndiaDashBoardStats", method = RequestMethod.GET)
	public ResponseEntity<?> getIndiaDashBoardStats() {
		try {
			// Submittal
			Map<String, Object> yesterdaySubmittalStatsMap = fetchYesterdayIndiaSubmittalStats();
			Map<String, Object> previousYearSubmittalStatsMap = fetchPreviousYearIndiaSubmittalsStats();
			Map<String, Object> presentYearSubmittalStatsMap = fetchIndiaSubmittalStatsPresent();

			// Job Orders
			Map<String, Object> yesterdayJobStatsMap = fetchYesterdayIndiaJoStats();
			Map<String, Object> previousYearJobStatsMap = fetchIndiaJoStatsHistory();
			Map<String, Object> presentYearJobStatsMap = fetchIndiaJoStatsPresent();


			Map<String, Object> indiaDashBoardDataMap = new LinkedHashMap<String, Object>();
			indiaDashBoardDataMap.put("yesterdayIndiaSubmittalStatsMap", yesterdaySubmittalStatsMap);
			indiaDashBoardDataMap.put("previousYearSubmittalStatsMap", previousYearSubmittalStatsMap);
			indiaDashBoardDataMap.put("presentYearSubmittalStatsMap", presentYearSubmittalStatsMap);

			indiaDashBoardDataMap.put("yesterdayJobStatsMap", yesterdayJobStatsMap);
			indiaDashBoardDataMap.put("previousYearJobStatsMap", previousYearJobStatsMap);
			indiaDashBoardDataMap.put("presentYearJobStatsMap", presentYearJobStatsMap);


			Calendar calendar = Calendar.getInstance();

			indiaDashBoardDataMap.put("currentYear", calendar.get(Calendar.YEAR));
			indiaDashBoardDataMap.put("previousYear", (calendar.get(Calendar.YEAR) - 1));

			return new ResponseEntity<>(indiaDashBoardDataMap, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	
	public Map<String, Object> fetchYesterdayIndiaSubmittalStats() {
		try {
			Map<String, Object> yesterdaySubmittalsMap = new LinkedHashMap<String, Object>();
			Date yesterdayFrom = Utils.getYesterdayFromDate();
			Date yesterdayTo = Utils.getYesterdayToDate();
			log.info("yesterdayFrom>>>" + yesterdayFrom);
			log.info("yesterdayTo>>>" + yesterdayTo);
			Map<SubmittalStatus, Integer> submittalsMap = IndiaSubmittalService.getAllIndiaSubmittalStatusCounts(yesterdayFrom, yesterdayTo);
			Map<String, Integer> inActiveStartsMap = IndiaSubmittalService.getIndiaInActiveStartedCounts(yesterdayFrom, yesterdayTo);
			List<Integer> submittalsLst = new ArrayList<Integer>(submittalsMap.values());
			Iterator<Integer> itr = submittalsLst.iterator();
			int values = 0;
			int totalSubmittals;
			totalSubmittals = 0;
			while (itr.hasNext()) {
				values = (Integer) itr.next();
				totalSubmittals += values;
				log.info("values" + values);
			}
			submittalsMap.put(SubmittalStatus.SUBMITTED, totalSubmittals);
			
			Map<String, Integer> finalRecordsMap = new HashMap<String,Integer>();
			for(SubmittalStatus status:submittalsMap.keySet()){
				finalRecordsMap.put(status.name(), submittalsMap.get(status));
			}
			finalRecordsMap.put(Constants.INACTIVE_STARTS, inActiveStartsMap.get(Constants.INACTIVE_STARTS));
			
			log.info("fetchTodaySubmittalStats....." + submittalsMap);
			log.info("totalSubmittals...." + totalSubmittals);
			yesterdaySubmittalsMap.put("totalCount", totalSubmittals);
			yesterdaySubmittalsMap.put("records", finalRecordsMap);

			return yesterdaySubmittalsMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	public Map<String, Object> fetchPreviousYearIndiaSubmittalsStats() {
		try {
			Map<String, Object> previousYearSubmittalsMap = new LinkedHashMap<String, Object>();
			Map<SubmittalStatus, Integer> submittalsHistoryMap = IndiaSubmittalService.getAllIndiaSubmittalStatusCounts(getPreviousYearStart(), getPreviousYearEnd());
			Map<String, Integer> inActiveStartsMap = IndiaSubmittalService.getIndiaInActiveStartedCounts(getPreviousYearStart(), getPreviousYearEnd());
			List<Integer> submittalsLst = new ArrayList<Integer>(submittalsHistoryMap.values());
			Iterator<Integer> itr = submittalsLst.iterator();
			int values = 0;
			int totalSubmittals;
			totalSubmittals = 0;
			while (itr.hasNext()) {
				values = (Integer) itr.next();
				totalSubmittals += values;
				log.info("values" + values);
			}
			submittalsHistoryMap.put(SubmittalStatus.SUBMITTED, totalSubmittals);
			log.info("fetchSubmittalStatsHistory....." + submittalsHistoryMap);
			log.info("totalSubmittals...." + totalSubmittals);
			
			
			Map<String, Integer> finalRecordsMap = new HashMap<String,Integer>();
			for(SubmittalStatus status:submittalsHistoryMap.keySet()){
				finalRecordsMap.put(status.name(), submittalsHistoryMap.get(status));
			}
			finalRecordsMap.put(Constants.INACTIVE_STARTS, inActiveStartsMap.get(Constants.INACTIVE_STARTS));
			
			previousYearSubmittalsMap.put("totalCount", totalSubmittals);
			previousYearSubmittalsMap.put("records", finalRecordsMap);
			return previousYearSubmittalsMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	public Map<String, Object> fetchIndiaSubmittalStatsPresent() {
		try {
			Map<String, Object> presentYearSubmittalsMap = new LinkedHashMap<String, Object>();
			Date presentYearStart = getPresentYearStart();
			Date presentYearEnd = getPresentYearEnd();
			log.info("presentFromDate>>>" + presentYearStart);
			log.info("presentToDate>>>" + presentYearEnd);
			Map<SubmittalStatus, Integer> submittalsPresentMap = IndiaSubmittalService.getAllIndiaSubmittalStatusCounts(presentYearStart, presentYearEnd);
			Map<String, Integer> inActiveStartsMap = IndiaSubmittalService.getIndiaInActiveStartedCounts(presentYearStart,presentYearEnd);
			List<Integer> submittalsLst = new ArrayList<Integer>(submittalsPresentMap.values());
			Iterator<Integer> itr = submittalsLst.iterator();
			int values = 0;
			int totalSubmittals;
			totalSubmittals = 0;
			while (itr.hasNext()) {
				values = (Integer) itr.next();
				totalSubmittals += values;
				log.info("values" + values);
			}
			submittalsPresentMap.put(SubmittalStatus.SUBMITTED, totalSubmittals);

			Map<String, Integer> finalRecordsMap = new HashMap<String,Integer>();
			for(SubmittalStatus status:submittalsPresentMap.keySet()){
				finalRecordsMap.put(status.name(), submittalsPresentMap.get(status));
			}
			finalRecordsMap.put(Constants.INACTIVE_STARTS, inActiveStartsMap.get(Constants.INACTIVE_STARTS));
			
			log.info("fetchSubmittalStatsPresent....." + submittalsPresentMap);
			log.info("totalSubmittals...." + totalSubmittals);
			presentYearSubmittalsMap.put("totalCount", totalSubmittals);
			presentYearSubmittalsMap.put("records", finalRecordsMap);
			return presentYearSubmittalsMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	public Map<String, Object> fetchYesterdayIndiaJoStats() {
		try {
			Map<String, Object> yesterdayJobStatsMap = new LinkedHashMap<String, Object>();
			Date yesterdayFrom = Utils.getYesterdayFromDate();
			Date yesterdayTo = Utils.getYesterdayToDate();
			Map<JobOrderStatus, Integer> jobOrdersMap = IndiaJobOrderService.getAllIndiaJobOrdersCounts(yesterdayFrom, yesterdayTo);
			List<Integer> josLst = new ArrayList<Integer>(jobOrdersMap.values());
			Iterator<Integer> itr = josLst.iterator();
			int values = 0;
			int totalJos = 0;
			while (itr.hasNext()) {
				values = (Integer) itr.next();
				totalJos += values;
			}
			log.info("jobOrdersMap...." + jobOrdersMap);
			log.info("fetchTodayJoStats...." + totalJos);
			yesterdayJobStatsMap.put("totalCount", totalJos);
			yesterdayJobStatsMap.put("records", jobOrdersMap);
			return yesterdayJobStatsMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	
	public Map<String, Object> fetchIndiaJoStatsHistory() {
		try {
			Map<String, Object> previousYearJobStatsMap = new LinkedHashMap<String, Object>();
			Map<JobOrderStatus, Integer> jobOrdersHistoryMap = IndiaJobOrderService.getAllIndiaJobOrdersCounts(getPreviousYearStart(), getPreviousYearEnd());
			List<Integer> josLst = new ArrayList<Integer>(jobOrdersHistoryMap.values());
			Iterator<Integer> itr = josLst.iterator();
			int values = 0;
			int totalJos = 0;
			while (itr.hasNext()) {
				values = (Integer) itr.next();
				totalJos += values;
			}
			log.info("fetchJoStatsHistory...." + totalJos);
			previousYearJobStatsMap.put("totalCount", totalJos);
			previousYearJobStatsMap.put("records", jobOrdersHistoryMap);
			return previousYearJobStatsMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Object> fetchIndiaJoStatsPresent() {
		try {
			Map<String, Object> jobOrdersPresentStatsMap = new LinkedHashMap<String, Object>();
			Map<JobOrderStatus, Integer> jobOrdersPresentMap = IndiaJobOrderService.getAllIndiaJobOrdersCounts(getPresentYearStart(), getPresentYearEnd());
			List<Integer> josLst = new ArrayList<Integer>(jobOrdersPresentMap.values());
			Iterator<Integer> itr = josLst.iterator();
			int values = 0;
			int totalJos = 0;
			while (itr.hasNext()) {
				values = (Integer) itr.next();
				totalJos += values;
			}
			log.info("fetchJoStatsPresent...." + totalJos);
			jobOrdersPresentStatsMap.put("totalCount", totalJos);
			jobOrdersPresentStatsMap.put("records", jobOrdersPresentMap);

			return jobOrdersPresentStatsMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}
}

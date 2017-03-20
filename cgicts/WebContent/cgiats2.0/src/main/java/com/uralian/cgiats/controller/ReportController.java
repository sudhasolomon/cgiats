package com.uralian.cgiats.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.uralian.cgiats.dto.ReportwiseDto;
import com.uralian.cgiats.dto.ResumesUpdateCountDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.dto.SubmittalStatsDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.service.JobOrderService;
import com.uralian.cgiats.service.SubmittalService;
import com.uralian.cgiats.service.UserService;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.MonthEnum;
import com.uralian.cgiats.util.Utils;

import bsh.util.Util;

@RestController
@RequestMapping("reportController")
public class ReportController {
	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private SubmittalService submittalService;

	@Autowired
	private JobOrderService jobOrderService;

	@Autowired
	private UserService userService;
	//
	// @Autowired
	// private CandidateService candidateService;

	// @Autowired
	// private ResumeHistoryService resumeHistoryService;

	@RequestMapping(value = "/getAllSubmittalRecruitersReport", method = RequestMethod.GET)
	public ResponseEntity<?> getAllSubmittalRecruitersReport(@RequestParam(value = "dmName", required = false) String dmName, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				Date dateStart = Utils.getYesterdayFromDate();
				Date dateEnd = Utils.getYesterdayToDate();
				log.info("yesterdayFrom>>>" + dateStart);
				log.info("yesterdayTo>>>" + dateEnd);
				Object yesterDayResultList = getUserWithSubmittalCounts(Utils.getLoginUser(request), dmName, null, dateStart, dateEnd);
				dateStart = Utils.getPreviousWeekStartDate();
				dateEnd = Utils.getPreviousWeekEndDate();
				log.info("Last Week From>>>" + dateStart);
				log.info("Last Week To>>>" + dateEnd);
				Object previousWeekResultList = getUserWithSubmittalCounts(Utils.getLoginUser(request), dmName, null, dateStart, dateEnd);

				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DATE, 1);
				dateStart = Utils.convertStringToDate(Utils.convertDateToString(cal.getTime()));
				dateEnd = Utils.getEndDate(Utils.convertDateToString(new Date()));
				log.info("Current Month From>>>" + dateStart);
				log.info("Current Month To>>>" + dateEnd);
				Object currentMontResultList = getUserWithSubmittalCounts(Utils.getLoginUser(request), dmName, null, dateStart, dateEnd);

				Map<String, Object> resultMap = new HashMap<String, Object>();

				resultMap.put("yesterDay", yesterDayResultList);
				resultMap.put("lastWeek", previousWeekResultList);
				resultMap.put("currentMonth", currentMontResultList);

				return new ResponseEntity<>(resultMap, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		return null;
	}

	@RequestMapping(value = "/getSubmittalCurrentReport", method = RequestMethod.GET)
	public ResponseEntity<?> getSubmittalCurrentReport(@RequestParam(value = "city", required = false) String city, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				Date dateStart = Utils.convertStringToDate(Utils.convertDateToString(new Date()));
				Date dateEnd = Utils.getEndDate(Utils.convertDateToString(new Date()));
				log.info("Current Day From >>>" + dateStart);
				log.info("Current Day To >>>" + dateEnd);
				Object dmWithRecruitersResultList = getUserWithSubmittalCounts(Utils.getLoginUser(request), null, city != null ? Arrays.asList(city) : null,
						dateStart, dateEnd);
				return new ResponseEntity<>(dmWithRecruitersResultList, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		return null;
	}

	@RequestMapping(value = "/getSubmittalYesterDayReport", method = RequestMethod.GET)
	public ResponseEntity<?> getSubmittalYesterDayReport(@RequestParam(value = "dmName", required = false) String dmName, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				Date dateStart = Utils.getYesterdayFromDate();
				Date dateEnd = Utils.getYesterdayToDate();
				log.info("yesterdayFrom>>>" + dateStart);
				log.info("yesterdayTo>>>" + dateEnd);
				Object dmWithRecruitersResultList = getUserWithSubmittalCounts(Utils.getLoginUser(request), dmName, null, dateStart, dateEnd);
				return new ResponseEntity<>(dmWithRecruitersResultList, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		return null;
	}

	@RequestMapping(value = "/getSubmittalLastWeekReport", method = RequestMethod.GET)
	public ResponseEntity<?> getSubmittalLastWeekReport(@RequestParam(value = "dmName", required = false) String dmName, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				Date dateStart = Utils.getPreviousWeekStartDate();
				Date dateEnd = Utils.getPreviousWeekEndDate();
				log.info("Last Week From>>>" + dateStart);
				log.info("Last Week To>>>" + dateEnd);
				Object dmWithRecruitersResultList = getUserWithSubmittalCounts(Utils.getLoginUser(request), dmName, null, dateStart, dateEnd);
				return new ResponseEntity<>(dmWithRecruitersResultList, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		return null;
	}

	@RequestMapping(value = "/getSubmittalCurrentMonthReport", method = RequestMethod.GET)
	public ResponseEntity<?> getSubmittalCurrentMonthReport(@RequestParam(value = "dmName", required = false) String dmName, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DATE, 1);
				Date dateStart = Utils.convertStringToDate(Utils.convertDateToString(cal.getTime()));
				Date dateEnd = Utils.getEndDate(Utils.convertDateToString(new Date()));
				log.info("Current Month From>>>" + dateStart);
				log.info("Current Month To>>>" + dateEnd);
				Object dmWithRecruitersResultList = getUserWithSubmittalCounts(Utils.getLoginUser(request), dmName, null, dateStart, dateEnd);
				return new ResponseEntity<>(dmWithRecruitersResultList, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		return null;
	}

	private Object getUserWithSubmittalCounts(UserDto userDto, String dmName, List<String> officeLocations, Date startDate, Date endDate) {
		Object dmWithRecruitersResultList = null;
		List<String> recList = null;
		if (dmName != null && dmName.equalsIgnoreCase(Constants.UNDEFINED)) {
			dmName = Constants.ALL;
		}
		if (dmName != null && dmName.length() > 0 && officeLocations == null && !dmName.equalsIgnoreCase(Constants.ALL)) {
			recList = userService.listAllADMsAndRec(Arrays.asList(dmName),true);
			recList.add(dmName);
		} else if (dmName != null && dmName.length() > 0 && dmName.equalsIgnoreCase(Constants.ALL)) {
			recList = userService.listAllDMSAndADMsAndRecByLocAndUserIds(null, null,Constants.ACTIVE);
		}
		if (officeLocations != null) {
			recList = userService.listAllDMSAndADMsAndRecByLocAndUserIds(null, officeLocations,Constants.ACTIVE);
		}
		Map<String, Map<SubmittalStatus, Integer>> userWithStatusCountMap = jobOrderService.getSubmittalStatusByLocation(userDto, officeLocations, recList,
				startDate, endDate,null);

		if (userWithStatusCountMap != null) {
			log.info("list ::: " + userWithStatusCountMap.size());
			List<String> submittalBdms = new ArrayList<String>(userWithStatusCountMap.keySet());
			// if (dmName != null && dmName.length() > 0) {
			// submittalBdms.add(dmName);
			// }
			if (recList != null && recList.size() > 0) {
				submittalBdms.addAll(recList);
			}
			List<UserDto> userDtoList = null;
			if (submittalBdms != null && submittalBdms.size() > 0) {
				userDtoList = userService.getUsersInfoByIds(submittalBdms);
			}
			if (userDtoList != null && userDtoList.size() > 0) {
				for (UserDto dto : userDtoList) {
					if (userWithStatusCountMap.get(dto.getUserId()) == null) {
						userWithStatusCountMap.put(dto.getUserId(), new HashMap<SubmittalStatus, Integer>());
					}
					if (dto.getUserRole().equals(UserRole.DM)) {
						userWithStatusCountMap.remove(dto.getUserId());
					}
				}
			}

			dmWithRecruitersResultList = getFinalResult(officeLocations, userDtoList, userWithStatusCountMap);
		}
		return dmWithRecruitersResultList;
	}

	@SuppressWarnings("unchecked")
	private Object getFinalResult(List<String> officeLocations, List<UserDto> userDtoList, Map<String, Map<SubmittalStatus, Integer>> userWithStatusCountMap) {
		Map<String, Map<SubmittalStatus, Integer>> userWithStatusCountResultMap = new TreeMap<String, Map<SubmittalStatus, Integer>>();
		// Getting only recruiters
		// for (UserDto userDto : userDtoList) {
		// // if (userDto.getUserRole().equals(UserRole.Recruiter)) {
		// // Adding rest of sub ordinates(who has zero
		// // submittals)
		// if (userWithStatusCountMap.get(userDto.getUserId()) == null) {
		// userWithStatusCountResultMap.put(userDto.getUserId(), new
		// HashMap<SubmittalStatus, Integer>());
		// } else {
		// userWithStatusCountResultMap.put(userDto.getUserId(),
		// userWithStatusCountMap.get(userDto.getUserId()));
		// }
		// // }
		// }
		userWithStatusCountResultMap.putAll(userWithStatusCountMap);
		// Inserting the zero values to empty values
		for (String user : userWithStatusCountResultMap.keySet()) {
			Map<SubmittalStatus, Integer> submittalTotalsByStatus = userWithStatusCountResultMap.get(user);
			for (SubmittalStatus submittalStatus : SubmittalStatus.values()) {
				if (submittalTotalsByStatus.get(submittalStatus) == null) {
					submittalTotalsByStatus.put(submittalStatus, 0);
				}
			}
		}
		Map<String, Object> listWithTotalSubmittalMap = getUserWithSubmittalStatusList(userDtoList, userWithStatusCountResultMap);
		// List<Object> dmWithRecruitersResultList =
		// dmWithRecruitersMap((List<SubmittalStatsDto>)
		// listWithTotalSubmittalMap.get("list"));
		// if (officeLocations == null) {
		// return dmWithRecruitersResultList;
		// } else {
		// return listWithTotalSubmittalMap;
		// }
		return listWithTotalSubmittalMap;
	}

	private Map<String, Object> getUserWithSubmittalStatusList(List<UserDto> userDtoList,
			Map<String, Map<SubmittalStatus, Integer>> userWithStatusCountResultMap) {
		Map<String, Object> listWithTotalSubmittalMap = new HashMap<String, Object>();
		List<SubmittalStatsDto> userWithSubmittalList = new ArrayList<SubmittalStatsDto>();

		Map<String, UserDto> userIdWithUserMap = new HashMap<String, UserDto>();
		// Getting the information of each user(dm/createdBy)
		if (userDtoList != null && userDtoList.size() > 0) {
			for (UserDto userDto : userDtoList) {
				// System.out.println(userDto.getUserId());
				userIdWithUserMap.put(userDto.getUserId(), userDto);
			}
		}

		// It is for final submittal related count
		Map<String, Integer> submittalTotalsByStatus = new HashMap<String, Integer>();

		for (String user : userWithStatusCountResultMap.keySet()) {
			Map<SubmittalStatus, Integer> map = userWithStatusCountResultMap.get(user);
			SubmittalStatsDto dto = new SubmittalStatsDto();
			int totalByUser = 0, nuTotal = 0, statusCount = 0;
			for (Map.Entry<SubmittalStatus, Integer> entry : map.entrySet()) {
				SubmittalStatus status = entry.getKey();
				Integer count = entry.getValue();
				Integer oldCount = submittalTotalsByStatus.get(status.name());
				int newCount = oldCount != null ? oldCount + count : count;

				totalByUser += count;
				if (!status.equals(SubmittalStatus.SUBMITTED)) {
					statusCount += count;
					submittalTotalsByStatus.put(status.name(), newCount);
				}
			}

			nuTotal = totalByUser - statusCount;

			if (submittalTotalsByStatus.get(SubmittalStatus.SUBMITTED.name()) != null) {
				submittalTotalsByStatus.put(SubmittalStatus.SUBMITTED.name(), submittalTotalsByStatus.get(SubmittalStatus.SUBMITTED.name()) + totalByUser);
			} else {
				submittalTotalsByStatus.put(SubmittalStatus.SUBMITTED.name(), totalByUser);
			}
			if (submittalTotalsByStatus.get(Constants.NOT_UPDATED) != null) {
				submittalTotalsByStatus.put(Constants.NOT_UPDATED, submittalTotalsByStatus.get(Constants.NOT_UPDATED) + nuTotal);
			} else {
				submittalTotalsByStatus.put(Constants.NOT_UPDATED, nuTotal);
			}

			dto.setName(userIdWithUserMap.get(user).getFullName());
			dto.setDM(userIdWithUserMap.get(user).getAssignedBdm() != null ? userIdWithUserMap.get(user).getAssignedBdm() : user);
			dto.setUserId(user);
			dto.setLocation(userIdWithUserMap.get(user).getOfficeLocation());
			dto.setSUBMITTED(Utils.getIntegerValueOfObj(totalByUser));
			dto.setDMREJ(Utils.getIntegerValueOfObj(map.get(SubmittalStatus.DMREJ)));
			dto.setACCEPTED(Utils.getIntegerValueOfObj(map.get(SubmittalStatus.ACCEPTED)));

			dto.setINTERVIEWING(Utils.getIntegerValueOfObj(map.get(SubmittalStatus.INTERVIEWING)));
			dto.setCONFIRMED(Utils.getIntegerValueOfObj(map.get(SubmittalStatus.CONFIRMED)));
			dto.setREJECTED(Utils.getIntegerValueOfObj(map.get(SubmittalStatus.REJECTED)));
			dto.setSTARTED(Utils.getIntegerValueOfObj(map.get(SubmittalStatus.STARTED)));
			dto.setBACKOUT(Utils.getIntegerValueOfObj(map.get(SubmittalStatus.BACKOUT)));
			dto.setOUTOFPROJ(Utils.getIntegerValueOfObj(map.get(SubmittalStatus.OUTOFPROJ)));
			dto.setNotUpdated(Utils.getIntegerValueOfObj(nuTotal));
			userWithSubmittalList.add(dto);
		}
		listWithTotalSubmittalMap.put("list", userWithSubmittalList);
		listWithTotalSubmittalMap.put("total", submittalTotalsByStatus);

		return listWithTotalSubmittalMap;
	}

	/*
	 * private List<Object> dmWithRecruitersMap(List<SubmittalStatsDto>
	 * userWithSubmittalList) { Map<String, List<SubmittalStatsDto>>
	 * dmWithRecruitersMap = null; List<Object> dmWithRecruitersResultList =
	 * null;
	 * 
	 * if (userWithSubmittalList != null && userWithSubmittalList.size() > 0) {
	 * dmWithRecruitersMap = new TreeMap<String, List<SubmittalStatsDto>>(); for
	 * (SubmittalStatsDto dto : userWithSubmittalList) { if
	 * (dmWithRecruitersMap.get(dto.getDM()) != null) {
	 * dmWithRecruitersMap.get(dto.getDM()).add(dto); } else {
	 * List<SubmittalStatsDto> list = new ArrayList<SubmittalStatsDto>();
	 * list.add(dto); dmWithRecruitersMap.put(dto.getDM(), list); } }
	 * dmWithRecruitersResultList = new ArrayList<Object>(); for (String dmName
	 * : dmWithRecruitersMap.keySet()) { Map<String, Object>
	 * dmWithSubmittalListMap = new TreeMap<String, Object>(); Map<String,
	 * Integer> submittalTotalsByStatus = new HashMap<String, Integer>(); int
	 * submittal = 0, dmrej = 0, accepted = 0, interviewing = 0, confirmed = 0,
	 * rejected = 0, backout = 0, outOfProject = 0, nu = 0, started = 0; for
	 * (SubmittalStatsDto dto : dmWithRecruitersMap.get(dmName)) { submittal +=
	 * dto.getSUBMITTED(); dmrej += dto.getDMREJ(); accepted +=
	 * dto.getACCEPTED(); interviewing += dto.getINTERVIEWING(); confirmed +=
	 * dto.getCONFIRMED(); rejected += dto.getREJECTED(); backout +=
	 * dto.getBACKOUT(); outOfProject += dto.getOUTOFPROJ(); nu +=
	 * dto.getNotUpdated(); started += dto.getSTARTED(); }
	 * submittalTotalsByStatus.put(SubmittalStatus.SUBMITTED.name(), submittal);
	 * submittalTotalsByStatus.put(SubmittalStatus.DMREJ.name(), dmrej);
	 * submittalTotalsByStatus.put(SubmittalStatus.ACCEPTED.name(), accepted);
	 * submittalTotalsByStatus.put(SubmittalStatus.INTERVIEWING.name(),
	 * interviewing);
	 * submittalTotalsByStatus.put(SubmittalStatus.CONFIRMED.name(), confirmed);
	 * submittalTotalsByStatus.put(SubmittalStatus.REJECTED.name(), rejected);
	 * submittalTotalsByStatus.put(SubmittalStatus.BACKOUT.name(), backout);
	 * submittalTotalsByStatus.put(SubmittalStatus.OUTOFPROJ.name(),
	 * outOfProject); submittalTotalsByStatus.put(Constants.NOT_UPDATED, nu);
	 * submittalTotalsByStatus.put(SubmittalStatus.STARTED.name(), started);
	 * 
	 * dmWithSubmittalListMap.put("name", dmName);
	 * dmWithSubmittalListMap.put("list", dmWithRecruitersMap.get(dmName));
	 * dmWithSubmittalListMap.put("totalList", submittalTotalsByStatus);
	 * dmWithRecruitersResultList.add(dmWithSubmittalListMap); } }
	 * 
	 * return dmWithRecruitersResultList; }
	 */

	@RequestMapping(value = "/getResumesUpdateCount", method = RequestMethod.POST)
	public ResponseEntity<?> getResumesUpdateCount(HttpServletRequest request, @RequestParam("startDate") String actualStrFromDate,
			@RequestParam("endDate") String actualStrToDate) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException {
		if (Utils.getLoginUserId(request) != null) {
			Map<String, Object> resumesupadtecountlist = new HashMap<String, Object>();
			Date actualFromDate = Utils.convertStringToDate(actualStrFromDate);
			Date actualToDate = Utils.convertStringToDate(actualStrToDate);

			Calendar startDate = Calendar.getInstance();
			startDate.setTime(actualFromDate);
			startDate.add(Calendar.DATE, -1);
			startDate.set(Calendar.HOUR, 21);

			Calendar endDate = Calendar.getInstance();
			endDate.setTime(actualToDate);
			endDate.set(Calendar.HOUR_OF_DAY, 9);

			// srtDate.setHours(19);
			// srtDate.setMinutes(00);
			// srtDate.setSeconds(00);
			//
			// endDate.setHours(19);
			// endDate.setMinutes(00);
			// endDate.setSeconds(00);
			//
			// DateFormat timeFormat = new SimpleDateFormat("MM/dd/yyyy
			// HH:mm:ss");
			// timeFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
			// String StartEstTime = timeFormat.format(srtDate);
			// srtDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",
			// Locale.ENGLISH).parse(StartEstTime);
			//
			//
			// String endEstTime = timeFormat.format(endDate);
			// endDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",
			// Locale.ENGLISH).parse(endEstTime);

			System.out.println("startDate  " + startDate.getTime() + " " + "endDate  " + endDate.getTime());
			List<ResumesUpdateCountDto> resumesupdatecountdata = new ArrayList<ResumesUpdateCountDto>();
			resumesupdatecountdata = userService.getResumesUpdateCount(startDate.getTime(), endDate.getTime(),actualStrFromDate,actualStrToDate);

			resumesupadtecountlist.put("resumesupdatecountdata", resumesupdatecountdata);
			return new ResponseEntity<Map<String, Object>>(resumesupadtecountlist, HttpStatus.OK);

		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}

	@RequestMapping(value = "/getAllSubmittalYears", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getAllSubmittalYears(HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			List<?> list = submittalService.getAllSubmittalYears();
			return new ResponseEntity<>(list, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getWeeksOfMonth", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getWeeksOfMonth(HttpServletRequest request, @RequestParam("year") Integer year,
			@RequestParam("month") String month) {
		if (Utils.getLoginUserId(request) != null) {
			List<Map<String, Object>> datesMapList = new ArrayList<Map<String, Object>>();
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MONTH, MonthEnum.valueOf(month.toUpperCase()).getMonthIndex());
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.YEAR, year);
			int numberOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			int initDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			int a = (initDay == 0 ? 7 : initDay) + numberOfDays - 1;
			int numberOfWeeks = a / 7 + (a % 7 == 0 ? 0 : 1);

			for (int i = 0; i < numberOfWeeks; i++) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				while (cal.get(Calendar.DAY_OF_WEEK) > (cal.getFirstDayOfWeek() + 1)) {
					cal.add(Calendar.DATE, -1); // Substract 1 day until first
												// day
												// of week.
				}
				Date startDate = cal.getTime();
				cal.add(Calendar.DATE, +6);
				Date endDate = cal.getTime();
				System.out.println(startDate + "\t" + endDate);
				map.put("label", Utils.convertDateToString(startDate) + " - " + Utils.convertDateToString(endDate));
				map.put("value", "Week " + (i + 1));
				cal.setTime(endDate);
				cal.add(Calendar.DATE, +1);
				datesMapList.add(map);
			}

			return new ResponseEntity<>(datesMapList, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getAllAssignedBDMs", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getAllAssignedBDMs(HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			List<?> assignedBDMs = submittalService.getAllAssignedBDMs();
			return new ResponseEntity<>(assignedBDMs, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getWeekWiseRecruiterReport", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getWeekWiseRecruiterReport(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
		if (Utils.getLoginUserId(request) != null) {
			Map<String, Object> listWithTotalSubmittalMap = new HashMap<String, Object>();
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
			}
			if (reportwiseDto.getWeek() != null && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
				reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
			} else {
				reportwiseDto.setWeek(null);
			}
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
			}

			if (reportwiseDto.getDmName() != null && !reportwiseDto.getDmName().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setDmNames(Utils.getStrList_FromStr(reportwiseDto.getDmName()));
			}

			List<?> list = submittalService.getWeekWiseRecruiterReport(reportwiseDto);
			if (list != null && list.size() > 0) {
				Map<String, Integer> submittalTotalsByStatus = new HashMap<String, Integer>();
				Utils.getTotalCount((List<SubmittalStatsDto>) list, submittalTotalsByStatus);
				listWithTotalSubmittalMap.put("total", submittalTotalsByStatus);
			}
			listWithTotalSubmittalMap.put("list", list);
			return new ResponseEntity<>(listWithTotalSubmittalMap, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	
	@RequestMapping(value = "/getTurnAroundReportData", method = RequestMethod.POST)
	public ResponseEntity<?> getTurnAroundReportData(HttpServletRequest request, @RequestBody Map<String, String> map) {
		if (Utils.getLoginUserId(request) != null) {
			 List<SubmittalDto> dto = new ArrayList<SubmittalDto>();
			if (map.get("startDate") != null && map.get("endDate") != null) {
				 dto = submittalService.getTurnAroundTimeReport(Utils.convertStringToDate(map.get("startDate")), Utils.getEndDate(map.get("endDate")), 
						 map.get("dmName"));
				return new ResponseEntity<>(dto, HttpStatus.OK);
			}
			return new ResponseEntity<>(null, HttpStatus.OK);

		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	@RequestMapping(value = "/getResumeAuditLogData",method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> getResumeAuditLogData(HttpServletRequest request, @RequestBody Map<String, String> map){
		
		if(Utils.getLoginUserId(request) != null){
			Map<String,Object> list = new HashMap<String,Object>();
			Date srtDate = Utils.convertStringToDate(map.get("startDate"));
			Date endDate = Utils.getEndDate(map.get("endDate"));
			String candidateId = map.get("candidateId");
			String viewedBy = map.get("viewedBy");
			list = submittalService.getResumeAuditLogData(srtDate,endDate,candidateId,viewedBy);
			
			return new ResponseEntity<>(list,HttpStatus.OK);
		}else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	
}

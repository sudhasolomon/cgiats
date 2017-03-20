package com.uralian.cgiats.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.xpath.operations.Bool;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.uralian.cgiats.dto.ReportwiseDto;
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
import com.uralian.cgiats.util.SubmittalColors;
import com.uralian.cgiats.util.Utils;

@RestController
@RequestMapping("totalReportController")
public class TotalReportController {
	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private SubmittalService submittalService;

	@Autowired
	private JobOrderService jobOrderService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/getTotalOpenAndClosedReport", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getTotalOpenAndClosedReport(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
		if (Utils.getLoginUserId(request) != null) {
			Map<String, Object> listWithTotalSubmittalMap = new HashMap<String, Object>();
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getWeek().trim().length() > 0 && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
				reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
			} else {
				reportwiseDto.setWeek(null);
			}
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
			}

			if (reportwiseDto.getDmName() != null && !reportwiseDto.getDmName().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setDmNames(Utils.getStrList_FromStr(reportwiseDto.getDmName()));
			}

			List<?> list = jobOrderService.getJobOrderReport(reportwiseDto);
			listWithTotalSubmittalMap.put("data", list);
			return new ResponseEntity<>(listWithTotalSubmittalMap, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getDMsPerformanceTotalReport", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getDMsPerformanceTotalReport(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
		if (Utils.getLoginUserId(request) != null) {
			Map<String, Object> listWithTotalSubmittalMap = new HashMap<String, Object>();
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getWeek().trim().length() > 0 && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
				reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
			} else {
				reportwiseDto.setWeek(null);
			}
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
			}

			if (reportwiseDto.getDmName() != null && !reportwiseDto.getDmName().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setDmNames(Utils.getStrList_FromStr(reportwiseDto.getDmName()));
			}

			Map<String, Integer> map = (Map<String, Integer>) submittalService.getDMsPerformanceTotalReport(reportwiseDto);
			if (map != null && map.size() > 0) {
				List<String> userIds = new ArrayList<String>();
				List<Integer> userStatuses = new ArrayList<Integer>();
				for (String key : map.keySet()) {
					userIds.add(key);
					userStatuses.add(map.get(key));
				}
				listWithTotalSubmittalMap.put("dms", userIds);
				listWithTotalSubmittalMap.put("status", userStatuses);
			}
			return new ResponseEntity<>(listWithTotalSubmittalMap, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getDMWiseRecPerformanceTotalReport", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getDMWiseRecPerformanceTotalReport(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
		if (Utils.getLoginUserId(request) != null) {
			Map<String, Object> listWithTotalSubmittalMap = new HashMap<String, Object>();
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getWeek().trim().length() > 0 && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
				reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
			} else {
				reportwiseDto.setWeek(null);
			}
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
			}

			if (reportwiseDto.getDmName() != null && !reportwiseDto.getDmName().equalsIgnoreCase(Constants.ALL)) {
				List<String> userIds = userService.listAllADMsAndRec(Arrays.asList(reportwiseDto.getDmName()), null);
				reportwiseDto.setDmNames(Utils.getStrList_FromStr(reportwiseDto.getDmName()));
				reportwiseDto.getDmNames().addAll(userIds);
			}

			Map<String, Integer> map = (Map<String, Integer>) submittalService.getDMWiseRecPerformanceTotalReport(reportwiseDto);

			/*
			 * if (map != null && map.size() > 0) { int totalStarts = 0; for
			 * (String key : map.keySet()) { totalStarts += map.get(key); }
			 * 
			 * int noOfRecruiters =
			 * userService.listAllADMsAndRec(Arrays.asList(reportwiseDto.
			 * getDmName()), true).size() + 1; int noOfMonthsCount = 12; if
			 * (reportwiseDto.getYear() != null) { Integer yearsCount =
			 * reportwiseDto.getYear().split(",").length; Integer noOfMonths =
			 * 12; if (reportwiseDto.getMonth() != null) { noOfMonths =
			 * reportwiseDto.getMonths().size(); } noOfMonthsCount = yearsCount
			 * * noOfMonths; }
			 * 
			 * 
			 * { "minValue": "0", "maxValue": "50", "code": "#e44a00" }, {
			 * "minValue": "50", "maxValue": "75", "code": "#f8bd19" }, {
			 * "minValue": "75", "maxValue": "100", "code": "#6baa01" }
			 * 
			 * 
			 * Integer minValue =
			 * Utils.getTwoDecimalDoubleFromObj((noOfRecruiters) * (0.5) *
			 * (noOfMonthsCount)).intValue(); Integer avgValue =
			 * Utils.getTwoDecimalDoubleFromObj((noOfRecruiters) * (1) *
			 * (noOfMonthsCount)).intValue(); Integer maxValue =
			 * Utils.getTwoDecimalDoubleFromObj((noOfRecruiters) * (1.5) *
			 * (noOfMonthsCount)).intValue(); List<Map<String, Object>>
			 * rangesMap = new ArrayList<Map<String, Object>>(); Map<String,
			 * Object> ranges1 = new HashMap<String, Object>();
			 * ranges1.put("minValue", 0); ranges1.put("maxValue", minValue);
			 * ranges1.put("code", Constants.RED);
			 * 
			 * Map<String, Object> ranges2 = new HashMap<String, Object>();
			 * ranges2.put("minValue", (minValue + 1)); ranges2.put("maxValue",
			 * avgValue); ranges2.put("code", Constants.YELLOW);
			 * 
			 * Map<String, Object> ranges3 = new HashMap<String, Object>();
			 * ranges3.put("minValue", (avgValue + 1)); ranges3.put("maxValue",
			 * maxValue); ranges3.put("code", Constants.GREEN);
			 * 
			 * rangesMap.add(ranges1); rangesMap.add(ranges2);
			 * rangesMap.add(ranges3);
			 * 
			 * listWithTotalSubmittalMap.put("range", rangesMap);
			 * listWithTotalSubmittalMap.put("level", totalStarts);
			 * listWithTotalSubmittalMap.put("dmName",
			 * userService.getUsersByIds(Arrays.asList(reportwiseDto.getDmName()
			 * )).get(0).getFullName()); }
			 */

			// listWithTotalSubmittalMap.put("data", list);
			return new ResponseEntity<>(map, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getPeriodWiseTotalNumberOfStatus", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getPeriodWiseTotalNumberOfStatus(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
		if (Utils.getLoginUserId(request) != null) {
			Map<String, Object> listWithTotalSubmittalMap = new HashMap<String, Object>();
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setYears(Utils.getIntList_FromStr(reportwiseDto.getYear()));
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getWeek().trim().length() > 0 && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
				reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
			} else {
				reportwiseDto.setWeek(null);
			}
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
			}

			if (reportwiseDto.getDmName() != null && !reportwiseDto.getDmName().equalsIgnoreCase(Constants.ALL)) {
				List<String> userIds = userService.listAllADMsAndRec(Arrays.asList(reportwiseDto.getDmName()), null);
				reportwiseDto.setDmNames(Utils.getStrList_FromStr(reportwiseDto.getDmName()));
				reportwiseDto.getDmNames().addAll(userIds);
			}

			Map<String, Integer> map = (Map<String, Integer>) submittalService.getDMWiseRecPerformanceTotalReport(reportwiseDto);

			Map<String, SubmittalDto>  submittalMap=submittalService.getInActiveStartedOfRecruiter(null, null, reportwiseDto.getDmName(), null, reportwiseDto,null);
			
			if (map != null) {
				int totalStarts = 0;
				for (String key : map.keySet()) {
					totalStarts += map.get(key);
				}

				List<String> strStartDates = new ArrayList<String>();
				List<String> strEndDates = new ArrayList<String>();
				List<Date> startDates = new ArrayList<Date>();
				List<Date> endDates = new ArrayList<Date>();
				Boolean isFullYear = false;
				if (reportwiseDto.getYears() != null && reportwiseDto.getYears().size() > 0) {
					if (reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() > 0 && reportwiseDto.getMonths().size() < 12) {
						for (Integer year : reportwiseDto.getYears()) {
							for (String month : reportwiseDto.getMonths()) {
								Calendar cal = Calendar.getInstance();
								cal.set(Calendar.YEAR, year);
								cal.set(Calendar.DATE, 1);
								cal.set(Calendar.MONTH, MonthEnum.valueOf(month.toUpperCase()).getMonthIndex());
								strEndDates.add(Utils.convertDateToString(cal.getTime()));
								endDates.add(cal.getTime());
								cal.set(Calendar.YEAR, year);
								cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
								strStartDates.add(Utils.convertDateToString(cal.getTime()));
								startDates.add(cal.getTime());
							}

						}
					} else {
						isFullYear = true;

					}
				}

				List<UserDto> userDtoList = (List<UserDto>) userService.getServedDatesOfUser(strStartDates, strEndDates, isFullYear, reportwiseDto.getYears(),
						reportwiseDto.getDmName());
				if (userDtoList != null && userDtoList.size() > 0) {
					float noOfMonthsCount = 12f;
					if (reportwiseDto.getYear() != null) {
						Integer yearsCount = reportwiseDto.getYears().size();
						Integer noOfMonths = 12;
						if (reportwiseDto.getMonth() != null) {
							noOfMonths = reportwiseDto.getMonths().size();
						}
						noOfMonthsCount = yearsCount * noOfMonths;
						if (isFullYear) {
							Calendar calender = Calendar.getInstance();
							if (reportwiseDto.getYears().contains(calender.get(Calendar.YEAR))) {
								for (int i = calender.get(Calendar.MONTH); i < 11; i++) {
									System.out.println(noOfMonthsCount--);
								}
							}
							for (Integer year : reportwiseDto.getYears()) {
								if (reportwiseDto.getYears().contains(calender.get(Calendar.YEAR))) {
									for (int i = 0; i <= calender.get(Calendar.MONTH); i++) {
										Calendar cal = Calendar.getInstance();
										cal.set(Calendar.YEAR, year);
										cal.set(Calendar.DATE, 1);
										cal.set(Calendar.MONTH, i);
										endDates.add(cal.getTime());
										cal.set(Calendar.YEAR, year);
										cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
										startDates.add(cal.getTime());
									}
								} else {
									for (int i = 0; i < 12; i++) {
										Calendar cal = Calendar.getInstance();
										cal.set(Calendar.YEAR, year);
										cal.set(Calendar.DATE, 1);
										cal.set(Calendar.MONTH, i);
										endDates.add(cal.getTime());
										cal.set(Calendar.YEAR, year);
										cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
										startDates.add(cal.getTime());
									}
								}
							}
						}
					}

					float totalMinvalue = 0f;
					float totalMaxvalue = 0f;
					float avgMin = 0f, maxValue = 0f, avgMax = 0f;
					String userName = null;
					int activeCount = 0;
					for (UserDto dto : userDtoList) {
						if (dto.getStatus().equalsIgnoreCase(Constants.ACTIVE)) {
							activeCount++;
						}

						totalMinvalue = 0;
						totalMaxvalue = 0;
						if (dto.getUserId().equalsIgnoreCase(reportwiseDto.getDmName())) {
							userName = dto.getFullName();
						}
						dto.setNoOfStarts(map.get(dto.getUserId()) == null ? 0 : map.get(dto.getUserId()));
						dto.setNoOfActiveStarts(dto.getNoOfStarts());
						dto.setNoOfInActiveStarts(0);
						if(submittalMap!=null && submittalMap.get(dto.getUserId())!=null){
							dto.setNoOfInActiveStarts(Integer.parseInt(submittalMap.get(dto.getUserId()).getInActiveStartedCount()));
							dto.setNoOfStarts(dto.getNoOfActiveStarts()+dto.getNoOfInActiveStarts());
							totalStarts+=dto.getNoOfInActiveStarts();
						}
						int noOfMonthsDone = 0;
						if (!dto.getUserRole().equals(UserRole.DM)) {
							for (int i = 0; i < startDates.size(); i++) {
								if (dto.getJoinDate().compareTo(startDates.get(i)) <= 0 && dto.getServedDate().compareTo(endDates.get(i)) >= 0) {
									totalMinvalue += (dto.getMinStartCount() / 12f);
									totalMaxvalue += (dto.getMaxStartCount() / 12f);
									noOfMonthsDone++;
								}
							}
							dto.setAvgHires(Utils.getTwoDecimalDoubleFromObj(Double.valueOf(dto.getNoOfStarts()) / Double.valueOf(noOfMonthsDone)));

							if (dto.getAvgHires() < (Double.valueOf((dto.getMinStartCount() / 12f)) * (0.75d))) {
								dto.setStatusValue(1);
							} else if (dto.getAvgHires() >= (Double.valueOf((dto.getMinStartCount() / 12f)) * (0.75d))
									&& dto.getAvgHires() < Double.valueOf(dto.getMinStartCount() / 12f)) {
								dto.setStatusValue(2);
							} else if (dto.getAvgHires() >= Double.valueOf(dto.getMinStartCount() / 12f)
									&& dto.getAvgHires() < (Double.valueOf((dto.getMinStartCount() / 12f)) * (1.50d))) {
								dto.setStatusValue(3);
							} else if (dto.getAvgHires() >= (Double.valueOf((dto.getMinStartCount() / 12f)) * (1.50d))) {
								dto.setStatusValue(4);
							}

							dto.setNoOfMonthsWorked(noOfMonthsDone);
							avgMin += totalMinvalue;
							maxValue += totalMaxvalue;
						}
					}
					avgMin = Math.round(avgMin * 100.0f) / 100.0f;
					maxValue = Math.round(maxValue * 100.0f) / 100.0f;

					avgMax = Math.round((avgMin + avgMin * (0.50f)) * 100.0f) / 100.0f;
					// Math.round(avgMin + avgMin * (0.50f)) == avgMin ? (avgMin
					// + 1) : Math.round(avgMin + avgMin * (0.50f));

					/*
					 * { "minValue": "0", "maxValue": "50", "code": "#e44a00" },
					 * { "minValue": "50", "maxValue": "75", "code": "#f8bd19"
					 * }, { "minValue": "75", "maxValue": "100", "code":
					 * "#6baa01" }
					 */

					float minValue = Math.round((avgMin - avgMin * (0.25f)) * 100.0f) / 100.0f;
					// Math.round(avgMin - avgMin * (0.25f)) == avgMin ? (avgMin
					// - 1) : Math.round(avgMin - avgMin * (0.25f));
					List<Map<String, Object>> rangesMap = new ArrayList<Map<String, Object>>();
					Map<String, Object> ranges1 = new HashMap<String, Object>();
					ranges1.put("minValue", 0);
					ranges1.put("maxValue", minValue);
					ranges1.put("code", Constants.RED);

					Map<String, Object> ranges2 = new HashMap<String, Object>();
					ranges2.put("minValue", (minValue));
					ranges2.put("maxValue", avgMin);
					ranges2.put("code", Constants.YELLOW);

					Map<String, Object> ranges3 = new HashMap<String, Object>();
					ranges3.put("minValue", (avgMin));
					ranges3.put("maxValue", avgMax);
					ranges3.put("code", Constants.LIGHT_GREEN);

					Map<String, Object> ranges4 = new HashMap<String, Object>();
					ranges4.put("minValue", (avgMax));
					ranges4.put("maxValue", maxValue);
					ranges4.put("code", Constants.GREEN);

					rangesMap.add(ranges1);
					rangesMap.add(ranges2);
					rangesMap.add(ranges3);
					rangesMap.add(ranges4);

					int inActiveCount = userDtoList != null ? (userDtoList.size() - activeCount) : 0;

					listWithTotalSubmittalMap.put("range", rangesMap);
					listWithTotalSubmittalMap.put("maxValue", maxValue);
					listWithTotalSubmittalMap.put("level", totalStarts);
					listWithTotalSubmittalMap.put("dmName", userName);
					listWithTotalSubmittalMap.put("recList", userDtoList);
					listWithTotalSubmittalMap.put("Active_Count", activeCount);
					listWithTotalSubmittalMap.put("In_Active_Count", inActiveCount);
					listWithTotalSubmittalMap.put("total_Count", userDtoList == null ? 0 : userDtoList.size());
				}
			}
			// listWithTotalSubmittalMap.put("data", list);
			return new ResponseEntity<>(listWithTotalSubmittalMap, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/findHitRatio", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> findHitRatio(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
		if (Utils.getLoginUserId(request) != null) {
			Map<String, Object> listWithTotalSubmittalMap = new HashMap<String, Object>();
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setYears(Utils.getIntList_FromStr(reportwiseDto.getYear()));
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getWeek().trim().length() > 0 && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
				reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
			} else {
				reportwiseDto.setWeek(null);
			}
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
			}

			if (reportwiseDto.getDmName() != null && !reportwiseDto.getDmName().equalsIgnoreCase(Constants.ALL)) {
				List<String> userIds = userService.listAllADMsAndRec(Arrays.asList(reportwiseDto.getDmName()), null);
				reportwiseDto.setDmNames(Utils.getStrList_FromStr(reportwiseDto.getDmName()));
				reportwiseDto.getDmNames().addAll(userIds);
			}

			Map<String, Integer> map = (Map<String, Integer>) submittalService.findHitRatio(reportwiseDto);
			
			if (map != null) {
				Map<String, SubmittalDto>  submittalMap=submittalService.getInActiveStartedOfRecruiter(null, null, reportwiseDto.getDmName(), null, reportwiseDto,null);
				float inActiveStarts=0f,hitRatio=0f;
				if(submittalMap!=null){
					for(String key:submittalMap.keySet()){
						inActiveStarts+=Integer.parseInt(submittalMap.get(key).getInActiveStartedCount());
					}
				}
				List<Map<String, Object>> rangesMap = new ArrayList<Map<String, Object>>();
				Map<String, Object> ranges1 = new HashMap<String, Object>();
				ranges1.put("minValue", 0);
				ranges1.put("maxValue", Constants.MIN_STARTS_VAL);
				ranges1.put("code", Constants.RED);

				Map<String, Object> ranges2 = new HashMap<String, Object>();
				ranges2.put("minValue", (Constants.MIN_STARTS_VAL + 1));
				ranges2.put("maxValue", Constants.AVG_STARTS_MIN);
				ranges2.put("code", Constants.YELLOW);

				Map<String, Object> ranges3 = new HashMap<String, Object>();
				ranges3.put("minValue", (Constants.AVG_STARTS_MIN + 1));
				ranges3.put("maxValue", Constants.AVG_STARTS_MAX);
				ranges3.put("code", Constants.LIGHT_GREEN);

				Map<String, Object> ranges4 = new HashMap<String, Object>();
				ranges4.put("minValue", (Constants.AVG_STARTS_MAX + 1));
				ranges4.put("maxValue", Constants.MAX_STARTS_VAL);
				ranges4.put("code", Constants.GREEN);

				rangesMap.add(ranges1);
				rangesMap.add(ranges2);
				rangesMap.add(ranges3);
				rangesMap.add(ranges4);

				if(map.get(Constants.HIT_RATIO) != null){
					hitRatio=((Utils.getIntegerValueOfBigDecimalObj(map.get(Constants.TOTAL_STARTS)).floatValue()+inActiveStarts)/Utils.getIntegerValueOfBigDecimalObj(map.get(Constants.TOTAL_SUBMITTALS)).floatValue())*100f;
				}
				
				listWithTotalSubmittalMap.put("range", rangesMap);
				listWithTotalSubmittalMap.put("maxValue", Constants.MAX_STARTS_VAL);
				listWithTotalSubmittalMap.put(Constants.TOTAL_SUBMITTALS, map.get(Constants.TOTAL_SUBMITTALS));
				listWithTotalSubmittalMap.put("level",hitRatio);
			}
			// listWithTotalSubmittalMap.put("data", list);
			return new ResponseEntity<>(listWithTotalSubmittalMap, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getDMWiseRecOverallPerformanceReport", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getDMWiseRecOverallPerformanceReport(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
		if (Utils.getLoginUserId(request) != null) {
			Map<String, Object> listWithTotalSubmittalMap = new HashMap<String, Object>();
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getWeek().trim().length() > 0 && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
				reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
			} else {
				reportwiseDto.setWeek(null);
			}
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
			}

			if (reportwiseDto.getDmName() != null && !reportwiseDto.getDmName().equalsIgnoreCase(Constants.ALL)) {
				List<String> userIds = userService.listAllADMsAndRec(Arrays.asList(reportwiseDto.getDmName()), null);
				reportwiseDto.setDmNames(Utils.getStrList_FromStr(reportwiseDto.getDmName()));
				reportwiseDto.getDmNames().addAll(userIds);
			}

			List<SubmittalStatsDto> list = (List<SubmittalStatsDto>) submittalService.getDMWiseRecOverallPerformanceReport(reportwiseDto);
			listWithTotalSubmittalMap.put("data", list);
			if (list != null && list.size() > 0) {
				Map<String, Integer> submittalTotalsByStatus = new HashMap<String, Integer>();
				getTotalCount((List<SubmittalStatsDto>) list, submittalTotalsByStatus);
				listWithTotalSubmittalMap.put("total", submittalTotalsByStatus);
			}
			return new ResponseEntity<>(listWithTotalSubmittalMap, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getDMWiseJobOrderServiceReport", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getDMWiseJobOrderServiceReport(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
		if (Utils.getLoginUserId(request) != null) {
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getWeek().trim().length() > 0 && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
				reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
			} else {
				reportwiseDto.setWeek(null);
			}
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
			}

			if (reportwiseDto.getDmName() != null && !reportwiseDto.getDmName().equalsIgnoreCase(Constants.ALL)) {
				List<String> userIds = userService.listAllADMsAndRec(Arrays.asList(reportwiseDto.getDmName()), null);
				reportwiseDto.setDmNames(Utils.getStrList_FromStr(reportwiseDto.getDmName()));
				reportwiseDto.getDmNames().addAll(userIds);
			}

			Object obj = submittalService.getDMWiseJobOrderServiceReport(reportwiseDto);

			return new ResponseEntity<>(obj, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getSubmittal_Service_Of_All_Job_Orders_BY_DM", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getSubmittal_Service_Of_All_Job_Orders_BY_DM(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
		if (Utils.getLoginUserId(request) != null) {
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getWeek().trim().length() > 0 && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
				reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
			} else {
				reportwiseDto.setWeek(null);
			}
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
			}

			if (reportwiseDto.getDmName() != null && !reportwiseDto.getDmName().equalsIgnoreCase(Constants.ALL)) {
				List<String> userIds = userService.listAllADMsAndRec(Arrays.asList(reportwiseDto.getDmName()), null);
				reportwiseDto.setDmNames(Utils.getStrList_FromStr(reportwiseDto.getDmName()));
				reportwiseDto.getDmNames().addAll(userIds);
			}

			Object obj = submittalService.getSubmittal_Service_Of_All_Job_Orders_BY_DM(reportwiseDto);

			return new ResponseEntity<>(obj, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getDMAverageNoOfDaysForStatus", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getDMAverageNoOfDaysForStatus(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
		if (Utils.getLoginUserId(request) != null) {
			Map<String, Object> listWithTotalSubmittalMap = new HashMap<String, Object>();
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getWeek().trim().length() > 0 && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
				reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
			} else {
				reportwiseDto.setWeek(null);
			}
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
			}

			if (reportwiseDto.getDmName() != null && !reportwiseDto.getDmName().equalsIgnoreCase(Constants.ALL)) {
				List<String> userIds = userService.listAllADMsAndRec(Arrays.asList(reportwiseDto.getDmName()), null);
				reportwiseDto.setDmNames(Utils.getStrList_FromStr(reportwiseDto.getDmName()));
				reportwiseDto.getDmNames().addAll(userIds);
			}

			Map<String, Object> map = (Map<String, Object>) submittalService.getDMAverageNoOfDaysForStatus(reportwiseDto);

			List<Map<String, Object>> rangesMap = new ArrayList<Map<String, Object>>();
			Map<String, Object> ranges1 = new HashMap<String, Object>();
			ranges1.put("minValue", 0);
			ranges1.put("maxValue", Constants.MIN_VAL);
			ranges1.put("code", Constants.GREEN);

			Map<String, Object> ranges2 = new HashMap<String, Object>();
			ranges2.put("minValue", (Constants.MIN_VAL + 1));
			ranges2.put("maxValue", Constants.AVG_MIN);
			ranges2.put("code", Constants.LIGHT_GREEN);

			Map<String, Object> ranges3 = new HashMap<String, Object>();
			ranges3.put("minValue", (Constants.AVG_MIN + 1));
			ranges3.put("maxValue", Constants.AVG_MAX);
			ranges3.put("code", Constants.YELLOW);

			Map<String, Object> ranges4 = new HashMap<String, Object>();
			ranges4.put("minValue", (Constants.AVG_MAX + 1));
			ranges4.put("maxValue", Constants.MAX_VAL);
			ranges4.put("code", Constants.RED);

			rangesMap.add(ranges1);
			rangesMap.add(ranges2);
			rangesMap.add(ranges3);
			rangesMap.add(ranges4);

			listWithTotalSubmittalMap.put("range", rangesMap);
			listWithTotalSubmittalMap.put("maxValue", Constants.MAX_VAL);
			listWithTotalSubmittalMap.put("level", map.get(Constants.AVERAGE_DAYS) != null ? map.get(Constants.AVERAGE_DAYS) : 0);
			// listWithTotalSubmittalMap.put("dmName",
			// reportwiseDto.getDmName());
			return new ResponseEntity<>(listWithTotalSubmittalMap, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getAvgNoOfStatusesDaysByJobOrderId/{orderId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getAvgNoOfStatusesDaysByJobOrderId(HttpServletRequest request, @PathVariable("orderId") Integer orderId) {
		if (Utils.getLoginUserId(request) != null) {

			Object obj = submittalService.getAvgNoOfStatusesDaysByJobOrderId(orderId);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getSubmittalServiceDetailByOrderId_Status_CreatedBy", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getSubmittalServiceDetailByOrderId_Status_CreatedBy(HttpServletRequest request,
			@RequestParam("orderId") Integer orderId, @RequestParam("createdBy") String createdBy, @RequestParam("status") String status) {
		if (Utils.getLoginUserId(request) != null) {

			Object obj = submittalService.getSubmittalServiceDetailByOrderId_Status_CreatedBy(orderId, createdBy, status);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getAllDMsOpenAndClosedJobOrders", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getAllDMsOpenAndClosedJobOrders(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
		if (Utils.getLoginUserId(request) != null) {
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getWeek().trim().length() > 0 && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
				reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
			} else {
				reportwiseDto.setWeek(null);
			}
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
			}

			if (reportwiseDto.getDmName() != null && !reportwiseDto.getDmName().equalsIgnoreCase(Constants.ALL)) {
				List<String> userIds = userService.listAllADMsAndRec(Arrays.asList(reportwiseDto.getDmName()), null);
				reportwiseDto.setDmNames(Utils.getStrList_FromStr(reportwiseDto.getDmName()));
				reportwiseDto.getDmNames().addAll(userIds);
			}

			Object obj = jobOrderService.getAllDMsOpenAndClosedJobOrders(reportwiseDto);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getAllSubmittalsByOrderId/{orderId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getAllSubmittalsByOrderId(HttpServletRequest request, @PathVariable("orderId") Integer orderId) {
		if (Utils.getLoginUserId(request) != null) {
			List<SubmittalStatsDto> dtoList = (List<SubmittalStatsDto>) submittalService.getOrderWiseSubmittals(orderId);
			Map<String, Object> resultMap = new HashMap<String, Object>();
			if (dtoList != null && dtoList.size() > 0) {
				Map<String, Map<String, Integer>> userWithStatusCountMap = new LinkedHashMap<String, Map<String, Integer>>();
				Map<String, SubmittalStatsDto> userWithDtoMap = new LinkedHashMap<String, SubmittalStatsDto>();
				for (SubmittalStatsDto dto : dtoList) {
					userWithDtoMap.put(dto.getUserId(), dto);
					// userWithStatusCountMap.put(dto.getUserId(), )
					if (userWithStatusCountMap.get(dto.getUserId()) != null) {
						userWithStatusCountMap.get(dto.getUserId()).put(dto.getStatus(), dto.getCount());
					} else {
						Map<String, Integer> statusMap = new HashMap<String, Integer>();
						statusMap.put(dto.getStatus(), dto.getCount());
						userWithStatusCountMap.put(dto.getUserId(), statusMap);
					}
				}
				List<Object> resultList = new ArrayList<Object>();
				for (SubmittalStatus status : SubmittalStatus.values()) {
					Map<String, Object> map = new LinkedHashMap<String, Object>();
					map.put(Constants.NAME, status.name());
					map.put(Constants.COLOR, SubmittalColors.valueOf(status.name().toUpperCase()).getColor());
					List<Object> obj = new ArrayList<Object>();
					for (String key : userWithStatusCountMap.keySet()) {
						Integer count = userWithStatusCountMap.get(key).get(status.name()) != null ? userWithStatusCountMap.get(key).get(status.name()) : 0;
						Map<String, Object> dataMap = new HashMap<String, Object>();
						dataMap.put(Constants.Y, count);
						dataMap.put(Constants.COLOR, SubmittalColors.valueOf(status.name().toUpperCase()).getColor());
						obj.add(dataMap);
					}
					map.put(Constants.DATA, obj);
					resultList.add(map);
				}
				Collections.reverse(resultList);
				resultMap.put("series", resultList);
				resultMap.put("dms", userWithDtoMap.keySet());
				return new ResponseEntity<>(resultMap, HttpStatus.OK);
			}

			return new ResponseEntity<>(dtoList, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getSubmittalDetailByOrderId_Status_CreatedBy", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getSubmittalDetailByOrderId_Status_CreatedBy(HttpServletRequest request, @RequestParam("orderId") Integer orderId,
			@RequestParam("createdBy") String createdBy, @RequestParam(value = "status", required = false) String status) {
		if (Utils.getLoginUserId(request) != null) {
			List<SubmittalStatsDto> dtoList = (List<SubmittalStatsDto>) submittalService.getSubmittalDetailByOrderId_Status_CreatedBy(orderId, createdBy,
					status);
			return new ResponseEntity<>(dtoList, HttpStatus.OK);

		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getOther_Than_Started_Report", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getOther_Than_Started_Report(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
		if (Utils.getLoginUserId(request) != null) {
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getWeek().trim().length() > 0 && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
				reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
			} else {
				reportwiseDto.setWeek(null);
			}
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
			}

			if (reportwiseDto.getDmName() != null && !reportwiseDto.getDmName().equalsIgnoreCase(Constants.ALL)) {
				List<String> userIds = userService.listAllADMsAndRec(Arrays.asList(reportwiseDto.getDmName()), null);
				reportwiseDto.setDmNames(Utils.getStrList_FromStr(reportwiseDto.getDmName()));
				reportwiseDto.getDmNames().addAll(userIds);
			}

			Object obj = submittalService.getOther_Than_Started_Report(reportwiseDto);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getOpenOrClosedOrdersByDM", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getOpenOrClosedOrdersByDM(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
		if (Utils.getLoginUserId(request) != null) {
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getWeek().trim().length() > 0 && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
				reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
			} else {
				reportwiseDto.setWeek(null);
			}
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
			}

			if (reportwiseDto.getDmName() != null && !reportwiseDto.getDmName().equalsIgnoreCase(Constants.ALL)) {
				List<String> userIds = userService.listAllADMsAndRec(Arrays.asList(reportwiseDto.getDmName()), null);
				reportwiseDto.setDmNames(Utils.getStrList_FromStr(reportwiseDto.getDmName()));
				reportwiseDto.getDmNames().addAll(userIds);
			}

			Object obj = jobOrderService.getOpenOrClosedOrdersByDM(reportwiseDto);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	/*
	 * @RequestMapping(value = "/getAllClients", method = RequestMethod.POST,
	 * produces = "application/json") public @ResponseBody ResponseEntity<?>
	 * getAllClients(HttpServletRequest request, @RequestBody ReportwiseDto
	 * reportwiseDto) { if (Utils.getLoginUserId(request) != null) { if
	 * (reportwiseDto.getYear() != null) {
	 * reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$",
	 * "")); } if (reportwiseDto.getWeek() != null &&
	 * reportwiseDto.getWeek().trim().length() > 0 &&
	 * !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
	 * reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$",
	 * "").replaceAll("Week ", "")); } else { reportwiseDto.setWeek(null); } if
	 * (reportwiseDto.getMonth() != null &&
	 * !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
	 * reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll(
	 * "^,|,$", ""));
	 * reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()
	 * )); } if (reportwiseDto.getStatus() != null &&
	 * !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
	 * reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.
	 * getStatus())); }
	 * 
	 * if (reportwiseDto.getDmName() != null &&
	 * !reportwiseDto.getDmName().equalsIgnoreCase(Constants.ALL)) {
	 * List<String> userIds =
	 * userService.listAllADMsAndRec(Arrays.asList(reportwiseDto.getDmName()),
	 * null);
	 * reportwiseDto.setDmNames(Utils.getStrList_FromStr(reportwiseDto.getDmName
	 * ())); reportwiseDto.getDmNames().addAll(userIds); }
	 * 
	 * Object obj = jobOrderService.getAllClients(reportwiseDto); return new
	 * ResponseEntity<>(obj, HttpStatus.OK); } else { return new
	 * ResponseEntity<>(HttpStatus.FORBIDDEN); } }
	 */

	@RequestMapping(value = "/getAllTitlesByClient", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getAllTitlesByClient(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
		Map<String, Object> listWithTotalSubmittalMap = new HashMap<String, Object>();
		if (Utils.getLoginUserId(request) != null) {
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getWeek().trim().length() > 0 && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
				reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
			} else {
				reportwiseDto.setWeek(null);
			}
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
			}

			Object obj = jobOrderService.getAllTitlesByClient(reportwiseDto);
			listWithTotalSubmittalMap.put("data", obj);
			return new ResponseEntity<>(listWithTotalSubmittalMap, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getAllJobOrdersByTitle_Client", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getAllJobOrdersByTitle_Client(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {

		if (Utils.getLoginUserId(request) != null) {
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getWeek().trim().length() > 0 && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
				reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
			} else {
				reportwiseDto.setWeek(null);
			}
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
			}

			Object obj = jobOrderService.getAllJobOrdersByTitle_Client(reportwiseDto);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getAllClients", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getAllClients(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
		Map<String, Object> listWithTotalSubmittalMap = new HashMap<String, Object>();
		if (Utils.getLoginUserId(request) != null) {
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getWeek().trim().length() > 0 && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
				reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
			} else {
				reportwiseDto.setWeek(null);
			}
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
			}

			if (reportwiseDto.getName() != null && reportwiseDto.getName().trim().length() > 0) {
				reportwiseDto.setName(reportwiseDto.getName().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setNames(Utils.getStrList_FromStr(reportwiseDto.getName()));
			}

			Object obj = jobOrderService.getAllClients(reportwiseDto);
			listWithTotalSubmittalMap.put("data", obj);
			return new ResponseEntity<>(listWithTotalSubmittalMap, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getStartsByDateRange", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getStartsByDateRange(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
		if (reportwiseDto.getYear() != null) {
			reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
		}
		if (reportwiseDto.getWeek() != null && reportwiseDto.getWeek().trim().length() > 0 && !reportwiseDto.getWeek().trim().equalsIgnoreCase("null")) {
			reportwiseDto.setWeek(reportwiseDto.getWeek().trim().replaceAll("^,|,$", "").replaceAll("Week ", ""));
		} else {
			reportwiseDto.setWeek(null);
		}
		if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
			reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
			reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
		}
		if (reportwiseDto.getStatus() != null && !reportwiseDto.getStatus().equalsIgnoreCase(Constants.ALL)) {
			reportwiseDto.setStatuses(Utils.getStrList_FromStr(reportwiseDto.getStatus()));
		}

		if (reportwiseDto.getName() != null && reportwiseDto.getName().trim().length() > 0) {
			reportwiseDto.setName(reportwiseDto.getName().trim().replaceAll("^,|,$", ""));
			reportwiseDto.setNames(Utils.getStrList_FromStr(reportwiseDto.getName()));
		}

		Object obj = submittalService.getStartsByDateRange(reportwiseDto);
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	private void getTotalCount(List<SubmittalStatsDto> list, Map<String, Integer> submittalTotalsByStatus) {
		for (SubmittalStatsDto dto : list) {
			getCountOfEachStatus(SubmittalStatus.SUBMITTED.name(), submittalTotalsByStatus, dto.getSUBMITTED());
			getCountOfEachStatus(SubmittalStatus.DMREJ.name(), submittalTotalsByStatus, dto.getDMREJ());
			getCountOfEachStatus(SubmittalStatus.ACCEPTED.name(), submittalTotalsByStatus, dto.getACCEPTED());
			getCountOfEachStatus(SubmittalStatus.INTERVIEWING.name(), submittalTotalsByStatus, dto.getINTERVIEWING());
			getCountOfEachStatus(SubmittalStatus.CONFIRMED.name(), submittalTotalsByStatus, dto.getCONFIRMED());
			getCountOfEachStatus(SubmittalStatus.REJECTED.name(), submittalTotalsByStatus, dto.getREJECTED());
			getCountOfEachStatus(SubmittalStatus.STARTED.name(), submittalTotalsByStatus, dto.getSTARTED());
			getCountOfEachStatus(SubmittalStatus.BACKOUT.name(), submittalTotalsByStatus, dto.getBACKOUT());
			getCountOfEachStatus(SubmittalStatus.OUTOFPROJ.name(), submittalTotalsByStatus, dto.getOUTOFPROJ());
			getCountOfEachStatus(Constants.NOT_UPDATED, submittalTotalsByStatus, dto.getNotUpdated());
		}
	}

	private void getCountOfEachStatus(String status, Map<String, Integer> submittalTotalsByStatus, Integer count) {
		if (submittalTotalsByStatus.get(status) != null) {
			submittalTotalsByStatus.put(status, submittalTotalsByStatus.get(status) + count);
		} else {
			submittalTotalsByStatus.put(status, count);
		}
	}
	
	
	@RequestMapping(value = "/getCandidateSourceDetails", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> getCandidateSourceData (HttpServletRequest request,
			@RequestBody ReportwiseDto reportwiseDto){
		if (Utils.getLoginUserId(request) != null) {
			if (reportwiseDto.getYear() != null) {
				reportwiseDto.setYear(reportwiseDto.getYear().trim().replaceAll("^,|,$", ""));
			}
			
			if (reportwiseDto.getMonth() != null && !reportwiseDto.getMonth().equalsIgnoreCase(Constants.ALL)) {
				reportwiseDto.setMonth(reportwiseDto.getMonth().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setMonths(Utils.getStrList_FromStr(reportwiseDto.getMonth()));
			}
			
			if (reportwiseDto.getName() != null && reportwiseDto.getName().trim().length() > 0) {
				reportwiseDto.setName(reportwiseDto.getName().trim().replaceAll("^,|,$", ""));
				reportwiseDto.setNames(Utils.getStrList_FromStr(reportwiseDto.getName()));
			}
			
			Map<String, Object>  objMap = submittalService.getcandidateSourceDetails(reportwiseDto);
			
			return new ResponseEntity<>(objMap, HttpStatus.OK);
		}else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		
	}
}

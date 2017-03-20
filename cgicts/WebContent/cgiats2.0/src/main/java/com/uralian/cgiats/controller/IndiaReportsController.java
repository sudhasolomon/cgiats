package com.uralian.cgiats.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uralian.cgiats.dto.IndiaSubmittalStatsDto;
import com.uralian.cgiats.dto.ReportwiseDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.IndiaSubmittal;
import com.uralian.cgiats.model.JobOrderSearchDto;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.service.IndiaJobOrderService;
import com.uralian.cgiats.service.IndiaSubmittalService;
import com.uralian.cgiats.service.IndiaUserService;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.MonthEnum;
import com.uralian.cgiats.util.Utils;

@Controller
@RequestMapping("indiaReports")
public class IndiaReportsController {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired

	IndiaJobOrderService jobService;

	@Autowired
	private IndiaSubmittalService submittalService;

	@Autowired
	IndiaUserService userService;

	@RequestMapping(value = "/getJobOrderStats", method = RequestMethod.POST)
	public ResponseEntity<?> getJobOrderStats(HttpServletRequest request, @RequestBody Object obj)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		Map<String, Object> jobOrderStats = null;

		if (Utils.getLoginUserId(request) != null) {

			if (obj != null) {
				Date startDate = Utils.convertAngularStrToDate_India(((String) PropertyUtils.getProperty(obj, "startDate")));
				Date endDate = Utils.convertAngularStrToDate_India(((String) PropertyUtils.getProperty(obj, "endDate")));
				User user = userService.loadUser(Utils.getLoginUserId(request));
				jobOrderStats = jobService.getStatsByUser(user, startDate, endDate, true);
			} else {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}

		}
		return new ResponseEntity<Map<String, Object>>(jobOrderStats, HttpStatus.OK);
	}

	@RequestMapping(value = "/getSubmitalStats", method = RequestMethod.POST)
	public ResponseEntity<?> getSubmitalStats(HttpServletRequest request, @RequestBody Object obj)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		System.out.println("in controller" + obj);

		Map<String, Object> submittalStatusMap = new HashMap<String, Object>();
		Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser = new HashMap<String, Map<SubmittalStatus, Integer>>();
		if (Utils.getLoginUserId(request) != null) {

			if (obj != null) {
				Date startDate = Utils.convertAngularStrToDate_India((String) PropertyUtils.getProperty(obj, "startDate"));
				Date endDate = Utils.convertAngularStrToDate_India((String) PropertyUtils.getProperty(obj, "endDate"));

				Calendar calendar = Calendar.getInstance();
				calendar.setTime(endDate);
				calendar.set(Calendar.HOUR, 23);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 59);
				endDate = calendar.getTime();

				String officeLocation = (String) PropertyUtils.getProperty(obj, "officeLocation");
				if (officeLocation != null && officeLocation.trim().equalsIgnoreCase(Constants.ALL)) {
					officeLocation = null;
				}

				Map<String, Integer> statusNotUpdated = new HashMap<String, Integer>();
				Date submittalFrom = startDate;
				Date submittalTo = endDate;

				submittalStatusByUser = jobService.getIndiaSubmittalStatusByLocation(Utils.getLoginUser(request), officeLocation, submittalFrom, submittalTo);
				String assignedBdm = null, bdmLocation = null;
				Map<String, String> assignedBdms = new HashMap<String, String>();
				Map<String, String> bdmLocations = new HashMap<String, String>();
				Map<String, String> fullNames = new HashMap<String, String>();
				List<String> submittalBdms = new ArrayList<String>(submittalStatusByUser.keySet());
				List<UserDto> userDtoList = userService.getUsersInfoByIds(submittalBdms);

				for (UserDto userDto : userDtoList) {
					assignedBdm = userDto.getAssignedBdm();
					bdmLocation = userDto.getOfficeLocation();
					if (assignedBdm == null) {
						assignedBdm = "";
					}
					if (bdmLocation == null) {
						bdmLocation = "";
					}
					assignedBdms.put(userDto.getUserId(), assignedBdm);
					bdmLocations.put(userDto.getUserId(), bdmLocation);
					fullNames.put(userDto.getUserId(), userDto.getFullName());
				}
				Map<String, Integer> submittalTotalsByUser = new HashMap<String, Integer>();
				HashMap<SubmittalStatus, Integer> submittalTotalsByStatus = new HashMap<SubmittalStatus, Integer>();
				SubmittalStatus status = null;
				List<IndiaSubmittalStatsDto> statsDtoList = new ArrayList<IndiaSubmittalStatsDto>();
				for (String user : submittalStatusByUser.keySet()) {
					Map<SubmittalStatus, Integer> map = submittalStatusByUser.get(user);
					IndiaSubmittalStatsDto statsDto = new IndiaSubmittalStatsDto();

					int totalByUser = 0, nuTotal = 0, statusCount = 0;
					for (Map.Entry<SubmittalStatus, Integer> entry : map.entrySet()) {
						status = entry.getKey();
						Integer count = entry.getValue();
						Integer oldCount = submittalTotalsByStatus.get(status);
						int newCount = oldCount != null ? oldCount + count : count;

						totalByUser += count;
						if (!status.equals(SubmittalStatus.SUBMITTED)) {
							statusCount += count;
							submittalTotalsByStatus.put(status, newCount);
						}
					}

					statsDto.setFullName(fullNames.get(user));
					statsDto.setName(user);

					submittalTotalsByUser.put(user, totalByUser);
					statusNotUpdated.put(user, totalByUser - statusCount);
					nuTotal = totalByUser - statusCount;
					if (submittalTotalsByStatus.get(SubmittalStatus.SUBMITTED) != null) {
						submittalTotalsByStatus.put(SubmittalStatus.SUBMITTED, submittalTotalsByStatus.get(SubmittalStatus.SUBMITTED) + totalByUser);
					} else {
						submittalTotalsByStatus.put(SubmittalStatus.SUBMITTED, totalByUser);
					}
					statsDto.setSUBMITTED(totalByUser);
					statsDto.setNotUpdated(nuTotal);
					statsDto.setACCEPTED(map.get(SubmittalStatus.ACCEPTED));
					statsDto.setCONFIRMED(map.get(SubmittalStatus.CONFIRMED));
					statsDto.setDMREJ(map.get(SubmittalStatus.DMREJ));
					statsDto.setINTERVIEWING(map.get(SubmittalStatus.INTERVIEWING));
					statsDto.setREJECTED(map.get(SubmittalStatus.REJECTED));
					statsDto.setSTARTED(map.get(SubmittalStatus.STARTED));
					statsDto.setBACKOUT(map.get(SubmittalStatus.BACKOUT));
					statsDto.setOUTOFPROJ(map.get(SubmittalStatus.OUTOFPROJ));
					statsDto.setDM(assignedBdms.get(user));
					statsDto.setLocation(bdmLocations.get(user));
					statsDtoList.add(statsDto);

				}
				submittalStatusMap.put("submittalStatsData", statsDtoList);
				submittalStatusMap.put("submittalTotalsByStatus", submittalTotalsByStatus);

			} else {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}

		}
		return new ResponseEntity<Map<String, Object>>(submittalStatusMap, HttpStatus.OK);
	}

	@SuppressWarnings("null")
	@RequestMapping(value = "/getSubmittalDetails", method = RequestMethod.POST)
	public ResponseEntity<?> getSubmittalDetails(HttpServletRequest request, @RequestBody Object obj)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> submittalMap = new HashMap<String, Object>();
		List<IndiaSubmittal> submittalDetails = new ArrayList<>();
		List<SubmittalDto> submittalDtoList = new ArrayList<SubmittalDto>();
		JobOrderSearchDto criteria = new JobOrderSearchDto();
		if (Utils.getLoginUserId(request) != null) {
			if (obj != null) {
				Date startDate = Utils.convertStringToDate_India((String) PropertyUtils.getProperty(obj, "startEntryDate"));
				Date endDate = Utils.convertStringToDate_India((String) PropertyUtils.getProperty(obj, "endEntryDate"));
				String userName = (String) PropertyUtils.getProperty(obj, "submittalBdms");
				criteria.setSubmittalBdms(userName);
				criteria.setStartEntryDate(startDate);
				criteria.setEndEntryDate(endDate);
				submittalDetails = submittalService.findSubmittals(criteria);
				Iterator<IndiaSubmittal> iterator = submittalDetails.iterator();
				while (iterator.hasNext()) {
					IndiaSubmittal indiaSubmittal = iterator.next();

					SubmittalDto dto = new SubmittalDto();
					dto.setSubmittalId(indiaSubmittal.getId().toString());
					dto.setCandidateName(indiaSubmittal.getCandidate().getFirstName() + " " + indiaSubmittal.getCandidate().getLastName());
					dto.setCreatedBy(indiaSubmittal.getCreatedBy() != null ? indiaSubmittal.getCreatedBy() : "");
					dto.setCreatedOn(Utils.convertDateToString_HH_MM_A_India(indiaSubmittal.getCreatedOn()));
					dto.setUpdatedOn(Utils.convertDateToString_HH_MM_A_India(indiaSubmittal.getCreatedDate()));
					dto.setStatus(indiaSubmittal.getStatus() != null ? indiaSubmittal.getStatus().toString() : "");

					submittalDtoList.add(dto);
				}
				submittalMap.put("submittalDetails", submittalDtoList);
			} else {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}

		}
		return new ResponseEntity<Map<String, Object>>(submittalMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/getClientwiseSubmitalStats", method = RequestMethod.POST)
	public ResponseEntity<?> getClientWiseSubmitalStats(HttpServletRequest request, @RequestBody Object obj)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (Utils.getLoginUserId(request) != null) {
			Map<String, Object> clientWiseSubmittalStatusMap = new HashMap<String, Object>();
			Map<String, Map<SubmittalStatus, Integer>> clientWiseSubmittalStatus = new HashMap<String, Map<SubmittalStatus, Integer>>();
			Map<String, String> fullNames = new HashMap<String, String>();
			if (obj != null) {
				Date strDate = Utils.convertAngularStrToDate_India((String) PropertyUtils.getProperty(obj, "startDate"));
				Date endDate = Utils.convertAngularStrToDate_India((String) PropertyUtils.getProperty(obj, "endDate"));
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(endDate);
				calendar.set(Calendar.HOUR, 23);
				calendar.set(Calendar.MINUTE, 59);
				calendar.set(Calendar.SECOND, 59);
				endDate = calendar.getTime();
				System.out.println("StrDate " + strDate + "  " + "EndDate " + endDate);
				Map<String, Integer> statusNotUpdated = new HashMap<String, Integer>();
				clientWiseSubmittalStatus = jobService.getClientwiseSubmittalStats(strDate, endDate);

				Map<String, Integer> submittalTotalsByUser = new HashMap<String, Integer>();
				HashMap<SubmittalStatus, Integer> submittalTotalsByStatus = new HashMap<SubmittalStatus, Integer>();
				SubmittalStatus status = null;
				List<IndiaSubmittalStatsDto> statsDtoList = new ArrayList<IndiaSubmittalStatsDto>();
				for (String user : clientWiseSubmittalStatus.keySet()) {
					Map<SubmittalStatus, Integer> map = clientWiseSubmittalStatus.get(user);
					IndiaSubmittalStatsDto statsDto = new IndiaSubmittalStatsDto();

					int totalByUser = 0, nuTotal = 0, statusCount = 0;
					for (Map.Entry<SubmittalStatus, Integer> entry : map.entrySet()) {
						status = entry.getKey();
						Integer count = entry.getValue();
						Integer oldCount = submittalTotalsByStatus.get(status);
						int newCount = oldCount != null ? oldCount + count : count;

						totalByUser += count;
						if (!status.equals(SubmittalStatus.SUBMITTED)) {
							statusCount += count;
							submittalTotalsByStatus.put(status, newCount);
						}
					}

					statsDto.setFullName(fullNames.get(user));
					statsDto.setName(user);

					submittalTotalsByUser.put(user, totalByUser);
					statusNotUpdated.put(user, totalByUser - statusCount);
					nuTotal = totalByUser - statusCount;
					if (submittalTotalsByStatus.get(SubmittalStatus.SUBMITTED) != null) {
						submittalTotalsByStatus.put(SubmittalStatus.SUBMITTED, submittalTotalsByStatus.get(SubmittalStatus.SUBMITTED) + totalByUser);
					} else {
						submittalTotalsByStatus.put(SubmittalStatus.SUBMITTED, totalByUser);
					}
					statsDto.setSUBMITTED(totalByUser);
					statsDto.setNotUpdated(nuTotal);
					statsDto.setACCEPTED(map.get(SubmittalStatus.ACCEPTED));
					statsDto.setCONFIRMED(map.get(SubmittalStatus.CONFIRMED));
					statsDto.setDMREJ(map.get(SubmittalStatus.DMREJ));
					statsDto.setINTERVIEWING(map.get(SubmittalStatus.INTERVIEWING));
					statsDto.setREJECTED(map.get(SubmittalStatus.REJECTED));
					statsDto.setSTARTED(map.get(SubmittalStatus.STARTED));
					statsDto.setBACKOUT(map.get(SubmittalStatus.BACKOUT));
					statsDto.setOUTOFPROJ(map.get(SubmittalStatus.OUTOFPROJ));
					statsDtoList.add(statsDto);

				}
				clientWiseSubmittalStatusMap.put("submittalStatsData", statsDtoList);
				clientWiseSubmittalStatusMap.put("submittalTotalsByStatus", submittalTotalsByStatus);

			}
			return new ResponseEntity<>(clientWiseSubmittalStatusMap, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getDmSummaryReport", method = RequestMethod.POST)
	public ResponseEntity<?> getDmSummaryReport(HttpServletRequest request, @RequestBody Object obj)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (Utils.getLoginUserId(request) != null) {
			Map<String, Object> dmSummaryReportMap = new HashMap<String, Object>();
			System.out.println("obj" + obj);
			Date startDate = Utils.convertAngularStrToDate_India((String) PropertyUtils.getProperty(obj, "startDate"));
			Date endDate = Utils.getEndDate(Utils.convertDateToString(Utils.convertAngularStrToDate_India((String) PropertyUtils.getProperty(obj, "endDate"))));

			System.out.println("StrDate " + startDate + "  " + "EndDate " + endDate);
			dmSummaryReportMap = jobService.getDmSummaryReport(startDate, endDate);

			return new ResponseEntity<>(dmSummaryReportMap, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}
	
	@RequestMapping(value = "/getUserSubmittalsById", method = RequestMethod.POST)
	public ResponseEntity<?> getUserSubmittalsById(HttpServletRequest request, @RequestBody Object obj) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		if(Utils.getLoginUserId(request)!=null){
			try {
				Date startDate = Utils.convertAngularStrToDate_India((String) PropertyUtils.getProperty(obj, "startDate"));
				Date endDate = Utils.getEndDate(Utils.convertDateToString(Utils.convertAngularStrToDate_India((String) PropertyUtils.getProperty(obj, "endDate"))));
	            String user_id = (String) PropertyUtils.getProperty(obj, "userId");
	            String status = (String) PropertyUtils.getProperty(obj, "status");
	            //System.out.println(obj);
	            List<?> list = jobService.getUserSubmittalsById(user_id,status,startDate,endDate);
	            return new ResponseEntity<>(list,HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}else{
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getIndiaDmMetricData", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> getIndiaDmMetricData(HttpServletRequest request, @RequestBody ReportwiseDto reportwiseDto) {
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
			Integer backoutCount = 0;
			if (reportwiseDto.getDmNames() != null && reportwiseDto.getDmNames().size() > 0) {
				reportwiseDto.setStatuses(Utils.getStrList_FromStr(Constants.BACKOUT));
				Map<String, Integer> backoutmap = (Map<String, Integer>) submittalService.getDMWiseRecPerformanceTotalReport(reportwiseDto);
				for (String key : backoutmap.keySet()) {
					backoutCount += backoutmap.get(key);
				}
			}
			Map<String, SubmittalDto>  submittalMap =submittalService.getInActiveStartedOfRecruiter(null, null, reportwiseDto.getDmName(), null, reportwiseDto,null);
			
			if(map != null){

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
					float totalAvgvalue = 0f;
					float avgMin = 0f, maxValue = 0f, avgMax = 0f, avgValue = 0f;
					String userName = null;
					int activeCount = 0;
					for (UserDto dto : userDtoList) {
						if (dto.getStatus().equalsIgnoreCase(Constants.ACTIVE)) {
							activeCount++;
						}

						totalMinvalue = 0;
						totalMaxvalue = 0;
						totalAvgvalue = 0;
						if (dto.getUserId().equalsIgnoreCase(reportwiseDto.getDmName())) {
							userName = dto.getFullName();
						}
						dto.setNoOfStarts(map.get(dto.getUserId()) == null ? 0 : map.get(dto.getUserId()));
						if (reportwiseDto.getDmNames() != null && reportwiseDto.getDmNames().size() > 0) {
							dto.setNoOfBackOuts(backoutCount);
						}
						dto.setNoOfActiveStarts(dto.getNoOfStarts());
						dto.setNoOfInActiveStarts(0);
						if(submittalMap!=null && submittalMap.get(dto.getUserId())!=null){
							dto.setNoOfInActiveStarts(Integer.parseInt(submittalMap.get(dto.getUserId()).getInActiveStartedCount()));
							dto.setNoOfStarts(dto.getNoOfActiveStarts()+dto.getNoOfInActiveStarts());
							totalStarts+=dto.getNoOfInActiveStarts();
						}
						int noOfMonthsDone = 0;
						if (!dto.getUserRole().equals(UserRole.IN_DM)) {
							for (int i = 0; i < startDates.size(); i++) {
								if (dto.getJoinDate().compareTo(startDates.get(i)) <= 0 && dto.getServedDate().compareTo(endDates.get(i)) >= 0) {
									totalMinvalue += (dto.getMinStartCount() / 12f);
									totalMaxvalue += (dto.getMaxStartCount() / 12f);
									totalAvgvalue += (dto.getAvgStartCount() / 12f);
									
									noOfMonthsDone++;
								}
							}
							dto.setAvgHires(Utils.getTwoDecimalDoubleFromObj(Double.valueOf(dto.getNoOfStarts()) / Double.valueOf(noOfMonthsDone)));

							if (dto.getAvgHires() < (Double.valueOf((dto.getMinStartCount() / 12f)))) {
								dto.setStatusValue(1);
							} else if (dto.getAvgHires() >= (Double.valueOf((dto.getMinStartCount() / 12f)))
									&& dto.getAvgHires() < Double.valueOf(dto.getAvgStartCount() / 12f)) {
								dto.setStatusValue(2);
							} else if (dto.getAvgHires() >= Double.valueOf(dto.getAvgStartCount() / 12f)
									&& dto.getAvgHires() < (Double.valueOf((dto.getMaxStartCount() / 12f)))) {
								dto.setStatusValue(3);
							} else if (dto.getAvgHires() >= (Double.valueOf((dto.getMaxStartCount() / 12f)))) {
								dto.setStatusValue(4);
							}

							dto.setNoOfMonthsWorked(noOfMonthsDone);
							avgMin += totalMinvalue;
							maxValue += totalMaxvalue;
							avgValue += totalAvgvalue;
						}
					}
					avgMin = Math.round(avgMin * 100.0f) / 100.0f;
					maxValue = Math.round(maxValue * 100.0f) / 100.0f;
					avgValue = Math.round(avgValue * 100.0f) / 100.0f;
					
					/*avgMin = Math.round(avgMin * 100.0f) / 100.0f;
					maxValue = Math.round(maxValue * 100.0f) / 100.0f;
					

					avgMax = Math.round((avgMin + avgMin * (0.50f)) * 100.0f) / 100.0f;*/
					// Math.round(avgMin + avgMin * (0.50f)) == avgMin ? (avgMin
					// + 1) : Math.round(avgMin + avgMin * (0.50f));

					/*
					 * { "minValue": "0", "maxValue": "50", "code": "#e44a00" },
					 * { "minValue": "50", "maxValue": "75", "code": "#f8bd19"
					 * }, { "minValue": "75", "maxValue": "100", "code":
					 * "#6baa01" }
					 */

					float abvMaxValue = Math.round(((maxValue + maxValue) *(100.0f))/100.0f);
					//float minValue = Math.round((avgMin - avgMin * (0.25f)) * 100.0f) / 100.0f;
					// Math.round(avgMin - avgMin * (0.25f)) == avgMin ? (avgMin
					// - 1) : Math.round(avgMin - avgMin * (0.25f));
					List<Map<String, Object>> rangesMap = new ArrayList<Map<String, Object>>();
					Map<String, Object> ranges1 = new HashMap<String, Object>();
					ranges1.put("minValue", 0);
					ranges1.put("maxValue", avgMin);
					ranges1.put("code", Constants.RED);

					Map<String, Object> ranges2 = new HashMap<String, Object>();
					ranges2.put("minValue", (avgMin));
					ranges2.put("maxValue", avgValue);
					ranges2.put("code", Constants.YELLOW);

					Map<String, Object> ranges3 = new HashMap<String, Object>();
					ranges3.put("minValue", (avgValue));
					ranges3.put("maxValue", maxValue);
					ranges3.put("code", Constants.LIGHT_GREEN);

					Map<String, Object> ranges4 = new HashMap<String, Object>();
					ranges4.put("minValue", (maxValue));
					ranges4.put("maxValue", abvMaxValue);
					ranges4.put("code", Constants.GREEN);

					rangesMap.add(ranges1);
					rangesMap.add(ranges2);
					rangesMap.add(ranges3);
					rangesMap.add(ranges4);

					int inActiveCount = userDtoList != null ? (userDtoList.size() - activeCount) : 0;

					listWithTotalSubmittalMap.put("range", rangesMap);
					listWithTotalSubmittalMap.put("maxValue", abvMaxValue);
					listWithTotalSubmittalMap.put("level", totalStarts);
					listWithTotalSubmittalMap.put("dmName", userName);
					listWithTotalSubmittalMap.put("recList", userDtoList);
					listWithTotalSubmittalMap.put("Active_Count", activeCount);
					listWithTotalSubmittalMap.put("In_Active_Count", inActiveCount);
					listWithTotalSubmittalMap.put("total_Count", userDtoList == null ? 0 : userDtoList.size());
				}
			
			}
			
			
			return new ResponseEntity<>(listWithTotalSubmittalMap, HttpStatus.OK);

		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getIndiaDMAverageNoOfDaysForStatus", method = RequestMethod.POST, produces = "application/json")
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

			Map<String, Object> map = (Map<String, Object>) submittalService.getIndiaDMAverageNoOfDaysForStatus(reportwiseDto);

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

	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/indiadmfindHitRatio", method = RequestMethod.POST, produces = "application/json")
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

			Map<String, Integer> map = (Map<String, Integer>) submittalService.indiadmfindHitRatio(reportwiseDto);

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
	
	
	@RequestMapping(value = "/getIndiaDmCustomReportData", method = RequestMethod.POST)
	public ResponseEntity<?> getIndiaDmsCustomReport(HttpServletRequest request, @RequestBody Map<String, String> map) {
		if (Utils.getLoginUserId(request) != null) {
			String status = map.get("status");
			if (status != null && status.trim().equalsIgnoreCase(Constants.ALL)) {
				status = null;
			}
			if (map.get("startDate") != null && map.get("endDate") != null) {
				Object dto = submittalService.getIndiadmsDetailsReport(Utils.convertStringToDate(map.get("startDate")), Utils.getEndDate(map.get("endDate")),
						map.get("dmName"), status);
				return new ResponseEntity<>(dto, HttpStatus.OK);
			}
			return new ResponseEntity<>(null, HttpStatus.OK);

		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	
	@RequestMapping(value = "/getIndiaClientReportData", method = RequestMethod.POST)
	public ResponseEntity<?> getIndiaClientReportData(HttpServletRequest request,
			@RequestBody Map<String, String> map){
		if (Utils.getLoginUserId(request) != null) {
			Map<String, Object> dto = new HashMap<String, Object>();
			if (map.get("startDate") != null && map.get("endDate") != null) {
				dto = submittalService.getIndiaClientReportData(Utils.convertStringToDate(map.get("startDate")), Utils.getEndDate(map.get("endDate")));
			}
		
		return new ResponseEntity<>(dto,HttpStatus.OK);
		
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	
	
	@RequestMapping(value = "/getIndiaClientDetailsReportData", method = RequestMethod.POST)
	public ResponseEntity<?> getIndiaClientDetialsReportData(HttpServletRequest request,
			@RequestBody Map<String, String> map){
		if (Utils.getLoginUserId(request) != null) {
			Map<String, Object> dto = new HashMap<String, Object>();
			if (map.get("startDate") != null && map.get("endDate") != null) {
				dto = submittalService.getIndiaClientDetialsReportData(Utils.convertStringToDate(map.get("startDate")), Utils.getEndDate(map.get("endDate")),
						map.get("client"));
			}
		
		return new ResponseEntity<>(dto,HttpStatus.OK);
		
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
}

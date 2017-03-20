package com.uralian.cgiats.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uralian.cgiats.dto.DashboardSearchDto;
import com.uralian.cgiats.dto.SubmittalStatsDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.JobOrderSearchDto;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.service.JobOrderService;
import com.uralian.cgiats.service.SubmittalService;
import com.uralian.cgiats.service.UserService;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.Utils;

@RestController
@RequestMapping("submittalStatsController")
public class SubmittalStatsController {
	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private UserService userService;

	@Autowired
	private JobOrderService orderService;

	@Autowired
	private SubmittalService submittalService;

	@RequestMapping(value = "/getSubmittalStatsReport", method = RequestMethod.POST)
	public ResponseEntity<?> getSubmittalStatsReport(@RequestBody DashboardSearchDto dashboardSearchDto,
			HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			List<Object> cityWithStatusWithTotalList = new ArrayList<Object>();
			try {

				String loginUserId = null;
				if (dashboardSearchDto.getIsAuthRequired() != null && dashboardSearchDto.getIsAuthRequired() && (dashboardSearchDto.getStrDMOrAdms() == null || 
						dashboardSearchDto.getStrDMOrAdms().trim().length() == 0)) {
					UserDto userDto = Utils.getLoginUser(request);
					if (userDto.getUserRole().equals(UserRole.DM) || userDto.getUserRole().equals(UserRole.ADM)
							|| userDto.getUserRole().equals(UserRole.Recruiter)) {
							loginUserId = userDto.getUserId();
							if(userDto.getUserRole().equals(UserRole.Recruiter)){
								loginUserId = userDto.getAssignedBdm();
							}
						dashboardSearchDto.setStrDMOrAdms(loginUserId);
					}
				}
				
				if (dashboardSearchDto != null && dashboardSearchDto.getUserStatus() != null
						&& dashboardSearchDto.getUserStatus().equalsIgnoreCase(Constants.ALL)) {
					dashboardSearchDto.setUserStatus(null);
				}

				if (dashboardSearchDto.getStrOfficeLocations() != null
						&& dashboardSearchDto.getStrOfficeLocations().length() > 0) {
					dashboardSearchDto.setOfficeLocations(
							Arrays.asList(Utils.getStrArray_FromStr(dashboardSearchDto.getStrOfficeLocations())));
				}
				if (dashboardSearchDto.getStrDMOrAdms() != null && dashboardSearchDto.getStrDMOrAdms().length() > 0) {
					dashboardSearchDto.setDmOrAdms(Utils.getStrList_FromStr(dashboardSearchDto.getStrDMOrAdms()));
					dashboardSearchDto.getDmOrAdms()
							.addAll(userService.listAllDMSAndADMsAndRecByLocAndUserIds(
									Arrays.asList(Utils.getStrArray_FromStr(dashboardSearchDto.getStrDMOrAdms())),
									dashboardSearchDto.getOfficeLocations(), dashboardSearchDto.getUserStatus()));
				} else {
					dashboardSearchDto.setDmOrAdms(userService.listAllDMSAndADMsAndRecByLocAndUserIds(null,
							dashboardSearchDto.getOfficeLocations(), dashboardSearchDto.getUserStatus()));
				}
				Map<String, Map<SubmittalStatus, Integer>> submittalStatusByUser = orderService
						.getSubmittalStatusByLocation(Utils.getLoginUser(request),
								dashboardSearchDto.getOfficeLocations(), dashboardSearchDto.getDmOrAdms(),
								Utils.convertAngularStrToDate(dashboardSearchDto.getStartDate()),
								Utils.getEndDate(dashboardSearchDto.getEndDate()), dashboardSearchDto.getUserStatus());
				if (submittalStatusByUser != null) {
					Map<String, Map<SubmittalStatus, Integer>> finalSubmittalStatusByUser = new HashMap<String, Map<SubmittalStatus, Integer>>();
					List<String> submittalBdms = new ArrayList<String>(submittalStatusByUser.keySet());
					if (dashboardSearchDto.getStrDMOrAdms() != null
							&& dashboardSearchDto.getStrDMOrAdms().length() > 0) {
						if ((dashboardSearchDto.getOfficeLocations() != null
								&& dashboardSearchDto.getOfficeLocations().size() > 0)) {
							submittalBdms.addAll(userService.listAllDMSAndADMsAndRecByLocAndUserIds(
									Utils.convertStringToArrayList(dashboardSearchDto.getStrDMOrAdms()),
									dashboardSearchDto.getOfficeLocations(), dashboardSearchDto.getUserStatus()));
						} else {
							Boolean status = (dashboardSearchDto.getUserStatus() != null
									&& dashboardSearchDto.getUserStatus().equals(Constants.ACTIVE)) ? true : false;
							submittalBdms.addAll(
									Arrays.asList(Utils.getStrArray_FromStr(dashboardSearchDto.getStrDMOrAdms())));
							submittalBdms.addAll(userService
									.listAllADMsAndRec(Arrays.asList(dashboardSearchDto.getStrDMOrAdms()), status));
						}
					} else if (dashboardSearchDto.getDmOrAdms() != null
							&& dashboardSearchDto.getDmOrAdms().size() > 0) {
						submittalBdms.addAll(dashboardSearchDto.getDmOrAdms());
					}
					Map<String, UserDto> userWithIdMap = new HashMap<String, UserDto>();
					List<UserDto> userDtoList = null;
					if (submittalBdms != null && submittalBdms.size() > 0) {
						userDtoList = userService.getUsersInfoByIds(submittalBdms);
					}
					if (userDtoList != null && userDtoList.size() > 0) {
						for (UserDto userDto : userDtoList) {

							if (userDto.getUserRole().equals(UserRole.DM) || userDto.getUserRole().equals(UserRole.ADM)
									|| userDto.getUserRole().equals(UserRole.Recruiter)) {
								if (submittalStatusByUser.get(userDto.getUserId()) == null) {
									finalSubmittalStatusByUser.put(userDto.getUserId(),
											new HashMap<SubmittalStatus, Integer>());
								} else {
									finalSubmittalStatusByUser.put(userDto.getUserId(),
											submittalStatusByUser.get(userDto.getUserId()));
								}
								userWithIdMap.put(userDto.getUserId(), userDto);
							}
						}
					}

					SubmittalStatus status = null;

					Map<String, List<Entry<String, Map<SubmittalStatus, Integer>>>> cityWithUserSubmittalStatusMap = new HashMap<String, List<Entry<String, Map<SubmittalStatus, Integer>>>>();

					for (Entry<String, Map<SubmittalStatus, Integer>> entry : finalSubmittalStatusByUser.entrySet()) {
						String city = userWithIdMap.get(entry.getKey()).getOfficeLocation() != null
								? userWithIdMap.get(entry.getKey()).getOfficeLocation() : "";
						if (cityWithUserSubmittalStatusMap.get(city) != null) {
							cityWithUserSubmittalStatusMap.get(city).add(entry);
						} else {
							List<Entry<String, Map<SubmittalStatus, Integer>>> userWithStatusMap = new ArrayList<Entry<String, Map<SubmittalStatus, Integer>>>();
							userWithStatusMap.add(entry);
							cityWithUserSubmittalStatusMap.put(city, userWithStatusMap);
						}
					}

					Map<String, List<SubmittalStatsDto>> cityWithUserSubmittalCountStatusMap = new TreeMap<String, List<SubmittalStatsDto>>();
					Map<String, Map<String, Integer>> cityWithTotalCountMap = new HashMap<String, Map<String, Integer>>();

					for (String city : cityWithUserSubmittalStatusMap.keySet()) {
						if (city != null && city.trim().length() > 0) {
							Map<String, Integer> submittalTotalsByStatus = new HashMap<String, Integer>();
							List<Entry<String, Map<SubmittalStatus, Integer>>> userWithStatusMap = cityWithUserSubmittalStatusMap
									.get(city);
							for (Entry<String, Map<SubmittalStatus, Integer>> userWithStatus : userWithStatusMap) {
								// for (String user : userWithStatus.keySet()) {
								SubmittalStatsDto dto = new SubmittalStatsDto();
								Map<SubmittalStatus, Integer> map = userWithStatus.getValue();
								int totalByUser = 0, nuTotal = 0, statusCount = 0;
								for (Map.Entry<SubmittalStatus, Integer> entry : map.entrySet()) {
									status = entry.getKey();
									Integer count = entry.getValue();
									Integer oldCount = submittalTotalsByStatus.get(status.name());
									int newCount = oldCount != null ? oldCount + count : count;

									totalByUser += count;
									// submittalTotal += count;
									if (!status.equals(SubmittalStatus.SUBMITTED)) {
										statusCount += count;
										submittalTotalsByStatus.put(status.name(), newCount);
									}
								}
								nuTotal = totalByUser - statusCount;
								if (submittalTotalsByStatus.get(SubmittalStatus.SUBMITTED.name()) != null) {
									submittalTotalsByStatus.put(SubmittalStatus.SUBMITTED.name(),
											submittalTotalsByStatus.get(SubmittalStatus.SUBMITTED.name())
													+ totalByUser);
								} else {
									submittalTotalsByStatus.put(SubmittalStatus.SUBMITTED.name(), totalByUser);
								}
								if (submittalTotalsByStatus.get(Constants.NOT_UPDATED) != null) {
									submittalTotalsByStatus.put(Constants.NOT_UPDATED,
											submittalTotalsByStatus.get(Constants.NOT_UPDATED) + nuTotal);
								} else {
									submittalTotalsByStatus.put(Constants.NOT_UPDATED, nuTotal);
								}

								// dto.setName(userIdWithUserMap.get(user).getFullName());
								// dto.setUserId(user);
								// dto.setLocation(userIdWithUserMap.get(user).getOfficeLocation());
								dto.setName(userWithIdMap.get(userWithStatus.getKey()).getFullName());
								dto.setUserId(userWithStatus.getKey());
								dto.setDM(Utils.replaceNullWithEmpty(
										userWithIdMap.get(userWithStatus.getKey()).getAssignedBdm()));
								dto.setLocation(userWithIdMap.get(userWithStatus.getKey()).getOfficeLocation());

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

								for (SubmittalStatus submittalStatus : SubmittalStatus.values()) {
									if (submittalTotalsByStatus.get(submittalStatus.name()) == null) {
										submittalTotalsByStatus.put(submittalStatus.name(), 0);
									}
								}

								cityWithTotalCountMap.put(city, submittalTotalsByStatus);
								// dto.setSubmittalTotalsByStatus(submittalTotalsByStatus);
								if (cityWithUserSubmittalCountStatusMap.get(city) != null) {
									cityWithUserSubmittalCountStatusMap.get(city).add(dto);
								} else {
									List<SubmittalStatsDto> submittalStatsDtoList = new ArrayList<SubmittalStatsDto>();
									submittalStatsDtoList.add(dto);
									cityWithUserSubmittalCountStatusMap.put(city, submittalStatsDtoList);
								}

							}
						}
					}

					for (String city : cityWithUserSubmittalCountStatusMap.keySet()) {

						Collections.sort(cityWithUserSubmittalCountStatusMap.get(city),
								new Comparator<SubmittalStatsDto>() {

									@Override
									public int compare(SubmittalStatsDto o1, SubmittalStatsDto o2) {
										// TODO Auto-generated method stub
										return o1.getName().compareTo(o2.getName());
									}
								});

						/*
						 * Collections.sort(cityWithUserSubmittalCountStatusMap.
						 * get(city), new Comparator<SubmittalStatsDto>() {
						 * 
						 * @Override public int compare(SubmittalStatsDto o1,
						 * SubmittalStatsDto o2) { // TODO Auto-generated method
						 * stub return
						 * o2.getSTARTED().compareTo(o1.getSTARTED()); } });
						 */

						Map<String, Object> cityWithStatusWithTotalMap = new HashMap<String, Object>();
						cityWithStatusWithTotalMap.put("city", city);
						cityWithStatusWithTotalMap.put("records", cityWithUserSubmittalCountStatusMap.get(city));
						// cityWithStatusWithTotalMap.put("bdm", dmAndAdmList);
						cityWithStatusWithTotalMap.put("totalRecordsCount", cityWithTotalCountMap.get(city));
						cityWithStatusWithTotalList.add(cityWithStatusWithTotalMap);
					}
				}
				return new ResponseEntity<>(cityWithStatusWithTotalList, HttpStatus.OK);
			}

			// if (selectedDM != null && selectedDM.length() > 0)
			// bdmCount();

			catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
				return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
			}
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getDMsSubmittalStatsReportWithoutLogin", method = RequestMethod.POST)
	public ResponseEntity<?> getDMsSubmittalStatsReportWithoutLogin(@RequestBody DashboardSearchDto dashboardSearchDto,
			HttpServletRequest request) {
		// if (Utils.getLoginUserId(request) != null) {
		try {

			Map<String, Object> submittalStatusByUser = orderService.getDMsSubmittalStatsReport(
					Utils.convertAngularStrToDate(dashboardSearchDto.getStartDate()),
					Utils.getEndDate(dashboardSearchDto.getEndDate()), Utils.getLoginUserId(request));

			return new ResponseEntity<>(submittalStatusByUser, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
		// } else {
		// log.error("User must login");
		// return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		// }
	}

	@RequestMapping(value = "/getDMsSubmittalStatsReport", method = RequestMethod.POST)
	public ResponseEntity<?> getDMsSubmittalStatsReport(@RequestBody DashboardSearchDto dashboardSearchDto,
			HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				String loginUserId = null;
				if (dashboardSearchDto.getIsAuthRequired() != null && dashboardSearchDto.getIsAuthRequired()) {
					UserDto userDto = Utils.getLoginUser(request);
					if (userDto.getUserRole().equals(UserRole.DM) || userDto.getUserRole().equals(UserRole.ADM)) {
						if (userDto.getUserRole().equals(UserRole.DM)) {
							loginUserId = userDto.getUserId();
						} else {
							loginUserId = userDto.getAssignedBdm();
						}
					}
				}
				Map<String, Object> submittalStatusByUser = orderService.getDMsSubmittalStatsReport(
						Utils.convertAngularStrToDate(dashboardSearchDto.getStartDate()),
						Utils.getEndDate(dashboardSearchDto.getEndDate()), loginUserId);

				return new ResponseEntity<>(submittalStatusByUser, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
				return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
			}
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getUserSubmittalsById", method = RequestMethod.POST)
	public ResponseEntity<?> getUserSubmittalsById(@RequestBody DashboardSearchDto dashboardSearchDto,
			HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				dashboardSearchDto.setDtStartDate(Utils.convertAngularStrToDate(dashboardSearchDto.getStartDate()));
				dashboardSearchDto.setDtEndDate(Utils.getEndDate(dashboardSearchDto.getEndDate()));
				if (dashboardSearchDto != null && dashboardSearchDto.getUserStatus() != null
						&& dashboardSearchDto.getUserStatus().equalsIgnoreCase(Constants.ALL)) {
					dashboardSearchDto.setUserStatus(null);
				}
				List<?> list = null;
				if (dashboardSearchDto.getIsDm() != null && dashboardSearchDto.getIsDm()) {
					list = orderService.getDMSubmittalDetailsById(dashboardSearchDto);
				} else {
					list = orderService.getUserSubmittalDetailsById(dashboardSearchDto);
				}
				return new ResponseEntity<>(list, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
				return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
			}
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getAllSubmittalsByUserId", method = RequestMethod.POST)
	public ResponseEntity<?> getAllSubmittalsByUserId(@RequestBody DashboardSearchDto dashboardSearchDto,
			HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				JobOrderSearchDto jobOrderSearchDto = new JobOrderSearchDto();
				jobOrderSearchDto.setStartEntryDate(Utils.convertAngularStrToDate(dashboardSearchDto.getStartDate()));
				jobOrderSearchDto.setEndEntryDate(Utils.getEndDate(dashboardSearchDto.getEndDate()));
				jobOrderSearchDto.setSubmittalBdms(dashboardSearchDto.getUserId());
				List<?> list = submittalService.findSubmittals(jobOrderSearchDto);

				return new ResponseEntity<>(list, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
				return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
			}
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

}

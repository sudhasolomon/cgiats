package com.uralian.cgiats.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.uralian.cgiats.dto.AddEditJobOrderDto;
import com.uralian.cgiats.dto.JobOrderDto;
import com.uralian.cgiats.dto.JobOrderFieldDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.dto.SubmittalEventDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.model.IndiaCandidate;
import com.uralian.cgiats.model.IndiaJobOrder;
import com.uralian.cgiats.model.IndiaSubmittal;
import com.uralian.cgiats.model.JobOrderPriority;
import com.uralian.cgiats.model.JobOrderSearchDto;
import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.JobType;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.service.IndiaCandidateService;
import com.uralian.cgiats.service.IndiaJobOrderService;
import com.uralian.cgiats.service.IndiaSubmittalService;
import com.uralian.cgiats.service.IndiaUserService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.DataTypeEnum;
import com.uralian.cgiats.util.TransformEntityToDto;
import com.uralian.cgiats.util.TransformEntityToDtoForIndia;
import com.uralian.cgiats.util.Utils;

@Controller
@RequestMapping("India_JobOrder")
public class IndiaJobOrderController {
	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	public IndiaJobOrderService indiaJobOrderService;
	@Autowired
	private IndiaCandidateService indiaCandidateService;
	@Autowired
	private IndiaSubmittalService indiaSubmittalService;
	@Autowired
	private IndiaUserService userService;
	
	@RequestMapping(value = "/saveOrUpdateIndiaJobOrder", method = RequestMethod.POST, produces = "application/json", consumes = "multipart/form-data")
	public ResponseEntity<?> saveOrUpdateIndiaJobOrder(@RequestPart(value = "file", required = true) List<MultipartFile> files,
			@RequestParam(value = "addEditJobOrderDto") Object data, HttpServletRequest request){
		if (Utils.getLoginUserId(request) != null) {
			try {
			if (data != null) {
				String requestData = (String) data;
				AddEditJobOrderDto addEditJobOrderDto;
					addEditJobOrderDto = new ObjectMapper().readValue(requestData, AddEditJobOrderDto.class);
					addEditJobOrderDto.setCreatedBy(Utils.getLoginUserId(request));
					addEditJobOrderDto.setUpdatedBy(Utils.getLoginUserId(request));
					indiaJobOrderService.saveJobOrder(addEditJobOrderDto, files);
					
			}
			
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			return new ResponseEntity<Object>(HttpStatus.OK);
	} else {
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
	}
	
	
	@RequestMapping(value = "/getIndiaJobOrderById/{orderId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getJobOrderById(@PathVariable("orderId") String orderId, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			AddEditJobOrderDto jobOrderDto = null;
			if (orderId != null) {
				IndiaJobOrder jobOrder = indiaJobOrderService.getJobOrder(Integer.parseInt(orderId), false, false);
				if (jobOrder != null) {
					jobOrderDto = TransformEntityToDtoForIndia.getJobOrderDto(jobOrder);
				}
			}
			return new ResponseEntity<>(jobOrderDto, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	@RequestMapping(value = "/getAllIndiaDeletedMyJobOrders", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getAllDeletedMyJobOrders(@RequestBody JobOrderSearchDto jobOrderSearchDto, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			long startTime = Calendar.getInstance().getTimeInMillis();
			List<JobOrderDto> jobOrders = null;

			log.info("Method has started.. at : " + startTime);

			if (!Utils.isNull(jobOrderSearchDto)) {
				getJobOrderSearchDtoInfo(jobOrderSearchDto);

				UserDto userObj = Utils.getLoginUser(request);
				if (userObj != null) {
					if (userObj.getUserRole().equals(UserRole.DM))
						jobOrderSearchDto.setBdm(userObj.getUserId());
					else if (userObj.getUserRole().equals(UserRole.Recruiter))
						jobOrderSearchDto.setBdm(userObj.getAssignedBdm());
					else
						jobOrderSearchDto.setBdm(userObj.getUserId());
				}

				jobOrders = indiaJobOrderService.findDeletedJobOrders(jobOrderSearchDto, userObj);
			}
			long endTime = Calendar.getInstance().getTimeInMillis();
			log.info("Time taken to execute this method : " + (endTime - startTime) + " ms");
			return new ResponseEntity<>(jobOrders, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	@RequestMapping(value = "/getAllIndiaDeletedJobOrders", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getAllDeletedJobOrders(@RequestBody JobOrderSearchDto jobOrderSearchDto, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			long startTime = Calendar.getInstance().getTimeInMillis();
			List<JobOrderDto> jobOrders = null;

			log.info("Method has started.. at : " + startTime);

			if (!Utils.isNull(jobOrderSearchDto)) {
				getJobOrderSearchDtoInfo(jobOrderSearchDto);
				UserDto userObj = Utils.getLoginUser(request);

				jobOrders = indiaJobOrderService.findDeletedJobOrders(jobOrderSearchDto, userObj);
			}
			long endTime = Calendar.getInstance().getTimeInMillis();
			log.info("Time taken to execute this method : " + (endTime - startTime) + " ms");
			return new ResponseEntity<>(jobOrders, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	private void getJobOrderSearchDtoInfo(JobOrderSearchDto jobOrderSearchDto) {
		try {
			if (jobOrderSearchDto.getJobOrderTimeIntervalMap() != null) {
				jobOrderSearchDto.setStartEntryDate(Utils.convertAngularStrToDate_India(jobOrderSearchDto.getJobOrderTimeIntervalMap().get("startDate")));
				jobOrderSearchDto.setEndEntryDate(Utils.convertAngularStrToDate_India(jobOrderSearchDto.getJobOrderTimeIntervalMap().get("endDate")));
			}
			if (jobOrderSearchDto.getStrPriorities() != null) {
				List<JobOrderPriority> jobOrderPriorities = null;
				String[] strArray = Utils.getStrArray_FromStr(jobOrderSearchDto.getStrPriorities());
				if (strArray != null && strArray.length > 0) {
					jobOrderPriorities = new ArrayList<JobOrderPriority>();
					for (String str : strArray) {
						jobOrderPriorities.add(JobOrderPriority.valueOf(str));
					}
					jobOrderSearchDto.setPriorities(jobOrderPriorities);
				}
			}
			if (jobOrderSearchDto.getStrStatuses() != null) {
				List<JobOrderStatus> jobOrderStatus = null;
				String[] strArray = Utils.getStrArray_FromStr(jobOrderSearchDto.getStrStatuses());
				if (strArray != null && strArray.length > 0) {
					jobOrderStatus = new ArrayList<JobOrderStatus>();
					for (String str : strArray) {
						jobOrderStatus.add(JobOrderStatus.valueOf(str));
					}
					jobOrderSearchDto.setStatuses(jobOrderStatus);
				}
			}
			if (jobOrderSearchDto.getStrJobTypes() != null) {
				List<JobType> jobTypes = null;
				String[] strArray = Utils.getStrArray_FromStr(jobOrderSearchDto.getStrJobTypes());
				if (strArray != null && strArray.length > 0) {
					jobTypes = new ArrayList<JobType>();
					for (String str : strArray) {
						jobTypes.add(JobType.valueOf(str));
					}
					jobOrderSearchDto.setJobTypes(jobTypes);
				}
			}
			if (jobOrderSearchDto.getStrJobBelongsTo() != null) {
				String[] strArray = Utils.getStrArray_FromStr(jobOrderSearchDto.getStrJobBelongsTo());
				if (strArray != null && strArray.length > 0) {
					jobOrderSearchDto.setJobBelongsTo(Arrays.asList(strArray));
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Exception in parsing date ranges", ex.getMessage(), ex);
		}
	}
	
	
	@RequestMapping(value = "/reopenIndiaJobOrder/{orderId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> reopenJobOrder(@PathVariable("orderId") String orderId, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			AddEditJobOrderDto jobOrderDto = null;
			if (orderId != null) {
				indiaJobOrderService.reopenJobOrder(Integer.parseInt(orderId), Utils.getLoginUserId(request));
			}
			return new ResponseEntity<>(jobOrderDto, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getAllIndiaJobOrders", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getJobOrders(@RequestBody JobOrderSearchDto jobOrderSearchDto, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			long startTime = Calendar.getInstance().getTimeInMillis();
			List<JobOrderDto> jobOrders = null;

			log.info("Method has started.. at : " + startTime);

			if (!Utils.isNull(jobOrderSearchDto)) {
				getJobOrderSearchDtoInfo(jobOrderSearchDto);
				if (jobOrderSearchDto.getJobOrderId() != null) {
					Integer jobOrderId = jobOrderSearchDto.getJobOrderId();
					/*boolean isHot = false;
					if (jobOrderSearchDto.isHot()) {
						isHot = true;
					}*/
					jobOrderSearchDto = new JobOrderSearchDto();
					jobOrderSearchDto.setJobOrderId(jobOrderId);
					//jobOrderSearchDto.setHot(isHot);
				}

				jobOrders = indiaJobOrderService.searchJobOrders(jobOrderSearchDto, null, 0, 0);
			}
			long endTime = Calendar.getInstance().getTimeInMillis();
			log.info("Time taken to execute this method : " + (endTime - startTime) + " ms");
			return new ResponseEntity<>(jobOrders, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

@SuppressWarnings("unchecked")
@RequestMapping(value = "/getMyIndiaJobOrders", method = RequestMethod.POST, produces="application/json")
public @ResponseBody ResponseEntity<?> getMyIndiaJobOrders(@RequestBody JobOrderSearchDto jobOrderSearchDto, HttpServletRequest request) {
	if (Utils.getLoginUserId(request) != null) {
		long startTime = Calendar.getInstance().getTimeInMillis();
		List<JobOrderDto> jobOrders = null;

		log.info("Method has started.. at : " + startTime);
		if (!Utils.isNull(jobOrderSearchDto)) {
			getJobOrderSearchDtoInfo(jobOrderSearchDto);
			jobOrderSearchDto.setAssignedTo(Utils.getLoginUserId(request));

			if (jobOrderSearchDto.getBdm() != null) {
				jobOrderSearchDto.setAssignedTo(null);
			}
			jobOrders = indiaJobOrderService.searchJobOrders(jobOrderSearchDto,Utils.getLoginUser(request), 0, 0);
			log.info("job orders details"+jobOrders);
			//jobOrders = jobOrderService.findJobOrders(jobOrderSearchDto, Utils.getLoginUser(request), 0, 0);
		}
		long endTime = Calendar.getInstance().getTimeInMillis();
		log.info("Time taken to execute this method : " + (endTime - startTime) + " ms");
		return new ResponseEntity<>(jobOrders, HttpStatus.OK);
	} else {
		log.error("User must login");
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
}

@RequestMapping(value = "/copyJobOrder/{orderId}", method = RequestMethod.GET, produces = "application/json")
public @ResponseBody ResponseEntity<?> copyJobOrder(@PathVariable("orderId") Integer orderId, HttpServletRequest request) throws ServiceException {
	if (Utils.getLoginUserId(request) != null) {
		try {
			IndiaJobOrder jobOrder = indiaJobOrderService.getJobOrder(orderId).clone();
			log.info("order" + jobOrder.getId());
			log.info("orderCreatedBy" + jobOrder.getCreatedBy());
			jobOrder.setCreatedBy(Utils.getLoginUserId(request));
			jobOrder.setUpdatedBy(Utils.getLoginUserId(request));
			JobOrderDto jobOrderto = indiaJobOrderService.saveOrder(jobOrder);

			return new ResponseEntity<>(jobOrderto, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	} else {
		log.error("User must login");
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

}

@SuppressWarnings("unchecked")
@RequestMapping(value = "/downloadJobOrder/{jobOrderId}", method = RequestMethod.GET)
public void downloadJobOrder(@PathVariable("jobOrderId") Integer jobOrderId, HttpServletRequest req, HttpServletResponse response) {

	try {
		JobOrderSearchDto dto = new JobOrderSearchDto();
		dto.setJobOrderId(jobOrderId);
		getJobOrderSearchDtoInfo(dto);

		List<JobOrderDto> list = indiaJobOrderService.searchJobOrders(dto, null, 0, 0);

		if (list != null && list.size() > 0) {
			JobOrderDto jobOrderDto = list.get(0);

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Job_Orders");
			int rowCount = 0;
			Row row = sheet.createRow(rowCount++);
			// Excel sheet header names
			String[] headerNames = { "Id", "Priority", "Status", "Type", "Title", "Client", "Location", "Dm", "AssignedTo", "UpdatedDate", "Sbm",
					"ActiveDays" };
			// Excel sheet header value fields
			String[] headerValues = { "jobOrderId", "priority", "status", "type", "title", "client", "location", "dm", "assignedTo", "updatedDate", "sbm",
					"activeDays" };
			// Excel sheet header value types
			String[] headerValuesType = { DataTypeEnum.INTEGER.getValue(), DataTypeEnum.STRING.getValue(), DataTypeEnum.STRING.getValue(),
					DataTypeEnum.STRING.getValue(), DataTypeEnum.STRING.getValue(), DataTypeEnum.STRING.getValue(), DataTypeEnum.STRING.getValue(),
					DataTypeEnum.STRING.getValue(), DataTypeEnum.STRING.getValue(), DataTypeEnum.STRING.getValue(), DataTypeEnum.STRING.getValue(),
					DataTypeEnum.STRING.getValue(), };
			int columnCount = 0;
			// Display header names
			for (String headerName : headerNames) {
				Cell cell = row.createCell(columnCount++);
				cell.setCellValue(headerName);
			}
			columnCount = 0;

			row = sheet.createRow(rowCount++);
			// Display header values
			for (int i = 0; i < headerValues.length; i++) {
				Cell cell = row.createCell(columnCount++);
				if (headerValuesType[i].equals(DataTypeEnum.INTEGER.getValue())) {
					Integer value = (Integer) PropertyUtils.getProperty(jobOrderDto, headerValues[i]);
					cell.setCellValue(value);
				}
				if (headerValuesType[i].equals(DataTypeEnum.STRING.getValue())) {
					String value = (String) PropertyUtils.getProperty(jobOrderDto, headerValues[i]);
					cell.setCellValue(value);
				}
				sheet.autoSizeColumn(cell.getColumnIndex());

			}
			// response
			try (OutputStream output = response.getOutputStream();) {

				String fileName = "JobOrder_" + jobOrderDto.getJobOrderId() + ".xls";
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-disposition", fileName);
				ServletOutputStream out = response.getOutputStream();
				workbook.write(out);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	} catch (Exception e) {
		e.printStackTrace();
	}
	log.info(":: End of exportToExcel :: ");

}

// This Method Delete JobOrder By Taking Input As jobOrder Id
@RequestMapping(value = "/deleteJobOrder/{orderId}", method = RequestMethod.GET, produces = "application/json")
@ResponseBody
public ResponseEntity<?> deleteJobOrder(@PathVariable("orderId") String orderID, HttpServletRequest request) {
	if (Utils.getLoginUserId(request) != null) {
		try {
			indiaJobOrderService.deleteJobOrder(Integer.parseInt(orderID));
		} catch (NumberFormatException | ServiceException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			return new ResponseEntity<List<?>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<?>>(HttpStatus.OK);
	} else {
		log.error("User must login");
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
}


@RequestMapping(value = "/getReadableJobOrderById/{orderId}", method = RequestMethod.GET, produces = "application/json")
public @ResponseBody ResponseEntity<?> getReadableJobOrderById(@PathVariable("orderId") String orderId, HttpServletRequest request) {
	if (Utils.getLoginUserId(request) != null) {
		AddEditJobOrderDto jobOrderDto = null;
		if (orderId != null) {
			 IndiaJobOrder jobOrder = indiaJobOrderService.getJobOrder(Integer.parseInt(orderId));
			if (jobOrder != null) {
				jobOrderDto = TransformEntityToDto.getIndiaJobOrderDto(jobOrder);
				if (jobOrderDto.getState() != null) {
					jobOrderDto.setState(Utils.getStateNameByCode(jobOrderDto.getState()));
				}
				if (jobOrderDto.getJobOrderFieldList() != null && jobOrderDto.getJobOrderFieldList().size() > 0) {
					Iterator<JobOrderFieldDto> fieldDTOIterator = jobOrderDto.getJobOrderFieldList().iterator();
					while (fieldDTOIterator.hasNext()) {
						JobOrderFieldDto dto = fieldDTOIterator.next();
						if (!dto.getVisible()) {
							fieldDTOIterator.remove();
						}
					}
				}
			}

		}
		return new ResponseEntity<>(jobOrderDto, HttpStatus.OK);
	} else {
		log.error("User must login");
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
}


@RequestMapping(value = "/findTheStatusOfCandidate", method = RequestMethod.GET, produces = "application/json")
public @ResponseBody ResponseEntity<?> findTheStatusOfCandidate(@RequestParam("candidateId") Integer candidateId,
		@RequestParam("jobOrderId") Integer jobOrderId, HttpServletRequest request) {
	if (Utils.getLoginUserId(request) != null) {
		IndiaCandidate candidate = null;
		Map<String, String> msgMap = new HashMap<String, String>();
		log.info("Candidate Id:" + candidateId);
		Map<String,Object>  resultMap= indiaCandidateService.getCandidate(candidateId, true, true);
		if(resultMap!=null){
		candidate = (IndiaCandidate) resultMap.get(Constants.DATA);
		}
		if (candidate == null) {
			msgMap.put("message", "Candidate not found");
			return new ResponseEntity<>(msgMap, HttpStatus.OK);
		}
		List<IndiaSubmittal> submittailValue = indiaJobOrderService.getCandidate(jobOrderId, candidateId);
		if (submittailValue.size() > 0) {
			log.info("candidateName exists: " + submittailValue.size());
			msgMap.put("message", "candidate Id:" + candidateId + " is already assigned to this job order");
			return new ResponseEntity<>(msgMap, HttpStatus.OK);
		}

		List<IndiaSubmittal> selected = indiaSubmittalService.getCandidateJobrderStatus(candidateId);

		for (IndiaSubmittal sub : selected) {
			if (sub.getStatus().equals(SubmittalStatus.STARTED)) {
				msgMap.put("message", "Entered Candidate Id: " + candidateId + " is already in STARTED status for other job order");
				return new ResponseEntity<>(msgMap, HttpStatus.OK);
			}
		}

		List<IndiaSubmittal> candidateSubDetails = indiaJobOrderService.getCandidateSubDetails(candidateId);
		Iterator<IndiaSubmittal> itr = candidateSubDetails.iterator();
		while (itr.hasNext()) {
			IndiaSubmittal details = (IndiaSubmittal) itr.next();
			if (details.getStatus().equals(SubmittalStatus.STARTED)) {
				log.debug("Candidate already assigned: " + candidateId);
				msgMap.put("message", "Entered Candidate Id: " + candidateId + " is already assigned");
				return new ResponseEntity<>(msgMap, HttpStatus.OK);
			}
		}
		msgMap.put("message", null);
		return new ResponseEntity<>(msgMap, HttpStatus.OK);
	} else {
		log.error("User must login");
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
}

@RequestMapping(value = "/checkStartedStatusBy_CandidateId_SubmittalId", method = RequestMethod.GET, produces = "application/json")
public @ResponseBody ResponseEntity<?> checkStartedStatusBy_CandidateId_SubmittalId(@RequestParam("candidateId") Integer candidateId,
		@RequestParam("submittalId") Integer submittalId, HttpServletRequest request) {
	if (Utils.getLoginUserId(request) != null) {
		Map<String, String> msgMap = new HashMap<String, String>();
		log.info("Candidate Id:" + candidateId);
		Boolean candidateStarted = indiaSubmittalService.checkStartedStatusBy_CandidateId_SubmittalId(candidateId,submittalId);

		if (candidateStarted) {
			msgMap.put("message", "Candidate Id: " + candidateId + " is already in STARTED status for other job order");
			return new ResponseEntity<>(msgMap, HttpStatus.OK);
		}
		msgMap.put("message", null);
		return new ResponseEntity<>(msgMap, HttpStatus.OK);
	} else {
		log.error("User must login");
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
}

@RequestMapping(value = "/saveOrUpdateSubmittal", method = RequestMethod.POST, produces = "application/json")
public @ResponseBody ResponseEntity<?> saveOrUpdateSubmittal(@RequestBody SubmittalDto submittalDto, HttpServletRequest request) {
	if (Utils.getLoginUserId(request) != null) {
		try {
			if (submittalDto != null) {
				submittalDto.setCreatedBy(Utils.getLoginUserId(request));
				submittalDto.setUpdatedBy(Utils.getLoginUserId(request));
				submittalDto.setUserDto(Utils.getLoginUser(request));
				indiaJobOrderService.saveOrUpdateSubmittal(submittalDto);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Object>(HttpStatus.OK);
	} else {
		log.error("User must login");
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
}

@RequestMapping(value = "/submittalDetails", method = RequestMethod.POST, produces = "application/json")
public @ResponseBody ResponseEntity<?> getSubmittals(@RequestParam(value = "jobOrderId") String jobOrderid, HttpServletRequest request)
		throws ServiceException {
	if (Utils.getLoginUserId(request) != null) {

		try {
			final Integer jobOrderId = Integer.parseInt(jobOrderid);
			List<SubmittalDto> dtoList = indiaJobOrderService.findSubmittalsDetails(jobOrderId);
			return new ResponseEntity<>(dtoList, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Exception " + e);
			return null;
		}
	} else {
		log.error("User must login");
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
}

@RequestMapping(value = "/getSubmittalEventHistoryBySubmittalId/{submittalId}", method = RequestMethod.GET, produces = "application/json")
public @ResponseBody ResponseEntity<?> getSubmittalEventHistoryBySubmittalId(@PathVariable(value = "submittalId") String submittalId,
		HttpServletRequest request) throws ServiceException {
	if (Utils.getLoginUserId(request) != null) {

		try {
			if (submittalId != null) {
				List<SubmittalEventDto> dtoList = indiaJobOrderService.getSubmittalEventHistoryBySubmittalId(Integer.parseInt(submittalId));
				return new ResponseEntity<>(dtoList, HttpStatus.OK);
			} else {
				log.error("submittalId is null");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			log.error("Exception " + e);
			return null;
		}
	} else

	{
		log.error("User must login");
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}

}

@RequestMapping(value = "/deleteSubmittal", method = RequestMethod.GET, produces = "application/json")
@ResponseBody
public ResponseEntity<?> deleteSubmittal(@RequestParam("submittalID") String submittalID, @RequestParam("reason") String reason,
		HttpServletRequest request) {
	if (Utils.getLoginUserId(request) != null) {
		try {
			indiaJobOrderService.deleteSubmittal(Integer.parseInt(submittalID), reason);
		} catch (NumberFormatException | ServiceException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			return new ResponseEntity<List<?>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<List<?>>(HttpStatus.OK);
	} else {
		log.error("User must login");
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
}

@RequestMapping(value = "/getSubmittalById/{submittalId}", method = RequestMethod.GET, produces = "application/json")
public @ResponseBody ResponseEntity<?> getSubmittalById(@PathVariable("submittalId") Integer submittalId, HttpServletRequest request) {
	if (Utils.getLoginUserId(request) != null) {
		SubmittalDto submittalDto = null;
		if (submittalId != null) {
			IndiaSubmittal submittal = indiaJobOrderService.getSubmittal(submittalId);
			if (submittal != null) {
				submittalDto = TransformEntityToDto.getIndiaSubmittalDto(submittal);
				if (submittalDto.getJobOrderDto() != null && submittalDto.getJobOrderDto().getState() != null) {
					submittalDto.getJobOrderDto().setState(Utils.getStateNameByCode(submittalDto.getJobOrderDto().getState()));
				}
				//User user = userService.loadUser(submittal.getJobOrder().getCreatedBy());
				//submittalDto.setUserDto(TransformEntityToDto.getUserDto(user));
			}
		}
		return new ResponseEntity<>(submittalDto, HttpStatus.OK);
	} else {
		log.error("User must login");
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
}

@RequestMapping(value = "/downloadJobOrderAttachment/{jobOrderId}", produces = "application/json", method = RequestMethod.GET)
public void downloadJobOrderAttachment(@PathVariable(value = "jobOrderId") Integer jobOrderId, HttpServletRequest request, HttpServletResponse response) {
	AddEditJobOrderDto dto = indiaJobOrderService.getJobOrderAttachmentInfo(jobOrderId);
	try (OutputStream output = response.getOutputStream();) {
		ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dto.getAttachmentByte());
		response.reset();
		if (dto.getAttachmentFileName() != null) {
			response.setContentType(ContentType.resolveByFileName(dto.getAttachmentFileName()).toString());
		} else {
			response.setContentType("application/octet-stream");
		}

		if (dto.getAttachmentFileName() != null) {
			response.setContentLength((int) (dto.getAttachmentByte().length));
			response.setHeader("Content-Disposition",
					"attachment; filename=" + "file" + jobOrderId + "_." + Utils.getFileExtensionByName(dto.getAttachmentFileName()));
		} else {
			response.setContentLength(0);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + "NoContent.txt" + "\"");
		}
		IOUtils.copyLarge(fileInputStream, output);
		output.flush();
	} catch (IOException e) {
		e.printStackTrace();
	}
	// }

}

@RequestMapping(value = "/saveClient", method = RequestMethod.POST, produces = "application/json")
public @ResponseBody ResponseEntity<?> saveOrUpdateClient(@RequestBody Map<String, String> map, HttpServletRequest request){
	  if(Utils.getLoginUserId(request)!=null){
		  String clientName=map.get("clientName");
		  String updatedBy=Utils.getLoginUserId(request);
		  indiaJobOrderService.addClient(clientName,updatedBy);
		  return new ResponseEntity<>(null, HttpStatus.OK);
	  }else{
		  log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	  }
}

@RequestMapping(value = "/updateClient", method = RequestMethod.POST, produces = "application/json")
public @ResponseBody ResponseEntity<?> updateClient(@RequestBody Map<String, String> map, HttpServletRequest request){
	  if(Utils.getLoginUserId(request)!=null){
		  String oldClientName=map.get("oldClientName");
		  String newClientName=map.get("newClientName");
		  String updatedBy=Utils.getLoginUserId(request);
		  indiaJobOrderService.updateClient(oldClientName,newClientName,updatedBy);
		  return new ResponseEntity<>(null, HttpStatus.OK);
	  }else{
		  log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	  }
}
	
}

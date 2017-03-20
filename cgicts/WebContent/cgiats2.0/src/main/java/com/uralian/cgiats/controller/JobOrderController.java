/**
 * 
 */
package com.uralian.cgiats.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.FilenameUtils;
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
import com.uralian.cgiats.dto.CandidateBlockHotVo;
import com.uralian.cgiats.dto.CandidateDto;
import com.uralian.cgiats.dto.JobOrderDto;
import com.uralian.cgiats.dto.JobOrderFieldDto;
import com.uralian.cgiats.dto.ReportwiseDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.dto.SubmittalEventDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.JobOrderPriority;
import com.uralian.cgiats.model.JobOrderSearchDto;
import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.JobType;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.JobOrderService;
import com.uralian.cgiats.service.OnlineCgiCandidateService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.SubmittalService;
import com.uralian.cgiats.service.UserService;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.DataTypeEnum;
import com.uralian.cgiats.util.StatusMessage;
import com.uralian.cgiats.util.TransformEntityToDto;
import com.uralian.cgiats.util.Utils;

/**
 * @author Sreenath
 *
 */
@Controller
@RequestMapping("jobOrder")
public class JobOrderController {

	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private JobOrderService jobOrderService;
	@Autowired
	private CandidateService candidateService;
	@Autowired
	private SubmittalService submittalService;
	@Autowired
	private OnlineCgiCandidateService onlineCgiCandidateService;
	@Autowired
	private UserService userService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getJobOrders", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getJobOrders(@RequestBody JobOrderSearchDto jobOrderSearchDto, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			long startTime = Calendar.getInstance().getTimeInMillis();
			List<JobOrderDto> jobOrders = null;

			log.info("Method has started.. at : " + startTime);

			if (!Utils.isNull(jobOrderSearchDto)) {
				getJobOrderSearchDtoInfo(jobOrderSearchDto);
				if (jobOrderSearchDto.getJobOrderId() != null) {
					Integer jobOrderId = jobOrderSearchDto.getJobOrderId();
					boolean isHot = false;
					if (jobOrderSearchDto.isHot()) {
						isHot = true;
					}
					jobOrderSearchDto = new JobOrderSearchDto();
					jobOrderSearchDto.setJobOrderId(jobOrderId);
					jobOrderSearchDto.setHot(isHot);
				}

				jobOrders = jobOrderService.findJobOrders(jobOrderSearchDto, null, 0, 0);
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
	@RequestMapping(value = "/getHotJobOrdersForKen", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getHotJobOrdersForKen(@RequestBody JobOrderSearchDto jobOrderSearchDto, HttpServletRequest request) {
		long startTime = Calendar.getInstance().getTimeInMillis();
		List<JobOrderDto> jobOrders = null;

		log.info("Method has started.. at : " + startTime);

		if (!Utils.isNull(jobOrderSearchDto)) {
			getJobOrderSearchDtoInfo(jobOrderSearchDto);
			if (jobOrderSearchDto.getJobOrderId() != null) {
				Integer jobOrderId = jobOrderSearchDto.getJobOrderId();
				boolean isHot = false;
				if (jobOrderSearchDto.isHot()) {
					isHot = true;
				}
				jobOrderSearchDto = new JobOrderSearchDto();
				jobOrderSearchDto.setJobOrderId(jobOrderId);
				jobOrderSearchDto.setHot(isHot);
			}

			jobOrders = jobOrderService.findJobOrders(jobOrderSearchDto, null, 0, 0);
		}
		long endTime = Calendar.getInstance().getTimeInMillis();
		log.info("Time taken to execute this method : " + (endTime - startTime) + " ms");
		return new ResponseEntity<>(jobOrders, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getMyJobOrders", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getMyJobOrders(@RequestBody JobOrderSearchDto jobOrderSearchDto, HttpServletRequest request) {
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

				jobOrders = jobOrderService.findJobOrders(jobOrderSearchDto, Utils.getLoginUser(request), 0, 0);
			}
			long endTime = Calendar.getInstance().getTimeInMillis();
			log.info("Time taken to execute this method : " + (endTime - startTime) + " ms");
			return new ResponseEntity<>(jobOrders, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getEMJobOrders", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getEMJobOrders(@RequestBody JobOrderSearchDto jobOrderSearchDto, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			long startTime = Calendar.getInstance().getTimeInMillis();
			List<JobOrderDto> jobOrders = null;

			log.info("Method has started.. at : " + startTime);

			if (!Utils.isNull(jobOrderSearchDto)) {
				getJobOrderSearchDtoInfo(jobOrderSearchDto);
				jobOrders = jobOrderService.findEMJobOrders(jobOrderSearchDto, Utils.getLoginUser(request));
			}
			long endTime = Calendar.getInstance().getTimeInMillis();
			log.info("Time taken to execute this method : " + (endTime - startTime) + " ms");
			return new ResponseEntity<>(jobOrders, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getAllDeletedMyJobOrders", method = RequestMethod.POST, produces = "application/json")
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

				jobOrders = jobOrderService.findDeletedJobOrders(jobOrderSearchDto, userObj);
			}
			long endTime = Calendar.getInstance().getTimeInMillis();
			log.info("Time taken to execute this method : " + (endTime - startTime) + " ms");
			return new ResponseEntity<>(jobOrders, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getAllDeletedJobOrders", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getAllDeletedJobOrders(@RequestBody JobOrderSearchDto jobOrderSearchDto, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			long startTime = Calendar.getInstance().getTimeInMillis();
			List<JobOrderDto> jobOrders = null;

			log.info("Method has started.. at : " + startTime);

			if (!Utils.isNull(jobOrderSearchDto)) {
				getJobOrderSearchDtoInfo(jobOrderSearchDto);
				UserDto userObj = Utils.getLoginUser(request);

				jobOrders = jobOrderService.findDeletedJobOrders(jobOrderSearchDto, userObj);
			}
			long endTime = Calendar.getInstance().getTimeInMillis();
			log.info("Time taken to execute this method : " + (endTime - startTime) + " ms");
			return new ResponseEntity<>(jobOrders, HttpStatus.OK);
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
				List<SubmittalDto> dtoList = jobOrderService.findSubmittalsDetails(jobOrderId);
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
					List<SubmittalEventDto> dtoList = jobOrderService.getSubmittalEventHistoryBySubmittalId(Integer.parseInt(submittalId));
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

	@RequestMapping(value = "/getJobOrder", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getJobOrder(@RequestParam String orderId, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			final Integer jobOrderId = Integer.parseInt(orderId);
			JobOrder jobOrder = jobOrderService.getJobOrder(jobOrderId);
			return new ResponseEntity<>(jobOrder, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getJobOrderById/{orderId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getJobOrderById(@PathVariable("orderId") String orderId, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			AddEditJobOrderDto jobOrderDto = null;
			if (orderId != null) {
				JobOrder jobOrder = jobOrderService.getJobOrder(Integer.parseInt(orderId));
				if (jobOrder != null) {
					jobOrderDto = TransformEntityToDto.getJobOrderDto(jobOrder);
				}
			}
			return new ResponseEntity<>(jobOrderDto, HttpStatus.OK);
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
				Submittal submittal = jobOrderService.getSubmittal(submittalId);
				if (submittal != null) {
					submittalDto = TransformEntityToDto.getSubmittalDto(submittal);

					if (submittalDto.getJobOrderDto() != null && submittalDto.getJobOrderDto().getState() != null) {
						submittalDto.getJobOrderDto().setState(Utils.getStateNameByCode(submittalDto.getJobOrderDto().getState()));
					}
					User user = userService.loadUser(
							submittal.getJobOrder().getDmName() != null ? submittal.getJobOrder().getDmName() : submittal.getJobOrder().getCreatedBy());
					submittalDto.setUserDto(TransformEntityToDto.getUserDto(user));
				}
			}
			return new ResponseEntity<>(submittalDto, HttpStatus.OK);
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
				JobOrder jobOrder = jobOrderService.getJobOrder(Integer.parseInt(orderId));
				if (jobOrder != null) {
					jobOrderDto = TransformEntityToDto.getJobOrderDto(jobOrder);
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

	@RequestMapping(value = "/saveOrUpdateJobOrder", method = RequestMethod.POST, produces = "application/json", consumes = "multipart/form-data")
	public @ResponseBody ResponseEntity<?> saveOrUpdateJobOrder(@RequestPart(value = "file", required = true) List<MultipartFile> files,
			@RequestParam(value = "addEditJobOrderDto") Object data, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				if (data != null) {
					String requestData = (String) data;
					AddEditJobOrderDto addEditJobOrderDto = new ObjectMapper().readValue(requestData, AddEditJobOrderDto.class);
					addEditJobOrderDto.setCreatedBy(Utils.getLoginUserId(request));
					addEditJobOrderDto.setUpdatedBy(Utils.getLoginUserId(request));
					jobOrderService.saveJobOrder(addEditJobOrderDto, files);
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

	@RequestMapping(value = "/saveOrUpdateSubmittal", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> saveOrUpdateSubmittal(@RequestBody SubmittalDto submittalDto, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				if (submittalDto != null) {
					submittalDto.setCreatedBy(Utils.getLoginUserId(request));
					submittalDto.setUpdatedBy(Utils.getLoginUserId(request));
					submittalDto.setUserDto(Utils.getLoginUser(request));
					jobOrderService.saveOrUpdateSubmittal(submittalDto);
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

	@RequestMapping(value = "/findTheStatusOfCandidate", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> findTheStatusOfCandidate(@RequestParam("candidateId") Integer candidateId,
			@RequestParam("jobOrderId") Integer jobOrderId, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			Candidate candidate = null;
			Map<String, String> msgMap = new HashMap<String, String>();
			log.info("Candidate Id:" + candidateId);
			candidate = candidateService.getCandidate(candidateId, true, true);

			if (candidate == null) {
				msgMap.put("message", "Candidate not found");
				return new ResponseEntity<>(msgMap, HttpStatus.OK);
			}
			List<Submittal> submittailValue = jobOrderService.getCandidate(jobOrderId, candidateId);
			if (submittailValue.size() > 0) {
				log.info("candidateName exists: " + submittailValue.size());
				msgMap.put("message", "candidate Id:" + candidateId + " is already assigned to this job order");
				return new ResponseEntity<>(msgMap, HttpStatus.OK);
			}

			List<Submittal> selected = submittalService.getCandidateJobrderStatus(candidateId);

			for (Submittal sub : selected) {
				if (sub.getStatus().equals(SubmittalStatus.STARTED)) {
					msgMap.put("message", "Entered Candidate Id: " + candidateId + " is already in STARTED status for other job order");
					return new ResponseEntity<>(msgMap, HttpStatus.OK);
				}
			}

			List<Submittal> candidateSubDetails = jobOrderService.getCandidateSubDetails(candidateId);
			Iterator<Submittal> itr = candidateSubDetails.iterator();
			while (itr.hasNext()) {
				Submittal details = (Submittal) itr.next();
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
			Boolean candidateStarted = submittalService.checkStartedStatusBy_CandidateId_SubmittalId(candidateId,submittalId);

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

	@RequestMapping(value = "/copyJobOrder/{orderId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> copyJobOrder(@PathVariable("orderId") Integer orderId, HttpServletRequest request) throws ServiceException {
		if (Utils.getLoginUserId(request) != null) {
			try {
				JobOrder jobOrder = jobOrderService.getJobOrder(orderId).clone();
				log.info("order" + jobOrder.getId());
				log.info("orderCreatedBy" + jobOrder.getCreatedBy());
				jobOrder.setCreatedBy(Utils.getLoginUserId(request));
				jobOrder.setUpdatedBy(Utils.getLoginUserId(request));
				JobOrderDto jobOrderto = jobOrderService.saveOrder(jobOrder);

				return new ResponseEntity<>(jobOrderto, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}

	/**
	 * This method is to download the job order record using job orderId
	 * 
	 * @param jobOrderId
	 * @param req
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/downloadJobOrder/{jobOrderId}", method = RequestMethod.GET)
	public void downloadJobOrder(@PathVariable("jobOrderId") Integer jobOrderId, HttpServletRequest req, HttpServletResponse response) {

		try {
			JobOrderSearchDto dto = new JobOrderSearchDto();
			dto.setJobOrderId(jobOrderId);
			getJobOrderSearchDtoInfo(dto);

			List<JobOrderDto> list = jobOrderService.findJobOrders(dto, null, 0, 0);

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
					log.error(e.getMessage(), e);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		log.info(":: End of exportToExcel :: ");

	}

	@RequestMapping(value = "/downloadJobOrderAttachment/{jobOrderId}", produces = "application/json", method = RequestMethod.GET)
	public void downloadJobOrderAttachment(@PathVariable(value = "jobOrderId") Integer jobOrderId, HttpServletRequest request, HttpServletResponse response) {
		AddEditJobOrderDto dto = jobOrderService.getJobOrderAttachmentInfo(jobOrderId);
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
			log.error(e.getMessage(), e);
		}
		// }

	}

	// This Method Delete JobOrder By Taking Input As jobOrder Id
	@RequestMapping(value = "/deleteJobOrder/{orderId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> deleteJobOrder(@PathVariable("orderId") String orderID, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				jobOrderService.deleteJobOrder(Integer.parseInt(orderID));
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

	@RequestMapping(value = "/deleteSubmittal", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> deleteSubmittal(@RequestParam("submittalID") String submittalID, @RequestParam("reason") String reason,
			HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				jobOrderService.deleteSubmittal(Integer.parseInt(submittalID), reason);
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

	// This Method is to get all the open job orders based on portal name
	@RequestMapping(value = "/getOpenJobOrders", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getOpenJobOrders(@RequestParam("portalName") String portalName, HttpServletRequest request) {
		try {
			List<AddEditJobOrderDto> jobOrderDtoList = null;
			JobOrderSearchDto jobOrderSearchDto = new JobOrderSearchDto();
			jobOrderSearchDto.setStrStatuses("OPEN,ASSIGNED,REOPEN");
//			Map<String, String> jobOrderTimeIntervalMap = new HashMap<String, String>();
			// putting the last 3 months date range into the map
			/*
			 * Calendar cal = Calendar.getInstance(); // Decrementing to the
			 * last 3 months cal.add(Calendar.DATE, -90);
			 * jobOrderTimeIntervalMap.put("startDate",
			 * Utils.convertDateToString(cal.getTime()));
			 * jobOrderTimeIntervalMap.put("endDate",
			 * Utils.convertDateToString(new Date()));
			 * jobOrderSearchDto.setJobOrderTimeIntervalMap(
			 * jobOrderTimeIntervalMap);
			 */
			getJobOrderSearchDtoInfo(jobOrderSearchDto);
			jobOrderDtoList = jobOrderService.findJobOrdersOnline(jobOrderSearchDto, portalName);
			if (jobOrderDtoList != null) {
				log.info("List size:" + jobOrderDtoList.size());
			}
			return new ResponseEntity<>(jobOrderDtoList, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			return new ResponseEntity<List<?>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// This Method is to get all the open job orders based on portal name
	@RequestMapping(value = "/getOpenJobOrdersDescription/{jobOrderId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getOpenJobOrdersDescription(@PathVariable("jobOrderId") Integer jobOrderId, HttpServletRequest request) {
		try {
			log.info("getOpenJobOrdersDescription() is started with job order id : " + jobOrderId);
			Map<String, String> jobOrderMap = new HashMap<String, String>();
			String description = jobOrderService.getOpenJobOrdersDescription(jobOrderId);
			log.info("Joborder Description is:" + description);
			if (description != null) {
				jobOrderMap.put("description", description);
			}
			return new ResponseEntity<>(jobOrderMap, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			return new ResponseEntity<List<?>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void getJobOrderSearchDtoInfo(JobOrderSearchDto jobOrderSearchDto) {
		try {
			if (jobOrderSearchDto.getJobOrderTimeIntervalMap() != null) {
				jobOrderSearchDto.setStartEntryDate(Utils.convertAngularStrToDate(jobOrderSearchDto.getJobOrderTimeIntervalMap().get("startDate")));
				Date endDate = Utils.convertAngularStrToDate(jobOrderSearchDto.getJobOrderTimeIntervalMap().get("endDate"));
				Calendar cal = Calendar.getInstance();
				cal.setTime(endDate);
				cal.set(Calendar.HOUR, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				jobOrderSearchDto.setEndEntryDate(cal.getTime());
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

	@RequestMapping(value = "/reopenJobOrder/{orderId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> reopenJobOrder(@PathVariable("orderId") String orderId, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			AddEditJobOrderDto jobOrderDto = null;
			if (orderId != null) {
				jobOrderService.reopenJobOrder(Integer.parseInt(orderId), Utils.getLoginUserId(request));
			}
			return new ResponseEntity<>(jobOrderDto, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * This method is to save or update online job order candidate
	 * 
	 * @param files
	 * @param data
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveOrUpdateCandidateForOnlineJobOrder", method = RequestMethod.POST, produces = "application/json", consumes = "multipart/form-data")
	public @ResponseBody ResponseEntity<?> saveOrUpdateCandidateForOnlineJobOrder(@RequestPart(value = "file", required = true) List<MultipartFile> files,
			@RequestParam(value = "candidateDto") Object data, HttpServletRequest request) {
		try {
			Map<String, String> msgMap = new HashMap<String, String>();
			if (data != null) {
				String requestData = (String) data;
				CandidateDto candidateDto = new ObjectMapper().readValue(requestData, CandidateDto.class);
				log.info("joapplyid ::: " + candidateDto.getJobOrderId());
				Candidate candidate = null;
				Boolean isCandidateExists = false;
				log.info("portalName>>----123------" + candidateDto.getCreatedUser());
				System.out.println("portalName------------------" + candidateDto.getCreatedUser());
				JobOrder jobOrder = jobOrderService.getJobOrder(Integer.parseInt(candidateDto.getJobOrderId()), true, true);
				candidate = candidateService.getCandidateByEmail(candidateDto.getEmail());
				if (jobOrder != null && jobOrder.getId() != 0) {
					Boolean isCandidateApplied = false;
					if (candidate != null) {
						// Based on this variable a candidate will save or
						// update
						isCandidateExists = true;
						isCandidateApplied = onlineCgiCandidateService.isCandidateAlreadyApplied(jobOrder.getId(), candidate.getId());
					}

					if (isCandidateApplied) {
						msgMap.put("errMsg", "You have already applied to this job");
						return new ResponseEntity<>(msgMap, HttpStatus.OK);
					} else {
						log.info("from else");
						if (candidate == null) {
							candidate = new Candidate();
						}

						MultipartFile originalDoc = files.get(0);

						String ext = FilenameUtils.getExtension(originalDoc.getOriginalFilename());
						if (ext.equals("docx"))
							candidate.setDocument(originalDoc.getBytes(), ContentType.DOCX);
						else if (ext.equals("doc"))
							candidate.setDocument(originalDoc.getBytes(), ContentType.MS_WORD);
						else if (ext.equals("txt"))
							candidate.setDocument(originalDoc.getBytes(), ContentType.PLAIN);
						else if (ext.equals("html") || ext.equals("htm"))
							candidate.setDocument(originalDoc.getBytes(), ContentType.HTML);
						else if (ext.equals("rtf"))
							candidate.setDocument(originalDoc.getBytes(), ContentType.RTF);
						else
							candidate.setDocument(originalDoc.getBytes(), ContentType.PDF);
						candidate.parseDocument();
						try {
							log.info("candidate fname>>" + candidateDto.getFirstName());
							if (candidateDto.getIsAuthFlag()) {
								candidate.setAuthFlag(Constants.Accepted);
							} else {
								candidate.setAuthFlag(Constants.Rejected);
							}

							candidate.setDeleteFlag(0);
							candidate.setBlock(false);
							candidate.setHot(false);
							candidate.setPortalViewedBy("");
							candidate.setCreatedUser(candidateDto.getCreatedUser());
							candidate.setPortalEmail("NA");
							candidate.setFirstName(candidateDto.getFirstName());
							candidate.setLastName(candidateDto.getLastName());
							candidate.setVisaType(candidateDto.getVisaType());
							candidate.setPresentRate(candidateDto.getPresentRate());
							candidate.setPhone(candidateDto.getPhoneNumber());
							candidate.setPhoneAlt(candidateDto.getAltPhoneNumber());
							candidate.setExpectedRate(candidateDto.getExpectedRate());
							candidate.setEmail(candidateDto.getEmail());

							candidateService.saveCandidateFromOpenJobOrder(candidate, jobOrder, isCandidateExists, candidateDto.getCreatedUser());

						}

						catch (Exception e) {
							e.printStackTrace();
							log.error(e.getMessage(), e);
						} finally {
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	@RequestMapping(value = "/hotComment", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> saveHotComment(HttpServletRequest request, @RequestBody final CandidateBlockHotVo jobOrderVo) {
		if (Utils.getLoginUserId(request) != null) {
			StatusMessage status = new StatusMessage();
			JobOrder jobOrder = jobOrderService.getJobOrder(Integer.parseInt(jobOrderVo.getCandidateId()), false, false);
			if (jobOrder != null) {

				jobOrder.setUpdatedOn(new Date());
				jobOrder.setUpdatedBy(Utils.getLoginUserId(request));

				if (jobOrder.isHot()) {
					jobOrder.setHot(!jobOrder.isHot());
					jobOrder.setReason(jobOrderVo.getReason().getBytes());
					status.setStatusMessage("job Order removed from hotlist");
				} else {
					jobOrder.setHot(!jobOrder.isHot());
					jobOrder.setReason(jobOrderVo.getReason().getBytes());
					if (Utils.getLoginUser(request) != null) {
						jobOrderService.sendHotJobMail(jobOrder, "All");
					}
					status.setStatusMessage("job Order added to hotlist");
				}
				try {
					jobOrderService.updateJobOrder(jobOrder);
					status.setStatusCode(String.valueOf(200));
				} catch (ServiceException e) {
					e.printStackTrace();
					log.error(e.getMessage(), e);
				}
			}
			return new ResponseEntity<>(status, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/resendEmails/{jobOrderId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> resendEmails(@PathVariable("jobOrderId") String jobOrderId, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			JobOrder jobOrder = jobOrderService.getJobOrder(Integer.parseInt(jobOrderId), false, false);
			jobOrderService.sendHotJobMail(jobOrder, "All");
			return new ResponseEntity<>(null, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	@RequestMapping(value = "/test", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getPossibleHotJobOrders(HttpServletRequest request) {
		ReportwiseDto reportwiseDto=new ReportwiseDto();
//		reportwiseDto.setYear(2016);
		
		Object obj = userService.getServedDatesOfUser(Arrays.asList("10-17-2015"), Arrays.asList("10-17-2016"), false, reportwiseDto.getYears(),
				"Devasya");
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
  @RequestMapping(value = "/saveClient", method = RequestMethod.POST, produces = "application/json")
  public @ResponseBody ResponseEntity<?> saveOrUpdateClient(@RequestBody Map<String, String> map, HttpServletRequest request){
	  if(Utils.getLoginUserId(request)!=null){
		  String clientName=map.get("clientName");
		  String updatedBy=Utils.getLoginUserId(request);
		  jobOrderService.addClient(clientName,updatedBy);
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
		  jobOrderService.updateClient(oldClientName,newClientName,updatedBy);
		  return new ResponseEntity<>(null, HttpStatus.OK);
	  }else{
		  log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	  }
  }
  
  @SuppressWarnings("unchecked")
	@RequestMapping(value = "/getPendingJobOrders", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getPendingJobOrders(@RequestBody JobOrderSearchDto jobOrderSearchDto, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			long startTime = Calendar.getInstance().getTimeInMillis();
			List<JobOrderDto> jobOrders = null;

			log.info("Method has started.. at : " + startTime);

			if (!Utils.isNull(jobOrderSearchDto)) {
				getJobOrderSearchDtoInfo(jobOrderSearchDto);

				jobOrders = jobOrderService.findJobOrders(jobOrderSearchDto, Utils.getLoginUser(request), 0, 0);
			}
			long endTime = Calendar.getInstance().getTimeInMillis();
			log.info("Time taken to execute this method : " + (endTime - startTime) + " ms");
			return new ResponseEntity<>(jobOrders, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
}

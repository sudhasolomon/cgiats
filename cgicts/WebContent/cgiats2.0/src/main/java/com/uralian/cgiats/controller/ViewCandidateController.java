/**
 * 
 */
package com.uralian.cgiats.controller;

import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.util.Log;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uralian.cgiats.dto.CandidateDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.dto.SubmittalEventDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.MobileCgiCandidates;
import com.uralian.cgiats.model.OnlineCgiCandidates;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalEvent;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.JobOrderService;
import com.uralian.cgiats.service.MobileCgiCandidateService;
import com.uralian.cgiats.service.OnlineCgiCandidateService;
import com.uralian.cgiats.util.Utils;

/**
 * @author Sreenath
 *
 */
@Controller
@RequestMapping(value = "viewCandidateController")
public class ViewCandidateController {

	protected final org.slf4j.Logger LOG = LoggerFactory.getLogger(getClass());
	private OnlineCgiCandidates onlineCgiCandidates;
	private MobileCgiCandidates mobileCgiCandidates;

	/**
	 * candidateService
	 */
	@Autowired
	private CandidateService candidateService;
	/**
	 * jobOrderService
	 */
	@Autowired
	private JobOrderService jobOrderService;

	/**
	 * onlineCgiCanidateService
	 */
	@Autowired
	private OnlineCgiCandidateService onlineCgiCanidateService;

	/**
	 * mobileCgiCandidateService
	 */
	@Autowired
	private MobileCgiCandidateService mobileCgiCandidateService;

	@RequestMapping(value = "/approveOnlineCandidates", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> approveOnlineCandidates(Principal principal, HttpServletRequest request, HttpServletResponse response) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				final String candidateId = request.getParameter("candidateId");
				final String jobOrderId = request.getParameter("jobOrderId");
				final String onlineCandidateId = request.getParameter("onlineCandidateId");

				if (!Utils.isBlank(candidateId) && !Utils.isBlank(jobOrderId)) {
					onlineCgiCandidates = onlineCgiCanidateService.getOnlineCgiCandidate(Integer.parseInt(candidateId), Integer.parseInt(jobOrderId));
					Candidate candObj = candidateService.getCandidate(Integer.parseInt(candidateId), true, true);
				 
					SubmittalDto submittalDto = new SubmittalDto();

					List<SubmittalEventDto> eventDtoList = new ArrayList<SubmittalEventDto>();
					SubmittalEventDto eventDto = new SubmittalEventDto();

					eventDto.setCreatedBy(Utils.getLoginUserId(request));
					eventDto.setStatus(SubmittalStatus.SUBMITTED.name());
					eventDto.setNotes("New Submittal created.");
					eventDtoList.add(eventDto);
					submittalDto.setSubmittalEventHistoryDtoList(eventDtoList);
					submittalDto.setStatus("SUBMITTED");
				    submittalDto.setCreatedBy(Utils.getLoginUserId(request));
					submittalDto.setJobOrderId(jobOrderId);
					submittalDto.setCandidateId(Integer.parseInt(candidateId));
					jobOrderService.saveOrUpdateSubmittal(submittalDto);
					if (!Utils.isNull(onlineCgiCandidates)) {
						onlineCgiCandidates.setCandidateId(candObj);
						onlineCgiCandidates.setResumeStatus("APPROVED");
						onlineCgiCandidates.setUpdatedBy(Utils.getLoginUserId(request));
						onlineCgiCandidates.setUpdatedOn(new Date());
						onlineCgiCanidateService.updateCandidate(onlineCgiCandidates);
					}
					return new ResponseEntity<>(HttpStatus.OK);
				} else {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}

	@RequestMapping(value = "/approveMobileCandidates", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> approveMobileCandidates(Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {
			if (Utils.getLoginUserId(request) != null) {
				final String candidateId = request.getParameter("candidateId");
				final String jobOrderId = request.getParameter("jobOrderId");
				final String userId = "";
				if (!Utils.isBlank(candidateId) && !Utils.isBlank(jobOrderId)) {
					mobileCgiCandidates = mobileCgiCandidateService.getMobileCgiCandidate(Integer.parseInt(candidateId), Integer.parseInt(jobOrderId));
					Candidate candObj = candidateService.getCandidate(Integer.parseInt(candidateId), true, true);
					JobOrder jobOrder = jobOrderService.getJobOrder(Integer.parseInt(jobOrderId), true, true);
					Submittal submittal = new Submittal();
					SubmittalEvent event = new SubmittalEvent();
					submittal.setCandidate(candObj);
					event.setCreatedBy(userId);
					event.setCreatedDate(new Date());
					event.setStatus(SubmittalStatus.SUBMITTED);
					event.setNotes("New Submittal created.");
					event.setSubmittal(submittal);
					submittal.addEvent(event);
					event = new SubmittalEvent();
					submittal.setCreatedBy(userId);
					event.setCreatedDate(new Date());
					jobOrder.addSubmittal(submittal);
					submittal.setJobOrder(jobOrder);
					if (!Utils.isNull(mobileCgiCandidates)) {
						mobileCgiCandidates.setResumeStatus("APPROVED");
						mobileCgiCandidates.setUpdatedBy(userId);
						mobileCgiCandidates.setUpdatedOn(new Date());
						mobileCgiCandidateService.updateCandidate(mobileCgiCandidates);
					}
					submittal = null;
					event = null;
					return new ResponseEntity<>(HttpStatus.OK);
				} else {

					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			} else {
				LOG.error("User must login");
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
		} catch (Exception e) {
			LOG.error("Error" + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * @param principal
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/rejectOnlineCandidates", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> rejectOnlineCandidates(Principal principal, HttpServletRequest request, HttpServletResponse response) {
		try {
			if (Utils.getLoginUserId(request) != null) {
				final String candidateId = request.getParameter("candidateId");
				final String jobOrderId = request.getParameter("jobOrderId");
				String userId = "";
				if (!Utils.isBlank(candidateId) && !Utils.isBlank(jobOrderId)) {
					onlineCgiCandidates = onlineCgiCanidateService.getOnlineCgiCandidate(Integer.parseInt(candidateId), Integer.parseInt(jobOrderId));
					onlineCgiCandidates.setResumeStatus("REJECTED");
					onlineCgiCandidates.setUpdatedBy(userId);
					onlineCgiCandidates.setUpdatedOn(new Date());
					onlineCgiCanidateService.updateCandidate(onlineCgiCandidates);
					return new ResponseEntity<>(HttpStatus.OK);
				} else {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			} else {
				LOG.error("User must login");
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
		} catch (Exception e) {
			LOG.error("Error" + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/rejectMobileCandidates", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> rejectMobileCandidates(HttpServletRequest request, HttpServletResponse response) {
		try {
			if (Utils.getLoginUserId(request) != null) {
				final String candidateId = request.getParameter("candidateId");
				final String jobOrderId = request.getParameter("jobOrderId");
				String userId = "";
				if (!Utils.isBlank(candidateId) && !Utils.isBlank(jobOrderId)) {
					mobileCgiCandidates = mobileCgiCandidateService.getMobileCgiCandidate(Integer.parseInt(candidateId), Integer.parseInt(jobOrderId));
					mobileCgiCandidates.setResumeStatus("REJECTED");
					mobileCgiCandidates.setUpdatedBy(userId);
					mobileCgiCandidates.setUpdatedOn(new Date());
					mobileCgiCandidateService.updateCandidate(mobileCgiCandidates);
					return new ResponseEntity<>(HttpStatus.OK);
				} else {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			} else {
				LOG.error("User must login");
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
		} catch (Exception e) {
			LOG.error("Error" + e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * This Method Will get Data Based On Created By Like CGI,PortalResume,
	 * 
	 * @param createdBy
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/viewMobileAndOnlineCandidates", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> viewMobileAndOnlineCandidates(@RequestBody CandidateSearchDto viewDetails,
			@RequestParam(value = "pageNumber") final String pageNumber, @RequestParam(value = "pageSize") final String pageSize, HttpServletRequest request)
					throws ParseException {
		if (Utils.getLoginUserId(request) != null) {
			UserDto userDto = Utils.getLoginUser(request);
			Log.info("page Number " + pageNumber + " page Size " + pageSize);
			if (!Utils.isBlank(pageNumber) && !Utils.isBlank(pageSize)) {
				int firstResult = Integer.parseInt(pageSize) * (Integer.parseInt(pageNumber) - 1);
				int maxResult = Integer.parseInt(pageSize);
				viewDetails.setStartPosition(firstResult);
				viewDetails.setMaxResults(maxResult);
			}
			try {

				final List<?> candidates = candidateService.getMobileAndOnlineResumens(viewDetails, userDto);
				if (!Utils.isEmpty(candidates))
					return new ResponseEntity<>(candidates, HttpStatus.OK);
				else
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} catch (Exception e) {
				LOG.error("Error" + e.getMessage());
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}

	/**
	 * This Method Will get Data Based On Created By Like CGI,PortalResume,
	 * 
	 * @param createdBy
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/viewCandidate", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> viewCandidateDetails(@RequestBody CandidateSearchDto viewDetails, @RequestParam("pageSize") String pageSize,
			@RequestParam("pageNumber") String pageNumber, HttpServletRequest request) throws ParseException {
		if (Utils.getLoginUserId(request) != null) {
			Log.info("page Number " + pageNumber + " page Size " + pageSize);

			if (!Utils.isBlank(pageNumber) && !Utils.isBlank(pageSize)) {
				int firstResult = Integer.parseInt(pageSize) * (Integer.parseInt(pageNumber) - 1);
				int maxResult = Integer.parseInt(pageSize);
				viewDetails.setStartPosition(firstResult);
				viewDetails.setMaxResults(maxResult);
			}

			try {
				final List<?> candidates = candidateService.getPortalCandidates(viewDetails);
				if (!Utils.isEmpty(candidates))
					return new ResponseEntity<>(candidates, HttpStatus.OK);
				else
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} catch (Exception e) {
				LOG.error("Error" + e.getMessage());
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	@RequestMapping(value = "getMissingDataCandidates", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<List<?>> getMissingDataCandidates(@RequestBody CandidateSearchDto criteria, @RequestParam("pageSize") String pageSize,
			@RequestParam("pageNumber") String pageNumber, HttpServletRequest request) {
		if(Utils.getLoginUserId(request)!=null ){
			if(!Utils.isBlank(pageNumber) && !Utils.isBlank(pageSize)){
				int firstResult = Integer.parseInt(pageSize) * (Integer.parseInt(pageNumber) - 1);
				int maxResult = Integer.parseInt(pageSize);
				criteria.setStartPosition(firstResult);
				criteria.setMaxResults(maxResult);
			}
			List<CandidateDto> missedCandidates =  candidateService.getBlankCandidates(criteria);
//			if(!Utils.isEmpty(missedCandidates)){
			return new ResponseEntity<List<?>>(missedCandidates,HttpStatus.OK);
//			}else{
//				return new ResponseEntity<List<?>>(HttpStatus.OK);
//			}
		}
		else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
}

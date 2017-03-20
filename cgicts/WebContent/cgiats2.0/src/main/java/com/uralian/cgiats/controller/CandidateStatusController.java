package com.uralian.cgiats.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.uralian.cgiats.dto.CandidateStatusesDto;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.SubmittalService;
import com.uralian.cgiats.util.Utils;

@Controller
@RequestMapping("candidateStatus")
public class CandidateStatusController {
	protected final org.slf4j.Logger LOG = LoggerFactory.getLogger(getClass());
	@Autowired
	private CandidateService service;
	@Autowired
	private SubmittalService submittalService;

	@RequestMapping(value = "/getCandidateStatusDetails", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<List<?>> getCandidateStatusDetails(@RequestBody CandidateSearchDto statusDto, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			List<CandidateDto> candidateList = null;
			if (!Utils.isBlank(statusDto.getStatus())) {
				candidateList = service.findCandidatesBasedOnStatus(statusDto, 0, 0);
			}
			return new ResponseEntity<List<?>>(candidateList, HttpStatus.OK);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getOutOfProjectDetails", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<List<?>> getOutOfProjectDetails(@RequestBody CandidateSearchDto Dto, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			List<CandidateDto> candidateList = null;

			if (!Utils.isBlank(Dto.getStatus())) {
				candidateList = submittalService.getOutofProjCandidates(Dto);
			}
			return new ResponseEntity<List<?>>(candidateList, HttpStatus.OK);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getCandidateStatusById", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<List<?>> getCandidateStatusById(@RequestParam("candidateId") String candidateId, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			List<CandidateStatusesDto> candidateStatus = null;
			if (!Utils.isBlank(candidateId)) {
				candidateStatus = service.getCandidateStatusListByCandidateId(Integer.parseInt(candidateId));
			}
			return new ResponseEntity<List<?>>(candidateStatus, HttpStatus.OK);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

}

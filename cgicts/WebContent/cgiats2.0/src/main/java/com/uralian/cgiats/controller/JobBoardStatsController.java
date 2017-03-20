package com.uralian.cgiats.controller;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.util.Utils;

@Controller
@RequestMapping(value = "jobBoardStats")
public class JobBoardStatsController {
	
	@Autowired
	private CandidateService candidateService;
	
	protected final org.slf4j.Logger LOG = LoggerFactory.getLogger(getClass());
	
	@RequestMapping(value = "/getJobBoardStatsInfo" , method = RequestMethod.POST)
	public ResponseEntity<?> getjobBoardStatsInfo(HttpServletRequest request,
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate){
		
		if (Utils.getLoginUserId(request) != null) {
			Map<String, Object> boardStats = null;
			if(!Utils.isBlank(startDate) && !Utils.isBlank(endDate)){
				Date from = Utils.convertStringToDate_India(startDate);
				Date to =  Utils.convertStringToDate_India(endDate);
				
			 boardStats = candidateService.getJobBoardStats(from ,to);
			}
			return new ResponseEntity<>(boardStats,HttpStatus.OK);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	@RequestMapping(value = "/getDiceJobStatsInfo" , method = RequestMethod.POST)
	public ResponseEntity<?> getDiceJobStatsInfo(HttpServletRequest request,
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate){
		
		if (Utils.getLoginUserId(request) != null) {
			Map<String, Object> boardStats = null;
			if(!Utils.isBlank(startDate) && !Utils.isBlank(endDate)){
				Date from = Utils.convertStringToDate_India(startDate);
				Date to =  Utils.convertStringToDate_India(endDate);
				
			 boardStats = candidateService.getPortalIdsByUser(from ,to, "Dice");
			}
			return new ResponseEntity<>(boardStats,HttpStatus.OK);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	@RequestMapping(value = "/getResumeStatsInfo" , method = RequestMethod.POST)
	public ResponseEntity<?> getResumeStatsInfo(HttpServletRequest request,
			@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate){
		
		if (Utils.getLoginUserId(request) != null) {
			Map<String, Object> boardStats = null;
			if(!Utils.isBlank(startDate) && !Utils.isBlank(endDate)){
				Date from = Utils.convertStringToDate_India(startDate);
				Date to =  Utils.convertStringToDate_India(endDate);
				
			 boardStats = candidateService.getResumeStats(from ,to, null);
			}
			return new ResponseEntity<>(boardStats,HttpStatus.OK);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
}

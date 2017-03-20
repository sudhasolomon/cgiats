package com.uralian.cgiats.controller;

import java.util.Date;
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

import com.uralian.cgiats.model.LoginAttempts;
import com.uralian.cgiats.model.LoginInfoDto;
import com.uralian.cgiats.service.LoginDetailsService;
import com.uralian.cgiats.util.TransformEntityToDto;
import com.uralian.cgiats.util.Utils;

@Controller
@RequestMapping("loginInfoReport")
public class LoginInfoReportController {
	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private LoginDetailsService loginDetailsService;
	
	@RequestMapping(value="/getLoginInfo", method = RequestMethod.POST)
	public ResponseEntity<?> getloginAttempts(HttpServletRequest request, @RequestBody LoginInfoDto infoDto){
		if (Utils.getLoginUserId(request) != null) {
		List<LoginInfoDto> loginAttemptsDto = null;
		if(!Utils.isBlank(infoDto.getStartDate()) && ! Utils.isBlank(infoDto.getEndDate())){
			Date StartDate = Utils.convertStringToDate_India(infoDto.getStartDate());
			Date EndDate = Utils.convertStringToDate_India(infoDto.getEndDate());
			loginAttemptsDto = loginDetailsService.getLoginAttemptsDetails(StartDate, EndDate);
			/*if(!Utils.isEmpty(loginAttempts)){
				loginAttemptsDto = TransformEntityToDto.getloginAttemptsDetails(loginAttempts);
			}*/
			
		}
		return new ResponseEntity<List<LoginInfoDto>>(loginAttemptsDto,HttpStatus.OK);
		}else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
}

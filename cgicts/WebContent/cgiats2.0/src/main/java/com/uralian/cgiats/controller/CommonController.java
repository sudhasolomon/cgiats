package com.uralian.cgiats.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.service.IndiaUserService;
import com.uralian.cgiats.service.UserService;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.StatusMessage;
import com.uralian.cgiats.util.Utils;

@RestController
@RequestMapping("commonController")
public class CommonController {
	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private UserService userService;
	@Autowired
	private IndiaUserService indiaUserService;
	
	@RequestMapping(value = "/getAllUsers", method = RequestMethod.GET)
	public ResponseEntity<?> getAllUsers(HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			List<UserDto> list = userService.listUsers();
			return new ResponseEntity<>(list, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getIndiaAllUsers", method = RequestMethod.GET)
	public ResponseEntity<?> getIndiaAllUsers(HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			List<UserDto> list = userService.listIndiaUsers();
			return new ResponseEntity<>(list, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getAllEMs", method = RequestMethod.GET)
	public ResponseEntity<?> getAllEMs(HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			List<UserDto> list = userService.listEMs();
			return new ResponseEntity<>(list, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getAllADMs/{emId}", method = RequestMethod.GET)
	public ResponseEntity<?> getAllADMs(HttpServletRequest request, @PathVariable("emId") String emId) {
		if (Utils.getLoginUserId(request) != null) {
			List<UserDto> list = userService.listADMs(emId);
			return new ResponseEntity<>(list, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getAllDMs", method = RequestMethod.GET)
	public ResponseEntity<?> getAllDMs(HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			List<UserDto> list = userService.listDMs();
			return new ResponseEntity<>(list, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	@RequestMapping(value = "/getAllIndiaDMsNoStatus", method = RequestMethod.GET)
	public ResponseEntity<?> getAllIndiaDMsNoStatus(HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			List<UserDto> list = userService.getAllIndiaDMsNoStatus();
			return new ResponseEntity<>(list, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	@RequestMapping(value = "/getAllDMsNoStatus", method = RequestMethod.GET)
	public ResponseEntity<?> getAllDMsNoStatus(HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			List<UserDto> list = userService.getAllDMsNoStatus();
			return new ResponseEntity<>(list, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getAllDMAndADM_OfficeLocations", method = RequestMethod.GET)
	public ResponseEntity<?> getAllDMAndADM_OfficeLocations(HttpServletRequest request,@RequestParam(value="isAuthRequired",required=false)Boolean isAuthRequired
			,@RequestParam(value="isActive",required=false)Boolean isActive
			,@RequestParam(value="isIndia",required=false)Boolean isIndia) {
		if (Utils.getLoginUserId(request) != null) {
			List<UserDto> list = userService.getAllDMAndADM_OfficeLocations(isAuthRequired!=null?Utils.getLoginUser(request):null,isActive,isIndia);
			Map<String, Object> returnMap = new HashMap<String, Object>();
			Set<String> citySet = new TreeSet<String>();
			if (list != null && list.size() > 0) {
				Collections.sort(list,new Comparator<UserDto>() {

					@Override
					public int compare(UserDto o1, UserDto o2) {
						// TODO Auto-generated method stub
						return o1.getFullName().toLowerCase().compareTo(o2.getFullName().toLowerCase());
					}
				});
				
				
				for (UserDto dto : list) {
					if (dto.getOfficeLocation() != null) {
						citySet.add(dto.getOfficeLocation());
					}
				}
				
				if(isIndia != null && isIndia){
					Iterator<UserDto> dtoIterator=list.iterator();
					while(dtoIterator.hasNext()){
						UserDto dto=dtoIterator.next();
						if(dto.getUserRole().equals(UserRole.IN_Recruiter)){
							dtoIterator.remove();
						}
					}
				}else{
					Iterator<UserDto> dtoIterator=list.iterator();
					while(dtoIterator.hasNext()){
						UserDto dto=dtoIterator.next();
						if(dto.getUserRole().equals(UserRole.Recruiter)){
							dtoIterator.remove();
						}
					}
				}
				
				
				
			}
			returnMap.put("cities", citySet);
			returnMap.put("users", list);
			return new ResponseEntity<>(returnMap, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getCurrentTime", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrentTime() {
		String currentTime = Utils.convertDateToString_HH_MM(new Date());
		Map<String, String> currentTimeMap = new HashMap<String, String>();
		currentTimeMap.put("currentTime", currentTime);
		return new ResponseEntity<>(currentTimeMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/checkTheUserExistence", method = RequestMethod.GET)
	public ResponseEntity<?> checkTheUserExistence(@RequestParam(value = "userId") String userId,@RequestParam(value = "userRole") String userRole, HttpServletRequest request)  {
		if (Utils.getLoginUserId(request) != null) {
			StatusMessage status = new StatusMessage();
			User user=null;
			if(userRole!=null && userRole.toUpperCase().startsWith(Constants.IN_PREFIX)){
			user = indiaUserService.loadUser(userId);
			}else{
			user = userService.loadUser(userId);
			}
			if (user !=null) {
				status.setStatusMessage("User Already Exits");
			} else {
				status.setStatusMessage("");
			}
			return new ResponseEntity<>(status, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	
	@RequestMapping(value = "/getAllRecruiters", method = RequestMethod.GET)
	public ResponseEntity<?> getAllRecruiters(HttpServletRequest request,@RequestParam(value="isAuthRequired",required=false)Boolean isAuthRequired,
			@RequestParam(value="isIndia",required=false)Boolean isIndia){
		if(Utils.getLoginUserId(request)!=null){
			List<UserDto> list = userService.getAllRecruiters(isAuthRequired!=null?Utils.getLoginUser(request):null, isIndia);
			return new ResponseEntity<>(list,HttpStatus.OK);
		}else{
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		}		
 
	
@RequestMapping(value = "/getClientsUnderDM", method = RequestMethod.POST)
public ResponseEntity<?> getClientsUnderDM(HttpServletRequest request, @RequestBody Map<String,String> map){
	if(Utils.getLoginUserId(request)!=null){
		Date srtDate = Utils.convertStringToDate(map.get("startDate"));
		Date endDate = Utils.getEndDate(map.get("endDate"));
		String dm = map.get("dmName");
		List<?> list = userService.getClientsUnderDM(srtDate, endDate,dm);
		return new ResponseEntity<>(list,HttpStatus.OK);
	}else{
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
	
}

@RequestMapping(value = "/getAllClients", method = RequestMethod.GET)
public ResponseEntity<?> getAllClinets(HttpServletRequest request){
	if(Utils.getLoginUserId(request)!=null){
		List<?> list = userService.getAllClinets();
		return new ResponseEntity<>(list,HttpStatus.OK);
	}else{
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
	
}

@RequestMapping(value = "/getAllLocations", method = RequestMethod.GET)
public ResponseEntity<?> getAllLocations(HttpServletRequest request){
	if(Utils.getLoginUserId(request)!=null){
		List<?> list = userService.getAllLocations();
		return new ResponseEntity<>(list,HttpStatus.OK);
	}else{
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
	
}

@RequestMapping(value = "/getAllRecruitersAndADMs", method = RequestMethod.GET)
public ResponseEntity<?> getAllRecruitersAndADMs(HttpServletRequest request,@RequestParam(value="isAuthRequired",required=false)Boolean isAuthRequired,
		@RequestParam(value="isIndia",required=false)Boolean isIndia){
	if(Utils.getLoginUserId(request)!=null){
		List<UserDto> list = userService.getAllRecruitersAndADMs(isAuthRequired!=null?Utils.getLoginUser(request):null, isIndia);
		return new ResponseEntity<>(list,HttpStatus.OK);
	}else{
		return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	}
	}


}

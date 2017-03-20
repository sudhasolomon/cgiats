package com.uralian.cgiats.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;
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

import com.uralian.cgiats.dto.ClientInfoDto;
import com.uralian.cgiats.model.Address;
import com.uralian.cgiats.model.AgencyDetails;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateInfo;
import com.uralian.cgiats.model.ClientInfo;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.service.AgencyDetailsService;
import com.uralian.cgiats.service.CandidateInfoService;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.ClientInfoService;
import com.uralian.cgiats.service.JobOrderService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.SubmittalService;
import com.uralian.cgiats.service.UserService;
import com.uralian.cgiats.util.StatusMessage;
import com.uralian.cgiats.util.TransformEntityToDto;
import com.uralian.cgiats.util.Utils;

/**
 * 
 * @author Sudha
 *
 */
@Controller
@RequestMapping(value = "consultantInfo")
public class ConsultantInfoController {

	protected final org.slf4j.Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	private transient ClientInfoService clientInfoService;

	@Autowired
	private CandidateInfoService candidateInfoService;

	@Autowired
	private transient SubmittalService submittalService;

	@Autowired
	private CandidateService service;

	@Autowired
	private JobOrderService orderService;

	@Autowired
	private UserService userService;

	@Autowired
	private AgencyDetailsService agencyDetailsService;

	@RequestMapping(value = "/getAllClientInfo", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<List<?>> getAllClientInfo(HttpServletRequest httpServletRequest) {
		LOG.info("in client info controller");
		if (Utils.getLoginUserId(httpServletRequest) != null) {

			List<ClientInfoDto> clientInfo = clientInfoService.getListClientInfo();
			 
			LOG.info("in client info controller");
			return new ResponseEntity<List<?>>(clientInfo, HttpStatus.OK);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getAllCandidateIds", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> getAllCandidateIds(HttpServletRequest httpServletRequest) {
		if (Utils.getLoginUserId(httpServletRequest) != null) {
			ClientInfoDto infoDto = new ClientInfoDto();
			List<String> clientInfo = (List<String>) submittalService.getListStartedCandidates();
			infoDto.setCandidateIds(clientInfo);
			return new ResponseEntity<>(infoDto, HttpStatus.OK);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}

	@RequestMapping(value = "/getFillProjectDetails", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> getFillProjectDetails(HttpServletRequest httpServletRequest,
			@RequestParam(value = "id") String id, @RequestParam(value = "infoId", required = false) String infoId) {
		LOG.info("in client info controller" + id + " info id " + infoId);
		if (Utils.getLoginUserId(httpServletRequest) != null) {
			ClientInfoDto clientDto = null;
			if (infoId == "") {
				clientDto = new ClientInfoDto();
				List<?> submittalDetails = orderService.getCandidateSubmitalDetails(Integer.parseInt(id));
				if (submittalDetails != null && submittalDetails.size() > 0) {
					clientDto = fillCanidateDetails(submittalDetails);
				}
			} else {
				if (infoId != null) {
					clientDto = new ClientInfoDto();
					ClientInfo clientInfo = clientInfoService.getClientInfo(Integer.parseInt(infoId));
					AgencyDetails agencyDetails = agencyDetailsService.getAgencyDetails(Integer.parseInt(infoId));
					
					clientDto = TransformEntityToDto.getprojectInfoDetails(clientInfo, agencyDetails);
				}
			}
			return new ResponseEntity<>(clientDto, HttpStatus.OK);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}
	
	
	@RequestMapping(value = "/saveOrUpdateProjectDetails", method = RequestMethod.POST, produces = "application/json")
	public  ResponseEntity<?> saveOrUpdateProjectDetails(HttpServletRequest httpServletRequest,	@RequestBody ClientInfoDto projectDto) {
		
		if (Utils.getLoginUserId(httpServletRequest) != null) {
			StatusMessage status = new StatusMessage();
			if(!Utils.isBlank(projectDto.getCandidateId())){
				projectDto.setCreatedBy(Utils.getLoginUserId(httpServletRequest));
			if(!Utils.isBlank(projectDto.getCandidateInfoId())){
				clientInfoService.saveUpdateClient(projectDto);
				status.setStatusCode("200");
				status.setStatusMessage("Project Details Updated Successfully");
				
			}else{
				 Candidate candidate = service.getCandidate(Integer.parseInt(projectDto.getCandidateId()), false, false);
				 projectDto.setCandidate(candidate);
				 clientInfoService.saveUpdateClient(projectDto);
				 status.setStatusCode("200");
					status.setStatusMessage("Project details saved Successfully");
			}
			}
		 return new ResponseEntity<>(status, HttpStatus.OK);
		
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
	

	private ClientInfoDto fillCanidateDetails(List<?> submittalDetails) {
		// TODO Auto-generated method stub
		ClientInfoDto clientDto = new ClientInfoDto();
		Object[] sub = (Object[]) submittalDetails.get(0);
		Candidate candidate = (Candidate) sub[0];
		String jobOrderId = String.valueOf(sub[1]);
		String createdBy = (String) sub[2];
		if (candidate != null) {
			clientDto.setFirstName(Utils.replaceNullWithEmpty(candidate.getFirstName()));
			clientDto.setLastName(Utils.replaceNullWithEmpty(candidate.getLastName()));
			clientDto.setEmail(Utils.replaceNullWithEmpty(candidate.getEmail()));
			clientDto.setCandidatePhone(Utils.replaceNullWithEmpty(candidate.getPhone()));
			clientDto.setPhoneAlt(Utils.replaceNullWithEmpty(candidate.getPhoneAlt()));
			clientDto.setImmigrationStatus(candidate.getVisaType());
			Address address = candidate.getAddress();
			if (address != null) {
				clientDto.setCity(address.getCity() != null ? address.getCity() : "");
				clientDto.setState(address.getState() != null ? address.getState() : "");
				clientDto.setZipCode(address.getZipcode() != null ? address.getZipcode() : "");
				clientDto.setStreet1(address.getStreet1() != null ? address.getStreet1() : "");
				clientDto.setStreet2(address.getStreet2() != null ? address.getStreet2() : "");
			}
		}
		clientDto.setJobOrderId(Utils.replaceNullWithEmpty(jobOrderId));
		if (!Utils.isBlank(createdBy)) {
			User u = userService.loadUser(createdBy);
			if(u!=null){
			clientDto.setRecruiterFirstName(Utils.replaceNullWithEmpty(u.getFirstName()));
			clientDto.setRecruiterLastName(Utils.replaceNullWithEmpty(u.getLastName()));
			}
			String bdm = Utils.replaceNullWithEmpty(u.getAssignedBdm());
			 u = userService.loadUser(bdm);
			 if(u!=null){
			clientDto.setBdmFirstName(Utils.replaceNullWithEmpty(u.getFirstName()));
			clientDto.setBdmLastName(Utils.replaceNullWithEmpty(u.getLastName()));
			 }
		}
		return clientDto;
	}
	
	
	

}

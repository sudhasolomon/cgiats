package com.uralian.cgiats.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jfree.util.Log;
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

import com.uralian.cgiats.dto.CandidateBlockHotVo;
import com.uralian.cgiats.dto.CandidateDto;
import com.uralian.cgiats.dto.CandidateStatusesDto;
import com.uralian.cgiats.dto.CandidateVo;
import com.uralian.cgiats.dto.ResumeDto;
import com.uralian.cgiats.dto.SearchCriteria;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.model.Address;
import com.uralian.cgiats.model.CadidateSearchAudit;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateHistory;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.CandidateStatus;
import com.uralian.cgiats.model.CandidateStatuses;
import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.model.OnlineCgiCandidates;
import com.uralian.cgiats.model.OrderByType;
import com.uralian.cgiats.model.Resume;
import com.uralian.cgiats.model.ResumeAuditStatus;
import com.uralian.cgiats.model.ResumeHistory;
import com.uralian.cgiats.service.CandidateHistoryService;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.OnlineCgiCandidateService;
import com.uralian.cgiats.service.ResumeHistoryService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.StatusMessage;
import com.uralian.cgiats.util.TransformDtoToEntity;
import com.uralian.cgiats.util.TransformEntityToDto;
import com.uralian.cgiats.util.Utils;

/**
 * @author Sreenath
 *
 */

@Controller
@RequestMapping("searchResume")
public class SearchResumeController {

	protected final org.slf4j.Logger LOG = LoggerFactory.getLogger(getClass());
	@Autowired
	private CandidateService service;
	/**
	 * onlineCgiCanidateService
	 */
	@Autowired
	private OnlineCgiCandidateService onlineCgiCanidateService;

	/**
	 * candidateHistoryService
	 */
	@Autowired
	private CandidateHistoryService candidateHistoryService;

	@Autowired
	private transient ResumeHistoryService historyService;


	/**
	 * @param resume
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getSearchResumes", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<List<?>> getResumes(@RequestBody CandidateSearchDto resume, @RequestParam("pageSize") String pageSize,
			@RequestParam("pageNumber") String pageNumber, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				// Pagination Logic
				if (!Utils.isBlank(pageSize) && !Utils.isBlank(pageNumber)) {
					if (pageSize.trim().toLowerCase().contains("undefin")) {
						pageSize = "10";
					}
					if (pageNumber.trim().toLowerCase().contains("undefin")) {
						pageNumber = "1";
					}
					int firstResult = Integer.parseInt(pageSize) * (Integer.parseInt(pageNumber) - 1);
					int maxResult = Integer.parseInt(pageSize);
					resume.setStartPosition(firstResult);
					resume.setMaxResults(maxResult);
				}
				if (resume.getSortName() != null) {
					if (resume.getSortName().equalsIgnoreCase(resume.getOrderByType().name())) {
						resume.setOrderByType(OrderByType.ASC);
					} else {
						resume.setOrderByType(OrderByType.DESC);
					}
				}
				// if (resume.getFieldName() != null) {
				// for(OrderByColumn column:resume.getOrderByColumn().values()){
				// if(column.name().equalsIgnoreCase(resume.getFieldName())){
				// resume.setOrderByColumn(column);
				// }
				// }
				// }

				// if (!Utils.isBlank(resume.getQueryName()) &&
				// resume.getQueryName().length() > 0)
				if (!resume.getIsCallFromPagination()) {
					if (resume.getIsCancelBtnClicked()) {
						resume.setQueryName(null);
						resume.setQueryId(null);
					}
					saveQuery(resume, request);
				}
				List<CandidateDto> candidates = null;
				candidates = (List<CandidateDto>) service.findCandidates(resume);
				if (!Utils.isEmpty(candidates)) {
					candidates.get(0).setQueryId(resume.getQueryId());
					return new ResponseEntity<List<?>>(candidates, HttpStatus.OK);
				} else
					return new ResponseEntity<List<?>>(HttpStatus.OK);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
				return new ResponseEntity<List<?>>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * @param searchCriteria
	 * @return
	 */
	@RequestMapping(value = "/globalSearch", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<List<?>> searchCandidates(@RequestBody SearchCriteria searchCriteria, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			List<?> candidates = null;
			try {
				if (searchCriteria != null) {
					candidates = service.getCandidatesList(searchCriteria);
					if (!Utils.isEmpty(candidates))
						return new ResponseEntity<List<?>>(candidates, HttpStatus.OK);
					else
						return new ResponseEntity<List<?>>(HttpStatus.OK);

				} else {
					return new ResponseEntity<List<?>>(HttpStatus.OK);

				}
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
				return new ResponseEntity<List<?>>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * return candidate details based on candidate id
	 * 
	 * @param candidateId
	 * @return
	 */
	@RequestMapping(value = "/getCandidate", method = RequestMethod.POST, produces = "application/json")

	public @ResponseBody ResponseEntity<CandidateDto> getCandidate(@RequestParam(value = "candidateId") final String candidateId, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {

				final Candidate candidate = service.getCandidate(Integer.parseInt(candidateId), true, true);
				if (candidate != null) {

					final CandidateDto dto = TransformEntityToDto.getCandidateDto(candidate);

					return new ResponseEntity<CandidateDto>(dto, HttpStatus.OK);
				} else {
					return new ResponseEntity<CandidateDto>(HttpStatus.NO_CONTENT);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
				return new ResponseEntity<CandidateDto>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getCandidateForEdit", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getCandidateForEdit(@RequestParam(value = "candidateId") final String candidateId, HttpServletRequest request) throws IOException {
		if (Utils.getLoginUserId(request) != null) {
			final Candidate candidate = service.getCandidate(Integer.parseInt(candidateId), true, true);
			final CandidateVo candidateVo = new CandidateVo();
			final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
			List<CandidateStatuses> statusHistory;
			if (candidate != null) {

				candidateVo.setFirstname(candidate.getFirstName() != null ? candidate.getFirstName() : "");
				candidateVo.setLastname(candidate.getLastName() != null ? candidate.getLastName() : "");
				candidateVo.setEmail(candidate.getEmail() != null ? candidate.getEmail() : "");
				candidateVo.setTitle(candidate.getTitle() != null ? candidate.getTitle() : "");
				candidateVo.setKeySkills(candidate.getKeySkill() != null ? candidate.getKeySkill() : "");
				if (candidate.getReason() != null) {
					String statusReason = new String(candidate.getReason());
					candidateVo.setStatusReason(statusReason);
					LOG.info("Reaons" + statusReason);
				} else {
					candidateVo.setStatusReason("");
				}
				candidateVo.setJobType(candidate.getJobType() != null ? candidate.getJobType().toString() : "");
				candidateVo.setPhoneCell(candidate.getPhone() != null ? candidate.getPhone() : "");
				candidateVo.setPhoneWork(candidate.getPhoneAlt() != null ? candidate.getPhoneAlt() : "");
				candidateVo.setQualification(candidate.getPortalResumeQual() != null ? candidate.getPortalResumeQual() : "");
				candidateVo.setTotalExperience(candidate.getPortalResumeExperience() != null ? candidate.getPortalResumeExperience() : "");
				candidateVo.setLastCompany(candidate.getPortalResumeLastComp() != null ? candidate.getPortalResumeLastComp() : "");
				candidateVo.setLastPosition(candidate.getPortalResumeLastPosition() != null ? candidate.getPortalResumeLastPosition() : "");
				candidateVo.setEmploymentStatus(candidate.getEmploymentStatus() != null ? candidate.getEmploymentStatus() : "");
				candidateVo.setMinSalaryRequirement(candidate.getMinimumSalary() != null ? candidate.getMinimumSalary() : "");
				candidateVo.setPresentRate(candidate.getPresentRate() != null ? candidate.getPresentRate() : "");
				candidateVo.setExpectedRate(candidate.getExpectedRate() != null ? candidate.getExpectedRate() : "");
				candidateVo.setPortalEmail(candidate.getPortalEmail() != null ? candidate.getPortalEmail() : "");
				candidateVo.setAtsUserId(candidate.getPortalViewedBy() != null ? candidate.getPortalViewedBy() : "");
				candidateVo.setSecurityClearance(Utils.getDefaultBooleanValue(candidate.isSecurityClearance()));
				candidateVo.setOtherResumeSource(candidate.getOtherResumeSource()!=null ? candidate.getOtherResumeSource() : "");
				Address address = candidate.getAddress();
				if (address != null) {
					candidateVo.setCity(address.getCity() != null ? address.getCity() : "");
					candidateVo.setState(address.getState() != null ? address.getState() : "");
					candidateVo.setZip(address.getZipcode() != null ? address.getZipcode() : "");
					candidateVo.setAddress1(address.getStreet1() != null ? address.getStreet1() : "");
					candidateVo.setAddress2(address.getStreet2() != null ? address.getStreet2() : "");

				}
				candidateVo.setStatus(candidate.getStatus() != null ? candidate.getStatus().toString() : "");
				candidateVo.setVisaType(candidate.getVisaType());
				if (candidate.getVisaExpiredDate() != null)
					candidateVo.setVisaExpiryDate(candidate.getVisaExpiredDate() != null ? formatter.format(candidate.getVisaExpiredDate()) : "");
				candidateVo.setSkills(candidate.getSkills() != null ? candidate.getSkills() : "");
				if (candidate.getCreatedUser() != null) {
//					if (candidate.getCreatedUser().equalsIgnoreCase(Constants.CAREERBUILDER_OLD)) {
//						candidateVo.setUploadedBy(Constants.CAREERBUILDER_NEW);
//					} else if (candidate.getCreatedUser().equalsIgnoreCase(Constants.TECHFETCH_OLD)) {
//						candidateVo.setUploadedBy(Constants.TECHFETCH_NEW);
//					} else {
						candidateVo.setUploadedBy(candidate.getCreatedUser());
//					}
				} else {
					candidateVo.setUploadedBy("");
				}

				candidateVo.setBlock(candidate.isBlock());
				candidateVo.setHot(candidate.isHot());

				candidateVo.setReferenceName(candidate.getReference1());
				statusHistory = new ArrayList<CandidateStatuses>(candidate.getStatushistory());
				if (statusHistory != null && statusHistory.size() > 0) {
					List<CandidateStatusesDto> candidateStatusesDtoList = new ArrayList<CandidateStatusesDto>();
					for (CandidateStatuses status : statusHistory) {
						candidateStatusesDtoList.add(TransformEntityToDto.getCandidateStatusesDto(status));
					}
					Collections.sort(candidateStatusesDtoList, new Comparator<CandidateStatusesDto>() {
						@Override
						public int compare(CandidateStatusesDto dto1, CandidateStatusesDto dto2) {
							return (dto2.getCreatedDate()).compareTo((dto1.getCreatedDate()));
						}
					});

					candidateVo.setStatusHistory(candidateStatusesDtoList);
				}

				if (!Utils.isNull(candidate.getResume())) {
					String content = null;
					Resume resume = candidate.getResume();
					if (resume.getParsedText() != null) {
						candidateVo.setResumeContent(resume.getParsedText());
					} else {
						candidateVo.setResumeContent("");
					}

					byte[] oringinalByte = resume.getDocument();
					if (oringinalByte != null) {
						candidateVo.setOriginalResumeUpdatedMsg(
								Utils.getDocumentDetails(candidate.getResume().getOriginalLastUpdate(), oringinalByte, candidate.getDocumentType()));
					}
					byte[] rtrByte = resume.getRtrDocument();
					if (rtrByte != null) {
						ByteArrayInputStream rtrByteStream = new ByteArrayInputStream(rtrByte);
						content = Utils.parseFile(Utils.getFileMimeTypeByEnum(candidate.getRtrDocumentType().name()), rtrByteStream);
						candidateVo.setRtrContent(content);
						candidateVo.setRtrDocumentUpdatedMsg(
								Utils.getDocumentDetails(candidate.getResume().getRtrLastUpdate(), rtrByte, candidate.getRtrDocumentType()));
					} else {
						candidateVo.setRtrContent("");
					}

					byte[] cgiByte = resume.getProcessedDocument();
					if (cgiByte != null) {
						ByteArrayInputStream cgiByteStream = new ByteArrayInputStream(cgiByte);
						content = Utils.parseFile(Utils.getFileMimeTypeByEnum(candidate.getProcessedDocumentType().name()), cgiByteStream);
						candidateVo.setCgiContent(content);
						candidateVo.setCgiDocumentUpdatedMsg(
								Utils.getDocumentDetails(candidate.getResume().getProcessedLastUpdate(), cgiByte, candidate.getProcessedDocumentType()));
					} else {
						candidateVo.setCgiContent("");
					}

				}

			} else {
				StatusMessage status = new StatusMessage();
				status.setStatusCode(String.valueOf(201));
				status.setStatusMessage("Candidate details are not found");
				return new ResponseEntity<>(status, HttpStatus.OK);

			}

			return new ResponseEntity<>(candidateVo, HttpStatus.OK);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}

	@RequestMapping(value = "/viewCandidateDetails", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> viewCandidateDetails(@RequestParam(value = "candidateId") final String candidateId, HttpServletRequest request)
			throws IOException {
		if (Utils.getLoginUserId(request) != null) {
			saveResumeHistory(Constants.viewed, candidateId, Constants.OTHERS, request);
			CandidateDto candidateDto = service.viewCandidate(Integer.parseInt(candidateId));
			// final Candidate candidate =
			// service.getCandidate(Integer.parseInt(candidateId), true, true);
			// if (candidate != null) {
			// candidateDto = TransformEntityToDto.getCandidateDto(candidate);
			// }
			return new ResponseEntity<>(candidateDto, HttpStatus.OK);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}

	/**
	 * Deleting candidate based on candidate id and return message
	 * 
	 * Logical deletion of candidate means changing delete_flg 0 to 1
	 * 
	 * @param candidateId
	 * @param request
	 * @param response
	 * @return
	 */

	@RequestMapping(value = "/deleteCandidate", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> deleteCandidate(@RequestBody Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {
		if (Utils.getLoginUserId(request) != null) {
			final StatusMessage resStatus = new StatusMessage();

			try {

				if (params.size() > 0) {
					final Candidate candidate = service.getCandidate(Integer.parseInt(params.get("candidateId")), true, true);

					if (candidate != null && candidate.getStatus() == CandidateStatus.Available) {
						if (candidate.getId() != null) {
							final OnlineCgiCandidates onlineCgiCandidates = onlineCgiCanidateService.getOnlineCgiCandidate(candidate.getId(), 0);
							if (onlineCgiCandidates != null && onlineCgiCandidates.getCandidateId() != null && onlineCgiCandidates.getResumeStatus() != null
									&& !onlineCgiCandidates.getResumeStatus().trim().equals("")) {
								onlineCgiCandidates.setResumeStatus("DELETED");
								onlineCgiCandidates.setUpdatedBy(Utils.getLoginUserId(request));
								onlineCgiCandidates.setUpdatedOn(new Date());
								onlineCgiCanidateService.updateCandidate(onlineCgiCandidates);
							}
						}
						candidate.setDeleteFlag(1);

						final CandidateHistory candidateHistory = new CandidateHistory(candidate);

						if (candidate != null && candidate.getId() != null) {
							candidateHistory.setCandidate(candidate);
							candidateHistory.setStatus("Deleted");
							// need to add
							candidateHistory.setReason(params.get("reason"));
							candidateHistoryService.saveCandidateHistory(candidateHistory);
						}
						service.updateCandidate(candidate);
						// need to add deleteComment = "";

						resStatus.setStatusCode(String.valueOf(200));
						resStatus.setStatusMessage("Candidate Deleted Successfully");
						return new ResponseEntity<>(resStatus, HttpStatus.OK);
					} else {
						resStatus.setStatusCode(String.valueOf(500));
						resStatus.setStatusMessage("Canidate Can't Delete");
						return new ResponseEntity<>(resStatus, HttpStatus.OK);
					}
				} else {
					return new ResponseEntity<>(resStatus, HttpStatus.OK);
				}
			} catch (ServiceException e) {
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
				resStatus.setStatusCode(String.valueOf(500));
				resStatus.setStatusMessage(e.getMessage());
				return new ResponseEntity<>(resStatus, HttpStatus.INTERNAL_SERVER_ERROR);

			}
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}

	/**
	 * saving the candidate and return success message
	 * 
	 * @param candidateVo
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/saveCandidate", method = RequestMethod.POST, consumes = "multipart/form-data")
	@ResponseBody
	public ResponseEntity<?> saveCandidate(@RequestPart(value = "file", required = true) List<MultipartFile> files,
			@RequestParam(value = "candidate") Object data, @RequestParam(value = "fileNames") String fileNames, HttpServletRequest request)
					throws IOException {
		if (Utils.getLoginUserId(request) != null) {
			final Candidate candidate = new Candidate();
			String requestData = (String) data;
			CandidateVo candidateVo = new ObjectMapper().readValue(requestData, CandidateVo.class);
			try {
				if (candidateVo != null) {
					List<Candidate> existEmail = service.getCandidateDetails(candidateVo.getEmail() != null ? candidateVo.getEmail().trim() : "");
					if (existEmail.size() == 0) {
						final String actionType = "create";
						TransformDtoToEntity.getCandidate(candidate, candidateVo, files, actionType, fileNames);

						service.saveCandidate(candidate);
						saveCandidateStatus(candidate);
						
						Utils.deleteWriteLock();
						
						return new ResponseEntity<>(HttpStatus.OK);

					} else {
						return new ResponseEntity<>(HttpStatus.NO_CONTENT);

					}

				}

			} catch (

			ServiceException e)

			{
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			return null;
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * saving the candidate and return success message
	 * 
	 * @param candidateVo
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/saveOnlineCandidate", method = RequestMethod.POST, consumes = "multipart/form-data")
	@ResponseBody
	public ResponseEntity<?> saveOnlineCandidate(@RequestPart(value = "file", required = true) List<MultipartFile> files,
			@RequestParam(value = "candidate") Object data, @RequestParam(value = "fileNames") String fileNames, HttpServletRequest request)
					throws IOException {
		final Candidate candidate = new Candidate();
		String requestData = (String) data;
		CandidateVo candidateVo = new ObjectMapper().readValue(requestData, CandidateVo.class);
		try {
			if (candidateVo != null) {
				List<Candidate> existEmail = service.getCandidateDetails(candidateVo.getEmail() != null ? candidateVo.getEmail().trim() : "");
				if (existEmail.size() == 0) {
					final String actionType = "create";
					TransformDtoToEntity.getCandidate(candidate, candidateVo, files, actionType, fileNames);

					service.saveCandidate(candidate);
					saveCandidateStatus(candidate);
					return new ResponseEntity<>(HttpStatus.OK);

				} else {
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);

				}

			}

		} catch (ServiceException e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return null;
	}

	/**
	 * updating candidate
	 * 
	 * @param candidateVo
	 * @return
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value = "/updateCandidate", method = RequestMethod.POST, produces = "application/json", consumes = "multipart/form-data")
	@ResponseBody
	public ResponseEntity<?> updateCanidate(@RequestPart(value = "file", required = true) List<MultipartFile> files,
			@RequestParam(value = "candidate") Object data, @RequestParam(value = "fileNames") String fileNames, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				String requestData = (String) data;
				CandidateVo candidateVo = new ObjectMapper().readValue(requestData, CandidateVo.class);
				candidateVo.setUpdatedBy(Utils.getLoginUserId(request));
				final Candidate candidate = service.getCandidate(Integer.parseInt(candidateVo.getCandidateId()), true, true);
				if (candidateVo != null) {

					final String actionType = "update";
					TransformDtoToEntity.getCandidate(candidate, candidateVo, files, actionType, fileNames);
				} else {
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);

				}
				service.updateCandidate(candidate);
				Utils.deleteWriteLock();
				if(candidateVo.getPageName()!=null && candidateVo.getPageName().trim().equalsIgnoreCase(Constants.MISSING_DATA)){
					saveResumeHistory(ResumeAuditStatus.UPDATED.toString(), candidateVo.getCandidateId(), null, request);
				}
				
				return new ResponseEntity<>(HttpStatus.OK);
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

	/**
	 * @param candidateVo
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "/saveBlockList", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> saveBlock(@RequestBody final CandidateBlockHotVo candidateVo, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			Candidate candidate = service.getCandidate(Integer.parseInt(candidateVo.getCandidateId()), true, true);
			final CandidateDto dto = new CandidateDto();
			final CandidateHistory candidateHistory = new CandidateHistory(candidate);
			final StatusMessage status = new StatusMessage();

			try {
				if (candidate != null && candidate.getId() != null) {
					candidate.setUpdatedOn(new Date());
					candidate.setUpdatedBy(Utils.getLoginUserId(request));
					candidateHistory.setCandidate(candidate);
					candidateHistory.setReason(candidateVo.getReason());
					candidateHistory.setCreatedBy(Utils.getLoginUserId(request));
					candidateHistory.setCreatedOn(new Date());
					if (candidate.isBlock()) {
						candidateHistory.setStatus("UnBlack");
						candidate.setBlock(!candidate.isBlock());
						candidateHistoryService.saveCandidateHistory(candidateHistory);
						status.setStatusMessage("Candidate removed from Block List");

					} else {
						candidateHistory.setStatus("Black");
						candidate.setBlock(!candidate.isBlock());
						candidateHistoryService.saveCandidateHistory(candidateHistory);
						status.setStatusMessage("Candidate added to Block List");
						try {
							// Need To Be Set Current Login User
							if (Utils.getLoginUser(request) != null) {
								candidateHistoryService.sendBlackListMail(candidateHistory, Utils.getLoginUser(request).getOfficeLocation());
							}
						} catch (Exception e) {
							e.printStackTrace();
							LOG.error(e.getMessage(), e);
						}

					}
					Candidate responseFromDb = service.updateCandidate(candidate);

					status.setStatusCode(String.valueOf(200));
					return new ResponseEntity<>(status, HttpStatus.OK);
				} else {
					status.setStatusMessage("Candidate dosen't exists");
					status.setStatusCode(String.valueOf(200));
					return new ResponseEntity<>(status, HttpStatus.OK);
				}

			} catch (Exception e) {
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
			}
			return null;
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * 
	 * @param candidateVo
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "/saveHotComment", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> saveHotComment(@RequestBody final CandidateBlockHotVo candidateVo, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			Candidate candidate = service.getCandidate(Integer.parseInt(candidateVo.getCandidateId()), true, true);

			final CandidateHistory candidateHistory = new CandidateHistory(candidate);
			final StatusMessage status = new StatusMessage();
			final CandidateDto dto = new CandidateDto();

			try {
				if (candidate != null && candidate.getId() != null) {
					candidate.setUpdatedOn(new Date());
					candidate.setUpdatedBy(Utils.getLoginUserId(request));
					candidateHistory.setCreatedBy(Utils.getLoginUserId(request));
					candidateHistory.setCreatedOn(new Date());
					candidateHistory.setCandidate(candidate);
					candidateHistory.setReason(candidateVo.getReason());
					if (candidate.isHot()) {
						candidate.setHot(!candidate.isHot());
						candidateHistory.setStatus("UnHot");
						candidateHistoryService.saveCandidateHistory(candidateHistory);
						status.setStatusMessage("Candidate removed from Hot List");

					} else {
						candidate.setHot(!candidate.isHot());
						candidateHistory.setStatus("Hot");
						candidateHistoryService.saveCandidateHistory(candidateHistory);
						status.setStatusMessage("Candidate added to Hot List");

						if (Utils.getLoginUser(request) != null) {
							candidateHistoryService.sendHotMail(candidateHistory, Utils.getLoginUser(request).getOfficeLocation());
						}
					}
					Candidate responseFromDb = service.updateCandidate(candidate);

					status.setStatusCode(String.valueOf(200));
					return new ResponseEntity<>(status, HttpStatus.OK);
				} else {
					status.setStatusMessage("Candidate dosen't exists");
					status.setStatusCode(String.valueOf(200));
					return new ResponseEntity<>(status, HttpStatus.OK);
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

	/**
	 * @param candidateId
	 * @return
	 */
	@RequestMapping(value = "/getSubmittalsInfo", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getSubmittalsInfoByCandidateId(@RequestParam("candidateId") final String candidateId, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				final Integer canId = Integer.parseInt(candidateId);
				final List<SubmittalDto> submittal = service.getSubmittalsInfoByCandidateId(canId);
				if (!Utils.isEmpty(submittal))
					return new ResponseEntity<>(submittal, HttpStatus.OK);
				else

					return new ResponseEntity<>(HttpStatus.OK);

			} catch (ServiceException e) {
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
				return new ResponseEntity<>("Internal Error", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * by using this we can index candidate pojo using hibernate lucene api
	 */
	@RequestMapping(value = "/reindex", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public void reindex() {

		service.reindexCandidates(null, true);

	}

	@RequestMapping(value = "/getSearchQueries", method = RequestMethod.GET)
	public ResponseEntity<?> getSearchQueries(@RequestParam("queryname") final String queryname, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			final String currentLoginUser = "";
			final String queryName = queryname;

			Calendar fromDate = Calendar.getInstance();
			fromDate.add(Calendar.DATE, -10);

			Date toDate = new Date();
			List<CadidateSearchAudit> searchAuditLogData = service.getSearchCandidateAudit(fromDate.getTime(), toDate, currentLoginUser, queryName);
			List<CandidateSearchDto> dto = new ArrayList<CandidateSearchDto>();

			for (CadidateSearchAudit candidateSearch : searchAuditLogData) {

				CandidateSearchDto candidateSearchDto = new CandidateSearchDto();
				candidateSearchDto.setCandidateSearchId(String.valueOf(candidateSearch.getCandidateSearchId()));
				candidateSearchDto.setFirstName(candidateSearch.getFirstName());
				candidateSearchDto.setLastName(candidateSearch.getLastName());
				if (!Utils.isBlank(candidateSearch.getState()))
					candidateSearchDto.setStates(Arrays.asList(candidateSearch.getState().split(",")));
				if (!Utils.isBlank(candidateSearch.getVisaType()))
					candidateSearchDto.setVisaStats(Arrays.asList(candidateSearch.getVisaType().split(",")));
				candidateSearchDto.setEmail(candidateSearch.getEmail());
				candidateSearchDto.setTitle(candidateSearch.getTitle());
				candidateSearchDto.setKeySkill(candidateSearch.getKeySkills());
				candidateSearchDto.setResumeTextQuery(candidateSearch.getResumeText());
				if (!Utils.isBlank(candidateSearch.getEducation()))
					candidateSearchDto.setEducation(Arrays.asList(candidateSearch.getEducation().split(",")));
				candidateSearchDto.setCity(candidateSearch.getCity());
				if (candidateSearch.getFromDate() != null && candidateSearch.getToDate() != null) {
					candidateSearchDto.setStartDate(candidateSearch.getFromDate().toString());
					candidateSearchDto.setEndDate(candidateSearch.getToDate().toString());
				}
				candidateSearchDto.setPhoneNumber(candidateSearch.getPhone());
				candidateSearchDto.setQueryName(candidateSearch.getQueryName());

				dto.add(candidateSearchDto);
			}
			if (!Utils.isEmpty(dto))
				return new ResponseEntity<>(dto, HttpStatus.OK);
			else
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getSavedQueryNames", method = RequestMethod.GET)
	public ResponseEntity<?> getSavedQueryNames(HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			Calendar fromDate = Calendar.getInstance();
			fromDate.add(Calendar.DATE, -10);

			Date toDate = new Date();
			List<String> queryNameList = service.getSavedQueryNames(fromDate.getTime(), toDate, Utils.getLoginUserId(request));
			List<CandidateSearchDto> candidateSearchDtoList = null;
			if (queryNameList != null && queryNameList.size() > 0) {
				candidateSearchDtoList = new ArrayList<CandidateSearchDto>();
				for (String queryName : queryNameList) {
					CandidateSearchDto dto = new CandidateSearchDto();
					dto.setQueryName(queryName);
					candidateSearchDtoList.add(dto);
				}
			}

			if (!Utils.isEmpty(candidateSearchDtoList))
				return new ResponseEntity<>(candidateSearchDtoList, HttpStatus.OK);
			else

				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	private void saveQuery(CandidateSearchDto resume, HttpServletRequest request) {

		CadidateSearchAudit candidateSearchVo = null;
		// Saving Search Query Info Database Start

		if (resume.getQueryId() != null && resume.getQueryId().length() > 0)
			candidateSearchVo = service.getSavedQuery(Integer.parseInt(resume.getQueryId()));
		else
			candidateSearchVo = new CadidateSearchAudit();

		if (candidateSearchVo != null) {
			if (!Utils.isBlank(resume.getFirstName()))
				candidateSearchVo.setFirstName(resume.getFirstName());
			else
				candidateSearchVo.setFirstName("");
			if (!Utils.isBlank(resume.getLastName()))
				candidateSearchVo.setLastName(resume.getLastName());
			else
				candidateSearchVo.setLastName("");
			if (!Utils.isBlank(resume.getCity()))
				candidateSearchVo.setCity(resume.getCity());
			else
				candidateSearchVo.setCity("");
			if (!Utils.isBlank(resume.getState()))
				candidateSearchVo.setState(resume.getState());
			else
				candidateSearchVo.setState("");
			if (!Utils.isBlank(resume.getEmail()))
				candidateSearchVo.setEmail(resume.getEmail());
			else
				candidateSearchVo.setEmail("");
			if (!Utils.isBlank(resume.getVisa()))
				candidateSearchVo.setVisaType(resume.getVisa());
			else
				candidateSearchVo.setVisaType("");
			if (!Utils.isBlank(resume.getTitle()))
				candidateSearchVo.setTitle(resume.getTitle());
			else
				candidateSearchVo.setTitle("");
			if (!Utils.isBlank(resume.getResumeStats())) {
				candidateSearchVo.setResumeUpdate(resume.getResumeStats());
			} else {
				candidateSearchVo.setResumeUpdate("");
			}
			if (!Utils.isBlank(resume.getPhoneNumber())) {
				candidateSearchVo.setPhone(resume.getPhoneNumber());
			} else {
				candidateSearchVo.setPhone("");
			}

			candidateSearchVo.setFromDate(Utils.convertAngularStrToDate(resume.getCreated().get("startDate")));
			candidateSearchVo.setToDate(Utils.convertAngularStrToDate(resume.getCreated().get("endDate")));

			if (!Utils.isBlank(resume.getKeySkill()))
				candidateSearchVo.setKeySkills(resume.getKeySkill());
			else
				candidateSearchVo.setKeySkills("");
			if (!Utils.isBlank(resume.getResumeTextQuery()))
				candidateSearchVo.setResumeSearch(resume.getResumeTextQuery().getBytes());
			else
				candidateSearchVo.setResumeSearch("".getBytes());

			candidateSearchVo.setQueryName(resume.getQueryName());

			if (resume.getQueryId() != null && resume.getQueryId().length() > 0) {
				candidateSearchVo.setUpdatedOn(new Date());
				candidateSearchVo.setUpdatedBy(Utils.getLoginUserId(request));
			} else {
				candidateSearchVo.setCreatedOn(new Date());
				candidateSearchVo.setCreatedBy(Utils.getLoginUserId(request));

			}
			if (!Utils.isBlank(candidateSearchVo.getEducation())) {

			}

			service.saveSearchCandidateAuditDetails(candidateSearchVo);
			// Saving Search Query Info Database End
			resume.setQueryId(String.valueOf(candidateSearchVo.getCandidateSearchId()));
		}
	}

	@RequestMapping(value = "/deleteSavedQuery", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<?> deleteSavedQuery(@RequestParam(value = "queryId") String queryId, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				Integer savedQueryId = null;
				if (queryId != null)
					savedQueryId = Integer.parseInt(queryId);

				int responseFromDb = service.deleteSavedQuery(savedQueryId);

				if (responseFromDb != 0)
					return new ResponseEntity<>(HttpStatus.OK);
				else
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
				return null;
			}
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/readFileContent", method = RequestMethod.POST, produces = "application/json", consumes = "multipart/form-data")
	public ResponseEntity<Map<String, String>> readFileContent(@RequestPart(value = "file") MultipartFile file, HttpServletRequest request) {
		LOG.info("file object " + file);
		if (Utils.getLoginUserId(request) != null) {
			String content = null;
			Map<String, String> contentMap = null;
			try {
				if (!Utils.isNull(file)) {
					contentMap = new HashMap<String, String>();
					String extCgi = FilenameUtils.getExtension(file.getOriginalFilename());
					content = Utils.parseFile(extCgi, file.getInputStream());
					contentMap.put("content", content);
				} else {
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);
				}
			} catch (Exception e) {
				LOG.error("Exception" + e);
			}
			return new ResponseEntity<>(contentMap, HttpStatus.OK);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/resumeByCandidateId", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<?> getResumeByCandidateId(@RequestParam(value = "candidateId") String candidateId, @RequestParam(value = "docStats") String docStats,
			HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				saveResumeHistory(Constants.viewed, candidateId, docStats, request);

				ResumeDto resume = service.getResumeByCandidateId(candidateId);
				if (resume != null)
					return new ResponseEntity<>(resume, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/resumeByCandidateId/{candidateId}/{docStats}/{typeOfResume}", produces = "application/json", method = RequestMethod.GET)
	public void getResumeByCandidateId(@PathVariable(value = "candidateId") String candidateId, @PathVariable(value = "docStats") String docStats,
			@PathVariable("typeOfResume") String typeOfResume, HttpServletRequest request, HttpServletResponse response) {
		ResumeDto resume = service.getResumeByCandidateId(candidateId);

		if (!Utils.isBlank(candidateId)) {
			saveResumeHistory(Constants.downloaded, candidateId, docStats, request);
			byte[] bytes = null;
			ContentType contentType = null;
			if (typeOfResume != null) {
				if (typeOfResume.equalsIgnoreCase(Constants.ORGDOC)) {
					bytes = resume.getOriginalDoc();
					contentType = resume.getOriginalDocType();
				} else if (typeOfResume.equalsIgnoreCase(Constants.RTRDOC)) {
					bytes = resume.getRtrDocumentDoc();
					contentType = resume.getRtrDocumentType();
				} else if (typeOfResume.equalsIgnoreCase(Constants.PROCDOC)) {
					bytes = resume.getProcessedDocument();
					contentType = resume.getProcessedDocumentType();
				}
			}

			if (bytes != null) {
				try (OutputStream output = response.getOutputStream();) {
					ByteArrayInputStream fileInputStream = new ByteArrayInputStream(bytes);
					response.reset();
					if (contentType != null) {
						response.setContentType(contentType.getMimeType());
					} else {
						response.setContentType("application/octet-stream");
					}

					if (resume.getOriginalDocType() != null) {
						response.setContentLength((int) (bytes.length));
						response.setHeader("Content-Disposition", "attachment; filename=" + "file" + candidateId + "_" + contentType.getExtensions()[0]);
					} else {
						response.setContentLength(0);
						response.setHeader("Content-Disposition", "attachment; filename=\"" + "NoContent.txt" + "\"");
					}
					IOUtils.copyLarge(fileInputStream, output);
					output.flush();
				} catch (IOException e) {
					e.printStackTrace();
					LOG.error(e.getMessage(), e);
				}
			} else {
				response.setContentType("application/octet-stream");
				response.setContentLength(0);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + "NoContent.txt" + "\"");
			}
		}

	}

	@RequestMapping(value = "saveResumeAuditList", produces = "application/json", method = RequestMethod.POST)
	public ResponseEntity<?> saveResumeAuditList(@RequestParam(value = "candidateId") String candidateId, @RequestParam(value = "docStats") String docStats,
			@RequestParam(value = "status") String status, HttpServletRequest request) {
		if (!Utils.isBlank(candidateId)) {
			saveResumeHistory(status, candidateId, docStats, request);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private void saveResumeHistory(String status, String candidateId, String docStats, HttpServletRequest request) {
		ResumeHistory resumeHistory = new ResumeHistory();
		resumeHistory.setCandidate(Integer.parseInt(candidateId));
		resumeHistory.setDocStatus(docStats);
		resumeHistory.setStatus(status);
		resumeHistory.setCreatedOn(new Date());
		resumeHistory.setCreatedBy(Utils.getLoginUserId(request));

		try {
			historyService.saveResumeHistory(resumeHistory);
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}

	}

	@RequestMapping(value = "getUserDetails", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<?> getUserDetails(HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			Log.info("get user details controller");
			CandidateVo userDetails = service.getAllUserDetails();
			if (userDetails != null)
				return new ResponseEntity<>(userDetails, HttpStatus.OK);

			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "candidateStatusUpdate", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getCandidateStatus(@RequestBody CandidateVo candidatevo, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			Candidate candidate = new Candidate();
			candidate.setId(Integer.parseInt(candidatevo.getCandidateId()));
			for (CandidateStatus candidateStatus : CandidateStatus.values()) {
				if (candidateStatus.toString().equalsIgnoreCase(candidatevo.getStatus())) {
					candidate.setStatus(candidateStatus);
					break;
				}
			}
			CandidateStatuses status = service.getCandidateStatus(candidate);
			if (status != null) {
				if (!Utils.isEmpty(status.getReason())) {
					String statusReason = new String(status.getReason());
					candidatevo.setReason(statusReason);
				} else {
					candidatevo.setReason("");
				}
			} else {
				candidatevo.setReason("");
			}
			return new ResponseEntity<>(candidatevo, HttpStatus.OK);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	private void saveCandidateStatus(Candidate candidate) {
		CandidateStatuses status = new CandidateStatuses();
		status.setCandidate(candidate);
		status.setStatus(candidate.getStatus());
		status.setReason(candidate.getReason());
		status.setCreatedBy(candidate.getUpdatedBy());
		status.setCreatedDate(new Date());
		service.updateCandidateStatus(status);
	}
	
	@RequestMapping(value="/getCandidateByEmailId", method = RequestMethod.POST , produces="application/json")
	public @ResponseBody ResponseEntity<?> getCandidateByEmailId(@RequestParam(value="emailId") String emailId, HttpServletRequest request){
		if(Utils.getLoginUser(request)!=null){
			try{	
				
			Candidate candidateDetais = service.getCandidateFromEmail(emailId, false, false);
			CandidateDto candidateDto = new CandidateDto();
			candidateDto.setId(candidateDetais.getId().toString());
			candidateDto.setKeySkill(candidateDetais.getKeySkill()!=null? candidateDetais.getKeySkill() : "");
			candidateDto.setFullName(candidateDetais.getFullName());
			candidateDto.setTitle(candidateDetais.getTitle()!=null ? candidateDetais.getTitle() : "");
			candidateDto.setLocation(candidateDetais.getCityState()!=null ? candidateDetais.getCityState() : "");
			return new ResponseEntity<>(candidateDto,HttpStatus.OK);
			}catch(Exception e){
			
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
	}
		else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

}

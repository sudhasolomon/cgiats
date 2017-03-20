package com.uralian.cgiats.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.uralian.cgiats.dto.CandidateStatusesDto;
import com.uralian.cgiats.dto.CandidateVo;
import com.uralian.cgiats.dto.IndiaCandidateDto;
import com.uralian.cgiats.dto.ResumeDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.model.Address;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.CandidateStatus;
import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.model.IndiaCandidate;
import com.uralian.cgiats.model.IndiaCandidateHistory;
import com.uralian.cgiats.model.IndiaCandidateStatuses;
import com.uralian.cgiats.model.IndiaOnlineCgiCandidates;
import com.uralian.cgiats.model.IndiaResume;
import com.uralian.cgiats.model.OrderByColumn;
import com.uralian.cgiats.model.OrderByType;
import com.uralian.cgiats.service.IndiaCandidateHistoryService;
import com.uralian.cgiats.service.IndiaCandidateService;
import com.uralian.cgiats.service.IndiaOnlineCgiCandidateService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.StatusMessage;
import com.uralian.cgiats.util.TransformDtoToEntity;
import com.uralian.cgiats.util.TransformEntityToDto;
import com.uralian.cgiats.util.Utils;

@Controller
@RequestMapping(value = "IndiaCandidates")
public class IndiaCandidateController {
	@Autowired
	IndiaCandidateService candidateService;

	@Autowired
	IndiaCandidateHistoryService candidateHistoryService;
	@Autowired
	IndiaOnlineCgiCandidateService onlineCgiCanidateService;

	protected final org.slf4j.Logger LOG = LoggerFactory.getLogger(getClass());

	@RequestMapping(value = "/getCandidates", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<List<?>> getCandidates(@RequestBody CandidateSearchDto searchDto,
			HttpServletRequest httpServletRequest) {
		CandidateSearchDto candidateDto = new CandidateSearchDto();
		if (Utils.getLoginUserId(httpServletRequest) != null) {
			try {
				if (!Utils.isBlank(searchDto.getPageSize()) && !Utils.isBlank(searchDto.getPageNumber())) {
					int firstResult = Integer.parseInt(searchDto.getPageSize())
							* (Integer.parseInt(searchDto.getPageNumber()) - 1);
					int maxResult = Integer.parseInt(searchDto.getPageSize());
					candidateDto.setStartPosition(firstResult);
					candidateDto.setMaxResults(maxResult);
				}
				if (!Utils.isBlank(searchDto.getFieldName()) && !Utils.isBlank(searchDto.getSortName())) {
					for (OrderByColumn order : OrderByColumn.values()) {
						if (searchDto.getFieldName().toUpperCase().equals(order.name())) {
							candidateDto.setOrderByColumn(order);
							candidateDto.setFieldName(searchDto.getFieldName());
							break;
						}
					}
					if (searchDto.getSortName().toUpperCase().equals(OrderByType.ASC.getValue())) {
						candidateDto.setOrderByType(OrderByType.ASC);
						candidateDto.setSortName(searchDto.getSortName());
					} else {
						candidateDto.setOrderByType(OrderByType.DESC);
						candidateDto.setSortName(searchDto.getSortName());
					}
				} /*
					 * else {
					 * candidateDto.setOrderByColumn(OrderByColumn.UPDATEDON);
					 * candidateDto.setOrderByType(OrderByType.DESC); }
					 */

				int count = candidateService.findCandidatesCount(candidateDto);
				List<IndiaCandidate> candidates = (List<IndiaCandidate>) candidateService.findCandidates(candidateDto);
				List<IndiaCandidateDto> candidatesList = TransformEntityToDto.getIndiaCandidates(candidates, count);
				if (candidates != null)
					return new ResponseEntity<List<?>>(candidatesList, HttpStatus.OK);
				else
					return new ResponseEntity<List<?>>(HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
			}
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<List<?>>(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getSubmittals", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getSubmittalInfo(@RequestParam("candidateId") String Id,
			HttpServletRequest httpServletRequest) {
		if (Utils.getLoginUserId(httpServletRequest) != null) {
			List<SubmittalDto> submittal = null;
			if (Utils.getLoginUserId(httpServletRequest) != null) {
				final Integer candidateId = Integer.parseInt(Id);
				submittal = candidateService.getIndiaSubmittals(candidateId);
			}

			if (!Utils.isEmpty(submittal))
				return new ResponseEntity<>(submittal, HttpStatus.OK);
			else
				return new ResponseEntity<>(HttpStatus.OK);

		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/saveBlockList", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> saveBlock(@RequestBody final CandidateBlockHotVo candidateVo, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			IndiaCandidate candidate = null;
			Map<String,Object>  resultMap= candidateService.getCandidate(Integer.parseInt(candidateVo.getCandidateId()), true, true);
			if(resultMap!=null){
			candidate = (IndiaCandidate) resultMap.get(Constants.DATA);
			}
			final IndiaCandidateHistory candidateHistory = new IndiaCandidateHistory(candidate);
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
						candidate.setBlock(!candidate.isBlock());
						candidateHistory.setStatus("UnBlack");
						candidateHistoryService.saveCandidateHistory(candidateHistory);
						status.setStatusMessage("Candidate removed from Block List");
					} else {
						candidate.setBlock(!candidate.isBlock());
						candidateHistory.setStatus("Black");
						candidateHistoryService.saveCandidateHistory(candidateHistory);
						status.setStatusMessage("Candidate added to Block List");

						try {
							// Need To Be Set Current Login User
							if (Utils.getLoginUser(request) != null) {
								candidateHistoryService.sendBlackListMail(candidateHistory,
										Utils.getLoginUser(request).getOfficeLocation());
							}
						} catch (Exception e) {
							e.printStackTrace();
							LOG.error(e.getMessage(), e);
						}

					}
					candidateService.updateCandidate(candidate);
					status.setStatusCode(String.valueOf(200));
					return new ResponseEntity<>(status, HttpStatus.OK);
				} else {
					status.setStatusCode(String.valueOf(200));
					status.setStatusMessage("Candidate dose not exist");
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

	@RequestMapping(value = "/saveHotComment", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> saveHotComment(@RequestBody final CandidateBlockHotVo candidateVo,
			HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			IndiaCandidate candidate = null;
			Map<String,Object>  resultMap= candidateService.getCandidate(Integer.parseInt(candidateVo.getCandidateId()), true, true);
			if(resultMap!=null){
			candidate = (IndiaCandidate) resultMap.get(Constants.DATA);
			}
			final IndiaCandidateHistory candidateHistory = new IndiaCandidateHistory(candidate);
			final StatusMessage status = new StatusMessage();

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
							candidateHistoryService.sendHotMail(candidateHistory,
									Utils.getLoginUser(request).getOfficeLocation());
						}
					}
					candidateService.updateCandidate(candidate);
					status.setStatusCode(String.valueOf(200));
					return new ResponseEntity<>(status, HttpStatus.OK);
				} else {
					status.setStatusCode(String.valueOf(500));
					status.setStatusMessage("Candidate does not exist");
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

	@RequestMapping(value = "/resumeByCandidateId", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<?> getResumeByCandidateId(@RequestParam(value = "candidateId") String candidateId,
			HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {

				ResumeDto resume = candidateService.getResumeByCandidateId(candidateId);
				if (resume != null) {
					byte[] orgDoc = resume.getOriginalDoc();
					ByteArrayInputStream rtrByteStream = new ByteArrayInputStream(orgDoc);
					String content = Utils.parseFile(Utils.getFileMimeTypeByEnum(resume.getOriginalDocType().name()),
							rtrByteStream);
					if (orgDoc != null) {
						resume.setResumeContent(content);
					}
					return new ResponseEntity<>(resume, HttpStatus.OK);
				}
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

	@RequestMapping(value = "/deleteIndiaCandidate", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> deleteCandidate(@RequestBody Map<String, String> params, HttpServletRequest request,
			HttpServletResponse response) {
		if (Utils.getLoginUserId(request) != null) {
			final StatusMessage resStatus = new StatusMessage();

			try {

				if (params.size() > 0) {
					IndiaCandidate candidate = null;
					Map<String,Object>  resultMap= candidateService.getCandidate(Integer.parseInt(params.get("candidateId")), true, true);
					if(resultMap!=null){
					candidate = (IndiaCandidate) resultMap.get(Constants.DATA);
					}

					if (candidate != null && candidate.getStatus() == CandidateStatus.Available) {
						if (candidate.getId() != null) {
							final IndiaOnlineCgiCandidates onlineCgiCandidates = onlineCgiCanidateService
									.getOnlineCgiCandidate(candidate.getId().intValue());
							if (onlineCgiCandidates != null && onlineCgiCandidates.getCandidateId() != null
									&& onlineCgiCandidates.getResumeStatus() != null
									&& !onlineCgiCandidates.getResumeStatus().trim().equals("")) {
								onlineCgiCandidates.setResumeStatus("DELETED");
								onlineCgiCandidates.setUpdatedBy(Utils.getLoginUserId(request));
								onlineCgiCandidates.setUpdatedOn(new Date());
								onlineCgiCanidateService.updateCandidate(onlineCgiCandidates);
							}
						}
						candidate.setDeleteFlag(1);

						final IndiaCandidateHistory candidateHistory = new IndiaCandidateHistory(candidate);

						if (candidate != null && candidate.getId() != null) {
							candidateHistory.setCandidate(candidate);
							candidateHistory.setStatus("Deleted");
							// need to add
							candidateHistory.setReason(params.get("reason"));
							candidateHistoryService.saveCandidateHistory(candidateHistory);
						}
						candidateService.updateCandidate(candidate);
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

	@RequestMapping(value = "/getCandidateForEdit", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getCandidateForEdit(@RequestParam(value = "candidateId") final String candidateId,
			HttpServletRequest request) throws IOException {
		if (Utils.getLoginUserId(request) != null) {
			IndiaCandidate candidate = null;
			List<IndiaCandidateStatuses> statusHistory;
			Map<String,Object>  resultMap= candidateService.getCandidate(Integer.parseInt(candidateId), true, true);
			if(resultMap!=null){
			candidate = (IndiaCandidate) resultMap.get(Constants.DATA);
			}
			final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
			CandidateVo candidateVo = new CandidateVo();
			if (candidate != null) {
				candidateVo.setFirstname(candidate.getFirstName() != null ? candidate.getFirstName() : "");
				candidateVo.setLastname(candidate.getLastName() != null ? candidate.getLastName() : "");
				candidateVo.setEmail(candidate.getEmail() != null ? candidate.getEmail() : "");
				candidateVo.setTitle(candidate.getTitle() != null ? candidate.getTitle() : "");
				candidateVo.setJobType(candidate.getJobType() != null ? candidate.getJobType().toString() : "");
				candidateVo.setPhoneCell(candidate.getPhone() != null ? candidate.getPhone() : "");
				candidateVo.setPhoneWork(candidate.getPhoneAlt() != null ? candidate.getPhoneAlt() : "");
				candidateVo.setQualification(candidate.getPortalResumeQual() != null ? candidate.getPortalResumeQual() : "");
				candidateVo.setKeySkills(candidate.getKeySkill() != null ? candidate.getKeySkill() : "");
				
				if(candidate.getReason() != null){
					String statusResason = new String(candidate.getReason());
					candidateVo.setReason(statusResason);
				}else{
					candidateVo.setReason("");
				}
				
				candidateVo.setTotalExperience(
						candidate.getPortalResumeExperience() != null ? candidate.getPortalResumeExperience() : "");
				candidateVo.setRelevantExperience(candidate.getRelevantExperience() !=null ? candidate.getRelevantExperience() : "");
				candidateVo.setLastCompany(
						candidate.getPortalResumeLastComp() != null ? candidate.getPortalResumeLastComp() : "");
				candidateVo.setLastPosition(
						candidate.getPortalResumeLastPosition() != null ? candidate.getPortalResumeLastPosition() : "");
				candidateVo.setEmploymentStatus(
						candidate.getEmploymentStatus() != null ? candidate.getEmploymentStatus() : "");
				candidateVo.setPresentRate(candidate.getPresentRate() != null ? candidate.getPresentRate() : "");
				candidateVo.setExpectedRate(candidate.getExpectedRate() != null ? candidate.getExpectedRate() : "");
				candidateVo.setPortalEmail(candidate.getPortalEmail() != null ? candidate.getPortalEmail() : "");
				candidateVo.setAtsUserId(candidate.getPortalViewedBy() != null ? candidate.getPortalViewedBy() : "");
				candidateVo.setOtherResumeSource(
						candidate.getOtherResumeSource() != null ? candidate.getOtherResumeSource() : "");
				Address address = candidate.getAddress();
				if (address != null) {
					candidateVo.setCity(address.getCity() != null ? address.getCity() : "");
					candidateVo.setState(address.getState() != null ? address.getState() : "");
					candidateVo.setZip(address.getZipcode() != null ? address.getZipcode() : "");
					candidateVo.setAddress1(address.getStreet1() != null ? address.getStreet1() : "");
					candidateVo.setAddress2(address.getStreet2() != null ? address.getStreet2() : "");
				}
				candidateVo.setStatus(candidate.getStatus() != null ? candidate.getStatus().toString() : "");
				candidateVo.setVisaExpiryDate(
						candidate.getVisaExpiredDate() != null ? formatter.format(candidate.getVisaExpiredDate()) : "");
				candidateVo.setVisaType(candidate.getVisaType() != null ? candidate.getVisaType() : "");
				candidateVo.setUploadedBy(candidate.getCreatedUser() != null ? candidate.getCreatedUser() : "");
				
				statusHistory = new ArrayList<IndiaCandidateStatuses>(candidate.getStatushistory());
				if (statusHistory != null && statusHistory.size() > 0) {
					List<CandidateStatusesDto> candidateStatusesDtoList = new ArrayList<CandidateStatusesDto>();
					for (IndiaCandidateStatuses status : statusHistory) {
						candidateStatusesDtoList.add(TransformEntityToDto.getIndiaCandidateStatusesDto(status));
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
					IndiaResume resume = candidate.getResume();
					if ((Boolean)resultMap.get(Constants.ISRESUMEEXISTS)) {
						byte[] oringinalByte = resume.getDocument();
						if (oringinalByte != null) {
							candidateVo.setOriginalResumeUpdatedMsg(
									Utils.getDocumentDetails(candidate.getResume().getOriginalLastUpdate(),
											oringinalByte, candidate.getDocumentType()));
							ByteArrayInputStream orgByteStream = new ByteArrayInputStream(oringinalByte);
							content = Utils.parseFile(Utils.getFileMimeTypeByEnum(candidate.getDocumentType().name()),
									orgByteStream);
							candidateVo.setResumeContent(content);
						} else {
							candidateVo.setResumeContent("");
						}

						byte[] rtrByte = resume.getRtrDocument();
						if (rtrByte != null) {
							ByteArrayInputStream rtrByteStream = new ByteArrayInputStream(rtrByte);
							content = Utils.parseFile(
									Utils.getFileMimeTypeByEnum(candidate.getRtrDocumentType().name()), rtrByteStream);
							candidateVo.setRtrContent(content);
							candidateVo.setRtrDocumentUpdatedMsg(Utils.getDocumentDetails(
									candidate.getResume().getRtrLastUpdate(), rtrByte, candidate.getRtrDocumentType()));
						} else {
							candidateVo.setRtrContent("");
						}

						byte[] cgiByte = resume.getProcessedDocument();
						if (cgiByte != null) {
							ByteArrayInputStream cgiByteStream = new ByteArrayInputStream(cgiByte);
							content = Utils.parseFile(
									Utils.getFileMimeTypeByEnum(candidate.getProcessedDocumentType().name()),
									cgiByteStream);
							candidateVo.setCgiContent(content);
							candidateVo.setCgiDocumentUpdatedMsg(
									Utils.getDocumentDetails(candidate.getResume().getProcessedLastUpdate(), cgiByte,
											candidate.getProcessedDocumentType()));
						} else {
							candidateVo.setCgiContent("");
						}
					} else {
						candidateVo.setResumeContent("");
						candidateVo.setRtrContent("");
						candidateVo.setCgiContent("");
					}

				}
			} else {
				return new ResponseEntity<>(HttpStatus.CREATED);
			}

			return new ResponseEntity<>(candidateVo, HttpStatus.OK);

		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/saveIndiaCandidate", method = RequestMethod.POST, consumes = "multipart/form-data")
	@ResponseBody
	public ResponseEntity<?> saveIndiaCandidate(@RequestPart(value = "file", required = true) List<MultipartFile> files,
			@RequestParam(value = "candidate") Object data, @RequestParam(value = "fileNames") String fileNames,
			HttpServletRequest request) throws IOException {
		if (Utils.getLoginUserId(request) != null) {
			final IndiaCandidate candidate = new IndiaCandidate();
			String requestData = (String) data;
			CandidateVo candidateVo = new ObjectMapper().readValue(requestData, CandidateVo.class);
			try {
				if (candidateVo != null) {
					List<?> existEmail = candidateService.isUserExistsByEmailOrPhone(candidateVo.getEmail(),
							candidateVo.getPhoneCell());
					if (existEmail == null || existEmail.size() == 0) {
						final String actionType = "create";
						TransformDtoToEntity.getIndiaCandidate(candidate, candidateVo, files, actionType, fileNames);

						candidateService.saveCandidate(candidate);
						saveIndiaCandidateStatus(candidate);
						return new ResponseEntity<>(HttpStatus.OK);

					} else {
						return new ResponseEntity<>(HttpStatus.NO_CONTENT);

					}

				}

			} catch (

			ServiceException e)

			{
				// TODO Auto-generated catch block
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

	private void saveIndiaCandidateStatus(IndiaCandidate candidate) {

		IndiaCandidateStatuses status = new IndiaCandidateStatuses();
		status.setIndiacandidate(candidate);
		status.setStatus(candidate.getStatus());
		status.setReason(candidate.getReason());
		status.setCreatedBy(candidate.getUpdatedBy());
		status.setCreatedDate(new Date());
		candidateService.updateIndiaCandidateStatus(status);
	
	}

	@SuppressWarnings("unused")
	@RequestMapping(value = "/updateIndiaCandidate", method = RequestMethod.POST, produces = "application/json", consumes = "multipart/form-data")
	@ResponseBody
	public ResponseEntity<?> updateIndiaCanidate(
			@RequestPart(value = "file", required = true) List<MultipartFile> files,
			@RequestParam(value = "candidate") Object data, @RequestParam(value = "fileNames") String fileNames,
			HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				String requestData = (String) data;
				CandidateVo candidateVo = new ObjectMapper().readValue(requestData, CandidateVo.class);
				candidateVo.setUpdatedBy(Utils.getLoginUserId(request));
				IndiaCandidate candidate = null;
				Map<String,Object>  resultMap= candidateService.getCandidate(Integer.parseInt(candidateVo.getCandidateId()), true, true);
				if(resultMap!=null){
				candidate = (IndiaCandidate) resultMap.get(Constants.DATA);
				}
				if (candidateVo != null) {

					final String actionType = "update";
					candidate.setUpdatedBy(Utils.getLoginUserId(request));
					TransformDtoToEntity.getIndiaCandidate(candidate, candidateVo, files, actionType, fileNames);
				} else {
					return new ResponseEntity<>(HttpStatus.NO_CONTENT);

				}
				candidateService.updateCandidate(candidate);

				return new ResponseEntity<>(HttpStatus.OK);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error(e.getMessage(), e);
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}

	@RequestMapping(value = "/downloadResumeByCandidateId/{candidateId}/{typeOfResume}", produces = "application/json", method = RequestMethod.GET)
	public void getResumeByCandidateId(@PathVariable(value = "candidateId") String candidateId,
			@PathVariable("typeOfResume") String typeOfResume, HttpServletRequest request,
			HttpServletResponse response) {
		ResumeDto resume = candidateService.getResumeByCandidateId(candidateId);

		if (!Utils.isBlank(candidateId)) {
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
					response.setHeader("Content-Disposition",
							"attachment; filename=" + "file" + candidateId + "_" + contentType.getExtensions()[0]);
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
		}

	}

	@RequestMapping(value = "/getCandidate", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getCandidate(@RequestParam(value = "CandidateId") Integer candidateid,
			HttpServletRequest request) {
		if (Utils.getLoginUser(request) != null) {
			try {
				IndiaCandidate candidate = null;
				Map<String,Object>  resultMap= candidateService.getCandidate(candidateid, false, false);
				if(resultMap!=null){
				candidate = (IndiaCandidate) resultMap.get(Constants.DATA);
				}
				IndiaCandidateDto candidatedto = new IndiaCandidateDto();
				candidatedto.setId(candidate.getId() != null ? candidate.getId().toString() : "");

				StringBuffer fullName = new StringBuffer();
				fullName.append(candidate.getFirstName() != null ? candidate.getFirstName() + " " : "");
				fullName.append(candidate.getLastName() != null ? " " + candidate.getLastName() : "");
				candidatedto.setFirstName(fullName != null ? fullName.toString() : "");
				candidatedto.setKeySkill(candidate.getKeySkill() != null ? candidate.getKeySkill() : "");
				candidatedto.setTitle(candidate.getTitle() != null ? candidate.getTitle() : "");
				candidatedto.setLocation(candidate.getCityState() != null ? candidate.getCityState() : "");

				return new ResponseEntity<>(candidatedto, HttpStatus.OK);
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

	@RequestMapping(value = "/getCandidateByEmailId", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getCandidateByEmailId(@RequestParam(value = "emailId") String emailId,
			HttpServletRequest request) {
		if (Utils.getLoginUser(request) != null) {
			try {

				IndiaCandidate candidate = candidateService.getCandidateFromEmail(emailId, false, false);
				IndiaCandidateDto candidatedto = new IndiaCandidateDto();
				candidatedto.setId(candidate.getId() != null ? candidate.getId().toString() : "");

				StringBuffer fullName = new StringBuffer();
				fullName.append(candidate.getFirstName() != null ? candidate.getFirstName() + " " : "");
				fullName.append(candidate.getLastName() != null ? " " + candidate.getLastName() : "");
				candidatedto.setFirstName(fullName != null ? fullName.toString() : "");
				candidatedto.setKeySkill(candidate.getKeySkill() != null ? candidate.getKeySkill() : "");
				candidatedto.setTitle(candidate.getTitle() != null ? candidate.getTitle() : "");
				candidatedto.setLocation(candidate.getCityState() != null ? candidate.getCityState() : "");

				return new ResponseEntity<>(candidatedto, HttpStatus.OK);
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

	@RequestMapping(value = "/getUserDetails", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<?> getUserDetails(HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			Log.info("get user details controller");
			IndiaCandidateDto userDetails = candidateService.getAllUserDetails();
			if (userDetails != null)
				return new ResponseEntity<>(userDetails, HttpStatus.OK);

			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/getCandidateBymobileNumber", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getCandidateBymobileNumber(
			@RequestParam(value = "mobileNumber") String mobileNumber, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				IndiaCandidate candidate = candidateService.getCandidateBymobile(mobileNumber);
				IndiaCandidateDto candidatedto = new IndiaCandidateDto();
				candidatedto.setId(candidate.getId() != null ? candidate.getId().toString() : "");

				StringBuffer fullName = new StringBuffer();
				fullName.append(candidate.getFirstName() != null ? candidate.getFirstName() + " " : "");
				fullName.append(candidate.getLastName() != null ? " " + candidate.getLastName() : "");
				candidatedto.setFirstName(fullName != null ? fullName.toString() : "");
				candidatedto.setKeySkill(candidate.getKeySkill() != null ? candidate.getKeySkill() : "");
				candidatedto.setTitle(candidate.getTitle() != null ? candidate.getTitle() : "");
				candidatedto.setLocation(candidate.getCityState() != null ? candidate.getCityState() : "");

				return new ResponseEntity<>(candidatedto, HttpStatus.OK);
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
	
	@RequestMapping(value = "indiaCandidateStatusUpdate", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> getIndiaCandidateStatus(@RequestBody CandidateVo candidatevo, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			IndiaCandidate candidate = new IndiaCandidate();
			candidate.setId(Integer.parseInt(candidatevo.getCandidateId()));
			for (CandidateStatus candidateStatus : CandidateStatus.values()) {
				if (candidateStatus.toString().equalsIgnoreCase(candidatevo.getStatus())) {
					candidate.setStatus(candidateStatus);
					break;
				}
			}
			IndiaCandidateStatuses status = candidateService.getIndiaCandidateStatus(candidate);
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
}
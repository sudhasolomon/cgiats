/**
 * 
 */
package com.uralian.cgiats.util;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uralian.cgiats.dto.AddEditJobOrderDto;
import com.uralian.cgiats.dto.CandidateDto;
import com.uralian.cgiats.dto.CandidateStatusesDto;
import com.uralian.cgiats.dto.CandidateVo;
import com.uralian.cgiats.dto.ClientInfoDto;
import com.uralian.cgiats.dto.IndiaCandidateDto;
import com.uralian.cgiats.dto.JobOrderFieldDto;
import com.uralian.cgiats.dto.OfferLetterDto;
import com.uralian.cgiats.dto.OfferLetterHistoryDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.dto.SubmittalEventDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.AgencyDetails;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateInfo;
import com.uralian.cgiats.model.CandidateStatus;
import com.uralian.cgiats.model.CandidateStatuses;
import com.uralian.cgiats.model.ClientInfo;
import com.uralian.cgiats.model.IndiaCandidate;
import com.uralian.cgiats.model.IndiaCandidateStatuses;
import com.uralian.cgiats.model.IndiaJobOrder;
import com.uralian.cgiats.model.IndiaSubmittal;
import com.uralian.cgiats.model.IndiaSubmittalEvent;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.JobOrderField;
import com.uralian.cgiats.model.OfferLetter;
import com.uralian.cgiats.model.OfferLetterHistory;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalEvent;
import com.uralian.cgiats.model.User;

/**
 * 
 * @author skurapati
 *
 */
public class TransformEntityToDto {
	private final static Logger LOG = LoggerFactory.getLogger(TransformEntityToDto.class);

	public static AddEditJobOrderDto getJobOrderDto(JobOrder jobOrder) {
		AddEditJobOrderDto dto = new AddEditJobOrderDto();
		try {
			dto.setId(jobOrder.getId());
			dto.setAccept1099(jobOrder.isAccept1099());
			dto.setAcceptC2c(jobOrder.isAcceptC2c());
			dto.setAcceptW2(jobOrder.isAcceptW2());
			dto.setAnnualRateW2(jobOrder.getAnnualRateW2());
			dto.setAssignedTo(jobOrder.getAssignedTo());
			// jobOrder.setAttachmentFileName(attachmentFileName);
			dto.setCategory(jobOrder.getCategory());
			dto.setDmName(jobOrder.getDmName());
			dto.setCity(jobOrder.getCity());
			dto.setCompanyFlag(jobOrder.getCompanyFlag());
			dto.setCustomer(jobOrder.getCustomer());
			dto.setStrCustomerHidden(Utils.convertBooleanToStringValue(jobOrder.isCustomerHidden()));
			dto.setDays(jobOrder.getDays());
			dto.setDeleteFlag(0);
			dto.setDescription(jobOrder.getDescription());
			// dto.setStartDate(Utils.convertDateToString(jobOrder.getStartDate()));
			dto.setEmName(jobOrder.getEmName());
			dto.setEndDate(Utils.convertDateToString(jobOrder.getEndDate()));
			dto.setHourlyRate1099(jobOrder.getHourlyRate1099());
			dto.setHourlyRateC2c(jobOrder.getHourlyRateC2c());
			dto.setHourlyRateC2cmax(jobOrder.getHourlyRateC2cmax());
			dto.setHourlyRateW2(jobOrder.getHourlyRateW2());
			dto.setHourlyRateW2max(jobOrder.getHourlyRateW2Max());
			dto.setHoursToOpen(jobOrder.getHoursToOpen());
			dto.setJobType(jobOrder.getJobType().toString());
			dto.setKeySkills(jobOrder.getKeySkills());
			dto.setLocation(jobOrder.getLocation());
			dto.setNote(jobOrder.getNote());
			dto.setNumOfPos(jobOrder.getNumOfPos());
			dto.setOnlineFlag(jobOrder.getOnlineFlag());
			dto.setPayrate(jobOrder.getPayrate());
			dto.setPermFee(jobOrder.getPermFee());
			dto.setPriority(jobOrder.getPriority().toString());
			dto.setSalary(jobOrder.getSalary());
			dto.setStartDate(Utils.convertDateToString(jobOrder.getStartDate()));
			dto.setState(jobOrder.getState());
			dto.setStatus(jobOrder.getStatus().toString());
			dto.setCreatedBy(jobOrder.getCreatedBy());
			dto.setTitle(jobOrder.getTitle());
			dto.setNoOfResumesRequired(jobOrder.getNoOfResumesRequired());
			dto.setJobExpireIn(jobOrder.getJobExpireIn());
			if (jobOrder.getAttachment() != null) {
				if (jobOrder.getAttachmentFileName() != null) {
					dto.setAttachmentFileName(jobOrder.getAttachmentFileName());
					ByteArrayInputStream attachmentInputStream = new ByteArrayInputStream(jobOrder.getAttachment());
					dto.setStrAttachment(Utils.parseFile(Utils.getFileExtensionByName(jobOrder.getAttachmentFileName()), attachmentInputStream));
				} else {
					dto.setStrAttachment(new String(jobOrder.getAttachment()));

				}
			} else {
				dto.setAttachmentFileName("N/A");
			}
			// Adding file information
			// if (!Utils.isEmpty(files)) {
			// MultipartFile originalDoc = files.get(0);
			// // String fileName =
			// // FilenameUtils.getBaseName(originalDoc.getOriginalFilename());
			// String fileName = originalDoc.getOriginalFilename();
			// dto.setAttachment(originalDoc.getBytes());
			// dto.setAttachmentFileName(fileName);
			// }
			// Adding job order fields
			if (jobOrder.getFieldList() != null && jobOrder.getFieldList().size() > 0) {
				List<JobOrderFieldDto> jobOrderFieldList = new ArrayList<JobOrderFieldDto>();
				for (JobOrderField jobOrderField : jobOrder.getFieldList()) {
					jobOrderFieldList.add(getJobOrderField(jobOrderField));
				}
				dto.setJobOrderFieldList(jobOrderFieldList);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
		}

		return dto;
	}

	/*
	 * public static List<ReadableJobOrderDto> getReadableJobOrderDto(JobOrder
	 * jobOrder) {
	 * 
	 * List<ReadableJobOrderDto> readableJobOrderDtoList = new
	 * ArrayList<ReadableJobOrderDto>();
	 * 
	 * ReadableJobOrderDto dto = null;
	 * 
	 * try { List<JobOrderFieldDto> jobOrderFieldList = null; if
	 * (jobOrder.getFieldList() != null && jobOrder.getFieldList().size() > 0) {
	 * jobOrderFieldList = new ArrayList<JobOrderFieldDto>(); for (JobOrderField
	 * jobOrderField : jobOrder.getFieldList()) {
	 * jobOrderFieldList.add(getJobOrderField(jobOrderField)); }
	 * 
	 * }
	 * 
	 * String propertyNames[] = { "accept1099", "acceptC2c", "acceptW2",
	 * "annualRateW2", "assignedTo", "category", "city", "companyFlag",
	 * "customer", "customerHidden", "days", "deleteFlag", "description",
	 * "emName", "endDate", "hourlyRate1099", "hourlyRateC2c", "hourlyRateW2",
	 * "hoursToOpen", "jobType", "keySkills", "location", "note", "numOfPos",
	 * "onlineFlag", "payrate", "permFee", "priority", "salary", "startDate",
	 * "state", "status", "title" }; Integer i = 0; for (String property :
	 * propertyNames) { Boolean isValueAssigned = false; dto = new
	 * ReadableJobOrderDto(); if (PropertyUtils.getProperty(jobOrder, property)
	 * != null) { if (PropertyUtils.getPropertyType(jobOrder,
	 * property).equals(Integer.class)) { isValueAssigned = true; if (((Integer)
	 * PropertyUtils.getProperty(jobOrder, property)) != 0) {
	 * dto.setFieldName(property);
	 * dto.setFieldValue(PropertyUtils.getProperty(jobOrder, property)); if
	 * (i.equals(0)) { dto.setJobOrderFieldList(jobOrderFieldList); }
	 * readableJobOrderDtoList.add(dto); i++; } } if
	 * (PropertyUtils.getPropertyType(jobOrder, property).equals(Boolean.class))
	 * { isValueAssigned = true; if ((Boolean)
	 * PropertyUtils.getProperty(jobOrder, property)) {
	 * dto.setFieldName(property);
	 * dto.setFieldValue(PropertyUtils.getProperty(jobOrder, property)); if
	 * (i.equals(0)) { dto.setJobOrderFieldList(jobOrderFieldList); }
	 * readableJobOrderDtoList.add(dto); i++; } } if
	 * (PropertyUtils.getPropertyType(jobOrder, property).equals(String.class))
	 * { isValueAssigned = true; if (((String)
	 * PropertyUtils.getProperty(jobOrder, property)).trim().length() > 0) {
	 * dto.setFieldName(property);
	 * dto.setFieldValue(PropertyUtils.getProperty(jobOrder, property)); if
	 * (i.equals(0)) { dto.setJobOrderFieldList(jobOrderFieldList); }
	 * readableJobOrderDtoList.add(dto); i++; } } if (!isValueAssigned) {
	 * dto.setFieldName(property);
	 * dto.setFieldValue(PropertyUtils.getProperty(jobOrder, property)); if
	 * (i.equals(0)) { dto.setJobOrderFieldList(jobOrderFieldList); }
	 * readableJobOrderDtoList.add(dto); i++; }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); LOG.error(e.getMessage(),
	 * e); }
	 * 
	 * return readableJobOrderDtoList; }
	 */

	public static AddEditJobOrderDto getIndiaJobOrderDto(IndiaJobOrder jobOrder) {
		AddEditJobOrderDto dto = new AddEditJobOrderDto();
		try {
			dto.setId(jobOrder.getId());
			dto.setAccept1099(jobOrder.getAccept1099());
			dto.setAcceptC2c(jobOrder.getAcceptC2c());
			dto.setAcceptW2(jobOrder.getAcceptW2());
			dto.setAnnualRateW2(jobOrder.getAnnualRateW2());
			//dto.setAssignedTo(jobOrder.getAssignedTo().getUserId());
			// jobOrder.setAttachmentFileName(attachmentFileName);
			dto.setCategory(jobOrder.getCategory());
			dto.setCity(jobOrder.getCity());
			//dto.setCompanyFlag(jobOrder.getCompanyFlag());
			dto.setCustomer(jobOrder.getCustomer());
			dto.setStrCustomerHidden(Utils.convertBooleanToStringValue(jobOrder.getCustomerHidden()));
			dto.setDays(jobOrder.getDays());
			dto.setDeleteFlag(0);
			dto.setDescription(jobOrder.getDescription());
			// dto.setStartDate(Utils.convertDateToString(jobOrder.getStartDate()));
			dto.setEmName(jobOrder.getEmName());
			dto.setEndDate(Utils.convertDateToString_India(jobOrder.getEndDate()));
			dto.setHourlyRate1099(jobOrder.getHourlyRate1099());
			dto.setHourlyRateC2c(jobOrder.getHourlyRateC2c());
			//dto.setHourlyRateC2cmax(jobOrder.getHourlyRateC2cmax());
			dto.setHourlyRateW2(jobOrder.getHourlyRateW2());
			//dto.setHourlyRateW2max(jobOrder.getHourlyRateW2Max());
			dto.setHoursToOpen(jobOrder.getHoursToOpen());
			dto.setJobType(jobOrder.getJobType().toString());
			dto.setKeySkills(jobOrder.getKeySkills());
			dto.setLocation(jobOrder.getLocation());
			//dto.setNote(jobOrder.getNote());
			dto.setDmName(jobOrder.getDmName());
			dto.setNumOfPos(jobOrder.getNumOfPos());
			dto.setOnlineFlag(jobOrder.getOnlineFlag());
			dto.setPayrate(jobOrder.getPayrate());
			dto.setPermFee(jobOrder.getPermFee());
			dto.setPriority(jobOrder.getPriority().toString());
			dto.setSalary(jobOrder.getSalary());
			dto.setMaxSal(jobOrder.getMaxSalary());
			dto.setMinExp(jobOrder.getMinExp());
			dto.setMaxExp(jobOrder.getMaxExp());
			dto.setStartDate(Utils.convertDateToString_India(jobOrder.getStartDate()));
			dto.setState(jobOrder.getState());
			dto.setStatus(jobOrder.getStatus().toString());
			dto.setCreatedBy(jobOrder.getCreatedBy());
			dto.setTitle(jobOrder.getTitle());
			dto.setNoOfResumesRequired(jobOrder.getNumOfRsumes());
			dto.setNumOfPos(jobOrder.getNumOfPos());
			
			if (jobOrder.getAttachment() != null) {
				if (jobOrder.getAttachmentFileName() != null) {
					dto.setAttachmentFileName(jobOrder.getAttachmentFileName());
					ByteArrayInputStream attachmentInputStream = new ByteArrayInputStream(jobOrder.getAttachment());
					dto.setStrAttachment(Utils.parseFile(Utils.getFileExtensionByName(jobOrder.getAttachmentFileName()), attachmentInputStream));
				} else {
					dto.setStrAttachment(new String(jobOrder.getAttachment()));

				}
			} else {
				dto.setAttachmentFileName("N/A");
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
		}

		return dto;
	}
	
	public static JobOrderFieldDto getJobOrderField(JobOrderField jobOrderField) {
		JobOrderFieldDto dto = new JobOrderFieldDto();
		try {
			dto.setFieldName(jobOrderField.getFieldName());
			dto.setFieldValue(jobOrderField.getFieldValue());
			dto.setVisible(jobOrderField.isVisible());
			dto.setId(jobOrderField.getId());

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
		}
		return dto;
	}

	public static UserDto getUserDto(User user) {
		UserDto userdto = new UserDto();
		userdto.setUserId(user.getUserId());
		userdto.setPassword(user.getPassword());
		userdto.setFullName(user.getFullName());
		userdto.setUserRole(user.getUserRole());
		userdto.setOfficeLocation(user.getOfficeLocation());
		userdto.setAssignedBdm(user.getAssignedBdm());
		userdto.setCity(user.getCity());
		userdto.setEmail(user.getEmail());
		userdto.setExecutiveDASHBOARD(user.getExecutiveDASHBOARD());
		userdto.setFirstName(user.getFirstName());
		userdto.setLastName(user.getLastName());
		userdto.setStrLastLogin(Utils.convertDateToString(new Date()));
		userdto.setPhone(user.getPhone());
		userdto.setStatus(user.getStatus());
		userdto.setCreatedon(Utils.convertDateToString_HH_MM_A(user.getCreatedOn()));
		userdto.setStrCreatedOn(Utils.convertDateToString_YYMM_HHMM(user.getCreatedOn()));
		if (user.getUserProfileImage() == null && user.getProfileImage() != null) {
			userdto.setBase64Image(Base64.encodeBase64String(user.getProfileImage()));
			userdto.setImageExt(user.getProfileImageExt());
		}else if(user.getUserProfileImage() !=null){
			userdto.setBase64Image(Base64.encodeBase64String(user.getUserProfileImage()));
			userdto.setImageExt(user.getProfileImageExt());
		}
		
		return userdto;
	}

	public static CandidateStatusesDto getCandidateStatusesDto(CandidateStatuses candidateStatuses) {
		CandidateStatusesDto dto = new CandidateStatusesDto();
		if (candidateStatuses.getReason() != null) {
			String reason = new String(candidateStatuses.getReason());
			dto.setReason(reason);
		}
		/*
		 * for(CandidateStatus candidateStatus : CandidateStatus.values()){
		 * if(candidateStatus.toString().equalsIgnoreCase(candidateStatuses.
		 * getStatus().toString())){ dto.setStatus(candidateStatus.toString());
		 * break; } }
		 */
		dto.setStatus(candidateStatuses.getStatus().toString());
		dto.setCreatedBy(candidateStatuses.getCreatedBy());
		dto.setCreatedDate(candidateStatuses.getCreatedDate());
		return dto;
	}
	
	public static CandidateStatusesDto getIndiaCandidateStatusesDto(IndiaCandidateStatuses candidateStatuses) {
		CandidateStatusesDto dto = new CandidateStatusesDto();
		if (candidateStatuses.getReason() != null) {
			String reason = new String(candidateStatuses.getReason());
			dto.setReason(reason);
		}
		/*
		 * for(CandidateStatus candidateStatus : CandidateStatus.values()){
		 * if(candidateStatus.toString().equalsIgnoreCase(candidateStatuses.
		 * getStatus().toString())){ dto.setStatus(candidateStatus.toString());
		 * break; } }
		 */
		dto.setStatus(candidateStatuses.getStatus().toString());
		dto.setCreatedBy(candidateStatuses.getCreatedBy());
		dto.setCreatedDate(candidateStatuses.getCreatedDate());
		return dto;
	}

	public static SubmittalDto getSubmittalDto(Submittal submittal) {
		SubmittalDto dto = new SubmittalDto();
		if (submittal.getCandidate() != null) {
			dto.setCandidateId(submittal.getCandidate().getId());
			dto.setCandidateDto(getCandidateDto(submittal.getCandidate()));
			if (submittal.getCandidate().getAddress() != null) {
				dto.getCandidateDto().setStreet1(submittal.getCandidate().getAddress().getStreet1());
				dto.getCandidateDto().setStreet2(submittal.getCandidate().getAddress().getStreet2());
			}
		}
		if (submittal.getJobOrder() != null) {
			dto.setJobOrderId(String.valueOf(submittal.getJobOrder().getId()));
			dto.setJobOrderDto(getJobOrderDto(submittal.getJobOrder()));
		}
		if (submittal.getHistory() != null && submittal.getHistory().size() > 0) {
			List<SubmittalEventDto> submittalEventDtoList = new ArrayList<SubmittalEventDto>();
			for (SubmittalEvent submittalEvent : submittal.getHistory()) {
				submittalEventDtoList.add(getSubmittalEventDto(submittalEvent));
			}
			dto.setSubmittalEventHistoryDtoList(submittalEventDtoList);
		}
		dto.setSubmittalId(String.valueOf(submittal.getId()));
		dto.setStatus(submittal.getStatus().name());
		dto.setComments(submittal.getComments());
		dto.setCreatedBy(submittal.getCreatedBy());

		return dto;
	}

	//India submittal Dto
	
	public static SubmittalDto getIndiaSubmittalDto(IndiaSubmittal submittal) {
		SubmittalDto dto = new SubmittalDto();
		if (submittal.getCandidate() != null) {
			dto.setCandidateId(submittal.getCandidate().getId());
			dto.setIndiacandidateDto(getIndiaCandidateDto(submittal.getCandidate()));
			if (submittal.getCandidate().getAddress() != null) {
//				dto.getCandidateDto().setStreet1(submittal.getCandidate().getAddress().getStreet1()!=null ? submittal.getCandidate().getAddress().getStreet1() :"");
//				dto.getCandidateDto().setStreet2(submittal.getCandidate().getAddress().getStreet2()!=null ? submittal.getCandidate().getAddress().getStreet2() : "");
			}
		}
		if (submittal.getJobOrder() != null) {
			dto.setJobOrderId(String.valueOf(submittal.getJobOrder().getId()));
			dto.setJobOrderDto(getIndiaJobOrderDto(submittal.getJobOrder()));
		}
		if (submittal.getHistory() != null && submittal.getHistory().size() > 0) {
			List<SubmittalEventDto> submittalEventDtoList = new ArrayList<SubmittalEventDto>();
			for (IndiaSubmittalEvent submittalEvent : submittal.getHistory()) {
				submittalEventDtoList.add(getIndiaSubmittalEventDto(submittalEvent));
			}
			dto.setSubmittalEventHistoryDtoList(submittalEventDtoList);
		}
		dto.setSubmittalId(String.valueOf(submittal.getId()));
		dto.setStatus(submittal.getStatus().name());
		dto.setComments(submittal.getComments());
		dto.setCreatedBy(submittal.getCreatedBy());

		return dto;
	}
	
	
	public static List<IndiaCandidateDto> getIndiaCandidates(List<IndiaCandidate> candidates, int count) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		List<IndiaCandidateDto> candidateDtolist = new ArrayList<IndiaCandidateDto>();
		IndiaCandidateDto candidateDto = null;
		if (candidates != null) {
			for (IndiaCandidate candidate : candidates) {
				candidateDto = new IndiaCandidateDto();
				candidateDto.setId(candidate.getId() != null ? candidate.getId().toString() : "");

				StringBuffer fullName = new StringBuffer();
				fullName.append(candidate.getFirstName() != null ? candidate.getFirstName() + " " : "");
				fullName.append(candidate.getLastName() != null ? " " + candidate.getLastName() : "");

				candidateDto.setFirstName(fullName != null ? fullName.toString() : "");
				candidateDto.setTitle(candidate.getTitle() != null ? candidate.getTitle() : "");
				candidateDto.setLocation(candidate.getCityState() != null ? candidate.getCityState() : "");
				candidateDto.setCreatedOn(candidate.getCreatedOn() != null ? sdf.format(candidate.getCreatedOn()) : "");
				candidateDto.setUpdatedOn(candidate.getUpdatedOn() != null ? sdf.format(candidate.getUpdatedOn()) : "");
				candidateDto.setStatus(candidate.getStatus() != null ? candidate.getStatus() : CandidateStatus.Available);
				candidateDto.setEmail(candidate.getEmail() != null ? candidate.getEmail() : "");
				candidateDto.setPhone(candidate.getPhone() != null ? candidate.getPhone() : "");
				candidateDto.setTotalRecords(String.valueOf(count));
				candidateDto.setHot(candidate.isHot());
				candidateDto.setBlock(candidate.isBlock());

				/*
				 * if(candidate.getResume()!=null){ IndiaResume resumes =
				 * candidate.getResume();
				 * candidateDto.setIndiaResumeContent(resumes.getParsedText()!=
				 * null?resumes.getParsedText():""); }
				 */

				candidateDtolist.add(candidateDto);
			}
		}

		return candidateDtolist;
	}

	public static CandidateVo getcandidateForEdit() {
		CandidateVo candidateVo = new CandidateVo();
		return candidateVo;
	}

	public static CandidateDto getCandidateDto(Candidate candidate) {
		CandidateDto dto = new CandidateDto();
		dto.setFirstName(candidate.getFirstName() != null ? candidate.getFirstName() : "");
		dto.setLastName(candidate.getLastName() != null ? candidate.getLastName() : "");
		dto.setFullName(candidate.getFullName());
		dto.setId(candidate.getId() != null ? candidate.getId().toString() : "");
		dto.setTitle(candidate.getTitle() != null ? candidate.getTitle() : "");
		dto.setEmail(candidate.getEmail() != null ? candidate.getEmail() : "");
		dto.setCreatedOn(candidate.getCreatedOn() != null ? candidate.getCreatedOn().toString() : "");
		dto.setKeySkill(candidate.getKeySkill() != null ? candidate.getKeySkill() : "");
		dto.setLocation(candidate.getCityState() != null ? candidate.getCityState() : "");
		dto.setPhoneNumber(candidate.getPhone() != null ? candidate.getPhone() : "");
		dto.setAltPhoneNumber(candidate.getPhoneAlt());
		dto.setStatus(candidate.getStatus() != null ? candidate.getStatus().toString() : "");
		dto.setUpdatedOn(candidate.getUpdatedOn() != null ? candidate.getUpdatedOn().toString() : "");
		dto.setVisaType(candidate.getVisaType() != null ? candidate.getVisaType() : "");
		dto.setSkills(candidate.getSkills() != null ? candidate.getSkills() : "");
		dto.setUploadedBy(candidate.getCreatedUser() != null ? candidate.getCreatedUser() : "");
		dto.setAltPhoneNumber(candidate.getPhoneAlt() != null ? candidate.getPhoneAlt() : "");
		dto.setBlock(candidate.isBlock());
		dto.setHot(candidate.isHot());
		dto.setReferenceName1(candidate.getReference1());
		dto.setReferenceName2(candidate.getReference2());
		dto.setResumeContent(candidate.getResume().getParsedText());
		dto.setCreatedUser(candidate.getCreatedUser());

		/* Added */
		if (candidate.getAddress() != null) {
			dto.setCity(candidate.getAddress().getCity());
			dto.setCountry(candidate.getAddress().getCountry());
			dto.setState(candidate.getAddress().getState());
			dto.setStreet1(candidate.getAddress().getStreet());
			dto.setZipcode(candidate.getAddress().getZipcode());
		}

		if (candidate.getStatushistory() != null && candidate.getStatushistory().size() > 0) {
			List<CandidateStatusesDto> candidateStatusesDtoList = new ArrayList<CandidateStatusesDto>();
			for (CandidateStatuses status : candidate.getStatushistory()) {
				candidateStatusesDtoList.add(getCandidateStatusesDto(status));
			}
			Collections.sort(candidateStatusesDtoList, new Comparator<CandidateStatusesDto>() {
				@Override
				public int compare(CandidateStatusesDto dto1, CandidateStatusesDto dto2) {
					return (dto2.getCreatedDate()).compareTo((dto1.getCreatedDate()));
				}
			});
			dto.setStatusHistory(candidateStatusesDtoList);
		}
		return dto;
	}

	public static IndiaCandidateDto getIndiaCandidateDto(IndiaCandidate candidate) {
		IndiaCandidateDto dto = new IndiaCandidateDto();
		dto.setFirstName(candidate.getFirstName() != null ? candidate.getFirstName() : "");
		dto.setLastName(candidate.getLastName() != null ? candidate.getLastName() : "");
		dto.setFullName(candidate.getFullName());
		dto.setId(candidate.getId() != null ? candidate.getId().toString() : "");
		dto.setTitle(candidate.getTitle() != null ? candidate.getTitle() : "");
		dto.setEmail(candidate.getEmail() != null ? candidate.getEmail() : "");
		dto.setCreatedOn(candidate.getCreatedOn() != null ? candidate.getCreatedOn().toString() : "");
		dto.setKeySkill(candidate.getKeySkill() != null ? candidate.getKeySkill() : "");
		dto.setLocation(candidate.getCityState() != null ? candidate.getCityState() : "");
		//dto.setPhoneNumber(candidate.getPhone() != null ? candidate.getPhone() : "");
		//dto.setAltPhoneNumber(candidate.getPhoneAlt());
		//dto.setStatus(candidate.getStatus() != null ? candidate.getStatus().toString() : "");
		dto.setUpdatedOn(candidate.getUpdatedOn() != null ? candidate.getUpdatedOn().toString() : "");
		dto.setVisaType(candidate.getVisaType() != null ? candidate.getVisaType() : "");
		//dto.setSkills(candidate.getSkills() != null ? candidate.getSkills() : "");
		//dto.setUploadedBy(candidate.getCreatedUser() != null ? candidate.getCreatedUser() : "");
		//dto.setAltPhoneNumber(candidate.getPhoneAlt() != null ? candidate.getPhoneAlt() : "");
		dto.setBlock(candidate.isBlock());
		dto.setHot(candidate.isHot());
		//dto.setReferenceName1(candidate.getReference1());
		//dto.setReferenceName2(candidate.getReference2());
		//dto.setResumeContent(candidate.getResume().getParsedText());
		dto.setCreatedUser(candidate.getCreatedUser());

		/* Added */
		if (candidate.getAddress() != null) {
			/*dto.setCity(candidate.getAddress().getCity());
			dto.setCountry(candidate.getAddress().getCountry());
			dto.setState(candidate.getAddress().getState());
			dto.setStreet1(candidate.getAddress().getStreet());
			dto.setZipcode(candidate.getAddress().getZipcode());*/
		}

		if (candidate.getStatushistory() != null && candidate.getStatushistory().size() > 0) {
			List<CandidateStatusesDto> candidateStatusesDtoList = new ArrayList<CandidateStatusesDto>();
			for (IndiaCandidateStatuses status : candidate.getStatushistory()) {
				candidateStatusesDtoList.add(getIndiaCandidateStatusesDto(status));
			}
			Collections.sort(candidateStatusesDtoList, new Comparator<CandidateStatusesDto>() {
				@Override
				public int compare(CandidateStatusesDto dto1, CandidateStatusesDto dto2) {
					return (dto2.getCreatedDate()).compareTo((dto1.getCreatedDate()));
				}
			});
			dto.setStatusHistory(candidateStatusesDtoList);
		}
		return dto;
	}

	public static SubmittalEventDto getSubmittalEventDto(SubmittalEvent submittalEvent) {
		SubmittalEventDto submittalEventDto = new SubmittalEventDto();
		submittalEventDto.setId(submittalEvent.getId());
		submittalEventDto.setCreatedBy(submittalEvent.getCreatedBy());
		submittalEventDto.setNotes(submittalEvent.getNotes());
		submittalEventDto.setStatus(submittalEvent.getStatus().name());
		submittalEventDto.setUpdatedBy(submittalEvent.getUpdatedBy());
		submittalEventDto.setStrCreatedOn(Utils.convertDateToString_HH_MM(submittalEvent.getCreatedDate()));
		return submittalEventDto;
	}
	
	public static SubmittalEventDto getIndiaSubmittalEventDto(IndiaSubmittalEvent submittalEvent) {
		SubmittalEventDto submittalEventDto = new SubmittalEventDto();
		submittalEventDto.setId(submittalEvent.getId());
		submittalEventDto.setCreatedBy(submittalEvent.getCreatedBy());
		submittalEventDto.setNotes(submittalEvent.getNotes());
		submittalEventDto.setStatus(submittalEvent.getStatus().name());
		submittalEventDto.setUpdatedBy(submittalEvent.getUpdatedBy());
		submittalEventDto.setStrCreatedOn(Utils.convertDateToString_HH_MM_India(submittalEvent.getCreatedDate()));
		return submittalEventDto;
	}

	public static OfferLetterDto getOfferLetterDto(OfferLetter offerLetter) {
		OfferLetterDto offerLetterDto = new OfferLetterDto();

		offerLetterDto.setBackgroundcheck(offerLetter.getBackgroundcheck());
		offerLetterDto.setBdmName(offerLetter.getBdmName());
		offerLetterDto.setBenifitsCategory(offerLetter.getBenifitsCategory());
		offerLetterDto.setBonusDescription(offerLetter.getBonusDescription());
		if (offerLetter.getCandidate() != null) {
			offerLetterDto.setCandidateDto(getCandidateDto(offerLetter.getCandidate()));
		}
		offerLetterDto.setCity(offerLetter.getCity());
		offerLetterDto.setClient_extension(offerLetter.getClient_extension());
		offerLetterDto.setClient_fax(offerLetter.getClient_fax());
		offerLetterDto.setClientcity(offerLetter.getClientcity());
		offerLetterDto.setClientcountry(offerLetter.getClientcountry());
		offerLetterDto.setClientemail(offerLetter.getClientemail());
		offerLetterDto.setClientphone(offerLetter.getClientphone());
		offerLetterDto.setClientstate(offerLetter.getClientstate());
		offerLetterDto.setClientstreet(offerLetter.getClientstreet());
		offerLetterDto.setClientzipcode(offerLetter.getClientzipcode());
		offerLetterDto.setCommibonusEligible(offerLetter.getCommibonusEligible());
		offerLetterDto.setCompanyName(offerLetter.getCompanyName());
		offerLetterDto.setContactPerson(offerLetter.getContactPerson());
		offerLetterDto.setCountry(offerLetter.getCountry());
		offerLetterDto.setDeleteFlag(offerLetter.getDeleteFlag());
		offerLetterDto.setDrugcheck(offerLetter.getDrugcheck());
		offerLetterDto.setElectingMedicalHourly(offerLetter.isElectingMedicalHourly());
		offerLetterDto.setElectingMedicalSalired(offerLetter.isElectingMedicalSalired());
		offerLetterDto.setEmail(offerLetter.getEmail());
		offerLetterDto.setEndclientName(offerLetter.getEndclientName());
		offerLetterDto.setExpections(offerLetter.getExpections());
		offerLetterDto.setFirstName(offerLetter.getFirstName());
		offerLetterDto.setGender(offerLetter.getGender());
		if (offerLetter.getHistory() != null && offerLetter.getHistory().size() > 0) {
			List<OfferLetterHistoryDto> offerLetterHistoryDtoList = new ArrayList<OfferLetterHistoryDto>();
			for (OfferLetterHistory offerLetterHistory : offerLetter.getHistory()) {
				offerLetterHistoryDtoList.add(getOfferLetterHistoryDto(offerLetterHistory));
			}
			offerLetterDto.setHistory(offerLetterHistoryDtoList);
		}
		offerLetterDto.setImmigrationStatus(offerLetter.getImmigrationStatus());
		offerLetterDto.setIndividual(offerLetter.getIndividual());
		offerLetterDto.setJobOrderId(offerLetter.getJobOrderId());
		offerLetterDto.setLastName(offerLetter.getLastName());
		offerLetterDto.setNotes(offerLetter.getNotes());
		offerLetterDto.setOfferFrom(offerLetter.getOfferFrom());
		offerLetterDto.setOfferLetterId(offerLetter.getId());
		offerLetterDto.setOvertime(offerLetter.getOvertime());
		offerLetterDto.setPaymentTerms(offerLetter.getPaymentTerms());
		offerLetterDto.setPhone(offerLetter.getPhone());
		offerLetterDto.setPositionTitle(offerLetter.getPositionTitle());
		offerLetterDto.setReason(offerLetter.getReason());
		offerLetterDto.setRecruiterName(offerLetter.getRecruiterName());
		offerLetterDto.setRelocationBenefits(offerLetter.getRelocationBenefits());
		offerLetterDto.setRelocationBenefitArray(Utils.getStrArray_FromStr(offerLetter.getRelocationBenefits()));
		offerLetterDto.setSalaryRate(offerLetter.getSalaryRate());
		offerLetterDto.setSpecialInstructions(offerLetter.getSpecialInstructions());
		offerLetterDto.setSsn(offerLetter.getSsn());
		offerLetterDto.setState(offerLetter.getState());
		offerLetterDto.setStatus(offerLetter.getStatus().name());
		offerLetterDto.setStrDateofbirth(Utils.convertDateToString(offerLetter.getDateofbirth()));

		// Modified
		offerLetterDto.setStreet1(offerLetter.getStreet1());
		offerLetterDto.setStreet2(offerLetter.getStreet2());
		// Modified
		if (offerLetter.getContactPerson() != null && offerLetter.getContactPerson().trim().length() > 0) {
			String[] strArray = Utils.splitAPipeWordIntoTwoStrings(offerLetter.getContactPerson());
			offerLetterDto.setContactPersonFirstName(strArray[0]);
			offerLetterDto.setContactPersonLastName(strArray[1]);
		} else {
			offerLetterDto.setContactPersonFirstName(offerLetter.getContactPersonFirstName());
			offerLetterDto.setContactPersonLastName(offerLetter.getContactPersonLastName());
		}

		// strArray =
		// Utils.splitAPipeWordIntoTwoStrings(offerLetter.getClientstreet());
		// Modified
		offerLetterDto.setClientstreet1(offerLetter.getClientstreet());
		offerLetterDto.setClientstreet2(offerLetter.getClientstreet2());

		offerLetterDto.setStrStartdateOfAssignment(Utils.convertDateToString(offerLetter.getStartdateOfAssignment()));
		offerLetterDto.setTaxId(offerLetter.getTaxId());
		offerLetterDto.setTitleOfName(offerLetter.getTitleOfName());
		offerLetterDto.setWaviningMedicalHourly(offerLetter.isWaviningMedicalHourly());
		offerLetterDto.setWaviningMedicalSalired(offerLetter.isWaviningMedicalSalired());

		// strArray =
		// Utils.splitAPipeWordIntoTwoStrings(offerLetter.getWorkLocation());
		// Modified
		offerLetterDto.setWorkLocation(offerLetter.getWorkLocation());
		offerLetterDto.setWorkLocationState(offerLetter.getWorkLocationState());

		// offerLetterDto.setWorkLocation(offerLetter.getWorkLocation());
		offerLetterDto.setZipcode(offerLetter.getZipcode());

		return offerLetterDto;
	}

	public static OfferLetterHistoryDto getOfferLetterHistoryDto(OfferLetterHistory offerLetterHistory) {
		OfferLetterHistoryDto dto = new OfferLetterHistoryDto();
		dto.setCreatedBy(offerLetterHistory.getCreatedBy());
		dto.setNotes(offerLetterHistory.getNotes());
		dto.setOfferLetterHistoryId(offerLetterHistory.getId());
		dto.setStatus(offerLetterHistory.getStatus().name());
		dto.setStrCreatedDate(Utils.convertDateToString(offerLetterHistory.getCreatedDate()));
		dto.setStrStatusCreatedOn(Utils.convertDateToString(offerLetterHistory.getStatusCreatedOn()));
		dto.setStrUpdatedOn(Utils.convertDateToString(offerLetterHistory.getUpdatedOn()));
		dto.setUpdatedBy(offerLetterHistory.getUpdatedBy());
		return dto;
	}

	public static ClientInfoDto getprojectInfoDetails(ClientInfo clientInfo, AgencyDetails agencyDetails) {
		ClientInfoDto clientDto = new ClientInfoDto();
		if (clientInfo != null) {
			CandidateInfo candidateInfo = clientInfo.getCandidateinfo();
			Candidate candidate = clientInfo.getCandidateinfo().getCandidate();

			clientDto.setBdmFirstName(Utils.replaceNullWithEmpty(candidateInfo.getBdmName()));
			clientDto.setBdmLastName(Utils.replaceNullWithEmpty(candidateInfo.getBdmLastName()));
			clientDto.setRecruiterFirstName(Utils.replaceNullWithEmpty(candidateInfo.getRecruiterName()));
			clientDto.setRecruiterLastName(Utils.replaceNullWithEmpty(candidateInfo.getRecruiterLastName()));

			clientDto.setJobOrderId(Utils.replaceNullWithEmpty(candidateInfo.getJobOrderId().toString()));
			clientDto.setFirstName(Utils.replaceNullWithEmpty(candidate.getFirstName()));
			clientDto.setLastName(Utils.replaceNullWithEmpty(candidate.getLastName()));
			clientDto.setDateOfBirth(Utils.convertDateToString(candidateInfo.getDateofbirth()));
			clientDto.setSource(Utils.replaceNullWithEmpty(candidateInfo.getSource()));
			clientDto.setSsn(Utils.replaceNullWithEmpty(candidateInfo.getSsn()));
			clientDto.setStreet1(Utils.replaceNullWithEmpty(candidateInfo.getStreet1()));
			clientDto.setStreet2(Utils.replaceNullWithEmpty(candidateInfo.getStreet2()));
			clientDto.setCity(Utils.replaceNullWithEmpty(candidateInfo.getCity()));
			clientDto.setState(Utils.replaceNullWithEmpty(candidateInfo.getState()));
			clientDto.setZipCode(Utils.replaceNullWithEmpty(candidateInfo.getZipcode()));
			clientDto.setCountry(Utils.replaceNullWithEmpty(candidateInfo.getCountry()));
			clientDto.setCandidatePhone(Utils.replaceNullWithEmpty(candidateInfo.getPhone()));
			clientDto.setPhoneAlt(Utils.replaceNullWithEmpty(candidateInfo.getPhoneAlt()));
			clientDto.setEmail(Utils.replaceNullWithEmpty(candidateInfo.getEmail()));

			clientDto.setEmploymentStatus(Utils.replaceNullWithEmpty(candidateInfo.getEmploymentStatus()));
			clientDto.setImmigrationStatus(Utils.replaceNullWithEmpty(candidateInfo.getImmigrationStatus()));
			clientDto.setSalaryRate(Utils.replaceNullWithEmpty(candidateInfo.getSalaryRate()));
			clientDto.setPerDiem(Utils.replaceNullWithEmpty(candidateInfo.getPerDiem()));
			clientDto.setStartDate(Utils.replaceNullWithEmpty(Utils.convertDateToString(candidateInfo.getStartDate())));
			clientDto.setEndDate(Utils.replaceNullWithEmpty(Utils.convertDateToString(candidateInfo.getEndDate())));
			clientDto.setClientName(Utils.replaceNullWithEmpty(clientInfo.getClientName()));

			clientDto.setClientContactFirstName(Utils.replaceNullWithEmpty(clientInfo.getContactPerson()));
			clientDto.setClientContactLastName(Utils.replaceNullWithEmpty(clientInfo.getPersonLastName()));

			clientDto.setClientPhone(Utils.replaceNullWithEmpty(clientInfo.getPhone()));
			clientDto.setExtenstion(Utils.replaceNullWithEmpty(clientInfo.getExtension()));
			clientDto.setFax(Utils.replaceNullWithEmpty(clientInfo.getFax()));
			clientDto.setClientEmail(Utils.replaceNullWithEmpty(clientInfo.getEmail()));
			clientDto.setClientStreet1(Utils.replaceNullWithEmpty(clientInfo.getClientstreet1()));
			clientDto.setClientStreet2(Utils.replaceNullWithEmpty(clientInfo.getClientstreet2()));
			clientDto.setClientcity(Utils.replaceNullWithEmpty(clientInfo.getClientcity()));
			clientDto.setClientState(Utils.replaceNullWithEmpty(clientInfo.getClientstate()));
			clientDto.setClientCountry(Utils.replaceNullWithEmpty(clientInfo.getClientcountry()));
			clientDto.setClientZipCode(Utils.replaceNullWithEmpty(clientInfo.getClientzipcode()));
			clientDto.setClientinvoicing(Utils.replaceNullWithEmpty(clientInfo.getInvoicing()));
			clientDto.setClientpaymentTerms(Utils.replaceNullWithEmpty(clientInfo.getPaymentTerms()));
			clientDto.setClientbillRate(Utils.replaceNullWithEmpty(clientInfo.getBillRate()));
			clientDto.setClientotRate(Utils.replaceNullWithEmpty(clientInfo.getOtRate()));
			clientDto.setClientotEligibility(Utils.convertBooleanToStringValue(clientInfo.getOtEligibility()));
			clientDto.setEndClientName(Utils.replaceNullWithEmpty(clientInfo.getEndClientName()));
			clientDto.setProjectManagerName(Utils.replaceNullWithEmpty(clientInfo.getProjectManagerName()));
			clientDto.setInvoicingStreet1(Utils.replaceNullWithEmpty(clientInfo.getInvoicingstreet1()));
			clientDto.setInvoicingstreet2(Utils.replaceNullWithEmpty(clientInfo.getInvoicingstreet2()));
			clientDto.setInvoicingcity(Utils.replaceNullWithEmpty(clientInfo.getInvoicingcity()));
			clientDto.setInvoicingState(Utils.replaceNullWithEmpty(clientInfo.getInvoicingstate()));
			clientDto.setInvoicingCountry(Utils.replaceNullWithEmpty(clientInfo.getInvoicingcountry()));
			clientDto.setInvoicingZipCode(Utils.replaceNullWithEmpty(clientInfo.getInvoicingzipcode()));
			clientDto.setProjectManagerPhone(Utils.replaceNullWithEmpty(clientInfo.getProjectManagerPhone()));
			clientDto.setProjectManagerExtension(Utils.replaceNullWithEmpty(clientInfo.getProjectManagerExtension()));
			clientDto.setPaidTimeOff(Utils.replaceNullWithEmpty(candidateInfo.getPaidTimeOff()));

			if (candidateInfo.getRelocationBenefits() != null)
				clientDto.setRelocationBenefits(Utils.convertStringToArrayList(candidateInfo.getRelocationBenefits()));

			clientDto.setMedical(Utils.replaceNullWithEmpty(candidateInfo.getMedical()));
			clientDto.setDentalVision(Utils.replaceNullWithEmpty(candidateInfo.getDentalVision()));
			clientDto.setExpections(Utils.replaceNullWithEmpty(candidateInfo.getExpections()));
			clientDto.setSpecialInstructions(Utils.replaceNullWithEmpty(candidateInfo.getSpecialInstructions()));

			clientDto.setC2cAgencyName(Utils.replaceNullWithEmpty(agencyDetails.getC2cAgencyName()));

			clientDto.setC2cContactFirstName(Utils.replaceNullWithEmpty(agencyDetails.getContactPersonName()));
			clientDto.setC2cContactLastName(Utils.replaceNullWithEmpty(agencyDetails.getContactPersonLastName()));

			clientDto.setContactPersonEmail(Utils.replaceNullWithEmpty(agencyDetails.getContactPersonEmail()));
			clientDto.setContactPersonFax(Utils.replaceNullWithEmpty(agencyDetails.getContactPersonFax()));
			clientDto.setContactPersonPhone(Utils.replaceNullWithEmpty(agencyDetails.getContactPersonPhone()));
			clientDto.setContactPersonExtension(Utils.replaceNullWithEmpty(agencyDetails.getContactPersonExtension()));
			clientDto.setC2cstreet1(Utils.replaceNullWithEmpty(agencyDetails.getStreet1()));
			clientDto.setC2cstreet2(Utils.replaceNullWithEmpty(agencyDetails.getStreet2()));
			clientDto.setC2ccity(Utils.replaceNullWithEmpty(agencyDetails.getCity()));
			clientDto.setC2cstate(Utils.replaceNullWithEmpty(agencyDetails.getState()));
			clientDto.setC2ccountry(Utils.replaceNullWithEmpty(agencyDetails.getCountry()));
			clientDto.setC2czipCode(Utils.replaceNullWithEmpty(agencyDetails.getZipcode()));
		}
		return clientDto;
	}

	/*public static List<LoginInfoDto> getloginAttemptsDetails(List<LoginAttempts> loginAttempts) {
		List<LoginInfoDto> infoDtos = new ArrayList<LoginInfoDto>();
		Iterator<?> itr = loginAttempts.iterator();
		while(itr.hasNext()){
			LoginInfoDto infoDto = new LoginInfoDto();
			LoginAttempts logAttempts = (LoginAttempts) itr.next();
			infoDto.setCreatedBy(Utils.nullIfBlank(logAttempts.getCreatedBy()));
			infoDto.setCreatedOn(Utils.convertDateToString_HH_MM(logAttempts.getCreatedOn()));
			infoDto.setDuration(Utils.nullIfBlank(Utils.nullIfBlank(logAttempts.getDuration())));
			infoDto.setDurationTime(String.valueOf(logAttempts.getDurationTime()));
			infoDto.setId(logAttempts.getId() != null ? logAttempts.getId().toString() : null);
			infoDto.setLoginDate(Utils.convertDateToString_HH_MM_A(logAttempts.getLoginDate()));
			infoDto.setLogoutDate(Utils.convertDateToString_HH_MM_A(logAttempts.getLogoutDate()));
			infoDto.setStatus(Utils.nullIfBlank(logAttempts.getStatus()));
			infoDto.setTotal( String.valueOf( logAttempts.getTotal()));
			infoDtos.add(infoDto);
		}
		
		return infoDtos;
	}*/

}

/**
 * 
 */
package com.uralian.cgiats.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.uralian.cgiats.dto.AddEditJobOrderDto;
import com.uralian.cgiats.dto.CandidateVo;
import com.uralian.cgiats.dto.ClientInfoDto;
import com.uralian.cgiats.dto.JobOrderFieldDto;
import com.uralian.cgiats.dto.OfferLetterDto;
import com.uralian.cgiats.dto.SubmittalEventDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.Address;
import com.uralian.cgiats.model.AgencyDetails;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateInfo;
import com.uralian.cgiats.model.CandidateStatus;
import com.uralian.cgiats.model.ClientInfo;
import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.model.IndiaCandidate;
import com.uralian.cgiats.model.IndiaJobOrder;
import com.uralian.cgiats.model.IndiaJobOrderField;
import com.uralian.cgiats.model.IndiaSubmittalEvent;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.JobOrderField;
import com.uralian.cgiats.model.JobOrderPriority;
import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.JobType;
import com.uralian.cgiats.model.LoginAttempts;
import com.uralian.cgiats.model.OfferLetter;
import com.uralian.cgiats.model.OfferLetterStatus;
import com.uralian.cgiats.model.SubmittalEvent;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;

/**
 * @author Sreenath
 *         <p>
 *         This class is to convert dto objects into the corresponding entity.
 *         </p>
 *
 */
public class TransformDtoToEntity {

	private final static Logger LOG = LoggerFactory.getLogger(TransformDtoToEntity.class);

	public static JobOrder getJobOrder(AddEditJobOrderDto dto, JobOrder jobOrder, List<MultipartFile> files) {
		try {

			jobOrder.setAccept1099(Utils.getDefaultBooleanValue(dto.getAccept1099()));
			jobOrder.setAcceptC2c(Utils.getDefaultBooleanValue(dto.getAcceptC2c()));
			jobOrder.setAcceptW2(Utils.getDefaultBooleanValue(dto.getAcceptW2()));

			jobOrder.setAssignedTo(dto.getAssignedTo());
			jobOrder.setCategory(dto.getCategory());
			jobOrder.setCity(dto.getCity());
			jobOrder.setCompanyFlag(dto.getCompanyFlag());
			jobOrder.setUpdatedOn(new Date());
			jobOrder.setUpdatedBy(dto.getUpdatedBy());
			if (dto.getId() == null) {
				jobOrder.setCreatedOn(new Date());
				jobOrder.setCreatedBy(dto.getCreatedBy());
			}
			jobOrder.setCustomer(dto.getCustomer());
			jobOrder.setCustomerHidden(Utils.convertStringToBooleanValue(dto.getStrCustomerHidden()));
			jobOrder.setDays(Utils.getDefaultLongValue(dto.getDays()));
			jobOrder.setDeleteFlag(0);
			jobOrder.setJobExpireIn(dto.getJobExpireIn());
			jobOrder.setNoOfResumesRequired(dto.getNoOfResumesRequired());
			jobOrder.setDescription(dto.getDescription());
			jobOrder.setEmName(dto.getEmName());
			jobOrder.setEndDate(Utils.convertAngularStrToDate(dto.getEndDate()));
			jobOrder.setHourlyRate1099(Utils.getDefaultIntegerValue(dto.getHourlyRate1099()));

			jobOrder.setJobType(JobType.valueOf(dto.getJobType()));

			if (dto.getJobType() != null && (dto.getJobType().toLowerCase().equalsIgnoreCase(Constants.BOTH)
					|| dto.getJobType().toLowerCase().equalsIgnoreCase(Constants.NOT_SPECIFIED)
					|| dto.getJobType().toLowerCase().equalsIgnoreCase(Constants.PERMANENT))) {
				jobOrder.setAcceptC2c(false);
				jobOrder.setAcceptW2(false);
			}

			if (dto.getAcceptW2() != null && dto.getAcceptW2()) {
				jobOrder.setHourlyRateW2(Utils.getDefaultIntegerValue(dto.getHourlyRateW2()));
				jobOrder.setHourlyRateW2Max(Utils.getDefaultIntegerValue(dto.getHourlyRateW2max()));
				jobOrder.setAnnualRateW2(Utils.getDefaultIntegerValue(dto.getAnnualRateW2()));
			} else {
				jobOrder.setHourlyRateW2(0);
				jobOrder.setHourlyRateW2Max(0);
				jobOrder.setAnnualRateW2(0);
			}

			if (dto.getAcceptC2c() != null && dto.getAcceptC2c()) {
				jobOrder.setHourlyRateC2c(Utils.getDefaultIntegerValue(dto.getHourlyRateC2c()));
				jobOrder.setHourlyRateC2cmax(Utils.getDefaultIntegerValue(dto.getHourlyRateC2cmax()));
			} else {
				jobOrder.setHourlyRateC2c(0);
				jobOrder.setHourlyRateC2cmax(0);
			}

			jobOrder.setHoursToOpen(Utils.getDefaultIntegerValue(dto.getHoursToOpen()));

			jobOrder.setKeySkills(dto.getKeySkills());
			jobOrder.setLocation(dto.getLocation());
			jobOrder.setNote(dto.getNote());
			jobOrder.setNumOfPos(Utils.getDefaultIntegerValue(dto.getNumOfPos()));
			jobOrder.setOnlineFlag(dto.getOnlineFlag());
			jobOrder.setPayrate(Utils.getDefaultIntegerValue(dto.getPayrate()));
			jobOrder.setPermFee(Utils.getDefaultIntegerValue(dto.getPermFee()));
			jobOrder.setPriority(JobOrderPriority.valueOf(dto.getPriority()));
			jobOrder.setSalary(Utils.getDefaultIntegerValue(dto.getSalary()));
			jobOrder.setStartDate(Utils.convertAngularStrToDate(dto.getStartDate()));
			jobOrder.setState(dto.getState());
			jobOrder.setStatus(JobOrderStatus.valueOf(dto.getStatus()));
			jobOrder.setTitle(dto.getTitle());
			jobOrder.setDmName(dto.getDmName());
			jobOrder.setVersion(0);
			// Adding file information
			if (!Utils.isEmpty(files)) {
				MultipartFile originalDoc = files.get(0);
				String fileName = originalDoc.getOriginalFilename();
				jobOrder.setAttachment(originalDoc.getBytes());
				jobOrder.setAttachmentFileName(fileName);
			}
			// Adding job order fields
			if (dto.getJobOrderFieldList() != null && dto.getJobOrderFieldList().size() > 0) {
				Map<String, JobOrderField> fields = new HashMap<String, JobOrderField>();
				for (JobOrderFieldDto jobOrderFieldDto : dto.getJobOrderFieldList()) {
					JobOrderField field = fields.get(jobOrderFieldDto.getFieldName());
					if (field == null) {
						field = new JobOrderField();
						field.setFieldName(jobOrderFieldDto.getFieldName());
						field.setFieldValue(jobOrderFieldDto.getFieldValue());
						field.setVisible(jobOrderFieldDto.getVisible());
					} else {
						field.setFieldValue(jobOrderFieldDto.getFieldValue());
						field.setVisible(jobOrderFieldDto.getVisible());
					}
					field.setJobOrder(jobOrder);
					fields.put(jobOrderFieldDto.getFieldName(), field);
				}
				jobOrder.setFields(fields);
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
		}

		return jobOrder;
	}

	public static void getCandidate(Candidate candidate, CandidateVo candidateVo, List<MultipartFile> files,
			String actionType, String fileNames) {
		final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
		try {
			if (actionType.equalsIgnoreCase("create")) {
				candidate.setCreatedOn(new Date());
				candidate.setUpdatedOn(new Date());
				candidate.setUpdatedBy(candidateVo.getUpdatedBy() != null ? candidateVo.getUpdatedBy() : "");
			} else {
				candidate.setUpdatedOn(new Date());
				candidate.setUpdatedBy(candidateVo.getUpdatedBy() != null ? candidateVo.getUpdatedBy() : "");
			}
			candidate.setCreatedUser(candidateVo.getUploadedBy() != null ? candidateVo.getUploadedBy() : "");
			candidate.setFirstName(candidateVo.getFirstname() != null ? candidateVo.getFirstname() : "");
			candidate.setLastName(candidateVo.getLastname() != null ? candidateVo.getLastname() : "");
			candidate.setEmail(candidateVo.getEmail() != null ? candidateVo.getEmail() : "");
			candidate.setTitle(candidateVo.getTitle() != null ? candidateVo.getTitle() : "");
			candidate.setKeySkill(candidateVo.getKeySkills() != null ? candidateVo.getKeySkills() : "");
			candidate.setReason(
					candidateVo.getReason() != null ? candidateVo.getReason().toString().getBytes() : "".getBytes());
			candidate.setJobType(candidateVo.getJobType() != null && candidateVo.getJobType().trim().length() > 0
					? JobType.valueOf(candidateVo.getJobType()) : null);
			candidate.setPhone(candidateVo.getPhoneCell() != null ? candidateVo.getPhoneCell() : "");
			candidate.setSkills(candidateVo.getSkills() != null ? candidateVo.getSkills() : "");
			candidate.setPhoneAlt(candidateVo.getPhoneWork() != null ? candidateVo.getPhoneWork() : "");
			candidate.setPortalResumeQual(candidateVo.getQualification() != null ? candidateVo.getQualification() : "");
			candidate.setPortalResumeExperience(
					candidateVo.getTotalExperience() != null ? candidateVo.getTotalExperience() : "");
			candidate.setPortalResumeLastComp(candidateVo.getLastCompany() != null ? candidateVo.getLastCompany() : "");
			candidate.setPortalResumeLastPosition(
					candidateVo.getLastPosition() != null ? candidateVo.getLastPosition() : "");
			candidate.setEmploymentStatus(
					candidateVo.getEmploymentStatus() != null ? candidateVo.getEmploymentStatus() : "");
			candidate.setMinimumSalary(
					candidateVo.getMinSalaryRequirement() != null ? candidateVo.getMinSalaryRequirement() : "");
			candidate.setPresentRate(candidateVo.getPresentRate() != null ? candidateVo.getPresentRate() : "");
			candidate.setExpectedRate(candidateVo.getExpectedRate() != null ? candidateVo.getExpectedRate() : "");
			candidate.setPortalViewedBy(candidateVo.getAtsUserId() != null ? candidateVo.getAtsUserId() : "");
			candidate.setOtherResumeSource(
					candidateVo.getOtherResumeSource() != null ? candidateVo.getOtherResumeSource() : "");

			candidate.setDeleteFlag(0);
			candidate.setPortalEmail(candidateVo.getPortalEmail() != null ? candidateVo.getPortalEmail() : "");

			Address address = new Address();
			address.setCity(candidateVo.getCity() != null ? candidateVo.getCity() : "");
			address.setState(candidateVo.getState() != null ? candidateVo.getState() : "");
			address.setZipcode(candidateVo.getZip() != null ? candidateVo.getZip() : "");
			address.setStreet1(candidateVo.getAddress1() != null ? candidateVo.getAddress1() : "");
			address.setStreet2(candidateVo.getAddress2() != null ? candidateVo.getAddress2() : "");

			candidate.setAddress(address);
			for (CandidateStatus candidateStatus : CandidateStatus.values()) {
				if (candidateStatus.toString().equalsIgnoreCase(candidateVo.getStatus())) {
					candidate.setStatus(candidateStatus);
					break;
				}
			}
			candidate.setVisaType(candidateVo.getVisaType() != null ? candidateVo.getVisaType() : "Not Available");
			candidate.setSecurityClearance(Utils.getDefaultBooleanValue(candidateVo.isSecurityClearance()));

			if (candidateVo.getVisaExpiryDate() != null && candidateVo.getVisaExpiryDate().trim().length() > 0) {
				if (candidateVo.getVisaExpiryDate().contains("/")) {
					candidate.setVisaExpiredDate(formatter.parse(candidateVo.getVisaExpiryDate()));
				} else {
					candidate.setVisaExpiredDate(Utils.convertStringToDate(candidateVo.getVisaExpiryDate()));
				}
			}

			if (!Utils.isEmpty(files)) {

				String fileNameArray[] = Utils.getStrArray_FromStr(fileNames);

				if (fileNameArray != null && fileNameArray.length > 0) {
					for (int i = 0; i < fileNameArray.length; i++) {
						if (fileNameArray[i].trim().length() > 0) {

							switch (fileNameArray[i]) {
							case "originalFile":
								MultipartFile originalDoc = files.get(i - 1);

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
								if (ext.equals("html") || ext.equals("htm")) {
									if (candidate.getResume().getParsedText() != null) {
										candidate.setDocument(candidate.getResume().getParsedText().getBytes(),
												ContentType.HTML);
									}
								}
								break;

							case "cgiFile":
								MultipartFile cgiResume = files.get(i - 1);

								String extCgi = FilenameUtils.getExtension(cgiResume.getOriginalFilename());
								if (extCgi.equals("docx"))
									candidate.setProcessedDocument(cgiResume.getBytes(), ContentType.DOCX);
								else if (extCgi.equals("doc"))
									candidate.setProcessedDocument(cgiResume.getBytes(), ContentType.MS_WORD);
								else if (extCgi.equals("txt"))
									candidate.setProcessedDocument(cgiResume.getBytes(), ContentType.PLAIN);
								else if (extCgi.equals("html") || extCgi.equals("htm"))
									candidate.setProcessedDocument(cgiResume.getBytes(), ContentType.HTML);
								else if (extCgi.equals("rtf"))
									candidate.setProcessedDocument(cgiResume.getBytes(), ContentType.RTF);
								else
									candidate.setProcessedDocument(cgiResume.getBytes(), ContentType.PDF);
								break;
							case "rtrFile":
								MultipartFile rtrResume = files.get(i - 1);
								String extRtr = FilenameUtils.getExtension(rtrResume.getOriginalFilename());
								if (extRtr.equals("docx"))
									candidate.setRtrDocument(rtrResume.getBytes(), ContentType.DOCX);
								else if (extRtr.equals("doc"))
									candidate.setRtrDocument(rtrResume.getBytes(), ContentType.MS_WORD);
								else if (extRtr.equals("txt"))
									candidate.setRtrDocument(rtrResume.getBytes(), ContentType.PLAIN);
								else if (extRtr.equals("html") || extRtr.equals("htm"))
									candidate.setRtrDocument(rtrResume.getBytes(), ContentType.HTML);
								else if (extRtr.equals("rtf"))
									candidate.setRtrDocument(rtrResume.getBytes(), ContentType.RTF);
								else
									candidate.setRtrDocument(rtrResume.getBytes(), ContentType.PDF);
							}

						}
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
		}
	}

	public static void getIndiaCandidate(IndiaCandidate candidate, CandidateVo candidateVo, List<MultipartFile> files,
			String actionType, String fileNames) {
		try {
//			if (actionType.equalsIgnoreCase("create")) {
//				candidate.setCreatedOn(new Date());
//				candidate.setUpdatedOn(null);
//			} else {
//				candidate.setUpdatedOn(new Date());
//				candidate.setUpdatedBy(candidateVo.getUploadedBy() != null ? candidateVo.getUploadedBy() : "");
//			}
//			candidate.setCreatedUser(candidateVo.getUploadedBy() != null ? candidateVo.getUploadedBy() : "");
//			
			
			if (actionType.equalsIgnoreCase("create")) {
				candidate.setCreatedOn(new Date());
				candidate.setUpdatedOn(new Date());
				candidate.setUpdatedBy(candidateVo.getUpdatedBy() != null ? candidateVo.getUpdatedBy() : "");
			} else {
				candidate.setUpdatedOn(new Date());
				candidate.setUpdatedBy(candidateVo.getUpdatedBy() != null ? candidateVo.getUpdatedBy() : candidate.getUpdatedBy());
			}
			candidate.setCreatedUser(candidateVo.getUploadedBy() != null ? candidateVo.getUploadedBy() : "");
			
			candidate.setFirstName(candidateVo.getFirstname() != null ? candidateVo.getFirstname() : "");
			candidate.setLastName(candidateVo.getLastname() != null ? candidateVo.getLastname() : "");
			candidate.setEmail(candidateVo.getEmail() != null ? candidateVo.getEmail() : "");
			candidate.setTitle(candidateVo.getTitle() != null ? candidateVo.getTitle() : "");
			candidate.setJobType(
					candidateVo.getJobType() != null ? JobType.valueOf(candidateVo.getJobType()) : JobType.valueOf(""));
			
			candidate.setReason(candidateVo.getReason() != null ? candidateVo.getReason().toString().getBytes() : "".getBytes());
			
			candidate.setKeySkill(Utils.nullIfBlank(candidateVo.getKeySkills()));
			candidate.setPhone(candidateVo.getPhoneCell() != null ? candidateVo.getPhoneCell() : "");
			candidate.setPhoneAlt(candidateVo.getPhoneWork() != null ? candidateVo.getPhoneWork() : "");
			candidate.setPortalResumeQual(candidateVo.getQualification() != null ? candidateVo.getQualification() : "");
			candidate.setPortalResumeExperience(
					candidateVo.getTotalExperience() != null ? candidateVo.getTotalExperience() : "");
			candidate.setRelevantExperience(candidateVo.getRelevantExperience() !=null ? candidateVo.getRelevantExperience() : "");
			candidate.setPortalResumeLastComp(candidateVo.getLastCompany() != null ? candidateVo.getLastCompany() : "");
			candidate.setPortalResumeLastPosition(
					candidateVo.getLastPosition() != null ? candidateVo.getLastPosition() : "");
			candidate.setEmploymentStatus(
					candidateVo.getEmploymentStatus() != null ? candidateVo.getEmploymentStatus() : "");
			candidate.setPresentRate(candidateVo.getPresentRate() != null ? candidateVo.getPresentRate() : "");
			candidate.setExpectedRate(candidateVo.getExpectedRate() != null ? candidateVo.getExpectedRate() : "");
			candidate.setPortalViewedBy(candidateVo.getAtsUserId() != null ? candidateVo.getAtsUserId() : "");
			candidate.setOtherResumeSource(
					candidateVo.getOtherResumeSource() != null ? candidateVo.getOtherResumeSource() : "");

			candidate.setDeleteFlag(0);
			candidate.setPortalEmail(candidateVo.getPortalEmail() != null ? candidateVo.getPortalEmail() : "");

			Address address = new Address();
			address.setCity(candidateVo.getCity() != null ? candidateVo.getCity() : "");
			address.setState(candidateVo.getState() != null ? candidateVo.getState() : "");
			address.setZipcode(candidateVo.getZip() != null ? candidateVo.getZip() : "");
			address.setStreet1(candidateVo.getAddress1() != null ? candidateVo.getAddress1() : "");
			address.setStreet2(candidateVo.getAddress2() != null ? candidateVo.getAddress2() : "");

			candidate.setAddress(address);
			for (CandidateStatus candidateStatus : CandidateStatus.values()) {
				if (candidateStatus.toString().equalsIgnoreCase(candidateVo.getStatus())) {
					candidate.setStatus(candidateStatus);
					break;
				}
			}
			/*
			 * candidate.setVisaType(candidateVo.getVisaType() != null ?
			 * candidateVo.getVisaType() : "Not Available");
			 * 
			 * if (candidateVo.getVisaExpiryDate() != null) { if
			 * (candidateVo.getVisaExpiryDate().contains("/")) {
			 * candidate.setVisaExpiredDate(formatter.parse(candidateVo.
			 * getVisaExpiryDate())); } else {
			 * candidate.setVisaExpiredDate(Utils.convertAngularStrToDate(
			 * candidateVo.getVisaExpiryDate())); } }
			 */
			if (!Utils.isEmpty(files)) {

				String fileNameArray[] = Utils.getStrArray_FromStr(fileNames);

				if (fileNameArray != null && fileNameArray.length > 0) {
					for (int i = 0; i < fileNameArray.length; i++) {
						if (fileNameArray[i].trim().length() > 0) {

							switch (fileNameArray[i]) {
							case "originalFile":
								MultipartFile originalDoc = files.get(i - 1);

								String ext = FilenameUtils.getExtension(originalDoc.getOriginalFilename());
								if (ext.equals("docx"))
									candidate.setDocument(originalDoc.getBytes(), ContentType.DOCX);
								else if (ext.equals("doc"))
									candidate.setDocument(originalDoc.getBytes(), ContentType.MS_WORD);
								else if (ext.equals("txt"))
									candidate.setDocument(originalDoc.getBytes(), ContentType.PLAIN);
								else if (ext.equals("rtf"))
									candidate.setDocument(originalDoc.getBytes(), ContentType.RTF);
								else if (ext.equals("html") || ext.equals("htm"))
									candidate.setDocument(originalDoc.getBytes(), ContentType.HTML);
								else
									candidate.setDocument(originalDoc.getBytes(), ContentType.PDF);
								candidate.parseDocument();
								break;

							case "cgiFile":
								MultipartFile cgiResume = files.get(i - 1);

								String extCgi = FilenameUtils.getExtension(cgiResume.getOriginalFilename());
								if (extCgi.equals("docx"))
									candidate.setProcessedDocument(cgiResume.getBytes(), ContentType.DOCX);
								else if (extCgi.equals("doc"))
									candidate.setProcessedDocument(cgiResume.getBytes(), ContentType.MS_WORD);
								else if (extCgi.equals("txt"))
									candidate.setProcessedDocument(cgiResume.getBytes(), ContentType.PLAIN);
								else if (extCgi.equals("rtf"))
									candidate.setProcessedDocument(cgiResume.getBytes(), ContentType.RTF);
								else if (extCgi.equals("html") || extCgi.equals("htm"))
									candidate.setProcessedDocument(cgiResume.getBytes(), ContentType.HTML);
								else
									candidate.setProcessedDocument(cgiResume.getBytes(), ContentType.PDF);
								break;
							case "rtrFile":
								MultipartFile rtrResume = files.get(i - 1);
								String extRtr = FilenameUtils.getExtension(rtrResume.getOriginalFilename());
								if (extRtr.equals("docx"))
									candidate.setRtrDocument(rtrResume.getBytes(), ContentType.DOCX);
								else if (extRtr.equals("doc"))
									candidate.setRtrDocument(rtrResume.getBytes(), ContentType.MS_WORD);
								else if (extRtr.equals("txt"))
									candidate.setRtrDocument(rtrResume.getBytes(), ContentType.PLAIN);
								else if (extRtr.equals("rtf"))
									candidate.setRtrDocument(rtrResume.getBytes(), ContentType.RTF);
								else if (extRtr.equals("html") || extRtr.equals("htm"))
									candidate.setRtrDocument(rtrResume.getBytes(), ContentType.HTML);
								else
									candidate.setRtrDocument(rtrResume.getBytes(), ContentType.PDF);
							}

						}
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
		}
	}

	public static SubmittalEvent getSubmittalEvent(SubmittalEvent submittalEvent, SubmittalEventDto submittalEventDto) {
		if (submittalEventDto.getStrCreatedOn() != null) {
			submittalEvent.setCreatedDate(Utils.convertStringToDate_HH_MM(submittalEventDto.getStrCreatedOn()));
		} else {
			submittalEvent.setCreatedDate(new Date());
		}
		submittalEvent.setStatus(SubmittalStatus.valueOf(submittalEventDto.getStatus()));
		submittalEvent.setNotes(submittalEventDto.getNotes());
		submittalEvent.setCreatedBy(submittalEventDto.getCreatedBy());
		submittalEvent.setUpdatedBy(submittalEventDto.getUpdatedBy());
		submittalEvent.setUpdatedOn(new Date());
		return submittalEvent;
	}

	public static IndiaSubmittalEvent getSubmittalEvent(IndiaSubmittalEvent submittalEvent,
			SubmittalEventDto submittalEventDto) {
		submittalEvent.setCreatedDate(Utils.convertStringToDate_HH_MM_India(submittalEventDto.getStrCreatedOn()));
		submittalEvent.setStatus(SubmittalStatus.valueOf(submittalEventDto.getStatus()));
		submittalEvent.setNotes(submittalEventDto.getNotes());
		submittalEvent.setCreatedBy(submittalEventDto.getCreatedBy());
		submittalEvent.setUpdatedBy(submittalEventDto.getUpdatedBy());
		submittalEvent.setUpdatedOn(new Date());
		return submittalEvent;
	}

	public static void getOfferLetter(OfferLetterDto offerLetterDto, OfferLetter offerLetter) {

		offerLetter.setBackgroundcheck(offerLetterDto.getBackgroundcheck());
		offerLetter.setBdmName(offerLetterDto.getBdmName());
		offerLetter.setBenifitsCategory(offerLetterDto.getBenifitsCategory());
		offerLetter.setBonusDescription(offerLetterDto.getBonusDescription());
		offerLetter.setCity(offerLetterDto.getCity());
		offerLetter.setClient_extension(offerLetterDto.getClient_extension());
		offerLetter.setClient_fax(offerLetterDto.getClient_fax());
		offerLetter.setClientcity(offerLetterDto.getClientcity());
		offerLetter.setClientcountry(offerLetterDto.getClientcountry());
		offerLetter.setClientemail(offerLetterDto.getClientemail());
		offerLetter.setClientphone(offerLetterDto.getClientphone());
		offerLetter.setClientstate(offerLetterDto.getClientstate());
		// Modified
		offerLetter.setClientstreet(offerLetterDto.getClientstreet1());
		offerLetter.setClientstreet2(offerLetterDto.getClientstreet2());

		offerLetter.setClientzipcode(offerLetterDto.getClientzipcode());
		offerLetter.setCommibonusEligible(offerLetterDto.getCommibonusEligible());
		offerLetter.setCompanyName(offerLetterDto.getCompanyName());
		// Modified
		offerLetter.setContactPerson(null);
		offerLetter.setContactPersonFirstName(offerLetterDto.getContactPersonFirstName());
		offerLetter.setContactPersonLastName(offerLetterDto.getContactPersonLastName());

		offerLetter.setCountry(offerLetterDto.getCountry());
		offerLetter.setDeleteFlag(offerLetterDto.getDeleteFlag());
		offerLetter.setDrugcheck(offerLetterDto.getDrugcheck());
		offerLetter.setElectingMedicalHourly(offerLetterDto.isElectingMedicalHourly());
		offerLetter.setElectingMedicalSalired(offerLetterDto.isElectingMedicalSalired());
		offerLetter.setEmail(offerLetterDto.getEmail());
		offerLetter.setEndclientName(offerLetterDto.getEndclientName());
		offerLetter.setExpections(offerLetterDto.getExpections());
		offerLetter.setFirstName(offerLetterDto.getFirstName());
		offerLetter.setGender(offerLetterDto.getGender());
		offerLetter.setImmigrationStatus(offerLetterDto.getImmigrationStatus());
		offerLetter.setIndividual(offerLetterDto.getIndividual());
		offerLetter.setJobOrderId(offerLetterDto.getJobOrderId());
		offerLetter.setLastName(offerLetterDto.getLastName());
		offerLetter.setNotes(offerLetterDto.getNotes());
		offerLetter.setOfferFrom(offerLetterDto.getOfferFrom());
		offerLetter.setOvertime(offerLetterDto.getOvertime());
		offerLetter.setPaymentTerms(offerLetterDto.getPaymentTerms());
		offerLetter.setPhone(offerLetterDto.getPhone());
		offerLetter.setPositionTitle(offerLetterDto.getPositionTitle());
		offerLetter.setReason(offerLetterDto.getReason());
		offerLetter.setRecruiterName(offerLetterDto.getRecruiterName());
		offerLetter.setRelocationBenefits(Utils.getStringFromArray(offerLetterDto.getRelocationBenefitArray()));
		offerLetter.setSalaryRate(offerLetterDto.getSalaryRate());
		offerLetter.setSpecialInstructions(offerLetterDto.getSpecialInstructions());
		offerLetter.setSsn(offerLetterDto.getSsn());
		offerLetter.setState(offerLetterDto.getState());
		offerLetter.setStatus(OfferLetterStatus.valueOf(offerLetterDto.getStatus()));
		offerLetter.setDateofbirth(Utils.convertAngularStrToDate(offerLetterDto.getStrDateofbirth()));

		// Modified
		offerLetter.setStreet1(offerLetterDto.getStreet1());
		offerLetter.setStreet2(offerLetterDto.getStreet2());

		offerLetter
				.setStartdateOfAssignment(Utils.convertAngularStrToDate(offerLetterDto.getStrStartdateOfAssignment()));
		offerLetter.setTaxId(offerLetterDto.getTaxId());
		offerLetter.setTitleOfName(offerLetterDto.getTitleOfName());
		offerLetter.setWaviningMedicalHourly(offerLetterDto.isWaviningMedicalHourly());
		offerLetter.setWaviningMedicalSalired(offerLetterDto.isWaviningMedicalSalired());
		// Modified
		offerLetter.setWorkLocation(offerLetterDto.getWorkLocation());
		offerLetter.setWorkLocationState(offerLetterDto.getWorkLocationState());

		offerLetter.setZipcode(offerLetterDto.getZipcode());
	}

	public static void getUser(UserDto userDto, User user, String imageObj) {
		try {

			user.setCity(userDto.getCity());
			user.setEmail(userDto.getEmail());
			user.setFirstName(userDto.getFirstName());
			user.setLastName(userDto.getLastName());
			user.setLastLogin(Utils.convertStringToDate(userDto.getStrLastLogin()));
			user.setPhone(userDto.getPhone());
			if (userDto.getLevel() != null) {
				user.setLevel(Integer.parseInt(userDto.getLevel()));
			}
			if (userDto.getJoiningDate() != null) {
				user.setJoinDate(Utils.convertStringToDate(userDto.getJoiningDate()));
			}
			// user.setRelievingDate(Utils.convertStringToDate(userDto.getRelievingDate()));
			if (userDto.getEmployeeId() != null) {
				user.setEmployeeId(userDto.getEmployeeId());
			}
			if (!userDto.getIsFromProfile()) {
				if (userDto.getPassword() != null) {
					user.setPassword(userDto.getPassword());
				}
				user.setUserRole(userDto.getUserRole());
				user.setAssignedBdm(userDto.getAssignedBdm());
				user.setOfficeLocation(userDto.getOfficeLocation());
			}

			if (user.getUserId() != null) {
				user.setUpdatedOn(new Date());
				user.setUpdatedBy(userDto.getUpdatedBy());
			} else {
				user.setCreatedOn(new Date());
				user.setCreatedBy(userDto.getCreatedBy());
			}
			user.setUserId(userDto.getUserId());
			if (userDto.getStatus() != null) {
				user.setStatus(userDto.getStatus());
			}
			// Adding file information
			if (imageObj != null && imageObj.length() > 0 && imageObj.contains(Constants.BASE64)) {

				// user.setProfileImage(Utils.getByteFromBase64String(imageObj));
				user.setUserProfileImage(Utils.getByteFromBase64String(imageObj));
				user.setProfileImageExt(Utils.getFileExtensionFromBase64String(imageObj));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getFilledProjectDetails(Candidate candidate, CandidateInfo candidateInfo, ClientInfo clientInfo,
			AgencyDetails agencyDetails, ClientInfoDto projectDto) {
		// TODO Auto-generated method stub
		if (candidate != null) {
			candidateInfo.setCandidate(candidate);
		}
		candidateInfo.setBdmName(Utils.replaceNullWithEmpty(projectDto.getBdmFirstName()));
		candidateInfo.setBdmLastName(Utils.replaceNullWithEmpty(projectDto.getBdmLastName()));

		candidateInfo.setCity(Utils.replaceNullWithEmpty(projectDto.getCity()));
		candidateInfo.setState(Utils.replaceNullWithEmpty(projectDto.getState()));
		candidateInfo.setCountry(Utils.replaceNullWithEmpty(projectDto.getCountry()));
		candidateInfo.setDateofbirth(Utils.convertStringToDate(projectDto.getDateOfBirth()));
		candidateInfo.setDentalVision(Utils.replaceNullWithEmpty(projectDto.getDentalVision()));
		candidateInfo.setEmail(Utils.replaceNullWithEmpty(projectDto.getEmail()));
		candidateInfo.setEmploymentStatus(Utils.replaceNullWithEmpty(projectDto.getEmploymentStatus()));
		candidateInfo
				.setEndDate(Utils.convertStringToDate(projectDto.getEndDate() != null ? projectDto.getEndDate() : ""));
		candidateInfo.setExpections(Utils.replaceNullWithEmpty(projectDto.getExpections()));
		candidateInfo.setFirstName(Utils.replaceNullWithEmpty(projectDto.getFirstName()));
		candidateInfo.setImmigrationStatus(Utils.replaceNullWithEmpty(projectDto.getImmigrationStatus()));
		candidateInfo.setJobOrderId(Integer.parseInt(projectDto.getJobOrderId()));
		candidateInfo.setLastName(Utils.replaceNullWithEmpty(projectDto.getLastName()));
		candidateInfo.setMedical(Utils.replaceNullWithEmpty(projectDto.getMedical()));
		candidateInfo.setPaidTimeOff(Utils.replaceNullWithEmpty(projectDto.getPaidTimeOff()));
		candidateInfo.setPerDiem(Utils.replaceNullWithEmpty(projectDto.getPerDiem()));
		candidateInfo.setPhone(Utils.replaceNullWithEmpty(projectDto.getCandidatePhone()));
		candidateInfo.setPhoneAlt(Utils.replaceNullWithEmpty(projectDto.getPhoneAlt()));

		candidateInfo.setRecruiterName(Utils.replaceNullWithEmpty(projectDto.getRecruiterFirstName()));
		candidateInfo.setRecruiterLastName(Utils.replaceNullWithEmpty(projectDto.getRecruiterLastName()));
		candidateInfo.setRelocationBenefits(Utils.getStringFromList(projectDto.getRelocationBenefits()));
		candidateInfo.setSalaryRate(Utils.replaceNullWithEmpty(projectDto.getSalaryRate()));
		candidateInfo.setSource(Utils.replaceNullWithEmpty(projectDto.getSource()));
		candidateInfo.setSpecialInstructions(Utils.replaceNullWithEmpty(projectDto.getSpecialInstructions()));
		candidateInfo.setSsn(Utils.replaceNullWithEmpty(projectDto.getSsn()));
		candidateInfo.setStartDate(Utils.convertStringToDate(projectDto.getStartDate()));
		candidateInfo.setStreet1(Utils.replaceNullWithEmpty(projectDto.getStreet1()));
		candidateInfo.setStreet2(Utils.replaceNullWithEmpty(projectDto.getStreet2()));
		candidateInfo.setZipcode(Utils.replaceNullWithEmpty(projectDto.getZipCode()));
		candidateInfo.setUpdatedOn(new Date());
		candidateInfo.setUpdatedBy(Utils.replaceNullWithEmpty(projectDto.getCreatedBy()));
		if (candidateInfo != null) {
			clientInfo.setCandidateinfo(candidateInfo);
			clientInfo.setBillRate(Utils.replaceNullWithEmpty(projectDto.getClientbillRate()));
			clientInfo.setClientcity(Utils.replaceNullWithEmpty(projectDto.getClientcity()));
			clientInfo.setClientcountry(Utils.replaceNullWithEmpty(projectDto.getClientCountry()));
			clientInfo.setClientName(Utils.replaceNullWithEmpty(projectDto.getClientName()));
			clientInfo.setClientstate(Utils.replaceNullWithEmpty(projectDto.getClientState()));
			clientInfo.setClientstreet1(Utils.replaceNullWithEmpty(projectDto.getClientStreet1()));
			clientInfo.setClientstreet2(Utils.replaceNullWithEmpty(projectDto.getClientStreet2()));
			clientInfo.setClientzipcode(Utils.replaceNullWithEmpty(projectDto.getClientZipCode()));
			clientInfo.setContactPerson(Utils.replaceNullWithEmpty(projectDto.getClientContactFirstName()));
			clientInfo.setPersonLastName(Utils.replaceNullWithEmpty(projectDto.getClientContactLastName()));
			clientInfo.setEmail(Utils.replaceNullWithEmpty(projectDto.getClientEmail()));
			clientInfo.setEndClientName(Utils.replaceNullWithEmpty(projectDto.getEndClientName()));
			clientInfo.setExtension(Utils.replaceNullWithEmpty(projectDto.getExtenstion()));
			clientInfo.setFax(Utils.replaceNullWithEmpty(projectDto.getFax()));
			clientInfo.setInvoicing(Utils.replaceNullWithEmpty(projectDto.getClientinvoicing()));
			clientInfo.setInvoicingcity(Utils.replaceNullWithEmpty(projectDto.getInvoicingcity()));
			clientInfo.setInvoicingcountry(Utils.replaceNullWithEmpty(projectDto.getInvoicingCountry()));
			clientInfo.setInvoicingstate(Utils.replaceNullWithEmpty(projectDto.getInvoicingState()));
			clientInfo.setInvoicingstreet1(Utils.replaceNullWithEmpty(projectDto.getInvoicingStreet1()));
			clientInfo.setInvoicingstreet2(Utils.replaceNullWithEmpty(projectDto.getInvoicingstreet2()));
			clientInfo.setInvoicingzipcode(Utils.replaceNullWithEmpty(projectDto.getInvoicingZipCode()));
			clientInfo.setOtEligibility(Utils.convertStringToBooleanValue(projectDto.getClientotEligibility()));
			clientInfo.setOtRate(Utils.replaceNullWithEmpty(projectDto.getClientotRate()));
			clientInfo.setPaymentTerms(Utils.replaceNullWithEmpty(projectDto.getClientpaymentTerms()));
			clientInfo.setPhone(Utils.replaceNullWithEmpty(projectDto.getClientPhone()));
			clientInfo.setProjectManagerExtension(Utils.replaceNullWithEmpty(projectDto.getProjectManagerExtension()));
			clientInfo.setProjectManagerName(Utils.replaceNullWithEmpty(projectDto.getProjectManagerName()));
			clientInfo.setProjectManagerPhone(Utils.replaceNullWithEmpty(projectDto.getProjectManagerPhone()));
			clientInfo.setUpdatedBy(Utils.replaceNullWithEmpty(projectDto.getCreatedBy()));
			clientInfo.setUpdatedOn(new Date());
			if (candidateInfo != null) {
				agencyDetails.setCandidateinfo(candidateInfo);
			}
			agencyDetails.setC2cAgencyName(Utils.replaceNullWithEmpty(projectDto.getC2cAgencyName()));
			agencyDetails.setCity(Utils.replaceNullWithEmpty(projectDto.getC2ccity()));
			agencyDetails.setContactPersonEmail(Utils.replaceNullWithEmpty(projectDto.getContactPersonEmail()));
			agencyDetails.setContactPersonExtension(Utils.replaceNullWithEmpty(projectDto.getContactPersonExtension()));
			agencyDetails.setContactPersonFax(Utils.replaceNullWithEmpty(projectDto.getContactPersonFax()));
			agencyDetails.setContactPersonName(Utils.replaceNullWithEmpty(projectDto.getC2cContactFirstName()));
			agencyDetails.setContactPersonLastName(Utils.replaceNullWithEmpty(projectDto.getC2cContactLastName()));
			agencyDetails.setContactPersonPhone(Utils.replaceNullWithEmpty(projectDto.getContactPersonPhone()));
			agencyDetails.setCountry(Utils.replaceNullWithEmpty(projectDto.getC2ccountry()));
			agencyDetails.setPaymentTerms(Utils.replaceNullWithEmpty(projectDto.getClientpaymentTerms()));
			agencyDetails.setState(Utils.replaceNullWithEmpty(projectDto.getC2cstate()));
			agencyDetails.setStreet1(Utils.replaceNullWithEmpty(projectDto.getC2cstreet1()));
			agencyDetails.setStreet2(Utils.replaceNullWithEmpty(projectDto.getC2cstreet2()));
			agencyDetails.setUpdatedBy(Utils.replaceNullWithEmpty(projectDto.getCreatedBy()));
			agencyDetails.setUpdatedOn(new Date());
			agencyDetails.setZipcode(Utils.replaceNullWithEmpty(projectDto.getC2czipCode()));

		}

	}

	public static void getuserloginAttempt(UserDto dto, LoginAttempts attempts) {
		SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
		if (!Utils.isNull(dto)) {
			if (Utils.isBlank(dto.getLoginAttemptId())) {

				attempts.setCreatedBy(Utils.nullIfBlank(dto.getUserId()));
				attempts.setCreatedOn(new Date());
				attempts.setLoginDate(new Date());
				attempts.setDuration(localDateFormat.format(new Date()));
				// attempts.setStatus("login");
			} else {
				attempts.setDurationTime(Utils.getTimeDifferenceBitweenTwoDates(attempts.getDuration(),
						localDateFormat.format(new Date())));
				attempts.setLogoutDate(new Date());
				// attempts.setStatus("logout");
			}

		}

	}

}

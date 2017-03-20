package com.uralian.cgiats.util;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uralian.cgiats.dto.AddEditJobOrderDto;
import com.uralian.cgiats.model.IndiaJobOrder;

public class TransformEntityToDtoForIndia {
	private final static Logger LOG = LoggerFactory.getLogger(TransformEntityToDto.class);

		public static AddEditJobOrderDto getJobOrderDto(IndiaJobOrder jobOrder) {
		AddEditJobOrderDto dto = new AddEditJobOrderDto();
		try {
			dto.setId(jobOrder.getId());
//			dto.setAccept1099(jobOrder.isAccept1099());
//			dto.setAcceptC2c(jobOrder.isAcceptC2c());
//			dto.setAcceptW2(jobOrder.isAcceptW2());
			dto.setAnnualRateW2(jobOrder.getAnnualRateW2());
			dto.setAssignedTo(jobOrder.getAssignedTo());
			// jobOrder.setAttachmentFileName(attachmentFileName);
			dto.setCategory(jobOrder.getCategory());
			dto.setCity(jobOrder.getCity());
//			dto.setCompanyFlag(jobOrder.getCompanyFlag());
			dto.setCustomer(jobOrder.getCustomer());
			dto.setDmName(jobOrder.getDmName());
			dto.setStrCustomerHidden(Utils.convertBooleanToStringValue(jobOrder.getCustomerHidden()));
			dto.setDays(jobOrder.getDays());
			dto.setDeleteFlag(0);
			dto.setDescription(jobOrder.getDescription());
			// dto.setStartDate(Utils.convertDateToString(jobOrder.getStartDate()));
//			dto.setEmName(jobOrder.getEmName());
			dto.setEndDate(Utils.convertDateToString_India(jobOrder.getEndDate()));
//			dto.setHourlyRate1099(jobOrder.getHourlyRate1099());
//			dto.setHourlyRateC2c(jobOrder.getHourlyRateC2c());
//			dto.setHourlyRateW2(jobOrder.getHourlyRateW2());
			dto.setHoursToOpen(jobOrder.getHoursToOpen());
			dto.setJobType(jobOrder.getJobType().toString());
			dto.setKeySkills(jobOrder.getKeySkills());
//			dto.setLocation(jobOrder.getLocation());
//			dto.setNote(jobOrder.getNote());
			dto.setNumOfPos(jobOrder.getNumOfPos());
			//dto.setNoOfResumesRequired(Utils.getDefaultIntegerValue(jobOrder.getNumOfRsumes()));
			dto.setNoOfResumesRequired(jobOrder.getNumOfRsumes());
//			dto.setOnlineFlag(jobOrder.getOnlineFlag());
			dto.setPayrate(jobOrder.getPayrate());
//			dto.setPermFee(jobOrder.getPermFee());
			dto.setPriority(jobOrder.getPriority().toString());
			dto.setSalary(jobOrder.getSalary());
			dto.setStartDate(Utils.convertDateToString_India(jobOrder.getStartDate()));
//			dto.setState(jobOrder.getState());
			dto.setStatus(jobOrder.getStatus().toString());
			dto.setCreatedBy(jobOrder.getCreatedBy());
			dto.setMaxSal(Utils.getDefaultIntegerValue(jobOrder.getMaxSalary()));
			dto.setMinExp(Utils.nullIfBlank(jobOrder.getMinExp()));
			dto.setMaxExp(Utils.nullIfBlank(jobOrder.getMaxExp()));
			dto.setEducation(Utils.nullIfBlank(jobOrder.getEducation()));
			dto.setTitle(jobOrder.getTitle());
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
		 
		/*	if (jobOrder.getFieldList() != null && jobOrder.getFieldList().size() > 0) {
				List<JobOrderFieldDto> jobOrderFieldList = new ArrayList<JobOrderFieldDto>();
				for (IndiaJobOrderField jobOrderField : jobOrder.getFieldList()) {
					jobOrderFieldList.add(getJobOrderField(jobOrderField));
				}
				dto.setJobOrderFieldList(jobOrderFieldList);
			}*/

		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
		}

		return dto;
	}

	/*	public static JobOrderFieldDto getJobOrderField(IndiaJobOrderField jobOrderField) {
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
	}*/

}

package com.uralian.cgiats.util;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.uralian.cgiats.dto.AddEditJobOrderDto;
import com.uralian.cgiats.model.IndiaJobOrder;
import com.uralian.cgiats.model.JobOrderPriority;
import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.JobType;

public class TransformDtoToEntityForIndia {

	public static IndiaJobOrder getIndiaJobOrder(AddEditJobOrderDto addEditJobOrderDto, IndiaJobOrder indiaJobOrder,
			List<MultipartFile> files) {
		try {
//		indiaJobOrder.setAccept1099(Utils.getDefaultBooleanValue(addEditJobOrderDto.getAccept1099()));
//		indiaJobOrder.setAcceptC2c(Utils.getDefaultBooleanValue(addEditJobOrderDto.getAcceptC2c()));
//		indiaJobOrder.setAcceptW2(Utils.getDefaultBooleanValue(addEditJobOrderDto.getAcceptW2()));
		indiaJobOrder.setAnnualRateW2(Utils.getDefaultIntegerValue(addEditJobOrderDto.getAnnualRateW2()));
		indiaJobOrder.setAssignedTo(addEditJobOrderDto.getAssignedTo());
		indiaJobOrder.setCategory(addEditJobOrderDto.getCategory());
		indiaJobOrder.setCity(Utils.nullIfBlank(addEditJobOrderDto.getCity()));
		indiaJobOrder.setUpdatedOn(new Date());
		indiaJobOrder.setUpdatedBy(addEditJobOrderDto.getUpdatedBy());
		if(indiaJobOrder.getId() == null){
			indiaJobOrder.setCreatedOn(new Date());
			indiaJobOrder.setCreatedBy(addEditJobOrderDto.getCreatedBy());
		}
		indiaJobOrder.setCustomer(addEditJobOrderDto.getCustomer());
		indiaJobOrder.setDmName(addEditJobOrderDto.getDmName());
		indiaJobOrder.setCustomerHidden(Utils.convertStringToBooleanValue(addEditJobOrderDto.getStrCustomerHidden()));
		indiaJobOrder.setDays(Utils.getDefaultLongValue(addEditJobOrderDto.getDays()));
		indiaJobOrder.setDeleteFlag(0);
		indiaJobOrder.setDescription(addEditJobOrderDto.getDescription());
//		indiaJobOrder.setEmName(addEditJobOrderDto.getEmName());
		indiaJobOrder.setEndDate(Utils.convertStringToDate_India(addEditJobOrderDto.getEndDate()));
//		indiaJobOrder.setHourlyRate1099(Utils.getDefaultIntegerValue(addEditJobOrderDto.getHourlyRate1099()));
//		indiaJobOrder.setHourlyRateC2c(Utils.getDefaultIntegerValue(addEditJobOrderDto.getHourlyRateC2c()));
//		indiaJobOrder.setHourlyRateW2(Utils.getDefaultIntegerValue(addEditJobOrderDto.getHourlyRateW2()));
//		indiaJobOrder.setHoursToOpen(Utils.getDefaultIntegerValue(addEditJobOrderDto.getHoursToOpen()));
		indiaJobOrder.setJobType(JobType.valueOf(addEditJobOrderDto.getJobType()));
		indiaJobOrder.setKeySkills(addEditJobOrderDto.getKeySkills());
//		indiaJobOrder.setLocation(addEditJobOrderDto.getLocation());
//		indiaJobOrder.setNote(addEditJobOrderDto.getNote());
		indiaJobOrder.setNumOfPos(Utils.getDefaultIntegerValue(addEditJobOrderDto.getNumOfPos()));
		//indiaJobOrder.setNumOfRsumes(Utils.getDefaultIntegerValue(addEditJobOrderDto.getNoOfResumesRequired()));
		indiaJobOrder.setNumOfRsumes(addEditJobOrderDto.getNoOfResumesRequired());
//		indiaJobOrder.setOnlineFlag(addEditJobOrderDto.getOnlineFlag());
//		indiaJobOrder.setPayrate(Utils.getDefaultIntegerValue(addEditJobOrderDto.getPayrate()));
//		indiaJobOrder.setPermFee(Utils.getDefaultIntegerValue(addEditJobOrderDto.getPermFee()));
		indiaJobOrder.setPriority(JobOrderPriority.valueOf(addEditJobOrderDto.getPriority()));
		indiaJobOrder.setSalary(Utils.getDefaultIntegerValue(addEditJobOrderDto.getSalary()));
		indiaJobOrder.setStartDate(Utils.convertStringToDate_India(addEditJobOrderDto.getStartDate()));
//		indiaJobOrder.setState(addEditJobOrderDto.getState());
		indiaJobOrder.setStatus(JobOrderStatus.valueOf(addEditJobOrderDto.getStatus()));
		indiaJobOrder.setMaxSalary(Utils.getDefaultIntegerValue(addEditJobOrderDto.getMaxSal()));
		indiaJobOrder.setMinExp(Utils.nullIfBlank(addEditJobOrderDto.getMinExp()));
		indiaJobOrder.setMaxExp(Utils.nullIfBlank(addEditJobOrderDto.getMaxExp()));
		indiaJobOrder.setEducation(Utils.nullIfBlank(addEditJobOrderDto.getEducation()));
		indiaJobOrder.setTitle(addEditJobOrderDto.getTitle());
		indiaJobOrder.setVersion(0);
		if (!Utils.isEmpty(files)) {
			MultipartFile originalDoc = files.get(0);
			String fileName = originalDoc.getOriginalFilename();
			
				indiaJobOrder.setAttachment(originalDoc.getBytes());
			
			indiaJobOrder.setAttachmentFileName(fileName);
		}
		// Adding job order fields
		/*if (addEditJobOrderDto.getJobOrderFieldList() != null && addEditJobOrderDto.getJobOrderFieldList().size() > 0) {
			Map<String, IndiaJobOrderField> fields = new HashMap<String, IndiaJobOrderField>();
			for (JobOrderFieldDto jobOrderFieldDto : addEditJobOrderDto.getJobOrderFieldList()) {
				IndiaJobOrderField field = fields.get(jobOrderFieldDto.getFieldName());
				if (field == null) {
					field = new IndiaJobOrderField();
					field.setFieldName(jobOrderFieldDto.getFieldName());
					field.setFieldValue(jobOrderFieldDto.getFieldValue());
					field.setVisible(jobOrderFieldDto.getVisible());
				} else {
					field.setFieldValue(jobOrderFieldDto.getFieldValue());
					field.setVisible(jobOrderFieldDto.getVisible());
				}
				field.setIndiaJobOrder(indiaJobOrder);
				fields.put(jobOrderFieldDto.getFieldName(), field);
			}
			indiaJobOrder.setFields(fields);
		}*/
		} catch (IOException e) {
			e.printStackTrace();
		}
		return indiaJobOrder;
	}

}

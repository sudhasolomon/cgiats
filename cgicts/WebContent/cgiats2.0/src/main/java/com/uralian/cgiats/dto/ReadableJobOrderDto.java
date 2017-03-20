/**
 * 
 */
package com.uralian.cgiats.dto;

import java.util.List;

/**
 * @author skurapati
 *
 */
public class ReadableJobOrderDto {
	private String fieldName;
	private Object fieldValue;
	private List<JobOrderFieldDto> jobOrderFieldList;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Object getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
	}

	public List<JobOrderFieldDto> getJobOrderFieldList() {
		return jobOrderFieldList;
	}

	public void setJobOrderFieldList(List<JobOrderFieldDto> jobOrderFieldList) {
		this.jobOrderFieldList = jobOrderFieldList;
	}

}

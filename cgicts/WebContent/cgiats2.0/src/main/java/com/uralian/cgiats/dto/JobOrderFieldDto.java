package com.uralian.cgiats.dto;

public class JobOrderFieldDto {
	private Integer id;
	private String fieldName;
	private String fieldValue;
	private Boolean visible;
	private Integer order_Id;
	private Boolean isCollapse;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public Integer getOrder_Id() {
		return order_Id;
	}

	public void setOrder_Id(Integer order_Id) {
		this.order_Id = order_Id;
	}

	/**
	 * @return the visible
	 */
	public Boolean getVisible() {
		return visible;
	}

	/**
	 * @param visible
	 *            the visible to set
	 */
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the isCollapse
	 */
	public Boolean getIsCollapse() {
		return isCollapse;
	}

	/**
	 * @param isCollapse the isCollapse to set
	 */
	public void setIsCollapse(Boolean isCollapse) {
		this.isCollapse = isCollapse;
	}

}
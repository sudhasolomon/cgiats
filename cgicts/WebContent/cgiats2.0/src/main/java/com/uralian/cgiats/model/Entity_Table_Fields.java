package com.uralian.cgiats.model;

public enum Entity_Table_Fields {
	
	CANDIDATE_ID("id","c.candidate_id"),FIRST_NAME("firstName","c.first_name"), TITLE("title","c.title"), CREATED_ON("createdOn","c.created_on"),
	UPDATED_ON("updatedOn","c.updated_on"),STATUS("status","c.status"), LOCATION("location","c.city"), VISA_TYPE("visaType","c.visa_type"),JOB_ORDER_ID("jobOrderId","j.order_id"),
	JOB_TITLE("jobTitle","j.title"),EMAIL("email","c.email"),PHONE("phoneNumber","c.phone"),CREATED_BY("portalInfo","c.created_by"),DM("dmName","j.created_by"),CITY("city","c.city"),STATE("state","c.state");
	String entityField, tableField;
	
	Entity_Table_Fields(String entityField, String tableField){
		this.entityField = entityField;
		this.tableField = tableField;
	}

	public String getEntityField(){
		return entityField;
	}
	public String getTableField(){
	return tableField;
	}
}

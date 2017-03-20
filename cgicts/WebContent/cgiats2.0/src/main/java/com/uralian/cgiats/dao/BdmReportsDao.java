package com.uralian.cgiats.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.model.User;

public interface BdmReportsDao extends GenericDao<Object, Serializable>{

	List<Map<String, Object>> fetchBdmsReports(Date reportStartDate,Date reportEndDate, User user);

	List<Map<String, Object>> fetchAdmsReports(Date startDate, Date endDate,User user);
	
	

}

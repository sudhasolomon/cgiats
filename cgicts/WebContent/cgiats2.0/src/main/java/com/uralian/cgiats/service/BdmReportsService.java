package com.uralian.cgiats.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.model.User;

public interface BdmReportsService {

	List<Map<String, Object>> fetchBdmsReports(Date reportStartDate,Date reportEndDate, User user);

	List<Map<String, Object>> fetchAdmsReports(Date startDate, Date endDate,User user);

}

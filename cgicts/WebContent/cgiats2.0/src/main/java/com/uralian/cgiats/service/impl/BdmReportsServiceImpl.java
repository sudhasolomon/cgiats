package com.uralian.cgiats.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.BdmReportsDao;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.service.BdmReportsService;
import com.uralian.cgiats.service.ServiceException;

@Service
@Transactional(rollbackFor = ServiceException.class)
public class BdmReportsServiceImpl implements BdmReportsService {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private BdmReportsDao reportsDao;

	@Override
	public List<Map<String, Object>> fetchBdmsReports(Date reportStartDate, Date reportEndDate, User user) {
		try {
			return reportsDao.fetchBdmsReports(reportStartDate, reportEndDate, user);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> fetchAdmsReports(Date startDate, Date endDate, User user) {

		try {
			return reportsDao.fetchAdmsReports(startDate, endDate, user);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

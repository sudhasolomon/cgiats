package com.uralian.cgiats.dao.impl;

import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.IndiaResumeHistoryDao;
import com.uralian.cgiats.model.IndiaResumeHistory;

@SuppressWarnings("unchecked")
@Repository
public class IndiaResumeHistoryDaoImpl extends IndiaGenericDaoImpl<IndiaResumeHistory, Integer>  implements IndiaResumeHistoryDao{

	protected IndiaResumeHistoryDaoImpl() {
		super(IndiaResumeHistory.class);
	}

}

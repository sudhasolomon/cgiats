package com.uralian.cgiats.dao.impl;



import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.IndiaCandidateHistoryDao;
import com.uralian.cgiats.model.IndiaCandidateHistory;

@SuppressWarnings("unchecked")
@Repository
public class IndiaCandidateHistoryDaoImpl extends IndiaGenericDaoImpl<IndiaCandidateHistory, Integer>   implements IndiaCandidateHistoryDao {

	protected IndiaCandidateHistoryDaoImpl() 
	{
		super(IndiaCandidateHistory.class);
	}

}

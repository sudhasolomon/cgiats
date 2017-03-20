package com.uralian.cgiats.dao.impl;
/*
 * Radhika
 * Created Date:8/1/2013
 * Comment: Created to store candidate history for the tickets ATS-231 and ATS-235
 */
import org.springframework.stereotype.Repository;
import com.uralian.cgiats.dao.CandidateHistoryDao;
import com.uralian.cgiats.model.CandidateHistory;

/**
 * @author Chaitanya
 *
 */
@SuppressWarnings("unchecked")
@Repository
public class CandidateHistoryDaoImpl extends GenericDaoImpl<CandidateHistory, Integer>  
		implements CandidateHistoryDao{

	protected CandidateHistoryDaoImpl() 
	{
		super(CandidateHistory.class);
	}
}
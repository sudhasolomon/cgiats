package com.uralian.cgiats.dao.impl;
/*
 * Radhika
 * Created Date:8/5/2013
 * Comment: Created to store candidate info for the tickets ATS-
 */
import org.springframework.stereotype.Repository;
import com.uralian.cgiats.dao.CandidateInfoDao;
import com.uralian.cgiats.model.CandidateInfo;


@SuppressWarnings("unchecked")
@Repository
public class CandidateInfoDaoImpl extends GenericDaoImpl<CandidateInfo, Integer>
implements CandidateInfoDao{
	
	/**
	 */
	public CandidateInfoDaoImpl()
	{
		super(CandidateInfo.class);
	}
}

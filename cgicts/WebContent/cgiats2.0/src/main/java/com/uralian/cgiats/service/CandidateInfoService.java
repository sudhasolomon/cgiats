package com.uralian.cgiats.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.CandidateInfoDao;
import com.uralian.cgiats.dao.impl.CandidateInfoDaoImpl;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateInfo;

/*
 * Radhika
 * Created Date:8/5/2013
 * Comment: Created to store candidate info for the tickets ATS-
 */
public interface CandidateInfoService {
	
	/**
	 * @param candidateInfo
	 * @throws ServiceException
	 */
	public void saveCandidate(CandidateInfo candidateInfo) throws ServiceException;
	
	/**
	 * @param candidateInfo
	 * @throws ServiceException
	 */
	public void updateCandidate(CandidateInfo candidateInfo) throws ServiceException;
	
	
	/**
	 * @return
	 */
	public List<CandidateInfo> getCandidateInfo();
	
	/**
	 * @param id
	 * @return
	 */
	public CandidateInfo getCandidateInfo(Integer id);
	
	/**
	 * @param id
	 * @return
	 */
	public CandidateInfo getCandidateInfoByCandidateId(Integer id);
	
}

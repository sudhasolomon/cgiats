package com.uralian.cgiats.service;

import java.util.List;

import com.uralian.cgiats.model.CandidateHistory;
import com.uralian.cgiats.model.IndiaCandidateHistory;

public interface IndiaCandidateHistoryService
{
	
	
	/**
	 * @param candidateHistory
	 * @throws ServiceException
	 */
	public void saveCandidateHistory(IndiaCandidateHistory candidateHistory)	throws ServiceException;

	/**
	 * @param candidateId
	 * @return
	 * 
	 *//*

	public List<CandidateHistory> getHotStatusDetails(Integer candidateId);

	*//**
	 * @param candidateId
	 * @return
	 *//*
	public List<CandidateHistory> getBlackStatusDetails(Integer candidateId);

	*//**
	 * @param candidateId
	 * @return
	 *//*
	public List<CandidateHistory> getDeletedStatusDetails(Integer candidateId);

	*//**
	 * 
	 * Method to send a mail when candidate is moved to hot list
	 * 
	 * @param candidateHistory
	 * @param city
	 */
	public void sendHotMail(IndiaCandidateHistory candidateHistory,	String officeLocation);

	/**
	 * @author Raghavender
	 * @param candidateHistory
	 * @param officeLoc
	 */
	public void sendBlackListMail(IndiaCandidateHistory candidateHistory,String officeLoc);
}

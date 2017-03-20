package com.uralian.cgiats.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.model.IndiaSubmittal;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalEvent;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;

public interface IndiaSubmittalDao extends IndiaGenericDao<IndiaSubmittal, Integer>{
	
	/**
	 * @param bdmNmae
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 *//*
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByBdm(User bdmName,Date dateStart, Date dateEnd);
	
	*//**
	 * @author Surya Chaitanya
	 * @param bdmNmae
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 *//*
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByRecruiter(User bdmName,Date dateStart, Date dateEnd);

	*//**
	 * @author Radhika
	 * @param status
	 * @param dateStart
	 * @param dateEnd
	 * @param createdUser]
	 * @return
	 *//*
	
	public List<Submittal> getSubmittalDetailsByStatus(SubmittalStatus status,Date dateStart,Date dateEnd,String createdUser);

	*//**
	 * @param currentDate
	 * @return
	 *//*
	public List<Submittal> getAllStarterdCandidates(Date currentDate);
	
	*//**
	 * @param bdmName
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 *//*
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByRecruiterDM(
			User bdmName, Date dateStart, Date dateEnd);
	
	public List<Submittal> getAllOutofProjCandidates(Date fromDate,Date toDate);
	
	*//**
	 * @param bdmName
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 *//*
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByDm(
			User bdmName, Date dateStart, Date dateEnd);
	
	
	*//**
	 * Method to edit specific submittalevent updatedBy and UpdatedOn based on the Submittal event id
	 * 
	 * @param sEvent
	 * @return Integer
	 *//*
	public Integer editSubmittalEvent(SubmittalEvent sEvent);
	
	*//**
	 * Method for fetching all the status and submittal counts based on dates given
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 *//*
	public Map<SubmittalStatus, Integer> getAllSubmittalStatusCounts(Date dateStart, Date dateEnd);*/
	
	public List<IndiaSubmittal> getCandidateJobrderStatus(Integer candidateId);

	public Map<SubmittalStatus, Integer> getAllSubmittalStatusCounts(Date dateStart, Date dateEnd);

}

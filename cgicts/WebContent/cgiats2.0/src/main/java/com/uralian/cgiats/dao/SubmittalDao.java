/*
 * SubmittalDao.java Jun 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.dto.CandidateDto;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.JobViewOrder;
import com.uralian.cgiats.model.SubmitalStats;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalEvent;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.rest.UserRoleVo;

/**
 * @author Vlad Orzhekhovskiy
 */
public interface SubmittalDao extends GenericDao<Submittal, Integer>
{
	/**
	 * @param location 
	 * @param bdmNmae
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByBdm(String location, User bdmName,Date dateStart, Date dateEnd);
	
	/**
	 * @author Surya Chaitanya
	 * @param bdmNmae
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByRecruiter(User bdmName,Date dateStart, Date dateEnd);

	/**
	 * @author Radhika
	 * @param status
	 * @param dateStart
	 * @param dateEnd
	 * @param loginUser 
	 * @param createdUser]
	 * @return
	 */
	
	public List<Submittal> getSubmittalDetailsByStatus(SubmittalStatus status,Date dateStart,Date dateEnd,String createdUser, String loginUser);

	/**
	 * @param currentDate
	 * @return
	 */
	public List<Submittal> getAllStarterdCandidates(Date currentDate);
	
	/**
	 * @param selectedLocation 
	 * @param bdmName
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByRecruiterDM(
			String selectedLocation, User bdmName, Date dateStart, Date dateEnd);
	
	public List<Submittal> getAllOutofProjCandidates(Date fromDate,Date toDate);
	
	/**
	 * @param bdmName
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByDm(
			User bdmName, Date dateStart, Date dateEnd);
	
	
	/**
	 * Method to edit specific submittalevent updatedBy and UpdatedOn based on the Submittal event id
	 * 
	 * @param sEvent
	 * @return Integer
	 */
	public Integer editSubmittalEvent(SubmittalEvent sEvent);
	
	/**
	 * Method for fetching all the status and submittal counts based on dates given
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<SubmittalStatus, Integer> getAllSubmittalStatusCounts(Date dateStart, Date dateEnd);
	
	public List<Submittal> getCandidateJobrderStatus(Integer candidateId);
	
	public List<Submittal> mobileSubmittalStatus(Integer jobId,Integer candidateId);

	//Mobile Application
	
	public SubmitalStats executiveTodaySubmittalStatus(UserRoleVo userRoleVo);
	
	public List<SubmitalStats> getYearWiseSubmittalsForExecutives(String year, UserRoleVo userRoleVo);
	
	public List<SubmitalStats> getAllSubmittalsStatsForExecutives(UserRoleVo userRoleVo);
	
	public List<SubmitalStats> getWeekelyStats(Date fromDate, Date toDate, UserRoleVo userRoleVo);
	
	public List<SubmitalStats> getRecruiterWeekelyStats(Date fromDate, Date toDate,String dmName, UserRoleVo userRoleVo);
	
	public List<SubmitalStats> getRecruiterLocationStats(Date fromDate, Date toDate,String location, UserRoleVo userRoleVo);

	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByDm(
			Date fromDate, Date toDate);

	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByDmJobOrder(
			User user, Date fromDate, Date toDate);

	public List<Submittal> getSubmittalDetailsbyStatusSapeareReport(
			SubmittalStatus subStatus, Date startDate, Date endDate,
			String submittalDms, String loginUserName, String portalName);


	public List<CandidateDto> getOutofProjs(CandidateSearchDto statusDto);

	
}

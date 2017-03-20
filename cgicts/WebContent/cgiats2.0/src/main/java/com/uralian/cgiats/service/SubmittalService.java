/**
 * 
 */
package com.uralian.cgiats.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.dto.CandidateDto;
import com.uralian.cgiats.dto.ReportwiseDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.dto.SubmittalStatsDto;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.JobOrderSearchDto;
import com.uralian.cgiats.model.SubmitalStats;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalEvent;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.rest.UserRoleVo;
import com.uralian.cgiats.util.RecRnRReportDto;

/**
 * @author Parameshwar
 *
 */
public interface SubmittalService {
	
	/**
	 * @param criteria
	 * @return
	 */
	public List<SubmittalDto> findSubmittals(JobOrderSearchDto criteria);
	
	public List<?> getWeekWiseRecruiterReport(ReportwiseDto reportwiseDto);
	
	public Object getDMsPerformanceTotalReport(ReportwiseDto reportwiseDto);
	
	public Object getDMWiseRecPerformanceTotalReport(ReportwiseDto reportwiseDto);
	
	public Map<String, SubmittalDto> getInActiveStartedOfRecruiter(Date from, Date to, String dmName, String status,ReportwiseDto reportwiseDto,String location);
	
	public Object getDMWiseRecOverallPerformanceReport(ReportwiseDto reportwiseDto);
	
	public Object getDMWiseJobOrderServiceReport(ReportwiseDto reportwiseDto);
	
	public Object getSubmittal_Service_Of_All_Job_Orders_BY_DM(ReportwiseDto reportwiseDto);
	
	public Object getDMAverageNoOfDaysForStatus(ReportwiseDto reportwiseDto);
	
	public Object getOrderWiseSubmittals(Integer orderId);
	
	public Object getSubmittalDetailByOrderId_Status_CreatedBy(Integer orderId,String createdBy,String status);
	
	public Object getOther_Than_Started_Report(ReportwiseDto reportwiseDto);
	
	public Object getStartsByDateRange(ReportwiseDto reportwiseDto);
	
	
	public Object getAvgNoOfStatusesDaysByJobOrderId(Integer orderId);
	
	public Object getSubmittalServiceDetailByOrderId_Status_CreatedBy(Integer orderId,String createdBy,String status);
	
	public Object findHitRatio(ReportwiseDto reportwiseDto);

	
	
	public List<?> getAllSubmittalYears();
	
	public List<?> getAllAssignedBDMs();
	/**
	 * @return
	 */
	public List<?> getListStartedCandidates();

	/**
	  * @param currentDate
	  * @return
	  */
	 public List<Submittal> getAllStarterdCandidates(Date currentDate);
	 	 
	 /**
	 * @param selectedLocation 
	 * @param bdmNmae
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByBdm(String selectedLocation, User bdmName,Date dateStart, Date dateEnd);
	
	 /**
	 * @param Recruiter
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByRecruiter(User user,Date dateStart, Date dateEnd);
	
	 /**
		 * @param status
		 * @param dateStart
		 * @param dateEnd
		 * @param createdUser
	 * @param loginUserName 
		 * @return
		 */
	public List<Submittal> getSubmittalDetailsbyStatus(SubmittalStatus status,Date dateStart,Date dateEnd,String createdUser, String loginUserName);
	
	/**
	 * @param selectedLocation 
	 * @param user
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByRecruiterDM(String selectedLocation, User user, Date dateStart, Date dateEnd);
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
	 * to edit submittalevent update date 
	 * 
	 * @param sEvent
	 * @return
	 */
	public Integer editSubmittalEvent(SubmittalEvent sEvent);

	/**@author Raghavender
	 * @return
	 */
	//Code added Raghavendra 19/05/14.
	public List<Submittal> getDeletedSubmittals();
	
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<SubmittalStatus, Integer> getAllSubmittalStatusCounts(Date dateStart, Date dateEnd);
	
	public Map<String, Integer> getInActiveStartedCounts(Date dateStart, Date dateEnd);

	public List<Submittal> getCandidateJobrderStatus(Integer candidateId);
	
	public Boolean checkStartedStatusBy_CandidateId_SubmittalId(Integer candidateId,Integer submittalId);
	
	//Mobile Appliccation
	
	public SubmitalStats executiveTodaySubmittalStatus(UserRoleVo userRoleVo);
	
	public List<SubmitalStats> getYearWiseSubmittalsForExecutives(String year, UserRoleVo userRoleVo);
	
	public List<SubmitalStats> getAllSubmittalsStatsForExecutives(UserRoleVo userRoleVo);
	
	public List<SubmitalStats> getWeekelyStats(Date fromDate, Date toDate, UserRoleVo userRoleVo);
	
	public List<SubmitalStats> getRecruiterWeekelyStats(Date fromDate, Date toDate,String dmName, UserRoleVo userRoleVo);
	
	public List<SubmitalStats> getRecruiterLocationStats(Date fromDate, Date toDate,String location, UserRoleVo userRoleVo);


	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByDm(
			Date fromDate, Date toDate);


	public List<RecRnRReportDto> getRecRnrReportDetails(Date fromDate, Date maxToDate,
			String recName);


	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByDmJobOrder(
			User user1, Date fromDate, Date toDate);


	public List<Submittal> getSubmittalDetailsbyStatusSapeareReport(
			SubmittalStatus subStatus, Date startDate, Date endDate,
			String submittalDms, String loginUserName, String portalName);


	public List<CandidateDto> getOutofProjCandidates(CandidateSearchDto statusDto);

	public Object getdmsDetailsReport(Date from, Date to, String dmName,String status);
	


	public Map<String, Object> getJobOrdersCustomReport(Date convertStringToDate, Date convertStringToDate2,
			String string);

	public Map<String, Object> getDMClientWiseSubmittals(Date srtDate, Date endDate, String dmName, String clients);

	public Map<String, Object> getcandidateSourceDetails(ReportwiseDto reportwiseDto);

	public Map<String, Object> getClientReportData(Date convertStringToDate, Date endDate);

	public Map<String, Object> getClientDetialsReportData(Date convertStringToDate, Date endDate, String string);

	public List<SubmittalDto> getTurnAroundTimeReport(Date convertStringToDate, Date endDate, String dmName);

	public Map<String, Object> getResumeAuditLogData(Date srtDate, Date endDate, String candidateId, String viewedBy);

	public Object getLocationReportData(Date convertStringToDate, Date endDate, String location);
	

	
	
}

package com.uralian.cgiats.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.dto.ReportwiseDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.model.IndiaSubmittal;
import com.uralian.cgiats.model.JobOrderSearchDto;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalEvent;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;

public interface IndiaSubmittalService {
	
	
	public List<IndiaSubmittal> findSubmittals(JobOrderSearchDto criteria);
	
	
	
	/*public List<Submittal> getListStartedCandidates();

	
	 public List<Submittal> getAllStarterdCandidates(Date currentDate);
	 	 
	 
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByBdm(User bdmName,Date dateStart, Date dateEnd);
	
	
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByRecruiter(User user,Date dateStart, Date dateEnd);
	
	 
	public List<Submittal> getSubmittalDetailsbyStatus(SubmittalStatus status,Date dateStart,Date dateEnd,String createdUser);
	
	
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByRecruiterDM(User user, Date dateStart, Date dateEnd);
	public List<Submittal> getAllOutofProjCandidates(Date fromDate,Date toDate);
	

	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByDm(
			User bdmName, Date dateStart, Date dateEnd);
	
	public Integer editSubmittalEvent(SubmittalEvent sEvent);


	public List<Submittal> getDeletedSubmittals();
	
	
	public Map<SubmittalStatus, Integer> getAllSubmittalStatusCounts(Date dateStart, Date dateEnd);*/

	public Boolean checkStartedStatusBy_CandidateId_SubmittalId(Integer candidateId,Integer submittalId);
	
	public List<IndiaSubmittal> getCandidateJobrderStatus(Integer candidateId);

	public List<?> getAllIndiaSubmittalYears();

	public List<IndiaSubmittal> getDeletedSubmittals();

	public Object getDMWiseRecPerformanceTotalReport(ReportwiseDto reportwiseDto);



	public Map<String, Object> getIndiaDMAverageNoOfDaysForStatus(ReportwiseDto reportwiseDto);



	public Object indiadmfindHitRatio(ReportwiseDto reportwiseDto);



	public Object getIndiadmsDetailsReport(Date convertStringToDate, Date endDate, String string, String status);



	public Map<String, Object> getIndiaClientReportData(Date convertStringToDate, Date endDate);



	public Map<String, Object> getIndiaClientDetialsReportData(Date convertStringToDate, Date endDate, String string);



	public Map<String, SubmittalDto> getInActiveStartedOfRecruiter(Date from, Date to, String dmName, String status,ReportwiseDto reportwiseDto,String location);



	public Map<SubmittalStatus, Integer> getAllIndiaSubmittalStatusCounts(Date yesterdayFrom, Date yesterdayTo);



	public Map<String, Integer> getIndiaInActiveStartedCounts(Date yesterdayFrom, Date yesterdayTo);

}

package com.uralian.cgiats.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;

import com.uralian.cgiats.dto.CandidateStatusesDto;
import com.uralian.cgiats.dto.CandidateVo;
import com.uralian.cgiats.dto.ResumeDto;
import com.uralian.cgiats.dto.SearchCriteria;
import com.uralian.cgiats.model.CadidateSearchAudit;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.CandidateStatuses;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.util.AllPortalResumes;

/**
 * This is the Candidate DAO interface.
 * 
 * @author Christian Rebollar
 */
public interface CandidateDao extends GenericDao<Candidate, Integer>
{
	/**
	 * @return
	 */
	public List<String> findAllTitles();

	/**
	 * @param monitor
	 * @param async
	 * @return
	 */
	public Future<?> reindexCandidates(MassIndexerProgressMonitor monitor,
	    boolean async);

	/**
	 * @param searchString
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	public List<Candidate> findByLuceneQuery(String searchString,CandidateSearchDto criteria);

	/**
	 * @param searchString
	 * @return
	 */
	public int findCountByLuceneQuery(String searchString);

	/*
	 * STATS
	 */

	/**
	 * @param dateStart
	 * @param dateEnd
	 * @param user 
	 * @return
	 */
	public Map<String, Integer> getCandidatesByUser(Date dateStart, Date dateEnd,User user);

	 
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @param portalName
	 * @return
	 */
	public Map<String, Object> getPortalIdsByUser(Date dateStart, Date dateEnd,String portalName);
	
	 /**
	  * 
	  * @param dateStart
	  * @param dateEnd
	  * @return
	  */
	
	public Map<String, Integer> getCandidatesOnUpdatedDate(Date dateStart, Date dateEnd);
	
	 /**
	  * 
	  * @param dateStart
	  * @param dateEnd
	  * @return
	  */
	
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String,Map<String, Integer>> getdiceUsageCount(Date dateStart, Date dateEnd);
	
	/**
	 * @param portalSelect
	 * @param dateStart
	 * @param dateEnd
	 * @param status
	 * @return
	 */
	public Map<String, Map<String, Integer>> getportalResumes(String portalSelect,String dateStart, String dateEnd,String status);
	
	/**
	 * @param portalSelect
	 * @param month
	 * @param year
	 * @return
	 */
	public List<Candidate> getportalCandidates(String portalSelect,String month, String year,String status);
	
	/**
	 * @param portalSelect
	 * @param month
	 * @param year
	 * @return
	 */
	public List<AllPortalResumes> getAllPortalCandidates(String portalSelect,String month, String year,String status);
	
	/**
	 * Method for fetching resumes came from portals
	 * @param dateStart
	 * @param dateEnd
	 * @param portalName
	 * @return
	 */
	public List<?> getPortalCandidates(CandidateSearchDto viewDetails);
	
	public Map<String, Integer> getAllCandidatesCounts(Date dateStart, Date dateEnd);

    public List<Integer> getAllResumesCounts();
    
    
    public Map<String, Integer> getSearchCandidatesByUser(Date dateStart, Date dateEnd, User user);

	/**
	 * @param candidateSearchVovoid
	 */
	public void saveSearchCandidateAuditDetails(
			CadidateSearchAudit candidateSearchVo);

	/**
	 * @param fromDate
	 * @param toDate
	 * @param userName 
	 * @param queryName 
	 * @returnList<CadidateSearchAudit>
	 */
	public List<CadidateSearchAudit> getSearchCandidateAudit(Date fromDate,
			Date toDate, String userName, String queryName);

	public List<Candidate> findCandidatesBasedOnStatus(StringBuffer sqlQuery, int first, int pageSize);

	public void updateCandidateStatus(CandidateStatuses status);

	public CandidateStatuses getCandidateStatus(Candidate candidate);

	public void updateStatus(CandidateStatuses stauses);

	/**
	 * @param searchCriteria
	 * @return
	 */
	public List getCandidatesList(SearchCriteria searchCriteria);

	/**
	 * @param canId
	 * @return
	 */
	public List<?> getSubmittalsInfoByCandidateId(Integer canId);

	/**
	 * @param string
	 * @param params
	 */
	public int deleteSavedQuery(String string, Map<String, Object> params);

	/**
	 * @param query 
	 * @param params
	 * @return
	 */
	public CadidateSearchAudit getSavedQuery(String query, Map<String, Object> params);

	/**
	 * @param candidateId
	 * @return
	 */
	public ResumeDto getResumeByCandidateId(String candidateId);

	/**
	 * @param searchString
	 * @param criteria
	 * @return
	 */
	public int getCandidatesCount(String searchString, CandidateSearchDto criteria);

	public CandidateVo getAllUserDetails();
	
	public Candidate getCandidateByEmail(String emailId);

	public List<CandidateStatusesDto> getCandidateStatusListByCandidateId(Integer candidateId);
	
	  public Long getAllResumesCounts(Date fromDate,Date toDate);

	public Map<String, Object> getJobBoardStats(Date startDate, Date endDate);

	public Map<String, Object> getResumeStats(Date dateStart, Date dateEnd, User user);


}
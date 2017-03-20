package com.uralian.cgiats.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;

import com.uralian.cgiats.dto.IndiaCandidateDto;
import com.uralian.cgiats.dto.ResumeDto;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.CandidateStatuses;
import com.uralian.cgiats.model.IndiaCandidate;
import com.uralian.cgiats.model.IndiaCandidateStatuses;

public interface IndiaCandidateDao extends IndiaGenericDao<IndiaCandidate, Integer>{
	
	/**
	 * @return
	 */
	public List<String> findAllTitles();

	public IndiaCandidateDto getAllUserDetails();
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
	public List<IndiaCandidate> findByLuceneQuery(String searchString,CandidateSearchDto criteria);

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
	 * @return
	 */
	public Map<String, Integer> getCandidatesByUser(Date dateStart, Date dateEnd);

	 
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @param portalName
	 * @return
	 */
	public Map<String, Integer> getPortalIdsByUser(Date dateStart, Date dateEnd,String portalName);
	
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
	  *//*
	
	*//**
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
	 *//*
	public Map<String, Map<String, Integer>> getportalResumes(String portalSelect,String dateStart, String dateEnd,String status);
	
	*//**
	 * @param portalSelect
	 * @param month
	 * @param year
	 * @return
	 *//*
	public List<Candidate> getportalCandidates(String portalSelect,String month, String year);
	
	*//**
	 * @param portalSelect
	 * @param month
	 * @param year
	 * @return
	 *//*
	public List<AllPortalResumes> getAllPortalCandidates(String portalSelect,String month, String year,String status);
	
	*//**
	 * Method for fetching resumes came from portals
	 * @param dateStart
	 * @param dateEnd
	 * @param portalName
	 * @return
	 *//*
	public List<Candidate> getPortalCandidates(Date dateStart, Date dateEnd,String portalName);
	
	public Map<String, Integer> getAllCandidatesCounts(Date dateStart, Date dateEnd);*/

    public List<Integer> getAllResumesCounts();

	public List<?> getIndiaSubmittals(Integer candidateId);

	public ResumeDto getResumeById(String candidateId);
	
	public void updateCandidateStatus(IndiaCandidateStatuses status);
	
	public IndiaCandidateStatuses getIndiaCandidateStatus(IndiaCandidate candidate);
	
	public void updateStatus(IndiaCandidateStatuses stauses);
	
	


}

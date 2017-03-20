package com.uralian.cgiats.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;

import com.uralian.cgiats.dto.CandidateDto;
import com.uralian.cgiats.dto.CandidateStatusesDto;
import com.uralian.cgiats.dto.CandidateVo;
import com.uralian.cgiats.dto.ResumeDto;
import com.uralian.cgiats.dto.SearchCriteria;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.CadidateSearchAudit;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.CandidateStatuses;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.Resume;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.util.AllPortalResumes;

/**
 * This interfaces exposes Candidate operations.
 * 
 * @author Christian Rebollar
 */
public interface CandidateService {
	/**
	 * Retrieves the candidate by id.
	 * 
	 * @param candidateId
	 * @param fetchDocument
	 * @param fetchProperties
	 * @return
	 */
	public Candidate getCandidate(Integer candidateId, boolean fetchDocument, boolean fetchProperties);
	
	public CandidateDto viewCandidate(Integer candidateId);

	/**
	 * Retrieves the candidate by id.
	 * 
	 * @param candidateId
	 * @param fetchDocument
	 * @param fetchProperties
	 * @return
	 */
	public Candidate getCandidateFromEmail(String emailId, boolean fetchDocument, boolean fetchProperties);

	/**
	 * @param candidate
	 * @throws ServiceException
	 */
	public void saveCandidate(Candidate candidate) throws ServiceException;

	/**
	 * @param candidates
	 * @throws ServiceException
	 */
	public void saveCandidates(Collection<Candidate> candidates) throws ServiceException;

	/**
	 * @param candidate
	 * @return
	 * @throws ServiceException
	 */
	public Candidate updateCandidate(Candidate candidate) throws ServiceException;

	/**
	 * @param criteria
	 * @return
	 */
	public List<?> findCandidates(CandidateSearchDto criteria);

	/**
	 * 
	 * @param criteria
	 * @return
	 */
	public int findCandidatesCount(CandidateSearchDto criteria);

	/**
	 * @param candidateId
	 * @throws ServiceException
	 */
	public void deleteCandidate(int candidateId) throws ServiceException;

	/**
	 * @return
	 */
	public List<String> listExistingTitles();

	/**
	 * @param dateStart
	 * @param dateEnd
	 * @param user
	 * @return
	 */
	public Map<String, Integer> getCandidatesByUser(Date dateStart, Date dateEnd, User user);

	/**
	 * @param monitor
	 * @param async
	 * @return
	 */
	public Future<?> reindexCandidates(MassIndexerProgressMonitor monitor, boolean async);

	/**
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @return
	 */

	public List<Candidate> getCandidateDetails(String email);

	/**
	 * @param id
	 * @return
	 */
	public List<Candidate> getExistCandidateDetails(int id);

	/**
	 * @return
	 */
	public List<Candidate> getDeletedCandidates();

	/**
	 * @return
	 */
	public List<Candidate> getBlackCandidates();

	/**
	 * @return
	 */
	public List<Candidate> getHotCandidates();

	public Map<String, Object> getPortalIdsByUser(Date dateStart, Date dateEnd, String portalName);

	/**
	 * @param toDate
	 * @param fromDate
	 * @return
	 */
	public List<CandidateDto> getBlankCandidates(CandidateSearchDto criteria);

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

	public Map<String, Map<String, Integer>> getdiceUsageCount(Date dateStart, Date dateEnd);

	/**
	 * @return
	 */
	public List<Candidate> getonlineCandidates();

	/**
	 * @param portalSelect
	 * @param dateStart
	 * @param dateEnd
	 * @param status
	 * @return
	 */
	public Map<String, Map<String, Integer>> getportalResumes(String portalSelect, String dateStart, String dateEnd, String status);

	/**
	 * @param portalSelect
	 * @param month
	 * @param year
	 * @return
	 */
	/*
	 * public List<Candidate> getportalCandidates(String portalSelect,String
	 * month, String year,String status);
	 */

	/**
	 * @param portalSelect
	 * @param month
	 * @param year
	 * @return
	 */
	public List<AllPortalResumes> getAllPortalCandidates(String portalSelect, String month, String year, String status);

	/**
	 * @param resumeIdLst
	 * @return
	 */
	public List<Candidate> getResumeIdByDate(List<String> resumeIdLst);

	/**
	 * @param resumeIdLst
	 * @return
	 */
	public List<Candidate> getResumeIds(List<String> resumeIdLst);

	public Candidate getCandidateFromResumeId(String resumeId, boolean fetchDocument, boolean fetchProperties);

	public List<Candidate> getCBCandidates();

	public List<?> getPortalCandidates(CandidateSearchDto viewDetails);

	public List<Candidate> getDiceResumeId(List<String> resumeIdLst);

	public Map<String, Integer> getAllCandidatesCounts(Date dateStart, Date dateEnd);

	public List<Integer> getAllResumesCounts();
	
	public Long getAllResumesCounts(Date fromDate,Date toDate);

	/* public Candidate loadCandidate(Integer candidateId); */

	/**
	 * @param candidate
	 * @throws ServiceException
	 */
	public Candidate saveCandidateFromMobile(Candidate candidate) throws ServiceException;

	public void upDateCandidateFromMobile(Candidate candidate) throws ServiceException;

	public List<Candidate> getmobileCandidates();

	public Map<String, Integer> getSearchCandidatesByUser(Date dateStart, Date dateEnd, User user);

	/**
	 * @param candidateSearchVovoid
	 */
	public void saveSearchCandidateAuditDetails(CadidateSearchAudit candidateSearchVo);

	/**
	 * @param fromDate
	 * @param toDate
	 * @param userName
	 * @param queryName
	 * @returnList<CadidateSearchAudit>
	 */
	public List<CadidateSearchAudit> getSearchCandidateAudit(Date fromDate, Date toDate, String userName, String queryName);

	public List<Candidate> getCandidates(Candidate candidate);

	//public List<Candidate> findCandidatesBasedOnStatus(Candidate candidate, int first, int pageSize);

	public void updateCandidateStatus(CandidateStatuses status);

	public CandidateStatuses getCandidateStatus(Candidate candidate);

	/**
	 * @param searchCriteria
	 * @return
	 */
	public List getCandidatesList(SearchCriteria searchCriteria);

	/**
	 * @param canId
	 * @return
	 * @throws ServiceException
	 */
	public List<SubmittalDto> getSubmittalsInfoByCandidateId(Integer canId) throws ServiceException;

	/**
	 * @param time
	 * @param toDate
	 * @param currentLoginUser
	 * @return
	 */
	public List<String> getSavedQueryNames(Date time, Date toDate, String currentLoginUser);

	/**
	 * @param userDto 
	 * @param frDate
	 * @param tDate
	 * @param status
	 * @return
	 */
	public List<?> getMobileAndOnlineResumens(CandidateSearchDto viewDetails, UserDto userDto);

	/**
	 * @param savedQueryId
	 */
	public int deleteSavedQuery(Integer savedQueryId);

	/**
	 * @param parseInt
	 * @return
	 */
	public CadidateSearchAudit getSavedQuery(int parseInt);

	/**
	 * @param candidateId
	 * @return
	 */
	public ResumeDto getResumeByCandidateId(String candidateId);

	public CandidateVo getAllUserDetails();

	public Candidate getCandidateByEmail(String emailId);

	public void saveCandidateFromOpenJobOrder(Candidate candidate, JobOrder jobOrder, Boolean isCandidateExists,String portalName) throws ServiceException;

	public List<CandidateDto> findCandidatesBasedOnStatus(CandidateSearchDto candidate, int first, int pageSize);

	public List<CandidateStatusesDto> getCandidateStatusListByCandidateId(Integer parseInt);

	public Map<String, Object> getJobBoardStats(Date startDate, Date endDate);

	public Map<String, Object> getResumeStats(Date dateStart, Date dateEnd, User user);



}
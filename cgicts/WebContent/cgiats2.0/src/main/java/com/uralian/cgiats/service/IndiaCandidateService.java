package com.uralian.cgiats.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;

import com.uralian.cgiats.dto.IndiaCandidateDto;
import com.uralian.cgiats.dto.ResumeDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.IndiaCandidate;
import com.uralian.cgiats.model.IndiaCandidateStatuses;

@WebService
public interface IndiaCandidateService {
	
	/**
	 * Retrieves the candidate by id.
	 * 
	 * @param candidateId
	 * @param fetchDocument
	 * @param fetchProperties
	 * @return
	 */
	public Map<String,Object> getCandidate(Integer candidateId, boolean fetchDocument,
			boolean fetchProperties);

	/**
	 * Retrieves the candidate by id.
	 * 
	 * @param candidateId
	 * @param fetchDocument
	 * @param fetchProperties
	 * @return
	 */
	public IndiaCandidate getCandidateFromEmail(String emailId, boolean fetchDocument,
			boolean fetchProperties);

	/**
	 * @param candidate
	 * @throws ServiceException
	 */
	
	@WebMethod(operationName="saveCandidate")
	public void saveCandidate(IndiaCandidate candidate) throws ServiceException;
	


	/**
	 * @param candidates
	 * @throws ServiceException
	 */
	public void saveCandidates(Collection<IndiaCandidate> candidates)
			throws ServiceException;

	/**
	 * @param candidate
	 * @throws ServiceException
	 */
	public void updateCandidate(IndiaCandidate candidate) throws ServiceException;

	/**
	 * @param criteria
	 * @return
	 */
	public List<IndiaCandidate> findCandidates(CandidateSearchDto criteria);

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
	 * @return
	 */
	public Map<String, Integer> getCandidatesByUser(Date dateStart, Date dateEnd);

	/**
	 * @param monitor
	 * @param async
	 * @return
	 */
	public Future<?> reindexCandidates(MassIndexerProgressMonitor monitor,	boolean async);

	/**
	 * @param firstName
	 * @param lastName
	 * @param email
	 * @return
	 */

	public List<IndiaCandidate> getCandidateDetails(String email);
	
	public List<?> isUserExistsByEmailOrPhone(String email,String phone);
	/**
	 * @param id
	 * @return
	 */
	public List<IndiaCandidate> getExistCandidateDetails(int id);

	/**
	 * @return
	 */
	public List<IndiaCandidate> getDeletedCandidates();
	/**
	 * @return
	 */
	public List<IndiaCandidate> getBlackCandidates();
	/**
	 * @return
	 */
	public List<IndiaCandidate> getHotCandidates();

	public Map<String, Integer> getPortalIdsByUser(Date dateStart, Date dateEnd,String portalName);
	/**
	 * @return
	 */
	public List<IndiaCandidate> getBlankCandidates();
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

	public Map<String,Map<String, Integer>> getdiceUsageCount(Date dateStart, Date dateEnd);

	/**
	 * @return
	 */
	public List<IndiaCandidate> getonlineCandidates();	

	/**
	 * @param portalSelect
	 * @param dateStart
	 * @param dateEnd
	 * @param status
	 * @return
	 */
	/*public Map<String, Map<String, Integer>> getportalResumes(String portalSelect,String dateStart, String dateEnd,String status);*/

	/**
	 * @param portalSelect
	 * @param month
	 * @param year
	 * @return
	 */
	/*public List<IndiaCandidate> getportalCandidates(String portalSelect,String month, String year);*/

	/**
	 * @param portalSelect
	 * @param month
	 * @param year
	 * @return
	 */
	/*public List<AllPortalResumes> getAllPortalCandidates(String portalSelect,String month, String year,String status);*/

	
	/**
	 * @param resumeIdLst
	 * @return
	 */
	public List<IndiaCandidate> getResumeIdByDate(List<String> resumeIdLst);
	
	/**
	 * @param resumeIdLst
	 * @return
	 */
	public List<IndiaCandidate> getResumeIds(List<String> resumeIdLst);
	
	
	public IndiaCandidate getCandidateFromResumeId(String resumeId, boolean fetchDocument,
			boolean fetchProperties);
	
	/*public List<IndiaCandidate> getCBCandidates();
	public List<IndiaCandidate> getPortalCandidates(Date dateStart, Date dateEnd,String portalName );
	public List<IndiaCandidate> getDiceResumeId(List<String> resumeIdLst);
	public Map<String, Integer> getAllCandidatesCounts(Date dateStart, Date dateEnd);*/
	public List<Integer> getAllResumesCounts();

	public List<SubmittalDto> getIndiaSubmittals(Integer candidateId);

	public ResumeDto getResumeByCandidateId(String candidateId);

	public IndiaCandidate getCandidateBymobile(String mobileNumber);
	
	public IndiaCandidateDto getAllUserDetails();

	public IndiaCandidateStatuses getIndiaCandidateStatus(IndiaCandidate candidate);


	public void updateIndiaCandidateStatus(IndiaCandidateStatuses status);

}

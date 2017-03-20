package com.uralian.cgiats.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.jws.WebService;

import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.IndiaCandidateDao;
import com.uralian.cgiats.dto.IndiaCandidateDto;
import com.uralian.cgiats.dto.ResumeDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.CandidateStatuses;
import com.uralian.cgiats.model.IndiaCandidate;
import com.uralian.cgiats.model.IndiaCandidateStatuses;
import com.uralian.cgiats.model.OrderByColumn;
import com.uralian.cgiats.model.OrderByType;
import com.uralian.cgiats.service.IndiaCandidateService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.Utils;

@WebService(endpointInterface = "com.uralian.cgiats.service.IndiaCandidateService")
@Service
@Transactional(rollbackFor = ServiceException.class)
public class IndiaCandidateServiceImpl implements IndiaCandidateService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private IndiaCandidateDao candidateDao;

	@Override
	@Transactional(readOnly = true)
	public Map<String,Object> getCandidate(Integer candidateId, boolean fetchDocument, boolean fetchProperties) {
		try {
			if (log.isDebugEnabled()) {
				log.debug("IndiaCandidate id [" + candidateId + "] - fetchDocument[" + fetchDocument
						+ "] fetchProperties[" + fetchProperties + "] ");
			}
			
			Map<String,Object> map=new HashMap<String,Object>();

			StringBuffer hql = new StringBuffer();
			hql.append("select c.id from IndiaResume c");
			hql.append(" where c.id = :candidateId");

			List<?> isResumeExists = candidateDao.findByQuery(hql.toString(), "candidateId", candidateId);
			if (isResumeExists != null && isResumeExists.size() > 0) {
				hql = new StringBuffer();
				hql.append("select distinct c from IndiaCandidate c");
				if (fetchDocument)
					hql.append(" join fetch c.resume");
				if (fetchProperties)
					hql.append(" left join fetch c.properties");
				hql.append(" where c.id = :candidateId and c.deleteFlag=0");

				List<IndiaCandidate> result = candidateDao.findByQuery(hql.toString(), "candidateId", candidateId);
				map.put(Constants.ISRESUMEEXISTS, true);
				map.put(Constants.DATA, !Utils.isEmpty(result) ? result.get(0) : null);
				return map;
			} else {
				hql = new StringBuffer();
				hql.append("select distinct c from IndiaCandidate c");
				hql.append(" where c.id = :candidateId and c.deleteFlag=0");

				List<IndiaCandidate> result = candidateDao.findByQuery(hql.toString(), "candidateId", candidateId);
				map.put(Constants.ISRESUMEEXISTS, false);
				map.put(Constants.DATA, !Utils.isEmpty(result) ? result.get(0) : null);
				return map;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public IndiaCandidate getCandidateFromEmail(String emailId, boolean fetchDocument, boolean fetchProperties) {
		try {
			if (log.isDebugEnabled()) {
				log.debug("IndiaCandidate id [" + emailId + "] - fetchDocument[" + fetchDocument + "] fetchProperties[" + fetchProperties + "] ");
			}

			StringBuffer hql = new StringBuffer();
			hql.append("select distinct c from IndiaCandidate c");
			if (fetchDocument)
				hql.append(" join fetch c.resume");
			if (fetchProperties)
				hql.append(" left join fetch c.properties");
			hql.append(" where c.email = :emailId and c.deleteFlag=0 ");

			List<IndiaCandidate> result = candidateDao.findByQuery(hql.toString(), "emailId", emailId);
			hql.append(" order by COALESCE(c.updatedOn,c.createdOn) desc ");
			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public IndiaCandidate getCandidateFromResumeId(String resumeId, boolean fetchDocument, boolean fetchProperties) {
		try {
			if (log.isDebugEnabled()) {
				log.debug("IndiaCandidate id [" + resumeId + "] - fetchDocument[" + fetchDocument + "] fetchProperties[" + fetchProperties + "] ");
			}

			StringBuffer hql = new StringBuffer();
			hql.append("select distinct c from IndiaCandidate c");
			if (fetchDocument)
				hql.append(" join fetch c.resume");
			if (fetchProperties)
				hql.append(" left join fetch c.properties");
			hql.append(" where c.portalResumeId = :resumeId and c.deleteFlag=0 ");

			List<IndiaCandidate> result = candidateDao.findByQuery(hql.toString(), "resumeId", resumeId);
			hql.append(" order by COALESCE(c.updatedOn,c.createdOn) desc ");
			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void saveCandidate(IndiaCandidate candidate) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + candidate);
			System.out.println("candidate save>>" + candidate);
			candidateDao.save(candidate);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist candidate", exception);
		}
	}

	@Override
	public void saveCandidates(Collection<IndiaCandidate> candidates) throws ServiceException {
		try {
			for (IndiaCandidate candidate : candidates)
				saveCandidate(candidate);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void updateCandidate(IndiaCandidate candidate) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Updating " + candidate);

			candidateDao.update(candidate);
			
			IndiaCandidateStatuses stauses = candidateDao.getIndiaCandidateStatus(candidate);

			if (candidate.isFlag()) {
				if (stauses != null) {
					if (stauses.getStatus().toString().equals(candidate.getStatus().toString())) {

						log.info("Status Exist");
						IndiaCandidateStatuses status = new IndiaCandidateStatuses();
						status.setId(stauses.getId());
						status.setCreatedDate(new Date());
						status.setReason(candidate.getReason());
						status.setCreatedBy(candidate.getUpdatedBy());
						status.setStatus(candidate.getStatus());
						updateStatus(status);
					} else {
						log.info("Status not  Exist");
						IndiaCandidateStatuses status = new IndiaCandidateStatuses();
						status.setIndiacandidate(candidate);
						status.setCreatedDate(new Date());
						status.setCreatedBy(candidate.getUpdatedBy());
						status.setStatus(candidate.getStatus());
						status.setReason(candidate.getReason());
						updateIndiaCandidateStatus(status);
					}
				} else {
					IndiaCandidateStatuses status = new IndiaCandidateStatuses();
					status.setIndiacandidate(candidate);
					status.setCreatedDate(new Date());
					status.setCreatedBy(candidate.getUpdatedBy());
					status.setStatus(candidate.getStatus());
					status.setReason(candidate.getReason());
					updateIndiaCandidateStatus(status);
				}
			}
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to update candidate", exception);
		}
	}
	
	private void updateStatus(IndiaCandidateStatuses stauses) {
		candidateDao.updateStatus(stauses);

	}
	public void updateIndiaCandidateStatus(IndiaCandidateStatuses status) {
		try {
			candidateDao.updateCandidateStatus(status);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

	}
	@Override
	@Transactional(readOnly = true)
	public List<IndiaCandidate> getCandidateDetails(String email) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from IndiaCandidate");
			hql.append(" where email =:email and deleteFlag=0 ");
			params.put("email", email);
			return candidateDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<IndiaCandidate> getExistCandidateDetails(int id) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from IndiaCandidate");
			hql.append(" where deleteFlag=0 and id =:id");
			params.put("id", id);
			return candidateDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<IndiaCandidate> getResumeIdByDate(List<String> resumeIdLst) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from IndiaCandidate");
			hql.append(" where deleteFlag=0 and portalResumeId||portalResumeLastUpd NOT IN(:resumeIdLst) and createdUser IN ('Careerbuilder','Monster')");
			params.put("resumeIdLst", resumeIdLst);
			return candidateDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<IndiaCandidate> getDiceResumeId(List<String> resumeIdLst) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from IndiaCandidate");
			hql.append(" where deleteFlag=0 and portalResumeId NOT IN(:resumeIdLst) and createdUser='Dice'");
			params.put("resumeIdLst", resumeIdLst);
			return candidateDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<IndiaCandidate> getResumeIds(List<String> resumeIdLst) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from IndiaCandidate");
			hql.append(" where deleteFlag=0 and portalResumeId IN (:resumeIdLst)");
			params.put("resumeIdLst", resumeIdLst);
			return candidateDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<IndiaCandidate> findCandidates(CandidateSearchDto criteria) {
		try {
			// null criteria not allowed
			if (criteria == null)
				throw new IllegalArgumentException("Search criteria not set");

			if (log.isDebugEnabled())
				log.debug("Using following criteria " + criteria);

			String searchString = buildLuceneQuery(criteria);
			if (!Utils.isEmpty(searchString)) {

				return candidateDao.findByLuceneQuery(searchString, criteria);
			} else {
				StringBuffer hql = new StringBuffer("select c from IndiaCandidate c where c.deleteFlag=0 ");
				
				OrderByColumn orderBy = criteria.getOrderByColumn();
				if (!Utils.isBlank(criteria.getFieldName()) && !Utils.isBlank(criteria.getSortName())){
					hql.append(orderBy.getValue() + " ");
					OrderByType orderType = criteria.getOrderByType() != null ? criteria.getOrderByType() : OrderByType.DESC;
					hql.append(orderType + " NULLS LAST");
				}else{
					hql.append(" order by COALESCE(c.updatedOn,c.createdOn) DESC");
				}
				if (log.isDebugEnabled())
					log.debug("This is HQL Query " + hql.toString());

				return candidateDao.findByQuery(hql.toString(), criteria.getStartPosition(), criteria.getMaxResults());
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public int findCandidatesCount(CandidateSearchDto criteria) {
		try {
			System.out.println("find India IndiaCandidate count");
			// null criteria not allowed
			if (criteria == null)
				throw new IllegalArgumentException("Search criteria not set");

			if (log.isDebugEnabled())
				log.debug("Using following criteria " + criteria);

			String searchString = buildLuceneQuery(criteria);
			if (!Utils.isEmpty(searchString))
				return candidateDao.findCountByLuceneQuery(searchString);
			else {
				List<?> result = candidateDao.runQuery("select count(c) from IndiaCandidate c where c.deleteFlag=0 ");
				return ((Number) result.get(0)).intValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public void deleteCandidate(int candidateId) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Deleting candidate with id [" + candidateId + "]");

			// Fetching candidate to delete it.
			IndiaCandidate candidate = candidateDao.findById(candidateId);
			if (candidate != null) {
				candidateDao.delete(candidate);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Error while deleting candidate", e);
		}
	}

	@Override
	public List<String> listExistingTitles() {
		try {
			return candidateDao.findAllTitles();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Integer> getCandidatesByUser(Date dateStart, Date dateEnd) {
		try {
			return candidateDao.getCandidatesByUser(dateStart, dateEnd);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Integer> getPortalIdsByUser(Date dateStart, Date dateEnd, String portalName) {
		try {
			return candidateDao.getPortalIdsByUser(dateStart, dateEnd, portalName);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Integer> getCandidatesOnUpdatedDate(Date dateStart, Date dateEnd) {
		try {
			return candidateDao.getCandidatesOnUpdatedDate(dateStart, dateEnd);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Map<String, Integer>> getdiceUsageCount(Date dateStart, Date dateEnd) {
		try {
			return candidateDao.getdiceUsageCount(dateStart, dateEnd);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Future<?> reindexCandidates(MassIndexerProgressMonitor monitor, boolean async) {
		try {
			return candidateDao.reindexCandidates(monitor, async);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<IndiaCandidate> getDeletedCandidates() {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from IndiaCandidate");
			hql.append(" where deleteFlag=1");
			return (List<IndiaCandidate>) candidateDao.runQuery(hql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private String buildLuceneQuery(CandidateSearchDto criteria) {
		try {
			List<String> clauses = new ArrayList<String>();

			if (!Utils.isEmpty(criteria.getFirstName())) {
				String clause = "firstName:" + criteria.getFirstName().trim() + "*";
				clauses.add(clause);
			}
			if (!Utils.isEmpty(criteria.getLastName())) {
				String clause = "lastName:" + criteria.getLastName().trim() + "*";
				clauses.add(clause);
			}
			if (!Utils.isEmpty(criteria.getEmail())) {
				String clause = "email:" + criteria.getEmail().trim() + "*";
				clauses.add(clause);
			}
			if (!Utils.isEmpty(criteria.getVisaStats())) {
				String clause = "visaType:(" + criteria.getVisaStats() + ")";
				clauses.add(clause);
			}
			if (!Utils.isEmpty(criteria.getTitle())) {
				String clause = "title:" + criteria.getTitle().trim();
				clauses.add(clause);
			}
			if (!Utils.isEmpty(criteria.getCity())) {
				String clause = "city:" + criteria.getCity().trim() + "*";
				clauses.add(clause);
			}
			if (!Utils.isEmpty(criteria.getStates())) {
				StringBuilder clause = new StringBuilder("state:");
				clause.append(criteria.getStates().get(0));
				for (int i = 1; i < criteria.getStates().size(); i++)
					clause.append(" OR state:").append(criteria.getStates().get(i));
				clauses.add("(" + clause + ")");
			}
			if (criteria.getCreatedBy() != null) {
				String clause = "createdUser:" + criteria.getCreatedBy().trim();
				clauses.add(clause);
			}
			if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
				String clause = "createdOn:[" + criteria.getStartDate() + " TO " + criteria.getEndDate() + "]";
				clauses.add(clause);
			}
			if (!Utils.isEmpty(criteria.getResumeTextQuery()))
				clauses.add(criteria.getResumeTextQuery());

			if (Utils.isEmpty(clauses))
				return null;

			StringBuffer searchString = new StringBuffer();
			searchString.append(clauses.get(0));
			for (int i = 1; i < clauses.size(); i++)
				searchString.append(" AND ").append(clauses.get(i));

			if (searchString != null) {
				searchString.append(" AND deleteFlag:0 ");
			}

			return searchString.toString();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<IndiaCandidate> getBlackCandidates() {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" from IndiaCandidate c ");
			hql.append(" where c.block=true and c.deleteFlag=0   ");
			return (List<IndiaCandidate>) candidateDao.runQuery(hql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<IndiaCandidate> getCBCandidates() {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" from IndiaCandidate c ");
			hql.append(" where c.createdUser='Careerbuilder' and c.deleteFlag=0 ");
			return (List<IndiaCandidate>) candidateDao.runQuery(hql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<IndiaCandidate> getHotCandidates() {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" from IndiaCandidate c ");
			hql.append(" where c.hot=true and c.deleteFlag=0 ");
			return (List<IndiaCandidate>) candidateDao.runQuery(hql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<IndiaCandidate> getBlankCandidates() {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" from IndiaCandidate c ");
			hql.append(
					" where  c.deleteFlag=0 and (c.firstName is null or  c.firstName IN('') or c.lastName is null or c.lastName IN('') or c.title is null or c.title IN('') or c.address.city is null or c.address.city IN('') or c.address.state is null or c.address.state IN('') or c.email is null or c.email IN('') ) ");
			hql.append(" order by createdOn DESC ");
			return (List<IndiaCandidate>) candidateDao.runQuery(hql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<IndiaCandidate> getonlineCandidates() {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" from IndiaCandidate c ");
			hql.append(" where c.createdUser='CGI' and c.deleteFlag=0 ");
			return (List<IndiaCandidate>) candidateDao.runQuery(hql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * public Map<String, Map<String, Integer>> getportalResumes(String
	 * portalSelect,String dateStart, String dateEnd,String status){ Map<String,
	 * Map<String, Integer>> map=
	 * candidateDao.getportalResumes(portalSelect,dateStart, dateEnd,status);
	 * return map; }
	 */
	/*
	 * public List<IndiaCandidate> getportalCandidates(String
	 * portalSelect,String month, String year){ List<IndiaCandidate> map=
	 * candidateDao.getportalCandidates(portalSelect,month,year); return map; }
	 */

	/*
	 * public List<AllPortalResumes> getAllPortalCandidates(String
	 * portalSelect,String month, String year,String status){
	 * List<AllPortalResumes> map=
	 * candidateDao.getAllPortalCandidates(portalSelect,month,year,status);
	 * return map; }
	 */
	/*
	 * public List<IndiaCandidate> getPortalCandidates(Date dateStart, Date
	 * dateEnd,String portalName ){ List<IndiaCandidate> map=
	 * candidateDao.getPortalCandidates(dateStart,dateEnd,portalName); return
	 * map; }
	 */
	/*
	 * public Map<String, Integer> getAllCandidatesCounts(Date dateStart, Date
	 * dateEnd){ Map<String, Integer> map=
	 * candidateDao.getAllCandidatesCounts(dateStart,dateEnd); return map; }
	 */
	public List<Integer> getAllResumesCounts() {
		try {
			List<Integer> map = candidateDao.getAllResumesCounts();
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<SubmittalDto> getIndiaSubmittals(Integer candidateId) {
		try {

			final List<?> submittalsInfo =  candidateDao.getIndiaSubmittals(candidateId);
			final List<SubmittalDto> submitalDetails = new ArrayList<SubmittalDto>();
			if (!Utils.isEmpty(submittalsInfo)) {
				final Iterator<?> it = submittalsInfo.iterator();
				while (it.hasNext()) {
					SubmittalDto submitalDto = new SubmittalDto();
					final Object data[] = (Object[]) it.next();

					final String status = (String) data[0];
					final String submittalId = String.valueOf(data[1]);
					final String jobOrderId = String.valueOf(data[2]);
					final Date createdOn = (Date) data[3];
					final String recName = String.valueOf(data[4]);
					if (status != null) {
						submitalDto.setStatus(status);
					} else
						submitalDto.setStatus("");

					if (!Utils.isEmpty(submittalId))
						submitalDto.setSubmittalId(submittalId);
					else
						submitalDto.setSubmittalId("");
					if (!Utils.isEmpty(jobOrderId))
						submitalDto.setJobOrderId(jobOrderId);
					else
						submitalDto.setJobOrderId("");

					if (createdOn != null) {
						submitalDto.setCreatedOn(Utils.convertDateToString_HH_MM_A(createdOn));
					} else
						submitalDto.setCreatedOn("");

					if (!Utils.isEmpty(recName))
						submitalDto.setCreatedBy(recName);
					else
						submitalDto.setCreatedBy("");
					submitalDetails.add(submitalDto);
				}
				return submitalDetails;
			} else {

				return submitalDetails;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public ResumeDto getResumeByCandidateId(String candidateId) {
		try {
			return candidateDao.getResumeById(candidateId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public IndiaCandidate getCandidateBymobile(String mobileNumber) {
		Map<String, Object> params = new HashMap<String, Object>();
		try{
		/*	StringBuffer hql = new StringBuffer();
			hql.append("select * from IndiaCandidate c where c.phone= :mobileNumber or c.phoneAlt= :mobileNumber");
			params.put("mobileNumber", mobileNumber);
			List<IndiaCandidate> result = candidateDao.findByQuery(hql.toString(), params);
			return !Utils.isEmpty(result) ? result.get(0) : null;*/
			
			StringBuffer hql = new StringBuffer();
			hql.append("select distinct c from IndiaCandidate c");
			hql.append(" where c.phone= :mobileNumber or c.phoneAlt= :mobileNumber and c.deleteFlag=0 ");
			params.put("mobileNumber", mobileNumber);
			List<IndiaCandidate> result = candidateDao.findByQuery(hql.toString(), params);
			hql.append(" order by COALESCE(c.updatedOn,c.createdOn) desc ");
			return !Utils.isEmpty(result) ? result.get(0) : null;
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage(), e);
			return null;
		}
	}
	
	@Override
	public List<?> isUserExistsByEmailOrPhone(String email,String phone) {
		Map<String, Object> params = new HashMap<String, Object>();
		try{
		/*	StringBuffer hql = new StringBuffer();
			hql.append("select * from IndiaCandidate c where c.phone= :mobileNumber or c.phoneAlt= :mobileNumber");
			params.put("mobileNumber", mobileNumber);
			List<IndiaCandidate> result = candidateDao.findByQuery(hql.toString(), params);
			return !Utils.isEmpty(result) ? result.get(0) : null;*/
			
			StringBuffer hql = new StringBuffer();
			hql.append("select c.id from IndiaCandidate c");
			hql.append(" where (c.phone= :mobileNumber or c.phoneAlt= :mobileNumber or c.email=:email) and c.deleteFlag=0 ");
			params.put("mobileNumber", phone);
			params.put("email", email);
			List<?> result = candidateDao.findByQuery(hql.toString(), params);
			return result;
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public IndiaCandidateDto getAllUserDetails() {
		try {
			return candidateDao.getAllUserDetails();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public IndiaCandidateStatuses getIndiaCandidateStatus(IndiaCandidate candidate) {
		try {
			IndiaCandidateStatuses stauses = candidateDao.getIndiaCandidateStatus(candidate);
			return stauses;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

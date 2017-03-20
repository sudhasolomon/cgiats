package com.uralian.cgiats.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.CandidateDao;
import com.uralian.cgiats.dao.OnlineCgiCanidateDao;
import com.uralian.cgiats.dao.UserDao;
import com.uralian.cgiats.dto.CandidateDto;
import com.uralian.cgiats.dto.CandidateStatusesDto;
import com.uralian.cgiats.dto.CandidateVo;
import com.uralian.cgiats.dto.ResumeDto;
import com.uralian.cgiats.dto.SearchCriteria;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.Address;
import com.uralian.cgiats.model.CadidateSearchAudit;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.CandidateStatus;
import com.uralian.cgiats.model.CandidateStatuses;
import com.uralian.cgiats.model.Entity_Table_Fields;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.OnlineCgiCandidates;
import com.uralian.cgiats.model.OrderByColumn;
import com.uralian.cgiats.model.OrderByType;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.CommService;
import com.uralian.cgiats.service.CommService.AttachmentInfo;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.AllPortalResumes;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.Utils;

/**
 * @author Christian Rebollar
 */

@Service
@Transactional(rollbackFor = ServiceException.class)
public class CandidateServiceImpl implements CandidateService {
	protected static final Logger log = LoggerFactory.getLogger(CandidateServiceImpl.class);

	@Autowired
	private CandidateDao candidateDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private CommService commService;
	@Autowired
	private OnlineCgiCanidateDao onlineCgiCanidateDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateService#getCandidate(int,
	 * boolean, boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public Candidate getCandidate(Integer candidateId, boolean fetchDocument, boolean fetchProperties) {
		try {
			if (log.isDebugEnabled()) {
				log.debug("Candidate id [" + candidateId + "] - fetchDocument[" + fetchDocument + "] fetchProperties[" + fetchProperties + "] ");
			}

			StringBuffer hql = new StringBuffer();
			hql.append("select distinct c from Candidate c");
			if (fetchDocument)
				hql.append(" join fetch c.resume");
			if (fetchProperties)
				hql.append(" left join fetch c.properties");
			hql.append(" where c.id = :candidateId and c.deleteFlag=0");

			List<Candidate> result = candidateDao.findByQuery(hql.toString(), "candidateId", candidateId);

			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateService#getCandidate(int,
	 * boolean, boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public Candidate getCandidateFromEmail(String emailId, boolean fetchDocument, boolean fetchProperties) {
		try {
			if (log.isDebugEnabled()) {
				log.debug("Candidate id [" + emailId + "] - fetchDocument[" + fetchDocument + "] fetchProperties[" + fetchProperties + "] ");
			}

			StringBuffer hql = new StringBuffer();
			hql.append("select distinct c from Candidate c");
			if (fetchDocument)
				hql.append(" join fetch c.resume");
			if (fetchProperties)
				hql.append(" left join fetch c.properties");
			// hql.append(" where c.email = :emailId ");for reful service
			hql.append(" where c.email = :emailId and c.deleteFlag=0 ");

			List<Candidate> result = candidateDao.findByQuery(hql.toString(), "emailId", emailId);
			hql.append(" order by COALESCE(c.updatedOn,c.createdOn) desc ");
			System.out.println(hql);
			System.out.println(result);
			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#saveCandidate(com.uralian
	 * .cgiats .model.Candidate)
	 */
	@Override
	public void saveCandidate(Candidate candidate) throws ServiceException {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#updateCandidate(com.uralian
	 * .cgiats.model.Candidate)
	 */
	@Override
	public Candidate updateCandidate(Candidate candidate) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Updating " + candidate);
			log.info("From updateCandidate");

			Candidate can = candidateDao.update(candidate);

			CandidateStatuses stauses = candidateDao.getCandidateStatus(candidate);

			if (candidate.isFlag()) {
				if (stauses != null) {
					if (stauses.getStatus().toString().equals(candidate.getStatus().toString())) {

						log.info("Status Exist");
						CandidateStatuses status = new CandidateStatuses();
						status.setId(stauses.getId());
						status.setCreatedDate(new Date());
						status.setReason(candidate.getReason());
						status.setCreatedBy(candidate.getUpdatedBy());
						status.setStatus(candidate.getStatus());
						updateStatus(status);
					} else {
						log.info("Status not  Exist");
						CandidateStatuses status = new CandidateStatuses();
						status.setCandidate(candidate);
						status.setCreatedDate(new Date());
						status.setCreatedBy(candidate.getUpdatedBy());
						status.setStatus(candidate.getStatus());
						status.setReason(candidate.getReason());
						updateCandidateStatus(status);
					}
				} else {
					CandidateStatuses status = new CandidateStatuses();
					status.setCandidate(candidate);
					status.setCreatedDate(new Date());
					status.setCreatedBy(candidate.getUpdatedBy());
					status.setStatus(candidate.getStatus());
					status.setReason(candidate.getReason());
					updateCandidateStatus(status);
				}
			}
			return can;
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to update candidate", exception);
		}
	}

	private void updateStatus(CandidateStatuses stauses) {
		candidateDao.updateStatus(stauses);

	}

	@Override
	@Transactional(readOnly = true)
	public List<Candidate> getCandidateDetails(String email) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from Candidate");
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
	public List<Candidate> getExistCandidateDetails(int id) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from Candidate");
			hql.append(" where deleteFlag=0 and id =:id");
			params.put("id", id);
			return candidateDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#findCandidates(com.uralian.
	 * cgiats.model.CandidateSearchDto)
	 */
	@Override
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public List<?> findCandidates(CandidateSearchDto criteria)

	{
		log.info("Start findCandidates");
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		String total_Records = null;
		final List<CandidateDto> candidatesDto = new ArrayList<CandidateDto>();
		List<?> candidates = null;
		int count = 0;
		try {
			if(criteria.getCandidateId()!=null || (criteria.getEmail()!=null && criteria.getEmail().length()>0)){
				StringBuffer hql = new StringBuffer(
						"select c.id,c.firstName,c.lastName, c.address.state,c.email,c.title,c.createdOn,c.updatedOn,c.status,c.visaType,c.phone,c.address.city,c.hot,c.block,c.keySkill,c.reason  from Candidate c where c.deleteFlag=0 ");
				if(criteria.getCandidateId()!=null && criteria.getCandidateId().length()>0)
					hql.append("and c.id="+criteria.getCandidateId()+" ");
				if(criteria.getEmail()!=null && criteria.getEmail().length()>0)
				hql.append(" and c.email='"+criteria.getEmail()+"' ");
				
				OrderByColumn orderBy = criteria.getOrderByColumn();
				if (orderBy != null) {
					hql.append(orderBy.getValue());
					OrderByType orderType = criteria.getOrderByType() != null ? criteria.getOrderByType() : OrderByType.ASC;
					hql.append(" "+orderType.getValue());
				}
				candidates = candidateDao.findByQuery(hql.toString(), criteria.getStartPosition(), criteria.getMaxResults());
				
			}
			else{
			log.info("fromDate" + criteria.getStartDate());
			String searchString = buildLuceneQuery(criteria);
			if (!Utils.isEmpty(searchString)) {
				candidates = candidateDao.findByLuceneQuery(searchString, criteria);
				count = candidateDao.getCandidatesCount(searchString, criteria);
				log.info("count  " + count);
			} else {
				StringBuffer hql = new StringBuffer(
						"select c.id,c.firstName,c.lastName, c.address.state,c.email,c.title,c.createdOn,c.updatedOn,c.status,c.visaType,c.phone,c.address.city,c.hot,c.block  from Candidate c where c.deleteFlag=0");
				OrderByColumn orderBy = criteria.getOrderByColumn();
				if (orderBy != null) {
					hql.append(orderBy.getValue());
					OrderByType orderType = criteria.getOrderByType() != null ? criteria.getOrderByType() : OrderByType.ASC;
					hql.append(orderType.getValue());
				}
				candidates = candidateDao.findByQuery(hql.toString(), criteria.getStartPosition(), criteria.getMaxResults());
			}
			}

			if (candidates != null && candidates.size() > 0) {
				Iterator<?> it = candidates.iterator();
				total_Records = Integer.toString(candidates.size());
				while (it.hasNext()) {
					Object candidate[] = (Object[]) it.next();
					CandidateDto dto = new CandidateDto();
					final String candidateId = String.valueOf(candidate[0]);
					final String firstName = (String) candidate[1];
					final String lastName = (String) candidate[2];
					StringBuffer fullName = new StringBuffer();
					if (!Utils.isEmpty(firstName) || !Utils.isEmpty(lastName)) {
						fullName.append(firstName + "");
						if (lastName != null)
							fullName.append(" " + lastName);

					}

					final String city = (String) candidate[11];
					final String state = (String) candidate[3];

					final StringBuffer location = new StringBuffer();
					final String email = (String) candidate[4];
					final String title = (String) candidate[5];
					 String createddate =null;
					 String updateddate =null;
					if(criteria.getCandidateId()!=null || (criteria.getEmail()!=null && criteria.getEmail().length()>0)){
						createddate = Utils.convertDateToString((Date) candidate[6]);
						updateddate = Utils.convertDateToString((Date) candidate[7]);
						dto.setTotalRecords(total_Records);
					}else{
						createddate = (String) candidate[6];
						updateddate = (String) candidate[7];
					}
					final CandidateStatus status = (CandidateStatus) candidate[8];
					final String visaType = (String) candidate[9];
					final String phoneNumber = (String) candidate[10];
					final Object hot = candidate[12];
					final Object block = candidate[13];
					final String keySkill = (String) candidate[14];
					final byte[] reason = (byte[]) candidate[15];
					// final String resumeContent = (String) candidate[14];

					if (!Utils.isEmpty(candidateId))
						if (!Utils.isEmpty(candidateId))
							dto.setId(candidateId);
						else
							dto.setId("");
					if (!Utils.isEmpty(fullName.toString()))
						dto.setFirstName(fullName.toString());
					else
						dto.setFirstName("");
					if (!Utils.isEmpty(city) && !Utils.isEmpty(state)) {
						location.append(city + " , " + state);
						dto.setCity(location.toString());
					} else if (Utils.isEmpty(city))
						dto.setCity(state);
					else if (Utils.isEmpty(state))
						dto.setCity(city);
					else
						dto.setLocation("");

					if (!Utils.isEmpty(email))
						dto.setEmail(email);
					else
						dto.setEmail("");
					if (!Utils.isEmpty(title))
						dto.setTitle(title);
					else
						dto.setTitle("");
					if (createddate != null) {
						// String createdOn =
						// Utils.convertDateToString(createddate);
						dto.setCreatedOn(createddate);
					}
					if (updateddate != null) {
						// String updatedOn = sdf.format(updateddate);
						dto.setUpdatedOn(updateddate);
					}

					if (status != null)
						dto.setStatus(status.toString());
					else
						dto.setStatus("Available");

					if (!Utils.isEmpty(visaType))
						dto.setVisaType(visaType);
					else
						dto.setVisaType("Not Available");

					if (!Utils.isEmpty(phoneNumber))
						dto.setPhoneNumber(phoneNumber);
					else
						dto.setPhoneNumber("");
					// if (!Utils.isEmpty(resumeContent))
					// dto.setResumeContent(resumeContent);
					// else
					// dto.setResumeContent("");

					if (hot != null) {
						dto.setHot((boolean) hot);
					} else
						dto.setHot(false);
					if (block != null) {
						dto.setBlock((boolean) block);
					} else
						dto.setBlock(false);

					if (keySkill != null)
						dto.setKeySkill(keySkill);
					else
						dto.setKeySkill("");

					if (reason != null) {
						String statusReason = new String(reason);
						dto.setReason(statusReason);
					} else {
						dto.setReason("");
					}
					if (count != 0)
						dto.setTotalRecords(String.valueOf(count));

					candidatesDto.add(dto);
				}
			}
			// performSortingOnDates(candidatesDto);
			return candidatesDto;

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			return null;
		}
	}

	private static void performSortingOnDates(List<CandidateDto> candidatesDto) {
		try {
			// To display the records in descending order based on updated Date
			if (candidatesDto != null && candidatesDto.size() > 0) {
				Map<String, List<CandidateDto>> dateWithMap = new LinkedHashMap<String, List<CandidateDto>>();
				for (CandidateDto dto : candidatesDto) {
					String date = dto.getUpdatedOn() != null ? dto.getUpdatedOn() : dto.getCreatedOn();
					if (dateWithMap.get(date) != null) {
						dateWithMap.get(date).add(dto);
					} else {
						List<CandidateDto> list = new ArrayList<CandidateDto>();
						list.add(dto);
						dateWithMap.put(date, list);
					}
				}
				candidatesDto.clear();
				for (String dateKey : dateWithMap.keySet()) {
					Collections.reverse(dateWithMap.get(dateKey));
					candidatesDto.addAll(dateWithMap.get(dateKey));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateService#deleteCandidate(int)
	 */
	@Override
	public void deleteCandidate(int candidateId) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Deleting candidate with id [" + candidateId + "]");

			// Fetching candidate to delete it.
			Candidate candidate = candidateDao.findById(candidateId);

			if (candidate != null) {
				candidateDao.delete(candidate);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Error while deleting candidate", e);
		}
	}

	/**
	 * @param criteria
	 * @return
	 */
	private String buildLuceneQuery(CandidateSearchDto criteria) {
		try {
			String clause = null;
			log.info("KeySkill" + criteria.getKeySkill());
			List<String> clauses = new ArrayList<String>();

			if (!Utils.isEmpty(criteria.getCandidateId())) {
				clause = "id:" + criteria.getCandidateId().trim();
				clauses.add(clause);
			}

			if (!Utils.isEmpty(criteria.getFirstName())) {
				clause = "firstName:" + criteria.getFirstName().trim() + "*";
				clauses.add(clause);
			}
			if (!Utils.isEmpty(criteria.getLastName())) {
				clause = "lastName:" + criteria.getLastName().trim() + "*";
				clauses.add(clause);
			}
			if (!Utils.isEmpty(criteria.getKeySkill())) {
				criteria.setKeySkill(criteria.getKeySkill().trim());

				char ch = '"';
				if (criteria.getKeySkill().contains(String.valueOf(ch)) || criteria.getKeySkill().contains("#") || criteria.getKeySkill().contains(",")) {
					if (criteria.getKeySkill().startsWith(String.valueOf(ch))) {
						log.info("from If " + criteria.getKeySkill());
						clause = "keySkill:" + criteria.getKeySkill().trim();
						clauses.add(clause);
					} else {
						if (criteria.getKeySkill().contains(",")) {
							String keySkills[] = criteria.getKeySkill().split(",");
							StringBuilder clauseSkills = new StringBuilder("keySkill:");
							clauseSkills.append(keySkills[0]);
							for (int i = 1; i < keySkills.length; i++)
								clauseSkills.append(" OR keySkill:").append(keySkills[i]);
							clauses.add("(" + clauseSkills + ")");
						} else {
							clause = "keySkill:(" + criteria.getKeySkill().trim() + ")";
							clauses.add(clause);
						}
					}
				} else {
					log.info("Else");
					/*
					 * if (criteria.getKeySkill().contains("(")) {
					 * criteria.setKeySkill(criteria.getKeySkill().replace("(",
					 * "")); } if (criteria.getKeySkill().contains(")")) {
					 * criteria.setKeySkill(criteria.getKeySkill().replace(")",
					 * "")); }
					 */
					clause = "keySkill:(" + criteria.getKeySkill().trim() + ")";
					clauses.add(clause);
				}
			}

			if (!Utils.isEmpty(criteria.getEmail())) {
				clause = "email:\"" + criteria.getEmail().trim() + "\"";
				clauses.add(clause);
			}
			if (!Utils.isEmpty(criteria.getVisaStats()) && criteria.getVisaStats().size() > 0) {

				// Single Select visaType Logic

				/*
				 * Pattern pattern = Pattern.compile("\\s"); Matcher matcher =
				 * pattern.matcher(criteria.getVisa()); boolean found =
				 * matcher.find(); if (found) {
				 * 
				 * String visa[] = criteria.getVisa().split(" ");
				 * 
				 * StringBuilder clause1 = new StringBuilder("visaType:");
				 * clause1.append(visa[0]);
				 * 
				 * for (int i = 1; i < visa.length; i++) clause1.append(
				 * " OR visaType:").append(visa[i]); clauses.add("(" + clause1 +
				 * ")"); } else { clause = "visaType:" +
				 * criteria.getVisa().trim(); clauses.add(clause); }
				 */

				// Multiple VisaType Selection Query Build Logic

				if (criteria.getVisaStats().size() == 1) {
					StringBuilder clause1 = new StringBuilder("visaType:");
					clause1.append("\"" + criteria.getVisaStats().get(0) + "\"");
					clauses.add("(" + clause1 + ")");
				} else {
					StringBuilder clause1 = new StringBuilder("visaType:");
					clause1.append("\"" + criteria.getVisaStats().get(0) + "\"");
					for (int i = 1; i < criteria.getVisaStats().size(); i++) {
						clause1.append(" OR visaType:").append("\"" + criteria.getVisaStats().get(i) + "\"");
					}
					clauses.add("(" + clause1 + ")");
				}
			}

			if (!Utils.isEmpty(criteria.getPhoneNumber())) {
				clause = "phone:" + criteria.getPhoneNumber();
				clauses.add(clause);
			}
			if (!Utils.isEmpty(criteria.getEducation())) {
				StringBuilder qualification = new StringBuilder("portalResumeQual:");
				qualification.append(criteria.getEducation().get(0) + "*");
				for (int i = 1; i < criteria.getEducation().size(); i++)
					qualification.append(" OR portalResumeQual:").append(criteria.getEducation().get(i) + "*");
				clauses.add("(" + qualification + ")");
			}
			if (!Utils.isEmpty(criteria.getWorkExperince())) {
				clause = "portalResumeExperience:[" + criteria.getWorkExperince() + "]";
				clauses.add(clause);
			}
			if (!Utils.isEmpty(criteria.getCompensation())) {
				clause = "presentRate:[" + criteria.getCompensation() + "]";
				clauses.add(clause);
			}

			if (!Utils.isEmpty(criteria.getTitle())) {
				clause = "title:" + criteria.getTitle().trim();
				clauses.add(clause);
			}
			if (!Utils.isEmpty(criteria.getCity())) {
				String[] loc = criteria.getCity().split(",");
				if (loc != null && loc.length > 0) {
					String cityName = loc[0];
					String cityNames[] = cityName.split(" ");
					clause = "city:(";
					for (int i = 0; i < cityNames.length; i++) {
						if (i == cityNames.length - 1) {
							clause += cityNames[i] + "*)";
						} else {
							clause += cityNames[i] + "* AND ";
						}
					}
					clauses.add(clause);
					if (loc.length > 1) {
						clause = "state:" + loc[1] + "*";
						clauses.add(clause);
					}
				}
			}
			if (!Utils.isEmpty(criteria.getStates())) {
				StringBuilder clauseState = new StringBuilder("state:");
				clauseState.append(criteria.getStates().get(0));
				for (int i = 1; i < criteria.getStates().size(); i++)
					clauseState.append(" OR state:").append(criteria.getStates().get(i));
				clauses.add("(" + clauseState + ")");
			}
			if (criteria.getCreatedBy() != null) {
				clause = "createdUser:" + criteria.getCreatedBy().trim();
				clauses.add(clause);
			}

			if (criteria.getCreated() != null) {
				clause = "createdOn:[" + parseDateRange(criteria.getCreated()) + "]";

				clauses.add(clause);
			}

			if (!Utils.isEmpty(criteria.getResumeTextQuery()))
				clauses.add("(" + criteria.getResumeTextQuery() + ")");

			if (Utils.isEmpty(clauses))
				return null;

			StringBuffer searchString = new StringBuffer();
			searchString.append(clauses.get(0));
			for (int i = 1; i < clauses.size(); i++)
				searchString.append(" AND ").append(clauses.get(i));

			if (searchString != null) {
				searchString.append(" AND deleteFlag:0 AND -status:" + CandidateStatus.OnAssignment);
			}
			log.info("searchString in Build Lucean:::::::" + searchString);
			return searchString.toString();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private String parseDateRange(Map<String, String> map) {
		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			Date startdate = null;
			Date enddate = null;
			String retVal = "";


			try {
				startdate = sdf1.parse(map.get("startDate").substring(0, 10));
				enddate = sdf1.parse(map.get("endDate").substring(0, 10));
				Calendar cal = Calendar.getInstance();
				// To get the accurate result
				cal.setTime(enddate);
				cal.set(Calendar.HOUR, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				enddate = cal.getTime();
				// date1 = sdf.format(startdate);
				// date2 = sdf.format(enddate);
				retVal = String.valueOf(startdate.getTime()) + " TO " + String.valueOf(enddate.getTime());
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}
			return retVal;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateService#
	 * getSubmittalsInfoByCandidateId(java.lang.Integer)
	 */
	/**
	 * @author Sreenath
	 * @throws ServiceException
	 *
	 */
	@Override
	public List<SubmittalDto> getSubmittalsInfoByCandidateId(Integer canId) throws ServiceException {
		try {
			final List<?> submittalsInfo = candidateDao.getSubmittalsInfoByCandidateId(canId);
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
			log.error(e.getMessage(), e);
			throw new ServiceException();

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#getSavedQueryNames(java.util.
	 * Date, java.util.Date, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSavedQueryNames(Date time, Date toDate, String currentLoginUser) {
		List<String> list = null;
		try {
			String query = "select distinct queryName from CadidateSearchAudit  where to_date(to_char(createdOn, 'YYYY/MM/DD'), 'YYYY/MM/DD') >=:fromDate and to_date(to_char(createdOn, 'YYYY/MM/DD'), 'YYYY/MM/DD') <=:toDate and createdBy=:createdBy and queryName is not null ORDER BY queryName asc";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("createdBy", currentLoginUser);
			java.sql.Date frDate = new java.sql.Date(time.getTime());
			java.sql.Date tDate = new java.sql.Date(toDate.getTime());

			params.put("fromDate", frDate);
			params.put("toDate", tDate);
			list = (List<String>) candidateDao.findByQuery(query, 0, 10, params);
			log.info("Save Queries List size:" + (list != null ? list.size() : 0));
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#getOnlineResumens(java.util.
	 * Date, java.util.Date, java.lang.String)
	 */
	@Override
	public List<?> getMobileAndOnlineResumens(CandidateSearchDto viewDetails, UserDto userDto) {
		try {
			final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
			Date dateStart = null;
			Date dateEnd = null;
			try {
				dateStart = formatter.parse(viewDetails.getStartDate());
				dateEnd = formatter.parse(viewDetails.getEndDate());
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
			final String createdBy = viewDetails.getCreatedBy();
			final String status = viewDetails.getStatus();
			// int count = 0;
			// count = getOnlineMobileCandidatesCountBySql(viewDetails,
			// dateStart,
			// dateEnd);
			final Map<String, Object> params = new HashMap<String, Object>();
			final StringBuilder hql = new StringBuilder();

			if (!Utils.isBlank(createdBy) && createdBy.equals("Online Resumes")) {
				hql.append(
						"select  c.candidate_id, c.first_name,c.last_name,c.state,c.email,c.title as candidateTitle ,c.created_on,c.updated_on,c.status,c.visa_type,c.phone,c.city,c.hot,c.block,n.order_id ,r.orig_document,c.created_by as portalInfo,j.title as jobTitle,j.created_by as dmName,n.id, count(*) OVER (PARTITION BY 1) as totalRecords,j.dmname as dm from candidate c,resume r,job_order j,online_cgi_candidates n ");

				hql.append(
						" where c.candidate_id=n.candidate_id and r.candidate_id=c.candidate_id and j.order_id=n.order_id  and ltrim(rtrim(c.created_by)) in ('CGI','Sapeare') and c.delete_flg=0 and  n.resume_status='"
								+ status + "'");

			}

			if (!Utils.isBlank(createdBy) && createdBy.equals("Mobile Resumes")) {
				hql.append(
						"select  c.candidate_id, c.first_name,c.last_name,c.state,c.email,c.title as candidateTitle ,c.created_on,c.updated_on,c.status,c.visa_type,c.phone,c.city,c.hot,c.block,n.order_id ,r.orig_document,c.created_by as portalInfo,j.title as jobTitle,j.created_by as dmName,n.id, count(*) OVER (PARTITION BY 1) as totalRecords,j.dmname as dm from candidate c,resume r,job_order j,mobile_cgi_candidates n ");

				hql.append(
						" where c.candidate_id=n.candidate_id and  r.candidate_id=c.candidate_id and n.order_id=j.order_id  and ltrim(rtrim(c.created_by)) in ('candidate_android','candidate_iPhone') and c.delete_flg=0 and n.resume_status='"
								+ status + "'");

			}
			if (dateStart != null) {
				hql.append(" and c.created_on >= :startDate");

				params.put("startDate", dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and c.created_on <= :endDate");

				params.put("endDate", DateUtils.addDays(dateEnd, 1));
			}
			if (!userDto.getUserRole().toString().equals(Constants.Admin)) {
				hql.append(" and (j.created_by = :logedInId or j.dmname = :logedInId)");
				params.put("logedInId", userDto.getUserId());
			}
			/*
			 * if(!Utils.isBlank(viewDetails.getFieldName()) &&
			 * !Utils.isBlank(viewDetails.getSortName())){
			 * if(viewDetails.getSortName().toUpperCase().equals(OrderByType.
			 * DESC. getValue())) hql.append(" order by "+
			 * viewDetails.getFieldName() + " DESC");
			 * if(viewDetails.getSortName().toUpperCase().equals(OrderByType.
			 * ASC. getValue())) hql.append(" order by "+
			 * viewDetails.getFieldName() + " ASC"); }
			 */
			if (!Utils.isBlank(viewDetails.getFieldName()) && !Utils.isBlank(viewDetails.getSortName())) {
				for (Entity_Table_Fields enumFields : Entity_Table_Fields.values()) {
					if (enumFields.getEntityField().equalsIgnoreCase(viewDetails.getFieldName())) {
						if (viewDetails.getSortName().toUpperCase().equals(OrderByType.DESC.getValue()))
							hql.append(" order by " + enumFields.getTableField() + " DESC");
						if (viewDetails.getSortName().toUpperCase().equals(OrderByType.ASC.getValue()))
							hql.append(" order by " + enumFields.getTableField() + " ASC");
					}
				}

			} else {
				hql.append(" order by COALESCE(c.updated_on,c.created_on) desc ");
			}
			/*
			 * else{ hql.append(" order by c.created_on DESC"); }
			 */
			hql.append(" LIMIT " + viewDetails.getMaxResults() + " OFFSET " + viewDetails.getStartPosition());

			List<?> result = candidateDao.findBySQLQuery(hql.toString(), params);
			log.info("size" + result.size());
			List<CandidateDto> candidateList = new ArrayList<>();
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

			if (result.size() > 0) {
				Iterator<?> it = result.iterator();
				while (it.hasNext()) {
					Object candidate[] = (Object[]) it.next();
					CandidateDto dto = new CandidateDto();
					final String candidateId = String.valueOf(candidate[0]);
					final String firstName = (String) candidate[1];
					final String lastName = (String) candidate[2];
					StringBuffer fullName = new StringBuffer();
					if (!Utils.isEmpty(firstName) || !Utils.isEmpty(lastName)) {
						StringBuffer name = new StringBuffer(firstName + "");
						fullName = name.append("  " + lastName);

					}

					final String city = (String) candidate[11];
					final String state = (String) candidate[3];

					final StringBuffer location = new StringBuffer();
					final String email = (String) candidate[4];
					final String title = (String) candidate[5];
					final Date createddate = (Date) candidate[6];
					final Date updateddate = (Date) candidate[7];
					// CandidateStatus candidateStatus = null;
					// final Integer statusId = (Integer) candidate[8];
					// if (statusId != null) {
					// for (CandidateStatus id : CandidateStatus.values()) {
					//
					// if (id.ordinal() == statusId) {
					// candidateStatus = id;
					// }
					//
					// }
					// }
					final String visaType = (String) candidate[9];
					final String phoneNumber = (String) candidate[10];
					final Object hot = candidate[12];
					final Object block = candidate[13];

					final String jobOrderId = candidate[14].toString();
					// final byte[] resumeContent = (byte[]) candidate[15];
					final String portalInfo = (String) candidate[16];
					final String jobTitile = (String) candidate[17];

					final String dmName = (candidate[21] != null ? (String) candidate[21] : (String) candidate[18]);

					final String totalRecords = candidate[20].toString();

					if (!Utils.isEmpty(candidateId))
						if (!Utils.isEmpty(candidateId))
							dto.setId(candidateId);
						else
							dto.setId("");
					if (!Utils.isEmpty(fullName.toString()))
						dto.setFirstName(fullName.toString());
					else
						dto.setFirstName("");
					if (!Utils.isEmpty(city) && !Utils.isEmpty(state)) {
						location.append(city + " , " + state);
						dto.setLocation(location.toString());
					} else if (Utils.isEmpty(city))
						dto.setLocation(state);
					else if (Utils.isEmpty(state))
						dto.setLocation(city);
					else
						dto.setLocation("");

					if (!Utils.isEmpty(email))
						dto.setEmail(email);
					else
						dto.setEmail("");
					if (!Utils.isEmpty(title))
						dto.setTitle(title);
					else
						dto.setTitle("");
					if (createddate != null) {
						String createdOn = sdf.format(createddate);
						dto.setCreatedOn(createdOn);
					}
					if (updateddate != null) {
						String updatedOn = sdf.format(updateddate);
						dto.setUpdatedOn(updatedOn);
					}
					if (status != null)
						dto.setStatus(status.toString());
					if (!Utils.isEmpty(visaType))
						dto.setVisaType(visaType);
					else
						dto.setVisaType("");
					if (!Utils.isEmpty(phoneNumber))
						dto.setPhoneNumber(phoneNumber);
					else
						dto.setPhoneNumber("");
					/*
					 * if (!Utils.isEmpty(resumeContent))
					 * dto.setResumeContent(new String(resumeContent)); else
					 * dto.setResumeContent("");
					 */
					if (hot != null) {
						dto.setHot((boolean) hot);
					} else
						dto.setHot(false);
					if (block != null) {
						dto.setBlock((boolean) block);
					} else
						dto.setBlock(false);

					if (!Utils.isBlank(jobOrderId))
						dto.setJobOrderId(jobOrderId);
					else
						dto.setJobOrderId("");
					if (!Utils.isBlank(portalInfo))
						dto.setPortalInfo(portalInfo);
					else
						dto.setPortalInfo("");
					if (!Utils.isBlank(jobTitile))
						dto.setJobTitle(jobTitile);
					else
						dto.setJobTitle("");
					if (!Utils.isBlank(dmName))
						dto.setDmName(dmName);
					else
						dto.setDmName("");
					if (!Utils.isBlank(createdBy) && createdBy.equals("Online Resumes")) {
						final Integer onlineCandidateId = (Integer) candidate[19];
						dto.setOnlineCandidateId(String.valueOf(onlineCandidateId));
					} else {
						final Integer mobileCandidateId = (Integer) candidate[19];
						dto.setMobileCandidateId(String.valueOf(mobileCandidateId));
					}
					if (totalRecords != null)
						dto.setTotalRecords(totalRecords);

					candidateList.add(dto);
				}
			}
			log.info("size" + candidateList.size());
			return (List<?>) candidateList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

	public int getOnlineMobileCandidatesCountBySql(CandidateSearchDto viewDetails, Date dateStart, Date dateEnd) {
		try {
			final String createdBy = viewDetails.getCreatedBy();
			final String status = viewDetails.getStatus();
			final Map<String, Object> params = new HashMap<String, Object>();
			final StringBuilder hql = new StringBuilder();
			int count = 0;
			if (!Utils.isBlank(createdBy) && createdBy.equals("Online Resumes")) {
				hql.append("select count(*) from Candidate c, Resume r, JobOrder j, OnlineCgiCandidates n ");
				hql.append("where c.id=n.candidateId and r.id=c.id and j.id=n.orderId and ltrim(rtrim(c.createdUser)) in ('CGI', 'Sapeare') and ");
				hql.append("c.deleteFlag=0 and n.resumeStatus=:status ");

			}
			if (!Utils.isBlank(createdBy) && createdBy.equals("Mobile Resumes")) {
				hql.append("select count(*) from Candidate c,Resume r,JobOrder j,MobileCgiCandidates n ");
				hql.append(
						"where c.id=n.candidateId and r.id=c.id and j.id=n.orderId and ltrim(rtrim(c.createdUser)) in ('candidate_android', 'candidate_iPhone') and ");
				hql.append("c.deleteFlag=0 and n.resumeStatus =:status ");
			}
			params.put("status", status);
			if (dateStart != null) {
				hql.append(" and c.createdOn >= :startDate");

				params.put("startDate", dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and c.createdOn <= :endDate");

				params.put("endDate", DateUtils.addDays(dateEnd, 1));
			}
			count = candidateDao.findCount(hql.toString(), params);
			log.info(" candidate count result " + count);

			return count;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#getportalCandidates(java.
	 * lang.String, java.lang.String, java.lang.String)
	 */
	/*
	 * public List<Candidate> getportalCandidates(String portalSelect, String
	 * month, String year, String status) { List<Candidate> map =
	 * candidateDao.getportalCandidates(portalSelect, month, year, status);
	 * return map; }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateService#getCandidate(int,
	 * boolean, boolean)
	 */
	@Override
	@Transactional(readOnly = true)
	public Candidate getCandidateFromResumeId(String resumeId, boolean fetchDocument, boolean fetchProperties) {
		try {
			if (log.isDebugEnabled()) {
				log.debug("Candidate id [" + resumeId + "] - fetchDocument[" + fetchDocument + "] fetchProperties[" + fetchProperties + "] ");
			}

			StringBuffer hql = new StringBuffer();
			hql.append("select distinct c from Candidate c");
			if (fetchDocument)
				hql.append(" join fetch c.resume");
			if (fetchProperties)
				hql.append(" left join fetch c.properties");
			hql.append(" where c.portalResumeId = :resumeId and c.deleteFlag=0 ");

			List<Candidate> result = candidateDao.findByQuery(hql.toString(), "resumeId", resumeId);
			hql.append(" order by COALESCE(c.updatedOn,c.createdOn) desc ");
			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#saveCandidates(java.util.
	 * Collection )
	 */
	@Override
	public void saveCandidates(Collection<Candidate> candidates) throws ServiceException {
		try {
			for (Candidate candidate : candidates)
				saveCandidate(candidate);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Candidate> getResumeIdByDate(List<String> resumeIdLst) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from Candidate");
			hql.append(" where deleteFlag=0 and portalResumeId||portalResumeLastUpd NOT IN(:resumeIdLst) and createdUser IN ('Careerbuilder','Monster')");
			params.put("resumeIdLst", resumeIdLst);
			return candidateDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<Candidate> getDiceResumeId(List<String> resumeIdLst) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from Candidate");
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
	public List<Candidate> getResumeIds(List<String> resumeIdLst) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from Candidate");
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
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public int findCandidatesCount(CandidateSearchDto criteria) {
		log.info("Start findCandidatesCount");
		try {
			String searchString = buildLuceneQuery(criteria);
			if (!Utils.isEmpty(searchString))
				return candidateDao.findCountByLuceneQuery(searchString);
			else {
				StringBuffer hql1 = new StringBuffer("select count(c) from Candidate c where c.deleteFlag=0 ");
				List<?> result = candidateDao.runQuery(hql1.toString());
				return ((Number) result.get(0)).intValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateService#listExistingTitles()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#getCandidatesByUser(java.util
	 * .Date, java.util.Date)
	 */
	@Override
	public Map<String, Integer> getCandidatesByUser(Date dateStart, Date dateEnd, User user) {
		try {
			return candidateDao.getCandidatesByUser(dateStart, dateEnd, user);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	
	@Override
	public Map<String, Object> getResumeStats(Date dateStart, Date dateEnd, User user) {
		try {
			return candidateDao.getResumeStats(dateStart, dateEnd, user);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#getCandidatesByUser(java.util
	 * .Date, java.util.Date)
	 */
	@Override
	public Map<String, Object> getPortalIdsByUser(Date dateStart, Date dateEnd, String portalName) {
		try {
			return candidateDao.getPortalIdsByUser(dateStart, dateEnd, portalName);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#getCandidatesOnUpdatedDate
	 * (java.util .Date, java.util.Date)
	 */
	public Map<String, Integer> getCandidatesOnUpdatedDate(Date dateStart, Date dateEnd) {
		try {
			return candidateDao.getCandidatesOnUpdatedDate(dateStart, dateEnd);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#getCandidatesOnUpdatedDate
	 * (java.util .Date, java.util.Date)
	 */
	public Map<String, Map<String, Integer>> getdiceUsageCount(Date dateStart, Date dateEnd) {
		try {
			return candidateDao.getdiceUsageCount(dateStart, dateEnd);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateService#reindexCandidates(org.
	 * hibernate .search.batchindexing.MassIndexerProgressMonitor, boolean)
	 */
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

	/**
	 * @return
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateService#getDeletedCandidates()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Candidate> getDeletedCandidates() {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("from Candidate");
			hql.append(" where deleteFlag=1");
			return (List<Candidate>) candidateDao.runQuery(hql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	// code added by Radhika on 1/8/2013 for retrieving hot list candidate
	// details in resumes page ATS-239
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Candidate> getBlackCandidates() {
		try {
			StringBuffer hql = new StringBuffer();
			// Map<String, Object> params = new HashMap<String, Object>();
			hql.append(" from Candidate c ");
			hql.append(" where c.block=true and c.deleteFlag=0   ");
			// params.put("status", "Hot");
			return (List<Candidate>) candidateDao.runQuery(hql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Candidate> getCBCandidates() {
		try {
			StringBuffer hql = new StringBuffer();
			// Map<String, Object> params = new HashMap<String, Object>();
			hql.append(" from Candidate c ");
			hql.append(" where c.createdUser='Careerbuilder' and c.deleteFlag=0 ");
			// params.put("status", "Hot");
			return (List<Candidate>) candidateDao.runQuery(hql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	// code added by Radhika on 1/8/2013 for retrieving black list candidate
	// details in resumes page ATS-240
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Candidate> getHotCandidates() {
		try {
			StringBuffer hql = new StringBuffer();
			// Map<String, Object> params = new HashMap<String, Object>();
			hql.append(" from Candidate c ");
			hql.append(" where c.hot=true and c.deleteFlag=0 ");
			// params.put("status", "Hot");
			return (List<Candidate>) candidateDao.runQuery(hql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateService#getBlankCandidates()
	 */
	@Override
	public List<CandidateDto> getBlankCandidates(CandidateSearchDto criteria) {
		try {
			long stime = System.currentTimeMillis();
			log.info("getBlankCandidates");
			final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
			Date fromDate = null;
			Date toDate = null;
			try {
				fromDate = formatter.parse(criteria.getStartDate());
				toDate = formatter.parse(criteria.getEndDate());
				Calendar cal = Calendar.getInstance();
				// To get the accurate result
				cal.setTime(toDate);
				cal.set(Calendar.HOUR, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				toDate = cal.getTime();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append(
					"select c.candidate_id,c.first_name, c.last_name, c.title, c.email, c.city, c.state,c.phone,c.visa_type,c.visa_expired_date,c.doctype, c.updated_on, count(*) OVER (PARTITION BY 1) as totalRecords from candidate c ");
			hql.append(
					" where  c.delete_flg=0 and (c.first_name is null or  c.first_name IN('') or c.last_name is null or c.last_name IN('') or c.title is null or c.title IN('') or c.city is null or c.city IN('') or c.state is null or c.state IN('') or c.email is null or c.email IN('') ) and c.doctype is not null");
			hql.append(" and (c.last_name NOT LIKE 'Monster:Unknown%' or c.email not IN('Monster:Unknown'))");

			/*
			 * if (fromDate != null && toDate != null) { hql.append(
			 * " and created_on >= :startDate and created_on <= :endDate");
			 * params.put("startDate", fromDate); params.put("endDate", toDate);
			 * 
			 * }
			 */
			if (fromDate != null) {
				hql.append(" and created_on >= :startDate");
				params.put("startDate", fromDate);
			}
			if (toDate != null) {
				hql.append(" and created_on <= :endDate");
				params.put("endDate", toDate);
			}
			// hql.append(" order by createdOn DESC ");
			/*
			 * OrderByColumn orderBy = criteria.getOrderByColumn(); if (orderBy
			 * != null) { hql.append(orderBy.getValue()); OrderByType orderType
			 * = criteria.getOrderByType() != null ? criteria.getOrderByType() :
			 * OrderByType.ASC; hql.append(orderType.getValue()); }
			 */
			if (!Utils.isBlank(criteria.getFieldName()) && !Utils.isBlank(criteria.getSortName())) {
				for (Entity_Table_Fields enumFields : Entity_Table_Fields.values()) {
					if (enumFields.getEntityField().equalsIgnoreCase(criteria.getFieldName())) {
						if (criteria.getSortName().toUpperCase().equals(OrderByType.DESC.getValue()))
							hql.append(" order by " + enumFields.getTableField() + " DESC");
						if (criteria.getSortName().toUpperCase().equals(OrderByType.ASC.getValue()))
							hql.append(" order by " + enumFields.getTableField() + " ASC");
					}
				}

			} else {
				hql.append("  order by COALESCE(c.updated_on,c.created_on) DESC");
			}

			hql.append(" LIMIT " + criteria.getMaxResults() + " OFFSET " + criteria.getStartPosition());
			log.info(hql.toString());
			System.out.println("log.info(hql.toString())" + hql.toString());

			List<?> candidates = candidateDao.findBySQLQuery(hql.toString(), params);

			// List<?> candidates = candidateDao.findByQuery(hql.toString(),
			// criteria.getStartPosition(),criteria.getMaxResults(), params);
			List<CandidateDto> missedCandidate = null;
			if(candidates!=null && candidates.size()>0){
				missedCandidate = new ArrayList<CandidateDto>();
			Iterator<?> itr = candidates.iterator();
			while (itr.hasNext()) {
				Object[] obj = (Object[]) itr.next();
				CandidateDto candidate = new CandidateDto();
				candidate.setId(Utils.getStringValueOfObj(obj[0]));
				candidate.setFirstName(Utils.concatenateTwoStringsWithSpace(Utils.getStringValueOfObj(obj[1]), Utils.getStringValueOfObj(obj[2])));
				candidate.setTitle(Utils.getStringValueOfObj(obj[3]));
				candidate.setEmail(Utils.getStringValueOfObj(obj[4]));
				candidate.setCity(Utils.getStringValueOfObj(obj[5]));
				candidate.setState(Utils.getStringValueOfObj(obj[6]));

				if (!Utils.isBlank(Utils.getStringValueOfObj(obj[5])) && !Utils.isBlank(Utils.getStringValueOfObj(obj[6]))) {
					candidate.setLocation(Utils.concatenateTwoStringsWithSpace(Utils.getStringValueOfObj(obj[5]), Utils.getStringValueOfObj(obj[6])));
				} else if (!Utils.isBlank(Utils.getStringValueOfObj(obj[5]))) {
					candidate.setLocation(Utils.getStringValueOfObj(obj[5]));
				} else if (!Utils.isBlank(Utils.getStringValueOfObj(obj[6]))) {
					candidate.setLocation(Utils.getStringValueOfObj(obj[6]));
				} else {
					candidate.setLocation(null);
				}

				candidate.setPhoneNumber(Utils.getStringValueOfObj(obj[7]));
				candidate.setVisaType(Utils.getStringValueOfObj(obj[8]));
				candidate.setVisaExpiryDate(Utils.getStringValueOfObj(obj[9]));
				candidate.setDocumentType(Utils.getStringValueOfObj(obj[10]));
				Date updated = (Date) obj[11];
				candidate.setUpdatedOn(Utils.convertDateToString(updated));
				candidate.setTotalRecords(Utils.getStringValueOfObj(obj[12]));
				missedCandidate.add(candidate);
			}
			}
			long etime = System.currentTimeMillis();
			log.info("getBlankCandidates" + (stime - etime));
			return missedCandidate;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateService#getonlineCandidates()
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Candidate> getonlineCandidates() {
		try {
			StringBuffer hql = new StringBuffer();
			// Map<String, Object> params = new HashMap<String, Object>();
			hql.append(" from Candidate c ");
			hql.append(" where c.createdUser in('CGI','RedGalaxy','Sapeare') and c.deleteFlag=0 ");
			// params.put("status", "Hot");
			return (List<Candidate>) candidateDao.runQuery(hql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#getportalResumes(java.lang
	 * .String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public Map<String, Map<String, Integer>> getportalResumes(String portalSelect, String dateStart, String dateEnd, String status) {
		try {
			Map<String, Map<String, Integer>> map = candidateDao.getportalResumes(portalSelect, dateStart, dateEnd, status);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @param portalSelect
	 * @param month
	 * @param year
	 * @return
	 */
	public List<AllPortalResumes> getAllPortalCandidates(String portalSelect, String month, String year, String status) {
		try {
			List<AllPortalResumes> map = candidateDao.getAllPortalCandidates(portalSelect, month, year, status);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<?> getPortalCandidates(CandidateSearchDto viewDetails) {
		try {
			List<?> map = candidateDao.getPortalCandidates(viewDetails);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Integer> getAllCandidatesCounts(Date dateStart, Date dateEnd) {
		try {
			Map<String, Integer> map = candidateDao.getAllCandidatesCounts(dateStart, dateEnd);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

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

	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = true) public Candidate loadCandidate(Integer
	 * candidateId) { Candidate candidate = candidateDao.findById(candidateId);
	 * return candidate; }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#saveCandidate(com.uralian
	 * .cgiats .model.Candidate)
	 */
	@Override
	public Candidate saveCandidateFromMobile(Candidate candidate) throws ServiceException {
		Candidate rtnCandidate = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + candidate);
			System.out.println("candidate save>>" + candidate);
			rtnCandidate = candidateDao.save(candidate);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist candidate", exception);
		}
		return rtnCandidate;
	}

	public void upDateCandidateFromMobile(Candidate candidate) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Updating " + candidate);

			candidateDao.update(candidate);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to update candidate", exception);
		}

	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<Candidate> getmobileCandidates() {
		try {
			StringBuffer hql = new StringBuffer();
			// Map<String, Object> params = new HashMap<String, Object>();
			hql.append(" from Candidate c ");
			hql.append(" where c.createdUser='candidate_iPhone' and c.deleteFlag=0 ");
			// params.put("status", "Hot");
			return (List<Candidate>) candidateDao.runQuery(hql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Integer> getSearchCandidatesByUser(Date dateStart, Date dateEnd, User user) {
		try {
			return candidateDao.getSearchCandidatesByUser(dateStart, dateEnd, user);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateService#
	 * saveSearchCandidateAuditDetails
	 * (com.uralian.cgiats.model.CadidateSearchAudit)
	 */
	@Override
	public void saveSearchCandidateAuditDetails(CadidateSearchAudit candidateSearchVo) {
		try {
			candidateDao.saveSearchCandidateAuditDetails(candidateSearchVo);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#getSearchCandidateAudit(java
	 * .util.Date, java.util.Date)
	 */
	@Override
	public List<CadidateSearchAudit> getSearchCandidateAudit(Date fromDate, Date toDate, String userName, String queryName) {
		try {
			return candidateDao.getSearchCandidateAudit(fromDate, toDate, userName, queryName);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<Candidate> getCandidates(Candidate candidate) {
		try {
			List<Candidate> candidates = new ArrayList<Candidate>();
			StringBuffer hql = new StringBuffer();
			hql.append("select distinct c from Candidate c where ");
			if (candidate.getFirstName().length() > 0) {
				hql.append(" c.firstName like '" + candidate.getFirstName() + "%'");
			}
			if ((candidate.getLastName().length() > 0) && (candidate.getFirstName().length() > 0)) {
				hql.append(" and c.lastName like '" + candidate.getLastName() + "%'");
			}
			if ((candidate.getFirstName().length() == 0) && (candidate.getLastName().length() > 0)) {
				hql.append(" c.lastName like '" + candidate.getLastName() + "%'");
			}
			if ((candidate.getEmail().length() > 0) && ((candidate.getFirstName().length() > 0) || (candidate.getLastName().length() > 0))) {
				hql.append(" and c.email like '" + candidate.getEmail() + "%'");
			}
			log.info("" + candidate.getFirstName().length() + "Email" + candidate.getEmail() + "hql" + hql);
			if ((candidate.getFirstName().length() == 0) && (candidate.getLastName().length() == 0) && (candidate.getEmail().length() > 0)) {
				hql.append("  c.email like '" + candidate.getEmail() + "%'");
			}
			hql.append(" order by c.createdOn desc");
			log.info("hql" + hql);

			candidates = candidateDao.findByQuery(hql.toString(), null);
			List<String> uniqueCandidates = new ArrayList<String>();

			for (java.util.Iterator<Candidate> it = candidates.iterator(); it.hasNext();) {
				Candidate c = it.next();
				if (!uniqueCandidates.contains(c.getEmail())) {
					uniqueCandidates.add(c.getEmail());
				} else {
					it.remove();
				}
			}

			return candidates;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<CandidateDto> findCandidatesBasedOnStatus(CandidateSearchDto candidate, int first, int pageSize) {
		try {
			Date dateStart = Utils.convertAngularStrToDate(Utils.replaceNullWithEmpty(candidate.getStartDate()));
			Date dateEnd = Utils.convertAngularStrToDate(Utils.replaceNullWithEmpty(candidate.getEndDate()));
			Map<String, Object> params = new HashMap<String, Object>();

			StringBuffer hqlQuery = new StringBuffer();
			hqlQuery.append("select c.id, c.firstName, c.lastName, c.title, c.address, c.email, c.createdOn, c.updatedOn, c.status from Candidate c");
			hqlQuery.append(" where c.deleteFlag=0 and");
			if (!Utils.isBlank(candidate.getStatus())) {

				hqlQuery.append(" c.status=?1");
				params.put("1", CandidateStatus.valueOf(candidate.getStatus()));

			}
			if (dateStart != null && dateEnd != null) {
				// if (dateStart.equals(dateEnd)) {
				Calendar endDate = Calendar.getInstance();
				endDate.setTime(dateEnd);
				endDate.add(Calendar.DATE, +1);
				hqlQuery.append(" and c.createdOn>=:startDate and c.createdOn<:endDate");
				params.put("startDate", dateStart);
				params.put("endDate", endDate.getTime());
				// } else {
				// hqlQuery.append(" and c.createdOn>=:startDate and
				// c.createdOn<=:endDate");
				// params.put("startDate", dateStart);
				// params.put("endDate", dateEnd);
				// }

			}
			hqlQuery.append(" order by c.createdOn DESC");
			List<?> result = candidateDao.findByQuery(hqlQuery.toString(), 0, 0, params);

			List<CandidateDto> candidateList = new ArrayList<>();
			if (result != null) {
				Iterator<?> itr = result.iterator();
				while (itr.hasNext()) {
					Object candidatestatus[] = (Object[]) itr.next();
					CandidateDto dto = new CandidateDto();
					dto.setId(Utils.getStringValueOfObj(candidatestatus[0]));
					dto.setFirstName(
							Utils.concatenateTwoStringsWithSpace(Utils.getStringValueOfObj(candidatestatus[1]), Utils.getStringValueOfObj(candidatestatus[2])));
					dto.setTitle(Utils.getStringValueOfObj(candidatestatus[3]));
					Address address = (Address) candidatestatus[4];
					if (address != null) {
						dto.setLocation(Utils.concatenateTwoStringsWithSpace(address.getCity(), address.getCountry()));
					}
					dto.setEmail(Utils.getStringValueOfObj(candidatestatus[5]));
					dto.setCreatedOn(Utils.convertDateToString((Date) candidatestatus[6]));
					dto.setUpdatedOn(Utils.convertDateToString((Date) candidatestatus[7]));
					dto.setStatus(((CandidateStatus) candidatestatus[8]).name());

					candidateList.add(dto);
				}
			}

			/*
			 * Integer onAssignment = CandidateStatus.OnAssignment.ordinal();
			 * Integer notIntersted = CandidateStatus.NotInterested.ordinal();
			 * Integer resumeSent = CandidateStatus.ResumeSent.ordinal();
			 * Integer lmvm = CandidateStatus.LMVM.ordinal();
			 * 
			 * if (candidate != null) { hqlQuery.append(" where c.status in('" +
			 * candidate.getStatus().ordinal() + "')");
			 * 
			 * } else { hqlQuery.append(" where c.status in(" + onAssignment +
			 * "," + notIntersted + "," + resumeSent + "," + lmvm + ")"); }
			 */

			return candidateList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void updateCandidateStatus(CandidateStatuses status) {
		try {
			candidateDao.updateCandidateStatus(status);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

	}

	@Override
	public CandidateStatuses getCandidateStatus(Candidate candidate) {
		try {
			CandidateStatuses stauses = candidateDao.getCandidateStatus(candidate);
			return stauses;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#getCandidatesList(com.uralian
	 * .cgiats.dto.SearchCriteria)
	 */
	@Override
	public List<?> getCandidatesList(SearchCriteria searchCriteria) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			List<CandidateDto> candidatesDto = new ArrayList<CandidateDto>();

			List<?> candidates = candidateDao.getCandidatesList(searchCriteria);
			if (candidates != null && candidates.size() > 0) {
				Iterator<?> it = candidates.iterator();

				while (it.hasNext()) {
					Object candidate[] = (Object[]) it.next();
					CandidateDto dto = new CandidateDto();
					dto.setId(String.valueOf(candidate[0]));
					dto.setFirstName((String) candidate[1] + (String) candidate[2]);

					dto.setLocation((String) candidate[3]);

					dto.setEmail((String) candidate[4]);
					dto.setTitle((String) candidate[5]);

					Date createddate = (Date) candidate[6];
					Date updateddate = (Date) candidate[7];
					if (createddate != null && updateddate != null) {
						String createdOn = sdf.format(createddate);
						String updatedOn = sdf.format(updateddate);
						dto.setCreatedOn(createdOn);
						dto.setUpdatedOn(updatedOn);
					}

					candidatesDto.add(dto);
				}
			}
			return candidatesDto;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#deleteSavedQuery(java.lang.
	 * Integer)
	 */
	@Override
	public int deleteSavedQuery(Integer savedQueryId) {
		try {
			StringBuffer query = new StringBuffer("delete from CadidateSearchAudit c where c.candidateSearchId=:candidateSearchId");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("candidateSearchId", savedQueryId);

			return candidateDao.deleteSavedQuery(query.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return 0;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateService#getSavedQuery(int)
	 */
	@Override
	public CadidateSearchAudit getSavedQuery(int savedQueryId) {
		try {
			StringBuffer query = new StringBuffer("from CadidateSearchAudit c where c.candidateSearchId=:candidateSearchId");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("candidateSearchId", savedQueryId);

			return candidateDao.getSavedQuery(query.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#getResumeByCandidateId(java.
	 * lang.String)
	 */
	@Override
	public ResumeDto getResumeByCandidateId(String candidateId) {
		try {
			return candidateDao.getResumeByCandidateId(candidateId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public CandidateVo getAllUserDetails() {
		try {
			return candidateDao.getAllUserDetails();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#isCandidateEmailExists(java.
	 * lang.Integer)
	 */
	@Override
	public Candidate getCandidateByEmail(String emailId) {
		try {
			return candidateDao.getCandidateByEmail(emailId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#saveCandidateFromOpenJobOrder
	 * (com.uralian.cgiats.model.Candidate, java.lang.Boolean)
	 */
	@Override
	public void saveCandidateFromOpenJobOrder(Candidate candidate, JobOrder jobOrder, Boolean isCandidateExists, String portalName) throws ServiceException {
		try {
			if (!isCandidateExists) {
				candidate.setCreatedOn(new Date());
				candidate.getResume().setCreatedOn(new Date());
				candidateDao.save(candidate);
			} else {
				candidate = getCandidateFromEmail(candidate.getEmail(), true, false);
			}
			candidate.setCreatedUser(portalName);
			OnlineCgiCandidates onlineCgiCandidates = new OnlineCgiCandidates();
			onlineCgiCandidates.setCandidateId(candidate);
			onlineCgiCandidates.setOrderId(jobOrder);
			onlineCgiCandidates.setCreatedOn(new Date());

			User user = userDao.findById(jobOrder.getDmName()!=null?jobOrder.getDmName():jobOrder.getCreatedBy());
			String name = "resume" + candidate.getDocumentType().getPreferredExtension();
			AttachmentInfo ai = new AttachmentInfo(name, candidate.getDocument(), candidate.getDocumentType().getMimeType());
			try {
				if (user != null && user.getEmail() != null && !user.getEmail().trim().equals("") && user.getEmail().contains("@")) {
					String subject = "Online Resume submitted for JobOrder # " + jobOrder.getId() + ":" + jobOrder.getTitle();
					String mailMsg = "Hi <b>";
					if (user != null && user.getFirstName() != null && !user.getFirstName().trim().equals(""))
						mailMsg = mailMsg + user.getFirstName() + "</b>,";
					if (candidate.getCreatedUser().equalsIgnoreCase(Constants.CharterGlobal) || candidate.getCreatedUser().equalsIgnoreCase(Constants.CGI)) {
						mailMsg = mailMsg + "<br/><br/>" + "A candidate has been submitted from CGI website.";
					} else if (candidate.getCreatedUser().equalsIgnoreCase(Constants.Sapeare)) {
						mailMsg = mailMsg + "<br/><br/>" + "A candidate has been submitted from Sapeare website.";
					} else if (candidate.getCreatedUser().equalsIgnoreCase(Constants.RedGalaxy)) {
						mailMsg = mailMsg + "<br/><br/>" + "A candidate has been submitted from RedGalaxy website.";
					}

					mailMsg = mailMsg + " Candidate details are as follows<br><br>" + "<table>" + " <tr><td><b>Candidate Id</b></td><td>:" + candidate.getId()
							+ "</td></tr>" + "<tr><td><b>Candidate Name</b></td><td>:" + candidate.getFullName() + "</td></tr>"
							+ "<tr><td><b>Email</b> </td><td>:" + candidate.getEmail() + "</td></tr>";
					if (jobOrder != null && jobOrder.getId() != 0 && jobOrder.getCreatedBy() != null && jobOrder.getAssignedTo() != null
							&& !jobOrder.getAssignedTo().equals("") && jobOrder.getAssignedToUser().getUserId() != null) {
						mailMsg = mailMsg + "<tr><td><b>Job Order Id</b></td><td>: " + jobOrder.getId() + "</td></tr>" + "<tr><td> <b>Title</b></td><td>: "
								+ jobOrder.getTitle() + "</td></tr>" + "<tr><td><b>Assigned To </b></td><td>:" + jobOrder.getAssignedToUser().getUserId()
								+ "</td></tr>" + "<tr><td><b>Job Order Location</b></td><td>:" + jobOrder.getCity() + ", " + jobOrder.getState()
								+ "</td></tr></table> ";
					} else if (jobOrder != null && jobOrder.getId() != 0 && jobOrder.getCreatedBy() != null) {
						mailMsg = mailMsg + "<tr><td><b>Job Order Id</b></td><td>: " + jobOrder.getId() + "</td></tr>" + "<tr><td> <b>Title</b></td><td>: "
								+ jobOrder.getTitle() + "</td></tr>" + "<tr><td><b>Job Order Location</b></td><td>:" + jobOrder.getCity() + ", "
								+ jobOrder.getState() + "</td></tr></table>  ";
					}
					mailMsg = mailMsg
							+ "<br>Please find the attached Resume of the mentioned candidate<br/><br/><b>*** This is an automatically generated email, please do not reply ***</b> ";
					commService.sendEmail(null, user.getEmail(), subject, mailMsg, ai);
					/*
					 * commService.sendEmail(CGIATSConstants. PROD_MAIL,
					 * "hsambalampalli@charterglobal.com" , subject, mailMsg,
					 * ai);
					 */
					log.info("mail sent to create by user::" + user.getEmail());
				}
				if (jobOrder != null && jobOrder.getAssignedTo() != null && jobOrder.getAssignedToUser().getEmail() != null
						&& !jobOrder.getAssignedToUser().getEmail().trim().equals("") && jobOrder.getAssignedToUser().getEmail().contains("@")) {
					String subject = "Online Resume submitted for JobOrder # " + jobOrder.getId() + " (" + jobOrder.getTitle() + ")";

					String mailMsg = "Hi  <b>" + jobOrder.getAssignedToUser().getFirstName() + "</b>,"
							+ "A candidate has been submitted from CGI website. Candidate details are as follows<br><br>" + "<table>"
							+ " <tr><td><b>Candidate Id</b></td><td>:" + candidate.getId() + "</td></tr>" + "<tr><td><b>Candidate Name</b></td><td>:"
							+ candidate.getFullName() + "</td></tr>" + "<tr><td><b>Email</b> </td><td>:" + candidate.getEmail() + "</td></tr>";

					mailMsg = mailMsg + "<tr><td><b>Job Order Id</b></td><td>: " + jobOrder.getId() + "</td></tr>" + "<tr><td> <b>Title</b></td><td>: "
							+ jobOrder.getTitle() + "</td></tr>" + "<tr><td><b>Assigned To </b></td><td>:" + jobOrder.getAssignedToUser().getUserId()
							+ "</td></tr>" + "<tr><td><b>Job Order Location</b></td><td>:" + jobOrder.getCity() + ", " + jobOrder.getState()
							+ "</td></tr></table> ";

					mailMsg = mailMsg
							+ "<br>Please find the attached Resume of the mentioned candidate<br/><br/><b>*** This is an automatically generated email, please do not reply ***</b> ";
					commService.sendEmail(null, jobOrder.getAssignedToUser().getEmail(), subject, mailMsg, ai);
					/*
					 * commService.sendEmail(CGIATSConstants. PROD_MAIL
					 * ,"hsambalampalli@charterglobal.com" , subject, mailMsg,
					 * ai);
					 */
					log.info("mail sent to assigned to user::" + jobOrder.getAssignedToUser().getEmail());
				}

				// if (candidate != null &&
				// portalName.equalsIgnoreCase(Constants.Sapeare) &&
				// candidate.getCreatedUser().equalsIgnoreCase(Constants.Sapeare)
				// && candidate.getEmail().contains("@")) {
				if (candidate != null && candidate.getEmail().contains("@")) {
					String subject = portalName.toUpperCase() + ": " + jobOrder.getTitle();

					String MailMsg = "Hi <b>" + candidate.getFullName() + "</b>,"
					/*
					 * +
					 * "Thank you for your interest in Sapeare Executive Search."
					 */
							+ "<br/><br/>" + " <div style='text-align:center'><b>Thank you for your interest in " + portalName
							+ " Executive Search. </b></div><br>" + "We have received your resume and have updated your record in our Executive Database. "
							+ " A member of our Executive Search team will follow up with you if more information is needed." + "<br/><br/>"
							+ " <table><tr><td><b>*** This is an automatically generated email, please do not reply ***</b></td></tr></table>";
					commService.sendEmail(null, candidate.getEmail().trim(), subject, MailMsg);
					log.info("mail sent to Candidate1::" + candidate.getEmail());
					log.info("mail sent to Candidate2::" + candidate.getEmail().trim());
				}

				if (jobOrder != null && jobOrder.getAssignedToUser() != null && jobOrder.getAssignedToUser().getUserId() != null
						&& !jobOrder.getAssignedToUser().getUserId().trim().equals("")) {
					// submittal.setCreatedBy(jobOrder.getAssignedTo().getUserId());
					onlineCgiCandidates.setCreatedBy(jobOrder.getAssignedToUser().getUserId());
					log.info("CreatedBy on OnlineCgiCandidates------------>" + jobOrder.getAssignedToUser().getUserId());
				} else if (user != null && user.getUserId() != null && !user.getUserId().trim().equals("")) {
					// submittal.setCreatedBy(user.getUserId());
					onlineCgiCandidates.setCreatedBy(user.getUserId());
					log.info("CreatedBy in Else------------>" + user.getUserId());
				}
				onlineCgiCandidates.setResumeStatus("PENDING");
				onlineCgiCanidateDao.save(onlineCgiCandidates);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public List<CandidateStatusesDto> getCandidateStatusListByCandidateId(Integer candidateId) {
		try {
			return candidateDao.getCandidateStatusListByCandidateId(candidateId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateService#viewCandidate(java.lang.
	 * Integer)
	 */
	@Override
	public CandidateDto viewCandidate(Integer candidateId) {
		CandidateDto candidateDto = null;
		try {
			StringBuffer query = new StringBuffer(
					"select c.id,c.firstName,c.lastName,c.email,c.phone,c.phoneAlt,c.securityClearance,c.keySkill,c.resume.parsedText from Candidate c where c.id=?1 and c.deleteFlag=0");
			List<?> result = candidateDao.findByQuery(query.toString(), "1", candidateId);
			if (result != null && result.size() > 0) {
				candidateDto = new CandidateDto();
				Iterator<?> candidateIterator = result.iterator();
				while (candidateIterator.hasNext()) {
					Object[] candidateObj = (Object[]) candidateIterator.next();
					candidateDto.setId(String.valueOf(candidateObj[0]));
					candidateDto.setFullName(Utils.concatenateTwoStringsWithSpace((String) candidateObj[1], (String) candidateObj[2]));
					candidateDto.setEmail((String) candidateObj[3]);
					candidateDto.setPhoneNumber((String) candidateObj[4]);
					candidateDto.setAltPhoneNumber((String) candidateObj[5]);
					candidateDto.setStrSecurityClearance(Utils.convertBooleanToStringValue((Boolean) candidateObj[6]));
					candidateDto.setKeySkill((String) candidateObj[7]);
					String Resume = (String) candidateObj[8];
					candidateDto.setResumeContent(Resume != null ? Resume : "Resume not Found");
				}
				query = new StringBuffer(
						"select cs.id,cs.status,cs.createdDate,cs.reason,cs.createdBy from CandidateStatuses cs where cs.candidate.id=?1 order by cs.createdDate desc");
				result = candidateDao.findByQuery(query.toString(), "1", candidateId);
				if (result != null && result.size() > 0) {
					List<CandidateStatusesDto> statusHistory = new ArrayList<CandidateStatusesDto>();
					Iterator<?> candidateHistoryIterator = result.iterator();
					while (candidateHistoryIterator.hasNext()) {
						CandidateStatusesDto historyDto = new CandidateStatusesDto();
						Object[] historyObj = (Object[]) candidateHistoryIterator.next();
						historyDto.setStatus(((CandidateStatus) historyObj[1]).name());
						historyDto.setCreatedDate((Date) historyObj[2]);
						historyDto.setCreatedBy(Utils.getStringValueOfObj(historyObj[4]));
						if (historyObj[3] != null)
							historyDto.setReason(new String((byte[]) historyObj[3]));
						statusHistory.add(historyDto);

					}
					candidateDto.setStatusHistory(statusHistory);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return candidateDto;
	}
	
	@Override
	public Long getAllResumesCounts(Date fromDate, Date toDate) {
		try {
			return candidateDao.getAllResumesCounts(fromDate, toDate);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Object> getJobBoardStats(Date startDate, Date endDate) {
		 
		return candidateDao.getJobBoardStats(startDate, endDate);
	}

}
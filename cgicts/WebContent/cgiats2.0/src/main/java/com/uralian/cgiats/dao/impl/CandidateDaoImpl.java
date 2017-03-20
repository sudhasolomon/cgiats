package com.uralian.cgiats.dao.impl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Future;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.apache.commons.lang.time.DateUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Version;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.Search;
import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.CandidateDao;
import com.uralian.cgiats.dto.CandidateDto;
import com.uralian.cgiats.dto.CandidateStatusesDto;
import com.uralian.cgiats.dto.CandidateVo;
import com.uralian.cgiats.dto.JobBoardStatsDto;
import com.uralian.cgiats.dto.ResumeDto;
import com.uralian.cgiats.dto.SearchCriteria;
import com.uralian.cgiats.model.Address;
import com.uralian.cgiats.model.CadidateSearchAudit;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.CandidateStatus;
import com.uralian.cgiats.model.CandidateStatuses;
import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.model.Entity_Table_Fields;
import com.uralian.cgiats.model.OrderByColumn;
import com.uralian.cgiats.model.OrderByType;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.util.AllPortalResumes;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.Utils;

import bsh.util.Util;

/**
 * This is the default implementation of the Candidate DAO.
 * 
 * @author Christian Rebollar
 */
@Repository
@SuppressWarnings("unchecked")
public class CandidateDaoImpl extends GenericDaoImpl<Candidate, Integer> implements CandidateDao {
	/**
	 */
	public CandidateDaoImpl() {
		super(Candidate.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.dao.CandidateDao#findAllTitles()
	 */
	@Override
	public List<String> findAllTitles() {
		String hql = "select distinct c.title from Candidate c where c.title is not null order by c.title";
		List<String> list = (List<String>) runQuery(hql.toString());
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.dao.CandidateDao#getCandidatesByUser(java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public Map<String, Integer> getCandidatesByUser(Date dateStart, Date dateEnd, User user) {
		List<String> paramNames = new ArrayList<String>();
		List<Object> paramValues = new ArrayList<Object>();

		StringBuilder hql = new StringBuilder();
		hql.append("select c.createdUser, count(*) from Candidate c");
		hql.append(" where 1=1 and c.deleteFlag=0 ");
		if (dateStart != null) {
			hql.append(" and c.createdOn >= :startDate");
			paramNames.add("startDate");
			paramValues.add(dateStart);
		}
		if (dateEnd != null) {
			hql.append(" and c.createdOn <= :endDate"); // as per ken modified
														// (Should it always use
														// the created date when
														// we select by date?)
			paramNames.add("endDate");
			paramValues.add(dateEnd);
		}
		if (user != null) {
			/*
			 * if (user.getUserRole().toString()
			 * .equals(UserRole.Manager.toString())) { log.info(" User Role" +
			 * user.getUserRole().toString()); hql.append(
			 * " and u.officeLocation='" + user.getOfficeLocation() + "'"); }
			 */}
		hql.append(" group by c.createdUser");

		List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

		Map<String, Integer> userMap = new TreeMap<String, Integer>();
		for (Object record : result) {
			Object[] tuple = (Object[]) record;
			String username = (String) tuple[0];
			Long count = (Long) tuple[1];
			String uName=null;
			if(username!=null && username.length()>1){
				uName = Character.toUpperCase(username.toLowerCase().charAt(0)) + username.toLowerCase().substring(1);
				uName = uName.replace(" ", "");
			}
			if (uName != null) {
				if(userMap.get(uName)!=null){
					userMap.put(uName,userMap.get(uName)+(count != null ? count.intValue() : 0));
				}else{
				userMap.put(uName, count != null ? count.intValue() : 0);
				}
			}
		}

		return userMap;
	}

	public List<?> getPortalCandidates(CandidateSearchDto viewDetails) {
		try {
			Date dateStart = null;
			Date dateEnd = null;
				dateStart = Utils.convertStringToDate(viewDetails.getStartDate());
				dateEnd = Utils.convertStringToDate(viewDetails.getEndDate());
				
			String portalName = viewDetails.getCreatedBy();
			/*
			 * int count = 0; count = getCandidatesCountBySql(viewDetails);
			 */

			final Map<String, Object> params = new HashMap<String, Object>();
			final StringBuilder hql = new StringBuilder();
			hql.append(
					"select  c.candidate_id, c.first_name,c.last_name,c.state,c.email,c.title as candidateTitle ,c.created_on,c.updated_on,c.status,c.visa_type,c.phone,c.city,c.hot,c.block, r.orig_document,c.created_by as portalInfo, count(*) OVER (PARTITION BY 1) as totalRecords from candidate c, resume r ");

			if (portalName.trim().toLowerCase().equalsIgnoreCase(Constants.CAREERBUILDER_NEW)
					|| portalName.trim().toLowerCase().equalsIgnoreCase(Constants.TECHFETCH_NEW)) {
				hql.append(
						" where r.candidate_id=c.candidate_id  and (ltrim(rtrim(c.created_by)) in (:portalName1) or ltrim(rtrim(c.created_by)) in (:portalName2))  and c.delete_flg=0 ");
			} else {
				hql.append(" where r.candidate_id=c.candidate_id  and ltrim(rtrim(c.created_by)) in (:portalName) and c.delete_flg=0 ");
			}
			if (portalName.trim().toLowerCase().equalsIgnoreCase(Constants.CAREERBUILDER_NEW)) {
				params.put("portalName1", Constants.CAREERBUILDER_NEW);
				params.put("portalName2", Constants.CAREERBUILDER_OLD);
			} else if (portalName.trim().toLowerCase().equalsIgnoreCase(Constants.TECHFETCH_NEW)) {
				params.put("portalName1", Constants.TECHFETCH_OLD);
				params.put("portalName2", Constants.TECHFETCH_NEW);
			} else {
				params.put("portalName", portalName.trim());
			}
			if (dateStart != null) {
				hql.append(" and c.created_on >= :startDate");
				// paramNames.add("startDate");
				// paramValues.add(dateStart);
				params.put("startDate", dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and c.created_on <= :endDate"); // as per ken
																// modified
																// (Should it
																// always
																// use
																// the created
																// date
																// when
																// we select by
																// date?)
				// paramNames.add("endDate");
				// paramValues.add(DateUtils.addDays(dateEnd, 1));

				params.put("endDate", DateUtils.addDays(dateEnd, 1));
			}
			/*
			 * if(viewDetails.getOrderByColumn() != null){ hql.append(
			 * " order by c.created_on DESC"); }
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
				hql.append(" order by COALESCE(c.updated_on,c.created_on) DESC");
			}
			/*
			 * else{ hql.append(" order by c.created_on DESC"); }
			 */
			hql.append(" LIMIT " + viewDetails.getMaxResults() + " OFFSET " + viewDetails.getStartPosition());

			List<?> result = findBySQLQuery(hql.toString(), params);
			log.info(" result size of view " + result.size());
			List<CandidateDto> candidateList = new ArrayList<>();
			log.info("size" + result.size());
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			if (result.size() > 0) {
				Iterator it = result.iterator();
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
					CandidateStatus status = null;
					final Integer statusId = (Integer) candidate[8];
					if (statusId != null) {
						for (CandidateStatus id : CandidateStatus.values()) {

							if (id.ordinal() == statusId) {
								status = id;
							}

						}
					}
					final String visaType = (String) candidate[9];
					final String phoneNumber = (String) candidate[10];
					final Object hot = candidate[12];
					final Object block = candidate[13];

					// final String jobOrderId = candidate[14].toString();
					final BigInteger resumeContent = (BigInteger) candidate[14];
					final String portalInfo = (String) candidate[15];
					log.info(" total recods count from query " + portalInfo);
					final String totalRecords = candidate[16].toString();
					log.info(" total recods count from query " + totalRecords);
					/*
					 * final String jobTitile = (String) candidate[17];
					 * 
					 * final String dmName = (String) candidate[18];
					 */
					byte[] longBytes = null;
					if (resumeContent != null) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						DataOutputStream dos = new DataOutputStream(baos);
						try {

							dos.writeLong(resumeContent.longValue());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							dos.close();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						longBytes = baos.toByteArray();
					}
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
					if (!Utils.isEmpty(longBytes))
						dto.setResumeContent(new String(longBytes));
					else
						dto.setResumeContent("");

					if (hot != null) {
						dto.setHot((boolean) hot);
					} else
						dto.setHot(false);
					if (block != null) {
						dto.setBlock((boolean) block);
					} else
						dto.setBlock(false);

					/*
					 * if (!Utils.isBlank(jobOrderId))
					 * dto.setJobOrderId(jobOrderId); else
					 * dto.setJobOrderId("");
					 */
					if (!Utils.isBlank(portalInfo))
						dto.setPortalInfo(portalInfo);
					else
						dto.setPortalInfo("");
					// if (!Utils.isBlank(jobTitile))
					// dto.setJobTitle(jobTitile);
					// else
					// dto.setJobTitle("");
					// if (!Utils.isBlank(dmName))
					// dto.setDmName(dmName);
					// else
					// dto.setDmName("");
					if (totalRecords != null)
						dto.setTotalRecords(totalRecords);

					candidateList.add(dto);
				}
			}
			log.info("candidateList" + candidateList.size());
			return (List<?>) candidateList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

	public int getCandidatesCountBySql(CandidateSearchDto viewDetails) {
		try {
			final SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
			Date dateStart = null;
			Date dateEnd = null;
			try {
				dateStart = formatter.parse(viewDetails.getStartDate());
				dateEnd = formatter.parse(viewDetails.getEndDate());
			} catch (Exception e) {
				e.printStackTrace();
			}
			String portalName = viewDetails.getCreatedBy();
			int count = 0;
			Map<String, Object> params = new HashMap<String, Object>();
			StringBuilder hql = new StringBuilder();
			hql.append("select count(*) from Candidate c,Resume r  where r.id=c.id ");
			hql.append("and ltrim(rtrim(c.createdUser)) in (:portalName) and c.deleteFlag=0 ");
			params.put("portalName", portalName.trim());

			if (dateStart != null) {
				hql.append("and c.createdOn >= :dateStart ");
				params.put("dateStart", dateStart);
			}
			if (dateEnd != null) {
				hql.append("and c.createdOn <= :dateEnd");
				params.put("dateEnd", dateEnd);
			}
			count = findCount(hql.toString(), params);
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
	 * com.uralian.cgiats.dao.CandidateDao#getCandidatesByUser(java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public Map<String, Integer> getCandidatesOnUpdatedDate(Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select updatedBy, count(*) from Candidate");
			hql.append(" where 1=1 and deleteFlag=0 ");
			if (dateStart != null) {
				hql.append(" and updatedOn >= :startDate");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and updatedOn <= :endDate");
				paramNames.add("endDate");
				paramValues.add(DateUtils.addDays(dateEnd, 1));
			}
			hql.append(" group by updatedBy");

			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			Map<String, Integer> userMap = new TreeMap<String, Integer>();
			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				Number count = (Number) tuple[1];
				if (username != null)
					userMap.put(username, count != null ? count.intValue() : 0);
			}

			return userMap;
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
	 * com.uralian.cgiats.dao.CandidateDao#getCandidatesByUser(java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public Map<String, Object> getPortalIdsByUser(Date dateStart, Date dateEnd, String portalName) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append(
					"select CASE WHEN trim(both ' ' from portalEmail)='' THEN 'NA' WHEN trim(both ' ' from portalEmail) is null THEN 'NA'  ELSE trim(both ' ' from portalEmail) END, count(*) from Candidate");
			hql.append(" where 1=1 and deleteFlag=0 ");
			if (dateStart != null) {
				hql.append(" and createdOn >= :startDate");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and createdOn <= :endDate"); // as per ken modified
															// (Should it always
															// use
															// the created date
															// when
															// we select by
															// date?)
				paramNames.add("endDate");
				paramValues.add(DateUtils.addDays(dateEnd, 1));
			}
			if (portalName != null) {
				hql.append(" and createdUser = :portalName"); // as per ken
																// modified
																// (Should it
																// always
																// use the
																// created
																// date when we
																// select by
																// date?)
				paramNames.add("portalName");
				paramValues.add(portalName);
			}
			hql.append(
					" group by CASE WHEN trim(both ' ' from portalEmail)='' THEN 'NA' WHEN trim(both ' ' from portalEmail) is null THEN 'NA'  ELSE trim(both ' ' from portalEmail) END ");
			hql.append(" order by CASE WHEN trim(both ' ' from portalEmail)='' THEN 'NA' WHEN trim(both ' ' from portalEmail) is null THEN 'NA'  ELSE trim(both ' ' from portalEmail) END ASC");
			// log.info("hql 170>>"+hql);
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			Map<String, Object> userMap = new HashMap<String, Object>();
			List<JobBoardStatsDto> statsList = new ArrayList<JobBoardStatsDto>();
			int total = 0;
			for (Object record : result) {
				JobBoardStatsDto stats = new JobBoardStatsDto();
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				Long count = (Long) tuple[1];
//				userMap.put(username != null ? username.trim() : "", count != null ? count.intValue() : 0);
				stats.setUsers(Utils.nullIfBlank(username));
				stats.setDownloadCount(Utils.nullIfBlank(String.valueOf(count)));
				stats.setMonthlyViews(String.valueOf(1500));
				statsList.add(stats);
				total += count;
			}
			userMap.put("diceStats", statsList);
			userMap.put("Total",total);
			return userMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.dao.CandidateDao#reindexCandidates(org.hibernate.
	 * search .batchindexing.MassIndexerProgressMonitor, boolean)
	 */
	@Override
	public Future<?> reindexCandidates(MassIndexerProgressMonitor monitor, boolean async) {
		try {

			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			FullTextSession fullTextSession = Search.getFullTextSession(session);
			MassIndexer indexer = fullTextSession.createIndexer();
			if (monitor != null)
				indexer.progressMonitor(monitor);

			try {
				if (async)
					return indexer.start();
				else {
					indexer.startAndWait();
					log.info("Candidate reindexing complete");
					return null;
				}
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException("Reindexing was interrupted");
			}
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
	 * com.uralian.cgiats.dao.CandidateDao#findByLuceneQuery(java.lang.String,
	 * int, int)
	 */

	public List findByLuceneQuery(String searchString, CandidateSearchDto criteria) {

		int firstResult = criteria.getStartPosition();
		int maxResults = criteria.getMaxResults();

		try {
			QueryParser parser = new QueryParser(Version.LUCENE_31, "resume", new StandardAnalyzer(Version.LUCENE_31));
			log.info("searchString::::" + searchString);
			org.apache.lucene.search.Query luceneQuery = parser.parse(searchString);
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			FullTextSession fullTextSession = Search.getFullTextSession(session);
			FullTextQuery ftQuery = fullTextSession.createFullTextQuery(luceneQuery, Candidate.class);
			ftQuery.setProjection("id", "firstName", "lastName", "state", "email", "title", "createdOn", "updatedOn", "status", "visaType", "phone", "city",
					"hot", "block", "keySkill", "reason");

			SortField sortField = null;

			log.info("filedName" + criteria.getFieldName() + "SortName" + criteria.getSortName());
			if (!Utils.isBlank(criteria.getFieldName()) && !Utils.isBlank(criteria.getSortName())) {
//				if (!criteria.getFieldName().equalsIgnoreCase(Constants.ID)) {
					int typeOfSortField = SortField.STRING;
					if (criteria.getFieldName().equalsIgnoreCase(Constants.UPDATEDON) ||
							criteria.getFieldName().equalsIgnoreCase(Constants.CREATEDON)) {
						typeOfSortField = SortField.LONG;
					}
					if (criteria.getFieldName().equalsIgnoreCase(Constants.ID)) {
						typeOfSortField = SortField.INT;
					}
					if (criteria.getSortName().toUpperCase().equals(OrderByType.DESC.getValue())) {
						sortField = new SortField(criteria.getFieldName(), typeOfSortField, true);
					} else {
						sortField = new SortField(criteria.getFieldName(), typeOfSortField, false);
					}
//				} else {
//					sortField = new SortField(Constants.UPDATEDON, SortField.LONG, true);
//				}
				ftQuery.setSort(new Sort(sortField));
			} else {
				sortField = new SortField(Constants.UPDATEDON, SortField.LONG, true);
				ftQuery.setSort(new Sort(sortField));
			}
			if (firstResult >= 0)
				ftQuery.setFirstResult(firstResult);
			if (maxResults > 0)
				ftQuery.setMaxResults(maxResults);

			List<?> result = ftQuery.list();

			List result1 = new ArrayList();
			List list = new ArrayList();

			if (result != null && result.size() > 0) {
				/*
				 * Iterator it = result.iterator(); while (it.hasNext()) {
				 * Object candidate[] = (Object[]) it.next(); try { final String
				 * email = (String) candidate[4]; final String firstName =
				 * (String) candidate[1]; final String lastName = (String)
				 * candidate[2]; StringBuffer fullName = new StringBuffer(); if
				 * (!Utils.isEmpty(firstName) || !Utils.isEmpty(lastName)) {
				 * StringBuffer name = new StringBuffer(firstName + "");
				 * fullName = name.append("  " + lastName);
				 * 
				 * } if (!Utils.isEmpty(fullName.toString()) &&
				 * !Utils.isEmpty(email)) { String str =
				 * fullName.toString().trim().concat(email.trim()); if (str !=
				 * null && !str.equals("") && email.length() > 0) { if
				 * (!list.contains(str.trim())) { list.add(str.trim());
				 * result1.add(candidate);
				 * 
				 * } } } } catch (Exception e) { e.printStackTrace();
				 * 
				 * } }
				 * 
				 */}
			log.info("Response Size" + result.size());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

	public int getCandidatesCount(String searchString, CandidateSearchDto criteria) {

		int firstResult = criteria.getStartPosition();
		int maxResults = criteria.getMaxResults();

		try {
			QueryParser parser = new QueryParser(Version.LUCENE_31, "resume", new StandardAnalyzer(Version.LUCENE_31));
			log.info("searchString::::" + searchString);
			org.apache.lucene.search.Query luceneQuery = parser.parse(searchString);
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			FullTextSession fullTextSession = Search.getFullTextSession(session);
			FullTextQuery ftQuery = fullTextSession.createFullTextQuery(luceneQuery, Candidate.class);
			ftQuery.setProjection("id", "firstName", "lastName", "state", "email", "title", "createdOn", "updatedOn", "status", "visaType", "phone", "city",
					"hot", "block", "parsedText");

			SortField sortField = null;

			String sortColumn = null;

			if (criteria.getOrderByColumn().equals(OrderByColumn.ID))
				sortColumn = "id";
			// else if(criteria.getOrderByColumn().equals(OrderByColumn.DATE))
			// sortColumn="createdOn";
			else if (criteria.getOrderByColumn().equals(OrderByColumn.FIRSTNAME))
				sortColumn = "firstName";
			else if (criteria.getOrderByColumn().equals(OrderByColumn.TITLE))
				sortColumn = "title";
			else if (criteria.getOrderByColumn().equals(OrderByColumn.LOCATION))
				sortColumn = "city";
			else if (criteria.getOrderByColumn().equals(OrderByColumn.BLACKLIST))
				sortColumn = "block";
			else if (criteria.getOrderByColumn().equals(OrderByColumn.FAVORITE))
				sortColumn = "hot";
			else if (criteria.getOrderByColumn().equals(OrderByColumn.DATE))
				sortColumn = "updatedOn";
			else if (criteria.getOrderByColumn().equals(OrderByColumn.CREATEDON))
				sortColumn = "createdOn";

			if (criteria.getOrderByType().equals(OrderByType.DESC)) {
				System.out.println("order::::desc" + sortColumn);
				sortField = new SortField(criteria.getFieldName(), SortField.STRING, true);
			} else {
				sortField = new SortField(criteria.getFieldName(), SortField.STRING, false);

			}
			ftQuery.setSort(new Sort(sortField));
			int count = ftQuery.getResultSize();
			if (count != 0)
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
	 * com.uralian.cgiats.dao.CandidateDao#findCountByLuceneQuery(java.lang.
	 * String )
	 */
	public int findCountByLuceneQuery(String searchString) {
		try {
			QueryParser parser = new QueryParser(Version.LUCENE_31, "resume", new StandardAnalyzer(Version.LUCENE_31));
			org.apache.lucene.search.Query luceneQuery = parser.parse(searchString);
			log.info("Search-------->");
			log.info("searchString:::::::::::::::" + searchString);
			Session session = getSessionFactory().getCurrentSession();
			FullTextSession fullTextSession = Search.getFullTextSession(session);
			FullTextQuery ftQuery = fullTextSession.createFullTextQuery(luceneQuery, Candidate.class);

			int size = ftQuery.getResultSize();
			log.info("Size---------->" + size);
			return size;
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
	 * com.uralian.cgiats.dao.CandidateDao#getCandidatesByUser(java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public Map<String, Map<String, Integer>> getdiceUsageCount(Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			StringBuilder hql = new StringBuilder();
			hql.append(
					"select CASE WHEN trim(both ' ' from c.portalViewedBy) ='' THEN 'NA' WHEN trim(both ' ' from c.portalViewedBy)  is null THEN 'NA'  ELSE trim(both ' ' from c.portalViewedBy)  END, ");
			hql.append(
					"CASE WHEN trim(both ' ' from c.portalEmail) ='' THEN 'NA' WHEN trim(both ' ' from c.portalEmail)  is null THEN 'NA'  ELSE trim(both ' ' from c.portalEmail)  END,");
			hql.append(" count(*) from Candidate c ");
			hql.append(" where 1=1 and c.createdUser='Dice' and deleteFlag=0");
			if (dateStart != null) {
				hql.append(" and c.createdOn >= :startDate");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and c.createdOn <= :endDate");
				paramNames.add("endDate");
				paramValues.add(DateUtils.addDays(dateEnd, 1));
			}
			hql.append(" group by trim(both ' ' from c.portalViewedBy) ,trim(both ' ' from c.portalEmail)  order by trim(both ' ' from c.portalViewedBy) ");

			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			Map<String, Map<String, Integer>> userMap = new TreeMap<String, Map<String, Integer>>();
			for (Object record : result) {
				Object[] tuple = (Object[]) record;

				String viewedBy = (String) tuple[0];
				String portalemail = (String) tuple[1];
				Number count = (Number) tuple[2];
				Map<String, Integer> statusMap = userMap.get(viewedBy);
				if (statusMap == null) {
					statusMap = new HashMap<String, Integer>();
					userMap.put(viewedBy, statusMap);
				}
				statusMap.put(portalemail, count != null ? count.intValue() : 0);
			}

			return userMap;
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
	 * com.uralian.cgiats.dao.CandidateDao#getportalResumes(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Map<String, Integer>> getportalResumes(String portalSelect, String dateStart, String dateEnd, String status) {
		try {
			Map<String, Map<String, Integer>> userMap = new TreeMap<String, Map<String, Integer>>();
			StringBuilder hql = new StringBuilder();
			List<?> result = null;
			hql.append(
					"select  CASE WHEN c.created_by in ('Dice','Monster','Careerbuilder','CGI','Sapeare','RedGalaxy') THEN c.created_by ELSE 'OTHERS' END,ltrim(rtrim(to_char(s.created_on, 'MONTH'))), count(CASE WHEN c.created_by in ('Dice','Monster','Careerbuilder','CGI','Sapeare','RedGalaxy') THEN c.created_by ELSE c.created_by END) from candidate c,submittal s  where s.candidate_id=c.candidate_id and c.status>=0");

			if (portalSelect != null && portalSelect.trim().equalsIgnoreCase("Dice")) {
				hql.append("and trim(both '' from c.created_by)='Dice'");
			} else if (portalSelect != null && portalSelect.trim().equalsIgnoreCase("Monster")) {
				hql.append("and trim(both '' from c.created_by)='Monster'");
			} else if (portalSelect != null && portalSelect.trim().equalsIgnoreCase("Careerbuilder")) {
				hql.append("and trim(both '' from c.created_by)='Careerbuilder'");
			} else if (portalSelect != null && portalSelect.trim().equalsIgnoreCase("CGI")) {
				hql.append("and trim(both '' from c.created_by)='CGI'");
			} else if (portalSelect != null && portalSelect.trim().equalsIgnoreCase("Sapeare")) {
				hql.append("and trim(both '' from c.created_by)='Sapeare'");
			} else if (portalSelect != null && portalSelect.trim().equalsIgnoreCase("RedGalaxy")) {
				hql.append("and trim(both '' from c.created_by)='RedGalaxy'");
			}

			if (dateEnd != null) {
				hql.append(" and EXTRACT(YEAR FROM  s.created_on) =?");
			}
			if (status != null && !status.equalsIgnoreCase("all") && dateStart == null) {
				hql.append(" and ltrim(rtrim(s.status)) =?");
			}
			if (status != null && status.equalsIgnoreCase("all") && dateStart != null) {
				hql.append(" and ltrim(rtrim( to_char(s.created_on, 'MONTH'))) =?");
			}
			if (status != null && !status.equalsIgnoreCase("all") && dateStart != null) {
				hql.append(" and ltrim(rtrim(s.status)) =? and ltrim(rtrim( to_char(s.created_on, 'MONTH'))) =?");
			}
			hql.append(
					" group by CASE WHEN c.created_by in ('Dice','Monster','Careerbuilder','CGI','Sapeare','RedGalaxy') THEN c.created_by ELSE 'OTHERS' END,ltrim(rtrim(to_char(s.created_on, 'MONTH'))),EXTRACT(MONTH FROM  s.created_on) order by CASE WHEN c.created_by in ('Dice','Monster','Careerbuilder','CGI','Sapeare','RedGalaxy') THEN c.created_by ELSE 'OTHERS' END");
			result = findBySqlQueryStr(hql.toString(), dateStart, dateEnd != null ? Integer.parseInt(dateEnd) : 0, status);
			int finalCount = 0;
			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String portalName = (String) tuple[0];
				String month = (String) tuple[1];
				Number count = (Number) tuple[2];

				Map<String, Integer> statusMap = userMap.get(portalName);
				if (statusMap == null) {
					statusMap = new HashMap<String, Integer>();

				}
				userMap.put(portalName + "|" + month, statusMap);
				if (statusMap.containsKey(month) && count != null) {
					finalCount = statusMap.get(month) + count.intValue();
					statusMap.put(month, finalCount);
				} else {
					statusMap.put(month, count != null ? count.intValue() : 0);
				}
			}
			return userMap;
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
	 * com.uralian.cgiats.dao.CandidateDao#getportalCandidates(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public List<Candidate> getportalCandidates(String portalSelect, String month, String year, String status) {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(
					"select c.candidate_id,c.first_name,c.title,c.city,c.state,c.email,s.status from  candidate c,submittal s where c.candidate_id=s.candidate_id ");
			List<?> result = null;
			if (portalSelect != null && portalSelect.trim().equalsIgnoreCase("Dice")) {
				hql.append("and trim(both '' from c.created_by)='Dice'");
			} else if (portalSelect != null && portalSelect.trim().equalsIgnoreCase("Monster")) {
				hql.append("and trim(both '' from c.created_by)='Monster'");
			} else if (portalSelect != null && portalSelect.trim().equalsIgnoreCase("Careerbuilder")) {
				hql.append("and trim(both '' from c.created_by)='Careerbuilder'");
			} else if (portalSelect != null && portalSelect.trim().equalsIgnoreCase("CGI")) {
				hql.append("and trim(both '' from c.created_by)='CGI'");
			} else {
				hql.append("and trim(both '' from c.created_by) NOT IN ('Dice','Monster','Careerbuilder','CGI')");
			}

			if (year != null) {
				hql.append("and EXTRACT(YEAR FROM  s.created_on) = ?");
			}
			if (month != null) {
				hql.append("and ltrim(rtrim(TO_CHAR(s.created_on, 'MONTH')))  = ?");
			}
			/*
			 * if(status!=null && !status.trim().equals("") &&
			 * status.equalsIgnoreCase("SUBMITTED")){ hql.append(
			 * " and ltrim(rtrim(s.status)) ='SUBMITTED'"); }
			 */
			hql.append("order by c.created_on desc");
			result = findBySqlQueryStr(hql.toString(), month, year != null ? Integer.parseInt(year) : 0, "all");
			Iterator ie = result.iterator();
			List<Candidate> canLst = new ArrayList<Candidate>();
			Candidate c = null;
			while (ie.hasNext()) {
				c = new Candidate();
				Object[] s = (Object[]) ie.next();
				if (s[6] != null && s[6].toString().equalsIgnoreCase("Submitted")) {
					c.setId(Integer.parseInt(s[0] != null ? s[0].toString() : null));
					c.setFirstName(s[1] != null ? s[1].toString() : null);
					c.setTitle(s[2] != null ? s[2].toString() : null);
					Address a = new Address();
					a.setCity(s[3] != null ? s[3].toString() : null);
					a.setState(s[4] != null ? s[4].toString() : null);
					c.setEmail(s[5] != null ? s[5].toString() : null);
					c.setAddress(a);
					canLst.add(c);
				}
			}
			return (List<Candidate>) canLst;
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
			StringBuffer hql = new StringBuffer();
			// hql.append("select s.order_id,c.candidate_id,c.first_name ||
			// c.last_name as name,c.email,s.status, CASE WHEN c.created_by in
			// ('Dice','Monster','Careerbuilder') THEN c.created_by ELSE
			// 'OTHERS'
			// END,COALESCE(s.updated_by,s.created_by),
			// COALESCE(s.updated_on,s.created_on) from candidate c,submittal s
			// where c.delete_flg=0");
			hql.append(
					"select s.order_id,c.candidate_id,c.first_name || c.last_name as name,c.email,s.status, CASE WHEN c.created_by in ('Dice','Monster','Careerbuilder','CGI') THEN c.created_by ELSE 'OTHERS' END,s.created_by, s.created_on from candidate c,submittal s where c.candidate_id=s.candidate_id and c.status>=0");
			List<?> result = null;
			if (portalSelect != null && portalSelect.trim().equalsIgnoreCase("Dice")) {
				hql.append("and trim(both '' from c.created_by)='Dice'");
			} else if (portalSelect != null && portalSelect.trim().equalsIgnoreCase("Monster")) {
				hql.append("and trim(both '' from c.created_by)='Monster'");
			} else if (portalSelect != null && portalSelect.trim().equalsIgnoreCase("Careerbuilder")) {
				hql.append("and trim(both '' from c.created_by)='Careerbuilder'");
			} else if (portalSelect != null && portalSelect.trim().equalsIgnoreCase("CGI")) {
				hql.append("and trim(both '' from c.created_by)='CGI'");
			} else {
				hql.append("and trim(both '' from c.created_by) NOT IN ('Dice','Monster','Careerbuilder','CGI')");
			}

			if (year != null) {
				hql.append("and EXTRACT(YEAR FROM  s.created_on) = ?");
			}
			if (month != null) {
				hql.append("and ltrim(rtrim(TO_CHAR(s.created_on, 'MONTH')))  = ?");
			}
			if (status != null && !status.trim().equals("") && !status.equalsIgnoreCase("all")) {
				hql.append(" and ltrim(rtrim(s.status)) =?");
			}
			hql.append("order by c.created_on desc");
			result = findBySqlQueryAllPortal(hql.toString(), month, year != null ? Integer.parseInt(year) : 0, status);
			Iterator ie = result.iterator();
			List<AllPortalResumes> canLst = new ArrayList<AllPortalResumes>();
			Candidate c = null;
			while (ie.hasNext()) {
				AllPortalResumes allPortalResumes = new AllPortalResumes();
				Object[] s = (Object[]) ie.next();
				allPortalResumes.setOrderId(s[0] != null ? s[0].toString() : null);
				allPortalResumes.setCandidateId(s[1] != null ? s[1].toString() : null);
				allPortalResumes.setFullName(s[2] != null ? s[2].toString() : null);
				allPortalResumes.setEmail(s[3] != null ? s[3].toString() : null);
				allPortalResumes.setStatus(s[4] != null ? s[4].toString() : null);
				allPortalResumes.setCanCreatedBy(s[5] != null ? s[5].toString() : null);
				allPortalResumes.setSubCreatedBy(s[6] != null ? s[6].toString() : null);
				Date d = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss.S");
				try {
					d = formatter.parse(s[7] != null ? s[7].toString() : null);
				} catch (ParseException e) {
					e.printStackTrace();
					log.error(e.getMessage(),e);
				}

				allPortalResumes.setSubCreatedOn(d);

				canLst.add(allPortalResumes);
			}
			return (List<AllPortalResumes>) canLst;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Integer> getAllCandidatesCounts(Date dateStart, Date dateEnd) {
		log.info("From getAllCandidatesCounts" + dateStart + "dateEnd" + dateEnd);
		List<String> paramNames = new ArrayList<String>();
		List<Object> paramValues = new ArrayList<Object>();
		StringBuilder hql = new StringBuilder();
		hql.append(
				"select case when (c.createdUser not in('Dice','Careerbuilder','Career Builder','Tech Fetch','Monster','Techfetch') or c.createdUser is NULL) then 'Others' else c.createdUser end,count(*) from Candidate c ");
		hql.append(" where 1=1 and c.deleteFlag=0");
		if (dateStart != null) {
			hql.append(" and cast(c.createdOn as date) >=  cast(:startDate as date)");
			paramNames.add("startDate");
			paramValues.add(dateStart);
		}
		if (dateEnd != null) {
			hql.append(" and cast(c.createdOn as date) <= cast(:endDate as date)");
			paramNames.add("endDate");
			paramValues.add(dateEnd);
		}
		hql.append(
				"  group by case when (c.createdUser not in('Dice','Careerbuilder','Career Builder','Tech Fetch','Monster','Techfetch') or c.createdUser is NULL) then 'Others' else c.createdUser end");
		List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
		log.info("Result" + result.size());
		log.info("sql" + hql);
		Map<String, Integer> userMap = new TreeMap<String, Integer>();
		for (Object record : result) {
			Object[] tuple = (Object[]) record;
			String username = (String) tuple[0];
			Long count = (Long) tuple[1];
			String uName=null;
			if(username!=null){
				uName = Character.toUpperCase(username.toLowerCase().charAt(0)) + username.toLowerCase().substring(1);
				uName = uName.replace(" ", "");
			}
			if (uName != null) {
				if(userMap.get(uName)!=null){
					userMap.put(uName,userMap.get(uName)+(count != null ? count.intValue() : 0));
				}else{
				userMap.put(uName, count != null ? count.intValue() : 0);
				}
			}
		}
		return userMap;
	}

	public List<Integer> getAllResumesCounts() {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			StringBuilder hql = new StringBuilder();
			hql.append("select count(*) from Candidate c where c.deleteFlag=0");
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			return (List<Integer>) result;
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
	 * com.uralian.cgiats.dao.CandidateDao#saveSearchCandidateAuditDetails(com
	 * .uralian.cgiats.model.CadidateSearchAudit)
	 */
	@Override
	public void saveSearchCandidateAuditDetails(CadidateSearchAudit candidateSearchVo) {
		try {
			Session session = getSessionFactory().getCurrentSession();
			session.saveOrUpdate(candidateSearchVo);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.dao.CandidateDao#getSearchCandidateAudit(java.util
	 * .Date, java.util.Date)
	 */
	@Override
	public List<CadidateSearchAudit> getSearchCandidateAudit(Date fromDate, Date toDate, String userName, String queryName) {
		Session session = null;
		Query qry = null;
		String hqlQuery = "from CadidateSearchAudit where to_date(to_char(createdOn, 'YYYY/MM/DD'), 'YYYY/MM/DD') >=:fromDate and to_date(to_char(createdOn, 'YYYY/MM/DD'), 'YYYY/MM/DD') <=:toDate ";
		try {
			session = getHibernateTemplate().getSessionFactory().getCurrentSession();

			java.sql.Date frDate = new java.sql.Date(fromDate.getTime());
			java.sql.Date tDate = new java.sql.Date(toDate.getTime());

			if (!Utils.isBlank(userName)) {
				hqlQuery = hqlQuery.concat(" and createdBy=:createdBy");
			}

			if (!Utils.isBlank(queryName)) {
				hqlQuery = hqlQuery.concat(" and queryName=:queryName");
			}

			hqlQuery = hqlQuery.concat(" order by createdOn desc");
			qry = session.createQuery(hqlQuery);
			qry.setParameter("fromDate", frDate);
			qry.setParameter("toDate", tDate);
			if (!Utils.isBlank(userName))
				qry.setParameter("createdBy", userName);
			if (!Utils.isBlank(queryName) && queryName.length() > 0)
				qry.setParameter("queryName", queryName);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		log.info("SIze" + qry.list().size());

		return Utils.isEmpty(qry.list()) ? new ArrayList<CadidateSearchAudit>() : qry.list();
	}

	@Override
	public Map<String, Integer> getSearchCandidatesByUser(Date dateStart, Date dateEnd, User user) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select c.createdBy, count(*) from CadidateSearchAudit c, User u");
			hql.append(" where 1=1  and c.createdBy=u.userId");
			if (dateStart != null) {
				hql.append(" and c.createdOn >= :startDate");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				hql.append(" and c.createdOn <= :endDate");
				paramNames.add("endDate");
				paramValues.add(DateUtils.addDays(dateEnd, 1));
			}
			if (user.getUserRole().toString().equals(UserRole.Manager.toString())) {
				hql.append(" and u.officeLocation='" + user.getOfficeLocation() + "'");
			}
			hql.append(" group by c.createdBy");

			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
			log.info("Candidate Search=======>" + hql.toString());
			Map<String, Integer> userMap = new TreeMap<String, Integer>();
			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				Number count = (Number) tuple[1];
				if (username != null) {
					userMap.put(username, count != null ? count.intValue() : 0);
				}
			}

			return userMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<Candidate> findCandidatesBasedOnStatus(StringBuffer sqlQuery, int first, int pageSize) {
		try {
			SQLQuery qry = getHibernateTemplate().getSessionFactory().getCurrentSession().createSQLQuery(sqlQuery.toString());
			qry.addEntity(Candidate.class);
			if (first > 0 && pageSize > 0) {
				qry.setFirstResult(first);
				qry.setMaxResults(pageSize);
			}
			List<Candidate> candidates = qry.list();

			return candidates;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void updateCandidateStatus(CandidateStatuses status) {
		try {
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			session.saveOrUpdate(status);
			log.info("Candidate Status" + status.getStatus().toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public CandidateStatuses getCandidateStatus(Candidate id) {
		try {
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

			Query query = session.createQuery("from CandidateStatuses c where c.candidate.id=:id and c.status=:status ORDER BY id DESC");
			query.setParameter("id", id.getId());
			query.setParameter("status", id.getStatus());
			query.setFirstResult(0);
			query.setMaxResults(0);
			List<CandidateStatuses> cs = query.list();
			if (!cs.isEmpty()) {
				return cs.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	public void updateStatus(CandidateStatuses stauses) {

		try {
			log.info("Reason" + stauses.getReason1() + "Candidate" + stauses.getId());
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			Query query = session.createQuery("update CandidateStatuses c set c.reason=:reason where c.id=:id and c.status=:status");
			query.setParameter("status", stauses.getStatus());
			query.setParameter("reason", stauses.getReason());
			query.setParameter("id", stauses.getId());
			query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.dao.CandidateDao#getCandidatesList(com.uralian.cgiats.
	 * dto.SearchCriteria)
	 */
	@Override
	public List getCandidatesList(final SearchCriteria searchCriteria) {
		List result = null;
		try {
			final Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			final FullTextSession fullTextSession = org.hibernate.search.Search.getFullTextSession(session);
			final QueryBuilder qb = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Candidate.class).get();

			org.apache.lucene.search.Query luceneQuery = qb.keyword().onFields("firstName", "lastName", "email", "title", "keySkill", "skills", "visaType")
					.matching(searchCriteria.getKeyWords()).createQuery();

			org.hibernate.search.FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(luceneQuery, Candidate.class);

			fullTextQuery.setProjection("id", "firstName", "lastName", "address.city", "email", "title", "createdOn", "updatedOn");
			result = fullTextQuery.list();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.dao.CandidateDao#getSubmittalsInfoByCandidateId(java.
	 * lang.Integer)
	 */
	@Override
	public List<?> getSubmittalsInfoByCandidateId(final Integer canId) {

		try {
			final StringBuffer sqlQuery = new StringBuffer(
					"select s.status, s.submittal_id,job.order_id,s.created_on, s.created_by as recName from candidate c,submittal s,job_order job  where c.candidate_id = s.candidate_id and s.order_id = job.order_id and c.candidate_id = :candidateId and s.delete_flag = 0");

			final Map<String, Object> param = new HashMap<String, Object>();
			param.put("candidateId", canId);

			return findBySQLQuery(sqlQuery.toString(), param);
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
	 * com.uralian.cgiats.dao.CandidateDao#deleteSavedQuery(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public int deleteSavedQuery(String qry, Map<String, Object> params) {

		try {
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

			Query query = session.createQuery(qry);

			for (Map.Entry<String, Object> param : params.entrySet()) {

				query.setParameter(param.getKey(), param.getValue());

			}

			return query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return 0;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.dao.CandidateDao#getSavedQuery(int)
	 */
	@Override
	public CadidateSearchAudit getSavedQuery(String query, Map<String, Object> params) {
		try {
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			Query qry = session.createQuery("from CadidateSearchAudit c where c.candidateSearchId=:candidateSearchId");
			for (Map.Entry<String, Object> param : params.entrySet()) {

				qry.setParameter(param.getKey(), param.getValue());

			}

			List<CadidateSearchAudit> result = qry.list();
			if (!Utils.isEmpty(result))
				return result.get(0);
			else
				return null;
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
	 * com.uralian.cgiats.dao.CandidateDao#getResumeByCandidateId(java.lang.
	 * String)
	 */
	@Override
	public ResumeDto getResumeByCandidateId(String candidateId) {
		try {
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

			Query query = session.createQuery("select r.document,r.candidate.documentType,r.parsedText,r.rtrDocument,r.candidate.rtrDocumentType,"
					+ "r.processedDocument,r.candidate.processedDocumentType  from Resume r where r.id=:id");
			log.info("resumeId" + candidateId);
			query.setParameter("id", Integer.parseInt(candidateId));
			List<?> list = query.list();
			ResumeDto resumeDto = new ResumeDto();
			if (!Utils.isEmpty(list)) {
				// String resumeContent = (String) list.get(0);
				// log.info("resume" + resumeContent + "Size" + list.size() +
				// "list"
				// + list);

				Iterator<?> iterator = list.iterator();
				while (iterator.hasNext()) {
					Object[] obj = (Object[]) iterator.next();
					resumeDto.setOriginalDoc((byte[]) obj[0]);
					resumeDto.setOriginalDocType((ContentType) obj[1]);
					resumeDto.setResumeContent((String) obj[2]);
					resumeDto.setRtrDocumentDoc((byte[]) obj[3]);
					resumeDto.setRtrDocumentType((ContentType) obj[4]);

					resumeDto.setProcessedDocument((byte[]) obj[5]);
					resumeDto.setProcessedDocumentType((ContentType) obj[6]);

				}

			}

			return resumeDto;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	public CandidateVo getAllUserDetails() {
		try {
			CandidateVo candidateVo = new CandidateVo();
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			Query userQuery = session.createQuery("select u.userId from User u where u.status = 'ACTIVE' group by u.userId order by u.userId ASC");
			Query portalQuery = session.createQuery("select p.portalUserId from PortalCredentials p group by p.portalUserId order by p.portalUserId ASC");
			List<String> userId = userQuery.list();
			List<String> portalId = portalQuery.list();
			log.info("user id size " + userId.size());
			log.info("final list " + portalId.size());
			candidateVo.setUserIds(userId);
			candidateVo.setPortalEmails(portalId);
			candidateVo.setUploaded(userId);
			return candidateVo;
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
	 * com.uralian.cgiats.dao.CandidateDao#isCandidateEmailExists(java.lang.
	 * Integer)
	 */
	@Override
	public Candidate getCandidateByEmail(String emailId) {
		try {
			Candidate candidate = null;
			StringBuffer hql = new StringBuffer();
			hql.append("select c.id,c.email from Candidate c");
			hql.append(" where c.email = :emailId and c.deleteFlag=0 ");

			List<?> result = findByQuery(hql.toString(), "emailId", emailId);
			if (result != null && result.size() > 0) {
				Iterator<?> iterator = result.iterator();
				while (iterator.hasNext()) {
					Object[] obj = (Object[]) iterator.next();
					candidate = new Candidate();
					candidate.setId((Integer) obj[0]);
					candidate.setEmail((String) obj[1]);
				}
			}
			return candidate;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<CandidateStatusesDto> getCandidateStatusListByCandidateId(Integer candidateId) {
		try {
			StringBuffer hqlQuery = new StringBuffer();
			hqlQuery.append("select c.status,c.reason, c.createdDate from CandidateStatuses  c where c.candidate.id = :id order By c.createdDate DESC");
			List<?> result = findByQuery(hqlQuery.toString(), "id", candidateId);
			List<CandidateStatusesDto> statusList = new ArrayList<CandidateStatusesDto>();
			if (result != null) {
				Iterator<?> itr = result.iterator();
				while (itr.hasNext()) {
					Object candidateStatus[] = (Object[]) itr.next();
					CandidateStatusesDto statusDto = new CandidateStatusesDto();
					statusDto.setStatus(Utils.getStringValueOfObj(candidateStatus[0]));
					byte[] reason = (byte[]) candidateStatus[1];
					String statusReason = null;
					if (reason != null) {
						statusReason = new String(reason);
					}
					statusDto.setReason(Utils.replaceNullWithEmpty(statusReason));
					Date created = (Date) candidateStatus[2];
					statusDto.setCreatedOn(Utils.convertDateToString(created));
					statusList.add(statusDto);
				}
			}
			return statusList;
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
	 * com.uralian.cgiats.dao.CandidateDao#getAllResumesCounts(java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public Long getAllResumesCounts(Date fromDate, Date toDate) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			StringBuilder hql = new StringBuilder();
			hql.append("select count(*) from Candidate c where c.deleteFlag=0 and c.createdOn >= ?1 and c.createdOn <= ?2");
			paramNames.add("1");
			paramNames.add("2");
			paramValues.add(fromDate);
			paramValues.add(toDate);
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
			Long totalRecords = null;
			if (result != null) {
				totalRecords = (Long) result.get(0);
			}
			return totalRecords;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Object> getJobBoardStats(Date startDate, Date endDate) {
		List<String> paramNames = new ArrayList<String>();
		List<Object> paramValues = new ArrayList<Object>();
		StringBuilder hql = new StringBuilder();
		hql.append("select c.createdUser, count(*) from Candidate c");
		hql.append(" where c.createdUser in('Dice','Monster','Careerbuilder','Techfetch', 'Career Builder', 'Tech Fetch') ");
		hql.append(" and 1=1 and c.deleteFlag=0 ");
		if (startDate != null) {
			hql.append(" and c.createdOn >= :startDate");
			paramNames.add("startDate");
			paramValues.add(startDate);
		}
		if (endDate != null) {
			hql.append(" and c.createdOn <= :endDate"); // as per ken modified
														// (Should it always use
														// the created date when
														// we select by date?)
			paramNames.add("endDate");
			paramValues.add(DateUtils.addDays(endDate, 1));
		}
		 
		hql.append(" group by c.createdUser order by c.createdUser DESC");

		List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
		

		Map<String, Object> usersMap = new HashMap<String, Object>();
		Map<String, Integer> userMap = new HashMap<String, Integer>();
		if(!Utils.isEmpty(result)){
		int total = 0;
		for (Object record : result) {
			Object[] tuple = (Object[]) record;
			String username = (String) tuple[0];
			Long count = (Long) tuple[1];
			String uName=null;
			if(username!=null && username.length()>1){
				uName = Character.toUpperCase(username.toLowerCase().charAt(0)) + username.toLowerCase().substring(1);
				uName = uName.replace(" ", "");
			}
			if (uName != null) {
				if(userMap.get(uName)!=null){
					userMap.put(uName,userMap.get(uName)+(count != null ? count.intValue() : 0));
				}else{
				userMap.put(uName, count != null ? count.intValue() : 0);
				}
			}
			
			total += count;
		}
		usersMap.put("jobBoardStats", getStatsList(userMap));
		usersMap.put("Total", total);
		}
		return usersMap;
	}

	 
	 

	

	@Override
	public Map<String, Object> getResumeStats(Date dateStart, Date dateEnd, User user) {
		List<String> paramNames = new ArrayList<String>();
		List<Object> paramValues = new ArrayList<Object>();

		StringBuilder hql = new StringBuilder();
		hql.append("select c.createdUser, count(*) from Candidate c");
		hql.append(" where 1=1 and c.deleteFlag=0 ");
		if (dateStart != null) {
			hql.append(" and c.createdOn >= :startDate");
			paramNames.add("startDate");
			paramValues.add(dateStart);
		}
		if (dateEnd != null) {
			hql.append(" and c.createdOn <= :endDate"); // as per ken modified
			paramNames.add("endDate");
			paramValues.add(DateUtils.addDays(dateEnd, 1));
		}
		if (user != null) {
			/*
			 * if (user.getUserRole().toString()
			 * .equals(UserRole.Manager.toString())) { log.info(" User Role" +
			 * user.getUserRole().toString()); hql.append(
			 * " and u.officeLocation='" + user.getOfficeLocation() + "'"); }
			 */}
		hql.append(" group by c.createdUser order by c.createdUser ASC");

		List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

		Map<String, Object> userMap = new HashMap<String, Object>();
		Map<String, Integer> usersMap = new HashMap<String, Integer>();
		List<JobBoardStatsDto> statsList = new ArrayList<JobBoardStatsDto>();
		int total = 0;
		for (Object record : result) {
			JobBoardStatsDto stats = new JobBoardStatsDto();
			Object[] tuple = (Object[]) record;
			String username = (String) tuple[0];
			Long count = (Long) tuple[1];
			String uName=null;
			if(count != null && Utils.isBlank(username)){
				username = "NA";
			}
			if(username!=null && username.length()>1){
				uName = Character.toUpperCase(username.toLowerCase().charAt(0)) + username.toLowerCase().substring(1);
				uName = uName.replace(" ", "");
			}
			if (uName != null) {
				if(usersMap.get(uName)!=null){
					usersMap.put(uName,usersMap.get(uName)+(count != null ? count.intValue() : 0));
				}else{
				usersMap.put(uName, count != null ? count.intValue() : 0);
				}
			}
			
			total += count;
			
		}
		userMap.put("resumeStats", getStatsList(usersMap));
		userMap.put("total", total);
		return userMap;
	}
	
	private List<JobBoardStatsDto> getStatsList(Map<String, Integer> userMap) {
		List<JobBoardStatsDto> statsList = new ArrayList<JobBoardStatsDto>();
		
		for(Map.Entry<String, Integer> entry : userMap.entrySet()){
			JobBoardStatsDto stats = new JobBoardStatsDto();
			stats.setUsers(Utils.nullIfBlank(entry.getKey()));
			stats.setDownloadCount( entry.getValue().toString());
			 statsList.add(stats);
		}
		Collections.sort(statsList, new Comparator<JobBoardStatsDto>() {
			@Override
			public int compare(JobBoardStatsDto dto1, JobBoardStatsDto dto2) {
				return dto1.getUsers().compareTo(dto2.getUsers());
			}
		});
		return statsList;
	}
}
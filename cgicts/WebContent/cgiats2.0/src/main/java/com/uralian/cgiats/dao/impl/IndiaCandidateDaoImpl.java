package com.uralian.cgiats.dao.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Future;

import org.apache.commons.lang.time.DateUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Version;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.Search;
import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.IndiaCandidateDao;
import com.uralian.cgiats.dto.IndiaCandidateDto;
import com.uralian.cgiats.dto.ResumeDto;
import com.uralian.cgiats.model.Address;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.CandidateStatuses;
import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.model.IndiaCandidate;
import com.uralian.cgiats.model.IndiaCandidateStatuses;
import com.uralian.cgiats.model.OrderByColumn;
import com.uralian.cgiats.model.OrderByType;
import com.uralian.cgiats.util.AllPortalResumes;
import com.uralian.cgiats.util.Utils;

@Repository
@SuppressWarnings("unchecked")
public class IndiaCandidateDaoImpl extends IndiaGenericDaoImpl<IndiaCandidate, Integer> implements IndiaCandidateDao {

	public IndiaCandidateDaoImpl() {
		super(IndiaCandidate.class);
	}

	@Override
	public List<String> findAllTitles() {
		try {
			String hql = "select distinct c.title from IndiaCandidate c where c.title is not null order by c.title";
			List<String> list = (List<String>) runQuery(hql.toString());
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Integer> getCandidatesByUser(Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select createdUser, count(*) from IndiaCandidate");
			hql.append(" where 1=1 and deleteFlag=0 ");
			if (dateStart != null) {
				hql.append(" and createdOn >= :startDate");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {
				// as per ken modified (Should it always use the created date
				// when we select by date?)
				hql.append(" and createdOn <= :endDate");
				paramNames.add("endDate");
				paramValues.add(DateUtils.addDays(dateEnd, 1));
			}
			hql.append(" group by createdUser");

			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			Map<String, Integer> userMap = new TreeMap<String, Integer>();
			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				Number count = (Number) tuple[1];
				userMap.put(username, count != null ? count.intValue() : 0);
			}

			return userMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<Candidate> getPortalCandidates(Date dateStart, Date dateEnd, String portalName) {
		try {

			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append(" from Candidate c ");
			hql.append(" where ltrim(rtrim(c.createdUser))=:portalName and c.deleteFlag=0 ");
			paramNames.add("portalName");
			paramValues.add(portalName.trim());
			if (dateStart != null) {
				hql.append(" and c.createdOn >= :startDate");
				paramNames.add("startDate");
				paramValues.add(dateStart);
			}
			if (dateEnd != null) {// as per ken modified (Should it always use
									// the created date when we select by date?)
				hql.append(" and c.createdOn <= :endDate");
				paramNames.add("endDate");
				paramValues.add(DateUtils.addDays(dateEnd, 1));
			}
			hql.append(" order by c.createdOn DESC");

			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
			return (List<Candidate>) result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	public Map<String, Integer> getCandidatesOnUpdatedDate(Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select updatedBy, count(*) from IndiaCandidate");
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

	@Override
	public Map<String, Integer> getPortalIdsByUser(Date dateStart, Date dateEnd, String portalName) {
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
				// as per ken modified (Should it always use the created date
				// when we select by date?)
				hql.append(" and createdOn <= :endDate");
				paramNames.add("endDate");
				paramValues.add(DateUtils.addDays(dateEnd, 1));
			}
			if (portalName != null) {
				// as per ken modified (Should it always use the created date
				// when we select by date?)
				hql.append(" and createdUser = :portalName");
				paramNames.add("portalName");
				paramValues.add(portalName);
			}
			hql.append(
					" group by CASE WHEN trim(both ' ' from portalEmail)='' THEN 'NA' WHEN trim(both ' ' from portalEmail) is null THEN 'NA'  ELSE trim(both ' ' from portalEmail) END");
			// log.info("hql 170>>"+hql);
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			Map<String, Integer> userMap = new TreeMap<String, Integer>();
			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String username = (String) tuple[0];
				Number count = (Number) tuple[1];
				userMap.put(username != null ? username.trim() : "", count != null ? count.intValue() : 0);
			}

			return userMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Future<?> reindexCandidates(MassIndexerProgressMonitor monitor, boolean async) {
		try {
			Session session = getSessionFactory().getCurrentSession();
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

	public List<IndiaCandidate> findByLuceneQuery(String searchString, CandidateSearchDto criteria) {
		try {
			int firstResult = criteria.getStartPosition();
			int maxResults = criteria.getMaxResults();

			QueryParser parser = new QueryParser(Version.LUCENE_31, "resume", new StandardAnalyzer(Version.LUCENE_31));
			log.info("searchString::::" + searchString);
			org.apache.lucene.search.Query luceneQuery = parser.parse(searchString);
			Session session = getSessionFactory().getCurrentSession();
			FullTextSession fullTextSession = Search.getFullTextSession(session);
			FullTextQuery ftQuery = fullTextSession.createFullTextQuery(luceneQuery, Candidate.class);
			SortField sortField = null;
			String sortColumn = null;

			log.info("Order by:::" + criteria.getOrderByColumn().getValue());

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

			Criteria c = session.createCriteria(Candidate.class).addOrder(Order.desc(sortColumn));
			ftQuery.setCriteriaQuery(c);

			if (criteria.getOrderByType().equals(OrderByType.DESC)) {
				log.info("order::::desc");
				sortField = new SortField(sortColumn, SortField.STRING, true);
			} else {
				log.info("order::::asc");
				sortField = new SortField(sortColumn, SortField.STRING, false);
			}
			ftQuery.setSort(new Sort(sortField));

			if (firstResult >= 0)
				ftQuery.setFirstResult(firstResult);
			if (maxResults > 0)
				ftQuery.setMaxResults(maxResults);

			List<?> result = ftQuery.list();
			return (List<IndiaCandidate>) result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DataRetrievalFailureException("Lucene query failed", e);
		}
	}

	public int findCountByLuceneQuery(String searchString) {
		try {
			QueryParser parser = new QueryParser(Version.LUCENE_31, "resume", new StandardAnalyzer(Version.LUCENE_31));
			org.apache.lucene.search.Query luceneQuery = parser.parse(searchString);

			Session session = getSessionFactory().getCurrentSession();

			FullTextSession fullTextSession = Search.getFullTextSession(session);

			FullTextQuery ftQuery = fullTextSession.createFullTextQuery(luceneQuery, Candidate.class);

			int size = ftQuery.getResultSize();
			return size;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DataRetrievalFailureException("Lucene query failed", e);
		}
	}

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
	 * @Override public Map<String, Map<String, Integer>>
	 * getportalResumes(String portalSelect,String dateStart, String
	 * dateEnd,String status) { Map<String, Map<String, Integer>> userMap = new
	 * TreeMap<String, Map<String, Integer>>(); StringBuilder hql = new
	 * StringBuilder(); List<?> result=null; hql.append(
	 * "select  CASE WHEN c.created_by in ('Dice','Monster','Careerbuilder','CGI') THEN c.created_by ELSE 'OTHERS' END,ltrim(rtrim(to_char(s.created_on, 'MONTH'))), count(CASE WHEN c.created_by in ('Dice','Monster','Careerbuilder','CGI') THEN c.created_by ELSE c.created_by END) from candidate c,submittal s  where s.candidate_id=c.candidate_id and c.status>=0"
	 * );
	 * 
	 * if(portalSelect!=null && portalSelect.trim().equalsIgnoreCase("Dice")){
	 * hql.append("and trim(both '' from c.created_by)='Dice'"); }else
	 * if(portalSelect!=null &&
	 * portalSelect.trim().equalsIgnoreCase("Monster")){ hql.append(
	 * "and trim(both '' from c.created_by)='Monster'"); }else
	 * if(portalSelect!=null &&
	 * portalSelect.trim().equalsIgnoreCase("Careerbuilder")){ hql.append(
	 * "and trim(both '' from c.created_by)='Careerbuilder'"); }else
	 * if(portalSelect!=null && portalSelect.trim().equalsIgnoreCase("CGI")){
	 * hql.append("and trim(both '' from c.created_by)='CGI'"); }
	 * 
	 * if (dateEnd != null) { hql.append(
	 * " and EXTRACT(YEAR FROM  s.created_on) =?"); } if(status!=null &&
	 * !status.equalsIgnoreCase("all") && dateStart==null){ hql.append(
	 * " and ltrim(rtrim(s.status)) =?"); } if (status!=null &&
	 * status.equalsIgnoreCase("all") && dateStart != null) { hql.append(
	 * " and ltrim(rtrim( to_char(s.created_on, 'MONTH'))) =?"); } if
	 * (status!=null && !status.equalsIgnoreCase("all") && dateStart != null) {
	 * hql.append(
	 * " and ltrim(rtrim(s.status)) =? and ltrim(rtrim( to_char(s.created_on, 'MONTH'))) =?"
	 * ); } hql.append(
	 * " group by CASE WHEN c.created_by in ('Dice','Monster','Careerbuilder','CGI') THEN c.created_by ELSE 'OTHERS' END,ltrim(rtrim(to_char(s.created_on, 'MONTH'))),EXTRACT(MONTH FROM  s.created_on) order by CASE WHEN c.created_by in ('Dice','Monster','Careerbuilder','CGI') THEN c.created_by ELSE 'OTHERS' END"
	 * ); result =
	 * findBySqlQueryStr(hql.toString(),dateStart,dateEnd!=null?Integer.parseInt
	 * (dateEnd):0,status); int finalCount=0; for (Object record : result) {
	 * Object[] tuple = (Object[]) record; String portalName = (String)
	 * tuple[0]; String month = (String) tuple[1]; Number count = (Number)
	 * tuple[2];
	 * 
	 * Map<String, Integer> statusMap = userMap.get(portalName); if (statusMap
	 * == null) { statusMap = new HashMap<String, Integer>();
	 * 
	 * } userMap.put(portalName+"|"+month, statusMap);
	 * if(statusMap.containsKey(month) && count!=null){
	 * finalCount=statusMap.get(month)+count.intValue(); statusMap.put(month,
	 * finalCount); }else{ statusMap.put(month, count != null ? count.intValue()
	 * : 0); } } return userMap; }
	 */

	public List<Candidate> getportalCandidates(String portalSelect, String month, String year) {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select c.candidate_id,c.first_name,c.title,c.city,c.state,c.email from  candidate c,submittal s where c.candidate_id=s.candidate_id ");
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
			hql.append("order by c.created_on desc");
			result = findBySqlQueryStr(hql.toString(), month, year != null ? Integer.parseInt(year) : 0, "all");
			Iterator ie = result.iterator();
			List<Candidate> canLst = new ArrayList<Candidate>();
			Candidate c = null;
			while (ie.hasNext()) {
				c = new Candidate();
				Object[] s = (Object[]) ie.next();
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
			return (List<Candidate>) canLst;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

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
					log.error(e.getMessage(),e);
					e.printStackTrace();
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
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			StringBuilder hql = new StringBuilder();
			hql.append(
					"select case when (c.createdUser not in('Dice','Careerbuilder','Monster','TechFetch') or c.createdUser is NULL) then 'Others' else c.createdUser end,count(*) from Candidate c ");
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
					"  group by case when (c.createdUser not in('Dice','Careerbuilder','Monster','TechFetch') or c.createdUser is NULL) then 'Others' else c.createdUser end");
			List<?> result = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			Map<String, Integer> userMap = new TreeMap<String, Integer>();
			for (Object record : result) {
				Object[] tuple = (Object[]) record;
				String status = (String) tuple[0];
				Number count = (Number) tuple[1];
				if (count == null)
					count = 0;
				userMap.put(status, count.intValue());
			}
			return userMap;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
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

	@Override
	public List<?> getIndiaSubmittals(Integer candidateId) {
		try {
			final StringBuffer sqlQuery = new StringBuffer(
					"select s.status, s.submittal_id,job.order_id,s.created_on, s.created_by as recName from india_candidate c,india_submittal s,india_job_order job  where c.candidate_id = s.candidate_id and s.order_id = job.order_id and c.candidate_id = :candidateId and s.delete_flag = 0");

			final Map<String, Object> param = new HashMap<String, Object>();
			param.put("candidateId", candidateId);

			return findBySQLQuery(sqlQuery.toString(), param);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public ResumeDto getResumeById(String candidateId) {
		try {
			// TODO Auto-generated method stub
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			Query query = session.createQuery("select r.document,r.candidate.documentType,r.parsedText,r.rtrDocument,r.candidate.rtrDocumentType,"
					+ "r.processedDocument,r.candidate.processedDocumentType  from IndiaResume r where r.id=:id");
			// Query query = session.createQuery("select r.parsedText from
			// IndiaResume r where r.id=:id");
			log.info("resumeId" + candidateId);
			query.setParameter("id", Integer.parseInt(candidateId));
			List<?> list = query.list();
			ResumeDto resumeDto = null;
			if (!Utils.isEmpty(list)) {
				resumeDto = new ResumeDto();
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
	public void updateCandidateStatus(IndiaCandidateStatuses status) {
		try {
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			session.saveOrUpdate(status);
			log.info("Candidate Status" + status.getStatus().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void updateStatus(IndiaCandidateStatuses stauses) {

		try {
			log.info("Reason" + stauses.getReason1() + "Candidate" + stauses.getId());
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			Query query = session.createQuery("update IndiaCandidateStatuses c set c.reason=:reason where c.id=:id and c.status=:status");
			query.setParameter("status", stauses.getStatus());
			query.setParameter("reason", stauses.getReason());
			query.setParameter("id", stauses.getId());
			query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}
	
	@Override
	public IndiaCandidateStatuses getIndiaCandidateStatus(IndiaCandidate id) {
		try {
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();

			Query query = session.createQuery("from IndiaCandidateStatuses c where c.indiacandidate.id=:id and c.status=:status ORDER BY id DESC");
			query.setParameter("id", id.getId());
			query.setParameter("status", id.getStatus());
			query.setFirstResult(0);
			query.setMaxResults(0);
			List<IndiaCandidateStatuses> cs = query.list();
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
	public IndiaCandidateDto getAllUserDetails() {
		try {
			IndiaCandidateDto candidateDto = new IndiaCandidateDto();
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			Query userQuery = session.createQuery("select u.userId from User u where u.status = 'ACTIVE' group by u.userId order by u.userId ASC");
			Query portalQuery = session.createQuery("select p.portalUserId from PortalCredentials p group by p.portalUserId order by p.portalUserId ASC");
			List<String> userId = userQuery.list();
			List<String> portalId = portalQuery.list();
			log.info("user id size " + userId.size());
			log.info("final list " + portalId.size());
			candidateDto.setUserIds(userId);
			candidateDto.setPortalEmails(portalId);
			candidateDto.setUploaded(userId);
			return candidateDto;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}
}

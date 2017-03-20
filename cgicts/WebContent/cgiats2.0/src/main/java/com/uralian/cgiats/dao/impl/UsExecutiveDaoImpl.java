package com.uralian.cgiats.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.time.DateUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.Version;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.UsExecutiveDao;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.IndiaCandidate;
import com.uralian.cgiats.model.OrderByColumn;
import com.uralian.cgiats.model.OrderByType;
import com.uralian.cgiats.model.UsExecutive;

@Repository
@SuppressWarnings("unchecked")
public class UsExecutiveDaoImpl extends GenericDaoImpl<UsExecutive, Integer> implements UsExecutiveDao {

	public UsExecutiveDaoImpl() {
		super(UsExecutive.class);
		// TODO Auto-generated constructor stub
	}

	public int findCountByLuceneQuery(String searchString) {
		try {
			QueryParser parser = new QueryParser(Version.LUCENE_31, "executiveresume", new StandardAnalyzer(Version.LUCENE_31));
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
	public Map<String, Integer> getExecutivesOnUpdatedDate(Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select updatedBy, count(*) from UsExecutive");
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
	public List<String> findAllTitles() {
		try {
			String hql = "select distinct ue.title from UsExecutive ue where ue.title is not null order by ue.title";
			List<String> list = (List<String>) runQuery(hql.toString());
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Integer> getExecutivesByUser(Date dateStart, Date dateEnd) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select createdUser, count(*) from UsExecutive");
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

	@Override
	public List<UsExecutive> findByLuceneQuery(String searchString, CandidateSearchDto criteria) {

		try {
			int firstResult = criteria.getStartPosition();
			int maxResults = criteria.getMaxResults();

			QueryParser parser = new QueryParser(Version.LUCENE_31, "executiveresume", new StandardAnalyzer(Version.LUCENE_31));
			log.info("searchString::::" + searchString);
			org.apache.lucene.search.Query luceneQuery = parser.parse(searchString);
			Session session = getSessionFactory().getCurrentSession();
			FullTextSession fullTextSession = Search.getFullTextSession(session);
			FullTextQuery ftQuery = fullTextSession.createFullTextQuery(luceneQuery, UsExecutive.class);
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

			Criteria ue = session.createCriteria(UsExecutive.class).addOrder(Order.desc(sortColumn));
			ftQuery.setCriteriaQuery(ue);

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
			return (List<UsExecutive>) result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DataRetrievalFailureException("Lucene query failed", e);
		}
		// TODO Auto-generated method stub

	}

}

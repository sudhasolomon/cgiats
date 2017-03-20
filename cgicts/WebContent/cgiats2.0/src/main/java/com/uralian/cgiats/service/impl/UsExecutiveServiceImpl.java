package com.uralian.cgiats.service.impl;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.UsExecutiveDao;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.IndiaCandidate;
import com.uralian.cgiats.model.OrderByColumn;
import com.uralian.cgiats.model.OrderByType;
import com.uralian.cgiats.model.UsExecutive;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.UsExecutiveService;
import com.uralian.cgiats.util.Utils;

@Service
@Transactional(rollbackFor = ServiceException.class)
public class UsExecutiveServiceImpl implements UsExecutiveService {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private UsExecutiveDao usExecutiveDao;

	@Override
	public void saveExecutive(UsExecutive usexecutive) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + usexecutive);
			System.out.println("executive save>>" + usexecutive);
			usExecutiveDao.save(usexecutive);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist executive", exception);
		}
	}

	@Override
	public void updateExecutive(UsExecutive usexecutive) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Updating " + usexecutive);

			usExecutiveDao.update(usexecutive);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to update candidate", exception);
		}

	}

	@Override
	@Transactional(readOnly = true)
	public List<UsExecutive> getExecutiveDetails(String email) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from UsExecutive");
			hql.append(" where email =:email and deleteFlag=0 ");
			params.put("email", email);
			return usExecutiveDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UsExecutive> getExistCandidateDetails(int id) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from UsExecutive");
			hql.append(" where deleteFlag=0 and id =:id");
			params.put("id", id);
			return usExecutiveDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public UsExecutive getExecutive(Integer executiveId, boolean fetchDocument, boolean fetchProperties) {
		try {
			if (log.isDebugEnabled()) {
				log.debug("UsExecutive id [" + executiveId + "] - fetchDocument[" + fetchDocument + "] fetchProperties[" + fetchProperties + "] ");
			}

			StringBuffer hql = new StringBuffer();
			hql.append("select distinct ue from UsExecutive ue");
			if (fetchDocument)
				hql.append(" join fetch ue.executiveresume");
			if (fetchProperties)
				hql.append(" left join fetch ue.properties");
			hql.append(" where ue.id = :executiveId and ue.deleteFlag=0");

			List<UsExecutive> result = usExecutiveDao.findByQuery(hql.toString(), "executiveId", executiveId);

			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UsExecutive> findExecutive(CandidateSearchDto criteria) {
		try {
			// null criteria not allowed
			if (criteria == null)
				throw new IllegalArgumentException("Search criteria not set");

			if (log.isDebugEnabled())
				log.debug("Using following criteria " + criteria);

			String searchString = buildLuceneQuery(criteria);
			if (!Utils.isEmpty(searchString)) {

				return usExecutiveDao.findByLuceneQuery(searchString, criteria);
			} else {
				StringBuffer hql = new StringBuffer("select c from UsExecutive c where c.deleteFlag=0 ");
				OrderByColumn orderBy = criteria.getOrderByColumn();
				if (orderBy != null) {
					hql.append(orderBy.getValue());
					OrderByType orderType = criteria.getOrderByType() != null ? criteria.getOrderByType() : OrderByType.ASC;
					hql.append(orderType.getValue());
				}
				if (log.isDebugEnabled())
					log.debug("This is HQL Query " + hql.toString());

				System.out.println(hql);

				return usExecutiveDao.findByQuery(hql.toString(), criteria.getStartPosition(), criteria.getMaxResults());
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public int findExecutivesCount(CandidateSearchDto criteria) {
		try {
			System.out.println("find Us USEXECUTIVE count");
			// null criteria not allowed
			if (criteria == null)
				throw new IllegalArgumentException("Search criteria not set");

			if (log.isDebugEnabled())
				log.debug("Using following criteria " + criteria);

			String searchString = buildLuceneQuery(criteria);
			if (!Utils.isEmpty(searchString))
				return usExecutiveDao.findCountByLuceneQuery(searchString);
			else {
				List<?> result = usExecutiveDao.runQuery("select count(ue) from UsExecutive ue where ue.deleteFlag=0 ");
				return ((Number) result.get(0)).intValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return 0;
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

	@Override
	public void deleteExecutive(int executiveId) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Deleting executive with id [" + executiveId + "]");

			// Fetching candidate to delete it.
			UsExecutive executive = usExecutiveDao.findById(executiveId);
			if (executive != null) {
				usExecutiveDao.delete(executive);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Error while deleting executive", e);
		}
	}

}

/**
 * 
 */
package com.uralian.cgiats.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.ResumeHistoryDao;
import com.uralian.cgiats.model.ResumeHistory;
import com.uralian.cgiats.service.ResumeHistoryService;
import com.uralian.cgiats.service.ServiceException;

/**
 * @author Parameshwar
 *
 */

@Service
@Transactional(rollbackFor = ServiceException.class)
public class ResumeHistoryServiceImpl implements ResumeHistoryService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ResumeHistoryDao resumeHistoryDao;

	@Override
	public List<String> findAllUsers() {
		try {
			return resumeHistoryDao.findAllUsers();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void saveResumeHistory(ResumeHistory resumeHistory) throws ServiceException {

		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + resumeHistory);

			resumeHistoryDao.save(resumeHistory);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist resumeHistory", exception);
		}
	}

	/**
	 * @param resumeHistory
	 * @return
	 */
	public List<ResumeHistory> getResumeAuditLogs(Date startDate, Date endDate, String userId, Integer candidateId) {
		try {
			if (log.isDebugEnabled())
				log.debug("Using following resumeSearchDto " + startDate + " " + endDate + " " + userId + " " + candidateId + "");

			StringBuffer hqlSelect = new StringBuffer("select r from ResumeHistory r ");
			Map<String, Object> params = new HashMap<String, Object>();

			StringBuffer hqlWhere = new StringBuffer(" where 1=1");
			buildWhereClause(startDate, endDate, userId, candidateId, hqlWhere, params);

			hqlSelect.append(hqlWhere);
			if (log.isDebugEnabled())
				log.debug("HQL Query " + hqlSelect.toString());

			return resumeHistoryDao.findByQuery(hqlSelect.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @param startDate
	 * @param endDate
	 * @param userId
	 * @param candidateId
	 * @param hqlWhere
	 * @param params
	 */
	private void buildWhereClause(Date startDate, Date endDate, String userId, Integer candidateId, StringBuffer hql, Map<String, Object> params) {
		log.debug("Using following inside buildWhereClause " + startDate + " " + endDate + " " + userId + " " + candidateId + "");
		try {
			if (userId != null && !userId.equals("")) {
				hql.append(" and r.createdBy = :createdBy ");
				params.put("createdBy", userId);
			}

			if (candidateId != null && candidateId != 0) {
				hql.append(" and r.candidate  = :candidateId ");
				params.put("candidateId", candidateId);
			}
			if (startDate != null) {
				hql.append(" and COALESCE( cast(r.createdOn as date)) >= cast(:startDate as date)");
				params.put("startDate", startDate);
			}
			if (endDate != null) {
				hql.append(" and COALESCE( cast(r.createdOn as date)) <= cast(:endDate as date) ");
				params.put("endDate", endDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}
}

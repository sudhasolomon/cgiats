package com.uralian.cgiats.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.OnlineCgiCanidateDao;
import com.uralian.cgiats.model.OnlineCgiCandidates;
import com.uralian.cgiats.service.OnlineCgiCandidateService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.OnlineResumeStats;
import com.uralian.cgiats.util.Utils;

/**
 * @author Chaitanya
 * 
 */
@Repository
@Service
@Transactional(rollbackFor = ServiceException.class)
public class OnlineCgiCandidateServiceImpl implements OnlineCgiCandidateService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private OnlineCgiCanidateDao onlineCgiCanidateDao;

	/**
	 * @param onlineCgiCanidates
	 * @throws ServiceException
	 */
	@Override
	public void saveCandidate(OnlineCgiCandidates onlineCgiCanidates) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + onlineCgiCanidates);

			onlineCgiCanidateDao.save(onlineCgiCanidates);
			log.debug("onlineCgiCandidate save :" + onlineCgiCanidates);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist OnlineCgiCandidate", exception);
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
	public void updateCandidate(OnlineCgiCandidates onlineCgiCanidates) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Updating " + onlineCgiCanidates);

			onlineCgiCanidateDao.update(onlineCgiCanidates);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to update OnlineCgiCandidate", exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateService#getCandidate(int,
	 * boolean, boolean)
	 */
	@Transactional(readOnly = true)
	public List<OnlineCgiCandidates> getOnlineCgiCandidates(String status, String userId) {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select distinct c from OnlineCgiCandidates c,JobOrder j  ");

			if (userId != null)
				hql.append(" where c.resumeStatus = :status and j.createdBy='" + userId + "' and j.id=c.orderId order by c.createdOn DESC");
			else
				hql.append(" where c.resumeStatus = :status  and j.id=c.orderId order by c.createdOn DESC");

			List<OnlineCgiCandidates> result = onlineCgiCanidateDao.findByQuery(hql.toString(), "status", status);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.OnlineCgiCandidateService#
	 * getOnlineCgiCandidate (int)
	 */
	@Override
	@Transactional(readOnly = true)
	public OnlineCgiCandidates getOnlineCgiCandidate(int candidateId, int jobOrderId) {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select distinct c from OnlineCgiCandidates c");
			hql.append(" where c.candidateId.id = :candidateId ");

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("candidateId", candidateId);
			if (jobOrderId > 0) {
				hql.append("and c.orderId.id=:orderId");
				params.put("orderId", jobOrderId);
			}
			hql.append(" order by c.createdOn DESC");
			List<OnlineCgiCandidates> result = onlineCgiCanidateDao.findByQuery(hql.toString(), params);

			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public List<OnlineResumeStats> getOnlineResumeStats(Date dateStart, Date dateEnd) {
		try {
			List<OnlineResumeStats> lst = onlineCgiCanidateDao.getOnlineResumeStats(dateStart, dateEnd);
			return lst;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
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
			OnlineCgiCandidates candidate = onlineCgiCanidateDao.findById(candidateId);
			if (candidate != null) {
				onlineCgiCanidateDao.delete(candidate);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Error while deleting candidate", e);
		}
	}

	public List<OnlineCgiCandidates> getCandidate(Integer orderId, Integer candidateId) {
		try {
			List<OnlineCgiCandidates> existsCandidateDetails = onlineCgiCanidateDao.getCandidate(orderId, candidateId);
			return existsCandidateDetails;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.OnlineCgiCandidateService#
	 * isCandidateAlreadyApplied(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public Boolean isCandidateAlreadyApplied(Integer orderId, Integer candidateId) {
		try {
			return onlineCgiCanidateDao.isCandidateAlreadyApplied(orderId, candidateId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

package com.uralian.cgiats.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.IndiaOnlineCgiCandidateDao;
import com.uralian.cgiats.dao.OnlineCgiCanidateDao;
import com.uralian.cgiats.model.IndiaOnlineCgiCandidates;
import com.uralian.cgiats.model.OnlineCgiCandidates;
import com.uralian.cgiats.service.IndiaOnlineCgiCandidateService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.OnlineResumeStats;
import com.uralian.cgiats.util.Utils;

@SuppressWarnings("unchecked")
@Service
@Transactional(rollbackFor = ServiceException.class)
public class IndiaOnlineCgiCandidateServiceImpl implements IndiaOnlineCgiCandidateService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private IndiaOnlineCgiCandidateDao onlineCgiCanidateDao;

	@Override
	public void saveCandidate(IndiaOnlineCgiCandidates onlineCgiCanidates) throws ServiceException {
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

	@Override
	public void updateCandidate(IndiaOnlineCgiCandidates onlineCgiCanidates) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Updating " + onlineCgiCanidates);

			onlineCgiCanidateDao.update(onlineCgiCanidates);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to update OnlineCgiCandidate", exception);
		}
	}

	@Transactional(readOnly = true)
	public List<IndiaOnlineCgiCandidates> getOnlineCgiCandidates(String status) {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select distinct c from IndiaOnlineCgiCandidates c  ");

			hql.append(" where c.resumeStatus = :status order by c.createdOn DESC");

			List<IndiaOnlineCgiCandidates> result = onlineCgiCanidateDao.findByQuery(hql.toString(), "status", status);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public IndiaOnlineCgiCandidates getOnlineCgiCandidate(int candidateId) {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select distinct c from OnlineCgiCandidates c");
			hql.append(" where c.candidateId.id = :jobOpenId order by c.createdOn DESC");

			List<IndiaOnlineCgiCandidates> result = onlineCgiCanidateDao.findByQuery(hql.toString(), "jobOpenId", candidateId);
			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<OnlineResumeStats> getOnlineResumeStats(Date dateStart, Date dateEnd) {
		try {
			List<OnlineResumeStats> list = onlineCgiCanidateDao.getOnlineResumeStats(dateStart, dateEnd);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void deleteCandidate(int candidateId) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Deleting candidate with id [" + candidateId + "]");

			// Fetching candidate to delete it.
			IndiaOnlineCgiCandidates candidate = onlineCgiCanidateDao.findById(candidateId);
			if (candidate != null) {
				onlineCgiCanidateDao.delete(candidate);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Error while deleting candidate", e);
		}
	}

}

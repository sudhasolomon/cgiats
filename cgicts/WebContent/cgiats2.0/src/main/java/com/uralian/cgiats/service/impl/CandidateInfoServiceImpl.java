package com.uralian.cgiats.service.impl;

/*
 * Radhika
 * Created Date:8/5/2013
 * Comment: Created to store candidate info for the tickets ATS-
 */
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.CandidateInfoDao;
import com.uralian.cgiats.model.CandidateInfo;
import com.uralian.cgiats.model.ClientInfo;
import com.uralian.cgiats.service.CandidateInfoService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.Utils;

@Service
@Transactional(rollbackFor = ServiceException.class)
public class CandidateInfoServiceImpl implements CandidateInfoService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private CandidateInfoDao candidateInfoDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#saveCandidate(com.uralian.
	 * cgiats .model.Candidate)
	 */
	@Override
	public void saveCandidate(CandidateInfo candidateInfo) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + candidateInfo);

			candidateInfoDao.save(candidateInfo);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist candidate", exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateInfoService#updateCandidate(com.
	 * uralian.cgiats.model.CandidateInfo)
	 */
	@Override
	public void updateCandidate(CandidateInfo candidateInfo) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + candidateInfo);

			candidateInfoDao.update(candidateInfo);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist candidate", exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateInfoService#getCandidateInfo()
	 */
	@Override
	public List<CandidateInfo> getCandidateInfo() {
		try {
			List<CandidateInfo> candidateInfo = candidateInfoDao.findAll();
			return candidateInfo;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public CandidateInfo getCandidateInfo(Integer id) {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select c from CandidateInfo c");
			hql.append(" where c.id = :id");

			List<CandidateInfo> result = candidateInfoDao.findByQuery(hql.toString(), "id", id);
			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public CandidateInfo getCandidateInfoByCandidateId(Integer id) {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select c from CandidateInfo c");
			hql.append(" where c.candidate.id = :id");

			List<CandidateInfo> result = candidateInfoDao.findByQuery(hql.toString(), "id", id);
			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

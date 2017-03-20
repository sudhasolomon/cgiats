package com.uralian.cgiats.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.AgencyDetailsDao;
import com.uralian.cgiats.model.AgencyDetails;
import com.uralian.cgiats.model.ClientInfo;
import com.uralian.cgiats.service.AgencyDetailsService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.Utils;

/*
 * Radhika
 * Created Date:8/5/2013
 * Comment: Created to store candidate info for the tickets ATS-
 */

@Service
@Transactional(rollbackFor = ServiceException.class)
public class AgencyDetailsServiceImpl implements AgencyDetailsService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private AgencyDetailsDao agencyDetailsDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#saveCandidate(com.uralian.
	 * cgiats .model.Candidate)
	 */
	@Override
	public void saveAgencyDetails(AgencyDetails agencyDetails) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + agencyDetails);

			agencyDetailsDao.save(agencyDetails);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist candidate", exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.AgencyDetailsService#updateAgencyDetails(com.
	 * uralian.cgiats.model.AgencyDetails)
	 */
	@Override
	public void updateAgencyDetails(AgencyDetails agencyDetails) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + agencyDetails);

			agencyDetailsDao.update(agencyDetails);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist candidate", exception);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.AgencyDetailsService#getAgencyDetails(java.
	 * lang.Integer)
	 */
	@Override
	public AgencyDetails getAgencyDetails(Integer id) {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select a from AgencyDetails a");
			hql.append(" where a.candidateinfo.id = :id");

			List<AgencyDetails> result = agencyDetailsDao.findByQuery(hql.toString(), "id", id);
			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

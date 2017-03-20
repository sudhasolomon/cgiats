package com.uralian.cgiats.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.OnsiteVisitsDao;
import com.uralian.cgiats.model.OnsiteVisits;
import com.uralian.cgiats.service.OnsiteVisitsService;
import com.uralian.cgiats.service.ServiceException;

@Service
@Transactional(rollbackFor = ServiceException.class)
public class OnsiteVisitsServiceImpl implements OnsiteVisitsService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private OnsiteVisitsDao onsiteVisitsDao;

	/**
	 * @param DuplicateResumeTrack
	 * @throws Exception
	 */
	@Override
	public void saveOnsiteVisits(OnsiteVisits onsiteVisits) throws Exception {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + onsiteVisits);

			onsiteVisitsDao.save(onsiteVisits);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist candidate", exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.MonthlySalesQuotaService#getDmMontlyQuotas(
	 * java.util .Date, java.util.Date)
	 */
	public List<OnsiteVisits> getOnsiteVisits(Date dateStart, Date dateEnd) {
		try {
			return onsiteVisitsDao.getOnsiteVisits(dateStart, dateEnd);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

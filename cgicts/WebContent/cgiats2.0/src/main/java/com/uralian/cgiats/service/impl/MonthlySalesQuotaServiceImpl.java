package com.uralian.cgiats.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.MonthlySalesQuotaDao;
import com.uralian.cgiats.model.MonthlySalesQuotas;
import com.uralian.cgiats.service.MonthlySalesQuotaService;
import com.uralian.cgiats.service.ServiceException;

@Service
@Transactional(rollbackFor = ServiceException.class)
public class MonthlySalesQuotaServiceImpl implements MonthlySalesQuotaService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private MonthlySalesQuotaDao monthlySalesQuotaDao;

	/**
	 * @param DuplicateResumeTrack
	 * @throws Exception
	 */
	@Override
	public void saveSalesQuota(MonthlySalesQuotas monthlySalesQuotas) throws Exception {
		try {
			log.info("From Save SalesQuota");
			if (log.isDebugEnabled())
				log.debug("Persisting " + monthlySalesQuotas);

			monthlySalesQuotaDao.save(monthlySalesQuotas);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist candidate", exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.MonthlySalesQuotaService#getDmMontlyQuotas
	 * (java.util .Date, java.util.Date)
	 */
	public Map<String, String> getDmMontlyQuotas(String dateStart, String dateEnd) {
		try {
			return monthlySalesQuotaDao.getDmMontlyQuotas(dateStart, dateEnd);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void updateSalesQuota(MonthlySalesQuotas monthlySalesQuotas) throws Exception {
		// TODO Auto-generated method stub

		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + monthlySalesQuotas);

			monthlySalesQuotaDao.update(monthlySalesQuotas);
		} catch (Exception exception) {
			exception.printStackTrace();
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to monthlySalesQuotas", exception);
		}
	}

	public List<MonthlySalesQuotas> getRecSalesQuotaList(String userId, String month, String year) {
		try {
			return monthlySalesQuotaDao.getRecSalesQuotaList(userId, month, year);
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
	 * com.uralian.cgiats.service.MonthlySalesQuotaService#getAdmSalesQuota(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<MonthlySalesQuotas> getAdmSalesQuota(String userId, String month, String year) {
		try {
			return monthlySalesQuotaDao.getAdmSalesQuota(userId, month, year);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

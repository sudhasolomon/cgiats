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

import com.uralian.cgiats.dao.AwardDao;
import com.uralian.cgiats.model.Awards;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.JobOrderSearchDto;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.service.AwardService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.Utils;

/**
 * @author Parameshwar
 *
 */
@Service
@Transactional(rollbackFor = ServiceException.class)
public class AwardServiceImpl implements AwardService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private AwardDao awardDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.AwardService#saveAward(com.uralian.cgiats.
	 * model .Award)
	 */
	@Override
	public void saveAwards(Awards awards) throws ServiceException {
		try {
			awardDao.save(awards);
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.AwardService#loadAward(java.lang.Integer)
	 */
	@Override
	@Transactional(readOnly = true)
	public Awards loadAward(Integer awardId) {
		try {
			Awards awards = awardDao.findById(awardId);
			return awards;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<Awards> listAwards() {
		try {
			List<Awards> awards = awardDao.findAll();

			return awards;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void updateAwards(Awards awards) throws ServiceException {
		try {
			awardDao.update(awards);
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
			throw new ServiceException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.AwardService#getCandidatesByUser(java.util
	 * .Date, java.util.Date)
	 */
	@Override
	public List<String> getCandidatesByUser(Date dateStart, Date dateEnd) {
		try {
			return awardDao.getCandidatesByUser(dateStart, dateEnd);
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
	 * com.uralian.cgiats.service.AwardService#findOpenings(com.uralian.cgiats
	 * .model.JobOpeningSearchDto)
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Awards> findAwards(JobOrderSearchDto criteria) {
		try {
			// null criteria not allowed
			if (criteria == null)
				throw new IllegalArgumentException("Search criteria not set");

			if (log.isDebugEnabled())
				log.debug("Using following criteria " + criteria);

			StringBuffer hqlSelect = new StringBuffer("from Awards a ");
			Map<String, Object> params = new HashMap<String, Object>();

			StringBuffer hqlWhere = new StringBuffer(" where 1=1 ");
			buildWhereClause(criteria, hqlWhere, params);

			hqlSelect.append(hqlWhere);
			if (log.isDebugEnabled())
				log.debug("HQL Query " + hqlSelect.toString());

			// return orderDao
			// .findByQuery(hqlSelect.toString(), criteria.getStartPosition(),
			// criteria.getMaxResults(), params.toArray());
			return awardDao.findByQuery(hqlSelect.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @param criteria
	 * @param hql
	 * @param params
	 */
	private void buildWhereClause(JobOrderSearchDto criteria, StringBuffer hql, Map<String, Object> params) {
		try {
			// add JobOrder fields
			log.info("Inside buildWhereClause :: Selected Awd : " + criteria.getAwd());

			if (!Utils.isEmpty(criteria.getAwd())) {
				hql.append(" and a.userId.id = :awd ");
				params.put("awd", criteria.getAwd());

				log.info("Inside Condition : " + criteria.getAwd());
				log.info("HQL Condition : " + hql.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

}

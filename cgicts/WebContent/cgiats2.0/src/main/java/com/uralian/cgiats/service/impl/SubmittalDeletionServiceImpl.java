package com.uralian.cgiats.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.SubmittalDao;
import com.uralian.cgiats.dao.SubmittalDeletionDao;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalDeletion;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.SubmittalDeletionService;
import com.uralian.cgiats.util.Utils;

@Service
@Transactional(rollbackFor = ServiceException.class)
public class SubmittalDeletionServiceImpl implements SubmittalDeletionService {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private SubmittalDeletionDao submittalDeletionDao;

	@Autowired
	private SubmittalDao submittalDao;

	@Override
	public void saveSubmittalDeletion(SubmittalDeletion submittalDeletion) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + submittalDeletion);

			submittalDeletionDao.save(submittalDeletion);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist resumeHistory", exception);
		}

	}

	// Code added Raghavendra 19/05/14.
	@Override
	public List<SubmittalDeletion> getDeletedStatusDetails(Integer submittalId) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from SubmittalDeletion s ");
			hql.append(" where s.status='Deleted' and s.submittalId=:submittalId");
			params.put("submittalId", submittalId.toString());
			return (List<SubmittalDeletion>) submittalDeletionDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	// Code added Raghavendra 20/05/14.
	@Override
	@Transactional(readOnly = true)
	public Submittal getDeletedSubmittal(int subid) {
		try {
			StringBuffer hql = new StringBuffer();

			hql.append("from  Submittal  s where s.id=:submittalId and s.deleteFlag=1 ");

			List<Submittal> result = submittalDao.findByQuery(hql.toString(), "submittalId", subid);

			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

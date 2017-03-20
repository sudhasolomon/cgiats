package com.uralian.cgiats.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.IndiaSubmittalDao;
import com.uralian.cgiats.dao.IndiaSubmittalDeletionDao;
import com.uralian.cgiats.dao.SubmittalDeletionDao;
import com.uralian.cgiats.model.IndiaSubmittal;
import com.uralian.cgiats.model.IndiaSubmittalDeletion;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalDeletion;
import com.uralian.cgiats.service.IndiaSubmittalDeletionService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.Utils;

@Service
@Transactional(rollbackFor = ServiceException.class)
public class IndiaSubmittalDeletionServiceImpl implements IndiaSubmittalDeletionService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private IndiaSubmittalDeletionDao submittalDeletionDao;

	@Autowired
	private IndiaSubmittalDao submittalDao;

	@Override
	public void saveSubmittalDeletion(IndiaSubmittalDeletion submittalDeletion) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + submittalDeletion);

			submittalDeletionDao.save(submittalDeletion);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist resumeHistory", exception);
		}

	}

	@Override
	public List<IndiaSubmittalDeletion> getDeletedStatusDetails(Integer submittalId) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from SubmittalDeletion s ");
			hql.append(" where s.status='Deleted' and s.submittalId=:submittalId");
			params.put("submittalId", submittalId.toString());
			return (List<IndiaSubmittalDeletion>) submittalDeletionDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public IndiaSubmittal getDeletedSubmittal(int subid) {
		try {
			StringBuffer hql = new StringBuffer();

			hql.append("from  Submittal  s where s.id=:submittalId and s.deleteFlag=1 ");

			List<IndiaSubmittal> result = submittalDao.findByQuery(hql.toString(), "submittalId", subid);
			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

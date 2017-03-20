package com.uralian.cgiats.service.impl;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.DupResumeTrackDao;
import com.uralian.cgiats.model.DuplicateResumeTrack;
import com.uralian.cgiats.service.DupResumeTrackService;
import com.uralian.cgiats.service.ServiceException;

@Service
@Transactional(rollbackFor = ServiceException.class)
public class DupResumeTrackServiceImpl implements DupResumeTrackService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private DupResumeTrackDao dupResumeTrackDao;

	/**
	 * @param DuplicateResumeTrack
	 * @throws Exception
	 */
	@Override
	public void saveDuplicateResume(DuplicateResumeTrack duplicateResumeTrack) throws Exception {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + duplicateResumeTrack);

			dupResumeTrackDao.save(duplicateResumeTrack);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist candidate", exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#getCandidatesByUser(java.util
	 * .Date, java.util.Date)
	 */
	@Override
	public Map<String, Integer> getDuplicateResumes(Date dateStart, Date dateEnd) {
		try {
			return dupResumeTrackDao.getDuplicateResumes(dateStart, dateEnd);
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
	 * com.uralian.cgiats.service.CandidateService#getCandidatesByUser(java.util
	 * .Date, java.util.Date)
	 */
	@Override
	public Map<String, Map<String, Integer>> getDiceDuplicateCount(Date dateStart, Date dateEnd) {
		try {
			return dupResumeTrackDao.getDiceDuplicateCount(dateStart, dateEnd);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}
}

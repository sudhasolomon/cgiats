package com.uralian.cgiats.service.impl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.primefaces.util.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.CandidateHistoryDao;
import com.uralian.cgiats.dao.IndiaCandidateHistoryDao;
import com.uralian.cgiats.model.CandidateHistory;
import com.uralian.cgiats.model.IndiaCandidateHistory;
import com.uralian.cgiats.service.CommService;
import com.uralian.cgiats.service.IndiaCandidateHistoryService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.web.UtilityBean;

@Service
@Transactional(rollbackFor = ServiceException.class)
public class IndiaCandidateHistoryServiceImpl implements IndiaCandidateHistoryService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private IndiaCandidateHistoryDao candidateHistoryDao;

	@Autowired
	private CommService commService;

	@Override
	public void saveCandidateHistory(IndiaCandidateHistory candidateHistory) throws ServiceException {

		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + candidateHistory);

			candidateHistoryDao.save(candidateHistory);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist resumeHistory", exception);
		}
	}

	/*
	 * @Transactional(readOnly = true) public List<CandidateHistory>
	 * getHotStatusDetails(Integer candidateId) { StringBuffer hql = new
	 * StringBuffer(); Map<String, Object> params = new HashMap<String,
	 * Object>(); hql.append("from CandidateHistory ch "); hql.append(
	 * " where ch.status='Hot' and ch.candidate.id=:candidateId");
	 * params.put("candidateId", candidateId); return (List<CandidateHistory>)
	 * candidateHistoryDao.findByQuery(hql.toString(), params); }
	 * 
	 * 
	 * @Transactional(readOnly = true) public List<CandidateHistory>
	 * getBlackStatusDetails(Integer candidateId) { StringBuffer hql = new
	 * StringBuffer(); Map<String, Object> params = new HashMap<String,
	 * Object>(); hql.append("from CandidateHistory ch "); hql.append(
	 * " where ch.status='Black' and ch.candidate.id=:candidateId");
	 * params.put("candidateId", candidateId); return (List<CandidateHistory>)
	 * candidateHistoryDao.findByQuery(hql.toString(), params); }
	 * 
	 * @Transactional(readOnly = true) public List<CandidateHistory>
	 * getDeletedStatusDetails(Integer candidateId) { StringBuffer hql = new
	 * StringBuffer(); Map<String, Object> params = new HashMap<String,
	 * Object>(); hql.append("from CandidateHistory ch "); hql.append(
	 * " where ch.status='Deleted' and ch.candidate.id=:candidateId");
	 * params.put("candidateId", candidateId); return (List<CandidateHistory>)
	 * candidateHistoryDao.findByQuery(hql.toString(), params); }
	 */

	@Override
	public void sendHotMail(IndiaCandidateHistory candidateHistory, String officeLoc) {
		try {
			String[] to = null;
			if (officeLoc.equalsIgnoreCase("HYD")) {
				to = UtilityBean.HYD_MAILS;
			} else if (officeLoc.equalsIgnoreCase("NOIDA")) {
				to = UtilityBean.NOIDA_MAILS;
			} else if (officeLoc.equalsIgnoreCase("PUNE")) {
				to = UtilityBean.PUNE_MAILS;
			} else {
				to = ArrayUtils.concat(UtilityBean.HYD_MAILS, UtilityBean.PUNE_MAILS, UtilityBean.NOIDA_MAILS);
			}

			String subject = "Candidate Hot List Information";

			SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");

			String modifiedDate = candidateHistory.getUpdatedOn() == null ? format.format(candidateHistory.getCreatedOn())
					: format.format(candidateHistory.getUpdatedOn());
			String modifiedBy = candidateHistory.getUpdatedBy() == null ? candidateHistory.getCreatedBy() : candidateHistory.getUpdatedBy();

			String content = "Hi,<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The  candidate having candidateId <b>" + candidateHistory.getCandidate().getId()
					+ "</b> moved to hot list by " + "<b>" + modifiedBy + "</b> on <b>" + modifiedDate + "</b>. "
					+ "Candidate details are as follows... <br><br>" + "<b>Name:</b>" + candidateHistory.getCandidate().getFullName() + " <br><b>Title:</b>"
					+ candidateHistory.getCandidate().getTitle() + " <br>" + "<b>Phone:</b>" + candidateHistory.getCandidate().getPhone() + " <br><b>Email:</b>"
					+ candidateHistory.getCandidate().getEmail() + " <br>" + "<b>Location:</b>" + candidateHistory.getCandidate().getAddress()
					+ "<br><b>Reason:</b>" + candidateHistory.getReason() + ".<br/><br/>"
					// + "You can access them at <a
					// href='http://localhost:8080/cgiats/hot_list_candidates.jsf'>Here</a><br><br><br><br>
					// "
					+ "<b>***This is an automatically generated email, please do not reply ***</b> ";
			try {
				commService.sendEmail(null, to, null, subject, content, null);
			} catch (ServiceException e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

	}

	@Override
	public void sendBlackListMail(IndiaCandidateHistory candidateHistory, String officeLoc) {
		try {
			String[] to = null;
			if (officeLoc.equalsIgnoreCase("HYD")) {
				to = UtilityBean.HYD_MAILS;
			} else if (officeLoc.equalsIgnoreCase("NOIDA")) {
				to = UtilityBean.NOIDA_MAILS;
			} else if (officeLoc.equalsIgnoreCase("PUNE")) {
				to = UtilityBean.PUNE_MAILS;
			} else {
				to = ArrayUtils.concat(UtilityBean.HYD_MAILS, UtilityBean.NOIDA_MAILS, UtilityBean.PUNE_MAILS);
			}
			String subject = "Candidate Black List Information";

			SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
			String modifiedDate = "";

			if (candidateHistory.getUpdatedOn() != null || candidateHistory.getCreatedOn() != null)
				modifiedDate = candidateHistory.getUpdatedOn() == null ? format.format(candidateHistory.getCreatedOn())
						: format.format(candidateHistory.getUpdatedOn());
			String modifiedBy = candidateHistory.getUpdatedBy() == null ? candidateHistory.getCreatedBy() : candidateHistory.getUpdatedBy();

			String content = "Hi,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; The candidate having candidateId: <b>" + candidateHistory.getCandidate().getId()
					+ "</b> moved to black list by " + "<b>" + modifiedBy + "</b> on <b>" + modifiedDate + "</b>. "
					+ "Candidate details are as follows <br><br> " + "<b>Name: </b>" + candidateHistory.getCandidate().getFullName() + "<br><b>Title: </b>"
					+ candidateHistory.getCandidate().getTitle() + "<br>" + "<b>Phone: </b>" + candidateHistory.getCandidate().getPhone() + "<br><b>Email: </b>"
					+ candidateHistory.getCandidate().getEmail() + "<br>" + "<b>Location: </b>" + candidateHistory.getCandidate().getAddress()
					+ "<br><b>Reason: </b>" + candidateHistory.getReason() + " .<br/><br/>"
					// + "You can access at <a href=
					// 'http://localhost:8080/cgiats/black_list_candidates.jsf'>here</a><br/><br/><br/><br/>"
					+ "<b>*** This is an automatically generated email, please do not reply ***</b> ";
			try {
				commService.sendEmail(null, to, null, subject, content, null);
			} catch (ServiceException e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

}

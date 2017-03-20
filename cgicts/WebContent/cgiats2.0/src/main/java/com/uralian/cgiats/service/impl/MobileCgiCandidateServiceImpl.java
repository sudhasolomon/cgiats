package com.uralian.cgiats.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.MobileCgiCandidatesDao;
import com.uralian.cgiats.dao.SubmittalDao;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.MobileCgiCandidates;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.service.MobileCgiCandidateService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.OnlineResumeStats;
import com.uralian.cgiats.util.Utils;

@Repository
@SuppressWarnings("unchecked")
@Service
@Transactional(rollbackFor = ServiceException.class)
public class MobileCgiCandidateServiceImpl implements MobileCgiCandidateService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private MobileCgiCandidatesDao mobileCgiCandidateDao;

	@Autowired
	private SubmittalDao submittalDao;

	/**
	 * @param onlineCgiCanidates
	 * @throws ServiceException
	 */

	@Override
	public void saveMobileCandidate(MobileCgiCandidates mobileCgiCanidates) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + mobileCgiCanidates);

			mobileCgiCandidateDao.save(mobileCgiCanidates);
			log.debug("mobileCgiCanidates save :" + mobileCgiCanidates);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist OnlineCgiCandidate", exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#updateCandidate(com.uralian
	 * .cgiats.model.Candidate)
	 */
	@Override
	public void updateCandidate(MobileCgiCandidates mobileCgiCanidates) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Updating " + mobileCgiCanidates);

			mobileCgiCandidateDao.update(mobileCgiCanidates);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to update OnlineCgiCandidate", exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateService#getCandidate(int,
	 * boolean, boolean)
	 */
	@Transactional(readOnly = true)
	public List<MobileCgiCandidates> getMobileCgiCandidates(String status, String userId) {
		try {
			log.info("From getMobileCgiCandidates  Start Service");
			StringBuffer hql = new StringBuffer();
			hql.append("select distinct c from MobileCgiCandidates c,JobOrder j  ");
			if (userId != null)
				hql.append(" where c.resumeStatus = :status and j.createdBy='" + userId + "' and j.id=c.orderId order by c.createdOn DESC");
			else
				hql.append(" where c.resumeStatus = :status and j.id=c.orderId order by c.createdOn DESC");
			List<MobileCgiCandidates> result = mobileCgiCandidateDao.findByQuery(hql.toString(), "status", status);
			log.info("From getMobileCgiCandidates  End Service");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.OnlineCgiCandidateService#
	 * getOnlineCgiCandidate (int)
	 */
	@Override
	@Transactional(readOnly = true)
	public MobileCgiCandidates getMobileCgiCandidate(int candidateId, int jobId) {
		try {
			if (log.isDebugEnabled())

			{
				/*
				 * log.debug("Job Opening id [" + openingId + "] - fetchFields["
				 * + fetchFields + "] fetchSubmittals[" + fetchSubmittals + "] "
				 * );
				 */
			}

			StringBuffer hql = new StringBuffer();
			hql.append("select distinct c from MobileCgiCandidates c");
			hql.append(" where c.candidateId.id = :candidateId and c.orderId.id=:jobOrderId  order by c.createdOn DESC");

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("candidateId", candidateId);
			params.put("jobOrderId", jobId);

			List<MobileCgiCandidates> result = mobileCgiCandidateDao.findByQuery(hql.toString(), params);
			System.out.println("hql------" + hql);
			System.out.println("result---------------" + result);
			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	/*
	 * public List<OnlineResumeStats> getOnlineResumeStats(Date dateStart, Date
	 * dateEnd){ List<OnlineResumeStats>
	 * lst=onlineCgiCanidateDao.getOnlineResumeStats(dateStart, dateEnd); return
	 * lst; }
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.CandidateService#deleteCandidate(int)
	 */
	@Override
	public void deleteCandidate(int candidateId) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Deleting candidate with id [" + candidateId + "]");

			// Fetching candidate to delete it.
			MobileCgiCandidates candidate = mobileCgiCandidateDao.findById(candidateId);
			if (candidate != null) {
				mobileCgiCandidateDao.delete(candidate);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Error while deleting candidate", e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<MobileCgiCandidates> getCandidate(Integer orderId, Integer candidateId) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();

			hql.append("from MobileCgiCandidates mc");
			hql.append(" where mc.orderId.id = :orderId and mc.candidateId.id= :candidateId ");
			hql.append(" order by coalesce(mc.updatedOn,mc.createdOn) desc");
			params.put("orderId", orderId);
			params.put("candidateId", candidateId);
			System.out.println("hql>>>>>>>>" + hql);
			return mobileCgiCandidateDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Map<String, String>> findCandidateJobList(Integer candidateId) {
		try {
			List<Submittal> subStatus = null;
			List<?> result = mobileCgiCandidateDao.findCandidateJobList(candidateId);
			List<Map<String, String>> recordsList = new ArrayList<Map<String, String>>();
			Map<String, String> record = new HashMap<String, String>();
			for (Object obj : result) {
				record = new HashMap<String, String>();
				Object[] tuple = (Object[]) obj;

				Object str1 = tuple[0];
				Object str2 = tuple[1];

				MobileCgiCandidates mobileCgiCandidates = (MobileCgiCandidates) str1;
				JobOrder jobOrder = (JobOrder) str2;
				record.put("jobOrderId", jobOrder.getId().toString());
				record.put("jobOrderTitle", jobOrder.getTitle().toString());
				record.put("jobOrderCity", jobOrder.getCity().toString());
				record.put("jobOrderState", jobOrder.getState().toString());
				// String Jobdate=new
				// SimpleDateFormat("MM-dd-yyyy").format(jobOrder.getUpdatedOn()!=null?jobOrder.getUpdatedOn():jobOrder.getCreatedOn());
				String Jobdate = new SimpleDateFormat("MM-dd-yyyy").format(jobOrder.getCreatedOn());
				record.put("jobOrderDate", Jobdate);
				record.put("jobOrderDes", jobOrder.getDescription().toString());
				System.out.println("mobileStatus" + mobileCgiCandidates.getResumeStatus());
				if ("ACCEPTED".equalsIgnoreCase(mobileCgiCandidates.getResumeStatus())) {

					System.out.println("jobOrder.getId()===========" + jobOrder.getId() + "============candidateId========" + candidateId);
					subStatus = submittalDao.mobileSubmittalStatus(jobOrder.getId(), candidateId);
					if (subStatus != null && subStatus.size() > 0) {
						Submittal submital = subStatus.get(0);
						record.put("mobileCandidateStatus", submital.getStatus().name());
					}
					System.out.println("subStatus---->" + subStatus);
				} else {
					record.put("mobileCandidateStatus", mobileCgiCandidates.getResumeStatus().toString());
				}
				// record.put("mobileCandidateStatus",mobileCgiCandidates.getResumeStatus().toString());
				// String mobileCandidateDate=new
				// SimpleDateFormat("MM-dd-yyyy").format(mobileCgiCandidates.getUpdatedOn()!=null?mobileCgiCandidates.getUpdatedOn():mobileCgiCandidates.getCreatedOn());
				String mobileCandidateDate = new SimpleDateFormat("MM-dd-yyyy").format(mobileCgiCandidates.getCreatedOn());
				record.put("mobileCandidateDate", mobileCandidateDate);
				recordsList.add(record);

			}

			return recordsList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

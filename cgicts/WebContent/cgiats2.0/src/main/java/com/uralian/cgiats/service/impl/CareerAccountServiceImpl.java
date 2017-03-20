package com.uralian.cgiats.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.CareerAccountDao;
import com.uralian.cgiats.dao.MobileCgiCandidatesDao;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CarrerAccount;
import com.uralian.cgiats.model.MobileCgiCandidates;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.service.CareerAccountService;
import com.uralian.cgiats.service.ServiceException;

@Service
@Transactional(rollbackFor = ServiceException.class)
public class CareerAccountServiceImpl implements CareerAccountService {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private CareerAccountDao careerAccountDao;

	@Autowired
	private MobileCgiCandidatesDao mobileCgiCandidatesDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.CandidateService#saveCandidate(com.uralian.
	 * cgiats .model.Candidate)
	 */
	@Override
	public Candidate saveCareerAccount(CarrerAccount carrerAccount) throws ServiceException {
		Candidate rtnCandidate = null;
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + carrerAccount);
			careerAccountDao.save(carrerAccount);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist candidate", exception);
		}
		return rtnCandidate;
	}

	@Override
	public List<CarrerAccount> getCareerAccountFromEmail(String emailId, String password) {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select c from CarrerAccount c where c.email = :emailId and c.careerPwd=:password");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("emailId", emailId);
			params.put("password", password);

			List<CarrerAccount> result = careerAccountDao.findByQuery(hql.toString(), params);
			/*
			 * List<CarrerAccount> result =
			 * careerAccountDao.findByQuery(hql.toString(), "emailId", emailId);
			 */

			System.out.println(result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public CarrerAccount getCareerFromEmail(String emailId, String password) {
		try {
			CarrerAccount carrerAccount = null;
			StringBuffer hql = new StringBuffer();
			hql.append("select c from CarrerAccount c where c.email = :emailId and c.careerPwd=:password");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("emailId", emailId);
			params.put("password", password);

			List<CarrerAccount> result = careerAccountDao.findByQuery(hql.toString(), params);

			/*
			 * List<CarrerAccount> result =
			 * careerAccountDao.findByQuery(hql.toString(), "emailId", emailId);
			 */
			if (result != null && result.size() > 0) {
				carrerAccount = result.get(0);
			}
			System.out.println(carrerAccount);
			return carrerAccount;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

	public CarrerAccount getEmailFromCareerAcct(String emailId) {
		try {
			CarrerAccount carrerAccount = null;
			StringBuffer hql = new StringBuffer();
			hql.append("select c from CarrerAccount c where c.email = :emailId ");

			List<CarrerAccount> result = careerAccountDao.findByQuery(hql.toString(), "emailId", emailId);
			if (result != null && result.size() > 0) {
				carrerAccount = result.get(0);
			}
			System.out.println(carrerAccount);
			return carrerAccount;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

	public void updateCarrerAccount(CarrerAccount carrerAccount) throws ServiceException {

		try {
			if (log.isDebugEnabled())
				log.debug("Updating " + carrerAccount);

			careerAccountDao.update(carrerAccount);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to update CarrerAccountCandidate", exception);
		}
	}

	public User getExecutiveUser(String userId, String password) {
		User user = null;
		return user;
	}

	/*
	 * @Override public Candidate saveCareerAccount(CarrerAccount carrerAccount)
	 * throws ServiceException { Candidate rtnCandidate = null; try { if
	 * (log.isDebugEnabled()) log.debug("Persisting " + carrerAccount);
	 * careerAccountDao.save(carrerAccount); } catch (Exception exception) {
	 * throw new ServiceException("Error while trying to persist candidate",
	 * exception); } return rtnCandidate; }
	 */

	/*
	 * @Override public MobileCgiCandidates savemobileCandidate(
	 * MobileCgiCandidates mobileCgiCandidates) { // TODO Auto-generated method
	 * stub
	 * 
	 * MobileCgiCandidates rtnmobileCandidate = null; try { if
	 * (log.isDebugEnabled()) log.debug("Persisting " + mobileCgiCandidates);
	 * mobileCgiCandidatesDao.save(mobileCgiCandidates); } catch (Exception
	 * exception) { try { throw new ServiceException(
	 * "Error while trying to persist candidate", exception); } catch
	 * (ServiceException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 * 
	 * 
	 * return rtnmobileCandidate; }
	 */

	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = true) public List<MobileCgiCandidates>
	 * getCandidate(Integer orderId, Integer candidateId) { // TODO
	 * Auto-generated method stub
	 * 
	 * StringBuffer hql = new StringBuffer(); Map<String,Object> params = new
	 * HashMap<String, Object>();
	 * 
	 * hql.append("from MobileCgiCandidates mc"); hql.append(
	 * " where mc.orderId = :orderId and mc.candidateId= :candidateId ");
	 * params.put("orderId",orderId); params.put("candidateId",candidateId);
	 * System.out.println("hql>>>>>>>>"+hql); return
	 * mobileCgiCandidatesDao.findByQuery(hql.toString(),params); }
	 */

	/*
	 * @Override
	 * 
	 * @Transactional(readOnly = true) public List<Map<String,String>>
	 * findCandidateJobList(Integer candidateId) { // TODO Auto-generated method
	 * stub
	 * 
	 * return careerAccountDao.findCandidateJobList(candidateId); }
	 */

}
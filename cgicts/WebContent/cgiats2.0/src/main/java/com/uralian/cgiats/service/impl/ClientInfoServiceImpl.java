package com.uralian.cgiats.service.impl;

import java.util.ArrayList;
/*
 * Radhika
 * Created Date:8/5/2013
 * Comment: Created to store client info for the tickets ATS-
 */
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.AgencyDetailsDao;
import com.uralian.cgiats.dao.CandidateDao;
import com.uralian.cgiats.dao.CandidateInfoDao;
import com.uralian.cgiats.dao.ClientInfoDao;
import com.uralian.cgiats.dto.ClientInfoDto;
import com.uralian.cgiats.model.AgencyDetails;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateInfo;
import com.uralian.cgiats.model.ClientInfo;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.OrderByType;
import com.uralian.cgiats.service.ClientInfoService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.TransformDtoToEntity;
import com.uralian.cgiats.util.TransformEntityToDto;
import com.uralian.cgiats.util.Utils;

@Service
@Transactional(rollbackFor = ServiceException.class)
public class ClientInfoServiceImpl implements ClientInfoService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ClientInfoDao clientInfoDao;

	@Autowired
	private AgencyDetailsDao agencyDetailsDao;

	@Autowired
	private CandidateInfoDao candidateInfoDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.ClientInfoService#getListClientInfo()
	 */

	/*
	 * @Override public List<ClientInfoDto> getListClientInfo() { StringBuffer
	 * hql = new StringBuffer();
	 * 
	 * List<ClientInfo> clientInfo = clientInfoDao.findAll(); return clientInfo;
	 * }
	 */
	@Override
	public List<ClientInfoDto> getListClientInfo() {
		try {
			List<ClientInfoDto> clientListDto = new ArrayList<ClientInfoDto>();

			StringBuffer hql = new StringBuffer();
			hql.append(
					"select c.candidateinfo.candidate.id, c.candidateinfo.jobOrderId, c.candidateinfo.firstName,c.candidateinfo.lastName, c.candidateinfo.email, c.clientName,");
			hql.append(
					" c.candidateinfo.salaryRate, c.candidateinfo.startDate, c.candidateinfo.endDate, c.candidateinfo.id from ClientInfo c where c.candidateinfo.candidate.deleteFlag=0");
			hql.append(" ORDER BY COALESCE(c.candidateinfo.updatedOn,c.candidateinfo.createdOn)");
			OrderByType orderType = OrderByType.DESC;
			hql.append(orderType.getValue());
			List<?> clientInfo = clientInfoDao.findByQuery(hql.toString(), 0, 0);
			if (clientInfo != null) {
				Iterator itr = clientInfo.iterator();
				while (itr.hasNext()) {
					ClientInfoDto clientDto = new ClientInfoDto();
					Object Client[] = (Object[]) itr.next();

					final String id = String.valueOf(Client[0]);
					final String jobOrderId = String.valueOf(Client[1]);
					final String firstName = (String) Client[2];
					final String lastName = (String) Client[3];
					final String email = (String) Client[4];
					final String clientName = (String) Client[5];
					final String salaryRate = (String) Client[6];
					final Date startDate = (Date) Client[7];
					final Date endDate = (Date) Client[8];
					final String candidateInfoId = String.valueOf(Client[9]);
					StringBuffer fullName = new StringBuffer();
					if (!Utils.isBlank(firstName) || !Utils.isBlank(lastName)) {
						fullName.append(firstName != null ? firstName : "");
						fullName.append(" ");
						fullName.append(lastName != null ? lastName : "");
					}
					if (id != null) {
						clientDto.setCandidateId(id);
					}
					clientDto.setJobOrderId(jobOrderId != null ? jobOrderId : "");
					clientDto.setFullName(fullName != null ? fullName.toString() : "");
					clientDto.setEmail(email != null ? email : "");
					clientDto.setClientName(clientName != null ? clientName : "");
					clientDto.setSalaryRate(salaryRate != null ? salaryRate : "");
					clientDto.setStartDate(startDate != null ? Utils.convertDateToString(startDate) : "");
					clientDto.setEndDate(endDate != null ? Utils.convertDateToString(endDate) : "");
					clientDto.setCandidateInfoId(candidateInfoId != null ? candidateInfoId : "");
					clientListDto.add(clientDto);

				}
			}

			return clientListDto;
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
	 * com.uralian.cgiats.service.CandidateService#saveCandidate(com.uralian.
	 * cgiats .model.Candidate)
	 */
	@Override
	public void saveClientInfo(ClientInfo clientInfo) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + clientInfo);

			clientInfoDao.save(clientInfo);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			exception.printStackTrace();
			throw new ServiceException("Error while trying to persist candidate", exception);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.ClientInfoService#updateClientInfo(com.uralian
	 * .cgiats.model.ClientInfo)
	 */
	@Override
	public void updateClientInfo(ClientInfo clientInfo) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + clientInfo);

			clientInfoDao.update(clientInfo);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			exception.printStackTrace();
			throw new ServiceException("Error while trying to persist candidate", exception);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.ClientInfoService#getClientInfo(java.lang.
	 * Integer)
	 */
	@Override
	public ClientInfo getClientInfo(Integer id) {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select c from ClientInfo c");
			hql.append(" where c.candidateinfo.id = :id");

			List<ClientInfo> result = clientInfoDao.findByQuery(hql.toString(), "id", id);
			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * @author Radhika ATS-254
	 */
	@Override
	public List<ClientInfo> getCandidateContractPeriod(Date dateStart, Date dateEnd) {
		try {
			return clientInfoDao.getCandidateContractPeriod(dateStart, dateEnd);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void saveUpdateClient(ClientInfoDto projectDto) {
		try {

			// Candidate candidate = null;
			CandidateInfo candidateInfo = null;
			ClientInfo clientInfo = null;
			AgencyDetails agencyDetails = null;
			Candidate candidate = null;
			if (!Utils.isBlank(projectDto.getCandidateInfoId())) {
				clientInfo = (ClientInfo) clientInfoDao.getObjectByCandidateInfoId(Integer.parseInt(projectDto.getCandidateInfoId()), ClientInfo.class);
				candidateInfo = clientInfo.getCandidateinfo();
				candidate = candidateInfo.getCandidate();
				agencyDetails = (AgencyDetails) clientInfoDao.getObjectByCandidateInfoId(Integer.parseInt(projectDto.getCandidateInfoId()),
						AgencyDetails.class);
				TransformDtoToEntity.getFilledProjectDetails(candidate, candidateInfo, clientInfo, agencyDetails, projectDto);
				clientInfoDao.update(clientInfo);
				agencyDetailsDao.update(agencyDetails);
			} else {
				candidateInfo = new CandidateInfo();
				clientInfo = new ClientInfo();
				agencyDetails = new AgencyDetails();
				candidate = projectDto.getCandidate();
				candidateInfo.setCreatedOn(new Date());
				clientInfo.setCreatedOn(new Date());
				agencyDetails.setCreatedOn(new Date());
				clientInfo.setCreatedBy(Utils.replaceNullWithEmpty(projectDto.getCreatedBy()));
				candidateInfo.setCreatedBy(Utils.replaceNullWithEmpty(projectDto.getCreatedBy()));
				agencyDetails.setCreatedBy(Utils.replaceNullWithEmpty(projectDto.getCreatedBy()));
				TransformDtoToEntity.getFilledProjectDetails(candidate, candidateInfo, clientInfo, agencyDetails, projectDto);
				candidateInfoDao.save(candidateInfo);
				clientInfoDao.save(clientInfo);
				agencyDetailsDao.save(agencyDetails);

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}
}

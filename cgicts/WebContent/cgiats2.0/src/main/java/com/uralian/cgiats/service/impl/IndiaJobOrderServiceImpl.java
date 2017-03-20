package com.uralian.cgiats.service.impl;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.time.DateUtils;
import org.primefaces.util.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.uralian.cgiats.dao.IndiaCandidateDao;
import com.uralian.cgiats.dao.IndiaClientNamesDao;
import com.uralian.cgiats.dao.IndiaJobOrdersDao;
import com.uralian.cgiats.dao.IndiaSubmittalDao;
import com.uralian.cgiats.dao.IndiaSubmittalDeletionDao;
import com.uralian.cgiats.dao.IndiaSubmittalEventDao;
import com.uralian.cgiats.dao.IndiaUserDao;
import com.uralian.cgiats.dto.AddEditJobOrderDto;
import com.uralian.cgiats.dto.IndiaSubmittalStatsDto;
import com.uralian.cgiats.dto.JobOrderDto;
import com.uralian.cgiats.dto.JobOrderFieldDto;
import com.uralian.cgiats.dto.JobOrderStatusDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.dto.SubmittalEventDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.CandidateStatus;
import com.uralian.cgiats.model.CandidateStatuses;
import com.uralian.cgiats.model.ClientNames;
import com.uralian.cgiats.model.IndiaCandidate;
import com.uralian.cgiats.model.IndiaCandidateStatuses;
import com.uralian.cgiats.model.IndiaJobOrder;
import com.uralian.cgiats.model.IndiaSubmittal;
import com.uralian.cgiats.model.IndiaSubmittalDeletion;
import com.uralian.cgiats.model.IndiaSubmittalEvent;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.JobOrderSearchDto;
import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.service.CommService;
import com.uralian.cgiats.service.CommService.AttachmentInfo;
import com.uralian.cgiats.service.IndiaJobOrderService;
import com.uralian.cgiats.service.IndiaUserService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.AppConfig;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.TransformDtoToEntity;
import com.uralian.cgiats.util.TransformDtoToEntityForIndia;
import com.uralian.cgiats.util.Utils;
import com.uralian.cgiats.web.UIBean;
import com.uralian.cgiats.web.UtilityBean;

@Service
@Transactional(rollbackFor = ServiceException.class)
public class IndiaJobOrderServiceImpl implements IndiaJobOrderService {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private IndiaClientNamesDao clientNamesDao; 
	
	@Autowired
	private IndiaJobOrdersDao orderDao;
	@Autowired
	private IndiaJobOrdersDao indiaJobOrdersDao;
	@Autowired
	private IndiaCandidateDao indiaCandidateDao;
	@Autowired
	IndiaSubmittalDao indiaSubmittalDao;
	@Autowired
	IndiaSubmittalDeletionDao indiaSubmittalDeletionDao;

	@Autowired
	private IndiaUserDao userDao;
	@Autowired
	private IndiaUserService userService;
	@Autowired
	private CommService commService;
	@Autowired
	private IndiaSubmittalEventDao indiaSubmittalEventDao;

	@Override
	@Transactional(readOnly = true)
	/*
	 * public List<IndiaJobOrder> searchJobOrders(JobOrderSearchDto criteria) {
	 * 
	 * StringBuffer hqlSelect = new StringBuffer(
	 * "select j from IndiaJobOrder j "); Map<String, Object> params = new
	 * HashMap<String, Object>();
	 * 
	 * 
	 * if (!Utils.isEmpty(criteria.getFields())) hqlSelect.append(
	 * ", IndiaJobOrderField jf ");
	 * 
	 * StringBuffer hqlWhere = new StringBuffer(" where 1=1 ");
	 * buildWhereClause(criteria, hqlWhere, params);
	 * 
	 * hqlSelect.append(hqlWhere);
	 * 
	 * hqlSelect.append(" order by COALESCE(j.updatedOn,j.createdOn) DESC");
	 * 
	 * log.info("The final IndiaJobOrders search Query is......"+hqlSelect);
	 * 
	 * return indiaJobOrdersDao.findByQuery(hqlSelect.toString(), params);
	 * 
	 * }
	 */

	public List<?> searchJobOrders(JobOrderSearchDto criteria, UserDto loginUser, int first, int pageSize) {
		// "select j.id,j.priority,j.status,j.jobType,j.title,j.customer,j.city
		// ||', '||
		// j.state,j.createdBy,j.assignedTo,j.createdOn,j.updatedOn,j.keySkills,j.hot,j.reason,j.customerHidden
		// from JobOrder j,User u "

		/*
		 * StringBuffer hqlSelect = new StringBuffer(
		 * "select jo from IndiaJobOrder jo ");
		 */
		StringBuffer hqlSelect = new StringBuffer(
				"select j.id,j.priority,j.status,j.jobType,j.title,j.customer,j.city ,j.createdBy,j.assignedTo,j.createdOn,j.updatedOn,j.keySkills,j.hot,j.reason,j.customerHidden,j.numOfPos, j.numOfRsumes,j.dmName from IndiaJobOrder j ");
		Map<String, Object> params = new HashMap<String, Object>();

		if (!Utils.isEmpty(criteria.getFields()))
			hqlSelect.append(", IndiaJobOrderField jf ");

		StringBuffer hqlWhere = new StringBuffer(" where j.deleteFlag=0 ");
		buildWhereClause(criteria, hqlWhere, params);

		hqlSelect.append(hqlWhere);

		hqlSelect.append(" order by COALESCE(j.updatedOn,j.createdOn) DESC");

		log.info("The final IndiaJobOrders search Query is......" + hqlSelect);

		List<?> jobOrders = orderDao.findByQuery(hqlSelect.toString(), first, pageSize, params);

		List<JobOrderDto> jobOrderDtoList = new ArrayList<JobOrderDto>();

		getJobOrderDtoInf(jobOrderDtoList, jobOrders);

		// Finding submittals and job additional fields for each job order
		if (jobOrderDtoList != null && jobOrderDtoList.size() > 0) {
			Iterator<?> it = null;
			List<Integer> jobOrderIds = new ArrayList<Integer>();
			for (JobOrderDto dto : jobOrderDtoList) {
				jobOrderIds.add(dto.getJobOrderId());
			}
			params = new HashMap<String, Object>();

			String hqlQuery = "select s.id,s.jobOrder.id from IndiaSubmittal s where s.deleteFlag=0 and s.jobOrder.id in ?1";
			params.put("1", jobOrderIds);
			List<?> jobSubmittalList = orderDao.findByQuery(hqlQuery, -1, -1, params);
			if (jobSubmittalList != null && jobSubmittalList.size() > 0) {
				Map<Integer, Integer> jobOrderIdWithSubmittalCountMap = new HashMap<Integer, Integer>();
				it = jobSubmittalList.iterator();
				while (it.hasNext()) {
					Object jobSubmittalObj[] = (Object[]) it.next();
					Integer orderId = Integer.parseInt(jobSubmittalObj[1].toString());
					if (jobOrderIdWithSubmittalCountMap.get(orderId) != null) {
						jobOrderIdWithSubmittalCountMap.put(orderId, jobOrderIdWithSubmittalCountMap.get(orderId) + 1);
					} else {
						jobOrderIdWithSubmittalCountMap.put(orderId, 1);
					}
				}
				for (JobOrderDto dto : jobOrderDtoList) {
					if (jobOrderIdWithSubmittalCountMap.get(dto.getJobOrderId()) != null) {
						dto.setSbm(String.valueOf(jobOrderIdWithSubmittalCountMap.get(dto.getJobOrderId())));
					} else {
						dto.setSbm("0");
					}
				}
			} else {
				for (JobOrderDto dto : jobOrderDtoList) {
					dto.setSbm("0");
				}
			}

			// Finding job additional fields list
			hqlQuery = "select jof.id,jof.fieldName,jof.fieldValue,jof.visible,jof.indiaJobOrder.id from IndiaJobOrderField jof where jof.indiaJobOrder.id in ?1 and (jof.fieldValue != null and jof.fieldValue != '') and jof.visible = true";
			params.put("1", jobOrderIds);
			List<?> jobOrderFieldList = orderDao.findByQuery(hqlQuery, -1, -1, params);

			if (jobOrderFieldList != null && jobOrderFieldList.size() > 0) {
				Map<Integer, List<JobOrderFieldDto>> jobOrderIdWithOrderFieldListMap = new HashMap<Integer, List<JobOrderFieldDto>>();
				it = jobOrderFieldList.iterator();
				while (it.hasNext()) {
					Object jobOrderFieldObj[] = (Object[]) it.next();
					JobOrderFieldDto dto = new JobOrderFieldDto();
					dto.setId(Integer.parseInt(jobOrderFieldObj[0].toString()));
					dto.setFieldName(jobOrderFieldObj[1].toString());
					dto.setFieldValue(jobOrderFieldObj[2].toString());
					dto.setVisible((Boolean) jobOrderFieldObj[3]);
					dto.setOrder_Id((Integer) jobOrderFieldObj[4]);

					if (jobOrderIdWithOrderFieldListMap.get(dto.getOrder_Id()) != null) {
						jobOrderIdWithOrderFieldListMap.get(dto.getOrder_Id()).add(dto);
					} else {
						List<JobOrderFieldDto> list = new ArrayList<JobOrderFieldDto>();
						list.add(dto);
						jobOrderIdWithOrderFieldListMap.put(dto.getOrder_Id(), list);
					}
				}
				for (JobOrderDto dto : jobOrderDtoList) {
					dto.setJobOrderFieldDtoList(jobOrderIdWithOrderFieldListMap.get(dto.getJobOrderId()));
				}
			}
		}
		// return indiaJobOrdersDao.findByQuery(hqlSelect.toString(), params);
		return jobOrderDtoList;

	}

	private void buildWhereClause(JobOrderSearchDto criteria, StringBuffer hql, Map<String, Object> params) {
		log.info("Inside buildWhereClause :: Selected Bdm : " + criteria.getBdm());
		String user = criteria.getAssignedTo();

		// if (!Utils.isBlank(user) && (!Utils.isBlank(criteria.getBdm()))) {
		if (!Utils.isBlank(user)) {
			if (criteria.getAdmName() != null) {
				hql.append(" and j.createdBy = :admName");
				params.put("admName", criteria.getAdmName());
			} else {
				User userObj = userService.loadUser(user);

				if (userObj != null && criteria.isDmJobOrders()) {
					hql.append(" and (j.dmName = :dmName or j.createdBy = :dmName)");
					params.put("dmName", userObj.getAssignedBdm());
				} else if (userObj != null && (userObj.isDM() || userObj.isADM())) {
					hql.append(" and (j.dmName = :user or j.createdBy = :user)");
					params.put("user", user);
				} /*
					 * else if (userObj != null && userObj.isRecruiter()) {
					 * hql.append(
					 * " and (j.createdBy=:assignedBdm or j.assignedToUser.id=:user or j.assignedToUser.id=:assignedBdm)"
					 * ); params.put("assignedBdm", userObj.getAssignedBdm());
					 * params.put("user", user); }
					 */
				else if (userObj != null && userObj.isIn_Recruiter()) {
//					hql.append(
//							" and (j.createdBy=:assignedBdm or j.createdBy=:user or j.assignedToUser.id=:assignedBdm  or j.assignedToUser.id=:user or j.assignedTo = :user)");
					hql.append(
							" and (j.createdBy=:assignedBdm or j.assignedToUser.id=:user or j.assignedToUser.id=:assignedBdm or j.dmName = :assignedBdm)");
					params.put("assignedBdm", userObj.getAssignedBdm());
					params.put("user", user);
				} else {
//					hql.append(" and (j.createdBy = :user or j.assignedToUser.id=:user)");
					hql.append(" and (j.dmName = :user or j.createdBy = :user or j.assignedToUser.id=:user)");
					params.put("user", user);
				}
			}
		} else if (criteria.getBdm() != null) {
//			hql.append(" and j.createdBy=:bdm");
			hql.append(" and (j.createdBy=:bdm or j.dmName = :bdm)");
			params.put("bdm", criteria.getBdm());
		}

		if (!Utils.isEmpty(criteria.getPriorities())) {
			hql.append(" and j.priority in (:priorities) ");
			params.put("priorities", criteria.getPriorities());
		}
		if (!Utils.isEmpty(criteria.getStatuses())) {
			hql.append(" and j.status in (:statuses) ");
			params.put("statuses", criteria.getStatuses());
		}
		if (!Utils.isEmpty(criteria.getJobTypes())) {
			hql.append(" and j.jobType in (:jobTypes) ");
			params.put("jobTypes", criteria.getJobTypes());
		}

		Date startDate = criteria.getStartEntryDate();
		if (startDate != null) {
			hql.append(" and j.createdOn >= :startDate");
			params.put("startDate", startDate);
		}
		Date endDate = criteria.getEndEntryDate();
		if (endDate != null) {
			hql.append(" and j.createdOn <= :endDate");
			params.put("endDate", DateUtils.addDays(endDate, 1));
			// params.put("endDate", endDate);
		}

		if (criteria.getJobBelongsTo() != null && criteria.getJobBelongsTo().size() > 0) {
			hql.append(" and j.companyFlag in(:jobbelongs)");

			params.put("jobbelongs", criteria.getJobBelongsTo());
		}

		if (criteria.getJobOrderId() != null) {
			hql.append(" and j.id = :jobOrderId");

			params.put("jobOrderId", criteria.getJobOrderId());
		}

		// add fields
		Map<String, String> props = criteria.getFields();
		if (!Utils.isEmpty(props)) {
			for (Map.Entry<String, String> entry : props.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				hql.append(" and j.id = (select j1.jobOpening.id from IndiaJobOrderField j1 ");
				hql.append(" where j1.fieldName = '").append(key).append("'");
				hql.append(" and j1.fieldValue = '").append(value).append("')");
			}
		}

		log.info("End buildWhereClause :: Selected Bdm : " + criteria.getBdm());
	}

	@Override
	public IndiaJobOrder getJobOrder(Integer orderId, boolean fetchFields, boolean fetchSubmittals) {

		StringBuffer hql = new StringBuffer();
		hql.append("select distinct j from IndiaJobOrder j");
		if (fetchFields)
			hql.append(" left join fetch j.fields");
		if (fetchSubmittals)
			hql.append(" left join fetch j.submittals");
		hql.append(" where j.id = :orderId");

		List<IndiaJobOrder> result = indiaJobOrdersDao.findByQuery(hql.toString(), "orderId", orderId);
		return !Utils.isEmpty(result) ? result.get(0) : null;
	}

	@Override
	public void saveJobOrder(IndiaJobOrder indiaJobOrder) throws ServiceException {

		try {
			indiaJobOrdersDao.save(indiaJobOrder);
		} catch (Exception e) {
			throw new ServiceException("Error while trying to persist Job Order", e);
		}

		// notfy job order to assinged user if it is ASSIGNED
		if (indiaJobOrder.getStatus() == JobOrderStatus.ASSIGNED)
			notifyAssigned(indiaJobOrder);

		notifyNewJobOrder(indiaJobOrder);
	}

	@Override
	public JobOrderDto saveOrder(IndiaJobOrder jobOrder) {
		jobOrder.setCreatedOn(new Date());
		IndiaJobOrder order = indiaJobOrdersDao.saveOrder(jobOrder);

		JobOrderDto dto = new JobOrderDto();
		dto.setJobOrderId(order.getId());
		dto.setPriority(order.getPriority().toString());
		dto.setStatus(order.getStatus().toString());
		dto.setType(order.getJobType().toString());
		dto.setClient(order.getCustomer());
		dto.setLocation(order.getLocation());

		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
		Date jobOrderCreatedOn = (Date) order.getCreatedOn();
		dto.setUpdatedDate(format.format(jobOrderCreatedOn));
		dto.setDm(order.getCreatedBy());
		if (order.getAssignedTo() != null) {
			dto.setAssignedTo(order.getAssignedToUser().getFullName());
		}
		// Caluclating Active Days
		if (jobOrderCreatedOn != null) {
			dto.setActiveDays(Utils.findActiveDays(jobOrderCreatedOn));
		}
		return dto;
	}

	private void notifyNewJobOrder(IndiaJobOrder jobOrder) throws ServiceException {

		String email = AppConfig.getInstance().getJoNotifyEmail();
		if (!Utils.isBlank(email)) {
			log.debug("Sending notification email for new job order: " + email);
			String[] to = email.split("[\\p{Space},]+");

			StringBuffer sb = new StringBuffer();
			sb.append("Hi &nbsp;");
			sb.append(",<br><br/>A job order #[ OrderId: <b>" + jobOrder.getId() + "</b>, Title: <b>" + jobOrder.getTitle() + "</b>, Created By: <b>"
					+ jobOrder.getCreatedBy() + "</b>] ").append(" has been created. You can access it ");
			sb.append("<a href='").append(UIBean.getBaseUrl()).append("/edit_order.jsf?orderId=").append(jobOrder.getId())
					.append("'>here</a><br/><br/><br/><b>*** This is an automatically generated email, please do not reply ***</b>");

			commService.sendEmail(null, to, null, "New Job Order created in ATS: " + jobOrder.getTitle(), sb.toString(), new AttachmentInfo[0]); // Modified
																																					// as
																																					// per
																																					// Ken
																																					// request
																																					// (ATS
																																					// -46)

		}
	}

	protected void notifyAssigned(IndiaJobOrder order) throws ServiceException {
		User recruiter = order.getAssignedToUser();
		String email = recruiter != null ? recruiter.getEmail() : null;
		if (!Utils.isBlank(email)) {

			if (email.contains("@")) {
				log.debug("Sending email to assigned user: " + email);
				StringBuffer sb = new StringBuffer();
				sb.append("Hi &nbsp;");
				if (order.getAssignedTo() != null)
					sb.append(order.getAssignedToUser().getFullName());
				sb.append(",<br><br/>A job order #[ OrderId: <b>" + order.getId() + "</b>, Title: <b>" + order.getTitle() + "</b>, Created By: <b>"
						+ order.getCreatedBy() + "</b>] ").append(" has been assigned to you.");
				/*
				 * sb.append("<a href='").append(UIBean.getBaseUrl())
				 * .append("/edit_india_submittal.jsf?orderId=").append(order.
				 * getId());
				 */
				sb.append("'>here</a><br/><br/><br/><b>*** This is an automatically generated email, please do not reply ***</b>");
				commService.sendEmail(null, email.trim(), "'" + order.getTitle() + "'" + " Job Order #" + order.getId() + " has been assigned", sb.toString()); // modified
																																								// as
																																								// per
																																								// Ken
																																								// request
																																								// -
																																								// ATS
																																								// -46
			} else {
				log.warn("Emailaddress is not in correct format.Please verify:::" + email);
			}
		}
	}

	@Override
	public void updateJobOrder(IndiaJobOrder indiaJobOrder) throws ServiceException {

		IndiaJobOrder oldOrder = getJobOrder(indiaJobOrder.getId(), false, false);
		JobOrderStatus status = oldOrder.getStatus();

		try {
			indiaJobOrdersDao.update(indiaJobOrder);
		} catch (Exception e) {
			throw new ServiceException("Error while trying to persist Job Order", e);
		}

		// send email to whom the order was assigned to
		if (!status.equals(JobOrderStatus.ASSIGNED) && indiaJobOrder.getStatus().equals(JobOrderStatus.ASSIGNED)) {
			// notifyAssigned(indiaJobOrder);
		}
	}

	@Override
	public List<String> listExistingTitles() {
		// TODO Auto-generated method stub
		return indiaJobOrdersDao.listExistingTitles();
	}

	@Override
	public List<String> listExistingCustomers() {
		// TODO Auto-generated method stub
		return indiaJobOrdersDao.listExistingCustomers();
	}

	@Override
	public List<String> listExistingCities() {
		// TODO Auto-generated method stub
		return indiaJobOrdersDao.listExistingCities();
	}

	@Override
	@Transactional(readOnly = true)
	public List<IndiaSubmittal> getSubmittalDetails(int candidateId) throws ServiceException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append("from IndiaSubmittal s");
		hql.append(" where s.candidate.id =:candidateId and deleteFlag!=1");
		params.put("candidateId", candidateId);
		// log.info("submittal details---"+hql.toString());
		return indiaSubmittalDao.findByQuery(hql.toString(), params);

	}

	@Override
	@Transactional(readOnly = true)
	public List<IndiaSubmittal> getCandidate(Integer orderId, Integer candidateId) {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();

		hql.append("from IndiaSubmittal o");
		hql.append(" where o.jobOrder.id = :orderId and o.candidate.id= :candidateId and o.deleteFlag != 1");
		params.put("orderId", orderId);
		params.put("candidateId", candidateId);
		return indiaSubmittalDao.findByQuery(hql.toString(), params);
	}

	@Override
	@Transactional(readOnly = true)
	public List<IndiaSubmittal> getCandidateSubDetails(Integer candidateId) {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append("from IndiaSubmittal o");
		hql.append(" where o.candidate.id= :candidateId and o.deleteFlag != 1");
		params.put("candidateId", candidateId);
		return indiaSubmittalDao.findByQuery(hql.toString(), params);
	}

	@Override
	public void updateSubmittal(IndiaSubmittal submittal) throws ServiceException {
		/*
		 * // try // { if (log.isDebugEnabled()) log.debug("Updating " +
		 * submittal);
		 * 
		 * submittalDao.update(submittal);
		 * 
		 * updateNotifyAuthor(submittal);
		 * 
		 * try { if (log.isDebugEnabled()) log.debug("Updating " + submittal);
		 * 
		 * IndiaSubmittal submital=getSubmittal(submittal.getId());
		 * 
		 * SubmittalStatus submitalStatus=submital.getStatus();
		 * 
		 * indiaSubmittalDao.update(submittal); if(submital.getDeleteFlag()==0)
		 * if(!submital.getStatus().equals(submitalStatus)&&(submittal.getStatus
		 * ()).equals(SubmittalStatus.OUTOFPROJ)) updateNotifyAuthor(submittal);
		 * 
		 * } catch (Exception e) { throw new ServiceException(
		 * "Error while persisting Job Order", e); }
		 * 
		 * // send email to who has created the order on candidate submission if
		 * (oldSubmittal.getStatus() != SubmittalStatus.SUBMITTED &&
		 * submittal.getStatus() == SubmittalStatus.SUBMITTED)
		 * //notifyAuthor(submittal);
		 * 
		 * // update candidate's status updateCandidateStatus(submittal);
		 */
	}

	/*protected void updateNotifyAuthor(IndiaSubmittal submittal, SubmittalDto submittalDto) throws ServiceException {
		IndiaJobOrder order = submittal.getJobOrder();
		Principal principal = UIBean.getFacesContext().getExternalContext().getUserPrincipal();
		String userId = principal.getName();
		log.info("Loged user name--" + userId);
		StringBuffer sb = new StringBuffer();
		User author = userService.loadUser(userId);
		if ((submittal.getStatus()).equals(SubmittalStatus.OUTOFPROJ)) {

			String officeLoc = author.getOfficeLocation();
			String[] mailTo = null;
			log.info("The Location of the author is......" + officeLoc);
			if (officeLoc != null) {
				if (officeLoc.equalsIgnoreCase("HYD")) {
					mailTo = UtilityBean.HYD_MAILS;
				} else if (officeLoc.equalsIgnoreCase("PUNE")) {
					mailTo = UtilityBean.PUNE_MAILS;
				} else if (officeLoc.equalsIgnoreCase("NOIDA")) {
					mailTo = UtilityBean.NOIDA_MAILS;
				} else {
					// mailTo=UtilityBean.ATLANTA_MAILS;
					mailTo = ArrayUtils.concat(UtilityBean.HYD_MAILS, UtilityBean.PUNE_MAILS, UtilityBean.NOIDA_MAILS);
				}

				String orderString = "Id:<b>" + order.getId() + "</b>, CreatedBy:<b>"
						+ (order.getDmName() != null ? order.getDmName() : order.getCreatedBy()) + "</b>";
				if (order.getAssignedTo() != null)
					orderString = orderString + ", AssignedTo: <b>" + order.getAssignedToUser().getFullName() + "</b>";

				sb.append("Hi,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The candidate with  CandidateId: <b>" + submittal.getCandidate().getId() + "</b>  "
						+ " has been moved  to " + submittal.getStatus() + " for the job order[" + orderString + "]. "
						+ "The complete details of submittal is as follows...<br>")
						.append("<b>CandidateId: </b>" + submittal.getCandidate().getId() + "<br>" + "<b>Candidate Name: </b>"
								+ submittal.getCandidate().getFullName() + "<br>" + "<b>Candidate Email: </b>" + submittal.getCandidate().getEmail() + "<br>"
								+ "<b>Order Id: </b>" + order.getId() + "<br>" + "<b>Title: </b>" + order.getTitle() + "<br>" + "<b>Updated By: </b>" + userId
								+ "<br>" + "<b>Updated On: </b>" + order.getUpdatedOn() + "<br>")
						.append("<br><br> You can access it ");
				sb.append("<a href='").append(UIBean.getBaseUrl()).append("/edit_submittal.jsf?submittalId=").append(submittal.getId()).append("'>here</a>");

				sb.append("<br><br><br><b>***This is an automatically generated email, please do not reply ***</b>");
				try {
					commService.sendEmail(null, mailTo, null, "Job Order #" + order.getId() + " submission", sb.toString());
				} catch (Exception e) {
					e.printStackTrace();
					log.warn("Mail Sending failed, verify mail address and settings");
				}
			} else {
				log.warn("Office location is not there.Please verify");
			}
		}
	}*/

	protected void updateNotifyAuthor(IndiaSubmittal submittal, SubmittalDto submittalDto) throws ServiceException {
		try {
			if (submittalDto.getUserDto() != null) {
				IndiaJobOrder order = submittal.getJobOrder();
				log.info("Loged user name--" + submittalDto.getUserDto().getUserId());
				StringBuffer sb = new StringBuffer();
				if ((submittal.getStatus()).equals(SubmittalStatus.OUTOFPROJ)) {
					String officeLoc = submittalDto.getUserDto().getOfficeLocation();
					String[] mailTo = null;
					log.info("The Location of the author is......" + officeLoc);
					if (officeLoc != null) {
						if (officeLoc.equalsIgnoreCase("HYD")) {
							mailTo = UtilityBean.HYD_MAILS;
						} else if (officeLoc.equalsIgnoreCase("PUNE")) {
							mailTo = UtilityBean.PUNE_MAILS;
						} else if (officeLoc.equalsIgnoreCase("NOIDA")) {
							mailTo = UtilityBean.NOIDA_MAILS;
						} else {
							// mailTo=UtilityBean.ATLANTA_MAILS;
							mailTo = ArrayUtils.concat(UtilityBean.HYD_MAILS, UtilityBean.PUNE_MAILS, UtilityBean.NOIDA_MAILS);
						}

						String orderString = "Id:<b>" + order.getId() + "</b>, CreatedBy:<b>"
								+ (order.getDmName() != null ? order.getDmName() : order.getCreatedBy()) + "</b>";
						if (order.getAssignedToUser() != null)
							orderString = orderString + ", AssignedTo: <b>" + order.getAssignedToUser().getFullName() + "</b>";

						sb.append("Hi,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The candidate with  CandidateId: <b>" + submittal.getCandidate().getId() + "</b>  "
								+ " has been moved  to " + submittal.getStatus() + " for the job order[" + orderString + "]. "
								+ "The complete details of submittal is as follows...<br>")
								.append("<b>CandidateId: </b>" + submittal.getCandidate().getId() + "<br>" + "<b>Candidate Name: </b>"
										+ submittal.getCandidate().getFullName() + "<br>" + "<b>Candidate Email: </b>" + submittal.getCandidate().getEmail()
										+ "<br>" + "<b>Order Id: </b>" + order.getId() + "<br>" + "<b>Title: </b>" + order.getTitle() + "<br>"
										+ "<b>Updated By: </b>" + submittalDto.getUserDto().getUserId() + "<br>" + "<b>Updated On: </b>" + order.getUpdatedOn()
										+ "<br>");
						// .append("<br><br> You can access it ");
						// sb.append("<a
						// href='").append(UIBean.getBaseUrl()).append("/edit_submittal.jsf?submittalId=").append(submittal.getId()).append("'>here</a>");

						sb.append("<br><br><br><b>***This is an automatically generated email, please do not reply ***</b>");
						try {
							commService.sendEmail(null, mailTo, null, "Job Order #" + order.getId() + " submission", sb.toString());
						} catch (Exception e) {
							e.printStackTrace();
							log.error("Mail Sending failed, verify mail address and settings");
						}
					} else {
						log.error("Office location is not there.Please verify");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}
	
	protected void updateCandidateStatus(IndiaSubmittal submittal, IndiaCandidate candidate)
			throws ServiceException {/*
										 * 
										 * int candidateId =
										 * submittal.getCandidate().getId();
										 * IndiaCandidate candidate =
										 * indiaCandidateService.getCandidate(
										 * candidateId, false,false);
										 * 
										 * if (candidate != null) { if
										 * (submittal.getDeleteFlag() == 0) {
										 * switch (submittal.getStatus()) { case
										 * SUBMITTED: case ACCEPTED: case
										 * INTERVIEWING:
										 * candidate.setStatus(CandidateStatus.
										 * Submitted);
										 * candidate.setSubmittal(submittal);
										 * break; case CONFIRMED: case STARTED:
										 * candidate.setStatus(CandidateStatus.
										 * Confirmed);
										 * candidate.setSubmittal(submittal);
										 * break; default:
										 * candidate.setStatus(CandidateStatus.
										 * Available);
										 * candidate.setSubmittal(null); break;
										 * } } else {
										 * candidate.setStatus(CandidateStatus.
										 * Available);
										 * candidate.setSubmittal(null);
										 * 
										 * }
										 * indiaCandidateService.updateCandidate
										 * (candidate); }
										 */
		if (candidate != null) {
			if (submittal.getDeleteFlag() == 0) {
				switch (submittal.getStatus()) {
				case SUBMITTED:
					candidate.setStatus(CandidateStatus.Available);
					candidate.setSubmittal(submittal);
					break;
				case ACCEPTED:
				case INTERVIEWING:
				case CONFIRMED:
					candidate.setStatus(CandidateStatus.ResumeSent);
					candidate.setSubmittal(submittal);
					break;

				case STARTED:
					candidate.setStatus(CandidateStatus.OnAssignment);
					candidate.setSubmittal(submittal);
					break;
				default:
					candidate.setStatus(CandidateStatus.Available);
					candidate.setSubmittal(null);
					// candidate.setStatus(null);
					break;
				}
			} else {
				candidate.setStatus(CandidateStatus.Available);
				candidate.setSubmittal(null);

			}
			candidate.setFlag(false);
			// candidate.setReason("NONE".getBytes());
			candidate.setUpdatedOn(new Date());
			candidate.setUpdatedBy(submittal.getUpdatedBy());
			indiaCandidateDao.update(candidate);

			IndiaCandidateStatuses status = new IndiaCandidateStatuses();
//			status.setIndiacandidate(candidate);
			status.setCreatedDate(new Date());
			status.setCreatedBy(candidate.getUpdatedBy());
			status.setStatus(candidate.getStatus());
			status.setIndiacandidate(candidate);
			if (submittal.isFlag()) {
				log.info("submittal");
				indiaCandidateDao.updateCandidateStatus(status);
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public IndiaSubmittal getSubmittal(int submittalId) {
		StringBuffer hql = new StringBuffer();
		hql.append("select distinct o from IndiaSubmittal o left join fetch o.history");
		hql.append(" where o.id = :submittalId and o.deleteFlag != 1");

		List<IndiaSubmittal> result = indiaSubmittalDao.findByQuery(hql.toString(), "submittalId", submittalId);

		return !Utils.isEmpty(result) ? result.get(0) : null;
	}

	@Override
	public void saveSubmittal(IndiaSubmittal submittal) throws ServiceException {
		/*
		 * try { if (log.isDebugEnabled()) log.debug("Persisting " + submittal);
		 * log.debug("Persisting " + submittal.getCreatedBy());
		 * submittal.setDeleteFlag(0); indiaSubmittalDao.save(submittal); }
		 * catch (Exception e) { throw new ServiceException(
		 * "Error while persisting Job Order", e); }
		 * 
		 * // send email to who has created the order on candidate submission
		 * List<IndiaSubmittalEvent> history = submittal.getKeyEvents(); for
		 * (IndiaSubmittalEvent event : history) { if (event.getStatus() ==
		 * SubmittalStatus.SUBMITTED) notifyAuthor(submittal); }
		 * 
		 * // update candidate's status updateCandidateStatus(submittal);
		 */
	}

	protected void notifyAuthor(IndiaSubmittal submittal) throws ServiceException {
		IndiaJobOrder order = submittal.getJobOrder();
		String orderBy = order.getDmName() != null ? order.getDmName() : order.getCreatedBy();
		User author = userService.loadUser(orderBy);

		String email = author != null ? author.getEmail() : null;
		if (!Utils.isBlank(email)) {
			if (email.contains("@")) {
				log.debug("Sending email to order's author: " + email);
				StringBuffer sb = new StringBuffer();

				sb.append("Hi " + author.getFullName() + ",<br><br>");

				sb.append("The Candidate [ CandidateId: <b>" + submittal.getCandidate().getId() + "</b> , Name: <b>" + submittal.getCandidate().getFullName()
						+ "</b>, EmailId: " + submittal.getCandidate().getEmail() + " ] is submitted  for the job order #[ OrderId:<b> " + order.getId()
						+ "</b>, Title:<b> " + order.getTitle() 
						+ "</b>, Created By:<b> " + (order.getDmName() != null ? order.getDmName() : order.getCreatedBy()) + "</b>].<br> ");
				// .append(" You can access it ");
				// sb.append("<a href='").append(UIBean.getBaseUrl())
				// .append("/edit_submittal.jsf?submittalId=").append(submittal.getId())
				// .append("'>here</a>");
				sb.append("<br><br><b>***This is an automatically generated email, please do not reply ***</b>");
				commService.sendEmail(null, email.trim(), "Job Order #" + order.getId() + " submission", sb.toString());
			} else {
				log.warn("Emailaddress is not in correct format.Please verify:::" + email);
			}
		}

	}

	@Override
	@Transactional(readOnly = true)
	public List<IndiaSubmittal> getOrderDeletedSubmittals(Integer orderId) {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append("from IndiaSubmittal o");
		hql.append(" where o.jobOrder.id = :orderId and o.deleteFlag != 1");
		params.put("orderId", orderId);
		log.info("submittal details---" + hql.toString());
		return indiaSubmittalDao.findByQuery(hql.toString(), params);
	}

	@Override
	public void deleteSubmittal(Integer submittalId) throws ServiceException {
		/*
		 * try { if (log.isDebugEnabled()) log.debug(
		 * "Deleting Submittal with id [" + submittalId + "]");
		 * 
		 * IndiaSubmittal submittal = indiaSubmittalDao.findById(submittalId);
		 * submittal.setDeleteFlag(1); indiaSubmittalDao.update(submittal);
		 * updateCandidateStatus(submittal); } catch (Exception e) { throw new
		 * ServiceException("Error while deleting submittal", e); }
		 */
	}

	@Override
	public void saveJobOrder(AddEditJobOrderDto addEditJobOrderDto, List<MultipartFile> files) throws ServiceException {
		IndiaJobOrder indiaJobOrder = null;
		try {
			if (addEditJobOrderDto.getId() != null) {
				indiaJobOrder = getJobOrder(addEditJobOrderDto.getId(), false, false);
			} else {
				indiaJobOrder = new IndiaJobOrder();
			}
			indiaJobOrder = TransformDtoToEntityForIndia.getIndiaJobOrder(addEditJobOrderDto, indiaJobOrder, files);
			if (indiaJobOrder.getId() == null) {
				indiaJobOrdersDao.save(indiaJobOrder);

				if (indiaJobOrder.getStatus() == JobOrderStatus.ASSIGNED) {
					User assignedToUser = userDao.findById(indiaJobOrder.getAssignedTo());
					// jobOrder.setAssignedToUser(assignedToUser);
					notifyAssigned(indiaJobOrder);
				}
				String email = AppConfig.getInstance().getJoNotifyEmail();
				StringBuffer hql = new StringBuffer();
				Map<String, Object> params = new HashMap<String, Object>();
				hql.append("from User u");
				hql.append(" where userRole  in(:roles) and status = :status and assignedBdm=:bdm");
				List<UserRole> roles = new ArrayList<UserRole>();
				roles.add(UserRole.Recruiter);
				roles.add(UserRole.ADM);
				params.put("roles", roles);
				params.put("status", "ACTIVE");
				params.put("bdm", indiaJobOrder.getCreatedBy());
				List<User> recruiters = userDao.findByQuery(hql.toString(), params);
				List<String> recruitersEmails = new ArrayList<String>();
				if (!Utils.isEmpty(recruiters)) {
					for (User user : recruiters) {
						recruitersEmails.add(user.getEmail());
					}

				}
				if (!Utils.isBlank(email)) {
					log.debug("Sending notification email for new job order: " + email);
					String[] notifyto = email.split("[\\p{Space},]+");
					for (String emailTo : notifyto) {
						recruitersEmails.add(emailTo);
					}
					log.info("Email Size" + recruitersEmails.size());
					String[] to = recruitersEmails.toArray(new String[recruitersEmails.size()]);
					for (String mail : to)
						log.info("To Mail" + mail);
					StringBuffer sb = new StringBuffer();
					sb.append("Hi &nbsp;");

					sb.append(",<br><br/>A job order #[ OrderId: <b>" + indiaJobOrder.getId() + "</b>, Title: <b>" + indiaJobOrder.getTitle()
							+ "</b>, Created By: <b>" + indiaJobOrder.getCreatedBy() + "</b>] ").append(" has been created. You can access it ");
					// sb.append("<a
					// href='").append(UIBean.getBaseUrl()).append("/edit_order.jsf?orderId=").append(jobOrder.getId())
					// .append("'>here</a><br/><br/><br/><b>*** This is an
					// automatically generated email, please do not reply
					// ***</b>");
					sb.append("<br/><br/><br/><b>*** This is an automatically generated email, please do not reply ***</b>");

					try {
						// Modified as per ken request(ATS-46)
						commService.sendEmail(null, to, null, "New Job Order created in ATS: " + indiaJobOrder.getTitle(), sb.toString(),
								new AttachmentInfo[0]);
					} catch (Exception e) {
						e.printStackTrace();
						log.error("Error Sending Email Line No 187" + e.getMessage(), e);
					}
				}

			}
		} catch (Exception e) {
			throw new ServiceException("Error while trying to persist Job Order", e);
		}
	}

	@Override
	public List<JobOrderDto> findDeletedJobOrders(JobOrderSearchDto criteria, UserDto user) {
		if (criteria == null)
			throw new IllegalArgumentException("Search criteria not set");

		StringBuffer hqlSelect = new StringBuffer(
				"select j.id,j.priority,j.status,j.jobType,j.title,j.customer,j.city ,j.createdBy,j.assignedTo,j.createdOn,j.updatedOn,j.keySkills,j.hot,j.reason,j.customerHidden,j.numOfPos, j.numOfRsumes,j.dmName from IndiaJobOrder j,User u ");

		Map<String, Object> params = new HashMap<String, Object>();
		if (!Utils.isEmpty(criteria.getFields()))
			hqlSelect.append(", IndiaJobOrderField j ");

		StringBuffer hqlWhere = new StringBuffer(" where j.deleteFlag=1  and u.userId=j.createdBy ");
		/*if (user.getUserRole().toString().equals(UserRole.Manager.toString())) {
			hqlWhere.append("  and u.officeLocation='" + user.getOfficeLocation() + "'");
		}*/
		buildWhereClause(criteria, hqlWhere, params);

		hqlSelect.append(hqlWhere);

		hqlSelect.append(" order by COALESCE(j.updatedOn,j.createdOn) DESC");
		if (log.isDebugEnabled())
			log.debug("HQL Query " + hqlSelect.toString());

		List<?> jobOrders = indiaJobOrdersDao.findByQuery(hqlSelect.toString(), params);

		List<JobOrderDto> jobOrderDtoList = new ArrayList<JobOrderDto>();

		getJobOrderDtoInf(jobOrderDtoList, jobOrders);

		return jobOrderDtoList;
	}

	private void getJobOrderDtoInf(List<JobOrderDto> jobOrderDtoList, List<?> jobOrderList) {
		/*
		 * Iterator<?> it = jobOrderList.iterator(); while (it.hasNext()) {
		 * Object pair[] = (Object[]) it.next(); JobOrderDto dto = new
		 * JobOrderDto();
		 * dto.setJobOrderId(Integer.parseInt(Utils.getStringValueOfObj(pair[0])
		 * )); dto.setPriority(Utils.getStringValueOfObj(pair[1]));
		 * dto.setStatus(Utils.getStringValueOfObj(pair[2]));
		 * dto.setType(Utils.getStringValueOfObj(pair[3]));
		 * dto.setTitle(Utils.getStringValueOfObj(pair[4])); Boolean hiddenvalue
		 * = (Boolean) pair[14]; if (hiddenvalue != null && hiddenvalue) {
		 * dto.setClient(""); } else {
		 * dto.setClient(Utils.getStringValueOfObj(pair[5])); }
		 * dto.setLocation(Utils.getStringValueOfObj(pair[6]));
		 * dto.setDm(Utils.getStringValueOfObj(pair[7])); // If it is Em related
		 * the value is em name instead of assignedTo
		 * dto.setAssignedTo(Utils.getStringValueOfObj(pair[8]));
		 * dto.setKeySkills(Utils.getStringValueOfObj(pair[11]));
		 * dto.setHot((boolean) (pair[12] == null ? false : pair[12])); byte[]
		 * reason = (byte[]) pair[13];
		 * dto.setReason(Utils.getStringFromByteArray(reason)); if
		 * (dto.getLocation() != null && dto.getLocation().length() > 0) {
		 * String[] strLocations = Utils.getStrArray_FromStr(dto.getLocation());
		 * String excel_Location = null; for (String strlocation : strLocations)
		 * { if (excel_Location == null) { excel_Location = strlocation; } else
		 * { excel_Location += "-" + strlocation.trim(); } }
		 * dto.setExcell_Location(excel_Location); }
		 * 
		 * Date jobOrderCreatedOn =
		 * Utils.convertStringToDate(Utils.getStringValueOfObj(pair[9])); if
		 * (jobOrderCreatedOn != null) {
		 * dto.setActiveDays(Utils.findActiveDays(jobOrderCreatedOn)); }
		 * dto.setUpdatedDate(Utils.convertDateToStringWithTimelash(pair[10] !=
		 * null ? (Date) pair[10] : (Date) pair[9]));
		 * dto.setStrUpdatedOn(Utils.convertDateToStringWithTimeForSorting(pair[
		 * 10] != null ? (Date) pair[10] : (Date) pair[9]));
		 * jobOrderDtoList.add(dto); }
		 */

		Iterator<?> it = jobOrderList.iterator();
		while (it.hasNext()) {
			Object pair[] = (Object[]) it.next();
			JobOrderDto dto = new JobOrderDto();
			dto.setJobOrderId(Integer.parseInt(Utils.getStringValueOfObj(pair[0])));
			dto.setPriority(Utils.getStringValueOfObj(pair[1]));
			dto.setStatus(Utils.getStringValueOfObj(pair[2]));
			dto.setType(Utils.getStringValueOfObj(pair[3]));
			dto.setTitle(Utils.getStringValueOfObj(pair[4]));
			dto.setClient(Utils.getStringValueOfObj(pair[5]));
			dto.setLocation(Utils.getStringValueOfObj(pair[6]));
			dto.setDm(Utils.getStringValueOfObj(pair[7]));
			dto.setDm(pair[17] != null ? Utils.getStringValueOfObj(pair[17]) : Utils.getStringValueOfObj(pair[7]));
			// If it is Em related the value is em name instead of assignedTo
			dto.setAssignedTo(Utils.getStringValueOfObj(pair[8]));
			dto.setKeySkills(Utils.getStringValueOfObj(pair[11]));
			// dto.setHot((boolean) (pair[12] == null ? false : pair[12]));
			if (dto.getLocation() != null && dto.getLocation().length() > 0) {
				String[] strLocations = Utils.getStrArray_FromStr(dto.getLocation());
				String excel_Location = null;
				for (String strlocation : strLocations) {
					if (excel_Location == null) {
						excel_Location = strlocation;
					} else {
						excel_Location += "-" + strlocation.trim();
					}
				}
				dto.setExcell_Location(excel_Location);
			}

			Boolean hiddenvalue = (Boolean) pair[14];
			if (hiddenvalue != null && hiddenvalue) {
				dto.setClient("");
			} else {
				dto.setClient(Utils.getStringValueOfObj(pair[5]));
			}

			Date jobOrderCreatedOn = Utils.convertAngularStrToDate_India(Utils.getStringValueOfObj(pair[9]));
			if (jobOrderCreatedOn != null) {
				dto.setActiveDays(Utils.findActiveDays(jobOrderCreatedOn));
			}
			dto.setUpdatedDate(Utils.convertDateToString_HH_MM_A_India(pair[10] != null ? (Date) pair[10] : (Date) pair[9]));
			dto.setStrUpdatedOn(Utils.convertDateToString_HH_MM_A_India(pair[10] != null ? (Date) pair[10] : (Date) pair[9]));
			dto.setHot(pair[12]!=null?(Boolean)pair[12]:false);
			dto.setReason(Utils.getStringValueOfObj(pair[13]));
			dto.setNoOfPositions(Utils.getIntegerValueOfBigDecimalObj(pair[15]));
			dto.setNoOfResumesRequired(pair[16] != null ? ((Integer)pair[16]).toString() : "");
			jobOrderDtoList.add(dto);
		}
	}

	@Override
	public JobOrder reopenJobOrder(Integer jobOrderId, String updatedBy) {
		try {

			if (jobOrderId != null) {
				IndiaJobOrder jobOrder = indiaJobOrdersDao.findById(jobOrderId);
				if (jobOrder != null) {
					jobOrder.setStatus(JobOrderStatus.REOPEN);
					jobOrder.setDeleteFlag(0);
					jobOrder.setUpdatedBy(updatedBy);
					jobOrder.setUpdatedOn(new Date());
					indiaJobOrdersDao.update(jobOrder);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void deleteJobOrder(int orderId) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Deleting Order with id [" + orderId + "]");

			IndiaJobOrder jobOrder = orderDao.findById(orderId);
			if (jobOrder != null) {
				jobOrder.setDeleteFlag(1);
				jobOrder.setUpdatedOn(new Date());
				orderDao.update(jobOrder);
			}
		}

		catch (Exception e) {
			throw new ServiceException("Error while deleting job order", e);
		}

	}

	@Override
	public void saveOrUpdateSubmittal(SubmittalDto submittalDto) {
		IndiaSubmittal submittal = null;
		IndiaCandidate candidate = null;
		if (log.isDebugEnabled())
			log.debug("Persisting submittal");
		if (submittalDto.getSubmittalId() != null) {
			try {

				submittal = getSubmittal(Integer.parseInt(submittalDto.getSubmittalId()));

				SubmittalStatus submitalStatus = submittal.getStatus();

				if (submittalDto.getSubmittalEventHistoryDtoList() != null && submittalDto.getSubmittalEventHistoryDtoList().size() > 0) {
					submittal.setStatus(SubmittalStatus.valueOf(submittalDto.getStatus()));
					for (SubmittalEventDto submittalEventDTO : submittalDto.getSubmittalEventHistoryDtoList()) {
						if (submittalEventDTO.getId() == null) {
							submittalEventDTO.setCreatedBy(submittalDto.getCreatedBy());
							submittalEventDTO.setUpdatedBy(submittalDto.getUpdatedBy());
							submittal.addEvent(TransformDtoToEntity.getSubmittalEvent(new IndiaSubmittalEvent(), submittalEventDTO));
							if (submittalEventDTO.getStatus().equalsIgnoreCase(SubmittalStatus.SUBMITTED.name())) {
								submittal.setCreatedOn(Utils.convertStringToDate_HH_MM_India(submittalEventDTO.getStrCreatedOn()));
							} else if (submittal.getStatus().name().equalsIgnoreCase(submittalEventDTO.getStatus())) {
								submittal.setCreatedDate(Utils.convertStringToDate_HH_MM_India(submittalEventDTO.getStrCreatedOn()));
							}
						} else if (submittalEventDTO.isEditMode()) {
							IndiaSubmittalEvent submittalEvent = indiaSubmittalEventDao.findById(submittalEventDTO.getId());
							submittalEvent.setCreatedDate(Utils.convertStringToDate_HH_MM_India(submittalEventDTO.getStrCreatedOn()));
							submittalEvent.setUpdatedBy(submittalEventDTO.getUpdatedBy());
							submittalEvent.setUpdatedOn(new Date());
							if (submittalEvent.getStatus().equals(SubmittalStatus.SUBMITTED)) {
								submittal.setCreatedOn(Utils.convertStringToDate_HH_MM_India(submittalEventDTO.getStrCreatedOn()));
							} else if (submittal.getStatus().name().equalsIgnoreCase(submittalEvent.getStatus().name())) {
								submittal.setCreatedDate(Utils.convertStringToDate_HH_MM_India(submittalEventDTO.getStrCreatedOn()));
							}
						}
					}
				}
				submittal.setComments(submittalDto.getComments());
				// if (submittalDto.getCreatedOn() != null) {
				// submittal.setCreatedDate(Utils.convertStringToUSDate_WithHH_MM_SS(submittalDto.getCreatedOn()));
				// }
				submittal.setUpdatedBy(submittalDto.getUpdatedBy());

				indiaSubmittalDao.update(submittal);
				if (submittal.getDeleteFlag() == 0)
					if (!submittal.getStatus().equals(submitalStatus) && (submittal.getStatus()).equals(SubmittalStatus.OUTOFPROJ))
						updateNotifyAuthor(submittal, submittalDto);
				updateCandidateStatus(submittal, submittal.getCandidate());
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
		} else {
			try {
				submittal = new IndiaSubmittal();
				IndiaJobOrder jobOrder = getJobOrder(Integer.parseInt(submittalDto.getJobOrderId()), true, true);
				jobOrder.addSubmittal(submittal);
				candidate = indiaCandidateDao.findById(submittalDto.getCandidateId());
				submittal.setCandidate(candidate);

				if (submittalDto.getSubmittalEventHistoryDtoList() != null && submittalDto.getSubmittalEventHistoryDtoList().size() > 0) {
					// SubmittalEventDto submittalEventDto =
					// submittalDto.getSubmittalEventHistoryDtoList()
					// .get(submittalDto.getSubmittalEventHistoryDtoList().size()
					// - 1);
					// submittal.setCreatedDate(Utils.convertDateWithTimeToDate(submittalEventDto.getStrCreatedOn()));
					submittal.setStatus(SubmittalStatus.valueOf(submittalDto.getStatus()));
					for (SubmittalEventDto submittalEventDTO : submittalDto.getSubmittalEventHistoryDtoList()) {
						submittal.addEvent(TransformDtoToEntity.getSubmittalEvent(new IndiaSubmittalEvent(), submittalEventDTO));
						if (submittalEventDTO.getStatus().equalsIgnoreCase(SubmittalStatus.SUBMITTED.name())) {
							if (submittalEventDTO.getStrCreatedOn() != null) {
								submittal.setCreatedOn(Utils.convertStringToDate_HH_MM_India(submittalEventDTO.getStrCreatedOn()));
							} else {
								submittal.setCreatedOn(new Date());
							}
						} else if (submittal.getStatus().name().equalsIgnoreCase(submittalEventDTO.getStatus())) {
							submittal.setCreatedDate(Utils.convertStringToDate_HH_MM_India(submittalEventDTO.getStrCreatedOn()));
						}
					}
				}
				submittal.setComments(submittalDto.getComments());
				// submittal.setCreatedOn(new Date());
				// submittal.setCreatedDate(new Date());
				submittal.setCreatedBy(submittalDto.getCreatedBy());
				submittal.setUpdatedBy(submittalDto.getUpdatedBy());
				submittal.setDeleteFlag(0);
				indiaSubmittalDao.save(submittal);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
			try {
				// send email to who has created the order on candidate
				// submission
				List<IndiaSubmittalEvent> history = submittal.getKeyEvents();
				for (IndiaSubmittalEvent event : history) {
					if (event.getStatus() == SubmittalStatus.SUBMITTED)
						notifyAuthor(submittal);
				}

				// update candidate's status
				updateCandidateStatus(submittal, candidate);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
		}

	}

	@Override
	public List<SubmittalDto> findSubmittalsDetails(Integer jobOrderId) {
		List<SubmittalDto> submitalDetails = null;
		try {

			Map<String, Object> params = new HashMap<String, Object>();
			StringBuffer str = new StringBuffer(
					"select s.created_on,s.updated_on,s.created_by,s.updated_by,s.status, c.first_name||' '||c.last_name,s.submittal_id,s.candidate_id,s.order_id from india_submittal s,india_candidate c where s.delete_flag=0 and  s.order_id=:orderId and c.candidate_id=s.candidate_id order by COALESCE(s.updated_on,s.created_on) DESC");

			params.put("orderId", jobOrderId);

			List<?> submittals = orderDao.findSubmittalsDetails(params, str.toString());

			java.util.Iterator<?> it = submittals.iterator();

			submitalDetails = new ArrayList<SubmittalDto>();
			while (it.hasNext()) {

				Object pair[] = (Object[]) it.next();
				SubmittalDto submittal = new SubmittalDto();
				submittal.setCreatedOn(Utils.convertDateToString_HH_MM_A_India((Date) pair[0]));
				if (pair[1] != null) {
					submittal.setUpdatedOn(Utils.convertDateToString_HH_MM_A_India((Date) pair[1]));
				} else {
					submittal.setUpdatedOn(" ");
				}
				submittal.setCreatedBy(Utils.getStringValueOfObj(pair[2]));
				if (pair[3] != null) {
					submittal.setUpdatedBy(Utils.getStringValueOfObj(pair[3]));
				} else {
					submittal.setUpdatedBy(" ");
				}
				submittal.setStatus(Utils.getStringValueOfObj(pair[4]));
				submittal.setCandidateName(Utils.getStringValueOfObj(pair[5]));
				submittal.setSubmittalId(String.valueOf((Integer) pair[6]));
				submittal.setCandidateId((Integer) pair[7]);
				submittal.setJobOrderId(String.valueOf((Integer) pair[8]));
				submitalDetails.add(submittal);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		return submitalDetails;
	}

	@Override
	public List<SubmittalEventDto> getSubmittalEventHistoryBySubmittalId(Integer submittalId) {
		List<SubmittalEventDto> submittalEventDtoList = null;
		try {

			StringBuffer hqlSelect = new StringBuffer("select se.createdDate,se.status,se.notes from IndiaSubmittalEvent se where se.submittal.id=?1");
			Map<String, Object> params = new HashMap<String, Object>();

			params.put("1", submittalId);
			List<?> list = indiaSubmittalDao.findByQuery(hqlSelect.toString(), params);
			if (list != null && list.size() > 0) {
				submittalEventDtoList = new ArrayList<SubmittalEventDto>();
				Iterator<?> iterator = list.iterator();
				while (iterator.hasNext()) {
					Object[] obj = (Object[]) iterator.next();
					SubmittalEventDto dto = new SubmittalEventDto();
					// dto.setStrCreatedOn(Utils.convertDateToString_HH_MM_India((Date)
					// obj[0]));
					dto.setStrCreatedOn((obj[0] != null ? Utils.convertDateToString_HH_MM_India((Date) obj[0]) : ""));
					dto.setStatus(((SubmittalStatus) obj[1]).name());
					// dto.setNotes((String) obj[2]);
					dto.setNotes((obj[2]) != null ? (String) obj[2] : "");
					submittalEventDtoList.add(dto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return submittalEventDtoList;
	}

	@Override
	public void deleteSubmittal(int submittalId, String reason) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Deleting Submittal with id [" + submittalId + "]");

			IndiaSubmittal submittal = indiaSubmittalDao.findById(submittalId);
			submittal.setDeleteFlag(1);

			IndiaSubmittalDeletion submittalDeletion = new IndiaSubmittalDeletion();
			submittalDeletion = new IndiaSubmittalDeletion();
			submittalDeletion.setStatus("Deleted");
			submittalDeletion.setReason(reason);
			submittalDeletion.setSubmittalId(submittal.getId().toString());
			indiaSubmittalDeletionDao.save(submittalDeletion);
			indiaSubmittalDao.update(submittal);

			updateCandidateStatus(submittal, submittal.getCandidate());
		} catch (Exception e) {
			throw new ServiceException("Error while deleting submittal", e);
		}

	}

	@Override
	public IndiaJobOrder getJobOrder(Integer jobOrderId) {
		IndiaJobOrder jobOrder = null;
		jobOrder = orderDao.findById(jobOrderId);
		return jobOrder;
	}

	@Override
	public AddEditJobOrderDto getJobOrderAttachmentInfo(Integer jobOrderId) {
		AddEditJobOrderDto jobOrderDto = null;
		try {

			StringBuffer hqlSelect = new StringBuffer("select j.attachment,j.attachmentFileName from IndiaJobOrder j where j.id=?1");
			Map<String, Object> params = new HashMap<String, Object>();

			params.put("1", jobOrderId);
			List<?> list = orderDao.findByQuery(hqlSelect.toString(), params);
			if (list != null && list.size() > 0) {
				jobOrderDto = new AddEditJobOrderDto();
				Iterator<?> iterator = list.iterator();
				while (iterator.hasNext()) {
					Object[] obj = (Object[]) iterator.next();
					jobOrderDto.setAttachmentByte((byte[]) obj[0]);
					jobOrderDto.setAttachmentFileName((String) obj[1]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return jobOrderDto;
	}

	@Override
	public Map<String, Object> getStatsByUser(User user, Date dateStart, Date dateEnd, boolean flag) {
		Map<String, Map<JobOrderStatus, Integer>> mapObj = orderDao.getStatsByUser(user, dateStart, dateEnd, flag);
		// replace nulls with 0s
		for (Map<JobOrderStatus, Integer> stats : mapObj.values()) {
			for (JobOrderStatus status : JobOrderStatus.values()) {
				if (!stats.containsKey(status))
					stats.put(status, 0);
			}
		}
		return getorderTotalsByStatus(mapObj);
	}

	private Map<String, Object> getorderTotalsByStatus(Map<String, Map<JobOrderStatus, Integer>> mapObj) {
		List<String> bdms = null;
		int orderTotal = 0;
		List<JobOrderStatusDto> statusDtolist = new ArrayList<JobOrderStatusDto>();
		Map<String, Object> jobOrderStatsMap = new HashMap<String, Object>();
		Map<JobOrderStatus, Integer> orderTotalsByStatus = new HashMap<JobOrderStatus, Integer>();
		if (mapObj != null && mapObj.size() > 0) {

			bdms = new ArrayList<String>(mapObj.keySet());
			for (String user : mapObj.keySet()) {
				User userNameObj = userService.loadUser(user);
				JobOrderStatusDto statusDto = new JobOrderStatusDto();
				Map<JobOrderStatus, Integer> map = mapObj.get(user);
				int totalByUser = 0;
				for (Map.Entry<JobOrderStatus, Integer> entry : map.entrySet()) {

					JobOrderStatus status = entry.getKey();
					Integer count = entry.getValue();
					Integer oldCount = orderTotalsByStatus.get(status);
					int newCount = oldCount != null ? oldCount + count : count;
					orderTotalsByStatus.put(status, newCount);
					totalByUser += count;
					orderTotal += count;
				}
				statusDto.setUserId(user);
				statusDto.setUserName(userNameObj != null ? Utils.concatenateTwoStringsWithSpace(userNameObj.getFirstName(), userNameObj.getLastName()) : user);
				statusDto.setAssigned(map.get(JobOrderStatus.ASSIGNED).toString());
				statusDto.setClosed(map.get(JobOrderStatus.CLOSED).toString());
				statusDto.setFilled(map.get(JobOrderStatus.FILLED).toString());
				statusDto.setOpen(map.get(JobOrderStatus.OPEN).toString());
				statusDto.setReopen(map.get(JobOrderStatus.REOPEN).toString());
				statusDto.setTotal(String.valueOf(totalByUser));
				statusDtolist.add(statusDto);
			}

			orderTotalsByStatus.put(JobOrderStatus.TOTAL, orderTotal);
			jobOrderStatsMap.put("jobOrderData", statusDtolist);
			jobOrderStatsMap.put("jobOrderStatsTotal", orderTotalsByStatus);
		}
		return jobOrderStatsMap;
	}

	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getIndiaSubmittalStatusByLocation(UserDto userDto, String officeLoc, Date dateStart, Date dateEnd) {
		Map<String, Map<SubmittalStatus, Integer>> map = orderDao.getIndiaSubmittalStatusByLocation(userDto, officeLoc, dateStart, dateEnd);
		for (Map<SubmittalStatus, Integer> stats : map.values()) {
			for (SubmittalStatus status : SubmittalStatus.values()) {
				if (!stats.containsKey(status))
					stats.put(status, 0);
			}
		}
		return map;
		// return orderDao.getIndiaSubmittalStatusByLocation(userDto, officeLoc,
		// dateStart, dateEnd);
	}

	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getClientwiseSubmittalStats(Date strDate, Date endDate) {
		Map<String, Map<SubmittalStatus, Integer>> map = orderDao.getClientwiseSubmittalStats(strDate, endDate);
		for (Map<SubmittalStatus, Integer> stats : map.values()) {
			for (SubmittalStatus status : SubmittalStatus.values()) {
				if (!stats.containsKey(status))
					stats.put(status, 0);
			}
		}
		return map;
	}

	@Override
	public Map<String, Object> getDmSummaryReport(Date startDate, Date endDate) {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> dmSummaryReportMap = null;
		Map<String, Map<SubmittalStatus, Integer>> userMap = new TreeMap<String, Map<SubmittalStatus, Integer>>();
		try {

			StringBuilder hql = new StringBuilder("select allsubm.created_by,allsubm.count,allsubm.status,u.office_location,"
					+ "(CASE WHEN u.first_name ISNULL THEN '' ELSE u.first_name END)||' '||(CASE WHEN u.last_name ISNULL THEN '' ELSE u.last_name END)"
					+ " from user_acct u , (select j.created_by as created_by,count(*) as count,s.status as status"
					+ " from india_job_order j, india_submittal s, user_acct u where s.order_id = j.order_id "
					+ " and COALESCE(s.updated_by,s.created_by)=u.user_id and u.status='ACTIVE' and s.created_on>=:startDate"
					+ " and s.created_on<= :endDate and j.delete_flg=0 and s.delete_flag=0 "
					+ " GROUP BY j.created_by,s.status) as allsubm where u.user_id=allsubm.created_by and u.user_role='IN_DM'");

			params.put("startDate", startDate);

			params.put("endDate", endDate);
			System.out.println("hql>>" + hql);
			log.info("Query :::: " + hql.toString());
			List<?> result = indiaJobOrdersDao.findBySQLQuery(hql.toString(), params);

			System.out.println("List size is:::::::" + result.size());
			if (result != null && result.size() > 0) {
				Map<String, String> locMap = new HashMap<String, String>();
				Map<String, String> fullNameMap = new HashMap<String, String>();
				for (Object record : result) {
					Object[] tuple = (Object[]) record;
					String username = (String) tuple[0];
					SubmittalStatus status = SubmittalStatus.valueOf((String) tuple[2]);
					Number count = (Number) tuple[1];
					if (username != null) {
						Map<SubmittalStatus, Integer> statusMap = userMap.get(username);
						if (statusMap == null) {
							statusMap = new HashMap<SubmittalStatus, Integer>();
							userMap.put(username, statusMap);
						}
						statusMap.put(status, count != null ? count.intValue() : 0);
					}
					locMap.put(username, (String) tuple[3]);
					fullNameMap.put(username, (String) tuple[4]);
				}
				// Map<String, Map<SubmittalStatus, Integer>> map =
				// orderDao.getDmSummaryReport(startDate, endDate);
				for (Map<SubmittalStatus, Integer> stats : userMap.values()) {
					for (SubmittalStatus status : SubmittalStatus.values()) {
						if (!stats.containsKey(status))
							stats.put(status, 0);
					}
				}
				Map<String, Object> openAndClosedCount = new HashMap<String, Object>();
				getOpenAndClosedCount(startDate, endDate, openAndClosedCount, fullNameMap, locMap);
				dmSummaryReportMap = getSubmittalCount(openAndClosedCount, userMap, fullNameMap, locMap, startDate, endDate);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return dmSummaryReportMap;
	}

	private Map<String, Object> getSubmittalCount(Map<String, Object> openAndClosedCount, Map<String, Map<SubmittalStatus, Integer>> dmSummaryReportData,
			Map<String, String> fullNameMap, Map<String, String> locMap, Date startDate, Date endDate) {
		Map<String, Object> dmSummaryReportMap = new HashMap<String, Object>();
		Map<String, Integer> submittalTotalsByUser = new HashMap<String, Integer>();
		HashMap<Object, Integer> submittalTotalsByStatus = new HashMap<Object, Integer>();
		Map<String, Integer> statusNotUpdated = new HashMap<String, Integer>();
		List<IndiaSubmittalStatsDto> statsDtoList = new ArrayList<IndiaSubmittalStatsDto>();

		/////////

		/////////////
		Integer totalOpenCount = 0;
		Integer totalClosedCount = 0;

		if (dmSummaryReportData != null && openAndClosedCount != null) {
			for (String userId : openAndClosedCount.keySet()) {
				if (dmSummaryReportData.get(userId) == null) {
					Map<SubmittalStatus, Integer> jobOrderUserMap = new HashMap<SubmittalStatus, Integer>();
					dmSummaryReportData.put(userId, jobOrderUserMap);
				}
			}
		}

		for (String user : dmSummaryReportData.keySet()) {
			Map<SubmittalStatus, Integer> map = dmSummaryReportData.get(user);
			IndiaSubmittalStatsDto statsDto = new IndiaSubmittalStatsDto();

			int totalByUser = 0, nuTotal = 0, statusCount = 0;
			for (Map.Entry<SubmittalStatus, Integer> entry : map.entrySet()) {
				SubmittalStatus status = entry.getKey();
				Integer count = entry.getValue();
				Integer oldCount = submittalTotalsByStatus.get(status);
				int newCount = oldCount != null ? oldCount + count : count;

				totalByUser += count;
				if (!status.equals(SubmittalStatus.SUBMITTED)) {
					statusCount += count;
					submittalTotalsByStatus.put(status, newCount);
				}
			}
			List<Integer> LIST = null;
			if (openAndClosedCount.containsKey(user)) {
				LIST = (List<Integer>) openAndClosedCount.get(user);
				totalOpenCount += LIST.get(0);
				totalClosedCount += LIST.get(1);
			}
			statsDto.setOpenCount(LIST != null ? LIST.get(0) : 0);
			statsDto.setClosedCount(LIST != null ? LIST.get(1) : 0);
			submittalTotalsByStatus.put("totalOpenCount", totalOpenCount);
			submittalTotalsByStatus.put("totalClosedCount", totalClosedCount);

			statsDto.setFullName(fullNameMap.get(user));
			statsDto.setLocation(locMap.get(user));
			statsDto.setName(user);

			submittalTotalsByUser.put(user, totalByUser);
			statusNotUpdated.put(user, totalByUser - statusCount);
			nuTotal = totalByUser - statusCount;
			if (submittalTotalsByStatus.get(SubmittalStatus.SUBMITTED) != null) {
				submittalTotalsByStatus.put(SubmittalStatus.SUBMITTED, submittalTotalsByStatus.get(SubmittalStatus.SUBMITTED) + totalByUser);
			} else {
				submittalTotalsByStatus.put(SubmittalStatus.SUBMITTED, totalByUser);
			}
			if (submittalTotalsByStatus.get(Constants.NOT_UPDATED) != null) {
				submittalTotalsByStatus.put(Constants.NOT_UPDATED, submittalTotalsByStatus.get(Constants.NOT_UPDATED) + nuTotal);
			} else {
				submittalTotalsByStatus.put(Constants.NOT_UPDATED, nuTotal);
			}

			statsDto.setSUBMITTED(Utils.getDefaultIntegerValue(totalByUser));
			statsDto.setNotUpdated(Utils.getDefaultIntegerValue(nuTotal));
			statsDto.setACCEPTED(Utils.getDefaultIntegerValue(map.get(SubmittalStatus.ACCEPTED)));
			statsDto.setCONFIRMED(Utils.getDefaultIntegerValue(map.get(SubmittalStatus.CONFIRMED)));
			statsDto.setDMREJ(Utils.getDefaultIntegerValue(map.get(SubmittalStatus.DMREJ)));
			statsDto.setINTERVIEWING(Utils.getDefaultIntegerValue(map.get(SubmittalStatus.INTERVIEWING)));
			statsDto.setREJECTED(Utils.getDefaultIntegerValue(map.get(SubmittalStatus.REJECTED)));
			statsDto.setSTARTED(Utils.getDefaultIntegerValue(map.get(SubmittalStatus.STARTED)));
			statsDto.setBACKOUT(Utils.getDefaultIntegerValue(map.get(SubmittalStatus.BACKOUT)));
			statsDto.setOUTOFPROJ(Utils.getDefaultIntegerValue(map.get(SubmittalStatus.OUTOFPROJ)));
			statsDtoList.add(statsDto);
		}

		dmSummaryReportMap.put("submittalStatsData", statsDtoList);
		dmSummaryReportMap.put("submittalTotalsByStatus", submittalTotalsByStatus);
		return dmSummaryReportMap;
	}

	private Map<String, Object> getOpenAndClosedCount(Date startDate, Date endDate, Map<String, Object> openAndClosedCount, Map<String, String> fullNameMap,
			Map<String, String> locMap) {
		Map<String, Object> params = new HashMap<String, Object>();
		// Map<String,Map<Integer,Integer>> openAndClosedCount = new
		// HashMap<String,Map<Integer,Integer>>();
		/// ArrayList<Object> counts = new ArrayList<Object>();
		// Map<String, Number> counts = new HashMap<String, Number>();

		try {
			StringBuilder hql = new StringBuilder();
			hql.append("select a.created_by,a.open_count,a.closed_count,a.full_name,a.LOC from"
					+ " (select jo.created_by,  ((CASE WHEN u.first_name ISNULL THEN '' ELSE u.first_name END)||' '||"
					+ "(CASE WHEN u.last_name ISNULL THEN '' ELSE u.last_name END)) as full_name, u.office_location AS LOC,"
					+ " count(CASE when jo.status in ('OPEN','ASSIGNED','REOPEN') THEN 1 END)as open_count, "
					+ " count(CASE when jo.status in ('CLOSED','FILLED','HOLD') THEN 1 END)as closed_count from india_job_order jo, "
					+ "user_acct u where 1=1 AND u.user_role IN ('IN_DM') AND "
					+ " COALESCE(jo.updated_on,jo.created_on)>= :startDate AND COALESCE(jo.updated_on,jo.created_on)<= :endDate "
					+ " and u.user_id = jo.created_by and jo.delete_flg = 0 group by jo.created_by,u.user_id) as a ");

			params.put("startDate", startDate);
			params.put("endDate", endDate);
			System.out.println("hql>>" + hql);
			log.info("Query :::: " + hql.toString());
			List<?> result = indiaJobOrdersDao.findBySQLQuery(hql.toString(), params);
			if (result != null && result.size() > 0) {
				for (Object record : result) {
					List<Number> counts = new ArrayList<Number>();
					Object[] tuple = (Object[]) record;
					String userName = (String) tuple[0];
					Number openCount = (Number) tuple[1];
					Number closedCount = (Number) tuple[2];

					if (userName != null) {
						counts.add((openCount).intValue());
						counts.add((closedCount).intValue());
					}
					fullNameMap.put(userName, (String) tuple[3]);
					locMap.put(userName, (String) tuple[4]);
					openAndClosedCount.put(userName, counts);
				}
			}
			// openAndClosedCountMap.put("openAndClosedCount",
			// openAndClosedCount);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return openAndClosedCount;
	}

	@Override
	public List<?> getUserSubmittalsById(String user_id, String status, Date startDate, Date endDate) {
		List<IndiaSubmittalStatsDto> returnList = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			StringBuilder hql = new StringBuilder();
			hql.append("(select j.created_by as created_by ,s.status as status,j.customer,j.title,c.first_name,c.last_name , "
					+ "u.first_name as frstName,u.last_name as lstName,s.created_on from india_candidate c,india_job_order j, india_submittal s, user_acct u where s.order_id= j.order_id and s.candidate_id=c.candidate_id "
					+ "and s.created_by=u.user_id and u.status='ACTIVE' and s.created_on>= :startDate and s.created_on<= :endDate and j.delete_flg=0 and s.delete_flag=0 "
					+ "and j.created_by= :userId and s.status= :status)");
			params.put("userId", user_id);
			params.put("status", status);
			params.put("startDate", startDate);
			params.put("endDate", endDate);
			List<?> result = indiaJobOrdersDao.findBySQLQuery(hql.toString(), params);
			if (result != null && result.size() > 0) {
				Iterator<?> iterator = result.iterator();
				returnList = new ArrayList<IndiaSubmittalStatsDto>();
				while (iterator.hasNext()) {
					Object[] obj = (Object[]) iterator.next();
					IndiaSubmittalStatsDto dto = new IndiaSubmittalStatsDto();
					dto.setStatus((String) obj[1]);
					dto.setClientName((String) obj[2]);
					dto.setJobTitle((String) obj[3]);
					// dto.setCandidateFullName((String) obj[4]);
					dto.setCandidateFullName(Utils.concatenateTwoStringsWithSpace((String) obj[4], (String) obj[5]));
					dto.setRecruiterName(Utils.concatenateTwoStringsWithSpace((String) obj[6], (String) obj[7]));
					dto.setCreatedOn(Utils.convertDateToString_HH_MM_A_India((Date) obj[8]));
					returnList.add(dto);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}

		return returnList;
	}

	@Override
	public void addClient(String clientName, String updatedBy) {
		ClientNames clientnames = new ClientNames();
		clientnames.setClientName(clientName);
		clientnames.setUpdatedBy(updatedBy);
		clientNamesDao.save(clientnames);
		
		
	}

	@Override
	public void updateClient(String oldClientName, String newClientName, String updatedBy) {
		updateClientInJobOrders(oldClientName,newClientName,updatedBy);
		
		ClientNames clientnames = new ClientNames();
		StringBuffer sql = new StringBuffer();
		sql.append("update client_names  SET client_name = '"+newClientName+"' where client_name='"+oldClientName+"'");
		clientNamesDao.updateSQLQuery(sql.toString());
		clientnames.setUpdatedBy(updatedBy);
		
	}
	
	public void updateClientInJobOrders(String oldClientName, String newClientName, String updatedBy) {
		ClientNames clientnames = new ClientNames();
		StringBuffer sql = new StringBuffer();
		sql.append("select count(j) from india_job_order j where j.customer='" + oldClientName + "'");
		List<?> list = clientNamesDao.findBySQLQuery(sql.toString(), null);
		if (list != null && list.size() > 0) {
			sql = new StringBuffer();
			sql.append("update india_job_order  SET customer = '" + newClientName + "' where customer='" + oldClientName + "'");
			clientNamesDao.updateSQLQuery(sql.toString());
			clientnames.setUpdatedBy(updatedBy);
		}
		
	}

	@Override
	public Map<JobOrderStatus, Integer> getAllIndiaJobOrdersCounts(Date dateStart, Date dateEnd) {
		try {
			Map<JobOrderStatus, Integer> map = orderDao.getAllJobOrdersCounts(dateStart, dateEnd);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

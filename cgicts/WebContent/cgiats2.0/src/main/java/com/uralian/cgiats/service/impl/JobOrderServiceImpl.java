package com.uralian.cgiats.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.primefaces.util.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.uralian.cgiats.dao.CandidateDao;
import com.uralian.cgiats.dao.ClientNamesDao;
import com.uralian.cgiats.dao.JobOrderDao;
import com.uralian.cgiats.dao.SubmittalDao;
import com.uralian.cgiats.dao.SubmittalDeletionDao;
import com.uralian.cgiats.dao.SubmittalEventDao;
import com.uralian.cgiats.dao.UserDao;
import com.uralian.cgiats.dto.AddEditJobOrderDto;
import com.uralian.cgiats.dto.DashboardSearchDto;
import com.uralian.cgiats.dto.JobOrderDto;
import com.uralian.cgiats.dto.JobOrderFieldDto;
import com.uralian.cgiats.dto.ReportwiseDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.dto.SubmittalEventDto;
import com.uralian.cgiats.dto.SubmittalStatsDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateStatus;
import com.uralian.cgiats.model.CandidateStatuses;
import com.uralian.cgiats.model.ClientNames;
import com.uralian.cgiats.model.EmailConfiguration;
import com.uralian.cgiats.model.ExecutiveResumeView;
import com.uralian.cgiats.model.JobExpireStatus;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.JobOrderPriority;
import com.uralian.cgiats.model.JobOrderSearchDto;
import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.JobType;
import com.uralian.cgiats.model.JobViewOrder;
import com.uralian.cgiats.model.MonthlySalesQuotas;
import com.uralian.cgiats.model.SalesQuotaView;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalDeletion;
import com.uralian.cgiats.model.SubmittalEvent;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.rest.UserRoleVo;
import com.uralian.cgiats.service.CommService;
import com.uralian.cgiats.service.CommService.AttachmentInfo;
import com.uralian.cgiats.service.EmailConfigurationService;
import com.uralian.cgiats.service.JobOrderService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.UserService;
import com.uralian.cgiats.util.AppConfig;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.MonthEnum;
import com.uralian.cgiats.util.ReportNameConstants;
import com.uralian.cgiats.util.TransformDtoToEntity;
import com.uralian.cgiats.util.Utils;
import com.uralian.cgiats.web.UtilityBean;

/**
 * @author Christian Rebollar
 */
@Service
@Transactional(rollbackFor = ServiceException.class)
public class JobOrderServiceImpl implements JobOrderService {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ClientNamesDao clientNamesDao; 
	
	@Autowired
	private JobOrderDao orderDao;

	@Autowired
	private SubmittalDao submittalDao;

	@Autowired
	private CommService commService;

	@Autowired
	private UserService userService;

	@Autowired
	private CandidateDao candidateDao;

	@Autowired
	private SubmittalDeletionDao submittalDeletionDao;
	@Autowired
	private SubmittalEventDao submittalEventDao;
	@Autowired
	private UserDao userDao;

	@Autowired
	private EmailConfigurationService emailConfigurationService;
	
	@Override
	@Transactional(readOnly = true)
	public JobOrder getJobOrder(int openingId, boolean fetchFields, boolean fetchSubmittals) {
		try {
			if (log.isDebugEnabled()) {
			}
			StringBuffer hql = new StringBuffer();
			hql.append("select distinct c from JobOrder c");
			if (fetchFields)
				hql.append(" left join fetch c.fields");
			if (fetchSubmittals)
				hql.append(" left join fetch c.submittals");
			hql.append(" where c.id = :jobOpenId");

			List<JobOrder> result = orderDao.findByQuery(hql.toString(), "jobOpenId", openingId);
			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void saveJobOrder(AddEditJobOrderDto addEditJobOrderDto, List<MultipartFile> files) throws ServiceException {
		JobOrder jobOrder = null;
		try {
			if (addEditJobOrderDto.getId() != null) {
				jobOrder = getJobOrderById(addEditJobOrderDto.getId());
			} else {
				jobOrder = new JobOrder();
				jobOrder.setHot(false);
			}

			jobOrder = TransformDtoToEntity.getJobOrder(addEditJobOrderDto, jobOrder, files);
			if (log.isDebugEnabled())
				log.debug("Persisting " + jobOrder);
			if (jobOrder.getId() == null) {
				orderDao.save(jobOrder);

				if (jobOrder.getStatus() == JobOrderStatus.ASSIGNED) {
					User assignedToUser = userDao.findById(jobOrder.getAssignedTo());
					// jobOrder.setAssignedToUser(assignedToUser);
					notifyAssigned(jobOrder, assignedToUser);
				}
				String email = AppConfig.getInstance().getJoNotifyEmail();
				StringBuffer hql = new StringBuffer();
				Map<String, Object> params = new HashMap<String, Object>();
				hql.append("select u.email from User u");
				hql.append(" where userRole  in(:roles) and status = :status and assignedBdm=:bdm");
				List<UserRole> roles = new ArrayList<UserRole>();
				roles.add(UserRole.Recruiter);
				roles.add(UserRole.ADM);
				params.put("roles", roles);
				params.put("status", "ACTIVE");
				params.put("bdm", jobOrder.getCreatedBy());
				List<String> recruiters = (List<String>) userDao.findByQuery(hql.toString(),0,0, params);
				List<String> recruitersEmails = new ArrayList<String>();
				List<EmailConfiguration> emailCfgList = new ArrayList<EmailConfiguration>(Arrays.asList(emailConfigurationService.getEmails()));
				if (emailCfgList != null && emailCfgList.size() > 0) {
					for (EmailConfiguration emailCfg : emailCfgList) {

						if (emailCfg.getReportName().equals(ReportNameConstants.JobOrdersWithLessNoOfSubmittals.name())) {
							String[] emails = emailCfg.getEmails().split(",");
							for (String e : emails) {
								recruitersEmails.add(e);
							}
						}
					}
				}

				if (!Utils.isEmpty(recruiters)) {
						recruitersEmails.addAll(recruiters);
				}
				if (!Utils.isBlank(email)) {
					log.info("Sending notification email for new job order: " + email);
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

					sb.append(",<br><br/>A job order #[ OrderId: <b>" + jobOrder.getId() + "</b>, Title: <b>" + jobOrder.getTitle() + "</b>, Created By: <b>"
							+ jobOrder.getCreatedBy() + "</b>] ").append(" has been created.");
					// sb.append("<a
					// href='").append(UIBean.getBaseUrl()).append("/edit_order.jsf?orderId=").append(jobOrder.getId())
					// .append("'>here</a><br/><br/><br/><b>*** This is an
					// automatically generated email, please do not reply
					// ***</b>");
					sb.append("<br/><br/><br/><b>*** This is an automatically generated email, please do not reply ***</b>");

					try {
						// Modified as per ken request(ATS-46)
						commService.sendEmail(null, to, null, "New Job Order created in ATS: " + jobOrder.getTitle(), sb.toString(), new AttachmentInfo[0]);
					} catch (Exception e) {
						e.printStackTrace();
						log.error("Error Sending Email Line No 187" + e.getMessage(), e);
					}
				}
			}
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist Job Order", exception);
		}
	}

	@Override
	public void updateJobOrder(JobOrder jobOrder) throws ServiceException {
		try {
			JobOrder oldOrder = getJobOrder(jobOrder.getId(), true, true);
			JobOrderStatus status = oldOrder.getStatus();
			try {
				if (log.isDebugEnabled())
					log.debug("Updating " + jobOrder);
				orderDao.update(jobOrder);
			} catch (Exception exception) {
				throw new ServiceException("Error while trying to persist Job Opening", exception);
			}
			if (!status.equals(JobOrderStatus.ASSIGNED) && jobOrder.getStatus().equals(JobOrderStatus.ASSIGNED)) {
				notifyAssigned(jobOrder, jobOrder.getAssignedToUser());
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<?> findJobOrders(JobOrderSearchDto criteria, UserDto loginUser, int first, int pageSize) {
		try {
			StringBuffer hqlSelect = new StringBuffer(
					"select j.id,j.priority,j.status,j.jobType,j.title,j.customer,j.city ||', '|| j.state,j.createdBy,j.assignedTo,j.createdOn,j.updatedOn,j.keySkills,j.hot,j.reason,j.customerHidden,j.dmName,j.noOfResumesRequired,j.jobExpireIn,j.numOfPos from JobOrder j,User u ");
			Map<String, Object> params = new HashMap<String, Object>();
			StringBuffer hqlWhere = null;

			if (!criteria.isHot()) {
				if (!Utils.isEmpty(criteria.getFields()))
					hqlSelect.append(", JobOrderField j ");
				hqlWhere = new StringBuffer(" where j.deleteFlag=0  and u.userId=j.createdBy ");
				if (loginUser != null) {
					if (loginUser.getUserRole().toString().equals(UserRole.Manager.toString())) {
						hqlWhere.append("  and u.officeLocation='" + loginUser.getOfficeLocation() + "'");
					}
				}
			} else {
				hqlWhere = new StringBuffer(" where j.deleteFlag=0  and u.userId=j.createdBy and j.hot = true ");
//				List<JobOrderStatus> statusList = new ArrayList<JobOrderStatus>();
//				statusList.add(JobOrderStatus.CLOSED);
//				statusList.add(JobOrderStatus.FILLED);
//				params.put("statusList", statusList);
			}

			buildWhereClause(criteria, hqlWhere, params);

			hqlSelect.append(hqlWhere);

			hqlSelect.append(" order by COALESCE(j.updatedOn,j.createdOn) DESC");
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

				String hqlQuery = "select s.id,s.jobOrder.id from Submittal s where s.deleteFlag=0 and s.jobOrder.id in ?1";
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
				hqlQuery = "select jof.id,jof.fieldName,jof.fieldValue,jof.visible,jof.jobOrder.id from JobOrderField jof where jof.jobOrder.id in ?1 and (jof.fieldValue != null and jof.fieldValue != '') and jof.visible = true";
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

			return jobOrderDtoList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<AddEditJobOrderDto> findJobOrdersOnline(JobOrderSearchDto criteria, String portalName) {
		try {
			// null criteria not allowed
			List<AddEditJobOrderDto> jobOrderDtoList = null;

			if (criteria == null)
				throw new IllegalArgumentException("Search criteria not set");

			if (log.isDebugEnabled())
				log.debug("Using following criteria " + criteria);

			StringBuffer hqlSelect = new StringBuffer("select j.title,j.id,j.numOfPos,j.city,j.state,j.createdOn,j.updatedOn,j.createdBy from JobOrder j ");
			Map<String, Object> params = new HashMap<String, Object>();

			if (!Utils.isEmpty(criteria.getFields()))
				hqlSelect.append(", JobOrderField jo ");
			StringBuffer hqlWhere = new StringBuffer();
			if (portalName == null) {
				hqlWhere.append(" where j.deleteFlag=0 and j.onlineFlag='Yes' and j.companyFlag = 'CGI' ");
			} else {
				hqlWhere.append(" where j.deleteFlag=0 and j.onlineFlag='Yes' and j.companyFlag like :portalName");
				params.put("portalName", portalName.toUpperCase());
			}
			// Putting the where clause on createdOn and updatedOn
			Date startDate = criteria.getStartEntryDate();
			if (startDate != null) {
				hqlWhere.append(" and (j.createdOn >= :startDate or j.updatedOn >= :startDate)");
				params.put("startDate", startDate);
			}
			Date endDate = criteria.getEndEntryDate();
			if (endDate != null) {
				hqlWhere.append(" and (j.createdOn <= :endDate or j.updatedOn <= :endDate)");
				params.put("endDate", DateUtils.addDays(endDate, 1));
			}
			// inserting null
			criteria.setStartEntryDate(null);
			criteria.setEndEntryDate(null);
			buildWhereClause(criteria, hqlWhere, params);

			log.info("Build Where clause completed ");
			hqlSelect.append(hqlWhere);
			hqlSelect.append(" order by COALESCE(j.updatedOn,j.createdOn) DESC");
			log.info(hqlSelect.toString());
			List<?> list = orderDao.findByQuery(hqlSelect.toString(), params);
			log.info("list" + (list != null ? list.size() : 0));
			try {

				if (list != null && list.size() > 0) {
					jobOrderDtoList = new ArrayList<AddEditJobOrderDto>();

					java.util.Iterator<?> iterator = list.iterator();
					while (iterator.hasNext()) {
						try {
							Object[] obj = (Object[]) iterator.next();
							AddEditJobOrderDto jobOrder = new AddEditJobOrderDto();
							jobOrder.setTitle(obj[0] != null ? (String) obj[0] : "");
							jobOrder.setId((Integer) obj[1]);
							jobOrder.setNumOfPos(obj[2] != null ? Integer.parseInt((String.valueOf(obj[2]))) : 0);
							jobOrder.setCity(obj[3] != null ? (String) obj[3] : "");
							jobOrder.setState(obj[4] != null ? (String) obj[4] : "");
							Date createdOn = obj[5] != null ? (Date) obj[5] : null;
							Date updatedOn = obj[6] != null ? (Date) obj[6] : null;
							Date postedOn = null;
							if (createdOn != null) {
								postedOn = createdOn;
							} else if (updatedOn != null) {
								postedOn = updatedOn;
							}
							jobOrder.setStrPostedDate(Utils.convertDateToString_HH_MM_A(postedOn));
							jobOrder.setPostedDate(Utils.convertDateToString_YYMM_HHMM(postedOn));
							jobOrder.setCreatedBy(Utils.getStringValueOfObj(obj[7]));
							// jobOrder.setDescription(obj[7] != null ? (String)
							// obj[7] : "");
							jobOrderDtoList.add(jobOrder);
						} catch (Exception e) {
							e.printStackTrace();
							log.error(e.getMessage(), e);
						}

					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
			return jobOrderDtoList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public String getOpenJobOrdersDescription(Integer jobOrderId) {
		try {
			if (jobOrderId != null) {
				String description = orderDao.getJobOrderDescription(jobOrderId);
				return description;
			}
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
			log.info("Inside buildWhereClause :: Selected Bdm : " + criteria.getBdm());
			String user = criteria.getAssignedTo();

			// if (!Utils.isBlank(user) && (!Utils.isBlank(criteria.getBdm())))
			// {
			if (!Utils.isBlank(user)) {
				if (criteria.getAdmName() != null) {
					hql.append(" and (j.createdBy = :admName or j.dmName = :admName)");
					params.put("admName", criteria.getAdmName());
				} else {
					User userObj = userService.loadUser(user);

					if (userObj != null && criteria.isDmJobOrders()) {
						hql.append(" and (j.dmName = :dmName or j.createdBy = :dmName)");
						params.put("dmName", userObj.getAssignedBdm());
					} else if (userObj != null && (userObj.isDM() || userObj.isADM())) {
						hql.append(" and (j.dmName = :user or j.createdBy = :user)");
						params.put("user", user);
					} else if (userObj != null && userObj.isRecruiter()) {
						hql.append(
								" and (j.createdBy=:assignedBdm or j.assignedToUser.id=:user or j.assignedToUser.id=:assignedBdm or j.dmName = :assignedBdm)");
						params.put("assignedBdm", userObj.getAssignedBdm());
						params.put("user", user);
					} else {
						hql.append(" and (j.dmName = :user or j.createdBy = :user or j.assignedToUser.id=:user)");
						params.put("user", user);
					}
				}
			} else if (criteria.getBdm() != null) {
				hql.append(" and (j.createdBy=:bdm or j.dmName = :bdm)");
				params.put("bdm", criteria.getBdm());
			}

			if (!Utils.isEmpty(criteria.getPriorities())) {
				hql.append(" and j.priority in (:priorities) ");
					params.put("priorities", criteria.getPriorities());
			}
			if (!Utils.isEmpty(criteria.getStatuses())) {
				hql.append(" and j.status in (:statuses) ");
				
				if(criteria.getStatuses().contains(Constants.PENDING)){
					params.put("statuses", JobOrderStatus.PENDING);
				}else{
					params.put("statuses", criteria.getStatuses());
				}
			}else{
				hql.append(" and j.status not in (:statuses) ");
				params.put("statuses", JobOrderStatus.PENDING);
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
					hql.append(" and j.id = (select j1.jobOpening.id from JobOrderField j1 ");
					hql.append(" where j1.fieldName = '").append(key).append("'");
					hql.append(" and j1.fieldValue = '").append(value).append("')");
				}
			}

			log.info("End buildWhereClause :: Selected Bdm : " + criteria.getBdm());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void deleteJobOrder(int orderId) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Deleting Order with id [" + orderId + "]");

			JobOrder jobOrder = orderDao.findById(orderId);
			if (jobOrder != null) {
				jobOrder.setDeleteFlag(1);
				jobOrder.setUpdatedOn(new Date());
				orderDao.update(jobOrder);
			}
		}

		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Error while deleting job order", e);
		}
	}

	@Override
	public List<String> listExistingTitles() {
		try {
			return orderDao.findAllTitles();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<String> listExistingCustomers() {
		try {
			return orderDao.findAllCustomers();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<String> listExistingCities() {
		try {
			return orderDao.findAllCities();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<String> listExistingStates() {
		try {
			return orderDao.findAllStates();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<JobOrder> findEmptySbmJobOrders() {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer();
			hql.append(
					"from JobOrder where deleteFlag=0 and status != :status1 and status != :status2 and id not in(select jobOrder from Submittal) order by createdOn DESC");
			paramNames.add("status1");
			paramValues.add(JobOrderStatus.CLOSED);
			paramNames.add("status2");
			paramValues.add(JobOrderStatus.FILLED);
			return (List<JobOrder>) orderDao.runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<JobOrder> findOpenJobOrders() {
		try {
			log.info("in findOpenJobOrders()");
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer();
			hql.append("from JobOrder where deleteFlag=0 and status =:status1 and id not in(select jobOrder from Submittal) order by createdOn DESC");
			paramNames.add("status1");
			paramValues.add(JobOrderStatus.OPEN);
			return (List<JobOrder>) orderDao.runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public Submittal getSubmittal(int submittalId) {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("select distinct o from Submittal o left join fetch o.history");
			hql.append(" where o.id = :submittalId and o.deleteFlag != 1");

			List<Submittal> result = submittalDao.findByQuery(hql.toString(), "submittalId", submittalId);

			return !Utils.isEmpty(result) ? result.get(0) : null;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Submittal> getCandidate(Integer orderId, Integer candidateId) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();

			hql.append("from Submittal o");
			hql.append(" where o.jobOrder.id = :orderId and o.candidate.id= :candidateId and o.deleteFlag != 1");
			params.put("orderId", orderId);
			params.put("candidateId", candidateId);
			System.out.println("hql-----" + hql.toString());
			System.out.println("orderId-----" + orderId);
			System.out.println("candidateId-----" + candidateId);
			return submittalDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Submittal> getCandidateSubDetails(Integer candidateId) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from Submittal o");
			hql.append(" where o.candidate.id= :candidateId and o.deleteFlag != 1");
			params.put("candidateId", candidateId);
			return submittalDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<?> getCandidateSubmitalDetails(Integer candidateId) {
		try {
			StringBuffer hql = new StringBuffer();

			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("select o.candidate, o.jobOrder.id, o.createdBy from Submittal o");
			hql.append(" where o.candidate.id= :candidateId and o.status= :status AND o.deleteFlag != 1");
			params.put("candidateId", candidateId);
			params.put("status", SubmittalStatus.STARTED);
			return submittalDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	public void saveOrUpdateSubmittal(SubmittalDto submittalDto) {
		try {
			Submittal submittal = null;
			Candidate candidate = null;
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
								if (submittalEventDTO.getStatus().equalsIgnoreCase(SubmittalStatus.STARTED.name())
										|| submittalEventDTO.getStatus().equalsIgnoreCase(SubmittalStatus.CONFIRMED.name())) {
									mailOnSubmittalEvent(submittalEventDTO, submittal);
								}
								submittal.addEvent(TransformDtoToEntity.getSubmittalEvent(new SubmittalEvent(), submittalEventDTO));
								if (submittalEventDTO.getStatus().equalsIgnoreCase(SubmittalStatus.SUBMITTED.name())) {
									submittal.setCreatedOn(Utils.convertStringToDate_HH_MM(submittalEventDTO.getStrCreatedOn()));
								} else if (submittal.getStatus().name().equalsIgnoreCase(submittalEventDTO.getStatus())) {
									submittal.setCreatedDate(Utils.convertStringToDate_HH_MM(submittalEventDTO.getStrCreatedOn()));
								}
							} else if (submittalEventDTO.isEditMode()) {
								SubmittalEvent submittalEvent = submittalEventDao.findById(submittalEventDTO.getId());
								submittalEvent.setCreatedDate(Utils.convertStringToDate_HH_MM(submittalEventDTO.getStrCreatedOn()));
								submittalEvent.setUpdatedBy(submittalEventDTO.getUpdatedBy());
								if (submittalEventDTO.getStatus().equalsIgnoreCase(SubmittalStatus.STARTED.name())
										|| submittalEventDTO.getStatus().equalsIgnoreCase(SubmittalStatus.CONFIRMED.name())) {
									mailOnSubmittalEvent(submittalEventDTO, submittal);
								}
								submittalEvent.setUpdatedOn(new Date());
								if (submittalEvent.getStatus().equals(SubmittalStatus.SUBMITTED)) {
									submittal.setCreatedOn(Utils.convertStringToDate_HH_MM(submittalEventDTO.getStrCreatedOn()));
								} else if (submittal.getStatus().name().equalsIgnoreCase(submittalEvent.getStatus().name())) {
									submittal.setCreatedDate(Utils.convertStringToDate_HH_MM(submittalEventDTO.getStrCreatedOn()));
								}
							}
						}
					}
					submittal.setComments(submittalDto.getComments());
					// if (submittalDto.getCreatedOn() != null) {
					// submittal.setCreatedDate(Utils.convertStringToUSDate_WithHH_MM_SS(submittalDto.getCreatedOn()));
					// }
					submittal.setUpdatedBy(submittalDto.getUpdatedBy());

					submittalDao.update(submittal);
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
					submittal = new Submittal();
					JobOrder jobOrder = getJobOrder(Integer.parseInt(submittalDto.getJobOrderId()), true, true);
					jobOrder.addSubmittal(submittal);
					candidate = candidateDao.findById(submittalDto.getCandidateId());
					submittal.setCandidate(candidate);

					if (submittalDto.getSubmittalEventHistoryDtoList() != null && submittalDto.getSubmittalEventHistoryDtoList().size() > 0) {
						// SubmittalEventDto submittalEventDto =
						// submittalDto.getSubmittalEventHistoryDtoList()
						// .get(submittalDto.getSubmittalEventHistoryDtoList().size()
						// - 1);
						// submittal.setCreatedDate(Utils.convertDateWithTimeToDate(submittalEventDto.getStrCreatedOn()));
						submittal.setStatus(SubmittalStatus.valueOf(submittalDto.getStatus()));
						for (SubmittalEventDto submittalEventDTO : submittalDto.getSubmittalEventHistoryDtoList()) {
							submittal.addEvent(TransformDtoToEntity.getSubmittalEvent(new SubmittalEvent(), submittalEventDTO));
							if (submittalEventDTO.getStatus().equalsIgnoreCase(SubmittalStatus.SUBMITTED.name())) {
								if (submittalEventDTO.getStrCreatedOn() != null) {
									submittal.setCreatedOn(Utils.convertStringToDate_HH_MM(submittalEventDTO.getStrCreatedOn()));
								} else {
									submittal.setCreatedOn(new Date());
								}
							} else if (submittal.getStatus().name().equalsIgnoreCase(submittalEventDTO.getStatus())) {
								submittal.setCreatedDate(Utils.convertStringToDate_HH_MM(submittalEventDTO.getStrCreatedOn()));
							}
						}
					}
					submittal.setComments(submittalDto.getComments());
					// submittal.setCreatedOn(new Date());
					// submittal.setCreatedDate(new Date());
					submittal.setCreatedBy(submittalDto.getCreatedBy());
					submittal.setUpdatedBy(submittalDto.getUpdatedBy());
					submittal.setDeleteFlag(0);
					submittalDao.save(submittal);
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getMessage(), e);
				}
				try {
					// send email to who has created the order on candidate
					// submission
					List<SubmittalEvent> history = submittal.getKeyEvents();
					for (SubmittalEvent event : history) {
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
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	private void mailOnSubmittalEvent(SubmittalEventDto submittalEventDTO, Submittal submittal) {

		String[] to = null;
		if (!Utils.isBlank(submittalEventDTO.getStatus())) {
			to = UtilityBean.REC_HEAD;
		}
		log.info(to + "  mail");

		String subject = "Candidate status Confirmed/Started Notification";

		SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
		String content = "Hi,<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The  Candidate with ID: <b>" + submittal.getCandidate().getId() + "</b> and Name: <b>"
				+ submittal.getCandidate().getFirstName() + " " + submittal.getCandidate().getLastName() + "</b> is moved to <b>"
				+ submittalEventDTO.getStatus() + "</b> status by Recruiter: <b>" + submittal.getCreatedBy() + "</b> on <b>" + format.format(new Date())
				+ "</b> for Client: <b>" + submittal.getJobOrder().getCustomer() + "</b> and Position: <b>" + submittal.getJobOrder().getTitle()
				+ "</b><br><br>"
				// + "You can access them at <a
				// href='http://localhost:8080/cgiats/hot_list_candidates.jsf'>Here</a><br><br><br><br>
				// "
				+ "<b>***This is an automatically generated email, please do not reply ***</b> ";
		try {
			if (to != null) {
				commService.sendEmail(null, to, null, subject, content, null);
			}

		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	@Override
	public void deleteSubmittal(int submittalId, String reason) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Deleting Submittal with id [" + submittalId + "]");

			Submittal submittal = submittalDao.findById(submittalId);
			submittal.setDeleteFlag(1);

			SubmittalDeletion submittalDeletion = new SubmittalDeletion();
			submittalDeletion = new SubmittalDeletion();
			submittalDeletion.setStatus("Deleted");
			submittalDeletion.setReason(reason);
			submittalDeletion.setSubmittalId(submittal.getId().toString());
			submittalDeletionDao.save(submittalDeletion);
			submittalDao.update(submittal);

			updateCandidateStatus(submittal, submittal.getCandidate());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServiceException("Error while deleting submittal", e);
		}
	}

	@Override
	public Map<String, Map<JobOrderStatus, Integer>> getStatsByUser(User user, Date dateStart, Date dateEnd, boolean flag) {
		try {
			Map<String, Map<JobOrderStatus, Integer>> map = orderDao.getStatsByUser(user, dateStart, dateEnd, flag);
			// replace nulls with 0s
			for (Map<JobOrderStatus, Integer> stats : map.values()) {
				for (JobOrderStatus status : JobOrderStatus.values()) {
					if (!stats.containsKey(status))
						stats.put(status, 0);
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByUser(User user, Date dateStart, Date dateEnd) {
		try {
			Map<String, Map<SubmittalStatus, Integer>> map = orderDao.getSubmittalStatusByUser(user, dateStart, dateEnd);
			// replace nulls with 0s
			for (Map<SubmittalStatus, Integer> stats : map.values()) {
				for (SubmittalStatus status : SubmittalStatus.values()) {
					if (!stats.containsKey(status))
						stats.put(status, 0);
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByLocation(UserDto userDto, List<String> officeLocs, List<String> dms, Date dateStart,
			Date dateEnd,String userStatus) {
		try {
			Map<String, Map<SubmittalStatus, Integer>> map = orderDao.getSubmittalStatusByLocation(userDto, officeLocs, dms, dateStart, dateEnd,userStatus);
			// replace nulls with 0s
			for (Map<SubmittalStatus, Integer> stats : map.values()) {
				for (SubmittalStatus status : SubmittalStatus.values()) {
					if (!stats.containsKey(status))
						stats.put(status, 0);
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * private Map<String, Object> getsubmittaldata(Map<String,
	 * Map<SubmittalStatus, Integer>> mapObj) { List<String> bdms = null; int
	 * orderTotal = 0; int statusCount = 0; List<IndiaSubmittalStatsDto>
	 * statusDtolist = new ArrayList<IndiaSubmittalStatsDto>(); Map<String,
	 * Object> jobOrderStatsMap = new HashMap<String, Object>();
	 * Map<SubmittalStatus, Integer> orderTotalsByStatus = new
	 * HashMap<SubmittalStatus, Integer>(); if (mapObj != null && mapObj.size()
	 * > 0) {
	 * 
	 * bdms = new ArrayList<String>(mapObj.keySet()); for (String user :
	 * mapObj.keySet()) { IndiaSubmittalStatsDto statusDto = new
	 * IndiaSubmittalStatsDto(); Map<SubmittalStatus, Integer> map =
	 * mapObj.get(user); int totalByUser = 0; for (Map.Entry<SubmittalStatus,
	 * Integer> entry : map.entrySet()) {
	 * 
	 * SubmittalStatus status = entry.getKey(); Integer count =
	 * entry.getValue(); Integer oldCount = orderTotalsByStatus.get(status); int
	 * newCount = oldCount != null ? oldCount + count : count;
	 * orderTotalsByStatus.put(status, newCount); totalByUser += count;
	 * orderTotal += count;
	 * 
	 * if (status.equals(SubmittalStatus.SUBMITTED)) {
	 * 
	 * } else {
	 * 
	 * statusCount += count; } } statusDto.setName(user);
	 * statusDto.setACCEPTED(map.get(SubmittalStatus.ACCEPTED));
	 * statusDto.setBACKOUT(map.get(SubmittalStatus.BACKOUT));
	 * statusDto.setCONFIRMED(map.get(SubmittalStatus.CONFIRMED));
	 * statusDto.setDMREJ(map.get(SubmittalStatus.DMREJ));
	 * statusDto.setINTERVIEWING(map.get(SubmittalStatus.INTERVIEWING));
	 * statusDto.setOUTOFPROJ(map.get(SubmittalStatus.OUTOFPROJ));
	 * statusDto.setREJECTED(map.get(SubmittalStatus.REJECTED));
	 * statusDto.setSTARTED(map.get(SubmittalStatus.STARTED));
	 * statusDto.setSUBMITTED(map.get(SubmittalStatus.SUBMITTED));
	 * statusDto.setTotal(totalByUser); statusDto.setNotUpdated(totalByUser -
	 * statusCount); statusDtolist.add(statusDto);
	 * 
	 * 
	 * statusDto.setUserName(user);
	 * statusDto.setAssigned(map.get(JobOrderStatus.ASSIGNED). toString());
	 * statusDto.setClosed(map.get(JobOrderStatus.CLOSED).toString() );
	 * statusDto.setFilled(map.get(JobOrderStatus.FILLED).toString() );
	 * statusDto.setOpen(map.get(JobOrderStatus.OPEN).toString());
	 * statusDto.setReopen(map.get(JobOrderStatus.REOPEN).toString() );
	 * statusDto.setTotal(String.valueOf(totalByUser));
	 * statusDtolist.add(statusDto);
	 * 
	 * }
	 * 
	 * // orderTotalsByStatus.put(SubmittalStatus.TOTAL, orderTotal);
	 * jobOrderStatsMap.put("submittalData", statusDtolist);
	 * jobOrderStatsMap.put("submittalStatsTotal", orderTotalsByStatus); }
	 * return jobOrderStatsMap; }
	 */

	/**
	 * @param order
	 * @throws ServiceException
	 *             Mail format changed by Rajasekhar on 08/05/2014
	 */
	protected void notifyAssigned(JobOrder order, User recruiter) throws ServiceException {
		try {
			// User recruiter = order.getAssignedToUser();
			String email = recruiter != null ? recruiter.getEmail() : null;
			if (!Utils.isBlank(email)) {

				if (email.contains("@")) {
					log.debug("Sending email to assigned user: " + email);
					StringBuffer sb = new StringBuffer();
					sb.append("Hi &nbsp;");
					if (order.getAssignedToUser() != null)
						sb.append(order.getAssignedToUser().getFullName());
					sb.append(",<br><br/>A job order #[ OrderId: <b>" + order.getId() + "</b>, Title: <b>" + order.getTitle() + "</b>, Created By: <b>"
							+ order.getCreatedBy() + "</b>] ").append(" has been assigned to you.");
					// sb.append("<a
					// href='").append(UIBean.getBaseUrl()).append("/edit_submittal.jsf?orderId=")
					// .append(order.getId())
					// .append("'>here</a>"
					sb.append("<br/><br/><br/><b>*** This is an automatically generated email, please do not reply ***</b>");
					commService.sendEmail(null, email.trim(), "'" + order.getTitle() + "'" + " Job Order #" + order.getId() + " has been assigned",
							sb.toString()); // modified
											// as
											// per
											// Ken
											// request
											// -
											// ATS
											// -46
				} else {
					log.error("Emailaddress is not in correct format.Please verify:::" + email);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	protected void notifyAuthor(Submittal submittal)
	// throws ServiceException
	{
		try {
			List<UserDto> assignedBDMObj = null;
			JobOrder order = submittal.getJobOrder();
			String orderBy = order.getDmName() != null ? order.getDmName() : order.getCreatedBy();
			User author = userService.loadUser(orderBy);
			String email = author != null ? author.getEmail() : null;
			List<UserDto> submittalCreatedUser = userService.getUsersInfoByIds(Arrays.asList(submittal.getCreatedBy()));
			if(submittalCreatedUser!=null && submittalCreatedUser.size()>0 && submittalCreatedUser.get(0).getAssignedBdm()!=null){
				assignedBDMObj = userService.getUsersInfoByIds(Arrays.asList(submittalCreatedUser.get(0).getAssignedBdm()));
				if(assignedBDMObj.get(0).getUserRole().equals(UserRole.ADM)){
					assignedBDMObj = userService.getUsersInfoByIds(Arrays.asList(assignedBDMObj.get(0).getAssignedBdm()));
					email+=","+assignedBDMObj.get(0).getEmail();
				}
			}
			try {
				
				if (!Utils.isBlank(email)) {
					if(assignedBDMObj!=null && assignedBDMObj.get(0).getUserRole().equals(UserRole.DM)){
						email+=","+assignedBDMObj.get(0).getEmail();
					}
					if (email.contains("@")) {
						log.debug("Sending email to order's author: " + email);
						StringBuffer sb = new StringBuffer();
						if(assignedBDMObj!=null){
							sb.append("Hi,<br><br>");	
						}else{
						sb.append("Hi " + author.getFullName() + ",<br><br>");
						}
						sb.append("The Candidate [ CandidateId: <b>" + submittal.getCandidate().getId() + "</b> , Name: <b>"
								+ submittal.getCandidate().getFullName() + "</b>, EmailId: " + submittal.getCandidate().getEmail()
								+ " ] is submitted ");
						if(assignedBDMObj!=null){
							sb.append("by "+submittalCreatedUser.get(0).getFullName());	
						}
						sb.append(" for the job order #[ OrderId:<b> " + order.getId() + "</b>, Title:<b> " + order.getTitle()
								+ "</b>, Created By:<b> " + (order.getDmName() != null ? order.getDmName() : order.getCreatedBy()) + "</b>].<br> ");
						// .append(" You can access it ");
						// sb.append("<a
						// href='").append(UIBean.getBaseUrl()).append("/edit_submittal.jsf?submittalId=").append(submittal.getId())
						// .append("'>here</a>");
						sb.append("<br><br><b>***This is an automatically generated email, please do not reply ***</b>");
						commService.sendEmail(null, email.trim(), "Job Order #" + order.getId() + " submission", sb.toString());
					} else {
						log.error("Emailaddress is not in correct format.Please verify:::" + email);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Error Sending Mail Line No 797" + e.getMessage(), e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * @param submittal
	 */
	protected void updateNotifyAuthor(Submittal submittal, SubmittalDto submittalDto) throws ServiceException {
		try {
			if (submittalDto.getUserDto() != null) {
				JobOrder order = submittal.getJobOrder();
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

	/**
	 * @param submittal
	 * @throws ServiceException
	 */
	protected void updateCandidateStatus(Submittal submittal, Candidate candidate) throws ServiceException {
		try {
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
				candidate.setReason("NONE".getBytes());
				candidate.setUpdatedOn(new Date());
				candidate.setUpdatedBy(submittal.getUpdatedBy());
				candidateDao.update(candidate);

				CandidateStatuses status = new CandidateStatuses();
				status.setCandidate(candidate);
				status.setCreatedDate(new Date());
				status.setCreatedBy(candidate.getUpdatedBy());
				status.setStatus(candidate.getStatus());
				if (submittal.isFlag()) {
					log.info("submittal");
					candidateDao.updateCandidateStatus(status);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * @param candidateId
	 * @throws ServiceException
	 */
	@Override
	@Transactional(readOnly = true)
	public List<Submittal> getSubmittalDetails(int candidateId) throws ServiceException {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from Submittal s");
			hql.append(" where s.candidate.id =:candidateId and deleteFlag!=1");
			params.put("candidateId", candidateId);
			return submittalDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<JobOrderDto> findDeletedJobOrders(JobOrderSearchDto criteria, UserDto user) {
		try {
			if (criteria == null)
				throw new IllegalArgumentException("Search criteria not set");
			if (log.isDebugEnabled())
				log.debug("Using following criteria " + criteria);

			StringBuffer hqlSelect = new StringBuffer(
					"select j.id,j.priority,j.status,j.jobType,j.title,j.customer,j.city ||', '|| j.state,j.createdBy,j.assignedTo,j.createdOn,j.updatedOn,j.keySkills,j.hot,j.reason,j.customerHidden,j.dmName,j.noOfResumesRequired,j.jobExpireIn,j.numOfPos from JobOrder j,User u ");

			Map<String, Object> params = new HashMap<String, Object>();
			if (!Utils.isEmpty(criteria.getFields()))
				hqlSelect.append(", JobOrderField j ");

			StringBuffer hqlWhere = new StringBuffer(" where j.deleteFlag=1  and u.userId=j.createdBy ");
			if (user.getUserRole().toString().equals(UserRole.Manager.toString())) {
				hqlWhere.append("  and u.officeLocation='" + user.getOfficeLocation() + "'");
			}
			buildWhereClause(criteria, hqlWhere, params);

			hqlSelect.append(hqlWhere);

			hqlSelect.append(" order by COALESCE(j.updatedOn,j.createdOn) DESC");
			if (log.isDebugEnabled())
				log.debug("HQL Query " + hqlSelect.toString());

			List<?> jobOrders = orderDao.findByQuery(hqlSelect.toString(), params);

			List<JobOrderDto> jobOrderDtoList = new ArrayList<JobOrderDto>();

			getJobOrderDtoInf(jobOrderDtoList, jobOrders);

			return jobOrderDtoList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private void getJobOrderDtoInf(List<JobOrderDto> jobOrderDtoList, List<?> jobOrderList) {
		try {

			Iterator<?> it = jobOrderList.iterator();
			while (it.hasNext()) {
				Object pair[] = (Object[]) it.next();
				JobOrderDto dto = new JobOrderDto();
				dto.setJobOrderId(Integer.parseInt(Utils.getStringValueOfObj(pair[0])));
				dto.setPriority(Utils.getStringValueOfObj(pair[1]));
				dto.setStatus(Utils.getStringValueOfObj(pair[2]));
				dto.setType(Utils.getStringValueOfObj(pair[3]));
				dto.setTitle(Utils.getStringValueOfObj(pair[4]));
				Boolean hiddenvalue = (Boolean) pair[14];
				if (hiddenvalue != null && hiddenvalue) {
					dto.setClient("");
				} else {
					dto.setClient(Utils.getStringValueOfObj(pair[5]));
				}
				dto.setLocation(Utils.getStringValueOfObj(pair[6]));
				dto.setDm(pair[15] != null ? Utils.getStringValueOfObj(pair[15]) : Utils.getStringValueOfObj(pair[7]));
				dto.setNoOfResumesRequired(Utils.getStringValueOfObj(pair[16]));
				dto.setNoOfPositions(Utils.getIntegerValueOfBigDecimalObj(pair[18]));
				dto.setJobExpireIn(Utils.getStringValueOfObj(pair[17]));
				// If it is Em related the value is em name instead of
				// assignedTo
				dto.setAssignedTo(Utils.getStringValueOfObj(pair[8]));
				dto.setKeySkills(Utils.getStringValueOfObj(pair[11]));
				dto.setHot((boolean) (pair[12] == null ? false : pair[12]));
				byte[] reason = (byte[]) pair[13];
				dto.setReason(Utils.getStringFromByteArray(reason));
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

				Date jobOrderCreatedOn = Utils.convertAngularStrToDate(Utils.getStringValueOfObj(pair[9]));
				if (jobOrderCreatedOn != null) {
					dto.setActiveDays(Utils.findActiveDays(jobOrderCreatedOn));
				}
				dto.setUpdatedDate(Utils.convertDateToString_HH_MM_A(pair[10] != null ? (Date) pair[10] : (Date) pair[9]));
				dto.setStrUpdatedOn(Utils.convertDateToString_HH_MM(pair[10] != null ? (Date) pair[10] : (Date) pair[9]));
				jobOrderDtoList.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<Submittal> getOrderDeletedSubmittals(int orderId) {
		try {
			StringBuffer hql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			hql.append("from Submittal o");
			hql.append(" where o.jobOrder.id = :orderId and o.deleteFlag != 1");
			params.put("orderId", orderId);
			log.info("submittal details---" + hql.toString());
			return submittalDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Map<SubmittalStatus, Integer>> getDmSubmittalStatusByUser(String userRole, Date dateStart, Date dateEnd, User user) {
		try {
			Map<String, Map<SubmittalStatus, Integer>> map = orderDao.getDmSubmittalStatusByUser(userRole, dateStart, dateEnd, user);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<MonthlySalesQuotas> getRecSalesQuotaList(String userId, String month, String year) {
		try {
			return orderDao.getRecSalesQuotaList(userId, month, year);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Map<SubmittalStatus, Integer>> getDmSubmittalStatusByRecruiter(Date dateStart, Date dateEnd) {
		try {
			Map<String, Map<SubmittalStatus, Integer>> map = orderDao.getDmSubmittalStatusByRecruiter(dateStart, dateEnd);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<String, Map<SubmittalStatus, Integer>> getAdmMonthlyQuota(String role, Date submittalFrom, Date submittalTo, String userId) {
		try {
			Map<String, Map<SubmittalStatus, Integer>> map = orderDao.getAdmMonthlyQuota(role, submittalFrom, submittalTo, userId);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<JobOrderStatus, Integer> getAllJobOrdersCounts(Date dateStart, Date dateEnd) {
		try {
			Map<JobOrderStatus, Integer> map = orderDao.getAllJobOrdersCounts(dateStart, dateEnd);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public Map<JobOrderStatus, Integer> getTodayJobOrdersCountsForExecutives(Date dateStart, Date dateEnd) {
		try {
			Map<JobOrderStatus, Integer> map = orderDao.getTodayJobOrdersCountsForExecutives(dateStart, dateEnd);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<JobOrderDto> findEMJobOrders(JobOrderSearchDto criteria, UserDto loginUser) {
		try {
			// null criteria not allowed
			if (criteria == null)
				throw new IllegalArgumentException("Search criteria not set");

			if (log.isDebugEnabled())
				log.debug("Using following criteria " + criteria);

			// StringBuffer hqlSelect = new StringBuffer("select c from JobOrder
			// c,User u");
			StringBuffer hqlSelect = new StringBuffer(
					"select j.id,j.priority,j.status,j.jobType,j.title,j.customer,j.city ||', '|| j.state,j.createdBy,j.emName,j.createdOn,j.updatedOn,j.keySkills,j.hot,j.reason,j.customerHidden,j.dmName,j.noOfResumesRequired,j.jobExpireIn,j.numOfPos from JobOrder j,User u ");
			Map<String, Object> params = new HashMap<String, Object>();

			if (!Utils.isEmpty(criteria.getFields()))
				hqlSelect.append(", JobOrderField j ");

			StringBuffer hqlWhere = new StringBuffer(" where j.deleteFlag=0  and u.userId=j.createdBy ");
			if (loginUser.getUserRole().toString().equals(UserRole.Manager.toString())) {
				hqlWhere.append("  and u.officeLocation='" + loginUser.getOfficeLocation() + "'");
			}
			buildWhereClause(criteria, hqlWhere, params);

			hqlSelect.append(hqlWhere);
			hqlSelect.append(" and j.emName IS NOT NULL order by COALESCE(j.updatedOn,j.createdOn) DESC");

			if (log.isDebugEnabled())
				log.debug("HQL Query " + hqlSelect.toString());
			log.info("HQL Query " + hqlSelect.toString());
			List<?> jobOrders = orderDao.findByQuery(hqlSelect.toString(), params);

			List<JobOrderDto> jobOrderDtoList = new ArrayList<JobOrderDto>();

			getJobOrderDtoInf(jobOrderDtoList, jobOrders);

			if (jobOrderDtoList != null && jobOrderDtoList.size() > 0) {
				Iterator<?> it = null;
				List<Integer> jobOrderIds = new ArrayList<Integer>();
				for (JobOrderDto dto : jobOrderDtoList) {
					jobOrderIds.add(dto.getJobOrderId());
				}
				params = new HashMap<String, Object>();

				String hqlQuery = "select s.id,s.jobOrder.id from Submittal s where s.deleteFlag=0 and s.jobOrder.id in ?1";
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
				hqlQuery = "select jof.id,jof.fieldName,jof.fieldValue,jof.visible,jof.jobOrder.id from JobOrderField jof where jof.jobOrder.id in ?1 and (jof.fieldValue != null and jof.fieldValue != '') and jof.visible = true";
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

			return jobOrderDtoList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Map<String, Integer>> findJobOrdersReportCount(Set<String> dmNameList, Map<String, UserDto> userIdWithUserMap, Date reportStartDate,
			Date reportEndDate) {
		Map<String, Map<String, Integer>> returnVal = new HashMap<String, Map<String, Integer>>();
		try {
			StringBuffer sql = new StringBuffer();

			Map<String, Object> params = new HashMap<String, Object>();

			sql.append(
					"select COALESCE(j.dmName,j.createdBy),j.status from JobOrder j where  " + "j.createdOn >= ?2 and j.createdOn <= ?3 and j.deleteFlag = 0 and "
							+ " j.status != 'PENDING'");
			params.put("2", reportStartDate);
			params.put("3", reportEndDate);

			System.out.println("sql---->" + sql);
			List<?> list = orderDao.findByQuery(sql.toString(), params);
			if (list != null && list.size() > 0) {
				for (Object obj : list) {
					Object[] tuple = (Object[]) obj;
					String dmName = (String) tuple[0];
					dmNameList.add(dmName);
				}
			}
			// Getting the information of each user(dm/createdBy)
			if (dmNameList != null && dmNameList.size() > 0) {
				List<UserDto> userDtoList = userService.getUsersByIds(dmNameList);
				for (UserDto userDto : userDtoList) {
					// System.out.println(userDto.getUserId());
					userIdWithUserMap.put(userDto.getUserId(), userDto);
				}
			}
			if (list != null && list.size() > 0) {

				for (Object obj : list) {
					Object[] tuple = (Object[]) obj;
					try {

						String user = userIdWithUserMap.get(((String) tuple[0])).getUserRole().equals(UserRole.DM) ? (String) tuple[0]
								: userIdWithUserMap.get(((String) tuple[0])).getAssignedBdm() != null
										? userIdWithUserMap.get(((String) tuple[0])).getAssignedBdm() : (String) tuple[0];
						if (userIdWithUserMap.get(user).getUserRole().equals(UserRole.DM) || userIdWithUserMap.get(user).getUserRole().equals(UserRole.ADM)) {
							JobOrderStatus joStatus = (JobOrderStatus) tuple[1];
							String status = null;
							if (joStatus.equals(JobOrderStatus.FILLED) || joStatus.equals(JobOrderStatus.CLOSED)) {
								status = Constants.CLOSED;
							} else {
								status = Constants.OPEN;
							}
							if (returnVal.get(status) != null) {
								Map<String, Integer> map = returnVal.get(status);
								if (map.get(user) != null) {
									map.put(user, (map.get(user)) + 1);
								} else {
									map.put(user, 1);
								}
							} else {
								Map<String, Integer> map = new HashMap<String, Integer>();
								map.put(user, 1);
								returnVal.put(status, map);
							}
						}
					} catch (Exception e) {
						// System.out.println(tuple[0]);
						log.error("Error in findJobOrdersReportCount() because of " + tuple[0]);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return returnVal;
	}

	@Override
	public Map<String, Map<String, Object>> findSubmittalCounts(Date startDate, Date endDate) {
		try {
			StringBuffer sql = new StringBuffer();
			Map<String, Object> params = new HashMap<String, Object>();
			sql.append("select jo.created_by,");
			sql.append(
					" count(CASE when  s.status IN ('SUBMITTED','DMREJ','ACCEPTED','INTERVIEWING','CONFIRMED','REJECTED','STARTED','BACKOUT','OUTOFPROJ') THEN 1 END)as submitted_count, ");
			sql.append(" count(CASE when  s.status='DMREJ' THEN 1 END)as dmrej_count, ");
			sql.append(" count(CASE when  s.status='ACCEPTED' THEN 1 END)as accepted_count, ");
			sql.append(" count(CASE when  s.status='INTERVIEWING' THEN 1 END)as inteviewing_count, ");
			sql.append(" count(CASE when  s.status='CONFIRMED' THEN 1 END)as confirmed_count, ");
			sql.append(" count(CASE when  s.status='REJECTED' THEN 1 END)as rejected_count, ");
			sql.append(" count(CASE when  s.status='STARTED' THEN 1 END)as started_count, ");
			sql.append(" count(CASE when  s.status='BACKOUT' THEN 1 END)as backout_count, ");
			sql.append(" count(CASE when  s.status='OUTOFPROJ' THEN 1 END)as outofprj_count ");
			sql.append("  from submittal s,job_order jo where s.order_id=jo.order_id ");

			if (startDate != null) {
				sql.append(" and date(COALESCE(s.updated_on,s.created_on))>=date(:startDate) ");
				params.put("startDate", startDate);
			}

			if (endDate != null) {
				sql.append(" and date(COALESCE(s.updated_on,s.created_on))<=date(:endDate) ");
				params.put("endDate", endDate);
			}

			sql.append("and s.delete_flag=0 and jo.delete_flg=0 group by jo.created_by order by jo.created_by asc");

			List<?> list = orderDao.findBySQLQuery(sql.toString(), params);

			Map<String, Map<String, Object>> userRecords = new HashMap<String, Map<String, Object>>();

			if (list != null)
				for (Object obj : list) {
					Object[] tuple = (Object[]) obj;
					String user = tuple[0].toString();
					Object sub_count = tuple[1] == null ? 0 : tuple[1];
					Object dmrej_count = tuple[2] == null ? 0 : tuple[2];
					Object accepted_count = tuple[3] == null ? 0 : tuple[3];
					Object inter_count = tuple[4] == null ? 0 : tuple[4];
					Object confirm_count = tuple[5] == null ? 0 : tuple[5];
					Object rej_count = tuple[6] == null ? 0 : tuple[6];
					Object started_count = tuple[7] == null ? 0 : tuple[7];
					Object backout_count = tuple[8] == null ? 0 : tuple[8];
					Object outofproj_count = tuple[9] == null ? 0 : tuple[9];

					Map<String, Object> record = new HashMap<String, Object>();

					record.put("sub_count", sub_count);
					record.put("dmrej_count", dmrej_count);
					record.put("accepted_count", accepted_count);
					record.put("inter_count", inter_count);
					record.put("confirm_count", confirm_count);
					record.put("rej_count", rej_count);
					record.put("started_count", started_count);
					record.put("backout_count", backout_count);
					record.put("outofproj_count", outofproj_count);

					userRecords.put(user, record);
				}
			return userRecords;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<JobOrder> findJobOrdersOnMobile(Date jbDate) {
		try {
			log.info("Date" + jbDate);
			Map<String, Object> params = null;
			StringBuffer hql = new StringBuffer(
					"select new JobOrder(j.id,j.city,j.state,j.numOfPos,j.title,j.description,j.status,j.createdOn,j.updatedOn) from JobOrder j where 1=1 ");
			hql.append(" and j.deleteFlag=0 ");

			if (jbDate != null) {
				hql.append("  and date(COALESCE(j.createdOn))>=date(:startDate)  ");
				params = new HashMap<String, Object>();
				params.put("startDate", jbDate);
			}
			hql.append(" order by case when j.updatedOn != null then j.updatedOn else j.createdOn end desc");
			return orderDao.findByQuery(hql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

	public List<JobViewOrder> getAllJobOrderStatsForExecutives(UserRoleVo userRoleVo) {
		try {
			List<JobViewOrder> allStatList = orderDao.getAllJobOrderStatsForExecutives(userRoleVo);
			return allStatList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public JobViewOrder getTodayJobOrderStatsForExecutives(UserRoleVo userRoleVo) {
		try {
			JobViewOrder statList = orderDao.getTodayJobOrderStatsForExecutives(userRoleVo);
			return statList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<JobViewOrder> getYearWiseStatusForExecutives(String year, UserRoleVo userRoleVo) {
		try {
			List<JobViewOrder> yearStatList = orderDao.getYearWiseStatusForExecutives(year, userRoleVo);
			return yearStatList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<ExecutiveResumeView> getTodayResumesStats() {
		try {
			List<ExecutiveResumeView> todayStatList = orderDao.getTodayResumesStats();
			return todayStatList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<ExecutiveResumeView> getYearWiseResumesStats() {
		try {
			List<ExecutiveResumeView> yearStatList = orderDao.getYearWiseResumesStats();
			return yearStatList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public List<SalesQuotaView> getDmSubmittalsByUserQuota(String month, String year, String userId) {
		try {
			List<SalesQuotaView> submittalsByUserQuotaList = orderDao.getDmSubmittalsByUserQuota(month, year, userId);
			return submittalsByUserQuotaList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public ExecutiveResumeView getAllResumesCounts() {
		try {
			ExecutiveResumeView allResumeStats = orderDao.getAllResumesCounts();
			return allResumeStats;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public int findJobOrdersCount(JobOrderSearchDto criteria, User login) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			StringBuilder sql = new StringBuilder();
			sql.append(
					" select count(*) from job_order joborder0_ cross   join    USER_ACCT user1_  where  1=1  and joborder0_.delete_flg=0  and user1_.USER_ID=joborder0_.created_by ");
			String user = criteria.getAssignedTo();
			log.info("user" + user);
			if (!Utils.isBlank(user) && (!Utils.isBlank(criteria.getBdm()))) {

				if (criteria.getAdmName() != null) {
					log.info("from if" + criteria.getAdmName());
					sql.append(" and joborder0_.created_by = :admName");
					params.put("admName", criteria.getAdmName());
				} else {
					User userObj = userService.loadUser(user);

					if (userObj != null && criteria.isDmJobOrders()) {
						sql.append(" and joborder0_.created_by = :dmName");
						params.put("dmName", userObj.getAssignedBdm());
					} else if (userObj != null && (userObj.isDM() || userObj.isADM())) {
						sql.append(" and joborder0_.created_by = :user");
						params.put("user", user);
					} else if (userObj != null && userObj.isRecruiter()) {
						sql.append(" and ( joborder0_.created_by=:assignedBdm or joborder0_.assigned_to=:user or joborder0_.assigned_to=:assignedBdm)");
						params.put("assignedBdm", userObj.getAssignedBdm());
						params.put("user", user);
					} else {

						sql.append(" and (joborder0_.created_by = :user or joborder0_.assigned_to=:user)");
						params.put("user", user);
					}
				}
			} else if (criteria.getBdm() != null) {
				sql.append(" and joborder0_.created_by=:bdm");
				params.put("bdm", criteria.getBdm());
			}

			if (criteria.getPriorities() != null) {
				sql.append(" and joborder0_.priority in (:priorities) ");
				int i = 0;
				String[] paramValue = new String[criteria.getPriorities().size()];
				for (JobOrderPriority str : criteria.getPriorities()) {
					paramValue[i] = str.toString();
					i++;
				}
				params.put("priorities", paramValue);
			}
			if (criteria.getStatuses() != null) {
				sql.append(" and joborder0_.status in (:status) ");
				int i = 0;
				String[] paramValue = new String[criteria.getStatuses().size()];
				for (JobOrderStatus str : criteria.getStatuses()) {
					paramValue[i] = str.toString();
					i++;
				}

				params.put("status", paramValue);
			}
			if (criteria.getJobTypes() != null) {
				sql.append(" and joborder0_.jobtype in (:jobtype) ");
				int i = 0;
				String[] paramValue = new String[criteria.getJobTypes().size()];
				for (JobType str : criteria.getJobTypes()) {
					paramValue[i] = str.toString();
					i++;
				}
				params.put("jobtype", paramValue);
			}
			if (criteria.getStartEntryDate() != null && criteria.getEndEntryDate() != null) {
				sql.append(" and joborder0_.created_on>=:entryDate and joborder0_.created_on<=:endDate ");
				params.put("entryDate", criteria.getStartEntryDate());
				params.put("endDate", criteria.getEndEntryDate());

			}

			if (criteria.getJobBelongsTo() != null && criteria.getJobBelongsTo().size() > 0) {

				sql.append(" and joborder0_.company_flg in (:jobBelongs) ");
				params.put("jobBelongs", criteria.getJobBelongsTo());
			}

			return orderDao.findJobOrdersCount(params, sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public List<SubmittalDto> findSubmittalsDetails(Integer jobOrderId) {
		List<SubmittalDto> submitalDetails = null;
		try {

			Map<String, Object> params = new HashMap<String, Object>();
			StringBuffer str = new StringBuffer(
					"select s.created_on,s.updated_on,s.created_by,s.updated_by,s.status, c.first_name||' '||c.last_name,s.submittal_id,s.candidate_id,s.order_id from submittal s,candidate c where s.delete_flag=0 and  s.order_id=:orderId and c.candidate_id=s.candidate_id order by COALESCE(s.updated_on,s.created_on) DESC");

			params.put("orderId", jobOrderId);

			List<?> submittals = orderDao.findSubmittalsDetails(params, str.toString());

			java.util.Iterator<?> it = submittals.iterator();

			submitalDetails = new ArrayList<SubmittalDto>();
			while (it.hasNext()) {

				Object pair[] = (Object[]) it.next();
				SubmittalDto submittal = new SubmittalDto();
				submittal.setCreatedOn(Utils.convertDateToString_HH_MM_A((Date) pair[0]));
				if (pair[1] != null) {
					submittal.setUpdatedOn(Utils.convertDateToString_HH_MM_A((Date) pair[1]));
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
	public JobOrder getJobOrder(Integer jobOrderId) {
		try {
			JobOrder jobOrder = null;
			jobOrder = orderDao.findById(jobOrderId);
			return jobOrder;
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
	 * com.uralian.cgiats.service.JobOrderService#saveOrder(com.uralian.cgiats.
	 * model.JobOrder)
	 */
	@Override
	public JobOrderDto saveOrder(JobOrder jobOrder) {
		try {
			jobOrder.setCreatedOn(new Date());
			JobOrder order = orderDao.saveOrder(jobOrder);

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
			if (order.getAssignedToUser() != null) {
				dto.setAssignedTo(order.getAssignedToUser().getFullName());
			}
			// Caluclating Active Days
			if (jobOrderCreatedOn != null) {
				dto.setActiveDays(Utils.findActiveDays(jobOrderCreatedOn));
			}
			return dto;
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
	 * com.uralian.cgiats.service.JobOrderService#getJobOrderById(java.lang.
	 * Integer)
	 */
	@Override
	public JobOrder getJobOrderById(Integer jobOrderId) {
		try {
			return orderDao.findById(jobOrderId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<JobOrder> getJobOrderByIds(List<Integer> jobOrderIds) {
		try {
			return orderDao.findByIds(jobOrderIds);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.JobOrderService#reopenJobOrder(java.lang.
	 * Integer)
	 */
	@Override
	public JobOrder reopenJobOrder(Integer jobOrderId, String updatedBy) {
		try {

			if (jobOrderId != null) {
				JobOrder jobOrder = orderDao.findById(jobOrderId);
				if (jobOrder != null) {
					jobOrder.setStatus(JobOrderStatus.REOPEN);
					jobOrder.setDeleteFlag(0);
					jobOrder.setUpdatedBy(updatedBy);
					jobOrder.setUpdatedOn(new Date());
					orderDao.update(jobOrder);
				}
			}
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
	 * com.uralian.cgiats.service.JobOrderService#getJobOrderAttachmentInfo(java
	 * .lang.Integer)
	 */
	@Override
	public AddEditJobOrderDto getJobOrderAttachmentInfo(Integer jobOrderId) {
		AddEditJobOrderDto jobOrderDto = null;
		try {

			StringBuffer hqlSelect = new StringBuffer("select j.attachment,j.attachmentFileName from JobOrder j where j.id=?1");
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.JobOrderService#
	 * getSubmittalEventHistoryBySubmittalId(java.lang.Integer)
	 */
	@Override
	public List<SubmittalEventDto> getSubmittalEventHistoryBySubmittalId(Integer submittalId) {
		List<SubmittalEventDto> submittalEventDtoList = null;
		try {

			StringBuffer hqlSelect = new StringBuffer("select se.createdDate,se.status,se.notes from SubmittalEvent se where se.submittal.id=?1");
			Map<String, Object> params = new HashMap<String, Object>();

			params.put("1", submittalId);
			List<?> list = submittalDao.findByQuery(hqlSelect.toString(), params);
			if (list != null && list.size() > 0) {
				submittalEventDtoList = new ArrayList<SubmittalEventDto>();
				Iterator<?> iterator = list.iterator();
				while (iterator.hasNext()) {
					Object[] obj = (Object[]) iterator.next();
					SubmittalEventDto dto = new SubmittalEventDto();
					dto.setStrCreatedOn(Utils.convertDateToString_HH_MM_SS((Date) obj[0]));
					dto.setStatus(((SubmittalStatus) obj[1]).name());
					dto.setNotes((String) obj[2]);
					submittalEventDtoList.add(dto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return submittalEventDtoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void sendHotJobMail(JobOrder jobOrder, String officeLocation) {
		try {
			String[] to = null;
			to = ArrayUtils.concat(UtilityBean.HYD_MAILS, UtilityBean.PUNE_MAILS, UtilityBean.NOIDA_MAILS);

			StringBuffer hql = new StringBuffer();
			hql.append("select u.EMAIL from USER_ACCT u");
			hql.append(" where u.USER_ROLE ='DM' and u.STATUS = 'ACTIVE'");
			// List<String> list = userDao.findByQuery(hql.toString(), params);
			List<String> list = (List<String>) userDao.findBySQLQuery(hql.toString(), null);
			if (list != null && list.size() > 0) {
				log.info("<<<<< User to send mails Size ::: " + list.size() + " >>>>>>");
				list.addAll(Arrays.asList(to));
				to = list.toArray(new String[0]);
			}

			String subject = "Job Order Hot List Information";
			if (jobOrder.getCreatedOn() != null) {
/*				String modifiedDate = jobOrder.getUpdatedOn() == null ? format.format(jobOrder.getCreatedOn()) : format.format(jobOrder.getUpdatedOn());
				String modifiedBy = jobOrder.getUpdatedBy() == null ? jobOrder.getCreatedBy() : jobOrder.getUpdatedBy();
				String reason = new String(jobOrder.getReason());

				String content = "Hi,<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The  Job Order having Id <b>" + jobOrder.getId() + "</b> moved to hot list by "
						+ "<b>" + modifiedBy + "</b> on <b>" + modifiedDate + "</b>.<br/> " + "Job Order details are as follows... <br><br>" + "<b>Title : </b>"
						+ jobOrder.getTitle() + " <br><b>Key Skills : </b>" + jobOrder.getKeySkills() + " <br>" + "<b>Location : </b>" + jobOrder.getLocation()
						+ " <br><b>Client : </b>" + jobOrder.getCustomer() + " <br>" + "<b>Status : </b>" + jobOrder.getStatus().name()
						+ "<br><b>Priority : </b>" + jobOrder.getPriority() + "<br><b>Job Type : </b>" + jobOrder.getJobType();

				if (jobOrder.getHourlyRateW2() != null && jobOrder.getHourlyRateW2() > 0) {
					content += "<br><b>W-2 Rate Range : </b> $" + jobOrder.getHourlyRateW2();
					if (jobOrder.getHourlyRateW2Max() != null && jobOrder.getHourlyRateW2Max() > 0) {
						content += " - $" + jobOrder.getHourlyRateW2Max();
					}
				} else {
					if (jobOrder.getHourlyRateW2Max() != null && jobOrder.getHourlyRateW2Max() > 0) {
						content += "<br><b>W-2 Rate Range : </b> $" + jobOrder.getHourlyRateW2Max();
					}
				}
				if (jobOrder.getHourlyRateC2c() != null && jobOrder.getHourlyRateC2c() > 0) {
					content += "<br><b>Corp-to-Corp Rate Range : </b> $" + jobOrder.getHourlyRateC2c();
					if (jobOrder.getHourlyRateC2cmax() != null && jobOrder.getHourlyRateC2cmax() > 0) {
						content += " - $" + jobOrder.getHourlyRateC2cmax();
					}
				} else {
					if (jobOrder.getHourlyRateC2cmax() != null && jobOrder.getHourlyRateC2cmax() > 0) {
						content += "<br><b>Corp-to-Corp Rate Range : </b> $" + jobOrder.getHourlyRateC2cmax();
					}
				}
				if (jobOrder.getSalary() != null && jobOrder.getSalary() > 0) {
					content += "<br><b>Salary : </b> $" + jobOrder.getSalary();
				} else {
					content += "<br><b>Salary : </b> NA";
				}

				content += "<br><b>Reason : </b>" + reason + "<br/><br/>" + "<br><br><br><br> "
						+ "<b>***This is an automatically generated email, please do not reply ***</b> ";*/
				String content=jobOrderEmailBody(jobOrder,"moved to hot list");
				try {
					commService.sendEmail(null, to, null, subject, content, null);
				} catch (ServiceException e) {
					log.error(e.getMessage(), e);
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	
	private String jobOrderEmailBody(JobOrder jobOrder,String emailTopic){
		String modifiedDate = jobOrder.getUpdatedOn() == null ? Utils.convertDateToString(jobOrder.getCreatedOn()) : Utils.convertDateToString((jobOrder.getUpdatedOn()));
		//String modifiedBy = jobOrder.getUpdatedBy() == null ? jobOrder.getCreatedBy() : jobOrder.getUpdatedBy();
		String modifiedBy = jobOrder.getDmName();
		String reason = null;
		if(jobOrder.getReason()!=null){
			reason = new String(jobOrder.getReason());
		}
		String content = "Hi,<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The  Job Order having Id <b>" + jobOrder.getId() + "</b> "+emailTopic
				+ " by <b>" + modifiedBy + "</b> on <b>" + modifiedDate + "</b>.<br/> " + "Job Order details are as follows... <br><br>" + "<b>Title : </b>"
				+ jobOrder.getTitle() + " <br><b>Key Skills : </b>" + jobOrder.getKeySkills() + " <br>" + "<b>Location : </b>" + jobOrder.getLocation()
				+ " <br><b>Client : </b>" + jobOrder.getCustomer() + " <br>" + "<b>Status : </b>" + jobOrder.getStatus().name()
				+ "<br><b>Priority : </b>" + jobOrder.getPriority() + "<br><b>Job Type : </b>" + jobOrder.getJobType();

		if (jobOrder.getHourlyRateW2() != null && jobOrder.getHourlyRateW2() > 0) {
			content += "<br><b>W-2 Rate Range : </b> $" + jobOrder.getHourlyRateW2();
			if (jobOrder.getHourlyRateW2Max() != null && jobOrder.getHourlyRateW2Max() > 0) {
				content += " - $" + jobOrder.getHourlyRateW2Max();
			}
		} else {
			if (jobOrder.getHourlyRateW2Max() != null && jobOrder.getHourlyRateW2Max() > 0) {
				content += "<br><b>W-2 Rate Range : </b> $" + jobOrder.getHourlyRateW2Max();
			}
		}
		if (jobOrder.getHourlyRateC2c() != null && jobOrder.getHourlyRateC2c() > 0) {
			content += "<br><b>Corp-to-Corp Rate Range : </b> $" + jobOrder.getHourlyRateC2c();
			if (jobOrder.getHourlyRateC2cmax() != null && jobOrder.getHourlyRateC2cmax() > 0) {
				content += " - $" + jobOrder.getHourlyRateC2cmax();
			}
		} else {
			if (jobOrder.getHourlyRateC2cmax() != null && jobOrder.getHourlyRateC2cmax() > 0) {
				content += "<br><b>Corp-to-Corp Rate Range : </b> $" + jobOrder.getHourlyRateC2cmax();
			}
		}
		if (jobOrder.getSalary() != null && jobOrder.getSalary() > 0) {
			content += "<br><b>Salary : </b> $" + jobOrder.getSalary();
		} else {
			content += "<br><b>Salary : </b> NA";
		}
		content += "<br>"+(jobOrder.getReason()!=null?("<b>Reason : </b>" + reason + "<br/>"):"")+"<br/>" + "<br><br><br><br> "
				+ "<b>***This is an automatically generated email, please do not reply ***</b> ";
		return content;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.JobOrderService#getDMsSubmittalStatsReport(
	 * java.util.Date, java.util.Date)
	 */
	@Override
	public Map<String, Object> getDMsSubmittalStatsReport(Date startDate, Date endDate,String loginUserId) {

		Map<String, Object> dmSubmittalMap = null;
		try {
			StringBuilder query = new StringBuilder(
					"select result.assigned_bdm,"
					+ " count(result.status) AS submitted_count, "
					+ " count(CASE WHEN result.status='ACCEPTED' THEN 1 END) AS accepted_count, "
					+ " count(CASE WHEN result.status='DMREJ' THEN 1 END) AS dmrej_count, "
					+ " count(CASE WHEN result.status='INTERVIEWING' THEN 1 END) AS inteviewing_count, "
					+ " count(CASE WHEN result.status='CONFIRMED' THEN 1 END) AS confirmed_count, "
					+ " count(CASE WHEN result.status='REJECTED' THEN 1 END) AS rejected_count, "
					+ " count(CASE WHEN result.status='STARTED' THEN 1 END) AS started_count, "
					+ " count(CASE WHEN result.status='BACKOUT' THEN 1 END) AS backout_count, "
					+ " count(CASE WHEN result.status='OUTOFPROJ' THEN 1 END) AS outofproj_count, "
					+ " count(CASE WHEN result.status='SUBMITTED' THEN 1 END) AS nu_count "
					+ "  from "
					+ " (select "
					+ " (CASE WHEN us.user_role = 'DM' THEN us.user_id ELSE "
					+ " CASE WHEN us.assigned_bdm ISNULL THEN  us.user_id ELSE  assigned_bdm END END) AS assigned_bdm, "
					+ " res.status AS status "
					+ "  from "
					+ " (select COALESCE(j.dmname, j.created_by) AS created_by,s.status from submittal s,user_acct u,job_order j where "
					+ " date(COALESCE(s.updated_on,s.created_on)) >='"+startDate+"' AND "
					+ " date(COALESCE(s.updated_on,s.created_on)) <='"+endDate+"' "
					+ " AND s.delete_flag=0 "
					+ " AND s.order_id=j.order_id "
					+ " AND s.created_by=u.user_id "
					+ " )AS res,user_acct us "
					+ " where res.created_by = us.user_id)AS result,user_acct uas"
					+ " where uas.user_id = result.assigned_bdm "
					+ " AND uas.status='ACTIVE' AND uas.user_role='DM'"
					+ " group by result.assigned_bdm ");
			
			List<?> list = orderDao.findBySQLQuery(query.toString(), null);

			Set<String> dmNameList = new HashSet<String>();
			if (list == null) {
				list = new ArrayList<>();
			}

			// Putting each record into dmNameList
			if (list != null) {
				log.info("List-->" + list.size());
				List<SubmittalStatsDto> userWithSubmittalList = new ArrayList<SubmittalStatsDto>();
				Map<String,String> userWithSubmittalMap=new HashMap<String,String>(); 
				Iterator<?> iterator = list.iterator();
				while (iterator.hasNext()) {
					Object[] obj = (Object[]) iterator.next();
					SubmittalStatsDto dto=new SubmittalStatsDto();
					dto.setUserId(Utils.getStringValueOfObj(obj[0]));
					dto.setSUBMITTED(Utils.getIntegerValueOfObj(obj[1]));
					dto.setDMREJ(Utils.getIntegerValueOfObj(obj[3]));
					dto.setACCEPTED(Utils.getIntegerValueOfObj(obj[2]));

					dto.setINTERVIEWING(Utils.getIntegerValueOfObj(obj[4]));
					dto.setCONFIRMED(Utils.getIntegerValueOfObj(obj[5]));
					dto.setREJECTED(Utils.getIntegerValueOfObj(obj[6]));
					dto.setSTARTED(Utils.getIntegerValueOfObj(obj[7]));
					dto.setBACKOUT(Utils.getIntegerValueOfObj(obj[8]));
					dto.setOUTOFPROJ(Utils.getIntegerValueOfObj(obj[9]));
					dto.setNotUpdated(Utils.getIntegerValueOfObj(obj[10]));
					userWithSubmittalList.add(dto);
					userWithSubmittalMap.put(dto.getUserId(), dto.getUserId());
					// Putting dm/createdBy name
					dmNameList.add(dto.getUserId());
				}

				Map<String, UserDto> userIdWithUserMap = new HashMap<String, UserDto>();

				Map<String, Map<String, Integer>> openAndClosedJobOrderMap = findJobOrdersReportCount(dmNameList, userIdWithUserMap, startDate, endDate);
				/*// It is for inserting submittal count of each user
				Map<String, Map<SubmittalStatus, Integer>> userWithSubmittalCountMap = new HashMap<String, Map<SubmittalStatus, Integer>>();
				if (submittalDtoList != null && submittalDtoList.size() > 0) {
					for (SubmittalDto dto : submittalDtoList) {
						try {

							String dmName = userIdWithUserMap.get(dto.getDmName()).getUserRole().equals(UserRole.DM) ? dto.getDmName()
									: userIdWithUserMap.get(dto.getDmName()).getAssignedBdm() != null ? userIdWithUserMap.get(dto.getDmName()).getAssignedBdm()
											: dto.getDmName();

							if (userIdWithUserMap.get(dmName) != null && !userIdWithUserMap.get(dmName).getUserRole().name().equals(Constants.DM)) {
								dmName = dto.getDmName();
							}

							if (userIdWithUserMap.get(dmName) != null && userIdWithUserMap.get(dmName).getUserRole().name().equals(Constants.DM)) {
								if (userWithSubmittalCountMap.get(dmName) != null) {
									insertSubmittalCount(userWithSubmittalCountMap, dmName, SubmittalStatus.valueOf(dto.getStatus()));
								} else {
									userWithSubmittalCountMap.put(dmName, new HashMap<SubmittalStatus, Integer>());
									insertSubmittalCount(userWithSubmittalCountMap, dmName, SubmittalStatus.valueOf(dto.getStatus()));
								}
							}

						} catch (Exception e) {
							// System.out.println(dto.getDmName());
							log.error("Error in getDMsSubmittalStatsReport() because of " + dto.getDmName());
						}
					}
				}*/
				Boolean isLoginUserExists = false;
				if(loginUserId!=null){
					if(userWithSubmittalMap.get(loginUserId)!=null || openAndClosedJobOrderMap.get(loginUserId)!=null){
						isLoginUserExists = true;
					}
					if(!isLoginUserExists){
						userIdWithUserMap.put(loginUserId, userService.getUsersByIds(Arrays.asList(loginUserId)).get(0));
						SubmittalStatsDto dto=new SubmittalStatsDto();
						getSubmittalWithEmptyValues(dto, loginUserId);
						userWithSubmittalMap.put(loginUserId, loginUserId);
						userWithSubmittalList.add(dto);
					}
				}
				if (userWithSubmittalList != null && openAndClosedJobOrderMap != null) {
					for (String status : openAndClosedJobOrderMap.keySet()) {
						for (String userId : openAndClosedJobOrderMap.get(status).keySet()) {
							if (userWithSubmittalMap.get(userId) == null) {
								SubmittalStatsDto dto=new SubmittalStatsDto();
								userWithSubmittalMap.put(userId, userId);
								getSubmittalWithEmptyValues(dto, userId);
								userWithSubmittalList.add(dto);
							}
						}
					}
				}
				
				

				// It is for final one
				dmSubmittalMap = new HashMap<String, Object>();
				
				// It is for final submittal related count
				Map<String, Integer> submittalTotalsByStatus = new HashMap<String, Integer>();
				// It is for open and closed count
				Map<String, Integer> openAndClosedJobOrdersMap = new HashMap<String, Integer>();
				for(SubmittalStatsDto dto:userWithSubmittalList){
					dto.setName(userIdWithUserMap.get(dto.getUserId()).getFullName());
					dto.setLocation(userIdWithUserMap.get(dto.getUserId()).getOfficeLocation());
					if (openAndClosedJobOrderMap.get(Constants.OPEN) != null) {
						dto.setOpenJobOrders(Utils.getIntegerValueOfObj(openAndClosedJobOrderMap.get(Constants.OPEN).get(dto.getUserId())));
					} else {
						dto.setOpenJobOrders(0);
					}
					if (openAndClosedJobOrderMap.get(Constants.CLOSED) != null) {
						dto.setClosedJobOrders(Utils.getIntegerValueOfObj(openAndClosedJobOrderMap.get(Constants.CLOSED).get(dto.getUserId())));
					} else {
						dto.setClosedJobOrders(0);
					}
					// This is for open job orders
					openAndClosedJobOrdersMap.put(Constants.OPEN, (openAndClosedJobOrdersMap.get(Constants.OPEN) != null
							? (openAndClosedJobOrdersMap.get(Constants.OPEN) + dto.getOpenJobOrders()) : dto.getOpenJobOrders()));
					// This is for closed job orders
					openAndClosedJobOrdersMap.put(Constants.CLOSED, (openAndClosedJobOrdersMap.get(Constants.CLOSED) != null
							? (openAndClosedJobOrdersMap.get(Constants.CLOSED) + dto.getClosedJobOrders()) : dto.getClosedJobOrders()));
//					userWithSubmittalList.add(dto);
				}

				Collections.sort(userWithSubmittalList, new Comparator<SubmittalStatsDto>() {

					@Override
					public int compare(SubmittalStatsDto o1, SubmittalStatsDto o2) {
						return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
					}
				});

				submittalTotalsByStatus.put(Constants.OPEN, openAndClosedJobOrdersMap.get(Constants.OPEN));
				submittalTotalsByStatus.put(Constants.CLOSED, openAndClosedJobOrdersMap.get(Constants.CLOSED));

				for (SubmittalStatus submittalStatus : SubmittalStatus.values()) {
					if (submittalTotalsByStatus.get(submittalStatus.name()) == null) {
						submittalTotalsByStatus.put(submittalStatus.name(), 0);
					}
				}
				
				if(loginUserId!=null){
					Iterator<SubmittalStatsDto> dtoIterator = userWithSubmittalList.iterator();
					while(dtoIterator.hasNext()){
						SubmittalStatsDto dto = dtoIterator.next();
						if(!dto.getUserId().equalsIgnoreCase(loginUserId)){
							dtoIterator.remove();
						}
					}
				}
				
				Utils.getTotalCount(userWithSubmittalList, submittalTotalsByStatus);
				
				if(loginUserId!=null){
					submittalTotalsByStatus.put(Constants.OPEN, userWithSubmittalList.get(0).getOpenJobOrders());
					submittalTotalsByStatus.put(Constants.CLOSED, userWithSubmittalList.get(0).getClosedJobOrders());
				}
				
				dmSubmittalMap.put("records", userWithSubmittalList);
				dmSubmittalMap.put("totalRecordsCount", submittalTotalsByStatus);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		return dmSubmittalMap;
	}
	
	private void getSubmittalWithEmptyValues(SubmittalStatsDto dto,String userId){
		dto.setUserId(userId);
		dto.setSUBMITTED(0);
		dto.setDMREJ(0);
		dto.setACCEPTED(0);

		dto.setINTERVIEWING(0);
		dto.setCONFIRMED(0);
		dto.setREJECTED(0);
		dto.setSTARTED(0);
		dto.setBACKOUT(0);
		dto.setOUTOFPROJ(0);
		dto.setNotUpdated(0);
	}
	
	@Override
	public List<SubmittalStatsDto> getDMSubmittalDetailsById( DashboardSearchDto dashboardSearchDto) {

		List<SubmittalStatsDto> submittalStatsDtoList = null;
		try {
			StringBuilder query = new StringBuilder(
					"select  "
					+ " result.created_by AS user_id, "
					+ " ((CASE WHEN ua.first_name ISNULL THEN '' ELSE ua.first_name END) ||' '|| "
					+ " (CASE WHEN ua.last_name ISNULL THEN '' ELSE ua.last_name END))AS full_name, "
					+ " ua.assigned_bdm, "
					+ " result.updated_on AS updated_on, "
					+ " result.customer AS client, "
					+ " result.title AS title, "
					+ " result.full_name AS cand_full_name "
					+ " from (select s.created_by,s.updated_on,j.customer,j.title, "
					+ " ((CASE WHEN c.first_name ISNULL THEN '' ELSE c.first_name END) ||' '|| "
					+ " (CASE WHEN c.last_name ISNULL THEN '' ELSE c.last_name END)) AS full_name "
					+ "  from submittal s,job_order j,user_acct u,candidate c "
					+ " where s.delete_flag=0 "
					+ " AND s.candidate_id=c.candidate_id "
					+ " AND s.order_id=j.order_id AND date(COALESCE(s.updated_on,s.created_on)) >='"+dashboardSearchDto.getDtStartDate()+"' AND "
					+ " date(COALESCE(s.updated_on,s.created_on)) <='"+dashboardSearchDto.getDtEndDate()+"' "
					+ " AND COALESCE(j.dmname, j.created_by)=u.user_id "
					+ " AND COALESCE(j.dmname, j.created_by) in (select u.user_id from user_acct u where ((u.assigned_bdm ="
					+ "       '"+dashboardSearchDto.getUserId()+"' AND u.user_role not in ('DM')) or"
							+ " u.user_id = '"+dashboardSearchDto.getUserId()+"' or u.assigned_bdm in (select us.user_id from "
					+ "        user_acct us where us.assigned_bdm = '"+dashboardSearchDto.getUserId()+"' AND us.user_role not in ('DM'))))" 
					+ " AND s.status='"+dashboardSearchDto.getStatus()+"') AS result,user_acct ua where result.created_by=ua.user_id ");
			
			List<?> list = orderDao.findBySQLQuery(query.toString(), null);


			// Putting each record into dmNameList
			if (list != null) {
				log.info("List-->" + list.size());
				submittalStatsDtoList = new ArrayList<SubmittalStatsDto>();
				Iterator<?> iterator = list.iterator();
				while (iterator.hasNext()) {
					Object[] obj = (Object[]) iterator.next();
					SubmittalStatsDto dto=new SubmittalStatsDto();
					dto.setClientName(Utils.getStringValueOfObj(obj[4]));
					dto.setJobTitle(Utils.getStringValueOfObj(obj[5]));
					dto.setCandidateFullName(Utils.getStringValueOfObj(obj[6]));
					dto.setCreatedOrUpdatedBy(Utils.getStringValueOfObj(obj[1]));
					dto.setCreatedDate(Utils.convertDateToString_HH_MM_A((Date) obj[3]));
					dto.setAssignedBdm(Utils.getStringValueOfObj(obj[2]));
					submittalStatsDtoList.add(dto);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		return submittalStatsDtoList;
	}
	
	

	private static void insertSubmittalCount(Map<String, Map<SubmittalStatus, Integer>> userWithSubmittalCountMap, String dmName, SubmittalStatus status) {
		if (userWithSubmittalCountMap.get(dmName).get(status) != null) {
			int count = userWithSubmittalCountMap.get(dmName).get(status) + 1;
			userWithSubmittalCountMap.get(dmName).put(status, count);
		} else {
			userWithSubmittalCountMap.get(dmName).put(status, 1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.JobOrderService#getUserSubmittalDetailsById(
	 * com.uralian.cgiats.dto.DashboardSearchDto)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SubmittalStatsDto> getUserSubmittalDetailsById(DashboardSearchDto dashboardSearchDto) {
		List<SubmittalStatsDto> returnList = null;
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			StringBuilder query = null;
			List<String> userIds = null;
			if (dashboardSearchDto.getIsDm()) {
				query = new StringBuilder(
						"select u.userId from User u where u.assignedBdm = ?1 ");
				if(dashboardSearchDto.getUserStatus()!=null){
				query.append( " and u.status='"+dashboardSearchDto.getUserStatus()+"' ");
				}
						query.append(" and (u.userRole = 'ADM' or u.userRole = 'Recruiter')");
				params.put("1", dashboardSearchDto.getUserId());
				userIds = (List<String>) orderDao.findByQuery(query.toString(), 0, 0, params);
			}

			query = new StringBuilder("select s.jobOrder.customer,s.jobOrder.title,(s.candidate.firstName||' '||s.candidate.lastName)");
			// if (dashboardSearchDto.getIsDm()) {
			// query.append(
			// ",COALESCE(s.updatedBy,s.createdBy),u.firstName,u.lastName from
			// Submittal s,User u where s.deleteFlag = 0 and
			// COALESCE(s.updatedBy,s.createdBy) = u.userId "
			// + "and (COALESCE(s.createdDate,s.createdOn)>=?1 and
			// COALESCE(s.createdDate,s.createdOn) <=?2)");
			// } else {
			query.append(
					",s.createdBy,u.firstName,u.lastName,COALESCE(s.createdDate,s.createdOn),u.assignedBdm from Submittal s,User u where s.deleteFlag = 0");
			if(dashboardSearchDto.getUserStatus()!=null){
				query.append( " and u.status='"+dashboardSearchDto.getUserStatus()+"' ");
				}
			query.append( " and (COALESCE(s.createdDate,s.createdOn)>=?1 and COALESCE(s.createdDate,s.createdOn) <=?2)");
			// }

			if (!dashboardSearchDto.getStatus().equalsIgnoreCase(SubmittalStatus.SUBMITTED.name())) {
				if (dashboardSearchDto.getStatus().equalsIgnoreCase(Constants.NOT_UPDATED)) {
					dashboardSearchDto.setStatus(SubmittalStatus.SUBMITTED.name());
				}
				query.append("  and s.status = ?3");
			}
			if (dashboardSearchDto.getIsDm() && userIds != null) {
				userIds.add(dashboardSearchDto.getUserId());
				query.append(" and u.userId = s.createdBy and COALESCE(s.jobOrder.dmName,s.jobOrder.createdBy) in ?4 ");
				params.put("4", userIds);
			} else if (dashboardSearchDto.getUserId() != null) {
				query.append(" and s.createdBy = ?4 and u.userId = s.createdBy ");
				params.put("4", dashboardSearchDto.getUserId());
			}
			params.put("1", dashboardSearchDto.getDtStartDate());
			params.put("2", dashboardSearchDto.getDtEndDate());
			if (query.toString().contains("3")) {
				params.put("3", SubmittalStatus.valueOf(dashboardSearchDto.getStatus().toUpperCase()));
			}
			List<?> list = orderDao.findByQuery(query.toString(), 0, 0, params);
			if (list != null && list.size() > 0) {
				Iterator<?> iterator = list.iterator();
				returnList = new ArrayList<SubmittalStatsDto>();
				while (iterator.hasNext()) {
					Object[] obj = (Object[]) iterator.next();
					SubmittalStatsDto dto = new SubmittalStatsDto();
					dto.setClientName(Utils.getStringValueOfObj(obj[0]));
					dto.setJobTitle(Utils.getStringValueOfObj(obj[1]));
					dto.setCandidateFullName(Utils.getStringValueOfObj(obj[2]));
					dto.setCreatedOrUpdatedBy(Utils.concatenateTwoStringsWithSpace((String) obj[4], (String) obj[5]));
					dto.setCreatedDate(Utils.convertDateToString_HH_MM_A((Date) obj[6]));
					dto.setAssignedBdm(Utils.getStringValueOfObj(obj[7]));
					returnList.add(dto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return returnList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.service.JobOrderService#getPossibleHotJobOrders()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<JobOrder> getPossibleJobOrdersToHot() {
		List<JobOrder> list = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -2);

			Date twoDayBeforeDate = cal.getTime();

			log.info("Two days before Date :::: " + twoDayBeforeDate);

			StringBuffer hqlSelect = new StringBuffer(
					"select j from JobOrder j where j.jobExpireIn in ?1 and j.deleteFlag = 0 and COALESCE(j.updatedOn,j.createdOn) <= ?2"
							+ " and j.hot = false and j.status in ?3");
			Map<String, Object> params = new HashMap<String, Object>();
			List<String> jobExpiresIn = new ArrayList<String>(Arrays.asList(JobExpireStatus.WEEK1.getStatus(), JobExpireStatus.ONGOING.getStatus()));
			List<JobOrderStatus> jobOrderStatusList = new ArrayList<JobOrderStatus>(Arrays.asList(JobOrderStatus.OPEN, JobOrderStatus.REOPEN,JobOrderStatus.ASSIGNED));
			params.put("1", jobExpiresIn);
			params.put("2", twoDayBeforeDate);
			params.put("3", jobOrderStatusList);

			log.info("Query :::: <<<<<<< " + hqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			list = (List<JobOrder>) orderDao.findByQuery(hqlSelect.toString(), -1, -1, params);
			
			if(list!=null && list.size()>0){
				String orderIds = null;
				for(JobOrder order:list){
					if(orderIds==null){
						orderIds = String.valueOf(order.getId());
					}else{
						orderIds+=","+String.valueOf(order.getId());
					}
				}
				StringBuilder sb=new StringBuilder("select s.order_id,count(*)  from submittal s where s.order_id in ("+orderIds+") group by s.order_id");
				List<?> resultList = submittalDao.findBySQLQuery(sb.toString(), null);
				if (resultList != null && resultList.size() > 0) {
					for (Object object : resultList) {
						Object obj[] = (Object[]) object;
						Integer orderId = Utils.getIntegerValueOfBigDecimalObj(obj[0]);
						Integer count = Utils.getIntegerValueOfBigDecimalObj(obj[1]);
						for (JobOrder order : list) {
							if (order.getId().equals(orderId)
									&& ((Integer) (order.getNoOfResumesRequired() != null ? order.getNoOfResumesRequired() : 0)).compareTo(count) <= 0) {
								list.remove(order);
								break;
							}
						}
					}
				}
			}
			
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * This is to get all the job orders those have zero submittals in last 24
	 * hours(not considering the jobs expire in 12 and 24 hours)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<?> getJobOrdersByWithoutSubmittals() {
		List<JobOrderDto> list = null;
		try {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);

			Date oneDayBeforeDate = cal.getTime();

			log.info("One day before Date :::: " + oneDayBeforeDate);

			// Query is getting joborders whose don't have required no.of
			// positions
			StringBuffer sqlSelect = new StringBuffer(
					"select j1.order_id,count(*) from submittal s1,(select j.order_id,j.no_of_resumes_required,count(*) as cnt"
							+ " from submittal s,job_order j where s.order_id= j.order_id  and j.no_of_resumes_required notnull"
							+ " and s.delete_flag = 0 and j.status in ('OPEN','ASSIGNED','REOPEN') and date(j.created_on) <= date(:startDate) and j.job_expire_in not in"
							+ " ('" + JobExpireStatus.HOURS12.getStatus() + "','" + JobExpireStatus.HOURS24.getStatus() + "') "
							+ " group by j.order_id ) as j1  where s1.order_id = j1.order_id and j1.cnt < j1.no_of_resumes_required  group by j1.order_id");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("startDate", oneDayBeforeDate);

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = (List<Integer>) orderDao.findBySQLQuery(sqlSelect.toString(), params);
			if (resultList != null && resultList.size() > 0) {
				list = new ArrayList<JobOrderDto>();
				for (Object obj : resultList) {
					Object[] object = (Object[]) obj;
					JobOrderDto dto = new JobOrderDto();
					dto.setJobOrderId(Utils.getIntegerValueOfObj(object[0]));
					dto.setCount(Utils.getIntegerValueOfObj(object[1]));
					list.add(dto);
				}
			}
			// System.out.println(list);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.JobOrderService#getJobOrderReport(com.uralian.
	 * cgiats.dto.ReportwiseDto)
	 */
	@Override
	public List<?> getJobOrderReport(ReportwiseDto reportwiseDto) {
		List<Map<String, Object>> list = null;
		try {
			StringBuffer sqlSelect = new StringBuffer("select count(CASE WHEN j.status in ('OPEN','ASSIGNED','REOPEN') THEN 1 END) AS OPEN,"
					+ " count(CASE WHEN j.status in ('CLOSED','FILLED') THEN 1 END) AS CLOSED " + " from job_order j where j.delete_flg=0 ");
			// + " and EXTRACT(YEAR FROM j.created_on) in (2016,2015)"
			// + " and to_char(COALESCE(j.created_on),'Mon') in ('Jan','Feb')");
			if (reportwiseDto.getYear() != null) {
				sqlSelect.append(" and  EXTRACT(YEAR FROM j.created_on) in (" + reportwiseDto.getYear() + ")");
			}
			if (reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() > 0) {
				sqlSelect.append(" and to_char(COALESCE(j.created_on),'Mon') in (" + Utils.getListWithSingleQuote(reportwiseDto.getMonths()) + ")");
			}
			// if (reportwiseDto.getDmNames() != null &&
			// reportwiseDto.getDmNames().size() > 0) {
			// sqlSelect.append(" and (s.assigned_dm in (" +
			// Utils.getListWithSingleQuote(reportwiseDto.getDmNames()) + ") or
			// s.recut_name in ("
			// + Utils.getListWithSingleQuote(reportwiseDto.getDmNames()) +
			// "))");
			// }
			if (reportwiseDto.getWeek() != null && reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() == 1
					&& reportwiseDto.getMonths().get(0).equalsIgnoreCase(MonthEnum.JAN.getMonth())) {
				sqlSelect.append(" and (extract(week from j.created_on) in (" + reportwiseDto.getWeek() + "))");
			} else if (reportwiseDto.getWeek() != null) {
				sqlSelect.append(" and (extract(week from j.created_on) - " + "extract(week from date_trunc('month', j.created_on)) + 1) in ("
						+ reportwiseDto.getWeek() + ")");
			}

			// if (reportwiseDto.getStatuses() != null &&
			// reportwiseDto.getStatuses().size() > 0) {
			// sqlSelect.append(" and s.status in (" +
			// Utils.getListWithSingleQuote(reportwiseDto.getStatuses()) + ")");
			// }
			// sqlSelect.append(" group by year;");

			/*
			 * { "x": "Started", "y": [ 70 ], "tooltip": "Started",
			 * "label":"Started" },
			 */

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				list = new ArrayList<Map<String, Object>>();
				for (Object object : resultList) {
					Object obj[] = (Object[]) object;
					Map<String, Object> map1 = new HashMap<String, Object>();
					// [{ name: 'One', y: 10},{ name: 'Two', y: 10},{ name:
					// 'Three', y: 10},{ name: 'Four', y: 10}]
					map1.put("name", Constants.OPEN);
					map1.put(Constants.Y, obj[0]);

					Map<String, Object> map2 = new HashMap<String, Object>();

					map2.put("name", Constants.CLOSED);
					map2.put(Constants.Y, obj[1]);

					list.add(map1);
					list.add(map2);
				}

				// log.info("Size of the Records ::: " + resultList.size());
				// list = new ArrayList<SubmittalStatsDto>();
				// getSubmittalData(resultList, list);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public Object getAllDMsOpenAndClosedJobOrders(ReportwiseDto reportwiseDto) {
		List<Map<String, Object>> list = null;
		try {
			StringBuffer sqlSelect = new StringBuffer("SELECT result.DMNAME,");
					if (reportwiseDto.getStatus() != null && reportwiseDto.getStatus().length()>0) {
						if (reportwiseDto.getStatus().equalsIgnoreCase(Constants.OPEN)) {
							sqlSelect.append(" sum(result.OPEN) AS OPEN,");
						} else {
							sqlSelect.append(" sum(result.CLOSED) AS CLOSED, ");
						}
					}
					
					sqlSelect.append( " ((CASE WHEN ua.first_name ISNULL THEN '' ELSE ua.first_name END)||' '||"
					+ " (CASE WHEN ua.last_name ISNULL THEN '' ELSE ua.last_name END)) AS FULL_NAME" + " from  (select "
					+ " count(CASE WHEN j.status in ('OPEN','ASSIGNED','REOPEN') THEN 1 END) AS OPEN, "
					+ "count(CASE WHEN j.status in ('CLOSED','FILLED') THEN 1 END) AS CLOSED " + " ,j.created_by,u.user_id,u.assigned_bdm, "
					+ " CASE WHEN u.user_role ='DM' THEN u.user_id " + " WHEN u.assigned_bdm ISNULL THEN u.user_id ELSE " + " u.assigned_bdm END AS DMNAME "
					+ "  from  job_order j,user_acct u where j.created_by = u.user_id " + " and j.delete_flg=0   ");

			// + "EXTRACT(YEAR FROM j.created_on) in (2016) "
			// + " and to_char(j.created_on,'Mon') in ('Jan','Feb','May') "
			// + "and (extract(week from j.created_on) - "
			// + " extract(week from date_trunc('month', j.created_on)) + 1) "
			// + " in (1,2,3,4,5) "
			// + " group by j.created_by,u.user_id) AS result,user_acct ua "
			// + " where result.DMNAME = ua.user_id "
			// + " group by result.DMNAME,ua.user_id ");

			if (reportwiseDto.getYear() != null) {
				sqlSelect.append("  and EXTRACT(YEAR FROM j.created_on) in (" + reportwiseDto.getYear() + ")");
			}
			if (reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() > 0) {
				sqlSelect.append(" and to_char(j.created_on,'Mon') in (" + Utils.getListWithSingleQuote(reportwiseDto.getMonths()) + ")");
			}
			if (reportwiseDto.getDmNames() != null && reportwiseDto.getDmNames().size() > 0) {
				sqlSelect.append(" and (s.created_by in (" + Utils.getListWithSingleQuote(reportwiseDto.getDmNames()) + "))");
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() == 1
					&& reportwiseDto.getMonths().get(0).equalsIgnoreCase(MonthEnum.JAN.getMonth())) {
				sqlSelect.append(" and (extract(week from j.created_on) in (" + reportwiseDto.getWeek() + "))");
			} else if (reportwiseDto.getWeek() != null) {
				sqlSelect.append(" and (extract(week from j.created_on) - " + "extract(week from date_trunc('month', j.created_on)) + 1) in ("
						+ reportwiseDto.getWeek() + ")");
			}

			sqlSelect.append(" group by j.created_by,u.user_id) AS result,user_acct ua " + " where result.DMNAME = ua.user_id");
			if (reportwiseDto.getStatus() != null && reportwiseDto.getStatus().length()>0) {
				if (reportwiseDto.getStatus().equalsIgnoreCase(Constants.OPEN)) {
					sqlSelect.append(" and  result.OPEN !=0");
				} else {
					sqlSelect.append("  and result.CLOSED !=0 ");
				}
			}
			sqlSelect.append(" group by result.DMNAME,ua.user_id ");

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			List<?> averageJobOrderServiceList = (List<?>) getDMWise_Average_JobOrderServiceReport(reportwiseDto);
			
			if (resultList != null && resultList.size() > 0) {
				Map<String,Object> averageJobOrderServiceMap=new HashMap<String,Object>();
				if(averageJobOrderServiceList!=null && averageJobOrderServiceList.size()>0){
					for(Object object:averageJobOrderServiceList){
						Object obj[]=(Object[]) object;
						averageJobOrderServiceMap.put(Utils.getStringValueOfObj(obj[0]), Utils.getStringValueOfObj(obj[2]));
								
					}
				}
				
				
				list = new ArrayList<Map<String, Object>>();
				for (Object object : resultList) {
					Object obj[] = (Object[]) object;
					Map<String, Object> map = new HashMap<String, Object>();
					// [{ name: 'One', y: 10},{ name: 'Two', y: 10},{ name:
					// 'Three', y: 10},{ name: 'Four', y: 10}]
					map.put(Constants.NAME, obj[2]);
					map.put(Constants.Y, obj[1]);
					map.put(Constants.USERID, obj[0]);
					map.put(Constants.AVERAGE_DAYS, Math.round(Utils.getTwoDecimalDoubleFromObj(averageJobOrderServiceMap.get(Utils.getStringValueOfObj(obj[0])))));
					list.add(map);
				}

				// log.info("Size of the Records ::: " + resultList.size());
				// list = new ArrayList<SubmittalStatsDto>();
				// getSubmittalData(resultList, list);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return list;
	}
	
	
	@Override
	public Object getDMWise_Average_JobOrderServiceReport(ReportwiseDto reportwiseDto) {
		Object resultObj = null;
		try {
			StringBuffer sqlSelect = new StringBuffer("  select  result.DMNAME,count(*),sum(result.DATE_DIFF)/count(*)/24 from"
					+ " (select j.order_id AS orderID,j.title as title,j.customer AS customer,"
					+ " j.status AS status,j.created_by AS createdBy,j.created_on AS createdOn,"
					+ " j.updated_on AS updatedOn,");
			if (reportwiseDto.getStatus() != null && reportwiseDto.getStatus().length()>0) {
				if (reportwiseDto.getStatus().equalsIgnoreCase(Constants.OPEN)) {
					sqlSelect.append("(extract ( epoch from (CURRENT_DATE-j.created_on))/60/60) AS DATE_DIFF,");
				} else {
					sqlSelect.append("(extract ( epoch from (j.updated_on-j.created_on))/60/60) AS DATE_DIFF,");
				}
			}
					sqlSelect.append(" j.assigned_to AS assignedTo,"
					+ "  CASE WHEN u.user_role ='DM' THEN u.user_id"
					+ " WHEN u.assigned_bdm ISNULL THEN u.user_id ELSE "
					+ " u.assigned_bdm END AS DMNAME "
					+ " from job_order j,user_acct u "
					+ " where j.delete_flg=0  "
					/*
					+ "(j.created_by in ('Sachin','siva') or j.created_by in "
					+ "  (select ua.user_id from user_acct ua where ua.assigned_bdm in ('Sachin','Devasya')))"*/
					
					+ "  and  j.created_by=u.user_id ");
//					+ "  and  EXTRACT(YEAR FROM j.created_on) in (2016)"
//					+ " and j.status in ('CLOSED','FILLED')) AS result  group by result.DMNAME");
			// + "and EXTRACT(YEAR from COALESCE(s.updated_on,s.created_on)) in
			// (2016) "
			// + " and to_char(COALESCE(s.updated_on,s.created_on),'Mon') in
			// ('Jan','Feb','May','Sep') "
			// + "and (extract(week from COALESCE(s.updated_on,s.created_on)) -
			// "
			// + " extract(week from date_trunc('month',
			// COALESCE(s.updated_on,s.created_on))) + 1) "
			// + " in (1,2,3,4,5,6) "
			// + ") AS result group by result.assignedbdm");
			if (reportwiseDto.getYear() != null) {
				sqlSelect.append("  and EXTRACT(YEAR FROM j.created_on) in (" + reportwiseDto.getYear() + ")");
			}
			if (reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() > 0) {
				sqlSelect.append(" and to_char(j.created_on,'Mon') in (" + Utils.getListWithSingleQuote(reportwiseDto.getMonths()) + ")");
			}
			if (reportwiseDto.getDmName() != null && reportwiseDto.getDmName().length() > 0) {
				sqlSelect.append(" and (j.created_by in ("+reportwiseDto.getDmName()+") or j.created_by in "
					+ " (select u.user_id from user_acct u where u.assigned_bdm in ("+reportwiseDto.getDmName()+")))");
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() == 1
					&& reportwiseDto.getMonths().get(0).equalsIgnoreCase(MonthEnum.JAN.getMonth())) {
				sqlSelect.append(" and EXTRACT(week from j.created_on) in (" + reportwiseDto.getWeek() + ")");
			} else if (reportwiseDto.getWeek() != null) {
				sqlSelect.append(" and (extract(week from j.created_on) - " + "extract(week from date_trunc('month', j.created_on)) + 1) in ("
						+ reportwiseDto.getWeek() + ")");
			}
			if (reportwiseDto.getStatus() != null && reportwiseDto.getStatus().length()>0) {
				if (reportwiseDto.getStatus().equalsIgnoreCase(Constants.OPEN)) {
					sqlSelect.append(" and j.status in ('OPEN','ASSIGNED','REOPEN') ");
				} else {
					sqlSelect.append(" and j.status in ('CLOSED','FILLED') ");
				}
			}

			sqlSelect.append(" ) AS result  group by result.DMNAME ");

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			resultObj = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return resultObj;
	}
	
	@Override
	public Object getOpenOrClosedOrdersByDM(ReportwiseDto reportwiseDto) {
		List<JobOrderDto> jobOrderDtoList = new ArrayList<JobOrderDto>();
		try {
			StringBuffer sqlSelect = new StringBuffer("select j.order_id AS orderID,j.title as title,j.customer AS customer,"
					+ " j.status AS status,j.created_by AS createdBy,j.created_on AS createdOn,j.assigned_to AS assignedTo");
			if (reportwiseDto.getStatus().equalsIgnoreCase(Constants.CLOSED)) {
				sqlSelect.append(" ,round((EXTRACT(EPOCH FROM j.updated_on-j.created_on)/3600)/24)");
			}else{
				sqlSelect.append(" ,round((EXTRACT(EPOCH FROM CURRENT_DATE-j.created_on)/3600)/24)");
			}
			sqlSelect.append(" ,num_pos AS noOfPos");
					sqlSelect.append("  from job_order j,user_acct ua "
					+ " where j.delete_flg=0 "
//					+ " and (j.created_by='Sachin' or j.created_by in "
//					+ " (select u.user_id from user_acct u where u.assigned_bdm = 'Sachin')) "
					+ " and  j.created_by=ua.user_id ");
//					+ " and  EXTRACT(YEAR FROM j.created_on) in (2016)   ");

			// + "EXTRACT(YEAR FROM j.created_on) in (2016) "
			// + " and to_char(j.created_on,'Mon') in ('Jan','Feb','May') "
			// + "and (extract(week from j.created_on) - "
			// + " extract(week from date_trunc('month', j.created_on)) + 1) "
			// + " in (1,2,3,4,5) "
			// + " group by j.created_by,u.user_id) AS result,user_acct ua "
			// + " where result.DMNAME = ua.user_id "
			// + " group by result.DMNAME,ua.user_id ");

			if (reportwiseDto.getYear() != null) {
				sqlSelect.append("  and EXTRACT(YEAR FROM j.created_on) in (" + reportwiseDto.getYear() + ")");
			}
			if (reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() > 0) {
				sqlSelect.append(" and to_char(j.created_on,'Mon') in (" + Utils.getListWithSingleQuote(reportwiseDto.getMonths()) + ")");
			}
			if (reportwiseDto.getDmName() != null && reportwiseDto.getDmName().length() > 0) {
				sqlSelect.append(" and (j.created_by='"+reportwiseDto.getDmName()+"' or j.created_by in "
					+ " (select u.user_id from user_acct u where u.assigned_bdm = '"+reportwiseDto.getDmName()+"'))");
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() == 1
					&& reportwiseDto.getMonths().get(0).equalsIgnoreCase(MonthEnum.JAN.getMonth())) {
				sqlSelect.append(" and EXTRACT(week from j.created_on) in (" + reportwiseDto.getWeek() + ")");
			} else if (reportwiseDto.getWeek() != null) {
				sqlSelect.append(" and (extract(week from j.created_on) - " + "extract(week from date_trunc('month', j.created_on)) + 1) in ("
						+ reportwiseDto.getWeek() + ")");
			}

			if (reportwiseDto.getStatus() != null && reportwiseDto.getStatus().length()>0) {
				if (reportwiseDto.getStatus().equalsIgnoreCase(Constants.OPEN)) {
					sqlSelect.append(" and j.status in ('OPEN','ASSIGNED','REOPEN') ");
				} else {
					sqlSelect.append(" and j.status in ('CLOSED','FILLED') ");
				}
			}

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				
				for(Object object:resultList){
					Object obj[]=(Object[])object;
					JobOrderDto dto = new JobOrderDto();
					dto.setJobOrderId(Utils.getIntegerValueOfBigDecimalObj(obj[0]));
					dto.setTitle(Utils.getStringValueOfObj(obj[1]));
					dto.setClient(Utils.getStringValueOfObj(obj[2]));
					dto.setStatus(Utils.getStringValueOfObj(obj[3]));
					dto.setCreatedBy(Utils.getStringValueOfObj(obj[4]));
					dto.setStrUpdatedOn(Utils.convertDateToString((Date)obj[5]));
//					Date jobOrderCreatedOn = Utils.convertAngularStrToDate(Utils.getStringValueOfObj(obj[5]));
//					if (reportwiseDto.getStatus().equalsIgnoreCase(Constants.CLOSED)) {
//						dto.setActiveDays(String.valueOf(Utils.getIntegerValueOfDoubleObj(obj[7])));
//					}else{
						dto.setActiveDays(String.valueOf(Utils.getIntegerValueOfDoubleObj(obj[7])));
//					}
					dto.setAssignedTo(Utils.getStringValueOfObj(obj[6]));
					dto.setNoOfPositions(Utils.getIntegerValueOfBigDecimalObj(obj[8]));
					jobOrderDtoList.add(dto);
				}
				
				
				
				List<Integer> jobOrderIds = new ArrayList<Integer>();
				for (JobOrderDto dto : jobOrderDtoList) {
					jobOrderIds.add(dto.getJobOrderId());
				}
				params = new HashMap<String, Object>();

				String hqlQuery = "select s.id,s.jobOrder.id,s.status from Submittal s where s.deleteFlag=0 and s.jobOrder.id in ?1";
				params.put("1", jobOrderIds);
				List<?> jobSubmittalList = orderDao.findByQuery(hqlQuery, -1, -1, params);
				if (jobSubmittalList != null && jobSubmittalList.size() > 0) {
					Map<Integer, Integer> jobOrderIdWithSubmittalCountMap = new HashMap<Integer, Integer>();
					Map<Integer, Integer> jobOrderIdWithSubmittalStartsCountMap = new HashMap<Integer, Integer>();
					Iterator<?> it = jobSubmittalList.iterator();
					while (it.hasNext()) {
						Object jobSubmittalObj[] = (Object[]) it.next();
						Integer orderId = Integer.parseInt(jobSubmittalObj[1].toString());
						SubmittalStatus status = (SubmittalStatus) jobSubmittalObj[2];
						if (jobOrderIdWithSubmittalCountMap.get(orderId) != null) {
							jobOrderIdWithSubmittalCountMap.put(orderId, jobOrderIdWithSubmittalCountMap.get(orderId) + 1);
						} else {
							jobOrderIdWithSubmittalCountMap.put(orderId, 1);
						}
						if (status.equals(SubmittalStatus.STARTED)) {
							if (jobOrderIdWithSubmittalStartsCountMap.get(orderId) != null) {
								jobOrderIdWithSubmittalStartsCountMap.put(orderId, jobOrderIdWithSubmittalStartsCountMap.get(orderId) + 1);
							} else {
								jobOrderIdWithSubmittalStartsCountMap.put(orderId, 1);
							}
						}
					}
					for (JobOrderDto dto : jobOrderDtoList) {
						if (jobOrderIdWithSubmittalCountMap.get(dto.getJobOrderId()) != null) {
							dto.setSbm(String.valueOf(jobOrderIdWithSubmittalCountMap.get(dto.getJobOrderId())));
						} else {
							dto.setSbm("0");
						}
						if(jobOrderIdWithSubmittalStartsCountMap.get(dto.getJobOrderId())!=null){
							dto.setOpenJobOrders(dto.getNoOfPositions()-jobOrderIdWithSubmittalStartsCountMap.get(dto.getJobOrderId()));
						}else{
							dto.setOpenJobOrders(dto.getNoOfPositions());
						}
					}
				} else {
					for (JobOrderDto dto : jobOrderDtoList) {
						dto.setSbm("0");
						dto.setOpenJobOrders(dto.getNoOfPositions());
					}
				}
				
				
				

				// log.info("Size of the Records ::: " + resultList.size());
				// list = new ArrayList<SubmittalStatsDto>();
				// getSubmittalData(resultList, list);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return jobOrderDtoList;
	}
	
	/*@Override
	public Object getAllClients(ReportwiseDto reportwiseDto) {
		List<Map<String, Object>> list=null;
		try {
			StringBuffer sqlSelect = new StringBuffer("select j.customer,count(*) as cnt from job_order j "
					+ " where j.delete_flg= 0 ");
//					+ " and  EXTRACT(YEAR FROM j.created_on) in (2016)   ");

			// + "EXTRACT(YEAR FROM j.created_on) in (2016) "
			// + " and to_char(j.created_on,'Mon') in ('Jan','Feb','May') "
			// + "and (extract(week from j.created_on) - "
			// + " extract(week from date_trunc('month', j.created_on)) + 1) "
			// + " in (1,2,3,4,5) "
			// + " group by j.created_by,u.user_id) AS result,user_acct ua "
			// + " where result.DMNAME = ua.user_id "
			// + " group by result.DMNAME,ua.user_id ");

			if (reportwiseDto.getYear() != null) {
				sqlSelect.append("  and EXTRACT(YEAR FROM j.created_on) in (" + reportwiseDto.getYear() + ")");
			}
			if (reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() > 0) {
				sqlSelect.append(" and to_char(j.created_on,'Mon') in (" + Utils.getListWithSingleQuote(reportwiseDto.getMonths()) + ")");
			}
			if (reportwiseDto.getDmName() != null && reportwiseDto.getDmName().length() > 0) {
				sqlSelect.append(" and (j.created_by='"+reportwiseDto.getDmName()+"' or j.created_by in "
					+ " (select u.user_id from user_acct u where u.assigned_bdm = '"+reportwiseDto.getDmName()+"'))");
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() == 1
					&& reportwiseDto.getMonths().get(0).equalsIgnoreCase(MonthEnum.JAN.getMonth())) {
				sqlSelect.append(" and EXTRACT(week from j.created_on) in (" + reportwiseDto.getWeek() + ")");
			} else if (reportwiseDto.getWeek() != null) {
				sqlSelect.append(" and (extract(week from j.created_on) - " + "extract(week from date_trunc('month', j.created_on)) + 1) in ("
						+ reportwiseDto.getWeek() + ")");
			}

			if (reportwiseDto.getStatus() != null && reportwiseDto.getStatus().length()>0) {
				if (reportwiseDto.getStatus().equalsIgnoreCase(Constants.OPEN)) {
					sqlSelect.append(" and j.status in ('OPEN','ASSIGNED','REOPEN') ");
				} else {
					sqlSelect.append(" and j.status in ('CLOSED','FILLED') ");
				}
			}
			sqlSelect.append("group by j.customer ");
			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if(resultList!=null && resultList.size()>0){
				list = new ArrayList<Map<String, Object>>();
			for (Object object : resultList) {
				Object obj[] = (Object[]) object;
				Map<String, Object> map = new HashMap<String, Object>();
				// [{ name: 'One', y: 10},{ name: 'Two', y: 10},{ name:
				// 'Three', y: 10},{ name: 'Four', y: 10}]
				map.put(Constants.NAME, obj[0]);
				map.put(Constants.Y, obj[1]);
				list.add(map);
			}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return list;
	}*/
	
	@Override
	public Object getAllClients(ReportwiseDto reportwiseDto) {
		List<Map<String, Object>> list=null;
		try {
			StringBuffer sqlSelect = new StringBuffer("select result.client,result.cnt from"
					+ " (select j.customer AS client,count(*) AS cnt  from job_order j "
					+ " where j.delete_flg= 0 ");
//					+ " and  EXTRACT(YEAR FROM j.created_on) in (2016)   ");

			// + "EXTRACT(YEAR FROM j.created_on) in (2016) "
			// + " and to_char(j.created_on,'Mon') in ('Jan','Feb','May') "
			// + "and (extract(week from j.created_on) - "
			// + " extract(week from date_trunc('month', j.created_on)) + 1) "
			// + " in (1,2,3,4,5) "
			// + " group by j.created_by,u.user_id) AS result,user_acct ua "
			// + " where result.DMNAME = ua.user_id "
			// + " group by result.DMNAME,ua.user_id ");

			if (reportwiseDto.getYear() != null) {
				sqlSelect.append("  and EXTRACT(YEAR FROM j.created_on) in (" + reportwiseDto.getYear() + ")");
			}
			if (reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() > 0) {
				sqlSelect.append(" and to_char(j.created_on,'Mon') in (" + Utils.getListWithSingleQuote(reportwiseDto.getMonths()) + ")");
			}
			if (reportwiseDto.getNames() != null && reportwiseDto.getNames().size() > 0) {
				sqlSelect.append(" and j.customer in (" + Utils.getListWithSingleQuote(reportwiseDto.getNames()) + ")");
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() == 1
					&& reportwiseDto.getMonths().get(0).equalsIgnoreCase(MonthEnum.JAN.getMonth())) {
				sqlSelect.append(" and EXTRACT(week from j.created_on) in (" + reportwiseDto.getWeek() + ")");
			} else if (reportwiseDto.getWeek() != null) {
				sqlSelect.append(" and (extract(week from j.created_on) - " + "extract(week from date_trunc('month', j.created_on)) + 1) in ("
						+ reportwiseDto.getWeek() + ")");
			}

			if (reportwiseDto.getStatus() != null && reportwiseDto.getStatus().length()>0) {
				if (reportwiseDto.getStatus().equalsIgnoreCase(Constants.OPEN)) {
					sqlSelect.append(" and j.status in ('OPEN','ASSIGNED','REOPEN') ");
				} else {
					sqlSelect.append(" and j.status in ('CLOSED','FILLED') ");
				}
			}
			sqlSelect.append("group by j.customer) AS result order by result.cnt Desc ");
			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if(resultList!=null && resultList.size()>0){
				list = new ArrayList<Map<String, Object>>();
			for (Object object : resultList) {
				Object obj[] = (Object[]) object;
				Map<String, Object> map = new HashMap<String, Object>();
				// [{ name: 'One', y: 10},{ name: 'Two', y: 10},{ name:
				// 'Three', y: 10},{ name: 'Four', y: 10}]
				map.put(Constants.NAME,((String)obj[0]).trim().split(" ")[0]);
				map.put(Constants.Y, obj[1]);
				map.put(Constants.FULL_NAME, obj[0] );
				list.add(map);
			}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public Object getAllTitlesByClient(ReportwiseDto reportwiseDto) {
		List<Map<String, Object>> list=null;
		try {
			StringBuffer sqlSelect = new StringBuffer("select j.title,count(*) from job_order j where j.delete_flg= 0 ");

			if (reportwiseDto.getYear() != null) {
				sqlSelect.append("  and EXTRACT(YEAR FROM j.created_on) in (" + reportwiseDto.getYear() + ")");
			}
			if (reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() > 0) {
				sqlSelect.append(" and to_char(j.created_on,'Mon') in (" + Utils.getListWithSingleQuote(reportwiseDto.getMonths()) + ")");
			}
			if (reportwiseDto.getName() != null && reportwiseDto.getName().length() > 0) {
				sqlSelect.append(" and j.customer='"+reportwiseDto.getName()+"'");
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() == 1
					&& reportwiseDto.getMonths().get(0).equalsIgnoreCase(MonthEnum.JAN.getMonth())) {
				sqlSelect.append(" and EXTRACT(week from j.created_on) in (" + reportwiseDto.getWeek() + ")");
			} else if (reportwiseDto.getWeek() != null) {
				sqlSelect.append(" and (extract(week from j.created_on) - " + "extract(week from date_trunc('month', j.created_on)) + 1) in ("
						+ reportwiseDto.getWeek() + ")");
			}

			if (reportwiseDto.getStatus() != null && reportwiseDto.getStatus().length()>0) {
				if (reportwiseDto.getStatus().equalsIgnoreCase(Constants.OPEN)) {
					sqlSelect.append(" and j.status in ('OPEN','ASSIGNED','REOPEN') ");
				} else {
					sqlSelect.append(" and j.status in ('CLOSED','FILLED') ");
				}
			}
			sqlSelect.append(" group by j.title ");
			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if(resultList!=null && resultList.size()>0){
				list = new ArrayList<Map<String, Object>>();
			for (Object object : resultList) {
				Object obj[] = (Object[]) object;
				Map<String, Object> map = new HashMap<String, Object>();
				// [{ name: 'One', y: 10},{ name: 'Two', y: 10},{ name:
				// 'Three', y: 10},{ name: 'Four', y: 10}]
				map.put(Constants.NAME, obj[0]);
				map.put(Constants.Y, obj[1]);
				list.add(map);
			}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public Object getAllJobOrdersByTitle_Client(ReportwiseDto reportwiseDto) {
		List<JobOrderDto> jobOrderDtoList = new ArrayList<JobOrderDto>();
		try {
			StringBuffer sqlSelect = new StringBuffer("select j.order_id AS orderID,j.title as title,j.customer AS customer,"
					+ " j.status AS status,j.created_by AS createdBy,j.created_on AS createdOn,j.assigned_to AS assignedTo");
			if (reportwiseDto.getStatus().equalsIgnoreCase(Constants.CLOSED)) {
				sqlSelect.append(" ,round((EXTRACT(EPOCH FROM j.updated_on-j.created_on)/3600)/24)");
			}else{
				sqlSelect.append(" ,round((EXTRACT(EPOCH FROM CURRENT_DATE-j.created_on)/3600)/24)");
			}
			sqlSelect.append(" ,num_pos AS noOfPos");
					sqlSelect.append("  from job_order j,user_acct ua "
					+ " where j.delete_flg=0 "
//					+ " and (j.created_by='Sachin' or j.created_by in "
//					+ " (select u.user_id from user_acct u where u.assigned_bdm = 'Sachin')) "
					+ " and  j.created_by=ua.user_id ");
//					+ " and  EXTRACT(YEAR FROM j.created_on) in (2016)   ");

			// + "EXTRACT(YEAR FROM j.created_on) in (2016) "
			// + " and to_char(j.created_on,'Mon') in ('Jan','Feb','May') "
			// + "and (extract(week from j.created_on) - "
			// + " extract(week from date_trunc('month', j.created_on)) + 1) "
			// + " in (1,2,3,4,5) "
			// + " group by j.created_by,u.user_id) AS result,user_acct ua "
			// + " where result.DMNAME = ua.user_id "
			// + " group by result.DMNAME,ua.user_id ");

			if (reportwiseDto.getYear() != null) {
				sqlSelect.append("  and EXTRACT(YEAR FROM j.created_on) in (" + reportwiseDto.getYear() + ")");
			}
			if (reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() > 0) {
				sqlSelect.append(" and to_char(j.created_on,'Mon') in (" + Utils.getListWithSingleQuote(reportwiseDto.getMonths()) + ")");
			}
			if (reportwiseDto.getName() != null && reportwiseDto.getName().length() > 0) {
				sqlSelect.append(" and j.customer='"+reportwiseDto.getName()+"'");
			}
			if (reportwiseDto.getTitle() != null && reportwiseDto.getTitle().length() > 0) {
				sqlSelect.append(" and j.title='"+reportwiseDto.getTitle()+"'");
			}
			if (reportwiseDto.getWeek() != null && reportwiseDto.getMonths() != null && reportwiseDto.getMonths().size() == 1
					&& reportwiseDto.getMonths().get(0).equalsIgnoreCase(MonthEnum.JAN.getMonth())) {
				sqlSelect.append(" and EXTRACT(week from j.created_on) in (" + reportwiseDto.getWeek() + ")");
			} else if (reportwiseDto.getWeek() != null) {
				sqlSelect.append(" and (extract(week from j.created_on) - " + "extract(week from date_trunc('month', j.created_on)) + 1) in ("
						+ reportwiseDto.getWeek() + ")");
			}

			if (reportwiseDto.getStatus() != null && reportwiseDto.getStatus().length()>0) {
				if (reportwiseDto.getStatus().equalsIgnoreCase(Constants.OPEN)) {
					sqlSelect.append(" and j.status in ('OPEN','ASSIGNED','REOPEN') ");
				} else {
					sqlSelect.append(" and j.status in ('CLOSED','FILLED') ");
				}
			}

			Map<String, Object> params = new HashMap<String, Object>();

			log.info("Query :::: <<<<<<< " + sqlSelect.toString() + " >>>>>>>>>>>>>");
			log.info("Params :::: <<<<<<< " + params + " >>>>>>>>>>>>>");
			List<?> resultList = submittalDao.findBySQLQuery(sqlSelect.toString(), null);
			if (resultList != null && resultList.size() > 0) {
				
				for(Object object:resultList){
					Object obj[]=(Object[])object;
					JobOrderDto dto = new JobOrderDto();
					dto.setJobOrderId(Utils.getIntegerValueOfBigDecimalObj(obj[0]));
					dto.setTitle(Utils.getStringValueOfObj(obj[1]));
					dto.setClient(Utils.getStringValueOfObj(obj[2]));
					dto.setStatus(Utils.getStringValueOfObj(obj[3]));
					dto.setCreatedBy(Utils.getStringValueOfObj(obj[4]));
					dto.setStrUpdatedOn(Utils.convertDateToString((Date)obj[5]));
//					Date jobOrderCreatedOn = Utils.convertAngularStrToDate(Utils.getStringValueOfObj(obj[5]));
//					if (reportwiseDto.getStatus().equalsIgnoreCase(Constants.CLOSED)) {
//						dto.setActiveDays(String.valueOf(Utils.getIntegerValueOfDoubleObj(obj[7])));
//					}else{
						dto.setActiveDays(String.valueOf(Utils.getIntegerValueOfDoubleObj(obj[7])));
//					}
					dto.setAssignedTo(Utils.getStringValueOfObj(obj[6]));
					dto.setNoOfPositions(Utils.getIntegerValueOfBigDecimalObj(obj[8]));
					jobOrderDtoList.add(dto);
				}
				
				
				
				List<Integer> jobOrderIds = new ArrayList<Integer>();
				for (JobOrderDto dto : jobOrderDtoList) {
					jobOrderIds.add(dto.getJobOrderId());
				}
				params = new HashMap<String, Object>();

				String hqlQuery = "select s.id,s.jobOrder.id,s.status from Submittal s where s.deleteFlag=0 and s.jobOrder.id in ?1";
				params.put("1", jobOrderIds);
				List<?> jobSubmittalList = orderDao.findByQuery(hqlQuery, -1, -1, params);
				if (jobSubmittalList != null && jobSubmittalList.size() > 0) {
					Map<Integer, Integer> jobOrderIdWithSubmittalCountMap = new HashMap<Integer, Integer>();
					Map<Integer, Integer> jobOrderIdWithSubmittalStartsCountMap = new HashMap<Integer, Integer>();
					Iterator<?> it = jobSubmittalList.iterator();
					while (it.hasNext()) {
						Object jobSubmittalObj[] = (Object[]) it.next();
						Integer orderId = Integer.parseInt(jobSubmittalObj[1].toString());
						SubmittalStatus status = (SubmittalStatus) jobSubmittalObj[2];
						if (jobOrderIdWithSubmittalCountMap.get(orderId) != null) {
							jobOrderIdWithSubmittalCountMap.put(orderId, jobOrderIdWithSubmittalCountMap.get(orderId) + 1);
						} else {
							jobOrderIdWithSubmittalCountMap.put(orderId, 1);
						}
						if (status.equals(SubmittalStatus.STARTED)) {
							if (jobOrderIdWithSubmittalStartsCountMap.get(orderId) != null) {
								jobOrderIdWithSubmittalStartsCountMap.put(orderId, jobOrderIdWithSubmittalStartsCountMap.get(orderId) + 1);
							} else {
								jobOrderIdWithSubmittalStartsCountMap.put(orderId, 1);
							}
						}
					}
					for (JobOrderDto dto : jobOrderDtoList) {
						if (jobOrderIdWithSubmittalCountMap.get(dto.getJobOrderId()) != null) {
							dto.setSbm(String.valueOf(jobOrderIdWithSubmittalCountMap.get(dto.getJobOrderId())));
						} else {
							dto.setSbm("0");
						}
						if(jobOrderIdWithSubmittalStartsCountMap.get(dto.getJobOrderId())!=null){
							dto.setOpenJobOrders(dto.getNoOfPositions()-jobOrderIdWithSubmittalStartsCountMap.get(dto.getJobOrderId()));
						}else{
							dto.setOpenJobOrders(dto.getNoOfPositions());
						}
					}
				} else {
					for (JobOrderDto dto : jobOrderDtoList) {
						dto.setSbm("0");
						dto.setOpenJobOrders(dto.getNoOfPositions());
					}
				}
				
				
				

				// log.info("Size of the Records ::: " + resultList.size());
				// list = new ArrayList<SubmittalStatsDto>();
				// getSubmittalData(resultList, list);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return jobOrderDtoList;
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
		sql.append("select count(j) from job_order j where j.customer='" + oldClientName + "'");
		List<?> list = clientNamesDao.findBySQLQuery(sql.toString(), null);
		if (list != null && list.size() > 0) {
			sql = new StringBuffer();
			sql.append("update job_order  SET customer = '" + newClientName + "' where customer='" + oldClientName + "'");
			clientNamesDao.updateSQLQuery(sql.toString());
			clientnames.setUpdatedBy(updatedBy);
		}
		
	}

}
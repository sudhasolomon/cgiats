package com.uralian.cgiats.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import com.uralian.cgiats.dto.AddEditJobOrderDto;
import com.uralian.cgiats.dto.DashboardSearchDto;
import com.uralian.cgiats.dto.JobOrderDto;
import com.uralian.cgiats.dto.ReportwiseDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.dto.SubmittalEventDto;
import com.uralian.cgiats.dto.SubmittalStatsDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.ExecutiveResumeView;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.JobOrderSearchDto;
import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.JobViewOrder;
import com.uralian.cgiats.model.MonthlySalesQuotas;
import com.uralian.cgiats.model.SalesQuotaView;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.rest.UserRoleVo;

/**
 * @author Christian Rebollar
 */
public interface JobOrderService {
	/*
	 * JOB ORDERs
	 */

	/**
	 * Retrieves the job opening by id.
	 * 
	 * @param orderId
	 * @param fetchFields
	 * @param fetchSubmittals
	 * @return
	 */
	public JobOrder getJobOrder(int orderId, boolean fetchFields, boolean fetchSubmittals);

	/**
	 * @param jobOrder
	 * @throws ServiceException
	 */
	public void saveJobOrder(AddEditJobOrderDto addEditJobOrderDto, List<MultipartFile> files) throws ServiceException;

	/**
	 * @param jobOrder
	 * @throws ServiceException
	 */
	public void updateJobOrder(JobOrder jobOrder) throws ServiceException;

	public List<?> getJobOrderReport(ReportwiseDto reportwiseDto);
	
	public Object getAllDMsOpenAndClosedJobOrders(ReportwiseDto reportwiseDto);
	
	public Object getOpenOrClosedOrdersByDM(ReportwiseDto reportwiseDto);
	
	public Object getAllClients(ReportwiseDto reportwiseDto);
	
//	public Object getAllClientsForComboBox(ReportwiseDto reportwiseDto);
	
	public Object getAllTitlesByClient(ReportwiseDto reportwiseDto);
	
	public Object getAllJobOrdersByTitle_Client(ReportwiseDto reportwiseDto);
	
	
	public Object getDMWise_Average_JobOrderServiceReport(ReportwiseDto reportwiseDto);
	/**
	 * @param criteria
	 * @param login
	 * @param pageSize
	 * @param first
	 * @return
	 */
	public List findJobOrders(JobOrderSearchDto criteria, UserDto login, int first, int pageSize);

	/**
	 * @param criteria
	 * @return
	 */
	public List<AddEditJobOrderDto> findJobOrdersOnline(JobOrderSearchDto criteria, String portalName);

	/**
	 * @param orderId
	 * @throws ServiceException
	 */
	public void deleteJobOrder(int orderId) throws ServiceException;

	public String getOpenJobOrdersDescription(Integer jobOrderId);

	/**
	 * @return
	 */
	public List<String> listExistingTitles();

	/**
	 * @return
	 */
	public List<String> listExistingCustomers();

	/**
	 * @return
	 */
	public List<String> listExistingCities();

	/**
	 * @return
	 */
	public List<String> listExistingStates();

	/*
	 * SUBMITTALs
	 */

	/**
	 * @param submittalId
	 * @return
	 */
	public Submittal getSubmittal(int submittalId);

	/**
	 * @param submittal
	 * @throws ServiceException
	 */
	public void saveOrUpdateSubmittal(SubmittalDto submittal) throws ServiceException;

	/**
	 * @param submittal
	 * @throws ServiceException
	 */
	// public void updateSubmittal(Submittal submittal) throws ServiceException;

	/**
	 * @param submittalId
	 * @throws ServiceException
	 */
	public void deleteSubmittal(int submittalId, String reason) throws ServiceException;

	/* STATS */
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String, Map<JobOrderStatus, Integer>> getStatsByUser(User user, Date dateStart, Date dateEnd, boolean flag);

	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByUser(User user, Date dateStart, Date dateEnd);

	/**
	 * @param orderId
	 * @param candidateId
	 * @return
	 */
	public List<Submittal> getCandidate(Integer orderId, Integer candidateId);

	/**
	 * @param candidateId
	 * @throws ServiceException
	 */

	public List<Submittal> getSubmittalDetails(int candidateId) throws ServiceException;

	/**
	 * @param criteria
	 * @param userObj
	 * 
	 */
	public List<JobOrderDto> findDeletedJobOrders(JobOrderSearchDto criteria, UserDto userObj);

	/**
	 * @param orderId
	 * 
	 */
	public List<Submittal> getOrderDeletedSubmittals(int orderId);

	/**
	 * @param candidateId
	 * 
	 */
	public List<Submittal> getCandidateSubDetails(Integer candidateId);

	/**
	 * @return
	 */
	public List<JobOrder> findEmptySbmJobOrders();

	public AddEditJobOrderDto getJobOrderAttachmentInfo(Integer jobOrderId);

	/**
	 * @return
	 */
	public List<JobOrder> findOpenJobOrders();
	
	
	public List<JobOrder> getPossibleJobOrdersToHot();
	
	public List<?> getJobOrdersByWithoutSubmittals();
	
	

	/**
	 * @param userObj
	 * @return
	 */
	public Map<String, Map<SubmittalStatus, Integer>> getDmSubmittalStatusByUser(String userRole, Date dateStart, Date dateEnd, User userObj);

	public List<MonthlySalesQuotas> getRecSalesQuotaList(String userId, String month, String year);

	public List<SalesQuotaView> getDmSubmittalsByUserQuota(String month, String year, String userId);

	/**
	 * @return
	 */
	public Map<String, Map<SubmittalStatus, Integer>> getDmSubmittalStatusByRecruiter(Date dateStart, Date dateEnd);

	public Map<String, Map<SubmittalStatus, Integer>> getAdmMonthlyQuota(String role, Date submittalFrom, Date submittalTo, String userId);

	public Map<JobOrderStatus, Integer> getAllJobOrdersCounts(Date dateStart, Date dateEnd);

	public List<JobOrderDto> findEMJobOrders(JobOrderSearchDto criteria, UserDto loginUser);

	public Map<String, Map<String, Integer>> findJobOrdersReportCount(Set<String> dmNameList,Map<String, UserDto> userIdWithUserMap,Date reportStartDate, Date reportEndDate);

	public Map<String, Map<String, Object>> findSubmittalCounts(Date startDate, Date endDate);

	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByLocation(UserDto userDto, List<String> officeLocs,List<String> dms, Date dateStart, Date dateEnd,String userStatus);
	
	public Map<String, Object> getDMsSubmittalStatsReport(Date dateStart, Date dateEnd,String loginUserId);
	
	
	public List<SubmittalStatsDto> getUserSubmittalDetailsById(DashboardSearchDto dashboardSearchDto);
	
	public List<SubmittalStatsDto> getDMSubmittalDetailsById(DashboardSearchDto dashboardSearchDto);

	public List<JobOrder> findJobOrdersOnMobile(Date date);

	// Mobile Application

	public Map<JobOrderStatus, Integer> getTodayJobOrdersCountsForExecutives(Date dateStart, Date dateEnd);

	public JobViewOrder getTodayJobOrderStatsForExecutives(UserRoleVo userRoleVo);

	public List<JobViewOrder> getAllJobOrderStatsForExecutives(UserRoleVo userRoleVo);

	public List<JobViewOrder> getYearWiseStatusForExecutives(String year, UserRoleVo userRoleVo);

	public List<ExecutiveResumeView> getTodayResumesStats();

	public List<ExecutiveResumeView> getYearWiseResumesStats();

	public ExecutiveResumeView getAllResumesCounts();

	public int findJobOrdersCount(JobOrderSearchDto criteria, User login);

	/**
	 * @param jobOrderId
	 * @return
	 */
	public List<SubmittalDto> findSubmittalsDetails(Integer jobOrderId);

	public List<SubmittalEventDto> getSubmittalEventHistoryBySubmittalId(Integer submittalId);
	
	/**
	 * @param jobOrderId
	 * @return
	 */
	public JobOrder getJobOrder(Integer jobOrderId);

	public JobOrder getJobOrderById(Integer jobOrderId);
	
	public List<JobOrder> getJobOrderByIds(List<Integer> jobOrderIds);

	public JobOrder reopenJobOrder(Integer jobOrderId, String updatedBy);

	/**
	 * @param jobOrder
	 * @return
	 */
	public JobOrderDto saveOrder(JobOrder jobOrder);

	public List<?> getCandidateSubmitalDetails(Integer candidateId);

	public void sendHotJobMail(JobOrder jobOrder, String officeLocation);

	public void addClient(String clientName, String updatedBy);

	public void updateClient(String oldClientName, String newClientName, String updatedBy);

}

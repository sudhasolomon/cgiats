package com.uralian.cgiats.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.ExecutiveResumeView;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.JobViewOrder;
import com.uralian.cgiats.model.MonthlySalesQuotas;
import com.uralian.cgiats.model.SalesQuotaView;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.rest.UserRoleVo;


/**
 * @author Christian Rebollar
 */
public interface JobOrderDao extends GenericDao<JobOrder, Integer>
{
	/**
	 * @return
	 */
	public List<String> findAllTitles();

	/**
	 * @return
	 */
	public List<String> findAllCustomers();

	/**
	 * @return
	 */
	public List<String> findAllCities();

	/**
	 * @return
	 */
	public List<String> findAllStates();

	public JobOrder findById(Integer orderId);
	
	public List<JobOrder> findByIds(List<Integer> orderIds);
	
	
	public String getJobOrderDescription(Integer jobOrderId);
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String, Map<JobOrderStatus, Integer>> getStatsByUser(
			User user,Date dateStart, Date dateEnd,boolean flag);

	/**
	 * @param user
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByUser(
			User user,Date dateStart, Date dateEnd);
	
	
	public Map<String, Map<SubmittalStatus, Integer>> getSubmittalStatusByLocation(UserDto userDto,List<String> officeLocs, List<String> dms,Date dateStart, Date dateEnd,String userStatus);

	/**
	 * @param userRole
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String, Map<SubmittalStatus, Integer>> getDmSubmittalStatusByUser(String userRole,
			Date dateStart, Date dateEnd,User user );

	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public Map<String, Map<SubmittalStatus, Integer>> getDmSubmittalStatusByRecruiter(Date dateStart, Date dateEnd);
	
	/**
	 * for fetching ADM Monthly Quotas
	 * @param role
	 * @param submittalFrom
	 * @param submittalTo
	 * @param userId
	 * @return
	 */
	public Map<String, Map<SubmittalStatus, Integer>> getAdmMonthlyQuota(
			String role, Date submittalFrom, Date submittalTo, String userId);
	
	public List<SalesQuotaView> getDmSubmittalsByUserQuota(String month,String year,String userId);
	
	public List<MonthlySalesQuotas> getRecSalesQuotaList(String userId, String month, String year);
	
	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	
	// Mobile Application 
	
	public Map<JobOrderStatus, Integer> getAllJobOrdersCounts(Date dateStart, Date dateEnd);
	
	public Map<JobOrderStatus, Integer> getTodayJobOrdersCountsForExecutives(Date dateStart, Date dateEnd);
	
	public JobViewOrder getTodayJobOrderStatsForExecutives(UserRoleVo userRoleVo);
	
	public List<JobViewOrder> getAllJobOrderStatsForExecutives(UserRoleVo userRoleVo);
	
	public List<JobViewOrder> getYearWiseStatusForExecutives(String year, UserRoleVo userRoleVo);
	
	public List<ExecutiveResumeView> getTodayResumesStats();
	
	public List<ExecutiveResumeView> getYearWiseResumesStats();
	
	public ExecutiveResumeView getAllResumesCounts();

	public int findJobOrdersCount(Map<String,Object> params, String sqlQuery);

	/**
	 * @param params
	 * @param string
	 * @return
	 */
	public List findSubmittalsDetails(Map<String, Object> params, String string);

	/**
	 * @param jobOrder
	 * @return
	 */
	public JobOrder saveOrder(JobOrder jobOrder);

	
	
}
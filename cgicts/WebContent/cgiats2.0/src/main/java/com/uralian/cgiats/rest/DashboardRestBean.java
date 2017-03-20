package com.uralian.cgiats.rest;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.service.JobOrderService;
import com.uralian.cgiats.web.AtsDashboardBean;
import com.uralian.cgiats.web.UtilityBean;

@Path("/dashboard")
public class DashboardRestBean extends SpringBeanAutowiringSupport {

	private static final Logger log = Logger.getLogger(DashboardRestBean.class);
	private Integer pastYear;
	private Integer presentYear;

	@Autowired
	@ManagedProperty(value = "#{atsDashboardBean}")
	private AtsDashboardBean dashboardBean;

	@Autowired
	@ManagedProperty(value = "#{utilityBean}")
	private UtilityBean utilityBean;

	@Autowired
	private JobOrderService jobOrderService;

	@PostConstruct
	public void init() {
		Calendar calender = Calendar.getInstance();
		presentYear = calender.get(Calendar.YEAR);
		pastYear = calender.get(Calendar.YEAR) - 1;

	}

	// Job Orders
	private Map<JobOrderStatus, Integer> todayJobOrdersMap;
	private Map<JobOrderStatus, Integer> presentJobOrdersMap;
	private Map<JobOrderStatus, Integer> pastJobOrdersMap;

	private List<JobOrderStatus> jobOrderStatus;

	// Submittals
	private Map<SubmittalStatus, Integer> todaySubmittalsMap;
	private Map<SubmittalStatus, Integer> presentSubmittalsMap;
	private Map<SubmittalStatus, Integer> pastSubmittalsMap;
	private List<SubmittalStatus> todaySubmittalStatus;
	private List<SubmittalStatus> submittalStatus;

	// Resumes
	private Map<String, Integer> todayResumesMap;
	private Map<String, Integer> presentResumesMap;
	private Map<String, Integer> pastResumesMap;
	private Integer todayResumesCount;
	private Long totalResumesCount;

	@GET
	@Path("/job_orders")
	@Produces(MediaType.APPLICATION_JSON)
	public String getJobOrdersCount() throws JSONException {
		log.info("from DashboardRestBean getJobOrdersCount() Start....");

		jobOrderStatus = utilityBean.getDashboardJobOrdersList();

		todayJobOrdersMap = dashboardBean.fetchTodayJoStats();
		presentJobOrdersMap = dashboardBean.fetchJoStatsPresent();
		pastJobOrdersMap = dashboardBean.fetchJoStatsHistory();

		for (JobOrderStatus status : jobOrderStatus) {
			if (!todayJobOrdersMap.containsKey(status))
				todayJobOrdersMap.put(status, 0);
			if (!presentJobOrdersMap.containsKey(status))
				presentJobOrdersMap.put(status, 0);
			if (!pastJobOrdersMap.containsKey(status))
				pastJobOrdersMap.put(status, 0);
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("today", todayJobOrdersMap);
		jsonObject.put("present", presentJobOrdersMap);
		jsonObject.put("past", pastJobOrdersMap);
		jsonObject.put("presentYear", presentYear);
		jsonObject.put("pastYear", pastYear);
		log.info("from DashboardRestBean getJobOrdersCount() End....");
		return jsonObject.toString();
	}

	@GET
	@Path("/submittals")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSubmittalsCount() throws JSONException {
		log.info("from DashboardRestBean getSubmittalsCount() Start....");
		todaySubmittalStatus = utilityBean.getSubmittalStatusList();
		submittalStatus = utilityBean.getDashboardSubmittalStatusList();

		todaySubmittalsMap = dashboardBean.getSubmittalsMap();
		presentSubmittalsMap = dashboardBean.getSubmittalsPresentMap();
		pastSubmittalsMap = dashboardBean.getSubmittalsHistoryMap();

		for (SubmittalStatus status : todaySubmittalStatus) {
			if (!todaySubmittalsMap.containsKey(status))
				todaySubmittalsMap.put(status, 0);
		}
		for (SubmittalStatus status : submittalStatus) {
			if (!presentSubmittalsMap.containsKey(status))
				presentSubmittalsMap.put(status, 0);
			if (!pastSubmittalsMap.containsKey(status))
				pastSubmittalsMap.put(status, 0);
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("today", todaySubmittalsMap);
		jsonObject.put("present", presentSubmittalsMap);
		jsonObject.put("past", pastSubmittalsMap);
		jsonObject.put("presentYear", presentYear);
		jsonObject.put("pastYear", pastYear);
		log.info("from DashboardRestBean getSubmittalsCount() End....");
		return jsonObject.toString();
	}

	@GET
	@Path("/resumes")
	@Produces(MediaType.APPLICATION_JSON)
	public String getResumesCount() throws JSONException {
		log.info("from DashboardRestBean getResumesCount() Start....");
		List<String> resumeAuthors = dashboardBean.getResumeAuthors();
		todayResumesMap = dashboardBean.getResumesMap();
		presentResumesMap = dashboardBean.getResumesPresentMap();
		pastResumesMap = dashboardBean.getResumesHistoryMap();
		for (String authors : resumeAuthors) {
			if (!todayResumesMap.containsKey(authors))
				todayResumesMap.put(authors, 0);
			if (!presentResumesMap.containsKey(authors))
				presentResumesMap.put(authors, 0);
			if (!pastResumesMap.containsKey(authors))
				pastResumesMap.put(authors, 0);
		}

		dashboardBean.fetchTotalResumesCounts();
		todayResumesCount = dashboardBean.getTotalResumesToday();
		totalResumesCount = dashboardBean.getTotalResumesCount();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("today", todayResumesMap);
		jsonObject.put("present", presentResumesMap);
		jsonObject.put("past", pastResumesMap);
		jsonObject.put("todayResumesCount", todayResumesCount);
		jsonObject.put("totalResumesCount", totalResumesCount);
		jsonObject.put("presentYear", presentYear);
		jsonObject.put("pastYear", pastYear);
		log.info("from DashboardRestBean getResumesCount() End....");
		return jsonObject.toString();
	}

	public AtsDashboardBean getDashboardBean() {
		return dashboardBean;
	}

	public void setDashboardBean(AtsDashboardBean dashboardBean) {
		this.dashboardBean = dashboardBean;
	}

	public UtilityBean getUtilityBean() {
		return utilityBean;
	}

	public void setUtilityBean(UtilityBean utilityBean) {
		this.utilityBean = utilityBean;
	}

	public Map<JobOrderStatus, Integer> getTodayJobOrdersMap() {
		return todayJobOrdersMap;
	}

	public void setTodayJobOrdersMap(
			Map<JobOrderStatus, Integer> todayJobOrdersMap) {
		this.todayJobOrdersMap = todayJobOrdersMap;
	}

	public List<JobOrderStatus> getJobOrderStatus() {
		return jobOrderStatus;
	}

	public void setJobOrderStatus(List<JobOrderStatus> jobOrderStatus) {
		this.jobOrderStatus = jobOrderStatus;
	}

	public JobOrderService getJobOrderService() {
		return jobOrderService;
	}

	public void setJobOrderService(JobOrderService jobOrderService) {
		this.jobOrderService = jobOrderService;
	}

	public Map<JobOrderStatus, Integer> getPresentJobOrdersMap() {
		return presentJobOrdersMap;
	}

	public void setPresentJobOrdersMap(
			Map<JobOrderStatus, Integer> presentJobOrdersMap) {
		this.presentJobOrdersMap = presentJobOrdersMap;
	}

	public Map<JobOrderStatus, Integer> getPastJobOrdersMap() {
		return pastJobOrdersMap;
	}

	public void setPastJobOrdersMap(
			Map<JobOrderStatus, Integer> pastJobOrdersMap) {
		this.pastJobOrdersMap = pastJobOrdersMap;
	}

	public Map<SubmittalStatus, Integer> getTodaySubmittalsMap() {
		return todaySubmittalsMap;
	}

	public void setTodaySubmittalsMap(
			Map<SubmittalStatus, Integer> todaySubmittalsMap) {
		this.todaySubmittalsMap = todaySubmittalsMap;
	}

	public Map<SubmittalStatus, Integer> getPresetnSubmittalsMap() {
		return presentSubmittalsMap;
	}

	public void setPresetnSubmittalsMap(
			Map<SubmittalStatus, Integer> presetnSubmittalsMap) {
		this.presentSubmittalsMap = presetnSubmittalsMap;
	}

	public Map<SubmittalStatus, Integer> getPastSubmittalsMap() {
		return pastSubmittalsMap;
	}

	public void setPastSubmittalsMap(
			Map<SubmittalStatus, Integer> pastSubmittalsMap) {
		this.pastSubmittalsMap = pastSubmittalsMap;
	}

	public Map<SubmittalStatus, Integer> getPresentSubmittalsMap() {
		return presentSubmittalsMap;
	}

	public void setPresentSubmittalsMap(
			Map<SubmittalStatus, Integer> presentSubmittalsMap) {
		this.presentSubmittalsMap = presentSubmittalsMap;
	}

	public Map<String, Integer> getTodayResumesMap() {
		return todayResumesMap;
	}

	public void setTodayResumesMap(Map<String, Integer> todayResumesMap) {
		this.todayResumesMap = todayResumesMap;
	}

	public Map<String, Integer> getPresentResumesMap() {
		return presentResumesMap;
	}

	public void setPresentResumesMap(Map<String, Integer> presentResumesMap) {
		this.presentResumesMap = presentResumesMap;
	}

	public Map<String, Integer> getPastResumesMap() {
		return pastResumesMap;
	}

	public void setPastResumesMap(Map<String, Integer> pastResumesMap) {
		this.pastResumesMap = pastResumesMap;
	}

	public Integer getTodayResumesCount() {
		return todayResumesCount;
	}

	public void setTodayResumesCount(Integer todayResumesCount) {
		this.todayResumesCount = todayResumesCount;
	}

	public Long getTotalResumesCount() {
		return totalResumesCount;
	}

	public void setTotalResumesCount(Long totalResumesCount) {
		this.totalResumesCount = totalResumesCount;
	}

	public List<SubmittalStatus> getTodaySubmittalStatus() {
		return todaySubmittalStatus;
	}

	public void setTodaySubmittalStatus(
			List<SubmittalStatus> todaySubmittalStatus) {
		this.todaySubmittalStatus = todaySubmittalStatus;
	}

	public List<SubmittalStatus> getSubmittalStatus() {
		return submittalStatus;
	}

	public void setSubmittalStatus(List<SubmittalStatus> submittalStatus) {
		this.submittalStatus = submittalStatus;
	}

	public Integer getPastYear() {
		return pastYear;
	}

	public void setPastYear(Integer pastYear) {
		this.pastYear = pastYear;
	}

	public Integer getPresentYear() {
		return presentYear;
	}

	public void setPresentYear(Integer presentYear) {
		this.presentYear = presentYear;
	}
}

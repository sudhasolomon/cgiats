/*
 * LoginBean.java May 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.web;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.JobOrderService;
import com.uralian.cgiats.service.SubmittalService;

@Component("atsDashboardBean")
@Scope("view")
public class AtsDashboardBean extends UIBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Autowired
	private SubmittalService submittalService;

	@Autowired
	private JobOrderService jobOrderService;

	@Autowired
	private CandidateService candidateService;

	private int totalSubmittals;

	private Date fromDate;
	private Date toDate;
	private Date previousFromDate;
	private Date previousToDate;
	private Date presentFromDate;
	private Date presentToDate;
	private List<String> resumeAuthors;

	private Map<SubmittalStatus, Integer> submittalsMap;
	private Map<SubmittalStatus, Integer> submittalsHistoryMap;
	private Map<SubmittalStatus, Integer> submittalsPresentMap;

	// Job Orders
	private Map<JobOrderStatus, Integer> jobOrdersMap;
	private Map<JobOrderStatus, Integer> jobOrdersHistoryMap;
	private Map<JobOrderStatus, Integer> jobOrdersPresentMap;

	// Resumes
	private Map<String, Integer> resumesMap;
	private Map<String, Integer> resumesHistoryMap;
	private Map<String, Integer> resumesPresentMap;
	ChartSeries resumes = null;
	ChartSeries jos = null;
	private CartesianChartModel resumeCategoryModel;
	private CartesianChartModel joCategoryModel;

	public AtsDashboardBean() {
		log.debug("Bean created: " + hashCode());
	}

	/**
	 */
	@PostConstruct
	public void init() {
		log.debug("Bean initialized: " + hashCode());
		resumeAuthors = new ArrayList<String>();
		resumeAuthors.add("Dice");
		resumeAuthors.add("Careerbuilder");
		resumeAuthors.add("Monster");
		resumeAuthors.add("Techfetch");
		resumeAuthors.add("Others");
		fetchTodaySubmittalStats();
		fetchSubmittalStatsHistory();
		fetchSubmittalStatsPresent();
		fetchJoStatsPresent();
		fetchJoStatsHistory();
		fetchTodayJoStats();
		fetchTodayResumesStats();
		fetchResumesStatsHistory();
		fetchResumesStatsPresent();
		fetchTotalResumesCounts();
	}

	// public void initView()
	// {
	// fetchTodaySubmittalStats();
	// }

	/*
	 * public void fromPreviousYearStart(){ Calendar originalDate =
	 * Calendar.getInstance(); originalDate.set(2013, 0, 1, 00, 00, 00); Date
	 * resumeFrom2 = originalDate.getTime();
	 * log.info("fromPreviousYearStart--"+resumeFrom2);
	 * setPreviousFromDate(resumeFrom2); }
	 * 
	 * 
	 * public void fromPreviousYearEnd(){ Calendar originalDate =
	 * Calendar.getInstance(); originalDate.set(2013, 11, 31, 23, 59, 59); Date
	 * resumeTo2=originalDate.getTime();
	 * log.info("fromPreviousYearEnd--"+resumeTo2);
	 * setPreviousToDate(resumeTo2); }
	 */

	public void fromPreviousYearStart() {
		Calendar originalDate = Calendar.getInstance();
		originalDate.set(2014, 0, 1, 00, 00, 00);
		Date resumeFrom2 = originalDate.getTime();
		log.info("fromPreviousYearStart--" + resumeFrom2);
		setPreviousFromDate(resumeFrom2);
	}

	public void fromPreviousYearEnd() {
		Calendar originalDate = Calendar.getInstance();
		originalDate.set(2014, 11, 31, 23, 59, 59);
		Date resumeTo2 = originalDate.getTime();
		log.info("fromPreviousYearEnd--" + resumeTo2);
		setPreviousToDate(resumeTo2);
	}

	public void fetchSubmittalStatsHistory() {
		fromPreviousYearStart();
		fromPreviousYearEnd();
		System.out.println("previousFromDate>>>" + previousFromDate);
		System.out.println("previousToDate>>>" + previousToDate);
		submittalsHistoryMap = submittalService.getAllSubmittalStatusCounts(
				previousFromDate, previousToDate);
		List<Integer> submittalsLst = new ArrayList<Integer>(
				submittalsHistoryMap.values());
		Iterator itr = submittalsLst.iterator();
		int values = 0;
		int totalSubmittals;
		totalSubmittals = 0;
		while (itr.hasNext()) {
			values = (Integer) itr.next();
			totalSubmittals += values;
			System.out.println("values" + values);
		}
		submittalsHistoryMap.put(SubmittalStatus.SUBMITTED, totalSubmittals);
		System.out.println("fetchSubmittalStatsHistory....."
				+ submittalsHistoryMap);
		System.out.println("totalSubmittals...." + totalSubmittals);
	}

	/*
	 * public void fromPresentYearStart(){ Calendar originalDate =
	 * Calendar.getInstance(); originalDate.set(2014, 0, 1, 00, 00, 00); Date
	 * resumeFrom2 = originalDate.getTime();
	 * log.info("fromPreviousYearStart--"+resumeFrom2);
	 * setPresentFromDate(resumeFrom2); }
	 * 
	 * 
	 * public void fromPresentYearEnd(){ Calendar originalDate =
	 * Calendar.getInstance(); originalDate.set(2014, 11, 31, 23, 59, 59); Date
	 * resumeTo2=originalDate.getTime();
	 * log.info("fromPreviousYearEnd--"+resumeTo2); setPresentToDate(resumeTo2);
	 * }
	 */

	public void fromPresentYearStart() {
		Calendar originalDate = Calendar.getInstance();
		originalDate.set(2015, 0, 1, 00, 00, 00);
		Date resumeFrom2 = originalDate.getTime();
		log.info("fromPreviousYearStart--" + resumeFrom2);
		setPresentFromDate(resumeFrom2);
	}

	public void fromPresentYearEnd() {
		Calendar originalDate = Calendar.getInstance();
		originalDate.set(2015, 11, 31, 23, 59, 59);
		Date resumeTo2 = originalDate.getTime();
		log.info("fromPreviousYearEnd--" + resumeTo2);
		setPresentToDate(resumeTo2);
	}

	public void fetchSubmittalStatsPresent() {
		subPieModel = new PieChartModel();
		fromPresentYearStart();
		fromPresentYearEnd();
		System.out.println("presentFromDate>>>" + presentFromDate);
		System.out.println("presentToDate>>>" + presentToDate);
		submittalsPresentMap = submittalService.getAllSubmittalStatusCounts(
				presentFromDate, presentToDate);
		List<Integer> submittalsLst = new ArrayList<Integer>(
				submittalsPresentMap.values());
		Iterator itr = submittalsLst.iterator();
		int values = 0;
		int totalSubmittals;
		totalSubmittals = 0;
		while (itr.hasNext()) {
			values = (Integer) itr.next();
			totalSubmittals += values;
			System.out.println("values" + values);
		}
		submittalsPresentMap.put(SubmittalStatus.SUBMITTED, totalSubmittals);
		for (SubmittalStatus a : submittalsPresentMap.keySet()) {
			createSubPieModel(a.name(), submittalsPresentMap.get(a));
		}

		System.out.println("fetchSubmittalStatsPresent....."
				+ submittalsPresentMap);
		System.out.println("totalSubmittals...." + totalSubmittals);
	}

	public void fromdates() {
		Calendar originalDate = Calendar.getInstance();
		// originalDate.add(Calendar.DAY_OF_YEAR, 0);
		originalDate.set(Calendar.HOUR_OF_DAY, 0);
		originalDate.set(Calendar.MINUTE, 0);
		originalDate.set(Calendar.SECOND, 0);
		originalDate.set(Calendar.MILLISECOND, 0);
		// originalDate.set(originalDate.DATE,originalDate.getActualMinimum(originalDate.DATE));
		Date resumeFrom2 = originalDate.getTime();
		log.info("resumefrom--" + resumeFrom2);
		setFromDate(resumeFrom2);
	}

	public void todates() {
		Calendar originalDate = Calendar.getInstance();
		// originalDate.add(Calendar.DAY_OF_YEAR, 0);
		originalDate.set(Calendar.HOUR_OF_DAY, 23);
		originalDate.set(Calendar.MINUTE, 59);
		originalDate.set(Calendar.SECOND, 59);
		// originalDate.set(originalDate.DATE,
		// originalDate.getActualMaximum(originalDate.DATE));
		Date resumeTo2 = originalDate.getTime();
		log.info("resumeTo2--" + resumeTo2);
		setToDate(resumeTo2);
	}

	public void fetchTodaySubmittalStats() {
		fromdates();
		todates();
		System.out.println("fromDate>>>" + fromDate);
		System.out.println("toDate>>>" + toDate);
		submittalsMap = submittalService.getAllSubmittalStatusCounts(fromDate,
				toDate);
		List<Integer> submittalsLst = new ArrayList<Integer>(
				submittalsMap.values());
		Iterator itr = submittalsLst.iterator();
		int values = 0;
		int totalSubmittals;
		totalSubmittals = 0;
		while (itr.hasNext()) {
			values = (Integer) itr.next();
			totalSubmittals += values;
			System.out.println("values" + values);
		}
		submittalsMap.put(SubmittalStatus.SUBMITTED, totalSubmittals);
		System.out.println("fetchTodaySubmittalStats....." + submittalsMap);
		System.out.println("totalSubmittals...." + totalSubmittals);
	}

	public Map<JobOrderStatus, Integer> fetchTodayJoStats() {
		fromdates();
		todates();
		jobOrdersMap = jobOrderService.getAllJobOrdersCounts(fromDate, toDate);
		List<Integer> josLst = new ArrayList<Integer>(jobOrdersMap.values());
		Iterator itr = josLst.iterator();
		int values = 0;
		int totalJos = 0;
		while (itr.hasNext()) {
			values = (Integer) itr.next();
			totalJos += values;
		}
		System.out.println("jobOrdersMap...." + jobOrdersMap);
		System.out.println("fetchTodayJoStats...." + totalJos);
		return jobOrdersMap;
	}

	public Map<JobOrderStatus, Integer> fetchJoStatsHistory() {
		fromPreviousYearStart();
		fromPreviousYearEnd();
		jobOrdersHistoryMap = jobOrderService.getAllJobOrdersCounts(
				previousFromDate, previousToDate);
		List<Integer> josLst = new ArrayList<Integer>(
				jobOrdersHistoryMap.values());
		Iterator itr = josLst.iterator();
		int values = 0;
		int totalJos = 0;
		while (itr.hasNext()) {
			values = (Integer) itr.next();
			totalJos += values;
		}
		System.out.println("fetchJoStatsHistory...." + totalJos);
		return jobOrdersHistoryMap;
	}

	public Map<JobOrderStatus, Integer> fetchJoStatsPresent() {
		fromPresentYearStart();
		fromPresentYearEnd();
		joCategoryModel = new CartesianChartModel();
		jobOrdersPresentMap = jobOrderService.getAllJobOrdersCounts(
				presentFromDate, presentToDate);
		List<Integer> josLst = new ArrayList<Integer>(
				jobOrdersPresentMap.values());
		Iterator itr = josLst.iterator();
		int values = 0;
		int totalJos = 0;
		while (itr.hasNext()) {
			values = (Integer) itr.next();
			totalJos += values;
		}
		jos = new ChartSeries();
		jos.setLabel("Job Orders");
		for (JobOrderStatus a : jobOrdersPresentMap.keySet()) {
			jos.set(a.name(), jobOrdersPresentMap.get(a));
		}
		joCategoryModel.addSeries(jos);
		System.out.println("fetchJoStatsPresent...." + totalJos);
		return jobOrdersPresentMap;
	}

	private int totalResumesToday;

	public void fetchTodayResumesStats() {
		fromdates();
		todates();
		log.info("fromBean" + fromDate + "ToDate" + toDate);

		resumesMap = candidateService.getAllCandidatesCounts(fromDate, toDate);
		// resumeAuthors = new ArrayList<String>(resumesMap.keySet());
		List<Integer> josLst = new ArrayList<Integer>(resumesMap.values());
		log.info("Size" + josLst.size());
		Iterator itr = josLst.iterator();
		Integer values = 0;
		totalResumesToday = 0;
		while (itr.hasNext()) {
			values = (Integer) itr.next();
			if (values == null)
				values = 0;
			totalResumesToday += values;
		}
		System.out.println("jobOrdersMap...." + resumesMap);
		System.out.println("fetchTodayResumesStats...." + totalResumesToday);
	}

	/*
	 * public void fetchTotResumesStats(){ fromdates(); todates();
	 * resumesMap=candidateService.getAllCandidatesCounts(fromDate, toDate); //
	 * resumeAuthors = new ArrayList<String>(resumesMap.keySet()); List<Integer>
	 * josLst=new ArrayList<Integer>(resumesMap.values()); Iterator
	 * itr=josLst.iterator(); Integer values=0;totalResumesToday=0;
	 * while(itr.hasNext()){ values=(Integer)itr.next(); if(values==null)
	 * values=0; totalResumesToday+=values; }
	 * System.out.println("jobOrdersMap...."+resumesMap);
	 * System.out.println("fetchTodayResumesStats...."+totalResumesToday); }
	 */

	public void fetchResumesStatsHistory() {
		fromPreviousYearStart();
		fromPreviousYearEnd();
		resumesHistoryMap = candidateService.getAllCandidatesCounts(
				previousFromDate, previousToDate);
		// resumeAuthors = new ArrayList<String>(resumesHistoryMap.keySet());
		List<Integer> josLst = new ArrayList<Integer>(
				resumesHistoryMap.values());
		Iterator itr = josLst.iterator();
		Integer values = 0;
		int totalResumes = 0;
		while (itr.hasNext()) {
			values = (Integer) itr.next();
			if (values == null)
				values = 0;
			totalResumes += values;
		}
		System.out.println("resumesHistoryMap...." + resumesHistoryMap);
		System.out.println("fetchResumesStatsHistory...." + totalResumes);
	}

	public void fetchResumesStatsPresent() {
		resumeCategoryModel = new CartesianChartModel();
		resumes = new ChartSeries();
		resumes.setLabel("Resumes");
		fromPresentYearStart();
		fromPresentYearEnd();
		resumesPresentMap = candidateService.getAllCandidatesCounts(
				presentFromDate, presentToDate);
		// resumeAuthors = new ArrayList<String>(resumesPresentMap.keySet());
		List<Integer> josLst = new ArrayList<Integer>(
				resumesPresentMap.values());
		Iterator itr = josLst.iterator();
		Integer values = 0;
		int totalResumes = 0;
		resumes.setLabel("Resumes");
		for (String portal : resumesPresentMap.keySet()) {
			resumes.set(portal, resumesPresentMap.get(portal));

		}
		resumeCategoryModel.addSeries(resumes);
		// resumes=null;
		while (itr.hasNext()) {

			values = (Integer) itr.next();
			if (values == null)
				values = 0;
			totalResumes += values;
		}

		System.out.println("resumesPresentMap...." + resumesPresentMap);
		System.out.println("fetchResumesStatsPresent...." + totalResumes);
	}

	private long totalResumesCount;

	public void fetchTotalResumesCounts() {
		Calendar originalDate = Calendar.getInstance();
		originalDate.set(2005, 0, 1, 00, 00, 00);
		Date resumeFrom2 = originalDate.getTime();

		Calendar originalDate2 = Calendar.getInstance();
		originalDate2.set(2020, 0, 1, 00, 00, 00);
		Date resumeFrom22 = originalDate2.getTime();
		List<Integer> resumesPresentMap = null;
		resumesPresentMap = candidateService.getAllResumesCounts();
		Iterator itr = resumesPresentMap.iterator();
		long a = 0;
		while (itr.hasNext()) {
			a = (Long) itr.next();
		}
		totalResumesCount = a;
		/*
		 * List<Integer> josLst=new
		 * ArrayList<Integer>(resumesPresentMap.values()); Iterator
		 * itr=josLst.iterator(); Integer values=0;int totalResumes= 0;
		 * while(itr.hasNext()){
		 * 
		 * values=(Integer)itr.next(); if(values==null) values=0;
		 * totalResumes+=values; }
		 */

		System.out.println("a...." + a);
	}

	public void onRefresh() {
		try {
			getFacesContext().getExternalContext()
					.redirect("ats_dashboard.jsf");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	ChartSeries submittals = null;

	private PieChartModel subPieModel;

	public PieChartModel getSubPieModel() {
		return subPieModel;
	}

	public void setSubPieModel(PieChartModel subPieModel) {
		this.subPieModel = subPieModel;
	}

	private int submittalValue;

	public int getSubmittalValue() {
		return submittalValue;
	}

	public void setSubmittalValue(int submittalValue) {
		this.submittalValue = submittalValue;
	}

	private void createSubPieModel(String status, Integer key) {

		UtilityBean ub = new UtilityBean();
		List<SubmittalStatus> lst = ub.getDashboardSubmittalStatusList();
		Iterator<SubmittalStatus> ie = lst.iterator();
		submittalValue = key;
		subPieModel.set(status, key);
	}

	public Date getFromDate() {
		return fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Map<SubmittalStatus, Integer> getSubmittalsMap() {
		return submittalsMap;
	}

	public void setSubmittalsMap(Map<SubmittalStatus, Integer> submittalsMap) {
		this.submittalsMap = submittalsMap;
	}

	public int getTotalSubmittals() {
		return totalSubmittals;
	}

	public void setTotalSubmittals(int totalSubmittals) {
		this.totalSubmittals = totalSubmittals;
	}

	public Date getPreviousFromDate() {
		return previousFromDate;
	}

	public Date getPreviousToDate() {
		return previousToDate;
	}

	public void setPreviousFromDate(Date previousFromDate) {
		this.previousFromDate = previousFromDate;
	}

	public void setPreviousToDate(Date previousToDate) {
		this.previousToDate = previousToDate;
	}

	public Map<SubmittalStatus, Integer> getSubmittalsHistoryMap() {
		return submittalsHistoryMap;
	}

	public void setSubmittalsHistoryMap(
			Map<SubmittalStatus, Integer> submittalsHistoryMap) {
		this.submittalsHistoryMap = submittalsHistoryMap;
	}

	public Date getPresentFromDate() {
		return presentFromDate;
	}

	public Date getPresentToDate() {
		return presentToDate;
	}

	public void setPresentFromDate(Date presentFromDate) {
		this.presentFromDate = presentFromDate;
	}

	public void setPresentToDate(Date presentToDate) {
		this.presentToDate = presentToDate;
	}

	public Map<SubmittalStatus, Integer> getSubmittalsPresentMap() {
		return submittalsPresentMap;
	}

	public void setSubmittalsPresentMap(
			Map<SubmittalStatus, Integer> submittalsPresentMap) {
		this.submittalsPresentMap = submittalsPresentMap;
	}

	public Map<JobOrderStatus, Integer> getJobOrdersMap() {
		return jobOrdersMap;
	}

	public Map<JobOrderStatus, Integer> getJobOrdersHistoryMap() {
		return jobOrdersHistoryMap;
	}

	public Map<JobOrderStatus, Integer> getJobOrdersPresentMap() {
		return jobOrdersPresentMap;
	}

	public void setJobOrdersMap(Map<JobOrderStatus, Integer> jobOrdersMap) {
		this.jobOrdersMap = jobOrdersMap;
	}

	public void setJobOrdersHistoryMap(
			Map<JobOrderStatus, Integer> jobOrdersHistoryMap) {
		this.jobOrdersHistoryMap = jobOrdersHistoryMap;
	}

	public void setJobOrdersPresentMap(
			Map<JobOrderStatus, Integer> jobOrdersPresentMap) {
		this.jobOrdersPresentMap = jobOrdersPresentMap;
	}

	public Map<String, Integer> getResumesMap() {
		return resumesMap;
	}

	public Map<String, Integer> getResumesHistoryMap() {
		return resumesHistoryMap;
	}

	public Map<String, Integer> getResumesPresentMap() {
		return resumesPresentMap;
	}

	public void setResumesMap(Map<String, Integer> resumesMap) {
		this.resumesMap = resumesMap;
	}

	public void setResumesHistoryMap(Map<String, Integer> resumesHistoryMap) {
		this.resumesHistoryMap = resumesHistoryMap;
	}

	public void setResumesPresentMap(Map<String, Integer> resumesPresentMap) {
		this.resumesPresentMap = resumesPresentMap;
	}

	public List<String> getResumeAuthors() {
		return resumeAuthors;
	}

	public void setResumeAuthors(List<String> resumeAuthors) {
		this.resumeAuthors = resumeAuthors;
	}

	public int getTotalResumesToday() {
		return totalResumesToday;
	}

	public void setTotalResumesToday(int totalResumesToday) {
		this.totalResumesToday = totalResumesToday;
	}

	public CartesianChartModel getResumeCategoryModel() {
		return resumeCategoryModel;
	}

	public void setResumeCategoryModel(CartesianChartModel resumeCategoryModel) {
		this.resumeCategoryModel = resumeCategoryModel;
	}

	public CartesianChartModel getJoCategoryModel() {
		return joCategoryModel;
	}

	public void setJoCategoryModel(CartesianChartModel joCategoryModel) {
		this.joCategoryModel = joCategoryModel;
	}

	public ChartSeries getJos() {
		return jos;
	}

	public void setJos(ChartSeries jos) {
		this.jos = jos;
	}

	public long getTotalResumesCount() {
		return totalResumesCount;
	}

	public void setTotalResumesCount(long totalResumesCount) {
		this.totalResumesCount = totalResumesCount;
	}

}

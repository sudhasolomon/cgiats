package com.uralian.cgiats.rest;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.sun.faces.facelets.PrivateApiFaceletCacheAdapter;
import com.uralian.cgiats.model.AuditableEntity;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.MobileCgiCandidates;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.CareerAccountService;
import com.uralian.cgiats.service.JobOrderService;
import com.uralian.cgiats.service.MobileCgiCandidateService;

@Component
@Path("/restJoblist")
public class JoblistRestBean extends SpringBeanAutowiringSupport {

	private static final Logger log=Logger.getLogger(JoblistRestBean.class);
	@Autowired
	private JobOrderService jobOrderService;
	@Autowired
	public CareerAccountService careerAccount;
	@Autowired
	private MobileCgiCandidateService mobileCgiCandidateService;
	@Autowired
	CandidateService candidateService;
	Candidate candidate = null;
	private JobOrder order;
	private String postedDate;

	public String getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(String postedDate) {
		this.postedDate = postedDate;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String jobList(@Context HttpServletRequest request,
			@QueryParam("jbDate") String jbDate) throws JSONException, ParseException {
		JSONObject jsonObj = new JSONObject();
		List<Map<String, Object>> jsonJobList = new ArrayList<Map<String, Object>>();
		List<JobOrder> orderLst = null;
		SimpleDateFormat formatter =null;
		Date jobDate=null;
		if(jbDate!=null){
			formatter= new SimpleDateFormat("MM-dd-yyyy");
			jobDate=formatter.parse(jbDate);
		}
		orderLst = jobOrderService.findJobOrdersOnMobile(jobDate);
		

		for (JobOrder jobOrder : orderLst) {
			Map<String, Object> jobOrderDataMap = new HashMap<String, Object>();
			jobOrderDataMap.put("job_id", jobOrder.getId());
			jobOrderDataMap.put("job_title", jobOrder.getTitle());
			jobOrderDataMap.put("positions", jobOrder.getNumOfPos());
			jobOrderDataMap.put("city", jobOrder.getCity());
			jobOrderDataMap.put("state", jobOrder.getState());
			
			if (jobOrder.getStatus().equals(JobOrderStatus.OPEN)
					|| jobOrder.getStatus().equals(JobOrderStatus.REOPEN)
					|| jobOrder.getStatus().equals(JobOrderStatus.ASSIGNED))
				jobOrderDataMap.put("status", "OPEN");
			else
				jobOrderDataMap.put("status", "CLOSED");

			String date = new SimpleDateFormat("MM-dd-yyyy").format(jobOrder
					.getUpdatedOn() != null ? jobOrder.getUpdatedOn()
					: jobOrder.getCreatedOn());
			jobOrderDataMap.put("posted_date", date);
			jobOrderDataMap.put("description", jobOrder.getDescription());
			jsonJobList.add(jobOrderDataMap);
		}

		jsonObj.put("jobOrderList", jsonJobList);
		jsonObj.put("statusMessage", "JobList");
		jsonObj.put("statusCode", 1);
		// }

		return jsonObj.toString();

	}

	@Path("/candidateJobStatus")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String jobListStatus(@QueryParam("email") String email,
			@Context HttpServletRequest request) throws JSONException {
		log.info("from JobOrderListBean jobListStatus() method Start...");
		
		JSONObject jsonObj = new JSONObject();
		// List<Map<String,Object>> jobList = new
		// ArrayList<Map<String,Object>>();
		// Object[] candidateJobArr= null;
		List<Map<String, String>> candidateJobArr = null;
		candidate = candidateService.getCandidateFromEmail(email, true, false);
		Integer candidateID = candidate.getId();
		System.out.println("Inside 2 " + candidateID);
		candidateJobArr = mobileCgiCandidateService
				.findCandidateJobList(candidateID);
		System.out.println("candidateJobArr>>>>>>>>>> " + candidateJobArr);
		/*
		 * for (Map<String, String> map: candidateJobArr){ for (Entry<String,
		 * String> entry : map.entrySet()) { System.out.println("obj  1 "
		 * +entry.getKey()); System.out.println("obj  2 " +entry.getValue()); }
		 * }
		 */
		jsonObj.put("jobStatus", candidateJobArr);
		jsonObj.put("statusMessage", "JobStatus");
		jsonObj.put("statusCode", 1);
		log.info("from JobOrderListBean jobListStatus() method End...");
		return jsonObj.toString();

	}

}

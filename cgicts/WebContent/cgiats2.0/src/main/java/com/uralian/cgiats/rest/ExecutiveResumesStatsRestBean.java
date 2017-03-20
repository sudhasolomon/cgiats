package com.uralian.cgiats.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.uralian.cgiats.model.ExecutiveResumeView;
import com.uralian.cgiats.model.SubmitalStats;
import com.uralian.cgiats.service.JobOrderService;
import com.uralian.cgiats.util.Utils;

@Controller
@Path("/executiveResumeStats")
public class ExecutiveResumesStatsRestBean extends SpringBeanAutowiringSupport{
	
	private static final Logger log=Logger.getLogger(ExecutiveResumesStatsRestBean.class);
	@Autowired
	private JobOrderService jobOrderService;
	
	@GET
	@Path("/resumeStats")
	@Produces(MediaType.APPLICATION_JSON)
	public String executiveResume() throws JSONException {
		log.info("from ExecutiveResumesStatsRestBean executiveResume() Start....");
		List<ExecutiveResumeView> todayResumesStats = null;
		List<ExecutiveResumeView> yearlyResumesStats = null;
		ExecutiveResumeView totalResumeCounts = null;
		Map<String, String> totalResumes = new HashMap<String, String>() ;
		List<Map<String, String>> resultList = null;
		JSONObject resumeData = new JSONObject();
		JSONArray resumeStatsArray = new JSONArray();
		todayResumesStats = jobOrderService.getTodayResumesStats();
		for(ExecutiveResumeView vo : todayResumesStats){
			resumeData.put(vo.getPortalName(), vo.getPortalCount());
			
		}
		totalResumeCounts = jobOrderService.getAllResumesCounts();
		resumeData.put("today_counts", totalResumeCounts.getToDayResumesCounts());
		resumeData.put("total_counts", totalResumeCounts.getTotalResumeCounts());
		resumeStatsArray.put(resumeData);
		String year ="";
		yearlyResumesStats = jobOrderService.getYearWiseResumesStats();
		JSONObject jsonData = new JSONObject();
		for (ExecutiveResumeView vo :yearlyResumesStats)
		{
			if (year.equalsIgnoreCase(vo.getYear())) {
				jsonData.put(vo.getPortalName(), vo.getPortalCount());
			}
			else {
				if(Utils.isBlank(year)){
					year = vo.getYear();
					
				jsonData.put(vo.getPortalName(), vo.getPortalCount());

				}
				else{
					jsonData.put("year", year);
					resumeStatsArray.put(jsonData);
					year = vo.getYear();
					jsonData  =new JSONObject();
					jsonData.put(vo.getPortalName(), vo.getPortalCount());
				}
			}
	}
		jsonData.put("year", year);
		resumeStatsArray.put(jsonData);
		log.info("from ExecutiveResumesStatsRestBean executiveResume() End....");
		return resumeStatsArray.toString();
	}

}

package com.uralian.cgiats.service;

import java.util.Map;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import com.uralian.cgiats.model.JobOrderStatus;

public interface ExecutiveJobStatService {
	
	
	public String getJobOrdersCount() throws JSONException;

}

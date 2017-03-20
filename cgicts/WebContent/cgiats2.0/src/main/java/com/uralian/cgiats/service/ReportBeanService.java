package com.uralian.cgiats.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.SubmittalStatus;

public interface ReportBeanService {

	Map<String, Map<String, String>> getSubmittaslOfDm(Date fromDate, Date toDate, String selectedAdmName, List<String> selectedDms, List<String> selectedLocations);

	Map<String, Map<SubmittalStatus, Integer>> getRecWiseSubmittals(Date fromDate, Date toDate, String selectedDmName,
			String selectedLocation);

	List<Candidate> getCandidateData(Date fromDate, Date toDate, String recName, String dmName);

	
}

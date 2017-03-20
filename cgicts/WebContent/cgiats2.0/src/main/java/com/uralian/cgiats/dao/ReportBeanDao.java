package com.uralian.cgiats.dao;

import java.util.Date;
import java.util.List;

import com.uralian.cgiats.model.Candidate;

public interface ReportBeanDao{

	List getSubmittaslOfDm(Date fromDate, Date toDate, String selectedAdmName, List<String> selectedDms, List<String> selectedLocations);

	List getRecWiseSubmittals(Date fromDate, Date toDate, String selectedDmName, String selectedLocation);

	List<Candidate> getCandidateData(Date fromDate, Date toDate, String recName, String dmName);

}

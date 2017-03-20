package com.uralian.cgiats.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.ReportBeanDao;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.service.ReportBeanService;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ReportBeanServiceImpl implements ReportBeanService {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private ReportBeanDao reportBeanDao;

	@Override
	public Map<String, Map<String, String>> getSubmittaslOfDm(Date fromDate, Date toDate, String selectedAdmName, List<String> selectedDms,
			List<String> selectedLocations) {
		try {
			List list = reportBeanDao.getSubmittaslOfDm(fromDate, toDate, selectedAdmName, selectedDms, selectedLocations);
			Map<String, Map<String, String>> submittals = new HashMap<String, Map<String, String>>();
			Map<String, String> stats;
			for (int i = 0; i < list.size(); i++) {
				stats = new HashMap<String, String>();
				Object pair[] = (Object[]) list.get(i);
				stats.put(SubmittalStatus.SUBMITTED.toString(), pair[2].toString());
				stats.put(SubmittalStatus.DMREJ.toString(), pair[3].toString());
				stats.put(SubmittalStatus.ACCEPTED.toString(), pair[4].toString());
				stats.put(SubmittalStatus.BACKOUT.toString(), pair[5].toString());
				stats.put(SubmittalStatus.CONFIRMED.toString(), pair[6].toString());
				stats.put(SubmittalStatus.INTERVIEWING.toString(), pair[7].toString());
				stats.put(SubmittalStatus.OUTOFPROJ.toString(), pair[8].toString());
				stats.put(SubmittalStatus.REJECTED.toString(), pair[9].toString());
				stats.put(SubmittalStatus.STARTED.toString(), pair[10].toString());
				stats.put("Location", pair[1].toString());
				stats.put("OPEN", pair[11].toString());
				stats.put("CLOSED", pair[12].toString());

				submittals.put(pair[0].toString(), stats);
			}

			return submittals;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Map<String, Map<SubmittalStatus, Integer>> getRecWiseSubmittals(Date fromDate, Date toDate, String selectedDmName, String selectedLocation) {
		try {
			List list = reportBeanDao.getRecWiseSubmittals(fromDate, toDate, selectedDmName, selectedLocation);
			Map<String, Map<SubmittalStatus, Integer>> submittals = new HashMap<String, Map<SubmittalStatus, Integer>>();
			Map<SubmittalStatus, Integer> stats;
			for (int i = 0; i < list.size(); i++) {
				stats = new HashMap<SubmittalStatus, Integer>();
				Object pair[] = (Object[]) list.get(i);
				stats.put(SubmittalStatus.SUBMITTED, Integer.valueOf(pair[1].toString()));
				stats.put(SubmittalStatus.DMREJ, Integer.valueOf(pair[2].toString()));
				stats.put(SubmittalStatus.ACCEPTED, Integer.valueOf(pair[3].toString()));
				stats.put(SubmittalStatus.BACKOUT, Integer.valueOf(pair[4].toString()));
				stats.put(SubmittalStatus.CONFIRMED, Integer.valueOf(pair[5].toString()));
				stats.put(SubmittalStatus.INTERVIEWING, Integer.valueOf(pair[6].toString()));
				stats.put(SubmittalStatus.OUTOFPROJ, Integer.valueOf(pair[7].toString()));
				stats.put(SubmittalStatus.REJECTED, Integer.valueOf(pair[8].toString()));
				stats.put(SubmittalStatus.STARTED, Integer.valueOf(pair[9].toString()));
				submittals.put(pair[0].toString(), stats);
			}

			return submittals;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<Candidate> getCandidateData(Date fromDate, Date toDate, String recName, String dmName) {
		try {
			return reportBeanDao.getCandidateData(fromDate, toDate, recName, dmName);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

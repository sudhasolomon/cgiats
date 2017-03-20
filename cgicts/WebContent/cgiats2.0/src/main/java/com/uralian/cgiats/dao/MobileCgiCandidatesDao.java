package com.uralian.cgiats.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder.In;

import com.uralian.cgiats.model.MobileCgiCandidates;
import com.uralian.cgiats.model.Submittal;

public interface MobileCgiCandidatesDao extends GenericDao<MobileCgiCandidates, Integer>{
	
	public List<?> findCandidateJobList(Integer candidateId);
	
}

package com.uralian.cgiats.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.model.CandidateSearchDto;
import com.uralian.cgiats.model.UsExecutive;

public interface UsExecutiveDao extends GenericDao<UsExecutive, Integer>{
	
	public List<UsExecutive> findByLuceneQuery(String searchString,CandidateSearchDto criteria);

	public int findCountByLuceneQuery(String searchString);
	
	public Map<String, Integer> getExecutivesOnUpdatedDate(Date dateStart, Date dateEnd);

	public List<String> findAllTitles();

	public Map<String, Integer> getExecutivesByUser(Date dateStart, Date dateEnd);

}

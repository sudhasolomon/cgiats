package com.uralian.cgiats.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.IndiaJobOrder;
import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;

public interface IndiaJobOrdersDao extends IndiaGenericDao<IndiaJobOrder, Integer>{

	public	List<String> listExistingTitles();

	public	List<String> listExistingCustomers();

	public	List<String> listExistingCities();
	
	public IndiaJobOrder saveOrder(IndiaJobOrder jobOrder);

	public List<?> findSubmittalsDetails(Map<String, Object> params, String string);

	public Map<String, Map<JobOrderStatus, Integer>> getStatsByUser(User user, Date dateStart, Date dateEnd,
			boolean flag);

	public Map<String, Map<SubmittalStatus, Integer>> getIndiaSubmittalStatusByLocation(UserDto userDto, String officeLoc,
			Date dateStart, Date dateEnd);

	public Map<String, Map<SubmittalStatus, Integer>> getClientwiseSubmittalStats(Date strDate, Date endDate);

	public Map<JobOrderStatus, Integer> getAllJobOrdersCounts(Date dateStart, Date dateEnd);
	
	

}

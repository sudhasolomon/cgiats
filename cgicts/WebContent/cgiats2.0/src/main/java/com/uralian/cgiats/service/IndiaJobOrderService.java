package com.uralian.cgiats.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.dto.JobOrderDto;
import com.uralian.cgiats.dto.SubmittalDto;
import com.uralian.cgiats.dto.SubmittalEventDto;
import com.uralian.cgiats.dto.UserDto;

import org.springframework.web.multipart.MultipartFile;

import com.uralian.cgiats.dto.AddEditJobOrderDto;
import com.uralian.cgiats.dto.IndiaSubmittalStatsDto;
import com.uralian.cgiats.dto.JobOrderDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.IndiaJobOrder;
import com.uralian.cgiats.model.IndiaSubmittal;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.JobOrderSearchDto;
import com.uralian.cgiats.model.JobOrderStatus;
import com.uralian.cgiats.model.Submittal;
import com.uralian.cgiats.model.SubmittalStatus;
import com.uralian.cgiats.model.User;

public interface IndiaJobOrderService {

	public List searchJobOrders(JobOrderSearchDto criteria, UserDto login, int first, int pageSize);

	public IndiaJobOrder getJobOrder(Integer orderId, boolean b, boolean c);

	public void updateJobOrder(IndiaJobOrder indiaJobOrder) throws ServiceException;

	public void saveJobOrder(IndiaJobOrder indiaJobOrder) throws ServiceException;

	public List<String> listExistingTitles();

	public List<String> listExistingCustomers();

	public List<String> listExistingCities();
	
	
	//added for candidate pages
	
	public List<IndiaSubmittal> getSubmittalDetails(int candidateId) throws ServiceException;
	
	public List<IndiaSubmittal> getCandidate(Integer orderId,Integer candidateId);
	
	public List<IndiaSubmittal> getCandidateSubDetails(Integer candidateId);

	public void updateSubmittal(IndiaSubmittal submittal) throws ServiceException;
	
	public IndiaSubmittal getSubmittal(int submittalId);
	
	public void saveSubmittal(IndiaSubmittal submittal) throws ServiceException;

	public List<IndiaSubmittal> getOrderDeletedSubmittals(Integer id);

	public void deleteSubmittal(Integer id) throws ServiceException;

	public void saveJobOrder(AddEditJobOrderDto addEditJobOrderDto, List<MultipartFile> files) throws ServiceException;

	public List<JobOrderDto> findDeletedJobOrders(JobOrderSearchDto jobOrderSearchDto, UserDto userObj);

	public JobOrder reopenJobOrder(Integer jobOrderId, String updatedBy);
	
	
	public IndiaJobOrder getJobOrder(Integer jobOrderId);
	
	public JobOrderDto saveOrder(IndiaJobOrder jobOrder);
	
	public void deleteJobOrder(int orderId) throws ServiceException;

	public void saveOrUpdateSubmittal(SubmittalDto submittalDto);

	public List<SubmittalDto> findSubmittalsDetails(Integer jobOrderId);

	public List<SubmittalEventDto> getSubmittalEventHistoryBySubmittalId(Integer submittalId);

	public void deleteSubmittal(int submittalId, String reason) throws ServiceException;

	public AddEditJobOrderDto getJobOrderAttachmentInfo(Integer jobOrderId);

	public Map<String, Object> getStatsByUser(User user, Date dateStart, Date dateEnd, boolean flag);

	public Map<String, Map<SubmittalStatus, Integer>> getIndiaSubmittalStatusByLocation(UserDto userDto, String officeLoc,
			Date dateStart, Date dateEnd);

	public Map<String, Map<SubmittalStatus, Integer>> getClientwiseSubmittalStats(Date strDate, Date endDate);
	
	public Map<String, Object> getDmSummaryReport(Date startDate, Date endDate);

	public List<?> getUserSubmittalsById(String user_id, String status, Date startDate, Date endDate);

	public void addClient(String clientName, String updatedBy);

	public void updateClient(String oldClientName, String newClientName, String updatedBy);

	public Map<JobOrderStatus, Integer> getAllIndiaJobOrdersCounts(Date yesterdayFrom, Date yesterdayTo); 
		
	

	


}

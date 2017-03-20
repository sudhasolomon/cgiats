package com.uralian.cgiats.dao.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.CareerAccountDao;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CarrerAccount;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.MobileCgiCandidates;

/**
 * This is the default implementation of the Candidate DAO.
 * 
 * @author Christian Rebollar
 */
@Repository
@SuppressWarnings("unchecked")
public class CareerAccountDaoImpl extends GenericDaoImpl<CarrerAccount, Integer>
implements CareerAccountDao
{

	/*@Autowired
	private CareerAccountDao careerAccountDao;*/
	/**
	 */
	public CareerAccountDaoImpl()
	{
		super(CarrerAccount.class);
	}

	/*@Override
	public List<CarrerAccount> getCareerAccountFromEmail(String emailId) {


		StringBuffer hql = new StringBuffer();
		hql.append("select distinct c from career_acct c");
		hql.append(" where c.email = :emailId and c.deleteFlag=0");

		List<CarrerAccount> result = careerAccountDao.findByQuery(hql.toString(),
				"emailId", emailId);

		return result;

	}*/

	/*@Override
	public void save(CarrerAccount carrerAccount) {

	}*/

	/*@Override
	@Transactional(readOnly = true)
	public List<Map<String,String>> findCandidateJobList(Integer candidateId) {
		// TODO Auto-generated method stub

		List<String> paramNames = new ArrayList<String>();
		List<Object> paramValues = new ArrayList<Object>();
		List<Map<String,String>> recordsList=new ArrayList<Map<String,String>>();
		Map<String, String> record= new HashMap<String, String>();
		StringBuilder hql = new StringBuilder();
		hql.append("select "
				+ " mc, jo from MobileCgiCandidates  mc, JobOrder jo");
		hql.append(" where 1=1 and   mc.orderId = jo.id and mc.candidateId = :candidateId");
		paramNames.add("candidateId");
		paramValues.add(candidateId); 
		//System.out.println("hql------->"+hql);
		List<?> result = runQuery(hql.toString(),
				paramNames.toArray(new String[0]), paramValues.toArray());
		System.out.println("result------>"+result);
		for(Object obj: result){
			record=new HashMap<String, String>();
			Object[] tuple = (Object[]) obj;

			Object str1=tuple[0];
			Object str2=tuple[1];

			MobileCgiCandidates mobileCgiCandidates=(MobileCgiCandidates)str1;
			JobOrder jobOrder=(JobOrder)str2;
			record.put("jobOrderId",jobOrder.getId().toString());
			record.put("jobOrderTitle",jobOrder.getTitle().toString());
			record.put("jobOrderCity",jobOrder.getCity().toString());
			record.put("jobOrderState",jobOrder.getState().toString());
			String Jobdate=new SimpleDateFormat("MM/dd/yyyy").format(jobOrder.getUpdatedOn()!=null?jobOrder.getUpdatedOn():jobOrder.getCreatedOn());
			record.put("jobOrderDate",Jobdate);
			record.put("jobOrderDes",jobOrder.getDescription().toString());
			record.put("mobileCandidateStatus",mobileCgiCandidates.getResumeStatus().toString());
			String mobileCandidateDate=new SimpleDateFormat("MM/dd/yyyy").format(mobileCgiCandidates.getUpdatedOn()!=null?mobileCgiCandidates.getUpdatedOn():mobileCgiCandidates.getCreatedOn());
			record.put("mobileCandidateDate",mobileCandidateDate);
			recordsList.add(record);

		}
		System.out.println("recordsList<><><><><><><><><>"+record);
		System.out.println("recordsList<><><><><><><><><>"+recordsList);
		return recordsList;
	}*/
}
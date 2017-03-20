package com.uralian.cgiats.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.model.MonthlySalesQuotas;

/*
 * Radhika
 * Created Date:8/5/2013
 * Comment: Created to store candidate info for the tickets ATS-
 */

public interface MonthlySalesQuotaDao extends GenericDao<MonthlySalesQuotas,Integer>{

	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public  Map<String,String> getDmMontlyQuotas(
			String dateStart, String dateEnd);
	
	public List<MonthlySalesQuotas> getRecSalesQuotaList(String userId, String month, String year);

	/**
	 * @param userId
	 * @param month
	 * @param year
	 * @returnList<MonthlySalesQuotas>
	 */
	public List<MonthlySalesQuotas> getAdmSalesQuota(String userId,
			String month, String year);
}

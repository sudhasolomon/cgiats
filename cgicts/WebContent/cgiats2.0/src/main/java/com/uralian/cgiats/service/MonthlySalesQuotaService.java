package com.uralian.cgiats.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.uralian.cgiats.model.MonthlySalesQuotas;

public interface MonthlySalesQuotaService {
	/**
	 * @param DuplicateResumeTrack
	 * @throws Exception
	 */
	public void saveSalesQuota(MonthlySalesQuotas monthlySalesQuotas) throws Exception;
	
	public void updateSalesQuota(MonthlySalesQuotas monthlySalesQuotas) throws Exception;
	
	
   List<MonthlySalesQuotas> getRecSalesQuotaList(String userId, String month, String year);
	
	
	

	/**
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	public  Map<String,String> getDmMontlyQuotas(
			String dateStart, String dateEnd);

	/**
	 * @param userId
	 * @param month
	 * @param year
	 * @returnList<MonthlySalesQuotas>
	 */
	public List<MonthlySalesQuotas> getAdmSalesQuota(String userId,
			String month, String year);
	
}

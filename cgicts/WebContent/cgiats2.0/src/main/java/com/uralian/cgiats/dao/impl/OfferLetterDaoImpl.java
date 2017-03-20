package com.uralian.cgiats.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.OfferLetterDao;
import com.uralian.cgiats.dto.OfferLetterSearchDto;
import com.uralian.cgiats.model.OfferLetter;

@SuppressWarnings("unchecked")
@Repository
public class OfferLetterDaoImpl extends GenericDaoImpl<OfferLetter, Integer> implements OfferLetterDao {

	public OfferLetterDaoImpl() {
		super(OfferLetter.class);
	}

	@Override
	public List<OfferLetter> loadOfferLetters() {
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" from OfferLetter ol   order by coalesce(ol.updatedOn,ol.createdOn) desc");
			return findByQuery(hql.toString(), null);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<?> getOfferLettersInterval(OfferLetterSearchDto offerLetterSearchDto) {
		try {
			List<String> paramNames = new ArrayList<String>();
			List<Object> paramValues = new ArrayList<Object>();

			StringBuilder hql = new StringBuilder();
			hql.append("select ol.id,ol.jobOrderId,ol.candidate.id,ol.firstName,ol.lastName,ol.email,"
					+ "ol.candidate.address.city,ol.candidate.address.state,ol.companyName," + "ol.bdmName,ol.recruiterName,"
					+ "ol.status,ol.createdBy,ol.updatedBy,ol.createdOn,ol.updatedOn,ol.reason from OfferLetter ol ");

			if (offerLetterSearchDto.getDeleteFlag()) {
				hql.append(" where ol.deleteFlag=1");
			} else {
				hql.append(" where ol.deleteFlag=0");
			}

			if (offerLetterSearchDto.getUserId() != null) {
				hql.append(" and ol.bdmName=:dmName");
				paramNames.add("dmName");
				paramValues.add(offerLetterSearchDto.getUserId());
			} else {
				hql.append(" and ol.status NOT IN('OFFER_LETTER_CREATED','OFFER_LETTER_SAVED')");
			}

			if (offerLetterSearchDto.getStartEntryDate() != null) {
				/*
				 * hql.append(
				 * " and COALESCE(cast(ol.updatedOn as date),cast(ol.createdOn as date)) >=:startDate"
				 * );
				 */
				hql.append(" and ol.createdOn >=:startDate");
				paramNames.add("startDate");
				paramValues.add(offerLetterSearchDto.getStartEntryDate());
			}

			if (offerLetterSearchDto.getEndEntryDate() != null) {
				/*
				 * hql.append(
				 * " and COALESCE(cast(ol.updatedOn as date),cast(ol.createdOn as date)) <=:endDate "
				 * );
				 */
				hql.append(" and ol.createdOn <=:endDate ");
				paramNames.add("endDate");
				paramValues.add(DateUtils.addDays(offerLetterSearchDto.getEndEntryDate(), 1));
			}
			hql.append(" order by COALESCE(ol.updatedOn,ol.createdOn) DESC");

			List<?> resultList = runQuery(hql.toString(), paramNames.toArray(new String[0]), paramValues.toArray());

			return resultList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

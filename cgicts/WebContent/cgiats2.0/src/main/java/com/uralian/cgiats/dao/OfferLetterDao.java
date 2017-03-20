package com.uralian.cgiats.dao;

import java.util.List;

import com.uralian.cgiats.dto.OfferLetterSearchDto;
import com.uralian.cgiats.model.OfferLetter;

public interface OfferLetterDao extends GenericDao<OfferLetter, Integer> {

	List<OfferLetter> loadOfferLetters();

	public List<?> getOfferLettersInterval(OfferLetterSearchDto offerLetterSearchDto);

}

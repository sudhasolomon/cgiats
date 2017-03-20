package com.uralian.cgiats.service;

import java.io.IOException;
import java.util.List;

import com.uralian.cgiats.dto.OfferLetterDto;
import com.uralian.cgiats.dto.OfferLetterHistoryDto;
import com.uralian.cgiats.dto.OfferLetterSearchDto;
import com.uralian.cgiats.model.OfferLetter;

public interface OfferLetterService {

	public void saveOrUpdateOfferLetter(OfferLetterDto offerLetterDto) throws ServiceException;

	public void updateOfferLetter(OfferLetter offerLetter) throws ServiceException;

	public void saveOfferLetterHistory(OfferLetterHistoryDto offerLetterHistoryDto) throws ServiceException;

	List<OfferLetter> getOfferLetters();

	public OfferLetterDto getOfferLetter(Integer candidateId, Integer jobOrderId);
	
	public OfferLetter getOfferLetterById(Integer offerLetterId);

	public List<OfferLetterDto> getOfferLettersInterval(OfferLetterSearchDto offerLetterSearchDto);

	byte[] sendPDF(OfferLetter offerLetter) throws IOException;

	public List<OfferLetterHistoryDto> getOfferLetterHistoryByOfferId(Integer offerLetterId);
	
	public void deleteofferLetter(Integer offerLetterId, String reason);

}

package com.uralian.cgiats.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.primefaces.util.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.CandidateDao;
import com.uralian.cgiats.dao.OfferLetterDao;
import com.uralian.cgiats.dto.OfferLetterDto;
import com.uralian.cgiats.dto.OfferLetterHistoryDto;
import com.uralian.cgiats.dto.OfferLetterSearchDto;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.OfferLetter;
import com.uralian.cgiats.model.OfferLetterHistory;
import com.uralian.cgiats.model.OfferLetterStatus;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.proxy.CGIATSConstants;
import com.uralian.cgiats.service.CommService;
import com.uralian.cgiats.service.CommService.AttachmentInfo;
import com.uralian.cgiats.service.OfferLetterService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.UserService;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.TransformDtoToEntity;
import com.uralian.cgiats.util.TransformEntityToDto;
import com.uralian.cgiats.util.Utils;

@Service
@Transactional(rollbackFor = ServiceException.class)
public class OfferLetterServiceImpl implements OfferLetterService {

	protected final static Logger log = LoggerFactory.getLogger(OfferLetterServiceImpl.class);

	@Autowired
	private OfferLetterDao offerLetterDao;
	@Autowired
	private CandidateDao candidateDao;
	@Autowired
	private CommService commService;
	@Autowired
	private UserService userService;

	@Override
	public void saveOrUpdateOfferLetter(OfferLetterDto offerLetterDto) throws ServiceException {
		OfferLetter offerLetter = null;
		Candidate candidate = null;
		try {
			if (offerLetterDto.getOfferLetterId() != null) {
				offerLetter = offerLetterDao.findById(offerLetterDto.getOfferLetterId());
				TransformDtoToEntity.getOfferLetter(offerLetterDto, offerLetter);
				offerLetter.setUpdatedBy(offerLetterDto.getCreatedBy());
				offerLetter.setUpdatedOn(new Date());
			} else {
				offerLetter = new OfferLetter();
				TransformDtoToEntity.getOfferLetter(offerLetterDto, offerLetter);
				offerLetter.setCreatedBy(offerLetterDto.getCreatedBy());
				offerLetter.setCreatedOn(new Date());
				OfferLetterHistory history = new OfferLetterHistory();
				history.setCreatedBy(offerLetterDto.getCreatedBy());
				history.setCreatedDate(new Date());
				history.setStatusCreatedOn(new Date());
				history.setStatus(OfferLetterStatus.OFFER_LETTER_CREATED);
				offerLetter.addEvent(history);
				candidate = candidateDao.findById(offerLetterDto.getCandidateId());
				offerLetter.setCandidate(candidate);
				offerLetter.setDeleteFlag(0);
				if (log.isDebugEnabled())
					log.debug("Persisting " + offerLetter);

				offerLetterDao.save(offerLetter);
				sendStatusChangeMail(offerLetter, OfferLetterStatus.OFFER_LETTER_CREATED, OfferLetterStatus.OFFER_LETTER_CREATED.name());
			}

		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist offerLetter", exception);
		}

	}

	@Override
	public List<OfferLetter> getOfferLetters() {
		try {
			List<OfferLetter> list = new ArrayList<OfferLetter>();
			list = offerLetterDao.loadOfferLetters();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public OfferLetterDto getOfferLetter(Integer candidateId, Integer jobOrderId) {
		try {
			OfferLetterDto offerLetterDto = null;
			log.info("candidateId ::: " + candidateId + " ; jobOrderId ::: " + jobOrderId);
			try {
				StringBuffer hql = new StringBuffer();
				Map<String, Object> params = new HashMap<String, Object>();
				hql.append("select distinct ol from OfferLetter ol where deleteFlag=0 ");

				if (candidateId != null) {
					hql.append(" and ol.candidate.id = :candidateId");
					params.put("candidateId", candidateId);
				}
				if (jobOrderId != null) {
					hql.append(" and ol.jobOrderId=:jobOrderId");
					params.put("jobOrderId", jobOrderId);

				}
				List<OfferLetter> offerLetterList = offerLetterDao.findByQuery(hql.toString(), params);
				if (offerLetterList != null && offerLetterList.size() > 0) {
					for (OfferLetter offerLetter : offerLetterList) {
						offerLetterDto = TransformEntityToDto.getOfferLetterDto(offerLetter);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
			return offerLetterDto;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<OfferLetterDto> getOfferLettersInterval(OfferLetterSearchDto offerLetterSearchDto) {
		try {
			List<?> offerLetterList = offerLetterDao.getOfferLettersInterval(offerLetterSearchDto);
			List<OfferLetterDto> offerLetterDtoList = null;
			if (offerLetterList != null && offerLetterList.size() > 0) {
				Iterator<?> iterator = offerLetterList.iterator();
				offerLetterDtoList = new ArrayList<OfferLetterDto>();
				while (iterator.hasNext()) {
					OfferLetterDto dto = new OfferLetterDto();
					Object[] obj = (Object[]) iterator.next();
					dto.setOfferLetterId((Integer) obj[0]);
					dto.setJobOrderId((Integer) obj[1]);
					dto.setCandidateId((Integer) obj[2]);
					dto.setFullName(Utils.concatenateTwoStringsWithSpace((String) obj[3], (String) obj[4]));
					dto.setEmail((String) obj[5]);
					dto.setAddress(Utils.concatenateTwoStringsWithComma((String) obj[6], (String) obj[7]));
					dto.setCompanyName((String) obj[8]);
					dto.setBdmName((String) obj[9]);
					dto.setRecruiterName((String) obj[10]);
					if (obj[11] != null) {
						if (((OfferLetterStatus) obj[11]).name().equalsIgnoreCase(Constants.FIRST_CONTACT_MADE_PRE_OFFER_DISCUSSION)) {
							dto.setStatus(Constants.FIRST_CONTACT_MADE_OR_PRE_OFFER_DISCUSSION);
						} else {
							dto.setStatus(((OfferLetterStatus) obj[11]).name());
						}
					}
					dto.setUpdatedBy((String) Utils.getOneValueFromTwo(obj[13], obj[12]));
					dto.setStrUpdatedOn(Utils.convertDateToString_HH_MM_A((Date) Utils.getOneValueFromTwo(obj[15], obj[14])));
					dto.setReason(Utils.getStringValueOfObj(obj[16]));
					offerLetterDtoList.add(dto);
				}
			}

			return offerLetterDtoList;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public void updateOfferLetter(OfferLetter offerLetter) throws ServiceException {
		try {
			if (log.isDebugEnabled())
				log.debug("Persisting " + offerLetter);

			offerLetterDao.update(offerLetter);
		} catch (Exception exception) {
			log.error(exception.getMessage(), exception);
			throw new ServiceException("Error while trying to persist offerLetter", exception);
		}
	}

	@Override
	public byte[] sendPDF(OfferLetter offerLetter) throws IOException {
		try {
			byte[] pdfBytes = null;
			SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");

			PDDocument document = new PDDocument();
			PDPage page = new PDPage();
			document.addPage(page);
			PDPageContentStream stream = new PDPageContentStream(document, page);

			// page header
			PDFont headFont = PDType1Font.HELVETICA_BOLD;
			stream.setFont(headFont, 12);
			stream.beginText();
			stream.moveTextPositionByAmount(250, 700);
			stream.drawString("OFFER LETTER");
			stream.endText();
			stream.drawLine(0, 699, 1000, 699);

			// body

			PDFont bodyFont = PDType1Font.HELVETICA_BOLD;
			stream.setFont(bodyFont, 8);

			drawOfferLeterLine(stream, 150, 680, "BDM/DM : " + offerLetter.getBdmName());

			drawOfferLeterLine(stream, 320, 680, "RECRUITER'S NAME : " + offerLetter.getRecruiterName());

			Integer i = 650;
			Integer j = 12;
			stream.drawLine(50, 662, 1000, 662);
			drawOfferLeterLine(stream, 250, i, "Candidate Information ");
			i = i - j;
			stream.drawLine(50, 648, 1000, 648);

			String dateOfBirth = "";
			if (offerLetter.getDateofbirth() != null)
				dateOfBirth = format.format(offerLetter.getDateofbirth());

			// String[] strArray =
			// Utils.splitAPipeWordIntoTwoStrings(offerLetter.getStreet1());
			String street = Utils.concatenateTwoStringsWithComma(offerLetter.getStreet1(), offerLetter.getStreet2());

			String[][] content0 = { { "Candidate Name", ": " + offerLetter.getFullName() },
					{ "Date Of Birth", ": " + dateOfBirth, "Gender : " + offerLetter.getGender() }, { "SSN:", ": " + offerLetter.getSsn(), "", "" },
					{ "Address" }, { "", "Street :" + street }, { "", "City : " + offerLetter.getCity() },
					{ "", "State  : " + Utils.getStateNameByCode(offerLetter.getState()), "Zip Code : " + offerLetter.getZipcode() },
					{ "", "Country  : " + offerLetter.getCountry() }, { "Phone", ":" + offerLetter.getPhone() }, { "Email", ":" + offerLetter.getEmail() },
					{ "Immigration Status", ": " + offerLetter.getImmigrationStatus() }, };

			i = drawTable(page, stream, i, 50, content0);

			stream.drawLine(50, i + 2, 1000, i + 2);
			i = i - 10;
			drawOfferLeterLine(stream, 250, i, "Position Information ");
			stream.drawLine(50, i - 2, 1000, i - 2);

			String bonusDesc = null;
			if (offerLetter.getCommibonusEligible() != null) {
				bonusDesc = offerLetter.getCommibonusEligible().equals("true") ? "Yes  (Bonus Description  :" + offerLetter.getBonusDescription() + ")"
						: " No ";
			} else {
				bonusDesc = " No ";
			}
			String salary = "";
			if (offerLetter.getSalaryRate() != null && offerLetter.getSalaryRate().trim().length() > 0) {
				DecimalFormat dFormat = new DecimalFormat("####,###,###");
				salary = "$" + dFormat.format(Long.parseLong(offerLetter.getSalaryRate()));
				// salary =
				// NumberFormat.getCurrencyInstance().format(Integer.parseInt(offerLetter.getSalaryRate()));
			}
			String startAsstDate = "";
			if (offerLetter.getStartdateOfAssignment() != null)
				startAsstDate = format.format(offerLetter.getStartdateOfAssignment());

			String[][] content1 = { { "Position Title", ": " + offerLetter.getPositionTitle() }, { "Salary/Pay Rate", ": " + salary },
					{ "Commission/Bonus Eligible:", ": " + bonusDesc }, { "End Client's Name", ": " + offerLetter.getEndclientName() },
					{ "Work Location", ": " + offerLetter.getWorkLocation() }, { "StartDate Assignment:", ": " + startAsstDate } };

			String positionCheckBoxes = "Overtime(Hours Paid?) : "
					+ (offerLetter.getOvertime() != null && offerLetter.getOvertime().equals("Yes") ? "Yes" : "No") + "                   "
					+ "Backgound Check : " + (offerLetter.getBackgroundcheck() != null && offerLetter.getBackgroundcheck().equals("Yes") ? "Yes" : "No")
					+ "                  " + "Drug Check : " + (offerLetter.getDrugcheck() != null && offerLetter.getDrugcheck().equals("Yes") ? "Yes" : "No");

			i = drawTable(page, stream, i, 50, content1);
			i = i - 10;

			drawOfferLeterLine(stream, 100, i, positionCheckBoxes);
			i = i - j;

			stream.drawLine(50, i, 1000, i);
			i = i - j;
			drawOfferLeterLine(stream, 250, i, "Consultant Info");
			stream.drawLine(50, i - 2, 1000, i - 2);

			// strArray =
			// Utils.splitAPipeWordIntoTwoStrings(offerLetter.getContactPerson());
			String conatactPerson = null;

			if (offerLetter.getContactPerson() != null && offerLetter.getContactPerson().trim().length() > 0) {
				String[] strArray = Utils.splitAPipeWordIntoTwoStrings(offerLetter.getContactPerson());
				conatactPerson = Utils.concatenateTwoStringsWithSpace(strArray[0], strArray[0]);
			} else {
				conatactPerson = Utils.concatenateTwoStringsWithSpace(offerLetter.getContactPersonFirstName(), offerLetter.getContactPersonLastName());
			}

			// strArray =
			// Utils.splitAPipeWordIntoTwoStrings(offerLetter.getClientstreet());
			String clientStreet = Utils.concatenateTwoStringsWithComma(offerLetter.getClientstreet(), offerLetter.getClientstreet2());

			String[][] content2 = { { "Company Name", ": " + offerLetter.getCompanyName(), "", "Tax Id : " + offerLetter.getTaxId() },
					{ "Phone", ": " + offerLetter.getClientphone(), "Xtn", ": " + offerLetter.getClient_extension() },
					{ "Contact Person's Name", ": " + conatactPerson, "Title Of Person:", ": " + offerLetter.getTitleOfName() },
					{ "Fax", ": " + offerLetter.getClient_fax(), "Email", ": " + offerLetter.getClientemail() },
					{ "Payment Terms", ": " + offerLetter.getPaymentTerms(), "Sole Proprietor/Individual", ": " + offerLetter.getIndividual() },
					{ "Address", " ", "", "" }, { "", "Street :" + clientStreet, "City : " + offerLetter.getClientcity() },
					{ "", "State  : " + offerLetter.getClientstate(), "Zip Code : " + offerLetter.getClientzipcode() } };

			i = drawTable1(page, stream, i, 50, content2);

			stream.drawLine(50, i, 1000, i);
			i = i - j;
			drawOfferLeterLine(stream, 250, i, "Employment Relationship");
			stream.drawLine(50, i - 2, 1000, i - 2);
			String[][] content3 = { { "Offer From", ": " + offerLetter.getOfferFrom(), "", "" },
					{ "Category1(Salaried)", ":  EE Electing Medical : " + (offerLetter.isElectingMedicalSalired() ? "Yes" : "No"),
							"EE Waiving Medical : " + (offerLetter.isWaviningMedicalSalired() ? "Yes" : "No"), "" },
					{ "Category2(Hourly)", ":  EE Electing Medical : " + (offerLetter.isElectingMedicalHourly() ? "Yes" : "No"),
							"EE Waiving Medical : " + (offerLetter.isWaviningMedicalHourly() ? "Yes" : "No"), "" }, };
			i = drawTable1(page, stream, i, 50, content3);
			stream.drawLine(50, i, 1000, i);
			i = i - j;
			drawOfferLeterLine(stream, 250, i, "Relocation Benefits");
			stream.drawLine(50, i - 2, 1000, i - 2);

			i = i - j - 5;

			StringBuilder reloacatioBenifits = new StringBuilder();

			reloacatioBenifits.append("Relocation Benefits          :");

			reloacatioBenifits.append("		Airfare: ");
			if (offerLetter.getRelocationBenefits().contains("Airfare"))
				reloacatioBenifits.append("Yes");
			else {
				reloacatioBenifits.append("No");
			}
			reloacatioBenifits.append("					1 Week Motel: ");
			if (offerLetter.getRelocationBenefits().contains("1 Week motel"))
				reloacatioBenifits.append("Yes");
			else {
				reloacatioBenifits.append("No");
			}
			reloacatioBenifits.append("					1 Week Meals: ");
			if (offerLetter.getRelocationBenefits().contains("1 Week meals"))
				reloacatioBenifits.append("Yes");
			else {
				reloacatioBenifits.append("No");
			}
			reloacatioBenifits.append("					1 Week Rental Car: ");
			if (offerLetter.getRelocationBenefits().contains("1 Week rental/cab"))
				reloacatioBenifits.append("Yes");
			else {
				reloacatioBenifits.append("No");
			}

			drawOfferLeterLine(stream, 50, i, reloacatioBenifits.toString());
			i = i - j;

			i = i - 20;

			drawOfferLeterLine(stream, 50, i, "Any Exceptions: ");
			i = i - j;

			i = drawOfferLeterParagraph(stream, 110, i, offerLetter.getExpections());
			i = i - 5;

			drawOfferLeterLine(stream, 50, i, "Any Special Instructions: ");
			i = i - j;

			i = drawOfferLeterParagraph(stream, 110, i, offerLetter.getSpecialInstructions());
			i = i - j;

			stream.close();

			try {

				ByteArrayOutputStream output = new ByteArrayOutputStream();
				document.save(output);
				pdfBytes = output.toByteArray();
			} catch (COSVisitorException e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
			document.close();

			return pdfBytes;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private Integer drawOfferLeterParagraph(PDPageContentStream stream, int i, Integer i2, String string) {
		try {
			List<String> lines = new ArrayList<String>();
			String[] result = null;
			if (string != null) {
				result = string.split("\\s");
			}

			StringBuffer buffer = new StringBuffer();
			if (result != null) {
				for (String line : result) {
					if (buffer.length() <= 100) {
						buffer.append(line + " ");
					} else {
						lines.add(buffer.toString());
						buffer.delete(0, buffer.length());
						buffer.append(line + " ");
					}
				}
			}
			if (buffer.length() > 0) {
				lines.add(buffer.toString());
			}

			System.out.println(lines);

			for (String line : lines) {
				try {
					drawOfferLeterLine(stream, i, i2, line);
					i2 = i2 - 12;
				} catch (IOException e) {
					e.printStackTrace();
					log.error(e.getMessage(), e);
				}
			}

			return i2;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private void drawOfferLeterLine(PDPageContentStream stream, int x, int y, String string) throws IOException {
		try {
			stream.beginText();
			stream.moveTextPositionByAmount(x, y);
			stream.drawString(string);
			stream.endText();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unused")
	public static Integer drawTable(PDPage page, PDPageContentStream contentStream, float y, float margin, String[][] content) throws IOException {
		try {
			final int rows = content.length;
			final int cols = content[0].length;
			final float rowHeight = 12f;
			final float tableWidth = (page.findMediaBox().getWidth() - margin - margin) / 2;
			final float tableHeight = rowHeight * rows;
			final float colWidth = tableWidth / (float) cols;
			final float cellMargin = 0f;

			// draw the rows
			float nexty = y;
			for (int i = 0; i <= rows; i++) {
				nexty -= rowHeight;
			}

			// draw the columns
			float nextx = margin;
			for (int i = 0; i <= cols; i++) {
				nextx += colWidth;
			}

			// now add the text
			contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);

			float textx = margin + cellMargin;
			float texty = y - 15;
			for (int i = 0; i < content.length; i++) {
				for (int j = 0; j < content[i].length; j++) {
					String text = content[i][j];
					contentStream.beginText();
					contentStream.moveTextPositionByAmount(textx, texty);
					contentStream.drawString(text);
					contentStream.endText();
					textx += colWidth;
				}
				texty -= rowHeight;
				textx = margin + cellMargin;
			}
			return (int) nexty;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unused")
	public static Integer drawTable1(PDPage page, PDPageContentStream contentStream, float y, float margin, String[][] content) throws IOException {
		try {
			final int rows = content.length;
			final int cols = content[0].length;
			final float rowHeight = 12f;
			final float tableWidth = page.findMediaBox().getWidth() - margin - margin;
			final float tableHeight = rowHeight * rows;
			final float colWidth = tableWidth / (float) cols;
			final float cellMargin = 5f;

			// draw the rows
			float nexty = y;
			for (int i = 0; i <= rows; i++) {
				nexty -= rowHeight;
			}

			// draw the columns
			float nextx = margin;
			for (int i = 0; i <= cols; i++) {
				nextx += colWidth;
			}

			// now add the text
			contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);

			float textx = margin + cellMargin;
			float texty = y - 15;
			for (int i = 0; i < content.length; i++) {
				for (int j = 0; j < content[i].length; j++) {
					String text = content[i][j];
					contentStream.beginText();
					contentStream.moveTextPositionByAmount(textx, texty);
					contentStream.drawString(text);
					contentStream.endText();
					textx += colWidth;
				}
				texty -= rowHeight;
				textx = margin + cellMargin;
			}
			return (int) texty;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private void sendStatusChangeMail(OfferLetter offerLetter, OfferLetterStatus offerLetterStatus, String presentStatus) {
		try {
			StringBuffer mailBody = new StringBuffer();
			String subject = "OfferLetter of " + offerLetter.getFullName();
			// User user = userService.loadUser(principal.getName());

			User dmUser = userService.loadUser(offerLetter.getBdmName() != null ? offerLetter.getBdmName() : offerLetter.getRecruiterName());

			generateMailBody(mailBody, offerLetter, offerLetterStatus, presentStatus);

			if (offerLetterStatus.equals(OfferLetterStatus.OFFER_LETTER_CREATED) || offerLetterStatus.equals(OfferLetterStatus.OFFER_LETTER_SAVED)
					|| offerLetterStatus.equals(OfferLetterStatus.FIRST_CONTACT_MADE_PRE_OFFER_DISCUSSION)) {
				try {
					mailBody.append("<br><br><br>");
					mailBody.append("<b>*** This is an automatically generated email, please do not reply ***</b> ");
					AttachmentInfo attachments[] = null;
					commService.sendEmail(null, dmUser.getEmail(), subject, mailBody.toString(), attachments);
				} catch (ServiceException e) {
					e.printStackTrace();
					log.error(e.getMessage(), e + "failed to sending mails");
				}
			} else if (offerLetterStatus.equals(OfferLetterStatus.OFFER_LETTER_SUBMITTED)) {

				// mail with an attachment
				byte[] pdfBytes = null;
				try {
					pdfBytes = sendPDF(offerLetter);
				} catch (IOException e) {
					e.printStackTrace();
					log.error(e.getMessage(), e + "failed to sending mails");
				}
				if (pdfBytes != null) {
					AttachmentInfo ai = new AttachmentInfo("OfferLetter", pdfBytes, "application/pdf");

					AttachmentInfo aiArray[] = { ai };

					mailBody.append("<br>Please find the offer letter as attachment.<br><br><br>");
					mailBody.append("<b>*** This is an automatically generated email, please do not reply ***</b> ");

					String[] mails = ArrayUtils.concat(CGIATSConstants.OFFERLETTER_MAILS, new String[] { dmUser.getEmail() });

					try {
						commService.sendEmail(null, mails, null, subject, mailBody.toString(), aiArray);

					} catch (ServiceException e) {
						e.printStackTrace();
						log.error(e.getMessage(), e + "failed to sending mails");
					}
				}
			} else {
				// mails for statuses changed by hr
				mailBody.append("<br><br><br>");
				mailBody.append("<b>*** This is an automatically generated email, please do not reply ***</b> ");

				String[] mails = ArrayUtils.concat(CGIATSConstants.OFFERLETTER_MAILS, new String[] { dmUser.getEmail() });
				try {
					AttachmentInfo attachments[] = null;
					commService.sendEmail(null, mails, null, subject, mailBody.toString(), attachments);
				} catch (ServiceException e) {
					e.printStackTrace();
					log.error(e.getMessage(), e + "failed to sending mails");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	private void generateMailBody(StringBuffer mailBody, OfferLetter offerLetter, OfferLetterStatus offerLetterStatus, String presentStatus) {
		try {
			mailBody.append("Hi ,<br><br>");
			mailBody.append("<u>Candidate Information</u><br>");
			mailBody.append("Candidate Id:&nbsp;<b>" + offerLetter.getCandidate().getId() + "</b><br>");
			mailBody.append("Candidate Name:&nbsp;<b>" + offerLetter.getFullName() + "</b><br>");
			mailBody.append("JobOrder Id :&nbsp;<b>" + offerLetter.getJobOrderId() + "</b><br>");
			mailBody.append("Candidate Phone:&nbsp;<b>" + offerLetter.getPhone() + "</b><br>");
			mailBody.append("Candidate Email:&nbsp;<b>" + offerLetter.getEmail() + "</b><br>");
			mailBody.append("Candidate Immigration Status:&nbsp;<b>" + offerLetter.getImmigrationStatus() + "</b><br><br>");
			if (!offerLetterStatus.equals(OfferLetterStatus.OFFER_LETTER_CREATED)) {
				String movedStatus = offerLetterStatus.equals(OfferLetterStatus.FIRST_CONTACT_MADE_PRE_OFFER_DISCUSSION)
						? "FIRST_CONTACT_MADE/PRE_OFFER_DISCUSSION" : offerLetterStatus.toString();
				System.out.println(movedStatus);
				mailBody.append("OfferLetter status is moved From " + (presentStatus) + "  to " + (offerLetterStatus) + ".");
			} else {
				mailBody.append("OfferLetter is created with " + OfferLetterStatus.OFFER_LETTER_CREATED + " status.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public List<OfferLetterHistoryDto> getOfferLetterHistoryByOfferId(Integer offerLetterId) {
		log.info("OfferLetterId ::: " + offerLetterId);
		List<OfferLetterHistoryDto> offerLetterHistoryDtoList = null;
		try {
			StringBuffer hqlSelect = new StringBuffer(
					"select o.id,o.createdBy,o.statusCreatedOn,o.status,o.notes from OfferLetterHistory o where o.offerletter.id=?1");
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("1", offerLetterId);
			hqlSelect.append(" order by o.createdDate DESC");
			List<?> historyList = offerLetterDao.findByQuery(hqlSelect.toString(), 0, 0, params);
			if (historyList != null && historyList.size() > 0) {
				offerLetterHistoryDtoList = new ArrayList<OfferLetterHistoryDto>();
				Iterator<?> iterator = historyList.iterator();
				while (iterator.hasNext()) {
					OfferLetterHistoryDto historyDto = new OfferLetterHistoryDto();
					Object[] obj = (Object[]) iterator.next();
					historyDto.setOfferLetterHistoryId((Integer) obj[0]);
					historyDto.setCreatedBy((String) obj[1]);
					historyDto.setStrStatusCreatedOn(Utils.convertDateToString_HH_MM_A((Date) obj[2]));
					historyDto.setStatus(((OfferLetterStatus) obj[3]).name());
					historyDto.setNotes(Utils.replaceNullWithEmpty((String) obj[4]));
					offerLetterHistoryDtoList.add(historyDto);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return offerLetterHistoryDtoList;
	}

	@Override
	public void saveOfferLetterHistory(OfferLetterHistoryDto offerLetterHistoryDto) throws ServiceException {
		try {
			log.error("Offer Letter is saving.....");

			OfferLetterHistory offerLetterEvent = new OfferLetterHistory();
			offerLetterEvent.setCreatedBy(offerLetterHistoryDto.getCreatedBy());
			offerLetterEvent.setStatus(OfferLetterStatus.valueOf(offerLetterHistoryDto.getStatus()));
			if (offerLetterHistoryDto.getStrStatusCreatedOn() != null) {
				offerLetterEvent
						.setStatusCreatedOn(Utils.getDateWithTimeFromDate(Utils.convertAngularStrToDate(offerLetterHistoryDto.getStrStatusCreatedOn())));
			} else
				offerLetterEvent.setStatusCreatedOn(new Date());

			offerLetterEvent.setCreatedDate(new Date());
			OfferLetter offerLetter = offerLetterDao.findById(offerLetterHistoryDto.getOfferLetterId());
			String presentStatus = offerLetter.getStatus().name();
			offerLetterEvent.setNotes(offerLetterHistoryDto.getNotes());
			offerLetter.setStatus(OfferLetterStatus.valueOf(offerLetterHistoryDto.getStatus()));
			offerLetter.setUpdatedBy(offerLetterHistoryDto.getCreatedBy());
			offerLetter.setUpdatedOn(new Date());
			offerLetter.addEvent(offerLetterEvent);

			offerLetterDao.update(offerLetter);

			sendStatusChangeMail(offerLetter, offerLetter.getStatus(), presentStatus);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void deleteofferLetter(Integer offerLetterId, String reason) {
		log.info("offerletter id" + offerLetterId + "reason" + reason);
		try {
			if (offerLetterId != null) {
				OfferLetter offerLetter = offerLetterDao.findById(offerLetterId);
				offerLetter.setDeleteFlag(1);
				offerLetter.setReason(reason);
				offerLetterDao.update(offerLetter);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.cgiats.service.OfferLetterService#getOfferLetterById(java.
	 * lang.Integer)
	 */
	@Override
	public OfferLetter getOfferLetterById(Integer offerLetterId) {
		try {
			log.info("offerLetterId :::" + offerLetterId);
			return offerLetterDao.findById(offerLetterId);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

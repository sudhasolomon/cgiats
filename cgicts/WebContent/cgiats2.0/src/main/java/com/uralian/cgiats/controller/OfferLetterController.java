/**
 * 
 */
package com.uralian.cgiats.controller;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.primefaces.util.ArrayUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.uralian.cgiats.dto.OfferLetterDto;
import com.uralian.cgiats.dto.OfferLetterHistoryDto;
import com.uralian.cgiats.dto.OfferLetterSearchDto;
import com.uralian.cgiats.dto.UserDto;
import com.uralian.cgiats.model.OfferLetter;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;
import com.uralian.cgiats.proxy.CGIATSConstants;
import com.uralian.cgiats.service.CommService;
import com.uralian.cgiats.service.CommService.AttachmentInfo;
import com.uralian.cgiats.service.OfferLetterService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.UserService;
import com.uralian.cgiats.util.Utils;

/**
 * 
 * @author skurapati
 *
 */
@RestController
@RequestMapping("offerLetter")
public class OfferLetterController {

	protected final org.slf4j.Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private OfferLetterService offerLetterService;
	@Autowired
	private UserService userService;
	@Autowired
	private CommService commService;

	/**
	 * This method is to get the all offer letters based on the time duration
	 * 
	 * @param offerLetterSearchDto
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getAllOfferLettersByInterval", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getAllOfferLettersByInterval(@RequestBody OfferLetterSearchDto offerLetterSearchDto, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			long startTime = Calendar.getInstance().getTimeInMillis();
			List<OfferLetterDto> offerLetterDtoList = null;

			log.info("Method has started.. at : " + startTime);

			if (!Utils.isNull(offerLetterSearchDto)) {
				if (offerLetterSearchDto.getTimeIntervalMap() != null) {
					offerLetterSearchDto.setStartEntryDate(Utils.convertAngularStrToDate(offerLetterSearchDto.getTimeIntervalMap().get("startDate")));
					offerLetterSearchDto.setEndEntryDate(Utils.convertAngularStrToDate(offerLetterSearchDto.getTimeIntervalMap().get("endDate")));
				}
				UserDto userDto = Utils.getLoginUser(request);
				// HR can view all the offer letters
				if (!userDto.getUserRole().equals(UserRole.HR)) {
					offerLetterSearchDto.setUserId(userDto.getUserId());
				}
				offerLetterDtoList = offerLetterService.getOfferLettersInterval(offerLetterSearchDto);
			}
			long endTime = Calendar.getInstance().getTimeInMillis();
			log.info("Time taken to execute this method : " + (endTime - startTime) + " ms");
			return new ResponseEntity<>(offerLetterDtoList, HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * This method is to get the offer letter based on candidateId and
	 * jobOrderId
	 * 
	 * @param candidateId
	 * @param jobOrderId
	 * @param request
	 * @return offerLetterDto
	 */
	@RequestMapping(value = "/getOfferLetterByCandidateAndJobOrderId", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> getOfferLetterByCandidateAndJobOrderId(@RequestParam("candidateId") Integer candidateId,
			@RequestParam("jobOrderId") String jobOrderId, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			if (jobOrderId != null && candidateId != null) {
				OfferLetterDto offerLetterDto = offerLetterService.getOfferLetter(candidateId, Integer.parseInt(jobOrderId));
				return new ResponseEntity<>(offerLetterDto, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * This method is to perform save or update
	 * 
	 * @param offerLetterDto
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveOrUpdateOfferLetter", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> saveOrUpdateOfferLetter(@RequestBody OfferLetterDto offerLetterDto, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				if (offerLetterDto != null) {
					offerLetterDto.setCreatedBy(Utils.getLoginUserId(request));
					offerLetterDto.setUpdatedBy(Utils.getLoginUserId(request));
					offerLetterService.saveOrUpdateOfferLetter(offerLetterDto);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
				
				return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
			}

			return new ResponseEntity<Object>(HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * Perform only saving of offer letter history
	 * 
	 * @param offerLetterHistoryDto
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/saveOfferLetterHistory", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<?> saveOfferLetterHistory(@RequestBody OfferLetterHistoryDto offerLetterHistoryDto, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {
				Map<String, String> msgMap = new HashMap<String, String>();
				if (offerLetterHistoryDto != null) {
					List<OfferLetterHistoryDto> historyDtoList = offerLetterService.getOfferLetterHistoryByOfferId(offerLetterHistoryDto.getOfferLetterId());
					for (OfferLetterHistoryDto hist : historyDtoList) {
						if (hist.getStatus().equals(offerLetterHistoryDto.getStatus())) {
							msgMap.put("errMsg", "Selected status already exists");
							return new ResponseEntity<>(msgMap, HttpStatus.OK);
						}
					}

					offerLetterHistoryDto.setCreatedBy(Utils.getLoginUserId(request));
					offerLetterHistoryDto.setUpdatedBy(Utils.getLoginUserId(request));
					offerLetterService.saveOfferLetterHistory(offerLetterHistoryDto);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
				
				return new ResponseEntity<Object>(HttpStatus.INTERNAL_SERVER_ERROR);
			}

			return new ResponseEntity<Object>(HttpStatus.OK);
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * Get list of histories of offer letter status
	 * 
	 * @param offerLetterId
	 * @param request
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(value = "/getHistoryByOfferLetterId/{offerLetterId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> getHistoryByOfferLetterId(@PathVariable(value = "offerLetterId") Integer offerLetterId, HttpServletRequest request)
			throws ServiceException {
		if (Utils.getLoginUserId(request) != null) {

			try {
				List<OfferLetterHistoryDto> historyDtoList = offerLetterService.getOfferLetterHistoryByOfferId(offerLetterId);
				return new ResponseEntity<>(historyDtoList, HttpStatus.OK);
			} catch (Exception e) {
				log.error("Exception " + e);
				return null;
			}
		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	/**
	 * Delete offer letter
	 * 
	 * @param offerLetterId
	 * @param reason
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deleteOfferLetter", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> deleteOfferLetter(@RequestParam("offerLetterId") Integer offerLetterId, @RequestParam("reason") String reason,
			HttpServletRequest request) {

		if (Utils.getLoginUserId(request) != null) {
			try {
				offerLetterService.deleteofferLetter(offerLetterId, reason);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}

		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<List<?>>(HttpStatus.OK);
	}

	@RequestMapping(value = "/sendOfferLetter/{offerLetterId}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<?> sendOfferLetter(@PathVariable("offerLetterId") Integer offerLetterId, HttpServletRequest request) {
		if (Utils.getLoginUserId(request) != null) {
			try {

				log.debug("sendOfferLetter() method execution is started");
				Map<String, String> msgMap = new HashMap<String, String>();
				if (offerLetterId != null) {

					OfferLetter offerLetter = offerLetterService.getOfferLetterById(offerLetterId);

					if (offerLetter == null) {
						msgMap.put("errMsg", "Unable to send OfferLetter");
						return new ResponseEntity<>(msgMap, HttpStatus.OK);
					}

					// Principal principal =
					// UIBean.getFacesContext().getExternalContext().getUserPrincipal();
					// User user = userService.loadUser(principal.getName());
					UserDto userDto = Utils.getLoginUser(request);

					byte[] pdfBytes = offerLetterService.sendPDF(offerLetter);

					String dmname = offerLetter.getBdmName().length() > 0 ? offerLetter.getBdmName() : offerLetter.getRecruiterName();

					User dmUser = userService.loadUser(dmname);

					if (pdfBytes != null) {
						AttachmentInfo ai = new AttachmentInfo("Offer Letter", pdfBytes, "application/pdf");

						AttachmentInfo aiArray[] = { ai };

						String subject = "Offer Letter of " + offerLetter.getFullName();
						StringBuffer content = new StringBuffer();

						content.append("Hi All,<br><br>");
						content.append("<u>Candidate Information</u><br>");
						content.append("Candidate Id:&nbsp;<b>" + offerLetter.getCandidate().getId() + "</b><br>");
						content.append("Candidate Name:&nbsp;<b>" + offerLetter.getFullName() + "</b><br>");
						content.append("JobOrder Id:&nbsp;<b>" + offerLetter.getJobOrderId() + "</b><br>");
						content.append("Candidate Phone:&nbsp;<b>" + offerLetter.getPhone() + "</b><br>");
						content.append("Candidate Email:&nbsp;<b>" + offerLetter.getEmail() + "</b><br>");
						content.append("Candidate Immigration Status:&nbsp;<b>" + offerLetter.getImmigrationStatus() + "</b><br><br>");

						content.append("Please find the offer letter as attachment.<br><br><br>");
						content.append("<b>*** This is an automatically generated email, please do not reply ***</b> ");
						String[] mails = ArrayUtils.concat(CGIATSConstants.OFFERLETTER_MAILS, new String[] { dmUser.getEmail() });
						try {
							if (userDto.getEmail() != null && userDto.getEmail().length() > 0) {

								commService.sendEmail(null, mails, null, subject, content.toString(), aiArray);
								// commService.sendEmail(userDto.getEmail(),
								// mails, null, subject, content.toString(),
								// aiArray);
								// addInfoMessage("Success", "OfferLetter Sent
								// Successfully");
								// msgMap.put("errMsg", "Unable to send
								// OfferLetter");
								return new ResponseEntity<List<?>>(HttpStatus.OK);
							} else {
								msgMap.put("errMsg", "Unable to send OfferLetter");
								return new ResponseEntity<>(msgMap, HttpStatus.OK);
								// addMessage(null, SEVERITY_ERROR, "Error",
								// "Unable
								// to send OfferLetter");
							}

						} catch (ServiceException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							log.error(e.getMessage(), e);
							// addMessage(null, SEVERITY_ERROR, "Error", "Unable
							// to
							// send OfferLetter");
							msgMap.put("errMsg", "Unable to send OfferLetter");
							return new ResponseEntity<>(msgMap, HttpStatus.OK);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} else {
			log.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<List<?>>(HttpStatus.OK);
	}

}

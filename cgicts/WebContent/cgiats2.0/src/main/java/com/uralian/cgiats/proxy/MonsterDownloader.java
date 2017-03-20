package com.uralian.cgiats.proxy;

import java.util.Date;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.internal.ConcurrentHashMap;

import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateStatus;
import com.uralian.cgiats.model.CandidateStatuses;
import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.proxy.json.JsonHelper;
import com.uralian.cgiats.proxy.json.JsonMonsterResume;
import com.uralian.cgiats.proxy.json.MonsterLanguage;
import com.uralian.cgiats.proxy.json.MonsterSkill;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.Utils;

/**
 * @author Vlad Orzhekhovskiy
 */
public class MonsterDownloader extends AbstractDownloader {
	private ConcurrentMap<String, Boolean> docsBeingDownloaded = new ConcurrentHashMap<String, Boolean>();
	private HttpResponse response;
	private HttpServletRequest request;

	public static final String MNST_CANDINAME_ID = "ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolder1_ctl00_controlDetailTop_lbName";
	public static final String MNST_CANDI_TITLE_ID = "ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolder1_ctl00_controlDetailTop_lbMostRecentJob";
	public static final String MNST_CANDI_ADDR_ID = "ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolder1_ctl00_controlDetailTop_lbAddress";
	public static final String MNST_CANDI_HOME_PHONE_ID = "ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolder1_ctl00_controlDetailTop_lbHome";
	public static final String MNST_CANDI_MOBILE_ID = "ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolder1_ctl00_controlDetailTop_lbMobile";
	public static final String MNST_CANDI_MAIL_ID = "ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolder1_ctl00_controlDetailTop_linkEmail";
	public static final String MNST_CANDI_UPDATED_DATE_ID = "ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolder1_ctl00_resumeDetailTabStrip_controlResumeTab_lblResumeUpdated";
	public static final String MNST_CANDI_RESUME_BODY_ID = "ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolder1_ctl00_resumeDetailTabStrip_controlResumeTab_lblResumeBody";
	public static final String MNST_CANDI_EXPERIENCE_ID = "ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolder1_ctl00_resumeDetailTabStrip_controlCareerTab_lbWorkExperience";
	public static final String MNST_CANDI_SEC_CLEARANCE_ID = "ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolder1_ctl00_resumeDetailTabStrip_controlCareerTab_lbClearance";

	/**
	 * @param response
	 */
	public MonsterDownloader(HttpResponse response) {
		this.response = response;
	}

	public DocumentHolder downloadResume(JsonMonsterResume resume) throws ServiceException {
		/*
		 * // ensure only one active download exists for the same resume Boolean
		 * prevStatus = docsBeingDownloaded.putIfAbsent(resume.getResumeKey(),
		 * true); if (prevStatus != null) { log.debug(
		 * "This resume is already being downloaded"); return null; }
		 */
		Boolean prevStatus = false;
		try {

			prevStatus = docsBeingDownloaded.putIfAbsent(resume.getResumeKey(), true);
		} catch (Exception e) {
			prevStatus = null;
		}
		if (prevStatus != null) {
			log.info("This resume is already being downloaded");
			return null;
		}
		log.info("resume" + resume);
		try {
			String resumeString = createResumeHtml(resume);
			DocumentHolder doc = new DocumentHolder();
			String name = resume.getName();
			if (name != null && !name.trim().equals("")) {
				int index = name.indexOf(" ");
				if (index > 0) {
					doc.setName(name.substring(0, index));
					doc.setLastName(name.substring(index + 1, name.length()));
				} else {
					doc.setName(name);
					doc.setLastName("Monster:Unknown-" + System.currentTimeMillis());
				}
			} else {
				log.info("In downloadResume() method name is null :: " + name);
				doc.setName("");
				doc.setLastName("Monster:Unknown-" + System.currentTimeMillis());
			}
			doc.setEmail(resume.getEmail());
			doc.setPhone((resume.getHomePhone() != null && resume.getHomePhone().trim().length() > 2) ? resume.getHomePhone() : resume.getMobilePhone());
			doc.setLocation(resume.getAddress());
			doc.setContentType(ContentType.HTML);
			doc.setData(resumeString.getBytes());
			log.debug("Resume downloaded successfully: " + doc.getLastName());
			return doc;
		} catch (Exception e) {
			throw new ServiceException("Error parsing resume from monster", e);
		} finally {
			// remove the download lock for this file
			if (resume.getResumeKey() != null && !resume.getResumeKey().trim().equals("")) {
				log.info("In finally of downloadResume() method");
				docsBeingDownloaded.remove(resume.getResumeKey());
			}
		}
	}

	/**
	 * @return
	 */
	public JsonMonsterResume getParametersFromResponse() {
		try {

			ChannelBuffer resBuffer = response.getContent().copy();

			byte[] resBytes = new byte[resBuffer.readableBytes()];
			if (Utils.isEmpty(resBytes)) {
				log.warn("JSON chain is empty");
				return null;
			}

			resBuffer.readBytes(resBytes);
			String resumeJSONChain = new String(resBytes);

			log.info("resumeJSONChain" + resumeJSONChain);
			log.debug("JSON chain obtained: " + resumeJSONChain.length() + " bytes");

			if (resumeJSONChain.contains(Constants.DOC_TYPE)) {
				JsonMonsterResume resume = new JsonMonsterResume();
				resume.setName(Utils.grabResumeDataFromHtmlString(resumeJSONChain, MNST_CANDINAME_ID));
				resume.setMostRecentJobTitle(Utils.grabResumeDataFromHtmlString(resumeJSONChain, MNST_CANDI_TITLE_ID));
				String address = Utils.grabResumeDataFromHtmlString(resumeJSONChain, MNST_CANDI_ADDR_ID);
				if (address != null && address.length() > 0) {
					resume.setAddress(address.replace("<br />", ""));
					if (address.split("<br />").length > 0) {
						resume.setState(address.split("<br />")[address.split("<br />").length - 1].trim());
					}
				}
				resume.setHomePhone(Utils.grabResumeDataFromHtmlString(resumeJSONChain, MNST_CANDI_HOME_PHONE_ID));
				resume.setMobilePhone(Utils.grabResumeDataFromHtmlString(resumeJSONChain, MNST_CANDI_MOBILE_ID));
				resume.setEmail(Utils.grabResumeDataFromHtmlString(resumeJSONChain, MNST_CANDI_MAIL_ID));
				resume.setExperience(Utils.grabResumeDataFromHtmlString(resumeJSONChain, MNST_CANDI_EXPERIENCE_ID));
				resume.setResumeBody(Utils.grabResumeDataFromHtmlString(resumeJSONChain, MNST_CANDI_RESUME_BODY_ID));
				resume.setSecurityClearance(
						Utils.convertStringToBooleanValue(Utils.grabResumeDataFromHtmlString(resumeJSONChain, MNST_CANDI_SEC_CLEARANCE_ID)));
				resume.setExperience(Utils.grabResumeDataFromHtmlString(resumeJSONChain, MNST_CANDI_EXPERIENCE_ID));
				resume.setDateModified(Utils.grabResumeDataFromHtmlString(resumeJSONChain, MNST_CANDI_EXPERIENCE_ID));
				return resume;
			} else {
				return JsonHelper.createMonsterResume(resumeJSONChain);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.proxy.AbstractDownloader#call()
	 */
	@Override
	public DocumentHolder call() throws Exception {
		log.debug("Processing started");

		DocumentHolder doc = null;
		try {
			JsonMonsterResume resume = getParametersFromResponse();
			if (resume == null) {
				log.info("No resume data found, skipping");
				return doc;
			}

			doc = downloadResume(resume);

			Candidate candidate = new Candidate();
			candidate.setFirstName(doc.getName());
			candidate.setLastName(doc.getLastName());
			candidate.getAddress().setCity(resume.getCity());
			candidate.getAddress().setState(resume.getState());
			candidate.getAddress().setZipcode(resume.getZipcode());
			candidate.getAddress().setStreet1(resume.getAddress());
			candidate.setSecurityClearance(resume.getSecurityClearance() != null ? resume.getSecurityClearance() : false);

			candidate.setEmail(doc.getEmail());

			candidate.setPhone(doc.getPhone());
			candidate.addProperty("source", "monster.com");
			candidate.addProperty("externalId", resume.getResumeKey());
			candidate.setDocument(doc.getData(), doc.getContentType());
			candidate.parseDocument();
			candidate.setCreatedUser("Monster");
			candidate.setStatus(CandidateStatus.Available);

			candidate.setDeleteFlag(0);
			candidate.setPortalEmail("");
			candidate.setCreatedOn(new Date());
			candidate.setUpdatedOn(new Date());
			candidate.setTitle(resume.getResumeTitle());
			// candidate.setPortalViewedBy(UserInfoBean.getCurrentUserName());
			candidate.setPortalViewedBy("Monster Proxy");
			candidate.setPortalResumeExperience(resume.getExperience());
			candidate.setVisaType(Constants.NOT_AVAILABLE);
			log.info("visa" + resume.getVisaType());
			// String emailId = doc.getEmail();
			// if(!emailId.equals("Dice:Unknown")){

			// code commented to allow saving the candidate even the email id
			// already exists in ATS

			/*
			 * List<Candidate> existEmail =
			 * getService().getCandidateDetails(emailId); if(existEmail.size() >
			 * 0){ log.debug("Monster Email Exists: " + emailId); log.error(
			 * "Monster Candidate already exists"); } else
			 */ {
				log.info("Downloaded Resume from Monster at line no:167" + candidate.getEmail());
				// fetching email, portal viewed and portal Id by if email is
				// not null
				if (candidate.getEmail() != null && !candidate.getEmail().trim().equals("")) {
					log.info("monster candidate email contains users.monster.com ::" + candidate.getEmail().contains("@users.monster.com"));
					if (candidate.getEmail().contains("@users.monster.com")) {
						candidate.setEmail("Monster:Unknown");
					}
					getService().saveCandidate(candidate);
					saveCandidateStatus(candidate);
				} else {
					log.info("Resume  downloaded with missing mail Id, viewed by:" + candidate.getPortalViewedBy() + "downloaded on:"
							+ candidate.getCreatedOn());
					candidate.setEmail("Monster:Unknown");
					getService().saveCandidate(candidate);
					saveCandidateStatus(candidate);

				}
			}
			// }
			// else{
			// log.info("Downloaded Resume from Monster at line
			// no:172"+candidate.getEmail());
			// getService().saveCandidate(candidate);
			// }
		} catch (ServiceException e) {
			log.error("Error saving Monster candidate resume", e.getMessage(), e);
		} catch (Exception e) {
			log.error("Error downloading Monster candidate resume", e.getMessage(), e);
		}
		return doc;
	}

	private void saveCandidateStatus(Candidate candidate) {
		CandidateStatuses status = new CandidateStatuses();
		status.setCandidate(candidate);
		status.setStatus(candidate.getStatus());
		status.setReason(candidate.getReason());
		status.setCreatedDate(new Date());
		getService().updateCandidateStatus(status);
	}

	/**
	 * @param resume
	 * @return
	 */
	private String createResumeHtml(JsonMonsterResume resume) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("<table border=\"0\" style=\"FONT-SIZE: 11pt; FONT-FAMILY: 'Arial Narrow','sans-serif'\">");
		sb.append("<tr>");
		sb.append(createHtmlTableRow("Name: ", resume.getName()));
		sb.append(createHtmlTableRow("Adress: ", resume.getAddress()));
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append(createHtmlTableRow("Company: ", resume.getCompany()));
		sb.append(createHtmlTableRow("Most Recent Job Title: ", resume.getMostRecentJobTitle()));
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append(createHtmlTableRow("Home phone: ", resume.getHomePhone()));
		sb.append(createHtmlTableRow("Mobile phone: ", resume.getMobilePhone()));
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append(createHtmlTableRow("Email: ", resume.getEmail()));
		sb.append(createHtmlTableRow("Gender: ", resume.getGender()));
		sb.append("</tr>");
		sb.append("</table>").append("<br><br>");
		if (!resume.getSkills().isEmpty()) {
			sb.append("<span style=\"FONT-SIZE: 11pt; FONT-FAMILY: 'Arial Narrow','sans-serif'\"> SKILLS </span>");
			sb.append("<table border=\"0\" style=\"FONT-SIZE: 11pt; FONT-FAMILY: 'Arial Narrow','sans-serif'\">");
			sb.append("<tr><th>&nbsp</th><th>Skill</th><th>Proficiency</th></tr>");
			for (MonsterSkill skill : resume.getSkills()) {
				sb.append("<tr>");
				sb.append(createHtmlTableRow(skill.getName(), skill.getProficiency()));
				sb.append("</tr>");
			}
			sb.append("</table>").append("<br><br>");
		}
		if (!resume.getLanguages().isEmpty()) {
			sb.append("<span style=\"FONT-SIZE: 11pt; FONT-FAMILY: 'Arial Narrow','sans-serif'\"> LANGUAGES </span>");
			sb.append("<table border=\"0\" style=\"FONT-SIZE: 11pt; FONT-FAMILY: 'Arial Narrow','sans-serif'\">");
			sb.append("<tr><th>&nbsp</th><th>Language</th><th>Proficiency</th></tr>");
			for (MonsterLanguage language : resume.getLanguages()) {
				sb.append("<tr>");
				sb.append(createHtmlTableRow(language.getName(), language.getProficiency()));
				sb.append("</tr>");
			}
			sb.append("</table>").append("<br><br>");
		}
		sb.append("<span style=\"FONT-SIZE: 11pt; FONT-FAMILY: 'Arial Narrow','sans-serif'\">");
		sb.append(resume.getResumeTitle()).append("</span>");
		sb.append("<span style=\"FONT-SIZE: 11pt; FONT-FAMILY: 'Arial Narrow','sans-serif'\">Date Modified: ");
		sb.append(resume.getDateModified()).append("</span>");
		sb.append(resume.getResumeBody());
		sb.append("</html>");
		return sb.toString();
	}

	/**
	 * @param fieldName
	 * @param fieldValue
	 * @return
	 */
	private String createHtmlTableRow(String fieldName, String fieldValue) {
		StringBuffer sb = new StringBuffer();
		sb.append("<td> &nbsp </td>");
		sb.append("<td>").append(fieldName).append("</td>");
		sb.append("<td>").append(fieldValue).append("</td>");
		sb.append("<td> &nbsp </td>");
		return sb.toString();
	}
}
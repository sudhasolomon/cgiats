package com.uralian.cgiats.proxy;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferIndexFinder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sun.xml.bind.v2.runtime.reflect.opt.Const;
import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.CandidateStatus;
import com.uralian.cgiats.model.CandidateStatuses;
import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.Constants;
import com.uralian.cgiats.util.Utils;
import com.uralian.cgiats.util.WorkStatusEnum;

/**
 * 
 * Class that downloads the resume from Tech Fetch, by parsing the original
 * response and getting the needed data from there. It implements Callable
 * interface so that the download can be launched in a different thread.
 * 
 * @author skurapati
 */
public class TechFetchDownloader extends AbstractDownloader {
	/*
	 * private static final String NAME_STRING = "Name:"; private static final
	 * String PHONE_STRING = "Phone:"; // private static final String
	 * EMAIL_STRING = "Email:"; private static final String LOCATION_STRING =
	 * "Location:"; private static final String WORKSTATUS_STRING =
	 * "Work Status:"; private static final String LASTMODIFIED_STRING =
	 * "Last Modified:"; private static final String RECENTJOBTITLE_STRING =
	 * "Recent Job Title:"; private static final String
	 * TOTALYEARSOFEXPERIENCE_STRING = "Total years experience:"; private static
	 * final String SECURITYCLEARANCE_STRING = "Security Clearance:"; private
	 * static final String LANGUAGES_SPOKEN_STRING = "Languages Spoken:";
	 * 
	 * private static final String YES = "Yes"; private static final String
	 * YEARS = "years"; private static final String MONTHS = "months"; private
	 * static final String US = "US"; private static final String EMAIL_ID =
	 * "EmailLink"; private static final String RESUME_ID = "resume"; private
	 * static final String ADDITIONALSKILLSID = "additionalskills";
	 * 
	 * private static final ChannelBufferIndexFinder MXResumeWordDoc_DID = new
	 * ByteArrayIndexFinder("MXResumeWordDoc_DID"); private static final
	 * ChannelBufferIndexFinder Resume_DID = new
	 * ByteArrayIndexFinder("Resume_DID"); private static final
	 * ChannelBufferIndexFinder Name = new ByteArrayIndexFinder(NAME_STRING);
	 * private static final ChannelBufferIndexFinder TR = new
	 * ByteArrayIndexFinder("</tr>"); private static final
	 * ChannelBufferIndexFinder Phone = new ByteArrayIndexFinder(PHONE_STRING);
	 * // private static final ChannelBufferIndexFinder Email = new //
	 * ByteArrayIndexFinder(EMAIL_STRING); private static final
	 * ChannelBufferIndexFinder Location = new
	 * ByteArrayIndexFinder(LOCATION_STRING); private static final
	 * ChannelBufferIndexFinder WORKSTATUS = new
	 * ByteArrayIndexFinder(WORKSTATUS_STRING); private static final
	 * ChannelBufferIndexFinder LASTMODIFIED = new
	 * ByteArrayIndexFinder(LASTMODIFIED_STRING); private static final
	 * ChannelBufferIndexFinder RECENTJOBTITLE = new
	 * ByteArrayIndexFinder(RECENTJOBTITLE_STRING); private static final
	 * ChannelBufferIndexFinder TOTALYEARSOFEXPERIENCE = new
	 * ByteArrayIndexFinder(TOTALYEARSOFEXPERIENCE_STRING); private static final
	 * ChannelBufferIndexFinder SECURITYCLEARANCE = new
	 * ByteArrayIndexFinder(SECURITYCLEARANCE_STRING); private static final
	 * ChannelBufferIndexFinder LANGUAGES_SPOKEN = new
	 * ByteArrayIndexFinder(LANGUAGES_SPOKEN_STRING);
	 */

	private static Pattern htmlPattern = Pattern.compile("<[A-Za-z0-9 \\t\\r\\n\\v\\f\\]\\[!\"#$%&'()*+,.:;=?@\\^_`{|}/~-]*>");
	private static Pattern blanksPattern = Pattern.compile("[\\t\\r\\n\\v\\f]*");

	public final static String RESUME_KEY = "RESUME_KEY";
	public final static String RESUME_WORD_KEY = "RESUME_WORD_KEY";
	public final String RESUME_DATA_KEY = "RESUME_DATA_KEY";

	private ConcurrentMap<String, Boolean> docsBeingDownloaded = new ConcurrentHashMap<String, Boolean>();
	private Hashtable<String, Object> parameters;

	private HttpResponse response;

	public TechFetchDownloader() {
	}

	/**
	 * @param response
	 */
	public TechFetchDownloader(HttpResponse response) {
		this.response = response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.proxy.AbstractDownloader#downloadResume()
	 */
	// @Override
	public DocumentHolder downloadResume() throws ServiceException {

		log.info("From Down Load Resume");
		if (log.isInfoEnabled())
			log.info("Starting downloading process");

		if (parameters == null) {
			getParametersFromResponse();
		}
		String resumeKey = (String) parameters.get(RESUME_KEY);
		String wordKey = (String) parameters.get(RESUME_WORD_KEY);

		if (resumeKey == null) {
			if (log.isErrorEnabled())
				log.error("Can't download the document, Resume_DID is null");
			return null;
		}

		if (wordKey == null) {
			if (log.isErrorEnabled())
				log.error("Can't download the document, MXResumeWordDoc_DID is null");
			return null;
		}

		// ensure only one active download exists for the same resume
		Boolean prevStatus = docsBeingDownloaded.putIfAbsent(resumeKey, true);
		if (prevStatus != null)
			return null;

		HttpClient client = null;
		try {
			// Create & initialise the client
			client = new DefaultHttpClient();
			log.info("From Down Load Resume End");
			// Setup the request
			HttpGet get = createDownloadRequest(wordKey, resumeKey);
			HttpContext localContext = new BasicHttpContext();
			org.apache.http.HttpResponse newResp = null;

			// Execute the request to download
			newResp = client.execute(get, localContext);
			if (newResp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				throw new ServiceException("Bad response status: " + newResp.getStatusLine().toString());
			}

			// extract the document
			byte[] data = extractDocument(newResp);
			DocumentHolder doc = (DocumentHolder) parameters.get(RESUME_DATA_KEY);
			doc.setContentType(getFileTypeFromResponse(newResp));
			doc.setData(data);
			log.info("Resume download complete");

			return doc;
		} catch (ClientProtocolException e) {
			throw new ServiceException("HTTP error executing the download request for " + resumeKey + " and " + wordKey, e);
		} catch (IOException e) {
			throw new ServiceException("I/O error executing the download request for " + resumeKey + " and " + wordKey, e);
		} catch (ServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ServiceException("Unexpected error while downloading resume for " + resumeKey + " and " + wordKey, e);
		} finally {
			// remove the download lock for this file
			docsBeingDownloaded.remove(resumeKey);
			// close resources
			if (client != null)
				client.getConnectionManager().shutdown();
		}

	}

	/**
	 * @return
	 */
	public Hashtable<String, Object> getParametersFromResponse() {
		parameters = new Hashtable<String, Object>(1);
		// 206031
		// byte[] myArray = new byte[response.getContent().capacity()-57000];
		// response.getContent().readBytes(myArray);
		// String htmlCode = new String(myArray);
		// System.out.println(htmlCode);
		//
		// try {
		// File newTextFile = new File("D:/thetextfile.txt");
		//
		// FileWriter fw = new FileWriter(newTextFile);
		// fw.write(htmlCode);
		// fw.close();
		//
		// } catch (IOException iox) {
		// //do stuff with exception
		// iox.printStackTrace();
		// }

		ChannelBuffer resBuffer = response.getContent().copy();

		byte[] resBytes = new byte[resBuffer.readableBytes()];
		if (Utils.isEmpty(resBytes)) {
			log.warn("JSON chain is empty");
			return null;
		}

		resBuffer.readBytes(resBytes);
		String strCandidateData = new String(resBytes);

		if (strCandidateData != null && strCandidateData.length() > 0) {
			DocumentHolder document = new DocumentHolder();
			document.setEmail(Constants.Tech_UNKNOWN_DEFALUT);
			// Getting Name
			String name = Utils.grabResumeDataFromHtmlString(strCandidateData, Constants.Tech_NAME_ID);
			if (name != null && name.length() > 0) {
				// If confidential
				if (name.toLowerCase().contains(Constants.Tech_CONFIDENTIAL.toLowerCase())) {
					document.setName(Constants.Tech_CONFIDENTIAL);
				} else {
					String[] names = name.split(Constants.NBSP);
					if (names != null && names.length > 0) {
						String[] fullNames = names[0].split(" ");
						if (fullNames != null && fullNames.length > 1) {
							document.setName(fullNames[0]);
							document.setLastName(fullNames[1]);
						} else {
							document.setName(names[0]);
						}

						if (names.length > 1) {
							String workAuthorization = names[1].replace("(", "").replace(")", "");
							if (workAuthorization != null && workAuthorization.length() > 0) {
								if (workAuthorization.trim().toLowerCase().contains(Constants.WA_US_CITIZEN.trim().toLowerCase())) {
									document.setWorkStatus(Constants.WA_US_CITIZEN);
								} else if (workAuthorization.trim().toLowerCase().contains(Constants.WA_GREENCARD.trim().toLowerCase())) {
									document.setWorkStatus(Constants.WA_GREENCARD);
								} else if (workAuthorization.trim().toLowerCase().contains(Constants.WA_H1B.trim().toLowerCase())) {
									document.setWorkStatus(Constants.WA_H1B_VISA);
								} else if (workAuthorization.trim().toLowerCase().contains(Constants.WA_OPT.trim().toLowerCase())) {
									document.setWorkStatus(Constants.WA_OPT);
								} else if (workAuthorization.trim().toLowerCase().contains(Constants.WA_CPT.trim().toLowerCase())) {
									document.setWorkStatus(Constants.WA_CPT);
								} else if (workAuthorization.trim().toLowerCase().contains(Constants.WA_L2.trim().toLowerCase())) {
									document.setWorkStatus(Constants.WA_L2_VISA);
								} else if (workAuthorization.trim().toLowerCase().contains(Constants.WA_H4.trim().toLowerCase())) {
									document.setWorkStatus(Constants.WA_H4_VISA);
								} else if (workAuthorization.trim().toLowerCase().contains(Constants.WA_TN.trim().toLowerCase())) {
									document.setWorkStatus(Constants.WA_TN_VISA);
								} else if (workAuthorization.trim().toLowerCase().contains(Constants.WA_SEC_CLEARANCE.trim().toLowerCase())) {
									document.setWorkStatus(Constants.NOT_AVAILABLE);
									document.setSecurityClearance(Constants.YES);
								} else {
									document.setWorkStatus(Constants.NOT_AVAILABLE);
								}

							}
						}
					}
				}
			}
			// Getting experience
			String experience = Utils.grabResumeDataFromHtmlString(strCandidateData, Constants.Tech_TOTALEX_ID);
			document.setTotalYearsOfExperience(experience);
			// Getting location
			String location = Utils.grabResumeDataFromHtmlString(strCandidateData, Constants.Tech_PLACE_ID);
			document.setLocation(location);
			if (location != null && location.length() > 0) {
				String locations[] = location.split(",");
				if (locations != null && locations.length > 1) {
					document.setCity(locations[0].trim());
					document.setState(locations[1].trim());
				}
			}

			// Getting Job Title
			String recentJobTitle = Utils.grabResumeDataFromHtmlString(strCandidateData, Constants.Tech_JOBTITLE_ID);
			document.setRecentJobTitle(recentJobTitle);

			// Getting Phone number
			String phoneNumber = Utils.grabResumeDataFromHtmlString(strCandidateData, Constants.Tech_PHONEC_ID);
			document.setPhone(phoneNumber);
			// Getting key skills
			String keyskills = Utils.grabResumeDataFromHtmlString(strCandidateData, Constants.Tech_SKILLS_ID);
			if (keyskills != null && keyskills.length() > 0) {
				keyskills = keyskills.replace("<span class=\"highlight\">", "").replace("</span>", "");
				document.setKeySkills(keyskills);
			}
			// Getting Resume
			String resumeData = Utils.grabResumeDataFromHtmlString(strCandidateData, Constants.Tech_RESUMETEXT_ID);
			document.setContentType(ContentType.HTML);
			if (resumeData != null)
				document.setData(resumeData.getBytes());

			parameters.put(RESUME_DATA_KEY, document);

		}

		// Set prameters for resume IDs

		if (log.isInfoEnabled())
			log.info("Successfully formed candidate from the data that could be acquired from site ");
		return parameters;
	}

	@Override
	public DocumentHolder call() throws Exception {

		log.info("From Document Holder Call");

		DocumentHolder doc = null;
		try {
			// if (parameters == null) {
			getParametersFromResponse();
			// }
			doc = (DocumentHolder) parameters.get(RESUME_DATA_KEY);
			// String resumeKey = (String)
			// parameters.get(TechFetchDownloader.RESUME_KEY);
			// String wordKey = (String)
			// parameters.get(CareerBuilderDownloader.RESUME_WORD_KEY);

			// doc = this.downloadResume();
			if (doc != null && doc.getData() != null) {
				Candidate candidate = new Candidate();
				candidate.setFirstName(doc.getName());
				candidate.setLastName(doc.getLastName());
				candidate.getAddress().setCity(doc.getCity());
				candidate.getAddress().setState(doc.getState());
				candidate.getAddress().setZipcode(doc.getZipcode());
				candidate.getAddress().setStreet1(doc.getLocation());
				candidate.setVisaType(doc.getWorkStatus());
				candidate.setEmail(doc.getEmail());
				candidate.setPhone(doc.getPhone());
				candidate.addProperty("source", "techfetch.com");
				// candidate.addProperty("externalId", resumeKey);
				// candidate.addProperty("externalId2", wordKey);
				candidate.setStatus(CandidateStatus.Available);
				candidate.setTitle(doc.getRecentJobTitle());
				candidate.setPortalResumeExperience(doc.getTotalYearsOfExperience());
				candidate.setPortalResumeLastUpd(doc.getLastModified());

				candidate.setDocument(doc.getData(), doc.getContentType());
				candidate.parseDocument();
				System.out.println("after doc parsing");
				candidate.setCreatedUser(Constants.TECHFETCH_OLD);
				candidate.setDeleteFlag(0);
				candidate.setPortalEmail("");
				// candidate.setPortalViewedBy(UserInfoBean.getCurrentUserName());
				candidate.setPortalViewedBy("TF Proxy");
				candidate.setCreatedOn(new Date());
				candidate.setUpdatedOn(new Date());
				candidate.setKeySkill(doc.getKeySkills());

				// if (doc.getSecurityClearance() != null &&
				// doc.getSecurityClearance().equalsIgnoreCase(YES)) {
				// candidate.setSecurityClearance(true);
				// } else {
				// candidate.setSecurityClearance(false);
				// }

				this.getService().saveCandidate(candidate);
				saveCandidateStatus(candidate);
			} else {
				log.info("call() returns value doc as ::" + doc);
			}
		} /*
			 * catch (ServiceException e) { if (log.isErrorEnabled()) {
			 * log.error(
			 * "Service error downloading and processing the CB candidate resume"
			 * , e); } }
			 */ catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Error downloading and processing the CB candidate resume", e);
			}
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
	 * @param wordKey
	 * @param resumeKey
	 * @return
	 */
	private HttpGet createDownloadRequest(String wordKey, String resumeKey) {
		log.info("From Create DownLoad Request");

		HttpGet get = new HttpGet("http://www.careerbuilder.com/JobPoster/Resumes/ResumeWordDocSave.aspx?V2=1&" + wordKey + "&" + resumeKey);
		get.addHeader(CGIATSConstants.HOST_KEY, CareerBuilderResponseFilter.HOST_VALUE);
		get.addHeader(CGIATSConstants.USER_AGENT_KEY, CareerBuilderResponseFilter.USER_AGENT_VALUE);
		get.addHeader(CGIATSConstants.ACCEPT_KEY, CareerBuilderResponseFilter.ACCEPT_VALUE);
		get.addHeader(CGIATSConstants.ACCEPT_LANGUAGE_KEY, CareerBuilderResponseFilter.ACCEPT_LANGUAGE_VALUE);
		get.addHeader(CGIATSConstants.ACCEPT_ENCODING_KEY, CareerBuilderResponseFilter.ACCEPT_ENCODING_VALUE);
		get.addHeader(CGIATSConstants.CONNECTION_KEY, CareerBuilderResponseFilter.CONNECTION_VALUE);
		get.addHeader(CGIATSConstants.COOKIE_KEY, CareerBuilderResponseFilter.COOKIE_VALUE);
		log.info("End Download");
		return get;
	}

//	private String grabResumeDataFromHtmlString(String htmlData, String elementId) {
//		try {
//			Document htmlDocument = Jsoup.parse(htmlData);
//			Element resumeContent = htmlDocument.getElementById(elementId);
//			return resumeContent.html();
//		} catch (Exception e) {
//			e.getMessage();
//		}
//		return null;
//
//	}

	private String getTitle(String htmlData) {
		try {
			if (htmlData != null && htmlData.trim().length() > 0) {
				Document htmlDocument = Jsoup.parse(htmlData);
				Elements titleTableDataEles = htmlDocument.getElementsByTag("table");
				Element titleTableData = titleTableDataEles.get(0);
				Elements titleTableTrsData = titleTableData.getElementsByTag("tr");
				Element titleTr = titleTableTrsData.get(0);
				Elements titleTds = titleTr.getElementsByTag("td");
				return titleTds.get(1).html();
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return null;

	}

	/**
	 * @param resume
	 * @return
	 */
	private String createResumeHtml(DocumentHolder resume, String resumeData) {
		StringBuffer sb = new StringBuffer();
		sb.append("<html>");
		sb.append("<table border=\"0\" style=\"FONT-SIZE: 11pt; FONT-FAMILY: 'Arial Narrow','sans-serif'\">");
		sb.append("<tr>");
		sb.append(createHtmlTableRow("Name: ", resume.getName()));
		sb.append(createHtmlTableRow("Adress: ", resume.getLocation()));
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append(createHtmlTableRow("Company: ", "-"));
		sb.append(createHtmlTableRow("Most Recent Job Title: ", resume.getRecentJobTitle()));
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append(createHtmlTableRow("Home phone: ", resume.getPhone()));
		sb.append(createHtmlTableRow("Mobile phone: ", resume.getPhone()));
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append(createHtmlTableRow("Email: ", resume.getEmail()));
		sb.append(createHtmlTableRow("Gender: ", "-"));
		sb.append("</tr>");
		sb.append("</table>").append("<br><br>");
		sb.append("<span style=\"FONT-SIZE: 11pt; FONT-FAMILY: 'Arial Narrow','sans-serif'\">");
		sb.append(resume.getRecentJobTitle()).append("</span>");
		sb.append("<span style=\"FONT-SIZE: 11pt; FONT-FAMILY: 'Arial Narrow','sans-serif'\">Date Modified: ");
		sb.append(resume.getLastModified()).append("</span>");
		sb.append(resumeData);
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
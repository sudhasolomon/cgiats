package com.uralian.cgiats.proxy;

import java.io.File;
import java.io.FileWriter;
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
 * Class that downloads the resume from career builder, by parsing the original
 * response and getting the needed data from there. It implements Callable
 * interface so that the download can be launched in a different thread.
 * 
 * @author jbernalm
 */
public class CareerBuilderDownloader extends AbstractDownloader {
	private static final String NAME_STRING = "Name:";
	private static final String PHONE_STRING = "Phone:";
	// private static final String EMAIL_STRING = "Email:";
	private static final String LOCATION_STRING = "Location:";
	private static final String WORKSTATUS_STRING = "Work Status:";
	private static final String LASTMODIFIED_STRING = "Last Modified:";
	private static final String RECENTJOBTITLE_STRING = "Recent Job Title:";
	private static final String TOTALYEARSOFEXPERIENCE_STRING = "Total years experience:";
	private static final String SECURITYCLEARANCE_STRING = "Security Clearance:";
	private static final String LANGUAGES_SPOKEN_STRING = "Languages Spoken:";

	private static final String YES = "Yes";
	private static final String YEARS = "years";
	private static final String MONTHS = "months";
	private static final String US = "US";
	private static final String EMAIL_ID = "EmailLink";
	private static final String RESUME_ID = "resume";
	private static final String ADDITIONALSKILLSID = "additionalskills";

	private static final ChannelBufferIndexFinder MXResumeWordDoc_DID = new ByteArrayIndexFinder("MXResumeWordDoc_DID");
	private static final ChannelBufferIndexFinder Resume_DID = new ByteArrayIndexFinder("Resume_DID");
	private static final ChannelBufferIndexFinder Name = new ByteArrayIndexFinder(NAME_STRING);
	private static final ChannelBufferIndexFinder TR = new ByteArrayIndexFinder("</tr>");
	private static final ChannelBufferIndexFinder Phone = new ByteArrayIndexFinder(PHONE_STRING);
	// private static final ChannelBufferIndexFinder Email = new
	// ByteArrayIndexFinder(EMAIL_STRING);
	private static final ChannelBufferIndexFinder Location = new ByteArrayIndexFinder(LOCATION_STRING);
	private static final ChannelBufferIndexFinder WORKSTATUS = new ByteArrayIndexFinder(WORKSTATUS_STRING);
	private static final ChannelBufferIndexFinder LASTMODIFIED = new ByteArrayIndexFinder(LASTMODIFIED_STRING);
	private static final ChannelBufferIndexFinder RECENTJOBTITLE = new ByteArrayIndexFinder(RECENTJOBTITLE_STRING);
	private static final ChannelBufferIndexFinder TOTALYEARSOFEXPERIENCE = new ByteArrayIndexFinder(TOTALYEARSOFEXPERIENCE_STRING);
	private static final ChannelBufferIndexFinder SECURITYCLEARANCE = new ByteArrayIndexFinder(SECURITYCLEARANCE_STRING);
	private static final ChannelBufferIndexFinder LANGUAGES_SPOKEN = new ByteArrayIndexFinder(LANGUAGES_SPOKEN_STRING);

	private static Pattern htmlPattern = Pattern.compile("<[A-Za-z0-9 \\t\\r\\n\\v\\f\\]\\[!\"#$%&'()*+,.:;=?@\\^_`{|}/~-]*>");
	private static Pattern blanksPattern = Pattern.compile("[\\t\\r\\n\\v\\f]*");

	public final static String RESUME_KEY = "RESUME_KEY";
	public final static String RESUME_WORD_KEY = "RESUME_WORD_KEY";
	public final String RESUME_DATA_KEY = "RESUME_DATA_KEY";

	private ConcurrentMap<String, Boolean> docsBeingDownloaded = new ConcurrentHashMap<String, Boolean>();
	private Hashtable<String, Object> parameters;

	private HttpResponse response;

	public CareerBuilderDownloader() {
	}

	/**
	 * @param response
	 */
	public CareerBuilderDownloader(HttpResponse response) {
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

		try {
			File newTextFile = new File("D:/theNewtextfile.txt");

			FileWriter fw = new FileWriter(newTextFile);
			fw.write(strCandidateData);
			fw.close();

		} catch (IOException iox) {
			// do stuff with exception
			iox.printStackTrace();
		}

		// Get the value of Resume_DID
		int startIndex = response.getContent().bytesBefore(Resume_DID);
		if (startIndex < 0)
			return parameters;
		int length = response.getContent().bytesBefore(startIndex, 50, (byte) 59);
		if (length < 0 || length >= 50)
			return parameters;

		ChannelBuffer copyKeyBuffer = response.getContent().copy(startIndex, length);
		byte[] responseBytes = new byte[copyKeyBuffer.readableBytes()];
		copyKeyBuffer.readBytes(responseBytes);
		String resumeKey = new String(responseBytes);
		if (log.isDebugEnabled())
			log.debug("Raw Resume_DID from response " + resumeKey);

		resumeKey = resumeKey.replaceAll("[\\]\' \t]", "");
		if (log.isInfoEnabled())
			log.info("Parsed Resume_DID " + resumeKey);

		// Get the value of MXResumeWordDoc_DID
		/*
		 * startIndex = response.getContent().bytesBefore(MXResumeWordDoc_DID);
		 * if (startIndex < 0) return parameters; length =
		 * response.getContent().bytesBefore(startIndex, 50, (byte) 38); if
		 * (length < 0 || length >= 50) return parameters;
		 * 
		 * copyKeyBuffer = response.getContent().copy(startIndex, length);
		 * responseBytes = new byte[copyKeyBuffer.readableBytes()];
		 * copyKeyBuffer.readBytes(responseBytes); String wordKey = new
		 * String(responseBytes);
		 */
		/*
		 * if (log.isInfoEnabled()) log.info("Parsed MXResumeWordDoc_DID " +
		 * wordKey);
		 */

		// Set prameters for resume IDs
		parameters.put(RESUME_KEY, resumeKey);
		// parameters.put(RESUME_WORD_KEY, wordKey);

		DocumentHolder document = new DocumentHolder();
		// Get the Name, need to find the 2nd ocurrence of Name: since response
		// contains that string previous to the candidate name.
		/*
		 * startIndex = response.getContent().bytesBefore(Name); //
		 * tempStartIndex += NAME_STRING.getBytes().length; // startIndex =
		 * response.getContent().bytesBefore(tempStartIndex,
		 * response.getContent().capacity() - tempStartIndex, Name); //
		 * startIndex = startIndex == -1 ? tempStartIndex : startIndex +
		 * tempStartIndex; if (startIndex < 0) { document.setName("CB:Unknown");
		 * document.setLastName("CB:Unknown-" + System.currentTimeMillis()); }
		 * else { startIndex += NAME_STRING.getBytes().length; length =
		 * response.getContent().bytesBefore(startIndex, 500, TR); if (length <
		 * 0 || length >= 500) { document.setName("CB:Unknown");
		 * document.setLastName("CB:Unknown-" + System.currentTimeMillis()); } }
		 * if (document.getName() == null) { copyKeyBuffer =
		 * response.getContent().copy(startIndex, length); responseBytes = new
		 * byte[copyKeyBuffer.readableBytes()];
		 * copyKeyBuffer.readBytes(responseBytes); String name = new
		 * String(responseBytes); name =
		 * htmlPattern.matcher(name).replaceAll(""); name =
		 * blanksPattern.matcher(name).replaceAll("").trim(); int index =
		 * name.indexOf(" "); if (index > 0) {
		 * document.setName(name.substring(0, index));
		 * document.setLastName(name.substring(index + 1, name.length())); }
		 * else { document.setName(name); document.setLastName("CB:Unknown-" +
		 * System.currentTimeMillis()); } }
		 */
		// To get the full Name
		String personalInfoDivId = grabResumeDataFromHtmlString(strCandidateData, Constants.CB_PERSONALINFO_ID);
		if (personalInfoDivId != null && personalInfoDivId.length() > 0) {
			try {
				Document htmlDocument = Jsoup.parse(personalInfoDivId);
				Elements resumeContent = htmlDocument.getElementsByClass(Constants.CB_PERSONALINFO_CLASS);
				if (resumeContent != null && resumeContent.size() > 0) {
					Element personalDetailsTable = resumeContent.get(0);
					Elements trs = personalDetailsTable.getElementsByTag("tr");
					if (trs != null && trs.size() > 0) {
						Element nameTr = trs.get(0);
						Elements nametds = nameTr.getElementsByTag("td");
						if (nametds != null && nametds.size() > 1) {
							Element fullNameTd = nametds.get(1);
							if (fullNameTd != null) {
								Elements fullNameDiv = fullNameTd.getElementsByTag("div");
								if (fullNameDiv != null && fullNameDiv.size() > 0) {
									String fullName = fullNameDiv.get(0).html();

									int index = fullName.indexOf(" ");
									if (index > 0) {
										document.setName(fullName.substring(0, index));
										document.setLastName(fullName.substring(index + 1, fullName.length()));
									} else {
										document.setName(fullName);
										document.setLastName("CB:Unknown-" + System.currentTimeMillis());
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.getMessage();
			}
		}

		// Get the phone
		startIndex = response.getContent().bytesBefore(Phone);
		if (startIndex < 0) {
			document.setPhone("-");
		} else {
			startIndex += PHONE_STRING.getBytes().length;
			length = response.getContent().bytesBefore(startIndex, 500, TR);
			if (length < 0 || length >= 500) {
				document.setPhone("-");
			}
		}
		if (document.getPhone() == null) {
			copyKeyBuffer = response.getContent().copy(startIndex, length);
			responseBytes = new byte[copyKeyBuffer.readableBytes()];
			copyKeyBuffer.readBytes(responseBytes);
			String phone = new String(responseBytes);
			phone = htmlPattern.matcher(phone).replaceAll("");
			phone = blanksPattern.matcher(phone).replaceAll("").trim();
			document.setPhone(phone);
		}
		/*
		 * // Get the email startIndex =
		 * response.getContent().bytesBefore(Email); if (startIndex < 0) {
		 * document.setEmail("CB:Unknown"); } else { startIndex +=
		 * EMAIL_STRING.getBytes().length; length =
		 * response.getContent().bytesBefore(startIndex, 500, TR); if (length <
		 * 0 || length >= 500) { document.setEmail("CB:Unknown"); } } if
		 * (document.getEmail() == null) { copyKeyBuffer =
		 * response.getContent().copy(startIndex, length); responseBytes = new
		 * byte[copyKeyBuffer.readableBytes()];
		 * copyKeyBuffer.readBytes(responseBytes); String email = new
		 * String(responseBytes); email =
		 * htmlPattern.matcher(email).replaceAll(""); email =
		 * blanksPattern.matcher(email).replaceAll("").trim();
		 * document.setEmail(email); }
		 */

		String email = grabResumeDataFromHtmlString(strCandidateData, EMAIL_ID);

		if (email != null) {
			document.setEmail(email);
		} else {
			document.setEmail("CB:Unknown");
		}

		// Get the location
		startIndex = response.getContent().bytesBefore(Location);
		if (startIndex < 0) {
			document.setLocation("CB:Unknown");
		} else {
			startIndex += LOCATION_STRING.getBytes().length;
			length = response.getContent().bytesBefore(startIndex, 500, TR);
			if (length < 0 || length >= 500) {
				document.setLocation("CB:Unknown");
			}
		}
		if (document.getLocation() == null) {
			copyKeyBuffer = response.getContent().copy(startIndex, length);
			responseBytes = new byte[copyKeyBuffer.readableBytes()];
			copyKeyBuffer.readBytes(responseBytes);
			String location = new String(responseBytes);
			location = htmlPattern.matcher(location).replaceAll("");
			location = blanksPattern.matcher(location).replaceAll("").trim();

			if (location != null && location.trim().length() > 0) {
				String[] locations = location.split("-");
				if (locations != null && locations.length > 3) {
					if (locations[0].equalsIgnoreCase(US)) {
						document.setState(locations[1]);
						document.setCity(locations[2]);
						document.setZipcode(locations[3]);
					}
				}
			}

			document.setLocation(location);
		}

		// Get the Work Status
		startIndex = response.getContent().bytesBefore(WORKSTATUS);
		if (startIndex < 0) {
			document.setWorkStatus("CB:Unknown");
		} else {
			startIndex += WORKSTATUS_STRING.getBytes().length;
			length = response.getContent().bytesBefore(startIndex, 500, TR);
			if (length < 0 || length >= 500) {
				document.setWorkStatus("CB:Unknown");
			}
		}
		if (document.getWorkStatus() == null) {
			copyKeyBuffer = response.getContent().copy(startIndex, length);
			responseBytes = new byte[copyKeyBuffer.readableBytes()];
			copyKeyBuffer.readBytes(responseBytes);
			String workStatus = new String(responseBytes);
			workStatus = htmlPattern.matcher(workStatus).replaceAll("");
			workStatus = blanksPattern.matcher(workStatus).replaceAll("").trim();
			if (workStatus != null) {
				for (WorkStatusEnum statusEnum : WorkStatusEnum.values()) {
					if (statusEnum.getCBType().equalsIgnoreCase(workStatus.trim())) {
						workStatus = statusEnum.getDBType();
						break;
					}
				}
			}
			document.setWorkStatus(workStatus);
		}

		// Get the Last Modified
		startIndex = response.getContent().bytesBefore(LASTMODIFIED);
		if (startIndex < 0) {
			document.setLastModified(null);
		} else {
			startIndex += LASTMODIFIED_STRING.getBytes().length;
			length = response.getContent().bytesBefore(startIndex, 500, TR);
			if (length < 0 || length >= 500) {
				document.setLastModified(null);
			}
		}
		if (document.getLastModified() == null) {
			copyKeyBuffer = response.getContent().copy(startIndex, length);
			responseBytes = new byte[copyKeyBuffer.readableBytes()];
			copyKeyBuffer.readBytes(responseBytes);
			String lastModified = new String(responseBytes);
			lastModified = htmlPattern.matcher(lastModified).replaceAll("");
			lastModified = blanksPattern.matcher(lastModified).replaceAll("").trim();
			document.setLastModified(lastModified);
		}
		// Get the job title
		/*
		 * startIndex = response.getContent().bytesBefore(RECENTJOBTITLE); if
		 * (startIndex < 0) { document.setRecentJobTitle("CB:Unknown"); } else {
		 * startIndex += RECENTJOBTITLE_STRING.getBytes().length; length =
		 * response.getContent().bytesBefore(startIndex, 500, TR); if (length <
		 * 0 || length >= 500) { document.setRecentJobTitle("CB:Unknown"); } }
		 * if (document.getRecentJobTitle() == null) { copyKeyBuffer =
		 * response.getContent().copy(startIndex, length); responseBytes = new
		 * byte[copyKeyBuffer.readableBytes()];
		 * copyKeyBuffer.readBytes(responseBytes); String jobTitle = new
		 * String(responseBytes); jobTitle =
		 * htmlPattern.matcher(jobTitle).replaceAll(""); jobTitle =
		 * blanksPattern.matcher(jobTitle).replaceAll("").trim();
		 * document.setRecentJobTitle(jobTitle); }
		 */
		String titleTableData = grabResumeDataFromHtmlString(strCandidateData, ADDITIONALSKILLSID);
		document.setRecentJobTitle(getTitle(titleTableData));

		// Get the Total experience
		startIndex = response.getContent().bytesBefore(TOTALYEARSOFEXPERIENCE);
		if (startIndex < 0) {
			document.setTotalYearsOfExperience("CB:Unknown");
		} else {
			startIndex += TOTALYEARSOFEXPERIENCE_STRING.getBytes().length;
			length = response.getContent().bytesBefore(startIndex, 500, TR);
			if (length < 0 || length >= 500) {
				document.setTotalYearsOfExperience("CB:Unknown");
			}
		}
		if (document.getTotalYearsOfExperience() == null) {
			copyKeyBuffer = response.getContent().copy(startIndex, length);
			responseBytes = new byte[copyKeyBuffer.readableBytes()];
			copyKeyBuffer.readBytes(responseBytes);
			String totalYearsOfExperience = new String(responseBytes);
			totalYearsOfExperience = htmlPattern.matcher(totalYearsOfExperience).replaceAll("");
			totalYearsOfExperience = blanksPattern.matcher(totalYearsOfExperience).replaceAll("").trim();
			if (totalYearsOfExperience != null && totalYearsOfExperience.trim().length() > 0) {
				String expArray[] = totalYearsOfExperience.split(",");
				if (expArray != null && expArray.length > 0) {
					String years = "0";
					String months = "0";
					for (String exp : expArray) {
						if (exp.trim().length() > 0 && exp.trim().toLowerCase().contains(YEARS)) {
							years = exp.trim().split(" ")[0];
						}
						if (exp.trim().length() > 0 && exp.trim().toLowerCase().contains(MONTHS)) {
							months = exp.trim().split(" ")[0];
						}
					}
					document.setTotalYearsOfExperience(years + "." + months);

					// String yearsArray[] = expArray[0].split(" ");
					// String monthsArray[] = expArray[1].split(" ");
					/*
					 * if (yearsArray != null && yearsArray.length > 1 &&
					 * monthsArray != null && monthsArray.length > 1) {
					 * yearWithMonthExp = yearsArray[0] + "." + monthsArray[0];
					 * } if (yearsArray != null && yearsArray.length > 1 &&
					 * monthsArray != null && !(monthsArray.length > 1)) {
					 * yearWithMonthExp = yearsArray[0] + ".0"; } if (yearsArray
					 * != null && !(yearsArray.length > 1) && monthsArray !=
					 * null && (monthsArray.length > 1)) { yearWithMonthExp =
					 * "0." + monthsArray[0]; }
					 */

				} else {
					document.setTotalYearsOfExperience(totalYearsOfExperience);
				}
			}

		}

		// Get the Security Clearance
		startIndex = response.getContent().bytesBefore(SECURITYCLEARANCE);
		if (startIndex < 0) {
			document.setSecurityClearance("CB:Unknown");
		} else {
			startIndex += SECURITYCLEARANCE_STRING.getBytes().length;
			length = response.getContent().bytesBefore(startIndex, 500, TR);
			if (length < 0 || length >= 500) {
				document.setSecurityClearance("CB:Unknown");
			}
		}
		if (document.getSecurityClearance() == null) {
			copyKeyBuffer = response.getContent().copy(startIndex, length);
			responseBytes = new byte[copyKeyBuffer.readableBytes()];
			copyKeyBuffer.readBytes(responseBytes);
			String securityClearance = new String(responseBytes);
			securityClearance = htmlPattern.matcher(securityClearance).replaceAll("");
			securityClearance = blanksPattern.matcher(securityClearance).replaceAll("").trim();
			document.setSecurityClearance(securityClearance);
		}

		// Get the Security Clearance
		startIndex = response.getContent().bytesBefore(LANGUAGES_SPOKEN);
		if (startIndex < 0) {
			document.setLanguagesSpoken("CB:Unknown");
		} else {
			startIndex += LANGUAGES_SPOKEN_STRING.getBytes().length;
			length = response.getContent().bytesBefore(startIndex, 500, TR);
			if (length < 0 || length >= 500) {
				document.setLanguagesSpoken("CB:Unknown");
			}
		}
		if (document.getLanguagesSpoken() == null) {
			copyKeyBuffer = response.getContent().copy(startIndex, length);
			responseBytes = new byte[copyKeyBuffer.readableBytes()];
			copyKeyBuffer.readBytes(responseBytes);
			String languagesSpoken = new String(responseBytes);
			languagesSpoken = htmlPattern.matcher(languagesSpoken).replaceAll("");
			languagesSpoken = blanksPattern.matcher(languagesSpoken).replaceAll("").trim();
			document.setLanguagesSpoken(languagesSpoken);
		}

		String resumeData = grabResumeDataFromHtmlString(strCandidateData, RESUME_ID);

		if (resumeData != null) {
			resumeData = createResumeHtml(document, resumeData);
			document.setContentType(ContentType.HTML);
			document.setData(resumeData.getBytes());
		}

		// Set prameters for resume IDs
		parameters.put(RESUME_DATA_KEY, document);

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
			String resumeKey = (String) parameters.get(CareerBuilderDownloader.RESUME_KEY);
			// String wordKey = (String)
			// parameters.get(CareerBuilderDownloader.RESUME_WORD_KEY);

			// doc = this.downloadResume();
			if (doc != null) {
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
				candidate.addProperty("source", "careerbuilder.com");
				candidate.addProperty("externalId", resumeKey);
				// candidate.addProperty("externalId2", wordKey);
				candidate.setStatus(CandidateStatus.Available);
				candidate.setTitle(doc.getRecentJobTitle());
				candidate.setPortalResumeExperience(doc.getTotalYearsOfExperience());
				candidate.setPortalResumeLastUpd(doc.getLastModified());

				candidate.setDocument(doc.getData(), doc.getContentType());
				candidate.parseDocument();
				System.out.println("after doc parsing");
				candidate.setCreatedUser(Constants.CAREERBUILDER_OLD);
				candidate.setDeleteFlag(0);
				candidate.setPortalEmail("");
				// candidate.setPortalViewedBy(UserInfoBean.getCurrentUserName());
				candidate.setPortalViewedBy("CB Proxy");
				candidate.setCreatedOn(new Date());
				candidate.setUpdatedOn(new Date());

				if (doc.getSecurityClearance() != null && doc.getSecurityClearance().equalsIgnoreCase(YES)) {
					candidate.setSecurityClearance(true);
				} else {
					candidate.setSecurityClearance(false);
				}

				String emailId = doc.getEmail();
				System.out.println("call() 1");
				if (!emailId.equals("CB:Unknown")) {
					System.out.println("call() 1st if 2");

					// code commented to allow inserting the candidate with
					// duplicate emailId

					/*
					 * List<Candidate> existEmail =
					 * getService().getCandidateDetails(emailId); if
					 * (existEmail.size() > 0) { System.out.println(
					 * "call() 2nd if 3"); log.debug(
					 * "Career Builder Email Exists: " + emailId); log.error(
					 * "Career Builder Candidate already exists"); } else
					 */ {
						System.out.println("call() 2nd else 4");
						this.getService().saveCandidate(candidate);
						saveCandidateStatus(candidate);
					}
				} else {
					System.out.println("call() 1st else 5");
					this.getService().saveCandidate(candidate);
					saveCandidateStatus(candidate);
				}
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

	private String grabResumeDataFromHtmlString(String htmlData, String elementId) {
		try {
			Document htmlDocument = Jsoup.parse(htmlData);
			Element resumeContent = htmlDocument.getElementById(elementId);
			return resumeContent.html();
		} catch (Exception e) {
			e.getMessage();
		}
		return null;

	}

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
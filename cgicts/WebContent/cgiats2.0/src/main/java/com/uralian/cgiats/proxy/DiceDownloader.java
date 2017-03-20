package com.uralian.cgiats.proxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferIndexFinder;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;

import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.service.DupResumeTrackService;
import com.uralian.cgiats.service.ServiceException;

/**
 * 
 * Class that downloads the resume from dice.com, by parsing the original
 * request url and setting the needed headers for authentication. It implements
 * Callable interface so that the download can be launched in a different
 * thread.
 * 
 * @author jbernalm
 */
public class DiceDownloader extends AbstractDownloader
{
	private static final String DOCKEY_STRING = "name=\"dockey\"";
	private static final String NAME_STRING = "<h1 class=\"candidateName\">";
	private static final String PHONE_STRING = "Primary Phone:";
	private static final String EMAIL_STRING = "E-mail:";
	private static final String LOCATION_STRING = "<div id=\"candidateLocationContainer\">";

	protected static final String DOCKEY_KEY = "DOCKEY_KEY";
	public final String RESUME_DATA_KEY = "RESUME_DATA_KEY";

	private static final ChannelBufferIndexFinder DOCKEY = new ByteArrayIndexFinder(
			DOCKEY_STRING);
	private static final ChannelBufferIndexFinder Name = new ByteArrayIndexFinder(
			NAME_STRING);
	private static final ChannelBufferIndexFinder Phone = new ByteArrayIndexFinder(
			PHONE_STRING);
	private static final ChannelBufferIndexFinder Email = new ByteArrayIndexFinder(
			EMAIL_STRING);
	private static final ChannelBufferIndexFinder Location = new ByteArrayIndexFinder(
			LOCATION_STRING);
	private static ChannelBufferIndexFinder H1 = new ByteArrayIndexFinder("</h1>");
	private static final ChannelBufferIndexFinder DD = new ByteArrayIndexFinder(
			"</dd>");
	private static final ChannelBufferIndexFinder DIV = new ByteArrayIndexFinder(
			"<div id");

	private static Pattern blanksPattern = Pattern.compile("[\\t\\r\\n\\v\\f]*");
	private static Pattern spacePattern = Pattern.compile(" *");
	private static Pattern quotePattern = Pattern.compile("\"");
	private static Pattern htmlPattern = Pattern
			.compile("<[A-Za-z0-9 \\t\\r\\n\\v\\f\\]\\[!\"#$%&'()*+,.:;=?@\\^_`{|}/~-]*>");
	private static Pattern nbspPattern = Pattern.compile("&nbsp;");

	private HttpResponse response;
	private ConcurrentMap<String, Boolean> docsBeingDownloaded = new ConcurrentHashMap<String, Boolean>();
	private Hashtable<String, Object> parameters;
	@Autowired
	private transient DupResumeTrackService dupResumeTrackService;
	/**
	 * @param request
	 */
	public DiceDownloader(HttpResponse response)
	{
		this.response = response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.ResumeDownloader#downloadResume()
	 */
	//	@Override
	public DocumentHolder downloadResume() throws ServiceException
	{
		if (log.isInfoEnabled())
			log.info("Starting downloading process");

		if (parameters == null)
		{
			getParametersFromResponse();
		}

		String dockey = (String) parameters.get(DOCKEY_KEY);
		if (dockey == null)
		{
			if (log.isErrorEnabled())
				log.error("Cant download the document, dockey is null");
			return null;
		}

		// ensure only one active download exists for the same resume
		Boolean prevStatus = docsBeingDownloaded.putIfAbsent(dockey, true);
		if (prevStatus != null)
			return null;

		HttpClient client = null;
		try
		{
			// Create & initialise the client
			client = new DefaultHttpClient();

			// Setup the request, the url is constant for all resume downloads
			HttpPost post = createDownloadRequest(dockey);
			HttpContext localContext = new BasicHttpContext();
			org.apache.http.HttpResponse newResp = null;

			// Execute the request to download
			newResp = client.execute(post, localContext);
			if (newResp.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
			{
				throw new ServiceException("Bad response status: "
						+ newResp.getStatusLine().toString());
			}

			// extract the document
			byte[] data = extractDocument(newResp);
			DocumentHolder doc = (DocumentHolder) parameters.get(RESUME_DATA_KEY);
			doc.setContentType(getFileTypeFromResponse(newResp));
			doc.setData(data);
			log.info("Resume download complete");

			return doc;
		}
		catch (ClientProtocolException e)
		{
			throw new ServiceException(
					"HTTP error executing the download request for dockey " + dockey, e);
		}
		catch (IOException e)
		{
			throw new ServiceException(
					"I/O error executing the download request for dockey " + dockey, e);
		}
		catch (ServiceException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new ServiceException(
					"Unexpected error executing the download request for dockey "
							+ dockey, e);
		}
		finally
		{
			// remove the download lock for this file
			docsBeingDownloaded.remove(dockey);
			// close resources
			if (client != null)
				client.getConnectionManager().shutdown();
		}
	}

	/**
	 * @param url
	 * @return
	 */
	public Hashtable<String, Object> getParametersFromResponse()
	{
		parameters = new Hashtable<String, Object>(1);

		// Get the value of Resume_DID
		int startIndex = response.getContent().bytesBefore(DOCKEY);
		if (startIndex < 0)
			return parameters;
		startIndex += DOCKEY_STRING.length();
		int length = response.getContent().bytesBefore(startIndex, 100, (byte) 47);
		if (length < 0 || length >= 100)
			return parameters;

		ChannelBuffer copyKeyBuffer = response.getContent()
				.copy(startIndex, length);
		byte[] responseBytes = new byte[copyKeyBuffer.readableBytes()];
		copyKeyBuffer.readBytes(responseBytes);
		String dockey = new String(responseBytes);
		dockey = spacePattern.matcher(dockey).replaceAll("");
		String[] items = quotePattern.split(dockey);
		if (items == null || items.length < 1)
			return parameters;

		dockey = items[items.length - 1];
		if (dockey == null || dockey.isEmpty())
			return parameters;

		if (log.isInfoEnabled())
			log.debug("Parsed dockey " + dockey);

		parameters.put(DOCKEY_KEY, dockey);
		DocumentHolder document = new DocumentHolder();

		// Get the name
		startIndex = response.getContent().bytesBefore(Name);
		if (startIndex < 0)
		{
			document.setName("Dice:Unknown");
			document.setLastName("Dice:Unknown-" + System.currentTimeMillis());
		}
		else
		{
			startIndex += NAME_STRING.getBytes().length;
			length = response.getContent().bytesBefore(startIndex, 500, H1);
			if (length < 0 || length >= 500)
			{
				document.setName("Dice:Unknown");
				document.setLastName("Dice:Unknown-" + System.currentTimeMillis());
			}
		}
		if (document.getName() == null)
		{
			copyKeyBuffer = response.getContent().copy(startIndex, length);
			responseBytes = new byte[copyKeyBuffer.readableBytes()];
			copyKeyBuffer.readBytes(responseBytes);
			String name = new String(responseBytes);
			name = htmlPattern.matcher(name).replaceAll("");
			name = blanksPattern.matcher(name).replaceAll("").trim();
			int index = name.indexOf(" ");
			if (index > 0)
			{
				document.setName(name.substring(0, index));
				document.setLastName(name.substring(index + 1, name.length()));
			}
			else
			{
				document.setName(name);
				document.setLastName("Dice:Unknown-" + System.currentTimeMillis());
			}
			System.out.println(name);
		}

		// Get the phone
		startIndex = response.getContent().bytesBefore(Phone);
		if (startIndex < 0)
		{
			document.setPhone("-");
		}
		else
		{
			startIndex += PHONE_STRING.getBytes().length;
			length = response.getContent().bytesBefore(startIndex, 500, DD);
			if (length < 0 || length >= 500)
			{
				document.setPhone("-");
			}
		}
		if (document.getPhone() == null)
		{
			copyKeyBuffer = response.getContent().copy(startIndex, length);
			responseBytes = new byte[copyKeyBuffer.readableBytes()];
			copyKeyBuffer.readBytes(responseBytes);
			String phone = new String(responseBytes);
			phone = htmlPattern.matcher(phone).replaceAll("");
			phone = blanksPattern.matcher(phone).replaceAll("").trim();
			System.out.println(phone);
			document.setPhone(phone);
		}

		// Get the email
		startIndex = response.getContent().bytesBefore(Email);
		if (startIndex < 0)
		{
			document.setEmail("Dice:Unknown");
		}
		else
		{
			startIndex += EMAIL_STRING.getBytes().length;
			length = response.getContent().bytesBefore(startIndex, 500, DD);
			if (length < 0 || length >= 500)
			{
				document.setEmail("Dice:Unknown");
			}
		}
		if (document.getEmail() == null)
		{
			copyKeyBuffer = response.getContent().copy(startIndex, length);
			responseBytes = new byte[copyKeyBuffer.readableBytes()];
			copyKeyBuffer.readBytes(responseBytes);
			String email = new String(responseBytes);
			email = htmlPattern.matcher(email).replaceAll("");
			email = blanksPattern.matcher(email).replaceAll("").trim();
			System.out.println(email);
			document.setEmail(email);
		}

		// Get the location
		startIndex = response.getContent().bytesBefore(Location);
		if (startIndex < 0)
		{
			document.setLocation("Dice:Unknown");
		}
		else
		{
			startIndex += LOCATION_STRING.getBytes().length;
			length = response.getContent().bytesBefore(startIndex, 2000, DIV);
			if (length < 0 || length >= 500)
			{
				document.setLocation("Dice:Unknown");
			}
		}
		if (document.getLocation() == null)
		{
			copyKeyBuffer = response.getContent().copy(startIndex, length);
			responseBytes = new byte[copyKeyBuffer.readableBytes()];
			copyKeyBuffer.readBytes(responseBytes);
			String location = new String(responseBytes);
			location = htmlPattern.matcher(location).replaceAll("");
			location = blanksPattern.matcher(location).replaceAll("").trim();
			location = nbspPattern.matcher(location).replaceAll(" ");
			System.out.println(location);
			document.setLocation(location);
		}

		// Set prameters for resume IDs
		parameters.put(RESUME_DATA_KEY, document);

		return parameters;
	}

	@Override
	public DocumentHolder call() throws Exception
	{
		DocumentHolder doc = null;
		try
		{
			if (parameters == null)
			{
				getParametersFromResponse();
			}
			String dockey = (String) parameters.get(DiceDownloader.DOCKEY_KEY);

			doc = downloadResume();
			Candidate candidate = new Candidate();

			candidate.setFirstName(doc.getName());
			candidate.setLastName(doc.getLastName());
			candidate.getAddress().setStreet1(doc.getLocation());
			candidate.setEmail(doc.getEmail());
			candidate.setPhone(doc.getPhone());
			candidate.addProperty("source", "dice.com");
			candidate.addProperty("externalId", dockey);
			candidate.setDocument(doc.getData(), doc.getContentType());
			candidate.parseDocument();
			candidate.setCreatedUser("Dice");
			candidate.setDeleteFlag(0);
//			candidate.setPortalEmail(UserInfoBean.getPortalId());
//			log.info("Current logged user for dice in downloader--"+UserInfoBean.getCurrentUserName());
//			candidate.setPortalViewedBy(UserInfoBean.getCurrentUserName());
			String emailId = doc.getEmail();
			if(!emailId.equals("Dice:Unknown")){
				/*List<Candidate> existEmail = getService().getCandidateDetails(emailId);
				if(existEmail.size() > 0){
					DuplicateResumeTrack duplicateResumeTrack=new DuplicateResumeTrack(); 
					duplicateResumeTrack.setAtsUserId(UserInfoBean.getCurrentUserName());
					duplicateResumeTrack.setPortalUserId(UserInfoBean.getPortalId());
					duplicateResumeTrack.setCandidateEmail(doc.getEmail());
					duplicateResumeTrack.setCandidateName(doc.getName());
					duplicateResumeTrack.setPortalName("Dice");
					duplicateResumeTrack.setCreatedBy("Dice");
					dupResumeTrackService.saveDuplicateResume(duplicateResumeTrack);
					getService().updateCandidate(candidate);
					log.debug("Email Exists: " + emailId);
					log.error("Candidate already exists");
				}
				else*/{
					getService().saveCandidate(candidate);
				}
			}
			else{
				getService().saveCandidate(candidate);
			}
		}
		catch (ServiceException e)
		{
			if (log.isErrorEnabled())
			{
				log.error(
						"Service error downloading and processing the Dice candidate resume", e);
			}
		}
		catch (Exception e)
		{
			if (log.isErrorEnabled())
			{
				log.error("Error downloading and processing the Dice candidate resume",
						e);
			}
		}
		return doc;
	}

	/**
	 * @param dockey
	 * @return
	 * @throws ServiceException
	 */
	private HttpPost createDownloadRequest(String dockey) throws ServiceException
	{
		HttpPost post = new HttpPost(
				"http://employer.dice.com/talentmatch/servlet/TalentmatchSearch");
		// Set the header for the download request from the original request
		post.addHeader(CGIATSConstants.HOST_KEY, DiceResponseFilter.HOST_VALUE);
		post.addHeader(CGIATSConstants.USER_AGENT_KEY,
				DiceResponseFilter.USER_AGENT_VALUE);
		post.addHeader(CGIATSConstants.ACCEPT_KEY, DiceResponseFilter.ACCEPT_VALUE);
		post.addHeader(CGIATSConstants.ACCEPT_LANGUAGE_KEY,
				DiceResponseFilter.ACCEPT_LANGUAGE_VALUE);
		post.addHeader(CGIATSConstants.ACCEPT_ENCODING_KEY,
				DiceResponseFilter.ACCEPT_ENCODING_VALUE);
		post.addHeader(CGIATSConstants.CONNECTION_KEY,
				DiceResponseFilter.CONNECTION_VALUE);
		// Original request has more cookies than we need, but saves us the hassle
		// of parsing the cookies to get the cookies needed for the session, which
		// are
		// VISIT50322 and s_nr
		post.addHeader(CGIATSConstants.COOKIE_KEY, DiceResponseFilter.COOKIE_VALUE);
		post.addHeader(CGIATSConstants.CONTENT_TYPE_KEY,
				"application/x-www-form-urlencoded");
		// Set the parameters for the download
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		// op value is constant, appears is the one dice.com designed for download
		// requests, no way to get this op value from the request or response, this
		// one is hardcoded
		// in the download button html. The op values you get from request or
		// response are different, if you use it the request will fail.
		nvps.add(new BasicNameValuePair("op", "7200"));
		nvps.add(new BasicNameValuePair("dockey", dockey));
		try
		{
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		}
		catch (UnsupportedEncodingException e1)
		{
			throw new ServiceException(
					"Incorrect encode set for the download request", e1);
		}
		return post;
	}

}
/*
 * DiceHttpServletRequestHandler.java Sep 18, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.proxy;

import static com.uralian.cgiats.proxy.CGIATSConstants.CONTENT_DISPOSITION_KEY;
import static com.uralian.cgiats.util.Utils.findSubstring;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.ContentType;
import com.uralian.cgiats.model.DuplicateResumeTrack;
import com.uralian.cgiats.model.Resume;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.service.CommService;
import com.uralian.cgiats.service.CommService.AttachmentInfo;
import com.uralian.cgiats.service.DupResumeTrackService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.service.UserService;
import com.uralian.cgiats.util.Utils;
import com.uralian.cgiats.web.UIBean;

/**
 * @author Vlad Orzhekhovskiy
 */
@Component("diceServletHandler")
@Scope("application")
public class DiceHttpServletRequestHandler extends UIBean implements HttpRequestHandler
{
	private static final String BASE_URL = "https://employer.dice.com";

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected  DefaultHttpClient client ;
	@Autowired
	private transient CandidateService service;
	
	@Autowired
	private transient DupResumeTrackService dupResumeTrackService;
	
	@Autowired
	private CommService commService;
	
	@Autowired
	private UserService userService;
	
//	private UserInfoBean userInfo ;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.web.HttpRequestHandler#handleRequest(javax.servlet.
	 * http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void handleRequest(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException
	{
		try{
		String pathInfo = request.getPathInfo();
		log.info("pathInfo::::::::::"+pathInfo);
		log.info("dockey::::::::::"+request.getParameter("dockey"));
		log.info("op::::::::::"+request.getParameter("op"));
		if(request.getUserPrincipal()!=null && request.getUserPrincipal().getName()!=null)
		log.info("At Method Start Principal Name:: "+request.getUserPrincipal().getName());
		if (Utils.isBlank(pathInfo))
		{
			log.info("pathInfo blank else::::::::::"+pathInfo);
			client = new DefaultHttpClient();	// added client in blank condition for logging automatically in Dice
			
		    ClientConnectionManager mgr = client.getConnectionManager();
		    HttpParams params = client.getParams();
		    client = new DefaultHttpClient(new ThreadSafeClientConnManager(params,mgr.getSchemeRegistry()), params);
			log.info(" Inside Utils.isBlank::::::::::"+request.getParameter("USERNAME"));
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			String html = accessHomePage(request);
			out.write(html);
		}
		else if (pathInfo.startsWith("/daf/servlet/DAFctrl"))
		{
//			log.info("pathInfo abc::::::::::"+pathInfo);
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			HttpPost loginPost = prepareLoginPost(request);
			HttpResponse loginResponse = client.execute(loginPost);

			StatusLine status = loginResponse.getStatusLine();
			EntityUtils.consume(loginResponse.getEntity());
			if (status.getStatusCode() == 302)
			{
				HttpPost bouncePost = prepareBouncePost();
				HttpResponse bounceResponse = client.execute(bouncePost);
				EntityUtils.consume(bounceResponse.getEntity());
			}

			String html = accessHomePage(request);
			out.write(html);
		}
		else if (pathInfo.startsWith("/talentmatch/servlet/TalentmatchSearch")
		    && request.getParameter("dockey") != null)
		{
//			log.info("pathInfo dockey else::::::::::"+pathInfo);
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			String html = runSearch(request);

			try
			{
				String id = getResumeId(request);
				if (!Utils.isBlank(id))
				{
					DocumentHolder dh = downloadResume(id);
					resolveCandidateAttributes(html, dh);
					AttachmentInfo ai=null;
					String currentUserId=null;
					log.info("Before If Condition Principal is:: "+request.getUserPrincipal());
					if(request.getUserPrincipal()!=null)
						log.info("Before If Condition Principal Name:: "+request.getUserPrincipal().getName());
					if(request.getUserPrincipal()!=null && request.getUserPrincipal().getName()!=null){
							currentUserId=request.getUserPrincipal().getName();
							log.info("In If Condition Principal Name:: "+currentUserId);
					}
					else{
						currentUserId="sriniB";
						log.info("In else condition:: "+currentUserId);
					}
					Candidate candidate = new Candidate();
					
					candidate.setFirstName(dh.getName());
					candidate.setLastName(dh.getLastName());
					candidate.setPhone(dh.getPhone());
					candidate.setEmail(dh.getEmail());
					candidate.setAddress(dh.getAddress());
					candidate.setDocument(dh.getData(), dh.getContentType());
					candidate.addProperty("source", "dice.com");
					candidate.addProperty("externalId", id);
					candidate.setCreatedUser("Dice");
					candidate.setDeleteFlag(0);
//					if(UserInfoBean.getPortalId()!=null && !UserInfoBean.getPortalId().trim().equals(""))
//					candidate.setPortalEmail(UserInfoBean.getPortalId().trim());
					log.info("Current logged user for dice---"+currentUserId);
				    candidate.setPortalViewedBy(currentUserId);
				    if (candidate.getDocument() != null)
					{
				    	try{
//						EmailBean emailBean=new EmailBean();
						StringBuffer sb = new StringBuffer();
						if (!Utils.isBlank(candidate.getFirstName()))
							sb.append(candidate.getFirstName());
						if (!Utils.isBlank(candidate.getLastName()))
							sb.append(candidate.getLastName());

						String fullName = sb.toString().trim();
						ai = buildAttachmentInfo(fullName,candidate.getDocument(), candidate.getDocumentType());
//						emailBean.addAttachment(ai);
						User user=userService.loadUser(currentUserId);
						String mailMsg="Hi <b>";
						if(user!=null && user.getFirstName()!=null && !user.getFirstName().trim().equals(""))
							mailMsg=mailMsg+user.getFirstName()+"</b>,";
							mailMsg=mailMsg+"<br/><br/>"
								+ "A candidate resume has been Viewed by you in Dice Portal. Candidate details are as follows<br><br>"
								+ "<table>"
								+ "<tr><td><b>Candidate Name</b></td><td>:"+candidate.getFullName()+"</td></tr>"
								+ "<tr><td><b>Email</b> </td><td>:"+candidate.getEmail()+"</td></tr>";
								if(candidate.getAddress()!=null && candidate.getAddress().getState()!=null && candidate.getAddress().getCity()!=null){
									mailMsg=mailMsg+ "<tr><td><b>Location</b> </td><td>:"+candidate.getAddress().getState()+","+candidate.getAddress().getCity()+"</td></tr></table>";
								}
						mailMsg=mailMsg+"<br/>Please find the attached Resume of the mentioned candidate<br/><br/><b>*** This is an automatically generated email, please do not reply ***</b> ";
						commService.sendEmail(CGIATSConstants.PROD_MAIL,user.getEmail(), "Resume Viewed in Dice Portal", mailMsg, ai);
				    	}catch(ServiceException se){
				    		se.printStackTrace();
				    	}
				    	
					}
					String emailId = candidate.getEmail();
					if(emailId!=null && !emailId.equals("Dice:Unknown")){
					List<Candidate> existEmail = service.getCandidateDetails(emailId);
					if(existEmail.size() > 0){
						log.debug("Email Exists: " + emailId);
						log.error("Candidate already exists");
						DuplicateResumeTrack duplicateResumeTrack=new DuplicateResumeTrack(); 
//						duplicateResumeTrack.setAtsUserId(UserInfoBean.getCurrentUserName());
//						duplicateResumeTrack.setPortalUserId(UserInfoBean.getPortalId());
						duplicateResumeTrack.setCandidateEmail(dh.getEmail());
						duplicateResumeTrack.setCandidateName(dh.getName());
						duplicateResumeTrack.setPortalName("Dice");
						duplicateResumeTrack.setCreatedBy("Dice");
						dupResumeTrackService.saveDuplicateResume(duplicateResumeTrack);
						Iterator itr= existEmail.iterator();
						Candidate existCandidate=null;
						while(itr.hasNext()){
							existCandidate=(Candidate)itr.next();
						}
						existCandidate.setFirstName(dh.getName());
						existCandidate.setLastName(dh.getLastName());
						existCandidate.setPhone(dh.getPhone());
						existCandidate.setEmail(dh.getEmail());
						existCandidate.setAddress(dh.getAddress());
						existCandidate.setDocument(dh.getData(), dh.getContentType());
						existCandidate.addProperty("source", "dice.com");
						existCandidate.addProperty("externalId", id);
						existCandidate.setCreatedUser("Dice");
						existCandidate.setDeleteFlag(0);
//						if(UserInfoBean.getPortalId()!=null && !UserInfoBean.getPortalId().trim().equals(""))
//							existCandidate.setPortalEmail(UserInfoBean.getPortalId().trim());
						log.info("Current logged user for dice in candidate update---"+currentUserId);
						existCandidate.setPortalViewedBy(currentUserId);
						Resume resume=new Resume();
						resume.setDocument(existCandidate.getDocument());
						resume.setProcessedDocument(existCandidate.getProcessedDocument());
						resume.setRtrDocument(existCandidate.getRtrDocument());
						resume.setOriginalLastUpdate(existCandidate.getUpdatedOn());
						resume.setProcessedLastUpdate(existCandidate.getResume().getProcessedLastUpdate());
						resume.setRtrLastUpdate(existCandidate.getResume().getRtrLastUpdate());
						existCandidate.setResume(resume);
						service.updateCandidate(existCandidate);
						log.info("candidate email in Dice Http request handler update candidate>>"+emailId);
						
					}
					else{
						log.info("Candidate Email before saving ::"+candidate.getEmail());
						service.saveCandidate(candidate);
					}
					}
					else{
						log.info("Candidate Email before saving in else ::"+candidate.getEmail());
						candidate.setEmail("Dice:Unknown");
						service.saveCandidate(candidate);
					}
					
										
					
				}
			}
			catch (Exception e)
			{
				log.error("Cannot save candidate", e);
			}

			out.write(html);
		}
		else if (pathInfo.startsWith("/talentmatch/servlet/TalentmatchSearch"))
		{
			
			try{
//			log.info("pathInfo search else::::::::::"+pathInfo);
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			String html = runSearch(request);
			html = decorateSearchResults(html);

			out.write(html);
			}catch(Exception e){
				 
			}
		}
		else if (pathInfo.startsWith("/assets")
		    || pathInfo.startsWith("/talentmatch/assets"))
		{
//			log.info("pathInfo forward else::::::::::"+pathInfo);
			forward(request, response);
		}
		else
		{
//			log.info("pathInfo last else::::::::::"+pathInfo);
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.write("<h1>Request Info</h1>");
			out.write(getRequestInfo(request));
		}
	}catch(Exception e){
//	    response.setHeader("Refresh","0");
	}
	}

	/**
	 * @param html
	 * @return
	 */
	private String decorateSearchResults(String html)
	{
		// TODO will decorate the search results
		return html;
	}

	/**
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	protected static void forward(HttpServletRequest request,
	    HttpServletResponse response) throws IOException
	{
		String pathInfo = request.getPathInfo();
		String query = request.getQueryString();

		String url = BASE_URL + pathInfo;
		if (!Utils.isBlank(query))
			url += "?" + query;
//		System.out.println("url forward:::"+url);
		HttpGet httpGet = new HttpGet(url);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse httpResponse = client.execute(httpGet);
		HttpEntity entity = httpResponse.getEntity();
//		EntityUtils.consume(httpResponse.getEntity());
		response.setContentType(entity.getContentType().getValue());
		ServletOutputStream os = response.getOutputStream();
		os.write(EntityUtils.toByteArray(entity));
		EntityUtils.consume(entity);
		System.out.println("forward ends");
	}

	/**
	 * @param request
	 * @return
	 */
	protected static String getRequestInfo(HttpServletRequest request)
	{
		StringBuffer url = request.getRequestURL();
		String contextPath = request.getContextPath();
		String method = request.getMethod();
		String pathInfo = request.getPathInfo();
		String query = request.getQueryString();
		String uri = request.getRequestURI();
		String servletPath = request.getServletPath();

		StringBuilder sb = new StringBuilder();
		sb.append("contextPath=").append(contextPath).append("<br/>");
		sb.append("method=").append(method).append("<br/>");
		sb.append("pathInfo=").append(pathInfo).append("<br/>");
		sb.append("query=").append(query).append("<br/>");
		sb.append("uri=").append(uri).append("<br/>");
		sb.append("url=").append(url.toString()).append("<br/>");
		sb.append("servletPath=").append(servletPath).append("<br/>");

		return sb.toString();
	}

	/**
	 * @param request
	 * @return
	 */
	protected static String getServletURL(HttpServletRequest request)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(request.getScheme());
		sb.append("://");
		sb.append(request.getServerName());
		sb.append(":");
		sb.append(request.getServerPort());
		sb.append(request.getContextPath());
		sb.append(request.getServletPath());
		return sb.toString();
	}

	/**
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private String runSearch(HttpServletRequest request) 
	{
		String html=null;
		try{
		String searchBase = BASE_URL + "/talentmatch/servlet/TalentmatchSearch";
		String searchParams = request.getQueryString();
//		String url=request.getPathInfo();
//		System.out.println("pathinfo>>"+url);
//System.out.println("searchParams"+searchParams);
//System.out.println("searchParams"+searchParams.replace("|", "\\|"));
//if(searchParams.endsWith("1")){
//	System.out.println("searchParams in if>>"+searchParams);
////	searchParams.lreplace("1", "0");
//	searchParams=searchParams.substring(0, searchParams.length() - 1) + '0';
//}
//if(searchParams.endsWith("0")){
//	System.out.println("searchParams in else>>"+searchParams);
////	searchParams.lreplace("1", "0");
//	searchParams=searchParams.substring(0, searchParams.length() - 1) + '1';
//}
		if(searchParams!=null && !searchParams.trim().equals("")){
			searchParams=searchParams.replace("|", "&");
		}
		HttpGet searchGet = new HttpGet(searchBase + "?" + searchParams);
		HttpResponse searchResponse = client.execute(searchGet);
		 html = EntityUtils.toString(searchResponse.getEntity());
		 EntityUtils.consume( searchResponse.getEntity());
		html = rewriteRelativeUrls(html, request);
		}catch(Exception e){
			e.printStackTrace();
		}
		return html;
	}

	/**
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private String accessHomePage(HttpServletRequest request) throws IOException
	{
//		System.out.println("accessHomePage>>");
		final String url = BASE_URL + "/daf/servlet/DAFctrl?op=1201";
		System.out.println("accessHomePage url>>"+url);
		HttpGet httpGet = new HttpGet(url);
		HttpResponse httpResponse = client.execute(httpGet);

		String html = EntityUtils.toString(httpResponse.getEntity());
		 EntityUtils.consume( httpResponse.getEntity());
		html = rewriteRelativeUrls(html, request);
		return html;
	}

	/**
	 * @param html
	 * @return
	 */
	private static String rewriteRelativeUrls(String html,
	    HttpServletRequest request)
	{
		html = html.replace("<head>", "<head><base href=\""
		    + getServletURL(request) + "/\" />");
		html = html.replace("\"/daf", "\"daf").replace("'/daf", "'daf");
		html = html.replace("\"/talentmatch", "\"talentmatch").replace(
		    "'/talentmatch", "'talentmatch");
		html = html.replace("\"/assets", "\"assets").replace("'/assets", "'assets");
		html = html.replace("url(/", "url(");
		return html;
	}

	/**
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static HttpPost prepareLoginPost(HttpServletRequest request)
	    throws UnsupportedEncodingException
	{
		final String url = BASE_URL + "/daf/servlet/DAFctrl";
		HttpPost post = new HttpPost(url);

		final String keyUser = "USERNAME";
		final String keyPass = "PASSWORD";

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("op", "1202"));
		nvps.add(new BasicNameValuePair(keyUser, request.getParameter(keyUser)));
		nvps.add(new BasicNameValuePair(keyPass, request.getParameter(keyPass)));
		nvps.add(new BasicNameValuePair("browserID", "default"));
		post.setEntity(new UrlEncodedFormEntity(nvps));
//		UserInfoBean.setPortalId(request.getParameter(keyUser));
		return post;
	}

	/**
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static HttpPost prepareBouncePost()
	    throws UnsupportedEncodingException
	{
		final String url = BASE_URL + "/daf/servlet/DAFctrl";
		HttpPost post = new HttpPost(url);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("op", "1202"));
		nvps.add(new BasicNameValuePair("bounceLogin", "CONTINUE"));
		nvps.add(new BasicNameValuePair("bounceCancel", ""));
		post.setEntity(new UrlEncodedFormEntity(nvps));

		return post;
	}

	/**
	 * @param request
	 * @return
	 */
	private static String getResumeId(HttpServletRequest request)
	{
		String dockey = request.getParameter("dockey");
		int index1 = dockey.lastIndexOf('/');
		int index2 = dockey.lastIndexOf('@');
		if (index1 < 0 || index2 < 0)
			return null;

		return dockey.substring(index1 + 1, index2);
	}

	/**
	 * @param response
	 * @return
	 */
	protected ContentType getContentTypeFromResponse(HttpResponse response)
	{
		log.debug("Resolving resume content type from " + response);

		HttpEntity entity = response.getEntity();

		Header ctypeHeader = entity.getContentType();
		ContentType type = ContentType.resolveByMimeType(ctypeHeader.getValue());
		if (type != null)
		{
			log.info("Content type resolved by header: " + type);
			return type;
		}

		HeaderElement[] elements = ctypeHeader.getElements();
		for (HeaderElement element : elements)
		{
			type = ContentType.resolveByMimeType(element.getName());
			if (type != null)
			{
				log.info("Content type resolved by headers: " + type);
				return type;
			}
		}

		final String pattern = "filename=";
		Header[] contentHeaders = response.getHeaders(CONTENT_DISPOSITION_KEY);
		if (!Utils.isEmpty(contentHeaders))
		{
			for (Header header : contentHeaders)
			{
				String headerValue = header.getValue();
				int index = headerValue.indexOf(pattern);
				if (index >= 0)
				{
					String fileName = headerValue.substring(index + pattern.length());
					type = ContentType.resolveByFileName(fileName);
					log.info("Content type resolved by filename: " + type);
					return type;
				}
			}
		}

		log.warn("Content type not resolved, using default (plain)");
		return ContentType.PLAIN;
	}

	/**
	 * @param id
	 * @return
	 * @throws IOException
	 */
	protected DocumentHolder downloadResume(String id) throws IOException
	{
		final String url = BASE_URL + "/talentmatch/servlet/TalentmatchSearch";

		HttpPost post = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("op", "7200"));
		nvps.add(new BasicNameValuePair("dockey", id));
		post.setEntity(new UrlEncodedFormEntity(nvps));

		log.debug("Downloading resume for id " + id);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse rsp = client.execute(post);
		log.debug("Resume response received: " + rsp);

		int statusCode = rsp.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK){
			log.info("Bad status: " + statusCode);
		//	throw new IOException("Bad status: " + statusCode);
		}

		HttpEntity entity = rsp.getEntity();
		byte[] data = EntityUtils.toByteArray(entity);
		ContentType type = getContentTypeFromResponse(rsp);
		EntityUtils.consume(entity);

		DocumentHolder dh = new DocumentHolder();
		dh.setContentType(type);
		dh.setData(data);
		log.info("Resume downloaded: " + dh);

		return dh;
	}
	/**
	 * @param doc
	 * @param type
	 * @return
	 */
	private static AttachmentInfo buildAttachmentInfo(String fullName,byte[] doc, ContentType type)
	{
		String name = fullName + type.getPreferredExtension();
		AttachmentInfo ai = new AttachmentInfo(name, doc, type.getMimeType());
		return ai;
	}

	/**
	 * @param html
	 * @param dh
	 */
	protected void resolveCandidateAttributes(String html, DocumentHolder dh)
	{
		String fullName = findSubstring(html, "<h1 class=\"candidateName\">",
		    "</h1>", 0);
		if (!Utils.isBlank(fullName))
		{
			int index = fullName.indexOf(',');
			if (index >= 0)
			{
				dh.setLastName(fullName.substring(0, index).trim());
				dh.setName(fullName.substring(index + 1).trim());
			}
			else
			{
				index = fullName.lastIndexOf(' ');
				if (index >= 0)
				{
					dh.setName(fullName.substring(0, index).trim());
					dh.setLastName(fullName.substring(index + 1).trim());
				}
				else
					dh.setLastName(fullName.trim());
			}
		}

		String location = findSubstring(html,
		    "<div id=\"candidateLocationContainer\">",
		    "<div id=\"candidateDetailList\">", 0);
		String street =findSubstring(location, "", "<div>", 0)!=null?findSubstring(location, "", "<div>", 0).replaceAll("\\p{Space}+", " ").trim():null;
		if (!Utils.isBlank(street))
			dh.getAddress().setStreet1(street);
		String cityStateZip = findSubstring(location, "<div>", "</div>", 0);
		if (!Utils.isBlank(cityStateZip))
		{
			String city = findSubstring(cityStateZip, "", ",&nbsp;", 0);
			if (!Utils.isBlank(city))
				dh.getAddress().setCity(city);
			String state = findSubstring(cityStateZip, ",&nbsp;", "&nbsp;", 0);
			if (!Utils.isBlank(state))
				dh.getAddress().setState(state);
			String zip = findSubstring(cityStateZip, "&nbsp;&nbsp;&nbsp;", "", 0);
			if (!Utils.isBlank(zip))
				dh.getAddress().setZipcode(zip);
		}

		String emailDiv = findSubstring(html, "<dt>E-mail:</dt>", "</dl>", 0);
		if (!Utils.isBlank(emailDiv))
		{
			String email = findSubstring(emailDiv, "<a href=\"mailto:", "\">", 0);
			if (!Utils.isBlank(email))
				dh.setEmail(email);
		}

		String phoneDiv = findSubstring(html, "<dt>Primary Phone:</dt>", "</dl>", 0);
		if (!Utils.isBlank(phoneDiv))
		{
			String phone = findSubstring(phoneDiv, "<dd>", "</dd>", 0);
			if (!Utils.isBlank(phone) && !phone.trim().equals("-"))
				dh.setPhone(phone);
		}
	}
}
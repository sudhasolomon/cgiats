package com.uralian.cgiats.proxy;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.HttpRequestMatcher;

import com.enterprisedt.util.debug.Logger;

/**
 * 
 * Request matcher to decide when Techfetch download process needs to be
 * launched from the backend.<br>
 * It also fills the data required from this request to create the download
 * request.
 *
 * @author jbernalm
 */
public class TechFetchRequestMatcher implements HttpRequestMatcher {

	private static final Logger log = Logger.getLogger(TechFetchRequestMatcher.class);

	@Override
	public boolean shouldFilterResponses(HttpRequest request) {

		log.info("Start From Techfetch RequestMatcher");
		String uri = request.getUri();

		/*if (uri.indexOf("Resume_DID") >= 0 && uri.indexOf("JobPoster/Resumes/ResumeDetails") >= 0) {
			// Fill the request headers for the download request that will be
			// triggered when we get the response
			// There is a risk that 2 different accounts trigger a different
			// download at the same time or almost at the same time and both
			// request are made
			// using the authentication cookies of the 2nd account, Also need to
			// test if this will be a problem to downalod the resume of the 1st
			// account.
			// TODO Vlad: Check with client if when any of their users views a
			// resume and gets charged for it, all charter global accounts have
			// a free pass on that resume
			// Otherwise we could create multiple charges.
			// TODO Vlad: Request another account to perform the test of 2
			// accounts accessing concurrently and verify that both resumes are
			// downloaded without problems.
			CareerBuilderResponseFilter.HOST_VALUE = request.getHeader(CGIATSConstants.HOST_KEY);
			// This one will create a problem if we support more browsers than
			// firefox, if at any point we need to support more browsers we will
			// need to
			// see how to match requests and responses.
			// Example of this: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0)
			// Gecko/20100101 Firefox/11.0
			CareerBuilderResponseFilter.USER_AGENT_VALUE = request.getHeader(CGIATSConstants.USER_AGENT_KEY);
			CareerBuilderResponseFilter.ACCEPT_VALUE = request.getHeader(CGIATSConstants.ACCEPT_KEY);
			CareerBuilderResponseFilter.ACCEPT_LANGUAGE_VALUE = request.getHeader(CGIATSConstants.ACCEPT_LANGUAGE_KEY);
			CareerBuilderResponseFilter.ACCEPT_ENCODING_VALUE = request.getHeader(CGIATSConstants.ACCEPT_ENCODING_KEY);
			CareerBuilderResponseFilter.CONNECTION_VALUE = request.getHeader(CGIATSConstants.PROXY_CONNECTION_KEY);
			CareerBuilderResponseFilter.COOKIE_VALUE = request.getHeader(CGIATSConstants.COOKIE_KEY);
			return true;
		}*/
		if(uri.toLowerCase().contains("www.techfetch.com/emp/Emp_view_rs.aspx".toLowerCase())){
			return true;
		}
		log.info("End From Techfetch RequestMatcher");
		return false;
	}
}

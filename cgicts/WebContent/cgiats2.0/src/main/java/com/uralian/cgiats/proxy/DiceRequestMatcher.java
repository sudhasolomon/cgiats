package com.uralian.cgiats.proxy;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.HttpRequestMatcher;

/**
 * 
 * Request matcher to decide when Dice.com download process needs to be launched
 * from the backend.<br>
 * 
 * @author jbernalm
 */
public class DiceRequestMatcher implements HttpRequestMatcher
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.littleshoot.proxy.HttpRequestMatcher#shouldFilterResponses(org.jboss
	 * .netty.handler.codec.http.HttpRequest)
	 */
	@Override
	public boolean shouldFilterResponses(HttpRequest request)
	{
		String uri = request.getUri();

		if (uri.indexOf("dockey") >= 0
		    && uri.indexOf("talentmatch/servlet/TalentmatchSearch") >= 0)
		{

			// Fill the request headers for the download request that will be
			// triggered when we get the response
			// There is a risk that 2 different accounts trigger a different download
			// at the same time or almost at the same time and both request are made
			// using the authentication cookies of the 2nd account, Also need to test
			// if this will be a problem to downalod the resume of the 1st account.
			DiceResponseFilter.HOST_VALUE = request
			    .getHeader(CGIATSConstants.HOST_KEY);
			// This one will create a problem if we support more browsers than
			// firefox, if at any point we need to support more browsers we will need
			// to
			// see how to match requests and responses.
			// Example of this: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0)
			// Gecko/20100101 Firefox/11.0
			DiceResponseFilter.USER_AGENT_VALUE = request
			    .getHeader(CGIATSConstants.USER_AGENT_KEY);
			DiceResponseFilter.ACCEPT_VALUE = request
			    .getHeader(CGIATSConstants.ACCEPT_KEY);
			DiceResponseFilter.ACCEPT_LANGUAGE_VALUE = request
			    .getHeader(CGIATSConstants.ACCEPT_LANGUAGE_KEY);
			DiceResponseFilter.ACCEPT_ENCODING_VALUE = request
			    .getHeader(CGIATSConstants.ACCEPT_ENCODING_KEY);
			DiceResponseFilter.CONNECTION_VALUE = request
			    .getHeader(CGIATSConstants.PROXY_CONNECTION_KEY);
			DiceResponseFilter.COOKIE_VALUE = request
			    .getHeader(CGIATSConstants.COOKIE_KEY);
			return true;
		}
		return false;
	}
}

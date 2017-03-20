package com.uralian.cgiats.proxy;

import java.util.regex.Pattern;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.littleshoot.proxy.HttpRequestMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jorge
 */
public class MonsterRequestMatcher implements HttpRequestMatcher
{
	private static Pattern URIPattern = Pattern.compile(
	    "/jcm/SharedUI/Services/Secure/jcmiiwebservices/[a-z]*.asmx",
	    Pattern.CASE_INSENSITIVE);

	private Logger log = LoggerFactory.getLogger(getClass());

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
//		if (URIPattern.matcher(uri).find())
		if(uri.toLowerCase().endsWith("GetResumeSearchDetailData".toLowerCase()) || uri.toLowerCase().contains("jcm/singleResumeView.aspx".toLowerCase()))
		{
			log.info("Intercepting request: " + request.getUri());
			return true;
		}
		return false;
	}
}
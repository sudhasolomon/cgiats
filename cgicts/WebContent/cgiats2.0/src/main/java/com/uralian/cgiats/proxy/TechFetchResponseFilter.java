package com.uralian.cgiats.proxy;

import org.jboss.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.HttpResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Class that will filters the response from Techfetch, and launches the
 * download resume process.
 * 
 * @author jbernalm
 */
public class TechFetchResponseFilter implements HttpResponseFilter
{
	private static Logger log = LoggerFactory
	    .getLogger(TechFetchResponseFilter.class);

	protected static String HOST_VALUE = "";
	protected static String USER_AGENT_VALUE = "";
	protected static String ACCEPT_VALUE = "";
	protected static String ACCEPT_LANGUAGE_VALUE = "";
	protected static String ACCEPT_ENCODING_VALUE = "";
	protected static String CONNECTION_VALUE = "";
	protected static String COOKIE_VALUE = "";

	private final HttpProxy proxy;

	/**
	 * @param proxy
	 */
	public TechFetchResponseFilter(HttpProxy proxy)
	{
		this.proxy = proxy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.littleshoot.proxy.HttpResponseFilter#filterResponse(org.jboss.netty
	 * .handler.codec.http.HttpResponse)
	 */
	@Override
	public HttpResponse filterResponse(HttpResponse response)
	{
		log.info("from ");
		try{
		if (log.isInfoEnabled())
			log.info("Creating a new thread to download the CB resume");

		TechFetchDownloader downloader = new TechFetchDownloader(response);
		this.proxy.submit(downloader);
		return response;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
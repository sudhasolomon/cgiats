package com.uralian.cgiats.proxy;

import org.jboss.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.HttpResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Class that will filter the response from dice.com
 * 
 * @author jbernalm
 */
public class DiceResponseFilter implements HttpResponseFilter
{
	private static Logger log = LoggerFactory.getLogger(DiceResponseFilter.class);

	protected static String HOST_VALUE = "";
	protected static String USER_AGENT_VALUE = "";
	protected static String ACCEPT_VALUE = "";
	protected static String ACCEPT_LANGUAGE_VALUE = "";
	protected static String ACCEPT_ENCODING_VALUE = "";
	protected static String CONNECTION_VALUE = "";
	protected static String COOKIE_VALUE = "";

	private final HttpProxy proxy;

	public DiceResponseFilter(HttpProxy proxy)
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
		if (log.isInfoEnabled())
			log.info("Creating a new thread to download the Dice resume");

		DiceDownloader downloader = new DiceDownloader(response);
		this.proxy.submit(downloader);
		return response;
	}
}
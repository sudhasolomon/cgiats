package com.uralian.cgiats.proxy;

import org.jboss.netty.handler.codec.http.HttpResponse;
import org.littleshoot.proxy.HttpResponseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jorge
 */
public class MonsterResponseFilter implements HttpResponseFilter
{
	private Logger log = LoggerFactory.getLogger(getClass());

	private final HttpProxy proxy;

	/**
	 * @param proxy
	 */
	public MonsterResponseFilter(HttpProxy proxy)
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
		log.debug("Creating a new thread to download the Monster resume");

		MonsterDownloader downloader = new MonsterDownloader(response);
		this.proxy.submit(downloader);
		return response;
	}
}

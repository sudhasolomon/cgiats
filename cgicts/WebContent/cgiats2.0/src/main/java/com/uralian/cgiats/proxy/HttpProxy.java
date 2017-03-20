/*
 * HttpProxy.java Mar 16, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.littleshoot.proxy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.uralian.cgiats.service.CandidateService;
import com.uralian.cgiats.util.AppConfig;

/**
 * @author Vlad Orzhekhovskiy
 */
@Component
public class HttpProxy {
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 20, 120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	protected final Logger log = LoggerFactory.getLogger(getClass());

	private int port = 9191;
	private String cbuilderUrls = "employer.careerbuilder.com";
	private String monsterUrls = "hiring.monster.com cookie.monster.com";

	private String techFetchUrls = "www.techfetch.com";

	private boolean running = false;

	private final Map<String, HttpFilter> filters;

	private DefaultHttpProxyServer proxy;

	@Autowired
	private CandidateService service;

	/**
	*/
	public HttpProxy() {
		filters = new HashMap<String, HttpFilter>();

		// create dice.com filter
		// DICE REMOVED PERMANENTLY FROM PROXY
		// HttpResponseFilter diceRSP = new DiceResponseFilter(this);
		// HttpRequestMatcher diceMatcher = new DiceRequestMatcher();
		// DefaultHttpFilter diceFilter = new CustomFilter(1024 * 500, diceRSP,
		// diceMatcher);
		// filters.put("employer.dice.com", diceFilter);
	}

	/**
	 * @return the port.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set.
	 */
	public void setPort(int port) {
		if (isRunning())
			throw new IllegalStateException("The proxy is already running");

		this.port = port;
	}

	/**
	 * @return the cbuilderUrls.
	 */
	public String getCbuilderUrls() {
		return cbuilderUrls;
	}

	/**
	 * @param cbuilderUrls
	 *            the cbuilderUrls to set.
	 */
	public void setCbuilderUrls(String cbuilderUrls) {
		if (isRunning())
			throw new IllegalStateException("The proxy is already running");

		this.cbuilderUrls = cbuilderUrls;
	}

	/**
	 * @return the monsterUrls.
	 */
	public String getMonsterUrls() {
		return monsterUrls;
	}

	/**
	 * @param monsterUrls
	 *            the monsterUrls to set.
	 */
	public void setMonsterUrls(String monsterUrls) {
		if (isRunning())
			throw new IllegalStateException("The proxy is already running");

		this.monsterUrls = monsterUrls;
	}

	/**
	 * @return the techFetchUrls
	 */
	public String getTechFetchUrls() {
		return techFetchUrls;
	}

	/**
	 * @param techFetchUrls
	 *            the techFetchUrls to set
	 */
	public void setTechFetchUrls(String techFetchUrls) {
		if (isRunning())
			throw new IllegalStateException("The proxy is already running");
		this.techFetchUrls = techFetchUrls;
	}

	/**
	 * @return the service.
	 */
	public CandidateService getService() {
		return service;
	}

	/**
	 * @param service
	 *            the service to set.
	 */
	public void setService(CandidateService service) {
		this.service = service;
	}

	/**
	 * @throws IllegalStateException
	 */
	public synchronized void start() throws IllegalStateException {
		if (isRunning())
			throw new IllegalStateException("The proxy is already running");

		filters.clear();

		// create career builder filter
		HttpResponseFilter cbRSP = new CareerBuilderResponseFilter(this);
		HttpRequestMatcher cbMatcher = new CareerBuilderRequestMatcher();
		DefaultHttpFilter cbFilter = new CustomFilter(1024 * 500, cbRSP, cbMatcher);
		for (String url : cbuilderUrls.split("\\s"))
			filters.put(url, cbFilter);

		// create monster filter
		HttpResponseFilter monsRSP = new MonsterResponseFilter(this);
		HttpRequestMatcher monsMatcher = new MonsterRequestMatcher();
		DefaultHttpFilter monsFilter = new CustomFilter(1024 * 500, monsRSP, monsMatcher);
		for (String url : monsterUrls.split("\\s"))
			filters.put(url, monsFilter);

		// Techfetch filter
		HttpResponseFilter techFetchRSP = new TechFetchResponseFilter(this);
		HttpRequestMatcher techFetchMatcher = new TechFetchRequestMatcher();
		DefaultHttpFilter techFetchFilter = new CustomFilter(1024 * 500, techFetchRSP, techFetchMatcher);
		for (String url : techFetchUrls.split("\\s"))
			filters.put(url, techFetchFilter);

		proxy = new DefaultHttpProxyServer(port, filters);
		proxy.start();
		running = true;

		log.info("HTTP Proxy started on port " + port);
	}

	/**
	 */
	public synchronized void stop() {
		if (!isRunning())
			return;

		proxy.stop();
		proxy = null;
		running = false;

		log.info("HTTP Proxy stopped");
	}

	/**
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 */
	@PostConstruct
	public void init() {
		AppConfig config = AppConfig.getInstance();

		setPort(config.getProxyPort());
		setMonsterUrls(config.getMonsterFilterUrls());
		setCbuilderUrls(config.getCbuilderFilterUrls());
		setTechFetchUrls(config.getTechFetchUrls());

		if (config.isAutostartProxy()) {
			log.debug("Autostarting HTTP proxy...");
			start();
		}
	}

	/**
	 */
	@PreDestroy
	public void shutdown() throws Exception {
		if (isRunning())
			stop();
	}

	/**
	 * @param downloader
	 */
	public void submit(AbstractDownloader downloader) {
		downloader.setService(getService());

		executor.submit(downloader);
	}

	/**
	 * @author Vlad Orzhekhovskiy
	 */
	public static class CustomFilter extends DefaultHttpFilter {
		private final int maxResponseSize;

		/**
		 * @param responseFilter
		 * @param requestRules
		 */
		public CustomFilter(HttpResponseFilter responseFilter, HttpRequestMatcher[] requestRules) {
			super(responseFilter, requestRules);
			this.maxResponseSize = -1;
		}

		/**
		 * @param responseFilter
		 * @param requestRules
		 * @param maxResponseSize
		 */
		public CustomFilter(int maxResponseSize, HttpResponseFilter responseFilter, HttpRequestMatcher... requestRules) {
			super(responseFilter, requestRules);
			this.maxResponseSize = maxResponseSize;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.littleshoot.proxy.DefaultHttpFilter#getMaxResponseSize()
		 */
		@Override
		public int getMaxResponseSize() {
			return maxResponseSize >= 0 ? maxResponseSize : super.getMaxResponseSize();
		}
	}
}
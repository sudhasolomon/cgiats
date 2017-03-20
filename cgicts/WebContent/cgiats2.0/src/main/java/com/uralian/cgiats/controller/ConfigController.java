package com.uralian.cgiats.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jfree.util.Log;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.uralian.cgiats.model.EmailConfiguration;
import com.uralian.cgiats.service.EmailConfigurationService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.AppConfig;
import com.uralian.cgiats.util.StatusMessage;
import com.uralian.cgiats.util.Utils;

/**
 * 
 * @author Sudha
 *
 */

@Controller
@RequestMapping("config")
public class ConfigController {

	protected final org.slf4j.Logger LOG = LoggerFactory.getLogger(getClass());
	@Autowired
	private EmailConfigurationService emailConfigurationService;
	private EmailConfiguration emailCfg[];
	private List<EmailConfiguration> emailCfgList;
	private List<EmailConfiguration> emailList;
	private EmailConfiguration emailVO;

	@RequestMapping(value = "/getAppConfig", method = RequestMethod.POST)
	public ResponseEntity<?> getApplicationConfig(HttpServletRequest request) {
		AppConfig cfg = null;
		if (Utils.getLoginUserId(request) != null) {
			cfg = AppConfig.getInstance();
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<AppConfig>(cfg, HttpStatus.OK);
	}

	@RequestMapping(value = "/saveConfig", method = RequestMethod.POST)
	public ResponseEntity<?> saveConfig(HttpServletRequest request, @RequestBody AppConfig appConfig) throws IOException {
		StatusMessage status = new StatusMessage();
		if (Utils.getLoginUserId(request) != null) {
			AppConfig cfg = AppConfig.getInstance();
			cfg.setSmtpHost(appConfig.getSmtpHost());
			cfg.setSmtpPort(appConfig.getSmtpPort());
			cfg.setSmtpUsername(appConfig.getSmtpUsername());
			cfg.setSmtpPassword(appConfig.getSmtpPassword());
			cfg.setEmailFrom(appConfig.getEmailFrom());
			cfg.setTimeZoneId(appConfig.getTimeZoneId());
			cfg.save();
			status.setStatusCode(String.valueOf(200));
			status.setStatusMessage("Application configuration updated successfully.");
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<StatusMessage>(status, HttpStatus.OK);
	}

	@RequestMapping(value = "/resetToDefaultConfig", method = RequestMethod.POST)
	public ResponseEntity<?> resetToDefaultConfig(HttpServletRequest request) {
		AppConfig cfg = null;
		if (Utils.getLoginUserId(request) != null) {
			AppConfig.getInstance().reset();
			cfg = AppConfig.getInstance();
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<AppConfig>(cfg, HttpStatus.OK);
	}

	@RequestMapping(value = "/emailConfiguration", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<?> emailConfiguration(@RequestParam(value = "reportName") String reportName, HttpServletRequest request) throws ServiceException {
		if (Utils.getLoginUserId(request) != null) {

			emailCfg = emailConfigurationService.getEmails();
			emailCfgList = new ArrayList<EmailConfiguration>(Arrays.asList(emailCfg));
			emailList = new ArrayList<EmailConfiguration>();
			EmailConfiguration emc;
			for (EmailConfiguration e : emailCfgList) {
				if (e.getReportName().equals(reportName)) {
					if (e.getEmails() != null && e.getEmails().length() > 0) {
						String emails[] = e.getEmails().split(",");
						for (String email : emails) {
							emc = new EmailConfiguration();
							emc.setReportName(e.getReportName());
							emc.setEmails(email);
							emailList.add(emc);
						}
					}
				}
			}
			return new ResponseEntity<>(emailList, HttpStatus.OK);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}

	@RequestMapping(value = "/deleteEmail", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<?> deleteEmail(@RequestParam(value = "reportName") String reportName, @RequestParam(value = "email") String email,
			HttpServletRequest request) throws ServiceException {
		if (Utils.getLoginUserId(request) != null) {

			emailCfg = emailConfigurationService.getEmails();
			emailCfgList = new ArrayList<EmailConfiguration>(Arrays.asList(emailCfg));

			for (EmailConfiguration emailCfg : emailCfgList) {

				if (reportName.equals(emailCfg.getReportName())) {
					String emails[] = emailCfg.getEmails().split(",");
					if (emails.length > 1) {
						StringBuilder em = new StringBuilder();
						for (String mail : emails) {
							if (mail.equals(email)) {

							} else {
								em.append(mail + ",");
							}
						}
						emailCfg.setEmails(em.toString());
						emailCfg.setUpdatedOn(new Date());
						emailCfg.setUpdatedBy(Utils.getLoginUserId(request));
						EmailConfiguration emcfg = emailConfigurationService.saveEmailConfig(emailCfg);
					} else {
						emailConfigurationService.deleteMail(emailCfg);
					}

				} else {

				}
			}
			return new ResponseEntity<>(HttpStatus.OK);
		}

		else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	@RequestMapping(value = "/saveEmailConfig", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<?> saveEmailConfig(@RequestParam(value = "reportName") String reportName, @RequestParam(value = "email") String email,
			HttpServletRequest request) throws ServiceException {
		if (Utils.getLoginUserId(request) != null) {

			EmailConfiguration emCfg = null;
			final StatusMessage status = new StatusMessage();
			try {
				emailCfg = emailConfigurationService.getEmails();
				emailCfgList = new ArrayList<EmailConfiguration>(Arrays.asList(emailCfg));
				List<String> reportNames = new ArrayList<String>();
				for (EmailConfiguration e : emailCfgList) {
					reportNames.add(e.getReportName());
				}
				if (reportNames.contains(reportName)) {
					for (EmailConfiguration emailCfg : emailCfgList) {
						if (reportName.equals(emailCfg.getReportName())) {
							String ems[] = new String[0];
							if (emailCfg.getEmails() != null && emailCfg.getEmails().length() > 0) {
								ems = emailCfg.getEmails().split(",");
							}
							List<String> emailCheck = new ArrayList<String>(Arrays.asList(ems));
							if (!emailCheck.contains(email)) {
								String email1 = null;
								if (emailCfg.getEmails() != null && emailCfg.getEmails().length() > 0) {
									email1 = emailCfg.getEmails()+","+email;
								} else {
									email1 = email;
								}
								emailCfg.setEmails(email1);
								emailCfg.setUpdatedOn(new Date());
								emailCfg.setUpdatedBy(Utils.getLoginUserId(request));
								emCfg = emailConfigurationService.saveEmailConfig(emailCfg);
							} else {
								status.setStatusMessage("Email Already Configured");
							}
						} else {

						}
					}

				} else {
					LOG.info("else");
					EmailConfiguration emailCfgs = new EmailConfiguration();
					emailCfgs.setReportName(reportName);
					emailCfgs.setEmails(email + ",");
					emailCfgs.setCreatedOn(new Date());
					emailCfgs.setCreatedBy(Utils.getLoginUserId(request));
					emCfg = emailConfigurationService.saveEmailConfig(emailCfgs);
				}

			} catch (Exception e) {
				e.printStackTrace();
				Log.error(e.getMessage(), e);
			}
			reportName = null;
			email = null;
			if (emCfg != null) {
				status.setStatusMessage("Mail Configured Successfully");
			}

			return new ResponseEntity<>(status, HttpStatus.OK);
		} else {
			LOG.error("User must login");
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

	}

}

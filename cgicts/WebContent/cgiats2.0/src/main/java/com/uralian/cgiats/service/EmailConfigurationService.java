/**
 * 
 */
package com.uralian.cgiats.service;

import java.util.List;

import com.uralian.cgiats.model.EmailConfiguration;
import com.uralian.cgiats.model.User;

/**
 * @author Sreenath
 * 
 */
public interface EmailConfigurationService {

	public EmailConfiguration saveEmailConfig(EmailConfiguration emailVO);

	/**
	 * @returnUser[]
	 */
	public EmailConfiguration[] getEmails();

	/**
	 * @param emailCfgvoid
	 */
	public void deleteMail(EmailConfiguration emailCfg);

}

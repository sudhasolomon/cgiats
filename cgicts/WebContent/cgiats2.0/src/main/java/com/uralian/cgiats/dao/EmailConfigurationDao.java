/**
 * 
 */
package com.uralian.cgiats.dao;

import java.io.Serializable;
import java.util.List;

import com.uralian.cgiats.model.CandidateInfo;
import com.uralian.cgiats.model.EmailConfiguration;
import com.uralian.cgiats.model.User;

/**
 * @author Sreenath
 * 
 */
public interface EmailConfigurationDao extends GenericDao<EmailConfiguration, Integer> {

	/**
	 * @param emailConfiguration
	 * @returnEmailConfiguration
	 */
	EmailConfiguration saveOrUpdate(EmailConfiguration emailConfiguration);
}

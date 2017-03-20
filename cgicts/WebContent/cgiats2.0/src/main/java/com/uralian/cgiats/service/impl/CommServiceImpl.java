/*
 * CommServiceImpl.java Jun 27, 2012
 *
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.service.impl;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.service.CommService;
import com.uralian.cgiats.service.ServiceException;
import com.uralian.cgiats.util.AppConfig;
import com.uralian.cgiats.util.Utils;

/**
 * @author Vlad Orzhekhovskiy
 */
@Service
@Transactional(rollbackFor = ServiceException.class)
public class CommServiceImpl implements CommService {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void sendEmail(String from, String to, String subject, String content, AttachmentInfo... attachments) throws ServiceException {
		try {
			String[] toArr = null;
			if(to.contains(",")){
				toArr = to.split(",");	
			}else{
				 toArr = new String[] { to };
			}
		sendEmail(from, toArr, null, subject, content, attachments);} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void sendEmail(String from, String[] to, String[] cc, String subject, String content, AttachmentInfo... attachments) {
		try {
			final AppConfig config = AppConfig.getInstance();
			log.info("...........config.getSmtpHost()............" + config.getSmtpHost());
			if (Utils.isBlank(config.getSmtpHost()))
				if (config.getSmtpPort() <= 0)
					log.info("...........config.getSmtpHost()............" + config.getSmtpHost());
			log.info("...........config.getSmtpPort()............" + config.getSmtpPort());
			System.out.println("...........config.getSmtpPort()............" + config.getSmtpPort());
			String portNumber = String.valueOf(config.getSmtpPort());
			Properties props = System.getProperties();
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.host", config.getSmtpHost());
			props.put("mail.smtp.port", portNumber);

			Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(config.getSmtpUsername(), config.getSmtpPassword());
				}
			});
			log.info("config.getSmtpUsername()" + config.getSmtpUsername());
			log.info("config.getSmtpPassword()" + config.getSmtpPassword());

			InternetAddress addrFrom;
			if (from == null) {
				addrFrom = new InternetAddress(config.getEmailFrom());
			} else {
				addrFrom = new InternetAddress(from);
			}
			log.info("...........addrFrom............" + addrFrom);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(addrFrom);
			log.info("...........to............" + to);
			for (String recp : to) {
				InternetAddress addr = new InternetAddress(recp);
				log.info(".....to......addr............" + addr);
				msg.addRecipient(RecipientType.TO, addr);
			}

			if (cc != null) {
				for (String recp : cc) {
					InternetAddress addr = new InternetAddress(recp);
					log.info("...cc........addr............" + addr);
					msg.addRecipient(RecipientType.CC, addr);
				}
			}

			msg.setSubject(subject);

			MimeBodyPart html = new MimeBodyPart();
			html.setContent(content, "text/html");

			Multipart mp = new MimeMultipart("related");
			mp.addBodyPart(html);

			if (attachments != null) {
				for (AttachmentInfo ai : attachments) {
					DataSource ds = new ByteArrayDataSource(ai.getData(), ai.getType());

					MimeBodyPart bp = new MimeBodyPart();
					bp.setDataHandler(new DataHandler(ds));
					bp.setHeader("Content-ID", ai.getName());
					bp.setFileName(ai.getName());

					mp.addBodyPart(bp);
				}
			}
			log.info("...........mp............" + mp);
			msg.setContent(mp);
			log.info("...........msg............" + msg);
			Transport.send(msg);
			log.info("Message sent successfully");
			System.out.println("Message sent successfully");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}
}

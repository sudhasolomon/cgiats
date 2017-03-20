///*
// * EmailBean.java Jun 27, 2012
// *
// * Copyright 2012 Uralian, LLC. All rights reserved.
// */
//package com.uralian.cgiats.web;
//
//import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.StringTokenizer;
//
//import javax.annotation.PostConstruct;
//import javax.faces.context.FacesContext;
//
//import org.primefaces.context.RequestContext;
//import org.primefaces.event.CloseEvent;
//import org.primefaces.event.FileUploadEvent;
//import org.primefaces.model.UploadedFile;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Component;
//
//import com.uralian.cgiats.proxy.CGIATSConstants;
//import com.uralian.cgiats.service.CommService;
//import com.uralian.cgiats.service.CommService.AttachmentInfo;
//import com.uralian.cgiats.service.ServiceException;
//import com.uralian.cgiats.util.AppConfig;
//import com.uralian.cgiats.util.Utils;
//
///**
// * @author Vlad Orzhekhovskiy
// */
//@Component
//@Scope("session")
//public class EmailBean extends UIBean implements Serializable
//{
//	private static final long serialVersionUID = 2503544239876036929L;
//
//	
//	   private AttachmentInfo selectedAtt; 
//	public AttachmentInfo getSelectedAtt() {
//		return selectedAtt;
//	}
//
//	public void setSelectedAtt(AttachmentInfo selectedAtt) {
//		this.selectedAtt = selectedAtt;
//	}
//
//	private String emailAddresses;
//	/**
//	 * @return the emailAddresses
//	 */
//	public String getEmailAddresses() {
//		return emailAddresses;
//		
//	}
//
//	/**
//	 * @param emailAddresses the emailAddresses to set
//	 */
//	public void setEmailAddresses(String emailAddresses) {
//		this.emailAddresses = emailAddresses;
//	}
//
//	private boolean copyMyself;
//	private boolean splitRecipients = false;
//	
//
//	private String subject;
//
//	private String content;
//
//	private List<AttachmentInfo> attachments = new ArrayList<AttachmentInfo>();
//
//	@Autowired
//	private CommService commService;
//
//	@Autowired
//	private UserInfoBean userInfo;
//	
//	final AppConfig config = AppConfig.getInstance();
//	
//
//	/**
//   */
//	public EmailBean()
//	{
//		log.debug("Bean created: " + hashCode());
//	}
//
//	/**
//	 */
//	@PostConstruct
//	public void init()
//	{
//		log.debug("Bean initialized: " + hashCode());
//	}
//
//	/**
//	 * @return the emailAddresses.
//	 */
//	
//
//	/**
//	 * @return the copyMyself.
//	 */
//	public boolean isCopyMyself()
//	{
//		return copyMyself;
//	}
//
//	/**
//	 * @param copyMyself the copyMyself to set.
//	 */
//	public void setCopyMyself(boolean copyMyself)
//	{
//		this.copyMyself = copyMyself;
//	}
//
//	/**
//	 * @return the splitRecipients.
//	 */
//	public boolean isSplitRecipients()
//	{
//		return splitRecipients;
//	}
//
//	/**
//	 * @param splitRecipients the splitRecipients to set.
//	 */
//	public void setSplitRecipients(boolean splitRecipients)
//	{
//		this.splitRecipients = splitRecipients;
//	}
//	
//	
//
//	/**
//	 * @return the subject.
//	 */
//	public String getSubject()
//	{
//		return subject;
//	}
//
//	/**
//	 * @param subject the subject to set.
//	 */
//	public void setSubject(String subject)
//	{
//		this.subject = subject;
//	}
//
//	/**
//	 * @return the content.
//	 */
//	public String getContent()
//	{
//		return content;
//	}
//
//	/**
//	 * @param content the content to set.
//	 */
//	public void setContent(String content)
//	{
//		this.content = content;
//	}
//
//	/**
//	 * @return the attachments.
//	 */
//	public List<AttachmentInfo> getAttachments()
//	{
//		return attachments;
//	}
//
//	/**
//	 * @param attachments the attachments to set.
//	 */
//	public void setAttachments(List<AttachmentInfo> attachments)
//	{
//		this.attachments = attachments;
//	}
//
//	/**
//	 */
//	public void clearAttachments()
//	{
//		this.attachments.clear();
//	}
//
//	/**
//	 * @param attachment
//	 */
//	public void addAttachment(AttachmentInfo attachment)
//	{
//		this.attachments.add(attachment);
//	}
//
//	/**
//	 * @param attachment
//	 */
//	public void removeAttachment(AttachmentInfo attachment)
//	{
//		this.attachments.remove(attachment);
//	}
//
//	/**
//	 * @param event
//	 */
//	public void handleFileUpload(FileUploadEvent event)
//	{
//		UploadedFile file = event.getFile();
//
//		AttachmentInfo attachment = new AttachmentInfo(file.getFileName(),
//		    file.getContents(), file.getContentType());
//		addAttachment(attachment);
//	}
//
//	/**
//	 */
//	public void send()
//	{
//		try
//		{
//			String[] to = null;
//			String[] cc = null;
//			String token=null;
//			List<String> toList = new ArrayList<String>();
////			log.info("emailAddr.getValue().toString()>>"+emailAddr.getValue().toString());
//			if (!Utils.isBlank(emailAddresses))
//			{
//				StringTokenizer stok = new StringTokenizer(emailAddresses,
//				    " \t\n\r\f,;:");
//						
//				while (stok.hasMoreTokens())
//				{
//					token = stok.nextToken();	
//					log.info("token>>>"+token);
//					toList.add(token);					
//				}
//			}
//			
//			 to = toList.toArray(new String[toList.size()]);
//			 log.info("to.length>>"+to.length);
//			if(copyMyself == true){
//				log.info("copyMyself>>"+copyMyself);
//				cc = new String[] { userInfo.getCurrentUser()
//					    .getEmail(),CGIATSConstants.PROD_MAIL};
//				log.info("cc if>>"+cc.length);
//			}
//			else{
//
//				cc = new String[] {CGIATSConstants.PROD_MAIL};	
//				log.info("cc else>>"+cc.length);
//
//			}
//			
//			
//			String from = userInfo.getCurrentUser().getEmail();
//
//			AttachmentInfo[] ai = !Utils.isEmpty(attachments) ? attachments
//			    .toArray(new AttachmentInfo[attachments.size()]) : null;
//				log.info("splitRecipients>>"+splitRecipients);
//			if (splitRecipients)
//			{
//				log.info("splitRecipients if >>"+splitRecipients);
//				for (String address : to)
//					commService.sendEmail(from, address, subject, content, ai);
//				for (String address : cc)
//					commService.sendEmail(from, address, subject, content, ai);
//			}
//			else {
//				log.info("splitRecipients else >>"+splitRecipients);
//				commService.sendEmail(from, to, cc, subject, content, ai);
//				
//			}
//			addInfoMessage("Success", "Email sent successfully");
//			setEmailAddresses(null);
//			FacesContext context = FacesContext.getCurrentInstance();
//			   context.getExternalContext().getFlash().setKeepMessages(true);
//			reset();
//			RequestContext rc = RequestContext.getCurrentInstance();
//			rc.execute("emailDialog.hide()");
//			
//		}
//		catch (ServiceException e)
//		{
//			e.printStackTrace();
//			addErrorMessage(null, SEVERITY_ERROR, e);
//		}
//		
//	}
//	
//	public void handleClose(CloseEvent event){
//		setEmailAddresses(null);
//		attachments.clear();
//		copyMyself=false;
//		splitRecipients = false;
//		clearAttachments();
//	}
//	public void delAttachments(){
//		attachments.remove(selectedAtt);
//	}
//	
//
//	/**
//	 */
//	public void reset()
//	{
//		emailAddresses = null;
//		copyMyself = false;
//		clearAttachments();
//	}
//}
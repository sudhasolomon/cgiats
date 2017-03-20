package com.uralian.cgiats.ws;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Date;

import javax.jws.WebService;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.xml.sax.helpers.DefaultHandler;

import com.uralian.cgiats.model.Address;
import com.uralian.cgiats.model.IndiaCandidate;
import com.uralian.cgiats.model.IndiaResume;
import com.uralian.cgiats.proxy.CGIATSConstants;
import com.uralian.cgiats.service.IndiaCandidateService;
import com.uralian.cgiats.service.ServiceException;


@WebService(endpointInterface="com.uralian.cgiats.ws.WebServiceCandidateService",portName="candidatePort",serviceName="wsCandidateService")
public class WebServiceCandidateServiceImpl extends SpringBeanAutowiringSupport implements WebServiceCandidateService{
	
	
	@Autowired
	public IndiaCandidateService candidateService;
	
	Logger log=LoggerFactory.getLogger(getClass());
	
	public boolean saveCandidate(String user,String password,WebServiceCandidate portalCandidate)throws RemoteException{

		//check for validity of portalCandidate class 
		
		try {
			if (user == null || password == null) {

				throw new ServiceException(
						"user_name and password elements are mandetory");
			}
		} catch (Exception e) {
			throw new RemoteException(
					"java.rmi.RemoteException.RemoteException", e);
		}
			
		try {
			
			
			if ((user.trim().length()==0)||(password.trim().length()==0)) {
				throw new ServiceException(
						"user_name and password elements cannot be blank");
			}

			if (!user.equals(CGIATSConstants.WS_USER_NAME)|| !password.equals(CGIATSConstants.WS_PASSWORD)) {
				throw new ServiceException(	"Bad credentials");
			}
		} catch (Exception e) {
			throw new RemoteException(
					"java.rmi.RemoteException.RemoteException", e);
		}
		
		SchemaFactory factory=SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		if(portalCandidate==null){
			return false;
		}
		
		try {

			URL url = new URL(CGIATSConstants.WEBSERVICE_URI);

			Schema schema = factory.newSchema(url);

			JAXBContext jaxbContext = JAXBContext
					.newInstance(WebServiceCandidate.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setSchema(schema);
			marshaller.marshal(portalCandidate, new DefaultHandler());
			
			

		} catch (Exception exeption) {

			try {
				log.error("exception while parsing "+exeption.getMessage());
				throw new ServiceException("Error while trying to update candidate",exeption);
			} catch (ServiceException e) {
				e.printStackTrace();
				throw new RemoteException("Error while parsing XML document ",exeption);
				
			}
			
		} 		
		IndiaCandidate candidate= candidateService.getCandidateFromEmail(portalCandidate.getEmail(), false, false);
		
		
		if(candidate==null){
		candidate=new IndiaCandidate(); 
		candidate.setFirstName(portalCandidate.getFirstName());
		candidate.setLastName(portalCandidate.getLastName());
		candidate.setPhone(portalCandidate.getPhoneNumber());
		candidate.setEmail(portalCandidate.getEmail());
        candidate.setTitle(portalCandidate.getTitle());
        
        candidate.setVisaType(portalCandidate.getVisaType());
        candidate.setVisaExpiredDate(portalCandidate.getVisaExpiryDate());
        
        candidate.setPresentRate(portalCandidate.getPresentRate());
        candidate.setExpectedRate(portalCandidate.getExpectedRate());
        candidate.setPortalResumeLastUpd(portalCandidate.getLastUpdated());
        candidate.setPortalResumeExperience(portalCandidate.getExperience());
        candidate.setPortalResumeLastPosition(portalCandidate.getLastPosition());
        candidate.setEmploymentStatus(portalCandidate.getEmploymentStatus());
        
        Address address=new Address();
        address.setStreet1(portalCandidate.getStreet());
        address.setCity(portalCandidate.getCity());
        address.setState(portalCandidate.getState());
        address.setZipcode(portalCandidate.getZip());
        IndiaResume resume=new IndiaResume();
        resume.setCreatedBy("sriniB");
        candidate.setAddress(address);
        candidate.setResume(resume);
        
        System.out.println(portalCandidate.getDocument().length);
        
        
        
        if(!(portalCandidate.getEmail().length()>0)){
        	
        	try {
				throw new ServiceException("Email is missing");
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
               throw new RemoteException("java.rmi.RemoteException.RemoteException", e);				
			}
        }
        
        
        
        
        
        
        if(!(portalCandidate.getDocument().length>0)){
        	
        	try {
				throw new ServiceException("Resume document is missing");
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
               throw new RemoteException("Document cannot be null and it's length cannot be 0", e);				
			}
        }
        
        candidate.setDocument(portalCandidate.getDocument(),portalCandidate.getDocumentType());
        try {
        	candidate.setDeleteFlag(0);
        	candidate.setCreatedOn(new Date());
        	candidate.setCreatedUser("sriniB");
			candidateService.saveCandidate(candidate);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		else{
			//candidate already exists
			log.info("candidate already exists with this mail Id");
			try {
				throw new ServiceException("candidate already exists with this mail Id");
			} catch (ServiceException e) {
				throw new RemoteException("Error in inserting candidate",e);
			}
		}
		return true;
	}
}

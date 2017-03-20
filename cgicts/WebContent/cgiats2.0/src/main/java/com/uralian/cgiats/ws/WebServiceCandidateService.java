package com.uralian.cgiats.ws;

import java.rmi.RemoteException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

@WebService(name="WebServiceCandidateService")
public interface WebServiceCandidateService {
	
	@WebMethod
	@WebResult(name = "result")
	public boolean saveCandidate(
			@WebParam(header=true,name="user_name")String username,
			@WebParam(header=true,name="password")String password,
			@XmlElement(required=true,nillable=false)@WebParam(name ="candidate") WebServiceCandidate candidate)throws RemoteException;
}

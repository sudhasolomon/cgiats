package com.uralian.cgiats.dao.impl;

import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.ClientNamesDao;
import com.uralian.cgiats.model.ClientNames;

@SuppressWarnings("unchecked")
@Repository
public class ClientNamesDaoImpl extends GenericDaoImpl<ClientNames, Integer> implements ClientNamesDao{

	protected ClientNamesDaoImpl() {
		super(ClientNames.class);
		// TODO Auto-generated constructor stub
	}

}

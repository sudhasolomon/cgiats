package com.uralian.cgiats.dao.impl;

import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.IndiaClientNamesDao;
import com.uralian.cgiats.model.ClientNames;

@SuppressWarnings("unchecked")
@Repository
public class IndiaClientNamesDaoImpl extends IndiaGenericDaoImpl<ClientNames, Integer> implements IndiaClientNamesDao{

	protected IndiaClientNamesDaoImpl() {
		super(ClientNames.class);
		// TODO Auto-generated constructor stub
	}

}

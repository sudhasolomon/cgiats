package com.uralian.cgiats.dao.impl;

import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.IndiaSubmittalEventDao;
import com.uralian.cgiats.model.IndiaSubmittalEvent;

@SuppressWarnings("unchecked")
@Repository
public class IndiaSubmittalEventDaoImpl extends IndiaGenericDaoImpl<IndiaSubmittalEvent, Integer>implements IndiaSubmittalEventDao{

	protected IndiaSubmittalEventDaoImpl() {
		super(IndiaSubmittalEvent.class);
	}

}

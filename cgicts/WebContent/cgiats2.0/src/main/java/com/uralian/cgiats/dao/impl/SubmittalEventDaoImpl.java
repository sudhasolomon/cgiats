package com.uralian.cgiats.dao.impl;

import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.SubmittalEventDao;
import com.uralian.cgiats.model.SubmittalEvent;

@SuppressWarnings("unchecked")
@Repository
public class SubmittalEventDaoImpl extends GenericDaoImpl<SubmittalEvent, Integer>implements SubmittalEventDao{

	protected SubmittalEventDaoImpl() {
		super(SubmittalEvent.class);
	}

}

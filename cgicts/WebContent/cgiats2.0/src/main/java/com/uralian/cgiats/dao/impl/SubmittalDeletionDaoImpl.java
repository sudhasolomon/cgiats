package com.uralian.cgiats.dao.impl;

import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.SubmittalDeletionDao;
import com.uralian.cgiats.model.SubmittalDeletion;

@SuppressWarnings("unchecked")
@Repository
public class SubmittalDeletionDaoImpl extends GenericDaoImpl<SubmittalDeletion, Integer>implements SubmittalDeletionDao{

	protected SubmittalDeletionDaoImpl() {
		super(SubmittalDeletion.class);
	}

}

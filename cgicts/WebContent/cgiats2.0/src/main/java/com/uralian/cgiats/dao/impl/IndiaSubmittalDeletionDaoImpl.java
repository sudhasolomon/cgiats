package com.uralian.cgiats.dao.impl;

import org.springframework.stereotype.Repository;

import com.uralian.cgiats.dao.IndiaSubmittalDeletionDao;
import com.uralian.cgiats.model.IndiaSubmittalDeletion;

@SuppressWarnings("unchecked")
@Repository
public class IndiaSubmittalDeletionDaoImpl extends IndiaGenericDaoImpl<IndiaSubmittalDeletion, Integer> implements IndiaSubmittalDeletionDao{

	protected IndiaSubmittalDeletionDaoImpl() {
		super(IndiaSubmittalDeletion.class);
	}
	

	
}

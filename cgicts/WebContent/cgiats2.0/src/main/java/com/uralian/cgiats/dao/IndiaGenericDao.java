/*
 * GenericDao.java Jan 18, 2012
 * 
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.dao;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;

import com.uralian.cgiats.model.Candidate;
import com.uralian.cgiats.model.User;

/**
 * This is a generic DAO interface.
 * 
 * @author Vlad Orzhekhovskiy
 */
/**
 * @author Chaitanya
 *
 * @param <T>
 * @param <ID>
 */
public interface IndiaGenericDao<T, ID extends Serializable>
{
	/**
	 * Finds the object by its id.
	 * 
	 * @param id id to search.
	 * @param if <code>true</code>, the object will be locked for update.
	 * @return the object.
	 * @throws RuntimeException if the object cannot be found.
	 */
	public T findById(ID id, boolean lock) throws RuntimeException;

	/**
	 * Finds the object by id. A shortcut for <tt>findById(id, false)</tt>.
	 * 
	 * @param id id to search.
	 * @return the object or <code>null</code> if the object cannot be found.
	 * @throws RuntimeException if the object cannot be found.
	 */
	public T findById(ID id) throws RuntimeException;

	/**
	 * Finds all saved instances of the given class.
	 * 
	 * @return a list of persisted instances of this class.
	 */
	public List<T> findAll();

	/**
	 * Find entities by an HQL query. A shortcut for
	 * <tt>findByQuery(query, new String[]{name}, new Object[]{value})</tt>.
	 * 
	 * @param query HQL query to execute.
	 * @param name parameter name.
	 * @param value parameter value.
	 * @return a list of persisted instances returned by the query.
	 */
	public List<T> findByQuery(String query, String name, Object value);

	/**
	 * Find entities by an HQL query.
	 * 
	 * @param query HQL query to execute.
	 * @param params parameter map for the query as name->value. A parameter must
	 *          appear in the query as <tt>:name</tt>. Can be <code>null</code>.
	 * @return a list of persisted instances returned by the query.
	 */
	public List<T> findByQuery(String query, Map<String, Object> params);

	/**
	 * Find entities by an HQL query.
	 * 
	 * @param query HQL query to execute.
	 * @param firstResult the position from where query would start fetching
	 *          results.
	 * @param maxResulst maximum number of rows to fetch.
	 * @param params parameter list of values. Must match number of "?" in the
	 *          query.
	 * @return a list of persisted instances returned by the query.
	 */
	public List<T> findByQuery(final String query, final int firstResult,
	    final int maxResults, final Object... params);

	/**
	 * Find entities by an SQL query.
	 * 
	 * @param query SQL query to execute.
	 * @param firstResult the position from where query would start fetching
	 *          results.
	 * @param maxResulst maximum number of rows to fetch.
	 * @param params parameter list of values. Must match number of "?" in the
	 *          query.
	 * @return a list of persisted instances returned by the query.
	 */
	public List<T> findBySqlQuery(final String query, final int firstResult,
	    final int maxResults, final Object... params);
	
	/**
	 * @param query
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<T> findBySqlQuery1(final String query,final Date startDate,final Date endDate,User user);
	
	/**
	 * @param query
	 * @param month
	 * @param year
	 * @param status
	 * @return
	 */
	public List findBySqlQueryStr(final String query,final String month,final int year,final String status);
	
	/**
	 * @param query
	 * @param month
	 * @param year
	 * @param status
	 * @return
	 */
	public List findBySqlQueryAllPortal(final String query,final String month,final int year,final String status);
	
	/**
	 * @param query
	 * @param startDate
	 * @param endDate
	 * @param userId
	 * @return
	 * method for fetching dm activity report including his adm,recruiter and adm's recruiter 
	 */
	public List findBySqlQueryAdm(final String query,final Date startDate,final Date endDate,final String userId);

	/**
	 * Find entities by an HQL query. A shortcut for
	 * <tt>findByQuery(query, new String[]{name}, new Object[]{value})</tt>.
	 * 
	 * @param name of a HQL query to execute.
	 * @param name parameter name.
	 * @param value parameter value.
	 * @return a list of persisted instances returned by the query.
	 */
	public List<T> findByNamedQueryAndNamedParam(String query, String name,
	    Object value);

	/**
	 * Find entities by an HQL query.
	 * 
	 * @param name of a HQL query to execute.
	 * @param params parameter map for the query as name->value. A parameter must
	 *          appear in the query as <tt>:name</tt>. Can be <code>null</code>.
	 * @return a list of persisted instances returned by the query.
	 */
	public List<T> findByNamedQueryAndNamedParam(String query,
	    Map<String, Object> params);

	/**
	 * Find entities by a Hibernate criteria. A shortcut for
	 * <tt>findByCriteria(dc, -1, -1)</tt>, which returns all the results.
	 * 
	 * @param dc search criteria.
	 * @return a list of persisted instances returned by the criteria.
	 */
	public List<T> findByCriteria(DetachedCriteria dc);

	/**
	 * Find entities by a Hibernate criteria.
	 * 
	 * @param dc search criteria.
	 * @param firstResult first object to return (starting from 0).
	 * @param maxResults the maximum number of objects to return.
	 * @return a list of persisted instances returned by the criteria.
	 */
	public List<T> findByCriteria(DetachedCriteria dc, int firstResult,
	    int maxResults);

	/**
	 * Saves the specified entity.
	 * 
	 * @param entity object to save.
	 * @return the persisted object with the identifier set.
	 */
	public T save(T entity);

	/**
	 * Updates the specified entity in the persisted location.
	 * 
	 * @param entity to update.
	 * @return 
	 */
	T update(T entity);

	/**
	 * Deletes the entity from the persisted location.
	 * 
	 * @param entity object to delete.
	 */
	void delete(T entity);

	/**
	 * Creates a Blob.
	 * 
	 * @param entity object to delete.
	 */
	Blob createBlob(byte[] bytes);

	/**
	 * Runs an arbitrary HQL query and returns the results.
	 * 
	 * @param query query to run.
	 * @param paramNames parameter names.
	 * @param paramValues parameter values.
	 * @return a list of objects returned by the query.
	 */
	public List<?> runQuery(String query, String[] paramNames,
	    Object[] paramValues);

	/**
	 * Runs an arbitrary HQL query and returns the resules.
	 * 
	 * @param query query to run.
	 * @param params list of parameter value.
	 * @return a list of objects returned by the query.
	 */
	public List<?> runQuery(String query, final Object... params);
	
	/**
	 * For writing SQL query for fetching the ADM Montly quotas depending on User Role
	 * @param query
	 * @param params
	 * @return
	 */
	public List<?> findBySQLQueryNamedParam(String query, Map<String, Object> params);
	
	public void updateSQLQuery(String query);

	List<?> findBySQLQuery(String query, Map<String, Object> params);
	
	List<?> findByQuery(String query,int first,int pageSize,Map<String, Object> params);
	
	public int findCount(String query, Map<String, Object> params);
	public Object getObjectByCandidateInfoId(Integer candidateInfoId, Class<?> clazz);
	
}
/*
 * GenericDaoImpl.java Jan 18, 2012
 * 
 * Copyright 2012 Uralian, LLC. All rights reserved.
 */
package com.uralian.cgiats.dao.impl;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.GenericDao;
import com.uralian.cgiats.model.ClientInfo;
import com.uralian.cgiats.model.JobOrder;
import com.uralian.cgiats.model.User;
import com.uralian.cgiats.model.UserRole;

/**
 * Hibernate-based DAO implementation. Requires an existing transaction to run.
 * 
 * @author Vlad Orzhekhovskiy
 */
@Transactional(propagation = MANDATORY)
public class GenericDaoImpl<T, ID extends Serializable> extends HibernateDaoSupport implements GenericDao<T, ID> {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	protected final Class<T> entityType;

	/**
	 * @param entityType
	 */
	protected GenericDaoImpl(Class<T> entityType) {
		this.entityType = entityType;
	}

	/**
	 * @param sessionFactory
	 */
	@Autowired
	protected void bindSessionFactory(@Qualifier(value="sessionFactory")SessionFactory sessionFactory) {
		try {
			setSessionFactory(sessionFactory);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.limswt.dao.GenericDAO#findById(java.io.Serializable,
	 * boolean, boolean)
	 */
	@Override
	public T findById(ID id, boolean lock) {
		T entity = null;
		try {
			LockMode lockMode = lock ? LockMode.PESSIMISTIC_WRITE : null;
			entity = getHibernateTemplate().get(entityType, id, lockMode);
			log.debug("Object retrieved for id " + id + ": " + entity);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.limswt.dao.GenericDAO#findById(java.io.Serializable)
	 */
	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	public T findById(ID id) {
		try {
			return findById(id, false);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.limswt.dao.GenericDAO#findAll()
	 */
	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	public List<T> findAll() {
		try {
			List<T> entities = getHibernateTemplate().loadAll(entityType);
			log.debug(entities.size() + " object(s) retrieved in all");
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.limswt.dao.GenericDAO#findByQuery(java.lang.String,
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	public List<T> findByQuery(String query, String name, Object value) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(name, value);
			return findByQuery(query, params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.limswt.dao.GenericDAO#findByQuery(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	@SuppressWarnings("unchecked")
	public List<T> findByQuery(String query, Map<String, Object> params) {
		try {
			String[] names = new String[0];
			Object[] values = new Object[0];

			if (params != null) {
				names = new String[params.size()];
				values = new Object[params.size()];

				int index = 0;
				for (Map.Entry<String, Object> entry : params.entrySet()) {
					names[index] = entry.getKey();
					values[index] = entry.getValue();
					index++;
				}
			}

			List<T> entities = (List<T>) getHibernateTemplate().findByNamedParam(query, names, values);
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> findByQuery(final String query, final int firstResult, final int maxResults, final Object... params) {
		try {
			/*
			 * List<T> entities = (List<T>) getHibernateTemplate().execute(new
			 * HibernateCallback() { public Object doInHibernate(Session
			 * session) throws HibernateException, SQLException { Query
			 * queryObject = session.createQuery(query); int count = 0; for
			 * (Object value : params) { queryObject.setParameter(count, value);
			 * count++; } if (firstResult >= 0) {
			 * queryObject.setFirstResult(firstResult); } if (maxResults > 0) {
			 * queryObject.setMaxResults(maxResults); }
			 * 
			 * return queryObject.list(); } });
			 */
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			Query queryObject = session.createQuery(query);
			int count = 0;
			for (Object value : params) {
				queryObject.setParameter(count, value);
				count++;
			}
			if (firstResult >= 0) {
				queryObject.setFirstResult(firstResult);
			}
			if (maxResults > 0) {
				queryObject.setMaxResults(maxResults);
			}

			return queryObject.list();

			/*
			 * 
			 * log.debug(entities.size() + " object(s) retrieved by query");
			 * return entities;
			 */} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List findBySqlQuery1(final String query, final Date startDate, final Date endDate, final User user) {
		try {
			List<T> entities = (List<T>) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					Query queryObject = session.createSQLQuery(query);
					if (startDate != null) {
						queryObject.setParameter(0, startDate);
					}
					if (endDate != null) {
						queryObject.setParameter(1, endDate);
					}
					if (user != null) {
						if (user.getUserRole().equals(UserRole.DM)) {
							queryObject.setParameter(2, user.getUserId());
							queryObject.setParameter(3, user.getUserId());

						} else if (user.getUserRole().equals(UserRole.ADM)) {
							queryObject.setParameter(2, user.getUserId());
						}
					}
					return queryObject.list();
				}
			});
			log.debug(entities.size() + " object(s) retrieved by query");
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List findBySqlQueryStr(final String query, final String month, final int year, final String status) {
		try {
			List<T> entities = (List<T>) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					Query queryObject = session.createSQLQuery(query);
					if (year != 0) {
						queryObject.setParameter(0, year);
					}

					if (status != null && !status.equalsIgnoreCase("all") && month == null) {
						queryObject.setParameter(1, status.trim().toUpperCase());
					} else if (status != null && status.equalsIgnoreCase("all") && month != null) {
						queryObject.setParameter(1, month.trim().toUpperCase());
					} else if (status != null && !status.equalsIgnoreCase("all") && month != null) {
						queryObject.setParameter(1, status.trim().toUpperCase());
						queryObject.setParameter(2, month.trim().toUpperCase());
					}
					return queryObject.list();

				}
			});
			log.debug(entities.size() + " object(s) retrieved by query");
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List findBySqlQueryAdm(final String query, final Date startDate, final Date endDate, final String userId) {
		try {
			List<T> entities = (List<T>) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					Query queryObject = session.createSQLQuery(query);
					if (startDate != null) {
						queryObject.setParameter(0, startDate);
					}
					if (endDate != null) {
						queryObject.setParameter(1, endDate);
					}
					if (userId != null) {
						queryObject.setParameter(2, userId);
						queryObject.setParameter(3, userId);
						queryObject.setParameter(4, userId);
					}
					return queryObject.list();

				}
			});
			log.debug(entities.size() + " object(s) retrieved by query");
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List findBySqlQueryAllPortal(final String query, final String month, final int year, final String status) {
		try {

			List<T> entities = (List<T>) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					Query queryObject = session.createSQLQuery(query);
					if (year != 0) {
						queryObject.setParameter(0, year);
					}
					if (month != null) {
						queryObject.setParameter(1, month.trim().toUpperCase());
					}
					if (status != null && !status.equalsIgnoreCase("all")) {
						queryObject.setParameter(2, status.trim().toUpperCase());
					}
					// System.out.println("lst..."+queryObject.getQueryString());
					return queryObject.list();

				}
			});
			log.debug(entities.size() + " object(s) retrieved by query");
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List findBySqlQuery(final String query, final int firstResult, final int maxResults, final Object... params) {
		try {
			List<T> entities = (List<T>) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					Query queryObject = session.createSQLQuery(query);
					int count = 0;
					for (Object value : params) {
						queryObject.setParameter(count, value);
						count++;
					}
					if (maxResults >= 0) {
						queryObject.setMaxResults(maxResults);
					}
					if (firstResult >= 0) {
						queryObject.setFirstResult(firstResult);
					}
					return queryObject.list();
				}
			});
			log.debug(entities.size() + " object(s) retrieved by query");
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.limswt.dao.GenericDAO#findByNamedQueryAndNamedParam(java.lang
	 * .String, java.lang.String, java.lang.Object)
	 */
	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	public List<T> findByNamedQueryAndNamedParam(String query, String name, Object value) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(name, value);
			return findByNamedQueryAndNamedParam(query, params);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.limswt.dao.GenericDAO#findByNamedQueryAndNamedParam(java.lang
	 * .String, java.util.Map)
	 */
	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	@SuppressWarnings("unchecked")
	public List<T> findByNamedQueryAndNamedParam(String query, Map<String, Object> params) {
		try {
			String[] names = new String[0];
			Object[] values = new Object[0];

			if (params != null) {
				names = new String[params.size()];
				values = new Object[params.size()];

				int index = 0;
				for (Map.Entry<String, Object> entry : params.entrySet()) {
					names[index] = entry.getKey();
					values[index] = entry.getValue();
					index++;
				}
			}

			List<T> entities = (List<T>) getHibernateTemplate().findByNamedQueryAndNamedParam(query, names, values);
			log.debug(entities.size() + " object(s) retrieved by query");
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.limswt.dao.GenericDAO#findByCriteria(org.hibernate.criterion
	 * .DetachedCriteria)
	 */
	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(DetachedCriteria dc) {
		try {
			List<T> entities = (List<T>) getHibernateTemplate().findByCriteria(dc);
			log.debug(entities.size() + " object(s) retrieved by criteria");
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.uralian.limswt.dao.GenericDAO#findByCriteria(org.hibernate.criterion
	 * .DetachedCriteria, int, int)
	 */
	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(DetachedCriteria dc, int firstResult, int maxResults) {
		try {
			List<T> entities = (List<T>) getHibernateTemplate().findByCriteria(dc, firstResult, maxResults);
			log.debug(entities.size() + " object(s) retrieved by criteria");
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.limswt.dao.GenericDAO#save(java.lang.Object)
	 */
	@Override
	public T save(T entity) {
		try {
			getHibernateTemplate().save(entity);

			log.debug("Entity saved: " + entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.limswt.dao.GenericDAO#update(java.lang.Object)
	 */
	@Override
	public T update(T entity) {
		try {
			return getHibernateTemplate().merge(entity);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.limswt.dao.GenericDAO#delete(java.lang.Object)
	 */
	@Override
	public void delete(T entity) {
		try {
			getHibernateTemplate().delete(entity);
			log.debug("Entity deleted: " + entity);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.limswt.dao.GenericDAO#runQuery(java.lang.String,
	 * java.lang.String[], java.lang.Object[])
	 */
	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	public List<?> runQuery(String query, String[] paramNames, Object[] paramValues) {
		try {
			if (paramNames == null)
				paramNames = new String[0];
			if (paramValues == null)
				paramValues = new Object[0];
			List<?> list = getHibernateTemplate().findByNamedParam(query, paramNames, paramValues);
			log.debug(list.size() + " object(s) retrieved by criteria");
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.dao.GenericDao#runQuery(java.lang.String,
	 * java.lang.Object[])
	 */
	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	@SuppressWarnings({ "rawtypes" })
	public List<?> runQuery(final String query, final Object... params) {
		try {

			List<?> entities = (List<?>) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					Query queryObject = session.createQuery(query);
					int count = 0;
					for (Object value : params) {
						queryObject.setParameter(count, value);
						count++;
					}
					return queryObject.list();
				}
			});
			log.debug(entities.size() + " object(s) retrieved by query");
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.dao.GenericDao#createBlob(byte[])
	 */
	@Override
	public Blob createBlob(byte[] bytes) {
		try {
			return getHibernateTemplate().getSessionFactory().getCurrentSession().getLobHelper().createBlob(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	@SuppressWarnings("unchecked")
	public List<?> findBySQLQueryNamedParam(final String query, final Map<String, Object> params) {
		try {
			List<?> entities = (List<?>) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException {
					Query queryObject = session.createSQLQuery(query);

					if (params != null) {
						if (params.get("submittalFrom") != null) {
							queryObject.setParameter("submittalFrom", params.get("submittalFrom"));
						}
						if (params.get("submittalTo") != null) {
							queryObject.setParameter("submittalTo", params.get("submittalTo"));
						}
						if (params.get("userId") != null) {
							queryObject.setParameter("userId", params.get("userId"));
						}

					}
					System.out.println(query);
					return queryObject.list();
				}
			});
			return entities;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	@Transactional(readOnly = true, propagation = MANDATORY)
	public List<?> findBySQLQuery(final String query, final Map<String, Object> params) {
		List<?> entities = null;
		try {

			entities = (List<?>) getHibernateTemplate().execute(new HibernateCallback<Object>() {
				public Object doInHibernate(Session session) throws HibernateException {
					Query queryObject = session.createSQLQuery(query);

					if (params != null) {

						for (Map.Entry<String, Object> entry : params.entrySet()) {
							queryObject.setParameter(entry.getKey(), entry.getValue());
						}
					}
					log.info(queryObject.list().size() + " query object size ");
					return queryObject.list();
				}
			});

			/*
			 * Session session =
			 * getHibernateTemplate().getSessionFactory().getCurrentSession();
			 * Query sqlQuery = session.createSQLQuery(query);
			 * 
			 * if (params != null) {
			 * 
			 * for (Map.Entry<String, Object> entry : params.entrySet()) {
			 * sqlQuery.setParameter(entry.getKey(), entry.getValue()); } }
			 * entities = sqlQuery.list();
			 */

			log.debug(entities.size() + " object(s) retrieved by query");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return entities;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.dao.GenericDao#findByQuery(java.lang.String, int,
	 * int, java.util.Map)
	 */
	@Override
	public List<?> findByQuery(String query, int first, int pageSize, Map<String, Object> params) {
		// new Logic
		Query hqlQuery = null;
		try {
			log.info("From Dao");
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			hqlQuery = session.createQuery(query);
			if (params != null) {
				for (Map.Entry<String, Object> entry : params.entrySet()) {

					if (entry.getValue() instanceof Collection) {
						hqlQuery.setParameterList(entry.getKey(), (Collection<?>) entry.getValue());
					} else if (entry.getValue() instanceof Object[]) {
						hqlQuery.setParameterList(entry.getKey(), (Object[]) entry.getValue());
					} else {
						hqlQuery.setParameter(entry.getKey(), entry.getValue());
					}

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		try {
			if (first >= 0 && pageSize > 0) {
				hqlQuery.setFirstResult(first);
				hqlQuery.setMaxResults(pageSize);
			}

			List<T> jobOrders = hqlQuery.list();
			log.info("end findJobOrder Dao");
			return jobOrders;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.uralian.cgiats.dao.GenericDao#findCount(java.lang.String,
	 * java.util.Map)
	 */
	@Override
	public int findCount(String query, Map<String, Object> params) {
		Query hqlQuery = null;
		Integer numberOfRecords = null;
		try {
			// select count(*) from TableName tn where
			log.info("From Dao");
			Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
			hqlQuery = session.createQuery(query);
			if (params != null) {
				for (Map.Entry<String, Object> entry : params.entrySet()) {

					if (entry.getValue() instanceof Collection) {
						hqlQuery.setParameterList(entry.getKey(), (Collection<?>) entry.getValue());
					} else if (entry.getValue() instanceof Object[]) {
						hqlQuery.setParameterList(entry.getKey(), (Object[]) entry.getValue());
					} else {
						hqlQuery.setParameter(entry.getKey(), entry.getValue());
					}

				}
			}
			Long lgnumberOfRecords = (Long) hqlQuery.uniqueResult();
			if (lgnumberOfRecords != null) {
				numberOfRecords = lgnumberOfRecords.intValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return numberOfRecords;
	}

	public Object getObjectByCandidateInfoId(Integer candidateInfoId, Class<?> clazz) {
		Object obj = null;
		try {
			Criteria critirea = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(clazz);
			critirea.createAlias("candidateinfo", "candidateinfo");
			critirea.add(Restrictions.eq("candidateinfo.id", candidateInfoId));
			obj = critirea.uniqueResult();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

		return obj;
	}
	
	public void updateSQLQuery(String query) {
		try {
			SQLQuery sqlQuery = getHibernateTemplate().getSessionFactory().getCurrentSession().createSQLQuery(query);
			sqlQuery.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}

	}

}
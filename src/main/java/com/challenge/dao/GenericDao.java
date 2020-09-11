package com.challenge.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;

import com.challenge.util.HibernateUtil;

/**
 * 
 * @author Ricardo Braga
 *
 */
public class GenericDao {
	
	/**
	 * static
	 */
	private GenericDao() {
	}
	
	public static <E> void save(E entity) {
		Transaction transaction = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			// start a transaction
			transaction = session.beginTransaction();
			session.save(entity);
			transaction.commit();

			session.close();
		} catch (Exception e) {
			LoggerFactory.getLogger(GenericDao.class).error(e.getMessage(), e);
			if (transaction != null) {
				transaction.rollback();
			}
		}
	}


}

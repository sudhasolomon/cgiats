package com.uralian.cgiats.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.uralian.cgiats.dao.ReportBeanDao;
import com.uralian.cgiats.model.Candidate;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class ReportBeanDaoImpl implements ReportBeanDao {

	private static final Logger log = Logger.getLogger(ReportBeanDaoImpl.class);
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List getSubmittaslOfDm(Date fromDate, Date toDate, String selectedAdmName, List<String> selectedDms, List<String> selectedLocations) {
		try {
			StringBuffer str = new StringBuffer(
					"select a.created_by,a.office_location,sum(a.submitted_count) as submitted,sum(a.dmrej_count) as dmRejected,sum(a.accepted_count) as accepted, sum(a.backout_count) as backout,sum(a.confirmed_count) as confirmed,sum(a.inteviewing_count) as interviewing, sum(a.outofprj_count) as outOfproject,sum(a.rejected_count) as rejected, sum(a.started_count) as started,sum(a.open_count) as openJobOrders,sum(a.closed_count) as ClosedJobOrders from dm_adm_weekly_status_view a");

			str.append("  where a.user_role = 'DM' ");
			if (fromDate != null && toDate != null) {
				str.append(" and a.submital_date >=:fromDate and a.submital_date <=:toDate ");
			}
			if (selectedDms != null && selectedDms.size() > 0) {
				str.append(" and a.created_by in (:created_by)  ");
			}
			if (selectedLocations != null && selectedLocations.size() > 0) {
				str.append("  and a.office_location  in(:office_location) ");

			}
			str.append("    GROUP by a.created_by,a.office_location");

			Session session = sessionFactory.getCurrentSession();
			SQLQuery query = session.createSQLQuery(str.toString());
			if (fromDate != null && toDate != null) {
				query.setParameter("fromDate", fromDate);
				query.setParameter("toDate", toDate);
			}
			if (selectedDms != null && selectedDms.size() > 0) {
				query.setParameterList("created_by", selectedDms);
			}
			if (selectedLocations != null && selectedLocations.size() > 0) {
				query.setParameterList("office_location", selectedLocations);

			}
			return query.list();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List getRecWiseSubmittals(Date fromDate, Date toDate, String selectedDmName, String selectedLocation) {
		try {
			StringBuffer str = new StringBuffer(
					"select a.recut_name,sum(a.submitted_count) as submitted,sum(a.dmrej_count) as dmRejected,sum(a.accepted_count) as accepted, sum(a.backout_count) as backout,sum(a.confirmed_count) as confirmed,sum(a.inteviewing_count) as interviewing, sum(a.outofprj_count) as outOfproject,sum(a.rejected_count) as rejected, sum(a.started_count) as started from recruiter_wise_submital_stat_view a ");
			if (selectedDmName != null)
				str.append("  where  a.order_created_dm=:dm ");

			if (fromDate != null && toDate != null)
				str.append("  and a.submital_date>=:fromDate and a.submital_date<=:toDate ");

			str.append(" group by a.recut_name");

			Session session = sessionFactory.getCurrentSession();
			SQLQuery query = session.createSQLQuery(str.toString());
			if (fromDate != null && toDate != null) {
				query.setParameter("fromDate", fromDate);
				query.setParameter("toDate", toDate);
			}
			if (selectedDmName != null) {
				query.setParameter("dm", selectedDmName);
			}
			if (selectedLocation != null) {
				query.setParameter("office_location", selectedLocation);

			}
			return query.list();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public List<Candidate> getCandidateData(Date fromDate, Date toDate, String recName, String dmName) {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			String fromdate = sdf.format(fromDate);
			String todate = sdf.format(toDate);

			Session session = sessionFactory.getCurrentSession();

			StringBuffer str = new StringBuffer("select * from candidate c where c.candidate_id in (select s.candidate_id from submittal s,job_order j  ");

			if (recName != null)
				str.append(" where s.created_by =:createdBy   and  ");

			if (dmName != null && dmName.length() > 0)
				str.append(" j.order_id=s.order_id and j.created_by=:dmName ");

			if (fromDate != null && toDate != null)
				str.append(
						" and COALESCE(to_char(s.updated_on,'yyyy-mm-dd'),to_char(s.created_on,'yyyy-mm-dd'))>=:fromDate and  COALESCE(to_char(s.updated_on,'yyyy-mm-dd'),to_char(s.created_on,'yyyy-mm-dd'))<=:toDate)");
			log.info("from Date" + fromDate + "toDate" + toDate);
			SQLQuery query = session.createSQLQuery(str.toString());

			if (recName != null)
				query.setParameter("createdBy", recName);
			if (fromDate != null && toDate != null) {

				/*
				 * java.sql.Date fDate = new java.sql.Date(fromDate.getTime());
				 * java.sql.Date tDate = new java.sql.Date(toDate.getTime());
				 * 
				 * query.setParameter("fromDate", fDate);
				 * query.setParameter("toDate", tDate);
				 */
				query.setParameter("fromDate", fromdate);
				query.setParameter("toDate", todate);
			}
			if (dmName != null) {
				query.setParameter("dmName", dmName);
			}
			query.addEntity(Candidate.class);
			List<Candidate> list = query.list();

			return list;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		return null;
	}

}

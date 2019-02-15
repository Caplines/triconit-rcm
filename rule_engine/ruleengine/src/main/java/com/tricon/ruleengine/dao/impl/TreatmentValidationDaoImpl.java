package com.tricon.ruleengine.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.TreatmentValidationDao;
import com.tricon.ruleengine.model.db.EagleSoftDBDetails;
import com.tricon.ruleengine.model.db.GoogleSheets;
import com.tricon.ruleengine.model.db.MVPandVAP;
import com.tricon.ruleengine.model.db.Mappings;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.OneDriveApp;
import com.tricon.ruleengine.model.db.ReportDetail;
import com.tricon.ruleengine.model.db.Reports;
import com.tricon.ruleengine.model.db.Rules;

/**
 * @author Deepak.Dogra
 *
 */
@Repository
public class TreatmentValidationDaoImpl extends BaseDaoImpl implements TreatmentValidationDao {

	@Override
	public GoogleSheets getSheetByAppSheetId(int id) {
	 return (GoogleSheets) getEntityById(GoogleSheets.class, id);
	}

	@Override
	public List<Rules> getAllActiveRules() {
		
	 List<Rules> list = (List<Rules>)(List<?>)getEntitiesByColumnName(Rules.class, "active", 1);	
	 return list;
	}

	@Override
	public Reports getReportsByTreamentPlanIdAndOffice(String treatmentPlanId,Office off) {

		Reports rep=null;
		Session session = getSession();
		try {
			Criteria criteria = session.createCriteria(Reports.class);
			criteria.add(Restrictions.eq("treatementPlanId", treatmentPlanId));
			criteria.createAlias("office", "off");
			criteria.add(Restrictions.eq("off.uuid", off.getUuid()));
			rep = (Reports) criteria.uniqueResult();
			
		} finally {
			closeSession(session);

		}
		return rep;
	}

	@Override
	public Reports getReportsByIVFIdAndOffice(String ivfId,Office off) {

		Reports rep=null;
		//Never use me issue no unique data will be there
		/*
		Session session = getSession();
		try {
			Criteria criteria = session.createCriteria(Reports.class);
			criteria.add(Restrictions.eq("ivfFormId", ivfId));
			criteria.createAlias("office", "off");
			criteria.add(Restrictions.eq("off.uuid", off.getUuid()));
			rep = (Reports) criteria.uniqueResult();
			
		} finally {
			closeSession(session);

		}
		*/
		return rep;
	}
	
	@Override
     public Reports getReportsByTPIdIVFIDAndOffice(String tpid,String ivfId,Office office) {

		Reports rep=null;
		Session session = getSession();
		try {
			Criteria criteria = session.createCriteria(Reports.class);
			criteria.add(Restrictions.eq("treatementPlanId", tpid));
			criteria.add(Restrictions.eq("ivfFormId", ivfId));
			criteria.createAlias("office", "off");
			criteria.add(Restrictions.eq("off.uuid", office.getUuid()));
			rep = (Reports) criteria.uniqueResult();
			
		} finally {
			closeSession(session);

		}
		return rep;
	}


	@Override
	public Serializable saveReports(Reports reports) {
		// TODO Auto-generated method stub
		return saveEntiy(reports);
	}

	@Override
	public Reports getReportsByName(String name) {
	 return (Reports) getEntityById(GoogleSheets.class, name);
	}

	@Override
	public Serializable saveReportDestail(ReportDetail reports) {
		// TODO Auto-generated method stub
		return saveEntiy(reports);
	}

	@Override
	public void updateReportDate(Reports reports) {
		
		updateEntity(reports);
		
	
	}

	@Override
	public List<GoogleSheets> getAllGoogleSheet() {
		List<GoogleSheets> list = (List<GoogleSheets>)(List<?>) getWholeEntity(GoogleSheets.class);
		return list;
	}

	@Override
	public List<Mappings> getAllMappings() {
		Session session = getSession();
		List<Mappings> list = null;
		try {
			Criteria criteria = session.createCriteria(Mappings.class);
			criteria.createAlias("adaCodes", "ad");
			criteria.createAlias("serviceCodeCategory", "sa");
			list=criteria.list();
			
		} finally {
			closeSession(session);

		}
		return list;

	}

	@Override
	public List<GoogleSheets> getAllGoogleSheetByOffice(Office off) {
		Session session = getSession();
		List<GoogleSheets> list = null;
		try {
			Criteria criteria = session.createCriteria(GoogleSheets.class);
			criteria.createAlias("office", "off");//
			criteria.add(Restrictions.eq("off.uuid", off.getUuid()));
			list=criteria.list();
			
		} finally {
			closeSession(session);

		}
		return list;
	}


	@Override
	public EagleSoftDBDetails getESDBDetailsByOffice(Office off) {
		Session session = getSession();
		EagleSoftDBDetails es = null;
		try {
			Criteria criteria = session.createCriteria(EagleSoftDBDetails.class);
			criteria.createAlias("office", "off");//
			criteria.add(Restrictions.eq("off.uuid", off.getUuid()));
			criteria.add(Restrictions.eq("server", true));
			es=(EagleSoftDBDetails) criteria.uniqueResult();
			
		} finally {
			closeSession(session);

		}
		return es;
	}

	@Override
	public List<MVPandVAP> getAllMVPVAP() {
		Session session = getSession();
		List<MVPandVAP> mvpvap = null;
		try {
			Criteria criteria = session.createCriteria(MVPandVAP.class);
			mvpvap=(List<MVPandVAP>) criteria.list();
			
		} finally {
			closeSession(session);

		}
		return mvpvap;
	}
}

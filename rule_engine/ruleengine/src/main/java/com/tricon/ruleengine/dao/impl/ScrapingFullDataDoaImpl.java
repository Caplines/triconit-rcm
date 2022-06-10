package com.tricon.ruleengine.dao.impl;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import com.tricon.ruleengine.dao.ScrapingFullDataDoa;
import com.tricon.ruleengine.dto.ReportResponseDto;
import com.tricon.ruleengine.dto.ScrappingFullDataDetailDto;
import com.tricon.ruleengine.dto.ScrappingFullDataDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.ScrappingFullDataManagment;
import com.tricon.ruleengine.model.db.ScrappingFullDataManagmentProcess;
import com.tricon.ruleengine.model.db.ScrappingSiteDetailsFull;
import com.tricon.ruleengine.model.db.ScrappingSiteFull;
import com.tricon.ruleengine.model.db.ScrappingSiteFullMaster;
import com.tricon.ruleengine.model.db.TaxMapping;

@Repository
public class ScrapingFullDataDoaImpl extends BaseDaoImpl implements ScrapingFullDataDoa {

	@Override
	public List<ScrappingFullDataDto> getSiteNames() {
		Session session = getSession();
		List<ScrappingFullDataDto> list = null;
		try {
			Criteria criteria = session.createCriteria(ScrappingSiteFull.class);
			ProjectionList pjList = Projections.projectionList();
			pjList.add(Projections.property("id"), "id");
			pjList.add(Projections.property("siteName"), "name");
			pjList.add(Projections.property("siteUrl"), "url");
			criteria.setProjection(pjList);
			criteria.setResultTransformer(Transformers.aliasToBean(ScrappingFullDataDto.class));
			list = (List<ScrappingFullDataDto>) criteria.list();
		} finally {
			closeSession(session);

		}
		return list;

	}
	
	@Override
	public List<ScrappingFullDataDto> getSiteNamesBySiteType(String type) {
		Session session = getSession();
		List<ScrappingFullDataDto> list = null;
		try {
			Criteria criteria = session.createCriteria(ScrappingSiteFull.class);
			criteria.add(Restrictions.eq("siteType", type));
			ProjectionList pjList = Projections.projectionList();
			pjList.add(Projections.property("id"), "id");
			pjList.add(Projections.property("siteName"), "name");
			pjList.add(Projections.property("siteUrl"), "url");
			criteria.setProjection(pjList);
			criteria.setResultTransformer(Transformers.aliasToBean(ScrappingFullDataDto.class));
			list = (List<ScrappingFullDataDto>) criteria.list();
		} finally {
			closeSession(session);

		}
		return list;

	}

	@Override
	public ScrappingFullDataDetailDto getScrappingDetails(int siteId, Office off) {
		Session session = getSession();
		ScrappingFullDataDetailDto scrappingFullDataDetailDto = null;
		try {
			Criteria criteria = session.createCriteria(ScrappingSiteDetailsFull.class);
			criteria.createAlias("scrappingSite", "s");
			criteria.createAlias("office", "office");
			criteria.createAlias("master", "sm");
			criteria.add(Restrictions.eq("s.id", siteId));
			criteria.add(Restrictions.eq("office.uuid", off.getUuid()));
			criteria.add(Restrictions.eq("scrappingSite.id", siteId));
			criteria.add(Restrictions.eq("sm.scrappingSite.id", siteId));
			ProjectionList pjList = Projections.projectionList();
			pjList.add(Projections.property("id"), "siteDetailId");
			pjList.add(Projections.property("scrappingSite.id"), "siteId");
			pjList.add(Projections.property("userName"), "userName");
			pjList.add(Projections.property("password"), "password");
			pjList.add(Projections.property("googleSheetId"), "googleSheetIdDb");
			pjList.add(Projections.property("googleSubId"),"googleSubId");
			
			//pjList.add(Projections.property("googleSheetId"), "sheetId");
			//pjList.add(Projections.property("googleSubId"), "sheetSubId");

			pjList.add(Projections.property("sm.patientId"), "patientId");
			pjList.add(Projections.property("sm.firstName"), "firstName");
			pjList.add(Projections.property("sm.lastName"), "lastName");
			pjList.add(Projections.property("sm.dob"), "dob");
			pjList.add(Projections.property("sm.enrolleeId"), "enrolleeId");
			pjList.add(Projections.property("sm.memberId"), "memberId");
			
			pjList.add(Projections.property("sm.ssnNumber"), "ssnNumber");
			pjList.add(Projections.property("sm.locationProvider"), "locationProvider");
			
			pjList.add(Projections.property("sm.gradePay"), "gradePay");
			pjList.add(Projections.property("sm.subscribersFirstName"), "subscribersFirstName");
			pjList.add(Projections.property("sm.subscribersLastName"), "subscribersLastName");
			pjList.add(Projections.property("sm.subscribersDob"), "subscribersDob");
			pjList.add(Projections.property("sm.otp"), "otp");
			pjList.add(Projections.property("sm.googleSheetId"), "googleSheetId");
			pjList.add(Projections.property("scrapSubType"), "scrapSubType");
			criteria.setProjection(pjList);
			criteria.setResultTransformer(Transformers.aliasToBean(ScrappingFullDataDetailDto.class));
			scrappingFullDataDetailDto = (ScrappingFullDataDetailDto) criteria.uniqueResult();
		} finally {
			closeSession(session);

		}
		return scrappingFullDataDetailDto;
	}
	////////////////////

	@Override
	public ScrappingSiteDetailsFull findScrappingDetailsById(int siteDetailId) {

		return (ScrappingSiteDetailsFull) getEntityById(ScrappingSiteDetailsFull.class, siteDetailId);
	}

	@Override
	public Serializable saveScrappingDetailsById(ScrappingSiteDetailsFull sdFull) {
		return saveEntiy(sdFull);
	}

	@Override
	public void updateScrappingDetailsById(ScrappingSiteDetailsFull sdFull) {

		updateEntity(sdFull);
	}

	@Override
	public ScrappingSiteFull findScrappingSiteFullById(int siteId) {
		return (ScrappingSiteFull) getEntityById(ScrappingSiteFull.class, siteId);
	}

	@Override
	public int findMaxProxyPortScrappinSiteDetailsFull() {
		
		Session session =null;
		Object v=null;
		try{
			session =getSession();
		
		String query="select max(proxy_port) from scrapping_site_details_full";
		v=session.createSQLQuery(query).uniqueResult();
		
		}finally {
			closeSession(session);
		}
		if (v==null ) return 0;
		else 
		return Integer.parseInt(v.toString());
	}
	
	
	@Override
	public void updateScrappingSiteRunningStatusAll() {
				Session session = null;
				try {
					session = getSession();
					SQLQuery query = session.createSQLQuery("update scrapping_site_details_full set is_running=false");
					query.executeUpdate();

				} finally {
					closeSession(session);

				}
	}


	@Override
	public String findAnyRunnigfullScrapBSiteName(String name) {
				Session session = null;
				String s="";
				try {
					session = getSession();
					SQLQuery query = session.createSQLQuery("select o.name from scrapping_full_data f , scrapping_site_details_full fu,office o where fu.scrapping_site_id=f.id"
							+ " and o.uuid= fu.office_id  and  is_running =true and site_name='"+name+"'");
			          Object v=  query.uniqueResult();
			              if (v==null ) s= "";
			      		else 
			      		s= ((String)v);

				} finally {
					closeSession(session);

				}
				return s;
	}
	
	@Override
	public ScrappingFullDataManagment getScrappingFullDataManagmentData() {
		
		return (ScrappingFullDataManagment) getWholeEntity(ScrappingFullDataManagment.class).get(0);
	}
	
	@Override
	public void increasecrapCount(ScrappingFullDataManagment manage) {
		
		updateEntity(manage);
		
	}
	
	@Override
	public void updateScrappingSiteManagement() {
	
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery("update scrapping_full_data_manage set process_count=0");
			query.executeUpdate();

		} finally {
			closeSession(session);

		}

	}

	@Override
	public Serializable createScrappingSiteManagementProcess(ScrappingFullDataManagmentProcess manageP) {

	return	saveEntiy(manageP);
		
	}

	@Override
	public ScrappingFullDataManagmentProcess getScrappingFullDataManagmentDataProcess(int id) {
		// TODO Auto-generated method stub
		return (ScrappingFullDataManagmentProcess)getEntityById(ScrappingFullDataManagmentProcess.class, id);
	}
	
	@Override
    public void updateScrappingFullDataManagmentProcess(ScrappingFullDataManagmentProcess manage) {
		
		updateEntity(manage);
		
	}

	@Override
	public String getTaxmapping(Office office, String type) {
		Session session = getSession();
		Object object = null;
		try {
			Criteria criteria = session.createCriteria(TaxMapping.class);
			criteria.add(Restrictions.eq("scType", type));
			criteria.createAlias("office", "office");
			criteria.add(Restrictions.eq("office.uuid", office.getUuid()));
			ProjectionList pjList = Projections.projectionList();
			pjList.add(Projections.property("taxId"), "taxId");
			criteria.setProjection(pjList);
			object =  criteria.uniqueResult();
			
		} finally {
			closeSession(session);

		}
		if (object==null) object="NOT SET(tax Mapping table)";
		return (String)object;
	}

	@Override
	public ScrappingSiteFullMaster getScrappingSiteFullMaster(int scrapSiteId) {
		Session session = getSession();
		Object object = null;
		try {
			Criteria criteria = session.createCriteria(ScrappingSiteFullMaster.class);
			criteria.createAlias("scrappingSite", "scrappingSite");
			criteria.add(Restrictions.eq("scrappingSite.id", scrapSiteId));
			object =  criteria.uniqueResult();
			
		} finally {
			closeSession(session);

		}
		
		return (ScrappingSiteFullMaster)object;
	}
	
	
}

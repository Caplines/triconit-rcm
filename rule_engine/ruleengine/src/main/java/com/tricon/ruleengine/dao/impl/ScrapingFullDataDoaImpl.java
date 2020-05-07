package com.tricon.ruleengine.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
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
import com.tricon.ruleengine.model.db.ScrappingSiteDetailsFull;
import com.tricon.ruleengine.model.db.ScrappingSiteFull;

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
	public int findMaxProxyPort(int siteDetailId) {
		
		Session session = getSession();
		String query="select max(proxy_port) from scrapping_site_details_full";
		Object v=session.createSQLQuery(query).uniqueResult();
		if (v==null ) return 0;
		else 
		return ((Integer)v).intValue();
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

}

package com.tricon.ruleengine.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.ScrappingDao;
import com.tricon.ruleengine.dto.OfficeDto;
import com.tricon.ruleengine.dto.ScrappingSiteDetailsDto;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.ScrappingSite;
import com.tricon.ruleengine.model.db.ScrappingSiteDetails;

@Repository
public class ScrappingDaoImpl extends BaseDaoImpl implements ScrappingDao {

	@Override
	public ScrappingSite getScrappingSiteDetails(int siteId, Office office) {
		// TODO Auto-generated method stub
		Session session = null;
		ScrappingSite s = null;
		try {
			session = getSession();

			
			DetachedCriteria subquery = DetachedCriteria.forClass(ScrappingSiteDetails.class)
				    .createAlias("office", "off")
				    .add(Restrictions.eq("off.uuid", office.getUuid()));
			subquery.setProjection(Projections.property("scrappingSite.id"));
			
			Criteria criteria = session.createCriteria(ScrappingSite.class);
			//criteria.INNER_JOIN
			criteria.setFetchMode("siteSiteDetails", FetchMode.JOIN);
			criteria.createAlias("siteSiteDetails", "sd");
			//criteria.createAlias("sd.office", "offf");
			criteria.add(Subqueries.propertyIn("id",subquery));
			//criteria.add(Restrictions.eq("offf.uuid", office.getUuid()));
			criteria.add(Restrictions.eq("id", siteId));
			s = (ScrappingSite) criteria.uniqueResult();
			//Hibernate.initialize(s.getSiteSiteDetails());
		} finally {
			closeSession(session);

		}
		
		return s;
	}

	@Override
	public ScrappingSiteDetails getScrappingSiteDetailsDetail(int siteDetailId, Office office) {
		// TODO Auto-generated method stub
		Session session = null;
		ScrappingSiteDetails s = null;
		try {
			session = getSession();
			Criteria criteria = session.createCriteria(ScrappingSiteDetails.class);
			criteria.createAlias("office", "office");
			criteria.createAlias("scrappingSite", "scrappingSite");
			
			criteria.add(Restrictions.eq("office.uuid", office.getUuid()));
			criteria.add(Restrictions.eq("scrappingSite.id", siteDetailId));
			s = (ScrappingSiteDetails) criteria.uniqueResult();

		} finally {
			closeSession(session);

		}
		return s;
	}

	@Override
	public ScrappingSiteDetailsDto getScrappingSiteDetailsDetailSDto(int siteDetailId, Office office) {
		// TODO Auto-generated method stub
		Session session = null;
		ScrappingSiteDetailsDto s = null;
		try {
			session = getSession();
			Criteria criteria = session.createCriteria(ScrappingSiteDetails.class);
			criteria.createAlias("office", "office");
			criteria.createAlias("scrappingSite", "scrappingSite");
			
			criteria.add(Restrictions.eq("office.uuid", office.getUuid()));
			criteria.add(Restrictions.eq("scrappingSite.id", siteDetailId));
			ProjectionList pjList = Projections.projectionList();
			pjList.add(Projections.property("userName"), "userName");
			pjList.add(Projections.property("password"), "password");
			pjList.add(Projections.property("googleSheetId"), "googleSheetId");
			pjList.add(Projections.property("googleSheetName"), "googleSheetName");
			pjList.add(Projections.property("googleSubId"), "googleSubId");
			pjList.add(Projections.property("locationProvider"), "locationProvider");
			pjList.add(Projections.property("scrappingSite.id"), "sid");
			
			criteria.setProjection(pjList);
			criteria.setResultTransformer(Transformers.aliasToBean(ScrappingSiteDetailsDto.class));
			s = (ScrappingSiteDetailsDto) criteria.uniqueResult();

		} finally {
			closeSession(session);

		}
		return s;
	}

	@Override
	public void updateScrappingSiteRunningStatus(ScrappingSiteDetails sd) {
		// TODO Auto-generated method stub
		updateEntity(sd);
	}

	@Override
	public void updateScrappingSiteRunningStatusAll() {
				Session session = null;
				try {
					session = getSession();
					SQLQuery query = session.createSQLQuery("update scrapping_site_details set is_running=false");
					query.executeUpdate();

				} finally {
					closeSession(session);

				}
	}

	@Override
	public List<ScrappingSiteDetails> getScrappingSiteDetailDetails() {
		// TODO Auto-generated method stub
		return (List<ScrappingSiteDetails>) (List<?>) getWholeEntity(ScrappingSiteDetails.class);
	}

}

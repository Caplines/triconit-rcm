package com.tricon.ruleengine.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.tricon.ruleengine.dao.SharePointDao;
import com.tricon.ruleengine.model.db.OneDriveApp;
import com.tricon.ruleengine.model.db.OneDriveFile;

@Repository
public class SharePointDaoImpl extends BaseDaoImpl implements SharePointDao {


	

	@Override
	public OneDriveApp getOneDriveAppDetailsByOfficeId(String officeId) {
		// TODO Auto-generated method stub
		Session session = getSession();
		OneDriveApp object = null;
		try {
			Criteria criteria = session.createCriteria(OneDriveApp.class);
			criteria.createAlias("office", "off");
			criteria.add(Restrictions.eq("off.uuid", officeId));
			object = (OneDriveApp) criteria.uniqueResult();
			
		} finally {
			closeSession(session);

		}
		return object;

	}

	@Override
	public void updateOneDrive(OneDriveApp oneD) {
		updateEntity(oneD);
		
	}

	@Override
	public OneDriveApp getOneDriveAppDetailsByAuthCode(String authCode) {
		// TODO Auto-generated method stub
		return (OneDriveApp)getEntityByColumnName(OneDriveApp.class, "authToken", authCode);
	}

	@Override
	public List<OneDriveFile> getOneDriveFileByOfficeId(String officeId) {
		Session session = getSession();
		List<OneDriveFile> object = null;
		try {
			Criteria criteria = session.createCriteria(OneDriveFile.class);
			criteria.createAlias("office", "off");
			criteria.add(Restrictions.eq("off.uuid", officeId));
			object = (List<OneDriveFile>) criteria.list();
			
		} finally {
			closeSession(session);

		}
		return object;
	}
	

}

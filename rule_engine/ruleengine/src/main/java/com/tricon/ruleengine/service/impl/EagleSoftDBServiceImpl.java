package com.tricon.ruleengine.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.TreatmentValidationDao;
import com.tricon.ruleengine.model.db.EagleSoftDBDetails;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.service.EagleSoftDBService;


@Transactional
@Service
public class EagleSoftDBServiceImpl implements EagleSoftDBService{

	@Autowired
	TreatmentValidationDao tvd;
	
	@Override
	public EagleSoftDBDetails getESDBDetailsByOffice(Office office) {
		// TODO Auto-generated method stub
		return tvd.getESDBDetailsByOffice(office);
	}
	
	

}

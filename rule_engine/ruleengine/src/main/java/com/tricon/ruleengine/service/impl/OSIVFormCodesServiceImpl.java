package com.tricon.ruleengine.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.OSIVFormCodesDao;
import com.tricon.ruleengine.model.db.OSIVFormCodes;
import com.tricon.ruleengine.service.OSIVFormCodesService;

@Service
public class OSIVFormCodesServiceImpl implements OSIVFormCodesService{
	
	@Autowired
	OSIVFormCodesDao dao;
	
	@Override
	public List<OSIVFormCodes> getAllActiveOSIVCodes() {
		// TODO Auto-generated method stub
		return dao.getAllActiveOSIVCodes();
	}

}



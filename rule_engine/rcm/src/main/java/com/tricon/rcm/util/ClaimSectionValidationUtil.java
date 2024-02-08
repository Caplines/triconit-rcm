package com.tricon.rcm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tricon.rcm.dto.AppealInformationDto;
import com.tricon.rcm.dto.ClaimLevelInformationDto;

@Service
public class ClaimSectionValidationUtil {

	private final Logger logger = LoggerFactory.getLogger(ClaimSectionValidationUtil.class);
	
	public boolean validationForClaimInfoSectionFields(ClaimLevelInformationDto claimInfoModel) {
		logger.info("ClaimInfoModel->" +claimInfoModel);
		if(claimInfoModel==null) return false;
		return true;
	}
	
	public boolean validationForAppealInfoSectionFields(AppealInformationDto appealInfoModel) {
		logger.info("AppealInformationDto->" +appealInfoModel);
		if(appealInfoModel==null) return false;
		return true;
	}
}

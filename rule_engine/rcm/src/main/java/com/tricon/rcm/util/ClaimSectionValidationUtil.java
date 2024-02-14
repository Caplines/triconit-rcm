package com.tricon.rcm.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tricon.rcm.dto.AppealInformationDto;
import com.tricon.rcm.dto.ClaimLevelInformationDto;

@Service
public class ClaimSectionValidationUtil {

	private final Logger logger = LoggerFactory.getLogger(ClaimSectionValidationUtil.class);
	
	public boolean validationForClaimInfoSectionFields(ClaimLevelInformationDto claimInfoModel) {
		logger.info("ClaimInfoModel->" + claimInfoModel);
		if (claimInfoModel == null)
			return false;
		if (!StringUtils.isNoneBlank(claimInfoModel.getClaimId())
				|| !StringUtils.isNoneBlank(claimInfoModel.getClaimPassFirstGo())
				|| !StringUtils.isNoneBlank(claimInfoModel.getClaimProcessingDate())
				|| !StringUtils.isNoneBlank(claimInfoModel.getClaimStatusEs())
				|| !StringUtils.isNoneBlank(claimInfoModel.getClaimStatusRcm())
				|| !StringUtils.isNoneBlank(claimInfoModel.getInitialDenial())
				|| !StringUtils.isNoneBlank(claimInfoModel.getNetwork())) {
			return false;
		}

		return true;
	}

	public boolean validationForAppealInfoSectionFields(AppealInformationDto appealInfoModel) {
		logger.info("AppealInformationDto->" + appealInfoModel);
		if (appealInfoModel == null)
			return false;
		if (!StringUtils.isNoneBlank(appealInfoModel.getAiToolUsed())
				|| !StringUtils.isNoneBlank(appealInfoModel.getAppealDocument())
				|| !StringUtils.isNoneBlank(appealInfoModel.getModeOfAppeal())
				|| !StringUtils.isNoneBlank(appealInfoModel.getRemarks())) {
			return false;
		}
		return true;
	}
}

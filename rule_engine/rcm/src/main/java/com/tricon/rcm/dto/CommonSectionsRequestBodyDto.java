package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class CommonSectionsRequestBodyDto {
	
	private String claimUuid;
	private ClaimLevelInformationDto claimInfoModel;
	private AppealInformationDto appealInfoModel;
	private EOBDto eobInfoModel;
	private PaymentInformationSectionDto paymentInformationInfoModel;
	private ServiceLevelInformationDto serviceLevelInformationInfoModel;
	private RcmFollowUpInsuranceDto rcmFollowUpInsuranceInfoModel;
	private boolean finalSubmit;
	private boolean moveToNextTeam;

}

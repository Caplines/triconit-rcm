package com.tricon.rcm.dto;

import lombok.Data;

@Data
public class CommonSectionsRequestBodyDto {
	
	private String claimUuid;
	private ClaimLevelInformationDto claimInfoModel;
	private AppealInformationDto appealInfoModel;
	private PatientPaymentSectionDto patientPaymentInfoModel;
	private EOBDto eobInfoModel;
	private PaymentInformationSectionDto paymentInformationInfoModel;
	private ServiceLevelInformationDto serviceLevelInformationInfoModel;
	private RcmFollowUpInsuranceDto rcmFollowUpInsuranceInfoModel;
	private RcmPatientStatementDto rcmPatientStatementInfoModel;
	private CurrentStatusAndNextActionDto nextActionRequiredInfoModel;
	private RcmPatientCommunicationDto patientCommunicationInfoModel;
	private CollectionAgencyDto collectionAgencyInfoModel;
	private  RebillingDto rebillingInfoModel;
	private RequestRebillingDto requestRebillingInfoModel;
	private boolean finalSubmit;
	private boolean moveToNextTeam;

}

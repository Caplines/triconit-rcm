package com.tricon.rcm.util;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tricon.rcm.dto.AppealInformationDto;
import com.tricon.rcm.dto.ClaimLevelInformationDto;
import com.tricon.rcm.dto.EOBDto;
import com.tricon.rcm.dto.PaymentInformationSectionDto;

@Service
public class ClaimSectionValidationUtil {

	private final Logger logger = LoggerFactory.getLogger(ClaimSectionValidationUtil.class);
	
	public boolean validationForClaimInfoSectionFields(ClaimLevelInformationDto claimInfoModel) {
		logger.info("ClaimInfoModel->" + claimInfoModel);
		if (claimInfoModel == null)
			return false;
		
		//For ClaimId
		if (!StringUtils.isNoneBlank(claimInfoModel.getClaimId())) return false;
		
		//For ClaimPassFirstGo
		
		else if (!StringUtils.isNoneBlank(claimInfoModel.getClaimPassFirstGo())) return false;
		
		//For Claim Processing Date
		
		else if (!StringUtils.isNoneBlank(claimInfoModel.getClaimProcessingDate())) return false;
		
		//For ClaimStatus ER
		
		else if ( !StringUtils.isNoneBlank(claimInfoModel.getClaimStatusEs())) return false;
		
		//For ClaimStatus RCM
		
		else if (!StringUtils.isNoneBlank(claimInfoModel.getClaimStatusRcm())) return false;
		
		
		//For InitialDenial
		
		else if (!StringUtils.isNoneBlank(claimInfoModel.getInitialDenial())) return false;
		
		//For Network
		
		else if (!StringUtils.isNoneBlank(claimInfoModel.getNetwork())) return false;

		else return true;
	}

	public boolean validationForAppealInfoSectionFields(AppealInformationDto appealInfoModel) {
		logger.info("AppealInformationDto->" + appealInfoModel);
		if (appealInfoModel == null)
			return false;
		
		//For AI Tool Used
		if (!StringUtils.isNoneBlank(appealInfoModel.getAiToolUsed())) return false;
		
		//For Appeal Document
		
		else if (!StringUtils.isNoneBlank(appealInfoModel.getAppealDocument())) return false;
		
		//For Mode of Appeal
		
		else if (!StringUtils.isNoneBlank(appealInfoModel.getModeOfAppeal())) return false;
		
		//For Remarks
		
		else if (!StringUtils.isNoneBlank(appealInfoModel.getRemarks())) return false;
	
		else return true;
	}

//	public boolean validationForPatientPaymentSectionFields(PatientPaymentSectionDto patientPaymentInfoModel) {
//		logger.info("PatientPaymentSectionDto->" + patientPaymentInfoModel);
//		if (patientPaymentInfoModel == null)
//			return false;
//		
//		//For Amount Collected Claims
//		
//		if (!StringUtils.isNoneBlank(patientPaymentInfoModel.getAmountCollectedClaims())) return false;
//		
//		//For DateOfPayments
//		
//		else if (!StringUtils.isNoneBlank(patientPaymentInfoModel.getDateOfPayment())) return false;
//		
//		//For DueBalance In PMS
//		
//		else if (!StringUtils.isNoneBlank(patientPaymentInfoModel.getDueBalanceInPMS())) return false;
//		
//		//For Mode Of Payment
//		
//		else if (!StringUtils.isNoneBlank(patientPaymentInfoModel.getModeOfPayment())) return false;
//		
//		//For PostedInPMS
//		
//		else if ( !StringUtils.isNoneBlank(patientPaymentInfoModel.getPostedInPMS())) return false;
//		
//		else return true;
//	}

	public boolean validationForEOBSectionFields(EOBDto eobInfoModel) {
		logger.info("EOBDto->" + eobInfoModel);
		if (eobInfoModel == null)
			return false;
		if (!StringUtils.isNoneBlank(eobInfoModel.getEobLink())) {
			return false;
		}
		return true;
	}

	public boolean validationForInsurancePaymentInformationSectionFields(
			PaymentInformationSectionDto paymentInformationInfoModel) {
		logger.info("PaymentInformationSectionDto->" + paymentInformationInfoModel);
		if (paymentInformationInfoModel == null ||!StringUtils.isNoneBlank(paymentInformationInfoModel.getPaidAmount()))
			return false;

		// if paid amount is 0 then no need to save data in this section
		if (!StringUtils.isNoneBlank(paymentInformationInfoModel.getPaidAmount())
				&& Double.parseDouble(paymentInformationInfoModel.getPaidAmount()) == 0.0) {
			return true;
		}
		
		//if payment mode is not check then checkdeliverTo field is not editable
		else if(!paymentInformationInfoModel.getPaymentMode().equals(Constants.PAYMENT_MODE_CHECK) && StringUtils.isNoneBlank(paymentInformationInfoModel.getCheckDeliverTo())) {
			
			return false;
		}
		
		//For AmountDateReceivedInBank
		else if(!StringUtils.isNoneBlank(paymentInformationInfoModel.getAmountDateReceivedInBank()))return false;
		
		//For AmountPostedInEs
		else if(!StringUtils.isNoneBlank(paymentInformationInfoModel.getAmountPostedInEs()))return false;
		
		//For AmountReceivedInBank
		else if(!StringUtils.isNoneBlank(paymentInformationInfoModel.getAmountReceivedInBank()))return false;
		
		//For CheckCashDate
		else if(!StringUtils.isNoneBlank(paymentInformationInfoModel.getCheckCashDate()))return false;
		
		//For CheckDeliverTo
		else if(!StringUtils.isNoneBlank(paymentInformationInfoModel.getCheckDeliverTo()))return false;
		
		//For PaymentIssueTo
		else if(!StringUtils.isNoneBlank(paymentInformationInfoModel.getPaymentIssueTo()))return false;
		
		//For CheckNumber
		else if(!StringUtils.isNoneBlank(paymentInformationInfoModel.getCheckNumber()))return false;
	
		else return true;
	}
}

package com.tricon.rcm.util;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tricon.rcm.dto.AppealInformationDto;
import com.tricon.rcm.dto.ClaimLevelInformationDto;
import com.tricon.rcm.dto.EOBDto;
import com.tricon.rcm.dto.PaymentInformationSectionDto;
import com.tricon.rcm.dto.ServiceLevelInformationDto;

@Service
public class ClaimSectionValidationUtil {

	private final Logger logger = LoggerFactory.getLogger(ClaimSectionValidationUtil.class);
	
	public boolean validationForClaimInfoSectionFields(ClaimLevelInformationDto claimInfoModel) {
		logger.info("ClaimInfoModel->" + claimInfoModel);
		boolean isValid = true;
		if (claimInfoModel == null) {
			return false;
		}

		// For ClaimId
		if (!StringUtils.isNoneBlank(claimInfoModel.getClaimId())) {
			isValid = false;
		}

		// For ClaimPassFirstGo

		if (!StringUtils.isNoneBlank(claimInfoModel.getClaimPassFirstGo())) {
			isValid = false;
		}

		// For Claim Processing Date

		if (!StringUtils.isNoneBlank(claimInfoModel.getClaimProcessingDate())) {
			isValid = false;
		}

		// For ClaimStatus ER

		if (!StringUtils.isNoneBlank(claimInfoModel.getClaimStatusEs())) {
			isValid = false;
		}

		// For ClaimStatus RCM

		if (!StringUtils.isNoneBlank(claimInfoModel.getClaimStatusRcm())) {

		}

		// For InitialDenial

		if (!StringUtils.isNoneBlank(claimInfoModel.getInitialDenial())) {
			isValid = false;
		}

		// For Network

		if (!StringUtils.isNoneBlank(claimInfoModel.getNetwork())) {
			isValid = false;
		}

		return isValid;
	}

	public boolean validationForAppealInfoSectionFields(AppealInformationDto appealInfoModel) {
		logger.info("AppealInformationDto->" + appealInfoModel);
		boolean isValid = true;
		if (appealInfoModel == null) {
			return false;
		}

		// For AI Tool Used
		if (!StringUtils.isNoneBlank(appealInfoModel.getAiToolUsed())) {
			isValid = false;
		}

		// For Appeal Document

		if (!StringUtils.isNoneBlank(appealInfoModel.getAppealDocument())) {
			isValid = false;
		}

		// For Mode of Appeal

		if (!StringUtils.isNoneBlank(appealInfoModel.getModeOfAppeal())) {
			isValid = false;
		}

		// For Remarks

		if (!StringUtils.isNoneBlank(appealInfoModel.getRemarks())) {
			isValid = false;
		}

		return isValid;
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
		boolean isValid = true;
		if (eobInfoModel == null) {
			return false;
		}
		if (!StringUtils.isNoneBlank(eobInfoModel.getEobLink())) {
			isValid = false;
		}
		return isValid;
	}

	public boolean validationForInsurancePaymentInformationSectionFields(
			PaymentInformationSectionDto paymentInformationInfoModel) {
		logger.info("PaymentInformationSectionDto->" + paymentInformationInfoModel);

		boolean isValid = true;
		if (paymentInformationInfoModel == null) {
			return false;
		}
		// if paid amount is 0 then no need to save data in this section
		if (paymentInformationInfoModel.getPaidAmount() == 0.0) {
			return true;
		}

		// For payment mode
		if (!StringUtils.isNoneBlank(paymentInformationInfoModel.getPaymentMode())) {
			isValid = false;
		}

		// if payment mode is check then
		if (paymentInformationInfoModel.getPaymentMode().equals(Constants.PAYMENT_MODE_CHECK)) {

			// For CheckDeliverTo
			if (StringUtils.isNoneBlank(paymentInformationInfoModel.getCheckDeliverTo()))
				isValid = false;
			// For CheckCashDate
			if (!StringUtils.isNoneBlank(paymentInformationInfoModel.getCheckCashDate()))
				isValid = false;
			// For CheckNumber
			if (!StringUtils.isNoneBlank(paymentInformationInfoModel.getCheckNumber()))
				isValid = false;

		}
		if (paymentInformationInfoModel.getPaymentMode().equals(Constants.PAYMENT_MODE_EFT)
				|| paymentInformationInfoModel.getPaymentMode().equals(Constants.PAYMENT_MODE_VCC)) {

			// For CheckCashDate
			if (!StringUtils.isNoneBlank(paymentInformationInfoModel.getCheckCashDate()))
				isValid = false;
			// For CheckNumber
			if (!StringUtils.isNoneBlank(paymentInformationInfoModel.getCheckNumber()))
				isValid = false;
			// For CheckDeliverTo
			if (!StringUtils.isNoneBlank(paymentInformationInfoModel.getCheckDeliverTo()))
				isValid = false;
		}
		// For AmountDateReceivedInBank
		if (!StringUtils.isNoneBlank(paymentInformationInfoModel.getAmountDateReceivedInBank())) {
			isValid = false;
		}

		// For AmountPostedInEs
//		if (paymentInformationInfoModel.getAmountPostedInEs()) {
//			isValid = false;
//		}
//
//		// For AmountReceivedInBank
//		if (paymentInformationInfoModel.getAmountReceivedInBank()) {
//			isValid = false;
//		}

		// For PaymentIssueTo
		if (!StringUtils.isNoneBlank(paymentInformationInfoModel.getPaymentIssueTo())) {
			isValid = false;
		}

		return isValid;
	}

	public boolean validationForServiceLevelInformationSectionFields(
			ServiceLevelInformationDto serviceLevelInformationInfoModel) {
		logger.info("ServiceLevelInformationDto->" + serviceLevelInformationInfoModel);

		boolean isValid = true;
		if (serviceLevelInformationInfoModel.getServiceLevelBody().isEmpty()) {
			return false;
		}

		return isValid;
	}
}

package com.tricon.ruleengine.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

import com.tricon.ruleengine.dto.PatientTreamentDto;

import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.TreatmentClaimDto;
import com.tricon.ruleengine.dto.TreatmentPlanBatchValidationDto;
import com.tricon.ruleengine.dto.TreatmentPlanDto;
import com.tricon.ruleengine.dto.TreatmentPlanValidationDto;
import com.tricon.ruleengine.model.db.IVFormType;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.Rules;
import com.tricon.ruleengine.model.sheet.CommonDataCheck;
import com.tricon.ruleengine.model.sheet.IVFTableSheet;

/**
 * @author Deepak.Dogra
 *
 */
public interface TreatmentPlanService {
	
	/**
	 * This method will validate the given treatment Plan and Return the corresponding
	 * success/ Failure Messages in object
	 * @param dto
	 * @return
	 */
	public Map<String,List<TPValidationResponseDto>> validateTreatmentPlan(TreatmentPlanValidationDto dto);
	
	public Map<String, List<TPValidationResponseDto>> saveDisplayUserInputsOnly(TreatmentPlanValidationDto dtod);

	public Map<String,List<TPValidationResponseDto>> validateTreatmentPlanPreBatch(TreatmentPlanBatchValidationDto dto);

	
	public Map<String,List<PatientTreamentDto>> getTreatments(TreatmentPlanDto dto) ;
	
	public Object[] logFileToAppendData(String officeName);
	
	public Rules getRulesFromList(List<Rules> rules, String name);
	
	public void saveReportsList(Authentication authentication, List<Rules> rules, CommonDataCheck tp,
			IVFTableSheet ivfSheet, List<TPValidationResponseDto> list, Office off,int userType,String insuranceType,IVFormType iVFormType,String ignoredValues);
	
	public void saveReportsListBatch(Authentication authentication, List<Rules> rules, IVFTableSheet ivfSheet,
			List<TPValidationResponseDto> list, Office off,IVFormType iVFormType,String mode);
	
	public Object getTreatmentClaimData(TreatmentClaimDto dto);
	
	public Object getTreatmentClaimDataForRCM(TreatmentClaimDto dto,String companyId);
	
	
}

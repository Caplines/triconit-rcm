package com.tricon.ruleengine.service;

import java.util.List;
import java.util.Map;

import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.TreatmentPlanBatchValidationDto;
import com.tricon.ruleengine.dto.TreatmentPlanValidationDto;

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

	public Map<String,List<TPValidationResponseDto>> validateTreatmentPlanPreBatch(TreatmentPlanBatchValidationDto dto);


}

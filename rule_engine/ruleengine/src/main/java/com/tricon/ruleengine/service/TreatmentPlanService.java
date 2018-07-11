package com.tricon.ruleengine.service;

import java.util.List;

import com.tricon.ruleengine.dto.TPValidationResponseDto;
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
	public List<TPValidationResponseDto> validateTreatmentPlan(TreatmentPlanValidationDto dto);

}

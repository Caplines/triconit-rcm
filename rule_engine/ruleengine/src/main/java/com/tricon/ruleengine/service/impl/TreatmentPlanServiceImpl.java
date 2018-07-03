package com.tricon.ruleengine.service.impl;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dto.TPValidationResponseDto;
import com.tricon.ruleengine.dto.TreatmentPlanValidationDto;
import com.tricon.ruleengine.service.TreatmentPlanService;

@Transactional
@Service
public class TreatmentPlanServiceImpl implements TreatmentPlanService{

	@Override
	public TPValidationResponseDto validateTreatmentPlan(TreatmentPlanValidationDto dto) {
		// TODO Auto-generated method stub
		return null;
	}

}

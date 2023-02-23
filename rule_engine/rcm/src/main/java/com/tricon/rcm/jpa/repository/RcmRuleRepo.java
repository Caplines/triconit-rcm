package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmRules;

public interface RcmRuleRepo extends JpaRepository<RcmRules, Integer>{

	
	RcmRules findByRuleTypeInAndActiveAndManualAuto(List<String> type,int active,String manualAuto);	
}

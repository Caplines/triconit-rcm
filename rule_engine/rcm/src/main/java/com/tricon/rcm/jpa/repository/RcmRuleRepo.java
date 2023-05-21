package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmRules;

public interface RcmRuleRepo extends JpaRepository<RcmRules, Integer>{

	
	List<RcmRules> findByRuleTypeInAndActiveAndManualAuto(List<String> type,int active,String manualAuto);	
	List<RcmRules> findByRuleTypeInAndActive(List<String> type,int active);
	RcmRules findById(int id);
	
}

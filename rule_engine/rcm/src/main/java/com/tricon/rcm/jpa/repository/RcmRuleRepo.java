package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmRules;

public interface RcmRuleRepo extends JpaRepository<RcmRules, Integer>{

	
}

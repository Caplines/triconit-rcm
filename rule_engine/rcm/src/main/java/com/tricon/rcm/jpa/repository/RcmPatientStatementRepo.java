package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmPatientStatementSection;

public interface RcmPatientStatementRepo extends JpaRepository<RcmPatientStatementSection, Integer>{
	
	RcmPatientStatementSection findFirstByClaimClaimUuidAndTeamIdOrderByCreatedDateDesc(String claimUuid,int teamId);
	RcmPatientStatementSection findFirstByClaimClaimUuidOrderByCreatedDateDesc(String claimUuid);

	
}

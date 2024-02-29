package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmPatientStatementSection;

public interface RcmPatientStatementRepo extends JpaRepository<RcmPatientStatementSection, Integer>{
	
	RcmPatientStatementSection findFirstByClaimClaimUuidAndCreatedByUuidAndTeamIdOrderByCreatedDateDesc(String claimUuid,String createdBy,int teamId);
	RcmPatientStatementSection findFirstByClaimClaimUuidAndCreatedByUuidOrderByCreatedDateDesc(String claimUuid,String createdBy);

	
}

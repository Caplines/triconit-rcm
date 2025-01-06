package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmPatientStatementSection;

public interface RcmPatientStatementRepo extends JpaRepository<RcmPatientStatementSection, Integer>{
	
	List<RcmPatientStatementSection> findByClaimClaimUuidAndTeamIdAndMarkAsDeletedFalseOrderByCreatedDateDesc(String claimUuid,int teamId);
	//RcmPatientStatementSection findFirstByClaimClaimUuidOrderByCreatedDateDesc(String claimUuid);
	List<RcmPatientStatementSection> findByClaimClaimUuidAndMarkAsDeletedFalseOrderByCreatedDateDesc(String claimUuid);
	
}

package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmAppealLevelInformation;

public interface RcmAppealInfoRepo extends JpaRepository<RcmAppealLevelInformation, Integer>{
	
	RcmAppealLevelInformation findFirstByClaimClaimUuidAndCreatedByUuidAndTeamIdIdOrderByCreatedDateDesc(String claimUuid,String createdBy,int teamId);

}

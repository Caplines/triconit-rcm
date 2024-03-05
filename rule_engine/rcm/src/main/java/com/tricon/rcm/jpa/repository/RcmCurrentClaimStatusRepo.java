package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.CurrentClaimStatusAndNextAction;

public interface RcmCurrentClaimStatusRepo extends JpaRepository<CurrentClaimStatusAndNextAction, Integer> {

	CurrentClaimStatusAndNextAction findFirstByClaimClaimUuidAndCreatedByUuidAndTeamIdOrderByCreatedDateDesc(String claimUuid,String createdBy,int teamId);
	CurrentClaimStatusAndNextAction findFirstByClaimClaimUuidAndCreatedByUuidOrderByCreatedDateDesc(String claimUuid,String createdBy);
}

package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.CurrentClaimStatusAndNextAction;

public interface RcmCurrentClaimStatusRepo extends JpaRepository<CurrentClaimStatusAndNextAction, Integer> {

	CurrentClaimStatusAndNextAction findFirstByClaimClaimUuidAndTeamIdOrderByCreatedDateDesc(String claimUuid,int teamId);
	CurrentClaimStatusAndNextAction findFirstByClaimClaimUuidOrderByCreatedDateDesc(String claimUuid);
}

package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmClaimLevelSection;

public interface RcmClaimLevelInfoRepo  extends JpaRepository<RcmClaimLevelSection, Integer>{

	RcmClaimLevelSection findFirstByClaimClaimUuidAndCreatedByUuidAndTeamIdIdOrderByCreatedDateDesc(String claimUuid,String createdBy,int teamId);
}

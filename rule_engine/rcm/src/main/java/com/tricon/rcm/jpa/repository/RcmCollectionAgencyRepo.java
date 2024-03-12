package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmCollectionAgency;

public interface RcmCollectionAgencyRepo extends JpaRepository<RcmCollectionAgency, Integer>{

	RcmCollectionAgency findFirstByClaimClaimUuidAndTeamIdOrderByCreatedDateDesc(String claimUuid,int teamId);
	RcmCollectionAgency findFirstByClaimClaimUuidOrderByCreatedDateDesc(String claimUuid);
}

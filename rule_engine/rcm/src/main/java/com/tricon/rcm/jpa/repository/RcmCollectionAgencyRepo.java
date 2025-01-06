package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmCollectionAgency;

public interface RcmCollectionAgencyRepo extends JpaRepository<RcmCollectionAgency, Integer>{

	List<RcmCollectionAgency> findByClaimClaimUuidAndTeamIdOrderByCreatedDateDesc(String claimUuid,int teamId);
	List<RcmCollectionAgency> findByClaimClaimUuidOrderByCreatedDateDesc(String claimUuid);
}

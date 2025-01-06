package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmAppealLevelInformation;

public interface RcmAppealInfoRepo extends JpaRepository<RcmAppealLevelInformation, Integer>{
	
	List<RcmAppealLevelInformation> findByClaimClaimUuidAndTeamIdIdOrderByCreatedDateDesc(String claimUuid,int teamId);
	List<RcmAppealLevelInformation> findByClaimClaimUuidOrderByCreatedDateDesc(String claimUuid);

}

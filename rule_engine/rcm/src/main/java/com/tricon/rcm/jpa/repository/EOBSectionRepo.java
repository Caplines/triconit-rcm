package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.EOBSectionInformation;

public interface EOBSectionRepo extends JpaRepository<EOBSectionInformation, Integer> {

	EOBSectionInformation findFirstByClaimClaimUuidAndCreatedByUuidAndAttachByTeamIdAndMarkAsDeletedFalseOrderByCreatedDateDesc(String claimUuid,String createdBy,int teamId);
	EOBSectionInformation findFirstByClaimClaimUuidAndCreatedByUuidAndMarkAsDeletedFalseOrderByCreatedDateDesc(String claimUuid,String createdBy);
	List<EOBSectionInformation>findByClaimClaimUuidAndAttachByTeamIdAndMarkAsDeletedFalseAndCreatedByUuidAndIdIn(String claimUuid,int teamId,String userUuid,List<Integer> id);
}

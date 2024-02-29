package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.EOBSectionInformation;

public interface EOBSectionRepo extends JpaRepository<EOBSectionInformation, Integer> {

	List<EOBSectionInformation>findByClaimClaimUuidAndAttachByTeamIdAndMarkAsDeletedFalseAndCreatedByUuidAndIdIn(String claimUuid,int teamId,String userUuid,List<Integer> id);
	List<EOBSectionInformation> findByClaimClaimUuidAndMarkAsDeletedFalseOrderByCreatedDateDesc(String claimUuid);
}

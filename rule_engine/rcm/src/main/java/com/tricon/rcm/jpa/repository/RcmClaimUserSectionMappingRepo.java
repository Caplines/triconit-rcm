package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.ClaimUserSectionMapping;

public interface RcmClaimUserSectionMappingRepo  extends JpaRepository<ClaimUserSectionMapping, Integer>{
	
	List<ClaimUserSectionMapping>findByCompanyUuidAndUserUuid(String clientUuid,String userUuid);
	
	ClaimUserSectionMapping findByCompanyUuidAndTeamIdIdAndSectionIdAndUserUuid(String clientUuid,int teamId,int sectionId,String userUuid);
	
	ClaimUserSectionMapping findByCompanyUuidAndTeamIdIdAndSectionId(String clientUuid,int teamId,int sectionId);
	
	List<ClaimUserSectionMapping> findByCompanyUuid(String clientUuid);

}

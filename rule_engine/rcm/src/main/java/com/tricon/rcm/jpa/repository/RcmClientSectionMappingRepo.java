package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmClientSectionMapping;

public interface RcmClientSectionMappingRepo  extends JpaRepository<RcmClientSectionMapping, Integer>{

	
	RcmClientSectionMapping findByCompanyUuidAndTeamIdIdAndSectionId(String clientUuid,int teamId,int sectionId);
	
	List<RcmClientSectionMapping> findByCompanyUuid(String clientUuid);
}

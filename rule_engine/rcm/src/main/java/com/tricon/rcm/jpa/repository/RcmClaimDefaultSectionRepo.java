package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmClaimDefaultSection;

public interface RcmClaimDefaultSectionRepo extends JpaRepository<RcmClaimDefaultSection,Integer>{
	
	RcmClaimDefaultSection findByTeamIdIdAndSectionId(int teamId,int sectionId);
	

}

package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmOffice;

public interface RcmClaimRepository extends JpaRepository<RcmClaims, String> {

	RcmClaims findByClaimIdAndOffice(int claimId,RcmOffice office);
	
	//@Query(nativeQuery=false,value="select * PlannedServices  set movedToCloud = :d where ")
	//RcmClaims findByClaimsByClaimTypeAndTeamId(@Param("claimType") int claimType);
}

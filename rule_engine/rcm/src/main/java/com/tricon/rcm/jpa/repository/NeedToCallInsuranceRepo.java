//package com.tricon.rcm.jpa.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import com.tricon.rcm.db.entity.NeedToCallInsurance;
//
//public interface NeedToCallInsuranceRepo extends JpaRepository<NeedToCallInsurance, Integer>{
//	
//	NeedToCallInsurance findFirstByClaimClaimUuidAndTeamIdIdOrderByCreatedDateDesc(String claimUuid,int teamId);
//	NeedToCallInsurance findFirstByClaimClaimUuidOrderByCreatedDateDesc(String claimUuid);
//
//}

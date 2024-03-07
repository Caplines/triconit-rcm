package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmPatientPayment;


public interface RcmPatientPaymentSectionRepo  extends JpaRepository<RcmPatientPayment, Integer>{
	
	RcmPatientPayment findFirstByClaimClaimUuidAndTeamIdIdOrderByCreatedDateDesc(String claimUuid,int teamId);
	RcmPatientPayment findFirstByClaimClaimUuidOrderByCreatedDateDesc(String claimUuid);

}

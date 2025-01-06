package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmPatientPayment;


public interface RcmPatientPaymentSectionRepo  extends JpaRepository<RcmPatientPayment, Integer>{
	
	List<RcmPatientPayment> findByClaimClaimUuidAndTeamIdIdOrderByCreatedDateDesc(String claimUuid,int teamId);
	List<RcmPatientPayment> findByClaimClaimUuidOrderByCreatedDateDesc(String claimUuid);

}

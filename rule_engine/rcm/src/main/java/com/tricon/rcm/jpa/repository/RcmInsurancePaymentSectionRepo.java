package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.PaymentInformationSection;

public interface RcmInsurancePaymentSectionRepo extends JpaRepository<PaymentInformationSection, Integer>{

	List<PaymentInformationSection> findByClaimClaimUuidAndTeamIdOrderByCreatedDateDesc(String claimUuid,int teamId);
	List<PaymentInformationSection> findByClaimClaimUuidOrderByCreatedDateDesc(String claimUuid);
}

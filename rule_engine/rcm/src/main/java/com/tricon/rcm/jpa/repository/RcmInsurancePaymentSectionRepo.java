package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.PaymentInformationSection;

public interface RcmInsurancePaymentSectionRepo extends JpaRepository<PaymentInformationSection, Integer>{

	PaymentInformationSection findFirstByClaimClaimUuidAndTeamIdOrderByCreatedDateDesc(String claimUuid,int teamId);
	PaymentInformationSection findFirstByClaimClaimUuidOrderByCreatedDateDesc(String claimUuid);
}

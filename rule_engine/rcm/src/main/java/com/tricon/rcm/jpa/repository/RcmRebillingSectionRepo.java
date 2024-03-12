package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmRebillingSection;

public interface RcmRebillingSectionRepo extends JpaRepository<RcmRebillingSection, Integer>{

	List<RcmRebillingSection> findByClaimClaimUuidOrderByCreatedDateDesc(String claimUuid);
}

package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tricon.rcm.db.entity.RcmRequestRebiilingSection;

public interface RcmRequestRebillingSectionRepo extends JpaRepository<RcmRequestRebiilingSection, Integer> {
	
	RcmRequestRebiilingSection findFirstByClaimClaimUuidOrderByCreatedDateDesc(String claimUuid);
	List<RcmRequestRebiilingSection> findByClaimClaimUuidOrderByCreatedDateDesc(String claimUuid);

}

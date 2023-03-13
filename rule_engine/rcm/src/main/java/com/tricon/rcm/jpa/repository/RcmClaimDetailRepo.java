package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.tricon.rcm.db.entity.RcmClaimDetail;

public interface RcmClaimDetailRepo extends JpaRepository<RcmClaimDetail, Integer>{

	List<RcmClaimDetail> findByClaimClaimUuid(String claimUUid);
	
	long countByClaimClaimUuid(String claimUUid);
}

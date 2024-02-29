package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmInsuranceFollowUpSection;

public interface FollowUpInsuranceRepo extends JpaRepository<RcmInsuranceFollowUpSection, Integer>{
	
	
	List<RcmInsuranceFollowUpSection> findByClaimClaimUuidOrderByCreatedDateDesc(String claimUuid);
	List<RcmInsuranceFollowUpSection> findByClaimClaimUuidAndCreatedByUuidOrderByCreatedDateDesc(String claimUuid,String createdBy);

}

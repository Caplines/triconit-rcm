package com.tricon.rcm.jpa.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmTPDetail;

public interface RcmTPDetailRepo extends JpaRepository<RcmTPDetail, Integer> {

	@Transactional
	@Modifying
	@Query(nativeQuery = true,value="delete from rcm_tp_detail where claim_id = :claimId")
	   Integer deleteByClaimId(@Param("claimId")String claimId);
	
	List<RcmTPDetail> findByClaimClaimUuid(String claimUUid);
	
}

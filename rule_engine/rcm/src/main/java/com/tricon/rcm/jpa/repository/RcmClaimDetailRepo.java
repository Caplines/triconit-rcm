package com.tricon.rcm.jpa.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaimDetail;

public interface RcmClaimDetailRepo extends JpaRepository<RcmClaimDetail, Integer>{

	List<RcmClaimDetail> findByClaimClaimUuid(String claimUUid);
	
	List<RcmClaimDetail> findByClaimClaimUuidAndActiveTrue(String claimUUid);
	
	long countByClaimClaimUuid(String claimUUid);
	
	@Modifying
	@Transactional
	@Query(value = "update rcm_claim_detail set active=false where claim_id=:claimUuid and service_code in(:codes) ", nativeQuery = true)
	void deActivatedRcmDetailWithClaimUUidAndCode(@Param("claimUuid")String claimUuid,@Param("codes")List<String> codes);
	
	@Query(value = "select * from rcm_claim_detail s "
			+ "where s.claim_id=:claimUuid and s.service_code in(:serviceCodes) "
			+ "and s.active is true", nativeQuery = true)
	List<RcmClaimDetail> findServiceCodesByClaimUuidAndCodes(@Param("claimUuid") String claimUuid,
			@Param("serviceCodes") List<String> serviceCodes);

}

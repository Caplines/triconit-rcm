package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmLinkedClaims;
import com.tricon.rcm.dto.customquery.LinkedClaimDto;


public interface RcmLinkedClaimsRepo extends JpaRepository<RcmLinkedClaims, Integer> {

	@Query(nativeQuery = true, value = "select claim_id from rcm_claims where  claim_uuid in (select linked_claim_id from rcm_linked_claims where claim_id=:claimuuid )")
	List<String> getLinkedClaims(@Param("claimuuid") String claimuuid);
	
	@Query(nativeQuery = true, value = "select * from rcm_linked_claims where claim_id=:claimuuid")
	RcmLinkedClaims  getLinkedClaimsByClaimUuid(@Param("claimuuid") String claimuuid);
	

	@Query(nativeQuery = true, value = "select claim_id as claimId,claim_uuid as claimUuid from rcm_claims where  claim_uuid in (select linked_claim_id from rcm_linked_claims where claim_id=:claimuuid )")
	List<LinkedClaimDto> getLinkedClaimsByClaimUuuid(@Param("claimuuid") String claimuuid);
}

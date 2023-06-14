package com.tricon.rcm.jpa.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaimsServiceRuleValidation;

public interface RcmClaimsServiceRuleValidationRepo extends JpaRepository<RcmClaimsServiceRuleValidation, String> {

   List<RcmClaimsServiceRuleValidation>	findByClaimClaimUuid(String claimUuid);
   
   RcmClaimsServiceRuleValidation	findByClaimClaimUuidAndNameAndServiceCode(String claimUuid,String name,String serviceCode);
   
   RcmClaimsServiceRuleValidation	findByClaimClaimUuidAndRemarkUuid(String claimUuid,String remarkUuid);
   
   @Modifying
   @Query(value="update rcm_claims_service_rule_val set active = false where claim_id =:claimUuid",nativeQuery = true)
   void deactivateOldClaimData(@Param("claimUuid") String claimUuid);
   
   
   @Transactional
   @Modifying
   @Query(nativeQuery = true,value="delete from rcm_claims_service_rule_val where claim_id = :claimId")
   Integer deleteByClaimId(@Param("claimId")String claimId);
   
   @Transactional
   @Modifying
   @Query(value="update rcm_claims_service_rule_val set mark_deleted = true where claim_id =:claimUuid",nativeQuery = true)
   void markOldClaimDataForDeletion(@Param("claimUuid") String claimUuid);
   
   
   @Transactional
   @Modifying
   @Query(nativeQuery = true,value="delete from rcm_claims_service_rule_val where claim_id = :claimId and mark_deleted=true ")
   Integer deleteByClaimIdMarkeForDeletion(@Param("claimId")String claimId);
}

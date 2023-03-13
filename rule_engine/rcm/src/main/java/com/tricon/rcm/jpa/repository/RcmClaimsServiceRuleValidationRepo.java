package com.tricon.rcm.jpa.repository;

import java.util.List;

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
   

}

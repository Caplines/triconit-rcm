package com.tricon.rcm.jpa.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.tricon.rcm.db.entity.RcmClaimRuleRemark;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmRules;
import com.tricon.rcm.dto.customquery.ClaimRuleRemarksDto;

public interface RcmClaimRuleRemarkRepo extends JpaRepository<RcmClaimRuleRemark, String> {

	
	RcmClaimRuleRemark findByRuleAndClaim(RcmRules rule,RcmClaims claims);
	@Query(nativeQuery = true, value = " select "
			+ " com.remarks remark,com.rule_id ruleId,com.created_date cd,us.first_name fName,us.last_name lName from rcm_claim_rule_remark com "
			+ " inner join rcm_user us on us.uuid=com.commented_by "
			+ " where com.claim_id=:claim_id and com.team_id =:teamId order by com.created_date desc "
			+ "")
	List<ClaimRuleRemarksDto> fetchClaimRuleRemarks(@Param("claim_id") String claimId,@Param("teamId") int teamId);
	
	@Query(nativeQuery = true, value = " select "
			+ " com.remarks remark,com.rule_id ruleId,com.created_date cd,us.first_name fName,us.last_name lName from rcm_claim_rule_remark com "
			+ " inner join rcm_user us on us.uuid=com.commented_by "
			+ " where com.claim_id=:claim_id order by com.created_date desc "
			+ "")
	List<ClaimRuleRemarksDto> fetchClaimRuleRemarksAnyTeam(@Param("claim_id") String claimId);

	 @Transactional
	 @Modifying
	 @Query(nativeQuery = true,value="delete from rcm_claim_rule_remark where claim_id = :claimId")
	 Integer deleteByClaimId(@Param("claimId")String claimId);
	 
}
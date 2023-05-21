package com.tricon.rcm.jpa.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaimRuleValidation;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.db.entity.RcmRules;
import com.tricon.rcm.dto.customquery.ClaimRuleValidationDto;

public interface RcmClaimRuleValidationRepo extends JpaRepository<RcmClaimRuleValidation, String> {

	
	RcmClaimRuleValidation findByRuleAndClaim(RcmRules rule,RcmClaims claims);
	
	@Query(nativeQuery = true, value = " select "
			+ " com.message message,com.rule_id ruleId,ru.manual_auto manualAuto,ru.name name ,"
			+ " com.message_type messageType,com.created_date cd,us.first_name fName,us.last_name lName from rcm_claim_rule_validation com "
			+ " inner join rcm_user us on us.uuid=com.run_by inner join rules ru on ru.id=com.rule_id "
			+ " where com.claim_id=:claim_id and com.team_id =:teamId order by com.created_date desc "
			+ "")
	List<ClaimRuleValidationDto> fetchClaimRuleValidationForTeam(@Param("claim_id") String claimId,@Param("teamId") int teamId);
	
	@Query(nativeQuery = true, value = " select "
			+ " com.message message,com.rule_id ruleId,ru.manual_auto manualAuto,ru.name name ,"
			+ " com.message_type messageType,com.created_date cd,us.first_name fName,us.last_name lName,rule_type ruleType"
			+ " from rcm_claim_rule_validation com "
			+ " inner join rcm_user us on us.uuid=com.run_by inner join rules ru on ru.id=com.rule_id "
			+ " where com.claim_id=:claim_id  order by com.created_date desc "
			+ "")
	List<ClaimRuleValidationDto> fetchClaimRuleValidation(@Param("claim_id") String claimId);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true,value="delete from rcm_claim_rule_validation where claim_id = :claimId")
	   Integer deleteByClaimId(@Param("claimId")String claimId);
}

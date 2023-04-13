package com.tricon.rcm.jpa.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.tricon.rcm.db.entity.RcmClaimComment;
import com.tricon.rcm.dto.customquery.ClaimRemarksDto;


public interface RcmClaimCommentRepo extends JpaRepository<RcmClaimComment, String> {

	
	
	@Query(nativeQuery = true, value = " select "
			+ " com.comments comment,com.created_date cd,us.first_name fName,us.last_name lName from rcm_claim_comment com "
			+ " inner join rcm_user us on us.uuid=com.commented_by "
			+ " where com.claim_id=:claim_id and com.team_id =:teamId order by com.created_date desc "
			+ "")
	List<ClaimRemarksDto> fetchClaimRemarksSameTeam(@Param("claim_id") String claimId,@Param("teamId") int teamId);
 
	RcmClaimComment findByCommentedByUuidAndClaimsClaimUuid(String commentedBy,String claimuuid);
	
	RcmClaimComment findByClaimsClaimUuid(String claimuuid);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true,value="delete from rcm_claim_comment where claim_id = :claimId")
	Integer deleteByClaimId(@Param("claimId")String claimId);
}

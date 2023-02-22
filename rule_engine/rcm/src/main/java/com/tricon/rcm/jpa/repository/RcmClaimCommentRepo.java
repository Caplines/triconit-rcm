package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.tricon.rcm.db.entity.RcmClaimComment;
import com.tricon.rcm.dto.customquery.ClaimRemarksDto;


public interface RcmClaimCommentRepo extends JpaRepository<RcmClaimComment, String> {

	@Query(nativeQuery = true, value = " select "
			+ " com.comments comment,com.created_date cd,us.first_name fName,us.last_name lName from rcm_claim_comment com "
			+ " inner join rcm_user us on us.uuid=com.commented_by "
			+ " where com.claim_id=:claim_id and com.team_id !=:teamId order by com.created_date desc "
			+ "")
	List<ClaimRemarksDto> fetchClaimRemarksOtherTeam(@Param("claim_id") String claimId,@Param("teamId") int teamId);
	
	@Query(nativeQuery = true, value = " select "
			+ " com.comments comment,com.created_date cd,us.first_name fName,us.last_name lName from rcm_claim_comment com "
			+ " inner join rcm_user us on us.uuid=com.commented_by "
			+ " where com.claim_id=:claim_id and com.team_id =:teamId order by com.created_date desc "
			+ "")
	List<ClaimRemarksDto> fetchClaimRemarksSameTeam(@Param("claim_id") String claimId,@Param("teamId") int teamId);

}

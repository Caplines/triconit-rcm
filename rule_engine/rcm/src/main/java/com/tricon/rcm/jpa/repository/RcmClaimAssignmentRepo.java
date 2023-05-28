package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaimAssignment;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.customquery.ClaimRemarksDto;

public interface RcmClaimAssignmentRepo extends JpaRepository<RcmClaimAssignment, Integer>{

	List<RcmClaimAssignment> findByAssignedToUuid(String assignTo);
	
	@Modifying
	@Query(value = "update  rcm_claim_assignment set comment_assigned_by=:comment,updated_by=:updatedBy,updated_date=now(),active=:status where assigned_to=:userUuid AND claim_id=:claimUuid ", nativeQuery = true)
	void updateClaimUserStatusAndComment(@Param("comment")String comment,@Param("updatedBy")RcmUser updatedBy,@Param("status")boolean status,@Param("userUuid")String userUuid,@Param("claimUuid")String claimUuid);

	
	RcmClaimAssignment findByAssignedToUuidAndClaimsClaimUuidAndActive(String assignTo,String claimUUid,boolean active);
	
	RcmClaimAssignment findByClaimsClaimUuidAndActive(String claimUUid,boolean active);
	
	RcmClaimAssignment findByClaimsClaimUuidAndActiveAndCurrentTeamIdId(String claimUUid,boolean active,int teamId);
	
	
	//RcmClaimAssignment findByClaimsClaimUuidAndActive(String claimUUid);
	
	@Query(value ="select count(assigned_to) from rcm_claim_assignment where assigned_to=:assignTo AND active is true",nativeQuery=true)
	int findExistingUserAssignClaimCountsAndActiveStatus(@Param("assignTo")String assignTo);
	
	@Query(value ="select count(assigned_to) from rcm_claim_assignment where assigned_to=:assignTo",nativeQuery=true)
	int findExistingUsersAssignClaimCounts(@Param("assignTo")String assignTo);
	
	@Query(value ="select * from rcm_claim_assignment ra join rcm_claims rc where rc.claim_uuid=ra.claim_id and ra.assigned_to=:assignTo and rc.pending is true",nativeQuery=true)
	List<RcmClaimAssignment>findExistingUserClaimsWithPendingState(String assignTo);
	
	
	@Query(nativeQuery = true, value = " select "
			 +" comment_assigned_by comment,assign.created_date cd, tm.description teamName,us.first_name fName,us.last_name lName "
			 +" from  rcm_claim_assignment assign inner join rcm_user us on us.uuid=assign.assigned_by "
			 +" inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
			 +" inner join rcm_team tm on tm.id=rut.team_Id "
			 +"  where claim_id=:claim_id and assign.current_team_id!=:teamId and tm.id!=:teamId and comment_assigned_by<>'' order by assign.created_date asc "
	+ "")
    List<ClaimRemarksDto> fetchClaimRemarksOtherTeam(@Param("claim_id") String claimId,@Param("teamId") int teamId);
	
	/*
	 * Before calling this Method Make sure u see claim is for valid Client 
	 */
	@Query(nativeQuery = true, value = "  "
			+" SELECT count(*) FROM rcm_claim_assignment rca "
			+" inner join rcm_team rt on rt.id=rca.current_team_id "
			+"  where rca.claim_id=:claim_id  and rca.active is true and rca.current_team_id!=:team_id and assigned_to is not null")
    int claimAssignedToSomeoneAlreadyofOtherTeam(@Param("claim_id") String claimId,@Param("team_id") int teamId);
	
	/*
	 * Before calling this Method Make sure u seed claim is for valid Client 
	 */
	@Query(nativeQuery = true, value = "  "
			+" SELECT count(*) FROM rcm_claim_assignment rca "
			+" inner join rcm_team rt on rt.id=rca.current_team_id "
			+"  where rca.claim_id=:claim_id and  rt.id<>:teamId  and rca.active is false")
   int claimWorkedBySomeEarlierByTLTeam(@Param("claim_id") String claimId,@Param("teamId") int teamId);
	
	
	
}

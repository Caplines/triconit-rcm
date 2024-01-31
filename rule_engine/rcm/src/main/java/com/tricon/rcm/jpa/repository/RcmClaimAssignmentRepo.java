package com.tricon.rcm.jpa.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaimAssignment;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.customquery.ClaimRemarksDto;
import com.tricon.rcm.dto.customquery.ExistingClaimDto;
import com.tricon.rcm.util.Constants;

public interface RcmClaimAssignmentRepo extends JpaRepository<RcmClaimAssignment, Integer>{

	List<RcmClaimAssignment> findByAssignedToUuid(String assignTo);
	
	@Modifying
	@Query(value = "update  rcm_claim_assignment set comment_assigned_by=:comment,updated_by=:updatedBy,updated_date=now(),active=:status where assigned_to=:userUuid AND claim_id=:claimUuid ", nativeQuery = true)
	void updateClaimUserStatusAndComment(@Param("comment")String comment,@Param("updatedBy")RcmUser updatedBy,@Param("status")boolean status,@Param("userUuid")String userUuid,@Param("claimUuid")String claimUuid);

	@Modifying
	@Query(value = "update rcm_claim_assignment set active =false where id=:id AND claim_id=:claimUuid ", nativeQuery = true)
	void updateClaimIssueAssignment(@Param("id")int id,@Param("claimUuid")String claimUuid);

	
	RcmClaimAssignment findByAssignedToUuidAndClaimsClaimUuidAndActive(String assignTo,String claimUUid,boolean active);
	
	RcmClaimAssignment findByCurrentTeamIdIdAndClaimsClaimUuidAndActive(int teamId,String claimUUid,boolean active);
	
	RcmClaimAssignment findByClaimsClaimUuidAndActive(String claimUUid,boolean active);
	
	@Query(value = "select id from rcm_claim_assignment where active =true and claim_id=:claimUuid order by created_date asc", nativeQuery = true)
	List<Integer> findIssueAssingments(@Param("claimUuid") String claimUUid);
	
	RcmClaimAssignment findByClaimsClaimUuidAndActiveAndCurrentTeamIdId(String claimUUid,boolean active,int teamId);
	
	
	//RcmClaimAssignment findByClaimsClaimUuidAndActive(String claimUUid);
	
	@Query(value ="select count(assigned_to) from rcm_claim_assignment ra ,rcm_claims rc where rc.claim_uuid=ra.claim_id and  ra.assigned_to=:assignTo AND active is true and "
			+ " rc.pending=true and rc.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED,nativeQuery=true)
	int findExistingUserAssignClaimCountsAndActiveStatus(@Param("assignTo")String assignTo);
	
	@Query(value ="select count(assigned_to) from rcm_claim_assignment where assigned_to=:assignTo",nativeQuery=true)
	int findExistingUsersAssignClaimCounts(@Param("assignTo")String assignTo);
	
	@Query(value ="select * from rcm_claim_assignment ra join rcm_claims rc where rc.claim_uuid=ra.claim_id and ra.assigned_to=:assignTo and "
			+ " rc.pending is true and rc.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED,nativeQuery=true)
	List<RcmClaimAssignment>findExistingUserClaimsWithPendingState(String assignTo);
	
	//assign.current_team_id!=:teamId and tm.id!=:teamId Removed Condition so that all remarks can come
		//,@Param("teamId") int teamId
	@Query(nativeQuery = true, value = " select "
			 +" comment_assigned_by comment,assign.created_date cd, tm.description teamName,us.first_name fName,us.last_name lName,assign.attachment_with_remarks attchmentsWithRemarks "
			 +" from  rcm_claim_assignment assign inner join rcm_user us on us.uuid=assign.assigned_by "
			 +" inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
			 +" inner join rcm_team tm on tm.id=rut.team_Id "
			 +" where claim_id=:claim_id and comment_assigned_by<>'' order by assign.created_date asc "
	+ "")
    List<ClaimRemarksDto> fetchClaimRemarksOtherTeam(@Param("claim_id") String claimId);
	
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
	
//	@Transactional
//	@Modifying
//	@Query(nativeQuery = true, value = "  "
//			+" insert into rcm_claim_assignment (created_date,updated_date,active,taken_back,created_by,assigned_by,"
//			+ " assigned_to,current_team_id,status_id,system_comment,"
//			+ "claim_id) values (now(),now(),true,false,:usBy,:usBy,:usTo,:teamId,:statusId,:systemCom,:claimId)")
//   Integer assignClaimToUser(@Param("usBy") String usBy,@Param("usTo") String usTo,
//		   @Param("teamId") int teamId,@Param("statusId") int statusId,@Param("systemCom") String systemCom,
//		   @Param("claimId") String claimId);
	
	@Query(value = "select comment_assigned_by from rcm_claim_assignment where active =false and current_team_id<>:teamId and claim_id=:claimUuid order by created_date desc limit 1", nativeQuery = true)
	String findLatestClaimCommentByOtherTeam(@Param("claimUuid") String claimUUid,@Param("teamId") int teamId);

	
	  @Query(value ="select rc.claim_id as ClaimId ,off.name AS OfficeUuid, "
	          + "off.company_id as ClientUuid ,c.name as ClientName, "
	          + "ra.current_team_id as TeamId from "
	          + "rcm_claim_assignment ra "
	          + "inner join rcm_claims rc "
	          + "on rc.claim_uuid=ra.claim_id "
	          + "inner join office off on off.uuid = rc.office_id "
	          + "inner join company c on c.uuid = off.company_id "
	          + "where ra.assigned_to=:assignTo "
	          + "and ra.active is true and rc.pending=true"
	          + " and rc.current_state="+Constants.CLAIM_ARCHIVE_PREFIX_CANBE_SUBMITED,nativeQuery=true)
	      List<ExistingClaimDto> findExistingUserAssignClaimsAndClientStatus(@Param("assignTo")String assignTo);
	
}

package com.tricon.rcm.jpa.repository;

import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaimAssignment;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.customquery.KeyValueDto;
import com.tricon.rcm.dto.customquery.OpenAndUnopendClaimStatusDto;
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
	
	RcmClaimAssignment findByClaimsClaimUuidAndActiveAndAssignedToNotNull(String claimUUid,boolean active);
	
	@Query(value = "select id from rcm_claim_assignment where active =true  and claim_id=:claimUuid order by created_date,id asc", nativeQuery = true)
	List<Integer> findIssueAssignments(@Param("claimUuid") String claimUUid);
	
	
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
			 //+" inner join rcm_user_team rut on rut.rcm_user_id=us.uuid "
			 +" inner join rcm_team tm on tm.id=assign.current_team_id "
			 +" where claim_id=:claim_id and comment_assigned_by<>'' order by assign.created_date asc "
	+ "")
    List<ClaimRemarksDto> fetchClaimRemarksOtherTeam(@Param("claim_id") String claimId);
	
	@Query(nativeQuery = true, value = "  "
			+"select remarks comment,assign.created_date cd, tm.description teamName,us.first_name fName,us.last_name lName,0 attchmentsWithRemarks "
			+"  from  rcm_next_action_required_section assign left join rcm_user us on us.uuid=assign.created_by"
			+" left join rcm_user_team rut on rut.rcm_user_id=us.uuid and rut.team_id=assign.team_id"
			+" left join rcm_team tm on tm.id=assign.team_id "
			+" where claim_uuid='9d53e0e7-3365-4696-bdec-d63b0ff83e7c' and assign.current_claim_status_rcm='Case Closed'  order by assign.created_date asc"  
	+ "")
   List<ClaimRemarksDto> fetchClaimClosedRemarksNextAction(@Param("claim_id") String claimId);
	
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
	
	@Query(nativeQuery = true, value = "  "
			+" SELECT count(*) FROM rcm_claim_assignment rca "
			+" where rca.claim_id=:claim_id")
   int findTotalEntiresinClaimAssignment(@Param("claim_id") String claimId);
	
	@Query(nativeQuery = true, value = "  "
			+" SELECT count(*) FROM rcm_claim_assignment rca "
			+" where rca.claim_id=:claim_id and  active is true")
   int countTotalActiveEntiresinClaimAssignment(@Param("claim_id") String claimId);

	
	@Query(nativeQuery = true, value = "  "
			+" SELECT * FROM rcm_claim_assignment rca "
			+" where rca.claim_id=:claim_id and current_team_id=:teamId and assigned_to is null and active is true")
	List<RcmClaimAssignment>findTotalEntiresinClaimAssignmentWithNullAssignedTo(@Param("claim_id") String claimId,@Param("teamId") int teamId);
	


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
	
			@Query(value = ""
					+ " select claim_id as keyy,comment_assigned_by AS value,created_date,updated_date from rcm_claim_assignment rca "
					+ " inner join ( "
					+ " select claim_id as keyy,max(created_date) as maxDate from rcm_claim_assignment where "
					+ " active =false and "
					+ " current_team_id<>:teamId and claim_id  in :claimUUids "
					+ " group by keyy) rca1  on rca.claim_id=rca1.keyy and rca1.maxDate=rca.created_date and rca.active=false",
					nativeQuery = true)
			List<KeyValueDto> findLatestClaimCommentByOtherTeam(@Param("claimUUids") List<String> claimUUids,
					@Param("teamId") int teamId);
		
			@Query(value = "select pending_since from rcm_claim_assignment where claim_id=:claimUuid and active =false and current_team_id=:teamId order by pending_since desc limit 1",nativeQuery = true)
			Object findPendingSinceDateByClaimUuidAndCurrentTeamId(@Param("claimUuid") String claimUuid,@Param("teamId")int teamId);
			
			@Query(value = "select ra.claim_id as claimUuid,ra.id as id,ra.created_date as createdDate,ra.updated_date as updatedDate,ra.active as active,"
					+ "ra.system_comment as comments,ra.current_team_id AS teamId from rcm_claim_assignment ra "
					+ "where ra.claim_id=:claimUuid order by ra.id", nativeQuery = true)
			List<OpenAndUnopendClaimStatusDto> findopenAndUnOpendClaims(@Param("claimUuid") String claimUuid);
			
			@Modifying
			@Query(value = "update rcm_claim_assignment set active =:status where id=:id and claim_id=:claimUuid", nativeQuery = true)
			void updateOpenAndUnOpendClaimsActiveStatus(@Param("id")int id, @Param("status") boolean status,@Param("claimUuid") String claimUuid);

			@Modifying
			@Query(value = "update rcm_claim_assignment set active=false,is_force_unassigned=true,updated_date=sysdate()  where claim_id in (:claimUuids) and active is true  ", nativeQuery = true)
			void updateClaimsUnAssignment(@Param("claimUuids")List<String> claimUuids);
			
			RcmClaimAssignment findFirstByClaimsClaimUuidAndActiveIsTrueOrderByIdDesc(String claimUuid);
			
			RcmClaimAssignment findFirstByClaimsClaimUuidAndActiveIsFalseOrderByIdDesc(String claimUuid);
			
			
}

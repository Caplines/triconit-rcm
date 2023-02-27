package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaimAssignment;
import com.tricon.rcm.db.entity.RcmUser;

public interface RcmClaimAssignmentRepo extends JpaRepository<RcmClaimAssignment, Integer>{

	List<RcmClaimAssignment> findByAssignedToUuid(String assignTo);
	
	@Modifying
	@Query(value = "update  rcm_claim_assignment set comment_assigned_by=:comment,updated_by=:updatedBy,updated_date=now(),active=:status where assigned_to=:userUuid AND claim_id=:claimUuid ", nativeQuery = true)
	void updateClaimUserStatusAndComment(@Param("comment")String comment,@Param("updatedBy")RcmUser updatedBy,@Param("status")boolean status,@Param("userUuid")String userUuid,@Param("claimUuid")String claimUuid);

	
	RcmClaimAssignment findByAssignedToUuidAndClaimsClaimUuidAndActive(String assignTo,String claimUUid,boolean active);
	
	RcmClaimAssignment findByClaimsClaimUuidAndActive(String claimUUid,boolean active);
}

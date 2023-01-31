package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.tricon.rcm.db.entity.RcmClaimAssignment;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.customquery.RcmClaimAssignmentDto;

public interface RcmClaimAssignmentRepo extends JpaRepository<RcmClaimAssignment, Integer>{

	List<RcmClaimAssignment>findByAssignedToUuid(String assignTo);
	
	@Query(value = "select c.assigned_to as UserId ,concat(u.first_name,' ',u.last_name)as FullName"
			+ " from rcm_claim_assignment c join rcm_user u where c.assigned_to=u.uuid "
			+ "AND c.assigned_to!=:assigntoUserUuid AND c.current_team_id=:teamId AND c.active=1", nativeQuery = true)
	List<RcmClaimAssignmentDto> findByCurrentTeamIdId(int teamId, String assigntoUserUuid);
	
	@Modifying
	@Query(value = "update  rcm_claim_assignment set comment_assigned_by=:comment,updated_by=:updatedBy,updated_date=now(),active=:status where assigned_to=:userUuid", nativeQuery = true)
	void updateClaimUserStatusAndComment(String comment,RcmUser updatedBy,boolean status,String userUuid);
	
}

package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.tricon.rcm.db.entity.RcmUserTeam;
import com.tricon.rcm.dto.customquery.UserClaimsAssignmentResponseDto;

public interface RcmUserTeamRepo  extends JpaRepository<RcmUserTeam, Integer>{

	List<RcmUserTeam> findByUserUuid(String userUuid);
	

	@Query(nativeQuery = true, value = ""
			+" select rut.rcm_user_id as uuid,us.first_name as firstName,us.last_name as lastName  "
			+" from rcm_user_team rut inner join rcm_user us on us.uuid=rut.rcm_user_id "
			+" inner join rcm_user_company ruc on ruc.rcm_user_id=rut.rcm_user_id "
			+" inner join company comp on ruc.company_id=comp.uuid "
			+" where rut.team_id=:teamId and comp.name=:client and us.active=true "
			)
	List<UserClaimsAssignmentResponseDto> getAllActiveUsersByTeam(@Param("teamId") int teamId,
			@Param("client") String client);

}

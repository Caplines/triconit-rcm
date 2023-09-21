package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.UserAssignOffice;
import com.tricon.rcm.dto.customquery.AssignOfficeDto;
import com.tricon.rcm.util.Constants;

public interface UserAssignOfficeRepo extends JpaRepository<UserAssignOffice, Integer>{
	
	UserAssignOffice findByOfficeUuidAndTeamId(String officeId,int teamId);
	List<UserAssignOffice> findByUserUuid(String uuid);
	UserAssignOffice findByUserUuidAndOfficeUuidAndTeamId(String userUuid,String officeId,int teamId);
	
	@Query(nativeQuery = true, value = "select uoff.office_id as OfficeUuid,uoff.user_id as UserUuid,uoff.team_id as TeamId "
			+ "from rcm_user_assign_office uoff "
			+ "inner join  office  off on off.uuid=uoff.office_id "
			+ "inner join rcm_user_role ur on ur.uuid=uoff.user_id " 
			+ "where off.company_id=:clientUuid and ur.role='"+Constants.ROLE_PREFIX+Constants.TEAMLEAD+"' and uoff.team_id=:teamId limit 1")
	AssignOfficeDto findByTLByClientUuidWithRoleAndTeam(@Param("clientUuid") String clientUuid,@Param("teamId") int teamId);
	
}

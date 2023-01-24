package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.UserAssignOffice;

public interface UserAssignOfficeRepo extends JpaRepository<UserAssignOffice, Integer>{
	
	UserAssignOffice findByOfficeUuidAndTeamId(String officeId,int teamId);
	UserAssignOffice findByUserUuid(String uuid);
	UserAssignOffice findByUserUuidAndOfficeUuidAndTeamId(String userUuid,String officeId,int teamId);
}

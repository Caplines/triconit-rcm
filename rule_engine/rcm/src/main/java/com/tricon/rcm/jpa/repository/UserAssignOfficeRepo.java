package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.UserAssignOffice;

public interface UserAssignOfficeRepo extends JpaRepository<UserAssignOffice, Integer>{
	
	UserAssignOffice findByOfficeIdUuid(String officeId);
	List<UserAssignOffice>findByUserIdUuid(String uuid);
	List<UserAssignOffice>findByOfficeIdUuidIn(List<String> officeId);
	

}

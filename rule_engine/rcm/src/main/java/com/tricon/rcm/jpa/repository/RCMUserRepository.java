package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmUser;

public interface RCMUserRepository extends JpaRepository<RcmUser, String> {
	
	
	RcmUser findByUserName(String userName);
}

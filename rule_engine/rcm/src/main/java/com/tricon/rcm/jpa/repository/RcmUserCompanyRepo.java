package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmUserCompany;


public interface RcmUserCompanyRepo extends JpaRepository<RcmUserCompany, Integer>{
	
	List<RcmUserCompany> findByUserUuid(String userUuid);

}

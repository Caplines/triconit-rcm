package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmCompany;

public interface RcmCompanyRepo  extends JpaRepository<RcmCompany, String> {

	RcmCompany findByName(String name); 
	RcmCompany findByUuid(String uuid);

}

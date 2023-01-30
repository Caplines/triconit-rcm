package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmMappingTable;

public interface RcmMappingTableRepo extends JpaRepository<RcmMappingTable, Integer>{
	
	RcmMappingTable findByNameAndCompany(String name,RcmCompany comp);

}

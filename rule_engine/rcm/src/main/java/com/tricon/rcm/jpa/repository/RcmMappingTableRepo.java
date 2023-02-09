package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.db.entity.RcmMappingTable;

public interface RcmMappingTableRepo extends JpaRepository<RcmMappingTable, Integer>{
	
	RcmMappingTable findByNameAndCompany(String name,RcmCompany comp);
	List<RcmMappingTable> findByCompany(RcmCompany comp);
	List<RcmMappingTable> findByGoogleSheetId(String sheetId);

}

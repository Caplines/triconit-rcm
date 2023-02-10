package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.dto.customquery.ClientCustomDto;

public interface RcmCompanyRepo  extends JpaRepository<RcmCompany, String> {

	RcmCompany findByName(String name); 
	RcmCompany findByUuid(String uuid);
	
	@Query(nativeQuery = true, value = 
			" select uuid,name as clientName from company ")
	List<ClientCustomDto> findAllClients();

}

package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmCompany;
import com.tricon.rcm.dto.customquery.ClientCustomDto;

public interface RcmCompanyRepo  extends JpaRepository<RcmCompany, String> {

	RcmCompany findByName(String name); 
	RcmCompany findByUuid(String uuid);
	
	@Query(nativeQuery = true, value = 
			" select uuid,name as clientName from company order by name asc ")
	List<ClientCustomDto> findAllClients();
	
	@Query(value = "select cmp.uuid,cmp.name as clientName from rcm_user_company us "
			+ " inner join company cmp on cmp.uuid=us.company_id where rcm_user_id =:userUuid order by cmp.name", nativeQuery = true)
	List<ClientCustomDto> findAllClientsOfAssociatedUser(@Param("userUuid") String userUuid);
	
	@Query(value = "select cmp.uuid as clientUUid from rcm_user_company us "
			+ " inner join company cmp on cmp.uuid=us.company_id where rcm_user_id =:userUuid order by cmp.name", nativeQuery = true)
	List<String> findAllClientUUidOfAssociatedUser(@Param("userUuid") String userUuid);

}

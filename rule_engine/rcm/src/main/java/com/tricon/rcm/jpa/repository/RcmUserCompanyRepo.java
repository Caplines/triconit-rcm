package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmUserCompany;
import com.tricon.rcm.dto.customquery.CompanyIdAndNameDto;


public interface RcmUserCompanyRepo extends JpaRepository<RcmUserCompany, Integer>{
	
	List<RcmUserCompany> findByUserUuid(String userUuid);
	
	@Query(value = "select company_id from rcm_user_company where rcm_user_id =:userUuid", nativeQuery = true)
	List<String> findAssociatedCompanyIdByUserUuid(@Param("userUuid") String userUuid);

	@Query(value = "select company_id uuid,cmp.name name from rcm_user_company us "
			+ " inner join company cmp on cmp.uuid=us.company_id where rcm_user_id =:userUuid", nativeQuery = true)
	List<CompanyIdAndNameDto> findAssociatedCompanyIdWithNameByUserUuid(@Param("userUuid") String userUuid);
	
	RcmUserCompany findByCompanyUuidAndUserUuid(String companyUuid,String userUuid);
}

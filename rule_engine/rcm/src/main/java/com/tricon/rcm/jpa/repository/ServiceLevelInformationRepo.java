package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmServiceLevelInformation;

public interface ServiceLevelInformationRepo extends JpaRepository<RcmServiceLevelInformation, Integer>{

	List<RcmServiceLevelInformation> findByClaimClaimUuid(String claimUuid);
	
	@Query(value = "select * from rcm_service_level_section where id in (SELECT MAX(id) "
			+ "FROM rcm_service_level_section s " 
			+ "WHERE  s.claim_id=:claimUuid and s.mark_as_deleted=false "
			+ "group by s.service_code " 
			+ "ORDER BY s.final_submit,s.created_date desc)", nativeQuery = true)
	List<RcmServiceLevelInformation> findServiceLevelCodesByClaimUuid(@Param("claimUuid") String claimUuid);
	
	@Query(value = "select * from rcm_service_level_section where id in (SELECT MAX(id) "
			+ "FROM rcm_service_level_section s " 
			+ "WHERE  s.claim_id=:claimUuid and s.mark_as_deleted=false "
			+ "and final_submit=true "
			+ "group by s.service_code,team_id " 
			+ "ORDER BY s.final_submit,s.created_date desc)", nativeQuery = true)
	List<RcmServiceLevelInformation> findServiceLevelNotesByClaimUuid(@Param("claimUuid") String claimUuid);
}

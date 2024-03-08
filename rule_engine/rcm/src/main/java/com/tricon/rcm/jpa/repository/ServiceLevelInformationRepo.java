package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmServiceLevelInformation;
import com.tricon.rcm.dto.customquery.RcmServiceNotesDto;

public interface ServiceLevelInformationRepo extends JpaRepository<RcmServiceLevelInformation, Integer>{

	List<RcmServiceLevelInformation> findByClaimClaimUuid(String claimUuid);
	
	@Query(value = "select * from rcm_service_level_section where id in (SELECT MAX(id) "
			+ "FROM rcm_service_level_section s "
			+ "WHERE  s.claim_id=:claimUuid and s.mark_as_deleted=false "
			+ "and s.active is true "
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
	
	@Query(value="SELECT COALESCE(MAX(group_run), 0) from rcm_service_level_section where claim_id=:claimUuid",nativeQuery=true)
	int getMaxRunFromServiceLevel(@Param("claimUuid") String claimUuid);
	
	@Query(value = "select notes as Notes,service_code as ServiceCode,u.first_name as CreatedBy,t.description as TeamName,s.created_date as Date from rcm_service_level_section s " + "inner join rcm_user u on u.uuid=s.created_by "
			+ "inner join rcm_team t on t.id=s.team_id where s.claim_id=:claimUuid "
			+ "and s.group_run <>:maxGroupRun and s.mark_as_deleted=false "
			+ "and s.active is true", nativeQuery = true)
	List<RcmServiceNotesDto> findOldServiceLevelCodesByClaimUuid(@Param("claimUuid") String claimUuid,
			@Param("maxGroupRun") int maxGroupRun);
}

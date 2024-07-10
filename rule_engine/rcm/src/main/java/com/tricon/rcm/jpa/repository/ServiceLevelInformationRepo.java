package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmServiceLevelInformation;
import com.tricon.rcm.dto.customquery.RcmServiceNotesDto;

public interface ServiceLevelInformationRepo extends JpaRepository<RcmServiceLevelInformation, Integer>{

	List<RcmServiceLevelInformation> findByClaimClaimUuid(String claimUuid);
	
	@Query(value = "select * from rcm_service_level_section s " 
	        + "inner join rcm_user u on u.uuid=s.created_by "
			+ "inner join rcm_team t on t.id=s.team_id where s.claim_id=:claimUuid "
			+ "and s.group_run =:maxGroupRun and s.mark_as_deleted=false "
			+ "and s.active is true", nativeQuery = true)
	List<RcmServiceLevelInformation> findServiceLevelCodesByClaimUuid(@Param("claimUuid") String claimUuid,@Param("maxGroupRun") int maxGroupRun);
	
	
	@Query(value="SELECT COALESCE(MAX(group_run), 0) from rcm_service_level_section where claim_id=:claimUuid",nativeQuery=true)
	int getMaxRunFromServiceLevel(@Param("claimUuid") String claimUuid);
	
	@Query(value = "select notes as Notes,tooth,surface,service_code as ServiceCode,u.first_name as CreatedBy,t.description as TeamName,s.created_date as Date from rcm_service_level_section s " + "inner join rcm_user u on u.uuid=s.created_by "
			+ "inner join rcm_team t on t.id=s.team_id where s.claim_id=:claimUuid "
			+ "and s.group_run <>:maxGroupRun and s.mark_as_deleted=false "
			+ "and s.active is true", nativeQuery = true)
	List<RcmServiceNotesDto> findOldServiceLevelCodesByClaimUuid(@Param("claimUuid") String claimUuid,
			@Param("maxGroupRun") int maxGroupRun);
	
	@Query(value = "select notes as Notes,tooth,surface,service_code as ServiceCode,u.first_name as CreatedBy,t.description as TeamName,s.created_date as Date from rcm_service_level_section s " + "inner join rcm_user u on u.uuid=s.created_by "
			+ "inner join rcm_team t on t.id=s.team_id where s.claim_id=:claimUuid "
			+ "and  s.mark_as_deleted=false "
			+ "and s.active is true", nativeQuery = true)
	List<RcmServiceNotesDto> findAllServiceLevelCodesByClaimUuid(@Param("claimUuid") String claimUuid);
	
	@Query(value = "select * from rcm_service_level_section s "
			+ "where s.claim_id=:claimUuid and s.service_code in(:serviceCodes)" + "and s.mark_as_deleted=false "
			+ "and s.active is true", nativeQuery = true)
	List<RcmServiceLevelInformation> findServiceCodesByClaimUuidAndCodes(@Param("claimUuid") String claimUuid,
			@Param("serviceCodes") List<String> serviceCodes);
}

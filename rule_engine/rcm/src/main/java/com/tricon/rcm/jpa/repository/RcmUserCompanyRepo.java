package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmUserCompany;
import com.tricon.rcm.dto.customquery.AssignOfficeDto;
import com.tricon.rcm.dto.customquery.ClientCustomDto;
import com.tricon.rcm.dto.customquery.CompanyIdAndNameDto;
import com.tricon.rcm.util.Constants;


public interface RcmUserCompanyRepo extends JpaRepository<RcmUserCompany, Integer>{
	
	List<RcmUserCompany> findByUserUuid(String userUuid);
	
	@Query(value = "select company_id from rcm_user_company where rcm_user_id =:userUuid", nativeQuery = true)
	List<String> findAssociatedCompanyIdByUserUuid(@Param("userUuid") String userUuid);
	
	@Query(value = "select c.name from rcm_user_company rc inner join company c on c.uuid=rc.company_id where rcm_user_id =:userUuid", nativeQuery = true)
	List<String> findAssociatedCompanyNameUserUuid(@Param("userUuid") String userUuid);

	@Query(value = "select company_id uuid,cmp.name name from rcm_user_company us "
			+ " inner join company cmp on cmp.uuid=us.company_id where rcm_user_id =:userUuid order by cmp.name", nativeQuery = true)
	List<CompanyIdAndNameDto> findAssociatedCompanyIdWithNameByUserUuid(@Param("userUuid") String userUuid);
	
	RcmUserCompany findByCompanyUuidAndUserUuid(String companyUuid,String userUuid);
	
	@Query(nativeQuery = true, value = "select count(uc.rcm_user_id) from rcm_user_company uc "
			+ "inner join rcm_user_role ur on ur.uuid=uc.rcm_user_id "
			+ "inner join rcm_user user on user.uuid=ur.uuid "
			+ "inner join rcm_user_team ut on ut.rcm_user_id=uc.rcm_user_id "
			+ "where uc.company_id=:clientUuid and ur.role='"+Constants.ROLE_PREFIX+Constants.TEAMLEAD+"' and ut.team_id=:teamId and user.active is true")
	int findExistingTLByClientUuidAndTeam(@Param("clientUuid") String clientUuid, @Param("teamId") int teamId);
	
	@Query(nativeQuery = true, value = "select ut.team_id as TeamId ,uc.rcm_user_id as UserUuid from rcm_user_company uc "
			+ "inner join rcm_user_role ur on ur.uuid=uc.rcm_user_id "
			+ "inner join rcm_user user on user.uuid=ur.uuid "
			+ "inner join rcm_user_team ut on ut.rcm_user_id=uc.rcm_user_id "
			+ "where uc.company_id=:clientUuid and ur.role='"+Constants.ROLE_PREFIX+Constants.TEAMLEAD+"' and ut.team_id=:teamId and user.active is true limit 1")
	AssignOfficeDto findAnyExistingTLByClientUuidAndTeamId(@Param("clientUuid") String clientUuid,@Param("teamId") int teamId);
	
	@Query(nativeQuery = true, value = "select count(uoff.user_id) from rcm_user_assign_office uoff "
			+ "inner join office  off on off.uuid=uoff.office_id "
			+ "inner join rcm_user_role ur on ur.uuid=uoff.user_id "
			+ "where off.company_id=:clientUuid "
			+ "AND (ur.role = '"+Constants.ROLE_PREFIX+Constants.TEAMLEAD+"' OR ur.role = '"+Constants.ROLE_PREFIX+Constants.ASSOCIATE+"') "
			+ "and uoff.team_id=:teamId "
			+ "and uoff.office_id=:officeId ")
	int findExistingTLByClientUuidAndTeamAndOffice(@Param("clientUuid") String clientUuid, @Param("teamId") int teamId,@Param("officeId") String officeId);
}

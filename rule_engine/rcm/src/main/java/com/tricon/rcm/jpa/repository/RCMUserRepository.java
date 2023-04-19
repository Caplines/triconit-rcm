package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.RcmUserToDto;
import com.tricon.rcm.dto.UserSearchDto;
import com.tricon.rcm.dto.customquery.RcmUserDetails;
import com.tricon.rcm.dto.customquery.TreatmentPlanLinkDto;

public interface RCMUserRepository extends JpaRepository<RcmUser, String> {
	
	RcmUser findByUuid(String uuid);
	RcmUser findByEmail(String email);
	
	@Query(value="select u.uuid as Uuid,u.email as Email,active as Active,concat(u.first_name,' ',u.last_name)as FullName,u.first_name as FirstName,u.last_name as LastName from rcm_user u join rcm_user_company ruc where ruc.company_id=:clientUuid and ruc.rcm_user_id=u.uuid and u.email=:email",nativeQuery=true)
	RcmUserDetails findUserByClientUuid(String email,String clientUuid);
	
	@Query(value=""+
			" select distinct u.uuid as Uuid,u.email as Email,active as Active,concat(u.first_name,' ',u.last_name)as FullName from rcm_user "+
			" u inner join rcm_user_team rt on rt.rcm_user_id=u.uuid "+
			" inner join rcm_user_company us on us.rcm_user_id=u.uuid "+
			"  where u.active=1 AND rt.team_id=:teamId  and us.company_id=:companyUuid",nativeQuery=true)
	List<RcmUserToDto> findUsersByTeamIdAndCompanyId(@Param("teamId") int teamId,@Param("companyUuid")  String companyUuid);
	
	@Query(value="select u.uuid as Uuid,u.first_name as FirstName,u.last_name as LastName from rcm_user u "
			+ "inner join rcm_user_role r on u.uuid=r.uuid "
			+ "inner join rcm_user_team t on u.uuid=t.rcm_user_id "
			+ "inner join rcm_user_company uc on u.uuid=uc.rcm_user_id "
			+ "where r.role=:role and u.active=1 and uc.company_id=:clientUuid and t.team_id=:teamId",nativeQuery=true)
	List<RcmUserToDto>findUsersByRoleAndTeamId(@Param("role") String role,@Param("clientUuid")String clientUuid,@Param("teamId")int teamId);
	
	@Query(value="select c.Uuid,c.Email,c.FullName,c.ClientName,c.Roles,c.Active "
			+ "from (select GROUP_CONCAT(distinct rc.name) as ClientName,"
			+ "GROUP_CONCAT(distinct ur.role)as Roles,"
			+ "concat(us.first_name,' ',us.last_name)as FullName,"
			+ "us.uuid as Uuid,us.email as Email,us.active as Active "
			+ "from rcm_user_company uc "
			+ "inner join company rc on rc.uuid=uc.company_id "
			+ "inner join rcm_user us on uc.rcm_user_id=us.uuid "
			+ "inner join rcm_user_role ur on us.uuid=ur.uuid where us.email not in(:ignoreUser) and rc.uuid =:clientUuid group by us.uuid)c",
			countQuery = "select count(*) from(select GROUP_CONCAT(distinct rc.name) as ClientName from rcm_user_company uc "
					+ "inner join company rc on rc.uuid=uc.company_id "
					+ "inner join rcm_user us on uc.rcm_user_id=us.uuid "
			+ "where email not in(:ignoreUser) and uc.company_id=:clientUuid group by us.uuid)c",nativeQuery = true)
	Page<RcmUserToDto> getUsersBySuperAdminUsingClicentUuid(Pageable page,@Param("clientUuid")String clientUuid,@Param("ignoreUser")List<String> ignoreUser);
	

	@Query(value = "select c.Uuid,c.Email,c.FullName,c.ClientName,c.Roles,c.Active "
			+ "from (select GROUP_CONCAT(distinct rc.name) as ClientName,"
			+ "GROUP_CONCAT(distinct ur.role)as Roles,"
			+ "concat(us.first_name,' ',us.last_name)as FullName,"
			+ "us.uuid as Uuid,us.email as Email,us.active as Active "
			+ "from rcm_user_company uc "
			+ "inner join company rc on rc.uuid=uc.company_id "
			+ "inner join rcm_user us on uc.rcm_user_id=us.uuid "
			+ "inner join rcm_user_role ur on us.uuid=ur.uuid where rc.uuid =:clientUuid and us.email not in(:ignoreUser) and ur.role not in ('ROLE_SUPER_ADMIN') group by us.uuid)c", countQuery = "select count(*) from(select GROUP_CONCAT(distinct rc.name) as ClientName,GROUP_CONCAT(distinct ur.role)from rcm_user_company uc"
			+ " inner join company rc on rc.uuid=uc.company_id"
			+ " inner join rcm_user us on uc.rcm_user_id=us.uuid"
			+ " inner join rcm_user_role ur on us.uuid=ur.uuid where email not in(:ignoreUser) and uc.company_id =:clientUuid and ur.role not in ('ROLE_SUPER_ADMIN') group by us.uuid)c", nativeQuery = true)
	Page<RcmUserToDto> getUserByCompanyUuidWithPagination(@Param("clientUuid") String clientUuid, Pageable page,@Param("ignoreUser")List<String> ignoreUser);
	
	@Query(value = "select c.Uuid,c.Email,c.FullName,c.ClientName,c.Roles,c.Active"
			+ "	from (select GROUP_CONCAT(distinct rc.name) as ClientName,GROUP_CONCAT(distinct ur.role)as Roles,concat(us.first_name,' ',us.last_name)as FullName,"
			+ " us.uuid as Uuid,us.email as Email,us.active as Active from rcm_user_company uc"
			+ " inner join company rc on rc.uuid=uc.company_id"
			+ "	inner join rcm_user us on uc.rcm_user_id=us.uuid"
			+ "	inner join rcm_user_role ur on us.uuid=ur.uuid where us.email not in(:ignoreUser) group by us.uuid)c",
			countQuery = "select count(*) from(select GROUP_CONCAT(distinct rc.name) as ClientName from rcm_user_company uc "
					+ "inner join company rc on rc.uuid=uc.company_id "
					+ "inner join rcm_user us on uc.rcm_user_id=us.uuid "
			+ " where email not in(:ignoreUser) group by us.uuid)c", nativeQuery = true)
	Page<RcmUserToDto> getAllUserBySuperAdmin(Pageable page,@Param("ignoreUser")List<String> ignoreUser);
	

	@Query(value = "select c.Uuid,c.Email,c.FullName,c.ClientName,c.Roles,c.Active"
			+ " from (select GROUP_CONCAT(distinct rc.name) as ClientName,GROUP_CONCAT(distinct ur.role)as Roles,"
			+ " us.uuid as Uuid,us.active as Active,concat(us.first_name,' ',us.last_name)as FullName,"
			+ " us.email as Email from rcm_user_company uc"
			+ " inner join company rc on rc.uuid=uc.company_id"
			+ " inner join rcm_user us on uc.rcm_user_id=us.uuid"
			+ " inner join rcm_user_role ur on us.uuid=ur.uuid where rc.uuid in(:clientUuid) and us.email not in(:ignoreUser) and ur.role not in ('ROLE_SUPER_ADMIN') group by us.uuid)c", countQuery = "select count(*) from(select GROUP_CONCAT(distinct rc.name),GROUP_CONCAT(distinct ur.role) from rcm_user_company uc"
			+ " inner join company rc on rc.uuid=uc.company_id"
			+ " inner join rcm_user us on uc.rcm_user_id=us.uuid"
			+ " inner join rcm_user_role ur on us.uuid=ur.uuid where email not in(:ignoreUser) and uc.company_id in(:clientUuid) and ur.role not in ('ROLE_SUPER_ADMIN') group by us.uuid)c", nativeQuery = true)
	Page<RcmUserToDto> getAllUserByPagination(Pageable page,@Param("ignoreUser")List<String> ignoreUser,@Param("clientUuid")List<String>clientUuid);

	@Modifying
	@Query(value="update rcm_user set active=:status,updated_by=:updatedBy,updated_date=CURRENT_TIMESTAMP where uuid in(:uuid)",nativeQuery = true)
	void enableOrDisableStatus(@Param("status")int active,@Param("updatedBy")String updatedBy,@Param("uuid")List<String> uuid);

	@Query(value = "select u.uuid as Uuid,u.email as Email,"
			+ "u.first_name as FirstName,u.last_name as LastName from rcm_user u "
			+ "inner join rcm_user_company ruc on ruc.rcm_user_id=u.uuid "
			+ "where (first_name like %:search% or email like %:search% or last_name like %:search%) and ruc.company_id=:clientUuid", nativeQuery = true)
	List<UserSearchDto> findByUserDetailsByAdmin(@Param("search") String search,
			@Param("clientUuid") String clientUuid);
	
	List<RcmUser>findByUuidIn(List<String> userId);
	
	@Query(value="select u.uuid as Uuid from rcm_user u inner join rcm_user_role r on u.uuid=r.uuid where r.role=:role",nativeQuery=true)
	List<RcmUserToDto> findSuperAdminUser(@Param("role") String role);
	
	@Query(value = "select u.uuid as Uuid,u.email as Email,"
			+ "u.first_name as FirstName,u.last_name as LastName from rcm_user u "
			+ "where (first_name like %:search% or email like %:search% or last_name like %:search%)", nativeQuery = true)
	List<UserSearchDto> findByUserDetailsBySuperAdmin(@Param("search") String search);

	//TreatmentPlan-link data query
	
	@Query(value = "select rc.date_last_updated_es as DatePlan,rcd.appt_id as Appt,rc.provider_id as Provider,rcd.service_code as Service,"
			+ "rcd.description as Description,rcd.tooth as Tth,rcd.surface as Surface,rcd.fee as Fee,rcd.est_insurance as Ins,rcd.patient_portion as Pat "
			+ "FROM rcm_claim_detail rcd inner join rcm_claims rc on rc.claim_uuid=rcd.claim_id "
			+ "where rc.claim_uuid=:claimUuid", nativeQuery = true)
	List<TreatmentPlanLinkDto> findTreatmentPlanLinkData(@Param("claimUuid") String claimUuid);
	
	@Query(value = "select count(cl.issue) from rcm_issue_claims cl "
			+ "left join office off on  off.uuid=cl.office_id  where off.company_id=:clientUuid and cl.resolved is false", nativeQuery = true)
	int findCountsOfIssueClaims(@Param("clientUuid") String clientUuid);
}

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

public interface RCMUserRepository extends JpaRepository<RcmUser, String> {
	
	RcmUser findByUuid(String uuid);
	RcmUser findByEmail(String email);
	
	@Query(value="select u.uuid as Uuid,u.email as Email,active as Active,concat(u.first_name,' ',u.last_name)as FullName,u.first_name as FirstName,u.last_name as LastName from rcm_user u join rcm_user_company ruc where ruc.company_id=:clientUuid and ruc.rcm_user_id=u.uuid and u.email=:email",nativeQuery=true)
	RcmUserDetails findUserByClientUuid(String email,String clientUuid);
	
	@Query(value="select u.uuid as Uuid,u.email as Email,active as Active,concat(u.first_name,' ',u.last_name)as FullName from rcm_user u where u.active=1 AND u.team_id=:teamId And u.company_id=:companyUuid",nativeQuery=true)
	List<RcmUserToDto> findUsersByTeamId(@Param("teamId") int teamId,@Param("companyUuid")  String companyUuid);
	
	@Query(value="select u.uuid as Uuid,u.first_name as FirstName,u.last_name as LastName from rcm_user u inner join rcm_user_role r on u.uuid=r.uuid where r.role=:role and u.active=1 and u.company_id=:clientUuid",nativeQuery=true)
	List<RcmUserToDto> findUsersByRole(@Param("role") String role,@Param("clientUuid")String clientUuid);
	
	@Query(value="select c.Uuid,c.Email,c.FullName,c.ClientName,c.Roles,c.Active "
			+ "from (select GROUP_CONCAT(distinct rc.name) as ClientName,"
			+ "GROUP_CONCAT(distinct case when SUBSTRING(ur.role,6)='TL' then 'TeamLead'"
			+ "when SUBSTRING(ur.role,6)='ASSO' then 'Associate'"
			+ "when SUBSTRING(ur.role,6)='ADMIN' then 'Admin'"
			+ "when SUBSTRING(ur.role,6)='REPORTING' then 'Reporting'"
			+ "when SUBSTRING(ur.role,6)='SUPER_ADMIN' then 'Super Admin'"
			+ "Else 'Role Not Match' End)as Roles,"
			+ "concat(us.first_name,' ',us.last_name)as FullName,"
			+ "us.uuid as Uuid,us.email as Email,us.active as Active "
			+ "from rcm_user_company uc "
			+ "inner join company rc on rc.uuid=uc.company_id "
			+ "inner join rcm_user us on uc.rcm_user_id=us.uuid "
			+ "inner join rcm_user_role ur on us.uuid=ur.uuid where us.email!=:ignoreUser and rc.uuid =:clientUuid group by us.uuid)c",
			countQuery = "select count(*) from(select GROUP_CONCAT(distinct rc.name) as ClientName,GROUP_CONCAT(distinct(ur.role)) as Roles from rcm_user_company uc "
					+ "inner join company rc on rc.uuid=uc.company_id "
					+ "inner join rcm_user us on uc.rcm_user_id=us.uuid "
					+ "inner join rcm_user_role ur on us.uuid=ur.uuid "
			+ "where email!=:ignoreUser and uc.company_id=:clientUuid group by us.uuid)c",nativeQuery = true)
	Page<RcmUserToDto> getUsersBySuperAdminUsingClicentUuid(Pageable page,@Param("clientUuid")String clientUuid,@Param("ignoreUser")String ignoreUser);
	

	@Query(value = "select c.Uuid,c.Email,c.FullName,c.ClientName,c.Roles,c.Active "
			+ "from (select GROUP_CONCAT(distinct rc.name) as ClientName,"
			+ "GROUP_CONCAT(distinct case when SUBSTRING(ur.role,6)='TL' then 'TeamLead'"
			+ "when SUBSTRING(ur.role,6)='ASSO' then 'Associate'"
			+ "when SUBSTRING(ur.role,6)='ADMIN' then 'Admin'"
			+ "when SUBSTRING(ur.role,6)='REPORTING' then 'Reporting'"
			+ "when SUBSTRING(ur.role,6)='SUPER_ADMIN' then 'Super Admin'"
			+ "Else 'Role Not Match' End)as Roles,"
			+ "concat(us.first_name,' ',us.last_name)as FullName,"
			+ "us.uuid as Uuid,us.email as Email,us.active as Active "
			+ "from rcm_user_company uc "
			+ "inner join company rc on rc.uuid=uc.company_id "
			+ "inner join rcm_user us on uc.rcm_user_id=us.uuid "
			+ "inner join rcm_user_role ur on us.uuid=ur.uuid where rc.uuid =:clientUuid and us.email!=:ignoreUser and ur.role not in ('ROLE_SUPER_ADMIN') group by us.uuid)c", countQuery = "select count(*) from(select GROUP_CONCAT(distinct rc.name) as ClientName,GROUP_CONCAT(distinct(ur.role))from rcm_user_company uc"
			+ " inner join company rc on rc.uuid=uc.company_id"
			+ " inner join rcm_user us on uc.rcm_user_id=us.uuid"
			+ " inner join rcm_user_role ur on us.uuid=ur.uuid where email!=:ignoreUser and uc.company_id =:clientUuid and ur.role not in ('ROLE_SUPER_ADMIN') group by us.uuid)c", nativeQuery = true)
	Page<RcmUserToDto> getUserByCompanyUuidWithPagination(@Param("clientUuid") String clientUuid, Pageable page,@Param("ignoreUser")String ignoreUser);
	
	@Query(value = "select c.Uuid,c.Email,c.FullName,c.ClientName,c.Roles,c.Active"
			+ "	from (select GROUP_CONCAT(distinct rc.name) as ClientName,GROUP_CONCAT(distinct case when SUBSTRING(ur.role,6)='TL' then 'TeamLead'"
			+ "when SUBSTRING(ur.role,6)='ASSO' then 'Associate'"
			+ "when SUBSTRING(ur.role,6)='ADMIN' then 'Admin'"
			+ "when SUBSTRING(ur.role,6)='REPORTING' then 'Reporting'"
			+ "when SUBSTRING(ur.role,6)='SUPER_ADMIN' then 'Super Admin'"
			+ "Else 'Role Not Match'"
			+ " End)as Roles,concat(us.first_name,' ',us.last_name)as FullName,"
			+ " us.uuid as Uuid,us.email as Email,us.Active from rcm_user_company uc"
			+ " inner join company rc on rc.uuid=uc.company_id"
			+ "	inner join rcm_user us on uc.rcm_user_id=us.uuid"
			+ "	inner join rcm_user_role ur on us.uuid=ur.uuid where rc.uuid in(:clientUuid) AND us.email!=:ignoreUser group by us.uuid)c",
			countQuery = "select count(*) from(select GROUP_CONCAT(distinct rc.name) as ClientName,GROUP_CONCAT(distinct(ur.role)) as Roles from rcm_user_company uc "
					+ "inner join company rc on rc.uuid=uc.company_id "
					+ "inner join rcm_user us on uc.rcm_user_id=us.uuid "
					+ "inner join rcm_user_role ur on us.uuid=ur.uuid "
			+ " where email!=:ignoreUser and uc.company_id in(:clientUuid) group by us.uuid)c", nativeQuery = true)
	Page<RcmUserToDto> getAllUserBySuperAdmin(Pageable page,@Param("ignoreUser")String ignoreUser,@Param("clientUuid")List<String>clientUuid);
	

	@Query(value = "select c.Uuid,c.Email,c.FullName,c.ClientName,c.Roles,c.Active"
			+ " from (select GROUP_CONCAT(distinct rc.name) as ClientName,GROUP_CONCAT(distinct case when SUBSTRING(ur.role,6)='TL' then 'TeamLead'"
			+ "when SUBSTRING(ur.role,6)='ASSO' then 'Associate'"
			+ "when SUBSTRING(ur.role,6)='ADMIN' then 'Admin'"
			+ "when SUBSTRING(ur.role,6)='REPORTING' then 'Reporting'"
			+ "when SUBSTRING(ur.role,6)='SUPER_ADMIN' then 'Super Admin'"
			+ "Else 'Role Not Match'"
			+ " End)as Roles,"
			+ " us.uuid as Uuid,us.Active,concat(us.first_name,' ',us.last_name)as FullName,"
			+ " us.email as Email from rcm_user_company uc"
			+ " inner join company rc on rc.uuid=uc.company_id"
			+ " inner join rcm_user us on uc.rcm_user_id=us.uuid"
			+ " inner join rcm_user_role ur on us.uuid=ur.uuid where rc.uuid in(:clientUuid) and us.email!=:ignoreUser and ur.role not in ('ROLE_SUPER_ADMIN') group by us.uuid)c", countQuery = "select count(*) from(select GROUP_CONCAT(distinct rc.name),GROUP_CONCAT(distinct(ur.role)) from rcm_user_company uc"
			+ " inner join company rc on rc.uuid=uc.company_id"
			+ " inner join rcm_user us on uc.rcm_user_id=us.uuid"
			+ " inner join rcm_user_role ur on us.uuid=ur.uuid where email!=:ignoreUser and uc.company_id in(:clientUuid) and ur.role not in ('ROLE_SUPER_ADMIN') group by us.uuid)c", nativeQuery = true)
	Page<RcmUserToDto> getAllUserByPagination(Pageable page,@Param("ignoreUser")String ignoreUser,@Param("clientUuid")List<String>clientUuid);

	@Modifying
	@Query(value="update rcm_user set active=:status,updated_by=:updatedBy,updated_date=CURRENT_TIMESTAMP where uuid in(:uuid)",nativeQuery = true)
	void enableOrDisableStatus(@Param("status")int active,@Param("updatedBy")String updatedBy,@Param("uuid")List<String> uuid);

	@Query(value = "select uuid as Uuid,active as Active,concat(first_name,' ',last_name)as FullName,email as Email,"
			+ "first_name as FirstName,last_name as LastName,team_id as TeamNameid from rcm_user where"
			+ "(first_name like %:search% or email like %:search% or last_name like %:search%)",nativeQuery = true)
	List<UserSearchDto> findByUserDetails(@Param("search")  String search);
	
	List<RcmUser>findByUuidIn(List<String> userId);
	
	@Query(value="select u.uuid as Uuid from rcm_user u inner join rcm_user_role r on u.uuid=r.uuid where r.role=:role",nativeQuery=true)
	List<RcmUserToDto> findSuperAdminUser(@Param("role") String role);

}

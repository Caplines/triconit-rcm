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
	
	@Query(value="select uuid as Uuid,active as Active,email as Email,concat(first_name,' ',last_name)as FullName,(select name from company c where c.uuid= rcm_user.company_id)as CompanyName from rcm_user where company_id=:uuid AND email!=:ignoreUser",nativeQuery = true)
	List<RcmUserToDto> getAllUserByCompanyUuid(@Param("uuid")String uuid,@Param("ignoreUser")String ignoreUser);
	

	@Query(value = "select uuid as Uuid,active as Active,email as Email,concat(first_name,' ',last_name)as FullName from rcm_user where company_id=:uuid AND email!=:ignoreUser", countQuery = "select count(*) from rcm_user where company_id=:uuid AND email!=:ignoreUser", nativeQuery = true)
	Page<RcmUserToDto> getAllUserByCompanyUuidWithPagination(@Param("uuid") String uuid, Pageable page,@Param("ignoreUser")String ignoreUser);
	
	@Query(value="select uuid as Uuid,active as Active,email as Email,concat(first_name,' ',last_name)as FullName,(select c.company_id from rcm_user_company c where c.rcm_user_id=rcm_user.uuid)as CompanyName from rcm_user where email!=:ignoreUser",nativeQuery = true)
	List<RcmUserToDto> getAllUser(@Param("ignoreUser")String ignoreUser);
	

	@Query(value = "select uuid as Uuid,active as Active,email as Email,concat(first_name,' ',last_name)as FullName,(select name from company c where c.uuid= rcm_user.company_id)as CompanyName from rcm_user where email!=:ignoreUser", countQuery = "select count(*) from rcm_user where email!=:ignoreUser", nativeQuery = true)
	Page<RcmUserToDto> getAllUserByPagination(Pageable page,@Param("ignoreUser")String ignoreUser);

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

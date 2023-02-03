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

public interface RCMUserRepository extends JpaRepository<RcmUser, String> {
	
	RcmUser findByUuid(String uuid);
	RcmUser findByEmail(String email);
	
	@Query(value="select u.uuid as Uuid,u.email as Email,active as Active,concat(u.first_name,' ',u.last_name)as FullName from rcm_user u where u.active=1 AND u.team_id=:teamId And u.company_id=:companyUuid",nativeQuery=true)
	List<RcmUserToDto> findUsersByTeamId(int teamId,String companyUuid);
	
	@Query(value="select u.uuid as Uuid,u.email as Email,active as Active,concat(u.first_name,' ',u.last_name)as FullName from rcm_user u join rcm_user_role r on u.uuid=r.uuid where r.role=?1 and u.active=1",nativeQuery=true)
	List<RcmUserToDto> findUsersByRole(String role);
	
	@Query(value="select uuid as Uuid,active as Active,email as Email,concat(first_name,' ',last_name)as FullName from rcm_user where company_id=?1",nativeQuery = true)
	List<RcmUserToDto> getAllUser(String uuid);
	

	@Query(value = "select uuid as Uuid,active as Active,email as Email,concat(first_name,' ',last_name)as FullName from rcm_user where company_id=:uuid", countQuery = "select count(*) from rcm_user where company_id=:uuid", nativeQuery = true)
	Page<RcmUserToDto> getAllUserByPagination(@Param("uuid") String uuid, Pageable page);

	@Modifying
	@Query(value="update rcm_user set active=:status,updated_by=:updatedBy,updated_date=CURRENT_TIMESTAMP where uuid in(:uuid)",nativeQuery = true)
	void enableOrDisableStatus(@Param("status")int active,@Param("updatedBy")String updatedBy,@Param("uuid")List<String> uuid);

	@Query(value = "select uuid as Uuid,active as Active,concat(first_name,' ',last_name)as FullName,email as Email,"
			+ "first_name as FirstName,last_name as LastName,team_id as TeamNameid from rcm_user where"
			+ "(first_name like %:search% or email like %:search% or last_name like %:search%)",nativeQuery = true)
	List<UserSearchDto> findByUserDetails(String search);
	
	List<RcmUser>findByUuidIn(List<String> userId);
}

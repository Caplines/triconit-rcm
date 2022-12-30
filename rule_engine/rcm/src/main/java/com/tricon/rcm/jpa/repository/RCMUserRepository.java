package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.RcmUserToDto;

public interface RCMUserRepository extends JpaRepository<RcmUser, String> {
	
	
	RcmUser findByUserNameOrEmail(String userName,String email);
	RcmUser findByUserNameAndActive(String userName,int active);
	RcmUser findByUuid(String uuid);
	RcmUser findByUserName(String userName);
	
	@Query(value="select u.uuid as Uuid,u.userName as UserName,concat(u.first_name,' ',u.last_name)as FullName from rcm_user u join rcm_user_role r on u.uuid=r.uuid where r.role=?1 and u.active=1",nativeQuery=true)
	List<RcmUserToDto> findUsersByRole(String role);
	
	@Query(value="select uuid as Uuid,active as Active,userName as UserName,concat(first_name,' ',last_name)as FullName from rcm_user where company_id=?1",nativeQuery = true)
	List<RcmUserToDto> getAllUser(String uuid);
	
	@Modifying
	@Query(value="update rcm_user set active=:status,updated_by=:updatedBy,updated_date=CURRENT_TIMESTAMP where uuid in(:uuid)",nativeQuery = true)
	void enableOrDisableStatus(@Param("status")int active,@Param("updatedBy")String updatedBy,@Param("uuid")List<String> uuid);
}

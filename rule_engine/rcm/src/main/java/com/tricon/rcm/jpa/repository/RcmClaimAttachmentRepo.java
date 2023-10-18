package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaimAttachment;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.customquery.RcmClaimAttachmentDto;
import com.tricon.rcm.util.Constants;

public interface RcmClaimAttachmentRepo extends JpaRepository<RcmClaimAttachment, Integer>{

	
	@Query(value = "select a.id as Id,a.file_name as FileName,a.is_deleted as IsDeleted,a.attachment_type_id as AttachmentId,u.first_name as CreatedBy,a.created_date as CreatedDate,t.name as UploadedByTeam,u.email as UploadedByUserUuid "
			+ "from rcm_claim_attachment a "
			+ "inner join rcm_attachment_type atype on atype.id=a.attachment_type_id "
			+ "inner join rcm_claims c on c.claim_uuid=a.claim_id "
			+ "inner join rcm_team t on t.id=a.created_by_team "
			+ "inner join rcm_user u on u.uuid=a.created_by "
			+ "where a.claim_id=:claimuUuid and a.is_deleted is false and a.status is true and t.active is true", nativeQuery = true)
	List<RcmClaimAttachmentDto> findByAttachmentId(@Param("claimuUuid") String claimuUuid);
	
	@Modifying
	@Query(value = "update rcm_claim_attachment set rename_file =:renameFile,status = false,is_deleted = true,updated_by=:updatedBy,updated_date=now() where id =:attachmentId", nativeQuery = true)
	int updateAttachmentStatusById(@Param("updatedBy") RcmUser updatedBy,
			@Param("attachmentId") int attachmentId,@Param("renameFile") String renameFile);
	
	
	@Query(value = "select a.id as Id,a.file_name as FileName,a.is_deleted as IsDeleted,a.status as Status,a.claim_id as ClaimUuid,u.uuid as UserUuid "
			+ "from rcm_claim_attachment a "
			+ "inner join rcm_attachment_type atype on atype.id=a.attachment_type_id "
			+ "inner join rcm_claims c on c.claim_uuid=a.claim_id "
			+ "inner join rcm_team t on t.id=a.created_by_team "
			+ "inner join rcm_user u on u.uuid=a.created_by "
			+ "where a.id IN (:attachmentsId) and a.claim_id=:claimuUuid and a.is_deleted is false and a.status is true and t.active is true", nativeQuery = true)
	List<RcmClaimAttachmentDto> findByAttachmentsById(@Param("attachmentsId")List<Integer>attachmentsId,@Param("claimuUuid") String claimuUuid);
   
	
	@Query(value = "select count(a.file_name) as FileName from rcm_claim_attachment a "
			+ "where a.file_name=:fileName and a.claim_id=:claimuUuid and a.is_deleted is false", nativeQuery = true)
	int fileCount(@Param("fileName") String fileName, @Param("claimuUuid") String claimuUuid);
	
	@Query(value = "select a.file_name as FileName,a.file_location as FileLocation,a.claim_id as ClaimUuid "
			+ "from rcm_claim_attachment a "
			+ "where a.id=:attachmentId and a.is_deleted is false "
			+ "and a.file_name is not null and a.attachment_type_id is not null", nativeQuery = true)
	RcmClaimAttachmentDto findAttachmentFile(@Param("attachmentId") int attachmentId);
	
	@Query(value = "SELECT a.rename_file as RenameFile "
			+ "FROM rcm_claim_attachment a "
			+ "inner join rcm_claims c on c.claim_uuid=a.claim_id "
			+ "WHERE a.claim_id =:claimUuid and a.file_name=:fileName and a.is_deleted is true and a.status is false "
			+ "ORDER BY a.rename_file DESC "
			+ "LIMIT 1", nativeQuery = true)
	RcmClaimAttachmentDto findRenameFile(@Param("fileName") String fileName,@Param("claimUuid") String claimUuid);
	
	
	@Query(value = "select attachment_count from rcm_claims "
			+ "where claim_uuid=:claimuUuid", nativeQuery = true)
	int attachmentCount(@Param("claimuUuid") String claimuUuid);
	
	@Modifying
	@Query(value = "update rcm_claims set attachment_count=:count where claim_uuid=:claimuUuid", nativeQuery = true)
	int updateAttachmentCountInRcmClaim(@Param("claimuUuid") String claimuUuid, @Param("count") int count);
	
	@Query(value = "select count(*) from rcm_claim_attachment attach "
			+ "inner join rcm_user u on u.uuid= attach.created_by "
			+ "inner join rcm_claims c on c.claim_uuid=attach.claim_id " 
			+ "where attach.created_by=:uuid "
			+ "and attach.is_deleted is false and attach.claim_id=:claimuUuid", nativeQuery = true)
	int attachmentCountOfUserUuid(@Param("claimuUuid") String claimuUuid, @Param("uuid") String userUuid);
	
}

package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaimAttachment;
import com.tricon.rcm.db.entity.RcmUser;
import com.tricon.rcm.dto.customquery.RcmClaimAttachmentDto;

public interface RcmClaimAttachmentRepo extends JpaRepository<RcmClaimAttachment, Integer>{

	
	@Query(value = "select a.id as Id,a.file_name as FileName,a.is_deleted as IsDeleted,a.attachment_type_id as AttachmentId "
			+ "from rcm_claim_attachment a "
			+ "inner join rcm_attachment_type atype on atype.id=a.attachment_type_id "
			+ "inner join rcm_claims c on c.claim_uuid=a.claim_id "
			+ "inner join rcm_team t on t.id=a.created_by_team "
			+ "where a.claim_id=:claimuUuid and a.is_deleted is false and a.status is true and t.active is true", nativeQuery = true)
	List<RcmClaimAttachmentDto> findByAttachmentId(@Param("claimuUuid") String claimuUuid);
	
	@Modifying
	@Query(value = "update rcm_claim_attachment set status = false,is_deleted = true,updated_by=:updatedBy,updated_date=now() where id =:attachmentId", nativeQuery = true)
	int updateAttachmentStatusById(@Param("updatedBy") RcmUser updatedBy,
			@Param("attachmentId") int attachmentId);
	
	
	@Query(value = "select a.id as Id,a.file_name as FileName,a.is_deleted as IsDeleted,a.status as Status "
			+ "from rcm_claim_attachment a "
			+ "inner join rcm_attachment_type atype on atype.id=a.attachment_type_id "
			+ "inner join rcm_claims c on c.claim_uuid=a.claim_id "
			+ "inner join rcm_team t on t.id=a.created_by_team "
			+ "where a.id IN (:attachmentsId) and a.claim_id=:claimuUuid and a.is_deleted is false and a.status is true and t.active is true", nativeQuery = true)
	List<RcmClaimAttachmentDto> findByAttachmentsById(@Param("attachmentsId")List<Integer>attachmentsId,@Param("claimuUuid") String claimuUuid);
   
	
	@Query(value = "select count(a.file_name) as FileName from rcm_claim_attachment a "
			+ "where a.file_name=:fileName and a.claim_id=:claimuUuid and a.is_deleted is false", nativeQuery = true)
	int fileCount(@Param("fileName") String fileName, @Param("claimuUuid") String claimuUuid);
	
}

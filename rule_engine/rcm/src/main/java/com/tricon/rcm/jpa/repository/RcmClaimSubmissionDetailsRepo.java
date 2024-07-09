package com.tricon.rcm.jpa.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaimSubmissionDetails;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.dto.customquery.RcmClaimSubmissionDto;

public interface RcmClaimSubmissionDetailsRepo extends JpaRepository<RcmClaimSubmissionDetails, String> {

	
	RcmClaimSubmissionDetails findByClaim(RcmClaims claims);
	
	@Query(nativeQuery = true, value = " select "
			+ " dt.claim_id claimUuid,dt.es_date esDate,es_time esTime,dt.channel channel,dt.attachment_send attachmentSend,"
			+ " dt.preauth preauth,us.first_name fName,us.last_name lName,us.uuid uuid,"
			+ " dt.refferal_letter refferalLetter,dt.claim_no claimNumber,dt.preauth_no preauthNo,dt.provider_ref_no "
			+ " providerRefNo,dt.clean_claim cleanClaim from rcm_claims_submission_details dt inner join rcm_user us on us.uuid=dt.submitted_by "
			+ " inner join rcm_user_company ruc on ruc.rcm_user_id=us.uuid "
			+ " where dt.claim_id=:claimId and ruc.company_id=:compid "
			)
	RcmClaimSubmissionDto findByClaimUuidAndCompanyId(@Param("claimId")String claimId,@Param("compid")String compid);

	@Transactional
	@Modifying
	@Query(nativeQuery = true,value="delete from rcm_claims_submission_details where claim_id = :claimId")
	   Integer deleteByClaimId(@Param("claimId")String claimId);
	 }

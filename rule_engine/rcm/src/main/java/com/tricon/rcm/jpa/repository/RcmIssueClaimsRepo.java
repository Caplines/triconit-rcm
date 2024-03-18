package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmIssueClaims;
import com.tricon.rcm.db.entity.RcmOffice;
import com.tricon.rcm.dto.customquery.IssueClaimDto;

public interface RcmIssueClaimsRepo extends JpaRepository<RcmIssueClaims, Integer>{

	RcmIssueClaims findByClaimIdAndOfficeAndSource(String claimId,RcmOffice office,String source);
	List<RcmIssueClaims> findByOfficeAndResolved(RcmOffice office, boolean resolved);
	List<RcmIssueClaims> findByIdInAndResolvedFalse(List<Integer> id);
	

	@Query(nativeQuery = true, value = " select  claim_id "
			+ "from  rcm_issue_claims cl "
			+ "inner join office off on off.uuid=cl.office_id "
			+ "inner join company cmp on cmp.uuid=off.company_id "
			+ "where cmp.uuid=:companyId and claim_id=:claimId and cl.is_archive is false and cl.resolved is false and off.uuid=:officeId")
	String fetchClaimByClaimIdAndCompany(@Param("claimId") String claimId,@Param("companyId") String companyId,@Param("officeId") String officeId) ;

	
	@Query(value = "select cl.id as Id,cl.is_archive as IsArchive,cl.claim_id claimId,cl.issue,cl.source,off.name officeName,off.uuid as OfficeUuid from rcm_issue_claims cl "
			+ "left join office off on off.uuid=cl.office_id "
			+ "where off.company_id=:companyId and cl.resolved is false and cl.is_archive is true", nativeQuery = true)
	List<IssueClaimDto> fetchAllUnarchiveClaimAssociatedClient(@Param("companyId") String companyId);
	
	List<RcmIssueClaims> findByClaimIdAndOfficeAndIsArchiveFalseAndResolvedFalse(String claimId,RcmOffice office);
}

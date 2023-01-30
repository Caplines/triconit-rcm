package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmIssueClaims;
import com.tricon.rcm.db.entity.RcmOffice;

public interface RcmIssueClaimsRepo extends JpaRepository<RcmIssueClaims, Integer>{

	RcmIssueClaims findByClaimIdAndOffice(String claimId,RcmOffice office);
	List<RcmIssueClaims> findByOfficeAndResolved(RcmOffice office, boolean resolved);
	

}

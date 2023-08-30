package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmIssueClaims;
import com.tricon.rcm.db.entity.RcmOffice;

public interface RcmIssueClaimsRepo extends JpaRepository<RcmIssueClaims, Integer>{

	RcmIssueClaims findByClaimIdAndOfficeAndSource(String claimId,RcmOffice office,String source);
	List<RcmIssueClaims> findByOfficeAndResolved(RcmOffice office, boolean resolved);
	List<RcmIssueClaims> findByIdIn(List<Integer> id);
	

}

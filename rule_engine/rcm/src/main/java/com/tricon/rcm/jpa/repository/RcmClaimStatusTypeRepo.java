package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmClaimStatusType;

public interface RcmClaimStatusTypeRepo extends JpaRepository<RcmClaimStatusType, Integer> {

	RcmClaimStatusType findByStatus(String status);

}
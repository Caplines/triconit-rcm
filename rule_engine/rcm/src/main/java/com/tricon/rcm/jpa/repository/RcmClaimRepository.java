package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmClaims;

public interface RcmClaimRepository extends JpaRepository<RcmClaims, String> {

}

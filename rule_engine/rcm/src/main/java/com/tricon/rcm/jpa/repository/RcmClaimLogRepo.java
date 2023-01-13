package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmClaimLog;

public interface RcmClaimLogRepo extends JpaRepository<RcmClaimLog, Integer> {

}

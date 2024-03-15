package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmRebilledClaimValidationRemark;

public interface RcmRecreationValidationRepo extends JpaRepository<RcmRebilledClaimValidationRemark,Integer> {

}

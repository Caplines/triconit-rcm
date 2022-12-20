package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmInsurance;

public interface RcmInsuranceRepo extends JpaRepository<RcmInsurance, Integer> {

	RcmInsurance findByInsuranceId(String insranceId);
}


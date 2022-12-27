package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmInsurance;
import com.tricon.rcm.db.entity.RcmOffice;

public interface RcmInsuranceRepo extends JpaRepository<RcmInsurance, Integer> {

	RcmInsurance findByInsuranceIdAndOffice(String insranceId,RcmOffice office);
	RcmInsurance findByNameAndOffice(String insranceId,RcmOffice office);
}


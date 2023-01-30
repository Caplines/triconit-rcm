package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmInsuranceType;

public interface RcmInsuranceTypeRepo extends JpaRepository<RcmInsuranceType, Integer>{

	RcmInsuranceType findByName(String name);
	RcmInsuranceType findById(int id);
	
}

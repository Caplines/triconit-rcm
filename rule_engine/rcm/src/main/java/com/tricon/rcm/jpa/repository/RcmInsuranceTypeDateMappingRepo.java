package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmInsuranceTypeDateMapping;

public interface RcmInsuranceTypeDateMappingRepo extends JpaRepository<RcmInsuranceTypeDateMapping, Integer> {

	RcmInsuranceTypeDateMapping findByTeamIdAndName(int id,String name);
}

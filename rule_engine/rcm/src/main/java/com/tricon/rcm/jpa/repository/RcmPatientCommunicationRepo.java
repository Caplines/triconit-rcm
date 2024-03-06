package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmPatientCommunicationSection;

public interface RcmPatientCommunicationRepo extends JpaRepository<RcmPatientCommunicationSection, Integer> {

	List<RcmPatientCommunicationSection> findByClaimClaimUuidOrderByCreatedDateDesc(String claimUuid);
}

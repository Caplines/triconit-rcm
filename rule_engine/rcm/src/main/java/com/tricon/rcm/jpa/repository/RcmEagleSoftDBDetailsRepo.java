package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmEagleSoftDBDetails;
import com.tricon.rcm.db.entity.RcmOffice;

public interface RcmEagleSoftDBDetailsRepo  extends JpaRepository<RcmEagleSoftDBDetails, String> {

	RcmEagleSoftDBDetails findByOffice(RcmOffice office); 
}


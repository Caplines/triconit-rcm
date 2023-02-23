package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaimNoteType;
import com.tricon.rcm.dto.customquery.RcmClaimNoteDto;

public interface RcmClaimNoteTypeRepo  extends JpaRepository<RcmClaimNoteType, Integer> {
	
	
	@Query(nativeQuery = true, value = " select "
			+ " name note,id noteId from rcm_claim_note_type  nty "
			+ " where nty.active=:active "
			+ "")
	List<RcmClaimNoteDto> fetchAllNotes(@Param("active") boolean active);
	

}
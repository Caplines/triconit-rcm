package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.rcm.db.entity.RcmClaimNoteType;
import com.tricon.rcm.db.entity.RcmClaimNotes;
import com.tricon.rcm.db.entity.RcmClaims;
import com.tricon.rcm.dto.customquery.RcmClaimNoteDto;

public interface RcmClaimNotesRepo extends JpaRepository<RcmClaimNotes, Integer> {

	RcmClaimNotes findByClaimAndNoteType(RcmClaims claim,RcmClaimNoteType type);
	
	@Query(nativeQuery = true, value = " select "
			+ " nt.note note,nt.note_id noteId from "
			+ " rcm_claim_notes nt , rcm_claim_note_type  nty "
			+ " where nt.claim_id=:claim_id and nty.id=nt.note_id and nt.team_id =:teamId order by nt.created_date desc "
			+ "")
	List<RcmClaimNoteDto> fetchClaimNotes(@Param("claim_id") String claimId,@Param("teamId") int teamId);

}

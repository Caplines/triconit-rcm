package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmTeam;

public interface RcmTeamRepo extends JpaRepository<RcmTeam, String>{

	RcmTeam findByNameId(String nameid);
}

package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmTeam;

public interface RcmTeamRepo extends JpaRepository<RcmTeam, Integer>{

	RcmTeam findByNameId(String nameid);
	RcmTeam findById(int id);
}

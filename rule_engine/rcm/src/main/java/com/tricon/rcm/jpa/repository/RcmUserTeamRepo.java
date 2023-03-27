package com.tricon.rcm.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmUserTeam;

public interface RcmUserTeamRepo  extends JpaRepository<RcmUserTeam, Integer>{

	List<RcmUserTeam> findByUserUuid(String userUuid);
}

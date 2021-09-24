package com.tricon.esdatareplication.dao.ruleenginedb;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.ChairsReplica;

public interface ChairsRepositoryRe extends JpaRepository<ChairsReplica, Integer> {

	//List<ChairsReplica> findByMovedToCloud(int i);

}

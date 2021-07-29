package com.tricon.esdatareplication.dao.ruleenginedb;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.PatientReplica;


public interface PatientRepositoryRe

		     extends JpaRepository<PatientReplica, Integer> {

}


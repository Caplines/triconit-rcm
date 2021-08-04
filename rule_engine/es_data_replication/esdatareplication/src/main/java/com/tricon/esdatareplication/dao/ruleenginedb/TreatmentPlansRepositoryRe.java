package com.tricon.esdatareplication.dao.ruleenginedb;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.TreatmentPlansReplica;

public interface TreatmentPlansRepositoryRe extends JpaRepository<TreatmentPlansReplica, Integer> {

}

package com.tricon.esdatareplication.dao.ruleenginedb;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.TreatmentPlanItemsReplica;

public interface TreatmentPlanItemsRepositoryRe extends JpaRepository<TreatmentPlanItemsReplica, Integer> {

}

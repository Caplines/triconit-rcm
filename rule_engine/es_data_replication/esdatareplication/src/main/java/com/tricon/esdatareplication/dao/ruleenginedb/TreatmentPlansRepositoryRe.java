package com.tricon.esdatareplication.dao.ruleenginedb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.TreatmentPlansReplica;

public interface TreatmentPlansRepositoryRe extends JpaRepository<TreatmentPlansReplica, Integer> {
	
public List<TreatmentPlansReplica> findByTreatmentPlanIdInAndOfficeId(Set<Integer> treatmentPlanIds,String officeuuid);
	
	public List<TreatmentPlansReplica> findByPatientIdIn(Set<String> patientId);

	public List<TreatmentPlansReplica> findByMovedToCloud(int i);

}

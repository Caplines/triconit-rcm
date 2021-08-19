package com.tricon.esdatareplication.dao.ruleenginedb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.TreatmentPlanItemsReplica;

public interface TreatmentPlanItemsRepositoryRe extends JpaRepository<TreatmentPlanItemsReplica, Integer> {
	
	
	public List<TreatmentPlanItemsReplica> findByTreatmentPlanIdInAndPatientIdInAndLineNumberInAndOfficeId(
			Set<Integer> tpids, Set<String> pids, Set<Integer> lines, String officeuuid);

	public List<TreatmentPlanItemsReplica> findByTreatmentPlanIdInAndPatientIdInAndOfficeId(
			Set<Integer> tpids, Set<String> pids, String officeuuid);

	public List<TreatmentPlanItemsReplica> findByPatientIdIn(Set<String> patientId);

	public List<TreatmentPlanItemsReplica> findByMovedToCloud(int i);

}

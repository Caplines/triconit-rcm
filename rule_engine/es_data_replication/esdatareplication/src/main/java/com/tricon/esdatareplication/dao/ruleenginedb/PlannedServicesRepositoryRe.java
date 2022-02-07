package com.tricon.esdatareplication.dao.ruleenginedb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.PlannedServicesReplica;

public interface PlannedServicesRepositoryRe extends JpaRepository<PlannedServicesReplica, Integer> {

	public List<PlannedServicesReplica> findByPatientIdInAndOfficeIdAndLineNumberIn(Set<String> patientids,
			String officeuuid, Set<Integer> linenumber);

	public List<PlannedServicesReplica> findByPatientIdInAndOfficeId(Set<String> patientId,String officeuuid);
	
	public PlannedServicesReplica findByPatientIdAndOfficeIdAndLineNumber(String patientids,
			String officeuuid, Integer linenumber);

	//public List<PlannedServicesReplica> findByMovedToCloud(int i);

}

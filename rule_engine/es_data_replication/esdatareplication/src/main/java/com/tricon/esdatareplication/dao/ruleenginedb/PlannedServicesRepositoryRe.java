package com.tricon.esdatareplication.dao.ruleenginedb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.esdatareplication.entity.ruleenginedb.PlannedServicesReplica;

public interface PlannedServicesRepositoryRe extends JpaRepository<PlannedServicesReplica, Integer> {

	public List<PlannedServicesReplica> findByPatientIdInAndOfficeIdAndLineNumberIn(Set<String> patientids,
			String officeuuid, Set<Integer> linenumber);

	public List<PlannedServicesReplica> findByPatientIdInAndOfficeId(Set<String> patientId,String officeuuid);
	
	public PlannedServicesReplica findByPatientIdAndOfficeIdAndLineNumber(String patientids,
			String officeuuid, Integer linenumber);
	
	
	@Modifying
	@Query("update PlannedServicesReplica set movedToCloud = :d")
	void deactivateAllData(@Param("d") int d);

	@Modifying
	@Query("delete PlannedServicesReplica where movedToCloud=:d")
	void deleteDeactivateData(@Param("d") int d);

}

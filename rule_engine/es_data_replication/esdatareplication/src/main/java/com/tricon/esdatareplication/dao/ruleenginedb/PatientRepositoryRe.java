package com.tricon.esdatareplication.dao.ruleenginedb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.PatientReplica;


public interface PatientRepositoryRe

		     extends JpaRepository<PatientReplica, Integer> {
	
	public List<PatientReplica> findByPatientIdInAndOfficeId(Set<String> patientId,String officeId);

}


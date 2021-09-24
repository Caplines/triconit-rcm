package com.tricon.esdatareplication.dao.repdb;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.tricon.esdatareplication.entity.repdb.Patient;

public interface PatientRepository

		     extends JpaRepository<Patient, Integer> {
	
	
	@Query("SELECT DISTINCT c.id FROM Patient c")
	public List<Integer> findLastNames();

	public List<Patient> findByPatientIdIn(Set<String> patientId);
	
	public List<Patient> findByMovedToCloud(int i,Pageable pageable);

	public Long countByMovedToCloud(int i);

}

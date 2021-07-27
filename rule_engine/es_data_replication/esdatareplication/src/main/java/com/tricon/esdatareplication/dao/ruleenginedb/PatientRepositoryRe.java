package com.tricon.esdatareplication.dao.ruleenginedb;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.Patient;


public interface PatientRepositoryRe

		     extends JpaRepository<Patient, Integer> {

}


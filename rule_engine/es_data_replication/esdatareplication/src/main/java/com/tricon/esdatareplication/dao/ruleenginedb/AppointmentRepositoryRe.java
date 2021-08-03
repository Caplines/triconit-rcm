package com.tricon.esdatareplication.dao.ruleenginedb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.AppointmentReplica;

public interface AppointmentRepositoryRe

		extends JpaRepository<AppointmentReplica, Integer> {

	public List<AppointmentReplica> findByPatientIdIn(Set<String> patientId);

	public List<AppointmentReplica> findByMovedToCloud(int i);

}

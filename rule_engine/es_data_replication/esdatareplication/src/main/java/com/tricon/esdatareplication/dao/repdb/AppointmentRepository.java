package com.tricon.esdatareplication.dao.repdb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tricon.esdatareplication.entity.repdb.Appointment;

public interface AppointmentRepository

		extends JpaRepository<Appointment, Integer> {

	public List<Appointment> findByAppointmentIdIn(Set<String> apointmentId);

	public List<Appointment> findByMovedToCloud(int i);

}

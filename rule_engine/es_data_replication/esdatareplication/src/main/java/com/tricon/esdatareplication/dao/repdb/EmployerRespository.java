package com.tricon.esdatareplication.dao.repdb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.repdb.Employer;

public interface EmployerRespository extends JpaRepository<Employer, Integer> {

	public List<Employer> findByEmployerIdIn(Set<Integer> employerId);

	public List<Employer> findByMovedToCloud(int i);

}

package com.tricon.esdatareplication.dao.ruleenginedb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.EmployerReplica;

public interface EmployerRespositoryRe extends JpaRepository<EmployerReplica, Integer> {
	
	public List<EmployerReplica> findByEmployerIdInAndOfficeId(Set<Integer> employerId, String officeuuid);

	public List<EmployerReplica> findByMovedToCloud(int i);

	

}

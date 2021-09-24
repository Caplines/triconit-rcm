package com.tricon.esdatareplication.dao.ruleenginedb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsDetailReplica;


public interface TransactionsDetailRepositoryRe extends JpaRepository<TransactionsDetailReplica, Integer> {
	
	
	public List<TransactionsDetailReplica> findByDetailIdInAndOfficeId(Set<Integer> detailId, String officeuuid);

	public List<TransactionsDetailReplica> findByPatientIdInAndOfficeId(Set<String> patientId,String officeuuid);

	//public List<TransactionsDetailReplica> findByMovedToCloud(int i);
	//public List<TransactionsDetailReplica> findByTranNumIn(Set<Integer> tranNum);

}

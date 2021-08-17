package com.tricon.esdatareplication.dao.ruleenginedb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsReplica;

public interface TransactionsRepositoryRe extends JpaRepository<TransactionsReplica, Integer> {
	
	
	public List<TransactionsReplica> findByTranNumInAndOfficeId(Set<Integer> tranNum,String officeId);

}

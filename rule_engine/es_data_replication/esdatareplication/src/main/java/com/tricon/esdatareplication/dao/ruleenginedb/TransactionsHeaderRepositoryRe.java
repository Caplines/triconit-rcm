package com.tricon.esdatareplication.dao.ruleenginedb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsHeaderReplica;

public interface TransactionsHeaderRepositoryRe extends JpaRepository<TransactionsHeaderReplica, Integer> {
	
	
	public List<TransactionsHeaderReplica> findByTranNumInAndOfficeId(Set<Integer> tranNum,String officeId);

}

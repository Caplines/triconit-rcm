package com.tricon.esdatareplication.dao.ruleenginedb;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsDetailReplica;


public interface TransactionsDetailRepositoryRe extends JpaRepository<TransactionsDetailReplica, Integer> {

}

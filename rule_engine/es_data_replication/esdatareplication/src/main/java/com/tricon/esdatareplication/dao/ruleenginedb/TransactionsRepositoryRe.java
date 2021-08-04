package com.tricon.esdatareplication.dao.ruleenginedb;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsReplica;

public interface TransactionsRepositoryRe extends JpaRepository<TransactionsReplica, Integer> {

}

package com.tricon.esdatareplication.dao.ruleenginedb;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.ProviderReplica;

public interface ProviderRepositoryRe extends JpaRepository<ProviderReplica, Integer> {

}

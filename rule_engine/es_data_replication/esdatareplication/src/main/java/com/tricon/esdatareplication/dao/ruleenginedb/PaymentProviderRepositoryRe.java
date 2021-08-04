package com.tricon.esdatareplication.dao.ruleenginedb;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.PaymentProviderReplica;

public interface PaymentProviderRepositoryRe extends JpaRepository<PaymentProviderReplica, Integer> {

}

package com.tricon.esdatareplication.dao.ruleenginedb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.PaymentProviderReplica;

public interface PaymentProviderRepositoryRe extends JpaRepository<PaymentProviderReplica, Integer> {

	public List<PaymentProviderReplica> findByTranNumInAndProviderIdInAndOfficeId(Set<Integer> tranNum,Set<String> pateintId, String officeuuid);

	public List<PaymentProviderReplica> findByMovedToCloud(int i);

}

package com.tricon.esdatareplication.dao.repdb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.repdb.PaymentProvider;

public interface PaymentProviderRepository extends JpaRepository<PaymentProvider, Integer> {

	public List<PaymentProvider> findByTranNumInAndProviderIdIn(Set<Integer> tranNum,Set<String>pats);

	public List<PaymentProvider> findByMovedToCloud(int i);

}

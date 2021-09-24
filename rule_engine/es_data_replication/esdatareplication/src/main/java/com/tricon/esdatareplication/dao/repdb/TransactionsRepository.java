
package com.tricon.esdatareplication.dao.repdb;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.repdb.Transactions;

public interface TransactionsRepository extends JpaRepository<Transactions, Integer> {

	public List<Transactions> findByTranNumIn(Set<Integer> tranNum);

	public List<Transactions> findByMovedToCloud(int i,Pageable prepairPage);

}

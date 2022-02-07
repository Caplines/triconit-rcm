package com.tricon.esdatareplication.dao.repdb;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.repdb.TransactionsHeader;

public interface TransactionsHeaderRepository extends JpaRepository<TransactionsHeader, Integer> {

	public List<TransactionsHeader> findByTranNumIn(Set<Integer> tranNum);

	public List<TransactionsHeader> findByMovedToCloud(int i,Pageable prepairPage);

}

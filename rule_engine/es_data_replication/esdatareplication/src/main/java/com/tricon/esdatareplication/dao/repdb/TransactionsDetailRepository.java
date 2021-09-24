package com.tricon.esdatareplication.dao.repdb;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.repdb.TransactionsDetail;

public interface TransactionsDetailRepository extends JpaRepository<TransactionsDetail, Integer> {

	public List<TransactionsDetail> findByDetailIdIn(Set<Integer> detailId);

	public List<TransactionsDetail> findByMovedToCloud(int i,Pageable prepairPage);

}

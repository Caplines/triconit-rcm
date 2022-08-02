package com.tricon.esdatareplication.dao.repdb;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.repdb.PayType;


public interface PayTypeRepository extends JpaRepository<PayType, Integer> {

	
	List<PayType> findByMovedToCloud(int i);
	
	PayType findByPayTypeId(int i);
	
	List<PayType> findByPayTypeIdIn(Set<Integer> ids);
	
	public List<PayType> findByMovedToCloud(int i,Pageable prepairPage);
	
	
}

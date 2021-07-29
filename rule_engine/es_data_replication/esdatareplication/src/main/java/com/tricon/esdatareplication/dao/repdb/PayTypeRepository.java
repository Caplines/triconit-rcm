package com.tricon.esdatareplication.dao.repdb;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.repdb.PayType;

public interface PayTypeRepository extends JpaRepository<PayType, Integer> {

	
	List<PayType> findByMovedToCloud(int i);
	
	
}

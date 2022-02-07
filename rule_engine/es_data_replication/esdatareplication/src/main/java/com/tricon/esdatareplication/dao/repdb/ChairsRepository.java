package com.tricon.esdatareplication.dao.repdb;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.repdb.Chairs;

public interface ChairsRepository extends JpaRepository<Chairs, Integer> {

	List<Chairs> findByMovedToCloud(int i);

}

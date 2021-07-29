package com.tricon.esdatareplication.dao.repdb;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.repdb.Office;

public interface OfficeRepository extends JpaRepository<Office, Integer> {

}
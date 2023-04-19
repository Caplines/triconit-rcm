package com.tricon.rcm.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.rcm.db.entity.RcmExceptionLogs;

public interface RcmExceptionLogsRepo extends JpaRepository<RcmExceptionLogs, Integer>{

}

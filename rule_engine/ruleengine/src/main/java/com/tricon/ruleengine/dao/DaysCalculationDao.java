package com.tricon.ruleengine.dao;

import com.tricon.ruleengine.model.db.ReplicationDays;

public interface DaysCalculationDao 
{
            public ReplicationDays findByQueryName(String queryName);
}

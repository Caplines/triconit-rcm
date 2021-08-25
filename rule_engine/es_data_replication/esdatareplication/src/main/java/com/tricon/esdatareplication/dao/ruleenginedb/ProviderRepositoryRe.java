package com.tricon.esdatareplication.dao.ruleenginedb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.ruleenginedb.ProviderReplica;

public interface ProviderRepositoryRe extends JpaRepository<ProviderReplica, Integer> {
	
	
	public List<ProviderReplica> findByProviderIdInAndOfficeId(Set<String> providerId, String officeuuid);

	public List<ProviderReplica> findByMovedToCloud(int i);

}

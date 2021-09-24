package com.tricon.esdatareplication.dao.repdb;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.repdb.Provider;

public interface ProviderRepository extends JpaRepository<Provider, Integer> {

	public List<Provider> findByProviderIdIn(Set<String> providerId);

	public List<Provider> findByMovedToCloud(int i,Pageable prepairPage);

}

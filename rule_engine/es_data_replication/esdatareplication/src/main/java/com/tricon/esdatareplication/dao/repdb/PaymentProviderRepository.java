package com.tricon.esdatareplication.dao.repdb;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.PaymentProvider;
import com.tricon.esdatareplication.entity.ruleenginedb.PaymentProviderReplica;

public interface PaymentProviderRepository extends JpaRepository<PaymentProvider, Integer> {

	public List<PaymentProvider> findByTranNumInAndProviderIdInAndProdProviderIdIn(Set<Integer> tranNum,Set<String>prids,Set<String>prodids);

	public List<PaymentProvider> findByMovedToCloud(int i,Pageable pageable);

	@Query("SELECT e FROM PaymentProvider e where e.tranNum= :tranNum and providerId= :providerId and prodProviderId= :prodProviderId ")
	public List<PaymentProvider> findByTranNumAndProdIdAndProdProviderId(@Param("tranNum") Integer tranNum,
			@Param("providerId") String providerId, @Param("prodProviderId") String prodProviderId);

	@Query("SELECT e FROM PaymentProvider e where e.tranNum= :tranNum and providerId is null and prodProviderId is null " )
	public List<PaymentProvider> findByTranNum(@Param("tranNum") Integer tranNum);
	
	@Query("SELECT e FROM PaymentProvider e where e.tranNum= :tranNum and providerId= :providerId and prodProviderId is null" )
	public List<PaymentProvider> findByTranNumAndProdId(@Param("tranNum") Integer tranNum,@Param("providerId") String providerId);

	@Query("SELECT e FROM PaymentProvider e where e.tranNum= :tranNum and prodProviderId= :prodProviderId and providerId is null")
	public List<PaymentProvider> findByTranNumAndProdProviderId(@Param("tranNum") Integer tranNum,@Param("prodProviderId") String prodProviderId);

}

package com.tricon.esdatareplication.dao.ruleenginedb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.esdatareplication.entity.repdb.PaymentProvider;
import com.tricon.esdatareplication.entity.ruleenginedb.PaymentProviderReplica;

public interface PaymentProviderRepositoryRe extends JpaRepository<PaymentProviderReplica, Integer> {

	public List<PaymentProviderReplica> findByTranNumInAndProviderIdInAndProdProviderIdInAndOfficeId(
			Set<Integer> tranNum, Set<String> prId, Set<String> prodId, String officeuuid);

	public List<PaymentProviderReplica> findByMovedToCloud(int i);

	@Query("SELECT e FROM PaymentProviderReplica e where e.tranNum= :tranNum and providerId is null and prodProviderId is null and officeId= :officeId")
	public List<PaymentProviderReplica> findByTranNum(@Param("tranNum") Integer tranNum,
			@Param("officeId") String officeId);

	@Query("SELECT e FROM PaymentProviderReplica e where e.tranNum= :tranNum and providerId= :providerId and prodProviderId= :prodProviderId and officeId= :officeId")
	public List<PaymentProviderReplica> findByTranNumAndProdIdAndProdProviderId(@Param("tranNum") Integer tranNum,
			@Param("providerId") String providerId, @Param("prodProviderId") String prodProviderId,
			@Param("officeId") String officeId);

	@Query("SELECT e FROM PaymentProviderReplica e where e.tranNum= :tranNum and providerId= :providerId and prodProviderId is null and officeId= :officeId")
	public List<PaymentProviderReplica> findByTranNumAndProdId(@Param("tranNum") Integer tranNum,
			@Param("providerId") String providerId, @Param("officeId") String officeId);

	@Query("SELECT e FROM PaymentProviderReplica e where e.tranNum= :tranNum and prodProviderId= :prodProviderId and providerId is null and officeId= :officeId")
	public List<PaymentProviderReplica> findByTranNumAndProdProviderId(@Param("tranNum") Integer tranNum,
			@Param("prodProviderId") String prodProviderId, @Param("officeId") String officeId);

}

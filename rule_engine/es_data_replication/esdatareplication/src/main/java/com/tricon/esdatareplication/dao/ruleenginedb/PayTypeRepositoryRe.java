package com.tricon.esdatareplication.dao.ruleenginedb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.esdatareplication.entity.ruleenginedb.PayTypeReplica;

public interface PayTypeRepositoryRe extends JpaRepository<PayTypeReplica, Integer> {
	
	
	PayTypeReplica findByPayTypeIdAndOfficeId(int paytypeId,String officeUUid);
	
	List<PayTypeReplica> findByPayTypeIdInAndOfficeId(Set<Integer> paytypeId,String officeUUid);


	@Modifying
	@Query("update PayTypeReplica set movedToCloud = :d where movedToCloud= :d1")
	void activateDeactiveData(@Param("d") int d,@Param("d1") int d1);

}

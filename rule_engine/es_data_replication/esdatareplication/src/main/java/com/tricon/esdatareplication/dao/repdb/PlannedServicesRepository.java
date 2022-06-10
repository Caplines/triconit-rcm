package com.tricon.esdatareplication.dao.repdb;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.esdatareplication.entity.repdb.PlannedServices;

public interface PlannedServicesRepository extends JpaRepository<PlannedServices, Integer> {

	public List<PlannedServices> findByApptGroupIn(Set<Integer> apptGroup);
	
	public List<PlannedServices> findByPatientIdInAndLineNumberInOrderByDatePlanned(Set<String> patientids,
			 Set<Integer> linenumber);
	
	public List<PlannedServices> findByPatientIdIn(Set<String> patientids);

	
	public List<PlannedServices> findByMovedToCloud(int i,Pageable prepairPage);
	
	public List<PlannedServices> findByLineNumberIn(Set<Integer> lineNumber);
	
	public PlannedServices findByPatientIdAndLineNumber(String patientid,Integer linenumber);
	
	
	@Modifying
	@Query(nativeQuery=true,value="TRUNCATE table PlannedServices")
	void truncatePlannedServices();
	
	@Modifying
	@Query(nativeQuery=true,value="update PlannedServices  set movedToCloud = :d where ")
	void deactivateAllData(@Param("d") int d);


}

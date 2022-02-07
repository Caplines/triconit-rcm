package com.tricon.esdatareplication.dao.repdb;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.repdb.TreatmentPlans;

public interface TreatmentPlansRepository extends JpaRepository<TreatmentPlans, Integer> {

	public List<TreatmentPlans> findByTreatmentPlanIdIn(Set<Integer> treatmentPlanId);

	public List<TreatmentPlans> findByMovedToCloud(int i,Pageable prepairPage);
	
	public TreatmentPlans findByTreatmentPlanId(Integer treatmentPlanId);

}

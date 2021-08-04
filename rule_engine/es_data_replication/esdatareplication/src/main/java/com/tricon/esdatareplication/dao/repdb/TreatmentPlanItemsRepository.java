package com.tricon.esdatareplication.dao.repdb;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tricon.esdatareplication.entity.repdb.TreatmentPlanItems;

public interface TreatmentPlanItemsRepository extends JpaRepository<TreatmentPlanItems, Integer> {

	public List<TreatmentPlanItems> findByTreatmentPlanIdIn(Set<Integer> treatmentPlanId);

	public List<TreatmentPlanItems> findByMovedToCloud(int i);


}

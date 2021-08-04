package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.esdatareplication.dao.repdb.TreatmentPlansRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.TreatmentPlansRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.TreatmentPlans;
import com.tricon.esdatareplication.entity.ruleenginedb.TreatmentPlansReplica;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class TreatmentPlansTableService {

	@Autowired
	private TreatmentPlansRepository treatmentPlansRepository;

	@Autowired
	private TreatmentPlansRepositoryRe treatmentPlansRepositoryRe;

	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		List<TreatmentPlans> p = treatmentPlansRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
		List<TreatmentPlansReplica> repList = new ArrayList<>();
		p.forEach(x -> {
			x.setOfficeId(office.getUuid());
			TreatmentPlansReplica rep = new TreatmentPlansReplica();
			BeanUtils.copyProperties(x, rep);
			rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			repList.add(rep);
		});
		// new repository for cloud.. and save data...
		treatmentPlansRepositoryRe.saveAll(repList);
		p.forEach(x -> {
			x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
		});
		treatmentPlansRepository.saveAll(p);
		es.setRecordsInsertedLastIteration(p.size());
		return es;

	}
	
	public void saveDataToLocalDB(List<?> data, boolean checkExisting) {
		
		if (!checkExisting)
			treatmentPlansRepository.saveAll((List<TreatmentPlans>) data);
		else {
			//
			Set<Integer> apptIdInES = new HashSet<>();
			Set<Integer> apptIdInDB = new HashSet<>();
			((List<TreatmentPlans>) data).stream().map(TreatmentPlans::getTreatmentPlanId).forEach(apptIdInES::add);
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<TreatmentPlans> inDB = treatmentPlansRepository.findByTreatmentPlanIdIn(apptIdInES);
			inDB.stream().map(TreatmentPlans::getTreatmentPlanId).forEach(apptIdInDB::add);

			apptIdInES.removeAll(apptIdInDB);// Patientid that are not in Local DB
			if (apptIdInES.size() > 0) {
				apptIdInES.forEach(id -> {
					treatmentPlansRepository.save(((List<TreatmentPlans>) data).stream().filter(p -> id.equals(p.getTreatmentPlanId()))
							.findAny().orElse(null));
				});
			}
			apptIdInDB.removeAll(apptIdInES);// Patient id that are there in Local DB we need to update.
			if (apptIdInDB.size() > 0) {
				apptIdInDB.forEach(id -> {
					TreatmentPlans p = ((List<TreatmentPlans>) data).stream().filter(dp -> id.equals(dp.getTreatmentPlanId())).findAny()
							.orElse(null);

					TreatmentPlans old = inDB.stream().filter(ind -> id.equals(ind.getTreatmentPlanId())).findAny().orElse(null);
					p.setId(old.getId());
					p.setMovedToCloud(0);
					p.setCreatedDate(old.getCreatedDate());
					treatmentPlansRepository.save(p);
				});
			}
		}

		
	}

}

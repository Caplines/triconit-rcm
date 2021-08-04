package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.esdatareplication.dao.repdb.TreatmentPlanItemsRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.TreatmentPlanItemsRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.TreatmentPlanItems;
import com.tricon.esdatareplication.entity.ruleenginedb.TreatmentPlanItemsReplica;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class TreatmentPlanItemsTableService {

	@Autowired
	private TreatmentPlanItemsRepository  treatmentPlanItemsRepository;

	@Autowired
	private TreatmentPlanItemsRepositoryRe treatmentPlanItemsRepositoryRe;

	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		List<TreatmentPlanItems> p = treatmentPlanItemsRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
		List<TreatmentPlanItemsReplica> repList = new ArrayList<>();
		p.forEach(x -> {
			x.setOfficeId(office.getUuid());
			TreatmentPlanItemsReplica rep = new TreatmentPlanItemsReplica();
			BeanUtils.copyProperties(x, rep);
			rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			repList.add(rep);
		});
		// new repository for cloud.. and save data...
		treatmentPlanItemsRepositoryRe.saveAll(repList);
		p.forEach(x -> {
			x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
		});
		treatmentPlanItemsRepository.saveAll(p);
		es.setRecordsInsertedLastIteration(p.size());
		return es;

	}
	
	public void saveDataToLocalDB(List<?> data, boolean checkExisting) {
		
		if (!checkExisting)
			treatmentPlanItemsRepository.saveAll((List<TreatmentPlanItems>) data);
		else {
			//
			Set<Integer> apptIdInES = new HashSet<>();
			Set<Integer> apptIdInDB = new HashSet<>();
			((List<TreatmentPlanItems>) data).stream().map(TreatmentPlanItems::getTreatmentPlanId).forEach(apptIdInES::add);
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<TreatmentPlanItems> inDB = treatmentPlanItemsRepository.findByTreatmentPlanIdIn(apptIdInES);
			inDB.stream().map(TreatmentPlanItems::getTreatmentPlanId).forEach(apptIdInDB::add);

			apptIdInES.removeAll(apptIdInDB);// Patientid that are not in Local DB
			if (apptIdInES.size() > 0) {
				apptIdInES.forEach(id -> {
					treatmentPlanItemsRepository.save(((List<TreatmentPlanItems>) data).stream().filter(p -> id.equals(p.getTreatmentPlanId()))
							.findAny().orElse(null));
				});
			}
			apptIdInDB.removeAll(apptIdInES);// Patient id that are there in Local DB we need to update.
			if (apptIdInDB.size() > 0) {
				apptIdInDB.forEach(id -> {
					TreatmentPlanItems p = ((List<TreatmentPlanItems>) data).stream().filter(dp -> id.equals(dp.getTreatmentPlanId())).findAny()
							.orElse(null);

					TreatmentPlanItems old = inDB.stream().filter(ind -> id.equals(ind.getTreatmentPlanId())).findAny().orElse(null);
					p.setId(old.getId());
					p.setMovedToCloud(0);
					p.setCreatedDate(old.getCreatedDate());
					treatmentPlanItemsRepository.save(p);
				});
			}
		}

		
	}

}

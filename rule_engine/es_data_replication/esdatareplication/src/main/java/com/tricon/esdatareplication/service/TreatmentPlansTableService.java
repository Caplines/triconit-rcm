package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tricon.esdatareplication.dao.repdb.TreatmentPlansRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.TreatmentPlansRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.TreatmentPlanItems;
import com.tricon.esdatareplication.entity.repdb.TreatmentPlans;
import com.tricon.esdatareplication.entity.ruleenginedb.TreatmentPlanItemsReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.TreatmentPlansReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class TreatmentPlansTableService extends CommonTableService{

	@Autowired
	private TreatmentPlansRepository treatmentPlansRepository;

	@Autowired
	private TreatmentPlansRepositoryRe treatmentPlansRepositoryRe;

	@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		try {
		List<TreatmentPlans> p = treatmentPlansRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
		List<TreatmentPlansReplica> repList = new ArrayList<>();
		StringBuilder bu = new StringBuilder();
		p.forEach(x -> {
			x.setOfficeId(office.getUuid());
			TreatmentPlansReplica rep = new TreatmentPlansReplica();
			BeanUtils.copyProperties(x, rep);
			bu.append(rep.getTreatmentPlanId() + ",");
			rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			repList.add(rep);
		});
		// new repository for cloud.. and save data...
		treatmentPlansRepositoryRe.saveAll(repList);
		appendLoggerToWriter(TreatmentPlansReplica.class, bw,
				Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
		appendLoggerToWriter(TreatmentPlansReplica.class, bw, bu.toString(), true);
		p.forEach(x -> {
			x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
		});
		treatmentPlansRepository.saveAll(p);
		es.setRecordsInsertedLastIteration(p.size());
		} catch (Exception ex) {
			es.setRecordsInsertedLastIteration(0);
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			es.setLastIssueDetail(errors.toString());
			appendLoggerToWriter(TreatmentPlans.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(TreatmentPlans.class, bw, ex);
		}
		return es;

	}
	
	public void saveDataToLocalDB(BufferedWriter bw,List<?> data, boolean checkExisting) {
		try {
		if (!checkExisting) {
			treatmentPlansRepository.saveAll((List<TreatmentPlans>) data);
			logFirstTimeDataEntryToTable(TreatmentPlans.class, bw, data.size());
		}
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

			logAfterFirstTimeDataEntryToTable(TreatmentPlans.class, bw, apptIdInES.size(), apptIdInDB.size(),
					String.join(",", apptIdInES.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
					String.join(",", apptIdInDB.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
		}
	} catch (Exception ex) {
		appenErrorToWriter(TreatmentPlanItems.class, bw, ex);
	}
}
}

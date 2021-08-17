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

import com.tricon.esdatareplication.dao.repdb.TreatmentPlanItemsRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.TreatmentPlanItemsRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.PlannedServices;
import com.tricon.esdatareplication.entity.repdb.Provider;
import com.tricon.esdatareplication.entity.repdb.TreatmentPlanItems;
import com.tricon.esdatareplication.entity.ruleenginedb.PlannedServicesReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.TreatmentPlanItemsReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class TreatmentPlanItemsTableService extends CommonTableService {

	@Autowired
	private TreatmentPlanItemsRepository treatmentPlanItemsRepository;

	@Autowired
	private TreatmentPlanItemsRepositoryRe treatmentPlanItemsRepositoryRe;

	@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		try {
			List<TreatmentPlanItems> p = treatmentPlanItemsRepository
					.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
			List<TreatmentPlanItemsReplica> repList = new ArrayList<>();
			StringBuilder bu = new StringBuilder();
			p.forEach(x -> {
				x.setOfficeId(office.getUuid());
				TreatmentPlanItemsReplica rep = new TreatmentPlanItemsReplica();
				BeanUtils.copyProperties(x, rep);
				bu.append(rep.getLineNumber() + ",");
				rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				repList.add(rep);
			});
			// new repository for cloud.. and save data...
			treatmentPlanItemsRepositoryRe.saveAll(repList);
			appendLoggerToWriter(TreatmentPlanItemsReplica.class, bw,
					Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
			appendLoggerToWriter(TreatmentPlanItemsReplica.class, bw, bu.toString(), true);
			p.forEach(x -> {
				x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			});
			treatmentPlanItemsRepository.saveAll(p);
			es.setRecordsInsertedLastIteration(p.size());
		} catch (Exception ex) {
			es.setRecordsInsertedLastIteration(0);
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			es.setLastIssueDetail(errors.toString());
			appendLoggerToWriter(TreatmentPlanItems.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(TreatmentPlanItems.class, bw, ex);
		}
		return es;

	}

	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {
		try {
			if (!checkExisting) {
				treatmentPlanItemsRepository.saveAll((List<TreatmentPlanItems>) data);
				logFirstTimeDataEntryToTable(TreatmentPlanItems.class, bw, data.size());
			} else {
				//
				Set<Integer> apptIdInES = new HashSet<>();
				Set<Integer> apptIdInDB = new HashSet<>();
				((List<TreatmentPlanItems>) data).stream().map(TreatmentPlanItems::getTreatmentPlanId)
						.forEach(apptIdInES::add);
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<TreatmentPlanItems> inDB = treatmentPlanItemsRepository.findByTreatmentPlanIdIn(apptIdInES);
				inDB.stream().map(TreatmentPlanItems::getTreatmentPlanId).forEach(apptIdInDB::add);

				apptIdInES.removeAll(apptIdInDB);// Patientid that are not in Local DB
				if (apptIdInES.size() > 0) {
					apptIdInES.forEach(id -> {
						treatmentPlanItemsRepository.save(((List<TreatmentPlanItems>) data).stream()
								.filter(p -> id.equals(p.getTreatmentPlanId())).findAny().orElse(null));
					});
				}
				apptIdInDB.removeAll(apptIdInES);// Patient id that are there in Local DB we need to update.
				if (apptIdInDB.size() > 0) {
					apptIdInDB.forEach(id -> {
						TreatmentPlanItems p = ((List<TreatmentPlanItems>) data).stream()
								.filter(dp -> id.equals(dp.getTreatmentPlanId())).findAny().orElse(null);

						TreatmentPlanItems old = inDB.stream().filter(ind -> id.equals(ind.getTreatmentPlanId()))
								.findAny().orElse(null);
						p.setId(old.getId());
						p.setMovedToCloud(0);
						p.setCreatedDate(old.getCreatedDate());
						treatmentPlanItemsRepository.save(p);
					});

				}

				logAfterFirstTimeDataEntryToTable(TreatmentPlanItems.class, bw, apptIdInES.size(), apptIdInDB.size(),
						String.join(",", apptIdInES.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
						String.join(",", apptIdInDB.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
			}
		} catch (Exception ex) {
			appenErrorToWriter(TreatmentPlanItems.class, bw, ex);
		}
	}
}

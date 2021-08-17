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

import com.tricon.esdatareplication.dao.repdb.PlannedServicesRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.PlannedServicesRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.PlannedServices;
import com.tricon.esdatareplication.entity.repdb.Provider;
import com.tricon.esdatareplication.entity.ruleenginedb.PlannedServicesReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.ProviderReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class PlannedServicesTableService extends CommonTableService {

	@Autowired
	private PlannedServicesRepository plannedServicesRepository;

	@Autowired
	private PlannedServicesRepositoryRe plannedServicesRepositoryRe;

	@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		try {
			List<PlannedServices> p = plannedServicesRepository
					.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
			List<PlannedServicesReplica> repList = new ArrayList<>();
			StringBuilder bu = new StringBuilder();
			p.forEach(x -> {
				x.setOfficeId(office.getUuid());
				PlannedServicesReplica rep = new PlannedServicesReplica();
				BeanUtils.copyProperties(x, rep);
				bu.append(rep.getLineNumber() + ",");
				rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				repList.add(rep);
			});
			// new repository for cloud.. and save data...
			plannedServicesRepositoryRe.saveAll(repList);
			appendLoggerToWriter(PlannedServicesReplica.class, bw,
					Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
			appendLoggerToWriter(PlannedServicesReplica.class, bw, bu.toString(), true);
			p.forEach(x -> {
				x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			});
			plannedServicesRepository.saveAll(p);
			es.setRecordsInsertedLastIteration(p.size());
		} catch (Exception ex) {
			es.setRecordsInsertedLastIteration(0);
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			es.setLastIssueDetail(errors.toString());
			appendLoggerToWriter(PlannedServices.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(PlannedServices.class, bw, ex);
		}
		return es;

	}

	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {
        try {
		if (!checkExisting) {
			plannedServicesRepository.saveAll((List<PlannedServices>) data);
			logFirstTimeDataEntryToTable(PlannedServices.class, bw, data.size());
		}
		else {
			//
			Set<Integer> apptIdInES = new HashSet<>();
			Set<Integer> apptIdInDB = new HashSet<>();
			((List<PlannedServices>) data).stream().map(PlannedServices::getLineNumber).forEach(apptIdInES::add);
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<PlannedServices> inDB = plannedServicesRepository.findByLineNumberIn(apptIdInES);
			inDB.stream().map(PlannedServices::getLineNumber).forEach(apptIdInDB::add);

			apptIdInES.removeAll(apptIdInDB);// that are not in Local DB
			if (apptIdInES.size() > 0) {
				apptIdInES.forEach(id -> {
					plannedServicesRepository.save(((List<PlannedServices>) data).stream()
							.filter(p -> id.equals(p.getLineNumber())).findAny().orElse(null));
				});
			}
			apptIdInDB.removeAll(apptIdInES);// Patient id that are there in Local DB we need to update.
			if (apptIdInDB.size() > 0) {
				apptIdInDB.forEach(id -> {
					PlannedServices p = ((List<PlannedServices>) data).stream()
							.filter(dp -> id.equals(dp.getLineNumber())).findAny().orElse(null);

					PlannedServices old = inDB.stream().filter(ind -> id.equals(ind.getLineNumber())).findAny()
							.orElse(null);
					p.setId(old.getId());
					p.setMovedToCloud(0);
					p.setCreatedDate(old.getCreatedDate());
					plannedServicesRepository.save(p);
				});

			}

			logAfterFirstTimeDataEntryToTable(PlannedServices.class, bw, apptIdInES.size(), apptIdInDB.size(),
					String.join(",", apptIdInES.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
					String.join(",", apptIdInDB.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
		}
	} catch (Exception ex) {
		appenErrorToWriter(PlannedServices.class, bw, ex);
	}
}
}

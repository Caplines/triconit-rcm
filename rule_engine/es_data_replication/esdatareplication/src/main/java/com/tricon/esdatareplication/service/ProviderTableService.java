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

import com.tricon.esdatareplication.dao.repdb.ProviderRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.ProviderRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Provider;
import com.tricon.esdatareplication.entity.ruleenginedb.ProviderReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class ProviderTableService extends CommonTableService {
	@Autowired
	private ProviderRepository providerRepository;

	@Autowired
	private ProviderRepositoryRe providerRepositoryRe;

	@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		try {
			List<Provider> p = providerRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
			List<ProviderReplica> repList = new ArrayList<>();
			StringBuilder bu = new StringBuilder();
			p.forEach(x -> {
				x.setOfficeId(office.getUuid());
				ProviderReplica rep = new ProviderReplica();
				BeanUtils.copyProperties(x, rep);
				bu.append(rep.getProviderId() + ",");
				rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				repList.add(rep);
			});
			// new repository for cloud.. and save data...
			providerRepositoryRe.saveAll(repList);
			appendLoggerToWriter(ProviderReplica.class, bw,
					Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
			appendLoggerToWriter(ProviderReplica.class, bw, bu.toString(), true);

			p.forEach(x -> {
				x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			});
			providerRepository.saveAll(p);
			es.setRecordsInsertedLastIteration(p.size());
		} catch (Exception ex) {
			es.setRecordsInsertedLastIteration(0);
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			es.setLastIssueDetail(errors.toString());
			appendLoggerToWriter(Provider.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(Provider.class, bw, ex);
		}
		return es;

	}

	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {
		try {
			if (!checkExisting) {
				providerRepository.saveAll((List<Provider>) data);
				logFirstTimeDataEntryToTable(Provider.class, bw, data.size());
			} else {
				//
				Set<Integer> apptIdInES = new HashSet<>();
				Set<Integer> apptIdInDB = new HashSet<>();
				((List<Provider>) data).stream().map(Provider::getProviderId).forEach(apptIdInES::add);
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<Provider> inDB = providerRepository.findByProviderIdIn(apptIdInES);
				inDB.stream().map(Provider::getProviderId).forEach(apptIdInDB::add);

				apptIdInES.removeAll(apptIdInDB);// Patientid that are not in Local DB
				if (apptIdInES.size() > 0) {
					apptIdInES.forEach(id -> {
						providerRepository.save(((List<Provider>) data).stream()
								.filter(p -> id.equals(p.getProviderId())).findAny().orElse(null));
					});
				}
				apptIdInDB.removeAll(apptIdInES);// Patient id that are there in Local DB we need to update.
				if (apptIdInDB.size() > 0) {
					apptIdInDB.forEach(id -> {
						Provider p = ((List<Provider>) data).stream().filter(dp -> id.equals(dp.getProviderId()))
								.findAny().orElse(null);

						Provider old = inDB.stream().filter(ind -> id.equals(ind.getProviderId())).findAny()
								.orElse(null);
						p.setId(old.getId());
						p.setMovedToCloud(0);
						p.setCreatedDate(old.getCreatedDate());
						providerRepository.save(p);
					});

				}

				logAfterFirstTimeDataEntryToTable(Provider.class, bw, apptIdInES.size(), apptIdInDB.size(),
						String.join(",", apptIdInES.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
						String.join(",", apptIdInDB.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
			}
		} catch (Exception ex) {
			appenErrorToWriter(Provider.class, bw, ex);
		}
	}
}

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

import com.tricon.esdatareplication.dao.repdb.PaymentProviderRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.PaymentProviderRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.PaymentProvider;
import com.tricon.esdatareplication.entity.ruleenginedb.PaymentProviderReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class PaymentProviderTableService extends CommonTableService {

	@Autowired
	private PaymentProviderRepository paymentProviderRepository;

	@Autowired
	private PaymentProviderRepositoryRe paymentProviderRepositoryRe;

	@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		try {
			List<PaymentProvider> p = paymentProviderRepository
					.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
			List<PaymentProviderReplica> repList = new ArrayList<>();
			StringBuilder bu = new StringBuilder();
			p.forEach(x -> {
				x.setOfficeId(office.getUuid());
				PaymentProviderReplica rep = new PaymentProviderReplica();
				BeanUtils.copyProperties(x, rep);
				bu.append(rep.getTranNum() + ",");
				rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				repList.add(rep);
			});
			// new repository for cloud.. and save data...
			paymentProviderRepositoryRe.saveAll(repList);
			appendLoggerToWriter(PaymentProviderReplica.class, bw,
					Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
			appendLoggerToWriter(PaymentProviderReplica.class, bw, bu.toString(), true);
			p.forEach(x -> {
				x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			});
			paymentProviderRepository.saveAll(p);
			es.setRecordsInsertedLastIteration(p.size());
		} catch (Exception ex) {
			es.setRecordsInsertedLastIteration(0);
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			es.setLastIssueDetail(errors.toString());
			appendLoggerToWriter(PaymentProvider.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(PaymentProvider.class, bw, ex);
		}
		return es;

	}

	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {
        try {
		if (!checkExisting) {
			paymentProviderRepository.saveAll((List<PaymentProvider>) data);
			logFirstTimeDataEntryToTable(PaymentProvider.class, bw, data.size());
		}
		else {
			//
			Set<Integer> apptIdInES = new HashSet<>();
			Set<Integer> apptIdInDB = new HashSet<>();
			((List<PaymentProvider>) data).stream().map(PaymentProvider::getTranNum).forEach(apptIdInES::add);
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<PaymentProvider> inDB = paymentProviderRepository.findByTranNumIn(apptIdInES);
			inDB.stream().map(PaymentProvider::getTranNum).forEach(apptIdInDB::add);

			apptIdInES.removeAll(apptIdInDB);// Patientid that are not in Local DB
			if (apptIdInES.size() > 0) {
				apptIdInES.forEach(id -> {
					paymentProviderRepository.save(((List<PaymentProvider>) data).stream()
							.filter(p -> id.equals(p.getTranNum())).findAny().orElse(null));
				});
			}
			apptIdInDB.removeAll(apptIdInES);// Patient id that are there in Local DB we need to update.
			if (apptIdInDB.size() > 0) {
				apptIdInDB.forEach(id -> {
					PaymentProvider p = ((List<PaymentProvider>) data).stream().filter(dp -> id.equals(dp.getTranNum()))
							.findAny().orElse(null);

					PaymentProvider old = inDB.stream().filter(ind -> id.equals(ind.getTranNum())).findAny()
							.orElse(null);
					p.setId(old.getId());
					p.setMovedToCloud(0);
					p.setCreatedDate(old.getCreatedDate());
					paymentProviderRepository.save(p);
				});

			}

			logAfterFirstTimeDataEntryToTable(PaymentProvider.class, bw, apptIdInES.size(), apptIdInDB.size(),
					String.join(",", apptIdInES.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
					String.join(",", apptIdInDB.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
		}
	} catch (Exception ex) {
		appenErrorToWriter(PaymentProvider.class, bw, ex);
	}
}
}

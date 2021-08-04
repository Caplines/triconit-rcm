package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.esdatareplication.dao.repdb.TransactionsRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.TransactionsRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Transactions;
import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsReplica;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class TransactionsTableService {

	@Autowired
	private TransactionsRepository transactionsRepository;

	@Autowired
	private TransactionsRepositoryRe transactionsRepositoryRe;

	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		List<Transactions> p = transactionsRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
		List<TransactionsReplica> repList = new ArrayList<>();
		p.forEach(x -> {
			x.setOfficeId(office.getUuid());
			TransactionsReplica rep = new TransactionsReplica();
			BeanUtils.copyProperties(x, rep);
			rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			repList.add(rep);
		});
		// new repository for cloud.. and save data...
		transactionsRepositoryRe.saveAll(repList);
		p.forEach(x -> {
			x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
		});
		transactionsRepository.saveAll(p);
		es.setRecordsInsertedLastIteration(p.size());
		return es;

	}
	
	public void saveDataToLocalDB(List<?> data, boolean checkExisting) {
		
		if (!checkExisting)
			transactionsRepository.saveAll((List<Transactions>) data);
		else {
			//
			Set<Integer> apptIdInES = new HashSet<>();
			Set<Integer> apptIdInDB = new HashSet<>();
			((List<Transactions>) data).stream().map(Transactions::getTranNum).forEach(apptIdInES::add);
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<Transactions> inDB = transactionsRepository.findByTranNumIn(apptIdInES);
			inDB.stream().map(Transactions::getTranNum).forEach(apptIdInDB::add);

			apptIdInES.removeAll(apptIdInDB);// Patientid that are not in Local DB
			if (apptIdInES.size() > 0) {
				apptIdInES.forEach(id -> {
					transactionsRepository.save(((List<Transactions>) data).stream().filter(p -> id.equals(p.getTranNum()))
							.findAny().orElse(null));
				});
			}
			apptIdInDB.removeAll(apptIdInES);// Patient id that are there in Local DB we need to update.
			if (apptIdInDB.size() > 0) {
				apptIdInDB.forEach(id -> {
					Transactions p = ((List<Transactions>) data).stream().filter(dp -> id.equals(dp.getTranNum())).findAny()
							.orElse(null);

					Transactions old = inDB.stream().filter(ind -> id.equals(ind.getTranNum())).findAny().orElse(null);
					p.setId(old.getId());
					p.setMovedToCloud(0);
					p.setCreatedDate(old.getCreatedDate());
					transactionsRepository.save(p);
				});
			}
		}

		
	}

}

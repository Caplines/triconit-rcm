package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tricon.esdatareplication.dao.repdb.ESTableRepository;
import com.tricon.esdatareplication.dao.repdb.TransactionsRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.TransactionsRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Transactions;
import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class TransactionsTableService extends CommonTableService {

	@Autowired
	private TransactionsRepository transactionsRepository;

	@Autowired
	private TransactionsRepositoryRe transactionsRepositoryRe;
	
	@Autowired
	private ESTableRepository estableRepository;

	@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		try {
			List<Transactions> pL = transactionsRepository
					.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
			List<TransactionsReplica> repList = new ArrayList<>();
			StringBuilder bu = new StringBuilder();
			pL.forEach(x -> {
				x.setOfficeId(office.getUuid());
				TransactionsReplica rep = new TransactionsReplica();
				BeanUtils.copyProperties(x, rep);
				bu.append(rep.getTranNum() + ",");
				rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				repList.add(rep);
			});
			//
			Set<Integer> apptIdInES = new HashSet<>();
			Set<Integer> apptIdInDB = new HashSet<>();
			((List<TransactionsReplica>) repList).stream().map(TransactionsReplica::getTranNum).forEach(apptIdInES::add);
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<TransactionsReplica> inDB = transactionsRepositoryRe.findByTranNumInAndOfficeId(apptIdInES,office.getUuid());
			inDB.stream().map(TransactionsReplica::getTranNum).forEach(apptIdInDB::add);
			apptIdInES.removeAll(apptIdInDB);// TranNum that are not in Local DB
			if (apptIdInES.size() > 0) {
				
				apptIdInES.forEach(id -> {
					TransactionsReplica q=((List<TransactionsReplica>) repList).stream()
					.filter(p -> id.intValue()==p.getTranNum().intValue()).findAny().orElse(null);
					q.setId(null);
					transactionsRepositoryRe.save(q);
				});
			}
			apptIdInDB.removeAll(apptIdInES);// TranNum id that are there in Local DB we need to update.
			if (apptIdInDB.size() > 0) {
				apptIdInDB.forEach(id -> {
					TransactionsReplica p = ((List<TransactionsReplica>) repList).stream().filter(dp -> id.intValue()==dp.getTranNum().intValue())
							.findAny().orElse(null);

					TransactionsReplica old = inDB.stream().filter(ind -> id.intValue()==ind.getTranNum().intValue()).findAny()
							.orElse(null);
					if (p!=null && old!=null) {
						if (old!=null) {
							p.setId(old.getId());
							p.setCreatedDate(old.getCreatedDate());
						}
					    p.setMovedToCloud(1);
					    
					   transactionsRepositoryRe.save(p);
					}
				});

			}
			appendLoggerToWriter(TransactionsReplica.class, bw,
					Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
			appendLoggerToWriter(TransactionsReplica.class, bw, bu.toString(), true);
			pL.forEach(x -> {
				x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			});
			transactionsRepository.saveAll(pL);
			es.setRecordsInsertedLastIteration(pL.size());
			es.setUpdatedDate(new Date());
			estableRepository.save(es);
		} catch (Exception ex) {
			es.setRecordsInsertedLastIteration(0);
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			es.setLastIssueDetail(errors.toString());
			appendLoggerToWriter(Transactions.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(Transactions.class, bw, ex);
		}
		return es;

	}

	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {
		try {
			if (!checkExisting) {
				transactionsRepository.saveAll((List<Transactions>) data);
				logFirstTimeDataEntryToTable(Transactions.class, bw, data.size());
			} else {
				//
				Set<Integer> apptIdInES = new HashSet<>();
				Set<Integer> apptIdInDB = new HashSet<>();
				((List<Transactions>) data).stream().map(Transactions::getTranNum).forEach(apptIdInES::add);
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<Transactions> inDB = transactionsRepository.findByTranNumIn(apptIdInES);
				inDB.stream().map(Transactions::getTranNum).forEach(apptIdInDB::add);

				apptIdInES.removeAll(apptIdInDB);// TranNum that are not in Local DB
				if (apptIdInES.size() > 0) {
					apptIdInES.forEach(id -> {
						transactionsRepository.save(((List<Transactions>) data).stream()
								.filter(p -> id.intValue()==p.getTranNum().intValue()).findAny().orElse(null));
					});
				}
				apptIdInDB.removeAll(apptIdInES);// TranNum id that are there in Local DB we need to update.
				if (apptIdInDB.size() > 0) {
					apptIdInDB.forEach(id -> {
						Transactions p = ((List<Transactions>) data).stream().filter(dp -> id.equals(dp.getTranNum()))
								.findAny().orElse(null);

						Transactions old = inDB.stream().filter(ind -> id.intValue()==ind.getTranNum().intValue()).findAny()
								.orElse(null);
						p.setId(old.getId());
						p.setMovedToCloud(0);
						p.setCreatedDate(old.getCreatedDate());
						transactionsRepository.save(p);
					});

				}

				logAfterFirstTimeDataEntryToTable(Transactions.class, bw, apptIdInES.size(), apptIdInDB.size(),
						String.join(",", apptIdInES.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
						String.join(",", apptIdInDB.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
			}
		} catch (Exception ex) {
			appenErrorToWriter(Transactions.class, bw, ex);
		}
	}
}

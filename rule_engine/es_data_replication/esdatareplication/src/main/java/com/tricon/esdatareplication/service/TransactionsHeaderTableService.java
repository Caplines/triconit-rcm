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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tricon.esdatareplication.dao.repdb.ESTableRepository;
import com.tricon.esdatareplication.dao.repdb.TransactionsHeaderRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.TransactionsHeaderRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.TransactionsHeader;
import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsHeaderReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class TransactionsHeaderTableService extends CommonTableService {

	@Autowired
	private TransactionsHeaderRepository transactionsRepository;

	@Autowired
	private TransactionsHeaderRepositoryRe transactionsRepositoryRe;

	@Autowired
	private ESTableRepository estableRepository;

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;

	//@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		int localCt = 0;
		try {
			// Long count =
			// patientRepository.countByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
			// long totalPages = Double.valueOf(Math.ceil(count / (float)
			// batchSize)).longValue();
			while (true) {
				Pageable prepairPage = PageRequest.of(0, batchSize);// 0,50
				List<TransactionsHeader> pL = transactionsRepository
						.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO, prepairPage);
				if (pL.size() == 0)
					break;
				List<TransactionsHeaderReplica> repList = new ArrayList<>();
				StringBuilder bu = new StringBuilder();
				pL.forEach(x -> {
					x.setOfficeId(office.getUuid());
					TransactionsHeaderReplica rep = new TransactionsHeaderReplica();
					BeanUtils.copyProperties(x, rep);
					bu.append(rep.getTranNum() + ",");
					rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
					repList.add(rep);
				});
				//
				Set<Integer> apptIdInES = new HashSet<>();
				Set<Integer> apptIdInDB = new HashSet<>();
				((List<TransactionsHeaderReplica>) repList).stream().map(TransactionsHeaderReplica::getTranNum)
						.forEach(apptIdInES::add);
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<TransactionsHeaderReplica> inDB = transactionsRepositoryRe.findByTranNumInAndOfficeId(apptIdInES,
						office.getUuid());
				inDB.stream().map(TransactionsHeaderReplica::getTranNum).forEach(apptIdInDB::add);
				apptIdInES.removeAll(apptIdInDB);// TranNum that are not in Local DB
				if (apptIdInES.size() > 0) {
					List<TransactionsHeaderReplica> l = new ArrayList<>();
					apptIdInES.forEach(id -> {
						TransactionsHeaderReplica q = ((List<TransactionsHeaderReplica>) repList).stream()
								.filter(p -> id.intValue() == p.getTranNum().intValue()).findAny().orElse(null);
						q.setId(null);
						l.add(q);
					});
					if (l.size() > 0)
						transactionsRepositoryRe.saveAllAndFlush(l);
				}
				apptIdInDB.removeAll(apptIdInES);// TranNum id that are there in Local DB we need to update.
				if (apptIdInDB.size() > 0) {
					List<TransactionsHeaderReplica> l = new ArrayList<>();
					apptIdInDB.forEach(id -> {
						TransactionsHeaderReplica p = ((List<TransactionsHeaderReplica>) repList).stream()
								.filter(dp -> id.intValue() == dp.getTranNum().intValue()).findAny().orElse(null);

						TransactionsHeaderReplica old = inDB.stream()
								.filter(ind -> id.intValue() == ind.getTranNum().intValue()).findAny().orElse(null);
						if (p != null && old != null) {
							if (old != null) {
								p.setId(old.getId());
								p.setCreatedDate(old.getCreatedDate());
							}
							p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);

							l.add(p);
						}
					});
					if (l.size() > 0) {
						transactionsRepositoryRe.saveAllAndFlush(l);
						//transactionsRepositoryRe.flush();
					}
				}
				appendLoggerToWriter(TransactionsHeaderReplica.class, bw,
						Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
				appendLoggerToWriter(TransactionsHeaderReplica.class, bw, bu.toString(), true);
				pL.forEach(x -> {
					x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				});
				transactionsRepository.saveAll(pL);
				localCt = localCt + pL.size();
			}
			es.setRecordsInsertedLastIteration(localCt);
			//es.setUpdatedDate(new Date());
			estableRepository.save(es);
		} catch (Exception ex) {
			es.setRecordsInsertedLastIteration(0);
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			es.setLastIssueDetail(errors.toString());
			appendLoggerToWriter(TransactionsHeader.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(TransactionsHeader.class, bw, ex);
		}
		return es;

	}

	@Transactional(rollbackFor = Exception.class, transactionManager = "repDbTransactionManager")
	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {
		try {
			if (!checkExisting) {
				transactionsRepository.saveAll((List<TransactionsHeader>) data);
				logFirstTimeDataEntryToTable(TransactionsHeader.class, bw, data.size());
			} else {
				//
				Set<Integer> apptIdInES = new HashSet<>();
				Set<Integer> apptIdInDB = new HashSet<>();
				((List<TransactionsHeader>) data).stream().map(TransactionsHeader::getTranNum).forEach(apptIdInES::add);
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<TransactionsHeader> inDB = transactionsRepository.findByTranNumIn(apptIdInES);
				inDB.stream().map(TransactionsHeader::getTranNum).forEach(apptIdInDB::add);

				apptIdInES.removeAll(apptIdInDB);// TranNum that are not in Local DB
				if (apptIdInES.size() > 0) {
					List<TransactionsHeader> l = new ArrayList<>();
					apptIdInES.forEach(id -> {
						TransactionsHeader q = ((List<TransactionsHeader>) data).stream()
								.filter(p -> id.intValue() == p.getTranNum().intValue()).findAny().orElse(null);
						q.setId(null);
						q.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						l.add(q);
					});

					if (l.size() > 0)
						transactionsRepository.saveAll(l);
				}
				apptIdInDB.removeAll(apptIdInES);// TranNum id that are there in Local DB we need to update.
				if (apptIdInDB.size() > 0) {
					List<TransactionsHeader> l = new ArrayList<>();
					apptIdInDB.forEach(id -> {
						TransactionsHeader p = ((List<TransactionsHeader>) data).stream().filter(dp -> id.equals(dp.getTranNum()))
								.findAny().orElse(null);

						TransactionsHeader old = inDB.stream().filter(ind -> id.intValue() == ind.getTranNum().intValue())
								.findAny().orElse(null);
						p.setId(old.getId());
						p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						p.setCreatedDate(old.getCreatedDate());
						l.add(p);
					});
					if (l.size() > 0)
						transactionsRepository.saveAll(l);
				}

				logAfterFirstTimeDataEntryToTable(TransactionsHeader.class, bw, apptIdInES.size(), apptIdInDB.size(),
						String.join(",", apptIdInES.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
						String.join(",", apptIdInDB.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
			}
		} catch (Exception ex) {
			appenErrorToWriter(TransactionsHeader.class, bw, ex);
		}
	}
}

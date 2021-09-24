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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tricon.esdatareplication.dao.repdb.ESTableRepository;
import com.tricon.esdatareplication.dao.repdb.TransactionsDetailRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.TransactionsDetailRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Transactions;
import com.tricon.esdatareplication.entity.repdb.TransactionsDetail;
import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsDetailReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class TransactionsDetailTableService extends CommonTableService {

	@Autowired
	private TransactionsDetailRepository transactionsDetailRepository;

	@Autowired
	private TransactionsDetailRepositoryRe transactionsDetailRepositoryRe;

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
				List<TransactionsDetail> pL = transactionsDetailRepository
						.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO, prepairPage);
				if (pL.size() == 0)
					break;
				StringBuilder bu = new StringBuilder();
				List<TransactionsDetailReplica> repList = new ArrayList<>();
				pL.forEach(x -> {
					x.setOfficeId(office.getUuid());
					TransactionsDetailReplica rep = new TransactionsDetailReplica();
					BeanUtils.copyProperties(x, rep);
					bu.append(rep.getDetailId() + ",");
					rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
					repList.add(rep);
				});
				// new repository for cloud.. and save data...
				//
				Set<Integer> apptIdInES = new HashSet<>();
				Set<Integer> apptIdInDB = new HashSet<>();
				((List<TransactionsDetailReplica>) repList).stream().map(TransactionsDetailReplica::getDetailId)
						.forEach(apptIdInES::add);
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<TransactionsDetailReplica> inDB = transactionsDetailRepositoryRe
						.findByDetailIdInAndOfficeId(apptIdInES, office.getUuid());
				inDB.stream().map(TransactionsDetailReplica::getDetailId).forEach(apptIdInDB::add);
				apptIdInES.removeAll(apptIdInDB);// Detail Id that are not in Local DB
				if (apptIdInES.size() > 0) {
					List<TransactionsDetailReplica> l = new ArrayList<>();
					apptIdInES.forEach(id -> {
						TransactionsDetailReplica q = ((List<TransactionsDetailReplica>) repList).stream()
								.filter(p -> id.intValue() == p.getDetailId().intValue()).findAny().orElse(null);
						q.setId(null);
						l.add(q);
					});
					if (l.size() > 0)
						transactionsDetailRepositoryRe.saveAllAndFlush(l);
				}
				apptIdInDB.removeAll(apptIdInES);// Detail id that are there in Local DB we need to update.
				if (apptIdInDB.size() > 0) {
					List<TransactionsDetailReplica> l = new ArrayList<>();
					apptIdInDB.forEach(id -> {
						TransactionsDetailReplica p = ((List<TransactionsDetailReplica>) repList).stream()
								.filter(dp -> id.intValue() == dp.getDetailId().intValue()).findAny().orElse(null);

						TransactionsDetailReplica old = inDB.stream()
								.filter(ind -> id.intValue() == ind.getDetailId().intValue()).findAny().orElse(null);
						if (p != null && old != null) {
							if (old != null) {
								p.setId(old.getId());
								p.setCreatedDate(old.getCreatedDate());
							}
							p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
							l.add(p);
						}
					});
					if (l.size() > 0)
						transactionsDetailRepositoryRe.saveAllAndFlush(l);
				}
				appendLoggerToWriter(TransactionsReplica.class, bw,
						Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
				appendLoggerToWriter(TransactionsReplica.class, bw, bu.toString(), true);
				pL.forEach(x -> {
					x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				});
				transactionsDetailRepository.saveAll(pL);
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
			appendLoggerToWriter(Transactions.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(Transactions.class, bw, ex);
		}
		return es;

	}

	@Transactional(rollbackFor = Exception.class, transactionManager = "repDbTransactionManager")
	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {
		try {
			if (!checkExisting) {
				transactionsDetailRepository.saveAll((List<TransactionsDetail>) data);
				logFirstTimeDataEntryToTable(TransactionsDetail.class, bw, data.size());
			} else {
				//
				Set<Integer> apptIdInES = new HashSet<>();
				Set<Integer> apptIdInDB = new HashSet<>();
				((List<TransactionsDetail>) data).stream().map(TransactionsDetail::getDetailId)
						.forEach(apptIdInES::add);
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<TransactionsDetail> inDB = transactionsDetailRepository.findByDetailIdIn(apptIdInES);
				inDB.stream().map(TransactionsDetail::getDetailId).forEach(apptIdInDB::add);

				apptIdInES.removeAll(apptIdInDB);// that are not in Local DB
				if (apptIdInES.size() > 0) {
					List<TransactionsDetail> l = new ArrayList<>();
					apptIdInES.forEach(id -> {
						TransactionsDetail q = ((List<TransactionsDetail>) data).stream()
								.filter(p -> id.equals(p.getDetailId())).findAny().orElse(null);
						q.setId(null);
						q.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						l.add(q);
					});
					if (l.size() > 0)
						transactionsDetailRepository.saveAll(l);
				}
				apptIdInDB.removeAll(apptIdInES);// Patient id that are there in Local DB we need to update.
				if (apptIdInDB.size() > 0) {
					List<TransactionsDetail> l = new ArrayList<>();
					apptIdInDB.forEach(id -> {
						TransactionsDetail p = ((List<TransactionsDetail>) data).stream()
								.filter(dp -> id.equals(dp.getDetailId())).findAny().orElse(null);

						TransactionsDetail old = inDB.stream().filter(ind -> id.equals(ind.getDetailId())).findAny()
								.orElse(null);
						p.setId(old.getId());
						p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						p.setCreatedDate(old.getCreatedDate());
						l.add(p);
					});
					if (l.size() > 0)
						transactionsDetailRepository.saveAll(l);
				}
				logAfterFirstTimeDataEntryToTable(TransactionsDetail.class, bw, apptIdInES.size(), apptIdInDB.size(),
						String.join(",", apptIdInES.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
						String.join(",", apptIdInDB.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
			}
		} catch (Exception ex) {
			appenErrorToWriter(TransactionsDetail.class, bw, ex);
		}
	}
}
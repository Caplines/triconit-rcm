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

import com.tricon.esdatareplication.dao.repdb.TransactionsDetailRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.TransactionsDetailRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.TransactionsDetail;
import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsDetailReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class TransactionsDetailTableService extends CommonTableService {

	@Autowired
	private TransactionsDetailRepository transactionsDetailRepository;

	@Autowired
	private TransactionsDetailRepositoryRe transactionsDetailRepositoryRe;

	@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		try {
			List<TransactionsDetail> p = transactionsDetailRepository
					.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
			StringBuilder bu = new StringBuilder();
			List<TransactionsDetailReplica> repList = new ArrayList<>();
			p.forEach(x -> {
				x.setOfficeId(office.getUuid());
				TransactionsDetailReplica rep = new TransactionsDetailReplica();
				BeanUtils.copyProperties(x, rep);
				bu.append(rep.getTranNum() + ",");
				rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				repList.add(rep);
			});
			// new repository for cloud.. and save data...
			transactionsDetailRepositoryRe.saveAll(repList);
			appendLoggerToWriter(TransactionsDetailReplica.class, bw,
					Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
			appendLoggerToWriter(TransactionsDetailReplica.class, bw, bu.toString(), true);

			p.forEach(x -> {
				x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			});
			transactionsDetailRepository.saveAll(p);
			es.setRecordsInsertedLastIteration(p.size());
		} catch (Exception ex) {
			es.setRecordsInsertedLastIteration(0);
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			es.setLastIssueDetail(errors.toString());
			appendLoggerToWriter(TransactionsDetail.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(TransactionsDetail.class, bw, ex);
		}
		return es;

	}

	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {
       try {
		if (!checkExisting) {
			transactionsDetailRepository.saveAll((List<TransactionsDetail>) data);
			logFirstTimeDataEntryToTable(TransactionsDetail.class, bw, data.size());
		}
		else {
			//
			Set<Integer> apptIdInES = new HashSet<>();
			Set<Integer> apptIdInDB = new HashSet<>();
			((List<TransactionsDetail>) data).stream().map(TransactionsDetail::getDetailId).forEach(apptIdInES::add);
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<TransactionsDetail> inDB = transactionsDetailRepository.findByDetailIdIn(apptIdInES);
			inDB.stream().map(TransactionsDetail::getDetailId).forEach(apptIdInDB::add);

			apptIdInES.removeAll(apptIdInDB);// that are not in Local DB
			if (apptIdInES.size() > 0) {
				apptIdInES.forEach(id -> {
					transactionsDetailRepository.save(((List<TransactionsDetail>) data).stream()
							.filter(p -> id.equals(p.getDetailId())).findAny().orElse(null));
				});
			}
			apptIdInDB.removeAll(apptIdInES);// Patient id that are there in Local DB we need to update.
			if (apptIdInDB.size() > 0) {
				apptIdInDB.forEach(id -> {
					TransactionsDetail p = ((List<TransactionsDetail>) data).stream()
							.filter(dp -> id.equals(dp.getDetailId())).findAny().orElse(null);

					TransactionsDetail old = inDB.stream().filter(ind -> id.equals(ind.getDetailId())).findAny()
							.orElse(null);
					p.setId(old.getId());
					p.setMovedToCloud(0);
					p.setCreatedDate(old.getCreatedDate());
					transactionsDetailRepository.save(p);
				});
			}
			logAfterFirstTimeDataEntryToTable(TransactionsDetail.class, bw, apptIdInES.size(), apptIdInDB.size(),
					String.join(",", apptIdInES.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
					String.join(",", apptIdInDB.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
		}
		}catch (Exception ex) {
			appenErrorToWriter(TransactionsDetail.class, bw, ex);
		}
	}
}
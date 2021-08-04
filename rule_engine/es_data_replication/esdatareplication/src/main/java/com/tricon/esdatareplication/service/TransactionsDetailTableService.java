package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.esdatareplication.dao.repdb.TransactionsDetailRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.TransactionsDetailRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.TransactionsDetail;
import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsDetailReplica;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class TransactionsDetailTableService {

	@Autowired
	private TransactionsDetailRepository transactionsDetailRepository;

	@Autowired
	private TransactionsDetailRepositoryRe transactionsDetailRepositoryRe;

	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		List<TransactionsDetail> p = transactionsDetailRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
		List<TransactionsDetailReplica> repList = new ArrayList<>();
		p.forEach(x -> {
			x.setOfficeId(office.getUuid());
			TransactionsDetailReplica rep = new TransactionsDetailReplica();
			BeanUtils.copyProperties(x, rep);
			rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			repList.add(rep);
		});
		// new repository for cloud.. and save data...
		transactionsDetailRepositoryRe.saveAll(repList);
		p.forEach(x -> {
			x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
		});
		transactionsDetailRepository.saveAll(p);
		es.setRecordsInsertedLastIteration(p.size());
		return es;

	}
	
	public void saveDataToLocalDB(List<?> data, boolean checkExisting) {
		
		if (!checkExisting)
			transactionsDetailRepository.saveAll((List<TransactionsDetail>) data);
		else {
			//
			Set<Integer> apptIdInES = new HashSet<>();
			Set<Integer> apptIdInDB = new HashSet<>();
			((List<TransactionsDetail>) data).stream().map(TransactionsDetail::getDetailId).forEach(apptIdInES::add);
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<TransactionsDetail> inDB = transactionsDetailRepository.findByDetailIdIn(apptIdInES);
			inDB.stream().map(TransactionsDetail::getDetailId).forEach(apptIdInDB::add);

			apptIdInES.removeAll(apptIdInDB);//  that are not in Local DB
			if (apptIdInES.size() > 0) {
				apptIdInES.forEach(id -> {
					transactionsDetailRepository.save(((List<TransactionsDetail>) data).stream().filter(p -> id.equals(p.getDetailId()))
							.findAny().orElse(null));
				});
			}
			apptIdInDB.removeAll(apptIdInES);// Patient id that are there in Local DB we need to update.
			if (apptIdInDB.size() > 0) {
				apptIdInDB.forEach(id -> {
					TransactionsDetail p = ((List<TransactionsDetail>) data).stream().filter(dp -> id.equals(dp.getDetailId())).findAny()
							.orElse(null);

					TransactionsDetail old = inDB.stream().filter(ind -> id.equals(ind.getDetailId())).findAny().orElse(null);
					p.setId(old.getId());
					p.setMovedToCloud(0);
					p.setCreatedDate(old.getCreatedDate());
					transactionsDetailRepository.save(p);
				});
			}
		}

		
	}

}

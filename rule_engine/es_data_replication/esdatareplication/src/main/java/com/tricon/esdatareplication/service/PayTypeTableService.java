package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tricon.esdatareplication.dao.repdb.PayTypeRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.PayTypeRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Patient;
import com.tricon.esdatareplication.entity.repdb.PayType;
import com.tricon.esdatareplication.entity.ruleenginedb.PatientReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.PayTypeReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class PayTypeTableService {

	@Autowired
	private PayTypeRepository payTypeRepository;

	@Autowired
	private PayTypeRepositoryRe payTypeRepositoryRe;

	@Autowired
	CommonTableService commonTableService;

	@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		List<PayType> p = payTypeRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
		List<PayTypeReplica> repList = new ArrayList<>();
		StringBuilder bu = new StringBuilder();
		try {
			p.forEach(x -> {
				x.setOfficeId(office.getUuid());
				PayTypeReplica rep = new PayTypeReplica();
				BeanUtils.copyProperties(x, rep);
				bu.append(rep.getPayTypeId() + ",");
				repList.add(rep);
			});
			// new repository for cloud.. and save data...
			payTypeRepositoryRe.saveAll(repList);
			commonTableService.appendLoggerToWriter(PayTypeReplica.class, bw,
					Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
			commonTableService.appendLoggerToWriter(PayTypeReplica.class, bw, bu.toString(), true);

			p.forEach(x -> {

				x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			});
			payTypeRepository.saveAll(p);
			es.setRecordsInsertedLastIteration(p.size());
		} catch (Exception ex) {
			es.setRecordsInsertedLastIteration(0);
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			es.setLastIssueDetail(errors.toString());
			commonTableService.appendLoggerToWriter(PayType.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			commonTableService.appenErrorToWriter(PayType.class, bw, ex);
		}
		return es;

	}

	public void saveDataToLocalDB(BufferedWriter bw, List<?> data) {
		try {
			payTypeRepository.saveAll((List<PayType>) data);
			commonTableService.logFirstTimeDataEntryToTable(PayType.class, bw, data.size());
		} catch (Exception ex) {
			commonTableService.appenErrorToWriter(PayType.class, bw, ex);
		}
	}

}

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

import com.tricon.esdatareplication.dao.repdb.ChairsRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.ChairsRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.Chairs;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Patient;
import com.tricon.esdatareplication.entity.ruleenginedb.ChairsReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.PatientReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class ChairsTableService {

	@Autowired
	private ChairsRepository chairsRepository;

	@Autowired
	private ChairsRepositoryRe chairsRepositoryRe;
	
	@Autowired 
	CommonTableService commonTableService;

	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw,Office office,ESTable es) {
		try {
		List<Chairs> p = chairsRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
		List<ChairsReplica> repList = new ArrayList<>();
		StringBuilder bu= new StringBuilder();
		p.forEach(x -> {
			x.setOfficeId(office.getUuid());
			ChairsReplica rep = new ChairsReplica();
			BeanUtils.copyProperties(x, rep);
			bu.append(rep.getChairNum()+",");
			repList.add(rep);
		});
		// new repository for cloud.. and save data...
		chairsRepositoryRe.saveAll(repList);
		p.forEach(x -> {

			x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
		});
		chairsRepository.saveAll(p);
		commonTableService.appendLoggerToWriter(ChairsReplica.class, bw,Constants.RECORDS_UPDATED_IN_TABLE_CLOUD+":"+repList.size() , true);
		commonTableService.appendLoggerToWriter(ChairsReplica.class, bw, bu.toString(), true);
		es.setRecordsInsertedLastIteration(p.size());
		}catch(Exception ex) {
			es.setRecordsInsertedLastIteration(0);
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			es.setLastIssueDetail(errors.toString());
			commonTableService.appendLoggerToWriter(Chairs.class,bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			commonTableService.appenErrorToWriter(Chairs.class, bw, ex);
		}
		return es;

	}
	
	@Transactional(rollbackFor = Exception.class, transactionManager = "repDbTransactionManager")
	public void saveDataToLocalDB(BufferedWriter bw,List<?> data) {
		try {
		chairsRepository.saveAll((List<Chairs>) data);
		commonTableService.logFirstTimeDataEntryToTable(Chairs.class, bw, data.size());
		}catch (Exception ex) {
			commonTableService.appenErrorToWriter(Chairs.class, bw, ex);
		}
	}

	
}

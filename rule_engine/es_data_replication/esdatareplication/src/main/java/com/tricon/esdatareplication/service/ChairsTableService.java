package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.esdatareplication.dao.repdb.ChairsRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.ChairsRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.Chairs;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.ruleenginedb.ChairsReplica;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class ChairsTableService {

	@Autowired
	private ChairsRepository chairsRepository;

	@Autowired
	private ChairsRepositoryRe chairsRepositoryRe;
	
	
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw,Office office,ESTable es) {
		List<Chairs> p = chairsRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
		List<ChairsReplica> repList = new ArrayList<>();
		p.forEach(x -> {
			x.setOfficeId(office.getUuid());
			ChairsReplica rep = new ChairsReplica();
			BeanUtils.copyProperties(x, rep);
			repList.add(rep);
		});
		// new repository for cloud.. and save data...
		chairsRepositoryRe.saveAll(repList);
		p.forEach(x -> {

			x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
		});
		chairsRepository.saveAll(p);
		es.setRecordsInsertedLastIteration(p.size());
		
		return es;

	}
	
	public void saveDataToLocalDB(List<?> data) {
		chairsRepository.saveAll((List<Chairs>) data);
	}

	
}

package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.esdatareplication.dao.repdb.PayTypeRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.PayTypeRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.PayType;
import com.tricon.esdatareplication.entity.ruleenginedb.PayTypeReplica;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class PayTypeTableService {


	@Autowired
	private PayTypeRepository payTypeRepository;

	@Autowired
	private PayTypeRepositoryRe payTypeRepositoryRe;
	
	
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw,Office office,ESTable es) {
		List<PayType> p = payTypeRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
		List<PayTypeReplica> repList = new ArrayList<>();
		p.forEach(x -> {
			x.setOfficeId(office.getUuid());
			PayTypeReplica rep = new PayTypeReplica();
			BeanUtils.copyProperties(x, rep);
			repList.add(rep);
		});
		// new repository for cloud.. and save data...
		payTypeRepositoryRe.saveAll(repList);
		p.forEach(x -> {

			x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
		});
		payTypeRepository.saveAll(p);
		es.setRecordsInsertedLastIteration(p.size());
		
		return es;

	}
	
	public void saveDataToLocalDB(List<?> data) {
		payTypeRepository.saveAll((List<PayType>) data);
	}

}

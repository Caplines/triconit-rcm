package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.esdatareplication.dao.repdb.PaymentProviderRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.PaymentProviderRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.PaymentProvider;
import com.tricon.esdatareplication.entity.ruleenginedb.PaymentProviderReplica;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class PaymentProviderTableService {

	@Autowired
	private PaymentProviderRepository paymentProviderRepository;

	@Autowired
	private PaymentProviderRepositoryRe paymentProviderRepositoryRe;

	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		List<PaymentProvider> p = paymentProviderRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
		List<PaymentProviderReplica> repList = new ArrayList<>();
		p.forEach(x -> {
			x.setOfficeId(office.getUuid());
			PaymentProviderReplica rep = new PaymentProviderReplica();
			BeanUtils.copyProperties(x, rep);
			rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			repList.add(rep);
		});
		// new repository for cloud.. and save data...
		paymentProviderRepositoryRe.saveAll(repList);
		p.forEach(x -> {
			x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
		});
		paymentProviderRepository.saveAll(p);
		es.setRecordsInsertedLastIteration(p.size());
		return es;

	}
	
	public void saveDataToLocalDB(List<?> data, boolean checkExisting) {
		
		if (!checkExisting)
			paymentProviderRepository.saveAll((List<PaymentProvider>) data);
		else {
			//
			Set<Integer> apptIdInES = new HashSet<>();
			Set<Integer> apptIdInDB = new HashSet<>();
			((List<PaymentProvider>) data).stream().map(PaymentProvider::getTranNum).forEach(apptIdInES::add);
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<PaymentProvider> inDB = paymentProviderRepository.findByTranNumIn(apptIdInES);
			inDB.stream().map(PaymentProvider::getTranNum).forEach(apptIdInDB::add);

			apptIdInES.removeAll(apptIdInDB);// Patientid that are not in Local DB
			if (apptIdInES.size() > 0) {
				apptIdInES.forEach(id -> {
					paymentProviderRepository.save(((List<PaymentProvider>) data).stream().filter(p -> id.equals(p.getTranNum()))
							.findAny().orElse(null));
				});
			}
			apptIdInDB.removeAll(apptIdInES);// Patient id that are there in Local DB we need to update.
			if (apptIdInDB.size() > 0) {
				apptIdInDB.forEach(id -> {
					PaymentProvider p = ((List<PaymentProvider>) data).stream().filter(dp -> id.equals(dp.getTranNum())).findAny()
							.orElse(null);

					PaymentProvider old = inDB.stream().filter(ind -> id.equals(ind.getTranNum())).findAny().orElse(null);
					p.setId(old.getId());
					p.setMovedToCloud(0);
					p.setCreatedDate(old.getCreatedDate());
					paymentProviderRepository.save(p);
				});
			}
		}

		
	}

}

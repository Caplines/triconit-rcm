package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.esdatareplication.dao.repdb.PlannedServicesRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.PlannedServicesRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.PlannedServices;
import com.tricon.esdatareplication.entity.ruleenginedb.PlannedServicesReplica;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class PlannedServicesTableService {

	@Autowired
	private PlannedServicesRepository plannedServicesRepository;

	@Autowired
	private PlannedServicesRepositoryRe plannedServicesRepositoryRe;

	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		List<PlannedServices> p = plannedServicesRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
		List<PlannedServicesReplica> repList = new ArrayList<>();
		p.forEach(x -> {
			x.setOfficeId(office.getUuid());
			PlannedServicesReplica rep = new PlannedServicesReplica();
			BeanUtils.copyProperties(x, rep);
			rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			repList.add(rep);
		});
		// new repository for cloud.. and save data...
		plannedServicesRepositoryRe.saveAll(repList);
		p.forEach(x -> {
			x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
		});
		plannedServicesRepository.saveAll(p);
		es.setRecordsInsertedLastIteration(p.size());
		return es;

	}

	public void saveDataToLocalDB(List<?> data, boolean checkExisting) {

		if (!checkExisting)
			plannedServicesRepository.saveAll((List<PlannedServices>) data);
		else {
			//
			Set<Integer> apptIdInES = new HashSet<>();
			Set<Integer> apptIdInDB = new HashSet<>();
			((List<PlannedServices>) data).stream().map(PlannedServices::getApptGroup).forEach(apptIdInES::add);
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<PlannedServices> inDB = plannedServicesRepository.findByApptGroupIn(apptIdInES);
			inDB.stream().map(PlannedServices::getApptGroup).forEach(apptIdInDB::add);

			apptIdInES.removeAll(apptIdInDB);//  that are not in Local DB
			if (apptIdInES.size() > 0) {
				apptIdInES.forEach(id -> {
					plannedServicesRepository.save(((List<PlannedServices>) data).stream()
							.filter(p -> id.equals(p.getApptGroup())).findAny().orElse(null));
				});
			}
			apptIdInDB.removeAll(apptIdInES);// Patient id that are there in Local DB we need to update.
			if (apptIdInDB.size() > 0) {
				apptIdInDB.forEach(id -> {
					PlannedServices p = ((List<PlannedServices>) data).stream().filter(dp -> id.equals(dp.getApptGroup()))
							.findAny().orElse(null);

					PlannedServices old = inDB.stream().filter(ind -> id.equals(ind.getApptGroup())).findAny()
							.orElse(null);
					p.setId(old.getId());
					p.setMovedToCloud(0);
					p.setCreatedDate(old.getCreatedDate());
					plannedServicesRepository.save(p);
				});
			}
		}

	}

}

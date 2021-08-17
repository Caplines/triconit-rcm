package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.esdatareplication.dao.repdb.EmployerRespository;
import com.tricon.esdatareplication.dao.ruleenginedb.EmployerRespositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Employer;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.ruleenginedb.EmployerReplica;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class EmployerTableService {

	@Autowired
	private EmployerRespository employerRespository;

	@Autowired
	private EmployerRespositoryRe employerRespositoryRe;

	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		List<Employer> p = employerRespository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
		List<EmployerReplica> repList = new ArrayList<>();
		p.forEach(x -> {
			x.setOfficeId(office.getUuid());
			EmployerReplica rep = new EmployerReplica();
			BeanUtils.copyProperties(x, rep);
			rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			repList.add(rep);
		});
		// new repository for cloud.. and save data...
		employerRespositoryRe.saveAll(repList);
		p.forEach(x -> {
			x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
		});
		employerRespository.saveAll(p);
		es.setRecordsInsertedLastIteration(p.size());
		return es;

	}

	public void saveDataToLocalDB(BufferedWriter bw,List<?> data, boolean checkExisting) {

		if (!checkExisting)
			employerRespository.saveAll((List<Employer>) data);
		else {
			//
			Set<Integer> apptIdInES = new HashSet<>();
			Set<Integer> apptIdInDB = new HashSet<>();
			((List<Employer>) data).stream().map(Employer::getEmployerId).forEach(apptIdInES::add);
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<Employer> inDB = employerRespository.findByEmployerIdIn(apptIdInES);
			inDB.stream().map(Employer::getEmployerId).forEach(apptIdInDB::add);

			apptIdInES.removeAll(apptIdInDB);// Patientid that are not in Local DB
			if (apptIdInES.size() > 0) {
				apptIdInES.forEach(id -> {
					employerRespository.save(((List<Employer>) data).stream()
							.filter(p -> id.equals(p.getEmployerId())).findAny().orElse(null));
				});
			}
			apptIdInDB.removeAll(apptIdInES);// EmployerId that are there in Local DB we need to update.
			if (apptIdInDB.size() > 0) {
				apptIdInDB.forEach(id -> {
					Employer p = ((List<Employer>) data).stream().filter(dp -> id.equals(dp.getEmployerId()))
							.findAny().orElse(null);

					Employer old = inDB.stream().filter(ind -> id.equals(ind.getEmployerId())).findAny()
							.orElse(null);
					p.setId(old.getId());
					p.setMovedToCloud(0);
					p.setCreatedDate(old.getCreatedDate());
					employerRespository.save(p);
				});
			}
		}

	}

}

package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.esdatareplication.dao.repdb.PatientRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.PatientRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Patient;
import com.tricon.esdatareplication.entity.ruleenginedb.PatientReplica;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class PatientTableService {

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private PatientRepositoryRe patientRepositoryre;

	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		List<Patient> p = patientRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
		List<PatientReplica> repList = new ArrayList<>();
		p.forEach(x -> {
			x.setOfficeId(office.getUuid());
			PatientReplica rep = new PatientReplica();
			BeanUtils.copyProperties(x, rep);
			rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			repList.add(rep);
		});
		// new repository for cloud.. and save data...
		patientRepositoryre.saveAll(repList);
		p.forEach(x -> {
			x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
		});
		patientRepository.saveAll(p);
		es.setRecordsInsertedLastIteration(p.size());
		return es;
	}

	public void saveDataToLocalDB(List<?> data, boolean checkExisting) {

		if (!checkExisting)
			patientRepository.saveAll((List<Patient>) data);
		else {
			//
			Set<String> patIdInES = new HashSet<>();
			Set<String> patIdInDB = new HashSet<>();
			((List<Patient>) data).stream().map(Patient::getPatientId).forEach(patIdInES::add);
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<Patient> inDB = patientRepository.findByPatientIdIn(patIdInES);
			inDB.stream().map(Patient::getPatientId).forEach(patIdInDB::add);

			patIdInES.removeAll(patIdInDB);// Patientid that are not in Local DB
			if (patIdInES.size() > 0) {
				patIdInES.forEach(id -> {
					patientRepository.save(((List<Patient>) data).stream().filter(p -> id.equals(p.getPatientId()))
							.findAny().orElse(null));
				});
			}
			patIdInDB.removeAll(patIdInES);// Patient id that are there in Local DB we need to update.
			if (patIdInDB.size() > 0) {
				patIdInDB.forEach(id -> {
					Patient p = ((List<Patient>) data).stream().filter(dp -> id.equals(dp.getPatientId())).findAny()
							.orElse(null);

					Patient old = inDB.stream().filter(ind -> id.equals(ind.getPatientId())).findAny().orElse(null);
					p.setId(old.getId());
					p.setMovedToCloud(0);
					p.setCreatedDate(old.getCreatedDate());
					patientRepository.save(p);
				});
			}
		}

	}
}

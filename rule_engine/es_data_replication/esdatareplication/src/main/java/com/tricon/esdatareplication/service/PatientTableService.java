package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tricon.esdatareplication.dao.repdb.ESTableRepository;
import com.tricon.esdatareplication.dao.repdb.PatientRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.PatientRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Patient;
import com.tricon.esdatareplication.entity.ruleenginedb.PatientReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class PatientTableService extends CommonTableService{

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private PatientRepositoryRe patientRepositoryre;

	@Autowired
	private ESTableRepository estableRepository;
	
	//@Autowired 
	//CommonTableService commonTableService;

	@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		try {
		List<Patient> pL = patientRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
		StringBuilder bu= new StringBuilder();
		List<PatientReplica> repList = new ArrayList<>();
		//List<String> uni1 = new ArrayList<>();
		pL.forEach(x -> {
			x.setOfficeId(office.getUuid());
			PatientReplica rep = new PatientReplica();
			BeanUtils.copyProperties(x, rep);
			bu.append(rep.getPatientId()+",");
			//uni.add(rep.getPatientId());
			rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			repList.add(rep);
		});
		// new repository for cloud.. and save data...
		
		//
		Set<String> patIdInES = new HashSet<>();
		Set<String> patIdInDB = new HashSet<>();
		((List<PatientReplica>) repList).stream().map(PatientReplica::getPatientId).forEach(patIdInES::add);
		// or
		// d2.forEach(a -> patIds.add(a.getPatientId()));
		List<PatientReplica> inDB = patientRepositoryre.findByPatientIdInAndOfficeId(patIdInES,office.getUuid());
		inDB.stream().map(PatientReplica::getPatientId).forEach(patIdInDB::add);

		patIdInES.removeAll(patIdInDB);// Patientid that are not in Local DB
        //  new Records..
		
		if (patIdInES.size() > 0) {
			patIdInES.forEach(id -> {
				PatientReplica q=((List<PatientReplica>) repList).stream().filter(p -> id.equals(p.getPatientId()))
						.findAny().orElse(null);
				q.setId(null);
				patientRepositoryre.save(q);
			});
		}
		patIdInDB.removeAll(patIdInES);// Patient id that are there in Local DB we need to update.
		if (patIdInDB.size() > 0) {
			patIdInDB.forEach(id -> {
				PatientReplica p = ((List<PatientReplica>) repList).stream().filter(dp -> id.equals(dp.getPatientId())).findAny()
						.orElse(null);

				PatientReplica old = inDB.stream().filter(ind -> id.equals(ind.getPatientId())).findAny().orElse(null);
				if (p!=null && old!=null) {
				p.setId(old.getId());
				p.setMovedToCloud(1);
				p.setCreatedDate(old.getCreatedDate());
				patientRepositoryre.save(p);
				}
			});
		}
		
		//
		//
		
		appendLoggerToWriter(PatientReplica.class, bw,Constants.RECORDS_UPDATED_IN_TABLE_CLOUD+":"+repList.size() , true);
		appendLoggerToWriter(PatientReplica.class, bw, bu.toString(), true);
		pL.forEach(x -> {
			x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
		});
		patientRepository.saveAll(pL);
		es.setRecordsInsertedLastIteration(pL.size());
		es.setUpdatedDate(new Date());
		estableRepository.save(es);
		}catch(Exception ex) {
			es.setRecordsInsertedLastIteration(0);
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			es.setLastIssueDetail(errors.toString());
			appendLoggerToWriter(Patient.class,bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(Patient.class, bw, ex);
		}
		return es;
	}

	public void saveDataToLocalDB(BufferedWriter bw,List<?> data, boolean checkExisting) {
		try {
		if (!checkExisting) {
			patientRepository.saveAll((List<Patient>) data);
			logFirstTimeDataEntryToTable(Patient.class, bw, data.size());
		}
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
            //  new Records..
			
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
			logAfterFirstTimeDataEntryToTable(Patient.class, bw, patIdInES.size(), patIdInDB.size(),
					String.join(",", patIdInES), String.join(",", patIdInDB));
		}
		}catch (Exception ex) {
			appenErrorToWriter(Patient.class, bw, ex);
		}
	}
}

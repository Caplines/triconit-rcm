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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tricon.esdatareplication.dao.repdb.ESTableRepository;
import com.tricon.esdatareplication.dao.repdb.PatientRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.PatientRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Patient;
import com.tricon.esdatareplication.entity.ruleenginedb.EmployerReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.PatientReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.PlannedServicesReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class PatientTableService extends CommonTableService {

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private PatientRepositoryRe patientRepositoryre;

	@Autowired
	private ESTableRepository estableRepository;

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;

	// @Autowired
	// CommonTableService commonTableService;

	//@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		int localCt = 0;
		try {
			//Long count = patientRepository.countByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
			//long totalPages = Double.valueOf(Math.ceil(count / (float) batchSize)).longValue();
			while(true) {
				Pageable prepairPage = PageRequest.of(0, batchSize);// 0,50
				List<Patient> pL = patientRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO,
						prepairPage);
                if (pL.size()==0) break;
				// List<Patient> pL =
				// patientRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
				StringBuilder bu = new StringBuilder();
				List<PatientReplica> repList = new ArrayList<>();
				// List<String> uni1 = new ArrayList<>();
				pL.forEach(x -> {
					x.setOfficeId(office.getUuid());
					PatientReplica rep = new PatientReplica();
					BeanUtils.copyProperties(x, rep);
					bu.append(rep.getPatientId() + ",");
					// uni.add(rep.getPatientId());
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
				List<PatientReplica> inDB = patientRepositoryre.findByPatientIdInAndOfficeId(patIdInES,
						office.getUuid());
				inDB.stream().map(PatientReplica::getPatientId).forEach(patIdInDB::add);

				patIdInES.removeAll(patIdInDB);// Patientid that are not in Local DB
				// new Records..

				if (patIdInES.size() > 0) {
					List<PatientReplica> l = new ArrayList<>();
					patIdInES.forEach(id -> {
						PatientReplica q = ((List<PatientReplica>) repList).stream()
								.filter(p -> id.equals(p.getPatientId())).findAny().orElse(null);
						q.setId(null);
						q.setOfficeId(office.getUuid());
						l.add(q);
					});
					//patientRepositoryre.saveAllAndFlush(l);
					try {
						patientRepositoryre.saveAllAndFlush(l);
						}catch(Exception ex1) {
							appendLoggerToWriter(PatientReplica.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
							String tnum="";
							for(PatientReplica p:l) {
								appendLoggerToWriter(PatientReplica.class, bw, "Now in Loop", true);
								tnum="Patient_id:->"+p.getPatientId()+";";
								try {
								patientRepositoryre.saveAndFlush(p);
								}catch(Exception ex) {
									appendLoggerToWriter(PatientReplica.class, bw, tnum, true);
									StringWriter errors = new StringWriter();
									ex.printStackTrace(new PrintWriter(errors));
									es.setLastIssueDetail(errors.toString());
									appendLoggerToWriter(PatientReplica.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
									appenErrorToWriter(PatientReplica.class, bw, ex);
								}
							}
							
						}
					
				}
				patIdInDB.removeAll(patIdInES);// Patient id that are there in Local DB we need to update.
				if (patIdInDB.size() > 0) {
					List<PatientReplica> l = new ArrayList<>();
					patIdInDB.forEach(id -> {
						PatientReplica p = ((List<PatientReplica>) repList).stream()
								.filter(dp -> id.equals(dp.getPatientId())).findAny().orElse(null);

						PatientReplica old = inDB.stream().filter(ind -> id.equals(ind.getPatientId())).findAny()
								.orElse(null);
						if (p != null && old != null) {
							p.setId(old.getId());
							p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
							p.setCreatedDate(old.getCreatedDate());
							p.setOfficeId(office.getUuid());
							l.add(p);
						}
					});
					try {
						patientRepositoryre.saveAllAndFlush(l);
						}catch(Exception ex1) {
							appendLoggerToWriter(PatientReplica.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
							String tnum="";
							for(PatientReplica p:l) {
								appendLoggerToWriter(PatientReplica.class, bw, "Now in Loop", true);
								tnum="Patient_id:->"+p.getPatientId()+";";
								try {
								patientRepositoryre.saveAndFlush(p);
								}catch(Exception ex) {
									appendLoggerToWriter(PatientReplica.class, bw, tnum, true);
									StringWriter errors = new StringWriter();
									ex.printStackTrace(new PrintWriter(errors));
									es.setLastIssueDetail(errors.toString());
									appendLoggerToWriter(PatientReplica.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
									appenErrorToWriter(PatientReplica.class, bw, ex);
								}
							}
							
						}
				}

				//
				//

				appendLoggerToWriter(PatientReplica.class, bw,
						Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
				appendLoggerToWriter(PatientReplica.class, bw, bu.toString(), true);
				pL.forEach(x -> {
					x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				});
				patientRepository.saveAll(pL);
				localCt = localCt + pL.size();
			}

			es.setRecordsInsertedLastIteration(localCt);
			//es.setUpdatedDate(new Date());
			estableRepository.save(es);
		} catch (Exception ex) {
			es.setRecordsInsertedLastIteration(0);
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			es.setLastIssueDetail(errors.toString());
			appendLoggerToWriter(Patient.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(Patient.class, bw, ex);
		}
		return es;
	}

	@Transactional(rollbackFor = Exception.class, transactionManager = "repDbTransactionManager")
	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {
		try {
			if (!checkExisting) {
				patientRepository.saveAll((List<Patient>) data);
				logFirstTimeDataEntryToTable(Patient.class, bw, data.size());
			} else {
				//
				Set<String> patIdInES = new HashSet<>();
				Set<String> patIdInDB = new HashSet<>();
				((List<Patient>) data).stream().map(Patient::getPatientId).forEach(patIdInES::add);
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<Patient> inDB = patientRepository.findByPatientIdIn(patIdInES);
				inDB.stream().map(Patient::getPatientId).forEach(patIdInDB::add);

				patIdInES.removeAll(patIdInDB);// Patientid that are not in Local DB
				// new Records..

				if (patIdInES.size() > 0) {
					List<Patient> l = new ArrayList<>();
					patIdInES.forEach(id -> {
						Patient q = ((List<Patient>) data).stream().filter(p -> id.equals(p.getPatientId())).findAny()
								.orElse(null);
						q.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						q.setId(null);
						l.add(q);
					});
					if (l.size() > 0)
						patientRepository.saveAll(l);
				}
				patIdInDB.removeAll(patIdInES);// Patient id that are there in Local DB we need to update.
				if (patIdInDB.size() > 0) {
					List<Patient> l = new ArrayList<>();
					patIdInDB.forEach(id -> {
						Patient p = ((List<Patient>) data).stream().filter(dp -> id.equals(dp.getPatientId())).findAny()
								.orElse(null);

						Patient old = inDB.stream().filter(ind -> id.equals(ind.getPatientId())).findAny().orElse(null);
						p.setId(old.getId());
						p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						p.setCreatedDate(old.getCreatedDate());

						l.add(p);
					});
					if (l.size() > 0)
						patientRepository.saveAll(l);
				}
				logAfterFirstTimeDataEntryToTable(Patient.class, bw, patIdInES.size(), patIdInDB.size(),
						String.join(",", patIdInES), String.join(",", patIdInDB));
			}
		} catch (Exception ex) {
			appenErrorToWriter(Patient.class, bw, ex);
		}
	}
}

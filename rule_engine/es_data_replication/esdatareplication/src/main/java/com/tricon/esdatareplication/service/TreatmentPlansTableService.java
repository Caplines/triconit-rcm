package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.ls.LSSerializer;

import com.tricon.esdatareplication.dao.repdb.ESTableRepository;
import com.tricon.esdatareplication.dao.repdb.TreatmentPlansRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.TreatmentPlansRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.Appointment;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Patient;
import com.tricon.esdatareplication.entity.repdb.TreatmentPlanItems;
import com.tricon.esdatareplication.entity.repdb.TreatmentPlans;
import com.tricon.esdatareplication.entity.ruleenginedb.AppointmentReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.TreatmentPlanItemsReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.TreatmentPlansReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class TreatmentPlansTableService extends CommonTableService {

	@Autowired
	private TreatmentPlansRepository treatmentPlansRepository;

	@Autowired
	private TreatmentPlansRepositoryRe treatmentPlansRepositoryRe;

	@Autowired
	private ESTableRepository estableRepository;

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;

	//@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		int localCt = 0;
		try {
			// Long count =
			// patientRepository.countByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
			// long totalPages = Double.valueOf(Math.ceil(count / (float)
			// batchSize)).longValue();
			while (true) {
				Pageable prepairPage = PageRequest.of(0, batchSize);// 0,50
				List<TreatmentPlans> pL = treatmentPlansRepository
						.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO, prepairPage);
				if (pL.size() == 0)
					break;
				List<TreatmentPlansReplica> repList = new ArrayList<>();
				StringBuilder bu = new StringBuilder();
				pL.forEach(x -> {
					x.setOfficeId(office.getUuid());
					TreatmentPlansReplica rep = new TreatmentPlansReplica();
					BeanUtils.copyProperties(x, rep);
					bu.append(rep.getTreatmentPlanId() + ",");
					rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
					repList.add(rep);
				});
				// new repository for cloud.. and save data...
				Set<Integer> apptIdInES = new HashSet<>();
				Set<Integer> apptIdInDB = new HashSet<>();
				((List<TreatmentPlansReplica>) repList).stream().map(TreatmentPlansReplica::getTreatmentPlanId)
						.forEach(apptIdInES::add);
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<TreatmentPlansReplica> inDB = treatmentPlansRepositoryRe
						.findByTreatmentPlanIdInAndOfficeId(apptIdInES, office.getUuid());
				inDB.stream().map(TreatmentPlansReplica::getTreatmentPlanId).forEach(apptIdInDB::add);
				apptIdInES.removeAll(apptIdInDB);// TranNum that are not in Local DB
				if (apptIdInES.size() > 0) {

					apptIdInES.forEach(id -> {
						TreatmentPlansReplica q = ((List<TreatmentPlansReplica>) repList).stream()
								.filter(p -> id.intValue() == p.getTreatmentPlanId().intValue()).findAny().orElse(null);
						q.setId(null);
						treatmentPlansRepositoryRe.save(q);
					});
				}
				apptIdInDB.removeAll(apptIdInES);// TranNum id that are there in Local DB we need to update.
				if (apptIdInDB.size() > 0) {
					apptIdInDB.forEach(id -> {
						TreatmentPlansReplica p = ((List<TreatmentPlansReplica>) repList).stream()
								.filter(dp -> id.intValue() == dp.getTreatmentPlanId().intValue()).findAny()
								.orElse(null);

						TreatmentPlansReplica old = inDB.stream()
								.filter(ind -> id.intValue() == ind.getTreatmentPlanId().intValue()).findAny()
								.orElse(null);
						if (p != null && old != null) {
							if (old != null) {
								p.setId(old.getId());
								p.setCreatedDate(old.getCreatedDate());
							}
							p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);

							treatmentPlansRepositoryRe.save(p);
						}
					});

				}
				appendLoggerToWriter(TransactionsReplica.class, bw,
						Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
				appendLoggerToWriter(TransactionsReplica.class, bw, bu.toString(), true);
				pL.forEach(x -> {
					x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				});
				treatmentPlansRepository.saveAll(pL);
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
				treatmentPlansRepository.saveAll((List<TreatmentPlans>) data);
				logFirstTimeDataEntryToTable(TreatmentPlans.class, bw, data.size());
			} else {
				//
				Set<Integer> apptIdInES = new HashSet<>();
				Set<Integer> apptIdInDB = new HashSet<>();
				((List<TreatmentPlans>) data).stream().map(TreatmentPlans::getTreatmentPlanId).forEach(apptIdInES::add);
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<TreatmentPlans> inDB = treatmentPlansRepository.findByTreatmentPlanIdIn(apptIdInES);
				inDB.stream().map(TreatmentPlans::getTreatmentPlanId).forEach(apptIdInDB::add);

				apptIdInES.removeAll(apptIdInDB);// Patientid that are not in Local DB
				if (apptIdInES.size() > 0) {
					List<TreatmentPlans> l = new ArrayList<>();
					apptIdInES.forEach(id -> {
						TreatmentPlans q = ((List<TreatmentPlans>) data).stream()
								.filter(p -> id.equals(p.getTreatmentPlanId())).findAny().orElse(null);
						q.setId(null);
						q.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						l.add(q);
					});
					if (l.size() > 0)
						treatmentPlansRepository.saveAll(l);
				}
				apptIdInDB.removeAll(apptIdInES);// Patient id that are there in Local DB we need to update.
				if (apptIdInDB.size() > 0) {
					List<TreatmentPlans> l = new ArrayList<>();
					apptIdInDB.forEach(id -> {
						TreatmentPlans p = ((List<TreatmentPlans>) data).stream()
								.filter(dp -> id.equals(dp.getTreatmentPlanId())).findAny().orElse(null);

						TreatmentPlans old = inDB.stream().filter(ind -> id.equals(ind.getTreatmentPlanId())).findAny()
								.orElse(null);
						p.setId(old.getId());
						p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						p.setCreatedDate(old.getCreatedDate());
						l.add(p);
					});
					if (l.size() > 0)
						treatmentPlansRepository.saveAll(l);
				}

				logAfterFirstTimeDataEntryToTable(TreatmentPlans.class, bw, apptIdInES.size(), apptIdInDB.size(),
						String.join(",", apptIdInES.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
						String.join(",", apptIdInDB.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
			}
		} catch (Exception ex) {
			appenErrorToWriter(TreatmentPlanItems.class, bw, ex);
		}

	}

	/**
	 * 
	 * @param bw
	 * @param data
	 * @return Treatment Plan Id that needs to be deleted.
	 */
	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class, transactionManager = "repDbTransactionManager")
	public Set<Integer> deleteRelevantDataFromLocalDB(BufferedWriter bw, List<?> data) {

		Set<Integer> apptIdInDB = new HashSet<>();
		try {
			//
			Set<Integer> apptIdInES = new HashSet<>();
			((List<TreatmentPlans>) data).stream().map(TreatmentPlans::getTreatmentPlanId).forEach(apptIdInES::add);
			((List<TreatmentPlans>) data).stream().map(TreatmentPlans::getTreatmentPlanId).forEach(apptIdInDB::add);
			
			/*no deletion happens
			List<TreatmentPlans> inDB = treatmentPlansRepository.findAll();

			inDB.stream().map(TreatmentPlans::getTreatmentPlanId).forEach(apptIdInDB::add);
			apptIdInDB.removeAll(apptIdInES);
			if (apptIdInDB.size() > 0) {
				List<TreatmentPlans> del = new ArrayList<>();
				apptIdInDB.forEach(id -> {
					TreatmentPlans q = inDB.stream().filter(p -> (id.intValue() == p.getTreatmentPlanId().intValue()))
							.findAny().orElse(null);
					del.add(q);
				});
				if (del.size() > 0)
					treatmentPlansRepository.deleteAll(del);
			}
			logDeletedFromTable(TreatmentPlanItems.class, bw, apptIdInDB.size(),
					String.join(",", apptIdInDB.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
              */
		} catch (Exception ex) {
			appenErrorToWriter(TreatmentPlanItems.class, bw, ex);
		}

		return apptIdInDB;

	}

	//@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public void deleteRelevantDataFromColudDB(BufferedWriter bw, Set<Integer> data, Office office) {
       /*
		try {
			//
			List<TreatmentPlansReplica> inDB = treatmentPlansRepositoryRe.findByTreatmentPlanIdInAndOfficeId(data,
					office.getUuid());

			if (data.size() > 0) {
				treatmentPlansRepositoryRe.deleteAll(inDB);
				logDeletedFromTable(TreatmentPlanItemsReplica.class, bw, data.size(),
						String.join(",", data.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
			}
		} catch (Exception ex) {
			appenErrorToWriter(TreatmentPlanItems.class, bw, ex);
		}
     */
	}
    
}

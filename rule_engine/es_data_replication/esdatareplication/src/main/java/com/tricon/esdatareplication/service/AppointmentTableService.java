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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tricon.esdatareplication.dao.repdb.AppointmentRepository;
import com.tricon.esdatareplication.dao.repdb.ESTableRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.AppointmentRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.Appointment;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Patient;
import com.tricon.esdatareplication.entity.repdb.Transactions;
import com.tricon.esdatareplication.entity.ruleenginedb.AppointmentReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class AppointmentTableService  extends CommonTableService{

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private AppointmentRepositoryRe appointmentRepositoryRe;
	
	@Autowired
	private ESTableRepository estableRepository;


	@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		try {
			List<Appointment> pL = appointmentRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
			List<AppointmentReplica> repList = new ArrayList<>();
			StringBuilder bu = new StringBuilder();
			pL.forEach(x -> {
				x.setOfficeId(office.getUuid());
				AppointmentReplica rep = new AppointmentReplica();
				BeanUtils.copyProperties(x, rep);
				bu.append(rep.getAppointmentId() + ",");
				rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				repList.add(rep);
			});
			// new repository for cloud.. and save data...
			Set<Integer> apptIdInES = new HashSet<>();
			Set<Integer> apptIdInDB = new HashSet<>();
			((List<AppointmentReplica>) repList).stream().map(AppointmentReplica::getAppointmentId).forEach(apptIdInES::add);
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<AppointmentReplica> inDB = appointmentRepositoryRe.findByAppointmentIdInAndOfficeId(apptIdInES,office.getUuid());
			inDB.stream().map(AppointmentReplica::getAppointmentId).forEach(apptIdInDB::add);
			apptIdInES.removeAll(apptIdInDB);// TranNum that are not in Local DB
			if (apptIdInES.size() > 0) {
				
				apptIdInES.forEach(id -> {
					AppointmentReplica q=((List<AppointmentReplica>) repList).stream()
					.filter(p -> id.intValue()==p.getAppointmentId().intValue()).findAny().orElse(null);
					q.setId(null);
					appointmentRepositoryRe.save(q);
				});
			}
			apptIdInDB.removeAll(apptIdInES);// TranNum id that are there in Local DB we need to update.
			if (apptIdInDB.size() > 0) {
				apptIdInDB.forEach(id -> {
					AppointmentReplica p = ((List<AppointmentReplica>) repList).stream().filter(dp -> id.intValue()==dp.getAppointmentId().intValue())
							.findAny().orElse(null);

					AppointmentReplica old = inDB.stream().filter(ind -> id.intValue()==ind.getAppointmentId().intValue()).findAny()
							.orElse(null);
					if (p!=null && old!=null) {
						if (old!=null) {
							p.setId(old.getId());
							p.setCreatedDate(old.getCreatedDate());
						}
					    p.setMovedToCloud(1);
					    
					    appointmentRepositoryRe.save(p);
					}
				});

			}
			appendLoggerToWriter(TransactionsReplica.class, bw,
					Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
			appendLoggerToWriter(TransactionsReplica.class, bw, bu.toString(), true);
			pL.forEach(x -> {
				x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			});
			appointmentRepository.saveAll(pL);
			es.setRecordsInsertedLastIteration(pL.size());
			es.setUpdatedDate(new Date());
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

	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {

		try {
			if (!checkExisting) {
				appointmentRepository.saveAll((List<Appointment>) data);
				logFirstTimeDataEntryToTable(Appointment.class, bw, data.size());
			} else {
				//
				Set<Integer> apptIdInES = new HashSet<>();
				Set<Integer> apptIdInDB = new HashSet<>();
				((List<Appointment>) data).stream().map(Appointment::getAppointmentId).forEach(apptIdInES::add);
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<Appointment> inDB = appointmentRepository.findByAppointmentIdIn(apptIdInES);
				inDB.stream().map(Appointment::getAppointmentId).forEach(apptIdInDB::add);

				apptIdInES.removeAll(apptIdInDB);// AppointmentId that are not in Local DB
				if (apptIdInES.size() > 0) {
					apptIdInES.forEach(id -> {
						appointmentRepository.save(((List<Appointment>) data).stream()
								.filter(p -> id.equals(p.getAppointmentId())).findAny().orElse(null));
					});
				}
				apptIdInDB.removeAll(apptIdInES);// Patient id that are there in Local DB we need to update.
				if (apptIdInDB.size() > 0) {
					apptIdInDB.forEach(id -> {
						Appointment p = ((List<Appointment>) data).stream()
								.filter(dp -> id.equals(dp.getAppointmentId())).findAny().orElse(null);

						Appointment old = inDB.stream().filter(ind -> id.equals(ind.getAppointmentId())).findAny()
								.orElse(null);
						p.setId(old.getId());
						p.setMovedToCloud(0);
						p.setCreatedDate(old.getCreatedDate());
						appointmentRepository.save(p);
					});
				}
				logAfterFirstTimeDataEntryToTable(Appointment.class, bw, apptIdInES.size(), apptIdInDB.size(),
						String.join(",", apptIdInES.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
						String.join(",", apptIdInDB.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
			}

		} catch (

		Exception ex) {
			appenErrorToWriter(Appointment.class, bw, ex);
		}
	}
}

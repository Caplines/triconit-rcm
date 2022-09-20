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

import com.tricon.esdatareplication.dao.repdb.AppointmentRepository;
import com.tricon.esdatareplication.dao.repdb.ESTableRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.AppointmentRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.Appointment;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Patient;
import com.tricon.esdatareplication.entity.ruleenginedb.AppointmentReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class AppointmentTableService extends CommonTableService {

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private AppointmentRepositoryRe appointmentRepositoryRe;

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
				List<Appointment> pL = appointmentRepository
						.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO, prepairPage);
				if (pL.size() == 0)
					break;
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
				((List<AppointmentReplica>) repList).stream().map(AppointmentReplica::getAppointmentId)
						.forEach(apptIdInES::add);
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<AppointmentReplica> inDB = appointmentRepositoryRe.findByAppointmentIdInAndOfficeId(apptIdInES,
						office.getUuid());
				inDB.stream().map(AppointmentReplica::getAppointmentId).forEach(apptIdInDB::add);
				apptIdInES.removeAll(apptIdInDB);// TranNum that are not in Local DB
				if (apptIdInES.size() > 0) {
					List<AppointmentReplica> l = new ArrayList<>();
					apptIdInES.forEach(id -> {
						AppointmentReplica q = ((List<AppointmentReplica>) repList).stream()
								.filter(p -> id.intValue() == p.getAppointmentId().intValue()).findAny().orElse(null);
						q.setId(null);
						q.setOfficeId(office.getUuid());
						l.add(q);
					});
					appointmentRepositoryRe.saveAllAndFlush(l);
				}
				apptIdInDB.removeAll(apptIdInES);// TranNum id that are there in Local DB we need to update.
				if (apptIdInDB.size() > 0) {
					List<AppointmentReplica> l = new ArrayList<>();
					apptIdInDB.forEach(id -> {
						AppointmentReplica p = ((List<AppointmentReplica>) repList).stream()
								.filter(dp -> id.intValue() == dp.getAppointmentId().intValue()).findAny().orElse(null);

						AppointmentReplica old = inDB.stream()
								.filter(ind -> id.intValue() == ind.getAppointmentId().intValue()).findAny()
								.orElse(null);
						if (p != null && old != null) {
							if (old != null) {
								p.setId(old.getId());
								p.setCreatedDate(old.getCreatedDate());
							}
							p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
							p.setOfficeId(office.getUuid());
							l.add(p);
						}
					});
					appointmentRepositoryRe.saveAllAndFlush(l);
				}
				appendLoggerToWriter(AppointmentReplica.class, bw,
						Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
				appendLoggerToWriter(AppointmentReplica.class, bw, bu.toString(), true);
				pL.forEach(x -> {
					x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				});
				appointmentRepository.saveAll(pL);
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
			appendLoggerToWriter(Appointment.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(Appointment.class, bw, ex);
		}
		return es;

	}

	@Transactional(rollbackFor = Exception.class, transactionManager = "repDbTransactionManager")
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
					List<Appointment> l = new ArrayList<>();
					apptIdInES.forEach(id -> {
						Appointment q = ((List<Appointment>) data).stream().filter(p -> id.equals(p.getAppointmentId()))
								.findAny().orElse(null);
						q.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						q.setId(null);
						l.add(q);
					});
					appointmentRepository.saveAll(l);
				}
				apptIdInDB.removeAll(apptIdInES);// AppointmentId id that are there in Local DB we need to update.
				if (apptIdInDB.size() > 0) {
					List<Appointment> l = new ArrayList<>();
					apptIdInDB.forEach(id -> {
						Appointment p = ((List<Appointment>) data).stream()
								.filter(dp -> id.equals(dp.getAppointmentId())).findAny().orElse(null);

						Appointment old = inDB.stream().filter(ind -> id.equals(ind.getAppointmentId())).findAny()
								.orElse(null);
						p.setId(old.getId());
						p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						p.setCreatedDate(old.getCreatedDate());
						l.add(p);
					});
					appointmentRepository.saveAll(l);
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
	
	
	/*
	 * 
	 * SELECT a.appointment_id+1 AS st, MIN(b.appointment_id) - 1 AS en
    FROM PPM.appointment  as a, PPM.appointment as  b
    WHERE a.appointment_id < b.appointment_id    GROUP BY a.appointment_id
    HAVING st < MIN(b.appointment_id)
    
	 */
	@Transactional(rollbackFor = Exception.class, transactionManager = "repDbTransactionManager")
	public void deleteDataToLocalDB(BufferedWriter bw, List<?> data) {
		try {
		for (String values:(List<String>)data) {
			
			String [] loopTo= values.split(",");
			for (int i=Integer.parseInt(loopTo[0]);i<=Integer.parseInt(loopTo[1]);i++) {
				//delete me i == appointment Id 
				//System.out.println("-----"+values);
				Appointment app=appointmentRepository.findByAppointmentId(i);
				if (app!=null) {
					appointmentRepository.delete(app);
					appendLoggerToWriter(Appointment.class, bw,
							Constants.TABLE_DATA_DELETION_PROCESS + ": Delete Appointment:" + app.getAppointmentId(), true);

					}

				}

			}
		} catch (Exception ex) {
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			appendLoggerToWriter(Appointment.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(Appointment.class, bw, ex);
		}
	}

	public void deleteDataToColudDB(BufferedWriter bw, List<?> data, Office office) {
		try {
			for (String values : (List<String>) data) {
				String[] loopTo = values.split(",");
				for (int i = Integer.parseInt(loopTo[0]); i <= Integer.parseInt(loopTo[1]); i++) {
					// delete me i == appointment Id
					//System.out.println("-----" + values);
					AppointmentReplica app = appointmentRepositoryRe.findByAppointmentIdAndOfficeId(i,
							office.getUuid());
					if (app != null) {
						 appointmentRepositoryRe.delete(app);
						appendLoggerToWriter(AppointmentReplica.class, bw,	Constants.TABLE_DATA_DELETION_PROCESS + ": Delete Appointment:" + app.getAppointmentId(), true);
					
				}
				
			}
			
		}
	}catch(Exception ex ) {
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		appendLoggerToWriter(AppointmentReplica.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
		appenErrorToWriter(AppointmentReplica.class, bw, ex);
	}

  }
}

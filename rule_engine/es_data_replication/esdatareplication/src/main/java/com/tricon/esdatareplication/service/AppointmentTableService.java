package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tricon.esdatareplication.dao.repdb.AppointmentRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.AppointmentRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.Appointment;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Patient;
import com.tricon.esdatareplication.entity.ruleenginedb.AppointmentReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.PatientReplica;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class AppointmentTableService {

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private AppointmentRepositoryRe appointmentRepositoryRe;

	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		List<Appointment> p = appointmentRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
		List<AppointmentReplica> repList = new ArrayList<>();
		p.forEach(x -> {
			x.setOfficeId(office.getUuid());
			AppointmentReplica rep = new AppointmentReplica();
			BeanUtils.copyProperties(x, rep);
			rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			repList.add(rep);
		});
		// new repository for cloud.. and save data...
		appointmentRepositoryRe.saveAll(repList);
		p.forEach(x -> {
			x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
		});
		appointmentRepository.saveAll(p);
		es.setRecordsInsertedLastIteration(p.size());
		return es;

	}
	
	public void saveDataToLocalDB(List<?> data, boolean checkExisting) {
		
		if (!checkExisting)
			appointmentRepository.saveAll((List<Appointment>) data);
		else {
			//
			Set<String> apptIdInES = new HashSet<>();
			Set<String> apptIdInDB = new HashSet<>();
			((List<Appointment>) data).stream().map(Appointment::getAppointmentId).forEach(apptIdInES::add);
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<Appointment> inDB = appointmentRepository.findByAppointmentIdIn(apptIdInES);
			inDB.stream().map(Appointment::getAppointmentId).forEach(apptIdInDB::add);

			apptIdInES.removeAll(apptIdInDB);// Patientid that are not in Local DB
			if (apptIdInES.size() > 0) {
				apptIdInES.forEach(id -> {
					appointmentRepository.save(((List<Appointment>) data).stream().filter(p -> id.equals(p.getAppointmentId()))
							.findAny().orElse(null));
				});
			}
			apptIdInDB.removeAll(apptIdInES);// Patient id that are there in Local DB we need to update.
			if (apptIdInDB.size() > 0) {
				apptIdInDB.forEach(id -> {
					Appointment p = ((List<Appointment>) data).stream().filter(dp -> id.equals(dp.getAppointmentId())).findAny()
							.orElse(null);

					Appointment old = inDB.stream().filter(ind -> id.equals(ind.getAppointmentId())).findAny().orElse(null);
					p.setId(old.getId());
					p.setMovedToCloud(0);
					p.setCreatedDate(old.getCreatedDate());
					appointmentRepository.save(p);
				});
			}
		}

		
	}
}

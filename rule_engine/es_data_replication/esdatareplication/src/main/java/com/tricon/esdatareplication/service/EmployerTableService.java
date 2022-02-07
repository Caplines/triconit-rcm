package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tricon.esdatareplication.dao.repdb.ESTableRepository;
import com.tricon.esdatareplication.dao.repdb.EmployerRespository;
import com.tricon.esdatareplication.dao.ruleenginedb.EmployerRespositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Employer;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.ruleenginedb.EmployerReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class EmployerTableService extends CommonTableService {

	@Autowired
	private EmployerRespository employerRespository;

	@Autowired
	private EmployerRespositoryRe employerRespositoryRe;

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
				List<Employer> pL = employerRespository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO,
						prepairPage);
				if (pL.size()==0) break;
				List<EmployerReplica> repList = new ArrayList<>();
				StringBuilder bu = new StringBuilder();
				pL.forEach(x -> {
					x.setOfficeId(office.getUuid());
					EmployerReplica rep = new EmployerReplica();
					BeanUtils.copyProperties(x, rep);
					bu.append(rep.getEmployerId() + ",");
					rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
					repList.add(rep);
				});
				// new repository for cloud.. and save data...
				Set<Integer> apptIdInES = new HashSet<>();
				Set<Integer> apptIdInDB = new HashSet<>();
				((List<EmployerReplica>) repList).stream().map(EmployerReplica::getEmployerId).forEach(apptIdInES::add);
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<EmployerReplica> inDB = employerRespositoryRe.findByEmployerIdInAndOfficeId(apptIdInES,
						office.getUuid());
				inDB.stream().map(EmployerReplica::getEmployerId).forEach(apptIdInDB::add);
				apptIdInES.removeAll(apptIdInDB);// TranNum that are not in Local DB
				if (apptIdInES.size() > 0) {
					List<EmployerReplica> l = new ArrayList<>();
					apptIdInES.forEach(id -> {
						EmployerReplica q = ((List<EmployerReplica>) repList).stream()
								.filter(p -> id.intValue() == p.getEmployerId().intValue()).findAny().orElse(null);
						q.setId(null);
						l.add(q);
					});
					if (l.size() > 0)
						employerRespositoryRe.saveAll(l);
				}
				apptIdInDB.removeAll(apptIdInES);// EmployerId that are there in Local DB we need to update.
				if (apptIdInDB.size() > 0) {
					List<EmployerReplica> l = new ArrayList<>();
					apptIdInDB.forEach(id -> {
						EmployerReplica p = ((List<EmployerReplica>) repList).stream()
								.filter(dp -> id.intValue() == dp.getEmployerId().intValue()).findAny().orElse(null);

						EmployerReplica old = inDB.stream()
								.filter(ind -> id.intValue() == ind.getEmployerId().intValue()).findAny().orElse(null);
						if (p != null && old != null) {
							if (old != null) {
								p.setId(old.getId());
								p.setCreatedDate(old.getCreatedDate());
							}
							p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);

							l.add(p);
						}
					});
					if (l.size() > 0)
						employerRespositoryRe.saveAll(l);
				}
				appendLoggerToWriter(TransactionsReplica.class, bw,
						Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
				appendLoggerToWriter(TransactionsReplica.class, bw, bu.toString(), true);
				pL.forEach(x -> {
					x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				});
				employerRespository.saveAll(pL);
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
			appendLoggerToWriter(Employer.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(Employer.class, bw, ex);
		}
		return es;

	}

	@Transactional(rollbackFor = Exception.class, transactionManager = "repDbTransactionManager")
	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {

		if (!checkExisting)
			employerRespository.saveAll((List<Employer>) data);
		else {
			//
			Set<Integer> apptIdInES = new HashSet<>();
			Set<Integer> apptIdInDB = new HashSet<>();
			if (data != null)
				((List<Employer>) data).stream().map(Employer::getEmployerId).forEach(apptIdInES::add);
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<Employer> inDB = employerRespository.findByEmployerIdIn(apptIdInES);
			inDB.stream().map(Employer::getEmployerId).forEach(apptIdInDB::add);

			apptIdInES.removeAll(apptIdInDB);// EmployerId that are not in Local DB
			if (apptIdInES.size() > 0) {
				List<Employer> l = new ArrayList<>();
				apptIdInES.forEach(id -> {
					Employer q = ((List<Employer>) data).stream().filter(p -> id.equals(p.getEmployerId())).findAny()
							.orElse(null);
					q.setMovedToCloud(DataStatus.StatusEnum.LOCAL_DATA_UPLOADED.NO);
					q.setId(null);
					l.add(q);
				});

				if (l.size() > 0)
					employerRespository.saveAll(l);
			}
			apptIdInDB.removeAll(apptIdInES);// EmployerId that are there in Local DB we need to update.
			if (apptIdInDB.size() > 0) {
				List<Employer> l = new ArrayList<>();
				apptIdInDB.forEach(id -> {
					Employer p = ((List<Employer>) data).stream().filter(dp -> id.equals(dp.getEmployerId())).findAny()
							.orElse(null);

					Employer old = inDB.stream().filter(ind -> id.equals(ind.getEmployerId())).findAny().orElse(null);
					p.setId(old.getId());
					p.setMovedToCloud(DataStatus.StatusEnum.LOCAL_DATA_UPLOADED.NO);
					p.setCreatedDate(old.getCreatedDate());
					l.add(p);
				});
				if (l.size() > 0)
					employerRespository.saveAll(l);
			}
		}

	}

}

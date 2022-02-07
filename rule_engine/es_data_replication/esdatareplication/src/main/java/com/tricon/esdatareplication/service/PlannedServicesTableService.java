
package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
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

import com.tricon.esdatareplication.dao.repdb.ESTableRepository;
import com.tricon.esdatareplication.dao.repdb.PlannedServicesRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.PlannedServicesRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.PlannedServices;
import com.tricon.esdatareplication.entity.repdb.TreatmentPlanItems;
import com.tricon.esdatareplication.entity.ruleenginedb.PlannedServicesReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class PlannedServicesTableService extends CommonTableService {

	@Autowired
	private PlannedServicesRepository plannedServicesRepository;

	@Autowired
	private PlannedServicesRepositoryRe plannedServicesRepositoryRe;

	@Autowired
	private ESTableRepository estableRepository;
	
	@Autowired
	ReplicationService replicationService;

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
				List<PlannedServices> pL = plannedServicesRepository
						.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO, prepairPage);
				if (pL.size() == 0)
					break;
				List<PlannedServicesReplica> repList = new ArrayList<>();
				StringBuilder bu = new StringBuilder();
				pL.forEach(x -> {
					x.setOfficeId(office.getUuid());
					PlannedServicesReplica rep = new PlannedServicesReplica();
					BeanUtils.copyProperties(x, rep);
					bu.append(rep.getLineNumber() + ",");
					rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
					repList.add(rep);
				});
				// new repository for cloud.. and save data...
				Set<String> apptIdInES = new HashSet<>();
				// Set<String> apptIdInDB = new HashSet<>();

				Set<Integer> apptIdInESSecond = new HashSet<>();

				Set<String> apptIdInES1 = new HashSet<>();
				Set<String> apptIdInDB1 = new HashSet<>();

				((List<PlannedServicesReplica>) repList).stream().map(PlannedServicesReplica::getPatientId)
						.forEach(apptIdInES::add);

				for (PlannedServicesReplica r : repList) {
					apptIdInES1.add(r.getPatientId() + "-" + r.getLineNumber());
				}

				((List<PlannedServicesReplica>) repList).stream().map(PlannedServicesReplica::getLineNumber)
						.forEach(apptIdInESSecond::add);// check me

				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));

				List<PlannedServicesReplica> inDBExtra = plannedServicesRepositoryRe.findByPatientIdInAndOfficeId(apptIdInES,office.getUuid());

				Set<String> extraAll = new HashSet<>();
				for (PlannedServicesReplica k : inDBExtra) {
					extraAll.add(k.getPatientId() + "-" + k.getLineNumber());
					apptIdInDB1.add(k.getPatientId() + "-" +k.getLineNumber());
				}
                /*
				List<PlannedServicesReplica> inDB = plannedServicesRepositoryRe
						.findByPatientIdInAndOfficeIdAndLineNumberIn(apptIdInES, office.getUuid(), apptIdInESSecond);
                
				for (PlannedServicesReplica r : inDB) {
					apptIdInDB1.add(r.getPatientId() + "-" + r.getLineNumber());
				}
                */ 
				// Delete Data that is there in Cloud
                extraAll.removeAll(apptIdInES1);

				if (extraAll.size() > 0) {
					List<PlannedServicesReplica> del = new ArrayList<>();
					extraAll.forEach(id -> {
						PlannedServicesReplica q = inDBExtra.stream()
								.filter(p -> (id.split("-")[0].equals(p.getPatientId())
										&& Integer.parseInt(id.split("-")[1]) == p.getLineNumber().intValue()))
								.findAny().orElse(null);
						del.add(q);
					});
					if (del.size() > 0) {
					//	plannedServicesRepositoryRe.deleteAll(del);//DO NOT DELETE HERE
					}
				}

				// inDB.stream().map( c => {c. }).forEach(apptIdInDB::add);
				// inDB.stream().map( PlannedServicesReplica).forEach(apptIdInDB::add);
				apptIdInES1.removeAll(apptIdInDB1);// Data that are not in Local DB
				if (apptIdInES1.size() > 0) {
					List<PlannedServicesReplica> l = new ArrayList<>();
					apptIdInES1.forEach(id -> {
						PlannedServicesReplica q = ((List<PlannedServicesReplica>) repList).stream()
								.filter(p -> (id.split("-")[0].equals(p.getPatientId())
										&& Integer.parseInt(id.split("-")[1]) == p.getLineNumber()))
								.findAny().orElse(null);
						q.setId(null);
						l.add(q);
					});
					if (l.size() > 0)
						plannedServicesRepositoryRe.saveAllAndFlush(l);
				}
				apptIdInDB1.removeAll(apptIdInES1);// Data that are there in Local DB we need to update.
				if (apptIdInDB1.size() > 0) {
					List<PlannedServicesReplica> l = new ArrayList<>();
					apptIdInDB1.forEach(id -> {
						PlannedServicesReplica p = ((List<PlannedServicesReplica>) repList).stream()
								.filter(dp -> (id.split("-")[0].equals(dp.getPatientId())
										&& Integer.parseInt(id.split("-")[1]) == dp.getLineNumber()))
								.findAny().orElse(null);

						PlannedServicesReplica old = inDBExtra.stream()
								.filter(ind -> (id.split("-")[0].equals(ind.getPatientId())
										&& Integer.parseInt(id.split("-")[1]) == ind.getLineNumber()))
								.findAny().orElse(null);
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
						plannedServicesRepositoryRe.saveAllAndFlush(l);
				}
				appendLoggerToWriter(PlannedServicesReplica.class, bw,
						Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
				appendLoggerToWriter(PlannedServicesReplica.class, bw, bu.toString(), true);
				pL.forEach(x -> {
					x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				});
				plannedServicesRepository.saveAll(pL);
				localCt = localCt + pL.size();
			}
			es.setRecordsInsertedLastIteration(localCt);
			//es.setUpdatedDate(new Date());
			estableRepository.save(es);
			
			//Delete Data
			try {
				while (true) {
					Pageable prepairPage = PageRequest.of(0, batchSize);// 0,50
					List<PlannedServices> pL = plannedServicesRepository
							.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS_DEL.YES, prepairPage);
					
					if (pL.size()>0) {
						
						Set<String> apptIdInES = new HashSet<>();
						 Set<String> apptIdInDB = new HashSet<>();

						//Set<Integer> apptIdInESSecond = new HashSet<>();

						for (PlannedServices r : pL) {
							apptIdInDB.add(r.getPatientId() + "-" + r.getLineNumber());
							//apptIdInESSecond.add(r.getLineNumber());
							apptIdInES.add(r.getPatientId());
						}
						
						List<PlannedServicesReplica> inDBExtra = plannedServicesRepositoryRe.findByPatientIdInAndOfficeId(apptIdInES,office.getUuid());
                         
                        apptIdInDB.forEach(id -> {
                        	PlannedServicesReplica p = inDBExtra.stream()
    								.filter(dp -> (id.split("-")[0].equals(dp.getPatientId())
    										&& Integer.parseInt(id.split("-")[1]) == dp.getLineNumber()))
    								.findAny().orElse(null);
                        	if (p!=null)plannedServicesRepositoryRe.delete(p);
						});
						
						plannedServicesRepository.deleteAll(pL);
						
					}else break;
				}
				
			}catch(Exception ex) {
				es.setRecordsInsertedLastIteration(0);
				StringWriter errors = new StringWriter();
				ex.printStackTrace(new PrintWriter(errors));
				es.setLastIssueDetail(errors.toString());
				appendLoggerToWriter(PlannedServicesReplica.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
				appenErrorToWriter(PlannedServicesReplica.class, bw, ex);
			}
		} catch (Exception ex) {
			es.setRecordsInsertedLastIteration(0);
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			es.setLastIssueDetail(errors.toString());
			appendLoggerToWriter(PlannedServicesReplica.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(PlannedServicesReplica.class, bw, ex);
		}
		return es;

	}

	@Transactional(rollbackFor = Exception.class, transactionManager = "repDbTransactionManager")
	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {
		try {
			if (!checkExisting) {
				plannedServicesRepository.saveAll((List<PlannedServices>) data);
				logFirstTimeDataEntryToTable(PlannedServices.class, bw, data.size());
			} else {
				//
				// new repository for cloud.. and save data...
				Set<String> apptIdInES = new HashSet<>();
				// Set<String> apptIdInDB = new HashSet<>();

				Set<Integer> apptIdInESSecond = new HashSet<>();

				Set<String> apptIdInES1 = new HashSet<>();
				Set<String> apptIdInDB1 = new HashSet<>();

				if (data != null) {
					((List<PlannedServices>) (List<PlannedServices>) data).stream().map(PlannedServices::getPatientId)
							.forEach(apptIdInES::add);

					for (PlannedServices r : (List<PlannedServices>) data) {
						apptIdInES1.add(r.getPatientId() + "-" + r.getLineNumber());
					}
					((List<PlannedServices>) (List<PlannedServices>) data).stream().map(PlannedServices::getLineNumber)
							.forEach(apptIdInESSecond::add);// check me
				}
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<PlannedServices> inDBExtra = plannedServicesRepository.findByPatientIdIn(apptIdInES);

				Set<String> extraAll = new HashSet<>();
				for (PlannedServices k : inDBExtra) {
					extraAll.add(k.getPatientId() + "-" + k.getLineNumber());
					apptIdInDB1.add(k.getPatientId() + "-" + k.getLineNumber());
				}
                /* 
				List<PlannedServices> inDB = plannedServicesRepository
						.findByPatientIdInAndLineNumberInOrderByDatePlanned(apptIdInES, apptIdInESSecond);

				for (PlannedServices r : inDB) {
					apptIdInDB1.add(r.getPatientId() + "-" + r.getLineNumber());
				}
                */
				// Delete Data that is there in Cloud
				// we need to delete in case TP items are deleted from local when TP is edited
				extraAll.removeAll(apptIdInES1);

				if (extraAll.size() > 0) {
					List<PlannedServices> del = new ArrayList<>();
					extraAll.forEach(id -> {
						PlannedServices q = inDBExtra.stream()
								.filter(p -> (id.split("-")[0].equals(p.getPatientId())
										&& Integer.parseInt(id.split("-")[1]) == p.getLineNumber().intValue()))
								.findAny().orElse(null);
						
						ESTable table= new ESTable();
						table.setTableName(Constants.TABLE_PLANNED_SERVICES);
						
						List<?> dataES= replicationService.fetchDataFromLocalDeletionES(table, q.getPatientId(),
								q.getLineNumber(), null,bw);
						
						if (dataES!=null && dataES.size()==0) {
							q.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS_DEL.YES);
							del.add(q);
						}
						
					});
					if (del.size() > 0) {
						plannedServicesRepository.saveAll(del);
					}
				}

				// inDB.stream().map( c => {c. }).forEach(apptIdInDB::add);
				// inDB.stream().map( PlannedServicesReplica).forEach(apptIdInDB::add);
				apptIdInES1.removeAll(apptIdInDB1);// Data that are not in Local DB

				if (apptIdInES1.size() > 0) {
					List<PlannedServices> l = new ArrayList<>();
					apptIdInES1.forEach(id -> {
						PlannedServices q = ((List<PlannedServices>) (List<PlannedServices>) data).stream()
								.filter(p -> (id.split("-")[0].equals(p.getPatientId())
										&& Integer.parseInt(id.split("-")[1]) == p.getLineNumber()))
								.findAny().orElse(null);
						q.setId(null);
						q.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						l.add(q);
					});
					if (l.size() > 0)
						plannedServicesRepository.saveAll(l);
				}
				apptIdInDB1.removeAll(apptIdInES1);// Data that are there in Local DB we need to update.
				if (apptIdInDB1.size() > 0) {
					List<PlannedServices> l = new ArrayList<>();
					apptIdInDB1.forEach(id -> {
						PlannedServices p = ((List<PlannedServices>) (List<PlannedServices>) data).stream()
								.filter(dp -> (id.split("-")[0].equals(dp.getPatientId())
										&& Integer.parseInt(id.split("-")[1]) == dp.getLineNumber()))
								.findAny().orElse(null);

						PlannedServices old = inDBExtra.stream()
								.filter(ind -> (id.split("-")[0].equals(ind.getPatientId())
										&& Integer.parseInt(id.split("-")[1]) == ind.getLineNumber()))
								.findAny().orElse(null);
						if (p != null && old != null) {
							if (old != null) {
								p.setId(old.getId());
								p.setCreatedDate(old.getCreatedDate());
							}
							p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
							// if (p.getId()==1) p.setMovedToCloud(111);
							// else p.setMovedToCloud(22);
							// System.out.println("--->"+p.getApptGroup()+"-"+p.getDatePlanned()+"-->>"+p.getId());
							l.add(p);
						}
					});
					if (l.size() > 0)
						plannedServicesRepository.saveAll(l);
				}
				logAfterFirstTimeDataEntryToTable(PlannedServices.class, bw, apptIdInES.size(), apptIdInDB1.size(),
						String.join(",", apptIdInES.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
						String.join(",",
								apptIdInDB1.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
			}
		} catch (Exception ex) {
			appenErrorToWriter(PlannedServices.class, bw, ex);
		}
	}

	/**
	 * 
	 * @param bw
	 * @param data
	 * @return Treatment Plan Id that needs to be deleted.
	 */
	@Transactional(rollbackFor = Exception.class, transactionManager = "repDbTransactionManager")
	public List<PlannedServices> deleteRelevantDataFromLocalDB(BufferedWriter bw, List<TreatmentPlanItems> data) {

		// Set<Integer> apptIdInDB = new HashSet<>();
		//List<PlannedServices> inDB = new ArrayList<>();
		List<PlannedServices> inDB1=new ArrayList<>();
		try {
			//
			Set<Integer> apptIdInES = new HashSet<>();
			Set<String> apptIdInES1 = new HashSet<>();
			((List<TreatmentPlanItems>) data).stream().map(TreatmentPlanItems::getLineNumber).forEach(apptIdInES::add);
			((List<TreatmentPlanItems>) data).stream().map(TreatmentPlanItems::getPatientId).forEach(apptIdInES1::add);

			//inDB = plannedServicesRepository.findByPatientIdInAndLineNumberInOrderByDatePlanned(apptIdInES1,
			//		apptIdInES);
			Set<Integer> apptIdInDB = new HashSet<>();
			List<PlannedServices> inDB = plannedServicesRepository.findByPatientIdIn(apptIdInES1);
			inDB.stream().map(PlannedServices::getId).forEach(apptIdInDB::add);
			
			List<Integer> both= new ArrayList<>();
			((List<TreatmentPlanItems>) data).forEach(sd -> {
				inDB.forEach(d -> {
			          if (sd.getPatientId().equals(d.getPatientId()) && sd.getLineNumber().intValue()==d.getLineNumber().intValue()) {
			        	  both.add(d.getId());
			          }
				});    
			});
			apptIdInDB.removeAll(both);
			apptIdInDB.forEach(d -> {
				inDB.forEach(da -> {
				    if ( da.getId().intValue()==d.intValue()) {
				    	inDB1.add(da);
				    	plannedServicesRepository.delete(da);
				    }
				});     
			});
			//plannedServicesRepository.deleteAll(inDB);

			logDeletedFromTable(PlannedServices.class, bw, apptIdInDB.size(),
					String.join(",", data.stream().map(s -> String.valueOf(apptIdInDB)).collect(Collectors.toList())));

		} catch (Exception ex) {
			appenErrorToWriter(PlannedServices.class, bw, ex);
		}

		return inDB1;

	}

	////@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public void deleteRelevantDataFromColudDB(BufferedWriter bw, List<PlannedServices> data, Office office) {

		// Set<Integer> apptIdInDB = new HashSet<>();
		//List<PlannedServices> inDB = new ArrayList<>();
		try {
			//
			Set<Integer> apptIdInES = new HashSet<>();
			Set<String> apptIdInES1 = new HashSet<>();
			((List<PlannedServices>) data).stream().map(PlannedServices::getLineNumber).forEach(apptIdInES::add);
			((List<PlannedServices>) data).stream().map(PlannedServices::getPatientId).forEach(apptIdInES1::add);

			//inDB = plannedServicesRepository.findByPatientIdInAndLineNumberInOrderByDatePlanned(apptIdInES1,
			//		apptIdInES);
			Set<Integer> apptIdInDB = new HashSet<>();
			List<PlannedServicesReplica> inDB = plannedServicesRepositoryRe.findByPatientIdInAndOfficeId(apptIdInES1,office.getUuid());
			inDB.stream().map(PlannedServicesReplica::getId).forEach(apptIdInDB::add);
			
			List<Integer> both= new ArrayList<>();
			((List<PlannedServices>) data).forEach(sd -> {
				inDB.forEach(d -> {
			          if (sd.getPatientId().equals(d.getPatientId()) && sd.getLineNumber().intValue()==d.getLineNumber().intValue()) {
			        	  both.add(d.getId());
			          }
				});    
			});
			apptIdInDB.removeAll(both);
			apptIdInDB.forEach(d -> {
				inDB.forEach(da -> {
				    if ( da.getId().intValue()==d.intValue()) {
				    	plannedServicesRepositoryRe.delete(da);
				    }
				});     
			});
			//plannedServicesRepository.deleteAll(inDB);

			logDeletedFromTable(PlannedServices.class, bw, apptIdInDB.size(),
					String.join(",", data.stream().map(s -> String.valueOf(apptIdInDB)).collect(Collectors.toList())));

		} catch (Exception ex) {
			appenErrorToWriter(PlannedServices.class, bw, ex);
		}
	}

}

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

import com.tricon.esdatareplication.dao.repdb.ESTableRepository;
import com.tricon.esdatareplication.dao.repdb.TreatmentPlanItemsRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.TreatmentPlanItemsRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Patient;
import com.tricon.esdatareplication.entity.repdb.TreatmentPlanItems;
import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.TreatmentPlanItemsReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class TreatmentPlanItemsTableService extends CommonTableService {

	@Autowired
	private TreatmentPlanItemsRepository treatmentPlanItemsRepository;

	@Autowired
	private TreatmentPlanItemsRepositoryRe treatmentPlanItemsRepositoryRe;
	
	@Autowired
	private ESTableRepository estableRepository;


	@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		try {
			List<TreatmentPlanItems> pL = treatmentPlanItemsRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
			List<TreatmentPlanItemsReplica> repList = new ArrayList<>();
			StringBuilder bu = new StringBuilder();
			pL.forEach(x -> {
				x.setOfficeId(office.getUuid());
				TreatmentPlanItemsReplica rep = new TreatmentPlanItemsReplica();
				BeanUtils.copyProperties(x, rep);
				bu.append(rep.getTreatmentPlanId() + ",");
				rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				repList.add(rep);
			});
			// new repository for cloud.. and save data...
			Set<Integer> apptIdInES = new HashSet<>();
			//Set<Integer> apptIdInDB = new HashSet<>();
			
			Set<Integer> apptIdInESLI = new HashSet<>();
			
			Set<String> apptIdInEPat = new HashSet<>();
			
			((List<TreatmentPlanItemsReplica>) repList).stream().map(TreatmentPlanItemsReplica::getTreatmentPlanId).forEach(apptIdInES::add);
			((List<TreatmentPlanItemsReplica>) repList).stream().map(TreatmentPlanItemsReplica::getLineNumber).forEach(apptIdInESLI::add);
			((List<TreatmentPlanItemsReplica>) repList).stream().map(TreatmentPlanItemsReplica::getPatientId).forEach(apptIdInEPat::add);
				// or
			List<TreatmentPlanItemsReplica> inDBExtra = treatmentPlanItemsRepositoryRe.findByTreatmentPlanIdInAndPatientIdInAndOfficeId(
					                              apptIdInES,apptIdInEPat, office.getUuid());
			Set<String> extraAll = new HashSet<>();
			for(TreatmentPlanItemsReplica k:inDBExtra) {
				extraAll.add(k.getTreatmentPlanId()+"-"+k.getPatientId()+"-"+k.getLineNumber());
			}
                  
			
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<TreatmentPlanItemsReplica> inDB = treatmentPlanItemsRepositoryRe.findByTreatmentPlanIdInAndPatientIdInAndLineNumberInAndOfficeId(
					apptIdInES,apptIdInEPat,apptIdInESLI,office.getUuid());
			
			Set<String> apptIdInDB1 = new HashSet<>();
			Set<String> apptIdInES1 = new HashSet<>();
			
			//inDB.stream().map(TreatmentPlanItemsReplica::getTreatmentPlanId).forEach(apptIdInDB::add);
			for(TreatmentPlanItemsReplica c:repList) {
				apptIdInES1.add(c.getTreatmentPlanId()+"-"+c.getPatientId()+"-"+c.getLineNumber());
			}
			for(TreatmentPlanItemsReplica c:inDB) {
				apptIdInDB1.add(c.getTreatmentPlanId()+"-"+c.getPatientId()+"-"+c.getLineNumber());
			}
			System.out.println("removeAll"); 
			System.out.println(String.join(",", extraAll)); 
			System.out.println(String.join(",", apptIdInES1)); 
			
			
			
			//Delete Data that is there in Cloud
			//we need to delete in case TP items are deleted from local when TP is edited
			extraAll.removeAll(apptIdInES1);
			
           if (extraAll.size() > 0) {
			 List<TreatmentPlanItemsReplica> del= new ArrayList<>();	
        	   extraAll.forEach(id -> {
        		   TreatmentPlanItemsReplica q=inDBExtra.stream()
					.filter(p -> (
							 Integer.parseInt(id.split("-")[0])==p.getTreatmentPlanId().intValue() &&
							 id.split("-")[1].equals(p.getPatientId()) &&
							 Integer.parseInt(id.split("-")[2])==p.getLineNumber().intValue()
			                 )
							).findAny().orElse(null);
					del.add(q);
				});
        	 if (del.size()>0)treatmentPlanItemsRepositoryRe.deleteAll(del);
			}
			//End
			
			
			apptIdInES1.removeAll(apptIdInDB1);// Data that are not in Cloud DB
			if (apptIdInES1.size() > 0) {
				List<TreatmentPlanItemsReplica> l= new ArrayList<>();
				apptIdInES1.forEach(id -> {
					TreatmentPlanItemsReplica q=((List<TreatmentPlanItemsReplica>) repList).stream()
					.filter(p -> (
							 Integer.parseInt(id.split("-")[0])==p.getTreatmentPlanId().intValue() &&
							 id.split("-")[1].equals(p.getPatientId()) &&
							 Integer.parseInt(id.split("-")[2])==p.getLineNumber().intValue()
			                 )
							).findAny().orElse(null);
					q.setId(null);
					l.add(q);
				});
				if (l.size()>0)treatmentPlanItemsRepositoryRe.saveAll(l);
			}
			apptIdInDB1.removeAll(apptIdInES1);// id that are there in Local DB we need to update.
			if (apptIdInDB1.size() > 0) {
				List<TreatmentPlanItemsReplica> l = new ArrayList<>();
				apptIdInDB1.forEach(id -> {
					TreatmentPlanItemsReplica p = ((List<TreatmentPlanItemsReplica>) repList).stream().filter(
							dp -> (
									 Integer.parseInt(id.split("-")[0])==dp.getTreatmentPlanId().intValue() &&
									 id.split("-")[1].equals(dp.getPatientId()) &&
									 Integer.parseInt(id.split("-")[2])==dp.getLineNumber().intValue()
					                 )
									).findAny().orElse(null);

					TreatmentPlanItemsReplica old = inDB.stream().filter(
							ind -> (
									 Integer.parseInt(id.split("-")[0])==ind.getTreatmentPlanId().intValue() &&
									 id.split("-")[1].equals(ind.getPatientId()) &&
									 Integer.parseInt(id.split("-")[2])==ind.getLineNumber().intValue()
								 )
					            ).findAny().orElse(null);
					if (p!=null && old!=null) {
						if (old!=null) {
							p.setId(old.getId());
							p.setCreatedDate(old.getCreatedDate());
						}
					    p.setMovedToCloud(1);
					    
					    l.add(p);
					}
				});
				if (l.size()>0)treatmentPlanItemsRepositoryRe.saveAll(l);
			}
			appendLoggerToWriter(TransactionsReplica.class, bw,
					Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
			appendLoggerToWriter(TransactionsReplica.class, bw, bu.toString(), true);
			pL.forEach(x -> {
				x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			});
			treatmentPlanItemsRepository.saveAll(pL);
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

	@Transactional(rollbackFor = Exception.class, transactionManager = "repDbTransactionManager") 
	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {
		try {
			if (!checkExisting) {
				treatmentPlanItemsRepository.saveAll((List<TreatmentPlanItems>) data);
				logFirstTimeDataEntryToTable(TreatmentPlanItems.class, bw, data.size());
			} else {
				//
				// new repository for cloud.. and save data...
				Set<Integer> apptIdInES = new HashSet<>();
				Set<Integer> apptIdInDB = new HashSet<>();
				
				Set<Integer> apptIdInESLI = new HashSet<>();
				
				Set<String> apptIdInEPat = new HashSet<>();
				
				((List<TreatmentPlanItems>) data).stream().map(TreatmentPlanItems::getTreatmentPlanId).forEach(apptIdInES::add);
				((List<TreatmentPlanItems>) data).stream().map(TreatmentPlanItems::getLineNumber).forEach(apptIdInESLI::add);
				((List<TreatmentPlanItems>) data).stream().map(TreatmentPlanItems::getPatientId).forEach(apptIdInEPat::add);
					// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<TreatmentPlanItems> inDBExtra = treatmentPlanItemsRepository.findByTreatmentPlanIdInAndPatientIdIn(
                        apptIdInES,apptIdInEPat);
				Set<String> extraAll = new HashSet<>();
				for(TreatmentPlanItems k:inDBExtra) {
					extraAll.add(k.getTreatmentPlanId()+"-"+k.getPatientId()+"-"+k.getLineNumber());
				}
				
				

				//
				List<TreatmentPlanItems> inDB = treatmentPlanItemsRepository.findByTreatmentPlanIdInAndPatientIdInAndLineNumberIn(
						                                               apptIdInES,apptIdInEPat,apptIdInESLI);
				
				Set<String> apptIdInDB1 = new HashSet<>();
				Set<String> apptIdInES1 = new HashSet<>();
				
				//inDB.stream().map(TreatmentPlanItemsReplica::getTreatmentPlanId).forEach(apptIdInDB::add);
				for(TreatmentPlanItems c:(List<TreatmentPlanItems>) data) {
					apptIdInES1.add(c.getTreatmentPlanId()+"-"+c.getPatientId()+"-"+c.getLineNumber());
				}
				for(TreatmentPlanItems c:inDB) {
					apptIdInDB1.add(c.getTreatmentPlanId()+"-"+c.getPatientId()+"-"+c.getLineNumber());
				}
				
				//Delete Data that is there in Cloud
				//we need to delete in case TP items are deleted from local when TP is edited
				extraAll.removeAll(apptIdInES1);
				
               if (extraAll.size() > 0) {
				 List<TreatmentPlanItems> del= new ArrayList<>();	
            	   extraAll.forEach(id -> {
						TreatmentPlanItems q=inDBExtra.stream()
						.filter(p -> (
								 Integer.parseInt(id.split("-")[0])==p.getTreatmentPlanId().intValue() &&
								 id.split("-")[1].equals(p.getPatientId()) &&
								 Integer.parseInt(id.split("-")[2])==p.getLineNumber().intValue()
				                 )
								).findAny().orElse(null);
						del.add(q);
					});
            	 if (del.size()>0)treatmentPlanItemsRepository.deleteAll(del);
				}
				
				//End
				
				
				apptIdInES1.removeAll(apptIdInDB1);// Data that are not in Cloud DB
				if (apptIdInES1.size() > 0) {
					List<TreatmentPlanItems> l= new ArrayList<>();
					apptIdInES1.forEach(id -> {
						TreatmentPlanItems q=((List<TreatmentPlanItems>) (List<TreatmentPlanItems>) data).stream()
						.filter(p -> (
								 Integer.parseInt(id.split("-")[0])==p.getTreatmentPlanId().intValue() &&
								 id.split("-")[1].equals(p.getPatientId()) &&
								 Integer.parseInt(id.split("-")[2])==p.getLineNumber().intValue()
				                 )
								).findAny().orElse(null);
						l.add(q);
					});
					if (l.size()>0)treatmentPlanItemsRepository.saveAll(l);
				}
				apptIdInDB1.removeAll(apptIdInES1);// id that are there in Local DB we need to update.
				if (apptIdInDB1.size() > 0) {
					List<TreatmentPlanItems> l = new ArrayList<>();
					apptIdInDB1.forEach(id -> {
						TreatmentPlanItems p = ((List<TreatmentPlanItems>) (List<TreatmentPlanItems>) data).stream().filter(
								dp -> (
										 Integer.parseInt(id.split("-")[0])==dp.getTreatmentPlanId().intValue() &&
										 id.split("-")[1].equals(dp.getPatientId()) &&
										 Integer.parseInt(id.split("-")[2])==dp.getLineNumber().intValue()
						                 )
										).findAny().orElse(null);

						TreatmentPlanItems old = inDB.stream().filter(
								ind -> (
										 Integer.parseInt(id.split("-")[0])==ind.getTreatmentPlanId().intValue() &&
										 id.split("-")[1].equals(ind.getPatientId()) &&
										 Integer.parseInt(id.split("-")[2])==ind.getLineNumber().intValue()
									 )
						            ).findAny().orElse(null);
						if (p!=null && old!=null) {
							if (old!=null) {
								p.setId(old.getId());
								p.setCreatedDate(old.getCreatedDate());
							}
						    p.setMovedToCloud(0);
						    l.add(p);
						}
					});
					if (l.size()>0)treatmentPlanItemsRepository.saveAll(l);
				}
				logAfterFirstTimeDataEntryToTable(TreatmentPlanItems.class, bw, apptIdInES.size(), apptIdInDB.size(),
						String.join(",", apptIdInES.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
						String.join(",", apptIdInDB.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
			}
		} catch (Exception ex) {
			appenErrorToWriter(TreatmentPlanItems.class, bw, ex);
		}
	}
}

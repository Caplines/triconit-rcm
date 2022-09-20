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
import com.tricon.esdatareplication.dao.repdb.PayTypeRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.PayTypeRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.PayType;
import com.tricon.esdatareplication.entity.ruleenginedb.PayTypeReplica;

import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class PayTypeTableService extends CommonTableService{

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;
	
	@Autowired
	private PayTypeRepository payTypeRepository;

	@Autowired
	private PayTypeRepositoryRe payTypeRepositoryRe;

	@Autowired
	CommonTableService commonTableService;

	@Autowired
	private ESTableRepository estableRepository;
	
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		int localCt = 0;
		try {
			// Long count =
			// patientRepository.countByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
			// long totalPages = Double.valueOf(Math.ceil(count / (float)
			// batchSize)).longValue();
			boolean wentInLoopAtleastOnce=false;
			while (true) {
				Pageable prepairPage = PageRequest.of(0, batchSize);// 0,50
				List<PayType> pL = payTypeRepository
						.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO, prepairPage);
				if (pL.size() == 0)
					break;
				wentInLoopAtleastOnce=true;
				List<PayTypeReplica> repList = new ArrayList<>();
				StringBuilder bu = new StringBuilder();
				pL.forEach(x -> {
					x.setOfficeId(office.getUuid());
					PayTypeReplica rep = new PayTypeReplica();
					BeanUtils.copyProperties(x, rep);
					bu.append(rep.getPayTypeId() + ",");
					rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
					repList.add(rep);
				});
				// new repository for cloud.. and save data...
				Set<Integer> apptIdInES = new HashSet<>();
				// Set<String> apptIdInDB = new HashSet<>();

				Set<Integer> apptIdInESSecond = new HashSet<>();

				Set<Integer> apptIdInES1 = new HashSet<>();
				Set<Integer> apptIdInDB1 = new HashSet<>();

				((List<PayTypeReplica>) repList).stream().map(PayTypeReplica::getPayTypeId)
						.forEach(apptIdInES::add);
				List<PayTypeReplica> del1 = new ArrayList<>();
				PayTypeReplica ds=null;
				for (PayTypeReplica r : repList) {
					apptIdInES1.add(r.getPayTypeId());
					ds =payTypeRepositoryRe.findByPayTypeIdAndOfficeId(r.getPayTypeId(),office.getUuid());
					if (ds!=null) del1.add(ds);
				}
				if (del1.size()>0)payTypeRepositoryRe.deleteAll(del1);
				((List<PayTypeReplica>) repList).stream().map(PayTypeReplica::getPayTypeId)
						.forEach(apptIdInESSecond::add);// check me

				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));

				List<PayTypeReplica> inDBExtra = payTypeRepositoryRe.findByPayTypeIdInAndOfficeId(apptIdInES,office.getUuid());

				Set<Integer> extraAll = new HashSet<>();
				for (PayTypeReplica k : inDBExtra) {
					extraAll.add(k.getPayTypeId());
					apptIdInDB1.add(k.getPayTypeId());
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
					List<PayTypeReplica> del = new ArrayList<>();
					extraAll.forEach(id -> {
						PayTypeReplica q = inDBExtra.stream()
								.filter(p -> (id.intValue()==p.getPayTypeId()
										
												))
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
					
					
					
					
					List<PayTypeReplica> l = new ArrayList<>();
					apptIdInES1.forEach(id -> {
						PayTypeReplica q = ((List<PayTypeReplica>) repList).stream()
								.filter(p -> (id.intValue()==p.getPayTypeId()
										
												))
								.findAny().orElse(null);
						q.setId(null);
						q.setOfficeId(office.getUuid());
						l.add(q);
					});
					if (l.size() > 0)
						payTypeRepositoryRe.saveAllAndFlush(l);
				}
				
				
				apptIdInDB1.removeAll(apptIdInES1);// Data that are there in Local DB we need to update.
				if (apptIdInDB1.size() > 0) {
					
					
					List<PayTypeReplica> l = new ArrayList<>();
					apptIdInDB1.forEach(id -> {
						PayTypeReplica p = ((List<PayTypeReplica>) repList).stream()
								.filter(dp -> (id.intValue()==dp.getPayTypeId())
									)
								.findAny().orElse(null);

						PayTypeReplica old = inDBExtra.stream()
								.filter(ind ->  (id.intValue()==ind.getPayTypeId())
										)
									.findAny().orElse(null);
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
					if (l.size() > 0)
						payTypeRepositoryRe.saveAllAndFlush(l);
				}
				appendLoggerToWriter(PayTypeReplica.class, bw,
						Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
				appendLoggerToWriter(PayTypeReplica.class, bw, bu.toString(), true);
				pL.forEach(x -> {
					x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				});
				payTypeRepository.saveAll(pL);
				localCt = localCt + pL.size();
			}
			while (true) {
				Pageable prepairPage = PageRequest.of(0, batchSize);// 0,50
				List<PayType> pL = payTypeRepository
						.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS_DEL.YES, prepairPage);
				if (pL.size() == 0)
					break;
				List<PayTypeReplica> repList = new ArrayList<>();
				pL.forEach(x -> {
					x.setOfficeId(office.getUuid());
					PayTypeReplica rep = new PayTypeReplica();
					BeanUtils.copyProperties(x, rep);
					rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS_DEL.YES);
					repList.add(rep);
				});
				payTypeRepository.deleteAll(pL);
				payTypeRepositoryRe.deleteAll(repList);
				//localCt = localCt + pL.size();
			}
			//deleteOldData();
			//deleteOldDataRe();
			es.setRecordsInsertedLastIteration(localCt);
			//es.setUpdatedDate(new Date());
			estableRepository.save(es);
			
			//Delete Data
			try {
				while (true) {
					Pageable prepairPage = PageRequest.of(0, batchSize);// 0,50
					List<PayType> pL = payTypeRepository
							.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS_DEL.YES, prepairPage);
					
					if (pL.size()>0) {
						
						Set<Integer> apptIdInES = new HashSet<>();
						 Set<Integer> apptIdInDB = new HashSet<>();

						//Set<Integer> apptIdInESSecond = new HashSet<>();

						for (PayType r : pL) {
							apptIdInDB.add(r.getPayTypeId());
							//apptIdInESSecond.add(r.getLineNumber());
							apptIdInES.add(r.getPayTypeId());
						}
						
						List<PayTypeReplica> inDBExtra = payTypeRepositoryRe.findByPayTypeIdInAndOfficeId(apptIdInES,office.getUuid());
                         
                        apptIdInDB.forEach(id -> {
                        	PayTypeReplica p = inDBExtra.stream()
    								.filter(dp -> (id.intValue()==dp.getPayTypeId())
    										)
    								.findAny().orElse(null);
                        	if (p!=null)payTypeRepositoryRe.delete(p);
						});
						
                        payTypeRepository.deleteAll(pL);
						
					}else break;
				}
			if (wentInLoopAtleastOnce==false) {
				
				
			}
			}catch(Exception ex) {
				ex.printStackTrace();
				es.setRecordsInsertedLastIteration(0);
				StringWriter errors = new StringWriter();
				ex.printStackTrace(new PrintWriter(errors));
				es.setLastIssueDetail(errors.toString());
				appendLoggerToWriter(PayTypeTableService.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
				appenErrorToWriter(PayTypeTableService.class, bw, ex);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			es.setRecordsInsertedLastIteration(0);
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			es.setLastIssueDetail(errors.toString());
			appendLoggerToWriter(PayTypeTableService.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(PayTypeTableService.class, bw, ex);
		}
		return es;

	}
	
	//@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDBNotused(BufferedWriter bw, Office office, ESTable es) {
		List<PayType> p = payTypeRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
		List<PayTypeReplica> repList = new ArrayList<>();
		StringBuilder bu = new StringBuilder();
		try {
			p.forEach(x -> {
				x.setOfficeId(office.getUuid());
				PayTypeReplica rep = new PayTypeReplica();
				BeanUtils.copyProperties(x, rep);
				bu.append(rep.getPayTypeId() + ",");
				repList.add(rep);
			});
			// new repository for cloud.. and save data...
			payTypeRepositoryRe.saveAllAndFlush(repList);
			commonTableService.appendLoggerToWriter(PayTypeReplica.class, bw,
					Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
			commonTableService.appendLoggerToWriter(PayTypeReplica.class, bw, bu.toString(), true);

			p.forEach(x -> {

				x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			});
			payTypeRepository.saveAll(p);
			es.setRecordsInsertedLastIteration(p.size());
		} catch (Exception ex) {
			es.setRecordsInsertedLastIteration(0);
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			es.setLastIssueDetail(errors.toString());
			commonTableService.appendLoggerToWriter(PayType.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			commonTableService.appenErrorToWriter(PayType.class, bw, ex);
		}
		return es;

	}

	@Transactional(rollbackFor = Exception.class, transactionManager = "repDbTransactionManager")
	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {
		try {
			if (!checkExisting) {
			payTypeRepository.saveAll((List<PayType>) data);
			commonTableService.logFirstTimeDataEntryToTable(PayType.class, bw, data.size());
			}else {
				Set<Integer> apptIdInES = new HashSet<>();
				Set<Integer> apptIdInESSecond = new HashSet<>();

				Set<Integer> apptIdInES1 = new HashSet<>();
				Set<Integer> apptIdInDB1 = new HashSet<>();

				if (data != null) {
					((List<PayType>) (List<PayType>) data).stream().map(PayType::getPayTypeId)
							.forEach(apptIdInES::add);
					List<PayType> del1 = new ArrayList<>();
					PayType ds=null;
					for (PayType r : (List<PayType>) data) {
						apptIdInES1.add(r.getPayTypeId());
						ds =payTypeRepository.findByPayTypeId(r.getPayTypeId());
						if (ds!=null) {
							System.out.println("IN DELETION"+ds.getPayTypeId());
							del1.add(ds);
						}
					}
					if (del1.size()>0) {
						payTypeRepository.deleteAll(del1);
					}
					
					((List<PayType>) (List<PayType>) data).stream().map(PayType::getPayTypeId)
							.forEach(apptIdInESSecond::add);// check me
				}
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<PayType> inDBExtra = payTypeRepository.findByPayTypeIdIn(apptIdInES);

				Set<Integer> extraAll = new HashSet<>();
				for (PayType k : inDBExtra) {
					extraAll.add(k.getPayTypeId());
					apptIdInDB1.add(k.getPayTypeId());
				}
              extraAll.removeAll(apptIdInES1);
              Set<Integer> payIds= new HashSet<>();
				apptIdInES1.removeAll(apptIdInDB1);// Data that are not in Local DB
				
				
				try {
				if (apptIdInES1.size() > 0) {
					
					
					List<PayType> l = new ArrayList<>();
					apptIdInES1.forEach(id -> {
						PayType q = ((List<PayType>) (List<PayType>) data).stream()
								.filter(p -> (id.intValue()==p.getPayTypeId()
										))
								.findAny().orElse(null);
						q.setId(null);
						q.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						l.add(q);
						payIds.add(q.getPayTypeId());
					});
					if (l.size() > 0) {
						payTypeRepository.saveAll(l);
						
					}
				}
			    }catch(Exception v) {
			    	
			    }
				apptIdInDB1.removeAll(apptIdInES1);// Data that are there in Local DB we need to update.
				if (apptIdInDB1.size() > 0) {
					List<PayType> l = new ArrayList<>();
					payIds.clear();
                    
					
					apptIdInDB1.forEach(id -> {
						PayType p = ((List<PayType>) (List<PayType>) data).stream()
								.filter(dp -> (id.intValue()==dp.getPayTypeId()
										
										
										))
								.findAny().orElse(null);

						PayType old = inDBExtra.stream()
								.filter(ind ->(id.intValue()==ind.getPayTypeId()
										))
								.findAny().orElse(null);
						if (p != null && old != null) {
							if (old != null) {
								p.setId(old.getId());
								p.setCreatedDate(old.getCreatedDate());
							}
							p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
							l.add(p);
							payIds.add(p.getPayTypeId());
							
						}
					});
					if (l.size() > 0) {
						payTypeRepository.saveAll(l);
						
					}
				}
				
			
			}
		} catch (Exception ex) {
			commonTableService.appenErrorToWriter(PayType.class, bw, ex);
		}
	}
	
	@Transactional("ruleEngineTransactionManager")
	public void activateDeactiveData(String officeId) {
		payTypeRepositoryRe.activateDeactiveData(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES, DataStatus.StatusEnum.DATA_CLOUD_STATUS_INVALID.YES,
				 officeId);
	}

}

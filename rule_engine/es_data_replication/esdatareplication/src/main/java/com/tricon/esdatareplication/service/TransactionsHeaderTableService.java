package com.tricon.esdatareplication.service;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tricon.esdatareplication.dao.repdb.ESTableRepository;
import com.tricon.esdatareplication.dao.repdb.TransactionsHeaderRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.TransactionsHeaderRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.TransactionsHeader;
import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsDetailReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsHeaderReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class TransactionsHeaderTableService extends CommonTableService {

	@Autowired
	private TransactionsHeaderRepository transactionsRepository;

	@Autowired
	private TransactionsHeaderRepositoryRe transactionsRepositoryRe;

	@Autowired
	private ESTableRepository estableRepository;
	
	@Autowired
	@Qualifier("repDbEntityManager")
	private EntityManager entityManager;

	@Autowired
	@Qualifier("ruleEngineEntityManager")
	private EntityManager entityManagerRe;

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
				List<TransactionsHeader> pL = transactionsRepository
						.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO, prepairPage);
				if (pL.size() == 0)
					break;
				List<TransactionsHeaderReplica> repList = new ArrayList<>();
				StringBuilder bu = new StringBuilder();
				pL.forEach(x -> {
					x.setOfficeId(office.getUuid());
					TransactionsHeaderReplica rep = new TransactionsHeaderReplica();
					BeanUtils.copyProperties(x, rep);
					bu.append(rep.getTranNum() + ",");
					//System.out.println("DSSS-->"+rep.getId()+"--"+rep.getDescription());
					//System.out.println("IMpac-->"+rep.getId()+"--"+rep.getImpacts());
					rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
					repList.add(rep);
				});
				//
				Set<Integer> apptIdInES = new HashSet<>();
				Set<Integer> apptIdInDB = new HashSet<>();
				((List<TransactionsHeaderReplica>) repList).stream().map(TransactionsHeaderReplica::getTranNum)
						.forEach(apptIdInES::add);
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<TransactionsHeaderReplica> inDB = transactionsRepositoryRe.findByTranNumInAndOfficeId(apptIdInES,
						office.getUuid());
				inDB.stream().map(TransactionsHeaderReplica::getTranNum).forEach(apptIdInDB::add);
				apptIdInES.removeAll(apptIdInDB);// TranNum that are not in Local DB
				if (apptIdInES.size() > 0) {
					List<TransactionsHeaderReplica> l = new ArrayList<>();
					apptIdInES.forEach(id -> {
						TransactionsHeaderReplica q = ((List<TransactionsHeaderReplica>) repList).stream()
								.filter(p -> id.intValue() == p.getTranNum().intValue()).findAny().orElse(null);
						q.setId(null);
						q.setOfficeId(office.getUuid());
						//System.out.println("DSSS-->"+q.getId()+"--"+q.getDescription());
						//System.out.println("IMpac-->"+q.getId()+"--"+q.getImpacts());
						l.add(q);
					});
					if (l.size() > 0) {
						//transactionsRepositoryRe.saveAllAndFlush(l);
						try {
							transactionsRepositoryRe.saveAllAndFlush(l);
							}catch(Exception ex1) {
								appendLoggerToWriter(TransactionsHeader.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
								String tnum="";
								for(TransactionsHeaderReplica p:l) {
									appendLoggerToWriter(TransactionsHeader.class, bw, "Now in Loop", true);
									tnum=p.getTranNum()+";";
									try {
										transactionsRepositoryRe.saveAndFlush(p);
										}catch(Exception ex) {
											appendLoggerToWriter(TransactionsHeader.class, bw, tnum, true);
											StringWriter errors = new StringWriter();
											ex.printStackTrace(new PrintWriter(errors));
											es.setLastIssueDetail(errors.toString());
											appendLoggerToWriter(TransactionsHeader.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
											appenErrorToWriter(TransactionsHeader.class, bw, ex);
										}
									
								}
								
							}
					}
				}
				apptIdInDB.removeAll(apptIdInES);// TranNum id that are there in Local DB we need to update.
				if (apptIdInDB.size() > 0) {
					List<TransactionsHeaderReplica> l = new ArrayList<>();
					apptIdInDB.forEach(id -> {
						TransactionsHeaderReplica p = ((List<TransactionsHeaderReplica>) repList).stream()
								.filter(dp -> id.intValue() == dp.getTranNum().intValue()).findAny().orElse(null);

						TransactionsHeaderReplica old = inDB.stream()
								.filter(ind -> id.intValue() == ind.getTranNum().intValue()).findAny().orElse(null);
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
					if (l.size() > 0) {
						try {
						transactionsRepositoryRe.saveAllAndFlush(l);
						}catch(Exception ex1) {
							appendLoggerToWriter(TransactionsHeader.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
							String tnum="";
							for(TransactionsHeaderReplica p:l) {
								appendLoggerToWriter(TransactionsHeader.class, bw, "Now in Loop", true);
								tnum=p.getTranNum()+";";
								try {
									transactionsRepositoryRe.saveAndFlush(p);
									}catch(Exception ex) {
										appendLoggerToWriter(TransactionsHeader.class, bw, tnum, true);
										StringWriter errors = new StringWriter();
										ex.printStackTrace(new PrintWriter(errors));
										es.setLastIssueDetail(errors.toString());
										appendLoggerToWriter(TransactionsHeader.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
										appenErrorToWriter(TransactionsHeader.class, bw, ex);
									}
								
							}
							
						}
						//transactionsRepositoryRe.flush();
					}
				}
				appendLoggerToWriter(TransactionsHeaderReplica.class, bw,
						Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
				appendLoggerToWriter(TransactionsHeaderReplica.class, bw, bu.toString(), true);
				pL.forEach(x -> {
					x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				});
				transactionsRepository.saveAll(pL);
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
			appendLoggerToWriter(TransactionsHeader.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(TransactionsHeader.class, bw, ex);
		}
		return es;

	}

	@Transactional(rollbackFor = Exception.class, transactionManager = "repDbTransactionManager")
	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {
		try {
			if (!checkExisting) {
				transactionsRepository.saveAll((List<TransactionsHeader>) data);
				logFirstTimeDataEntryToTable(TransactionsHeader.class, bw, data.size());
			} else {
				//
				Set<Integer> apptIdInES = new HashSet<>();
				Set<Integer> apptIdInDB = new HashSet<>();
				((List<TransactionsHeader>) data).stream().map(TransactionsHeader::getTranNum).forEach(apptIdInES::add);
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<TransactionsHeader> inDB = transactionsRepository.findByTranNumIn(apptIdInES);
				inDB.stream().map(TransactionsHeader::getTranNum).forEach(apptIdInDB::add);

				apptIdInES.removeAll(apptIdInDB);// TranNum that are not in Local DB
				if (apptIdInES.size() > 0) {
					List<TransactionsHeader> l = new ArrayList<>();
					apptIdInES.forEach(id -> {
						TransactionsHeader q = ((List<TransactionsHeader>) data).stream()
								.filter(p -> id.intValue() == p.getTranNum().intValue()).findAny().orElse(null);
						q.setId(null);
						q.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						l.add(q);
					});

					if (l.size() > 0)
						transactionsRepository.saveAll(l);
				}
				apptIdInDB.removeAll(apptIdInES);// TranNum id that are there in Local DB we need to update.
				if (apptIdInDB.size() > 0) {
					List<TransactionsHeader> l = new ArrayList<>();
					apptIdInDB.forEach(id -> {
						TransactionsHeader p = ((List<TransactionsHeader>) data).stream().filter(dp -> id.equals(dp.getTranNum()))
								.findAny().orElse(null);

						TransactionsHeader old = inDB.stream().filter(ind -> id.intValue() == ind.getTranNum().intValue())
								.findAny().orElse(null);
						p.setId(old.getId());
						p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						p.setCreatedDate(old.getCreatedDate());
						l.add(p);
					});
					if (l.size() > 0)
						transactionsRepository.saveAll(l);
				}

				logAfterFirstTimeDataEntryToTable(TransactionsHeader.class, bw, apptIdInES.size(), apptIdInDB.size(),
						String.join(",", apptIdInES.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
						String.join(",", apptIdInDB.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
			}
		} catch (Exception ex) {
			appenErrorToWriter(TransactionsHeader.class, bw, ex);
		}
	}
	
	@Transactional("repDbTransactionManager")
	public void updateOldData(String whereClause) {
		System.out.println(whereClause);
		//entityManager.getTransaction().begin();
		entityManager.createNativeQuery("update "+Constants.TABLE_TRANSACTIONS_HEADER+" set moved_to_cloud="+DataStatus.StatusEnum.DATA_CLOUD_STATUS_INVALID.YES+" where "+whereClause).executeUpdate();
		
		//entityManager.getTransaction().commit();
	}

	@Transactional("repDbTransactionManager")
	public void deleteOldData(String where) {
		entityManager.createNativeQuery("delete from "+Constants.TABLE_TRANSACTIONS_HEADER+" where "+where).executeUpdate();
		
		//entityManager.getTransaction().commit();
	}
	
	@Transactional("ruleEngineTransactionManager")
	public void updateOldDataRe(String whereClause,String officeId) {
		System.out.println(whereClause);
		//entityManager.getTransaction().begin();
		entityManagerRe.createNativeQuery("update "+Constants.TABLE_REPLICA_IN_CLOUD + Constants.TABLE_TRANSACTIONS_HEADER+" set moved_to_cloud="+DataStatus.StatusEnum.DATA_CLOUD_STATUS_INVALID.YES+" where "+whereClause +" and office_id='"+officeId+"'").executeUpdate();
		
		//entityManager.getTransaction().commit();
	}
	
	@Transactional("ruleEngineTransactionManager")
	public void deleteOldDataRe(String officeId) {
		//entityManagerRe.getTransaction().begin();
		entityManagerRe.createNativeQuery("delete from "+Constants.TABLE_REPLICA_IN_CLOUD + Constants.TABLE_TRANSACTIONS_HEADER+" where moved_to_cloud="+DataStatus.StatusEnum.DATA_CLOUD_STATUS_INVALID.YES + " and  office_id='"+officeId+"'").executeUpdate();
		
		//entityManagerRe.getTransaction().commit();
	}
}

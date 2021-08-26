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
import com.tricon.esdatareplication.dao.repdb.PaymentProviderRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.PaymentProviderRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.PaymentProvider;
import com.tricon.esdatareplication.entity.repdb.Transactions;
import com.tricon.esdatareplication.entity.ruleenginedb.PaymentProviderReplica;
import com.tricon.esdatareplication.entity.ruleenginedb.TransactionsReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class PaymentProviderTableService extends CommonTableService {

	@Autowired
	private PaymentProviderRepository paymentProviderRepository;

	@Autowired
	private PaymentProviderRepositoryRe paymentProviderRepositoryRe;

	@Autowired
	private ESTableRepository estableRepository;

	@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		try {
			List<PaymentProvider> pL = paymentProviderRepository
					.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
			List<PaymentProviderReplica> repList = new ArrayList<>();
			StringBuilder bu = new StringBuilder();
			pL.forEach(x -> {
				x.setOfficeId(office.getUuid());
				PaymentProviderReplica rep = new PaymentProviderReplica();
				BeanUtils.copyProperties(x, rep);
				bu.append(rep.getTranNum() + ",");
				rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				repList.add(rep);
			});
			// new repository for cloud.. and save data...
			//
			Set<Integer> apptIdInES = new HashSet<>();
			Set<String> apptIdInDB1 = new HashSet<>();
			Set<String> apptIdInES1 = new HashSet<>();
			Set<String> apptIdInESExtra = new HashSet<>();
			//Set<String> apptIdInDBExtra = new HashSet<>();
			
			
			((List<PaymentProviderReplica>) repList).stream().map(PaymentProviderReplica::getTranNum)
					.forEach(apptIdInES::add);
			((List<PaymentProviderReplica>) repList).stream().map(PaymentProviderReplica::getProviderId)
			.forEach(apptIdInESExtra::add);
			
			for(PaymentProviderReplica r:(List<PaymentProviderReplica>) repList) {
				apptIdInES1.add(r.getTranNum()+"---"+r.getProviderId());
			}
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<PaymentProviderReplica> inDB = paymentProviderRepositoryRe.findByTranNumInAndProviderIdInAndOfficeId(apptIdInES,apptIdInESExtra,
					office.getUuid());
			//inDB.stream().map(PaymentProviderReplica::getTranNum).forEach(apptIdInDB::add);
			for(PaymentProviderReplica r:inDB) {
				apptIdInDB1.add(r.getTranNum()+"---"+r.getProviderId());
				
			}
			apptIdInES1.removeAll(apptIdInDB1);// TranNum that are not in Local DB
			if (apptIdInES1.size() > 0) {
				List<PaymentProviderReplica> l= new ArrayList<>();
				apptIdInES1.forEach(id -> {
					PaymentProviderReplica q = ((List<PaymentProviderReplica>) repList).stream()
							.filter(p ->Integer.parseInt(id.split("---")[0]) == p.getTranNum().intValue()
							&& id.split("---")[1].equals(p.getProviderId())		
									
							).findAny().orElse(null);
					q.setId(null);
					l.add(q);
				});
				if (l.size()>0) paymentProviderRepositoryRe.saveAll(l);
			}
			apptIdInDB1.removeAll(apptIdInES1);// TranNum id that are there in Local DB we need to update.
			// no unique id just a mapping table //Check latter
			if (apptIdInDB1.size() > 0) {
				List<PaymentProviderReplica> l= new ArrayList<>();
				apptIdInDB1.forEach(id -> {
					PaymentProviderReplica p = ((List<PaymentProviderReplica>) repList).stream()
							.filter(dp ->Integer.parseInt(id.split("---")[0]) == dp.getTranNum().intValue()
							&& id.split("---")[1].equals(dp.getProviderId())		
									
							).findAny().orElse(null);

					PaymentProviderReplica old = inDB.stream()
							.filter(dp ->Integer.parseInt(id.split("---")[0]) == dp.getTranNum().intValue()
							&& id.split("---")[1].equals(dp.getProviderId())		
									
							).findAny().orElse(null);
					if (p != null && old != null) {
						if (old != null) {
							p.setId(old.getId());
							p.setCreatedDate(old.getCreatedDate());
						}
						p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);

						l.add(p);
					}
				});
                 if (l.size()>0) paymentProviderRepositoryRe.saveAll(l);
			}
			appendLoggerToWriter(PaymentProviderReplica.class, bw,
					Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
			appendLoggerToWriter(PaymentProviderReplica.class, bw, bu.toString(), true);
			pL.forEach(x -> {
				x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			});
			paymentProviderRepository.saveAll(pL);
			es.setRecordsInsertedLastIteration(pL.size());
			es.setUpdatedDate(new Date());
			estableRepository.save(es);
		} catch (Exception ex) {
			es.setRecordsInsertedLastIteration(0);
			StringWriter errors = new StringWriter();
			ex.printStackTrace(new PrintWriter(errors));
			es.setLastIssueDetail(errors.toString());
			appendLoggerToWriter(Transactions.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(Transactions.class, bw, ex);
		}
		return es;

	}

	@Transactional(rollbackFor = Exception.class, transactionManager = "repDbTransactionManager")
	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {
		try {
			if (!checkExisting) {
				paymentProviderRepository.saveAll((List<PaymentProvider>) data);
				logFirstTimeDataEntryToTable(PaymentProvider.class, bw, data.size());
			} else {
				//
				Set<Integer> apptIdInES = new HashSet<>();
				Set<String> apptIdInDB1 = new HashSet<>();
				Set<String> apptIdInES1 = new HashSet<>();
				Set<String> apptIdInESExtra = new HashSet<>();
				//Set<String> apptIdInDBExtra = new HashSet<>();
				
				if (data!=null) {
				((List<PaymentProvider>) data).stream().map(PaymentProvider::getTranNum)
						.forEach(apptIdInES::add);
				((List<PaymentProvider>) data).stream().map(PaymentProvider::getProviderId)
				.forEach(apptIdInESExtra::add);
				
				for(PaymentProvider r:(List<PaymentProvider>) data) {
					apptIdInES1.add(r.getTranNum()+"---"+r.getProviderId());
				}
				}
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<PaymentProvider> inDB = paymentProviderRepository.findByTranNumInAndProviderIdIn(apptIdInES,apptIdInESExtra);
				//inDB.stream().map(PaymentProviderReplica::getTranNum).forEach(apptIdInDB::add);
				for(PaymentProvider r:inDB) {
					apptIdInDB1.add(r.getTranNum()+"---"+r.getProviderId());
					
				}
				apptIdInES1.removeAll(apptIdInDB1);// TranNum that are not in Local DB
				if (apptIdInES1.size() > 0) {
                    List<PaymentProvider> l= new ArrayList<>();
					apptIdInES1.forEach(id -> {
						PaymentProvider q = ((List<PaymentProvider>) data).stream()
								.filter(p ->Integer.parseInt(id.split("---")[0]) == p.getTranNum().intValue()
								&& id.split("---")[1].equals(p.getProviderId())		
										
								).findAny().orElse(null);
						q.setId(null);
						q.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						l.add(q);
					});
					if (l.size()>0) paymentProviderRepository.saveAll(l);
				}
				apptIdInDB1.removeAll(apptIdInES1);// TranNum id that are there in Local DB we need to update.
				// no unique id just a mapping table //Check latter
				if (apptIdInDB1.size() > 0) {
					List<PaymentProvider> l= new ArrayList<>();
					apptIdInDB1.forEach(id -> {
						PaymentProvider p = ((List<PaymentProvider>) data).stream()
								.filter(dp ->Integer.parseInt(id.split("---")[0]) == dp.getTranNum().intValue()
								&& id.split("---")[1].equals(dp.getProviderId())		
										
								).findAny().orElse(null);

						PaymentProvider old = inDB.stream()
								.filter(dp ->Integer.parseInt(id.split("---")[0]) == dp.getTranNum().intValue()
								&& id.split("---")[1].equals(dp.getProviderId())		
										
								).findAny().orElse(null);
						if (p != null && old != null) {
							if (old != null) {
								p.setId(old.getId());
								p.setCreatedDate(old.getCreatedDate());
							}
							p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);

							l.add(p);
						}
					});
					if (l.size()>0) paymentProviderRepository.saveAll(l);
				}
				logAfterFirstTimeDataEntryToTable(PaymentProvider.class, bw, apptIdInES1.size(), apptIdInDB1.size(),
						String.join(",", apptIdInES1.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
						String.join(",", apptIdInDB1.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
			}
		} catch (Exception ex) {
			appenErrorToWriter(PaymentProvider.class, bw, ex);
		}
	}
}

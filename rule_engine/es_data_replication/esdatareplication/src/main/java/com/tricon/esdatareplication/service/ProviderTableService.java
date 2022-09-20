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
import com.tricon.esdatareplication.dao.repdb.ProviderRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.ProviderRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.Provider;
import com.tricon.esdatareplication.entity.ruleenginedb.ProviderReplica;
import com.tricon.esdatareplication.util.Constants;
import com.tricon.esdatareplication.util.DataStatus;

@Service
public class ProviderTableService extends CommonTableService {
	@Autowired
	private ProviderRepository providerRepository;

	@Autowired
	private ProviderRepositoryRe providerRepositoryRe;
	
	@Autowired
	private ESTableRepository estableRepository;
	
	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;
	
	//@Transactional(rollbackFor = Exception.class, transactionManager = "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		int localCt = 0;
		try {
			//Long count = patientRepository.countByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
			//long totalPages = Double.valueOf(Math.ceil(count / (float) batchSize)).longValue();
			while(true) {
				Pageable prepairPage = PageRequest.of(0, batchSize);// 0,50
			List<Provider> pL = providerRepository.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO,prepairPage);
			if (pL.size()==0) break;
			List<ProviderReplica> repList = new ArrayList<>();
			StringBuilder bu = new StringBuilder();
			pL.forEach(x -> {
				x.setOfficeId(office.getUuid());
				ProviderReplica rep = new ProviderReplica();
				BeanUtils.copyProperties(x, rep);
				bu.append(rep.getProviderId() + ",");
				rep.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				repList.add(rep);
			});
			// new repository for cloud.. and save data...
			Set<String> apptIdInES = new HashSet<>();
			Set<String> apptIdInDB = new HashSet<>();
			((List<ProviderReplica>) repList).stream().map(ProviderReplica::getProviderId).forEach(apptIdInES::add);
			// or
			// d2.forEach(a -> patIds.add(a.getPatientId()));
			List<ProviderReplica> inDB = providerRepositoryRe.findByProviderIdInAndOfficeId(apptIdInES,office.getUuid());
			inDB.stream().map(ProviderReplica::getProviderId).forEach(apptIdInDB::add);
			apptIdInES.removeAll(apptIdInDB);// Provider ID that are not in Local DB
			if (apptIdInES.size() > 0) {
				List<ProviderReplica> l = new ArrayList<>();
				apptIdInES.forEach(id -> {
					ProviderReplica q=((List<ProviderReplica>) repList).stream()
					.filter(p -> id.equals(p.getProviderId())).findAny().orElse(null);
					q.setId(null);
					q.setOfficeId(office.getUuid());
					l.add(q);
				});
				if (l.size()>0)providerRepositoryRe.saveAllAndFlush(l);
			}
			apptIdInDB.removeAll(apptIdInES);// Provider ID id that are there in Local DB we need to update.
			if (apptIdInDB.size() > 0) {
				List<ProviderReplica> l = new ArrayList<>();
				apptIdInDB.forEach(id -> {
					
					ProviderReplica p = ((List<ProviderReplica>) repList).stream().filter(dp -> id.equals(dp.getProviderId()))
							.findAny().orElse(null);

					ProviderReplica old = inDB.stream().filter(ind -> id.equals(ind.getProviderId())).findAny()
							.orElse(null);
					if (p!=null && old!=null) {
						if (old!=null) {
							p.setId(old.getId());
							p.setCreatedDate(old.getCreatedDate());
						}
					    p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
					    p.setOfficeId(office.getUuid());
					    l.add(p);
					}
				});
				if (l.size()>0)providerRepositoryRe.saveAllAndFlush(l);
			}
			appendLoggerToWriter(ProviderReplica.class, bw,
					Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
			appendLoggerToWriter(ProviderReplica.class, bw, bu.toString(), true);
			pL.forEach(x -> {
				x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
			});
			providerRepository.saveAll(pL);
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
			appendLoggerToWriter(Provider.class, bw, Constants.ERROR_IN_PUSHING_TO_CLOUD, true);
			appenErrorToWriter(Provider.class, bw, ex);
		}
		return es;

	}

	@Transactional(rollbackFor = Exception.class, transactionManager = "repDbTransactionManager")
	public void saveDataToLocalDB(BufferedWriter bw, List<?> data, boolean checkExisting) {
		try {
			if (!checkExisting) {
				providerRepository.saveAll((List<Provider>) data);
				logFirstTimeDataEntryToTable(Provider.class, bw, data.size());
			} else {
				//
				Set<String> apptIdInES = new HashSet<>();
				Set<String> apptIdInDB = new HashSet<>();
				((List<Provider>) data).stream().map(Provider::getProviderId).forEach(apptIdInES::add);
				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				List<Provider> inDB = providerRepository.findByProviderIdIn(apptIdInES);
				inDB.stream().map(Provider::getProviderId).forEach(apptIdInDB::add);

				apptIdInES.removeAll(apptIdInDB);// Provider ID that are not in Local DB
				if (apptIdInES.size() > 0) {
					List<Provider> l = new ArrayList<>();
					apptIdInES.forEach(id -> {
						Provider q =((List<Provider>) data).stream()
								.filter(p -> id.equals(p.getProviderId())).findAny().orElse(null);
						q.setId(null);
						q.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						l.add(q);
					});
					if (l.size()>0)providerRepository.saveAll(l);
				}
				apptIdInDB.removeAll(apptIdInES);// Patient id that are there in Local DB we need to update.
				if (apptIdInDB.size() > 0) {
					List<Provider> l = new ArrayList<>();
					apptIdInDB.forEach(id -> {
						Provider p = ((List<Provider>) data).stream().filter(dp -> id.equals(dp.getProviderId()))
								.findAny().orElse(null);

						Provider old = inDB.stream().filter(ind -> id.equals(ind.getProviderId())).findAny()
								.orElse(null);
						p.setId(old.getId());
						p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
						p.setCreatedDate(old.getCreatedDate());
						l.add(p);
					});
					if (l.size()>0) providerRepository.saveAll(l);
				}

				logAfterFirstTimeDataEntryToTable(Provider.class, bw, apptIdInES.size(), apptIdInDB.size(),
						String.join(",", apptIdInES.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
						String.join(",", apptIdInDB.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
			}
		} catch (Exception ex) {
			appenErrorToWriter(Provider.class, bw, ex);
		}
	}
}

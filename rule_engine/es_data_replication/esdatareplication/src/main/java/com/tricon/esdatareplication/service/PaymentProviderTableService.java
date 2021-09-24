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

import com.tricon.esdatareplication.dao.repdb.ESTableRepository;
import com.tricon.esdatareplication.dao.repdb.PaymentProviderRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.PaymentProviderRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.ESTable;
import com.tricon.esdatareplication.entity.repdb.Office;
import com.tricon.esdatareplication.entity.repdb.PaymentProvider;
import com.tricon.esdatareplication.entity.repdb.Transactions;
import com.tricon.esdatareplication.entity.ruleenginedb.PaymentProviderReplica;
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

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;

	// @Transactional(rollbackFor = Exception.class, transactionManager =
	// "ruleEngineTransactionManager")
	public ESTable pushDataFromLocalESToColudDB(BufferedWriter bw, Office office, ESTable es) {
		int localCt = 0;
		try {
			// Long count =
			// patientRepository.countByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);
			// long totalPages = Double.valueOf(Math.ceil(count / (float)
			// batchSize)).longValue();
			while (true) {
				Pageable prepairPage = PageRequest.of(0, batchSize);// 0,50

				List<PaymentProvider> pL = paymentProviderRepository
						.findByMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO, prepairPage);
				if (pL.size() == 0)
					break;
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
				//Set<String> apptIdInESExtra = new HashSet<>();
				//Set<String> apptIdInESExtra1 = new HashSet<>();
				// Set<String> apptIdInDBExtra = new HashSet<>();
				// Case 1
				List<PaymentProviderReplica> repList1 = ((List<PaymentProviderReplica>) repList).stream()
						.filter(x -> x.getProdProviderId() != null && x.getProviderId() != null)
						.collect(Collectors.toList());
				List<PaymentProviderReplica> repList2 = ((List<PaymentProviderReplica>) repList).stream()
						.filter(x -> x.getProdProviderId() == null || x.getProviderId() == null)
						.collect(Collectors.toList());

				((List<PaymentProviderReplica>) repList1).stream().map(PaymentProviderReplica::getTranNum)
						.forEach(apptIdInES::add);
				//((List<PaymentProviderReplica>) repList1).stream().map(PaymentProviderReplica::getProviderId)
				//		.forEach(apptIdInESExtra::add);
				///((List<PaymentProviderReplica>) repList1).stream().map(PaymentProviderReplica::getProdProviderId)
				//		.forEach(apptIdInESExtra1::add);

				for (PaymentProviderReplica r : (List<PaymentProviderReplica>) repList) {
					apptIdInES1.add(r.getTranNum() + "---" + r.getProviderId() + "---" + r.getProdProviderId());
				}

				List<PaymentProviderReplica> inDB = new ArrayList<>();
				if (repList1 != null) {
					for (PaymentProviderReplica x : repList1) {
						List<PaymentProviderReplica> m = paymentProviderRepositoryRe
								.findByTranNumAndProdIdAndProdProviderId(x.getTranNum(), x.getProviderId(),
										x.getProdProviderId(), office.getUuid());
						if (m != null)
							inDB.addAll(m);
					}
				}
				if (repList2 != null) {
					appendLoggerToWriter(PaymentProviderReplica.class, bw, "repList2 is here :" + repList2.size(),
							true);
					for (PaymentProviderReplica x : repList2) {
						if (x.getProdProviderId() == null) {
							List<PaymentProviderReplica> m = paymentProviderRepositoryRe
									.findByTranNumAndProdId(x.getTranNum(), x.getProviderId(), office.getUuid());
							if (m != null)
								inDB.addAll(m);
						} else {
							List<PaymentProviderReplica> m = paymentProviderRepositoryRe.findByTranNumAndProdProviderId(
									x.getTranNum(), x.getProdProviderId(), office.getUuid());
							if (m != null)
								inDB.addAll(m);
						}
					}
				}
				///////////////////

				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				for (PaymentProviderReplica r : inDB) {
					apptIdInDB1.add(r.getTranNum() + "---" + r.getProviderId() + "---" + r.getProdProviderId());

				}
				apptIdInES1.removeAll(apptIdInDB1);// TranNum that are not in Local DB
				if (apptIdInES1.size() > 0) {
					List<PaymentProviderReplica> l = new ArrayList<>();
					for (String x : apptIdInES1) {
						for (PaymentProviderReplica k : repList) {
							if (k.getProviderId() == null) {
								k.setProviderId("null");
							}
							if (k.getProdProviderId() == null) {
								k.setProdProviderId("null");
								appendLoggerToWriter(PaymentProviderReplica.class, bw, "SET TO NULLL STR-->"+k.getTranNum(), true);
							}
							if (Integer.parseInt(x.split("---")[0]) == k.getTranNum().intValue()
									&& x.split("---")[1].equals(k.getProviderId())
									&& x.split("---")[2].equals(k.getProdProviderId())) {
								if (k.getProviderId().equals("null"))
									k.setProviderId(null);
								if (k.getProdProviderId().equals("null")) {
									k.setProdProviderId(null);
									appendLoggerToWriter(PaymentProviderReplica.class, bw, "SET TO NULLL OBJ-->"+k.getTranNum(), true);
								}
								k.setId(null);
								l.add(k);
							}
							if (k.getProviderId()!=null && k.getProviderId().equals("null"))
								k.setProviderId(null);
							if (k.getProdProviderId()!=null && k.getProdProviderId().equals("null")) {
								k.setProdProviderId(null);
							}
						}
					}

					/*
					 * List<PaymentProviderReplica> l = new ArrayList<>(); apptIdInES1.forEach(id ->
					 * { PaymentProviderReplica q = ((List<PaymentProviderReplica>)
					 * repList).stream() .filter(p -> Integer.parseInt(id.split("---")[0]) ==
					 * p.getTranNum().intValue() && id.split("---")[1].equals(p.getProviderId()) &&
					 * id.split("---")[2].equals(p.getProdProviderId())
					 * 
					 * ).findAny().orElse(null); q.setId(null); l.add(q); });
					 */
					if (l.size() > 0)
						paymentProviderRepositoryRe.saveAllAndFlush(l);
				}
				apptIdInDB1.removeAll(apptIdInES1);// TranNum id that are there in Local DB we need to update.
				// no unique id just a mapping table //Check latter
				if (apptIdInDB1.size() > 0) {
					List<PaymentProviderReplica> l = new ArrayList<>();
					for (String x : apptIdInDB1) {
						PaymentProviderReplica p = null, old = null;
						for (PaymentProviderReplica k : repList) {
							if (k.getProviderId() == null)
								k.setProviderId("null");
							if (k.getProdProviderId() == null)
								k.setProdProviderId("null");
							if (Integer.parseInt(x.split("---")[0]) == k.getTranNum().intValue()
									&& x.split("---")[1].equals(k.getProviderId())
									&& x.split("---")[2].equals(k.getProdProviderId())) {
								if (k.getProviderId().equals("null"))
									k.setProviderId(null);
								if (k.getProdProviderId().equals("null"))
									k.setProdProviderId(null);
								p = k;
							}
							if (k.getProviderId()!=null && k.getProviderId().equals("null"))
								k.setProviderId(null);
							if (k.getProdProviderId()!=null && k.getProdProviderId().equals("null")) {
								k.setProdProviderId(null);
							}
						}
						for (PaymentProviderReplica k : inDB) {
							if (k.getProviderId() == null)
								k.setProviderId("null");
							if (k.getProdProviderId() == null)
								k.setProdProviderId("null");

							if (Integer.parseInt(x.split("---")[0]) == k.getTranNum().intValue()
									&& x.split("---")[1].equals(k.getProviderId())
									&& x.split("---")[2].equals(k.getProdProviderId())) {
								if (k.getProviderId().equals("null"))
									k.setProviderId(null);
								if (k.getProdProviderId().equals("null"))
									k.setProdProviderId(null);
								old = k;
							}
							if (k.getProviderId()!=null && k.getProviderId().equals("null"))
								k.setProviderId(null);
							if (k.getProdProviderId()!=null && k.getProdProviderId().equals("null")) {
								k.setProdProviderId(null);
							}
						}
						if (p != null && old != null) {
							if (old != null) {
								p.setId(old.getId());
								p.setCreatedDate(old.getCreatedDate());
							}
							p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);

							l.add(p);
						}
					}
					/*
					 * apptIdInDB1.forEach(id -> { PaymentProviderReplica p =
					 * ((List<PaymentProviderReplica>) repList).stream() .filter(dp ->
					 * Integer.parseInt(id.split("---")[0]) == dp.getTranNum().intValue() &&
					 * id.split("---")[1].equals(dp.getProviderId()) &&
					 * id.split("---")[2].equals(dp.getProdProviderId())
					 * 
					 * ).findAny().orElse(null);
					 * 
					 * PaymentProviderReplica old = inDB.stream() .filter(dp ->
					 * Integer.parseInt(id.split("---")[0]) == dp.getTranNum().intValue() &&
					 * id.split("---")[1].equals(dp.getProviderId()) &&
					 * id.split("---")[2].equals(dp.getProdProviderId())
					 * 
					 * ).findAny().orElse(null);
					 * 
					 * if (p != null && old != null) { if (old != null) { p.setId(old.getId());
					 * p.setCreatedDate(old.getCreatedDate()); }
					 * p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
					 * 
					 * l.add(p); } });
					 */
					if (l.size() > 0)
						paymentProviderRepositoryRe.saveAllAndFlush(l);
				}
				appendLoggerToWriter(PaymentProviderReplica.class, bw,
						Constants.RECORDS_UPDATED_IN_TABLE_CLOUD + ":" + repList.size(), true);
				appendLoggerToWriter(PaymentProviderReplica.class, bw, bu.toString(), true);
				pL.forEach(x -> {
					x.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
				});
				paymentProviderRepository.saveAll(pL);
				localCt = localCt + pL.size();
			} // while
			es.setRecordsInsertedLastIteration(localCt);
			//es.setUpdatedDate(new Date());
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
				Set<String> apptIdInESExtra1 = new HashSet<>();
				// Set<String> apptIdInDBExtra = new HashSet<>();
				// Case 1
				if (data!=null) {
				List<PaymentProvider> repList1 = ((List<PaymentProvider>) data).stream()
						.filter(x -> x.getProdProviderId() != null && x.getProviderId() != null)
						.collect(Collectors.toList());
				List<PaymentProvider> repList2 = ((List<PaymentProvider>) data).stream()
						.filter(x -> x.getProdProviderId() == null || x.getProviderId() == null)
						.collect(Collectors.toList());

				((List<PaymentProvider>) repList1).stream().map(PaymentProvider::getTranNum).forEach(apptIdInES::add);
				((List<PaymentProvider>) repList1).stream().map(PaymentProvider::getProviderId)
						.forEach(apptIdInESExtra::add);
				((List<PaymentProvider>) repList1).stream().map(PaymentProvider::getProdProviderId)
						.forEach(apptIdInESExtra1::add);

				for (PaymentProvider r : (List<PaymentProvider>) data) {
					apptIdInES1.add(r.getTranNum() + "---" + r.getProviderId() + "---" + r.getProdProviderId());
				}

				List<PaymentProvider> inDB = new ArrayList<>();
				if (repList1 != null) {
					for (PaymentProvider x : repList1) {
						List<PaymentProvider> m = paymentProviderRepository.findByTranNumAndProdIdAndProdProviderId(
								x.getTranNum(), x.getProviderId(), x.getProdProviderId());
						if (m != null)
							inDB.addAll(m);
					}
				}
				if (repList2 != null) {
					for (PaymentProvider x : repList2) {
						if (x.getProdProviderId() == null) {
							List<PaymentProvider> m = paymentProviderRepository.findByTranNumAndProdId(x.getTranNum(),
									x.getProviderId());
							if (m != null)
								inDB.addAll(m);
						} else {
							List<PaymentProvider> m = paymentProviderRepository
									.findByTranNumAndProdProviderId(x.getTranNum(), x.getProdProviderId());
							if (m != null)
								inDB.addAll(m);
						}
					}
				}
				///////////////////

				// or
				// d2.forEach(a -> patIds.add(a.getPatientId()));
				for (PaymentProvider r : inDB) {
					apptIdInDB1.add(r.getTranNum() + "---" + r.getProviderId() + "---" + r.getProdProviderId());

				}
				apptIdInES1.removeAll(apptIdInDB1);// TranNum that are not in Local DB
				if (apptIdInES1.size() > 0) {
					List<PaymentProvider> l = new ArrayList<>();
					for (String x : apptIdInES1) {
						for (PaymentProvider k : (List<PaymentProvider>) data) {
							if (k.getProviderId() == null)
								k.setProviderId("null");
							if (k.getProdProviderId() == null)
								k.setProdProviderId("null");
							if (Integer.parseInt(x.split("---")[0]) == k.getTranNum().intValue()
									&& x.split("---")[1].equals(k.getProviderId())
									&& x.split("---")[2].equals(k.getProdProviderId())) {
								if (k.getProviderId().equals("null"))
									k.setProviderId(null);
								if (k.getProdProviderId().equals("null"))
									k.setProdProviderId(null);
								k.setId(null);
								l.add(k);
							}
							if (k.getProviderId()!=null && k.getProviderId().equals("null"))
								k.setProviderId(null);
							if (k.getProdProviderId()!=null && k.getProdProviderId().equals("null")) {
								k.setProdProviderId(null);
							}
						}
					}

					/*
					 * List<PaymentProviderReplica> l = new ArrayList<>(); apptIdInES1.forEach(id ->
					 * { PaymentProviderReplica q = ((List<PaymentProviderReplica>)
					 * repList).stream() .filter(p -> Integer.parseInt(id.split("---")[0]) ==
					 * p.getTranNum().intValue() && id.split("---")[1].equals(p.getProviderId()) &&
					 * id.split("---")[2].equals(p.getProdProviderId())
					 * 
					 * ).findAny().orElse(null); q.setId(null); l.add(q); });
					 */
					if (l.size() > 0)
						paymentProviderRepository.saveAllAndFlush(l);
				}
				apptIdInDB1.removeAll(apptIdInES1);// TranNum id that are there in Local DB we need to update.
				// no unique id just a mapping table //Check latter
				if (apptIdInDB1.size() > 0) {
					List<PaymentProvider> l = new ArrayList<>();
					for (String x : apptIdInDB1) {
						PaymentProvider p = null, old = null;
						for (PaymentProvider k : (List<PaymentProvider>) data) {
							if (k.getProviderId() == null)
								k.setProviderId("null");
							if (k.getProdProviderId() == null)
								k.setProdProviderId("null");
							if (Integer.parseInt(x.split("---")[0]) == k.getTranNum().intValue()
									&& x.split("---")[1].equals(k.getProviderId())
									&& x.split("---")[2].equals(k.getProdProviderId())) {
								if (k.getProviderId().equals("null"))
									k.setProviderId(null);
								if (k.getProdProviderId().equals("null"))
									k.setProdProviderId(null);
								p = k;
							}
							if (k.getProviderId()!=null && k.getProviderId().equals("null"))
								k.setProviderId(null);
							if (k.getProdProviderId()!=null && k.getProdProviderId().equals("null")) {
								k.setProdProviderId(null);
							}
						}
						for (PaymentProvider k : inDB) {
							if (k.getProviderId() == null)
								k.setProviderId("null");
							if (k.getProdProviderId() == null)
								k.setProdProviderId("null");
							if (Integer.parseInt(x.split("---")[0]) == k.getTranNum().intValue()
									&& x.split("---")[1].equals(k.getProviderId())
									&& x.split("---")[2].equals(k.getProdProviderId())) {
								if (k.getProviderId().equals("null"))
									k.setProviderId(null);
								if (k.getProdProviderId().equals("null"))
									k.setProdProviderId(null);
								old = k;
							}
							if (k.getProviderId()!=null && k.getProviderId().equals("null"))
								k.setProviderId(null);
							if (k.getProdProviderId()!=null && k.getProdProviderId().equals("null")) {
								k.setProdProviderId(null);
							}
						}
						if (p != null && old != null) {
							if (old != null) {
								p.setId(old.getId());
								p.setCreatedDate(old.getCreatedDate());
							}
							p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.NO);

							l.add(p);
						}
					}
					/*
					 * apptIdInDB1.forEach(id -> { PaymentProviderReplica p =
					 * ((List<PaymentProviderReplica>) repList).stream() .filter(dp ->
					 * Integer.parseInt(id.split("---")[0]) == dp.getTranNum().intValue() &&
					 * id.split("---")[1].equals(dp.getProviderId()) &&
					 * id.split("---")[2].equals(dp.getProdProviderId())
					 * 
					 * ).findAny().orElse(null);
					 * 
					 * PaymentProviderReplica old = inDB.stream() .filter(dp ->
					 * Integer.parseInt(id.split("---")[0]) == dp.getTranNum().intValue() &&
					 * id.split("---")[1].equals(dp.getProviderId()) &&
					 * id.split("---")[2].equals(dp.getProdProviderId())
					 * 
					 * ).findAny().orElse(null);
					 * 
					 * if (p != null && old != null) { if (old != null) { p.setId(old.getId());
					 * p.setCreatedDate(old.getCreatedDate()); }
					 * p.setMovedToCloud(DataStatus.StatusEnum.DATA_CLOUD_STATUS.YES);
					 * 
					 * l.add(p); } });
					 */
					if (l.size() > 0)
						paymentProviderRepository.saveAllAndFlush(l);
				}
				}
				logAfterFirstTimeDataEntryToTable(PaymentProvider.class, bw, apptIdInES1.size(), apptIdInDB1.size(),
						String.join(",", apptIdInES1.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())),
						String.join(",",
								apptIdInDB1.stream().map(s -> String.valueOf(s)).collect(Collectors.toList())));
				
			}
		} catch (Exception ex) {
			appenErrorToWriter(PaymentProvider.class, bw, ex);
		}
	}

	public static void main(String aa[]) {

		String a = null;
		String b = "a" + "---" + a;
		System.out.println(b);
		System.out.println(b.split("---")[1].equals("null"));
		if (a==null) {
			a="null";
		}
        if (a.equals("null")) {
        	a=null;
        }
        System.out.println(a);
        String h="asa---"+a;
        System.out.println(h);
        String z=null;
        System.out.println("null".equals(null));
        
		List<PaymentProviderReplica> repList = new ArrayList<>();
		List<String> apptIdInES = new ArrayList<>();

		String q="null";
		System.out.println("DDDDDD");
		System.out.println(q==null);
		PaymentProviderReplica r = null;
		r = new PaymentProviderReplica();
		r.setProviderId(null);
		repList.add(r);
		r = new PaymentProviderReplica();
		r.setProviderId("oo");
		repList.add(r);
		((List<PaymentProviderReplica>) repList).stream().map(PaymentProviderReplica::getProviderId)
				.filter(x -> x != null).forEach(apptIdInES::add);
		for (String ss : apptIdInES) {
			System.out.println(ss + "ad");
		}
	}
}

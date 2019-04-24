package com.tricon.ruleengine.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tricon.ruleengine.dao.OfficeDao;
import com.tricon.ruleengine.dao.ScrappingDao;
import com.tricon.ruleengine.dto.ScrappingInputDto;
import com.tricon.ruleengine.dto.ScrappingUserDataInputDto;
import com.tricon.ruleengine.logger.RuleEngineLogger;
import com.tricon.ruleengine.model.db.Office;
import com.tricon.ruleengine.model.db.ScrappingSiteDetails;
import com.tricon.ruleengine.model.sheet.MCNADentaSheet;
import com.tricon.ruleengine.service.ScrappingService;
import com.tricon.ruleengine.utils.ConnectAndReadSheets;
import com.tricon.ruleengine.utils.Constants;
import com.tricon.ruleengine.utils.ConstantsScrapping;

@Transactional
@Service
public class ScrappingServiceImpl implements ScrappingService {

	static Class<?> clazz = ScrappingServiceImpl.class;

	@Value("${google.credential.folder}")
	private String CREDENTIALS_FOLDER;

	@Value("${google.client.secret}")
	private String CLIENT_SECRET_DIR;

	@Autowired
	ScrappingDao sDao;
	// @Autowired MCNARosterScrappingService mservice;
	@Autowired
	OfficeDao officeDoa;
	// @Autowired MCNAEligibilityScrappingService meservice;
	//@Autowired
	//DentaQEligibilityScrappingService deservice;

	@Override
	public Map<String, List<?>> scrapSite(ScrappingInputDto dto) throws InterruptedException, ExecutionException {

		//boolean updateStatusinSheet = false;
		Office off = officeDoa.getOfficeByUuid(dto.getOfficeId());
		// List<?> data=null;
		Map<String, List<?>> map = new HashMap<>();
		// ScrappingSite st= sDao.getScrappingSiteDetails(dto.getSiteId(), off);
		ScrappingSiteDetails sd = sDao.getScrappingSiteDetailsDetail(dto.getScrapType(), off);
		// Hibernate.initialize(st.getSiteSiteDetails());
		RuleEngineLogger.generateLogs(clazz, "ScrappingServiceImpl 1", Constants.rule_log_debug, null);
		/*
		 * Set<ScrappingSiteDetails> sdL=
		 * st.getSiteSiteDetails();//sDao.getScrappingSiteDetailsDetail(dto.
		 * getSiteDetailId(), off); ScrappingSiteDetails sd=null;
		 * 
		 * for(ScrappingSiteDetails sd1 :sdL) { sd=sd1; break; }
		 */

		if (sd == null) {
			map.put(ConstantsScrapping.OFFICE_NOT_SET, null);
			return map;
		}
		RuleEngineLogger.generateLogs(clazz, "ScrappingServiceImpl  " + sd.isRunning(), Constants.rule_log_debug,
				null);
		if (!sd.isRunning()) {
			sd.setRunning(true);
			sDao.updateScrappingSiteRunningStatus(sd);

			try {
				RuleEngineLogger.generateLogs(clazz, "ScrappingServiceImpl  " + sd.getScrappingSite().getDescription(),
						Constants.rule_log_debug, null);
				if (sd.getScrappingSite().getDescription().equalsIgnoreCase(ConstantsScrapping.ROSTER_FET)) {
					//updateStatusinSheet = true;
					ConnectAndReadSheets.updateSheetMCNADentaRunStatus(sd.getGoogleSheetId(), sd.getGoogleSubId(),
							CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, "YES",
							ConstantsScrapping.MCNA_ROSTER_ROW_INDEX_STATUS,
							ConstantsScrapping.MCNA_ROSTER_COLUMN_INDEX_STATUS);
					// MCNARosterScrappingServiceImpl m= new MCNARosterScrappingServiceImpl();
					// List<?> r=m.scrapSite(sd);
					// Runnable rr = new MCNARosterScrappingServiceImpl(sd);
					ExecutorService service = Executors.newCachedThreadPool();// FixedThreadPool(1);

					// Future<List<?>> rr =
					service.submit(new MCNARosterScrappingServiceImpl(sd, CLIENT_SECRET_DIR, CREDENTIALS_FOLDER));
					map.put(ConstantsScrapping.SCRAPPING_INIT + sd.getGoogleSheetId() + "-" + sd.getGoogleSubId(),
							null);

					// List<?> r= rr.get();
					/*
					 * ConnectAndReadSheets.updateSheetRoster(sd.getGoogleSheetId(),
					 * sd.getGoogleSubId(), CLIENT_SECRET_DIR,
					 * CREDENTIALS_FOLDER,(List<RosterDetails>)r,sd.getRowCount());
					 */
					/*
					 * map.put("Done",n); if (r!=null) { sd.setRowCount(r.size()); }else {
					 * sd.setRowCount(0); }
					 */
				} else if (sd.getScrappingSite().getDescription().equalsIgnoreCase(ConstantsScrapping.MCNA_ELIG)
						|| sd.getScrappingSite().getDescription().equalsIgnoreCase(ConstantsScrapping.DENTA_ELIG)) {
					Map<String, List<Object>> mapData = null;
					ExecutorService service = Executors.newCachedThreadPool();// FixedThreadPool(1);
					if (!dto.isIsdataFromUi() || !dto.isOnlyDisplay()) {
						ConnectAndReadSheets.updateSheetMCNADentaRunStatus(sd.getGoogleSheetId(), sd.getGoogleSubId(),
								CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, "YES", ConstantsScrapping.ELE_ROW_INDEX_STATUS,
								ConstantsScrapping.ELE_COLUMN_INDEX_STATUS);
					}
					if (!dto.isIsdataFromUi())
						mapData = ConnectAndReadSheets.readSheetMcnaDenta(sd.getGoogleSheetId(),
								sd.getGoogleSheetName(), CLIENT_SECRET_DIR, CREDENTIALS_FOLDER);
					else {
						if (dto.getListUd() != null && dto.getListUd().size() > 0) {
							int x = 0;
							MCNADentaSheet mcna = null;
							for (ScrappingUserDataInputDto d : dto.getListUd()) {

								if (!d.getDob().equals("")) {
									if (d.getSubscriberId().equals("") && d.getLastName().equals("")) {
										// do not add in map
									} else {
										if (mapData == null)
											mapData = new HashMap<>();
										mcna = new MCNADentaSheet(d.getFirstName(), d.getLastName(),
												d.getSubscriberId(), d.getDob(), d.getInsuranceName(), x + "");
										List<Object> l = new ArrayList<>();
										l.add(mcna);
										mapData.put((x++) + "", l);
									}

								}
							}
						}
					}
					if (mapData != null) {

						List<?> r = null;
						boolean update = false;
						if (!dto.isIsdataFromUi()) {
							update = true;
						}
						// Future<List<?>> rr =
						if (sd.getScrappingSite().getDescription().equalsIgnoreCase(ConstantsScrapping.MCNA_ELIG)) {
							if (update) {
								service.submit(new MCNAEligibilityScrappingServiceImpl(sd, CLIENT_SECRET_DIR,
										CREDENTIALS_FOLDER, mapData, update));
								map.put(ConstantsScrapping.SCRAPPING_INIT + sd.getGoogleSheetId() + "-"
										+ sd.getGoogleSubId(), null);

							} else {
								Future<List<?>> rr = service.submit(new MCNAEligibilityScrappingServiceImpl(sd,
										CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, mapData, update));
								r = rr.get();
								map.put("Done", r);
							}
						} else if (sd.getScrappingSite().getDescription()
								.equalsIgnoreCase(ConstantsScrapping.DENTA_ELIG)) {
							if (update) {
								service.submit(new DentaQEligibilityScrappingServiceImpl(sd, CLIENT_SECRET_DIR,
										CREDENTIALS_FOLDER, mapData, update));
								map.put(ConstantsScrapping.SCRAPPING_INIT + sd.getGoogleSheetId() + "-"
										+ sd.getGoogleSubId(), null);
							} else {
								Future<List<?>> rr = service.submit(new DentaQEligibilityScrappingServiceImpl(sd,
										CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, mapData, update));
								r = rr.get();
								map.put("Done", r);
							}
						}

						if (r != null) {
							sd.setRowCount(r.size());
						} else {
							sd.setRowCount(0);
						}

					}
				}

			} catch (IOException e) {
				RuleEngineLogger.generateLogs(clazz,
						"ScrappingServiceImpl EE " + sd.getScrappingSite().getDescription(), Constants.rule_log_debug,
						null);
				try {
					if (sd.getScrappingSite().getDescription().equalsIgnoreCase(ConstantsScrapping.ROSTER_FET)) {
						ConnectAndReadSheets.updateSheetMCNADentaRunStatus(sd.getGoogleSheetId(), sd.getGoogleSubId(),
								CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, "NO",
								ConstantsScrapping.MCNA_ROSTER_ROW_INDEX_STATUS,
								ConstantsScrapping.MCNA_ROSTER_COLUMN_INDEX_STATUS);

					}
					if (sd.getScrappingSite().getDescription().equalsIgnoreCase(ConstantsScrapping.MCNA_ELIG)
							|| sd.getScrappingSite().getDescription().equalsIgnoreCase(ConstantsScrapping.DENTA_ELIG)) {
						ConnectAndReadSheets.updateSheetMCNADentaRunStatus(sd.getGoogleSheetId(), sd.getGoogleSubId(),
								CLIENT_SECRET_DIR, CREDENTIALS_FOLDER, "NO", ConstantsScrapping.ELE_ROW_INDEX_STATUS,
								ConstantsScrapping.ELE_COLUMN_INDEX_STATUS);

					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				e.printStackTrace();
			} finally {
				sd.setRunning(false);
				sDao.updateScrappingSiteRunningStatus(sd);

			}

		} else {
			RuleEngineLogger.generateLogs(clazz, "ScrappingServiceImpl Already Running ", Constants.rule_log_debug,
					null);
			map.put("Already Running", null);

		}

		return map;
	}

	@Override
	public void updateScrapRunStatus(int siteId, Office office) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateScrapRunStatus() {

		List<ScrappingSiteDetails> list = sDao.getScrappingSiteDetailDetails();
		if (list != null) {
			for (ScrappingSiteDetails sd : list) {
				sd.setRunning(false);
				sDao.updateScrappingSiteRunningStatus(sd);
			}
		}
	}

}

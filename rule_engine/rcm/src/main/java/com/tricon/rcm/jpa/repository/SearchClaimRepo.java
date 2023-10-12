package com.tricon.rcm.jpa.repository;

import java.math.BigInteger;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import com.tricon.rcm.util.SearchClaimUtil;

@Repository
public class SearchClaimRepo {

	private static final Logger logger = LoggerFactory.getLogger(SearchClaimRepo.class);

	@PersistenceContext
	private EntityManager entityManager;

	public List<Object[]> buildSearchQuery(StringBuilder searchQuery, int pageNumber, int totalRecordsperPage) {
		String finalQuery = SearchClaimUtil.generateFinalQuery(searchQuery);
		List<Object[]> searchDto = null;
		try {
			Query query = entityManager.createNativeQuery(finalQuery);
			query.setFirstResult((pageNumber - 1) * totalRecordsperPage);
			query.setMaxResults(totalRecordsperPage);
			searchDto = query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return searchDto;
	}

	public long generateCountQuery(StringBuilder searchQuery) {
		String countQuery = SearchClaimUtil.generateCountQuery(searchQuery);
		long counts = 0;
		try {
			Query query = entityManager.createNativeQuery(countQuery);
			BigInteger countResult = (BigInteger) query.getSingleResult();
			return countResult.longValue();
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return counts;
	}
}

package com.tricon.esdatareplication.dao.repdb;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tricon.esdatareplication.entity.repdb.ESTable;

public interface ESTableRepository extends JpaRepository<ESTable, Integer> {
	
	
	@Query("SELECT count(*) FROM ESTable c")
	public List<Integer> findTotalCount();
	
	//public List<ESTable> findByCodeWrittenAndUploadedToLocal(int codeWritten,int uploadedToLocal);
	
	@Query("SELECT e FROM ESTable e where e.codeWritten = :codeWritten and e.uploadedToLocal = :uploadedToLocal order by orderTable asc")
	public List<ESTable> findByCodeWrittenAndUploadedToLocal(@Param("codeWritten") int codeWritten,@Param("uploadedToLocal") int uploadedToLocal);
	
	@Query("SELECT e FROM ESTable e where e.codeWritten= :codeWritten order by orderTable asc")
	public List<ESTable> findByCodeWritten(@Param("codeWritten") int codeWritten);
	
	

}
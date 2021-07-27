package com.tricon.esdatareplication.dao.repdb;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tricon.esdatareplication.entity.repdb.ESTable;

public interface ESTableRepository extends JpaRepository<ESTable, Integer> {
	
	
	@Query("SELECT count(*) FROM ESTable c")
	public List<Integer> findTotalCount();
	
	//public List<ESTable> findByCodeWrittenAndUploadedToLocal(int codeWritten,int uploadedToLocal);
	
	@Query("SELECT e FROM ESTable e where e.codeWritten=1 and e.uploadedToLocal=0 order by orderTable asc")
	public List<ESTable> findByCodeWrittenAndUploadedToLocal(int cw,int ul);
	
	
	

}
package com.tricon.esdatareplication;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.tricon.esdatareplication.dao.repdb.PatientRepository;
import com.tricon.esdatareplication.dao.ruleenginedb.PatientRepositoryRe;
import com.tricon.esdatareplication.entity.repdb.Patient;
import com.tricon.esdatareplication.entity.ruleenginedb.PatientReplica;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableTransactionManagement
public class JpaMultipleDBIntegrationTest {
 
    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientRepositoryRe patientRepositoryre;

    @Test
    //@Transactional("repDbTransactionManager")
    public void whenCreatingPatient_thenCreated() {
        Patient pat = new Patient();
        //pat.setId(1);
        //pat = patientRepository.save(pat);
       //List<Integer> x=patientRepository.findLastNames();
     //System.out.println(x.size()+"--"+x.get(0)+"-");
     
     
     Patient c1 = new Patient();
     Patient c2 =  new Patient();
     Patient c3 =  new Patient();
     Patient c4 =  new Patient();
     List<Patient> pats = Arrays.asList(c1, c2, c3, c4);
     
     List<Patient> ps=patientRepository.saveAll(pats);
     System.out.println(ps.size());
        //assertNotNull(patientRepository.findById(pat.getId()));
    }

    @Test
    //@Transactional("ruleEngineTransactionManager") //if we need roll back then use transactional annotation
    public void whenCreatingPatient_thenCreated1() {
        PatientReplica pat = new PatientReplica();
        //pat.setId1(1);
        pat = patientRepositoryre.save(pat);
        PatientReplica pat1 = new PatientReplica();
        PatientReplica pat2 = new PatientReplica();
        PatientReplica pat3 = new PatientReplica();

        List<PatientReplica> pats = Arrays.asList(pat1, pat2, pat3);
        List<PatientReplica> ps=patientRepositoryre.saveAll(pats);
        System.out.println(ps.size());
        
        //assertNotNull(patientRepository.findById(pat.getId()));
        
    }
 
}
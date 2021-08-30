package com.tricon.esdatareplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Starting Point of Replication Application
 * lombok -> Goto jar File (then java -jar lombo
 * k-1.x.y.jar will ask for Eclipse path)
 * Spring batch .. Spring scheduling /..Spring JPA .. Pipeline
 * https://www.baeldung.com/spring-data-jpa-multiple-databases
 * https://springframework.guru/how-to-configure-multiple-data-sources-in-a-spring-boot-application/
 * How to add new Table
 * 1.Create Common* Entity Class
 * 2. Add it in es_table
 * 3 in  QueryTable add in Query
 * 4 PrepairESDataFromFromResultSet Create Query to fetch data from ES
 * 5.ReplicationService -->
 *  a) createWhereClause 
 *  b) saveDataToLocalDB
 *  c) pushDataFromLocalESToColudDB
 *  d)
 *  6 Create new Service for each table
 */
@SpringBootApplication
@EnableScheduling
public class DataReplicationApp 
{
    public static void main( String[] args )
    {
        System.out.println( "Data Relpication App Starting!" );
        SpringApplication.run(DataReplicationApp.class);
        
    }
}

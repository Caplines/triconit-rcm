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
 */
@SpringBootApplication
@EnableScheduling
public class DataReplicationApp 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        SpringApplication.run(DataReplicationApp.class);
        
    }
}

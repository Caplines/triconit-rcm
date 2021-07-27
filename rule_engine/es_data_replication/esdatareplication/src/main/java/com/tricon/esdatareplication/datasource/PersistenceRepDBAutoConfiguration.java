package com.tricon.esdatareplication.datasource;


import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource({"classpath:persistence-multiple-db-boot.properties"})
@EnableJpaRepositories(
  basePackages = "com.tricon.esdatareplication.dao.repdb",
  entityManagerFactoryRef = "repDbEntityManager",
  transactionManagerRef = "repDbTransactionManager")
public class PersistenceRepDBAutoConfiguration {
    
	
	
	@Autowired
    private Environment env;
	
	public PersistenceRepDBAutoConfiguration() {
        super();
    }
	
	
    @Primary
    @Bean
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource repDbDataSource() {
        return DataSourceBuilder.create().build();
    }
    // userEntityManager bean 

    // userTransactionManager bean
    @Bean
    public LocalContainerEntityManagerFactoryBean repDbEntityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(repDbDataSource());
        em.setPackagesToScan("com.tricon.esdatareplication.entity.repdb");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setDatabase(Database.MYSQL);
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.generate_statistic","true");
        
        properties.put("spring.jpa.properties.hibernate.generate_statistic","false");
        properties.put("spring.jpa.properties.hibernate.jdbc.batch_size","4");
        properties.put("spring.jpa.properties.hibernate.order_inserts","true");
        properties.put("spring.jpa.properties.hibernate.order_updates","true");	

        properties.put("hibernate.generate_statistic","true");
        properties.put("hibernate.jdbc.batch_size","4");
        properties.put("hibernate.order_inserts","true");
        properties.put("hibernate.order_updates","true");	
        
        //properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        em.setJpaPropertyMap(properties);

        return em;
    }
    

    @Bean
    public PlatformTransactionManager repDbTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(repDbEntityManager().getObject());
        return transactionManager;
    }
    
    
    
    
    
    
    
    
}
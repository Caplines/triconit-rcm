package com.tricon.esdatareplication.datasource;

		
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
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
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
//@PropertySource({"classpath:persistence-multiple-db-boot.properties"})
@EnableJpaRepositories(
  basePackages = "com.tricon.esdatareplication.dao.ruleenginedb",
  entityManagerFactoryRef = "ruleEngineEntityManager",
  transactionManagerRef = "ruleEngineTransactionManager")
public class PersistenceRuleEngineDBAutoConfiguration {
    
	
	@Autowired
    private Environment env;
	
	public PersistenceRuleEngineDBAutoConfiguration() {
        super();
    }

    @Bean
    @ConfigurationProperties("app.datasource.cardholder")
    public DataSourceProperties ruleEngineDataSourceProperties() {
        return new DataSourceProperties();
    }

    
    @Bean
    //@ConfigurationProperties("app.datasource.cardholder.configuration")
    public DataSource ruleEngineDataSource() {
        return ruleEngineDataSourceProperties().initializeDataSourceBuilder()
                .type(BasicDataSource.class).build();
    }

    @Bean//(name = "ruleEngineTransactionManager")
    public LocalContainerEntityManagerFactoryBean ruleEngineEntityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(ruleEngineDataSource());
       em.setPackagesToScan("com.tricon.esdatareplication.entity.ruleenginedb");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String, Object> properties = new HashMap<String, Object>();
        //properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.hbm2ddl.auto", "update");
        
        properties.put("hibernate.jdbc.batch_size","50");
        properties.put("hibernate.order_inserts","true");
        properties.put("hibernate.order_updates","true");
        properties.put("hibernate.generate_statistics","false");
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Bean
    public PlatformTransactionManager ruleEngineTransactionManager(
            final @Qualifier("ruleEngineEntityManager") LocalContainerEntityManagerFactoryBean ruleEngineEntityManager) {
        return new JpaTransactionManager(ruleEngineEntityManager.getObject());
    }

	
	
	
	/*
	
    @Primary
    @Bean
    @ConfigurationProperties(prefix="spring.ruleengine-datasource")
    public DataSource ruleEngineDataSource() {
        return DataSourceBuilder.create().build();
    }
    // userEntityManager bean 

    // userTransactionManager bean
    @Bean
    public LocalContainerEntityManagerFactoryBean ruleEngineEntityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(ruleEngineDataSource());
       em.setPackagesToScan("com.tricon.esdatareplication.entity.ruleenginedb");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.ddl-auto","update");
        em.setJpaPropertyMap(properties);

        return em;
    }
    

    @Bean
    public PlatformTransactionManager ruleEngineTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(ruleEngineEntityManager().getObject());
        return transactionManager;
    }
   */
}

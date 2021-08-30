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
import org.springframework.beans.factory.annotation.Value;
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
    
	
	@Value("${hibernate.hbm2ddl.auto}")
	private String hibernateHbm2ddlAuto;

	@Value("${hibernate.generate_statistic}")
	private String hibernateGenerateStatistic;

	@Value("${spring.jpa.properties.hibernate.generate_statistic}")
	private String springJpaPropertiesHibernateGenerateStatistic;

	@Value("${spring.jpa.properties.hibernate.order_inserts}")
	private String springJpaPropertiesHibernateOrderInserts;

	@Value("${spring.jpa.properties.hibernate.order_updates}")
	private String springJpaPropertiesHibernateOrderUpdates;
    

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private String springJpaPropertiesHibernateJdbcBatchSize;

	@Value("${hibernate.order_inserts}")
	private String hibernateOrderInserts;

	@Value("${hibernate.order_updates}")
	private String hibernateOrderUpdates;

	@Value("${spring.jpa.show-sql}")
	private String springJpaShowSql;

	@Autowired
    private Environment env;
	
	public PersistenceRuleEngineDBAutoConfiguration() {
        super();
    }

    @Bean
    @ConfigurationProperties("spring.ruleengine-datasource")
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
        //hard code not from configuration... 
		//properties.put("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
		properties.put("hibernate.hbm2ddl.auto","validate");
		properties.put("hibernate.generate_statistic", hibernateGenerateStatistic);

		properties.put("spring.jpa.properties.hibernate.generate_statistic", springJpaPropertiesHibernateGenerateStatistic);
		properties.put("spring.jpa.properties.hibernate.order_inserts", springJpaPropertiesHibernateOrderInserts);
		properties.put("spring.jpa.properties.hibernate.order_updates", springJpaPropertiesHibernateOrderUpdates);

		properties.put("hibernate.generate_statistic", hibernateGenerateStatistic);
		properties.put("spring.jpa.properties.hibernate.jdbc.batch_size", springJpaPropertiesHibernateJdbcBatchSize);
		properties.put("hibernate.order_inserts", hibernateOrderInserts);
		properties.put("hibernate.order_updates", hibernateOrderUpdates);
		properties.put("hibernate.show_sql",springJpaShowSql);
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

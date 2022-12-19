package com.tricon.rcm.config;

import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

//import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import static org.hibernate.cfg.Environment.*;
import org.springframework.context.MessageSource;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "jpaEntityManagerFactory", transactionManagerRef = "jpaTransactionManager", basePackages = {
		"com.tricon.rcm.jpa.repository" })

public class AppConfig {
	
	@Autowired
	private Environment env;
	
	@Bean(name = "jpaDataSource")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "jpaEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("jpaDataSource") DataSource dataSource) {
		
		 Map<String, Object> properties = new HashMap<>();
		    properties.put("hibernate.hbm2ddl.auto",
		            env.getProperty("spring.jpa.hibernate.ddl-auto"));
		    
		    log.info("default ddl proper value: "+env.getProperty("spring.jpa.hibernate.ddl-auto"));
		    properties.put("hibernate.dialect",
		            env.getProperty("spring.jpa.database-platform"));
		    properties.put("hibernate.show_sql",env.getProperty("spring.jpa.show-sql"));
		    return builder
		            .dataSource(dataSource)
		            .properties(properties)
		            .packages("com.tricon.rcm.db.entity")
		            .persistenceUnit("spring")
		            .build();
		//return builder.dataSource(dataSource).packages("com.oa.FTTHPlatform.jpa").persistenceUnit("jpadb").build();
	}

	@Bean
	public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
	   return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
	}
	
	@Bean(name = "jpaTransactionManager")
	public PlatformTransactionManager jpaTransactionManager(
			
			@Qualifier("jpaEntityManagerFactory") EntityManagerFactory jpaEntityManagerFactory)
	{
		return new JpaTransactionManager(jpaEntityManagerFactory);
	}
}
/*
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.tricon.rcm.jpa.repository")
public class AppConfig {

	@Value("${spring.datasource.driver-class-name}")
	private String DB_DRIVER;

	@Value("${spring.datasource.password}")
	private String DB_PASSWORD;

	@Value("${spring.datasource.url}")
	private String DB_URL;

	@Value("${spring.datasource.username}")
	private String DB_USERNAME;

	@Value("${spring.jpa.database-platform}")
	private String HIBERNATE_DIALECT;

	@Value("${spring.jpa.show-sql}")
	private String HIBERNATE_SHOW_SQL;

	@Value("${spring.jpa.hibernate.ddl-auto}")
	private String HIBERNATE_HBM2DDL_AUTO;

	@Value("${entitymanager.packagesToScan}")
	private String ENTITYMANAGER_PACKAGES_TO_SCAN;

	@Value("${hibernate.c3p0.max_size}")
	private String CONN_POOL_MAX_SIZE;

	@Value("${hibernate.c3p0.min_size}")
	private String CONN_POOL_MIN_SIZE;

	@Value("${hibernate.c3p0.idle_test_period}")
	private String CONN_POOL_IDLE_PERIOD;
	
	@Value("${hibernate.c3p0.acquire_increment}")
	private String CONN_POOL_ACQUIRE_INC;

	@Value("${hibernate.c3p0.timeout}")
	private String CONN_POOL_TIMWEOUT;

	@Value("${hibernate.c3p0.max_statements}")
	private String CONN_POOL_MAX_STAT;


//	@Autowired
//	private Environment env;

	@Bean
	public ComboPooledDataSource dataSource() {
		// a named datasource is best practice for later jmx monitoring
		//https://chburmeister.github.io/2017/02/04/springboot-application-with-hibernate-and-c3p0-connection-pooling.html
		ComboPooledDataSource dataSource = new ComboPooledDataSource("jupiter");
		try {
			dataSource.setDriverClass(DB_DRIVER);
		} catch (PropertyVetoException pve) {
			System.out.println("Cannot load datasource driver (" + DB_DRIVER + ") : " + pve.getMessage());
			return null;
		}
		dataSource.setJdbcUrl(DB_URL);
		dataSource.setUser(DB_USERNAME);
		dataSource.setPassword(DB_PASSWORD);
		dataSource.setMinPoolSize(Integer.parseInt(CONN_POOL_MIN_SIZE));
		dataSource.setMaxPoolSize(Integer.parseInt(CONN_POOL_MAX_SIZE));
		dataSource.setMaxIdleTime(Integer.parseInt(CONN_POOL_IDLE_PERIOD));
		
		dataSource.setAcquireIncrement(Integer.parseInt(CONN_POOL_ACQUIRE_INC));
		dataSource.setMaxStatements(Integer.parseInt(CONN_POOL_MAX_STAT));
		
		// https://aodcoding.wordpress.com/2015/05/22/handling-connection-pool-issues-in-spring-boot/
		// http://christoph-burmeister.eu/?p=3093

		return dataSource;
	}

	@Bean
	public LocalSessionFactoryBean getSessionFactory() {
		LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();

		sessionFactoryBean.setDataSource(dataSource());
		sessionFactoryBean.setPackagesToScan(ENTITYMANAGER_PACKAGES_TO_SCAN);
		Properties hibernateProperties = new Properties();
		hibernateProperties.put("hibernate.dialect", HIBERNATE_DIALECT);
		hibernateProperties.put("hibernate.show_sql", HIBERNATE_SHOW_SQL);
		hibernateProperties.put("hibernate.hbm2ddl.auto", HIBERNATE_HBM2DDL_AUTO);
		hibernateProperties.put("hibernate.search.default.directory_provider", "filesystem");
		hibernateProperties.put("hibernate.search.default.indexwriter.ram_buffer_size", "64");
		sessionFactoryBean.setHibernateProperties(hibernateProperties);
		return sessionFactoryBean;
	}

	@Bean
	public HibernateTransactionManager getTransactionManager() {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(getSessionFactory().getObject());
		return transactionManager;
	}
	
	 @Bean
	    public MessageSource messageSource() {
	    	ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
	        //messageSource.setBasenames("i18/users", "i18/errormsg");
	    	messageSource.setBasenames("i18n/error");
	        messageSource.setDefaultEncoding("UTF-8");
	        return messageSource;
	    }
	 
	 @Bean
	    @ConfigurationProperties("spring.ruleengine-datasource")
	    public DataSourceProperties ruleEngineDataSourceProperties() {
	        return new DataSourceProperties();
	    }
	 
	 @Bean
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
			properties.put("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
			//properties.put("hibernate.hbm2ddl.auto","validate");
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
	  
}
  */

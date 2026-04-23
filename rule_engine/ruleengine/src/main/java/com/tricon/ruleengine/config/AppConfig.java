package com.tricon.ruleengine.config;

import java.beans.PropertyVetoException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import static org.hibernate.cfg.Environment.*;
import org.springframework.context.MessageSource;

@Configuration
@EnableTransactionManagement

public class AppConfig {

	@Value("${spring.datasource1.driver-class-name}")
	private String DB_DRIVER;

	@Value("${spring.jpa1.properties.hibernate.connection.password}")
	private String DB_PASSWORD;

	@Value("${spring.jpa1.properties.hibernate.connection.url}")
	private String DB_URL;

	@Value("${spring.jpa1.properties.hibernate.connection.username}")
	private String DB_USERNAME;

	@Value("${spring.jpa1.properties.hibernate.dialect}")
	private String HIBERNATE_DIALECT;

	@Value("${spring.jpa1.show-sql}")
	private String HIBERNATE_SHOW_SQL;

	@Value("${spring.jpa1.hibernate.ddl-auto}")
	private String HIBERNATE_HBM2DDL_AUTO;

	@Value("${entitymanager.packagesToScan}")
	private String ENTITYMANAGER_PACKAGES_TO_SCAN;

	@Value("${hibernate.c3p01.max_size}")
	private String CONN_POOL_MAX_SIZE;

	@Value("${hibernate.c3p01.min_size}")
	private String CONN_POOL_MIN_SIZE;

	@Value("${hibernate.c3p01.idle_test_period}")
	private String CONN_POOL_IDLE_PERIOD;
	
	@Value("${hibernate.c3p01.acquire_increment}")
	private String CONN_POOL_ACQUIRE_INC;

	@Value("${hibernate.c3p01.timeout}")
	private String CONN_POOL_TIMWEOUT;

	@Value("${hibernate.c3p01.max_statements}")
	private String CONN_POOL_MAX_STAT;

	@Value("${hibernate.c3p01.num_helper_threads}")
	private String CONN_POOL_NUM_HELPER_THREADS;

	@Value("${hibernate.c3p01.unreturned_connection_timeout}")
	private String CONN_POOL_UNRETURNED_TIMEOUT;


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
		// CONN_POOL_TIMWEOUT = timeout property (seconds a connection may sit idle before eviction)
		dataSource.setMaxIdleTime(Integer.parseInt(CONN_POOL_TIMWEOUT));
		// CONN_POOL_IDLE_PERIOD = idle_test_period (how often C3P0 tests idle connections, in seconds)
		dataSource.setIdleConnectionTestPeriod(Integer.parseInt(CONN_POOL_IDLE_PERIOD));
		// Test every connection before handing it to the app; prevents stale-connection errors
		dataSource.setTestConnectionOnCheckout(true);
		dataSource.setPreferredTestQuery("SELECT 1");
		dataSource.setAcquireIncrement(Integer.parseInt(CONN_POOL_ACQUIRE_INC));
		// maxStatements=0 disables the global statement cache (GooGooStatementCache).
		// With caching enabled, C3P0's 3 helper threads close evicted prepared
		// statements via ClientPreparedStatement.realClose() which can block on a
		// slow/busy MySQL connection and deadlock the entire pool. Disabling cache
		// eliminates that path entirely — statements close synchronously on the
		// app thread where they were used, not through the shared helper pool.
		dataSource.setMaxStatements(Integer.parseInt(CONN_POOL_MAX_STAT));
		// More helper threads means pool management (connection test/refurbish) is
		// not starved even if a few tasks take longer than expected.
		dataSource.setNumHelperThreads(Integer.parseInt(CONN_POOL_NUM_HELPER_THREADS));
		// Force-reclaim connections that haven't been returned after this many seconds.
		// Prevents a single slow/leaked operation from exhausting the pool permanently.
		dataSource.setUnreturnedConnectionTimeout(Integer.parseInt(CONN_POOL_UNRETURNED_TIMEOUT));
		dataSource.setDebugUnreturnedConnectionStackTraces(true);
		
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
		// Parse as boolean so only "true" enables stdout "Hibernate: ..." SQL (avoids accidental truthy strings).
		hibernateProperties.put("hibernate.show_sql",
				Boolean.toString(Boolean.parseBoolean(HIBERNATE_SHOW_SQL != null ? HIBERNATE_SHOW_SQL.trim() : "false")));
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
}

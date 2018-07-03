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
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import static org.hibernate.cfg.Environment.*;

@Configuration
@EnableTransactionManagement

public class AppConfig {

	@Value("${spring.datasource.driver-class-name}")
	private String DB_DRIVER;

	@Value("${spring.jpa.properties.hibernate.connection.password}")
	private String DB_PASSWORD;

	@Value("${spring.jpa.properties.hibernate.connection.url}")
	private String DB_URL;

	@Value("${spring.jpa.properties.hibernate.connection.username}")
	private String DB_USERNAME;

	@Value("${spring.jpa.properties.hibernate.dialect}")
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

	@Autowired
	private Environment env;

	@Bean
	public ComboPooledDataSource dataSource() {
		// a named datasource is best practice for later jmx monitoring
		ComboPooledDataSource dataSource = new ComboPooledDataSource("jupiter");
		System.out.println("9999999999999999999999999999999999999");
		try {
			dataSource.setDriverClass(DB_DRIVER);
			System.out.println("9999999999999999999999999999999999999");
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
}

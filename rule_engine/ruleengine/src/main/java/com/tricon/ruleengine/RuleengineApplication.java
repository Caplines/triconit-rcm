package com.tricon.ruleengine;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//https://o7planning.org/en/11543/create-a-login-application-with-spring-boot-spring-security-spring-jdbc
//for reference we can use
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * @author Deepak.Dogra
 *	
 */
//https://graph.microsoft.com/v1.0/me/drive/root/children from graph explorer to get ID of sheet..
/* 
/* to create build of front end--- ng  build --prod*/ //for local its npm start 
//from cmd in folder \capline\rule_engine\ruleengine>   mvn package
/* CD /opt/tomcat
 * FOR PRODCTION MAKE SURE 
 * in application.properties u have spring.profiles.active=prod
 *  prod.properties -->application.url needs change when needed
 * dev is for Developers
 * in Front end 
 * app.component.ts static API_URL="http://ip"; 
 * angular.json line no 17 "index": "src/index.jsp",
 * Password --capline 
 * 
 */

@SpringBootApplication(exclude = { JpaRepositoriesAutoConfiguration.class,
 HibernateJpaAutoConfiguration.class})
public class RuleengineApplication extends SpringBootServletInitializer{

	 
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(RuleengineApplication.class);
	}

	public static void main(String[] args) throws Exception {
		
		SpringApplication.run(RuleengineApplication.class, args);
	}
	
	/*
	 * https://apps.dev.microsoft.com
	 * office365@caplineservices.com     Smilepoint00 for corbsy

	 */
}

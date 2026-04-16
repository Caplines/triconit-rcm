package com.tricon.ruleengine;
import java.util.logging.Level;

//https://o7planning.org/en/11543/create-a-login-application-with-spring-boot-spring-security-spring-jdbc
//for reference we can use
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


/**
 * @author Deepak.Dogra
 *	
 */
//https://graph.microsoft.com/v1.0/me/drive/root/children from graph explorer to get ID of sheet..
/* https://github.com/SeleniumHQ/htmlunit-driver/releases
/* to create build of front end--- ng build --prod*/ //for local its npm start
//nvm --version nvm list ->nvm use 14.17.0
//3.23.130.139
//from cmd in folder \capline\rule_engine\ruleengine>mvn clean then   mvn initialize package
/* CD /opt/tomcat Delete root Folder in webapps
 * FOR PRODUCTION MAKE SURE 
 * in application.properties u have spring.profiles.active=prod
 *  prod.properties -->check env properties in Angular APP application.url needs change when needed
 *  CapDent$1 Pukumar@321
 *  admin_admin R&D00
 * dev is for Developers
 * test_test_cl --> 12345678 user6_user6_cl 12345678
 * in Front end 
 * app.component.ts static API_URL="http://ip"; 
 * angular.json line no 17 "index": "src/index.jsp",
 * Password --capline 
 * aws location cd /opt/project/tricon/client/
 * cd /opt/tomcat
 */

@SpringBootApplication(exclude = { JpaRepositoriesAutoConfiguration.class,
 HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class })
public class RuleengineApplication extends SpringBootServletInitializer{

	/**
	 * This method is still needed for backward compatibility with WAR deployment
	 * but is optional for standalone JAR execution
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(RuleengineApplication.class);
	}

	/**
	 * Main entry point for standalone JAR execution
	 */
	public static void main(String[] args) throws Exception {
		SpringApplication.run(RuleengineApplication.class, args);
	}
	
	/*	
	 * https://apps.dev.microsoft.com
	 * office365@caplineservices.com     Smilepoint00 for corbsy

	 */
}

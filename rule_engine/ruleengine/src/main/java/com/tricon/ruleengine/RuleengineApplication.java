package com.tricon.ruleengine;
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

/* from cmd >mvn package*/
/* to create build of front end--- npm run --prod*/

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
}

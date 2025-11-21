package com.tricon.rcm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
//@EnableWebMvc
@EnableScheduling
@EnableAspectJAutoProxy
public class RcmApplication {

	//>mvn clean then mvn package >>mvn clean package
	//ng build --configuration production
	////nvm --version nvm list ->nvm use 14.17.0
	public static void main(String[] args) {
		SpringApplication.run(RcmApplication.class, args);
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	/*@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
	    http
	        // ...
	        .redirectToHttps(withDefaults());
	    return http.build();
	}**/

}

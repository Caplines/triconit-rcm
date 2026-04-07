package com.tricon.rcm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
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
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(60_000);
		factory.setReadTimeout(1_800_000); // 30 min — matches nginx proxy_read_timeout
		return new RestTemplate(factory);
	}
	
	/*@Bean
	SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
	    http
	        // ...
	        .redirectToHttps(withDefaults());
	    return http.build();
	}**/

}

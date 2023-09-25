package com.tricon.rcm.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.tricon.rcm.jwt.service.JwtUserDetailsService;
import static org.springframework.security.config.Customizer.withDefaults;

/**
 * @author Deepak.Dogra
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

	@Autowired
	private CrossDomainCsrfTokenRepository csrfTokenRepository;

	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Value("${jwt.header}")
	private String tokenHeader;

	
	@Value("${jwt.headerC}")
	private String tokenHeaderClient;
	
	@Value("${jwt.headerR}")
	private String tokenHeaderRole;
	
	@Value("${jwt.headerT}")
	private String tokenHeaderTeam;
	    
	    
	@Value("${jwt.route.authentication.path}")
	private String authenticationPath;
	
	@Value("${jwt.route.testing.authentication.path}")
	private String authenticationPathForTesting;

	/*
	 * @Autowired public void configureGlobal(AuthenticationManagerBuilder auth)
	 * throws Exception { auth .userDetailsService(jwtUserDetailsService)
	 * .passwordEncoder(passwordEncoderBean()); }
	 */

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoderBean() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.cors().and()
				// we don't need CSRF because our token is invulnerable
				.csrf().disable()
				//
				// https://stackoverflow.com/questions/36261781/x-csrf-token-is-not-generated-by-spring-booot
				// .csrf().csrfTokenRepository(csrfTokenRepository).and()//added for CSRF...
				// .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()//added
				// for CSRF this worked by cookie is visible...
				.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

				// don't create session
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

				.authorizeRequests()

				// Un-secure H2 Database
				// .antMatchers("/h2-console/**/**").permitAll()

				.antMatchers("/auth/**").permitAll().anyRequest().authenticated();

		/*
		 * http .authorizeHttpRequests((authz) -> authz .anyRequest().authenticated() )
		 * .httpBasic(withDefaults());
		 */
		JwtAuthorizationTokenFilter authenticationTokenFilter = new JwtAuthorizationTokenFilter(jwtUserDetailsService,
				jwtTokenUtil, tokenHeader,tokenHeaderClient,tokenHeaderRole,tokenHeaderTeam);
		http.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
		// return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().antMatchers(HttpMethod.POST, authenticationPath,"/forgotPassword",authenticationPathForTesting).and()
				.ignoring().antMatchers(HttpMethod.GET,"/swagger-ui.html",
						"/login","/tool-update", "/fetch-claims","/users-status","/register","/manage-office",
						"/manage-client","/user-setting","/claim-assignment","/billing-claims/*","/billing-claims/*/ivf",
						"/billing-claims/{pathvariable:[0-9A-Za-z]+}/ivf",
						"/billing-claims/{pathvariable:[0-9A-Za-z]+}/tp",
						"/billing-claims/*/tp","/tool-update/issue-claims",
						"/list-of-claims","/all-pendency","/production","/update-pass",
						"/api/testSVSheet","/search-claims",
						 "/*.html",
			                "/index.jsp",
			                "/favicon.ico",
			                "/**/*.html",
			                "/**/*.css",
			                "/**/*.jpg",
			                "/**/*.ttf",
			                "/**/*.png",
			                "/**/*.js",
			                "/**/*.woff2",
			                "/**/*.pdf",
			                "/swagger-resources/configuration/ui",
			                "/swagger-resources/configuration/security",
			                "/swagger-resources",
			                "/csrf",
			                "/",
			                "/v2/api-docs",
						    "/webjars/*",
						    "/master/**");
	}

	/*
	 * @Override public void configure(WebSecurity web) throws Exception { //
	 * AuthenticationTokenFilter will ignore the below paths web .ignoring()
	 * .antMatchers( HttpMethod.POST, authenticationPath, "/savedatatore",
	 * "/savedatatoreCheck", "/savedatatoreos", "/queryivdatafromdb",
	 * "/queryivosdatafromdb", "/queryivdatafromdbTemp" )
	 * 
	 * // allow anonymous resource requests .and() .ignoring() .antMatchers(
	 * HttpMethod.GET, "/queryivdatatopdf",
	 * 
	 * "/", "/*.html", "/index.jsp", "/favicon.ico",
	 * 
	 * ).antMatchers(HttpMethod.POST, "/account/register", "/readDriveSuc"
	 * 
	 * )
	 * 
	 * // Un-secure H2 Database (for testing purposes, H2 console shouldn't be
	 * unprotected in production) .and() .ignoring() .antMatchers("/h2-console/**");
	 * }
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token", "_csrf","r","t","c",
				CrossDomainCsrfTokenRepository.XSRF_HEADER_NAME));
		configuration.setExposedHeaders(
				Arrays.asList("x-auth-token", CrossDomainCsrfTokenRepository.XSRF_HEADER_NAME, "_csrf"));
		// configurationconfiguration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}

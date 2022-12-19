package com.tricon.ruleengine.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.tricon.ruleengine.security.service.JwtUserDetailsService;

/**
 * @author Deepak.Dogra
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	
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

    @Value("${jwt.route.authentication.path}")
    private String authenticationPath;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(jwtUserDetailsService)
            .passwordEncoder(passwordEncoderBean());
    }

    @Bean
    public PasswordEncoder passwordEncoderBean() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
		.cors().and()
            // we don't need CSRF because our token is invulnerable
            .csrf().disable()
		//
		//https://stackoverflow.com/questions/36261781/x-csrf-token-is-not-generated-by-spring-booot
		   //   .csrf().csrfTokenRepository(csrfTokenRepository).and()//added for CSRF...
		   //   .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()//added for CSRF this worked by cookie is visible...
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

            // don't create session
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

            .authorizeRequests()

            // Un-secure H2 Database
            //.antMatchers("/h2-console/**/**").permitAll()

            .antMatchers("/auth/**").permitAll()
            .anyRequest().authenticated();

        // Custom JWT based security filter
        JwtAuthorizationTokenFilter authenticationTokenFilter = new JwtAuthorizationTokenFilter(userDetailsService(), jwtTokenUtil, tokenHeader);
        httpSecurity
            .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // disable page caching
        httpSecurity
            .headers()
            .frameOptions();//.sameOrigin()  // required to set for H2 else H2 Console will be blank.
            //.cacheControl();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // AuthenticationTokenFilter will ignore the below paths
        web
            .ignoring()
            .antMatchers(
                HttpMethod.POST,
                authenticationPath,
                "/savedatatore",
                "/savedatatoreCheck",
                "/savedatatoreos",
                "/queryivdatafromdb",
                "/queryivosdatafromdb",
                "/queryivdatafromdbTemp"
            )

            // allow anonymous resource requests
            .and()
            .ignoring()
            .antMatchers(
                HttpMethod.GET,
                "/queryivdatatopdf",
                "/queryivosdatatopdf",
                "/queryivdatatohtml",
                "/.well-known/acme-challenge/8h0NwOQLZreL70OMOZtKMYcM5W2Fme1JUatgJFuTElA",
                "/.well-known/acme-challenge/AoJiyMAei-mA1StzpKpf22vgqefQLxk8GgBsa3yplpA",
                "/queryivdatafromdbgoogle",
                "/queryivdatahistoryfromdbgoogle",
                "/open/*",
                "/login",
                "/readDriveSuc",
                "/sharePointIni",
                "/appdebug/*.txt",
                "/googleReport",
                "/savedatatore",
                "/savedatatoreos",
                "/savedatatoreCheck",
                "/googleReport2",
                "/googleESReport",
                 "/ivf",
                 "/dumpOldIVFData",
                 "/sealant",
                 "/scrapremotelite",
                "/ivfbatch",
                "/ivfbatchpre",
                "/report",
                "/register",
                "/writeData",
                "/enreports",
                "/scrap",
                //"/fetchesdata/*",
                //"/fetchesdata/*/*",
                "/ivftreatmentplan",
                "/diagnosticcheck",
                "/finduserbyusername",
                "/googleESReportReplication",
                "/ivfclaimid",
                "/ivfcl",
                "/ivfclbatch",
                "/reportcl",
                "/enreportscl",
                "/usersettings",
                "/scrapfulldata",
                "/rulereport",
                "/userinput",
                "/extIVF",
                "/fetch-claims",
                "/fetch-insurance",
                "/remote-lite-details",
                "/allrulereport",
                "/",
                "/*.html",
                "/index.jsp",
                "/favicon.ico",
                "/**/*.html",
                "/**/*.css",
                "/**/*.jpg",
                "/**/*.ttf",
                "/**/*.png",
                "/**/*.js"
            ).antMatchers(HttpMethod.POST,
                "/account/register",
                "/readDriveSuc"
                
                )

            // Un-secure H2 Database (for testing purposes, H2 console shouldn't be unprotected in production)
            .and()
            .ignoring()
            .antMatchers("/h2-console/**/**");
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token","_csrf",CrossDomainCsrfTokenRepository.XSRF_HEADER_NAME));
        configuration.setExposedHeaders(Arrays.asList("x-auth-token",CrossDomainCsrfTokenRepository.XSRF_HEADER_NAME,"_csrf"));
       //configurationconfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}


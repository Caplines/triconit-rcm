package com.tricon.ruleengine.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


import com.tricon.ruleengine.security.service.UserDetailsServiceImpl;
 ////https://o7planning.org/en/11543/create-a-login-application-with-spring-boot-spring-security-spring-jdbc
//https://dzone.com/articles/angular-2-and-spring-boot-development-environment
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
 
    @Autowired
    UserDetailsServiceImpl userDetailsService;
 
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }
     
 // This method is for overriding the default AuthenticationManagerBuilder.
 	// We can specify how the user details are kept in the application. It may
 	// be in a database, LDAP or in memory.
 	@Override
 	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
 		auth.userDetailsService(userDetailsService);
 	}
     
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { 
 
        // Setting Service to find User in the database.
        // And Setting PassswordEncoder
    	System.out.println("98989898899");
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());     
 
    }
   
 
    ///@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and()
		// starts authorizing configurations
		.authorizeRequests()
		// ignoring the guest's urls "
		.antMatchers("/account/register","/account/login","/open/*",
				"/logout","/","/resources/*","/", "/styles.*", "/images/**","/styles.*", "/runtime.*","/polyfills.*","/main.*").permitAll()
		//http.authorizeRequests().antMatchers("/css/**", "/js/**", "/images/**").permitAll()
		// authenticate all remaining URLS
		.anyRequest().fullyAuthenticated().and()
      /* "/logout" will log the user out by invalidating the HTTP Session,
       * cleaning up any {link rememberMe()} authentication that was configured, */
		.logout()
        .permitAll()
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
        .and()
		// enabling the basic authentication
		.httpBasic().and()
		// configuring the session on the server
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and()
		// disabling the CSRF - Cross Site Request Forgery
		.csrf().disable();
	}
    
    //@Override
    protected void configurertewtyre(HttpSecurity http) throws Exception {
 //https://docs.spring.io/spring-security/site/docs/current/reference/html/csrf.html
        http.csrf().disable();
 
        // The pages does not require login
        //http.authorizeRequests().antMatchers("/", "/login", "/logout").permitAll();
    	http.authorizeRequests().antMatchers("/account/register","/account/login","/login",
    			"/logout","/","/resources/*","/", "/styles.*", "/images/**","/styles.*", "/runtime.*","/polyfills.*","/main.*").permitAll()
        // /userInfo page requires login as ROLE_USER or ROLE_ADMIN.
        // If no login, it will redirect to /login page.
    	// authenticate all remaining URLS
    	.anyRequest().fullyAuthenticated().and()
        //http.authorizeRequests().antMatchers("/userInfo").access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')");
    	.logout()
        .permitAll()
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
        .and()
		// enabling the basic authentication
		.httpBasic().and()
		// configuring the session on the server
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
        // For ADMIN only.
        http.authorizeRequests().antMatchers("/admin").access("hasRole('ROLE_ADMIN')").and().exceptionHandling().accessDeniedPage("/403");
 
        // When the user has logged in as XX.
        // But access a page that requires role YY,
        // AccessDeniedException will be thrown.
       // http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");
 
        // Config for Login Form
        /*
        http.authorizeRequests().and().formLogin()//
                // Submit URL of login page.
                .loginProcessingUrl("/j_spring_security_check") // Submit URL
                .loginPage("/login")//
                .defaultSuccessUrl("/userAccountInfo")//
                .failureUrl("/login?error=true")//
                .usernameParameter("username")//
                .passwordParameter("password")
                // Config for Logout Page
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/logoutSuccessful");
        */
    }
}

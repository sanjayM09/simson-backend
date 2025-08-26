package com.example.kkBazar.service.user;

//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//	 @Override
//	    protected void configure(HttpSecurity http) throws Exception {
//	        http
//	            .authorizeRequests()
//	            .antMatchers("/user/login").permitAll() // Public login endpoint
//	            .anyRequest().authenticated() // All other endpoints require authentication
//	            .and()
//	            .oauth2Login() // Enable OAuth2 login
//	            .loginPage("/oauth2/authorization/google") // OAuth2 login endpoint
//	            .defaultSuccessUrl("/home", true) // Default success URL after login
//	            .failureUrl("/login?error=true"); // Failure URL
//	    }
//}

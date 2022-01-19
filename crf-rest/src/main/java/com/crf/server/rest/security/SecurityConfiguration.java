package com.crf.server.rest.security;

import static com.crf.server.rest.security.SecurityConstants.LOGIN_PAGE_URL;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService    userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SecurityConfiguration(MyUserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/tools/status");
        web.ignoring().antMatchers("/system/recaptchaKey");
        web.ignoring().antMatchers("/registration/**", "/adminregistration/**");
    }

    @Override
    protected final void configure(HttpSecurity http) throws Exception {
        http = http.cors().and().csrf().disable();
        
        http = http
            .sessionManagement()
            .invalidSessionUrl(LOGIN_PAGE_URL)
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and();
        
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/",
                    "/swagger-ui.html",
                    "/*.*",
                    LOGIN_PAGE_URL,
                    "/v2/api-docs",
                    "/swagger-resources/**",
                    "/resources/**",
                    "/assets/**",
                    "/webjars/**").permitAll()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/crf/**", "/accountsetup/**", "/upload/accountsetup/**", "/operator/**", "/depositproduct/**").hasAnyRole("1", "2", "100")
                .antMatchers("/depositaccount/**").hasRole("1")
                .antMatchers("/customer/**").hasAnyRole("1", "2", "3").antMatchers("/admin/**", "/customeradmin/**", "/depositaccountadmin/**", "/depositproductadmin**").hasRole(
            "100")
                .antMatchers("/system/**").authenticated()
                .anyRequest().authenticated();

        http.addFilterBefore(new ExceptionHandlerFilter(), JWTAuthenticationFilter.class)
            .addFilter(new JWTAuthenticationFilter(authenticationManager(), getApplicationContext()))
            .addFilter(new JWTAuthorizationFilter(authenticationManager(), userDetailsService)).logout().clearAuthentication(true).logoutSuccessUrl(LOGIN_PAGE_URL);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }
}

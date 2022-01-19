package com.crf.server.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private MyUserDetailsService myUserDetailsService;

    @Autowired
    public void setMyUserDetailsService(MyUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(myUserDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // permit all
        web.ignoring().antMatchers("/registration.html*");
        web.ignoring().antMatchers("/registration/**");
        web.ignoring().antMatchers("/adminregistration/registrationUser.html*");
        web.ignoring().antMatchers("/adminregistration/process");
        web.ignoring().antMatchers("/upload/accountsetup/singleUploadByPhone");
        web.ignoring().antMatchers("/mobileUpload.html");
        web.ignoring().antMatchers("/passwordForgot.html");
        web.ignoring().antMatchers("/passwordForgotChange.html");
        web.ignoring().antMatchers("/login/password/forgot/send");
        web.ignoring().antMatchers("/login/password/forgot/change");
        web.ignoring().antMatchers("/credentialsExpired.html");
        web.ignoring().antMatchers("/login/password/expired/update");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {

        http.headers().frameOptions().sameOrigin();

        http.csrf().disable().authorizeRequests().antMatchers("/app/**", "/lib/**", "/test").permitAll()
            .antMatchers("/crf/**", "/accountsetup/**", "/upload/accountsetup/**", "/operator/**", "/depositproduct/**").hasAnyRole("1", "2", "100")
            .antMatchers("/depositaccount/**").hasRole("1").antMatchers("/customer/**").hasAnyRole("1", "2", "3")
            .antMatchers("/admin*", "/users*","/customeradmin/**", "/depositaccountadmin/**", "/depositproductadmin**").hasRole("100").antMatchers("/login*").permitAll().anyRequest().authenticated().and().formLogin()
            .loginPage("/login.html").usernameParameter("input1").passwordParameter("input2").loginProcessingUrl("/perform_login").defaultSuccessUrl("/crf.html", true)
            .successHandler(authenticationSuccessHandler()).failureUrl("/login.html?failure").failureHandler(authenticationFailureHandler()).and().logout()
            .logoutUrl("/perform_logout").deleteCookies("JSESSIONID").logoutSuccessHandler(logoutSuccessHandler());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
}

package com.app.core.security

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

/**
 * Spring Security Java Configuration
 *
 * @author  Hans Christian Ang
 * @version 0.1
 */
@CompileStatic
@EnableWebSecurity
class SecurityConfig extends WebSecurityConfigurerAdapter  {

    @Autowired
    UserDetailsService userDetailsService
    @Autowired
    AuthenticationEntryPoint authenticationEntryPoint
    @Autowired
    TokenAuthenticationFilter tokenAuthenticationFilter

    @CompileStatic(TypeCheckingMode.SKIP)
    @Override
    void configure(HttpSecurity http) throws Exception {
        http
        .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeRequests()
            .antMatchers(HttpMethod.POST, "user/update").hasRole("ADMIN")
            .anyRequest().authenticated()
            .and()
        .httpBasic().disable()
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .exceptionHandling()
            .authenticationEntryPoint(authenticationEntryPoint)

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
        .antMatchers(HttpMethod.OPTIONS, "/**/*")
        .antMatchers("favicon.ico")
        .antMatchers("/auth/login/**")
        .antMatchers("/auth/logout")
        .antMatchers(HttpMethod.POST, "/user/create")
    }

    @Autowired
    void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder())
    }

    /////// Bean Registration

    /**
     * Expose AuthenticationManager as a Spring Bean to be used by <code>TokenAuthenticationFilter</code>
     * @return AuthenticationManager
     * @throws Exception
     */
    @Bean(name="authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean()
    }

    /**
     * Password Encoder used in <code>DaoAuthenticationProvider</code>
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }

    /**
     * Configure <code>DaoAuthenticationProvider</code>
     * @return DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(userDetailsService)
        authenticationProvider.setPasswordEncoder(passwordEncoder())
        return authenticationProvider
    }

    /**
     * Register {@link TokenAuthenticationFilter} to Spring Security Filter Chain
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean tokenAuthenticationFilterRegistration() {
        FilterRegistrationBean bean = new FilterRegistrationBean()
        bean.setFilter(tokenAuthenticationFilter)
        bean.setEnabled(false)
        return bean
    }

    /**
     * Register {@link CorsFilter} to Spring Security Filter Chain
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean corsFilter() {
        // Set up CORS configuration
        CorsConfiguration config = new CorsConfiguration()
        config.setAllowCredentials(true)
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")

        // Set up URL source for CORS configuration
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config);

        // Register bean
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source))
        bean.setOrder(0)
        return bean
    }
}

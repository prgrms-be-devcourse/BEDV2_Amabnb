package com.prgrms.amabnb.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;

import com.prgrms.amabnb.security.handler.ExceptionHandlerFilter;
import com.prgrms.amabnb.security.handler.JwtAuthenticationEntryPoint;
import com.prgrms.amabnb.security.handler.OAuthAuthenticationSuccessHandler;
import com.prgrms.amabnb.security.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuthAuthenticationSuccessHandler oAuth2SuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors()
            .and()

            .authorizeHttpRequests()
            .antMatchers("/tokens").permitAll()
            .antMatchers("/docs/**").permitAll()
            .antMatchers("/favicon.ico").permitAll()
            .antMatchers(HttpMethod.GET, "/rooms/**").permitAll()
            .anyRequest().authenticated()
            .and()

            .oauth2Login()
            .successHandler(oAuth2SuccessHandler)
            .and()

            .httpBasic().disable()
            .rememberMe().disable()
            .csrf().disable()
            .logout().disable()
            .requestCache().disable()
            .formLogin().disable()
            .headers().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()

            .exceptionHandling()
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and()

            .addFilterBefore(jwtAuthenticationFilter, OAuth2AuthorizationRequestRedirectFilter.class)
            .addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class);
        return http.build();
    }
}

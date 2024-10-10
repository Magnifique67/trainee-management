package com.user_management.user_management_service.config;

import com.user_management.user_management_service.filter.JwtAuthenticationFilter;
import com.user_management.user_management_service.util.errorHandler.ForbiddenErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ForbiddenErrorHandler forbiddenErrorHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuthenticationProvider authenticationProvider,
            ForbiddenErrorHandler forbiddenErrorHandler
    ) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.forbiddenErrorHandler = forbiddenErrorHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/users/login").permitAll() // Ensure login is permitted
                .requestMatchers("/users/v3/api-docs", "/users/swagger-ui.html", "/webjars/**", "/users/OAuth2", "/users/{id}/password").permitAll()
                .requestMatchers("/trainees/**", "/users/**").hasAnyRole("ADMIN","TRAINER") // Allow ADMIN access
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().accessDeniedHandler(forbiddenErrorHandler) // Custom error handler for forbidden access
                .and()
                .authenticationProvider(authenticationProvider)
                .build(); // Ensure only one build() call
    }
}

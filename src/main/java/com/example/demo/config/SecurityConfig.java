package com.example.demo.config;


 import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Optional: Disable CSRF for simplicity during development
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/search").permitAll() // Allow root and search page
                        .requestMatchers("/api/users/register", "/api/properties").permitAll() // Existing public APIs
                        .anyRequest().authenticated() // All other endpoints require auth
                )
                .httpBasic();
        return http.build();
    }
}
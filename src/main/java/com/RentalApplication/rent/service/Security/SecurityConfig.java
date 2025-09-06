package com.RentalApplication.rent.service.Security;

import com.RentalApplication.rent.service.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private final JWTAuthenticationFilter jwtAuthFilter;
    private final UsersDetailsService userDetailsService;
    private final UserRepository userRepository;



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JWTAuthenticationFilter jwtFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                           .requestMatchers("/api/v1/users/login", "/api/v1/users/register" ).permitAll() // login, register endpoints
                              .requestMatchers("/api/v1/users/admin/**").hasRole("Admin")                 // admin endpoints
                             .requestMatchers("/api/v1/users/owner/**").hasRole("Owner")                 // owner endpoints
                       .requestMatchers( "/api/v1/users/client/**").hasRole("Client")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }








    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}







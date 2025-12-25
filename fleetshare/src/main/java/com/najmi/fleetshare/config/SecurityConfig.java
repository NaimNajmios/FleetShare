package com.najmi.fleetshare.config;

import com.najmi.fleetshare.security.CustomAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Public resources
                        .requestMatchers("/css/**", "/js/**", "/assets/**", "/images/**").permitAll()
                        // Public pages
                        .requestMatchers("/", "/register").permitAll()
                        .requestMatchers("/login").permitAll()
                        // Role-based URL protection
                        .requestMatchers("/admin/**").hasRole("PLATFORM_ADMIN")
                        .requestMatchers("/owner/**").hasRole("FLEET_OWNER")
                        .requestMatchers("/renter/**").hasRole("RENTER")
                        // All other requests require authentication
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                        .successHandler(customAuthenticationSuccessHandler())
                        .failureUrl("/login?error=true"))
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied"));

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

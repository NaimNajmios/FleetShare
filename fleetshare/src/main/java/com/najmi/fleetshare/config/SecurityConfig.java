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
import org.springframework.security.web.header.writers.PermissionsPolicyHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .headers(headers -> headers
                                                .contentSecurityPolicy(csp -> csp
                                                                .policyDirectives("default-src 'self'; " +
                                                                                "script-src 'self' 'unsafe-inline' https://unpkg.com https://cdn.jsdelivr.net; "
                                                                                +
                                                                                "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com https://unpkg.com https://cdnjs.cloudflare.com https://cdn.jsdelivr.net; "
                                                                                +
                                                                                "img-src 'self' data: https://*.tile.openstreetmap.org; "
                                                                                +
                                                                                "font-src 'self' https://fonts.gstatic.com https://cdnjs.cloudflare.com data:; "
                                                                                +
                                                                                "object-src 'none'; " +
                                                                                "base-uri 'self'; " +
                                                                                "form-action 'self'; " +
                                                                                "frame-ancestors 'self';"))
                                                .httpStrictTransportSecurity(hsts -> hsts
                                                                .includeSubDomains(true)
                                                                .maxAgeInSeconds(31536000))
                                                .frameOptions(frame -> frame.sameOrigin())
                                                .referrerPolicy(referrer -> referrer
                                                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                                                .addHeaderWriter(new PermissionsPolicyHeaderWriter(
                                                                "camera=(), microphone=(), geolocation=(), payment=()")))
                                .authorizeHttpRequests(auth -> auth
                                                // Public resources
                                                .requestMatchers("/css/**", "/js/**", "/assets/**", "/images/**")
                                                .permitAll()
                                                // Public pages
                                                .requestMatchers("/", "/register", "/api/test/email/**").permitAll()
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

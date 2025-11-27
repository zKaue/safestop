package com.safestop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// O import do AntPathRequestMatcher foi removido

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // --- REGRAS CORRIGIDAS (sem 'AntPathRequestMatcher') ---
                        .requestMatchers(
                                "/",
                                "/h2-console/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico",
                                "/api/**",
                                "/login",
                                "/register"
                        ).permitAll()

                        // Exige autenticação para QUALQUER outra URL
                        .anyRequest().authenticated()
                )
                // DIZ AO SPRING ONDE ESTÁ NOSSA PÁGINA DE LOGIN
                .formLogin(form -> form
                        .loginPage("/login") // Nossa página de login customizada
                        .loginProcessingUrl("/login") // A URL que o <form> envia (padrão)
                        .defaultSuccessUrl("/", true) // Para onde vai depois do login
                        .failureUrl("/login?error=true") // Para onde vai se o login FALHAR
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout") // Volta para o login
                        .permitAll()
                );

        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
        http.csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
}
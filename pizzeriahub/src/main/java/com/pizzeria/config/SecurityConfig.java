package com.pizzeria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, LoginSuccessHandler successHandler) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/", "/index", "/menu", "/css/**", "/js/**", "/images/**", "/login", "/registro", "/debug/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/cocina/**").hasAnyRole("COCINA", "ADMIN")
                .requestMatchers("/delivery/**").hasAnyRole("DELIVERY", "MOTORIZADO", "ADMIN")
                .requestMatchers("/perfil/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .invalidSessionUrl("/login?session=expired")
                .maximumSessions(-1)  // Sin límite de sesiones
                .sessionRegistry(sessionRegistry())
                .expiredUrl("/login?session=expired")
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(successHandler)  // Handler de login personalizado
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // <-- ESTE ES EL CAMBIO
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .rememberMe(rememberMe -> rememberMe
                .key("pizzeriaHubSecretKey")
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(86400) // 1 día
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}
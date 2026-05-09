package com.securebank.config;

import com.securebank.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation
        .Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication
        .AuthenticationManager;
import org.springframework.security.authentication
        .dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation
        .authentication.configuration
        .AuthenticationConfiguration;
import org.springframework.security.config.annotation
        .method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation
        .web.builders.HttpSecurity;
import org.springframework.security.config.annotation
        .web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt
        .BCryptPasswordEncoder;
import org.springframework.security.crypto.password
        .PasswordEncoder;
import org.springframework.security.web
        .SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService
            userDetailsService;
    private final CustomAuthSuccessHandler
            successHandler;
    private final CustomAuthenticationFailureHandler
            failureHandler;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            CustomAuthSuccessHandler successHandler,
            @Lazy CustomAuthenticationFailureHandler
                    failureHandler) {
        this.userDetailsService = userDetailsService;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                // Static resources
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/webjars/**",
                    "/favicon.ico"
                ).permitAll()

                // Auth pages
                .requestMatchers(
                    "/auth/**"
                ).permitAll()

                // API docs
                .requestMatchers(
                    "/auth/api-docs"
                ).permitAll()

                // Public API endpoints
                .requestMatchers(
                    "/api/v1/auth/register",
                    "/api/v1/auth/login"
                ).permitAll()

                // Role-based web routes
                .requestMatchers(
                    "/admin/**"
                ).hasRole("ADMIN")

                .requestMatchers(
                    "/manager/**"
                ).hasAnyRole(
                    "ADMIN",
                    "MANAGER"
                )

                .requestMatchers(
                    "/teller/**"
                ).hasAnyRole(
                    "ADMIN",
                    "MANAGER",
                    "TELLER"
                )

                .requestMatchers(
                    "/customer/**"
                ).hasRole("CUSTOMER")

                // API routes — authenticated
                .requestMatchers(
                    "/api/**"
                ).authenticated()

                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl(
                        "/auth/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl(
                        "/auth/login?expired")
            )

            .exceptionHandling(ex -> ex
                .accessDeniedPage(
                        "/auth/access-denied")
            )

            // Allow API calls from Postman/mobile
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                        "/api/**")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider
            authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(
                userDetailsService);

        provider.setPasswordEncoder(
                passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager
            authenticationManager(
                    AuthenticationConfiguration config)
            throws Exception {

        return config.getAuthenticationManager();
    }
}
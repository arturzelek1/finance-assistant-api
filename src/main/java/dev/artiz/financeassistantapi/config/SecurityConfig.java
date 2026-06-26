package dev.artiz.financeassistantapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
        ServerHttpSecurity http
    ) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(auth ->
                auth
                    .pathMatchers(
                        "/actuator/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/webjars/swagger-ui/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**"
                    )
                    .permitAll()
                    .pathMatchers("/api/v1/users/register")
                    .permitAll()
                    .anyExchange()
                    .authenticated()
            )
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(Customizer.withDefaults())
            )
            .build();
    }
}

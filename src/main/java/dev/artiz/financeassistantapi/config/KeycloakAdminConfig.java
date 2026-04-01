package dev.artiz.financeassistantapi.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfig {

    @Value("${keycloak.admin.server-url:http://localhost:8080}")
    private String serverUrl;

    @Value("${keycloak.admin.realm:master}")
    private String realm;

    @Value("${keycloak.admin.client-id:admin-cli}")
    private String clientId;

    @Value("${keycloak.admin.username:admin}")
    private String username;

    @Value("${keycloak.admin.password:admin}")
    private String password;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm(realm)
            .clientId(clientId)
            .username(username)
            .password(password)
            .build();
    }
}

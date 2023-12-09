package com.beadando.beadandoapi.config;

import jakarta.websocket.server.ServerEndpoint;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "beadando.keycloak")
@Getter
@Setter
public class KeyCloakConfig {
    private String realm;
    private String serverURL;
    private String clientID;
    private String clientSecret;
}

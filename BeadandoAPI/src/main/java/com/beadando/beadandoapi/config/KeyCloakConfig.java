package com.beadando.beadandoapi.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyCloakConfig {
    static Keycloak keycloak = null;
    final static String serverUrl = "http://localhost:8180";
    public final static String realm = "beadando";
    final static String clientId = "beadando";
    final static String userName = "admin";
    final static String password = "admin";


    public static Keycloak getInstance() {
        if (keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.PASSWORD)
                    .clientId(clientId)
                    .username(userName)
                    .password(password)
                    .resteasyClient(new ResteasyClientBuilder()
                            .connectionPoolSize(10)
                            .build()
                                   )
                    .build();
        }
        return keycloak;
    }
}

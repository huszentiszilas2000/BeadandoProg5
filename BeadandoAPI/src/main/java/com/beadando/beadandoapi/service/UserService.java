package com.beadando.beadandoapi.service;

import com.beadando.beadandoapi.config.KeyCloakConfig;
import com.beadando.beadandoapi.dto.Credentials;
import com.beadando.beadandoapi.dto.UserDTO;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    KeyCloakConfig keyCloakConfig;
    public Response addUser(UserDTO userDTO){
        CredentialRepresentation credential = Credentials
                .createPasswordCredentials(userDTO.getPassword());
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDTO.getUsername());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setCredentials(Collections.singletonList(credential));
        user.setEnabled(true);

        UsersResource instance = getInstance();
        return instance.create(user);
    }

    public List<UserRepresentation> getUser(){
        UsersResource usersResource = getInstance();
        List<UserRepresentation> user = usersResource.list();
        return user;

    }

    public void updateUser(String userId, UserDTO userDTO){
        UsersResource usersResource = getInstance();
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userDTO.getUsername());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());

        usersResource.get(userId).update(user);
    }
    public void deleteUser(String userId){
        UsersResource usersResource = getInstance();
        usersResource.get(userId)
                .remove();
    }


    public void sendResetPassword(String userId){
        UsersResource usersResource = getInstance();

        usersResource.get(userId)
                .executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));
    }

    public Keycloak getKeyCloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(keyCloakConfig.getServerURL())
                .realm(keyCloakConfig.getRealm())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(keyCloakConfig.getClientID())
                .clientSecret(keyCloakConfig.getClientSecret())
                .resteasyClient(ClientBuilder.newClient())
                .build();
    }

    public UsersResource getInstance(){
        return getKeyCloakInstance().realm(keyCloakConfig.getRealm()).users();
    }
}

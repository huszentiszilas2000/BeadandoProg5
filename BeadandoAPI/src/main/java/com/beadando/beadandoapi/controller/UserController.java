package com.beadando.beadandoapi.controller;

import com.beadando.beadandoapi.dto.UserDTO;
import com.beadando.beadandoapi.model.ResponseMessage;
import com.beadando.beadandoapi.service.UserService;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/user")
public class UserController {
    @Autowired
    private final UserService userService;
    @PostMapping(path= "/addUser")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> addUser(@RequestBody UserDTO userDTO){
        Response response = userService.addUser(userDTO);
        String message = "User created successfully!";
        return ResponseEntity.status(response.getStatus()).body(new ResponseMessage(message));
    }

    @GetMapping(path = "/users")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEMBER')")
    public List<UserRepresentation> getUser(){
        List<UserRepresentation> users = userService.getUser();
        return users;
    }

    @PutMapping(path = "/update/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage>  updateUser(@PathVariable("userId") String userId,   @RequestBody UserDTO userDTO){
        userService.updateUser(userId, userDTO);
        String message = "Updated user successfully!";
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{userId}")
    public ResponseEntity<ResponseMessage> deleteUser(@PathVariable("userId") String userId){
        userService.deleteUser(userId);
        String message = "Deleted user successfully!";
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
    }

    @GetMapping(path = "/reset-password/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseMessage> sendResetPassword(@PathVariable("userId") String userId){
        userService.sendResetPassword(userId);
        String message = "Password reset link has been sent!";
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
    }
}

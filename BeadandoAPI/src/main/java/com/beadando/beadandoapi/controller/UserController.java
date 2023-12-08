package com.beadando.beadandoapi.controller;

import com.beadando.beadandoapi.dto.UserDTO;
import com.beadando.beadandoapi.service.UserService;
import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/user")
@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT }
)
public class UserController {
    @Autowired
    private final UserService userService;
    @PostMapping(path= "/addUser")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String addUser(@RequestBody UserDTO userDTO){
        userService.addUser(userDTO);
        return "User Added Successfully.";
    }

    @GetMapping(path = "/users")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MEMBER')")
    public List<UserRepresentation> getUser(){
        List<UserRepresentation> users = userService.getUser();
        return users;
    }

    @PutMapping(path = "/update/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String updateUser(@PathVariable("userId") String userId,   @RequestBody UserDTO userDTO){
        userService.updateUser(userId, userDTO);
        return "User Details Updated Successfully.";
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "/{userId}")
    public String deleteUser(@PathVariable("userId") String userId){
        userService.deleteUser(userId);
        return "User Deleted Successfully.";
    }

    @GetMapping(path = "/reset-password/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public String sendResetPassword(@PathVariable("userId") String userId){
        userService.sendResetPassword(userId);
        return "Reset Password Link Send Successfully to Registered E-mail Id.";
    }
}

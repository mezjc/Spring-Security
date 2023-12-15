package com.cursos.api.springsecuritycourse.controller;

import com.cursos.api.springsecuritycourse.dto.RegisteredUser;
import com.cursos.api.springsecuritycourse.dto.SaveUser;
import com.cursos.api.springsecuritycourse.service.auth.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//controlador para crear un nuevo usuario
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private AuthenticationService authenticationService;

    //RegisteredUser es un dto que sirve para registrar un usuario y guardara un jwt para cuando se registre se logue automaticante
    @PostMapping
    public ResponseEntity<RegisteredUser> registeterOne(@RequestBody  @Valid SaveUser newUser){
        RegisteredUser registeredUser = authenticationService.registerOneCustomer(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);

    }
}

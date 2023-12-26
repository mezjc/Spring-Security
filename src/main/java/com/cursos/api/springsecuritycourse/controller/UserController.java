package com.cursos.api.springsecuritycourse.controller;

import com.cursos.api.springsecuritycourse.service.auth.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private AuthenticationService authenticationService;
    @GetMapping("/{username}")
    public ResponseEntity<Boolean> equalsUsername(@PathVariable String username){
        Boolean result = authenticationService.equalsUsername(username);
        return ResponseEntity.ok(result);
    }
}

package com.cursos.api.springsecuritycourse.service.auth;

import com.cursos.api.springsecuritycourse.persistence.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    //se le pasa el userDetails para generar el token
    public String generarToken(UserDetails userDetails) {
        return null;
    }
}

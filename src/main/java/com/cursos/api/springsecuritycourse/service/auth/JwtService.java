package com.cursos.api.springsecuritycourse.service.auth;

import com.cursos.api.springsecuritycourse.persistence.entity.User;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class JwtService {

    @Value("${security.jwt.expiration-in-minutes}")
    private Long EXPIRATION_IN_MINUTES;

    @Value("${security.jwt.secret-key}")
    private String SECRET_KEY;

    //se le pasa el userDetails para generar el token
    public String generarToken(UserDetails userDetails, Map<String, Object> extraClaims) {

        //se puede poner como parametro de la clase generarToken
       // Map<String, Objects> extraClaims = new HashMap<>();

        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date( (EXPIRATION_IN_MINUTES * 60 * 1000) + issuedAt.getTime() );

        String jwt = Jwts.builder()
                //extraClaims son claims adicionales que no son obligatorios
                .setClaims(extraClaims)
                //es el propietario del token
                .setSubject(userDetails.getUsername())
                //la fecha de emision
                .setIssuedAt(issuedAt)
                //fecha de expiracion
                .setExpiration(expiration)
                //info del header
                .setHeaderParam(Header.TYPE,Header.JWT_TYPE)
                //firma y el algoritmo
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                //compact deveulve un string y equivale a nuestro jwt
                .compact();

        return jwt;
    }

    //genero la key con la palabra secreta luego lo convierto a byte
    private Key generateKey() {

        //decodificamos la secret key
        byte[] passwordDecoded = Decoders.BASE64.decode(SECRET_KEY);
        //keys es una clase utileria
        return Keys.hmacShaKeyFor(passwordDecoded);
    }


}

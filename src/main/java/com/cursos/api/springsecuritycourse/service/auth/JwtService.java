package com.cursos.api.springsecuritycourse.service.auth;

import com.cursos.api.springsecuritycourse.persistence.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
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
        Date expiration = new Date((EXPIRATION_IN_MINUTES * 60 * 1000) + issuedAt.getTime());

        String jwt = Jwts.builder()
                //info del header
                .header()
                .type("JWT")
                .and()
//extraClaims son claims adicionales que no son obligatorios
                .claims(extraClaims)
                //es el propietario del token
                .subject(userDetails.getUsername())
                //la fecha de emision
                .issuedAt(issuedAt)
                //info del header
                .expiration(expiration)
                //firma y el algoritmo
                .signWith(generateKey(), Jwts.SIG.HS256)
                .compact();
        return jwt;

    }
    //esto esta deprecado
//                .setClaims(extraClaims)
//                //es el propietario del token
//                .setSubject(userDetails.getUsername())
//                //la fecha de emision
//                .setIssuedAt(issuedAt)
//                //fecha de expiracion
//                .setExpiration(expiration)
//                //info del header
//                .setHeaderParam(Header.TYPE,Header.JWT_TYPE)
//                //firma y el algoritmo
//                .signWith(generateKey(), SignatureAlgorithm.HS256)
//                //compact deveulve un string y equivale a nuestro jwt
//                .compact();
//
//        return jwt;
//    }

    //genero la key con la palabra secreta luego lo convierto a byte

    //private Key generateKey() {esto esta deprecado
        private SecretKey generateKey(){
        //decodificamos la secret key
        byte[] passwordDecoded = Decoders.BASE64.decode(SECRET_KEY);
        //SOLO PARA VERIFICAR CUAL ES EL PASSWORD
        System.out.println(new String(passwordDecoded));
        //keys es una clase utileria
        return Keys.hmacShaKeyFor(passwordDecoded);
    }


    public String extractUsername(String jwt) {
        //aqui es donde da el error si el token es invalido
        System.out.println("esto es el username: "+extractAllClaims(jwt).getSubject());
        return extractAllClaims(jwt).getSubject();

    }


    //este método toma un token JWT como entrada, verifica su autenticidad y extrae las afirmaciones contenidas en él
    private Claims extractAllClaims(String jwt) {
        //se crea un constructor de un analizador de tokens JWT. Este analizador se utiliza para
        // verificar la validez y extraer información del token.
        return Jwts.parser() //parserBuilder()
                //Se establece la clave de firma que se utilizará para validar la autenticidad del token
                .verifyWith(generateKey())//.setSigningKey(generateKey()) esto esta deprecado
                //Se construye el analizador de tokens JWT con la clave de firma configurada.
                .build()
                // Se analiza el token JWT proporcionado (jwt). Esto verifica la firma y extrae las afirmaciones del token.
                .parseSignedClaims(jwt)//.parseClaimsJws(jwt) deprecado
                //Se obtiene el cuerpo del token, que contiene las afirmaciones. Las afirmaciones
                // pueden incluir información como el nombre de usuario, roles, permisos, etc.
                .getPayload();//.getBody();deprecado

    }
}

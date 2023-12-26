package com.cursos.api.springsecuritycourse.config.security.filter;

import com.cursos.api.springsecuritycourse.service.UserService;
import com.cursos.api.springsecuritycourse.service.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//esta clase es para autenticarnos solo una vez y acceder a los endpoint
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //1.Obtener encabezado http llamado Authorization
        String authHeader =request.getHeader("Authorization");
        //preguntamos si authHeader tiene texto y si authHeader no empieza con Bearer
        System.out.println( "esto es el header"+ authHeader);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")){
            //devolvemos el filterChain para que siga con los demas filtros
            filterChain.doFilter(request, response);
            //return retorna el control a quien mando a llamar al metrodo actual
            return;
        }
        //2.Obtener token JWT desde el encabezado
        String jwt = authHeader.split(" ")[1].trim();

        //3. Obtener el subject/username desde el token
        //esta accion a su vez valida el formato del token, firma y fecha de expiracion
        String username = jwtService.extractUsername(jwt);

        //4. setear objeto authentication dentro de security context holder

        UserDetails user2 = userDetailsService.loadUserByUsername(username);//esto es prueba


        UserDetails user = userService.findOneByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException( "User not found: "+username));

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    username,null,user.getAuthorities()
        );
        //se annaden detalles al authToken
        //agrega detalles específicos de la autenticación a un token de autenticación,
        // como la dirección IP del cliente o el agente de usuario del navegador.
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        //es una forma de establecer la autenticación del usuario actual en Spring Security,
        // lo que permite que la aplicación gestione la seguridad de manera efectiva.
        SecurityContextHolder.getContext().setAuthentication(authToken);

        //5. Ejecutar el registro de filtros
        filterChain.doFilter(request, response);
    }
}

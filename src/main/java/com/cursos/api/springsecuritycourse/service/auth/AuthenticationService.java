package com.cursos.api.springsecuritycourse.service.auth;

import com.cursos.api.springsecuritycourse.dto.RegisteredUser;
import com.cursos.api.springsecuritycourse.dto.SaveUser;
import com.cursos.api.springsecuritycourse.dto.auth.AuthenticationRequest;
import com.cursos.api.springsecuritycourse.dto.auth.AuthenticationResponse;
import com.cursos.api.springsecuritycourse.exception.ObjectNotFoundException;
import com.cursos.api.springsecuritycourse.persistence.entity.User;
import com.cursos.api.springsecuritycourse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public RegisteredUser registerOneCustomer(SaveUser newUser) {

        User user = userService.registerOneCustomer(newUser);

        RegisteredUser userDto = new RegisteredUser();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setUsername(user.getUsername());
        userDto.setRole(user.getRole().name());

        //creamos un jwt para asignarle cuando se cree el usuario
        String jwt = jwtService.generarToken(user, generateExtraClaims(user));
        userDto.setJwt(jwt);
        return userDto;
    }

    private Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("name",user.getName());
        extraClaims.put("role",user.getRole().name());
        extraClaims.put("authorities",user.getAuthorities());

        return extraClaims;
    }

    //el authenticationManager tiene el metodo authenticate que es quien realmente auténtica un usuario en el sistema
    //lamamos al authenticationManager para configurar su metodo authenticate : el SecurityContextHolder tiene dentro
    //un contexto de seguridad y dentro tiene el authentication nos lo devuelve cuando estamos logueados, si no estamos
    // logueados sirve como imput para el priceso de logueo
    public AuthenticationResponse login(AuthenticationRequest autRequest) {

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                autRequest.getUsername(),
                autRequest.getPassword()
        );
        //le pasamos el authentication para que intente hacer el loguin
        authenticationManager.authenticate(authentication);

        UserDetails user = userService.findOneByUsername(autRequest.getUsername()).orElseThrow();
        String jwt = jwtService.generarToken(user,generateExtraClaims((User) user));

        AuthenticationResponse  autheRsp = new AuthenticationResponse();
        autheRsp.setJwt(jwt);
        System.out.println( "esto es dentro del login "+ user.getUsername());
        return autheRsp;
    }

    public boolean validateToken(String jwt) {
        try {
            jwtService.extractUsername(jwt);
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    //Este método busca al usuario que ha iniciado sesión en la aplicación.
    public User findLoggedInUser() {

        //Aquí se obtiene el objeto de autenticación actual.SecurityContextHolder es una clase
        // proporcionada por Spring Security que almacena la información de autenticación en un
        // contexto específico para el hilo actual.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // El objeto de autenticación puede ser de diferentes tipos, y en este caso, se espera que
        // sea un UsernamePasswordAuthenticationToken.Este token contiene información sobre el usuario
        // autenticado, como el nombre de usuario y las credenciales.
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken)auth;
        //Aquí se extrae el nombre de usuario  El método getPrincipal() devuelve el principal asociado con la
        // autenticación, que en este caso es el nombre de usuario.
            String username = (String) authToken.getPrincipal();
        //Se llama al servicio userService para buscar un usuario por su nombre de usuario.
            return userService.findOneByUsername(username)
                    .orElseThrow(()-> new ObjectNotFoundException("User not found. Username: " + username));


    }

    public Boolean equalsUsername(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken)auth;


        if (authToken.getPrincipal().equals(username)){
            return true;
        }
        throw new AccessDeniedException("Los Usuarios no son iguales");
    }
}

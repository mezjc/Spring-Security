package com.cursos.api.springsecuritycourse.config.security;

import com.cursos.api.springsecuritycourse.exception.ObjectNotFoundException;
import com.cursos.api.springsecuritycourse.persistence.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//2do se configura AuthenticationConfiguration con los beans que usaremos en nuestro proyecto
@Configuration
public class SecurityBeansinjector {

    @Autowired
    private UserRepository userRepository;

    //AuthenticationConfiguration es  clase lo provee spring


    //primero configuramos el authentication devolvera un autentucationManager gracias al aunthenticationConfiguration
    //el AuthenticationManager recibe un authentication que es el usuareio logueado con los roles y el password ahi es donde se
    //analisa la estrategia para el logueo con el authenticationProvider
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {

        return authenticationConfiguration.getAuthenticationManager();

    }

    //creando nuestra estrategia de authentication DaoAuthenticationProvider es ta estrategia esta basado en authentication
    // con base de datos
    @Bean
    public AuthenticationProvider authenticationProvider(){
        //esto implemeneta el authenticationProvider y necesita un passwordEncode esto permite ver si coincide una
        //contrase;a codificada con una que no
        DaoAuthenticationProvider authenticationStrategy = new DaoAuthenticationProvider();
        authenticationStrategy.setPasswordEncoder(passwordEncoder());
        //tambien necesita el userdetailsService y necesita un usuario para comparar las contrase;as del front y back que esta en la DB
        authenticationStrategy.setUserDetailsService(userDetailsService());

        return authenticationStrategy;
    }


    //ya teniendo configurado nuestro UserDetails podemos configurar el paswordEnconder
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //creamos el userDetrailsService
    //es una interface que se utiliza para cargar los detalles de un usuario en especifico
    //userDetailsService devuelve un UserDetails y como mi entidad usuario implementa de UserDetails no nos da error
    @Bean
    public UserDetailsService userDetailsService(){
        return (username) -> userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("User not found with username " + username));
    };


}

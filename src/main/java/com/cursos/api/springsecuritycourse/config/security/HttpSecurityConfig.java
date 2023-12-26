package com.cursos.api.springsecuritycourse.config.security;

import com.cursos.api.springsecuritycourse.config.security.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity//esto arranca la creacion de ciertos componentes importantes de spring y lo conf por default
public class HttpSecurityConfig {

    @Autowired
    private AuthenticationProvider daoAuthProvider;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrfConfig -> csrfConfig.disable())
                //configuramos la session que es stateless osea sin estado
                .sessionManagement(sessMagConfig -> sessMagConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //estrategia de authentication que usaremos
                .authenticationProvider(daoAuthProvider)
                //agregamos un filtro jwtAuthenticationFilter se ejecutara antes del otro filtro usernamePasswordAutheticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                //configuramos los endPoint
                .authorizeHttpRequests( authReqConfig ->{

                    authReqConfig.requestMatchers(HttpMethod.POST,"/customers").permitAll();
                    authReqConfig.requestMatchers(HttpMethod.POST,"/auth/authenticate").permitAll();
                    authReqConfig.requestMatchers(HttpMethod.GET,"/auth/validate").permitAll();

                    authReqConfig.anyRequest().authenticated();

                })
                .build();

    }
}

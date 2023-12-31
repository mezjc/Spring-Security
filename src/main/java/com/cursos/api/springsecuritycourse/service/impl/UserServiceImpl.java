package com.cursos.api.springsecuritycourse.service.impl;

import com.cursos.api.springsecuritycourse.dto.SaveUser;
import com.cursos.api.springsecuritycourse.exception.InvalidPasswordException;
import com.cursos.api.springsecuritycourse.persistence.entity.User;
import com.cursos.api.springsecuritycourse.persistence.repository.UserRepository;
import com.cursos.api.springsecuritycourse.persistence.util.Role;
import com.cursos.api.springsecuritycourse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerOneCustomer(SaveUser newUser) {

        //validaciones
        validatePassword(newUser);

        User user = new User();
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        user.setUsername(newUser.getUsername());
        user.setName(newUser.getName());
        //se le setea un rol por default
        user.setRole(Role.ROLE_CUSTOMER);


        return userRepository.save(user);
    }

    @Override
    public Optional<User> findOneByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private void validatePassword(SaveUser newUser) {
        //erifica si la contraseña del nuevo usuario no tiene texto (es decir, si está vacía o solo contiene espacios en blanco)
        //hastext conuslta si tiene texto si es si devuelve true
        if(!StringUtils.hasText(newUser.getPassword()) || !StringUtils.hasText(newUser.getRepeatedPassword())){
            throw new InvalidPasswordException("Password don't match");
        }
        //compara si es igual los password
        if (!newUser.getPassword().equals(newUser.getRepeatedPassword())){
            throw new InvalidPasswordException("Password don't match2");
        }

    }

}

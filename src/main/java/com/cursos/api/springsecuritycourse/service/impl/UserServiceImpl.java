package com.cursos.api.springsecuritycourse.service.impl;

import com.cursos.api.springsecuritycourse.dto.SaveUser;
import com.cursos.api.springsecuritycourse.exception.InvalidPasswordException;
import com.cursos.api.springsecuritycourse.persistence.entity.User;
import com.cursos.api.springsecuritycourse.persistence.repository.UserRepository;
import com.cursos.api.springsecuritycourse.persistence.util.Role;
import com.cursos.api.springsecuritycourse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public User registerOneCustomer(SaveUser newUser) {

        //validaciones
        validatePassword(newUser);

        User user = new User();
        user.setName(newUser.getName());
        user.setUsername(newUser.getUsername());
        user.setPassword(newUser.getPassword());
        //se le setea un rol por default
        user.setRole(Role.ROLE_CUSTOMER);


        return userRepository.save(user);
    }

    private void validatePassword(SaveUser newUser) {
        //erifica si la contraseña del nuevo usuario no tiene texto (es decir, si está vacía o solo contiene espacios en blanco)
        //hastext conuslta si tiene texto si es si devuelve true
        if(!StringUtils.hasText(newUser.getPassword()) || !StringUtils.hasText(newUser.getRepeatedPassword())){
            throw new InvalidPasswordException("Password don't match");
        }
        //compara si es igual los password
        if (!newUser.getPassword().equals(newUser.getRepeatedPassword())){
            throw new InvalidPasswordException("Password don't match");
        }

    }

}

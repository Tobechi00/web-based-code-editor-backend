package com.wide.widebackend.controller;

import com.wide.widebackend.Entity.User;
import com.wide.widebackend.config.JwtGenerator;
import com.wide.widebackend.customexceptions.UserAlreadyExistsException;
import com.wide.widebackend.dataobjects.dao.UserDTO;
import com.wide.widebackend.dataobjects.dto.LoginDAO;
import com.wide.widebackend.dataobjects.dto.RegistrationDAO;
import com.wide.widebackend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/w-ide/api")
public class LoginRegistrationController {

    @Autowired
    private final UserService userService;
    private final JwtGenerator jwtGenerator;

    private final DaoAuthenticationProvider authenticationProvider;
    private final PasswordEncoder passwordEncoder;
    Logger logger = LoggerFactory.getLogger(LoginRegistrationController.class);

    public LoginRegistrationController(UserService userService, JwtGenerator jwtGenerator, DaoAuthenticationProvider daoAuthenticationProvider,PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtGenerator = jwtGenerator;
        this.authenticationProvider = daoAuthenticationProvider;
        this.passwordEncoder = passwordEncoder;
    }

    //user login api
    @PostMapping(value = "/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginDAO loginDao) {

        try {
            //todo:keeps returning null user fix later or roll back

            //auth
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDao.username(), loginDao.password());
            Authentication authentication = authenticationProvider.authenticate(authenticationToken);

            //generate token
            String userToken = jwtGenerator.generateToken(authentication);

            //create payload
            User user = userService.getUserByUsername(loginDao.username());
            UserDTO payload = new UserDTO();
            payload.setUsername(user.getEmail());
            payload.setLastName(user.getLastname());
            payload.setFirstName(user.getFirstname());
            payload.setId(user.getId());
            payload.setToken(userToken);

            return ResponseEntity.ok().body(payload);
        }catch (AuthenticationException a) {
            return ResponseEntity.notFound().build();
        }catch (Exception e){
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }


    //user registration api
    @PostMapping(value = "/register")
    public void register(@RequestBody RegistrationDAO registrationDAO, HttpServletResponse response) {
        try {
            User user = new User(registrationDAO.email(),passwordEncoder.encode(registrationDAO.password()), registrationDAO.firstname(), registrationDAO.lastname());
            userService.saveUser(user);
            response.setStatus(HttpStatus.OK.value());
        } catch (UserAlreadyExistsException e) {
            response.setStatus(HttpStatus.CONFLICT.value());
        }catch (Exception e){{
            logger.error(e.getMessage());
        }}
    }
}



package com.wide.widebackend.controller;

import com.wide.widebackend.Entity.User;
import com.wide.widebackend.config.JwtGenerator;
import com.wide.widebackend.dao.UserDto;
import com.wide.widebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

    DaoAuthenticationProvider authenticationProvider;
    Logger logger = LoggerFactory.getLogger(LoginRegistrationController.class);

    public LoginRegistrationController(UserService userService, JwtGenerator jwtGenerator, DaoAuthenticationProvider daoAuthenticationProvider) {
        this.userService = userService;
        this.jwtGenerator = jwtGenerator;
        this.authenticationProvider = daoAuthenticationProvider;
    }

    //user login api
    @PostMapping(value = "/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginDao loginDao){

        try {
            //todo:keeps returning null user fix later or roll back

            //auth
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginDao.username(), loginDao.password());
            Authentication authentication = authenticationProvider.authenticate(authenticationToken);

            //generate token
            String userToken = jwtGenerator.generateToken(authentication);

            //create payload
            User user = userService.getUserByUsername(loginDao.username());
            UserDto payload = new UserDto();
            payload.setUsername(user.getEmail());
            payload.setLastName(user.getLastname());
            payload.setFirstName(user.getFirstname());
            payload.setId(user.getId());
            payload.setToken(userToken);

            return ResponseEntity.ok().body(payload);
        }catch (AuthenticationException | NullPointerException e ){
            logger.warn(e.getMessage());
            }

            return ResponseEntity.notFound().build();
        }


    //user registration api
    @PostMapping(value = "/register")
    public ResponseEntity<String> register(@RequestBody User user){
        try {
            userService.saveUser(user);
            return ResponseEntity.ok("user registered");
        }catch (Exception e){
            logger.error("an error has occurred while trying to register user");
            return ResponseEntity.badRequest().build();
        }
    }

    record LoginDao(String username,String password){}
    }


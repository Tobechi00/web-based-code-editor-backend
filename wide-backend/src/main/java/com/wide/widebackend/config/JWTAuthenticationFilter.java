package com.wide.widebackend.config;

import com.wide.widebackend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
custom authentication filter which verifies the integrity of the token contained in a received request
 */

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTGenerator jwtGenerator;

    @Autowired
    private UserService userService;

    Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = getJwtFromRequest(request);
        String requestURI = request.getRequestURI();
        if (!requestURI.equals("/w-ide/api/login")){
            try {
                jwtGenerator.validate(token);
                filterChain.doFilter(request,response);
            }catch (AuthenticationException e){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                logger.error(e.getMessage());
            }
        }else {
            filterChain.doFilter(request,response);
        }
        }


    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("authentication");


        if (! (bearerToken == null)){
            return bearerToken;
        }
        return null;
    }

}

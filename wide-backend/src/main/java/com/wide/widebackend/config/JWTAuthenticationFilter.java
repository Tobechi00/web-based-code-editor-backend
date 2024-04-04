package com.wide.widebackend.config;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/*
custom authentication filter which verifies the integrity of the token contained in a received request
 */

public class JWTAuthenticationFilter extends OncePerRequestFilter {


    private final JwtGenerator jwtGenerator;

    Logger logger = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    public JWTAuthenticationFilter(JwtGenerator jwtGenerator) {
        this.jwtGenerator = jwtGenerator;
    }


    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        String token = getJwtFromRequest(request);
        String requestURI = request.getRequestURI();

        //paths ignored by jwt filter
        Set<String> authIgnorePaths = new HashSet<>();
        authIgnorePaths.add("/w-ide/api/login");
        authIgnorePaths.add("/w-ide/api/register");

        if (authIgnorePaths.contains(requestURI)){
            filterChain.doFilter(request,response);
        }else {
            try {
                jwtGenerator.validate(token);
                filterChain.doFilter(request,response);
            }catch (AuthenticationException e){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                logger.error(e.getMessage());
            }
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

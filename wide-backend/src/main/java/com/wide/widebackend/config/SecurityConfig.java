package com.wide.widebackend.config;

import com.wide.widebackend.service.user.UserService;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserService userService;
    private final JwtGenerator jwtGenerator;
    private final JwtAuthEntryPoint authEntryPoint;


    public SecurityConfig(UserService userService, JwtGenerator jwtGenerator, JwtAuthEntryPoint jwtAuthEntryPoint){
        this.userService = userService;
        this.jwtGenerator = jwtGenerator;
        this.authEntryPoint = jwtAuthEntryPoint;
    }

    //configuration for the spring auth manager
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception{

        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder())
                .and().build();
    }

    //bean for application wide password encoder
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //filter chain where all requests are sieved through
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.cors().and().csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authEntryPoint)
                .and()
                .sessionManagement()// disabling sessions for jwt
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)//changed
                .and()
                .authorizeRequests()
                .requestMatchers("/**","/**/**","/**/**/**").permitAll()
                .anyRequest().authenticated();
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public Filter jwtAuthenticationFilter() {
        return new JWTAuthenticationFilter(jwtGenerator);
    }

    //configures cross origin and enables acceptance of requests from localhost:8000 (frontend)
    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests from all origins
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:8000");
        config.addAllowedOrigin("http://localhost:57448");
        config.addAllowedHeader("*");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

}

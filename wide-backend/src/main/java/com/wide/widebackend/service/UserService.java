package com.wide.widebackend.service;

import com.wide.widebackend.Entity.PrincipalUser;
import com.wide.widebackend.Entity.User;
import com.wide.widebackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService implements UserDetailsService {

    Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);

        if (user == null){
            throw new UsernameNotFoundException("username:"+" "+username+" "+"could not be found");
        }else {
            return new PrincipalUser(user);
        }
    }

    public User getUserByUsername(String username){
        User user = userRepository.findUserByUsername(username);

        if (user == null){
            throw new UsernameNotFoundException("username:"+" "+username+" "+"could not be found");
        }else {
            return user;
        }
    }


    //save user
    public void saveUser(User user){
        if (user != null){
            userRepository.save(user);
        }else {
            throw new NullPointerException("user provided is null");
        }
    }

    public User getUserById(Long id){
            return userRepository.findById(id).orElseThrow(()-> new RuntimeException("user with specified id not found"));
    }

    //find user by id
    public User findUserById(Long id){
            return userRepository.findById(id).orElseThrow(
                    ()->new RuntimeException("error occurred while trying to find user by id"));
    }

    public List<String> getFilePathsById(Long id){
        User user = userRepository.findById(id).orElseThrow(
                ()->new RuntimeException("error occurred while trying to find user by id")
        );
        return user.getFilePaths();
    }


    @Transactional
    public void savePathsToUser(User user,List<String> list){
        user.setFilePaths(list);
    }



}

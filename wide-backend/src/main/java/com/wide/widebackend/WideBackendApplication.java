package com.wide.widebackend;

import com.wide.widebackend.Entity.User;
import com.wide.widebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class WideBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(WideBackendApplication.class, args);
	}

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@EventListener(ApplicationReadyEvent.class)
	public void addTestUser(){
		//sample user for testing
		User user = new User();
		user.setEmail("tobechiokaro2013@gmail.com");
		user.setPassword(passwordEncoder.encode("tobec"));
		user.setFirstname("Tobechi");
		user.setLastname("Okaro");

		List<String> fileList = new ArrayList<>();
		fileList.add("C:\\Users\\tobec\\ServerData\\user-files\\Document-1.txt");
		fileList.add("C:\\Users\\tobec\\ServerData\\user-files\\Report-1.txt");

		user.setFilePaths(fileList);

		userRepository.save(user);
	}


}

package com.wide.widebackend.repository;

import com.wide.widebackend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query("SELECT user FROM User user WHERE user.email = :username")
    User findUserByUsername(@Param("username") String username);

}
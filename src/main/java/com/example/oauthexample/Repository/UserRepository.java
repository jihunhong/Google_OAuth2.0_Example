package com.example.oauthexample.Repository;

import com.example.oauthexample.VO.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{
    User findByEmail(String email);
}
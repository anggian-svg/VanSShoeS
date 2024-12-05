package com.login.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.login.demo.models.Login;


public interface LoginRepository extends JpaRepository<Login,Integer> {
    Login findByUsernameAndPassword(String username, String password);
    Login findByRole(String string);
    Login findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByNama(String nama);
}

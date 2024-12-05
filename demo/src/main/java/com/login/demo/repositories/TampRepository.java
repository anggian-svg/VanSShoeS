package com.login.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.login.demo.models.Tamp;

public interface TampRepository extends JpaRepository<Tamp,Integer> {
    
}

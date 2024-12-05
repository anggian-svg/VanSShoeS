package com.login.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.login.demo.models.Stok;

public interface StokRepository extends JpaRepository<Stok,Integer> {

    Stok findBySum(Integer stok);
    
}

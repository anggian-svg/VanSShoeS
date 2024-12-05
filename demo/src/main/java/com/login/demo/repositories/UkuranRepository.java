package com.login.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.login.demo.models.Ukuran;

public interface UkuranRepository extends JpaRepository<Ukuran,Integer> {

    Ukuran findBySize(Integer ukuran);
    
}

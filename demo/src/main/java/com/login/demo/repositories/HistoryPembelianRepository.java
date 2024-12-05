package com.login.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.login.demo.models.HistoryPembelian;

public interface HistoryPembelianRepository extends JpaRepository<HistoryPembelian, Integer> {
    
    List<HistoryPembelian> findByUserId(Integer userId);


}

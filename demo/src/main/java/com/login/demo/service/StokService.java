package com.login.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.login.demo.models.Stok;
import com.login.demo.repositories.StokRepository;

@Service
public class StokService {
    @Autowired
    StokRepository stokRepository;


    public void saveStok(List<Integer> stoks) {
    for (Integer stok : stoks) {
        if (stokRepository.findBySum(stok) == null) {
            Stok st = new Stok();
            st.setSum(stok); 
            stokRepository.save(st); 
        }
    }
    }

}

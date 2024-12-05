package com.login.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.login.demo.models.Ukuran;
import com.login.demo.repositories.UkuranRepository;

@Service
public class UkuranService {
    @Autowired
    UkuranRepository ukuranRepository;

    public void saveUkuran(List<Integer> ukuranss) {

        for (Integer ukuran : ukuranss) {
            if (ukuranRepository.findBySize(ukuran) == null) {
                Ukuran ukn = new Ukuran();
                ukn.setSize(ukuran); 
                ukuranRepository.save(ukn);
            }
        }
    }
}

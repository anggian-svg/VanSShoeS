package com.login.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.login.demo.models.Tamp;
import com.login.demo.repositories.TampRepository;

@Service
public class TampService {
    @Autowired
    private TampRepository tampRepository;

    public void simpanDataLogin(String nama, String username, String password, int saldo) {
        hapusSemuaData();
        Tamp tamp = new Tamp();
        tamp.setNama(nama);
        tamp.setUsername(username);
        tamp.setPassword(password); 
        tamp.setSaldo(saldo); 

        tampRepository.save(tamp);
    }

    public void hapusSemuaData() {
        tampRepository.deleteAll();
    }

    public List<Tamp> findAll() {
        if(tampRepository.findAll() != null){
            return tampRepository.findAll();
        }
        return null;
    }

    public void saveSaldo(Tamp tamp){
        tampRepository.save(tamp);
    }

}

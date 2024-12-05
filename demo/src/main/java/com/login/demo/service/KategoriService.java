package com.login.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.login.demo.models.Kategori;
import com.login.demo.repositories.KategoriRepository;

@Service
public class KategoriService {
    @Autowired
    private KategoriRepository kategoriRepository;


    public void saveKat(Kategori kategori){
        kategoriRepository.save(kategori);
    }

    public List<Kategori> findAll() {
        return kategoriRepository.findAll();
    }
}

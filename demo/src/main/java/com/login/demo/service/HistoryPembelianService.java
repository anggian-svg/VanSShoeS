package com.login.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.login.demo.models.HistoryPembelian;
import com.login.demo.repositories.HistoryPembelianRepository;

@Service
public class HistoryPembelianService {
    @Autowired
    private HistoryPembelianRepository historyPembelianRepository;

    public void saveHistory(Integer userId, String name, String username, String namaProduk, int jumlah, int totalHarga) {
        HistoryPembelian history = new HistoryPembelian();
        history.setUserId(userId); 
        history.setName(name);
        history.setUsername(username);
        history.setNamaProduk(namaProduk);
        history.setJumlah(jumlah);
        history.setTotalHarga(totalHarga);
        historyPembelianRepository.save(history);
    }

    public List<HistoryPembelian> getAllHistory() {
        return historyPembelianRepository.findAll();
    }

    public List<HistoryPembelian> getHistoryByUserId(Integer userId) {
        return historyPembelianRepository.findByUserId(userId);
    }
}

package com.login.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.login.demo.models.Foerign;
import com.login.demo.models.Produk;
import com.login.demo.models.Stok;
import com.login.demo.repositories.ForeignRepository;
import com.login.demo.repositories.ProdukRepository;
import com.login.demo.repositories.StokRepository;
import com.login.demo.repositories.UkuranRepository;

@Service
public class ForeignService {
    @Autowired
    ForeignRepository foreignRepository;

    @Autowired
    ProdukRepository produkRepository;

    @Autowired
    UkuranRepository ukuranRepository;

    @Autowired
    StokRepository stokRepository;


    public List<Foerign> getByProduk(Produk produk) {
        return foreignRepository.findByProduk(produk);
    }

    public void updateStock(Integer idProduk, Integer jumlah, Integer idUkuran) {
        Foerign foerign = foreignRepository.findByProdukAndUkuran(
                produkRepository.getReferenceById(idProduk), 
                ukuranRepository.getReferenceById(idUkuran)); 

        Stok stoks = foerign.getStok();

        if (stoks.getSum() < jumlah) {
            throw new IllegalArgumentException("Jumlah pembelian melebihi stok tersedia.");
        }

        int sisaStok = stoks.getSum() - jumlah;

        Stok stokBaru = stokRepository.findBySum(sisaStok);
        if (stokBaru == null) {
            stokBaru = new Stok();
            stokBaru.setSum(sisaStok); 
            stokRepository.save(stokBaru);
        }

        foerign.setStok(stokBaru);
        foreignRepository.save(foerign); 
    }

    public void updateStokByAdmin(Integer idProduk, Integer idUkuran, Integer stokBaru, Integer idStok) {
        Foerign foerign = foreignRepository.findByProdukAndUkuran(
                produkRepository.getReferenceById(idProduk),
                ukuranRepository.getReferenceById(idUkuran));
        Stok stok1 = stokRepository.getReferenceById(idStok);
        Stok stok = stokRepository.findBySum(stokBaru + stok1.getSum());
        if (stok == null) {
            stok = new Stok();
            stok.setSum(stokBaru + stok1.getSum());
            stokRepository.save(stok);
            foerign.setStok(stok);
            foreignRepository.save(foerign);
        } else{
            System.out.println("jumlah stok "+stok.getSum());
            foerign.setStok(stok);
            foreignRepository.save(foerign);

        }
    }

    public Integer getStok(Integer idProduk,Integer idUkuran){
        Foerign foerign = foreignRepository.findByProdukAndUkuran(
                produkRepository.getReferenceById(idProduk),
                ukuranRepository.getReferenceById(idUkuran));
        return foerign.getStok().getSum();
    }
}

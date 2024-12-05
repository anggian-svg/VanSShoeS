package com.login.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.login.demo.models.Foerign;
import com.login.demo.models.Produk;
import com.login.demo.models.Ukuran;

public interface ForeignRepository extends JpaRepository<Foerign, Integer> {

    List<Foerign> findByProduk(Produk produk);

    Foerign findByProdukAndUkuran(Produk referenceById, Ukuran referenceById2);
}

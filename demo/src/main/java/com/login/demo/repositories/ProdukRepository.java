package com.login.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.login.demo.models.Kategori;
import com.login.demo.models.Produk;



public interface ProdukRepository extends JpaRepository<Produk, Integer> {
    List<Produk> findByKategori(Kategori kategori);
    List<Produk> findByNamaProduk(String namaProduk);
    List<Produk> findByHarga(Long harga);
    List<Produk> findAllByOrderByNamaProdukAsc();
    List<Produk> findAllByOrderByHargaAsc();
    List<Produk> findAllByOrderByNamaProdukDesc();
    List<Produk> findAllByOrderByHargaDesc();

}

package com.login.demo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.login.demo.models.Keranjang;
import com.login.demo.models.Login;
import com.login.demo.models.Produk;

public interface KeranjangRepository extends JpaRepository<Keranjang,Integer> {

    List<Keranjang> findByLogin(Login login);
    List<Keranjang> findByProduk(Produk produk);
    List<Keranjang> findAllByLogin(Login login);
    Keranjang findByProdukAndLogin(Produk produk, Login login);
    boolean existsByLoginAndProdukId(Login login, Integer produkId);    
}

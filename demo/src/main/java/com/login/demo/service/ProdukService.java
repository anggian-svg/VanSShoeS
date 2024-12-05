package com.login.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.login.demo.models.Foerign;
import com.login.demo.models.Keranjang;
import com.login.demo.models.Produk;
import com.login.demo.models.Stok;
import com.login.demo.models.Ukuran;
import com.login.demo.repositories.ForeignRepository;
import com.login.demo.repositories.KeranjangRepository;
import com.login.demo.repositories.ProdukRepository;
import com.login.demo.repositories.StokRepository;
import com.login.demo.repositories.UkuranRepository;

@Service
public class ProdukService {
    @Autowired
    private ProdukRepository produkRepository;

    @Autowired
    private StokRepository stokRepository;

    @Autowired
    private UkuranRepository ukuranRepository;

    @Autowired
    private ForeignRepository foreignRepository;

    @Autowired
    private KeranjangRepository keranjangRepository;

    public List<Produk> findAll() {
        return produkRepository.findAll();
    }

    public void saveProd(Produk produk) {
        produkRepository.save(produk);
    }

    public void saveF(Integer productId, Integer stok, Integer ukuran) {
        Produk produk = produkRepository.getReferenceById(productId); 
        Stok st = stokRepository.findBySum(stok); 
        Ukuran uk = ukuranRepository.findBySize(ukuran);
        Foerign fr = new Foerign(); 

        fr.setProduk(produk); 
        fr.setUkuran(uk); 
        fr.setStok(st); 

        foreignRepository.save(fr);
    }

    public List<Produk> searchByNamaProdukOrKategori(String keyword) {
        List<Produk> allProducts = findAll(); 
        String lowerKeyword = keyword.toLowerCase();

        return allProducts.stream()
                .filter(prod -> prod.getNamaProduk().toLowerCase().contains(lowerKeyword)
                        || prod.getKategori().getName().toLowerCase().contains(lowerKeyword))
                .toList();
    }

    public void deleteById(Integer id) {
        Produk produk = produkRepository.getReferenceById(id);
        List<Foerign> foerigns = foreignRepository.findByProduk(produk);
        foreignRepository.deleteAll(foerigns);
        List<Keranjang> keranjangItems = keranjangRepository.findByProduk(produk);
        keranjangRepository.deleteAll(keranjangItems);
        produkRepository.delete(produk);
    }

    public List<Produk> orderByNameAsc(String sort) {
        if ("nameAsc".equals(sort)) {
            return produkRepository.findAllByOrderByNamaProdukAsc();
        } else if ("priceLow".equals(sort)) {
            return produkRepository.findAllByOrderByHargaAsc();
        } else if ("nameDesc".equals(sort)) {
            return produkRepository.findAllByOrderByNamaProdukDesc();
        } else if ("priceHigh".equals(sort)) {
            return produkRepository.findAllByOrderByHargaDesc();
        }
        return produkRepository.findAll();
    }

    public Optional<Produk> findById(Integer id) {
        return produkRepository.findById(id);
    }

    public Produk getProduk(Integer id) {
        return produkRepository.getReferenceById(id);
    }

}

package com.login.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.login.demo.models.Keranjang;
import com.login.demo.models.Login;
import com.login.demo.models.Produk;
import com.login.demo.models.Tamp;
import com.login.demo.repositories.KeranjangRepository;
import com.login.demo.repositories.LoginRepository;
import com.login.demo.repositories.ProdukRepository;
import com.login.demo.repositories.TampRepository;

@Service
public class KeranjangService {

    @Autowired
    private KeranjangRepository keranjangRepository;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private TampRepository tampRepository;

    @Autowired
    private ProdukRepository produkRepository;

    @Autowired
    private LoginService loginService;


    public void addToCart(Integer id) {

        List<Tamp> tamps = tampRepository.findAll();
        Login login = loginRepository.findByUsernameAndPassword(
                tamps.get(0).getUsername(),
                tamps.get(0).getPassword()
        );

        if (keranjangRepository.existsByLoginAndProdukId(login, id)) {
            throw new IllegalArgumentException("Produk sudah ada di keranjang!");
        }

        Produk produk = produkRepository.getReferenceById(id);
        Keranjang keranjang = new Keranjang();

        keranjang.setLogin(login);
        keranjang.setProduk(produk);

        keranjangRepository.save(keranjang);
    }



    

    public Keranjang getKeranjang(Integer id) {
        return keranjangRepository.getReferenceById(id);
    }


    public List<Keranjang> getByUser() {
        List<Tamp> tamps = tampRepository.findAll();
        Login login = loginRepository.findByUsernameAndPassword(
                tamps.get(0).getUsername(), 
                tamps.get(0).getPassword() 
        );

        return keranjangRepository.findByLogin(login);
    }

    public void deleteAllByUser() {
        List<Tamp> tamps = tampRepository.findAll();
        Login login = loginRepository.findByUsernameAndPassword(tamps.get(0).getUsername(), tamps.get(0).getPassword());
        List<Keranjang> keranjangs = keranjangRepository.findAllByLogin(login);
        keranjangRepository.deleteAll(keranjangs);
    }

    public void deleteKeranjangByUser(Integer productId) {
        Produk produk = produkRepository.getReferenceById(productId);
        Login login = loginService.getLogin();
        Keranjang keranjang = keranjangRepository.findByProdukAndLogin(produk, login);
        keranjangRepository.delete(keranjang);
    }

    public void deleteKeranjang(Integer id) {
        Keranjang keranjang = keranjangRepository.getReferenceById(id);
        keranjangRepository.delete(keranjang);
    }
}

package com.login.demo.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.login.demo.models.Keranjang;
import com.login.demo.models.Login;
import com.login.demo.models.Tamp;
import com.login.demo.repositories.KeranjangRepository;
import com.login.demo.repositories.LoginRepository;

@Service
public class LoginService {
    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private TampService tampService;

    @Autowired
    private KeranjangRepository keranjangRepository;

    public void saveRegister(Login login) {
        if (loginRepository.existsByUsername(login.getUsername())) {
            System.out.println("disini kahh?");
            throw new IllegalArgumentException("Username sudah terdaftar");
        }
        if (loginRepository.existsByNama(login.getNama())) {
            System.out.println("dimana kahh?");

            throw new IllegalArgumentException("Nama sudah terdaftar");
        }
        
        login.setRole("user");
        loginRepository.save(login);
    }

    public String cekLogin(Login login, RedirectAttributes redirectAttributes) {
        Login login2 = loginRepository.findByUsernameAndPassword(login.getUsername(), login.getPassword());
        if (login2 != null) {

            tampService.simpanDataLogin(
                    login2.getNama(),
                    login2.getUsername(),
                    login2.getPassword(),
                    login2.getSaldo());

            if (login2.getRole().equals("admin")) {
                return "redirect:/menu-adm";
            } else if (login2.getRole().equals("user")) {
                return "redirect:/produk";
            }
            
            return "redirect:/home";
        }
        redirectAttributes.addAttribute("error", "Invalid username or password.");
        return "redirect:/home";
    }

    public void createAdm() {
        Login login = new Login();
        login.setNama("Admin");
        login.setPassword("admin123");
        login.setRole("admin");
        login.setUsername("admin");
        loginRepository.save(login);
    }

    public List<Login> findAll() {
        return loginRepository.findAll();
    }

    public void deleteById(Integer id) {
        Login login = loginRepository.getReferenceById(id);
        List<Keranjang> keranjangs = keranjangRepository.findByLogin(login);
        keranjangRepository.deleteAll(keranjangs);
        loginRepository.delete(login);
    }

    public Login findById(int id) {
        return loginRepository.findById(id).orElse(null);
    }

    public Login getCurrentInLoggedUser() {
        List<Tamp> tampData = tampService.findAll();
        if (!tampData.isEmpty()) {
            Tamp currentTamp = tampData.get(0);
            return loginRepository.findByUsernameAndPassword(currentTamp.getUsername(), currentTamp.getPassword());
        }
        return null;
    }

    public boolean topUpSaldo(BigDecimal nominal) {
        if (nominal.compareTo(BigDecimal.valueOf(10000)) < 0) {
            return false;
        }
        Login currentUser = getCurrentInLoggedUser();
        if (currentUser == null) {
            return false;
        }

        int currentSaldo = currentUser.getSaldo();
        int newSaldo = currentSaldo + nominal.intValue();

        currentUser.setSaldo(newSaldo);
        loginRepository.save(currentUser);
        tampService.simpanDataLogin(
                currentUser.getNama(),
                currentUser.getUsername(),
                currentUser.getPassword(),
                newSaldo);

        return true;
    }

    public void minSaldo(Integer jumlahBeli, int harga) {
        List<Tamp> tamps = tampService.findAll();
        Login login = loginRepository.findByUsernameAndPassword(tamps.get(0).getUsername(), tamps.get(0).getPassword());
        int totalHarga = jumlahBeli * harga;
        if (login.getSaldo() < totalHarga) {
            System.out.println("Saldo tidak cukup! Saldo saat ini: " + login.getSaldo() + ", Total harga: " + totalHarga);
            throw new IllegalArgumentException("Saldo tidak cukup untuk melakukan pembelian");
        }
        
        
        login.setSaldo(login.getSaldo() - totalHarga);
        Tamp tamp = tamps.get(0);
        tamp.setSaldo(tamps.get(0).getSaldo() - totalHarga);
        tampService.saveSaldo(tamp);
        loginRepository.save(login);
    }

    public int getSaldo() {
        List<Tamp> tamps = tampService.findAll();
        return tamps.get(0).getSaldo();
    }

    public Login getLogin() {
        List<Tamp> tamps = tampService.findAll();
        return loginRepository.findByUsername(tamps.get(0).getUsername());
    }

    public boolean cek() {
        if (tampService.findAll().isEmpty()) {
            return false;
        }
        return true;
    }

    public String getRole() {
        Login login = loginRepository.findByUsername(tampService.findAll().get(0).getUsername());
        return login.getRole();
    }
}

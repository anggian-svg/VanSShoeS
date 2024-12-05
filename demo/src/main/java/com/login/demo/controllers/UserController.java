package com.login.demo.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.login.demo.models.Foerign;
import com.login.demo.models.HistoryPembelian;
import com.login.demo.models.Keranjang;
import com.login.demo.models.Login;
import com.login.demo.models.Produk;
import com.login.demo.models.Tamp;
import com.login.demo.models.Ukuran;
import com.login.demo.repositories.LoginRepository;
import com.login.demo.service.ForeignService;
import com.login.demo.service.HistoryPembelianService;
import com.login.demo.service.KeranjangService;
import com.login.demo.service.LoginService;
import com.login.demo.service.ProdukService;
import com.login.demo.service.TampService;

@Controller
public class UserController {

    @Autowired
    private ProdukService produkService;

    @Autowired
    private TampService tampService;

    @Autowired
    private ForeignService foreignService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private KeranjangService keranjangService;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private HistoryPembelianService historyPembelianService;

    @GetMapping("/produk")
    public String listProduk(@RequestParam(required = false) String sort, Model model) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("user")) {
                model.addAttribute("data", produkService.orderByNameAsc(sort));
                return "produk";
            }
            return "redirect:/menu-adm";
        }
        return "belum-login";
    }

    @GetMapping("/search")
    public String searchProduk(@RequestParam(name = "search", required = false) String keyword, Model model) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("user")) {
                if (keyword == null || keyword.isEmpty()) {
                    model.addAttribute("data", produkService.findAll());
                } else {
                    model.addAttribute("data", produkService.searchByNamaProdukOrKategori(keyword));
                }
                return "produk";
            }
            return "redirect:/menu-adm";
        }
        return "belum-login";
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("user")) {
                model.addAttribute("prof", tampService.findAll());
                return "profile";
            }
            return "redirect:/menu-adm";
        }
        return "belum-login";
    }

    @GetMapping("/beli/{id}")
    public String beliProduk(@PathVariable Integer id, Model model) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("user")) {
                Produk produk = produkService.getProduk(id);
                List<Foerign> foerigns = foreignService.getByProduk(produk);
                Ukuran ukuran = new Ukuran();
                model.addAttribute("belibrg", foerigns);
                model.addAttribute("ukuran", ukuran);
                return "beli";
            }
            return "redirect:/menu-adm";
        }
        return "belum-login";
    }


    @GetMapping("/buy/{id}")
    public String beliProd(
            @PathVariable Integer id,
            @RequestParam("jumlah-beli") Integer jumlahBeli,
            @RequestParam("productId") Integer productId,
            Ukuran ukuran,
            RedirectAttributes redirectAttributes) {
        try {
            if (loginService.cek()) {
                if (loginService.getRole().equals("user")) {
                    Produk produk = produkService.getProduk(productId);
                    int total = produk.getHarga() * jumlahBeli;
                    System.out.println("jumlah beli : " + total);
                    Integer stok = foreignService.getStok(produk.getId(), ukuran.getId());
                    if (stok >= jumlahBeli) {
                        historyPembelianService.saveHistory(
                                loginService.getLogin().getId(),
                                loginService.getLogin().getNama(), 
                                loginService.getLogin().getUsername(),
                                produk.getNamaProduk(), 
                                jumlahBeli,
                                total 
                        );

                        redirectAttributes.addFlashAttribute("success", "Pembelian berhasil!");
                        loginService.minSaldo(jumlahBeli, produk.getHarga());
                        foreignService.updateStock(id, jumlahBeli, ukuran.getId());
                    } else {
                        redirectAttributes.addFlashAttribute("error", "Stok tidak mencukupi!");
                    }
                    return "redirect:/produk";
                }
                return "redirect:/menu-adm";
            }
            return "belum-login";
        } catch (IllegalArgumentException e) {
            System.out.println("apakah kesini?");
            redirectAttributes.addFlashAttribute("error", "Saldo tidak mencukupi!");
            System.out.println("kesini?");
            return "redirect:/produk";
        }
    }

    @GetMapping("/edit-data")
    public String editData(Model model) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("user")) {
                Tamp userData = tampService.findAll().get(0); // Ambil data user dari tabel Tamp
                model.addAttribute("prof", List.of(userData));
                return "edit-data";
            }
            return "redirect:/menu-adm";
        }
        return "belum-login";
    }

    @PostMapping("/update-data")
    public String updateData(
            @RequestParam String nama,
            @RequestParam String username,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            RedirectAttributes redirectAttributes) {
        try {
            if (loginService.cek()) {
                if (loginService.getRole().equals("user")) {
                    Login login = loginRepository.findByUsername(username);
                    if (!login.getPassword().equals(oldPassword)) {
                        redirectAttributes.addFlashAttribute("error", "Password lama tidak cocok.");
                        return "redirect:/edit-data";
                    }
                    if (loginRepository.existsByNama(nama) && !login.getNama().equals(nama)) {
                        redirectAttributes.addFlashAttribute("error", "Nama sudah digunakan oleh user lain.");
                        return "redirect:/edit-data";
                    }

                    else {

                        // Update data
                        login.setNama(nama);
                        login.setPassword(newPassword);
                        loginRepository.save(login);

                        // Perbarui data di tabel Tamp
                        tampService.simpanDataLogin(
                                login.getNama(),
                                login.getUsername(),
                                login.getPassword(),
                                login.getSaldo());
                    }
                    redirectAttributes.addFlashAttribute("succes", "Data Berhasil Diupdate.");
                    return "redirect:/profile";
                }
                return "redirect:/menu-adm";
            }
            return "belum-login";
        } catch (Exception e) {
            return "redirect:/edit-data";
        }
    }

    // menambah ke keranjang
    @GetMapping("/keranjang/{id}")
    public String viewKeranjang(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("user")) {
                try {
                    keranjangService.addToCart(id);
                    redirectAttributes.addFlashAttribute("success", "Produk berhasil ditambahkan ke keranjang.");
                } catch (IllegalArgumentException e) {
                    redirectAttributes.addFlashAttribute("error", e.getMessage());
                }
                return "redirect:/produk";
            }
            return "redirect:/menu-adm";
        }
        return "redirect:/login";
    }

    // menampilkan keranjang
    @GetMapping("/keranjang")
    public String keranjang(Model model) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("user")) {
                List<Foerign> foerigns = new ArrayList<>();
                List<Keranjang> keranjang = keranjangService.getByUser();
                for (Keranjang krj : keranjang) {
                    foerigns.addAll(foreignService.getByProduk(krj.getProduk()));
                }
                model.addAttribute("frg", foerigns);
                model.addAttribute("bask", keranjang);
                return "keranjang";
            }
            return "redirect:/menu-adm";
        }
        return "belum-login";
    }

    @GetMapping("/buyy/{id}")
    public String buyOne(@PathVariable Integer id, @RequestParam Integer jumlah, @RequestParam Integer sizeId,
            RedirectAttributes redirectAttributes) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("user")) {
                try {
                    System.out.println("size id : " + sizeId);
                    Produk produk = produkService.getProduk(id);
                    if (foreignService.getStok(id, sizeId) >= jumlah) {
                        loginService.minSaldo(jumlah, produk.getHarga());
                    }
                    foreignService.updateStock(produk.getId(), jumlah, sizeId);
                    keranjangService.deleteKeranjangByUser(produk.getId());

                    historyPembelianService.saveHistory(
                            loginService.getLogin().getId(),
                            loginService.getLogin().getNama(), 
                            loginService.getLogin().getUsername(),
                            produk.getNamaProduk(), 
                            jumlah,
                            produk.getHarga() * jumlah 
                    );

                    System.out.println("masuk di try");
                    redirectAttributes.addFlashAttribute("success", "Berhasil Dibeli");
                    return "redirect:/produk";
                } catch (Exception e) {
                    System.out.println("masuk di catch");
                    redirectAttributes.addFlashAttribute("error", "Periksa Saldo Anda Atau Stok Barang Kurang");
                    return "redirect:/keranjang";
                }
            }
            return "redirect:/menu-adm";
        }
        return "belum-login";
    }

    @GetMapping("/checkout-all")
    public String checkoutAll(@RequestParam List<Integer> productIds, @RequestParam List<Integer> param,
            @RequestParam List<Integer> jumlah, RedirectAttributes redirectAttributes) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("user")) {
                int idx = 0, total = 0;

                for (Integer productId : productIds) {
                    Produk produk = produkService.getProduk(productId);
                    Integer stok = foreignService.getStok(productId, param.get(idx));

                    if (stok == null || stok < jumlah.get(idx)) {
                        redirectAttributes.addFlashAttribute("error", "Stok produk tidak mencukupi!");
                        return "redirect:/keranjang";
                    }
                    total += jumlah.get(idx) * produk.getHarga();
                    idx++;
                }

                Login login = loginService.getLogin();
                if (total > login.getSaldo()) {
                    redirectAttributes.addFlashAttribute("error", "Saldo Anda tidak mencukupi!");
                    return "redirect:/keranjang";
                }

                idx = 0;
                for (Integer productId : productIds) {
                    Produk produk = produkService.getProduk(productId);
                    loginService.minSaldo(jumlah.get(idx), produk.getHarga());
                    foreignService.updateStock(productId, jumlah.get(idx), param.get(idx));

                    // Simpan ke riwayat pembelian
                    historyPembelianService.saveHistory(
                            loginService.getLogin().getId(),
                            login.getNama(),
                            login.getUsername(),
                            produk.getNamaProduk(), 
                            jumlah.get(idx), 
                            produk.getHarga() * jumlah.get(idx)
                    );

                    idx++;
                }

                keranjangService.deleteAllByUser();
                redirectAttributes.addFlashAttribute("success", "Checkout berhasil!");
                return "redirect:/produk";
            }
            return "redirect:/menu-adm";
        }
        return "belum-login";
    }

    // menghapus semua yang ada di keranjang
    @GetMapping("/delete-all")
    public String deleteCart(RedirectAttributes redirectAttributes) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("user")) {
                keranjangService.deleteAllByUser();
                redirectAttributes.addFlashAttribute("success", "Hapus Semua Item dari keranjang berhasil!");
                return "redirect:/produk";
            }
            return "redirect:/menu-adm";
        }
        return "belum-login";
    }

    // menghapus item yang ada di keranjang
    @GetMapping("/delete-card/{id}")
    public String dltCard(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        keranjangService.deleteKeranjang(id);
        redirectAttributes.addFlashAttribute("success", "Hapus Item dari keranjang berhasil!");
        return "redirect:/produk";
    }

    @GetMapping("/top-up")
    public String showTopUpPage() {
        if (loginService.cek()) {
            if (loginService.getRole().equals("user")) {
                return "top-up";
            }
            return "redirect:/menu-adm";
        }
        return "belum-login";
    }

    @PostMapping("/top-up")
    public String processTopUp(
            @RequestParam("nominal") BigDecimal nominal,
            RedirectAttributes redirectAttributes) {
        try {
            if (loginService.cek()) {
                if (loginService.getRole().equals("user")) {
                    boolean topUpSuccess = loginService.topUpSaldo(nominal);

                    if (topUpSuccess) {
                        redirectAttributes.addFlashAttribute("succes",
                                "Top-up berhasil. Saldo bertambah Rp. " + nominal);
                        return "redirect:/profile";
                    }
                }
                return "redirect:/menu-adm";
            }
            return "belum-login";
        } catch (Exception e) {
            return "redirect:/top-up";
        }
    }

    @GetMapping("/history-user")
    public String historyUser(Model model) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("user")) {
                Integer userId = loginService.getLogin().getId(); 
                List<HistoryPembelian> historyList = historyPembelianService.getHistoryByUserId(userId);

                model.addAttribute("historyList", historyList);
                return "history-user"; 
            }
            return "redirect:/menu-adm";
        }
        return "redirect:/login";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

}

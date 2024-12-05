package com.login.demo.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.login.demo.models.Foerign;
import com.login.demo.models.HistoryPembelian;
import com.login.demo.models.Kategori;
import com.login.demo.models.Login;
import com.login.demo.models.Produk;
import com.login.demo.service.ForeignService;
import com.login.demo.service.HistoryPembelianService;
import com.login.demo.service.KategoriService;
import com.login.demo.service.LoginService;
import com.login.demo.service.ProdukService;
import com.login.demo.service.StokService;
import com.login.demo.service.UkuranService;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {
    @Autowired
    private ProdukService produkService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private KategoriService kategoriService;

    @Autowired
    private UkuranService ukuranService;

    @Autowired
    private StokService stokService;

    @Autowired
    private ForeignService foreignService;

    @Autowired
    private HistoryPembelianService historyPembelianService;

    @GetMapping("/menu-adm")
    public String menuAdm(Model model) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("admin")) {
                model.addAttribute("page", produkService.findAll());
                return "adm-page";
            }
            return "redirect:/produk";
        }
        return "belum-login";
    }

    @GetMapping("/adm-page")
    public String admPage(Model model) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("admin")) {
                model.addAttribute("page", produkService.findAll());
                return "adm-page";
            }
            return "redirect:/produk";
        }
        return "belum-login";
    }

    @GetMapping("/tambah-produk")
    public String addProduk(Model model) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("admin")) {
                model.addAttribute("baru", new Produk());
                model.addAttribute("kategori", kategoriService.findAll());
                return "tambah-produk";
            }
            return "redirect:/produk";
        }
        return "belum-login";
    }

    @PostMapping("/save-product")
    public String saveProduct(@RequestParam("ukuran[]") List<Integer> ukuran, Model model,
            @RequestParam("stok[]") List<Integer> stok, Produk produk, @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes)
            throws IOException {
        if (loginService.cek()) {
            if (loginService.getRole().equals("admin")) {
                try {
                    produk.setGambar(file.getBytes());
                    ukuranService.saveUkuran(ukuran);
                    stokService.saveStok(stok);
                    produkService.saveProd(produk);
                    int idx = 0;
                    model.addAttribute("ukuran", stok);
                    for (Integer integer : stok) {
                        produkService.saveF(produk.getId(), integer, ukuran.get(idx));
                        idx++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return "redirect:/adm-page";
                }
                redirectAttributes.addFlashAttribute("success", "Produk Berhasil Ditambahkan");
                return "redirect:/adm-page";
            }
            return "redirect:/produk";
        }
        return "belum-login";
    }

    @GetMapping("/hapus-produk/{id}")
    public String delProduk(@PathVariable Integer id) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("admin")) {
                produkService.deleteById(id);
                return "redirect:/adm-page";
            }
            return "redirect:/produk";
        }
        return "belum-login";
    }

    @GetMapping("/user-data")
    public String viewUser(Model model) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("admin")) {
                List<Login> logins = loginService.findAll();
                model.addAttribute("list", logins);
                return "user-data";
            }
            return "redirect:/produk";
        }
        return "belum-login";
    }

    @GetMapping("/delete-user/{id}")
    public String deleteuser(@PathVariable Integer id) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("admin")) {
                loginService.deleteById(id);
                return "redirect:/user-data";
            }
            return "redirect:/produk";
        }
        return "belum-login";
    }

    @GetMapping("/add-kategori")
    public String addKategori(Model model) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("admin")) {
                model.addAttribute("kate", new Kategori());
                model.addAttribute("kategoris", kategoriService.findAll());
                return "kategori";
            }
            return "redirect:/produk";
        }
        return "belum-login";
    }

    @PostMapping("/save-kategori")
    public String saveKategori(Kategori kategori, RedirectAttributes redirectAttributes) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("admin")) {
                List<Kategori> existingCategories = kategoriService.findAll();

                boolean isDuplicate = existingCategories.stream()
                        .anyMatch(existing -> existing.getName().equalsIgnoreCase(kategori.getName()));

                if (isDuplicate) {
                    redirectAttributes.addFlashAttribute("error",
                            "Kategori sudah ada!");
                    return "redirect:/add-kategori";
                }

                kategoriService.saveKat(kategori);
                redirectAttributes.addFlashAttribute("success", "Kategori berhasil ditambahkan!");
                return "redirect:/adm-page";
            }
            return "redirect:/produk";
        }
        return "belum-login";
    }

    @GetMapping("/produk-gambar/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getProductImage(@PathVariable Integer id) {
        Produk produk = produkService.findById(id).orElseThrow(() -> new RuntimeException("produk tidak ditemukan"));

        byte[] imageBytes = produk.getGambar();
        if (produk.getGambar() == null || imageBytes.length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }

    @GetMapping("/detail-produk/{id}")
    public String detailProduk(@PathVariable Integer id, Model model) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("admin")) {
                Produk produk = produkService.getProduk(id);

                List<Foerign> foerigns = foreignService.getByProduk(produk);

                model.addAttribute("produk", produk);
                model.addAttribute("ukuranList", foerigns);

                return "detail-produk"; 
            }
            return "redirect:/produk";
        }
        return "belum-login";
    }

    @GetMapping("edit-produk/{id}")
    public String editProd(@PathVariable Integer id, Model model) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("admin")) {
                model.addAttribute("prd", produkService.getProduk(id));
                model.addAttribute("kategoriList", kategoriService.findAll());
                return "edit-produk";
            }
            return "redirect:/produk";
        }
        return "belum-login";
    }

    @PostMapping("/save-edit")
    public String saveEdit(Produk produk, RedirectAttributes redirectAttributes) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("admin")) {
                produkService.saveProd(produk);
                redirectAttributes.addFlashAttribute("success", "Data Berhasil Di Update");
                return "redirect:/adm-page";
            }
            return "redirect:/produk";
        }
        return "belum-login";
    }

    @PostMapping("/produk/update-stok-manual")
    public String updateStokManual(@RequestParam("idProduk") Integer idProduk,
            @RequestParam("idUkuran") Integer idUkuran,
            @RequestParam("stokBaru") Integer stokbaru,
            @RequestParam("idStok") Integer idStok,
            RedirectAttributes redirectAttributes) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("admin")) {
                try {
                    foreignService.updateStokByAdmin(idProduk, idUkuran, stokbaru, idStok);
                    redirectAttributes.addFlashAttribute("success", "Stok berhasil diperbarui!");
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
                }
                return "redirect:/detail-produk/" + idProduk;

            }
            return "redirect:/produk";

        }
        return "belum-login";

    }

    @GetMapping("/history")
    public String viewHistory(Model model) {
        if (loginService.cek()) {
            if (loginService.getRole().equals("admin")) {
                List<HistoryPembelian> histories = historyPembelianService.getAllHistory();
                model.addAttribute("histories", histories);
                return "history-admin";

            }
            return "redirect:/produk";
        }
        return "belum-login";

    }
}

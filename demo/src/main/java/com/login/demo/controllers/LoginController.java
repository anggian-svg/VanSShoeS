package com.login.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.login.demo.models.Login;
import com.login.demo.repositories.LoginRepository;
import com.login.demo.service.LoginService;
import com.login.demo.service.TampService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private TampService tampService;

    @GetMapping("/home")
    public String register(Model model) {
        Login login = loginRepository.findByRole("admin");
        if (login == null) {
            loginService.createAdm();
        }
        model.addAttribute("acount", new Login());
        return "login";
    }

    @PostMapping("/save-register")
    public String saveRegis(Login login, RedirectAttributes redirectAttributes) {
        try {
            loginService.saveRegister(login);
            redirectAttributes.addFlashAttribute("success","Akun Berhasil Di Buat");
            return "redirect:/home";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/home";
        }
    }

    @GetMapping("/cek-login")
    public String cekLogin(Login login, RedirectAttributes redirectAttributes) {
        String result = loginService.cekLogin(login, redirectAttributes);

        if (result.equals("redirect:/home")) { 
            redirectAttributes.addAttribute("error", "Invalid username or password");
            return result;
        }

        return result; 
    }

    @GetMapping("/log-out")
    public String logOut() {
        tampService.hapusSemuaData();
        return "redirect:/home";
    }
}

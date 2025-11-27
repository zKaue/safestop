package com.safestop.controller;

import com.safestop.entity.Usuario;
import com.safestop.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "login";
    }

    /**
     * Registra um novo usuário (sem permissão de Admin e inativo por padrão).
     * Trata exceção de e-mail duplicado.
     */
    @PostMapping("/register")
    public String registerNewUser(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            usuario.setAdmin(false);
            usuario.setAtivo(false);
            usuarioService.salvarNovoFuncionario(usuario);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Conta criada! Aguarde um administrador ativar seu login.");

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Este email já está em uso!");
        }

        return "redirect:/login";
    }
}
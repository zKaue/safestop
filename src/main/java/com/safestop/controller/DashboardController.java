package com.safestop.controller;

import com.safestop.dto.DashboardStatsDTO;
import com.safestop.service.VagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    @Autowired
    private VagaService vagaService;

    /**
     * Carrega os cards de estatísticas (Vagas Livres, Ocupadas) e a lista de tickets ativos.
     * Também verifica se o usuário logado possui role de ADMIN.
     */
    @GetMapping("/")
    public String getDashboard(Model model,
                               @RequestParam(value = "termoBusca", required = false) String termoBusca,
                               Authentication authentication) {

        DashboardStatsDTO stats = vagaService.getDashboardStats(termoBusca);
        model.addAttribute("stats", stats);
        model.addAttribute("termoBuscaPesquisado", termoBusca);

        boolean isAdmin = false;
        if (authentication != null) {
            isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ADMIN"));
        }
        model.addAttribute("isAdmin", isAdmin);

        return "dashboard";
    }
}
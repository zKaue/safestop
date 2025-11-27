package com.safestop.controller;

import com.safestop.dto.DashboardStatsDTO;
import com.safestop.service.VagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// 1. IMPORTE ESTAS CLASSES (MUITO IMPORTANTE)
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    @Autowired
    private VagaService vagaService;

    @GetMapping("/")
    public String getDashboard(Model model,
                               // 1. Recebe 'termoBusca'
                               @RequestParam(value = "termoBusca", required = false) String termoBusca,
                               Authentication authentication) {

        // 2. Passa 'termoBusca' para o service
        DashboardStatsDTO stats = vagaService.getDashboardStats(termoBusca);

        model.addAttribute("stats", stats);

        // 3. Devolve 'termoBusca' para o HTML "lembrar"
        model.addAttribute("termoBuscaPesquisado", termoBusca);

        // 7. Lógica para verificar se é Admin
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
package com.safestop.controller;

import com.safestop.dto.VagaAdminDTO;
import com.safestop.entity.Configuracao;
import com.safestop.entity.Ticket;
import com.safestop.entity.Usuario;
import com.safestop.entity.Vaga;
import com.safestop.enums.TicketStatus;
import com.safestop.enums.TipoVaga;
import com.safestop.enums.PrefixoAndar;
import com.safestop.repository.ConfiguracaoRepository;
import com.safestop.repository.UsuarioRepository;
import com.safestop.repository.VagaRepository;
import com.safestop.repository.TicketRepository;
import com.safestop.service.UsuarioService;
import com.safestop.service.VagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private VagaRepository vagaRepository;
    @Autowired
    private VagaService vagaService;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ConfiguracaoRepository configuracaoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private UsuarioService usuarioService;

    // ---------------------------------------------------------
    // GERENCIAMENTO DE VAGAS
    // ---------------------------------------------------------

    @GetMapping("/vagas")
    public String getGerenciarVagas(Model model,
                                    @RequestParam(value = "filtro_prefixo", required = false) String prefixo,
                                    @RequestParam(value = "filtro_tipo", required = false) TipoVaga tipo,
                                    @RequestParam(value = "filtro_ativo", required = false) Boolean ativo) {

        List<Vaga> vagasFiltradas;
        if (prefixo != null) {
            vagasFiltradas = vagaRepository.findByNumeroVagaStartingWithOrderByNumeroVagaAsc(prefixo + "-");
        } else if (tipo != null) {
            vagasFiltradas = vagaRepository.findByTipoOrderByNumeroVagaAsc(tipo);
        } else if (ativo != null) {
            vagasFiltradas = vagaRepository.findByAtivoOrderByNumeroVagaAsc(ativo);
        } else {
            vagasFiltradas = vagaRepository.findAll(Sort.by("numeroVaga"));
        }

        List<Ticket> ticketsAbertos = ticketRepository.findByStatus(TicketStatus.ABERTO);
        Set<Long> idsVagasEmUso = ticketsAbertos.stream()
                .map(ticket -> ticket.getVaga().getId())
                .collect(Collectors.toSet());
        List<VagaAdminDTO> vagasParaHtml = new ArrayList<>();
        for (Vaga vaga : vagasFiltradas) {
            boolean emUso = idsVagasEmUso.contains(vaga.getId());
            vagasParaHtml.add(new VagaAdminDTO(vaga, emUso));
        }

        model.addAttribute("vagasDTO", vagasParaHtml);
        model.addAttribute("prefixos", PrefixoAndar.values());
        model.addAttribute("tiposDeVaga", TipoVaga.values());
        model.addAttribute("filtroPrefixoAtivo", prefixo);
        model.addAttribute("filtroTipoAtivo", tipo);
        model.addAttribute("filtroAtivoAtivo", ativo);

        return "admin-vagas";
    }

    @GetMapping("/vagas/nova")
    public String getFormNovaVaga(Model model) {
        model.addAttribute("prefixos", PrefixoAndar.values());
        model.addAttribute("vaga", new Vaga());
        model.addAttribute("tiposDeVaga", TipoVaga.values());
        return "admin-vaga-form";
    }

    @PostMapping("/vagas/salvar")
    public String salvarNovaVaga(@RequestParam("prefixo") PrefixoAndar prefixo,
                                 @RequestParam("tipo") TipoVaga tipo) {
        vagaService.criarNovaVagaAutomatica(prefixo, tipo);
        return "redirect:/admin/vagas";
    }

    @GetMapping("/vagas/massa")
    public String getFormVagasEmMassa(Model model) {
        model.addAttribute("prefixos", PrefixoAndar.values());
        model.addAttribute("tiposDeVaga", TipoVaga.values());
        return "admin-vagas-massa-form";
    }

    @PostMapping("/vagas/massa/salvar")
    public String salvarVagasEmMassa(@RequestParam("prefixo") PrefixoAndar prefixo,
                                     @RequestParam("quantidade") int quantidade,
                                     @RequestParam("tipo") TipoVaga tipo) {
        vagaService.criarVagasEmMassa(prefixo, quantidade, tipo);
        return "redirect:/admin/vagas";
    }

    @PostMapping("/vagas/toggle/{id}")
    public String toggleVagaAtivo(@PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes,
                                  HttpServletRequest request) {
        boolean sucesso = vagaService.toggleAtivoStatus(id);
        if (!sucesso) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Não é possível desativar uma vaga que está em uso!");
        }
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/admin/vagas";
    }

    @GetMapping("/vagas/editar/{id}")
    public String getFormEditarVaga(@PathVariable("id") Long id, Model model) {
        Optional<Vaga> vagaOpt = vagaRepository.findById(id);
        if (vagaOpt.isEmpty()) {
            return "redirect:/admin/vagas";
        }
        model.addAttribute("vaga", vagaOpt.get());
        model.addAttribute("tiposDeVaga", TipoVaga.values());
        return "admin-vaga-editar-form";
    }

    @PostMapping("/vagas/editar/salvar")
    public String salvarEdicaoVaga(@RequestParam("id") Long id,
                                   @RequestParam("tipo") TipoVaga tipo) {
        vagaService.atualizarTipoVaga(id, tipo);
        return "redirect:/admin/vagas";
    }

    // ---------------------------------------------------------
    // CONFIGURAÇÕES GERAIS
    // ---------------------------------------------------------

    @GetMapping("/configuracoes")
    public String getFormConfiguracoes(Model model) {
        Configuracao config = configuracaoRepository.findById(1L)
                .orElse(new Configuracao());
        model.addAttribute("config", config);
        return "admin-config-form";
    }

    @PostMapping("/configuracoes/salvar")
    public String salvarConfiguracoes(@ModelAttribute Configuracao config) {
        config.setId(1L);
        configuracaoRepository.save(config);
        return "redirect:/";
    }

    // ---------------------------------------------------------
    // GERENCIAMENTO DE FUNCIONÁRIOS
    // ---------------------------------------------------------

    @GetMapping("/funcionarios")
    public String getGerenciarFuncionarios(Model model) {
        List<Usuario> todosOsUsuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", todosOsUsuarios);
        return "admin-funcionarios";
    }

    @PostMapping("/funcionarios/toggle/{id}")
    public String toggleFuncionarioAtivo(@PathVariable("id") Long id,
                                         RedirectAttributes redirectAttributes,
                                         Authentication authentication) {
        String emailAdminLogado = authentication.getName();
        boolean sucesso = usuarioService.toggleAtivoStatus(id, emailAdminLogado);
        if (!sucesso) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Um administrador não pode desativar a si mesmo!");
        }
        return "redirect:/admin/funcionarios";
    }

    @GetMapping("/funcionarios/editar/{id}")
    public String getFormEditarFuncionario(@PathVariable("id") Long id, Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/admin/funcionarios";
        }
        model.addAttribute("usuario", usuarioOpt.get());
        return "admin-funcionario-editar-form";
    }

    @PostMapping("/funcionarios/editar/salvar")
    public String salvarEdicaoFuncionario(@ModelAttribute Usuario usuario,
                                          RedirectAttributes redirectAttributes) {
        try {
            usuarioService.atualizarFuncionario(usuario);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "O email '" + usuario.getEmail() + "' já está em uso!");
            return "redirect:/admin/funcionarios/editar/" + usuario.getId();
        }
        return "redirect:/admin/funcionarios";
    }

    @GetMapping("/funcionarios/novo")
    public String getFormNovoFuncionario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "admin-funcionario-form";
    }

    @PostMapping("/funcionarios/novo/salvar")
    public String salvarNovoFuncionario(@ModelAttribute Usuario usuario,
                                        RedirectAttributes redirectAttributes) {
        try {
            usuarioService.salvarNovoFuncionario(usuario);
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "O email '" + usuario.getEmail() + "' já está em uso!");
            return "redirect:/admin/funcionarios/novo";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Funcionário criado com sucesso!");
        return "redirect:/admin/funcionarios";
    }

    // ---------------------------------------------------------
    // RELATÓRIOS
    // ---------------------------------------------------------

    /**
     * Gera relatório financeiro e operacional com base em um intervalo de datas.
     * Se nenhuma data for informada, utiliza o dia atual (Hoje).
     */
    @GetMapping("/relatorios")
    public String getRelatorios(
            @RequestParam(value = "dataInicio", required = false) String dataInicioStr,
            @RequestParam(value = "dataFim", required = false) String dataFimStr,
            Model model) {

        LocalDate inicio;
        LocalDate fim;

        if (dataInicioStr == null || dataInicioStr.isEmpty()) {
            inicio = LocalDate.now();
        } else {
            inicio = LocalDate.parse(dataInicioStr);
        }

        if (dataFimStr == null || dataFimStr.isEmpty()) {
            fim = LocalDate.now();
        } else {
            fim = LocalDate.parse(dataFimStr);
        }

        Double faturamentoPeriodo = 0.0;
        Long ticketsFechados = 0L;
        Long novasEntradas = 0L;
        Double valorMedio = 0.0;

        LocalDateTime inicioDoDia = inicio.atStartOfDay();
        LocalDateTime fimDoDia = fim.atTime(LocalTime.MAX);

        Double faturamento = ticketRepository.sumValorFechadoEntreDatas(inicioDoDia, fimDoDia);
        Long fechados = ticketRepository.countByStatusAndHorarioSaidaBetween(TicketStatus.FECHADO, inicioDoDia, fimDoDia);
        Long entradas = ticketRepository.countByHorarioEntradaBetween(inicioDoDia, fimDoDia);

        faturamentoPeriodo = (faturamento != null) ? faturamento : 0.0;
        ticketsFechados = (fechados != null) ? fechados : 0L;
        novasEntradas = (entradas != null) ? entradas : 0L;

        if (ticketsFechados > 0) {
            valorMedio = faturamentoPeriodo / ticketsFechados;
        }

        model.addAttribute("faturamentoPeriodo", faturamentoPeriodo);
        model.addAttribute("ticketsFechados", ticketsFechados);
        model.addAttribute("novasEntradas", novasEntradas);
        model.addAttribute("valorMedio", valorMedio);
        model.addAttribute("dataInicioPesquisada", inicio.toString());
        model.addAttribute("dataFimPesquisada", fim.toString());

        return "admin-relatorios";
    }
}
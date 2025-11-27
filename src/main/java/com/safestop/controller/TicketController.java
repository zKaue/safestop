package com.safestop.controller;

import com.safestop.entity.Ticket;
import com.safestop.entity.Vaga;
import com.safestop.entity.Veiculo;
import com.safestop.service.ClienteService;
import com.safestop.service.TicketService;
import com.safestop.service.VagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class TicketController {

    @Autowired
    private VagaService vagaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private TicketService ticketService;

    @GetMapping("/ticket/novo")
    public String getFormNovoTicket(Model model) {
        List<Vaga> vagasDisponiveis = vagaService.getVagasDisponiveis();
        model.addAttribute("vagasDisponiveis", vagasDisponiveis);
        return "registrar-entrada";
    }

    /**
     * Processa a entrada de um veículo.
     * Busca ou cria o cliente/veículo e gera um novo ticket vinculado à vaga.
     */
    @PostMapping("/ticket/salvar")
    public String salvarNovoTicket(
            @RequestParam("placa") String placa,
            @RequestParam("modelo") String modelo,
            @RequestParam("nomeCliente") String nomeCliente,
            @RequestParam("telefoneCliente") String telefoneCliente,
            @RequestParam("vagaId") Long vagaId) {

        Veiculo veiculo = clienteService.findOrCreateClienteComVeiculo(
                placa, modelo, nomeCliente, telefoneCliente
        );

        ticketService.registrarEntrada(veiculo, vagaId);

        return "redirect:/";
    }

    /**
     * Finaliza o ticket, libera a vaga e aplica descontos (se houver).
     */
    @PostMapping("/ticket/confirmar-saida")
    public String confirmarSaidaDoTicket(
            @RequestParam("ticketId") Long ticketId,
            @RequestParam(value = "descontoReais", required = false) Double descontoReais,
            RedirectAttributes redirectAttributes) {

        Ticket ticketFechado = ticketService.confirmarSaida(ticketId, descontoReais);
        String valorFormatado = String.format("R$ %.2f", ticketFechado.getValor());
        String mensagem = String.format("Saída do veículo %s registrada! Valor final: %s",
                ticketFechado.getVeiculo().getPlaca(),
                valorFormatado);
        redirectAttributes.addFlashAttribute("successMessage", mensagem);
        return "redirect:/";
    }

    @GetMapping("/ticket/saida/{id}")
    public String getFormRegistrarSaida(@PathVariable("id") Long ticketId, Model model) {
        carregarDadosTicket(ticketId, model);
        return "registrar-saida";
    }

    @GetMapping("/ticket/detalhes/{id}")
    public String getDetalhesTicket(@PathVariable("id") Long ticketId, Model model) {
        carregarDadosTicket(ticketId, model);
        return "ticket-detalhes";
    }

    /**
     * Método auxiliar para carregar dados do ticket e calcular tempo decorrido para exibição.
     */
    private void carregarDadosTicket(Long ticketId, Model model) {
        Ticket ticket = ticketService.calcularValorAtual(ticketId);
        Duration duracao = Duration.between(ticket.getHorarioEntrada(), LocalDateTime.now());
        long horas = duracao.toHours();
        long minutos = duracao.toMinutes() % 60;
        String tempoTotal = String.format("%d horas e %d minutos", horas, minutos);
        model.addAttribute("ticket", ticket);
        model.addAttribute("tempoTotal", tempoTotal);
    }
}
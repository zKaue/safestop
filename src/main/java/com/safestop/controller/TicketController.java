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
     * === MÉTODO ATUALIZADO (sem marca, cor, ano) ===
     */
    @PostMapping("/ticket/salvar")
    public String salvarNovoTicket(
            // --- Campos OBRIGATÓRIOS ---
            @RequestParam("placa") String placa,
            @RequestParam("modelo") String modelo,
            @RequestParam("nomeCliente") String nomeCliente,
            @RequestParam("telefoneCliente") String telefoneCliente,
            @RequestParam("vagaId") Long vagaId

            // --- Campos OPCIONAIS REMOVIDOS ---
    ) {

        // 1. Chama o "Cérebro" com a nova assinatura
        Veiculo veiculo = clienteService.findOrCreateClienteComVeiculo(
                placa, modelo, nomeCliente, telefoneCliente
        );

        // 2. Chama o "Executor" para criar o Ticket
        ticketService.registrarEntrada(veiculo, vagaId);

        // 3. Redireciona de volta para o Dashboard
        return "redirect:/";
    }

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
        Ticket ticket = ticketService.calcularValorAtual(ticketId);
        Duration duracao = Duration.between(ticket.getHorarioEntrada(), LocalDateTime.now());
        long horas = duracao.toHours();
        long minutos = duracao.toMinutes() % 60;
        String tempoTotal = String.format("%d horas e %d minutos", horas, minutos);
        model.addAttribute("ticket", ticket);
        model.addAttribute("tempoTotal", tempoTotal);
        return "registrar-saida";
    }

    @GetMapping("/ticket/detalhes/{id}")
    public String getDetalhesTicket(@PathVariable("id") Long ticketId, Model model) {
        Ticket ticket = ticketService.calcularValorAtual(ticketId);
        Duration duracao = Duration.between(ticket.getHorarioEntrada(), LocalDateTime.now());
        long horas = duracao.toHours();
        long minutos = duracao.toMinutes() % 60;
        String tempoTotal = String.format("%d horas e %d minutos", horas, minutos);
        model.addAttribute("ticket", ticket);
        model.addAttribute("tempoTotal", tempoTotal);
        return "ticket-detalhes";
    }
}
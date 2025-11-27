package com.safestop.service;

import com.safestop.entity.Configuracao;
import com.safestop.entity.Ticket;
import com.safestop.entity.Vaga;
import com.safestop.entity.Veiculo;
import com.safestop.enums.TicketStatus;
import com.safestop.repository.ConfiguracaoRepository;
import com.safestop.repository.TicketRepository;
import com.safestop.repository.VagaRepository;
import com.safestop.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private ConfiguracaoRepository configuracaoRepository;

    /**
     * Valida se a vaga está livre e registra o início da estadia.
     */
    @Transactional
    public Ticket registrarEntrada(Veiculo veiculo, Long vagaId) {

        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada!"));

        Ticket ticketJaAberto = ticketRepository.findByVagaIdAndStatus(vagaId, TicketStatus.ABERTO);
        if (ticketJaAberto != null) {
            throw new RuntimeException("Esta vaga já está ocupada!");
        }

        Ticket novoTicket = new Ticket();
        novoTicket.setVeiculo(veiculo);
        novoTicket.setVaga(vaga);
        novoTicket.setHorarioEntrada(LocalDateTime.now());
        novoTicket.setStatus(TicketStatus.ABERTO);

        return ticketRepository.save(novoTicket);
    }

    /**
     * Calcula o valor devido até o momento presente, sem fechar o ticket.
     * Usado para exibir a prévia na tela de saída.
     */
    public Ticket calcularValorAtual(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));

        double valor = calcularValor(ticket.getHorarioEntrada(), LocalDateTime.now());
        ticket.setValor(valor);

        return ticket;
    }

    /**
     * Finaliza o ticket: define horário de saída, calcula valor final (aplicando descontos)
     * e altera o status para FECHADO.
     */
    @Transactional
    public Ticket confirmarSaida(Long ticketId, Double descontoReais) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));

        ticket.setHorarioSaida(LocalDateTime.now());
        ticket.setStatus(TicketStatus.FECHADO);

        double valorBruto = calcularValor(ticket.getHorarioEntrada(), ticket.getHorarioSaida());
        double desconto = (descontoReais != null) ? descontoReais : 0.0;

        double valorFinal = valorBruto - desconto;
        if (valorFinal < 0) {
            valorFinal = 0;
        }

        BigDecimal valorFinalFormatado = new BigDecimal(valorFinal)
                .setScale(2, RoundingMode.HALF_UP);

        ticket.setValor(valorFinalFormatado.doubleValue());

        return ticketRepository.save(ticket);
    }

    /**
     * Lógica de precificação dinâmica.
     * Busca tolerância e valor/hora no banco de dados (Configuracao) e aplica a regra de cobrança.
     */
    private double calcularValor(LocalDateTime entrada, LocalDateTime saida) {

        Configuracao config = configuracaoRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Configuração do sistema (ID=1) não encontrada!"));

        double valorPorHora = config.getValorPorHora();
        long minutosDeTolerancia = config.getMinutosTolerancia();

        Duration duracao = Duration.between(entrada, saida);
        long minutosTotais = duracao.toMinutes();

        if (minutosTotais <= minutosDeTolerancia) {
            return 0.0;
        }

        double horasFracionadas = minutosTotais / 60.0;
        long horasCobradas = (long) Math.ceil(horasFracionadas);

        return horasCobradas * valorPorHora;
    }
}
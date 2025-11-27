package com.safestop.service;

import com.safestop.entity.Configuracao; // <-- 1. IMPORTADO
import com.safestop.entity.Ticket;
import com.safestop.entity.Vaga;
import com.safestop.entity.Veiculo;
import com.safestop.enums.TicketStatus;
import com.safestop.repository.ConfiguracaoRepository; // <-- 2. IMPORTADO
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
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private VagaRepository vagaRepository;

    // 3. INJETAMOS O NOVO REPOSITÓRIO DO "COFRE"
    @Autowired
    private ConfiguracaoRepository configuracaoRepository;

    // 4. AS LINHAS 'static final' FORAM REMOVIDAS DAQUI
    //

    /**
     * Lógica de Registrar Entrada (você já tem)
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
     * Fecha um ticket (dá saída no veículo) e calcula o valor.
     * (Este método já existia)
     */
    public Ticket fecharTicket(Long ticketId) {
        // 1. Encontra o ticket
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));

        // 2. Define os horários
        ticket.setHorarioSaida(LocalDateTime.now());
        ticket.setStatus(TicketStatus.FECHADO);

        // 3. === A MUDANÇA ESTÁ AQUI ===
        //    Ele agora chama o 'calcularValor' ATUALIZADO
        double valor = calcularValor(ticket.getHorarioEntrada(), ticket.getHorarioSaida());
        ticket.setValor(valor);

        return ticketRepository.save(ticket);
    }

    /**
     * === MÉTODO ATUALIZADO (Lógica de Preço Dinâmica) ===
     * Método PRIVADO com a sua regra de negócio de preço.
     */
    private double calcularValor(LocalDateTime entrada, LocalDateTime saida) {

        // 1. Busca as regras de negócio do BANCO DE DADOS
        Configuracao config = configuracaoRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Configuração do sistema (ID=1) não encontrada!"));

        double valorPorHora = config.getValorPorHora();
        long minutosDeTolerancia = config.getMinutosTolerancia();


        Duration duracao = Duration.between(entrada, saida);
        long minutosTotais = duracao.toMinutes();

        // 2. Regra dos minutos grátis (agora dinâmica)
        if (minutosTotais <= minutosDeTolerancia) {
            return 0.0;
        }

        // 3. Regra do arredondamento (idêntica)
        double horasFracionadas = minutosTotais / 60.0;
        long horasCobradas = (long) Math.ceil(horasFracionadas);

        // 4. Retorna o valor final (agora dinâmico)
        return horasCobradas * valorPorHora;
    }

    public Ticket calcularValorAtual(Long ticketId) {
        // 1. Encontra o ticket
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));

        // 2. Calcula o valor (usando o método que já lê do DB)
        double valor = calcularValor(ticket.getHorarioEntrada(), LocalDateTime.now());
        ticket.setValor(valor); // Define o valor preliminar

        return ticket; // Retorna o ticket com o valor calculado
    }

    @Transactional
    public Ticket confirmarSaida(Long ticketId, Double descontoReais) {
        // 1. Encontra o ticket
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket não encontrado"));

        // 2. Define os horários e status
        ticket.setHorarioSaida(LocalDateTime.now());
        ticket.setStatus(TicketStatus.FECHADO);

        // 3. Calcula o valor BRUTO (usando o método que já lê do DB)
        double valorBruto = calcularValor(ticket.getHorarioEntrada(), ticket.getHorarioSaida());

        // 4. Garante que o desconto não é nulo
        double desconto = (descontoReais != null) ? descontoReais : 0.0;

        // 5. Calcula o valor final
        double valorFinal = valorBruto - desconto;
        if (valorFinal < 0) {
            valorFinal = 0; // Não pode ser negativo
        }

        // 6. Arredonda para 2 casas decimais (evitar problemas com 0.00001)
        BigDecimal valorFinalFormatado = new BigDecimal(valorFinal)
                .setScale(2, RoundingMode.HALF_UP);

        ticket.setValor(valorFinalFormatado.doubleValue());

        return ticketRepository.save(ticket);
    }

}
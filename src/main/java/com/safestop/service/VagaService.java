package com.safestop.service;

import com.safestop.dto.DashboardStatsDTO;
import com.safestop.entity.Configuracao;
import com.safestop.entity.Ticket;
import com.safestop.entity.Vaga;
import com.safestop.enums.PrefixoAndar;
import com.safestop.enums.TicketStatus;
import com.safestop.enums.TipoVaga;
import com.safestop.repository.ConfiguracaoRepository;
import com.safestop.repository.TicketRepository;
import com.safestop.repository.VagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate; // <-- IMPORT ADICIONADO
import java.time.LocalDateTime;
import java.time.LocalTime; // <-- IMPORT ADICIONADO
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VagaService {

    @Autowired
    private VagaRepository vagaRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ConfiguracaoRepository configuracaoRepository;

    // --- (getNumeroDeVagasLivres) ---
    public long getNumeroDeVagasLivres() {
        long totalVagasAtivas = vagaRepository.countByAtivo(true);
        long vagasOcupadas = ticketRepository.countByStatus(TicketStatus.ABERTO);
        return totalVagasAtivas - vagasOcupadas;
    }

    /**
     * === MÉTODO ATUALIZADO (com Faturamento Diário) ===
     */
    public DashboardStatsDTO getDashboardStats(String termoBusca) {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // 1. Estatísticas de Vagas
        long totalVagas = vagaRepository.countByAtivo(true);
        long vagasOcupadas = ticketRepository.countByStatus(TicketStatus.ABERTO);
        long vagasLivres = totalVagas - vagasOcupadas;
        double percentual = 0.0;
        if (totalVagas > 0) {
            percentual = ((double) vagasOcupadas / totalVagas) * 100.0;
        }

        // 2. Lógica de Busca de Tickets
        List<Ticket> ticketsAbertos;
        if (StringUtils.hasText(termoBusca)) {
            ticketsAbertos = ticketRepository
                    .findByStatusAndPlacaOuClienteNome(TicketStatus.ABERTO, termoBusca);
        } else {
            ticketsAbertos = ticketRepository.findByStatus(TicketStatus.ABERTO);
        }

        // 3. === A NOVA LÓGICA DE FATURAMENTO DIÁRIO ===
        LocalDateTime inicioDoDia = LocalDate.now().atStartOfDay(); // Ex: Hoje às 00:00:00
        LocalDateTime fimDoDia = LocalDate.now().atTime(LocalTime.MAX); // Ex: Hoje às 23:59:59

        Double faturamentoHoje = ticketRepository.sumValorFechadoEntreDatas(inicioDoDia, fimDoDia);

        // 4. Preenche o DTO
        stats.setTotalVagas(totalVagas);
        stats.setVagasLivres(vagasLivres);
        stats.setVagasOcupadas(vagasOcupadas);
        stats.setPercentualOcupacao(percentual);
        stats.setTicketsAbertos(ticketsAbertos);

        // Se for nulo (nenhuma venda), define como 0.0
        stats.setFaturamentoDiario(faturamentoHoje != null ? faturamentoHoje : 0.0);

        return stats;
    }

    // --- (criarNovaVagaAutomatica) ---
    public void criarNovaVagaAutomatica(PrefixoAndar prefixo, TipoVaga tipo) {
        String prefixoBusca = prefixo.name() + "-";
        Vaga ultimaVaga = vagaRepository
                .findFirstByNumeroVagaStartingWithOrderByNumeroVagaDesc(prefixoBusca);
        int proximoNumero = 1;
        if (ultimaVaga != null) {
            String ultimoNumeroVaga = ultimaVaga.getNumeroVaga();
            String numeroAposHifen = ultimoNumeroVaga.substring(ultimoNumeroVaga.indexOf("-") + 1);
            int ultimoNumero = Integer.parseInt(numeroAposHifen);
            proximoNumero = ultimoNumero + 1;
        }
        String novoNumeroVaga = String.format("%s-%03d", prefixo.name(), proximoNumero);
        Vaga novaVaga = new Vaga();
        novaVaga.setNumeroVaga(novoNumeroVaga);
        novaVaga.setTipo(tipo);
        novaVaga.setAtivo(true);
        vagaRepository.save(novaVaga);
    }

    // --- (criarVagasEmMassa) ---
    public void criarVagasEmMassa(PrefixoAndar prefixo, int quantidade, TipoVaga tipo) {
        String prefixoBusca = prefixo.name() + "-";
        Vaga ultimaVaga = vagaRepository
                .findFirstByNumeroVagaStartingWithOrderByNumeroVagaDesc(prefixoBusca);
        int proximoNumero = 1;
        if (ultimaVaga != null) {
            String ultimoNumeroVaga = ultimaVaga.getNumeroVaga();
            String numeroAposHifen = ultimoNumeroVaga.substring(ultimoNumeroVaga.indexOf("-") + 1);
            int ultimoNumero = Integer.parseInt(numeroAposHifen);
            proximoNumero = ultimoNumero + 1;
        }
        List<Vaga> vagasParaSalvar = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            int numeroDaVez = proximoNumero + i;
            String novoNumeroVaga = String.format("%s-%03d", prefixo.name(), numeroDaVez);
            Vaga novaVaga = new Vaga();
            novaVaga.setNumeroVaga(novoNumeroVaga);
            novaVaga.setTipo(tipo);
            novaVaga.setAtivo(true);
            vagasParaSalvar.add(novaVaga);
        }
        vagaRepository.saveAll(vagasParaSalvar);
    }

    // --- (toggleAtivoStatus) ---
    public boolean toggleAtivoStatus(Long vagaId) {
        Optional<Vaga> vagaOpt = vagaRepository.findById(vagaId);
        if (vagaOpt.isEmpty()) {
            return false;
        }
        Vaga vaga = vagaOpt.get();
        if (vaga.isAtivo()) {
            Ticket ticketAberto = ticketRepository
                    .findByVagaIdAndStatus(vagaId, TicketStatus.ABERTO);
            if (ticketAberto != null) {
                return false;
            }
        }
        vaga.setAtivo(!vaga.isAtivo());
        vagaRepository.save(vaga);
        return true;
    }

    // --- (getVagasDisponiveis) ---
    public List<Vaga> getVagasDisponiveis() {
        Set<Long> idsVagasEmUso = ticketRepository.findByStatus(TicketStatus.ABERTO)
                .stream()
                .map(ticket -> ticket.getVaga().getId())
                .collect(Collectors.toSet());
        List<Vaga> vagasAtivas = vagaRepository.findByAtivoOrderByNumeroVagaAsc(true);
        List<Vaga> vagasDisponiveis = vagasAtivas.stream()
                .filter(vaga -> !idsVagasEmUso.contains(vaga.getId()))
                .collect(Collectors.toList());
        return vagasDisponiveis;
    }

    // --- (atualizarTipoVaga) ---
    public void atualizarTipoVaga(Long vagaId, TipoVaga novoTipo) {
        Optional<Vaga> vagaOpt = vagaRepository.findById(vagaId);
        if (vagaOpt.isEmpty()) {
            return;
        }
        Vaga vaga = vagaOpt.get();
        vaga.setTipo(novoTipo);
        vagaRepository.save(vaga);
    }
}
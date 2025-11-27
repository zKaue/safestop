package com.safestop.dto;

import com.safestop.entity.Ticket;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashboardStatsDTO {

    // "de X vagas"
    private long totalVagas;

    // "vagas livres"
    private long vagasLivres;

    // "vaga ocupadas"
    private long vagasOcupadas;

    // "ocupaçao (em porcentagem)"
    private double percentualOcupacao;

    // "lista com os carros que estao estacionado"
    private List<Ticket> ticketsAbertos;

    // === CAMPO ATUALIZADO ===
    private Double faturamentoDiario;

}
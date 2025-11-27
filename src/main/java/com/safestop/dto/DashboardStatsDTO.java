package com.safestop.dto;

import com.safestop.entity.Ticket;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashboardStatsDTO {

    private long totalVagas;
    private long vagasLivres;
    private long vagasOcupadas;
    private double percentualOcupacao;
    private List<Ticket> ticketsAbertos;
    private Double faturamentoDiario;

}
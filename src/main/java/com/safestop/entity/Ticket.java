package com.safestop.entity;

import com.safestop.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Representa a sessão de estacionamento.
 * Vincula um veículo a uma vaga, registrando horários de entrada/saída,
 * valor calculado e o status atual (Aberto/Fechado).
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_veiculo", nullable = false)
    private Veiculo veiculo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_vaga", nullable = false)
    private Vaga vaga;

    @Column(nullable = false)
    private LocalDateTime horarioEntrada;

    @Column
    private LocalDateTime horarioSaida;

    @Column
    private Double valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TicketStatus status;

}
package com.safestop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Singleton de banco de dados para parâmetros globais do sistema.
 * Armazena regras de negócio como tolerância e valor da hora.
 */
@Entity
@Getter
@Setter
public class Configuracao {

    @Id
    private Long id = 1L;

    @Column(nullable = false)
    private int minutosTolerancia;

    @Column(nullable = false)
    private double valorPorHora;
}
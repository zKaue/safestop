package com.safestop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Configuracao {

    @Id
    private Long id = 1L; // Sempre será ID 1

    @Column(nullable = false)
    private int minutosTolerancia; // Ex: 15

    @Column(nullable = false)
    private double valorPorHora; // Ex: 7.50
}
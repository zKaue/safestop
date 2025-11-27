package com.safestop.entity;

import com.safestop.enums.TipoVaga;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor 
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numeroVaga;

    @Enumerated(EnumType.STRING)
    private TipoVaga tipo;

    @Column(nullable = false)
    private boolean ativo = true;

}

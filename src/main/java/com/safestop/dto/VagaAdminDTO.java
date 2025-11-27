package com.safestop.dto;

import com.safestop.entity.Vaga;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Um DTO (pacote) que agrupa a Vaga
 * com a informação se ela está em uso ou não.
 */
@Getter
@Setter
@AllArgsConstructor
public class VagaAdminDTO {

    private Vaga vaga;
    private boolean emUso;
}
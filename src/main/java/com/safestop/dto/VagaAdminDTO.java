package com.safestop.dto;

import com.safestop.entity.Vaga;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class VagaAdminDTO {

    private Vaga vaga;
    private boolean emUso;
}
package com.safestop.repository;

import com.safestop.entity.Vaga;
import com.safestop.enums.TipoVaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VagaRepository extends JpaRepository<Vaga, Long> {

    // Conta o total de vagas ativas (para o cálculo de 'vagas livres')
    long countByAtivo(boolean ativo);

    // Encontra uma vaga pelo número dela
    Vaga findByNumeroVaga(String numeroVaga);

    /**
     * Encontra a ÚLTIMA vaga (findFirst) que começa com um prefixo (StartingWith)
     * ordenando pelo numeroVaga em ordem DECRESCENTE (OrderBy...Desc).
     *
     * Ex: Se tiver "A-01", "A-09", "A-02", ele vai retornar a "A-09".
     */
    Vaga findFirstByNumeroVagaStartingWithOrderByNumeroVagaDesc(String prefixo);

    // Busca por Tipo (COMUM, PCD, etc.) e ordena
    List<Vaga> findByTipoOrderByNumeroVagaAsc(TipoVaga tipo);

    // Busca por Ativo (true/false) e ordena
    List<Vaga> findByAtivoOrderByNumeroVagaAsc(boolean ativo);

    // Busca por Prefixo (A, B, etc.) e ordena
    List<Vaga> findByNumeroVagaStartingWithOrderByNumeroVagaAsc(String prefixo);

}

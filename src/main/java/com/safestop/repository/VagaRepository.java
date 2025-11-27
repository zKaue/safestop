package com.safestop.repository;

import com.safestop.entity.Vaga;
import com.safestop.enums.TipoVaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VagaRepository extends JpaRepository<Vaga, Long> {

    long countByAtivo(boolean ativo);

    Vaga findByNumeroVaga(String numeroVaga);

    /**
     * Retorna a última vaga cadastrada de um determinado setor (prefixo),
     * ordenando de forma decrescente. Útil para descobrir o próximo número a ser gerado.
     */
    Vaga findFirstByNumeroVagaStartingWithOrderByNumeroVagaDesc(String prefixo);

    List<Vaga> findByTipoOrderByNumeroVagaAsc(TipoVaga tipo);

    List<Vaga> findByAtivoOrderByNumeroVagaAsc(boolean ativo);

    /**
     * Filtra vagas por setor (ex: 'A-') e ordena.
     */
    List<Vaga> findByNumeroVagaStartingWithOrderByNumeroVagaAsc(String prefixo);

}
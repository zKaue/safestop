package com.safestop.repository;

import com.safestop.entity.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracaoRepository extends JpaRepository<Configuracao, Long> {
    // O JpaRepository já tem o findById(1L) que precisamos
}
package com.safestop.repository;

import com.safestop.entity.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracaoRepository extends JpaRepository<Configuracao, Long> {
    // Gerencia a entidade de configuração global (Singleton ID 1).
}
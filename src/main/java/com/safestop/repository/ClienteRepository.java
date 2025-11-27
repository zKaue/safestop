package com.safestop.repository;

import com.safestop.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Busca cliente pelo telefone para reaproveitamento de cadastro.
     */
    Optional<Cliente> findByTelefone(String telefone);

}
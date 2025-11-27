package com.safestop.repository;

import com.safestop.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca usuário para autenticação via Login.
     */
    Optional<Usuario> findByEmail(String email);

}
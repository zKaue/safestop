package com.safestop.repository;

import com.safestop.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // O Spring Security VAI precisar disso.
    // O 'Optional' é uma boa prática, pois o usuário pode não existir.
    Optional<Usuario> findByEmail(String email);

}

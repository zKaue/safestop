package com.safestop.service;

import com.safestop.entity.Usuario;
import com.safestop.repository.UsuarioRepository;
import com.safestop.util.FormatadorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
// === IMPORT ADICIONADO ===
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Método de login do Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário ou senha inválido"));

        // === LÓGICA DE LOGIN ATUALIZADA ===
        if (!usuario.isAtivo()) {
            // Lança a exceção específica para "Conta Desativada"
            throw new DisabledException("Sua conta está desativada. Fale com um administrador.");
        }
        // === FIM DA MUDANÇA ===

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(usuario.isAdmin() ? "ADMIN" : "USER")
                .build();
    }

    // ... (O resto dos seus métodos 'toggleAtivoStatus', 'atualizarFuncionario' e 'salvarNovoFuncionario' continuam aqui, perfeitos) ...
    public boolean toggleAtivoStatus(Long usuarioId, String emailDoAdminLogado) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isEmpty()) {
            return false;
        }

        Usuario usuarioParaMudar = usuarioOpt.get();

        if (usuarioParaMudar.getEmail().equals(emailDoAdminLogado)) {
            return false;
        }

        usuarioParaMudar.setAtivo(!usuarioParaMudar.isAtivo());
        usuarioRepository.save(usuarioParaMudar);
        return true;
    }

    public void atualizarFuncionario(Usuario usuarioDoForm) {

        Usuario usuarioDoBanco = usuarioRepository.findById(usuarioDoForm.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para atualizar"));

        String emailAntigo = usuarioDoBanco.getEmail();
        String emailNovo = usuarioDoForm.getEmail();

        if (!emailAntigo.equals(emailNovo)) {
            Optional<Usuario> outroUsuario = usuarioRepository.findByEmail(emailNovo);
            if (outroUsuario.isPresent()) {
                throw new DataIntegrityViolationException("Email duplicado");
            }
        }

        usuarioDoBanco.setNome(usuarioDoForm.getNome());
        usuarioDoBanco.setEmail(emailNovo);

        String telefoneFormatado = FormatadorUtils.formatarTelefone(usuarioDoForm.getTelefone());
        usuarioDoBanco.setTelefone(telefoneFormatado);

        usuarioDoBanco.setAdmin(usuarioDoForm.isAdmin());

        usuarioRepository.save(usuarioDoBanco);
    }

    public void salvarNovoFuncionario(Usuario novoUsuario) {

        Optional<Usuario> outroUsuario = usuarioRepository.findByEmail(novoUsuario.getEmail());
        if (outroUsuario.isPresent()) {
            throw new DataIntegrityViolationException("Email duplicado");
        }

        String telefoneFormatado = FormatadorUtils.formatarTelefone(novoUsuario.getTelefone());
        novoUsuario.setTelefone(telefoneFormatado);

        novoUsuario.setAtivo(false); // Corrigido: novos usuários começam inativos

        usuarioRepository.save(novoUsuario);
    }
}
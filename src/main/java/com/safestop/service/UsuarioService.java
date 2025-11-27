package com.safestop.service;

import com.safestop.entity.Usuario;
import com.safestop.repository.UsuarioRepository;
import com.safestop.util.FormatadorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
     * Método obrigatório do Spring Security.
     * Busca o usuário no DB e converte para o objeto 'User' do Spring.
     * Inclui verificação extra: se o usuário estiver inativo, bloqueia o login.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário ou senha inválido"));

        if (!usuario.isAtivo()) {
            throw new DisabledException("Sua conta está desativada. Fale com um administrador.");
        }

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .roles(usuario.isAdmin() ? "ADMIN" : "USER")
                .build();
    }

    /**
     * Ativa/Desativa um funcionário. Impede que o próprio admin se desative.
     */
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
        usuarioDoBanco.setTelefone(FormatadorUtils.formatarTelefone(usuarioDoForm.getTelefone()));
        usuarioDoBanco.setAdmin(usuarioDoForm.isAdmin());

        usuarioRepository.save(usuarioDoBanco);
    }

    public void salvarNovoFuncionario(Usuario novoUsuario) {
        Optional<Usuario> outroUsuario = usuarioRepository.findByEmail(novoUsuario.getEmail());
        if (outroUsuario.isPresent()) {
            throw new DataIntegrityViolationException("Email duplicado");
        }

        novoUsuario.setTelefone(FormatadorUtils.formatarTelefone(novoUsuario.getTelefone()));
        novoUsuario.setAtivo(false); // Novos cadastros aguardam aprovação

        usuarioRepository.save(novoUsuario);
    }
}
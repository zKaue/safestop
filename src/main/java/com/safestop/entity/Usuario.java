package com.safestop.entity;

import com.safestop.util.FormatadorUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa os operadores do sistema (Administradores e Funcionários).
 * Contém credenciais de acesso, flag de permissão (isAdmin) e utilitários de formatação.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String nome;

    @Column(length = 150, nullable = false, unique = true)
    private String email;

    @Column(length = 15, nullable = false)
    private String telefone;

    @Column(length = 100, nullable = false)
    private String senha;

    @Column(nullable = false)
    private boolean isAdmin;

    @Column(nullable = false)
    private boolean ativo = true;

    /**
     * Retorna o telefone formatado para exibição (View).
     */
    @Transient
    public String getTelefoneFormatado() {
        return FormatadorUtils.formatarTelefone(this.telefone);
    }

    /**
     * Retorna apenas os dígitos do telefone para formulários de edição.
     */
    @Transient
    public String getTelefoneSemFormatacao() {
        if (this.telefone == null) {
            return "";
        }
        return this.telefone.replaceAll("\\D", "");
    }
}
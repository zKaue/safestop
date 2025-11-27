package com.safestop.entity;

import com.safestop.util.FormatadorUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    // --- ESTE CAMPO ESTAVA FALTANDO ---
    @Column(nullable = false)
    private boolean ativo = true;

    /**
     * Getter para o HTML (mostra formatado)
     */
    @Transient
    public String getTelefoneFormatado() {
        return FormatadorUtils.formatarTelefone(this.telefone);
    }

    /**
     * Getter para o formulário de EDIÇÃO (mostra só os dígitos)
     */
    @Transient
    public String getTelefoneSemFormatacao() {
        if (this.telefone == null) {
            return "";
        }
        // Remove tudo que não for dígito
        return this.telefone.replaceAll("\\D", "");
    }
}
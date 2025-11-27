package com.safestop.dto;

import com.safestop.entity.Cliente;
import com.safestop.entity.Veiculo;
import lombok.Getter;

@Getter
public class VeiculoClienteDTO {

    private String placa;
    private String modelo;
    private String nomeCliente;
    private String telefoneCliente;

    public VeiculoClienteDTO(Veiculo veiculo) {
        this.placa = veiculo.getPlaca();
        this.modelo = veiculo.getModelo();

        Cliente cliente = veiculo.getCliente();
        if (cliente != null) {
            this.nomeCliente = cliente.getNome();
            this.telefoneCliente = cliente.getTelefone();
        }
    }
}
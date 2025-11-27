package com.safestop.service;

import com.safestop.entity.Cliente;
import com.safestop.entity.Veiculo;
import com.safestop.repository.ClienteRepository;
import com.safestop.repository.VeiculoRepository;
import com.safestop.util.FormatadorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    /**
     * Busca inteligente: Tenta encontrar o cliente pelo telefone (formatado).
     * Se não existir, cria o cliente. Depois, vincula o veículo.
     */
    @Transactional
    public Veiculo findOrCreateClienteComVeiculo(
            String placa, String modelo, String nomeCliente, String telefoneCliente) {

        String telefoneFormatado = FormatadorUtils.formatarTelefone(telefoneCliente);

        Cliente cliente = clienteRepository.findByTelefone(telefoneFormatado)
                .orElseGet(() -> {
                    Cliente novoCliente = new Cliente();
                    novoCliente.setNome(nomeCliente);
                    novoCliente.setTelefone(telefoneFormatado);
                    return clienteRepository.save(novoCliente);
                });

        Veiculo veiculo = veiculoRepository.findByPlaca(placa)
                .orElseGet(() -> {
                    Veiculo novoVeiculo = new Veiculo();
                    novoVeiculo.setPlaca(placa);
                    novoVeiculo.setModelo(modelo);
                    novoVeiculo.setCliente(cliente);
                    return veiculoRepository.save(novoVeiculo);
                });

        return veiculo;
    }
}
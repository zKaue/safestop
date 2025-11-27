package com.safestop.service;

import com.safestop.entity.Cliente;
import com.safestop.entity.Veiculo;
import com.safestop.repository.ClienteRepository;
import com.safestop.repository.VeiculoRepository;
import com.safestop.util.FormatadorUtils; // <-- 1. IMPORTE A NOVA CLASSE
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    /**
     * === MÉTODO ATUALIZADO (sem marca, cor, ano) ===
     */
    @Transactional
    public Veiculo findOrCreateClienteComVeiculo(
            String placa, String modelo, String nomeCliente, String telefoneCliente) {

        // 2. === A MUDANÇA ESTÁ AQUI ===
        //    Agora ele chama a nossa "ferramenta"
        String telefoneFormatado = FormatadorUtils.formatarTelefone(telefoneCliente);

        // 3. Lógica do Cliente (agora busca pelo telefone FORMATADO)
        Cliente cliente = clienteRepository.findByTelefone(telefoneFormatado)
                .orElseGet(() -> {
                    // Cliente não existe, cria um novo
                    Cliente novoCliente = new Cliente();
                    novoCliente.setNome(nomeCliente);
                    novoCliente.setTelefone(telefoneFormatado); // Salva formatado
                    return clienteRepository.save(novoCliente);
                });

        // 4. Lógica do Veículo (idêntica a antes)
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
package com.safestop.controller;

import com.safestop.dto.VeiculoClienteDTO;
import com.safestop.entity.Ticket;
import com.safestop.entity.Veiculo;
import com.safestop.enums.TicketStatus;
import com.safestop.repository.TicketRepository;
import com.safestop.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class ApiVeiculoController {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping("/api/veiculo/{placa}")
    public ResponseEntity<?> getVeiculoPorPlaca(@PathVariable String placa) {

        Optional<Ticket> ticketAberto = ticketRepository
                .findByVeiculoPlacaAndStatus(placa, TicketStatus.ABERTO);

        if (ticketAberto.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Este veículo já está estacionado!");
        }

        Optional<Veiculo> veiculoOpt = veiculoRepository.findByPlaca(placa);

        if (veiculoOpt.isPresent()) {
            // O DTO agora só tem os campos que queremos
            VeiculoClienteDTO dto = new VeiculoClienteDTO(veiculoOpt.get());
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
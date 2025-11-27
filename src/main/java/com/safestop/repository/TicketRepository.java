package com.safestop.repository;

import com.safestop.entity.Ticket;
import com.safestop.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // (Seus métodos antigos, 'countByStatus', 'findByVagaIdAndStatus', etc...)
    long countByStatus(TicketStatus status);
    Ticket findByVagaIdAndStatus(Long vagaId, TicketStatus status);
    List<Ticket> findByVeiculoPlaca(String placa);
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByStatusAndVeiculoPlacaStartingWithIgnoreCase(TicketStatus status, String placa);
    Optional<Ticket> findByVeiculoPlacaAndStatus(String placa, TicketStatus status);

    @Query("SELECT t FROM Ticket t JOIN t.veiculo v JOIN v.cliente c " +
            "WHERE t.status = :status AND " +
            "(LOWER(v.placa) LIKE LOWER(CONCAT(:termo, '%')) OR " +
            " LOWER(c.nome) LIKE LOWER(CONCAT(:termo, '%')))")
    List<Ticket> findByStatusAndPlacaOuClienteNome(
            @Param("status") TicketStatus status,
            @Param("termo") String termo);


    /**
     * Soma o valor de todos os tickets FECHADOS entre um horário de início e fim.
     */
    @Query("SELECT SUM(t.valor) FROM Ticket t " +
            "WHERE t.status = 'FECHADO' AND t.horarioSaida BETWEEN :inicio AND :fim")
    Double sumValorFechadoEntreDatas(@Param("inicio") LocalDateTime inicio,
                                     @Param("fim") LocalDateTime fim);

    /**
     * === NOVO MÉTODO (Tickets Fechados) ===
     * Conta quantos tickets foram FECHADOS entre as datas.
     */
    Long countByStatusAndHorarioSaidaBetween(TicketStatus status, LocalDateTime inicio, LocalDateTime fim);

    /**
     * === NOVO MÉTODO (Novas Entradas) ===
     * Conta quantos tickets foram ABERTOS (pela data de entrada) entre as datas.
     */
    Long countByHorarioEntradaBetween(LocalDateTime inicio, LocalDateTime fim);
}
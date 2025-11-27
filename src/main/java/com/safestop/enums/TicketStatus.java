package com.safestop.enums;

/**
 * Define o estado atual de um Ticket.
 * ABERTO: Veículo está no pátio (cobrança correndo).
 * FECHADO: Veículo já saiu e o pagamento foi processado.
 */
public enum TicketStatus {
    ABERTO,
    FECHADO
}
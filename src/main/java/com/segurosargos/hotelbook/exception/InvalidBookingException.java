package com.segurosargos.hotelbook.exception;

/*
 * Excepción de dominio para indicar que los datos de una pre-reserva o reserva son inválidos
 * según las reglas de negocio.
 */
public class InvalidBookingException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidBookingException(String message) {
        super(message);
    }
}

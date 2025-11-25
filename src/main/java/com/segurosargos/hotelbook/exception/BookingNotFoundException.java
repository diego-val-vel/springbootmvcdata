package com.segurosargos.hotelbook.exception;

/*
 * Excepción de dominio para indicar que una pre-reserva o reserva no fue encontrada.
 */
public class BookingNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BookingNotFoundException(String message) {
        super(message);
    }
}

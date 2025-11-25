package com.segurosargos.hotelbook.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO genérico para representar errores en las respuestas.
 * Se usa tanto para errores de validación como para errores de negocio.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {

    /*
     * Momento en que se generó el error.
     */
    @Builder.Default
    private Instant timestamp = Instant.now();

    /*
     * Código HTTP numérico asociado al error.
     */
    private int status;

    /*
     * Descripción corta del tipo de error (por ejemplo, BAD_REQUEST, NOT_FOUND).
     */
    private String error;

    /*
     * Mensaje de error principal.
     */
    private String message;

    /*
     * Path del request que originó el error.
     */
    private String path;

    /*
     * Errores de campo individuales, cuando aplique (por ejemplo, Bean Validation).
     */
    @Builder.Default
    private List<FieldErrorEntry> fieldErrors = new ArrayList<>();

    /*
     * Representa un error concreto de un campo de entrada.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldErrorEntry {

        /*
         * Nombre del campo que tuvo el problema.
         */
        private String field;

        /*
         * Mensaje asociado al error de ese campo.
         */
        private String message;
    }
}

package alex.probando.usuarios.infra.adapter.in.web;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ApiExceptionHandler {

    // 1. Manejo de errores de base de datos (Ej: Email duplicado)
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            WebRequest request) {
        
        return new ErrorResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                "Ya existe un usuario con ese email",
                getPath(request));
    }

    // 2. Manejo de validaciones del DTO (Ej: @NotNull, @Min)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(
            MethodArgumentNotValidException exception, 
            WebRequest request) {
        
        // Extrae todos los errores y los une en un solo texto
        String errorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                errorMessage,
                getPath(request));
    }

    // 3. Manejo de validaciones del Dominio (Ej: throw new IllegalArgumentException(...))
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(
            IllegalArgumentException exception,
            WebRequest request) {

        return new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                exception.getMessage(), // Extrae el mensaje directo de tu record Usuario
                getPath(request));
    }

    // --- Método auxiliar para no repetir código ---
    private String getPath(WebRequest request) {
        return request instanceof ServletWebRequest servletWebRequest
                ? servletWebRequest.getRequest().getRequestURI()
                : "";
    }

    // --- Estructura de la respuesta JSON ---
    public record ErrorResponse(
            Instant timestamp,
            int status,
            String error,
            String message,
            String path) {
    }
}
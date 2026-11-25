package ar.edu.unsam.ddso.tp.model.exceptions;

/**
 * Base class for business rule violations (domain invariants). Each concrete rule gets its own
 * subclass with its own message. Translated to HTTP 400 by the GlobalExceptionHandler.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}

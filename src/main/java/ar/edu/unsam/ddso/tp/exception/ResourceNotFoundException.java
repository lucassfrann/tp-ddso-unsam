package ar.edu.unsam.ddso.tp.exception;

/** Thrown by a service when the requested resource does not exist. Translated to HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

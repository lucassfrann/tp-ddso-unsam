package ar.edu.unsam.ddso.tp.exception;

import ar.edu.unsam.ddso.tp.model.exceptions.DomainException;
import ar.edu.unsam.ddso.tp.model.exceptions.TagInUseException;
import ar.edu.unsam.ddso.tp.model.exceptions.TagNameAlreadyExistsException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates exceptions to HTTP responses with a body like {"error": "message"}.
 *
 * <p>409 (conflict) covers a tag that is in use or whose name already exists. Those handlers are
 * more specific than the DomainException one (400), and Spring picks the closest in the hierarchy.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, String>> handleDomain(DomainException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({TagInUseException.class, TagNameAlreadyExistsException.class})
    public ResponseEntity<Map<String, String>> handleConflict(DomainException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ResponseEntity<Map<String, String>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}

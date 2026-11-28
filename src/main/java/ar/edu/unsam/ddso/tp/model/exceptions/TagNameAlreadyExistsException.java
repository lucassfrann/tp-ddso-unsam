package ar.edu.unsam.ddso.tp.model.exceptions;

/** Tag names are unique (case-insensitive). Translated to HTTP 409. */
public class TagNameAlreadyExistsException extends DomainException {

    public TagNameAlreadyExistsException(String name) {
        super("A tag named '" + name + "' already exists");
    }
}

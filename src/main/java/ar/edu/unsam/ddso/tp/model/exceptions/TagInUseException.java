package ar.edu.unsam.ddso.tp.model.exceptions;

/** A tag that is used by at least one digitalization cannot be deleted. Translated to HTTP 409. */
public class TagInUseException extends DomainException {

    public TagInUseException() {
        super("The tag is in use and cannot be deleted");
    }
}

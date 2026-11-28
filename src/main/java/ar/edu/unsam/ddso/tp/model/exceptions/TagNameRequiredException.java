package ar.edu.unsam.ddso.tp.model.exceptions;

/** A tag cannot exist without a name. */
public class TagNameRequiredException extends DomainException {

    public TagNameRequiredException() {
        super("The tag name is required");
    }
}

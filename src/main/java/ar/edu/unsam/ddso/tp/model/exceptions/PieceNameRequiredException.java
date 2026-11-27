package ar.edu.unsam.ddso.tp.model.exceptions;

/** A piece cannot exist without a name. */
public class PieceNameRequiredException extends DomainException {

    public PieceNameRequiredException() {
        super("The piece name is required");
    }
}

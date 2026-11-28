package ar.edu.unsam.ddso.tp.model.exceptions;

/** A search date range cannot start after it ends. */
public class InvalidSearchDateRangeException extends DomainException {

    public InvalidSearchDateRangeException() {
        super("The search date range is invalid: 'from' must not be after 'to'");
    }
}

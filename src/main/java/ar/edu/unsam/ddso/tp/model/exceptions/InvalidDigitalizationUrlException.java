package ar.edu.unsam.ddso.tp.model.exceptions;

/** A digitalization needs a valid http or https URL. */
public class InvalidDigitalizationUrlException extends DomainException {

    public InvalidDigitalizationUrlException() {
        super("The digitalization URL must be a valid http or https URL");
    }
}

package ar.edu.unsam.ddso.tp.model.exceptions;

/** A digitalization cannot exist without a description. */
public class DigitalizationDescriptionRequiredException extends DomainException {

    public DigitalizationDescriptionRequiredException() {
        super("The digitalization description is required");
    }
}

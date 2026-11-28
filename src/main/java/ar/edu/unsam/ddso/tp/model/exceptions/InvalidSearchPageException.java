package ar.edu.unsam.ddso.tp.model.exceptions;

/** A search must ask for a valid page: page number from 0 and a page size within the limit. */
public class InvalidSearchPageException extends DomainException {

    public InvalidSearchPageException(int maxPageSize) {
        super("The page must be >= 0 and the size must be between 1 and " + maxPageSize);
    }
}

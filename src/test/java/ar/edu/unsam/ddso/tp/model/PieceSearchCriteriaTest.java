package ar.edu.unsam.ddso.tp.model;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.unsam.ddso.tp.model.exceptions.InvalidSearchDateRangeException;
import ar.edu.unsam.ddso.tp.model.exceptions.InvalidSearchPageException;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PieceSearchCriteriaTest {

    @Test
    void normalizesTextAndTagsToLowercase() {
        // When
        PieceSearchCriteria criteria =
                new PieceSearchCriteria(
                        "  Letter ", " HAND", null, null, Set.of(" Photo ", "FAMILY"), 0, 10);

        // Then
        assertEquals("letter", criteria.name());
        assertEquals("hand", criteria.description());
        assertEquals(Set.of("photo", "family"), criteria.tagNames());
    }

    @Test
    void treatsBlankTextAndMissingTagsAsNoFilter() {
        // When
        PieceSearchCriteria criteria = new PieceSearchCriteria(" ", "", null, null, null, 0, 10);

        // Then
        assertNull(criteria.name());
        assertNull(criteria.description());
        assertTrue(criteria.tagNames().isEmpty());
    }

    @Test
    void acceptsARangeWhereFromEqualsTo() {
        // Given
        LocalDate day = LocalDate.of(2026, 5, 1);

        // When
        PieceSearchCriteria criteria = new PieceSearchCriteria(null, null, day, day, null, 0, 10);

        // Then
        assertEquals(day, criteria.from());
        assertEquals(day, criteria.to());
    }

    @Test
    void rejectsAFromAfterTo() {
        // Given
        LocalDate from = LocalDate.of(2026, 5, 2);
        LocalDate to = LocalDate.of(2026, 5, 1);

        // When / Then
        assertThrows(
                InvalidSearchDateRangeException.class,
                () -> new PieceSearchCriteria(null, null, from, to, null, 0, 10));
    }

    @Test
    void rejectsANegativePage() {
        assertThrows(
                InvalidSearchPageException.class,
                () -> new PieceSearchCriteria(null, null, null, null, null, -1, 10));
    }

    @Test
    void rejectsASizeOutOfRange() {
        assertThrows(
                InvalidSearchPageException.class,
                () -> new PieceSearchCriteria(null, null, null, null, null, 0, 0));
        assertThrows(
                InvalidSearchPageException.class,
                () ->
                        new PieceSearchCriteria(
                                null,
                                null,
                                null,
                                null,
                                null,
                                0,
                                PieceSearchCriteria.MAX_PAGE_SIZE + 1));
    }
}

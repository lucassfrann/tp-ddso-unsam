package ar.edu.unsam.ddso.tp.model;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.unsam.ddso.tp.model.exceptions.DigitalizationDescriptionRequiredException;
import ar.edu.unsam.ddso.tp.model.exceptions.InvalidDigitalizationUrlException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DigitalizationTest {

    private Piece piece;

    @BeforeEach
    void setUp() {
        piece = Piece.create("Letter from 1920", "Handwritten letter");
    }

    @Test
    void newDigitalizationKeepsItsDataBelongsToThePieceAndHasNoTags() {
        // When
        Digitalization digitalization =
                Digitalization.create(piece, "https://archive.org/scan-1.png", "Front scan");

        // Then
        assertEquals("https://archive.org/scan-1.png", digitalization.getUrl());
        assertEquals("Front scan", digitalization.getDescription());
        assertSame(piece, digitalization.getPiece());
        assertTrue(digitalization.getTags().isEmpty());
    }

    @Test
    void urlIsStoredWithoutSurroundingSpaces() {
        // When
        Digitalization digitalization =
                Digitalization.create(piece, "  https://archive.org/scan-1.png  ", "Front scan");

        // Then
        assertEquals("https://archive.org/scan-1.png", digitalization.getUrl());
    }

    @Test
    void pieceRegistersTheDigitalizationItAdds() {
        // When
        Digitalization digitalization =
                piece.addDigitalization("http://archive.org/scan-1.png", "Front scan");

        // Then
        assertEquals(1, piece.getDigitalizations().size());
        assertSame(digitalization, piece.getDigitalizations().get(0));
        assertSame(piece, digitalization.getPiece());
    }

    @Test
    void pieceCanHaveSeveralDigitalizations() {
        // When
        piece.addDigitalization("https://archive.org/front.png", "Front");
        piece.addDigitalization("https://archive.org/back.png", "Back");

        // Then
        assertEquals(2, piece.getDigitalizations().size());
    }

    @Test
    void acceptsHttpAndHttpsUrls() {
        assertDoesNotThrow(() -> piece.addDigitalization("http://example.org/a", "desc"));
        assertDoesNotThrow(() -> piece.addDigitalization("HTTPS://example.org/a?x=1", "desc"));
    }

    @Test
    void urlMustBeAValidHttpOrHttpsUrl() {
        assertUrlIsRejected(null);
        assertUrlIsRejected("");
        assertUrlIsRejected("  ");
        assertUrlIsRejected("not a url");
        assertUrlIsRejected("/relative/path.png");
        assertUrlIsRejected("ftp://example.org/file");
        assertUrlIsRejected("http://");
    }

    @Test
    void descriptionIsRequired() {
        assertThrows(
                DigitalizationDescriptionRequiredException.class,
                () -> piece.addDigitalization("https://example.org/a", null));
        assertThrows(
                DigitalizationDescriptionRequiredException.class,
                () -> piece.addDigitalization("https://example.org/a", ""));
        assertThrows(
                DigitalizationDescriptionRequiredException.class,
                () -> piece.addDigitalization("https://example.org/a", "   "));
    }

    @Test
    void invalidDigitalizationIsNotAddedToThePiece() {
        // When
        assertUrlIsRejected("x");

        // Then
        assertTrue(piece.getDigitalizations().isEmpty());
    }

    private void assertUrlIsRejected(String url) {
        assertThrows(
                InvalidDigitalizationUrlException.class,
                () -> piece.addDigitalization(url, "desc"));
    }
}

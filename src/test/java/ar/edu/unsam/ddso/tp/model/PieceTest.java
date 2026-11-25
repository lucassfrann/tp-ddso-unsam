package ar.edu.unsam.ddso.tp.model;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.unsam.ddso.tp.model.exceptions.PieceNameRequiredException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PieceTest {

    @Test
    void newPieceKeepsItsDataAndGetsTodayAsLoadDate() {
        // When
        Piece piece = Piece.create("Letter from 1920", "Handwritten letter");

        // Then
        assertEquals("Letter from 1920", piece.getName());
        assertEquals("Handwritten letter", piece.getDescription());
        assertEquals(LocalDate.now(), piece.getLoadDate());
        assertTrue(piece.getDigitalizations().isEmpty());
    }

    @Test
    void descriptionIsOptional() {
        // When
        Piece piece = Piece.create("Map", null);

        // Then
        assertNull(piece.getDescription());
    }

    @Test
    void nameIsRequired() {
        assertThrows(PieceNameRequiredException.class, () -> Piece.create(null, "desc"));
        assertThrows(PieceNameRequiredException.class, () -> Piece.create("", "desc"));
        assertThrows(PieceNameRequiredException.class, () -> Piece.create("   ", "desc"));
    }
}

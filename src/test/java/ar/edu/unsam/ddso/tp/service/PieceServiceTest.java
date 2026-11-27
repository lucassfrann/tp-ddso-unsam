package ar.edu.unsam.ddso.tp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ar.edu.unsam.ddso.tp.dto.PieceCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.model.exceptions.PieceNameRequiredException;
import ar.edu.unsam.ddso.tp.repository.PieceRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PieceServiceTest {

    @Mock private PieceRepository pieceRepository;

    private PieceService pieceService;

    @BeforeEach
    void setUp() {
        pieceService = new PieceService(pieceRepository);
    }

    @Test
    void createsAValidPiece() {
        // Given
        PieceCreateRequestDTO request = new PieceCreateRequestDTO("Letter", "Handwritten letter");
        when(pieceRepository.save(any(Piece.class)))
                .thenAnswer(
                        invocation -> {
                            Piece saved = invocation.getArgument(0);
                            saved.setId(1L);
                            return saved;
                        });

        // When
        PieceResponseDTO response = pieceService.create(request);

        // Then
        assertEquals(1L, response.id());
        assertEquals("Letter", response.name());
        assertEquals("Handwritten letter", response.description());
        assertEquals(LocalDate.now(), response.loadDate());
        verify(pieceRepository).save(any(Piece.class));
    }

    @Test
    void rejectsAPieceWithoutNameAndDoesNotSaveIt() {
        // Given
        PieceCreateRequestDTO request = new PieceCreateRequestDTO("  ", "No name");

        // When / Then
        assertThrows(PieceNameRequiredException.class, () -> pieceService.create(request));
        verify(pieceRepository, never()).save(any());
    }
}

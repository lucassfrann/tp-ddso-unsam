package ar.edu.unsam.ddso.tp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ar.edu.unsam.ddso.tp.dto.DigitalizationCreateRequestDTO;
import ar.edu.unsam.ddso.tp.dto.DigitalizationResponseDTO;
import ar.edu.unsam.ddso.tp.exception.ResourceNotFoundException;
import ar.edu.unsam.ddso.tp.model.Digitalization;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.model.exceptions.DigitalizationDescriptionRequiredException;
import ar.edu.unsam.ddso.tp.model.exceptions.InvalidDigitalizationUrlException;
import ar.edu.unsam.ddso.tp.repository.DigitalizationRepository;
import ar.edu.unsam.ddso.tp.repository.PieceRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DigitalizationServiceTest {

    @Mock private PieceRepository pieceRepository;
    @Mock private DigitalizationRepository digitalizationRepository;

    private DigitalizationService digitalizationService;
    private Piece piece;

    @BeforeEach
    void setUp() {
        digitalizationService =
                new DigitalizationService(pieceRepository, digitalizationRepository);
        piece = Piece.create("Letter", "Handwritten letter");
        piece.setId(1L);
    }

    @Test
    void addsADigitalizationToAnExistingPiece() {
        // Given
        DigitalizationCreateRequestDTO request =
                new DigitalizationCreateRequestDTO("https://archive.org/scan-1.png", "Front scan");
        when(pieceRepository.findById(1L)).thenReturn(Optional.of(piece));
        when(digitalizationRepository.save(any(Digitalization.class)))
                .thenAnswer(
                        invocation -> {
                            Digitalization saved = invocation.getArgument(0);
                            saved.setId(10L);
                            return saved;
                        });

        // When
        DigitalizationResponseDTO response = digitalizationService.create(1L, request);

        // Then
        assertEquals(10L, response.id());
        assertEquals(1L, response.pieceId());
        assertEquals("https://archive.org/scan-1.png", response.url());
        assertEquals("Front scan", response.description());
        assertTrue(response.tags().isEmpty());
        assertEquals(1, piece.getDigitalizations().size());
        verify(digitalizationRepository).save(any(Digitalization.class));
    }

    @Test
    void failsWithNotFoundWhenThePieceDoesNotExist() {
        // Given
        DigitalizationCreateRequestDTO request =
                new DigitalizationCreateRequestDTO("https://archive.org/scan-1.png", "Front scan");
        when(pieceRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(
                ResourceNotFoundException.class, () -> digitalizationService.create(99L, request));
        verify(digitalizationRepository, never()).save(any());
    }

    @Test
    void rejectsAnInvalidUrlAndDoesNotSaveIt() {
        // Given
        DigitalizationCreateRequestDTO request =
                new DigitalizationCreateRequestDTO("not a url", "Front scan");
        when(pieceRepository.findById(1L)).thenReturn(Optional.of(piece));

        // When / Then
        assertThrows(
                InvalidDigitalizationUrlException.class,
                () -> digitalizationService.create(1L, request));
        verify(digitalizationRepository, never()).save(any());
        assertTrue(piece.getDigitalizations().isEmpty());
    }

    @Test
    void rejectsABlankDescriptionAndDoesNotSaveIt() {
        // Given
        DigitalizationCreateRequestDTO request =
                new DigitalizationCreateRequestDTO("https://archive.org/scan-1.png", "  ");
        when(pieceRepository.findById(1L)).thenReturn(Optional.of(piece));

        // When / Then
        assertThrows(
                DigitalizationDescriptionRequiredException.class,
                () -> digitalizationService.create(1L, request));
        verify(digitalizationRepository, never()).save(any());
        assertTrue(piece.getDigitalizations().isEmpty());
    }
}

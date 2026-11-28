package ar.edu.unsam.ddso.tp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.dto.SearchRequestDTO;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.model.PieceSearchCriteria;
import ar.edu.unsam.ddso.tp.model.exceptions.InvalidSearchDateRangeException;
import ar.edu.unsam.ddso.tp.model.exceptions.InvalidSearchPageException;
import ar.edu.unsam.ddso.tp.repository.PieceRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock private PieceRepository pieceRepository;

    private SearchService searchService;

    @BeforeEach
    void setUp() {
        searchService = new SearchService(pieceRepository);
    }

    @Test
    void returnsTheMatchingPiecesAsDTOs() {
        // Given
        Piece letter = pieceWithId(1L, "Letter", "Handwritten letter");
        when(pieceRepository.search(any(PieceSearchCriteria.class))).thenReturn(List.of(letter));
        SearchRequestDTO request = new SearchRequestDTO("letter", null, null, null, null, 0, 10);

        // When
        List<PieceResponseDTO> result = searchService.search(request);

        // Then
        assertEquals(1, result.size());
        PieceResponseDTO found = result.get(0);
        assertEquals(1L, found.id());
        assertEquals("Letter", found.name());
        assertEquals("Handwritten letter", found.description());
        assertEquals(LocalDate.now(), found.loadDate());
    }

    @Test
    void returnsAnEmptyListWhenNothingMatches() {
        // Given
        when(pieceRepository.search(any(PieceSearchCriteria.class))).thenReturn(List.of());
        SearchRequestDTO request = new SearchRequestDTO("nothing", null, null, null, null, 0, 10);

        // When
        List<PieceResponseDTO> result = searchService.search(request);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void passesEveryFilterToTheRepositoryNormalized() {
        // Given
        when(pieceRepository.search(any(PieceSearchCriteria.class))).thenReturn(List.of());
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 12, 31);
        SearchRequestDTO request =
                new SearchRequestDTO(
                        "  Letter ", "HANDWRITTEN", from, to, List.of(" Photo", "Family"), 0, 10);

        // When
        searchService.search(request);

        // Then
        PieceSearchCriteria criteria = capturedCriteria();
        assertEquals("letter", criteria.name());
        assertEquals("handwritten", criteria.description());
        assertEquals(from, criteria.from());
        assertEquals(to, criteria.to());
        assertEquals(Set.of("photo", "family"), criteria.tagNames());
    }

    @Test
    void ignoresBlankFiltersAndRepeatedTags() {
        // Given
        when(pieceRepository.search(any(PieceSearchCriteria.class))).thenReturn(List.of());
        SearchRequestDTO request =
                new SearchRequestDTO(
                        "   ", "", null, null, List.of("photo", "PHOTO", "  "), 0, 10);

        // When
        searchService.search(request);

        // Then
        PieceSearchCriteria criteria = capturedCriteria();
        assertNull(criteria.name());
        assertNull(criteria.description());
        assertEquals(Set.of("photo"), criteria.tagNames());
    }

    @Test
    void asksTheRepositoryForTheRequestedPage() {
        // Given
        when(pieceRepository.search(any(PieceSearchCriteria.class))).thenReturn(List.of());
        SearchRequestDTO request = new SearchRequestDTO(null, null, null, null, null, 2, 5);

        // When
        searchService.search(request);

        // Then
        PieceSearchCriteria criteria = capturedCriteria();
        assertEquals(2, criteria.page());
        assertEquals(5, criteria.size());
    }

    @Test
    void rejectsAnInvertedDateRangeAndDoesNotQuery() {
        // Given
        SearchRequestDTO request =
                new SearchRequestDTO(
                        null,
                        null,
                        LocalDate.of(2026, 12, 31),
                        LocalDate.of(2026, 1, 1),
                        null,
                        0,
                        10);

        // When / Then
        assertThrows(InvalidSearchDateRangeException.class, () -> searchService.search(request));
        verifyNoInteractions(pieceRepository);
    }

    @Test
    void rejectsAnInvalidPageAndDoesNotQuery() {
        // Given
        SearchRequestDTO request = new SearchRequestDTO(null, null, null, null, null, -1, 10);

        // When / Then
        assertThrows(InvalidSearchPageException.class, () -> searchService.search(request));
        verifyNoInteractions(pieceRepository);
    }

    private PieceSearchCriteria capturedCriteria() {
        ArgumentCaptor<PieceSearchCriteria> captor =
                ArgumentCaptor.forClass(PieceSearchCriteria.class);
        verify(pieceRepository).search(captor.capture());
        return captor.getValue();
    }

    private Piece pieceWithId(Long id, String name, String description) {
        Piece piece = Piece.create(name, description);
        piece.setId(id);
        return piece;
    }
}

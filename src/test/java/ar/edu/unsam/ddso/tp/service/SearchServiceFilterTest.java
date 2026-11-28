package ar.edu.unsam.ddso.tp.service;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.unsam.ddso.tp.dto.PieceResponseDTO;
import ar.edu.unsam.ddso.tp.dto.SearchRequestDTO;
import ar.edu.unsam.ddso.tp.model.Digitalization;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.model.Tag;
import ar.edu.unsam.ddso.tp.repository.PieceRepositoryEnMemoria;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Behavior of the search, with no mocks: the real in-memory repository applies the filters. Every
 * test goes through SearchService.search and asserts on PieceResponseDTO, the same entry point the
 * Telegram handler will use. The repository assigns the ids and every piece gets today as load
 * date.
 */
class SearchServiceFilterTest {

    private static final String URL = "https://example.com/a.jpg";

    private PieceRepositoryEnMemoria repository;
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        repository = new PieceRepositoryEnMemoria();
        searchService = new SearchService(repository);
    }

    // ---------- Name ----------

    @Test
    void findsAPieceByNameContainingTheTextIgnoringCase() {
        // Given
        Piece letter = savePiece("Handwritten Letter", "From 1950");
        savePiece("Family photo", "Black and white");

        // When
        List<PieceResponseDTO> result = searchService.search(byName("LETTER"));

        // Then
        assertEquals(1, result.size());
        PieceResponseDTO found = result.get(0);
        assertEquals(letter.getId(), found.id());
        assertEquals("Handwritten Letter", found.name());
        assertEquals("From 1950", found.description());
        assertEquals(LocalDate.now(), found.loadDate());
    }

    @Test
    void returnsAnEmptyListWhenNothingMatches() {
        // Given
        savePiece("Handwritten Letter", "From 1950");

        // When
        List<PieceResponseDTO> result = searchService.search(byName("map"));

        // Then
        assertTrue(result.isEmpty());
    }

    // ---------- Tags ----------

    @Test
    void searchingByATagReturnsThePieceNotTheDigitalization() {
        // Given
        Piece piece = savePiece("Family photo", null);
        tagWith(piece.addDigitalization(URL, "Front side"), "foto");
        savePiece("Handwritten Letter", null);

        // When
        List<PieceResponseDTO> result = searchService.search(byTags("foto"));

        // Then
        assertEquals(1, result.size());
        assertEquals(piece.getId(), result.get(0).id());
        assertEquals("Family photo", result.get(0).name());
    }

    @Test
    void tagsAreMatchedIgnoringCase() {
        // Given
        Piece piece = savePiece("Family photo", null);
        tagWith(piece.addDigitalization(URL, "Front side"), "foto");

        // When
        List<PieceResponseDTO> result = searchService.search(byTags("FOTO"));

        // Then
        assertEquals(1, result.size());
        assertEquals(piece.getId(), result.get(0).id());
    }

    @Test
    void anUnknownTagReturnsAnEmptyList() {
        // Given
        Piece piece = savePiece("Family photo", null);
        tagWith(piece.addDigitalization(URL, "Front side"), "foto");

        // When
        List<PieceResponseDTO> result =
                searchService.search(byTags("does-not-exist"));

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void severalTagsAreCombinedWithAndAtPieceLevel() {
        // Given: one tag in each digitalization of the same piece still counts
        Piece both = savePiece("Both tags", null);
        tagWith(both.addDigitalization(URL, "First"), "foto");
        tagWith(both.addDigitalization(URL, "Second"), "familia");
        Piece onlyPhoto = savePiece("Only photo", null);
        tagWith(onlyPhoto.addDigitalization(URL, "Only"), "foto");

        // When
        List<PieceResponseDTO> result =
                searchService.search(byTags("foto", "familia"));

        // Then
        assertEquals(1, result.size());
        assertEquals(both.getId(), result.get(0).id());
    }

    // ---------- Description ----------

    @Test
    void findsByTheDescriptionOfThePiece() {
        // Given
        Piece piece = savePiece("Untitled", "Handwritten letter");
        savePiece("Other", "A map");

        // When
        List<PieceResponseDTO> result =
                searchService.search(byDescription("LETTER"));

        // Then
        assertEquals(1, result.size());
        assertEquals(piece.getId(), result.get(0).id());
    }

    @Test
    void findsByTheDescriptionOfADigitalization() {
        // Given
        Piece piece = savePiece("Untitled", "Nothing relevant");
        piece.addDigitalization(URL, "Scan of a handwritten letter");
        savePiece("Other", "A map");

        // When
        List<PieceResponseDTO> result =
                searchService.search(byDescription("letter"));

        // Then
        assertEquals(1, result.size());
        assertEquals(piece.getId(), result.get(0).id());
    }

    @Test
    void doesNotFindADescriptionThatIsNotThere() {
        // Given
        Piece piece = savePiece("Untitled", "Nothing relevant");
        piece.addDigitalization(URL, "Front side");

        // When
        List<PieceResponseDTO> result =
                searchService.search(byDescription("letter"));

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void aPieceWithTwoMatchingDigitalizationsAppearsOnlyOnce() {
        // Given
        Piece piece = savePiece("Untitled", "Handwritten letter");
        piece.addDigitalization(URL, "First scan of the letter");
        piece.addDigitalization(URL, "Second scan of the letter");

        // When
        List<PieceResponseDTO> result =
                searchService.search(byDescription("letter"));

        // Then
        assertEquals(1, result.size());
        assertEquals(piece.getId(), result.get(0).id());
    }

    // ---------- Load date ----------

    @Test
    void fromAndToEqualToTodayFindsThePiece() {
        // Given
        Piece piece = savePiece("Letter", null);
        LocalDate today = LocalDate.now();

        // When
        List<PieceResponseDTO> result =
                searchService.search(between(today, today));

        // Then
        assertEquals(1, result.size());
        assertEquals(piece.getId(), result.get(0).id());
    }

    @Test
    void aRangeEndingYesterdayFindsNothing() {
        // Given
        savePiece("Letter", null);
        LocalDate yesterday = LocalDate.now().minusDays(1);

        // When
        List<PieceResponseDTO> result =
                searchService.search(between(null, yesterday));

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void aRangeStartingTomorrowFindsNothing() {
        // Given
        savePiece("Letter", null);
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // When
        List<PieceResponseDTO> result =
                searchService.search(between(tomorrow, null));

        // Then
        assertTrue(result.isEmpty());
    }

    // ---------- Order and pagination ----------

    @Test
    void ordersByLoadDateDescendingAndThenByIdDescending() {
        // Given: same load date for all, so the id decides
        Piece first = savePiece("First", null);
        Piece second = savePiece("Second", null);
        Piece third = savePiece("Third", null);

        // When
        List<PieceResponseDTO> result =
                searchService.search(page(0, 10));

        // Then
        assertEquals(
                List.of(third.getId(), second.getId(), first.getId()),
                result.stream().map(PieceResponseDTO::id).toList());
    }

    @Test
    void secondPageOfTwoHasTheOldestPiece() {
        // Given
        Piece first = savePiece("First", null);
        savePiece("Second", null);
        savePiece("Third", null);

        // When
        List<PieceResponseDTO> result =
                searchService.search(page(1, 2));

        // Then
        assertEquals(1, result.size());
        assertEquals(first.getId(), result.get(0).id());
    }

    @Test
    void aPageBeyondTheEndIsEmpty() {
        // Given
        savePiece("First", null);
        savePiece("Second", null);
        savePiece("Third", null);

        // When
        List<PieceResponseDTO> result =
                searchService.search(page(2, 2));

        // Then
        assertTrue(result.isEmpty());
    }

    // ---------- Fixtures ----------

    private Piece savePiece(String name, String description) {
        return repository.save(Piece.create(name, description));
    }

    private void tagWith(Digitalization digitalization, String name) {
        // TODO US-04: usar Digitalization.tagWith
        digitalization.getTags().add(Tag.create(name, false));
    }

    private SearchRequestDTO byName(String name) {
        return new SearchRequestDTO(name, null, null, null, null, 0, 10);
    }

    private SearchRequestDTO byDescription(String description) {
        return new SearchRequestDTO(null, description, null, null, null, 0, 10);
    }

    private SearchRequestDTO byTags(String... tags) {
        return new SearchRequestDTO(null, null, null, null, List.of(tags), 0, 10);
    }

    private SearchRequestDTO between(LocalDate from, LocalDate to) {
        return new SearchRequestDTO(null, null, from, to, null, 0, 10);
    }

    private SearchRequestDTO page(int page, int size) {
        return new SearchRequestDTO(null, null, null, null, null, page, size);
    }
}

package ar.edu.unsam.ddso.tp.repository;

import ar.edu.unsam.ddso.tp.model.Digitalization;
import ar.edu.unsam.ddso.tp.model.Piece;
import ar.edu.unsam.ddso.tp.model.PieceSearchCriteria;
import ar.edu.unsam.ddso.tp.model.Tag;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class PieceRepositoryEnMemoria implements PieceRepository {

    private final Map<Long, Piece> data = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public Piece save(Piece piece) {
        if (piece.getId() == null) {
            piece.setId(nextId.getAndIncrement());
        }
        data.put(piece.getId(), piece);
        return piece;
    }

    @Override
    public Optional<Piece> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Piece> search(PieceSearchCriteria criteria) {
        return data.values().stream()
                .filter(piece -> matches(piece, criteria))
                .sorted(
                        Comparator.comparing(Piece::getLoadDate, Comparator.reverseOrder())
                                .thenComparing(Piece::getId, Comparator.reverseOrder()))
                .skip((long) criteria.page() * criteria.size())
                .limit(criteria.size())
                .toList();
    }

    private boolean matches(Piece piece, PieceSearchCriteria criteria) {
        return containsIgnoreCase(piece.getName(), criteria.name())
                && matchesDescription(piece, criteria.description())
                && (criteria.from() == null || !piece.getLoadDate().isBefore(criteria.from()))
                && (criteria.to() == null || !piece.getLoadDate().isAfter(criteria.to()))
                && tagNamesOf(piece).containsAll(criteria.tagNames());
    }

    private boolean matchesDescription(Piece piece, String description) {
        return description == null
                || containsIgnoreCase(piece.getDescription(), description)
                || piece.getDigitalizations().stream()
                        .anyMatch(d -> containsIgnoreCase(d.getDescription(), description));
    }

    private boolean containsIgnoreCase(String text, String expected) {
        return expected == null
                || (text != null && text.toLowerCase(Locale.ROOT).contains(expected));
    }

    private Set<String> tagNamesOf(Piece piece) {
        return piece.getDigitalizations().stream()
                .map(Digitalization::getTags)
                .flatMap(Set::stream)
                .map(Tag::getName)
                .map(name -> name.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
    }
}
